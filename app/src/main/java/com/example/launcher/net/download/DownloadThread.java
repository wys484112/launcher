package com.example.launcher.net.download;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class DownloadThread extends Thread {
	private static final String TAG = "DownloadThread";
	/**保存文件的位置*/
	private File saveFile;
	/**文件资源路径*/
	private URL downUrl;
	/**当前线程需要下载的长度*/
	private int blockLength;
	/** 当前线程的id */
	private int threadId = -1;
	/**当前线程已经下载的文件大小*/
	private int downLength;
	/**true 下载完成 否则没有下载完成*/
	private boolean finish = false;
	private FileDownloader downloader;

	/**
	 * 构造函数
	 * @param downloader FileDownloader
	 * @param downUrl 资源文件的URL
	 * @param saveFile 构造的本地文件
	 * @param block  当前线程需要下载的长度
	 * @param downLength 当前线程已经下载的文件大小
     * @param threadId 当前线程ID
     */
	public DownloadThread(FileDownloader downloader, URL downUrl,
						  File saveFile, int block, int downLength, int threadId) {
		this.downUrl = downUrl;
		this.saveFile = saveFile;
		this.blockLength = block;
		this.downloader = downloader;
		this.threadId = threadId;
		this.downLength = downLength;
	}

	@Override
	public void run() {
		if (downLength < blockLength) {
			try {
//get 方式获取数据
				HttpURLConnection http = (HttpURLConnection) downUrl.openConnection();
				http.setConnectTimeout(5 * 1000);
				http.setRequestMethod("GET");
				http.setRequestProperty("Accept",
						"image/gif, image/jpeg, image/pjpeg, image/pjpeg, " +
								"application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, " +
								"application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, " +
								"application/vnd.ms-powerpoint, application/msword, */*");
				http.setRequestProperty("Accept-Language", "zh-CN");
				http.setRequestProperty("Referer", downUrl.toString());// 先前网页的地址，当前请求网页紧随其后,即来路
				http.setRequestProperty("Charset", "UTF-8");
				int startPos = blockLength * (threadId - 1) + downLength;
				int endPos = blockLength * threadId - 1;// 结束位置
				http.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);//设置获取实体数据的范围
				http.setRequestProperty(
						"User-Agent",
						"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; " +
								".NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30;" +
								" .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
				http.setRequestProperty("Connection", "Keep-Alive"); //需要持久连接

				InputStream inStream = http.getInputStream();
				byte[] buffer = new byte[1024];
				int offset = 0;
				print("Thread " + this.threadId + " start download from position " + startPos);

				RandomAccessFile threadfile = new RandomAccessFile(this.saveFile, "rwd");
				// 定位下载位置
				threadfile.seek(startPos);
				while (!downloader.getExit() && (offset = inStream.read(buffer, 0, 1024)) != -1) {
					threadfile.write(buffer, 0, offset);
					downLength += offset;
					downloader.update(this.threadId, downLength);
					downloader.append(offset);
				}
				threadfile.close();
				inStream.close();
				print("Thread " + this.threadId + " download finish");
				this.finish = true;
			} catch (Exception e) {
				this.downLength = -1;
				print("Thread " + this.threadId + ":" + e);
			}
		}
	}

	private static void print(String msg) {
		Log.i(TAG, msg);
	}

	/**
	 * 判断是否已经下载完
	 *
	 * @return
	 */
	public boolean isFinish() {
		return finish;
	}

	/**
	 * 已经下载的文件大小
	 */
	public long getDownLength() {
		return downLength;
	}
}
