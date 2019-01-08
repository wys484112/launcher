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
    private String cities[] = new String[]{"应用", "设置", "Paris"};
    private ArrayList<String> data = new ArrayList<>();
    private TextView currentClickedTextView;
    private ArrayList<TextView> tabs = new ArrayList<>();

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
            final TextView textView = new TextView(this);
            textView.setText(data.get(i));
            textView.setTextColor(Color.WHITE);
            textView.setLayoutParams(layoutParams);
            if(i==viewPager.getCurrentItem()){
                ((TextView)textView).setTextSize(30);

            }else{
                ((TextView)textView).setTextSize(20);

            }
            textView.setOnClickListener(new MyOnClickListener(i));
            tabs.add(textView);
            textView.post(new Runnable() {
                @Override
                public void run() {
                    int width=textView.getWidth();
                    float x=textView.getX();
                    Log.e("wwww","Runnable x="+x);

                    Log.e("wwww","Runnable width="+width);

                }
            });
            textView.addOnLayoutChangeListener(new MyOnLayoutChangeListener(i));
            container.addView(textView);
            container.requestLayout();
            container.invalidate();
        }
    }
    private void InitCursorView(int width,float x) {
        cursorTextView = (TextView) findViewById(R.id.tab_pointer);
        ViewGroup.MarginLayoutParams layoutParams =
                (ViewGroup.MarginLayoutParams) tabs.get(currIndex).getLayoutParams();
        int leftMargin=layoutParams.leftMargin;
//        int left=tabs.get(viewPager.getCurrentItem()).getLeft();

        Log.e("wwww","InitCursorView leftMargin="+leftMargin);
        Log.e("wwww","InitCursorView x="+x);
        Log.e("wwww","InitCursorView width="+width);

        ViewGroup.MarginLayoutParams cursorLayoutParams =
                (ViewGroup.MarginLayoutParams) cursorTextView.getLayoutParams();
        cursorLayoutParams.setMarginStart(leftMargin);
        cursorLayoutParams.width=width;
        cursorTextView.setTextColor(Color.RED);

        cursorTextView.setX((float) x);
//        cursorTextView.setLeft(left);
        cursorTextView.setWidth(width);
        cursorTextView.setLayoutParams(cursorLayoutParams);
        cursorTextView.requestLayout();
        cursorTextView.invalidate();
//        cursorTextView.addOnLayoutChangeListener();

    }
    private void InitCursorView() {
        cursorTextView = (TextView) findViewById(R.id.tab_pointer);
        ViewGroup.MarginLayoutParams layoutParams =
                (ViewGroup.MarginLayoutParams) tabs.get(viewPager.getCurrentItem()).getLayoutParams();
        int width=tabs.get(viewPager.getCurrentItem()).getWidth();
        int leftMargin=layoutParams.leftMargin;
        float x=tabs.get(viewPager.getCurrentItem()).getX();
//        int left=tabs.get(viewPager.getCurrentItem()).getLeft();

        Log.e("wwww","InitCursorView leftMargin="+leftMargin);
        Log.e("wwww","InitCursorView x="+x);

        ViewGroup.MarginLayoutParams cursorLayoutParams =
                (ViewGroup.MarginLayoutParams) cursorTextView.getLayoutParams();
        cursorLayoutParams.setMarginStart(leftMargin);
        cursorLayoutParams.width=width;
        cursorTextView.setTextColor(Color.RED);

        cursorTextView.setX((float) x);
//        cursorTextView.setLeft(left);
        cursorTextView.setWidth(width);
        cursorTextView.setLayoutParams(cursorLayoutParams);
        cursorTextView.invalidate();
//        cursorTextView.addOnLayoutChangeListener();

    }

    public class MyOnLayoutChangeListener implements View.OnLayoutChangeListener {
        private int index = 0;
        public MyOnLayoutChangeListener(int i) {
            index = i;
        }
        @Override
        public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
//                    view.removeOnLayoutChangeListener(this);
            int width=view.getWidth();
            float x=view.getX();
            if(index==currIndex){
                InitCursorView(width,x);
            }
            Log.e("wwww","onLayoutChange x="+x);
            Log.e("wwww","onLayoutChange width="+width);
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            int leftMargin=layoutParams.leftMargin;
            Log.e("wwww","onLayoutChange leftMargin="+leftMargin);
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
              int oldindex=currIndex;
              currIndex=index;
              tabs.get(oldindex).setTextSize(20);
//              tabs.get(currIndex).requestLayout();
//              tabs.get(currIndex).invalidate();
              viewPager.setCurrentItem(index);
              tabs.get(index).setTextSize(30);
              Log.e("wwww","MyOnClickListener getWidth="+tabs.get(index).getWidth());

//              tabs.get(index).requestLayout();
//              tabs.get(index).invalidate();
//              InitCursorView();

          }
      }

    ;
    //初始化布局中的控件
    private void setUIRef()
    {
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
        container = (LinearLayout) findViewById(R.id.horizontalScrollViewItemContainer);
        cursorTextView = (TextView) findViewById(R.id.tab_pointer);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablelayout_mainxmlhorizontalscrollview);
        //实例化
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        //页面，数据源，里面是创建的三个页面（Fragment）
        list = new ArrayList<View>();
                 LayoutInflater mInflater = getLayoutInflater();
        list.add(mInflater.inflate(R.layout.activity_viewpagexml, null));
        list.add(mInflater.inflate(R.layout.activity_viewpagexml2, null));
        list.add(mInflater.inflate(R.layout.activity_viewpagexml3, null));


        viewPager.setAdapter(new MyPagerAdapter(list));
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());


        bindData();
        setUIRef();
        bindHZSWData();
//        tabs.get(viewPager.getCurrentItem()).setTextSize(30);
//        tabs.get(viewPager.getCurrentItem()).requestLayout();
//        tabs.get(viewPager.getCurrentItem()).invalidate();
//        InitCursorView();

        //将TabLayout和ViewPager绑定在一起，一个动另一个也会跟着动
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
            tabs.get(currIndex).setTextSize(20);
            tabs.get(currIndex).requestLayout();
            tabs.get(currIndex).invalidate();
            tabs.get(arg0).setTextSize(30);
            tabs.get(arg0).requestLayout();
            tabs.get(arg0).invalidate();
            currIndex=arg0;
//            InitCursorView();

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

//            //获取父布局布局管理器
//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(cursor.getLayoutParams());
//            /**
//             *   设置margin的值思路主要是position的值为当前fragment的下标 positionOffset为比例数当第一个页面滑动到第二个的时候positionOffset= 0
//             *   如果只采用item_width * position显得有些僵硬如果加上item_width * positionOffset就有动画的效果了
//             */
//            layoutParams.setMargins((int) (item_width * positionOffset) + item_width * position, 0, 0, 0);
//            cursor.setLayoutParams(layoutParams);

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }
}
