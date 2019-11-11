package com.qunar.im.thirdpush.core;

import android.app.IntentService;
import android.content.Intent;


/**
 * 透传服务类
 */
public abstract class QPushIntentService extends IntentService {

    public static final String TAG = "QPushIntentService";

    public QPushIntentService() {
        super("QPushIntentService");
    }

    @Override
    public final void onHandleIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            try {
                QPushMessage message = (QPushMessage) intent.getSerializableExtra(QPushMessageReceiver.MESSAGE);
                if (QPushMessageReceiver.RECEIVE_THROUGH_MESSAGE.equals(action)) {
                    onReceivePassThroughMessage(message);
                } else if (QPushMessageReceiver.NOTIFICATION_ARRIVED.equals(action)) {
                    onNotificationMessageArrived(message);
                } else if (QPushMessageReceiver.NOTIFICATION_CLICKED.equals(action)) {
                    onNotificationMessageClicked(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 透传
     */
    public abstract void onReceivePassThroughMessage(QPushMessage message);

    /**
     * 通知栏消息点击
     */
    public abstract void onNotificationMessageClicked(QPushMessage message);

    /**
     * 通知栏消息到达,
     * flyme6基于android6.0以上不再回调，
     * MIUI基于小米推送，在APP被杀死不会回调，
     * 在个推不会回调，所以不建议使用，
     */
    @Deprecated
    public void onNotificationMessageArrived(QPushMessage message) {

    }
}