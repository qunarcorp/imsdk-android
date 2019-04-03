package com.qunar.im.ui.presenter.views;

import android.content.Context;

/**
 * Created by xingchao.song on 12/9/2015.
 */
public interface IAnswerBuddyRequestView {
    /**
     * @return true表示同意 false表示拒绝该好友
     */
    boolean getFriendRequstResult();

    /**
     * 获取拒绝或者接受该好友的理由
     * @return 理由
     */
    String getResean();

    String getJid();

    void setStatus(boolean status);
    Context getContext();
}
