package com.qunar.im.ui.presenter.factory;

import com.qunar.im.ui.presenter.ILoginPresenter;
import com.qunar.im.ui.presenter.impl.LoginPresenter;
import com.qunar.im.ui.presenter.impl.QChatLoginPresenter;
import com.qunar.im.ui.presenter.impl.QTalkPublicLoginPresenter;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.protobuf.common.LoginType;

/**
 * Created by saber on 15-11-30.
 */
public class LoginFactory {
    public static ILoginPresenter createLoginPresenter() {
        if (CommonConfig.isQtalk) {
            if (LoginType.PasswordLogin.equals(QtalkNavicationService.getInstance().getLoginType())) {
                return new QTalkPublicLoginPresenter();
            } else {
                return new LoginPresenter();
            }
        } else {
            return new QChatLoginPresenter();
        }
    }
}
