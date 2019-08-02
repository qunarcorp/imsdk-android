package com.qunar.im.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

/**
 * Created by hubin on 2018/1/9.
 */

public class MyWebView extends WebView {


    public MyWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public MyWebView(Context context) {
        super(context);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }



}