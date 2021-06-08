package com.qunar.im.base.jsonbean;

import java.util.List;

/**
 * Created by xinbo.wang on 2015/5/26.
 */
public class GetVCardData extends BaseResult {
    public List<UserVCardInfo> users;
    public String domain;
    public static class UserVCardInfo
    {
        public String user;
        public String version;
    }
}
