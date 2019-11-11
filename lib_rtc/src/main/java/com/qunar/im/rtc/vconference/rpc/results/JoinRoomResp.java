package com.qunar.im.rtc.vconference.rpc.results;


import com.qunar.im.rtc.vconference.rpc.CommonJson;

import java.util.List;

/**
 * Created by xinbo.wang on 2017-04-05.
 */
public class JoinRoomResp extends CommonJson {
    public JoinRoomReuslt result;

    public static class JoinRoomReuslt
    {
        public List<JoinRoomValue> value;
        public String sessionId;
    }

    public static class JoinRoomValue{
        public String id;
        public List<Stream> streams;
        public int plat;
    }

    public static class Stream{
        public String id;
    }
}
