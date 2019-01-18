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
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;

import java.util.List;

/*
 * MainActivity class that loads {@link MainFragment}.
 */
public class MainTvActivity extends Activity {
    private static final String TAG = "MainTvActivity";
    private static final boolean DEBUG = true;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    //写一个List集合，把每个页面，也就是Fragment,存进去
    private List<Fragment> list;
    private String[] titles = {"页面1", "页面2", "页面3"};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main_tv);
        setContentView(R.layout.activity_main_tv_new);
        if (DEBUG)
            Log.d(TAG, "onCreate==");
    }

    public MainTvActivity() {
        super();
        if (DEBUG)
            Log.d(TAG, "MainTvActivity==");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (DEBUG)
            Log.d(TAG, "onStart==");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (DEBUG)
            Log.d(TAG, "onRestart==");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (DEBUG)
            Log.d(TAG, "onResume==");
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (DEBUG)
            Log.d(TAG, "onPostResume==");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (DEBUG)
            Log.d(TAG, "onPause==");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (DEBUG)
            Log.d(TAG, "onStop==");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (DEBUG)
            Log.d(TAG, "onDestroy==");
    }

    @Override
    public void onBackPressed() {

//        super.onBackPressed();
        if (DEBUG)
            Log.d(TAG, "onBackPressed==");
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (DEBUG)
            Log.d(TAG, "onAttachedToWindow==");
    }
}
