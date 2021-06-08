package com.qunar.im.base.common;

import com.qunar.im.protobuf.common.ProtoMessageOuterClass;

/**
 * Created by xinbo.wang on 2016-11-30.
 */
public class ConversitionType {
    //消息类型
    public final static int MSG_TYPE_CHAT = 0; //单人聊天
    public final static int MSG_TYPE_GROUP = 1; //群组聊天
    public final static int MSG_TYPE_HEADLINE = 2; //系统消息
    public final static int MSG_TYPE_CONSULT = 4;//consult消息  用户侧
    public final static int MSG_TYPE_CONSULT_SERVER = 5; //qchat note消息  客服侧
    public static final int MSG_TYPE_SHARE_LOCATION = 6;//shareLocation消息
    public static final int MSG_TYPE_COLLECTION=8; //代收消息
    public final static int MSG_TYPING = 16;
    public final static int MSG_GROUP_READ_MARK = 32;
    public final static int MSG_CHAT_READ_MARK = 64;
    public final static int MSG_TYPE_SUBSCRIPT = 128; //普通机器人
    public final static int MSG_TYPE_MSTAT = 256; //消息状态
    public final static int MSG_ALL_READ = 512;
    public static final int MSG_TYPE_IMPORTANT_SUBSCRIPT = 1024;// 单独显示的机器人
    public final static int MSG_TYPE_FRIENDS_REQUEST = 2048; //好友请求
    public final static int MSG_TYPE_ROBOT_WEB = 3072;
    public final static int MSG_TYPE_TRANSFER = 4096; //会话转移
    public final static int MSG_TYPE_REVOKE = 8192;
    public final static int MSG_TYPE_WEBRTC = MSG_TYPE_CONSULT<<1;
    public final static int MSG_TYPE_ENCRYPT = MSG_TYPE_CONSULT<<2;
    
    
    public static int getConversitionType(int signalType, String chatid) {
        if (signalType == ProtoMessageOuterClass.SignalType.SignalTypeChat_VALUE) {
            return ConversitionType.MSG_TYPE_CHAT;
        } else if (signalType == ProtoMessageOuterClass.SignalType.SignalTypeGroupChat_VALUE) {
            return ConversitionType.MSG_TYPE_GROUP;
        } else if (signalType == ProtoMessageOuterClass.SignalType.SignalTypeSubscription_VALUE) {//公众号消息
            return ConversitionType.MSG_TYPE_SUBSCRIPT;
        } else if (signalType == ProtoMessageOuterClass.SignalType.SignalTypeConsult_VALUE) {
            if ((ConversitionType.MSG_TYPE_CONSULT_SERVER + "").equals(chatid)) {
                return ConversitionType.MSG_TYPE_CONSULT_SERVER;
            } else {
                return ConversitionType.MSG_TYPE_CONSULT;
            }
        } else if ((signalType == ProtoMessageOuterClass.SignalType.SignalTypeHeadline_VALUE)) {//qtalk headline系统消息
            return ConversitionType.MSG_TYPE_HEADLINE;
        } else {
            return ConversitionType.MSG_TYPE_CHAT;
        }
    }
}
