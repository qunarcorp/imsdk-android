package com.qunar.im.thirdpush.client.thirdpush;

import android.content.Context;
import android.os.Build;

import com.orhanobut.logger.Logger;
import com.qunar.im.utils.HttpUtil;
import com.qunar.im.base.util.PhoneInfoUtils;
import com.qunar.im.thirdpush.Constants;
import com.qunar.im.thirdpush.QTPushConfiguration;
import com.qunar.im.thirdpush.core.QMessageProvider;
import com.qunar.im.thirdpush.core.QPushManager;
import com.qunar.im.utils.HttpUtil;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.List;


public class ThirdPushManager implements QPushManager {
    public static final String NAME = Constants.NAME_THIRD;
    public static QMessageProvider sQMessageProvider;
    private String appId;
    private String appKey;

    public ThirdPushManager(String appId, String appKey) {
        this.appId = appId;
        this.appKey = appKey;
    }

    @Override
    public void registerPush(Context context) {
        MiPushClient.registerPush(context.getApplicationContext(), appId, appKey);
        Logger.i("注册Third推送 registerPush appId : " + appId + "  appKey : " + appKey + "  regid : " + MiPushClient.getRegId(context));
    }

    @Override
    public void unRegisterPush(Context context) {
        Logger.i("注销Third推送 unRegisterPush  appId : " + appId + "  appKey : " + appKey + "  regid : " + MiPushClient.getRegId(context));
        unsetAlias(context, null);
        MiPushClient.unregisterPush(context.getApplicationContext());
        HttpUtil.unregistPushinfo(PhoneInfoUtils.getUniqueID(), QTPushConfiguration.getPlatName(), true);
    }

    @Override
    public void setAlias(Context context, String alias) {
        Logger.i("注册Third推送 setAlias  " + "regid : " + MiPushClient.getRegId(context));
//        if (!MiPushClient.getAllAlias(context).contains(alias)) {
            MiPushClient.setAlias(context,alias, null);
//        }
        //注册到服务器
        HttpUtil.registPush(alias, QTPushConfiguration.getPlatName() + "-" + Build.BRAND);
    }

    @Override
    public void unsetAlias(Context context, String alias) {
        List<String> allAlias = MiPushClient.getAllAlias(context);
        for (int i = 0; i < allAlias.size(); i++) {
            MiPushClient.unsetAlias(context, allAlias.get(i), null);
        }
    }

    @Override
    public void setTags(Context context, String... tags) {
        for (String tag : tags){
            MiPushClient.subscribe(context, tag, null);
        }

    }

    @Override
    public void unsetTags(Context context, String... tags) {
        for (String tag : tags) {
            MiPushClient.unsubscribe(context, tag, null);
        }
    }

    @Override
    public void clearNotification(Context context) {
        MiPushClient.clearNotification(context);
    }


    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void setMessageProvider(QMessageProvider provider) {
        sQMessageProvider = provider;
    }

    @Override
    public void disable(Context context) {
        unRegisterPush(context);
    }
}
