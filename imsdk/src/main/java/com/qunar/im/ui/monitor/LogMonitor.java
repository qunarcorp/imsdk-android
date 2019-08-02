package com.qunar.im.ui.monitor;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import com.orhanobut.logger.Logger;

/**
 * Created by lihaibin.li on 2018/3/16.
 */

public class LogMonitor {
    private static final String TAG = LogMonitor.class.getSimpleName();
    private static LogMonitor sInstance = new LogMonitor();
    private HandlerThread mLogThread = new HandlerThread("log");
    private Handler mIoHandler;
    private static final long TIME_BLOCK = 100L;//阈值一秒

    private LogMonitor() {
        mLogThread.start();
        mIoHandler = new Handler(mLogThread.getLooper());
    }

    private static Runnable mLogRunnable = new Runnable() {
        @Override
        public void run() {
            StringBuilder sb = new StringBuilder();
            StackTraceElement[] stackTrace = Looper.getMainLooper().getThread().getStackTrace();
            for (StackTraceElement s : stackTrace) {
                sb.append(s.toString() + "\n");
            }
            Logger.d(TAG + sb.toString());
        }
    };

    public static LogMonitor getInstance() {
        return sInstance;
    }

//    public boolean isMonitor() {
//        return mIoHandler.hasCallbacks(mLogRunnable);
//    }

    public void startMonitor() {
        mIoHandler.postDelayed(mLogRunnable, TIME_BLOCK);
    }

    public void removeMonitor() {
        mIoHandler.removeCallbacks(mLogRunnable);
    }
}
