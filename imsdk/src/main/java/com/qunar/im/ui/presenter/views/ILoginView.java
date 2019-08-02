package com.qunar.im.ui.presenter.views;

import android.content.Context;

import com.qunar.im.base.module.Nick;

/**
 * Created by xinbo.wang on 2015/1/30.
 */
public interface ILoginView {
    //登陆用户名
    String getUserName();
    //登陆密码
    String getPassword();
    //登陆结果
    void setLoginResult(boolean success, int errcode);

    String getPrenum();
    //获取Context对象
    Context getContext();
    //获取虚拟用户成功时操作
    void getVirtualUserRole(boolean b);
    void setHeaderImage(Nick nick);
    //登陆失效
    void LoginFailure(int errStr);
    //连接中断
    void connectInterrupt();
    //没有网络连接
    void noNetWork();
    //尝试连接
    void tryToConnect(String str);
    //是否是切换账户啊
    boolean isSwitchAccount();
}
