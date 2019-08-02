package com.qunar.im.ui.view.baseView;

import android.content.Context;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qunar.im.base.module.IMMessage;

/**
 * Created by zhaokai on 15-11-19.
 */
public interface IMessageItem {
    IMMessage getMessage();

    int getPosition();

    Context getContext();

    Handler getHandler();

    ProgressBar getProgressBar();

    ImageView getErrImageView();

    TextView getStatusView();
}
