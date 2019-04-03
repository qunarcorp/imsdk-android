package com.qunar.im.ui.presenter.views;

import com.qunar.im.base.module.WorkWorldDeleteResponse;
import com.qunar.im.base.module.WorkWorldItem;

import java.util.List;

public interface WorkWorldView {

    public void showNewData(List<WorkWorldItem> list);

    public void showMoreData(List<WorkWorldItem> list);

    public int getListCount();

    public void startRefresh();

    public WorkWorldItem getLastItem();

    public void removeWorkWorldItem(WorkWorldDeleteResponse worldDeleteResponse);

    public void showHeadView(int count);

    public void hiddenHeadView();

    public String getSearchId();
}
