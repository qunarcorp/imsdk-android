package com.qunar.im.base.jsonbean;

import java.util.List;

/**
 * Created by xinbo.wang on 2015/4/23.
 */
public class UserStatusResult extends BaseJsonResult {
    public List<UsersStatus> data;

    public static class UsersStatus implements BaseData
    {
        public String domain;
        public List<UserListItem> ul;
    }

    public static class UserListItem
    {
        public String u;
        public String o;
    }
}
