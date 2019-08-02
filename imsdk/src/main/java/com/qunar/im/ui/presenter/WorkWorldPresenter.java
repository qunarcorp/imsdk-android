package com.qunar.im.ui.presenter;

import com.qunar.im.base.module.WorkWorldItem;

public interface WorkWorldPresenter {

    public void workworldloadingHistory();

    public void workworldloadingNoticeCount();

    public void workworldloadingMore();

    public void workworldstartRefresh();

    public void workworlddeleteWorkWorldItem(WorkWorldItem item);

    public void workworldremoveEvent();
}
