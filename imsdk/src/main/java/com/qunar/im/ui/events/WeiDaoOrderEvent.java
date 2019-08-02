package com.qunar.im.ui.events;

/**
 * Created by xinbo.wang on 2016/5/20.
 */
public class WeiDaoOrderEvent {
    public String dealId;
    public String status;
    public WeiDaoOrderEvent(String dealId,String status)
    {
        this.dealId = dealId;
        this.status = status;
    }
}
