package com.qunar.im.ui.presenter.impl;

import android.text.TextUtils;

import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.module.Nick;
import com.qunar.im.ui.presenter.ILoginPresenter;
import com.qunar.im.ui.presenter.views.ILoginView;
import com.qunar.im.base.util.AccountSwitchUtils;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.enums.LoginStatus;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.utils.ConnectionUtil;

import de.greenrobot.event.EventBus;

import static com.qunar.im.base.util.Constants.Preferences.username;

/**
 * Created by saber on 16-1-26.
 */
public class QTalkPublicLoginPresenter implements ILoginPresenter, IMNotificaitonCenter.NotificationCenterDelegate {
    private static final String TAG = QTalkPublicLoginPresenter.class.getSimpleName();
    ILoginView loginView;
    protected ConnectionUtil connectionUtil;

    @Override
    public void setLoginView(ILoginView view) {
        loginView = view;
        connectionUtil = ConnectionUtil.getInstance();
        connectionUtil.addEvent(this, QtalkEvent.LOGIN_EVENT);
        connectionUtil.addEvent(this, QtalkEvent.LOGIN_FAILED);
        connectionUtil.addEvent(this, QtalkEvent.Connect_Interrupt);
        connectionUtil.addEvent(this, QtalkEvent.No_NetWork);
        connectionUtil.addEvent(this, QtalkEvent.Try_To_Connect);
        connectionUtil.addEvent(this, QtalkEvent.LOGIN_FAILED);
    }

    @Override
    public void release() {
        connectionUtil.removeEvent(this, QtalkEvent.LOGIN_EVENT);
        connectionUtil.removeEvent(this, QtalkEvent.LOGIN_FAILED);
        connectionUtil.removeEvent(this, QtalkEvent.Connect_Interrupt);
        connectionUtil.removeEvent(this, QtalkEvent.No_NetWork);
        connectionUtil.removeEvent(this, QtalkEvent.Try_To_Connect);
        connectionUtil.removeEvent(this, QtalkEvent.LOGIN_FAILED);
        loginView = null;
    }

    @Override
    public void login() {
        String userName = loginView.getUserName();
        String password = loginView.getPassword();
        connectionUtil.pbLogin(userName, password, true);
    }

    public void onEvent(EventBusEvent.LoginComplete loginComplete) {
        EventBus.getDefault().unregister(this);
        if (loginComplete.loginStatus) {
            if (!TextUtils.isEmpty(CommonConfig.verifyKey)) {
                DataUtils.getInstance(QunarIMApp.getContext()).putPreferences(username,
                        CurrentPreference.getInstance().getUserid());
                loginView.setLoginResult(true, 0);
                return;
            }
        }
        loginView.setLoginResult(false, 0);
        LogUtil.d("qtalk", "onEvent return false");
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

    /**
     * 设置自身头像
     */
    public void initUserInfo() {
        connectionUtil.getUserCard(loginView.getUserName(), new IMLogicManager.NickCallBack() {
            @Override
            public void onNickCallBack(Nick nick) {
                if(loginView!=null){
                    loginView.setHeaderImage(nick);
                }

            }
        }, false, false);
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
                if (args[0].equals(LoginStatus.Login)) {
                    AccountSwitchUtils.addAccount(com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid(), com.qunar.im.protobuf.common.CurrentPreference.getInstance().getToken(), getCurrentNavName(), getCurrentNavUrl());
                    loginView.setLoginResult(true, 0);
                } else if (args[0].equals(LoginStatus.Updating)) {
                    //// TODO: 2017/11/6 暂时没有逻辑
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

    //获取当前share存储的导航名称
    private String getCurrentNavName() {
        return DataUtils.getInstance(CommonConfig.globalContext).getPreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_NAME, AccountSwitchUtils.defalt_nav_name);
    }

    //获取当前share存储的导航地址
    private String getCurrentNavUrl() {
        return DataUtils.getInstance(CommonConfig.globalContext).getPreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_URL, QtalkNavicationService.getInstance().getNavicationUrl());
    }
}
