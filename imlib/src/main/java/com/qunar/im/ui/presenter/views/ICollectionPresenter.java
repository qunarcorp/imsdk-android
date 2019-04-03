package com.qunar.im.ui.presenter.views;


/**
 * Created by hubin on 2017/11/21.
 */

public interface ICollectionPresenter {
    //传入view
    void setView(ICollectionView view);

    //删除注册事件
    void removeEvent();
    //添加注册事件
    void addEvent();
    /**
     * 加载初始化时的历史记录
     */
    void propose();
    //加载历史记录
    void reloadMessages();

    void getBindUser();
}
