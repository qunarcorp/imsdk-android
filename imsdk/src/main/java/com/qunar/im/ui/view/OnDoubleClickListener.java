package com.qunar.im.ui.view;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by hubin on 2017/9/26.
 */

public class OnDoubleClickListener implements View.OnTouchListener {
    private final String TAG = this.getClass().getSimpleName();
    private int count = 0;
    private long firClick = 0;
    private long secClick = 0;
    /**
     * 两次点击时间间隔，单位毫秒
     */
    private final int interval = 1500;
    private DoubleClickCallback mCallback;

    public interface DoubleClickCallback {
        void onDoubleClick();
        void onSingleClick();
    }

    public OnDoubleClickListener(DoubleClickCallback callback) {
        super();
        this.mCallback = callback;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            count++;
            if (1 == count) {
                firClick = System.currentTimeMillis();
            } else if (2 == count) {
                secClick = System.currentTimeMillis();
                if (secClick - firClick < interval) {
                    if (mCallback != null) {
                        mCallback.onDoubleClick();
                    } else {
//                        Log.e(TAG, "请在构造方法中传入一个双击回调");
                    }
                    count = 0;
                    firClick = 0;
                    return true;
                } else {
                    firClick = secClick;
                    count = 1;
                }
                secClick = 0;

            }
        }
        return false;
    }

}