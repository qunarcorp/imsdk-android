package com.qunar.im.ui.presenter;

import com.qunar.im.ui.presenter.views.IFavourityMsgView;

/**
 * Created by saber on 16-1-25.
 */
public interface IFavourityMessagePresenter {
    void setFavourity(IFavourityMsgView view);
    void deleteFavourity();
    void addFavourity();
    void getAllFavourity();
}
