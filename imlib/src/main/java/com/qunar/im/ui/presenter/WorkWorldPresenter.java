package com.qunar.im.ui.presenter;

import com.qunar.im.base.module.WorkWorldItem;

public interface WorkWorldPresenter {

    public void loadingHistory();

    public void loadingNoticeCount();

    public void loadingMore();

    public void startRefresh();

    public void deleteWorkWorldItem(WorkWorldItem item);

    public void removeEvent();
}
