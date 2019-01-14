package com.example.launcher;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.Activity;
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

import com.example.launcher.net.download.DownloadProgressListener;
import com.example.launcher.net.download.FileDownloader;

public class MainActivityDownload extends Activity {
	private static final int PROCESSING = 1;
	private static final int FAILURE = -1;

	private EditText pathText;
	private TextView resultView;
	private Button downloadButton;
	private Button stopButton;
	/**进度条*/
	private ProgressBar progressBar;

	private Handler handler = new UIHandler();

	private long start,end;
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
					end=System.currentTimeMillis();
					Log.e("mmmm","times  =="+(end-start));
					Toast.makeText(getApplicationContext(), R.string.success, Toast.LENGTH_LONG).show();
				}
				break;
			case FAILURE: // 下载失败
				Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_LONG).show();
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
		pathText.setText("http://172.16.1.78/app_activity_bg.png");

	}

	private final class ButtonClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.downloadbutton: //开始下载
				start=System.currentTimeMillis();
				Toast.makeText(getApplicationContext(), "Now thread is downloadbutton!!", Toast.LENGTH_LONG).show();

				String path = "http://172.16.1.78/app_activity_bg.png";//pathText.getText().toString();
				String filename = path.substring(path.lastIndexOf('/') + 1);
				try {
					filename = URLEncoder.encode(filename, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();break;
				}

				path = path.substring(0, path.lastIndexOf("/") + 1) + filename;
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					File savDir = Environment.getExternalStorageDirectory();
					download(path, savDir);
				} else {
					Toast.makeText(getApplicationContext(), R.string.sdcarderror, Toast.LENGTH_LONG).show();
				}
				downloadButton.setEnabled(false);
				stopButton.setEnabled(true);
				break;
			case R.id.stopbutton: // 停止下载
				exit();
				Toast.makeText(getApplicationContext(), "Now thread is Stopping!!", Toast.LENGTH_LONG).show();
				downloadButton.setEnabled(true);
				stopButton.setEnabled(false);
				break;
			}
		}


		private DownloadTask task;

		private void exit() {
			if (task != null)
				task.exit();
		}

		private void download(String path, File savDir) {
			task = new DownloadTask(path, savDir);
			new Thread(task).start();
		}

		private final class DownloadTask implements Runnable {
			private String path;
			private File saveDir;
			private FileDownloader loader;

			public DownloadTask(String path, File saveDir) {
				this.path = path;
				this.saveDir = saveDir;
			}


			public void exit() {
				if (loader != null)
					loader.exit();
			}

			DownloadProgressListener downloadProgressListener = new DownloadProgressListener() {
				@Override
				public void onDownloadSize(int size) {
					Message msg = new Message();
					msg.what = PROCESSING;
					msg.getData().putInt("size", size);
					handler.sendMessage(msg);
				}
			};

			@Override
			public void run() {
				try {
					//构造下载器
					loader = new FileDownloader(getApplicationContext(), path, saveDir, 3);
					progressBar.setMax(loader.getFileSize());
					//开始下载
					loader.download(downloadProgressListener);
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendMessage(handler.obtainMessage(FAILURE));
				}
			}
		}
	}

}
