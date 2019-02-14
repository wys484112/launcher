/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.example.launcher;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.launcher.util.Utilities;

import java.util.List;

/*
 * MainActivity class that loads {@link MainFragment}.
 */
public class MainTvActivity extends Activity implements BootCompleteObserver.MFragmentCallbacks{
    private static final String TAG = "MainTvActivity";
    private static final boolean DEBUG = false;
    private BootCompleteObserver mBootcompleteObserver;
    private TextView  mLoading;
    private Context context;
    private boolean isclosedFragment=false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tv_new);
        mBootcompleteObserver=new BootCompleteObserver(this);
        mLoading=(TextView)findViewById(R.id.loading);
        mLoading.setVisibility(View.GONE);


        if (DEBUG)
            Log.d(TAG, "onCreate==");

        if(!Utilities.isBootCompleted()){
            oncloseFragment();
        }
        context=this;
        TimerHandler.postDelayed(myTimerRun, 30000);        //使用postDelayed方法，30秒后再调用此myTimerRun对象

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (DEBUG)
            Log.d(TAG, "onBackPressed==");
    }

    @Override
    public void onShowFragment() {
        mLoading.setVisibility(View.GONE);
        isclosedFragment=false;
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        MainAllAppsFragment myFragment = (MainAllAppsFragment) manager.findFragmentById(R.id.main_browse_fragment);
        transaction.show(myFragment);
        transaction.commit();

        if (DEBUG) Log.e(TAG, "onShowFragment");
    }

    @Override
    public void oncloseFragment() {
        mLoading.setVisibility(View.VISIBLE);
        isclosedFragment=true;
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        MainAllAppsFragment myFragment = (MainAllAppsFragment) manager.findFragmentById(R.id.main_browse_fragment);
        transaction.hide(myFragment);
        transaction.commit();
        if (DEBUG) Log.e(TAG, "oncloseFragment");

    }

    @Override
    public boolean isclosedFragment() {
        return isclosedFragment;
    }

    Handler TimerHandler = new Handler();                   //创建一个Handler对象

    Runnable myTimerRun = new Runnable()                //创建一个runnable对象

    {
        @Override
        public void run() {
            if(isclosedFragment){
                context.sendBroadcast(new Intent(StartUpReceiver.SYSTEM_READY));
            }
        }

    };


}

class BootCompleteObserver extends BroadcastReceiver {
    private static final String TAG = "BootCompleteObserver";
    private static final boolean DEBUG = true;
    MFragmentCallbacks callbacks;

    public interface MFragmentCallbacks {
        void onShowFragment();
        boolean isclosedFragment();
        void oncloseFragment();
    }
    public BootCompleteObserver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(StartUpReceiver.SYSTEM_READY);
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        context.registerReceiver(this, filter);
        callbacks=(MFragmentCallbacks)context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (DEBUG) Log.e(TAG, "+++ onShowFragment!" +
                " Notifying Loader... +++");
        if(callbacks!=null&&callbacks.isclosedFragment()){
            callbacks.onShowFragment();
        }
        // Tell the loader about the change.
    }
}