package com.example.launcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class StartUpReceiver extends BroadcastReceiver {
    public static final String SYSTEM_READY = "com.example.launcher.SYSTEM_READY";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d("StartUpReceiver","boot complete");
        context.sendStickyBroadcast(new Intent(SYSTEM_READY));

    }
}
