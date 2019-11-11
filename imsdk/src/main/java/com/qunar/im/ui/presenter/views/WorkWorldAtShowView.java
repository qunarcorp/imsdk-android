package com.qunar.im.ui.presenter.views;

import com.qunar.im.base.module.MultiItemEntity;
import com.qunar.im.base.module.WorkWorldNoticeItem;

import java.util.List;

public interface WorkWorldAtShowView {
    public void showNewData(List<? extends  MultiItemEntity> list);

    public void showMoreData(List<? extends  MultiItemEntity> list);

    public int getShowCount();

    public WorkWorldNoticeItem getLastItem();

    public boolean isMindMessage();

    public void startRefresh();

    public void closeRefresh();

    public int getListCount();

    public void setEmptyText(String string);

    public void clearData();
}
