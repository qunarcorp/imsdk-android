package com.qunar.im.rtc.vconference.rpc.messages;

/**
 * Created by xinbo.wang on 2017-04-05.
 */
public class JoinRoom extends RpcJson {

    public JoinRoomParams params;

    public static class JoinRoomParams extends CommonParam
    {
        public String user;
        public String room;
        public long startTime;
        public int plat;
        public String topic;
        public boolean dataChannels = false;
    }
}
