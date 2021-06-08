package com.qunar.im.ui.presenter.impl;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.jsonbean.RequestRobotInfo;
import com.qunar.im.base.jsonbean.RobotInfoResult;
import com.qunar.im.base.jsonbean.RobotInfoResult.RobotBody;
import com.qunar.im.base.jsonbean.RobotInfoResult.RobotItemResult;
import com.qunar.im.base.jsonbean.StringJsonResult;
import com.qunar.im.base.module.PublishPlatform;
import com.qunar.im.ui.presenter.IRobotListPresenter;
import com.qunar.im.ui.presenter.views.ISearchView;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.protocol.RobotAPI;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.NetworkUtils;
import com.qunar.im.core.manager.IMDatabaseManager;
import com.qunar.im.common.CurrentPreference;
import com.qunar.im.utils.QtalkStringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xinbo.wang on 15-9-14.
 */
//公众号列表获取逻辑
public class RobotListPresenter implements IRobotListPresenter {
    private static final String TAG = "RobotListPresenter";

    private String userId = CurrentPreference.getInstance().getUserid();
    private UpdateableView updateView;

    public RobotListPresenter() {
    }


    @Override
    public void init() {
    }

    @Override
    public void setIRobotListView(UpdateableView view) {
        this.updateView = view;
    }

    @Override
    public void loadRobotList() {
        List<PublishPlatform> publishPlatforms = IMDatabaseManager.getInstance().selectPublishPlatfroms(1000);
        if (publishPlatforms != null && publishPlatforms.size() > 0&&updateView!=null) {
            updateView.update(publishPlatforms);
        } else if(updateView!=null){
            loadRobotIdList4mNet();
        }
    }

    @Override
    public void updateRobotList(PublishPlatform publishPlatform) {
    }

    @Override
    public void loadRobotIdList4mNet() {
        if (NetworkUtils.isConnection(QunarIMApp.getContext()) == NetworkUtils.ConnectStatus.connected) {
            RobotAPI.getMyRobotList(userId, new ProtocolCallback.UnitCallback<StringJsonResult>() {
                @Override
                public void onCompleted(StringJsonResult stringJsonResult) {
                    if (stringJsonResult.ret) {

                        List<String> list = stringJsonResult.data;
                        if (list != null && !list.isEmpty()) {
                            Map<String, RequestRobotInfo> infoMap = new HashMap<String, RequestRobotInfo>();
                            for (String robotId : list) {
                                RequestRobotInfo info = new RequestRobotInfo(robotId, -1);
                                infoMap.put(robotId, info);
                            }

                            boolean refreshFlag = false;
                            List<PublishPlatform> publishPlatforms = IMDatabaseManager.getInstance().selectPublishPlatfroms(1000);
                            if (publishPlatforms != null) {
                                for (int i = 0; i < publishPlatforms.size(); i++) {
                                    PublishPlatform publishPlatform = publishPlatforms.get(i);
                                    RequestRobotInfo info = infoMap.get(publishPlatform.getId());
                                    if (info == null) {
                                        IMDatabaseManager.getInstance().deletePublishPlatformById(publishPlatform.getId());
//                                        publishPlatformDataModel.deleteById(publishPlatform.getId());
                                        publishPlatforms.remove(i);
                                        i--;
                                        refreshFlag = true;
                                    } else {
                                        infoMap.remove(publishPlatform.getId());
                                    }
                                }
                            }
                            if (refreshFlag && updateView != null)
                                updateView.update(publishPlatforms);

                            List<RequestRobotInfo> infos = new ArrayList<RequestRobotInfo>();
                            infos.addAll(infoMap.values());
                            getRobotInfo(infos);
                        } else {
                            LogUtil.e(TAG, "get list is empty");
                        }
                    }
                }

                @Override
                public void onFailure(String errMsg) {
                    defaultOnFailure("getRobotList()");
                    List<PublishPlatform> publishPlatforms = IMDatabaseManager.getInstance().selectPublishPlatfroms(1000);
                    if (!publishPlatforms.isEmpty() && updateView != null) {
                        updateView.update(publishPlatforms);
                    }
                }
            });
        }
    }

    @Override
    public void searchRobot4mNet() {
        String keywrod = updateView.getTerm();
        RobotAPI.searchRobotInfo(keywrod, new ProtocolCallback.UnitCallback<RobotInfoResult>() {
            @Override
            public void onCompleted(RobotInfoResult robotInfoResult) {
                if (robotInfoResult != null) {
                    final List<PublishPlatform> publishPlatforms = new ArrayList<PublishPlatform>();
                    if (robotInfoResult.ret) {
                        List<RobotItemResult> results = robotInfoResult.data;
                        for(RobotItemResult itemResult:results) {
                            String headerurl = itemResult.rbt_body.headerurl;
                            String name = TextUtils.isEmpty(itemResult.rbt_body.robotCnName)?
                                    itemResult.rbt_name:
                                    itemResult.rbt_body.robotCnName;
                            String desc = itemResult.rbt_body.robotDesc;
                            PublishPlatform platform = new PublishPlatform();
                            platform.setId(QtalkStringUtils.userId2Jid(itemResult.rbt_name));
                            platform.setName(name);
                            platform.setDescription(desc);
                            platform.setGravatarUrl(headerurl);
                            if (itemResult.rbt_body != null) {
                                if(!itemResult.rbt_body.replayable)
                                    platform.setPublishPlatformType(PublishPlatform.NOTICE_MSG);
                                if(itemResult.rbt_body.rawhtml)
                                    platform.setExtentionFlag(platform.getExtentionFlag()|PublishPlatform.WEB_MSG);
                                platform.setPublishPlatformInfo(JsonUtils.getGson().toJson(itemResult.rbt_body, RobotBody.class));
                            }
                            publishPlatforms.add(platform);
                        }
                        updateView.update(publishPlatforms);
                    } else {
                        updateView.error("未搜索到该公众号");
                    }
                }
            }

            @Override
            public void onFailure(String errMsg) {

            }
        });
    }

    @Override
    public void selectRobot() {
        String id = updateView.getSelId();
        if(!TextUtils.isEmpty(id))
        {
            PublishPlatform publishPlatform = IMDatabaseManager.getInstance().selectPublishPlatformById(id);
            boolean isFollow = publishPlatform !=null;
            boolean rawHtml = publishPlatform!=null&&(publishPlatform.getExtentionFlag()&PublishPlatform.WEB_MSG) == PublishPlatform.WEB_MSG;
            updateView.setSelRobotInfo(isFollow,id,rawHtml);
        }
    }

    protected void getRobotInfo(List<RequestRobotInfo> requestRobotInfos) {
        if(requestRobotInfos == null||requestRobotInfos.size() == 0)
            return;
        for(int i=0;i<requestRobotInfos.size();i++)
        {
            RobotAPI.getRobotInfo(requestRobotInfos.subList(i,i+1), new ProtocolCallback.UnitCallback<RobotInfoResult>() {
                @Override
                public void onCompleted(RobotInfoResult robotInfoResult) {
                    if (robotInfoResult.ret ) {
                        List<RobotItemResult> results = robotInfoResult.data;
                        //TODO 更新数据库
                        List<PublishPlatform> publishPlatforms = IMDatabaseManager.getInstance().selectPublishPlatfroms(1000);
                        for (RobotItemResult item : results) {
                            PublishPlatform platform = new PublishPlatform();
                            if (item.rbt_name != null) {
                                platform.setId(QtalkStringUtils.userId2Jid(item.rbt_name));
                            }
                            if (item.rbt_body != null) {
                                RobotBody body = item.rbt_body;
                                if (body.robotDesc != null) {
                                    platform.setDescription(body.robotDesc);
                                }
                                if (body.headerurl != null) {
                                    platform.setGravatarUrl(body.headerurl);
                                }
                                if (body.robotCnName != null) {
                                    platform.setName(body.robotCnName);
                                }
                                platform.setExtentionFlag((body.receiveswitch ? 1 : 0) & PublishPlatform.RECEIVE_SWITCH_FLAG);
                                if(!body.replayable)
                                    platform.setPublishPlatformType(PublishPlatform.NOTICE_MSG);
                                if(body.rawhtml)
                                    platform.setExtentionFlag(platform.getExtentionFlag()|PublishPlatform.WEB_MSG);
                                platform.setPublishPlatformInfo(JsonUtils.getGson().toJson(item.rbt_body,
                                        RobotBody.class));
                            }
                            publishPlatforms.add(platform);
                        }
                        if (!IMDatabaseManager.getInstance().InsertPublicNumber(publishPlatforms)) {
                            Logger.e("插入数据失败 insertOrUpdatePublishPlatform()");
                        }

                        if(updateView!=null) {
                            updateView.update(publishPlatforms);
                        }
                    }
                }

                @Override
                public void onFailure(String errMsg) {
                    defaultOnFailure("getRobotInfo");
                }
            });
        }
    }


    private void defaultOnFailure(String method) {
        // Toast.makeText(context, "请求失败" + method, Toast.LENGTH_SHORT).show();
        LogUtil.e(TAG, "Http 请求失败" + method);
    }

    public interface UpdateableView extends ISearchView<PublishPlatform> {
        void update(List<PublishPlatform> platforms);
        void error(String errmsg);
        void setSelRobotInfo(boolean isFollow,String jid,boolean rawhtml);
        String getSelId();
    }
}
