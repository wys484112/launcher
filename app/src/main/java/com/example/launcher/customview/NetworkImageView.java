package com.example.launcher.customview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.launcher.R;
import com.example.launcher.util.NetworkUtils;

/**
 * TODO: document your custom view class.
 */
public class NetworkImageView extends ImageView {
    private static final String TAG = "NetworkImageView";
    private static final Boolean DBG=false;

    private String mExampleString; // TODO: use a default from R.string...
    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;
    private Context context;

    public NetworkImageView(Context context) {
        super(context);
        this.context = context;

        init(null, 0);
    }

    public NetworkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        init(attrs, 0);
    }

    public NetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;

        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.NetworkImageView, defStyle, 0);

        mExampleString = a.getString(
                R.styleable.NetworkImageView_exampleString);
        mExampleColor = a.getColor(
                R.styleable.NetworkImageView_exampleColor,
                mExampleColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mExampleDimension = a.getDimension(
                R.styleable.NetworkImageView_exampleDimension,
                mExampleDimension);

        if (a.hasValue(R.styleable.NetworkImageView_exampleDrawable)) {
            mExampleDrawable = a.getDrawable(
                    R.styleable.NetworkImageView_exampleDrawable);
            mExampleDrawable.setCallback(this);
        }

        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
        if(DBG)
            Log.e(TAG,"init");
        context.getApplicationContext().registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        context.getApplicationContext().registerReceiver(wifiChange,
                new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));

    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(mExampleDimension);
        mTextPaint.setColor(mExampleColor);
        if(mExampleString!=null){
            mTextWidth = mTextPaint.measureText(mExampleString);
        }else{
            mTextWidth=0;
        }

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(DBG)
        Log.e(TAG,"onDraw");
        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        // Draw the text.
        if(mExampleString!=null){
            canvas.drawText(mExampleString,
                    paddingLeft + (contentWidth - mTextWidth) / 2,
                    paddingTop + (contentHeight + mTextHeight) / 2,
                    mTextPaint);
        }


        // Draw the example drawable on top of the text.
        if (mExampleDrawable != null) {
            mExampleDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            mExampleDrawable.draw(canvas);
        }
    }

    /**
     * Gets the example string attribute value.
     *
     * @return The example string attribute value.
     */
    public String getExampleString() {
        return mExampleString;
    }

    /**
     * Sets the view's example string attribute value. In the example view, this string
     * is the text to draw.
     *
     * @param exampleString The example string attribute value to use.
     */
    public void setExampleString(String exampleString) {
        mExampleString = exampleString;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     */
    public int getExampleColor() {
        return mExampleColor;
    }

    /**
     * Sets the view's example color attribute value. In the example view, this color
     * is the font color.
     *
     * @param exampleColor The example color attribute value to use.
     */
    public void setExampleColor(int exampleColor) {
        mExampleColor = exampleColor;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.
     */
    public float getExampleDimension() {
        return mExampleDimension;
    }

    /**
     * Sets the view's example dimension attribute value. In the example view, this dimension
     * is the font size.
     *
     * @param exampleDimension The example dimension attribute value to use.
     */
    public void setExampleDimension(float exampleDimension) {
        mExampleDimension = exampleDimension;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     */
    public Drawable getExampleDrawable() {
        return mExampleDrawable;
    }

    /**
     * Sets the view's example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     *
     * @param exampleDrawable The example drawable attribute value to use.
     */
    public void setExampleDrawable(Drawable exampleDrawable) {
        if(DBG)
            Log.e(TAG,"setExampleDrawable "+exampleDrawable.toString());
        mExampleDrawable = exampleDrawable;
        invalidate();
    }








    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if(DBG)
                Log.e(TAG,"mConnReceiver");
            boolean noConnectivity = intent.getBooleanExtra(
                    ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            String reason = intent
                    .getStringExtra(ConnectivityManager.EXTRA_REASON);
            boolean isFailover = intent.getBooleanExtra(
                    ConnectivityManager.EXTRA_IS_FAILOVER, false);

            NetworkInfo currentNetworkInfo = (NetworkInfo) intent
                    .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            NetworkInfo otherNetworkInfo = (NetworkInfo) intent
                    .getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

            if (currentNetworkInfo.isConnected()
                    && currentNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                Log.e(TAG,"TYPE_WIFI");

                if (DBG)
                    Toast.makeText(context, "Connected", Toast.LENGTH_LONG)
                            .show();

                setExampleDrawable(context.getResources()
                        .getDrawable(R.drawable.ic_wifi_4));

            } else if (currentNetworkInfo.isConnected()
                    && currentNetworkInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
                Log.e(TAG,"TYPE_ETHERNET");

                setExampleDrawable(context.getResources()
                        .getDrawable(R.drawable.ic_status_ethernet));

            } else if (!currentNetworkInfo.isConnected()) {
                Log.e(TAG,"networkstate_off");

                setExampleDrawable(context.getResources()
                        .getDrawable(R.drawable.networkstate_off));
            }

            if (DBG)
                Toast.makeText(
                        context,
                        "currentNetworkInfo.getType()=>>"
                                + currentNetworkInfo.getType()
                                + currentNetworkInfo.getTypeName(),
                        Toast.LENGTH_LONG).show();
        }
    };

    private BroadcastReceiver wifiChange = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(DBG)
                Log.e(TAG,"wifiChange");
            if(NetworkUtils.getConnectedType(context)!=ConnectivityManager.TYPE_WIFI){
                Log.e(TAG,"isWifiConnected");
                return;
            }
            WifiManager wifiManager = (WifiManager) context
                    .getSystemService(context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo.getBSSID() != null) {

                // wifi信号强度
                int signalLevel = WifiManager.calculateSignalLevel(
                        wifiInfo.getRssi(), 4);
                Log.e(TAG,"wifiInfo.getBSSID() != null signalLevel=="+signalLevel);

                if (signalLevel == 0) {
                    setExampleDrawable(context.getResources()
                            .getDrawable(R.drawable.ic_wifi_1));

                } else if (signalLevel == 1) {
                    setExampleDrawable(context.getResources()
                            .getDrawable(R.drawable.ic_wifi_2));

                } else if (signalLevel == 2) {
                    setExampleDrawable(context.getResources()
                            .getDrawable(R.drawable.ic_wifi_3));

                } else if (signalLevel == 3) {
                    setExampleDrawable(context.getResources()
                            .getDrawable(R.drawable.ic_wifi_4));
                }
                if (DBG)
                    Toast.makeText(context, "wifi level" + signalLevel,
                            Toast.LENGTH_SHORT).show();
            }
        }
    };
}
