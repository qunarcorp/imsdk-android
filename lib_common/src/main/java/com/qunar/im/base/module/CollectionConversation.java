package com.qunar.im.base.module;

/**
 * Created by hubin on 2017/11/24.
 */

public class CollectionConversation {
    private String msgId;
    private String originFrom;
    private String originTo;
    private String originType;
    private String xmppId;
    private String from;
    private String to;
    private String content;
    private String type;
    private String state;
    private String direction;
    private String readedTag;
    private String lastUpdateTime;
    private String realJid;
    private int unCount;

    public int getUnCount() {
        return unCount;
    }

    public void setUnCount(int unCount) {
        this.unCount = unCount;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getOriginFrom() {
        return originFrom;
    }

    public void setOriginFrom(String originFrom) {
        this.originFrom = originFrom;
    }

    public String getOriginTo() {
        return originTo;
    }

    public void setOriginTo(String originTo) {
        this.originTo = originTo;
    }

    public String getOriginType() {
        return originType;
    }

    public void setOriginType(String originType) {
        this.originType = originType;
    }

    public String getXmppId() {
        return xmppId;
    }

    public void setXmppId(String xmppId) {
        this.xmppId = xmppId;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getReadedTag() {
        return readedTag;
    }

    public void setReadedTag(String readedTag) {
        this.readedTag = readedTag;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getRealJid() {
        return realJid;
    }

    public void setRealJid(String realJid) {
        this.realJid = realJid;
    }
}
