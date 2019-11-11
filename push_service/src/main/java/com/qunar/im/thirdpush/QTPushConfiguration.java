package com.qunar.im.thirdpush;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.coloros.mcssdk.PushManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.orhanobut.logger.Logger;
import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.thirdpush.client.google.FCMPushManager;
import com.qunar.im.thirdpush.client.huawei.HwPushManager;
import com.qunar.im.thirdpush.client.meizu.MzPushManager;
import com.qunar.im.thirdpush.client.mipush.MiPushManager;
import com.qunar.im.thirdpush.client.oppo.OppoPushManager;
import com.qunar.im.thirdpush.client.thirdpush.ThirdPushManager;
import com.qunar.im.thirdpush.client.vivo.VivoPushManager;
import com.qunar.im.thirdpush.core.QPushClient;
import com.qunar.im.thirdpush.core.QPushManager;
import com.qunar.im.utils.PingUtils;
import com.vivo.push.PushClient;
import com.vivo.push.util.VivoPushException;

import java.util.Map;

//import com.qunar.im.thirdpush.client.meizu.MzPushManager;

/**
 * Created by Lex lex on 2018/1/25.
 */

public class QTPushConfiguration {

    private static final String META_APPID_NAME_PREFIX = "appid=";
    private static final String META_APPKEY_NAME_PREFIX = "appkey=";

    private static final String CHANNEL_NAME = "消息通知";

    public static String OPPO_APP_ID = "";
    public static String OPPO_APP_KEY = "";
    public static String OPPO_APP_SECRET = "";
    public static String MIPUSH_APP_ID = "";
    public static String MIPUSH_APP_KEY = "";
    public static String MEIZU_APP_ID = "";
    public static String MEIZU_APP_KEY = "";

    private static NotificationManager mNotificationManager;
    private static NotificationChannel mChannel;
    private static String mNotification_Channel_id ;

    public static void initPush(final Context context) {
        //init notification channel
        initNotificationChannel(context);

        if(OPPO_APP_ID != null && OPPO_APP_ID.startsWith(META_APPID_NAME_PREFIX)){
            OPPO_APP_ID = OPPO_APP_ID.substring(META_APPID_NAME_PREFIX.length());
        }
        if(OPPO_APP_KEY != null && OPPO_APP_KEY.startsWith(META_APPKEY_NAME_PREFIX)){
            OPPO_APP_KEY = OPPO_APP_KEY.substring(META_APPKEY_NAME_PREFIX.length());
        }
        if(MIPUSH_APP_ID != null && MIPUSH_APP_ID.startsWith(META_APPID_NAME_PREFIX)){
            MIPUSH_APP_ID = MIPUSH_APP_ID.substring(META_APPID_NAME_PREFIX.length());
        }
        if(MIPUSH_APP_KEY != null && MIPUSH_APP_KEY.startsWith(META_APPKEY_NAME_PREFIX)){
            MIPUSH_APP_KEY = MIPUSH_APP_KEY.substring(META_APPKEY_NAME_PREFIX.length());
        }
        if(MEIZU_APP_ID != null && MEIZU_APP_ID.startsWith(META_APPID_NAME_PREFIX)){
            MEIZU_APP_ID = MEIZU_APP_ID.substring(META_APPID_NAME_PREFIX.length());
        }
        if(MEIZU_APP_KEY != null && MEIZU_APP_KEY.startsWith(META_APPKEY_NAME_PREFIX)){
            MEIZU_APP_KEY = MEIZU_APP_KEY.substring(META_APPKEY_NAME_PREFIX.length());
        }
        if(!TextUtils.isEmpty(MIPUSH_APP_ID) && !TextUtils.isEmpty(MIPUSH_APP_KEY)) {
            QPushClient.addPushManager(new MiPushManager(MIPUSH_APP_ID, MIPUSH_APP_KEY));
            QPushClient.addPushManager(new ThirdPushManager(MIPUSH_APP_ID, MIPUSH_APP_KEY));
        }
        if(!TextUtils.isEmpty(MEIZU_APP_ID) && !TextUtils.isEmpty(MEIZU_APP_KEY)) {
            QPushClient.addPushManager(new MzPushManager(MEIZU_APP_ID, MEIZU_APP_KEY));
        }
        QPushClient.addPushManager(new HwPushManager("", ""));
        if(!TextUtils.isEmpty(OPPO_APP_ID) && !TextUtils.isEmpty(OPPO_APP_KEY) && !TextUtils.isEmpty(OPPO_APP_SECRET)) {
            QPushClient.addPushManager(new OppoPushManager(OPPO_APP_ID, OPPO_APP_KEY, OPPO_APP_SECRET));
        }
        QPushClient.addPushManager(new FCMPushManager());
        if(PushClient.getInstance(context).isSupport()) {//检查是否支持vivo push
            boolean isVivoPushEnable = true;
            try {
                PushClient.getInstance(context).checkManifest();
            } catch (VivoPushException e) {
                Logger.i("不支持vivo push  checkManifest e : " + e);
                isVivoPushEnable = false;
            }
            if(isVivoPushEnable) {
                QPushClient.addPushManager(new VivoPushManager("", "", ""));
            }
        } else {
            Logger.i("不支持vivo push isSupport : " +  PushClient.getInstance(context).isSupport());
        }

        QPushClient.setSelector(new QPushClient.Selector() {
            @Override
            public String select(Map<String, QPushManager> pushAdapterMap, String brand) {
                // return GeTuiManager.NAME;
                //底层已经做了小米推送、华为、魅族推送、个推判断，也可以按照自己的需求来选择推送
                return super.select(pushAdapterMap, brand);
            }
        });
        // 配置接收推送消息的服务类
        QPushClient.setPushIntentService(PushIntentService.class);

        if(TextUtils.isEmpty(QPushClient.getUsePushName())){
            QPushClient.setUsePushName(Constants.NAME_XIAOMI);
        }
        //oppo push但不支持的，转化成小米
        if(Constants.NAME_OPPO.equalsIgnoreCase(QPushClient.getUsePushName()) && !PushManager.isSupportPush(context)) {
            QPushClient.setUsePushName(Constants.NAME_XIAOMI);
        }
//        registPush(context);
    }

    public static void registPush(final Context context) {
        BackgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Logger.i("准备注册推送push  BackgroundExecutor.execute");
                //优先检测google网路是否连通
                boolean isGoogleConnected = PingUtils.isGoogleConnected(context);
                Logger.i("push 检测firebase service isGoogleConnected : " + isGoogleConnected);
                boolean isGooglePlayServicesAvailable = false;
                //检测google service是否有效
                int result = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
                if(result == ConnectionResult.SUCCESS) {
                    isGooglePlayServicesAvailable = true;
                }
                Logger.i("push firebase检测是否支持google service : " + isGooglePlayServicesAvailable + "  result :  " + result);

                if(isGoogleConnected && isGooglePlayServicesAvailable) {
                    QPushClient.setUsePushName(Constants.NAME_FCM);
                }
                CommonConfig.mainhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Logger.i("注册推送push");
                        // 注册推送
                        QPushClient.registerPush(context);
                    }
                });
            }
        });



        // 绑定别名，一般是填写用户的ID，便于定向推送
//        QPushClient.setAlias(context, PhoneInfoUtils.getUniqueID());
        // 设置标签
//        QPushClient.setTags(context, PhoneInfoUtils.getUniqueID());
    }

    public static void unRegistPush(Context context){
        Logger.i("注销推送");
        QPushClient.unRegisterPush(context);
    }

    public static String getPlatName() {
        return QPushClient.getUsePushName();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private static void initNotificationChannel(Context context) {
        if(26 <= Build.VERSION.SDK_INT) {
            if (mNotificationManager == null) {
                mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            }
            mNotification_Channel_id = context.getPackageName();
            mChannel = new NotificationChannel(mNotification_Channel_id, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            mChannel.enableVibration(false);//震动不可用
            mChannel.setVibrationPattern(new long[]{0}); //设置震动频率
            mChannel.setSound(null, null); //设置没有声音
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }
}
