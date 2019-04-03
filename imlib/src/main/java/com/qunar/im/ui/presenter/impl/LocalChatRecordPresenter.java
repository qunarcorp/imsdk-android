package com.qunar.im.ui.presenter.impl;

import com.qunar.im.ui.presenter.ILocalChatRecordPresenter;
import com.qunar.im.ui.presenter.views.ILocalChatRecordView;
import com.qunar.im.utils.ConnectionUtil;

/**
 * Created by saber on 15-7-7.
 */
//云端记录实现
public class LocalChatRecordPresenter implements ILocalChatRecordPresenter {
    ILocalChatRecordView chatRecordView;
    ConnectionUtil connectionUtil;
    int numPerPage = 20;
    long highTime;
    long lowTime;
    @Override
    public void setLocalChatRecordView(ILocalChatRecordView view) {
        chatRecordView = view;
        connectionUtil = ConnectionUtil.getInstance();
    }

    @Override
    public void loadOldderMsg() {
        if(highTime == 0)
        {
            highTime = chatRecordView.getCurrentMsgRecTime()+10000;
        }
    }

    @Override
    public void loadNewerMsg() {
        if(lowTime == 0)
        {
            lowTime = chatRecordView.getCurrentMsgRecTime();
        }
    }

}
