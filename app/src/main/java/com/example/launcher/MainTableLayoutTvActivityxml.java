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
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/*
 * MainActivity class that loads {@link MainFragment}.
 */
public class MainTableLayoutTvActivityxml extends Activity {

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

    private void InitImageView() {
        cursor = (ImageView) findViewById(R.id.cursor);
//        bmpW=getResources().getDimensionPixelOffset(R.dimen.tab_pointer_width);
        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.apps1).getWidth();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;// 获取屏幕分辨率宽度
        offset = (screenW / 3 - bmpW) / 2; // 计算偏移量:屏幕宽度/3，平分为3分，如果是3个view的话，再减去图片宽度，因为图片居中，所以要得到两变剩下的空隙需要再除以2
        Matrix matrix = new Matrix();
        Log.e("wwww","MyOnClickListener offset="+offset);
        Log.e("wwww","MyOnClickListener bmpW="+bmpW);
        Log.e("wwww","MyOnClickListener screenW="+screenW);

        matrix.postTranslate(offset, 0);  // 初始化位置，在中间
        cursor.setImageMatrix(matrix);   // 设置动画初始位置
    }

    private void InitTextView() {
        t1 = (TextView) findViewById(R.id.text1);
        t2 = (TextView) findViewById(R.id.text2);
        t3 = (TextView) findViewById(R.id.text3);
        t1.setClickable(true);
        t1.setOnClickListener(new MyOnClickListener(0));
        t2.setOnClickListener(new MyOnClickListener(1));
        t3.setOnClickListener(new MyOnClickListener(2));
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablelayout_mainxml);
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

        InitTextView();
        InitImageView();
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

            switch (arg0) {
                case 0:
                    if (currIndex == 1)
                    {
                        animation = new TranslateAnimation(one, 0, 0, 0);
                    }
                    else if (currIndex == 2)
                    {
                        animation = new TranslateAnimation(two, 0, 0, 0);
                    }
                    break;
                case 1:
                    if (currIndex == 0)
                    {
                        animation = new TranslateAnimation(offset, one, 0, 0);
                    }
                    else if (currIndex == 2)
                    {
                        animation = new TranslateAnimation(two, one, 0, 0);
                    }
                    break;
                case 2:
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(offset, two, 0, 0);
                    } else if (currIndex == 1) {
                        animation = new TranslateAnimation(one, two, 0, 0);
                    }
                    break;
            }
            currIndex = arg0;
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
//                    cursor.clearAnimation();
//                    int left = cursor.getLeft()+100;
//                    int top = cursor.getTop();
//                    int width = cursor.getWidth();
//                    int height = cursor.getHeight();
//                    cursor.layout(left, top, left+width, top+height);

                }
            });

            AnimationSet set = new AnimationSet(true);

            TranslateAnimation translate = new TranslateAnimation(

                    Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0.5f,

                    Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0);

            set.addAnimation(animation);
            set.setDuration(300);
            set.setFillAfter(true);// True:图片停在动画结束位置
            cursor.startAnimation(set);
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
