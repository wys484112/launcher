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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/*
 * MainActivity class that loads {@link MainFragment}.
 */
public class MainTableLayoutTvActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    //写一个List集合，把每个页面，也就是Fragment,存进去
    private List<Fragment> list;
    private MyAdapter adapter;
    private String[] titles = {"页面1", "页面2", "页面3"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablelayout_main);
        //实例化
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        //页面，数据源，里面是创建的三个页面（Fragment）
        list = new ArrayList<>();
        list.add(new Tab1Fragment());
        list.add(new Tab1Fragment());
        list.add(new Tab1Fragment());
        //ViewPager的适配器，获得Fragment管理器
        adapter = new MyAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        //将TabLayout和ViewPager绑定在一起，一个动另一个也会跟着动
        tabLayout.setupWithViewPager(viewPager);
    }
    //创建Fragment的适配器
    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }
        //获得每个页面的下标
        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }
        //获得List的大小
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
