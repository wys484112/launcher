package com.example.launcher;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	private  TextView mApps_title,mSettings_title,mFilemanager_title,mBrowser_title,mMediaplayer_title;

	private ImageView mApps_imageview,mSettings_imageview,mFilemanager_imageview,mBrowser_imageview,mMediaplayer_imageview;
    private View mApps;
    private View mSettings;
    private View mFilemanager;
    private View mBrowser;
    private View mMediaplayer;

    private AnimationDrawable mAppsAnimation;

    private View mApp_cfc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);



        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
// ÆÁÄ»¿í¶È£¨ÏñËØ£©
        int width = metric.widthPixels;
// ÆÁÄ»¸ß¶È£¨ÏñËØ£©
        int height = metric.heightPixels;
// ÆÁÄ»ÃÜ¶È£¨1.0 / 1.5 / 2.0£©
        float density = metric.density;
// ÆÁÄ»ÃÜ¶ÈDPI£¨160 / 240 / 320£©
        int densityDpi = metric.densityDpi;
        String info = "»ú¶¥ºÐÐÍºÅ: " + android.os.Build.MODEL + ",\nSDK°æ±¾:"
                + android.os.Build.VERSION.SDK + ",\nÏµÍ³°æ±¾:"
                + android.os.Build.VERSION.RELEASE  + "\nÆÁÄ»¿í¶È£¨ÏñËØ£©: "                       +width + "\nÆÁÄ»¸ß¶È£¨ÏñËØ£©: " + height + "\nÆÁÄ»ÃÜ¶È:  "                              +density+"\nÆÁÄ»ÃÜ¶ÈDPI: "+densityDpi;
        Log.d("wwww", info);

		mApps = findViewById(R.id.apps);
		mApps.setBackground(null);
        mApps_imageview= (ImageView)mApps.findViewById(R.id.image);
        mApps_imageview.setImageResource(R.drawable.icon_app);
        mApps_title= (TextView)mApps.findViewById(R.id.title);
        mApps_title.setText(R.string.apps_title);
        mApps_imageview.setClickable(true);
        mApps_imageview.setFocusable(true);
        mApps_imageview.setFocusableInTouchMode(true);
		mApps.setOnFocusChangeListener(new View.OnFocusChangeListener(){
			@Override
			public void onFocusChange(View var1, boolean var2) {
                if(var2){
                    mApps_imageview.requestFocus();
                }else{
                }

			}
		});
        mApps_imageview.setOnFocusChangeListener(new View.OnFocusChangeListener(){
			@Override
			public void onFocusChange(View var1, boolean var2) {
                mApps_imageview.setImageResource(R.drawable.animation_apps_bg);
				mAppsAnimation = (AnimationDrawable) mApps_imageview.getDrawable();
				if(var2){
					mAppsAnimation.start();
				}else{
					mAppsAnimation.stop();
					mAppsAnimation.selectDrawable(0);
                    mAppsAnimation=null;
                    mApps_imageview.setImageResource(R.drawable.icon_app);

				}
			}
		});
        mApps_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,AllAppList.class);
                startActivity(intent);
            }
        });

//two
        mSettings = findViewById(R.id.settings);
        mSettings.setBackground(null);
        mSettings_imageview= (ImageView)mSettings.findViewById(R.id.image);
        mSettings_imageview.setImageResource(R.drawable.icon_setting);
        mSettings_title= (TextView)mSettings.findViewById(R.id.title);
        mSettings_title.setText(R.string.settings_title);
        mSettings_imageview.setClickable(true);
        mSettings_imageview.setFocusable(true);
        mSettings_imageview.setFocusableInTouchMode(true);
        mSettings.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View var1, boolean var2) {
                if(var2){
                    mSettings_imageview.requestFocus();
                }else{
                }

            }
        });
        mSettings_imageview.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View var1, boolean var2) {
                mSettings_imageview.setImageResource(R.drawable.animation_settings_bg);
                mAppsAnimation = (AnimationDrawable) mSettings_imageview.getDrawable();
                if(var2){
                    mAppsAnimation.start();
                }else{
                    mAppsAnimation.stop();
                    mAppsAnimation.selectDrawable(0);
                    mAppsAnimation=null;
                    mSettings_imageview.setImageResource(R.drawable.icon_setting);
                }
            }
        });
        mSettings_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                try {
                    ComponentName componentName = new ComponentName("com.android.settings", "com.android.settings.Settings");
                    intent.setComponent(componentName);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }
        });





//three
        mFilemanager = findViewById(R.id.filemanager);
        mFilemanager.setBackground(null);
        mFilemanager_imageview= (ImageView)mFilemanager.findViewById(R.id.image);
        mFilemanager_imageview.setImageResource(R.drawable.icon_file);
        mFilemanager_title= (TextView)mFilemanager.findViewById(R.id.title);
        mFilemanager_title.setText(R.string.filemanager_title);
        mFilemanager_imageview.setClickable(true);
        mFilemanager_imageview.setFocusable(true);
        mFilemanager_imageview.setFocusableInTouchMode(true);
        mFilemanager.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View var1, boolean var2) {
                if(var2){
                    mFilemanager_imageview.requestFocus();
                }else{
                }

            }
        });
        mFilemanager_imageview.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View var1, boolean var2) {
                mFilemanager_imageview.setImageResource(R.drawable.animation_filemanager_bg);
                mAppsAnimation = (AnimationDrawable) mFilemanager_imageview.getDrawable();
                if(var2){
                    mAppsAnimation.start();
                }else{
                    mAppsAnimation.stop();
                    mAppsAnimation.selectDrawable(0);
                    mAppsAnimation=null;
                    mFilemanager_imageview.setImageResource(R.drawable.icon_file);
                }
            }
        });
        mFilemanager_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                try {
                    ComponentName componentName = new ComponentName("com.estrongs.android.pop", "com.estrongs.android.pop.app.openscreenad.NewSplashActivity");
                    intent.setComponent(componentName);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }
        });


//four
        mBrowser = findViewById(R.id.browser);
        mBrowser.setBackground(null);
        mBrowser_imageview= (ImageView)mBrowser.findViewById(R.id.image);
        mBrowser_imageview.setImageResource(R.drawable.icon_browser);
        mBrowser_title= (TextView)mBrowser.findViewById(R.id.title);
        mBrowser_title.setText(R.string.browser_title);
        mBrowser_imageview.setClickable(true);
        mBrowser_imageview.setFocusable(true);
        mBrowser_imageview.setFocusableInTouchMode(true);
        mBrowser.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View var1, boolean var2) {
                if(var2){
                    mBrowser_imageview.requestFocus();
                }else{
                }

            }
        });
        mBrowser_imageview.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View var1, boolean var2) {
                mBrowser_imageview.setImageResource(R.drawable.animation_browser_bg);
                mAppsAnimation = (AnimationDrawable) mBrowser_imageview.getDrawable();
                if(var2){
                    mAppsAnimation.start();
                }else{
                    mAppsAnimation.stop();
                    mAppsAnimation.selectDrawable(0);
                    mAppsAnimation=null;
                    mBrowser_imageview.setImageResource(R.drawable.icon_browser);
                }
            }
        });
        mBrowser_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                try {
                    ComponentName componentName = new ComponentName("com.fanshi.tvbrowser", "com.fanshi.tvbrowser.MainActivity");
                    intent.setComponent(componentName);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }

//                Intent intent = new Intent(MainActivity.this,AllAppList.class);
//                startActivity(intent);
            }
        });



//five
        mMediaplayer = findViewById(R.id.meidaplayer);
        mMediaplayer.setBackground(null);
        mMediaplayer_imageview= (ImageView)mMediaplayer.findViewById(R.id.image);
        mMediaplayer_imageview.setImageResource(R.drawable.icon_media);
        mMediaplayer_title= (TextView)mMediaplayer.findViewById(R.id.title);
        mMediaplayer_title.setText(R.string.mediaplayer_title);
        mMediaplayer_imageview.setClickable(true);
        mMediaplayer_imageview.setFocusable(true);
        mMediaplayer_imageview.setFocusableInTouchMode(true);
        mMediaplayer.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View var1, boolean var2) {
                if(var2){
                    mMediaplayer_imageview.requestFocus();
                }else{
                }

            }
        });
        mMediaplayer_imageview.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View var1, boolean var2) {
                mMediaplayer_imageview.setImageResource(R.drawable.animation_mediaplayer_bg);
                mAppsAnimation = (AnimationDrawable) mMediaplayer_imageview.getDrawable();
                if(var2){
                    mAppsAnimation.start();
                }else{
                    mAppsAnimation.stop();
                    mAppsAnimation.selectDrawable(0);
                    mAppsAnimation=null;
                    mMediaplayer_imageview.setImageResource(R.drawable.icon_media);
                }
            }
        });
        mMediaplayer_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,AllAppList.class);
                startActivity(intent);
            }
        });









//		imageview.setOnKeyListener(new View.OnKeyListener() {
//			@Override
//			public boolean onKey(View view, int i, KeyEvent keyEvent) {
//				int keycode = keyEvent.getKeyCode();
//				if ((keycode == KeyEvent.KEYCODE_ENTER || keycode == KeyEvent.KEYCODE_DPAD_CENTER) && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
//					Intent intent = new Intent(MainActivity.this,AllAppList.class);
//					startActivity(intent);
//					return true;
//				}
//				return false;
//			}
//		});


        mApp_cfc = findViewById(R.id.app_cfc);
        ((ImageView)mApp_cfc.findViewById(R.id.image)).setImageResource(R.drawable.cfc);
        ((TextView)(mApp_cfc.findViewById(R.id.title))).setText(R.string.cfc_title);
        mApp_cfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                try {
                    ComponentName componentName = new ComponentName("com.cfc.nddw", "com.cfc.nddw.MainActivity2");
                    intent.setComponent(componentName);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }
        });
    }


	private  void tryRecycleAnimationDrawable(AnimationDrawable animationDrawable) {
		if (animationDrawable != null) {
			animationDrawable.stop();
			for (int i = 0; i < animationDrawable.getNumberOfFrames(); i++) {
				Drawable frame = animationDrawable.getFrame(i);
				if (frame instanceof BitmapDrawable) {
					((BitmapDrawable) frame).getBitmap().recycle();
					Log.e("wwww","recycle  ");

				}
				frame.setCallback(null);
			}
			animationDrawable.setCallback(null);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
