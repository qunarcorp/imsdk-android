package com.qunar.im.base.jsonbean;

/**
 * Created by xinbo.wang on 2016-12-29.
 */
public class WebRtcJson extends BaseResult {
    public String type;
    public WebRtcPayload payload;

    public static class WebRtcPayload extends BaseResult
    {
        public String sdp;
        public String type;
        public String candidate;
        public String id;
        public int label;
    }
}
