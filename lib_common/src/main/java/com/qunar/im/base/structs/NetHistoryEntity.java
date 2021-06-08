package com.qunar.im.base.structs;

/**
 * Created by haoran.zuo on 2017/7/27.
 */
public class NetHistoryEntity {

    private String direction;
    private String from;
    private String from_host;
    private int limitnum;
    private String timestamp;
    private String to;
    private String to_host;

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFrom_host() {
        return from_host;
    }

    public void setFrom_host(String from_host) {
        this.from_host = from_host;
    }

    public int getLimitnum() {
        return limitnum;
    }

    public void setLimitnum(int limitnum) {
        this.limitnum = limitnum;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTo_host() {
        return to_host;
    }

    public void setTo_host(String to_host) {
        this.to_host = to_host;
    }
}
