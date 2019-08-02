package com.qunar.im.ui.presenter;

import com.qunar.im.ui.presenter.views.IInvitedFriendsView;

/**
 * Created by xinbo.wang on 2015/3/5.
 */
public interface IInvitedFriendsPresenter {
    void setInvitedFriendsView(IInvitedFriendsView view);

    /**
     * 加载的用户供搜索用
     */
    void loadAllContacts();
    void invited();

    /**
     * 加载的用户用建群的时候选择用
     */
    void loadTargetContacts();
    void release();
}
