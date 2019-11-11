package com.qunar.im.thirdpush.client.vivo;

import android.app.NotificationManager;
import android.content.Context;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.util.PhoneInfoUtils;
import com.qunar.im.thirdpush.Constants;
import com.qunar.im.thirdpush.QTPushConfiguration;
import com.qunar.im.thirdpush.core.QMessageProvider;
import com.qunar.im.thirdpush.core.QPushManager;
import com.qunar.im.utils.HttpUtil;
import com.vivo.push.IPushActionListener;
import com.vivo.push.PushClient;

/**
 * Created by Lex lex on 2018/8/22.
 */
public class VivoPushManager implements QPushManager {

    public static final String TAG = "VivoPushManager";
    public static final String NAME = Constants.NAME_VIVO;
    public static QMessageProvider sQMessageProvider;
    private String appId;
    private String appKey;
    private String appSecret;

    public VivoPushManager(String appId, String appKey, String appSecret) {
        this.appId = appId;
        this.appKey = appKey;
        this.appSecret = appSecret;
    }

    private int times = 0;

    @Override
    public void registerPush(final Context context) {
        try {
            PushClient.getInstance(context).initialize();
            PushClient.getInstance(context).turnOnPush(new IPushActionListener() {
                @Override
                public void onStateChanged(int state) {
                    if (state != 0) {
                        Logger.i("vivo打开push异常[" + state + "]");
                    } else {
                        Logger.i("vivo打开push成功");
                    }
                }
            });
            if(!TextUtils.isEmpty(PushClient.getInstance(context).getRegId())) {
                HttpUtil.registPush(PushClient.getInstance(context).getRegId(), QTPushConfiguration.getPlatName());
                Logger.i("注册vivo push registerPush" );
            }
        } catch (Exception e) {
            Logger.i("注册vivo push registerPush 异常 e=%s", e.getMessage());
        }
    }

    @Override
    public void unRegisterPush(final Context context) {
        PushClient.getInstance(context).turnOffPush(new IPushActionListener() {
            @Override
            public void onStateChanged(int state) {
                if (state != 0) {
                    Logger.i("vivo关闭push异常[" + state + "]");
                } else {
                    Logger.i("vivo关闭push成功");
                }
            }
        });
        HttpUtil.unregistPushinfo(PhoneInfoUtils.getUniqueID(), QTPushConfiguration.getPlatName(), true);
        Logger.i("注销vivo推送 unRegisterPush  registerid=%s", PushClient.getInstance(context).getRegId());
    }

    @Override
    public void setAlias(Context context, String alias) {
        Logger.i("注册vivo push setAlias alias=%s", alias);
    }

    @Override
    public void unsetAlias(Context context, String alias) {

    }

    @Override
    public void setTags(Context context, String... tags) {

    }

    @Override
    public void unsetTags(Context context, String... tags) {

    }

    @Override
    public void clearNotification(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getApplicationContext().
                        getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void setMessageProvider(QMessageProvider provider) {
    }

    @Override
    public void disable(Context context) {

    }

}
