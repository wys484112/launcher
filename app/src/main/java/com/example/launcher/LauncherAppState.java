package com.example.launcher;

import com.example.launcher.util.IconCache;

import android.content.Context;
import android.util.Log;

/**
 * Created by rharter on 1/11/14.
 */
public class LauncherAppState {

    private IconCache mIconCache;

    private static Context sContext;

    private static LauncherAppState INSTANCE;

    public static LauncherAppState getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LauncherAppState();
        }
        return INSTANCE;
    }

    public Context getContext() {
        return sContext;
    }

    public static void setApplicationContext(Context context) {
        if (sContext != null) {
            Log.w("Launcher",
                    "setApplicationContext called twice! old=" + sContext + " new=" + context);
        }
        sContext = context;
    }

    private LauncherAppState() {
        if (sContext == null) {
            throw new IllegalStateException("LauncherAppState inited before app context set.");
        }

        mIconCache = new IconCache(sContext);
    }

    public IconCache getIconCache() {
        return mIconCache;
    }

    public void setIconCache(IconCache iconCache) {
        mIconCache = iconCache;
    }
}
