package com.qunar.im.ui.presenter;

import com.qunar.im.base.module.WorkWorldNewCommentBean;
import com.qunar.im.ui.presenter.views.WorkWorldDetailsView;

public interface WorkWorldDetailsPresenter {
    public void sendComment();
    public void setView(WorkWorldDetailsView view);
    public void loadingHistory();
    public void startRefresh();

    void loadingMore(boolean localMore);

    void deleteWorkWorldCommentItem(WorkWorldNewCommentBean item);
}
