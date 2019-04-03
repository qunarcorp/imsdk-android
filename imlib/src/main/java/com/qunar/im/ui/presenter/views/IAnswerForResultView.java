package com.qunar.im.ui.presenter.views;

import com.qunar.im.base.module.BuddyRequest;

import java.util.List;

/**
 * Created by zhaokai on 15-12-9.
 */
public interface IAnswerForResultView {
    void doAnswerForResult(BuddyRequest request);
    void setRequestsList(List<BuddyRequest> list);
    BuddyRequest getBuddyRequest();
}
