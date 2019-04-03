package com.qunar.im.ui.presenter;

import com.qunar.im.ui.presenter.views.ICheckFriendsView;

/**
 * Created by saber on 15-12-8.
 */
public interface ICheckFriendPresenter {
    void setICheckFriendsView(ICheckFriendsView view);
    void checkFriend();
}
