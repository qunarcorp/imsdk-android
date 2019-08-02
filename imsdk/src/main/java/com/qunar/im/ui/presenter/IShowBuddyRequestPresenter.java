package com.qunar.im.ui.presenter;

import com.qunar.im.ui.presenter.views.IAnswerForResultView;

/**
 * Created by saber on 15-12-9.
 */
public interface IShowBuddyRequestPresenter {
    void initFriendRequestPropmt();
    void listBuddyRequests();
    void deleteBuddyRequests();
    void clearBuddyRequests();
    void setAnswerForResultView(IAnswerForResultView view);
}
