package com.qunar.im.ui.presenter.impl;

import com.qunar.im.base.jsonbean.BaseJsonResult;
import com.qunar.im.base.jsonbean.RequestRobotInfo;
import com.qunar.im.base.jsonbean.RobotInfoResult;
import com.qunar.im.base.jsonbean.RobotInfoResult.RobotBody;
import com.qunar.im.base.jsonbean.RobotInfoResult.RobotItemResult;
import com.qunar.im.base.module.PublishPlatform;
import com.qunar.im.ui.presenter.IRobotInfoPresenter;
import com.qunar.im.ui.presenter.model.IPublishPlatformDataModel;
import com.qunar.im.ui.presenter.model.IPublishPlatformNewsDataModel;
import com.qunar.im.ui.presenter.model.impl.PublishPlatformDataModel;
import com.qunar.im.ui.presenter.model.impl.PublishPlatformNewsDataModel;
import com.qunar.im.ui.presenter.views.IRobotInfoView;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.protocol.RobotAPI;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.utils.QtalkStringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by saber on 15-9-15.
 */
public class RobotInfoPresenter implements IRobotInfoPresenter {
    public final static String TAG = "RobotInfoPresenter";

    IRobotInfoView robotInfoView;

    IPublishPlatformDataModel publishPlatformDataModel;
    IPublishPlatformNewsDataModel publishPlatformNewsDataModel;

    public RobotInfoPresenter()
    {
        publishPlatformDataModel = new PublishPlatformDataModel();
        publishPlatformNewsDataModel = new PublishPlatformNewsDataModel();
    }

    PublishPlatform platform = null;

    @Override
    public void followRobot() {
        if(robotInfoView!=null) {
            RobotAPI.addRobot(robotInfoView.getUserId(), robotInfoView.getRobotId(), new ProtocolCallback.UnitCallback<BaseJsonResult>() {
                @Override
                public void onFailure(String errMsg) {
                    LogUtil.d(TAG, "error");
                }

                @Override
                public void onCompleted(BaseJsonResult baseJsonResult) {
                    LogUtil.d(TAG, baseJsonResult.errmsg);
                    if (baseJsonResult.ret ) {
                        robotInfoView.setFollowRobotResult(true);
                        publishPlatformDataModel.insertOrUpdatePublishPlatform(platform);
                    } else{
                        //已存在订阅
                        // Toast.makeText(context, "已经关注该公众号", Toast.LENGTH_SHORT).show();
                        robotInfoView.setFollowRobotResult(false);
                    }
                }
            });
        }
    }

    @Override
    public void unfollowRobot() {
        //删除成功
        if (!publishPlatformDataModel.deleteById(QtalkStringUtils.userId2Jid(robotInfoView.getRobotId()))) {
            LogUtil.d(TAG, "删除数据库失败\t" + robotInfoView.getRobotId());
        }
        if (robotInfoView != null) {
            RobotAPI.delRobot(robotInfoView.getUserId(), robotInfoView.getRobotId(), new ProtocolCallback.UnitCallback<BaseJsonResult>() {
                @Override
                public void onCompleted(BaseJsonResult baseJsonResult) {
                    if (!baseJsonResult.ret) {
                        // 删除失败
                        LogUtil.d(TAG, baseJsonResult.errmsg);
                        robotInfoView.setUnfollowRobotResult(false);
                    } else  {
                        robotInfoView.setUnfollowRobotResult(true);
                    }

                }

                @Override
                public void onFailure(String errMsg) {
                    LogUtil.d(TAG, "取消关注失败\t" + robotInfoView.getRobotId());
                    robotInfoView.setUnfollowRobotResult(false);
                }
            });
        }
    }

    @Override
    public void loadRobotInfo() {
        PublishPlatform publishPlatform = publishPlatformDataModel.selectById(QtalkStringUtils.userId2Jid(robotInfoView.getRobotId()));
        robotInfoView.setFollowStatus(publishPlatform!=null);
        int version = -1;
        if(publishPlatform!=null)
        {
            version = publishPlatform.getVersion();
            RobotBody body = JsonUtils.getGson().fromJson(publishPlatform.getPublishPlatformInfo(), RobotBody.class);
            updateInfo(body,null);
        }
        // 成功添加订阅
        List<RequestRobotInfo> requestRobotInfos = new ArrayList<RequestRobotInfo>();
        RequestRobotInfo info = new RequestRobotInfo(robotInfoView.getRobotId(), version);
        requestRobotInfos.add(info);
        getRobotInfo(requestRobotInfos);

    }

    private void updateInfo(RobotBody body,PublishPlatform platform) {
        Map<String, String> info = new HashMap<String, String>();
        if (platform != null)
            platform.setDescription(body.robotDesc);
        info.put("id", body.robotEnName);
        if (platform != null)
            platform.setDescription(body.robotDesc);
        info.put("description", body.robotDesc);

        if (platform != null)
            platform.setGravatarUrl(body.headerurl);
        info.put("gravatarUrl", body.headerurl);

        if (platform != null)
            platform.setName(body.robotCnName);
        info.put("name", body.robotCnName);


        info.put("tel", body.tel);

        if (robotInfoView != null) {
            robotInfoView.setInfo(info);
        }
    }


    protected void getRobotInfo(List<RequestRobotInfo> requestRobotInfos) {
        if(requestRobotInfos == null||requestRobotInfos.size() == 0)
            return;
        for(int i=0;i<requestRobotInfos.size();i++)
        {
            final List<RequestRobotInfo> list = requestRobotInfos.subList(i, i + 1);
            RobotAPI.getRobotInfo(list, new ProtocolCallback.UnitCallback<RobotInfoResult>() {
                @Override
                public void onCompleted(RobotInfoResult robotInfoResult) {
                    if (robotInfoResult.ret ) {
                        List<RobotItemResult> results = robotInfoResult.data;
                        //TODO 更新数据库
                        for (RobotItemResult item : results) {
                            PublishPlatform platform = new PublishPlatform();
                            if (item.rbt_name != null) {
                                platform.setId(QtalkStringUtils.userId2Jid(item.rbt_name));
                            }
                            if (item.rbt_body != null) {
                                RobotBody body = item.rbt_body;
                                updateInfo(body,platform);
                                platform.setExtentionFlag((body.receiveswitch ? 1 : 0) & PublishPlatform.RECEIVE_SWITCH_FLAG);
                                if (item.rbt_body != null) {
                                    if(!body.replayable)
                                        platform.setPublishPlatformType(PublishPlatform.NOTICE_MSG);
                                    if(body.rawhtml)
                                        platform.setExtentionFlag(platform.getExtentionFlag()|PublishPlatform.WEB_MSG);
                                    platform.setPublishPlatformInfo(JsonUtils.getGson().toJson(item.rbt_body, RobotBody.class));
                                }
                            }
                            RobotInfoPresenter.this.platform = platform;
                            if(list.get(0).version>-1) {
                                if (!publishPlatformDataModel.insertOrUpdatePublishPlatform(platform)) {
                                    LogUtil.d(TAG, "插入数据失败 insertOrUpdatePublishPlatform()" + platform.getId());
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(String errMsg) {
                    LogUtil.d(TAG, "network error");
                }
            });
        }
    }

    @Override
    public void setRobotInfoView(IRobotInfoView view) {
        robotInfoView = view;
    }

    @Override
    public void clearHistory() {
        if(robotInfoView == null)
        {
            return;
        }
        publishPlatformNewsDataModel.delMsgByPlatformId(QtalkStringUtils.userId2Jid(robotInfoView.getRobotId()));
    }
}
