package com.qunar.im.ui.presenter;

import com.qunar.im.ui.presenter.views.ILoginView;

/**
 * Created by xinbo.wang on 2015/2/2.
 */
public interface ILoginPresenter {
    //设置登录界面
    void setLoginView(ILoginView view);
    //登陆逻辑
    void login();
    //登出逻辑
    void logout();

    void autoLogin();

    void release();
}
