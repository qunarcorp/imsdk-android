package com.qunar.im.ui.presenter;

import com.qunar.im.ui.presenter.views.IProfileView;

/**
 * Created by xinbo.wang on 2015/2/2.
 * update by huayu.chen on 2016/5/20.
 */
public interface IProfilePresenter {
    void setProfileView(IProfileView view);

    void changeMsgSoundState();

    void changeMsgShockState();

    void changePushState();

    void changeShakeEvent();

    void changeLandscapeState();

    void changeFontSize();

    void changePushShowContent();
    void changeOfflinePush();
}
