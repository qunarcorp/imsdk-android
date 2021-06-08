package com.qunar.im.base.module;

import java.io.Serializable;

/**
 * Created by xinbo.wang on 2016/7/22.
 */
public class RobotConversation extends BaseModel implements Serializable {
    public String id;
    public String cnName;
    public String gravatar;
    public String lastMsg="";
    public long lastMsgTime;
    public int unread_msg_cont;
    public int msgType=1;
}
