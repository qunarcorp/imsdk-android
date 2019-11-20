package com.qunar.im.base.jsonbean;

import java.util.List;

/**
 * Created by xinbo.wang on 2015/6/4.
 */
public class GetMucVCardData extends BaseResult {
    public String domain;
    public List<MucInfo> mucs;
    static public class MucInfo{
        public String muc_name;
        public String version;
    }
}
