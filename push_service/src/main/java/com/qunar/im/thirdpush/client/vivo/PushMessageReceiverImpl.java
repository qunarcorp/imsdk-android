package com.qunar.im.thirdpush.client.vivo;

import android.content.Context;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.qunar.im.thirdpush.QTPushConfiguration;
import com.qunar.im.utils.HttpUtil;
import com.vivo.push.model.UPSNotificationMessage;
import com.vivo.push.sdk.OpenClientPushMessageReceiver;

public class PushMessageReceiverImpl extends OpenClientPushMessageReceiver {

    public static final String TAG = "VivoPushManager";

    @Override
    public void onNotificationMessageClicked(Context context, UPSNotificationMessage msg) {
        String customContentString = msg.getSkipContent();
        String notifyString = "vivo 通知点击 msgId " + msg.getMsgId() + " ;customContent=" + customContentString;
        Log.d(TAG, notifyString);
    }

    @Override
    public void onReceiveRegId(Context context, String regId) {
        String responseString = "vivo onReceiveRegId regId = " + regId;
        Log.d(TAG, responseString);
        HttpUtil.registPush(regId, QTPushConfiguration.getPlatName());
        Logger.i("注册vivo push firsttime onReceiveRegId" );
    }
}
