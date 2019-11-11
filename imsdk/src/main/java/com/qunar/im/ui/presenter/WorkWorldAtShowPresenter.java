package com.qunar.im.ui.presenter;

import com.qunar.im.ui.presenter.views.WorkWorldAtShowView;
import com.qunar.im.ui.presenter.views.WorkWorldNoticeView;

public interface WorkWorldAtShowPresenter {
    void loadingHistory();
    void setView(WorkWorldAtShowView view);
    void startRefresh();

    void loadingMore();
    void startSearch(String str);

}
