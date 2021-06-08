package com.qunar.im.base.jsonbean;

import java.util.List;

/**
 * Created by saber on 15-9-23.
 */
public class BatchGroupHistoryMessageResult extends BaseJsonResult {
    public List<BatchGroupHistoryMsgData> data;
    public static class BatchGroupHistoryMsgData
    {
        public long Time;
        public String ID;
        public String Domain;
        public List<MultiOfflineMsgResult> Msg;
    }
}
