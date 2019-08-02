package com.qunar.im.ui.presenter;

import com.qunar.im.ui.presenter.views.IFindBuddyView;

/**
 * Created by saber on 15-12-17.
 */
public interface IFindBuddyPresenter {
    void setIFindBuddyView(IFindBuddyView view);
    void doSearch();
}
