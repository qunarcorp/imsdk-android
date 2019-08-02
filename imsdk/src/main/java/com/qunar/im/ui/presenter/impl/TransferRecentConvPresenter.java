package com.qunar.im.ui.presenter.impl;

import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.module.RecentConversation;
import com.qunar.im.ui.presenter.ITransferRecentCovnPresenter;
import com.qunar.im.ui.presenter.views.ITransferRecentConvView;

import java.util.List;

/**
 * Created by saber on 15-9-21.
 */
public class TransferRecentConvPresenter implements ITransferRecentCovnPresenter {
    ITransferRecentConvView transferRecentConvView;
    @Override
    public void setView(ITransferRecentConvView view) {
        transferRecentConvView = view;
    }

    @Override
    public void showRecentConvs() {
        if(transferRecentConvView!=null)
        {
            List<RecentConversation> list = ConnectionUtil.getInstance().SelectConversationList(false);
            transferRecentConvView.setRecentConvList(list);
        }
    }
}
