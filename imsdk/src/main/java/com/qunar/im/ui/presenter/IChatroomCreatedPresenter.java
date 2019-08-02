package com.qunar.im.ui.presenter;

import com.qunar.im.ui.presenter.views.IChatroomCreatedView;

/**
 * Created by xinbo.wang on 2015/4/10.
 */
public interface IChatroomCreatedPresenter {
    public void setView(IChatroomCreatedView view);
    public void createChatroom();
}
