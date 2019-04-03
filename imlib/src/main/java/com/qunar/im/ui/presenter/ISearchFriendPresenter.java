package com.qunar.im.ui.presenter;

import com.qunar.im.ui.presenter.views.ISearchFriendView;

/**
 * Created by xinbo.wang on 2015/2/14.
 */
public interface ISearchFriendPresenter {
    void setSearchFriendView(ISearchFriendView view);
    /**
     *  查找所有联系人
     * */
    void doSearchContacts();
    /**
     *  查找群组
     * */
    void doSearchGroups();
    /**
     * */
    void doSearchPublishPlatform();

    void doSearchFriend();

}
