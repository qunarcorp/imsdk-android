package com.qunar.im.ui.presenter.impl;

import com.qunar.im.base.common.DailyMindConstants;
import com.qunar.im.base.jsonbean.BaseJsonResult;
import com.qunar.im.base.jsonbean.DailyMindMain;
import com.qunar.im.base.jsonbean.DailyMindSub;
import com.qunar.im.ui.presenter.IDailyMindPresenter;
import com.qunar.im.ui.presenter.model.IDailyMindDataModel;
import com.qunar.im.ui.presenter.model.impl.DailyMindDataModel;
import com.qunar.im.ui.presenter.views.IDailyMindMainView;
import com.qunar.im.ui.presenter.views.IDailyMindSubEditView;
import com.qunar.im.ui.presenter.views.IDailyMindSubView;
import com.qunar.im.base.protocol.DailyMindApi;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.util.EventBusEvent;

import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by lihaibin.li on 2017/8/22.
 */

public class DailyMindPresenter implements IDailyMindPresenter {
    private IDailyMindMainView iPasswordBoxView;
    private IDailyMindSubView iDailyMindSubView;
    private IDailyMindSubEditView iDailyMindSubEditView;
    private IDailyMindDataModel iDailyMindDataModel;

    public DailyMindPresenter() {
        iDailyMindDataModel = new DailyMindDataModel();
    }

    @Override
    public void setView(IDailyMindMainView iPasswordBoxView) {
        this.iPasswordBoxView = iPasswordBoxView;
    }

    @Override
    public void setView(IDailyMindSubView iDailyMindSubView) {
        this.iDailyMindSubView = iDailyMindSubView;
    }

    @Override
    public void setView(IDailyMindSubEditView iDailyMindSubEditView) {
        this.iDailyMindSubEditView = iDailyMindSubEditView;
    }

    @Override
    public void operateDailyMindFromHttp(final String method, Map<String, String> requestParams) {
        operateDailyMindFromHttp(false,method,requestParams);
    }

    @Override
    public void operateDailyMindFromHttp(final boolean isFromLogin, final String method, Map<String, String> requestParams) {
        DailyMindApi.operatePassword(method, requestParams, new ProtocolCallback.UnitCallback() {
            @Override
            public void onCompleted(Object o) {
                if (o instanceof BaseJsonResult) {
                    BaseJsonResult baseJsonResult = (BaseJsonResult) o;
                    String error = baseJsonResult.errmsg;
                    if (iPasswordBoxView != null)
                        iPasswordBoxView.showErrMsg(error);
                    if (iDailyMindSubView != null)
                        iDailyMindSubView.showErrMsg(error);
                    if (DailyMindConstants.GET_CLOUD_MAIN.equals(method)) {
                        if (iPasswordBoxView != null) iPasswordBoxView.setCloudMain();
                        if(!isFromLogin)
                            EventBus.getDefault().post(new EventBusEvent.PasswordBox(null, null));
                    } else if (DailyMindConstants.GET_CLOUD_SUB.equals(method)) {
                        if (iDailyMindSubView != null) iDailyMindSubView.setCloudSub();
                        if(!isFromLogin)
                            EventBus.getDefault().post(new EventBusEvent.PasswordBox(null, null));
                    }
                    return;
                }
                if (DailyMindConstants.SAVE_TO_MAIN.equals(method)||DailyMindConstants.UPDATE_MAIN.equals(method)) {//
                    DailyMindMain dailyMindMain = (DailyMindMain) o;
                    iDailyMindDataModel.insertBoxMain(dailyMindMain);
                    if (iPasswordBoxView != null)
                        iPasswordBoxView.addDailyMain(dailyMindMain);
                    EventBus.getDefault().post(new EventBusEvent.PasswordBox(dailyMindMain));
                } else if (DailyMindConstants.GET_CLOUD_MAIN.equals(method)) {
                    List<DailyMindMain> dailyMindMains = (List<DailyMindMain>) o;
                    iDailyMindDataModel.insertMultiBoxMain(dailyMindMains);
                    if (iPasswordBoxView != null)
                        iPasswordBoxView.setCloudMain();
                    if(!isFromLogin)
                    EventBus.getDefault().post(new EventBusEvent.PasswordBox(dailyMindMains, null));
                } else if (DailyMindConstants.SAVE_TO_SUB.equals(method) || DailyMindConstants.UPDATE_SUB.equals(method)) {
                    DailyMindSub dailyMindSub = (DailyMindSub) o;
                    iDailyMindDataModel.insertBoxSub(dailyMindSub);
                    if (iDailyMindSubView != null)
                        iDailyMindSubView.addDailySub(dailyMindSub);
                    EventBus.getDefault().post(new EventBusEvent.PasswordBox(dailyMindSub));
                } else if (DailyMindConstants.GET_CLOUD_SUB.equals(method)) {
                    List<DailyMindSub> dailyMindSubs = (List<DailyMindSub>) o;
                    iDailyMindDataModel.insertMultiBoxSub(dailyMindSubs);
                    if (iDailyMindSubView != null)
                        iDailyMindSubView.setCloudSub();
                    if(!isFromLogin)
                    EventBus.getDefault().post(new EventBusEvent.PasswordBox(null, dailyMindSubs));
                } else if (DailyMindConstants.DELETE_MAIN.equals(method)) {
                    String qid = o.toString();
                    iDailyMindDataModel.deleteBoxMain(qid);
                    if (iPasswordBoxView != null)
                        iPasswordBoxView.setCloudMain();
                }
            }

            @Override
            public void onFailure(String errMsg) {
                if (DailyMindConstants.GET_CLOUD_MAIN.equals(method)) {
                    if (iPasswordBoxView != null) iPasswordBoxView.setCloudMain();
                } else if (DailyMindConstants.GET_CLOUD_SUB.equals(method)) {
                    if (iDailyMindSubView != null) iDailyMindSubView.setCloudSub();
                }
                if (iPasswordBoxView != null)
                    iPasswordBoxView.showErrMsg("请求失败！");
                if (iDailyMindSubView != null)
                    iDailyMindSubView.showErrMsg("请求失败！");
            }
        });
    }

    @Override
    public void operateDailyMindFromHttp(final String method, DailyMindSub dailyMindSub) {
        DailyMindApi.operatePassword(method, dailyMindSub, new ProtocolCallback.UnitCallback() {
            @Override
            public void onCompleted(Object o) {
                if (o instanceof BaseJsonResult) {
                    BaseJsonResult baseJsonResult = (BaseJsonResult) o;
                    String error = baseJsonResult.errmsg;
                    if (iPasswordBoxView != null)
                        iPasswordBoxView.showErrMsg(error);
                    if (iDailyMindSubView != null)
                        iDailyMindSubView.showErrMsg(error);
                    return;
                }
                if (DailyMindConstants.UPDATE_SUB.equals(method)) {
                    DailyMindSub dailyMindSub = (DailyMindSub) o;
                    iDailyMindDataModel.updateBoxSub(dailyMindSub);
                    if (iDailyMindSubEditView != null)
                        iDailyMindSubEditView.updatePasswordBoxSub(dailyMindSub);
                }
            }

            @Override
            public void onFailure(String errMsg) {
                if (iPasswordBoxView != null)
                    iPasswordBoxView.showErrMsg("请求失败！");
                if (iDailyMindSubView != null)
                    iDailyMindSubView.showErrMsg("请求失败！");
            }
        });
    }

    @Override
    public List<DailyMindMain> getDailyMainFromDB(int type, int offset, int number) {
        return iDailyMindDataModel.getCloudMain(type,offset, number);
    }

    @Override
    public List<DailyMindSub> getDailySubFromDB(int offset, int number, int qid) {
        return iDailyMindDataModel.getCloudSub(offset, number, qid);
    }

    @Override
    public void dropBoxMainTable() {
        iDailyMindDataModel.dropPasswordBoxMainTable();
    }

    @Override
    public DailyMindSub getDailySubByTitleFromDB(String title, String qid) {
        return iDailyMindDataModel.getCloudSubByTitle(title, qid);
    }

    @Override
    public DailyMindMain getDailyMainByTitleFromDB() {
        return iDailyMindDataModel.getCloudMainByTitle();
    }
}
