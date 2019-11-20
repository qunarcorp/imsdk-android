package com.qunar.im.base.module;

import java.io.Serializable;

/**
 * Created by xinbo.wang on 2015/5/15.
 */
public class UserVCard  extends BaseModel implements Serializable {
    public static final String WECHAT_TYPE = "wechat";
    public String id;
    public String desc="";
    public String commentUrl="";
    public String telphone="";
    public String gravantarUrl="";
    public int gravantarVersion;
    public String email="";
    public String gender="";
    public String type="";//merchant代表客服
    public String nickname="";
    public String extension="";
}
