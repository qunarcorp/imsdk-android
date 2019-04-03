package com.qunar.im.ui.presenter.impl;

import com.qunar.im.base.module.WorkWorldNewCommentBean;
import com.qunar.im.base.module.WorkWorldNoticeHistoryResponse;
import com.qunar.im.base.module.WorkWorldNoticeItem;
import com.qunar.im.ui.presenter.WorkWorldNoticePresenter;
import com.qunar.im.ui.presenter.views.WorkWorldNoticeView;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.HttpUtil;

import java.util.List;

public class WorkWorldNoticeManagerPresenter implements WorkWorldNoticePresenter{

    private  WorkWorldNoticeView workWorldNoticeView;


    @Override
    public void loadingHistory() {
        List<WorkWorldNoticeItem> list = ConnectionUtil.getInstance().selectHistoryWorkWorldNotice(workWorldNoticeView.getShowCount(),0);
        workWorldNoticeView.showNewData(list);

        if(list.size()>0){
            WorkWorldNoticeItem item = list.get(0);
            HttpUtil.setWorkWorldNoticeReadTime(item.getCreateTime(), new ProtocolCallback.UnitCallback<WorkWorldNoticeHistoryResponse>() {
                @Override
                public void onCompleted(WorkWorldNoticeHistoryResponse workWorldNoticeHistoryResponse) {

                }

                @Override
                public void onFailure(String errMsg) {

                }
            });
        }
    }

    @Override
    public void setView(WorkWorldNoticeView view) {
        workWorldNoticeView = view;
    }
}
