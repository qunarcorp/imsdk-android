package com.qunar.im.base.structs;


/**
 * Created by xinbo.wang on 2015/5/5.
 */
public class MessageType {
    public final static int ORDER_INFO_MSG = 888;
    public final static int EXTEND_MSG = 666;
    public final static int REVOKE_MESSAGE = -1;
    public final static int TEXT_MESSAGE = 1;
    public final static int IMAGE_MESSAGE = 3;
    public final static int VOICE_MESSAGE = 2;
    public final static int FILE_MESSAGE = 5;
    public static final int MSG_ACTION = 6;
    public static final int MSG_RICH_TEXT = 7;
    public static final int MSG_ACTION_RICH_TEXT = 8;
    public final static int COMMENT_MESSAGE = 9;
    public static final int SHAKE_MESSAGE = 10;
    public static final int MSG_NOTE = 11;
    public static final int MSG_GROUP_AT = 12;
    public static final int INVITE_MESSAGE = 15;
    public final static int LOCATION_MESSAGE = 16;
    public final static int VIDEO_MESSAGE = 32;
    public final static int READ_TO_DESTROY_MESSAGE = 128;
    public static final int MSG_ACTIVITY_MESSAGE = 511;
    public static final int MSG_AA_MESSAGE = 513;
    public static final int MSG_HONGBAO_MESSAGE = 512;
    public static final int MSG_AA_PROMPT = 1025;
    public static final int TRANSFER_BACK_SERVER = 1004;
    public static final int TRANSFER_BACK_CUSTOM = 1003;
    public static final int TRANSFER_TO_SERVER = 1002;
    public static final int TRANSFER_TO_CUSTOMER = 1001;
    public static final int MSG_HONGBAO_PROMPT = 1024;
    public static final int MSG_WEILVXING_ORDER = 3001;
    public static final int MSG_PRODUCT_CARD = 4096;
    public static final int SHARE_LOCATION = 8192;
    public static final int MSG_TYPE_RBT_SYSTEM = 268435456;
    public static final int MSG_TYPE_RBT_NOTICE = 134217728;
    public static final int MSG_TYPE_RUNSHING_ORDER = 2001;
    public static final int MSG_TYPE_RUNSHING_ORDER_RESPONSE=2002;

    public static final int MSG_TYPE_ROB_ORDER = 2003;//qchat抢单
    public static final int MSG_TYPE_ROB_ORDER_RESPONSE = 2004;//qchat抢单状态
    public static final int MSG_TYPE_ZHONG_BAO = 2005;//qchat 众包消息
    public static final int MSG_TYPE_CALL_CENTER_TYPE = 16384;
//    public static final int MSG_TYPE_CALL_CENTER_CMD = 16384<<1;
//    public static final int MSG_TYPE_WEBRTC = MSG_TYPE_CALL_CENTER_CMD<<1;
//    public static final int MSG_TYPE_WEBRTC_AUDIO = MSG_TYPE_WEBRTC<<1;

    public final static int PREDICTION_MSG = 668;
    public final static int EXTEND_OPS_MSG = 667;

    public static final int MSG_VIDEO_CONFERENCE=5001;
    public static final int MSG_RTC_VIDEO_PING = 5100;
    public static final int MSG_RTC_VIDEO_RESP = 5101;
    public static final int MSG_RTC_AUDIO_PING = 5102;
    public static final int MSG_RTC_AUDIO_RESP = 5103;

    public final static int RQUEST_COOKIE_MESSAGE = 10;
    public static final int MSG_HISTORY_SPLITER = -321;
    public static final int AUTO_REPLY_MESSAGE = 13;
    //没有更多消息了type
    public static final int MSG_TYPE_NO_MORE_MESSAGE = -99;



    //此处是朋友圈消息类型使用
    public static final int image = 1;//图片类型
    public static final int link = 2;//连接类型
    public static final int video = 3;//视频类型
}
