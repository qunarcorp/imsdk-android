package com.qunar.im.rtc.vconference.rpc.messages;

import com.qunar.im.rtc.vconference.rpc.results.JoinRoomResp;

import java.util.List;

/**
 * Created by xinbo.wang on 2017-04-05.
 */
public class ParticipantPublished extends RpcJson {

    public ParticipantPublishedParam params;

    public static class ParticipantPublishedParam
    {
        public String id;
        public List<JoinRoomResp.Stream> streams;
    }
}
