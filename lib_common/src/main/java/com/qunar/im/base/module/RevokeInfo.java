package com.qunar.im.base.module;

/**
 * Created by hubin on 2017/8/25.
 */

public class RevokeInfo {
//    {\"fromId\":\"hubo.hu@ejabhost1\",\"message\":\"revoke a message\",\"messageId\":\"a275749053ca463bbccb87546ef5e918\"}"
    //发送人
    private String fromId;
    //消息详情
    private String message;
    //撤销消息id
    private String messageId;
    //消息类型
    private String messageType;
    //发送人
    private String from;
    //收
    private String to;

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
