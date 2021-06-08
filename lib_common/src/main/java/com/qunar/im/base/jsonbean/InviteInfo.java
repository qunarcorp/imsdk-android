package com.qunar.im.base.jsonbean;

import java.util.List;

/**
 * Created by saber on 16-1-7.
 */
public class InviteInfo extends BaseJsonResult {
    public List<FriendsInvite> data;
    public static class FriendsInvite
    {
        public String I;
        public String B;
        public String T;
    }
}
