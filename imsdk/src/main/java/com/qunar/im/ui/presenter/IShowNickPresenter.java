package com.qunar.im.ui.presenter;

import com.qunar.im.ui.presenter.views.IShowNickView;

/**
 * Created by saber on 16-1-25.
 */
public interface IShowNickPresenter {
    void checkShowNick();
    void setShowNickView(IShowNickView view);
}
