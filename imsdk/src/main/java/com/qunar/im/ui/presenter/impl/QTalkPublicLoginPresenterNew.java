package com.qunar.im.ui.presenter.impl;

/**
 * Created by froyomu on 2019-09-23
 * <p>
 * Describe:
 */
public class QTalkPublicLoginPresenterNew extends QTalkPublicLoginPresenter {

    @Override
    public void login() {
        String userName = loginView.getUserName();
        String password = loginView.getPassword();
        connectionUtil.pbLoginNew(userName, password);
    }
}
