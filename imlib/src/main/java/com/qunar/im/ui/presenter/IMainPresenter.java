package com.qunar.im.ui.presenter;

/**
 * Created by xingchao.song on 3/15/2016.
 */
public interface IMainPresenter {
    void getUnreadConversationMessage();
    void showErrorMessage(String str);
    void refreshOPSUnRead(boolean isShow);
}
