package com.qunar.im.ui.presenter.impl;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.module.WorkWorldDeleteResponse;
import com.qunar.im.base.module.WorkWorldItem;
import com.qunar.im.base.module.WorkWorldResponse;
import com.qunar.im.ui.presenter.WorkWorldPresenter;
import com.qunar.im.ui.presenter.views.WorkWorldView;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.structs.WorkWorldItemState;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.IMUserDefaults;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.HttpUtil;
import com.qunar.im.utils.MD5;
import com.qunar.im.utils.QtalkStringUtils;

import java.util.List;

public class WorkWorldManagerPresenter implements WorkWorldPresenter, IMNotificaitonCenter.NotificationCenterDelegate {
    public WorkWorldView mView;
    private int limit = 0;
    private int size = 20;
    private int childListSize = 5;
    private  int listSize = 10;

    private String searchId;
    private String owner = "";
    private String ownerHost = "";
    private boolean isUser = false;
    private String navurl="";



    public WorkWorldManagerPresenter() {
        addEvent();
    }

    public void addEvent() {
        ConnectionUtil.getInstance().addEvent(this, QtalkEvent.WORK_WORLD_NOTICE);
        ConnectionUtil.getInstance().addEvent(this, QtalkEvent.WORK_WORLD_REFRESH);
        ConnectionUtil.getInstance().addEvent(this,QtalkEvent.WORK_WORLD_SCROLL_TOP);

    }

    @Override
    public void workworldremoveEvent() {
        ConnectionUtil.getInstance().removeEvent(this, QtalkEvent.WORK_WORLD_NOTICE);
        ConnectionUtil.getInstance().removeEvent(this, QtalkEvent.WORK_WORLD_REFRESH);
    }


    @Override
    public void workworldloadingHistory() {
        List<WorkWorldItem> list = null;
        if (isUser) {
            list = ConnectionUtil.getInstance().selectHistoryWorkWorldItem(size, 0, searchId);
        } else {
            list = ConnectionUtil.getInstance().selectHistoryWorkWorldItem(size, 0);
        }

        mView.workworldshowNewData(list);

        IMUserDefaults.getStandardUserDefaults().newEditor(CommonConfig.globalContext)
                .putObject(com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                        + QtalkNavicationService.getInstance().getXmppdomain()
                        + CommonConfig.isDebug
                        + MD5.hex(navurl)
                        + "WORKWORLDSHOWUNREAD", false)
                .synchronize();
        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.WORK_WORLD_NOTICE);
//
        if(mView.workworldisStartRefresh()){
            workworldstartRefresh();
        }
//
    }

    @Override
    public void workworldloadingNoticeCount() {
        int count = ConnectionUtil.getInstance().selectWorkWorldNotice();
        if (count > 0) {
            mView.workworldshowHeadView(count);
        }
    }

    @Override
    public void workworldloadingMore() {
        WorkWorldItem item = mView.workworldgetLastItem();
        if (item == null) {
            return;
        }


//        HttpUtil.refreshWorkWorld(20, owner,ownerHost,0, new ProtocolCallback.UnitCallback<WorkWorldResponse>() {
//            @Override
//            public void onCompleted(WorkWorldResponse workWorldResponse) {
//                mView.workworldshowNewData(workWorldResponse.getData().getNewPost());
//            }
//
//            @Override
//            public void onFailure(String errMsg) {
//
//            }
//        });
//        int postType = WorkWorldItemState.normal;

        int postType = 0;
        //这里先采用这样的写法 区分请求类型 后期好更改
        if(TextUtils.isEmpty(searchId)){
            postType =  WorkWorldItemState.normal;
        }else{
            postType =  WorkWorldItemState.normal;
        }

        HttpUtil.refreshWorkWorldV2(listSize, childListSize, postType, owner, ownerHost, Long.parseLong(item.getCreateTime()), true, new ProtocolCallback.UnitCallback<WorkWorldResponse>() {
            @Override
            public void onCompleted(WorkWorldResponse workWorldResponse) {
                mView.workworldshowMoreData(workWorldResponse.getData().getNewPost());
            }

            @Override
            public void onFailure(String errMsg) {
                List<WorkWorldItem> list = null;


                if (isUser) {
                    list = ConnectionUtil.getInstance().selectHistoryWorkWorldItem(size, mView.workworldgetListCount(), searchId);
                } else {
                    list = ConnectionUtil.getInstance().selectHistoryWorkWorldItem(size, mView.workworldgetListCount());
                }


                mView.workworldshowMoreData(list);
            }
        });




//        HttpUtil.refreshWorkWorld(listSize, owner, ownerHost, Long.parseLong(item.getCreateTime()), new ProtocolCallback.UnitCallback<WorkWorldResponse>() {
//            @Override
//            public void onCompleted(WorkWorldResponse workWorldResponse) {
//                mView.workworldshowMoreData(workWorldResponse.getData().getNewPost());
//            }
//
//            @Override
//            public void onFailure(String errMsg) {
//                List<WorkWorldItem> list = null;
//
//
//                if (isUser) {
//                    list = ConnectionUtil.getInstance().selectHistoryWorkWorldItem(size, mView.workworldgetListCount(), searchId);
//                } else {
//                    list = ConnectionUtil.getInstance().selectHistoryWorkWorldItem(size, mView.workworldgetListCount());
//                }
//
//
//                mView.workworldshowMoreData(list);
//            }
//        }, 1);

    }

    @Override
    public void workworldstartRefresh() {
        mView.workworldstartRefresh();

        int postType = 0;
        if(TextUtils.isEmpty(searchId)){
           postType =  WorkWorldItemState.hot|WorkWorldItemState.top|WorkWorldItemState.normal;
        }else{
            postType =  WorkWorldItemState.normal;
        }
        Logger.i("获取的数据类型为:"+postType);
        HttpUtil.refreshWorkWorldV2(listSize, childListSize, postType, owner, ownerHost, 0, true, new ProtocolCallback.UnitCallback<WorkWorldResponse>() {
            @Override
            public void onCompleted(WorkWorldResponse workWorldResponse) {
                mView.workworldshowNewData(workWorldResponse.getData().getNewPost());
                IMUserDefaults.getStandardUserDefaults().newEditor(CommonConfig.globalContext)
                        .putObject(com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                                + QtalkNavicationService.getInstance().getXmppdomain()
                                + CommonConfig.isDebug
                                + MD5.hex(navurl)
                                + "WORKWORLDSHOWUNREAD", false)
                        .synchronize();
                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.WORK_WORLD_NOTICE);
            }

            @Override
            public void onFailure(String errMsg) {
                mView.workworldcloseRefresh();
            }
        });

//        HttpUtil.refreshWorkWorld(20, owner, ownerHost, 0, new ProtocolCallback.UnitCallback<WorkWorldResponse>() {
//            @Override
//            public void onCompleted(WorkWorldResponse workWorldResponse) {
//                mView.workworldshowNewData(workWorldResponse.getData().getNewPost());
//                IMUserDefaults.getStandardUserDefaults().newEditor(CommonConfig.globalContext)
//                        .putObject(com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
//                                + QtalkNavicationService.getInstance().getXmppdomain()
//                                + CommonConfig.isDebug
//                                + MD5.hex(navurl)
//                                + "WORKWORLDSHOWUNREAD", false)
//                        .synchronize();
//                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.WORK_WORLD_NOTICE);
////
//            }
//
//            @Override
//            public void onFailure(String errMsg) {
//
//            }
//        }, 1);
    }

    @Override
    public void workworlddeleteWorkWorldItem(WorkWorldItem item) {
        HttpUtil.deleteWorkWorldItem(item.getUuid(), new ProtocolCallback.UnitCallback<WorkWorldDeleteResponse>() {
            @Override
            public void onCompleted(WorkWorldDeleteResponse deleteWorkWorldItem) {
                mView.workworldremoveWorkWorldItem(deleteWorkWorldItem);
            }

            @Override
            public void onFailure(String errMsg) {

            }
        });
    }

    public WorkWorldManagerPresenter(WorkWorldView view) {
        this.mView = view;
         navurl = DataUtils.getInstance(CommonConfig.globalContext).getPreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_URL, "");

        addEvent();
    }

    public WorkWorldManagerPresenter(WorkWorldView view, String searchId) {
        this.mView = view;
        this.searchId = searchId;
        this.isUser = true;
        this.owner = QtalkStringUtils.parseId(searchId);
        this.ownerHost = QtalkStringUtils.parseDomain(searchId);
        navurl = DataUtils.getInstance(CommonConfig.globalContext).getPreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_URL, "");

        addEvent();
    }


    @Override
    public void didReceivedNotification(String key, Object... args) {
        if(mView!=null) {


            switch (key) {
                case QtalkEvent.WORK_WORLD_NOTICE:
                    int count = ConnectionUtil.getInstance().selectWorkWorldNotice();
                    if (count > 0) {

                        mView.workworldshowHeadView(count);
                    } else {
                        mView.workworldhiddenHeadView();
                    }
                    break;
                case QtalkEvent.WORK_WORLD_REFRESH:
                    workworldstartRefresh();

                    break;
                case QtalkEvent.WORK_WORLD_SCROLL_TOP:
                    mView.scrollTop();
                    break;
            }
        }
    }
}
