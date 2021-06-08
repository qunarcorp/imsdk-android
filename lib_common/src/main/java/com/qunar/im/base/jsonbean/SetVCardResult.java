package com.qunar.im.base.jsonbean;

import java.util.List;

/**
 * Created by xinbo.wang on 2015/5/26.
 */
public class SetVCardResult extends BaseJsonResult {
    public List<SetVCardItem> data;

    public static class SetVCardItem
    {
        public String user;
        public String version;
    }
}
