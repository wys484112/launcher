package com.example.launcher.customview;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.launcher.R;

public class CustomTabItem extends RelativeLayout {
    private TextView textView;
    private TextView cursor;


    public void setText(String text){
        textView.setText(text);
    }
    public void setTextColor(int color){
        textView.setTextColor(color);
    }
    public void setLayoutParams(ViewGroup.LayoutParams layoutParams){
        textView.setLayoutParams(layoutParams);
    }
    public void setTextSize(float size){
        textView.setTextSize(size);
    }
    public int getTextWidth(){
        return textView.getWidth();
    }
    public CustomTabItem(Context context) {
        this(context,null);
        Log.d("wwww","asdfasdfdddd444");

    }

    public CustomTabItem(Context context, AttributeSet attrs) {
        this(context,attrs,0);
        Log.d("wwww","asdfasdfdddd333");

    }

    public CustomTabItem(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context,attrs,0,0);
        Log.d("wwww","asdfasdfdddd");

    }

    public CustomTabItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Log.d("wwww","asdfasdf");
        Log.d("wwww","CustomTabItem");


        View root  = LayoutInflater.from(context).inflate(R.layout.tabitem, this);
        textView = (TextView) root.findViewById(R.id.textView);
        cursor = (TextView) root.findViewById(R.id.cursor);
    }




//    private void init(){
//        if(title!=-1){
//            textView.setText(title);
//        }
//        if(imageviewBackgroundResource!=-1){
//            imageview.setBackgroundResource(imageviewBackgroundResource);
//        }
//
//        if(isAnimateApp){
//            this.setBackground(null);
//            imageview.setClickable(true);
//            imageview.setFocusable(true);
//            imageview.setFocusableInTouchMode(true);
//            this.setOnFocusChangeListener(new OnFocusChangeListener(){
//                @Override
//                public void onFocusChange(View var1, boolean var2) {
//                    if(var2){
//                        imageview.requestFocus();
//                    }else{
//                    }
//
//                }
//            });
//
//
//            imageview.setOnFocusChangeListener(new OnFocusChangeListener(){
//                @Override
//                public void onFocusChange(View var1, boolean var2) {
//                    imageview.setBackgroundResource(imageviewBackgroundResource);
//                    mAppsAnimation = (AnimationDrawable) imageview.getBackground();
//                    if(var2){
//                        mAppsAnimation.start();
//                    }else{
//                        mAppsAnimation.stop();
//                        mAppsAnimation.selectDrawable(0);
//                        mAppsAnimation=null;
//                    }
//                }
//            });
//            imageview.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    startNewActivity();
//                }
//            });
//
//        }else{
//            this.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    startNewActivity();
//                }
//            });
//        }
//    }
//
//    private void startNewActivity(){
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        try {
//            ComponentName componentName = new ComponentName(pkg.toString(), cls.toString());
//            intent.setComponent(componentName);
//            getContext().startActivity(intent);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//        }
//    }
}
