package com.qunar.im.ui.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.qunar.im.base.util.LogUtil;

import java.util.ArrayList;

/**
 * Created by zhaokai on 15-8-28.
 */
public class ShakeEventHandler {

    public static final String TAG = "ShakeEventHandler";
    private final int SHAKE_THRESHOLD = 24;
//    /** 决定是否是摇晃的最小值. */
//    /**
//     * 一次摇晃需要的最少的方向改变次数
//     */
    private static final int MIN_DIRECTION_CHANGE = 3;
//    /** 动作之间的最长暂定时间 */
    private static final int MAX_PAUSE_BETHWEEN_DIRECTION_CHANGE = 330;

    long lastUpdateTime = 0;
    ArrayList<OnShakeListener> listeners;
    //超时时间,如果超过2s则清空次数
    private int changeTime = 0;
    private SensorManager mSensorManager;
    private Sensor sensor;

    private float lastX, lastY, lastZ;
    private SensorEventListener sensorEventListener;

    public ShakeEventHandler(Context context) {
        listeners = new ArrayList<>();
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorEventListener = new SensorEventListener() {
            @Override
            public synchronized void onSensorChanged(SensorEvent event) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastUpdateTime > MAX_PAUSE_BETHWEEN_DIRECTION_CHANGE) {
                    changeTime = 0;
                }
                float getX = event.values[0];
                float getY = event.values[1];
                float getZ = event.values[2];
                if (Math.round(getX) != 0 && Math.round(getY) != 0 && Math.round(getZ) != 0) {
                    lastUpdateTime = currentTime;
                    float differX = getX - lastX;
                    float differY = getY - lastY;
                    float differZ = getZ - lastZ;
                    double differ = Math.sqrt(differX * differX + differY * differY + differZ * differZ);
                    if (differ > SHAKE_THRESHOLD) {
                        LogUtil.d(TAG, "the last acceleration is\t" + "[x:" + lastX + "\ty:" + lastY + "\tz:" + lastZ + "]");
                        LogUtil.d(TAG, "the acceleration is\t" + "[x:" + getX + "\ty:" + getY + "\tz:" + getZ + "]");
                        LogUtil.d(TAG, "the differ is\t" + differ);
                        lastX = getX;
                        lastY = getY;
                        lastZ = getZ;
                        if (++changeTime > MIN_DIRECTION_CHANGE) {
                            changeTime = 0;
                            notifyDataChanged();
                        }
                    }
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }

        };
    }

    public void start() {
        if (mSensorManager == null || sensor == null || !mSensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL)) {
            throw new UnsupportedOperationException("设备不支持该操作");
        }
    }

    public void stop() {
        if (listeners != null) {
            listeners.clear();
        }
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(sensorEventListener);
        }
    }

    public void registerOnShakeListener(OnShakeListener listener) {
        if (listeners != null) {
            listeners.add(listener);
        }
    }

    private void notifyDataChanged() {
        if (listeners != null) {
            for (OnShakeListener listener : listeners) {
                listener.onShake();
            }
        }
    }


    public interface OnShakeListener {
        /**
         * 当手机摇晃的时候调用
         */
        void onShake();
    }
}
