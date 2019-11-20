package com.qunar.im.base.jsonbean;

/**
 * 会话同步
 */
public class SyncConversation {
//    id	带@的id
//    timestamp	毫秒时间戳
//    realjid	consult消息中的realjid,如果不是consult消息，这个地方填空
//            type
//    groupchat/chat/consult 与消息类型保持一致
//
//    qchatid	4/5
    public String id;
    public long timestamp;
    public String realjid;
    public String type;
    public String qchatid;
}


