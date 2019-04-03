package com.qunar.im.ui.presenter.views;

import android.content.Context;

/**
 * Created by xinbo.wang on 2015/2/2.
 */
public interface IProfileView {
    boolean getMsgSoundState();
    boolean getMsgShockState();
    boolean getPushMsgState();
    boolean getPushShowContent();
    boolean getOfflinePush();
    boolean getShakeEvent();
    boolean getLandscape();
    Context getContext();
}
