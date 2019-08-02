package com.qunar.im.ui.presenter;

import android.content.Context;

/**
 * Created by xinbo.wang on 2015/2/2.
 */
public interface ISystemPresenter {
    void loadPreference(Context context, boolean isWrite);
    void changeProcess2Failed();
    void checkSendingLine();
    boolean checkUnique();
    void getMyCapability();
    void getMyConfig();
    void checkTemplate();
}
