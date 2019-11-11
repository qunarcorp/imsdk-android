package com.qunar.im.thirdpush.client.google;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.orhanobut.logger.Logger;
import com.qunar.im.thirdpush.Constants;
import com.qunar.im.thirdpush.QTPushConfiguration;
import com.qunar.im.thirdpush.core.QMessageProvider;
import com.qunar.im.thirdpush.core.QPushManager;
import com.qunar.im.utils.HttpUtil;

/**
 * FCM
 * Created by Lex lex on 2019-10-15.
 */
public class FCMPushManager implements QPushManager {

    public static final String TAG = "FCMPushManager";
    public static final String NAME = Constants.NAME_FCM;

    @Override
    public void registerPush(Context context) {
        Logger.i(TAG + "  firebase 注册push fcm ");
        try {
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if(!task.isSuccessful()) {
                        Logger.i("获取firebase service token 失败 ");
                    } else {
                        String token = task.getResult().getToken();
                        Logger.i("获取firebase service token : " + token);
                        HttpUtil.registPush(token, QTPushConfiguration.getPlatName());
                    }
                }
            });
        } catch (Exception e) {
            Logger.i("firebase service getInstanceId failed e ： " + e);
        }
    }

    @Override
    public void unRegisterPush(Context context) {

    }

    @Override
    public void setAlias(Context context, String alias) {

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
