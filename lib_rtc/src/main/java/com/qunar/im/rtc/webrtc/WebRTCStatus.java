package com.qunar.im.rtc.webrtc;

/**
 * Created by xinbo.wang on 2017-01-09.
 */
public enum  WebRTCStatus {
    CONNECT("connect"),
    CONNECTING("connecting"),
    PICKUP("pickup"),
    CANCEL("cancel"),
    TIMEOUT("timeout"),
    DENY("deny"),
    CLOSE("close"),
    DISCONNECT("disconnect"),
    BUSY("busy");

    private String type ;

    WebRTCStatus(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
