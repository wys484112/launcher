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
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.example.launcher.util.ServiceUtils;

import java.util.List;

/*
 * MainActivity class that loads {@link MainFragment}.
 */
public class MainTvActivity extends BaseActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    //写一个List集合，把每个页面，也就是Fragment,存进去
    private List<Fragment> list;
    private MainTableLayoutTvActivity.MyAdapter adapter;
    private String[] titles = {"页面1", "页面2", "页面3"};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main_tv);
        setContentView(R.layout.activity_main_tv_new);

        mToken = ServiceUtils.bindToService(this, osc);
        if (mToken == null) {
            // something went wrong
            Toast.makeText(this, "service error!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mToken!=null){
            ServiceUtils.unbindFromService(mToken);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private boolean mIsExit;
    @Override
    /**
     * 双击返回键退出
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mIsExit) {
                this.finish();

            } else {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                mIsExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIsExit = false;
                    }
                }, 2000);
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    private ServiceUtils.ServiceToken mToken;
    private IFotaUpdateService mService = null;
    private ServiceConnection osc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName classname, IBinder obj) {
            mService = IFotaUpdateService.Stub.asInterface(obj);

            if (mService != null) {
                try {
                    mService.startVersionUpdate(false);
                } catch (RemoteException ex) {
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName classname) {
            Log.e("mmmm","onServiceDisconnected");
            mService = null;
        }
    };
}
