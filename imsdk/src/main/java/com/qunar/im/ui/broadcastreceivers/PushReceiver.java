package com.qunar.im.ui.broadcastreceivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseIntArray;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.module.Nick;
import com.qunar.im.common.CurrentPreference;
import com.qunar.im.base.shortutbadger.ShortcutBadger;
import com.qunar.im.base.util.ChatTextHelper;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.MediaUtils;
import com.qunar.im.base.util.MemoryCache;
import com.qunar.im.ui.util.ProfileUtils;
import com.qunar.im.base.util.Utils;
import com.qunar.im.base.util.graphics.MyDiskCache;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.ui.R;
import com.qunar.im.ui.entity.OpsPushMessage;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.QtalkStringUtils;

import java.io.File;


public class PushReceiver extends BroadcastReceiver {
    private static final String TAG = "PushReceiver";
    private static SparseIntArray countMap = new SparseIntArray();
    private static NotificationManager notificationManager;

    public void init(Context context) {
        notificationManager =
                (NotificationManager) CommonConfig.globalContext.getApplicationContext().
                        getSystemService(Context.NOTIFICATION_SERVICE);
        if (TextUtils.isEmpty(CurrentPreference.getInstance().getUserid())) {
//            ISystemPresenter presenter = new SystemPresenter();
            //加载了一些配置
//            presenter.loadPreference(context,false);
        }
        changeSound(context);
    }

    private void changeSound(Context context) {
        if (CommonConfig.isQtalk) {
            MediaUtils.loadNewMsgSound(QunarIMApp.getContext(), CommonConfig.DEFAULT_NEW_MSG);
            return;
        }
        if (!CommonConfig.isQtalk && CurrentPreference.getInstance().isMerchants()) {
            int soundId = -1;
            try {
                soundId = context.
                        getResources().
                        getIdentifier("atom_ui_qcaht_consult_sound", "raw", context.getPackageName());
            } catch (Exception e) {
                LogUtil.e(TAG, e.getMessage());
            }

            if (soundId > 0) {
                MediaUtils.unLoadNewMsgSound();
                MediaUtils.loadNewMsgSound(context, soundId);
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.d(TAG, "receiver received");
        if (notificationManager == null) {
            //接收到一个广播
            init(context);
        }
        if (intent != null && CommonConfig.globalContext.getPackageName().equals(intent.getPackage())) {
            if ("com.qunar.ops.push.CLEAR_NOTIFY".equals(intent.getAction())) {
                MemoryCache.emptyCache();
                countMap.clear();
                notificationManager.cancelAll();
                notificationManager = null;
            } else {
                if (!CommonConfig.leave) return;
                if (!CurrentPreference.getInstance().isOfflinePush()) return;
                Bundle extras = intent.getExtras();
                if (extras == null || !extras.containsKey("message")) return;
                String msg = extras.getString("message");
                LogUtil.d(TAG, msg);
                if (TextUtils.isEmpty(msg)) return;
                //没有开启消息推送， 直接返回
                try {
                    Logger.i("收到推送消息：" + msg);
                    final OpsPushMessage opsMessage = JsonUtils.getGson().fromJson(msg, OpsPushMessage.class);
                    final int msgType = Integer.parseInt(opsMessage.Mtype);
                    if (msgType == ProtoMessageOuterClass.MessageType.MessageTypeRedPackInfo_VALUE ||
                            msgType == ProtoMessageOuterClass.MessageType.MessageTypeAAInfo_VALUE) return;

                    final String notification = ChatTextHelper.showContentType(opsMessage.B, msgType);
                    switch (opsMessage.type) {
                        case "subscript":
                        case "consult":
                        case "chat":
                            final String jid;
//                            if(TextUtils.isEmpty(opsMessage.D)) {
                            jid = QtalkStringUtils.userId2Jid(opsMessage.F);
//                            }
//                            else jid = opsMessage.F+"@"+opsMessage.D;
                            ConnectionUtil.getInstance().getUserCard(jid, new IMLogicManager.NickCallBack() {
                                @Override
                                public void onNickCallBack(Nick nick) {
                                    if (nick != null) {
                                        showNotification(notification, nick.getName(), jid, opsMessage.type,
                                                System.currentTimeMillis(), msgType, nick.getHeaderSrc());
                                    } else {
                                        showNotification(notification, jid, jid, opsMessage.type,
                                                System.currentTimeMillis(), msgType, "");
                                    }
                                }
                            }, false, false);
//                            ProfileUtils.loadNickName(jid, true, new ProfileUtils.LoadNickNameCallback() {
//                                @Override
//                                public void finish(String name) {
//                                    showNotification(notification, name, jid, opsMessage.type,
//                                            System.currentTimeMillis(), msgType);
//                                }
//                            });
                            break;
                        case "collectionChat":
                            jid = QtalkStringUtils.userId2Jid(opsMessage.F);
                            ConnectionUtil.getInstance().getCollectionUserCard(jid, new IMLogicManager.NickCallBack() {
                                @Override
                                public void onNickCallBack(Nick nick) {
                                    if (nick != null) {
                                        showNotification(notification, "(代)"+nick.getName(), jid, opsMessage.type,
                                                System.currentTimeMillis(), msgType, nick.getHeaderSrc());
                                    } else {
                                        showNotification(notification, jid, jid, opsMessage.type,
                                                System.currentTimeMillis(), msgType, "");
                                    }
                                }
                            }, false, false);
                            break;
                        case "group":
                            final String groupName = QtalkStringUtils.parseBareIdWithoutAt(opsMessage.F);
                            final String nickOrUid = QtalkStringUtils.parseResourceWithoutAt(opsMessage.F);
                            final String groupJid = QtalkStringUtils.parseBareIdWithoutAt(opsMessage.Gjid);
                            if (CommonConfig.isQtalk) {
//                                showNotification(nickOrUid + ":" + notification, groupName, groupName, opsMessage.type,
//                                        System.currentTimeMillis(), msgType);

                                showNotification(nickOrUid + ":" + notification, groupName, groupJid, opsMessage.type,
                                        System.currentTimeMillis(), msgType, nickOrUid);
//                                ConnectionUtil.getInstance(context).getUserCard(nickOrUid, new IMLogicManager.NickCallBack() {
//                                    @Override
//                                    public void onNickCallBack(Nick nick) {
//                                        if(nick != null){
//                                        }else {
//                                            showNotification(nickOrUid + ":" + notification, groupName, groupName, opsMessage.type,
//                                        System.currentTimeMillis(), msgType, "");
//                                        }
//                                    }
//                                }, false, false);
                            } else {
                                String memberJid;
//                                if(TextUtils.isEmpty(opsMessage.D)) {
                                memberJid = QtalkStringUtils.userId2Jid(nickOrUid);
//                                }
//                                else memberJid = nickOrUid+"@"+opsMessage.D;
                                ProfileUtils.loadNickName(memberJid, true, new ProfileUtils.LoadNickNameCallback() {
                                    @Override
                                    public void finish(String name) {
                                        showNotification(name + ":" + notification, groupName, groupJid, opsMessage.type,
                                                System.currentTimeMillis(), msgType, "");
                                    }
                                });
                            }
                            break;
                        case "collectionGroup":
                            final String cgroupName = QtalkStringUtils.parseBareIdWithoutAt(opsMessage.F);
                            String cnickOrUid = QtalkStringUtils.parseResourceWithoutAt(opsMessage.F);
                            final String cgroupJid = QtalkStringUtils.parseBareIdWithoutAt(opsMessage.Gjid);
//
                            ConnectionUtil.getInstance().getCollectionUserCard(cnickOrUid, new IMLogicManager.NickCallBack() {
                                @Override
                                public void onNickCallBack(Nick nick) {
                                    showNotification(nick.getName() + ":" + notification, cgroupName, cgroupJid, opsMessage.type,
                                            System.currentTimeMillis(), msgType, nick.getName());
//
                                }
                            },false,false);


                            break;
                        default:
                            String title = opsMessage.F;
                            if (opsMessage.type.equals("headline")) title = "系统消息";
                            showNotification(notification, title, opsMessage.F, opsMessage.type,
                                    System.currentTimeMillis(), msgType, "");
                            break;
                    }
                } catch (Exception ex) {
                    LogUtil.e(TAG, "ERROR", ex);
                }
            }
        }
    }

    /**
     * 显示通知
     */
    private void showNotification(String message, String title, String id, String type, long time, int msgType, String imgUrl) {
        LogUtil.d(TAG, message);
        if (TextUtils.isEmpty(title)) title = "你有新消息";
//        int hascode = id.hashCode();
        int unknowHashcode = 0;
//        int unknowHashcode = 1916565926;//0;
        int hascode = unknowHashcode + id.hashCode();
        if(!CurrentPreference.getInstance().isShowContentPush()){
            title = "QTALK";
            message = "您有新消息，点击查看";
//            hascode = str / 10 * 10 + title.hashCode();
            hascode = unknowHashcode + title.hashCode();
        }
        String jid = null;
        Bitmap largeIcon = null;
        switch (type) {
            case "chat":
                jid = id;

                largeIcon = (Bitmap) MemoryCache.getMemoryCache(id);
                if (largeIcon == null) {
                    String imageUrl = ProfileUtils.getGravatarUrl(id);
                    if (!TextUtils.isEmpty(imageUrl)) {
                        File file = MyDiskCache.getSmallFile(imageUrl);
                        if (file.exists()) {
                            largeIcon = BitmapFactory.decodeFile(file.getPath());
                        }
                    }
                    if (largeIcon != null) MemoryCache.addObjToMemoryCache(id, largeIcon);
                    else
                        largeIcon = BitmapFactory.decodeResource(CommonConfig.globalContext.getResources(),
                                R.drawable.atom_ui_default_gravatar);
                }
                break;
            case "group":
                jid = id;//QtalkStringUtils.roomId2Jid(ProfileUtils.getRoomIdByName(id));
                largeIcon = (Bitmap) MemoryCache.getMemoryCache(jid);
                if (largeIcon == null) {
                    largeIcon = ProfileUtils.getGroupBitmap(jid);
                    if (largeIcon != null) MemoryCache.addObjToMemoryCache(jid, largeIcon);
                    else
                        largeIcon = BitmapFactory.decodeResource(CommonConfig.globalContext.getResources(),
                                R.drawable.atom_ui_ic_my_chatroom);
                }
                break;
            case "headline":
                jid = "headline";
                largeIcon = BitmapFactory.decodeResource(CommonConfig.globalContext.getResources(),
                        R.drawable.atom_ui_rbt_system);
                break;
        }
        if (TextUtils.isEmpty(jid)) jid = "null";
        if (largeIcon == null) {
            largeIcon = BitmapFactory.decodeResource(CommonConfig.globalContext.getResources(),
                    CommonConfig.globalContext.getApplicationInfo().icon);
        }
        int c = countMap.get(hascode, 0);
        c++;
        countMap.append(hascode, c);
        if (c > 1) message = "[" + c + "条]" + message;
        String content = ChatTextHelper.showContentType(message,
                msgType);
//        int systemIcon = ConnectionUtil.context.getApplicationInfo().icon;
//        if (Build.VERSION.SDK_INT >= 18) {
//            //该死的黑白图片，后续优化掉
//            try {
//                systemIcon = ConnectionUtil.context.
//                        getResources().
//                        getIdentifier("plat_small_icon", "drawable", ConnectionUtil.context.getPackageName());
//            } catch (Throwable t) {
//                LogUtil.e(TAG,"PUSH",t);
//            }
//        }
        //获取本应用程序信息
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = CommonConfig.globalContext.getPackageManager().getApplicationInfo(CommonConfig.globalContext.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String schema = "start_main_activity";
        if (applicationInfo != null) {
            schema = applicationInfo.metaData.getString("MAIN_SCHEMA");
        }
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://" + schema + "?jid=" + jid));
//        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema+"://start_main_activity?jid="+ jid));
        PendingIntent pendingIntent = PendingIntent.getActivity(CommonConfig.globalContext, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(CommonConfig.globalContext);
        boolean soundOn = CurrentPreference.getInstance().isTurnOnMsgSound();
        int defaults = Notification.DEFAULT_LIGHTS;
        if (soundOn) {
//            defaults |= Notification.DEFAULT_SOUND;
            if (CommonConfig.isQtalk) {//默认系统
                builder.setSound(Uri.parse("android.resource://" + CommonConfig.globalContext.getPackageName()
                        + "/" + R.raw.atom_ui_new_msg));
            } else {
                if (CurrentPreference.getInstance().isMerchants()) {//客服提示音
                    builder.setSound(Uri.parse("android.resource://" + CommonConfig.globalContext.getPackageName()
                            + "/" + R.raw.atom_ui_qcaht_consult_sound));
                }
            }
        }
        boolean vibrateOn = CurrentPreference.getInstance().isTurnOnMsgShock();
        if (vibrateOn) {
            defaults |= Notification.DEFAULT_VIBRATE;
        }
        // 5.0
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.drawable.atom_ui_small_icon_white);
            if (CommonConfig.isQtalk)
                builder.setColor(CommonConfig.globalContext.getResources().getColor(R.color.atom_ui_primary_color));
            else
                builder.setColor(CommonConfig.globalContext.getResources().getColor(R.color.atom_ui_qchat_logo_color));
        } else {

            builder.setSmallIcon(R.drawable.atom_ui_small_qtalk_icon);
        }

        builder.setContentTitle(title)
                .setContentIntent(pendingIntent)
                .setTicker(content)
                .setContentText(message)
                .setLights(Color.GREEN, 1000, 1000)
                .setPriority(Notification.PRIORITY_MAX)
                .setOngoing(false)
                .setWhen(time)
                .setLargeIcon(largeIcon)
//                .setSmallIcon(systemIcon)
                .setAutoCancel(true)
                .setDefaults(defaults);
        Logger.i("推送消息id = " + id + "  hashcode = " + String.valueOf(hascode));

        Notification notification = builder.build();
        //小米系统未读角标
        if (Utils.isMIUI()) {
            notificationManager.cancel(hascode);
            int total = ConnectionUtil.getInstance().SelectUnReadCount();
            ShortcutBadger.applyNotification(CommonConfig.globalContext.getApplicationContext(), notification, total);
        }
        notificationManager.notify(hascode, notification);
        if (soundOn) {
//            MediaUtils.playNewMsgSound(QunarIMApp.getContext());
        }
    }
}
