package com.qunar.im.ui.presenter.views;

import java.util.List;

/**
 * Created by zhaokai on 16-2-19.
 */
public interface IShareLocationView {
    /**
     * 获取共享位置成员
     * */
    List<String> getMembers();
    /**
     * 获取共享Id
     * */
    String getShareId();

    /**
     * 获取发起人Id
     * */
    String getFromId();
}