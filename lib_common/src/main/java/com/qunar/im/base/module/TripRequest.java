package com.qunar.im.base.module;

public class TripRequest {
    private String userName;
    private String q_ckey;
    private long updateTime;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getQ_ckey() {
        return q_ckey;
    }

    public void setQ_ckey(String q_ckey) {
        this.q_ckey = q_ckey;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
}
