package com.example.launcher.customview;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.example.launcher.R;

/**
 * TODO: document your custom view class.
 */
public class BluetoothImageView extends ImageView {
    private static final String TAG = "BluetoothImageView";
    private static final Boolean DBG=false;

    private Drawable mExampleDrawable;

    private Context context;

    public BluetoothImageView(Context context) {
        super(context);
        this.context = context;

        init(null, 0);
    }

    public BluetoothImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        init(attrs, 0);
    }

    public BluetoothImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;

        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.BluetoothImageView, defStyle, 0);
        if (a.hasValue(R.styleable.BluetoothImageView_bluetoothDrawable)) {
            mExampleDrawable = a.getDrawable(
                    R.styleable.BluetoothImageView_bluetoothDrawable);
            mExampleDrawable.setCallback(this);
        }

        a.recycle();


        if(DBG)
            Log.e(TAG,"init");
    }


    private void registerReceiver() {

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.bluetooth.input.profile.action.CONNECTION_STATE_CHANGED");               //监听hid是否连接


        getContext().registerReceiver(mIntentReceiver, filter, null, getHandler());
        int inputState=0;
        if(BluetoothAdapter.getDefaultAdapter()!=null){
            inputState=BluetoothAdapter.getDefaultAdapter().getProfileConnectionState(4);
        }
        if(inputState==BluetoothProfile.STATE_CONNECTED){
            setExampleDrawable(context.getResources()
                    .getDrawable(R.drawable.ic_remotecontrol));
        } else{//连接失败
            setExampleDrawable(context.getResources()
                    .getDrawable(R.drawable.ic_bluetooth));
        }
    }

    private void unregisterReceiver() {
        getContext().unregisterReceiver(mIntentReceiver);
    }
    private Boolean isPhicomPERIPHERAL(BluetoothDevice device) {
        if (device != null && device.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.PERIPHERAL) {
            if (device.getName() != null && !device.getName().isEmpty()) {
                if (device.getName().equals("斐讯遥控器")) {
                    return true;
                }
            }
        }
        return false;
    }
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case "android.bluetooth.input.profile.action.CONNECTION_STATE_CHANGED": {
                    int state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, 0);
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    Log.i(TAG, "state=" + state + ",device=" + device);
                    if (isPhicomPERIPHERAL(device)) {
                        if (state == BluetoothProfile.STATE_CONNECTED) {//连接成功
                            setExampleDrawable(context.getResources()
                                    .getDrawable(R.drawable.ic_remotecontrol));
                        } else{//连接失败
                            setExampleDrawable(context.getResources()
                                    .getDrawable(R.drawable.ic_bluetooth));
                        }
                    }
                }
                break;
            }
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        registerReceiver();

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unregisterReceiver();
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



        // Draw the example drawable on top of the text.
        if (mExampleDrawable != null) {
            mExampleDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            mExampleDrawable.draw(canvas);
        }
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
}
