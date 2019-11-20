package com.qunar.im.base.module;

import java.io.Serializable;

/**
 * Created by xinbo.wang on 2015/3/25.
 */
public class ChatRoomMember extends BaseModel implements Serializable,Comparable<ChatRoomMember> {
    public static final int OWNER = 0;
    public static final int ADMIN = 1;
    public static final int MEMBER= 2;
    public static final int NONE = 4;

    private String roomId;
    private String nickName;
    private String jid;
    private boolean isRevokeVoice;
    private int powerLevel = NONE;
    private String fuzzy;


    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getFuzzy() {
        return fuzzy;
    }

    public void setFuzzy(String fuzzy) {
        this.fuzzy = fuzzy;
    }

    public int getPowerLevel() {
        return powerLevel;
    }

    public void setPowerLevel(int powerLevel) {
        this.powerLevel = powerLevel;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    @Override
    public int compareTo(ChatRoomMember another) {
        if(this.powerLevel == another.powerLevel) return 0;
        if(this.powerLevel < another.powerLevel) return -1;
        else return 1;
    }

    @Override
    public boolean equals(Object another)
    {
        return another instanceof ChatRoomMember && (roomId+jid).equals(((ChatRoomMember) another).roomId +
            ((ChatRoomMember) another).jid);
    }

    @Override
    public int hashCode()
    {
        return (roomId+jid).hashCode();
    }
}
