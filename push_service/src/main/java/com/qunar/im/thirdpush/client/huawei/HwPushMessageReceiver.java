package com.qunar.im.thirdpush.client.huawei;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.huawei.hms.support.api.push.PushReceiver;
import com.orhanobut.logger.Logger;
import com.qunar.im.utils.HttpUtil;
import com.qunar.im.thirdpush.QTPushConfiguration;

public class HwPushMessageReceiver extends PushReceiver {
    private static final String TAG = "HwPushMessageReceiver";

    @Override

    public void onToken(Context context, String token, Bundle extras) {
        Logger.i(TAG + "onToonTokenken => " + token + "  extras = " + extras.toString());
        HttpUtil.registPush(token, QTPushConfiguration.getPlatName());


    }

    @Override

    public boolean onPushMsg(Context context, byte[] msg, Bundle bundle) {
//        String content = message.getContent();
//        QPushMessage qPushMessage = new QPushMessage();
//        qPushMessage.setPlatform(MiPushManager.NAME);
//        qPushMessage.setContent(content);
//        qPushMessage.setTitle(message.getTitle());
//        qPushMessage.setDescription(message.getDescription());
//        qPushMessage.setAlias(message.getAlias());
//        qPushMessage.setPassThrough(message.getPassThrough());
//        MiPushManager.sQMessageProvider.onReceivePassThroughMessage(context, qPushMessage);\

        Logger.i(TAG + "onPushMsg => " + bundle.toString());
        return false;
    }

    public void onEvent(Context context, Event event, Bundle extras) {
        Logger.i(TAG + "onEvent => " + " event = " + event + "  extras" + extras.toString());
        if (Event.NOTIFICATION_OPENED.equals(event) || Event.NOTIFICATION_CLICK_BTN.equals(event)) {

            int notifyId = extras.getInt(BOUND_KEY.pushNotifyId, 0);

            Log.i(TAG, "收到通知栏消息点击事件,notifyId:" + notifyId);

            if (0 != notifyId) {
                NotificationManager manager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(notifyId);

            }

        }



        String message = extras.getString(BOUND_KEY.pushMsgKey);

        super.onEvent(context, event, extras);
    }

    @Override

    public void onPushState(Context context, boolean pushState) {
        Logger.i(TAG + "PUSH连接状态为 onPushState => " + pushState);
    }


}