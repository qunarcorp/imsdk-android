package com.qunar.im.ui.presenter;

import com.qunar.im.ui.presenter.views.ISearchChatingView;

/**
 * Created by saber on 15-7-7.
 */
public interface ISearchChatingPresenter {
    void setSearchChatingView(ISearchChatingView view);
    void doSearchChating();
}
