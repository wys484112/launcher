package com.example.launcher.net;

import android.content.Context;


import java.net.URL;

/**
 * 使用 内存硬盘 双重缓存
 *
 * @author shenhui
 */

public class JsonUtil {

    private static final String TAG = "JsonUtil";
    private static Context mContext;
    private static JsonUtil instance;
    private DataCallBack mCallBack;
    private String mUrl;
    private WorkTask task;

    private JsonUtil(Context context) {
        mContext = context;

    }

    public static synchronized JsonUtil getInstance(Context context) {
        if (null == instance) {
            instance = new JsonUtil(context);
        }
        return instance;
    }

    public synchronized void getJsonData(String url, DataCallBack callBack) {
        mUrl = url;
        mCallBack = callBack;
        task = new WorkTask();
        new Thread(task).start();

    }

    /**
     * 直接从网络 获取 图片 不缓存
     * todo : 还未测试
     *
     * @param url
     * @return
     * @throws org.apache.http.conn.ConnectTimeoutException
     * @throws java.io.IOException
     */

    public String getJsonData(String url) {
        String jsonStr = null;
        try {
            URL mUrl = new URL(url);
            byte[] data = HttpClient.connect(mUrl, HttpClient.HTTP_POST,
                    null,
                    30000,
                    10000);
            if (data != null) {
                jsonStr = new String(data, "UTF-8");
            }
            if (mCallBack != null) {
                mCallBack.requestSuccess(jsonStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (mCallBack != null) {
                mCallBack.requestFailure(e);
            }
        }

        return jsonStr;
    }


    public interface DataCallBack {
        public void requestFailure(Exception e);
        public void requestSuccess(String result);
    }


    private final class WorkTask implements Runnable {
        public WorkTask() {
        }

        @Override
        public void run() {
            getJsonData(mUrl);
        }
    }
}
