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

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * MainActivity class that loads {@link MainFragment}.
 */
public class MainTvActivityTableLayoutStatusBar extends FragmentActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    //写一个List集合，把每个页面，也就是Fragment,存进去
    private List<Fragment> list;
    private MainTableLayoutTvActivity.MyAdapter adapter;
    private String[] titles = {"页面1", "页面2", "页面3"};

    private HorizontalScrollView horizontalScrollView;
    private LinearLayout container;
    private String cities[] = new String[]{"应用", "设置", "Paris", "Dubai", "Istanbul", "New York"};
    private ArrayList<String> data = new ArrayList<>();

    //将字符串数组与集合绑定起来
    private void bindData()
    {
        //add all cities to our ArrayList
        Collections.addAll(data, cities);
    }

    //将集合中的数据绑定到HorizontalScrollView上
    private void bindHZSWData()
    {	//为布局中textview设置好相关属性
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.setMargins(20, 10, 20, 10);

        for (int i = 0; i < data.size(); i++)
        {
            TextView textView = new TextView(this);
            textView.setText(data.get(i));
            textView.setTextColor(Color.WHITE);
            textView.setLayoutParams(layoutParams);
//            textView.setBackgroundColor(Color.BLACK);
            textView.setFocusable(true);
            ((TextView)textView).setTextSize(20);

            textView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(b){
//                        view.setBackgroundColor(Color.RED);
                        ((TextView)view).setTextSize(30);
                        performItemClick(view);
                    }else{
                        ((TextView)view).setTextSize(20);
//                        view.setBackgroundColor(Color.BLACK);
                    }
                }
            });
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        ((TextView)view).setTextSize(30);

                        performItemClick(view);
                    }
                });




            container.addView(textView);
            container.invalidate();
        }
    }

    private void performItemClick(View view) {
        //------get Display's Width--------
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int screenWidth = displayMetrics.widthPixels;

        int scrollX = (view.getLeft() - (screenWidth / 2)) + (view.getWidth() / 2);
        //smooth scrolling horizontalScrollView
        horizontalScrollView.smoothScrollTo(scrollX, 0);
        if(((TextView)view).getText().equals("设置")){
            Tab2Fragment fragment1 = new Tab2Fragment();
            addFragment(fragment1, "fragment1");
        }else{
            MainAllAppsFragment fragment1 = new MainAllAppsFragment();
            addFragment(fragment1, "fragment1");
        }

    }

    //初始化布局中的控件
    private void setUIRef()
    {
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);

        container = (LinearLayout) findViewById(R.id.horizontalScrollViewItemContainer);
//        testTextView = (TextView) findViewById(R.id.testTextView);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main_tv);
        setContentView(R.layout.activity_main_tv_tablelayout_statusbar);

        bindData();
        setUIRef();
        bindHZSWData();
    }

    private void addFragment(Fragment fragment, String tag) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, tag);
        transaction.commit();
    }

}
