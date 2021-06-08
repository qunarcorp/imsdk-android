package com.qunar.im.ui.presenter.impl;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.common.CurrentPreference;
import com.qunar.im.ui.presenter.ILoginPresenter;
import com.qunar.im.ui.presenter.views.ILoginView;
import com.qunar.im.base.util.AccountSwitchUtils;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.enums.LoginStatus;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.utils.ConnectionUtil;

import de.greenrobot.event.EventBus;

/**
 * Created by xinbo.wang on 2015/2/26.
 */
//登录实现
public class LoginPresenter implements ILoginPresenter, IMNotificaitonCenter.NotificationCenterDelegate {
    private static final String TAG = LoginPresenter.class.getSimpleName();

    ILoginView loginView;
    //连接相关管理
    private ConnectionUtil connectionUtil;

    @Override
    public void setLoginView(ILoginView view) {
        loginView = view;
        connectionUtil = ConnectionUtil.getInstance();
        connectionUtil.addEvent(this, QtalkEvent.LOGIN_EVENT);
        connectionUtil.addEvent(this, QtalkEvent.LOGIN_FAILED);
        connectionUtil.addEvent(this, QtalkEvent.Connect_Interrupt);
        connectionUtil.addEvent(this, QtalkEvent.No_NetWork);
        connectionUtil.addEvent(this, QtalkEvent.Try_To_Connect);
    }

    @Override
    public void release() {
        connectionUtil.removeEvent(this, QtalkEvent.LOGIN_EVENT);
        connectionUtil.removeEvent(this, QtalkEvent.LOGIN_FAILED);
        connectionUtil.removeEvent(this, QtalkEvent.Connect_Interrupt);
        connectionUtil.removeEvent(this, QtalkEvent.No_NetWork);
        connectionUtil.removeEvent(this, QtalkEvent.Try_To_Connect);
    }

    @Override
    public void login() {
        String rtxId = loginView.getUserName();
        String code = loginView.getPassword();
        if (TextUtils.isEmpty(code) || TextUtils.isEmpty(rtxId)) {
            loginView.setLoginResult(false, 0);
            return;
        }
        rtxId = rtxId.replaceAll("\\s", "").toLowerCase();
        //保存帐号

        CurrentPreference.getInstance().setUserid(rtxId);
        connectionUtil.pbLogin(rtxId, code, false);
    }

    @Override
    public void logout() {

        //PB退出登录
        if (CommonConfig.isPbProtocol) {
            ConnectionUtil.getInstance().pbLogout();
        } else {
        }

    }

    @Override
    public void autoLogin() {
        connectionUtil.pbAutoLogin();
    }


    @Override
    public void didReceivedNotification(String key, Object... args) {
        switch (key) {
            case QtalkEvent.LOGIN_FAILED:
                if (loginView != null) {


                    loginView.LoginFailure((int) args[0]);
                }

                break;
            case QtalkEvent.LOGIN_EVENT:
                Logger.i(TAG, "登录成功  " + args[0]);
                if (args[0].equals(LoginStatus.Login)) {
//                    CurrentPreference.getInstance().saveConfig();
//                    CurrentPreference.getInstance().unlockWrite();
//                    DataUtils.getInstance(QunarIMApp.getContext()).putPreferences(Constants.Preferences.username,
//                            CurrentPreference.getInstance().getUserId());
//                    getVirtualUser();
//                    initUserInfo();
                    AccountSwitchUtils.addAccount(CurrentPreference.getInstance().getUserid(), CurrentPreference.getInstance().getToken(), getCurrentNavName(), getCurrentNavUrl());
                    loginView.setLoginResult(true, 0);
                    loginView.getVirtualUserRole(true);
                } else if (args[0].equals(LoginStatus.Updating)) {
                    //// TODO: 2017/11/6 暂时没有更新 
                } else {
                    loginView.setLoginResult(false, 0);

                }

                break;
            //断线重连
            case QtalkEvent.Connect_Interrupt:
                loginView.connectInterrupt();
                break;
            case QtalkEvent.No_NetWork:
                loginView.noNetWork();
                break;
            case QtalkEvent.Try_To_Connect:
                loginView.tryToConnect(String.valueOf(args[0]));
                break;
        }
    }

    public void onEvent(EventBusEvent.LoginComplete loginComplete) {
        EventBus.getDefault().unregister(this);
        if (loginComplete.loginStatus) {
            if (!TextUtils.isEmpty(CommonConfig.verifyKey)) {
                DataUtils.getInstance(QunarIMApp.getContext()).putPreferences(Constants.Preferences.username,
                        CurrentPreference.getInstance().getUserid());
                loginView.setLoginResult(true, 0);
                return;
            }
        }
        loginView.setLoginResult(false, 0);
    }

    //获取当前share存储的导航名称
    private String getCurrentNavName() {
        return DataUtils.getInstance(CommonConfig.globalContext).getPreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_NAME, AccountSwitchUtils.defalt_nav_name);
    }

    //获取当前share存储的导航地址
    private String getCurrentNavUrl() {
        return DataUtils.getInstance(CommonConfig.globalContext).getPreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_URL, QtalkNavicationService.getInstance().getNavicationUrl());
    }
}
