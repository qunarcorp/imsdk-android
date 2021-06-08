package com.qunar.im.base.jsonbean;

import com.qunar.im.base.util.Constants;

/**
 * Created by xingchao.song on 9/14/2015.
 */
public class GetGroupOfflineMessageRequest {
    public String muc_name;
    public String timestamp;
    public String limitnum;
    public String direction;
    public String domain = Constants.Config.PUB_NET_XMPP_Domain;
    public String type="0";
    public String u;
    public String k;
}
