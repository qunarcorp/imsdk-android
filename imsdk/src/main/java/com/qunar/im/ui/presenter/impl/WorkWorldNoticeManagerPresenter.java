package com.qunar.im.ui.presenter.impl;

import android.text.TextUtils;

import com.qunar.im.base.module.WorkWorldMyReply;
import com.qunar.im.base.module.WorkWorldNoticeHistoryResponse;
import com.qunar.im.base.module.WorkWorldNoticeItem;
import com.qunar.im.base.util.Constants;
import com.qunar.im.ui.presenter.WorkWorldNoticePresenter;
import com.qunar.im.ui.presenter.views.WorkWorldNoticeView;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.HttpUtil;

import java.util.ArrayList;
import java.util.List;

public class WorkWorldNoticeManagerPresenter implements WorkWorldNoticePresenter{

    private  WorkWorldNoticeView workWorldNoticeView;
    private int size = 20;
    private int limit = 0;


    @Override
    public void loadingHistory() {
        if(workWorldNoticeView.isMindMessage()) {


            List<WorkWorldNoticeItem> list = ConnectionUtil.getInstance().selectHistoryWorkWorldNotice(workWorldNoticeView.getShowCount(), 0);
            workWorldNoticeView.showNewData(list);

            if (list.size() > 0) {
                WorkWorldNoticeItem item = list.get(0);
                String time = item.getCreateTime();
                if (TextUtils.isEmpty(time)) {
                    time = System.currentTimeMillis() + "";
                } else {

                    if ("0".equals(item.getCreateTime())) {
                        time = System.currentTimeMillis() + "";
                    } else {
                        time = item.getCreateTime();
                    }
                }
                HttpUtil.setWorkWorldNoticeReadTime(time, new ProtocolCallback.UnitCallback<WorkWorldNoticeHistoryResponse>() {
                    @Override
                    public void onCompleted(WorkWorldNoticeHistoryResponse workWorldNoticeHistoryResponse) {

                    }

                    @Override
                    public void onFailure(String errMsg) {

                    }
                });
            }
        }else{
            startRefresh();
        }
    }

    @Override
    public void setView(WorkWorldNoticeView view) {
        workWorldNoticeView = view;
    }

    @Override
    public void startRefresh() {
        List<String> typeList = new ArrayList<>();
        typeList.add(Constants.WorkWorldState.MYREPLYCOMMENT);
        List<WorkWorldNoticeItem> his = (List<WorkWorldNoticeItem>) ConnectionUtil.getInstance().selectHistoryWorkWorldNoticeByEventType(size,limit, typeList, false);

        workWorldNoticeView.showNewData(his);
        workWorldNoticeView.startRefresh();


        HttpUtil.refreshWorkWorldMyReply(20,  0, new ProtocolCallback.UnitCallback<WorkWorldMyReply>() {
            @Override
            public void onCompleted(WorkWorldMyReply workWorldResponse) {

                workWorldNoticeView.showNewData(workWorldResponse.getData().getNewComment());
            }

            @Override
            public void onFailure(String errMsg) {
                workWorldNoticeView.closeRefresh();
            }
        });

    }

    @Override
    public void loadingMore() {

        WorkWorldNoticeItem item = workWorldNoticeView.getLastItem();
        if (item == null) {
            return;
        }

        HttpUtil.refreshWorkWorldMyReply(20,  Long.parseLong(item.getCreateTime()), new ProtocolCallback.UnitCallback<WorkWorldMyReply>() {
            @Override
            public void onCompleted(WorkWorldMyReply workWorldResponse) {
                workWorldNoticeView.showMoreData(workWorldResponse.getData().getNewComment());
            }

            @Override
            public void onFailure(String errMsg) {
                List<WorkWorldNoticeItem> list = null;
                List<String> typeList = new ArrayList<>();
                typeList.add(Constants.WorkWorldState.MYREPLYCOMMENT);


                    list = (List<WorkWorldNoticeItem>) ConnectionUtil.getInstance().selectHistoryWorkWorldNoticeByEventType(size, workWorldNoticeView.getListCount(),typeList, false);
//                } else {
//                    list = ConnectionUtil.getInstance().selectHistoryWorkWorldItem(size, mView.workworldgetListCount());
//                }


                workWorldNoticeView.showMoreData(list);
            }
        });




    }
}
