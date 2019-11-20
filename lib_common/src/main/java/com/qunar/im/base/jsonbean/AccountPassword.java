package com.qunar.im.base.jsonbean;

import com.qunar.im.core.services.QtalkNavicationService;

/**
 * Created by lihaibin.li on 2017/9/7.
 */

public class AccountPassword {
    public String userid;//用户名
    public String password;//密码
    public String navname;//导航名称
    public String navurl = QtalkNavicationService.getInstance().getNavicationUrl();//导航

    @Override
    public String toString() {
        return "AccountPassword{" +
                "userid='" + userid + '\'' +
                ", password='" + password + '\'' +
                ", navname='" + navname + '\'' +
                ", navurl='" + navurl + '\'' +
                '}';
    }
}
