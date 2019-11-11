package com.qunar.im.thirdpush;

import android.content.Context;
import android.content.Intent;

import com.orhanobut.logger.Logger;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.thirdpush.core.QPushMessage;
import com.qunar.im.thirdpush.core.QPushMessageReceiver;

/**
 * 需要定义一个receiver 或 Service 来接收透传和通知栏点击的信息，建议使用Service，更加简单
 * Created by Wiki on 2017/6/1.
 */

public class PushMessageReceiver extends QPushMessageReceiver {

    private static final String TAG = "MyPushMessageReceiver";

    @Override
    public void onReceivePassThroughMessage(Context context, QPushMessage message) {
        Logger.i(TAG + "收到透传消息 -> "+message.getContent());
        Intent intent = new Intent();
        intent.setAction("com.qunar.ops.push.MSG_ARRIVED");
        intent.putExtra("message",message.getContent());
        intent.setPackage(CommonConfig.globalContext.getPackageName());
        CommonConfig.globalContext.sendBroadcast(intent);
    }

    @Override
    public void onNotificationMessageClicked(Context context, QPushMessage message) {
        Logger.i(TAG + "通知栏消息点击 -> "+message.getContent());
    }

    @Override
    public void onNotificationMessageArrived(Context context, QPushMessage message) {
        Logger.i(TAG + "通知栏消息到达 -> "+message.getContent());
//        Intent intent = new Intent();
//        intent.setAction("com.qunar.ops.push.MSG_ARRIVED");
//        intent.putExtra("message",message.getContent());
//        intent.setPackage(CommonConfig.globalContext.getPackageName());
//        CommonConfig.globalContext.sendBroadcast(intent);
    }
}
