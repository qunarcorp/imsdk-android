package com.qunar.im.ui.presenter.views;

import android.content.Context;

/**
 * Created by xingchao.song on 3/15/2016.
 */
public interface IMainView {
    void setUnreadConversationMessage(int unreadNumbers);
    void loginSuccess();
    void synchronousing();
    void refreshShortcutBadger(int count);
    Context getContext();
    void showDialog(String str);
    void refresh();
    void refreshOPSUnRead(boolean isShow);
    void refreshNoticeRed(boolean isShow, int count);
    void startOPS();
    void showProgress(String str);
    void dimissProgress();

    boolean getWorkWorldPermissions();

    void restartApplication();
    void showFeedBackProgressView(String[] args,boolean isShowNotify,boolean uploadDb);
    void startLoginView();
}
