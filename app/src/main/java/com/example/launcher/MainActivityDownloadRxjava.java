package com.example.launcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.launcher.fotaupdate.model.ServerJsonInfoModel;
import com.example.launcher.fotaupdate.model.UpdateInformation;
import com.example.launcher.fotaupdate.model.FileDownLoadInfo;
import com.example.launcher.net.FileDownLoadUtil;
import com.example.launcher.net.JsonUtil;
import com.example.launcher.net.download.DownloadProgressListener;
import com.example.launcher.service.fotaudpate.IFotaUpdateService;
import com.example.launcher.util.ServiceUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivityDownloadRxjava extends Activity {
    private static final int PROCESSING = 1;
    private static final int FAILURE = -1;
    private static final int SHOWDATA = 2;

    private EditText pathText;
    private TextView resultView;
    private Button downloadButton;
    private Button stopButton;
    /**
     * 进度条
     */
    private ProgressBar progressBar;

    private Handler handler = new UIHandler();

    private long start, end;


    // 通知栏
    private NotificationManager updateNotificationManager = null;
    private Notification updateNotification = null;
    private Intent updateIntent = null;// 下载完成
    private PendingIntent updatePendingIntent = null;// 在下载的时候


    //rxjava
    CompositeDisposable compositeDisposable = new CompositeDisposable();


    ServerJsonInfoModel model = new ServerJsonInfoModel();
    AlertDialog.Builder mDialog;
    private FileDownLoadUtil mFileDownLoadUtil;




    private ServiceUtils.ServiceToken mToken;
    private IFotaUpdateService mService = null;
    private ServiceConnection osc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName classname, IBinder obj) {
            mService = IFotaUpdateService.Stub.asInterface(obj);

            if (mService != null) {
                try {
                    mService.startPostData();
                } catch (RemoteException ex) {
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName classname) {
                Log.e("mmmm","onServiceDisconnected");
            mService = null;
        }
    };

    /**
     * 当Handler被创建会关联到创建它的当前线程的消息队列，该类用于往消息队列发送消息
     * 消息队列中的消息由当前线程内部进行处理
     */
    private final class UIHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PROCESSING: //
                    progressBar.setProgress(msg.getData().getInt("size"));
                    //已经下载的占总的大小的百分比
                    float fraction = (float) progressBar.getProgress() / (float) progressBar.getMax();
                    //当前已经下载的大小
                    int currentLength = (int) (fraction * 100);
                    resultView.setText(currentLength + "%");
                    if (progressBar.getProgress() == progressBar.getMax()) {
                        end = System.currentTimeMillis();
                        Log.e("mmmm", "times  ==" + (end - start));
                        Toast.makeText(getApplicationContext(), R.string.success, Toast.LENGTH_LONG).show();
                    }
                    break;
                case FAILURE: // 下载失败
                    Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_LONG).show();
                    break;
                case SHOWDATA: // 展示数据

                    Toast.makeText(getApplicationContext(), msg.getData().getString("data"), Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        pathText = (EditText) findViewById(R.id.path);
        resultView = (TextView) findViewById(R.id.resultView);
        downloadButton = (Button) findViewById(R.id.downloadbutton);
        stopButton = (Button) findViewById(R.id.stopbutton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        ButtonClickListener listener = new ButtonClickListener();
        downloadButton.setOnClickListener(listener);
        stopButton.setOnClickListener(listener);
//		pathText.setText("http://172.16.1.78/HttpURLConnection使用.zip");
        pathText.setText("http://172.16.1.78/app-debug.apk");
        Log.e("mmmm", "getFilesDir  ==" + this.getFilesDir().getAbsolutePath());
        Log.e("mmmm", "getCacheDir  ==" + this.getCacheDir().getAbsolutePath());
        Log.e("mmmm", "getObbDir  ==" + this.getObbDir().getAbsolutePath());
//        compositeDisposable.dispose();
        mToken = ServiceUtils.bindToService(this, osc);
        if (mToken == null) {
            // something went wrong
            Toast.makeText(this, "service error!", Toast.LENGTH_LONG).show();
        }
        compositeDisposable = new CompositeDisposable();
        if (getExit()) {
            compositeDisposable.add(Observable.create(new ObservableOnSubscribe<String>() {
                @Override
                public void subscribe(final ObservableEmitter<String> emitter) throws Exception {  //
                    JsonUtil.getInstance(MainActivityDownloadRxjava.this).getJsonData("http://172.16.1.78/update.json", new JsonUtil.DataCallBack() {
                                @Override
                                public void requestFailure(Exception e) {
                                }

                                @Override
                                public void requestSuccess(String result) {
                                    emitter.onNext(result);
//                                emitter.onNext(result);

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
                            checkVersion(MainActivityDownloadRxjava.this, false);
                        }

                    }));
        }else{

        }



    }

    private void exit() {
        FileDownLoadUtil.getInstance(getApplicationContext()).exit();
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
            update(context);
        } else {
            if (isShowDialog) {
                //没有最新版本，不用升级
                noNewVersion(context);
            }
            clearUpateFile(context);
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
        }).create().show();
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
                //ManagerActivity.getInstance().finishActivity();
                System.exit(0);
            }
        }).setCancelable(false).create().show();
    }

    private void startDownload() {
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
        }).create().show();
    }

    /**
     * 清理升级文件
     *
     * @param context
     */
    private void clearUpateFile(final Context context) {
        File updateDir;
        File updateFile;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            updateDir = new File(Environment.getExternalStorageDirectory(),
                    UpdateInformation.downloadDir);
        } else {
            updateDir = context.getFilesDir();
        }
        updateFile = new File(updateDir.getPath(), context.getResources()
                .getString(R.string.app_name) + ".apk");
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
            if (info.getTotalSize() != 0 && progressBar.getMax() != info.getTotalSize()) {
                progressBar.setMax(info.getTotalSize());
            }
            progressBar.setProgress(info.getDownloadedsize());
            //已经下载的占总的大小的百分比
            float fraction = (float) progressBar.getProgress() / (float) progressBar.getMax();
            //当前已经下载的大小
            int currentLength = (int) (fraction * 100);
            resultView.setText(currentLength + "%");
            if (progressBar.getProgress() == progressBar.getMax()) {
                end = System.currentTimeMillis();
                Log.e("mmmm", "times  ==" + (end - start));
                Toast.makeText(getApplicationContext(), R.string.success, Toast.LENGTH_LONG).show();
            }
        }
    };
    Consumer<Throwable> onError = new Consumer<Throwable>() {
        @Override
        public void accept(Throwable o) throws Exception {
            Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_LONG).show();

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
            MainActivityDownloadRxjava.this.startActivity(installIntent);
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
                        mFileInfo.setmFile(saveFilePath);
                        mFileInfo.setDownLoadComplete(true);
                        emitter.onNext(mFileInfo);
                        emitter.onComplete();
                        exit();
                    }

                    @Override
                    public void onDownloadError(Exception e) {
                        emitter.onError(e);
                    }
                }).download();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError, onComplete));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    private final class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.downloadbutton: //开始下载
                    start = System.currentTimeMillis();
//				Toast.makeText(getApplicationContext(), "Now thread is downloadbutton!!", Toast.LENGTH_LONG).show();

                    String path = pathText.getText().toString();
                    String filename = path.substring(path.lastIndexOf('/') + 1);
                    try {
                        filename = URLEncoder.encode(filename, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        break;
                    }

                    path = path.substring(0, path.lastIndexOf("/") + 1) + filename;
                    File savDir;
                    if (Environment.MEDIA_MOUNTED.equals(Environment
                            .getExternalStorageState())) {
                        savDir = getApplicationContext().getExternalCacheDir();
                    } else {
                        savDir = getApplicationContext().getFilesDir();
                    }
                    Log.e("mmmm", "savedir==" + savDir.getAbsolutePath());
                    download(path, savDir);

                    downloadButton.setEnabled(false);
                    stopButton.setEnabled(true);
                    break;
                case R.id.stopbutton: // 停止下载
                    exit();
                    downloadButton.setEnabled(true);
                    stopButton.setEnabled(false);
                    break;
            }
        }
    }
}
