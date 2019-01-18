/**
 * Copyright (c) 2014, Qualcomm Technologies, Inc. All Rights Reserved.
 * Qualcomm Technologies Proprietary and Confidential.
 */

package com.example.launcher.service.fotaudpate;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.telephony.ServiceState;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.launcher.IFotaUpdateService;
import com.example.launcher.MainActivityDownloadRxjava;
import com.example.launcher.MainTvActivity;
import com.example.launcher.R;
import com.example.launcher.fotaupdate.model.FileDownLoadInfo;
import com.example.launcher.fotaupdate.model.ServerJsonInfoModel;
import com.example.launcher.fotaupdate.model.UpdateInformation;
import com.example.launcher.net.FileDownLoadUtil;
import com.example.launcher.net.JsonUtil;
import com.example.launcher.net.download.DownloadProgressListener;
import com.example.launcher.net.download.db.FileDBService;
import com.hwangjr.rxbus.RxBus;

import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 *
 *
 * */
public class FotaUpdateService extends Service {

    private static final String TAG = "FotaUpdateService";
    private static final boolean DBG = true;


    private static final String PREF_CONNECTED_MINUTES = "network_connected_minutes";
    private static final String PREF_DAY_MONTH_YEAR_RECORD = "day_month_year_record";


    private SharedPreferences mSharedPreferences;
    private ConnectivityManager mConnectivityManager;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ServerJsonInfoModel model = new ServerJsonInfoModel();
    AlertDialog.Builder mDialog;


    // 通知栏
    private NotificationManager updateNotificationManager = null;
    private Notification updateNotification = null;
    private Intent updateIntent = null;// 下载完成
    private PendingIntent updatePendingIntent = null;// 在下载的时候
    // BT字节参考量
    private static final float SIZE_BT = 1024L;
    // KB字节参考量
    private static final float SIZE_KB = SIZE_BT * 1024.0f;
    // MB字节参考量
    private static final float SIZE_MB = SIZE_KB * 1024.0f;
    private static final String UpdateAction = "action.update";
    private UpdateReceiver updateReceiver = new UpdateReceiver();

    class UpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            startVersionUpdate(false);
        }

    }
    @Override
    public void onCreate() {
        super.onCreate();
        if (DBG)
            Log.d(TAG, "onCreate");


        compositeDisposable = new CompositeDisposable();
        IntentFilter filter = new IntentFilter();
        filter.addAction(UpdateAction);
        registerReceiver(updateReceiver, filter);
    }

    public static String getMsgSpeed(long downSize, long allSize) {
        StringBuffer sBuf = new StringBuffer();
        sBuf.append(getSize(downSize));
        sBuf.append("/");
        sBuf.append(getSize(allSize));
        sBuf.append(" ");
        sBuf.append(getPercentSize(downSize, allSize));
        return sBuf.toString();
    }

    /**
     * 获取大小
     * @param size
     * @return
     */
    public static String getSize(long size) {
        if (size >= 0 && size < SIZE_BT) {
            return (double) (Math.round(size * 10) / 10.0) + "B";
        } else if (size >= SIZE_BT && size < SIZE_KB) {
            return (double) (Math.round((size / SIZE_BT) * 10) / 10.0) + "KB";
        } else if (size >= SIZE_KB && size < SIZE_MB) {
            return (double) (Math.round((size / SIZE_KB) * 10) / 10.0) + "MB";
        }
        return "";
    }
    /**
     * 获取到当前的下载百分比
     * @param downSize   下载大小
     * @param allSize    总共大小
     * @return
     */
    public static String getPercentSize(long downSize, long allSize) {
        String percent = (allSize == 0 ? "0.0" : new DecimalFormat("0.0")
                .format((double) downSize / (double) allSize * 100));
        return "(" + percent + "%)";
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DBG)
            Log.d(TAG, "onStartCommand");

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        DownloadThreadExit();
        DownloadThreadRecordClear(UpdateInformation.updateurl);
        clearUpateFile(getApplicationContext(),UpdateInformation.updateurl);
    }

    private  boolean isDownlLoadFileComplete(String downloadUrl){
        if(isDownLoadFileExist(downloadUrl)&&isDownloadThreadRecordNull(downloadUrl)&&isDownloadThreadExit()){
            return true;
        }
        return false;
    }
    private  boolean isDownLoading(){
        return !FileDownLoadUtil.getInstance(getApplicationContext()).getExit();
    }

    private  boolean isDownloadThreadExit(){
        return FileDownLoadUtil.getInstance(getApplicationContext()).getExit();
    }


    private boolean isDownLoadFileExist(String downloadUrl){
        File  file=getDownLoadFile(downloadUrl);
        if(file.exists()&&file.length()>0){
            return true;
        }
        return false;
    }


    private  boolean isDownloadThreadRecordNull(String downloadUrl){
        FileDBService fileService = new FileDBService(getApplicationContext());
        Map<Integer, Integer> logdata = fileService.getData(downloadUrl);//获取下载记录
        if (logdata.size() > 0) {//如果存在下载记录
            return false;
        }
        return true;
    }
    private void DownloadThreadExit() {
        FileDownLoadUtil.getInstance(getApplicationContext()).exit();
    }

    private void DownloadThreadRecordClear(String downloadUrl) {
        FileDBService fileService = new FileDBService(getApplicationContext());
        fileService.delete(downloadUrl);
    }
    private File getDownLoadFile(String downloadUrl){
        String filename = downloadUrl.substring(downloadUrl.lastIndexOf('/') + 1);
        File savDir;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            savDir = getApplicationContext().getExternalCacheDir();
        } else {
            savDir = getApplicationContext().getFilesDir();
        }
        File saveFile = new File(savDir, filename);//构建保存文件
        return saveFile;
    }
    public boolean getExit() {
        return FileDownLoadUtil.getInstance(getApplicationContext()).getExit();
    }
    private void getUpdateInformation(ServerJsonInfoModel model) {
        try {
            /**
             * 获取到当前的本地版本
             */
            UpdateInformation.localVersion = getApplicationContext()
                    //包管理独享
                    .getPackageManager()
                    //包信息
                    .getPackageInfo(
                            getApplicationContext()
                                    .getPackageName(), 0).versionCode;
            /**
             * 获取到当前的版本名字
             */
            UpdateInformation.versionName = getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(
                            getApplicationContext()
                                    .getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //app名字
        UpdateInformation.appname = getApplicationContext()
                .getResources().getString(R.string.app_name);
        //服务器版本
        UpdateInformation.serverVersion = Integer.parseInt(model
                .getServerVersion());
        //服务器标志
        UpdateInformation.serverFlag = Integer.parseInt(model.getServerFlag());
        //强制升级
        UpdateInformation.lastForce = Integer.parseInt(model.getLastForce());
        //升级地址
        UpdateInformation.updateurl = model.getUpdateurl();
        //升级信息
        UpdateInformation.upgradeinfo = model.getUpgradeinfo();
    }

    /**
     * 检查版本更新
     *
     * @param context
     */
    private void checkVersion(Context context, boolean isShowDialog) {
        Log.e("mmmm", "checkVersion localVersion  ==" + UpdateInformation.localVersion);
        Log.e("mmmm", "checkVersion serverVersion  ==" + UpdateInformation.serverVersion);


        if (UpdateInformation.localVersion < UpdateInformation.serverVersion) {
            // 需要进行更新
            //更新
            clearUpateFile(context,UpdateInformation.updateurl);
            DownloadThreadRecordClear(UpdateInformation.updateurl);
            update(context);
        } else {
            if (isShowDialog) {
                //没有最新版本，不用升级
                noNewVersion(context);
            }
            clearUpateFile(context,UpdateInformation.updateurl);
            DownloadThreadRecordClear(UpdateInformation.updateurl);

        }
    }

    /**
     * 进行升级
     *
     * @param context
     */
    private void update(Context context) {
        if (UpdateInformation.serverFlag == 1) {
            // 官方推荐升级
            if (UpdateInformation.localVersion < UpdateInformation.lastForce) {
                //强制升级
                forceUpdate(context);
            } else {
                //正常升级
                normalUpdate(context);
            }
        } else if (UpdateInformation.serverFlag == 2) {
            // 官方强制升级
            forceUpdate(context);
        }
    }

    /**
     * 没有新版本
     *
     * @param context
     */
    private void noNewVersion(final Context context) {
        mDialog = new AlertDialog.Builder(context);
        mDialog.setTitle("版本更新");
        mDialog.setMessage("当前为最新版本");
        mDialog.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final Dialog dialog = mDialog.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    /**
     * 强制升级 ，如果不点击确定升级，直接退出应用
     *
     * @param context
     */
    private void forceUpdate(final Context context) {
        mDialog = new AlertDialog.Builder(context);
        mDialog.setTitle("版本更新");
        mDialog.setMessage(UpdateInformation.upgradeinfo);
        mDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startDownload();

            }
        }).setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 直接退出应用
                Intent i=new Intent("action.exit");
                sendBroadcast(i);
                dialog.dismiss();
            }
        }).setCancelable(false);

        final Dialog dialog = mDialog.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
        //不显示退出按键
//        ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.GONE);


    }

    private void startDownload() {


        updateNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        updateNotification = new Notification();
        //通知图标
        updateNotification.icon = R.drawable.ic_launcher;
        //通知信息描述
        updateNotification.tickerText = "正在下载 " + getApplicationContext().getResources().getString(R.string.app_name);
        updateNotification.when = System.currentTimeMillis();
        updateIntent = new Intent(this, MainTvActivity.class);
        updatePendingIntent = PendingIntent.getActivity(this, 0, updateIntent,
                0);
        updateNotification.contentIntent = updatePendingIntent;
        updateNotification.contentIntent.cancel();
        updateNotification.contentView = new RemoteViews(getPackageName(),
                //这个布局很简单，就是一个图片和两个textview，分别是正在下载和下载进度
                R.layout.download_notification);
        updateNotification.contentView.setTextViewText(
                R.id.download_notice_name_tv, getApplicationContext().getResources().getString(R.string.app_name) + " 正在下载");
        updateNotification.contentView.setTextViewText(
                R.id.download_notice_speed_tv, "0MB (0%)");
        updateNotificationManager.notify(0, updateNotification);


        File savDir;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            savDir = getApplicationContext().getExternalCacheDir();
        } else {
            savDir = getApplicationContext().getFilesDir();
        }
        Log.e("mmmm", "savedir==" + savDir.getAbsolutePath());
        download(UpdateInformation.updateurl, savDir);
    }

    /**
     * 正常升级，用户可以选择是否取消升级
     *
     * @param context
     */
    private void normalUpdate(final Context context) {
        mDialog = new AlertDialog.Builder(context);
        mDialog.setTitle("版本更新");
        mDialog.setMessage(UpdateInformation.upgradeinfo);
        mDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startDownload();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final Dialog dialog = mDialog.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    /**
     * 清理升级文件
     *
     * @param context
     */
    private void clearUpateFile(final Context context,String updateurl) {
        File updateFile=getDownLoadFile(updateurl);

        if (updateFile.exists()) {
            Log.d("update", "升级包存在，删除升级包");
            updateFile.delete();
        } else {
            Log.d("update", "升级包不存在，不用删除升级包");
        }
    }

    Consumer<FileDownLoadInfo> onNext = new Consumer<FileDownLoadInfo>() {
        @Override
        public void accept(FileDownLoadInfo info) throws Exception {
            updateNotification.contentView
                    .setTextViewText(
                            R.id.download_notice_speed_tv,
                            getMsgSpeed(info.getDownloadedsize(),info.getTotalSize()));
            updateNotificationManager.notify(0,
                    updateNotification);
        }
    };
    Consumer<Throwable> onError = new Consumer<Throwable>() {
        @Override
        public void accept(Throwable o) throws Exception {
            Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_LONG).show();

            Intent installIntent = new Intent(UpdateAction);

            updatePendingIntent = PendingIntent.getActivity(
                    FotaUpdateService.this, 0, installIntent, 0);
            updateNotification.contentIntent = updatePendingIntent;
            updateNotification.contentView.setTextViewText(
            R.id.download_notice_speed_tv,
                    getString(R.string.update_notice_error));
            updateNotification.tickerText = getApplicationContext().getResources().getString(R.string.app_name) + getString(R.string.update_notice_error);
            updateNotification.when = System.currentTimeMillis();
            updateNotification.defaults = Notification.DEFAULT_SOUND;
            updateNotification.flags |= Notification.FLAG_AUTO_CANCEL;
            updateNotificationManager.notify(0, updateNotification);

        }
    };
    Action onComplete = new Action() {
        @Override
        public void run() throws Exception {

            Log.e("mmmm", "onComplete  saveFilePath==" + mFileInfo.getmFile().getAbsolutePath());

            Uri uri = Uri.fromFile(mFileInfo.getmFile());
            //安装程序
            Intent installIntent = new Intent(Intent.ACTION_VIEW);
            installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            installIntent.setDataAndType(uri,
                    "application/vnd.android.package-archive");


            updatePendingIntent = PendingIntent.getActivity(
                    FotaUpdateService.this, 0, installIntent, 0);
            updateNotification.contentIntent = updatePendingIntent;
            updateNotification.contentView.setTextViewText(
                    R.id.download_notice_speed_tv,
                    getString(R.string.update_notice_finish));
            updateNotification.tickerText = getApplicationContext().getResources().getString(R.string.app_name) + "下载完成";
            updateNotification.when = System.currentTimeMillis();
            updateNotification.defaults = Notification.DEFAULT_SOUND;
            updateNotification.flags |= Notification.FLAG_AUTO_CANCEL;
            updateNotificationManager.notify(0, updateNotification);
            FotaUpdateService.this.startActivity(installIntent);
            stopSelf();
        }
    };
    private FileDownLoadInfo mFileInfo = new FileDownLoadInfo();

    private void download(final String downloadUrl, final File savDir) {
        compositeDisposable.add(Observable.create(new ObservableOnSubscribe<FileDownLoadInfo>() {
            @Override
            public void subscribe(final ObservableEmitter<FileDownLoadInfo> emitter) throws Exception {
                FileDownLoadUtil.getInstance(getApplicationContext()).initialize(downloadUrl, savDir, new DownloadProgressListener() {
                    @Override
                    public void onDownloadGetFileSize(int totalSize) {
                        mFileInfo.setTotalSize(totalSize);
                        emitter.onNext(mFileInfo);
                    }

                    @Override
                    public void onDownloadSize(int size) {
                        android.util.Log.e("mmmm", "onDownloadSize  =="+size );

                        mFileInfo.setDownloadedsize(size);
                        emitter.onNext(mFileInfo);
                    }

                    @Override
                    public void onDownloadComplete(File saveFilePath) {
                        DownloadThreadExit();
                        DownloadThreadRecordClear(downloadUrl);
                        mFileInfo.setmFile(saveFilePath);
                        mFileInfo.setDownLoadComplete(true);
                        emitter.onNext(mFileInfo);
                        emitter.onComplete();
                    }

                    @Override
                    public void onDownloadError(Exception e) {
                        DownloadThreadExit();
                        DownloadThreadRecordClear(downloadUrl);

                        emitter.onError(e);
                    }
                }).download();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError, onComplete));
    }






    private void startPostData() {
        if (DBG)
            Log.d(TAG, "startPostData");
    }


    private void startVersionUpdate(final boolean isShowDialog) {
        if (DBG)
            Log.d(TAG, "startVersionUpdate");

        if (!isDownLoading()) {
            compositeDisposable.add(Observable.create(new ObservableOnSubscribe<String>() {
                @Override
                public void subscribe(final ObservableEmitter<String> emitter) throws Exception {  //
                    JsonUtil.getInstance(getApplicationContext()).getJsonData("http://172.16.1.78/update.json", new JsonUtil.DataCallBack() {
                                @Override
                                public void requestFailure(Exception e) {
                                }

                                @Override
                                public void requestSuccess(String result) {
                                    emitter.onNext(result);

                                    emitter.onComplete();
                                }
                            }
                    );
                }
            }).subscribeOn(Schedulers.io()) //指定ObservableEmitter 发生的线程
                    .observeOn(AndroidSchedulers.mainThread()) //指定回调comsumer发生的线程
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String result) throws Exception {
                            Log.e("mmmm", "accept getJsonData  ==" + result);
                            JSONObject object = new JSONObject(result);
                            model.setAppname(object.getString("appname"));
                            model.setLastForce(object.getString("lastForce"));
                            model.setServerFlag(object.getString("serverFlag"));
                            model.setServerVersion(object.getString("serverVersion"));
                            model.setUpdateurl(object.getString("updateurl"));
                            model.setUpgradeinfo(object.getString("upgradeinfo"));
                            getUpdateInformation(model);
                            Log.e("mmmm", "getExit getExit  ==true");
                            checkVersion(getApplicationContext(), isShowDialog);
                        }

                    }));
        }
    }


    private void stopPostData() {

    }
    private final IBinder mBinder = new ServiceStub(this);


    /*
     * By making this a static class with a WeakReference to the Service, we
     * ensure that the Service can be GCd even when the system process still
     * has a remote reference to the stub.
     */
    static class ServiceStub extends IFotaUpdateService.Stub {
        WeakReference<FotaUpdateService> mService;

        ServiceStub(FotaUpdateService service) {
            mService = new WeakReference<FotaUpdateService>(service);
        }

        @Override
        public void startPostData() throws RemoteException {
            mService.get().startPostData();
        }

        @Override
        public void startVersionUpdate( boolean isShowDialog) throws RemoteException {
            mService.get().startVersionUpdate(isShowDialog);
        }
        @Override
        public void stopPostData() throws RemoteException {
            mService.get().stopPostData();

        }

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }
    }

}