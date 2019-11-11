package com.qunar.im.rtc.vconference.rpc.messages;

/**
 * Created by xinbo.wang on 2017-04-05.
 */
public class UnsubscribeFromVideo extends RpcJson {

    public UnsubscribeFromVideoParam params;
    public static class UnsubscribeFromVideoParam extends CommonParam{
        public String sender;
    }
}
