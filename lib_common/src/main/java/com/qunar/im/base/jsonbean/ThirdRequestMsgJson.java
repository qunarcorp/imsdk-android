package com.qunar.im.base.jsonbean;

/**
 * Created by saber on 16-1-27.
 */
public class ThirdRequestMsgJson extends BaseResult {
    //tv_date, tv_location,tv_time_stamp,tv_message_content,tv_action
    public String  headurl;
    public String detail;
    public String dealid;
    public String dealurl;
    public String source;
    public String timeout;
    public int status; // 0 未抢答,1 已抢答 2成功
}
