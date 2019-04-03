package com.qunar.im.ui.presenter;

import com.qunar.im.ui.presenter.views.IBuddyView;

/**
 * Created by saber on 15-12-4.
 */
public interface IBuddyPresenter {

    void setBuddyView(IBuddyView view);
    /**
     * 添加好友
     */
    void addFriend();
    /**
     * 删除好友
     */
    void deleteBuddy();


    void registerRoster();

    /**
     * 发送添加好友请求的验证
     */
    void sendAddBuddyRequest() ;
}
