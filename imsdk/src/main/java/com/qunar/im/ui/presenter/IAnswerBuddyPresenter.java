package com.qunar.im.ui.presenter;

import com.qunar.im.ui.presenter.views.IAnswerBuddyRequestView;

/**
 * Created by saber on 15-12-9.
 */
public interface IAnswerBuddyPresenter {
    void setAnswerView(IAnswerBuddyRequestView view);
    void answerForRequest();
}
