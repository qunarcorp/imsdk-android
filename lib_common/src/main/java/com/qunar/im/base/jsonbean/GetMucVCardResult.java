package com.qunar.im.base.jsonbean;

import java.util.List;

/**
 * Created by xinbo.wang on 2015/6/4.
 */
public class GetMucVCardResult extends BaseResult {

    public List<ExtMucVCard> data;

    static public class ExtMucVCard
    {
        public String domain;
        public List<MucVCard> mucs;
    }
    static public class MucVCard implements BaseData
    {
        public String MN;
        public String SN;
        public String MD;
        public String MT;
        public String MP;
        public String VS;
    }
}
