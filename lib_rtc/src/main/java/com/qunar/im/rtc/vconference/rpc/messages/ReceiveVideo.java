package com.qunar.im.rtc.vconference.rpc.messages;

/**
 * Created by xinbo.wang on 2017-04-05.
 */
public class ReceiveVideo extends RpcJson {

    public ReceiveVideoParam params;

    public static class ReceiveVideoParam extends CommonParam{
        public String sender;
        public String sdpOffer;
    }
}
