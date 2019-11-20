package com.qunar.im.base.jsonbean;

import java.util.List;

/**
 * Created by saber on 16-2-3.
 */
public class SearchUserResult extends BaseJsonResult {
    public List<SearchUserInfo> data;
    static public class SearchUserInfo
    {
        public String domain;
        public String nickname;
        public String username;
        public boolean isFriends = false;
    }
}
