package com.qunar.im.base.jsonbean;

import java.util.List;
import java.util.Map;

/**
 * Created by xinbo.wang on 2015/5/26.
 */
public class GetVCardResult extends BaseJsonResult {

    public List<VCardGroup> data;

    static public class VCardGroup implements BaseData
    {
        public String domain;
        public List<VCardInfoN> users;
    }

    static public class VCardInfoN
    {
        public String V="";
        public String commenturl="";
        public String type="";
        public String loginName="";
        public String mobile="";
        public String username="";
        public String email="";
        public String imageurl="";
        public Object gender="";
        public String nickname="";
        public String webname="";
        public String mood = "";
        public List<Map<String,Object>> extentInfo;
    }
}
