package com.qunar.im.ui.presenter.impl;

import com.qunar.im.base.module.WorkWorldAtShowResponse;
import com.qunar.im.base.module.WorkWorldSearchShowResponse;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.ui.presenter.WorkWorldAtShowPresenter;
import com.qunar.im.ui.presenter.views.WorkWorldAtShowView;
import com.qunar.im.utils.HttpUtil;

public class WorkWorldSearchManagerPresenter implements WorkWorldAtShowPresenter {

    private WorkWorldAtShowView workWorldAtShowView;
    private int size = 20;
    private int limit = 0;
    private static long searchTime;
    private String searchKey;

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

    }

    @Override
    public void loadingMore() {
       int limit =  workWorldAtShowView.getListCount();
//        searchTime = System.currentTimeMillis();
//        searchKey = str;
        HttpUtil.getSearchWorkWorldMessage(limit, size, searchTime, searchKey, "3", new ProtocolCallback.UnitCallback<WorkWorldSearchShowResponse>() {
            @Override
            public void onCompleted(WorkWorldSearchShowResponse workWorldSearchShowResponse) {
                workWorldAtShowView.showMoreData(workWorldSearchShowResponse.getData());
            }

            @Override
            public void onFailure(String errMsg) {

            }
        });
    }

    @Override
    public void startSearch(final String str) {
        workWorldAtShowView.clearData();
        searchTime = System.currentTimeMillis();
        searchKey = str;
        workWorldAtShowView.setEmptyText("搜索'"+str+"'中...");
        HttpUtil.getSearchWorkWorldMessage(limit, size, searchTime, str, "3", new ProtocolCallback.UnitCallback<WorkWorldSearchShowResponse>() {
            @Override
            public void onCompleted(WorkWorldSearchShowResponse workWorldSearchShowResponse) {
                if(workWorldSearchShowResponse.getData().size()>0){

                    workWorldAtShowView.showNewData(workWorldSearchShowResponse.getData());
                }else{
                    workWorldAtShowView.setEmptyText("未找到与'"+str+"'相关结果");
                }
            }

            @Override
            public void onFailure(String errMsg) {

            }
        });

    }
}
