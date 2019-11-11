package com.qunar.im.thirdpush.core;

import android.content.Context;

/**
 * Created by Wiki on 2017/6/1.
 */

public interface QMessageProvider {
    /**
     * 透传
     */
    public void onReceivePassThroughMessage(Context context, QPushMessage message);

    /**
     * 通知栏消息点击
     */
    public void onNotificationMessageClicked(Context context, QPushMessage message);

    /**
     * 通知栏消息到达
     */
    public void onNotificationMessageArrived(Context context, QPushMessage message);

//    public void onError(Context context, QPushMessage message);
}
