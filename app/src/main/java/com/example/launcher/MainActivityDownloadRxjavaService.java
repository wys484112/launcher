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

import com.example.launcher.fotaupdate.model.FileDownLoadInfo;
import com.example.launcher.fotaupdate.model.ServerJsonInfoModel;
import com.example.launcher.fotaupdate.model.UpdateInformation;
import com.example.launcher.net.FileDownLoadUtil;
import com.example.launcher.net.JsonUtil;
import com.example.launcher.net.download.DownloadProgressListener;
import com.example.launcher.IFotaUpdateService;
import com.example.launcher.util.ServiceUtils;

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

public class MainActivityDownloadRxjavaService extends Activity {
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


    private long start, end;


    // 通知栏
    private NotificationManager updateNotificationManager = null;
    private Notification updateNotification = null;
    private Intent updateIntent = null;// 下载完成
    private PendingIntent updatePendingIntent = null;// 在下载的时候


    //rxjava

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        pathText = (EditText) findViewById(R.id.path);
        resultView = (TextView) findViewById(R.id.resultView);
        downloadButton = (Button) findViewById(R.id.downloadbutton);
        stopButton = (Button) findViewById(R.id.stopbutton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

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

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }


}
