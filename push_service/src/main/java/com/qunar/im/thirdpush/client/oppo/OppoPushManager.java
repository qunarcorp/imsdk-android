package com.qunar.im.thirdpush.client.oppo;

import android.content.Context;
import android.text.TextUtils;

import com.coloros.mcssdk.PushManager;
import com.coloros.mcssdk.callback.PushAdapter;
import com.coloros.mcssdk.mode.SubscribeResult;
import com.orhanobut.logger.Logger;
import com.qunar.im.utils.HttpUtil;
import com.qunar.im.base.util.PhoneInfoUtils;
import com.qunar.im.thirdpush.Constants;
import com.qunar.im.thirdpush.QTPushConfiguration;
import com.qunar.im.thirdpush.core.QMessageProvider;
import com.qunar.im.thirdpush.core.QPushManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lex lex on 2018/8/22.
 */
public class OppoPushManager implements QPushManager {

    public static final String TAG = "OppoPushManager";
    public static final String NAME = Constants.NAME_OPPO;
    public static QMessageProvider sQMessageProvider;
    private String appId;
    private String appKey;
    private String appSecret;

    public OppoPushManager(String appId, String appKey, String appSecret) {
        this.appId = appId;
        this.appKey = appKey;
        this.appSecret = appSecret;
    }

    private int times = 0;

    @Override
    public void registerPush(final Context context) {
        try {

            PushManager.getInstance().register(context, appKey, appSecret, new PushAdapter() {
                @Override
                public void onRegister(int i, String s) {
                    Logger.i("注册OPPO push结果 onRegister code=%d result=%s", i, s);
                    if(i == 0) {
//                        QPushClient.setAlias(context, PhoneInfoUtils.getUniqueID());
                        HttpUtil.registPush(s, QTPushConfiguration.getPlatName());
                    } else {
                        if(times < 3) {
                            times++;
                            PushManager.getInstance().getRegister();
                            Logger.i("注册OPPO push失败重试 times=%d", times);
                        }
                    }
                }

                @Override
                public void onUnRegister(int i) {
                    Logger.i("注销OPPO push结果 onRegister code=%d ", i);
                }

                @Override
                public void onSetAliases(int i, List<SubscribeResult> list) {
                    if(list != null && list.size() > 0) {
                        Logger.i("OPPO 设置alias结果 onSetAliases code=%d result=%s", i, list.get(0).toString());
                        if(i == 0) {
                            HttpUtil.registPush(list.get(0).getSubscribeId(), QTPushConfiguration.getPlatName());
                        }
                    }else {
                        Logger.i("OPPO 设置alias结果 失败 code=%d result=%s", i, list);
                    }
                }
            });
            Logger.i("注册OPPO push registerPush" );
        } catch (Exception e) {
            Logger.i("注册OPPO push registerPush 异常 e=%s", e.getMessage());
        }
    }

    @Override
    public void unRegisterPush(Context context) {
        if(!TextUtils.isEmpty(PushManager.getInstance().getRegisterID())) {
            PushManager.getInstance().unRegister();
        }
        HttpUtil.unregistPushinfo(PhoneInfoUtils.getUniqueID(), QTPushConfiguration.getPlatName(), true);
        Logger.i("注销OPPO推送 unRegisterPush  registerid=%s", PushManager.getInstance().getRegisterID());
    }

    @Override
    public void setAlias(Context context, String alias) {
        Logger.i("注册OPPO push setAlias alias=%s", alias);
        List<String> list = new ArrayList<String>();
        list.add(alias);
        PushManager.getInstance().setAliases(list);
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
        if(!TextUtils.isEmpty(PushManager.getInstance().getRegisterID())) {
            PushManager.getInstance().clearNotificationType();
        }
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
