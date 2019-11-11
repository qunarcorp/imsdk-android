package com.qunar.im.thirdpush.client.mipush;

import android.content.Context;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.util.PhoneInfoUtils;
import com.qunar.im.thirdpush.core.QPushClient;
import com.qunar.im.thirdpush.core.QPushMessage;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import java.util.List;

public class MiPushMessageReceiver extends PushMessageReceiver {
    private static final String TAG = "MiPushMessageReceiver";
    private String mRegId;
    private String mAlias;
    private String mTopic;
    private String mStartTime;
    private String mEndTime;

    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        Logger.i(TAG + "onReceivePassThroughMessage  " + message.toString());

        if (MiPushManager.sQMessageProvider == null) {
            return;
        }
        String content = message.getContent();
        QPushMessage qPushMessage = new QPushMessage();
        qPushMessage.setPlatform(MiPushManager.NAME);
        qPushMessage.setContent(content);
        qPushMessage.setTitle(message.getTitle());
        qPushMessage.setDescription(message.getDescription());
        qPushMessage.setAlias(message.getAlias());
        qPushMessage.setPassThrough(message.getPassThrough());
        MiPushManager.sQMessageProvider.onReceivePassThroughMessage(context, qPushMessage);
    }

    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        Logger.i(TAG + "onNotificationMessageClicked");
        if (MiPushManager.sQMessageProvider == null) {
            return;
        }
        String content = message.getContent();
        QPushMessage qPushMessage = new QPushMessage();
        qPushMessage.setPlatform(MiPushManager.NAME);
        qPushMessage.setContent(content);
        qPushMessage.setTitle(message.getTitle());
        qPushMessage.setDescription(message.getDescription());
        qPushMessage.setAlias(message.getAlias());
        qPushMessage.setPassThrough(message.getPassThrough());
        MiPushManager.sQMessageProvider.onNotificationMessageClicked(context, qPushMessage);
    }


    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        Logger.i(TAG + "onNotificationMessageArrived  " + message.toString());
        if (MiPushManager.sQMessageProvider == null) {
            return;
        }
        String content = message.getContent();
        QPushMessage qPushMessage = new QPushMessage();
        qPushMessage.setPlatform(MiPushManager.NAME);
        qPushMessage.setContent(content);
        qPushMessage.setTitle(message.getTitle());
        qPushMessage.setDescription(message.getDescription());
        qPushMessage.setAlias(message.getAlias());
        qPushMessage.setPassThrough(message.getPassThrough());
        MiPushManager.sQMessageProvider.onNotificationMessageArrived(context, qPushMessage);

    }






    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
            Logger.i(TAG + "onCommandResult => " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
                QPushClient.setAlias(context, PhoneInfoUtils.getUniqueID());
            }
        } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mStartTime = cmdArg1;
                mEndTime = cmdArg2;
            }
        }
    }

    @Override
    public void onReceiveMessage(Context context, MiPushMessage miPushMessage) {
        Logger.i(TAG + "onReceiveMessage => " + miPushMessage.toString());
        super.onReceiveMessage(context, miPushMessage);
    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage miPushCommandMessage) {
        super.onReceiveRegisterResult(context, miPushCommandMessage);
        Logger.i(TAG + "onReceiveRegisterResult => " + miPushCommandMessage.toString());
    }
}