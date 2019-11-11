package com.qunar.im.thirdpush.client.meizu;

import android.content.Context;

import com.meizu.cloud.pushsdk.PushManager;
import com.orhanobut.logger.Logger;
import com.qunar.im.utils.HttpUtil;
import com.qunar.im.base.util.PhoneInfoUtils;
import com.qunar.im.thirdpush.Constants;
import com.qunar.im.thirdpush.QTPushConfiguration;
import com.qunar.im.thirdpush.core.QMessageProvider;
import com.qunar.im.thirdpush.core.QPushManager;

/**
 * Created by Lex lex on 2018/8/22.
 */
public class MzPushManager implements QPushManager {

    public static final String TAG = "MzPushManager";
    public static final String NAME = Constants.NAME_MEIZU;
    public static QMessageProvider sQMessageProvider;
    private String appId;
    private String appKey;

    public MzPushManager(String appId, String appKey) {
        this.appId = appId;
        this.appKey = appKey;
    }

    @Override
    public void registerPush(Context context) {
        PushManager.register(context, appId, appKey);
        Logger.i("注册meizu push registerPush" );
    }

    @Override
    public void unRegisterPush(Context context) {
        Logger.i("注销meizu push registerPush" );
        PushManager.unRegister(context, appId, appKey);
        HttpUtil.unregistPushinfo(PhoneInfoUtils.getUniqueID(), QTPushConfiguration.getPlatName(), true);
    }

    @Override
    public void setAlias(Context context, String alias) {
        Logger.i("注销meizu push setAlias" + alias);
//        PushManager.subScribeAlias(context, appId, appKey, alias, alias);
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
        PushManager.clearNotification(context);
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
