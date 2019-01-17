package com.example.launcher.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;
import android.util.Log;

import com.example.launcher.service.fotaudpate.FotaUpdateService;
import com.example.launcher.IFotaUpdateService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;      
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
public class ServiceUtils {
	private static final String TAG = "aaa";
    private static final boolean DBG = true;

	/**
	 * �жϷ����Ƿ���
	 * 
	 * @return
	 */
	public static boolean isServiceRunning(Context context, String ServiceName) {
		if (("").equals(ServiceName) || ServiceName == null)
			return false;
		ActivityManager myManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager
				.getRunningServices(30);
		for (int i = 0; i < runningService.size(); i++) {
			if (runningService.get(i).service.getClassName().toString()
					.contains(ServiceName)) {
				return true;
			}
		}
		return false;
	}



	public static void stopPostData() {
		if (sService != null) {
			try {
				sService.stopPostData();
			} catch (RemoteException ex) {
			}
		}
	}

	public static IFotaUpdateService sService = null;
	private static HashMap<Context, ServiceBinder> sConnectionMap = new HashMap<Context, ServiceBinder>();

	public static class ServiceToken {
		ContextWrapper mWrappedContext;
		ServiceToken(ContextWrapper context) {
			mWrappedContext = context;
		}
	}

	public static ServiceToken bindToService(Activity context) {
		return bindToService(context, null);
	}

	public static ServiceToken bindToService(Activity context, ServiceConnection callback) {
		Activity realActivity = context.getParent();
		if (realActivity == null) {
			realActivity = context;
		}
		ContextWrapper cw = new ContextWrapper(realActivity);
		cw.startService(new Intent(cw, FotaUpdateService.class));
		ServiceBinder sb = new ServiceBinder(callback);
		if (cw.bindService((new Intent()).setClass(cw, FotaUpdateService.class), sb, 0)) {
			sConnectionMap.put(cw, sb);
            if (DBG)
                Log.e(TAG, "bind to service");
			return new ServiceToken(cw);
		}
        if (DBG)
            Log.e(TAG, "Failed to bind to service");
		return null;
	}

	public static void unbindFromService(ServiceToken token) {
		if (token == null) {
			Log.e(TAG, "Trying to unbind with null token");
			return;
		}
		ContextWrapper cw = token.mWrappedContext;
		ServiceBinder sb = sConnectionMap.remove(cw);
		if (sb == null) {
            if (DBG)
                Log.e(TAG, "Trying to unbind for unknown Context");
			return;
		}
		cw.unbindService(sb);
		if (sConnectionMap.isEmpty()) {
			// presumably there is nobody interested in the service at this point,
			// so don't hang on to the ServiceConnection
            if (DBG)
                Log.e(TAG, "unbind to service");

			sService = null;
		}
	}

	private static class ServiceBinder implements ServiceConnection {
		ServiceConnection mCallback;
		ServiceBinder(ServiceConnection callback) {
			mCallback = callback;
		}

		public void onServiceConnected(ComponentName className, android.os.IBinder service) {
			sService = IFotaUpdateService.Stub.asInterface(service);
//			initAlbumArtCache();
			if (mCallback != null) {
				mCallback.onServiceConnected(className, service);
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			if (mCallback != null) {
				mCallback.onServiceDisconnected(className);
			}
			sService = null;
		}
	}
}