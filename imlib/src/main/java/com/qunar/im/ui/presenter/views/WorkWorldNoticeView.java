package com.qunar.im.ui.presenter.views;

import com.qunar.im.base.module.MultiItemEntity;

import java.util.List;

public interface WorkWorldNoticeView {
    public void showNewData(List<? extends  MultiItemEntity> list);

    public int getShowCount();
}
