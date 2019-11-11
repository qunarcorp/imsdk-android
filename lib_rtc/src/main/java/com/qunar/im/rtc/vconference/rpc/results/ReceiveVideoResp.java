package com.qunar.im.rtc.vconference.rpc.results;

import com.qunar.im.rtc.vconference.rpc.CommonJson;

/**
 * Created by xinbo.wang on 2017-04-05.
 */
public class ReceiveVideoResp extends CommonJson {

    public ReceiveVideoResult result;
    public static class  ReceiveVideoResult{
        public String sdpAnswer;
        public String sessionId;
    }
}
