package com.example.launcher.ui;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.launcher.R;

public class RowApp extends LinearLayout {
    private TextView textView;
    private ImageView imageview;

    private boolean isAnimateApp;
    private CharSequence pkg;
    private CharSequence cls;
    private int title=-1;

    private int backgroundResource;
    private int imageviewBackgroundResource=-1;

    public TextView getTextView() {
        return textView;
    }

    public ImageView getImageview() {
        return imageview;
    }
    public void setImageDrawable(Drawable drawable) {

    }
    public final void setText(CharSequence text) {
        if (textView == null) {
            return;
        }
        textView.setText(text);
    }
    private AnimationDrawable mAppsAnimation;

    public RowApp(Context context) {
        this(context,null);
        Log.d("wwww","asdfasdfdddd444");

    }

    public RowApp(Context context, AttributeSet attrs) {
        this(context,attrs,0);
        Log.d("wwww","asdfasdfdddd333");

    }

    public RowApp(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context,attrs,0,0);
        Log.d("wwww","asdfasdfdddd");

    }

    public RowApp(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Log.d("wwww","asdfasdf");
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RowApp, defStyleAttr, 0);
        try {
            isAnimateApp = a.getBoolean(R.styleable.RowApp_rowisAnimateApp,false);
            pkg = a.getText(R.styleable.RowApp_rowcomponentPKG);
            cls = a.getText(R.styleable.RowApp_rowcomponentCLS);
            imageviewBackgroundResource = a.getResourceId(R.styleable.RowApp_rowimageviewBackgroundResource,0);
            title = a.getResourceId(R.styleable.RowApp_rowtitle,-1);
            backgroundResource =a.getResourceId(R.styleable.RowApp_rowbackgroundResource,-1);
        } finally {
            a.recycle();
        }


        LayoutInflater.from(context).inflate(R.layout.row_app, this);
        textView = (TextView) findViewById(R.id.title);
        imageview = (ImageView) findViewById(R.id.image);
        init();
    }




    private void init(){
        if(title!=-1){
            textView.setText(title);
        }
        if(imageviewBackgroundResource!=-1){
            imageview.setBackgroundResource(imageviewBackgroundResource);
        }

        if(isAnimateApp){
            this.setBackground(null);
            imageview.setClickable(true);
            imageview.setFocusable(true);
            imageview.setFocusableInTouchMode(true);
            this.setOnFocusChangeListener(new View.OnFocusChangeListener(){
                @Override
                public void onFocusChange(View var1, boolean var2) {
                    if(var2){
                        imageview.requestFocus();
                    }else{
                    }

                }
            });


            imageview.setOnFocusChangeListener(new View.OnFocusChangeListener(){
                @Override
                public void onFocusChange(View var1, boolean var2) {
                    imageview.setBackgroundResource(imageviewBackgroundResource);
                    mAppsAnimation = (AnimationDrawable) imageview.getBackground();
                    if(var2){
                        mAppsAnimation.start();
                    }else{
                        mAppsAnimation.stop();
                        mAppsAnimation.selectDrawable(0);
                        mAppsAnimation=null;
                    }
                }
            });
            imageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startNewActivity();
                }
            });

        }else{
            this.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startNewActivity();
                }
            });
        }
    }

    private void startNewActivity(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        try {
            ComponentName componentName = new ComponentName(pkg.toString(), cls.toString());
            intent.setComponent(componentName);
            getContext().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }
}
