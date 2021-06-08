package com.qunar.im.base.structs;

/**
 * Created by xinbo.wang on 2015/5/8.
 */
public class MessageStatus {
//    public final static int STATUS_FAILED = 0;
//    public final static int STATUS_SUCCESS = 1;
//    public final static int STATUS_PROCESSION = 2;
//    public final static int STATUS_DELIVERY = 8;

    public final static int STATUS_SINGLE_DELIVERED = 3;//已送达
    public final static int STATUS_SINGLE_READED = 4;//已读
    public final static int STATUS_SINGLE_OPERATION = 7;//已操作
    public final static int STATUS_GROUP_READED = 2;
    public final static int STATUS_ALL_READED = 0;


    public final static int LOCAL_STATUS_FAILED = 0X00;//发送失败
    public final static int LOCAL_STATUS_PROCESSION = 0X01;//发送中
    public final static int LOCAL_STATUS_SUCCESS = 0X02;//发送成功
    public final static int LOCAL_STATUS_SUCCESS_PROCESSION = 0X03;//设置发送成功及发送中开关



    public final static int REMOTE_STATUS_CHAT_SUCCESS = 0X00;//发送到服务器
    public final static int REMOTE_STATUS_CHAT_DELIVERED = 0X01;//对方已接收
    public final static int REMOTE_STATUS_CHAT_READED = REMOTE_STATUS_CHAT_DELIVERED << 1;//0X02;//对方已读
    public final static int REMOTE_STATUS_CHAT_OPERATION = 0x04;//0x04 已操作状态

    public final static int REMOTE_STATUS_GROUP_READED = 0x03;//群消息已读


    public final static int MEDAL_HAVE = 0x01;//拥有
    public final static int MEDAL_WEAL = 0x02;//佩戴


    public static boolean isExistStatus(int status, int tag) {
        return (status & tag) != 0;
    }

    public static boolean isProcession(int status) {
        return (status & LOCAL_STATUS_SUCCESS_PROCESSION) == LOCAL_STATUS_PROCESSION;
    }


}
