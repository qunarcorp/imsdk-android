package com.qunar.im.rtc.vconference.rpc.messages;

/**
 * Created by xinbo.wang on 2017-04-05.
 */
public class PublishVideo extends RpcJson{

    public PublishVideoParam params;

    public static class PublishVideoParam extends CommonParam{
        public String sdpOffer;
    }
}
