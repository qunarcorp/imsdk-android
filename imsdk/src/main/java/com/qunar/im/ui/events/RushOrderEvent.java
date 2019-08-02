package com.qunar.im.ui.events;

import com.qunar.im.base.module.IMMessage;

/**
 * Created by xinbo.wang on 2016/5/3.
 */
public class RushOrderEvent {
    public IMMessage message;
    public String dealId;
    public int timeout;
    public RushOrderEvent(String id,IMMessage msg,int timeout)
    {
        this.message = msg;
        this.dealId = id;
        this.timeout = timeout;
    }
}
