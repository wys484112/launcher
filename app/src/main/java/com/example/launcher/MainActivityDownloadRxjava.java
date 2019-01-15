package com.example.launcher;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.launcher.model.FileDownLoadInfo;
import com.example.launcher.net.FileDownLoadUtil;
import com.example.launcher.net.JsonUtil;
import com.example.launcher.net.download.DownloadProgressListener;

import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
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
        compositeDisposable = new CompositeDisposable();
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
                    public void accept(String o) throws Exception {
                        Log.e("mmmm", "accept getJsonData  ==" + o);
                        try {
                            JSONObject jsonObject = new JSONObject(o);
                            resultView.setText(o);
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }
                }));

        


    }

    private void exit() {
        FileDownLoadUtil.getInstance(getApplicationContext()).exit();
    }


    Consumer<FileDownLoadInfo> onNext= new Consumer<FileDownLoadInfo>() {
        @Override
        public void accept(FileDownLoadInfo o) throws Exception {

        }
    };
    Consumer<FileDownLoadInfo> onError= new Consumer<FileDownLoadInfo>() {
        @Override
        public void accept(FileDownLoadInfo o) throws Exception {

        }
    };
    Action   onComplete =new Action() {
        @Override
        public void run() throws Exception {

        }
    };
    private FileDownLoadInfo mFileInfo =new FileDownLoadInfo();
    private void download(final String path, final File savDir) {
        Observable download=Observable.create(new ObservableOnSubscribe<FileDownLoadInfo>() {
            @Override
            public void subscribe(final ObservableEmitter<FileDownLoadInfo> emitter) throws Exception {
                FileDownLoadUtil.getInstance(getApplicationContext()).download(path, savDir, new DownloadProgressListener() {
                    @Override
                    public void onDownloadGetFileSize(int totalSize) {
                        mFileInfo.setTotalSize(totalSize);
                        emitter.onNext(mFileInfo);
                    }

                    @Override
                    public void onDownloadSize(int size) {
                        mFileInfo.setDownloadedsize(size);
                        emitter.onNext(mFileInfo);
                    }

                    @Override
                    public void onDownloadComplete(File saveFilePath) {
                        mFileInfo.setmFile(saveFilePath);
                        emitter.onNext(mFileInfo);
                        emitter.onComplete();
                    }

                    @Override
                    public void onDownloadError(Exception e) {
                        emitter.onError(e);
                    }
                });

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        download.subscribe(new Observer<FileDownLoadInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(FileDownLoadInfo info) {
                        if(info.getTotalSize()!=0&&progressBar.getMax()!=info.getTotalSize()){
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

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                        Log.e("mmmm", "onDownloadComplete  saveFilePath==" + mFileInfo.getmFile().getAbsolutePath());

                        Uri uri = Uri.fromFile(mFileInfo.getmFile());
                        //安装程序
                        Intent installIntent = new Intent(Intent.ACTION_VIEW);
                        installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        installIntent.setDataAndType(uri,
                                "application/vnd.android.package-archive");
                        MainActivityDownloadRxjava.this.startActivity(installIntent);
                    }
                });
    }

    DownloadProgressListener downloadProgressListener = new DownloadProgressListener() {
        @Override
        public void onDownloadSize(int size) {
            Message msg = new Message();
            msg.what = PROCESSING;
            msg.getData().putInt("size", size);
            handler.sendMessage(msg);
        }

        @Override
        public void onDownloadGetFileSize(int totalSize) {
            progressBar.setMax(totalSize);

        }

        @Override
        public void onDownloadComplete(File saveFilePath) {
            Log.e("mmmm", "onDownloadComplete  saveFilePath==" + saveFilePath);

            Uri uri = Uri.fromFile(saveFilePath);
            //安装程序
            Intent installIntent = new Intent(Intent.ACTION_VIEW);
            installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            installIntent.setDataAndType(uri,
                    "application/vnd.android.package-archive");
            MainActivityDownloadRxjava.this.startActivity(installIntent);

        }

        @Override
        public void onDownloadError(Exception e) {

        }
    };

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
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        File savDir = MainActivityDownloadRxjava.this.getExternalCacheDir();//Environment.getExternalStorageDirectory();
                        download(path, savDir);
                    } else {
                    }
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
