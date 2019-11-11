package com.qunar.im.rtc.vconference.rpc.results;


import com.qunar.im.rtc.vconference.rpc.CommonJson;

/**
 * Created by xinbo.wang on 2017-04-05.
 */
public class PublishVideoResp extends CommonJson {

    public PublishVideoResult result;
    public static class PublishVideoResult{
        public String sdpAnswer;
    }
}
