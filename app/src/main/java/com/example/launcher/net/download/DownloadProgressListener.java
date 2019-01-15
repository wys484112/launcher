package com.example.launcher.net.download;

public interface DownloadProgressListener {
	public void onDownloadGetFileSize(int totalSize);

	public void onDownloadSize(int size);
	public void onDownloadComplete();
	public void onDownloadError(Exception e);

}
