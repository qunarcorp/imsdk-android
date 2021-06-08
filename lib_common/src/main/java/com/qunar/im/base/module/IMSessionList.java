package com.qunar.im.base.module;

/**
 * Created by hubo.hu on 2017/8/24.
 */

public class IMSessionList {
    private String XmppId;
    private String RealJid;
    private String UserId;
    private String LastMessageId;
    private String LastUpdateTime;
    private String ChatType;
    private String ExtendedFlag;

    public String getXmppId() {
        return XmppId;
    }

    public void setXmppId(String xmppId) {
        XmppId = xmppId;
    }

    public String getRealJid() {
        return RealJid;
    }

    public void setRealJid(String realJid) {
        RealJid = realJid;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getLastMessageId() {
        return LastMessageId;
    }

    public void setLastMessageId(String lastMessageId) {
        LastMessageId = lastMessageId;
    }

    public String getLastUpdateTime() {
        return LastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        LastUpdateTime = lastUpdateTime;
    }

    public String getChatType() {
        return ChatType;
    }

    public void setChatType(String chatType) {
        ChatType = chatType;
    }

    public String getExtendedFlag() {
        return ExtendedFlag;
    }

    public void setExtendedFlag(String extendedFlag) {
        ExtendedFlag = extendedFlag;
    }
}
