package com.example.launcher.net;

import android.content.Context;
import android.os.Message;

import com.activeandroid.util.Log;
import com.example.launcher.net.download.DownloadProgressListener;
import com.example.launcher.net.download.FileDownloader;

import java.io.File;
import java.net.URL;

/**
 * 使用 内存硬盘 双重缓存
 *
 * @author shenhui
 */

public class FileDownLoadUtil {

    private static final String TAG = "JsonUtil";
    private static Context mContext;
    private static FileDownLoadUtil instance;
    private DownloadTask task;
    private DownloadProgressListener downloadProgressListener;

    private FileDownLoadUtil(Context context) {
        mContext = context;
    }

    public static synchronized FileDownLoadUtil getInstance(Context context) {
        if (null == instance) {
            android.util.Log.e("mmmm", " new FileDownLoadUtil  ==" );

            instance = new FileDownLoadUtil(context);
        }
        return instance;
    }
//
//    public void download(String path, File savDir, DownloadProgressListener listener) {
//        downloadProgressListener = listener;
//        task = new DownloadTask(path, savDir);
//        new Thread(task).start();
//
//    }
    public void exit() {
        if (task != null)
            task.exit();
    }

    public boolean getExit() {
        if (task != null)
            return task.getExit();
        return true;
    }
    public FileDownLoadUtil initialize(String path, File savDir, DownloadProgressListener listener){
        android.util.Log.e("mmmm", "download  ==" );
        downloadProgressListener = listener;
        task = new DownloadTask(path, savDir);
        return instance;
    }
    public void download() {
        new Thread(task).start();
    }

    private final class DownloadTask implements Runnable {
        private String path;
        private File saveDir;
        private FileDownloader loader;

        public DownloadTask(String path, File saveDir) {
            this.path = path;
            this.saveDir = saveDir;
            this.loader = new FileDownloader(mContext, path, saveDir,downloadProgressListener, 3);

        }
        public void exit() {
            if (loader != null)
                loader.exit();
        }
        public boolean getExit() {
            if (loader != null)
                return loader.getExit();
            return true;
        }
        @Override
        public void run() {
            try {
                android.util.Log.e("mmmm", "run  ==" );
                //构造下载器
                //开始下载
                loader.download();
            } catch (Exception e) {
                e.printStackTrace();
                if (downloadProgressListener != null){
                    downloadProgressListener.onDownloadError(e);//通知目前下载失败
                }
            }
        }
    }
}

