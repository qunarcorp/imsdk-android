package com.qunar.im.ui.presenter;

import com.qunar.im.ui.presenter.views.ILocalChatRecordView;

/**
 * Created by saber on 15-7-7.
 */
public interface ILocalChatRecordPresenter {
    void setLocalChatRecordView(ILocalChatRecordView view);
    void loadOldderMsg();
    void loadNewerMsg();

}
