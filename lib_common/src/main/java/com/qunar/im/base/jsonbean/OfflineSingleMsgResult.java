package com.qunar.im.base.jsonbean;

import java.util.List;

/**
 * Created by xinbo.wang on 2015/4/2.
 */
public class OfflineSingleMsgResult extends BaseJsonResult {
    public List<OfflineMsgResult> data;
    public static class OfflineMsgResult {
        public String F;
        public String FH;
        public String T;
        public String TH;
        public String B;
        public int R;
    }
}
