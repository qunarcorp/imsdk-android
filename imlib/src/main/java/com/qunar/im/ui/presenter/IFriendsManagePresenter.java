package com.qunar.im.ui.presenter;


import com.qunar.im.ui.presenter.views.IFriendsManageView;

/**
 * Created by xinbo.wang on 2015/2/2.
 */
public interface IFriendsManagePresenter {
    /**
     * 给presenter一个关联的view
     * @param view
     */
    void setFriendsView(IFriendsManageView view);

    /**
     * 从数据库加载好友数据
     */
    void updateContacts();

    /**
     * 从网络加载数据
     */
    void forceUpdateContacts();
}
