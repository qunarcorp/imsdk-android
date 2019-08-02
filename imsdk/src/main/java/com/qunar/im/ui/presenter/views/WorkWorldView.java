package com.qunar.im.ui.presenter.views;

import com.qunar.im.base.module.WorkWorldDeleteResponse;
import com.qunar.im.base.module.WorkWorldItem;

import java.util.List;

public interface WorkWorldView {

    public void workworldshowNewData(List<WorkWorldItem> list);

    public void workworldshowMoreData(List<WorkWorldItem> list);

    public int workworldgetListCount();

    public void workworldstartRefresh();

    public WorkWorldItem workworldgetLastItem();

    public void workworldremoveWorkWorldItem(WorkWorldDeleteResponse worldDeleteResponse);

    public void workworldshowHeadView(int count);

    public void workworldhiddenHeadView();

    public String workworldgetSearchId();

    public void workworldcloseRefresh();

    boolean workworldisStartRefresh();

    void scrollTop();
}
