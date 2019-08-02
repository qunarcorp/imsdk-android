package com.qunar.im.ui.view;

import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by huayu.chen on 2016/5/30.
 */
public class CustomAnimation extends Animation {
    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        t.getMatrix().setTranslate((float) Math.sin(interpolatedTime * 50) * 20, (float) Math.sin(interpolatedTime * 50) * 20);
        super.applyTransformation(interpolatedTime, t);
    }
}