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
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.launcher.ui.CustomTabItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * MainActivity class that loads {@link MainFragment}.
 */
public class MainTableLayoutTvActivityxmlHorizontalScrollView extends Activity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    //写一个List集合，把每个页面，也就是Fragment,存进去
    private List<View> list;
    private MyPagerAdapter adapter;
    private String[] titles = {"页面1", "页面2", "页面3"};
    private TextView t1, t2, t3;// 页卡头标
    private int offset = 0;//动画图片偏移量
    private int currIndex = 0;//当前页卡编号
    private ImageView cursor;// 动画图片
    private int bmpW;//动画图片宽度
    private HorizontalScrollView horizontalScrollView;
    private LinearLayout container;
    private TextView cursorTextView;
    private String cities[] = new String[]{"应用应用应用应用应用", "设置", "Paris"};
    private ArrayList<String> data = new ArrayList<>();
    private TextView currentClickedTextView;
    private ArrayList<View> tabs = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablelayout_mainxmlhorizontalscrollview);
        //实例化
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        //页面，数据源，里面是创建的三个页面（Fragment）
        list = new ArrayList<View>();
                 LayoutInflater mInflater = getLayoutInflater();
        list.add(mInflater.inflate(R.layout.activity_viewpagexml3, null));
        list.add(mInflater.inflate(R.layout.activity_viewpagexml2, null));
        list.add(mInflater.inflate(R.layout.activity_viewpagexml, null));


        viewPager.setAdapter(new MyPagerAdapter(list));
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());

        bindData();
        setUIRef();
        bindHZSWData();
    }
    //创建Fragment的适配器
    /**
     * ViewPager适配器
     */
    public class MyPagerAdapter extends PagerAdapter {
        public List<View> mListViews;

        public MyPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(mListViews.get(arg1));
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(mListViews.get(arg1), 0);
            return mListViews.get(arg1);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }
    }
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {


        @Override
        public void onPageSelected(int arg0) {
            int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
            int two = one * 2;// 页卡1 -> 页卡3 偏移量
            Animation animation = null;
            Log.e("wwww","MyOnPageChangeListener arg0="+arg0);
            int oldindex=currIndex;
            currIndex=arg0;
//            tabs.get(oldindex).findViewById(R.id.cursor).setVisibility(View.GONE);
            for(int i=0;i<tabs.size();i++){
                if(arg0==i){
                    tabs.get(arg0).findViewById(R.id.cursor).setVisibility(View.VISIBLE);
                    tabs.get(arg0).findViewById(R.id.textView).requestFocus();
                    ((TextView)tabs.get(arg0).findViewById(R.id.textView)).setTextSize(20);
                    ((TextView)tabs.get(arg0).findViewById(R.id.cursor)).setTextSize(20);


                }else{
                    tabs.get(i).findViewById(R.id.cursor).setVisibility(View.GONE);
                    ((TextView)tabs.get(i).findViewById(R.id.textView)).setTextSize(18);
                    ((TextView)tabs.get(i).findViewById(R.id.cursor)).setTextSize(18);

                }
            }

//            InitCursorView();

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }


    //将字符串数组与集合绑定起来
    private void bindData()
    {
        Collections.addAll(data, cities);
    }
    //将集合中的数据绑定到HorizontalScrollView上
    private void bindHZSWData()
    {	//为布局中textview设置好相关属性

        for (int i = 0; i < data.size(); i++)
        {
            View root  = LayoutInflater.from(this).inflate(R.layout.tabitem, container,false);
            TextView txt = (TextView) root
                    .findViewById(R.id.textView);
            TextView cursor = (TextView) root
                    .findViewById(R.id.cursor);
            txt.setText(data.get(i));

            txt.setTextColor(Color.WHITE);
            txt.setTextSize(18);
            cursor.setText(data.get(i));
            cursor.setTextSize(18);
            if(i==viewPager.getCurrentItem()){
                cursor.setVisibility(View.VISIBLE);

            }else{
                cursor.setVisibility(View.GONE);

            }

            txt.setOnClickListener(new MyOnClickListener(i));
            txt.setOnFocusChangeListener(new MyOnFocusChangeListener(i));
            tabs.add(root);
            Log.e("wwww","getTextWidth CustomTabItem="+txt.getWidth());
            container.addView(root);
        }
    }


    public class MyOnFocusChangeListener implements View.OnFocusChangeListener {
        private int index = 0;
        public MyOnFocusChangeListener(int i) {
            index = i;
        }

        @Override
        public void onFocusChange(View view, boolean b) {
            if(b){
                Log.e("wwww","onFocusChange index="+index);
                if(viewPager.getCurrentItem()!=index){
                    viewPager.setCurrentItem(index);
                }
            }else{
            }

        }
    }
    /**
     2      * 头标点击监听
     3 */
    public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            Log.e("wwww","MyOnClickListener index="+index);
            viewPager.setCurrentItem(index);

        }
    }

    ;
    //初始化布局中的控件
    private void setUIRef()
    {
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
        container = (LinearLayout) findViewById(R.id.horizontalScrollViewItemContainer);
    }
}
