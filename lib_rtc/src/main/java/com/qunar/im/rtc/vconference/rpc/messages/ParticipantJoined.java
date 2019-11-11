package com.qunar.im.rtc.vconference.rpc.messages;

/**
 * Created by xinbo.wang on 2017-04-05.
 */
public class ParticipantJoined extends RpcJson {

    public ParticipantJoinedParam params;

    public static class ParticipantJoinedParam extends CommonParam{
        public String id;
    }
}
