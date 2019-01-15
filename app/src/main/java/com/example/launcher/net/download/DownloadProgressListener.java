package com.example.launcher.net.download;

import java.io.File;

public interface DownloadProgressListener {
	public void onDownloadGetFileSize(int totalSize);

	public void onDownloadSize(int size);
	public void onDownloadComplete(File saveFilePath);
	public void onDownloadError(Exception e);

}
