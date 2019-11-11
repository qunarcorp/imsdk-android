package com.qunar.im.thirdpush.core;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.qunar.im.thirdpush.Constants;
import com.qunar.im.thirdpush.Rom;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class QPushClient {


    private static Map<String, QPushManager> sPushManagerMap = new HashMap<>();
    private static String sUsePushName;
    private static Selector sSelector;
    private static String sReceiverPermission = null;// 避免被其它APP接收
    private static Class<? extends QPushIntentService> sMixPushIntentServiceClass;


    private QPushClient() {

    }

    public static void setPushIntentService(Class<? extends QPushIntentService> mixPushIntentServiceClass) {
        QPushClient.sMixPushIntentServiceClass = mixPushIntentServiceClass;
    }

    public static void setSelector(Selector selector) {
        sSelector = selector;
        sUsePushName = sSelector.select(sPushManagerMap, Build.BRAND);

        sUsePushName = addLocale(sUsePushName);
    }

    public static String getUsePushName() {
        return sUsePushName;
    }

    public static void addPushManager(QPushManager pushAdapter) {
        sPushManagerMap.put(pushAdapter.getName(), pushAdapter);
        pushAdapter.setMessageProvider(mQMessageProvider);
    }

    public static void registerPush(Context context) {

        sReceiverPermission = context.getPackageName() + ".permission.MIXPUSH_RECEIVE";

        Set<String> keys = sPushManagerMap.keySet();
        for (String key : keys) {
            if (!TextUtils.isEmpty(sUsePushName) && sUsePushName.contains(key)) {
                sPushManagerMap.get(key).registerPush(context);
            } else {
//                sPushManagerMap.get(key).unRegisterPush(context);
            }
        }
    }
    private static QPushManager getPushManager(){
        if (sUsePushName == null){
            throw new RuntimeException("you need setSelector or setUsePushName");
        }

        return sPushManagerMap.get(removeLocale(sUsePushName));
    }

    public static void unRegisterPush(Context context) {
        if(getPushManager() != null)
            getPushManager().unRegisterPush(context);
    }

    public static void setUsePushName(String usePushName) {
        QPushClient.sUsePushName = addLocale(usePushName);

    }

    private static String removeLocale(String userPushName) {
        if(userPushName.contains("_")) {
            return userPushName.split("_")[0];
        }
        return userPushName;
    }

    private static String addLocale(String usePushName) {
        Logger.i("lex" + " usePushName : " + usePushName + "  lang : " + Locale.getDefault().getLanguage() + "  country : " + Locale.getDefault().getCountry());

        if(usePushName.split("_").length < 2) {
            StringBuilder builder = new StringBuilder(usePushName);
            builder.append("_");
            builder.append(Locale.getDefault().getLanguage());
//            builder.append("_");
//            builder.append(Locale.getDefault().getCountry());

            usePushName = builder.toString();
        }
        return usePushName;
    }

    public static void setAlias(Context context, String alias) {
        if(getPushManager() != null)
            getPushManager().setAlias(context, alias);
    }

    public static void unsetAlias(Context context, String alias) {
        if(getPushManager() != null)
            getPushManager().unsetAlias(context, alias);
    }

    public static void setTags(Context context, String... tags){
        if(getPushManager() != null)
           getPushManager().setTags(context, tags);
    }

    public static void unsetTags(Context context, String... tags){
        if(getPushManager() != null)
            getPushManager().unsetTags(context, tags);
    }

    public static void clearNotification(Context context){
        if(getPushManager() != null)
            getPushManager().clearNotification(context);
    }

    public static class Selector {
        public String select(Map<String, QPushManager> pushAdapterMap, String brand) {
            Logger.i("lex" + pushAdapterMap.toString() + "  brand : " + brand);
            if (pushAdapterMap.containsKey(Constants.NAME_XIAOMI)
                    && Rom.isMiui()) {
                return Constants.NAME_XIAOMI;
            } else if (pushAdapterMap.containsKey(Constants.NAME_HUAWEI)
                    && Rom.isEmui()
                   /* && Build.VERSION.SDK_INT < 26*/) {
                // TODO: 2018/4/2 华为push部分机型暂不支持8.0，先测试
                return Constants.NAME_HUAWEI;
            } else if (pushAdapterMap.containsKey(Constants.NAME_OPPO)
                    && Rom.isOppo()) {
                return Constants.NAME_OPPO;
            } else if(pushAdapterMap.containsKey(Constants.NAME_VIVO) && Rom.isVivo()) {
                return Constants.NAME_VIVO;
            } else if (pushAdapterMap.containsKey(Constants.NAME_MEIZU)
                    && Rom.isFlyme()) {
                return Constants.NAME_MEIZU;
            } else if (pushAdapterMap.containsKey("getui")) {
                return "getui";
            }
            return Constants.NAME_THIRD;
        }
    }

    private static QMessageProvider mQMessageProvider = new QMessageProvider() {
        @Override
        public void onReceivePassThroughMessage(Context context, QPushMessage message) {
            message.setNotify(0);
            Intent intent = new Intent(QPushMessageReceiver.RECEIVE_THROUGH_MESSAGE);
            intent.putExtra("message", message);
            context.sendBroadcast(intent, sReceiverPermission);
            Log.d("onReceivePassThrough", message.getContent());

            if (sMixPushIntentServiceClass != null){
                intent.setClass(context,sMixPushIntentServiceClass);
                context.startService(intent);
            }
        }

        @Override
        public void onNotificationMessageClicked(Context context, QPushMessage message) {
            message.setNotify(1);
            Intent intent = new Intent(QPushMessageReceiver.NOTIFICATION_CLICKED);
            intent.putExtra(QPushMessageReceiver.MESSAGE, message);
            context.sendBroadcast(intent, sReceiverPermission);
            Log.d("onNotificationClicked", message.getContent());

            if (sMixPushIntentServiceClass != null){
                intent.setClass(context,sMixPushIntentServiceClass);
                context.startService(intent);
            }
        }

        @Override
        public void onNotificationMessageArrived(Context context, QPushMessage message) {
//            Map<String, String> opsMessage = JsonUtils.getGson().fromJson(message.getContent(), Map.class);
//            if(opsMessage != null && opsMessage.containsKey("F")){
//                message.setNotify(opsMessage.get("F").toString().hashCode());
//            }
            Intent intent = new Intent(QPushMessageReceiver.NOTIFICATION_ARRIVED);
            intent.putExtra("message", message);
            context.sendBroadcast(intent, sReceiverPermission);
            Log.d("onNotificationArrived", message.getContent());

            if (sMixPushIntentServiceClass != null){
                intent.setClass(context,sMixPushIntentServiceClass);
                context.startService(intent);
            }
        }
    };
}
