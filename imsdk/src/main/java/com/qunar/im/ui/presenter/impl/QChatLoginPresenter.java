package com.qunar.im.ui.presenter.impl;

import android.text.TextUtils;

import com.qunar.im.base.common.QChatRSA;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.jsonbean.GeneralJson;
import com.qunar.im.base.jsonbean.QChatLoginResult;
import com.qunar.im.base.jsonbean.QVTResponseResult;
import com.qunar.im.base.module.Nick;
import com.qunar.im.common.CurrentPreference;
import com.qunar.im.ui.presenter.ILoginPresenter;
import com.qunar.im.ui.presenter.views.ILoginView;
import com.qunar.im.base.protocol.LoginAPI;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.util.BusinessUtils;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.base.util.IMUserDefaults;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.ListUtil;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.PhoneInfoUtils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.enums.LoginStatus;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.utils.ConnectionUtil;

import de.greenrobot.event.EventBus;

import static com.qunar.im.base.util.Constants.Preferences.username;
import static com.qunar.im.base.util.IMUserDefaults.getStandardUserDefaults;

/**
 * Created by saber on 16-1-26.
 */
public class QChatLoginPresenter implements ILoginPresenter, IMNotificaitonCenter.NotificationCenterDelegate {
    private static final String TAG = QChatLoginPresenter.class.getSimpleName();
    ILoginView loginView;
    private ConnectionUtil connectionUtil;
    private String plat = "app";

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
        String qvt = CurrentPreference.getInstance().getQvt();
        if (!TextUtils.isEmpty(qvt)) {
            loginByToken(plat);
            return;
        }
        String userName = loginView.getUserName();
        String password = loginView.getPassword();
        String type = "username";
        if (BusinessUtils.checkPhoneNumber(userName)) {
            type = "mobile";
        } else if (BusinessUtils.checkEmail(userName)) {
            type = "email";
        }
        try {
            password = QChatRSA.QunarRSAEncrypt(password);
        } catch (Exception e) {
            LogUtil.e(TAG, "error", e);
            loginView.setLoginResult(false, 0);
            return;
        }
        LoginAPI.QChatLogin(userName, password, loginView.getPrenum(), type, new ProtocolCallback.UnitCallback<QChatLoginResult>() {
            @Override
            public void onCompleted(final QChatLoginResult qChatLoginResult) {
                if (qChatLoginResult.ret && !ListUtil.isEmpty(qChatLoginResult.data)) {
                    QChatLoginResult.QchatUserInfo qchatUesr = qChatLoginResult.data.get(0);
                    QVTResponseResult qvtResponseResult = new QVTResponseResult();
                    qvtResponseResult.data = new QVTResponseResult.QVT();
                    qvtResponseResult.ret = true;
                    qvtResponseResult.data.tcookie = qchatUesr.tcookie;
                    qvtResponseResult.data.vcookie = qchatUesr.vcookie;
                    qvtResponseResult.data.qcookie = qchatUesr.qcookie;
                    CurrentPreference.getInstance().setQvt(JsonUtils.getGson().toJson(qvtResponseResult));
                    DataUtils.getInstance(CommonConfig.globalContext).putPreferences(Constants.Preferences.qchat_qvt, JsonUtils.getGson().toJson(qvtResponseResult));
                    CurrentPreference.getInstance().setMerchants(qchatUesr.type.equals("merchant"));
                    loginByToken(plat);
                } else {
                    int errcode = 100;
                    if (qChatLoginResult.errcode instanceof Integer)
                        errcode = (int) qChatLoginResult.errcode;
                    loginView.setLoginResult(false, errcode);
                    LogUtil.d("qchat", "ret is false or data is null");
                }
            }

            @Override
            public void onFailure(String errMsg) {
                LogUtil.d("qchat", "login failare");
                loginView.setLoginResult(false, 0);
            }
        });
    }

    public void loginByToken(final String plat) {
        LoginAPI.getQchatToken(PhoneInfoUtils.getUniqueID(), CurrentPreference.getInstance().getQvt(),plat, new ProtocolCallback.UnitCallback<GeneralJson>() {
            @Override
            public void onCompleted(GeneralJson generalJson) {
                if (generalJson.ret && generalJson.data != null) {
                    String username = generalJson.data.get("username");
                    String token = generalJson.data.get("token");
                    if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(token)) {
                        CurrentPreference.getInstance().setUserid(username);
                        String password = "{\"token\":{\"plat\":\""+plat+"\", \"macCode\":\""
                                + PhoneInfoUtils.getUniqueID() + "\", \"token\":\""
                                + token + "\"}}";
                        getStandardUserDefaults().newEditor(CommonConfig.globalContext).putObject(Constants.Preferences.lastuserid, username).synchronize();
                        getStandardUserDefaults().newEditor(CommonConfig.globalContext).putObject(Constants.Preferences.usertoken, password).synchronize();
                        if (!EventBus.getDefault().isRegistered(QChatLoginPresenter.this)) {
                            EventBus.getDefault().register(QChatLoginPresenter.this);
                        }
                        autoLogin();
                    } else {
                        if(loginView != null)
                            loginView.setLoginResult(false, 100);
                    }
                } else {
                    if(loginView != null)
                        loginView.setLoginResult(false, 100);
                }
            }

            @Override
            public void onFailure(String errMsg) {
                if(loginView != null)
                    loginView.setLoginResult(false, 100);
            }
        });
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
        LogUtil.d("qchat", "onEvent return false");
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
        LogUtil.d("qchat", "autoLogin");
        CurrentPreference.getInstance().setQvt(DataUtils.getInstance(CommonConfig.globalContext).getPreferences(Constants.Preferences.qchat_qvt, ""));
        final String userName = IMUserDefaults.getStandardUserDefaults().getStringValue(CommonConfig.globalContext, Constants.Preferences.lastuserid);
        final String password = IMUserDefaults.getStandardUserDefaults().getStringValue(CommonConfig.globalContext, Constants.Preferences.usertoken);
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            if (loginView != null) {
                LogUtil.d("qchat", "username or password is null!");
                loginView.setLoginResult(false, 100);
            }

        } else {
            ConnectionUtil.setInitialized(false);
            connectionUtil.pbAutoLogin();
        }
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
//                    initUserInfo();
                    loginView.setLoginResult(true, 0);
                    loginView.getVirtualUserRole(true);
                } else if(args[0].equals(LoginStatus.Updating)) {
                    //// TODO: 2017/11/6 暂时没有逻辑
                }else {
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

    public void initUserInfo() {
        connectionUtil.getUserCard(loginView.getUserName(), new IMLogicManager.NickCallBack() {
            @Override
            public void onNickCallBack(Nick nick) {
                if (loginView != null) {
                    loginView.setHeaderImage(nick);
                }

            }
        }, false, false);
    }
}
