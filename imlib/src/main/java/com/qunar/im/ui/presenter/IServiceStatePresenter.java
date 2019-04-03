package com.qunar.im.ui.presenter;

import com.qunar.im.ui.presenter.views.IServiceStateView;

/**
 * Created by huayu.chen on 2016/7/19.
 */
public interface IServiceStatePresenter {
    void setServiceStateView(IServiceStateView view);
    void setServiceState();
    void getServiceState();

}
