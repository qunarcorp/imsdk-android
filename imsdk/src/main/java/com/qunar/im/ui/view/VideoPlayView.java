package com.qunar.im.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.qunar.im.ui.util.easyVideo.JzvdStd;


public class VideoPlayView extends JzvdStd {


    public VideoPlayView(Context context) {
        super(context);
    }

    public VideoPlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setScreenNormal() {
        super.setScreenNormal();
        backButton.setVisibility(View.VISIBLE);
    }


}
