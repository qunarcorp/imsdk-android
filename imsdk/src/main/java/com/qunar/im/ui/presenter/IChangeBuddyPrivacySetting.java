package com.qunar.im.ui.presenter;

import com.qunar.im.ui.presenter.impl.BuddyPrivacySettingPresenter;

/**
 * Created by xingchao.song on 12/1/2015.
 */
public interface IChangeBuddyPrivacySetting {

     void setMode(int type, BuddyPrivacySettingPresenter.VerifyQuestion question);

    void setResult(boolean isSuccess);


}
