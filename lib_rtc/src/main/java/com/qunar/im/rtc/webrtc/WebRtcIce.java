package com.qunar.im.rtc.webrtc;

import com.qunar.im.base.jsonbean.BaseResult;

import java.util.List;

/**
 * Created by xinbo.wang on 2016-12-30.
 */
public class WebRtcIce extends BaseResult {
    public int error;
    public String message="";
    public List<IceServers> serverses;
    public static class IceServers{
        public String username;
        public String password;
        public long ttl;
        public List<String> uris;
    }

}
