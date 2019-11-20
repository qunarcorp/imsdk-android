package com.qunar.im.base.jsonbean;

import java.util.List;

/**
 * Created by saber on 15-9-2.
 */
public class Profile4mUCenter extends BaseJsonResult {
    public List<UCenterProfile> data;

    static public class UCenterProfile
    {
        public String email = "";
        public String gender = "";
        public String imageurl = "";
        public String loginName = "";
        public String mobile = "";
        public String nickname = "";
        public String username = "";
        public String webname = "";
    }
}
