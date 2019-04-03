package com.qunar.im.ui.presenter.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.text.TextUtils;

import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.module.RecentConversation;
import com.qunar.im.ui.presenter.IConversationsManagePresenter;
import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.ui.presenter.views.IConversationListView;
import com.qunar.im.base.structs.MessageStatus;
import com.qunar.im.base.structs.PushSettinsStatus;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.core.enums.LoginStatus;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.protobuf.dispatch.DispatchHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hubin on 2017/8/18.
 */

public class PbConversationManagePresenter implements IConversationsManagePresenter, IMNotificaitonCenter.NotificationCenterDelegate {
    private static final String TAG = PbConversationManagePresenter.class.getSimpleName();
    //会话列表展示界面
    private IConversationListView ConvView;
    //核心连接管理类
    private ConnectionUtil connectionUtil;
    //会话列表消息list
    List<RecentConversation> list = new ArrayList<>();

//    //判断是否显示头部
//    private boolean showhead;

    //线程安全的LinkHashMap
    Map<String, String> atMessageMap;

    //消息不提醒列表
    Map<String, String> dndMap = new HashMap<>();
    public List<String> dndList;
    public Vibrator vibrator;
    public volatile long lastMsgTime = 0;

    @Override
    public void setCoversationListView(IConversationListView view) {
        this.ConvView = view;
        connectionUtil = ConnectionUtil.getInstance();
        addEvent();

    }



    @Override
    public void removeEvent() {
        //注册二人消息通知
        connectionUtil.removeEvent(this, QtalkEvent.Chat_Message_Text_After_DB);
        //注册登陆状态通知
        connectionUtil.removeEvent(this, QtalkEvent.LOGIN_EVENT);
        //注册已读标记通知
        connectionUtil.removeEvent(this, QtalkEvent.Message_Read_Mark);
        //注册群组消息通知
        connectionUtil.removeEvent(this, QtalkEvent.Group_Chat_Message_Text_After_DB);
        //注册撤销消息通知
        connectionUtil.removeEvent(this, QtalkEvent.Chat_Message_Revoke);
        //注册群名片更新通知
        connectionUtil.removeEvent(this, QtalkEvent.Update_Muc_Vcard);
        //注册移除会话通知
        connectionUtil.removeEvent(this, QtalkEvent.Remove_Session);
        //注册加密会话通知
        connectionUtil.removeEvent(this, QtalkEvent.CHAT_MESSAGE_ENCRYPT);
        //注册置顶消息变动通知
        connectionUtil.removeEvent(this, QtalkEvent.Update_Placed_Top);
        //注册提醒消息变动通知
        connectionUtil.removeEvent(this, QtalkEvent.Update_ReMind);
        //注册删除消息通知
        connectionUtil.removeEvent(this, QtalkEvent.Delete_Message);
        //订阅号 机器人消息
        connectionUtil.removeEvent(this, QtalkEvent.CHAT_MESSAGE_SUBSCRIPTION);
        //消息发送失败通知
        connectionUtil.removeEvent(this, QtalkEvent.Chat_Message_Send_Failed);
        //代收消息通知
        connectionUtil.removeEvent(this, QtalkEvent.Collection_Message_Text);
        //显示消息
        connectionUtil.removeEvent(this, QtalkEvent.Show_List);
        //修改消息状态
        connectionUtil.removeEvent(this, QtalkEvent.Chat_Message_Read_State);
//        //群有变动
//        connectionUtil.addEvent(this,QtalkEvent.Have_Muc_Update);
        //清空消息通知
        connectionUtil.removeEvent(this, QtalkEvent.CLEAR_MESSAGE);
        //是否显示头部
        connectionUtil.removeEvent(this,QtalkEvent.SHOW_HEAD);

        //刷新session列表View
        connectionUtil.removeEvent(this, QtalkEvent.NOTIFY_SESSION_LIST);
    }

    private void addEvent() {
        //注册二人消息通知
        connectionUtil.addEvent(this, QtalkEvent.Chat_Message_Text_After_DB);
        //注册登陆状态通知
        connectionUtil.addEvent(this, QtalkEvent.LOGIN_EVENT);
        //注册已读标记通知
        connectionUtil.addEvent(this, QtalkEvent.Message_Read_Mark);
        //注册群组消息通知
        connectionUtil.addEvent(this, QtalkEvent.Group_Chat_Message_Text_After_DB);
        //注册撤销消息通知
        connectionUtil.addEvent(this, QtalkEvent.Chat_Message_Revoke);
        //注册群名片更新通知
        connectionUtil.addEvent(this, QtalkEvent.Update_Muc_Vcard);
        //注册移除会话通知
        connectionUtil.addEvent(this, QtalkEvent.Remove_Session);
        //注册加密会话通知
        connectionUtil.addEvent(this, QtalkEvent.CHAT_MESSAGE_ENCRYPT);
        //注册置顶消息变动通知
        connectionUtil.addEvent(this, QtalkEvent.Update_Placed_Top);
        //注册提醒消息变动通知
        connectionUtil.addEvent(this, QtalkEvent.Update_ReMind);
        //注册删除消息通知
        connectionUtil.addEvent(this, QtalkEvent.Delete_Message);
        //订阅号 机器人消息
        connectionUtil.addEvent(this, QtalkEvent.CHAT_MESSAGE_SUBSCRIPTION);
        //消息发送失败通知
        connectionUtil.addEvent(this, QtalkEvent.Chat_Message_Send_Failed);
        //代收消息通知
        connectionUtil.addEvent(this, QtalkEvent.Collection_Message_Text);
        //显示消息
        connectionUtil.addEvent(this, QtalkEvent.Show_List);
        //修改消息状态
        connectionUtil.addEvent(this, QtalkEvent.Chat_Message_Read_State);

        connectionUtil.addEvent(this, QtalkEvent.REFRESH_NICK);
//        //群有变动
//        connectionUtil.addEvent(this,QtalkEvent.Have_Muc_Update);
        //清空消息通知
        connectionUtil.addEvent(this, QtalkEvent.CLEAR_MESSAGE);

        //刷新session列表View
        connectionUtil.addEvent(this, QtalkEvent.NOTIFY_SESSION_LIST);

        connectionUtil.addEvent(this,QtalkEvent.SHOW_HEAD);
    }

    /**
     * 删除会话
     */
    @Override
    public void deleteCoversation() {
        connectionUtil.deleteCoversationAndMessage(ConvView.getXmppId(), ConvView.getRealUserId());
    }


    @Override
    public void deleteChatRecord() {

    }

    //显示会话界面数据
    @Override
    public void showRecentConvs(final boolean refreshDND) {
        list = connectionUtil.SelectConversationList(ConvView.isOnlyUnRead());
        ConvView.setRecentConvList(list);
        if (refreshDND) {
            initDnd();
        }
    }

    private void showAtMessage() {
        DispatchHelper.Async("initAtMessage", new Runnable() {
            @Override
            public void run() {
                connectionUtil.initAtMessage();
                ConvView.setRecentConvList(list);
            }
        });
    }

    private void showFileSharing(){
        ConvView.showFileSharing();
    }
    private void hidenFileSharing(){
        ConvView.hidenFileSharing();
    }

    //只notify列表
    public void showRecentConvsOnlyNotify() {
        ConvView.refresh();
    }

    @Override
    public void initReload(boolean toDB) {

        showRecentConvs(true);
//        initDnd();
    }

    @Override
    public void handleMessage(IMMessage message) {

    }

    @Override
    public void allRead() {

    }

    @Override
    public void markReadById() {
        if (ConvView != null) {
            String id = ConvView.getXmppId();
            RecentConversation rc = ConvView.getCurrentConv();
            if (TextUtils.isEmpty(id) || rc == null) {
                return;
            }
            boolean re = false;
            if (list != null && list.size() > 0) {
                if (rc.getConversationType() == ConversitionType.MSG_TYPE_CHAT || rc.getConversationType() == ConversitionType.MSG_TYPE_COLLECTION
                        || rc.getConversationType() == ConversitionType.MSG_TYPE_HEADLINE) {
                    connectionUtil.sendSingleAllRead(rc.getId(), rc.getId(),MessageStatus.STATUS_SINGLE_READED + "");
                    re = true;
                    //群有readmark 非群的要手动抛一个已读的通知 只是更新总的未读数
                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Show_Home_Unread_Count);
                } else if (rc.getConversationType() == ConversitionType.MSG_TYPE_GROUP) {
                    connectionUtil.sendGroupAllRead(rc.getId());
                    re = true;
                }
                if (re){
                    showRecentConvs(false);
                }
            }
        }
    }

    public void initDnd() {
        dndMap.clear();
        for (RecentConversation rc : list) {
            if (rc.getRemind() > 0) {
                dndMap.put(rc.getId(), rc.getRealUser());
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void prompt(final IMMessage message) {
        if (dndMap.containsKey(message.getConversationID())) {
            return;
        }
        if (message.getMsgType() != ProtoMessageOuterClass.MessageType.MessageTypeGroupNotify_VALUE) {
            if (System.currentTimeMillis() - lastMsgTime > 500
                    && message.getDirection() == IMMessage.DIRECTION_RECV) {
                if (!com.qunar.im.protobuf.common.CurrentPreference.getInstance().isBack()) {
                    if (com.qunar.im.protobuf.common.CurrentPreference.getInstance().isTurnOnMsgSound()) {
                        try {
//                            MediaUtils.playNewMsgSound(QunarIMApp.getContext());
                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            Ringtone r = RingtoneManager.getRingtone(CommonConfig.globalContext, notification);
                            r.play();

                        } catch (Exception e) {
//                        LogUtil.e(TAG, "error", e);
                        }
                    }
                    if (com.qunar.im.protobuf.common.CurrentPreference.getInstance().isTurnOnMsgShock() ||
                            message.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeShock_VALUE) {
                        if (vibrator == null && ConvView != null) {
                            vibrator = (Vibrator) ConvView.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                        }
                        if (vibrator != null) vibrator.vibrate(200);
                    }
                }
                lastMsgTime = System.currentTimeMillis();
            }
        }
    }

    private boolean isAtMe(String content) {
        if (TextUtils.isEmpty(content)) {
            return false;
        }
        String userName = CurrentPreference.getInstance().getUserName();
        return content.contains("@全体成员") ||
                content.contains("@所有人") ||
                content.contains("@all") ||
                content.contains("@ALL") ||
                content.contains("@All") ||
                (!TextUtils.isEmpty(userName) && content.contains("@" + userName));
    }


    //接到通知消息
    @Override
    public void didReceivedNotification(String key, Object... args) {
        switch (key) {
            //有消息更新一度状态
            case QtalkEvent.Message_Read_Mark://备注
                if (args[0].equals("HaveUpdate")) {
                    showRecentConvs(false);
                }
                break;
            case QtalkEvent.Chat_Message_Read_State:
                if (args != null && args.length > 0) {
                    //已读移除相应的at消息
                    Map<String, String> atMap = connectionUtil.getAtMessageMap();
                    if (atMap != null) {
                        IMMessage imMessage = (IMMessage) args[0];
                        if (imMessage != null) {
                            atMap.remove(imMessage.getConversationID());
                        }
                    }
                    showRecentConvs(false);
                }
                break;
            //收到新消息
            case QtalkEvent.CHAT_MESSAGE_SUBSCRIPTION:
                try {
                    IMMessage imMessage = (IMMessage) args[0];
                    if (imMessage.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeNotice_VALUE || imMessage.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeSystem_VALUE
                            || imMessage.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeGrabMenuVcard_VALUE || imMessage.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeGrabMenuResult_VALUE
                            || imMessage.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeQCZhongbao_VALUE) {
                        showRecentConvs(false);
//                        if (!imMessage.isCarbon()) {
                        prompt(imMessage);
//                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case QtalkEvent.Chat_Message_Text_After_DB:
                showRecentConvs(false);
                try {
                    IMMessage imMessage = (IMMessage) args[0];
                    if (!imMessage.isCarbon()) {
                        if (imMessage.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeShock_VALUE) {//窗口抖动
                            IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.SHAKE_WINDOW);
                        } else {
                            prompt(imMessage);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            //收到群组新消息
            case QtalkEvent.Group_Chat_Message_Text_After_DB:
                showRecentConvs(false);
                IMMessage imMessage = (IMMessage) args[0];
                //是否是at自己
                if (isAtMe(imMessage.getBody())) {
                    showAtMessage();
                }
                if (!TextUtils.isEmpty(imMessage.getRealfrom()) && !imMessage.getRealfrom().equals(com.qunar.im.protobuf.common.CurrentPreference.getInstance().getPreferenceUserId())) {
                    prompt(imMessage);
                }
                break;
            //收到登陆状态提醒
            case QtalkEvent.LOGIN_EVENT:
                if (args[0].equals(LoginStatus.Login)) {
                    com.qunar.im.protobuf.common.CurrentPreference.getInstance().setTurnOnMsgSound(ConnectionUtil.getInstance().getPushStateBy(PushSettinsStatus.SOUND_INAPP));
                    com.qunar.im.protobuf.common.CurrentPreference.getInstance().setTurnOnMsgShock(ConnectionUtil.getInstance().getPushStateBy(PushSettinsStatus.VIBRATE_INAPP));

                    showRecentConvs(false);

                    showAtMessage();

                }
                break;
            //收到撤销消息通知
            case QtalkEvent.Chat_Message_Revoke:
                showRecentConvs(false);
                break;
            //收到更新群信息通知
            case QtalkEvent.Update_Muc_Vcard:
//                showRecentConvs();
                showRecentConvsOnlyNotify();
                break;
            //收到删除会话通知
            case QtalkEvent.Remove_Session:
                if (args != null && args.length > 0) {
                    showRecentConvs(false);
                }
                break;
            case QtalkEvent.CHAT_MESSAGE_ENCRYPT:
                IMMessage message = (IMMessage) args[0];
                ConvView.parseEncryptMessage(message);
                break;
            //收到置顶变动通知
            case QtalkEvent.Update_Placed_Top:
                showRecentConvs(false);
                break;
            //收到提醒变动通知
            case QtalkEvent.Update_ReMind:
                showRecentConvs(false);
                initDnd();
                break;
            //收到删除消息通知
            case QtalkEvent.Delete_Message:
                showRecentConvs(false);
                break;
            case QtalkEvent.Chat_Message_Send_Failed:
                showRecentConvs(false);
                break;
            //代收消息通知
            case QtalkEvent.Collection_Message_Text:
                showRecentConvs(false);
                IMMessage cMessage = (IMMessage) args[0];
                prompt(cMessage);
                break;
            case QtalkEvent.Show_List:
                showRecentConvs(false);
                break;
            case QtalkEvent.REFRESH_NICK:
                showRecentConvs(false);
                break;

            case QtalkEvent.CLEAR_MESSAGE:
                showRecentConvs(false);
                break;
            case QtalkEvent.NOTIFY_SESSION_LIST:
                showRecentConvsOnlyNotify();
                break;

            case QtalkEvent.SHOW_HEAD:
                if((boolean)args[0]){
                    showFileSharing();
                }else{
                    hidenFileSharing();
                }
                break;
            //登陆群有变动
//            case QtalkEvent.Have_Muc_Update:
//                showRecentConvs(false,true);
//                break;
        }
    }
}
