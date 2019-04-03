package com.qunar.im.ui.presenter;

import com.qunar.im.ui.presenter.views.ITransferRecentConvView;

/**
 * Created by saber on 15-9-21.
 */
public interface ITransferRecentCovnPresenter {
    void setView(ITransferRecentConvView view);
    void showRecentConvs();
}
