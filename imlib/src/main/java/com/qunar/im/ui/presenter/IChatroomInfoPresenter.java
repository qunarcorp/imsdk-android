package com.qunar.im.ui.presenter;

import com.qunar.im.ui.presenter.views.IChatRoomInfoView;

/**
 * Created by xinbo.wang on 2015/2/11.
 */
public interface IChatroomInfoPresenter {
    void setView(IChatRoomInfoView view);

    void showMembers(boolean onlyFromDB);

    void showSingler();

    void leave();

    void showInfo();

    void joinChatRoom();

    void joinChatRoomWithPwd(String pwd);

    void destroy();

    void cleanGroup();

    void clearHistoryMsg();

    void updataMucInfo();

    void close();
}
