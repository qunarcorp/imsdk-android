package com.qunar.im.thirdpush.client.meizu;

import android.content.Context;

import com.meizu.cloud.pushsdk.MzPushMessageReceiver;
import com.meizu.cloud.pushsdk.platform.message.PushSwitchStatus;
import com.meizu.cloud.pushsdk.platform.message.RegisterStatus;
import com.meizu.cloud.pushsdk.platform.message.SubAliasStatus;
import com.meizu.cloud.pushsdk.platform.message.SubTagsStatus;
import com.meizu.cloud.pushsdk.platform.message.UnRegisterStatus;
import com.orhanobut.logger.Logger;
import com.qunar.im.utils.HttpUtil;
import com.qunar.im.thirdpush.QTPushConfiguration;

/**
 * Created by Lex lex on 2018/8/22.
 */
public class MzPushReceiver extends MzPushMessageReceiver {
    @Override
    public void onRegister(Context context, String s) {
    }

    @Override
    public void onUnRegister(Context context, boolean b) {

    }

    @Override
    public void onPushStatus(Context context, PushSwitchStatus pushSwitchStatus) {

    }

    @Override
    public void onRegisterStatus(Context context, RegisterStatus registerStatus) {
        if(registerStatus != null) {
            Logger.i("注册meizu push onRegisterStatus registerStatus=%s", registerStatus.toString());
//            QPushClient.setAlias(context, PhoneInfoUtils.getUniqueID());
            HttpUtil.registPush(registerStatus.getPushId(), QTPushConfiguration.getPlatName());
        }else {
            Logger.i("注册meizu push onRegisterStatus 失败");
        }
    }

    @Override
    public void onUnRegisterStatus(Context context, UnRegisterStatus unRegisterStatus) {

    }

    @Override
    public void onSubTagsStatus(Context context, SubTagsStatus subTagsStatus) {

    }

    @Override
    public void onSubAliasStatus(Context context, SubAliasStatus subAliasStatus) {
        if(subAliasStatus != null) {
            Logger.i("注册meizu push onSubAliasStatus subAliasStatus=%s", subAliasStatus.toString());
            HttpUtil.registPush(subAliasStatus.getAlias(), QTPushConfiguration.getPlatName());
        } else {
            Logger.i("注册meizu push onSubAliasStatus 失败");
        }
    }
}
