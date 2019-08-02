package com.qunar.im.ui.presenter;

import com.qunar.im.ui.presenter.views.WorkWorldNoticeView;

public interface WorkWorldNoticePresenter {
    void loadingHistory();
    void setView(WorkWorldNoticeView view);
    void startRefresh();

    void loadingMore();

}
