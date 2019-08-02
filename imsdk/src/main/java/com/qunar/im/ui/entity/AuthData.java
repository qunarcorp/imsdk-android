package com.qunar.im.ui.entity;

import com.qunar.im.base.module.BaseModel;

/**
 * 二维码扫描登录验证数据
 * Created by hubo.hu on 2017/10/26.
 */

public class AuthData extends BaseModel {

    public String v; //版本号
    public String t;//认证状态  固定1
    public String u;//用户名
    public String a;//avater 头像url
    public Data d;//验证信息
    public String p;//平台
    //qtalk
    public String ckey;//预留字段

    public  static class Data{
        public String q;
        public String v;
        public String t;

        public String q_ckey;
    }

    @Override
    public String toString() {
        return "AuthData  u = " + u + "  a = " + a + " d = " + d;
    }
}
