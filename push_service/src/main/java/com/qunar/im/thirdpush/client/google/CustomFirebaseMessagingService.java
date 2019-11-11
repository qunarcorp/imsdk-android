package com.qunar.im.thirdpush.client.google;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.orhanobut.logger.Logger;
import com.qunar.im.base.util.JsonUtils;

/**
 * Created by Lex lex on 2019-10-11.
 */
public class CustomFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMessagingService";

    @Override
    public void onNewToken(String s) {
        Logger.i("CustomFirebaseMessagingService onNewToken : " + s);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Logger.i("CustomFirebaseMessagingService onMessageReceived : " + remoteMessage
        + "  from = " + remoteMessage.getFrom()
                + "  to = " + remoteMessage.getTo()
                + "  msgid = " + remoteMessage.getMessageId()
                + "  msgtype = " + remoteMessage.getMessageType()
                + "  CollapseKey = " + remoteMessage.getCollapseKey()
                + "  OriginalPriority = " + remoteMessage.getOriginalPriority()
                + "  SentTime = " + remoteMessage.getSentTime()
                + "  tTtl = " + remoteMessage.getTtl()
                + "  Priority = " + remoteMessage.getPriority()
                + "  Notification = " + JsonUtils.getGson().toJson(remoteMessage.getNotification())
                + "  data = " + JsonUtils.getGson().toJson(remoteMessage.getData())
        );
        super.onMessageReceived(remoteMessage);
    }
}
