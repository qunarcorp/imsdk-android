package com.qunar.im.ui.presenter.impl;

import com.qunar.im.base.module.WorkWorldAtShowItem;
import com.qunar.im.base.module.WorkWorldAtShowResponse;
import com.qunar.im.base.module.WorkWorldMyReply;
import com.qunar.im.base.module.WorkWorldNoticeItem;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.util.Constants;
import com.qunar.im.ui.presenter.WorkWorldAtShowPresenter;
import com.qunar.im.ui.presenter.views.WorkWorldAtShowView;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.HttpUtil;

import java.util.ArrayList;
import java.util.List;

public class WorkWorldAtShowManagerPresenter implements WorkWorldAtShowPresenter {

    private WorkWorldAtShowView workWorldAtShowView;
    private int size = 20;
    private int limit = 0;

    @Override
    public void loadingHistory() {
        startRefresh();
    }

    @Override
    public void setView(WorkWorldAtShowView view) {
        workWorldAtShowView = view;
    }

    @Override
    public void startRefresh() {
        List<String> typeList = new ArrayList<>();
        typeList.add(Constants.WorkWorldState.COMMENTATMESSAGE);
        typeList.add(Constants.WorkWorldState.WORKWORLDATMESSAGE);
        List<WorkWorldAtShowItem> his = (List<WorkWorldAtShowItem>) ConnectionUtil.getInstance().selectHistoryWorkWorldNoticeByEventType(size,limit,typeList, true);

        workWorldAtShowView.showNewData(his);
        workWorldAtShowView.startRefresh();


        HttpUtil.refreshWorkWorldAtMe(size,  0, new ProtocolCallback.UnitCallback<WorkWorldAtShowResponse>() {
            @Override
            public void onCompleted(WorkWorldAtShowResponse workWorldResponse) {

                workWorldAtShowView.showNewData((List< WorkWorldAtShowItem>)workWorldResponse.getData().getNewAtList());
            }

            @Override
            public void onFailure(String errMsg) {
                workWorldAtShowView.closeRefresh();
            }
        });
    }

    @Override
    public void loadingMore() {

        WorkWorldNoticeItem item = workWorldAtShowView.getLastItem();
        if (item == null) {
            return;
        }

        HttpUtil.refreshWorkWorldAtMe(size,  Long.parseLong(item.getCreateTime()), new ProtocolCallback.UnitCallback<WorkWorldAtShowResponse>() {
            @Override
            public void onCompleted(WorkWorldAtShowResponse workWorldResponse) {
                workWorldAtShowView.showMoreData(workWorldResponse.getData().getNewAtList());
            }

            @Override
            public void onFailure(String errMsg) {
                List<WorkWorldNoticeItem> list = null;
                List<String> typeList = new ArrayList<>();
                typeList.add(Constants.WorkWorldState.COMMENTATMESSAGE);
                typeList.add(Constants.WorkWorldState.WORKWORLDATMESSAGE);

                list = (List<WorkWorldNoticeItem>) ConnectionUtil.getInstance().selectHistoryWorkWorldNoticeByEventType(size, workWorldAtShowView.getListCount(),typeList, true);
//                } else {
//                    list = ConnectionUtil.getInstance().selectHistoryWorkWorldItem(size, mView.workworldgetListCount());
//                }


                workWorldAtShowView.showMoreData(list);
            }
        });
    }

    @Override
    public void startSearch(String str) {

    }
}
