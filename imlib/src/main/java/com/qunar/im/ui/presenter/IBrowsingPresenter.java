package com.qunar.im.ui.presenter;

import com.qunar.im.ui.presenter.views.IBrowsingConversationImageView;

/**
 * Created by saber on 16-1-29.
 */
public interface IBrowsingPresenter {
    void setBrosingView(IBrowsingConversationImageView view);
    void loadImgsOfCurrentConversation();
}
