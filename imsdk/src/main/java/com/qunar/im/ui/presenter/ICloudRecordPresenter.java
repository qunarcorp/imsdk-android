package com.qunar.im.ui.presenter;

import com.qunar.im.ui.presenter.views.IChatView;

/**
 * Created by xinbo.wang on 2015/5/13.
 */
public interface ICloudRecordPresenter {
    void setView(IChatView view);

    void showMoreOldMsg(boolean isFromGroup);

    void showMoreOldMsgUp(boolean isFromGroup);
}
