package com.qunar.im.utils;


import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.orhanobut.logger.Logger;

/**
 * 应用前后台状态监听帮助类，仅在Application中使用
 * Created by dway on 2018/1/30.
 */
public class AppFrontBackHelper implements Application.ActivityLifecycleCallbacks{
    private static final String TAG = "AppFrontBackHelper";

    private OnAppStatusListener mOnAppStatusListener;

    public static final long CHECK_DELAY = 500;
    private boolean foreground = false, paused = true;
    private Runnable check;
    private Handler handler = new Handler(Looper.getMainLooper());

    public AppFrontBackHelper() {

    }

    /**
     * 注册状态监听，仅在Application中使用
     * @param application
     * @param listener
     */
    public void register(Application application, OnAppStatusListener listener){
        mOnAppStatusListener = listener;
        application.registerActivityLifecycleCallbacks(this);
    }

    public void unRegister(Application application){
        application.unregisterActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
//        Logger.d (TAG + "  onActivityCreated activity = " + activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
//        Logger.d (TAG + "  onActivityStarted activity = " + activity);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        paused = false;
        boolean wasBackground = !foreground;
        foreground = true;
        if (check != null)
            handler.removeCallbacks(check);
        if (wasBackground){
            if(mOnAppStatusListener != null)
                mOnAppStatusListener.onFront();
            Logger.d (TAG + "  应用切到前台 activity = " + activity);
        } else {
            Logger.d (TAG + "  still foreground activity = " + activity);
        }
    }

    @Override
    public void onActivityPaused(final Activity activity) {
        paused = true;
        if (check != null)
            handler.removeCallbacks(check);
        handler.postDelayed(check = new Runnable(){
            @Override
            public void run() {
                if (foreground && paused) {
                    foreground = false;
                    if(mOnAppStatusListener != null)
                        mOnAppStatusListener.onBack();
//                    Logger.d (TAG + "  应用切到后台 = " + activity);
                } else {
//                    Logger.d (TAG + "  still foreground = " + activity);
                }
            }
        }, CHECK_DELAY);
    }

    @Override
    public void onActivityStopped(Activity activity) {
//        Logger.d (TAG + "  onActivityStopped activity = " + activity);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
//        Logger.d (TAG + "  onActivityDestroyed activity = " + activity);
    }

    public interface OnAppStatusListener{
        void onFront();
        void onBack();
    }
}
