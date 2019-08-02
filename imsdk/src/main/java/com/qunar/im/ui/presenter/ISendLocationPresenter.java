package com.qunar.im.ui.presenter;

import com.qunar.im.ui.presenter.views.IChatView;

/**
 * Created by zhaokai on 16-2-22.
 */
public interface ISendLocationPresenter {
    void sendShareLocationMessage(String shareId);
    void setView(IChatView view);
}