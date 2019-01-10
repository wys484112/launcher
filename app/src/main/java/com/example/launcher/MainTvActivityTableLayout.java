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
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentManager.BackStackEntry;
import android.app.FragmentTransaction;
import android.telephony.NeighboringCellInfo;
import android.util.Log;


import java.util.List;

/*
 * MainActivity class that loads {@link MainFragment}.
 */
public class MainTvActivityTableLayout extends Activity {
    private static final String TAG = "MTATL";
    private static final boolean DEBUG = false;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    //写一个List集合，把每个页面，也就是Fragment,存进去
    private List<Fragment> list;
    private MainTableLayoutTvActivity.MyAdapter adapter;
    private String[] titles = {"页面1", "页面2", "页面3"};
    MainAllAppsFragment fragment1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main_tv);
        setContentView(R.layout.activity_main_tv_tablelayout);
        fragment1 = new MainAllAppsFragment();
        if (DEBUG)
            Log.d(TAG, "MainTvActivityTableLayout==");

    }

    private void addFragment(MainAllAppsFragment fragment, String tag) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, tag);
        transaction.commit();
    }

    private void removeFragment(MainAllAppsFragment fragment) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.remove(fragment);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (DEBUG)
            Log.d(TAG, "MainTvActivityTableLayout==    onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (DEBUG)
            Log.d(TAG, "MainTvActivityTableLayout==    onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (DEBUG)
            Log.d(TAG, "MainTvActivityTableLayout==    onStop");
    }

    @Override
    protected void onPause() {
        if (DEBUG)
            Log.d(TAG, "MainTvActivityTableLayout==    onPause");
        super.onPause();
        fragment1.dismissProgressDialog();
        removeFragment(fragment1);
    }

    @Override
    protected void onResume() {
        if (DEBUG)
            Log.d("wwwwww", "MainTvActivityTableLayout==    onResume");
        super.onResume();
        addFragment(fragment1, "fragment1");
    }
}
