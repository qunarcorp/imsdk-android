package com.qunar.im.core.manager;

import android.content.Context;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.jsonbean.SetWorkWorldRemindResponse;
import com.qunar.im.base.module.CityLocal;
import com.qunar.im.base.module.MedalListResponse;
import com.qunar.im.base.module.MedalUserStatusResponse;
import com.qunar.im.base.module.VideoSetting;
import com.qunar.im.base.module.WorkWorldNoticeHistoryResponse;
import com.qunar.im.base.module.WorkWorldResponse;
import com.qunar.im.base.structs.WorkWorldItemState;
import com.qunar.im.log.LogConstans;
import com.qunar.im.log.LogService;
import com.qunar.im.log.QLog;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.HttpUtil;
import com.qunar.im.base.jsonbean.BaseJsonResult;
import com.qunar.im.base.jsonbean.DepartmentResult;
import com.qunar.im.base.jsonbean.HotlinesResult;
import com.qunar.im.base.jsonbean.IncrementUsersResult;
import com.qunar.im.base.jsonbean.MessageStateSendJsonBean;
import com.qunar.im.base.jsonbean.NewRemoteConfig;
import com.qunar.im.base.jsonbean.OpsUnreadResult;
import com.qunar.im.base.jsonbean.PushSettingResponseBean;
import com.qunar.im.base.jsonbean.QuickReplyResult;
import com.qunar.im.base.module.AreaLocal;
import com.qunar.im.base.module.CalendarTrip;
import com.qunar.im.base.module.MucListResponse;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.structs.MessageStatus;
import com.qunar.im.base.structs.PushSettinsStatus;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.DateTimeUtils;
import com.qunar.im.base.util.IMUserDefaults;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.enums.LoginStatus;
import com.qunar.im.core.services.ClearLogService;
import com.qunar.im.core.services.QtalkHttpService;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.protobuf.entity.XMPPJID;
import com.qunar.im.protobuf.stream.PbAssemblyUtil;
import com.qunar.im.utils.CalendarSynchronousUtil;
import com.qunar.im.utils.MD5;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.util.Map;
import java.lang.reflect.Method;

/**
 * Created by may on 2017/7/12.
 */
//登录完成管理器
public class LoginComplateManager {

    private static final String TAG = "LoginComplateManager";

    private static boolean isBackgroundLogin = false;

    public static void loginComplate() {
        Logger.i("登陆完成,进行数据准备");
//        checkNetworkStatus();

//        checkMessageState();
//        updateLastMsgTime();
//        IMNotificaitonCenter.getInstance().postMainThreadNotificationName("LoginStatusChanged", LoginStatus.Updating);
        if (!isBackgroundLogin) {
            Logger.i("登通知界面更新UI:同步中...");
            IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.LOGIN_EVENT, LoginStatus.Updating);
            // TODO: 2017/11/15 记下来的代码继续进行log丰富
//            IMDatabaseManager.getInstance().updateMessageState(MessageState.Waiting, MessageState.Failed);
            //更新我自己的Card


            long a5s = System.currentTimeMillis();
            updateOfflineMessages();
            long a5e = System.currentTimeMillis();
            long time5 = a5e - a5s;
            Logger.i("time5:" + time5);
            try {
                //日志记录
                LogService.getInstance()
                        .saveLog(QLog.build(LogConstans.LogType.COD, LogConstans.LogSubType.NATIVE)
                                .describtion("收取历史耗时")
                                .costTime(time5)
                                .method("updateOfflineMessages"));
            } catch (Exception e) {

            }
            //请求checkconfig
//            if(true){
//                return;
//            }
            //拉取密码箱
            Logger.i("同步完成,登通知界面更新UI:已连接");
            IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.LOGIN_EVENT, LoginStatus.Login);
            checkWorkWorldPermissions();

            long a44a = System.currentTimeMillis();
            getWordWorldNoticeHistory();
            getWorkWorldRemind();
            long a44b = System.currentTimeMillis();
            Logger.i("time_work:" + (a44a - a44b));

            long updateStart = System.currentTimeMillis();
            updateMessageStateNoticeServer();
            Logger.i("更新消息状态用时:" + (System.currentTimeMillis() - updateStart));
            updateMyPushSetting();



            updateMedalList();

//            long a13s = System.currentTimeMillis();
//            updateUserMucPushConfig();
//            long a13e = System.currentTimeMillis();
//            Logger.i("time12" + (a13s - a13e));

            //更新自己的名片
            long a2s = System.currentTimeMillis();
            updateMyCard();
            if (!CommonConfig.isQtalk && CurrentPreference.getInstance().isMerchants()) {//qchat 是客服 通知服务器 客服已上线
                notifyOnLine();
            }
            long a2e = System.currentTimeMillis();
            Logger.i("time2:" + (a2e - a2s));
            //更新群组信息
            long a3s = System.currentTimeMillis();
            long time = updateMucList();
            long a3e = System.currentTimeMillis();
            Logger.i("time3:" + (a3e - a3s));
            //TODO 暂时注释掉更新全量的群信息 以后看时候可以做成增量的


            long a4s = System.currentTimeMillis();
            updateMucInfoList(time);
            long a4e = System.currentTimeMillis();
            Logger.i("time4:" + (a4e - a4s));

            //更新ops红点
            updateMyOPSMessage();

            long a6s = System.currentTimeMillis();
            updateMessageState();
            long a6e = System.currentTimeMillis();
            //获取虚拟用户信息
            Logger.i("time6:" + (a6e - a6s));

            getVideoSetting();

            long a7s = System.currentTimeMillis();
            get_virtual_user_role();
            long a7e = System.currentTimeMillis();
            Logger.i("time7:" + (a7e - a7s));


            long a8s = System.currentTimeMillis();
            updateUserServiceConfig(false);
            long a8e = System.currentTimeMillis();
            Logger.i("time8:" + (a8e - a8s));

            long a9s = System.currentTimeMillis();
            HttpUtil.getMyCapability(false);
            long a9e = System.currentTimeMillis();
            Logger.i("time9:" + (a9e - a9s));

            if (!CommonConfig.isQtalk) {
                updateQuickReply(false);
            }

            long a10s = System.currentTimeMillis();
            if (CommonConfig.isQtalk) {
                Logger.i("qtalk情况下,拉取密码箱");
                try {
                    Class<?> clazz = Class.forName("com.qunar.im.ui.services.PullPasswordBoxService");
                    Method method = clazz.getMethod("runPullPasswordBoxService", Context.class);
                    method.invoke(null, CommonConfig.globalContext);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            long a10e = System.currentTimeMillis();
            Logger.i("time10:" + (a10e - a10s));


            //暂时把部分接口放在通知完界面更新
            //获取组织架构人员,这里获取的应该是全公司
            long a1s = System.currentTimeMillis();
            Logger.i("开始获取组织架构人员");
            if (CommonConfig.isQtalk) {
                processBuddy();
            } else {
                getQchatDepInfo();
            }
            long a1e = System.currentTimeMillis();
            Logger.i("组织架构time:" + (a1e - a1s));

            //获取已添加的公众号信息
//            if (CommonConfig.isQtalk) {
//                RobotListPresenter presenter = new RobotListPresenter();
//                presenter.loadRobotIdList4mNet();
//            }

            long a11s = System.currentTimeMillis();
            ConnectionUtil.getInstance().setNotificationConfig();
            long a11e = System.currentTimeMillis();

            clearLogFile();

//            long a12s = System.currentTimeMillis();
//            setConfigProfile();
//            long a12e = System.currentTimeMillis();

            updateTripList();

            updateTripArae();

            updateTripCity();
            return;
        }
    }

    public static void updateMedalList() {


        int version = IMDatabaseManager.getInstance().selectMedalListVersion();
        HttpUtil.getMedal(version, new ProtocolCallback.UnitCallback<MedalListResponse>() {
            @Override
            public void onCompleted(MedalListResponse medalListResponse) {
                new String();
            }

            @Override
            public void onFailure(String errMsg) {

            }
        });

        int statusVersion = IMDatabaseManager.getInstance().selectUserMedalStatusVersion();
        HttpUtil.getUserMedalStatus(statusVersion, new ProtocolCallback.UnitCallback<MedalUserStatusResponse>() {
            @Override
            public void onCompleted(MedalUserStatusResponse medalUserStatusResponse) {
                new String();
            }

            @Override
            public void onFailure(String errMsg) {

            }
        });
    }

    /**
     * 获取视频权限设定接口
     */
    private static void getVideoSetting() {
        HttpUtil.videoSetting(new ProtocolCallback.UnitCallback<VideoSetting>() {
            @Override
            public void onCompleted(VideoSetting videoSetting) {
                Logger.i("获取视频接口设定成功:" + JsonUtils.getGson().toJson(videoSetting));
            }

            @Override
            public void onFailure(String errMsg) {
                Logger.i("获取视频接口设定失败");
            }
        });
    }


    /**
     * 更新会议城市
     */
    private static void updateTripCity() {
        HttpUtil.getCity(new ProtocolCallback.UnitCallback<CityLocal>() {
            @Override
            public void onCompleted(CityLocal areaLocal) {
                IMDatabaseManager.getInstance().InsertCity(areaLocal);
            }

            @Override
            public void onFailure(String errMsg) {

            }
        });
    }

    /**
     * 更新会议区域
     */
    public static void updateTripArae() {
        HttpUtil.getArea(new ProtocolCallback.UnitCallback<AreaLocal>() {
            @Override
            public void onCompleted(AreaLocal areaLocal) {
                IMDatabaseManager.getInstance().InsertArea(areaLocal);
            }

            @Override
            public void onFailure(String errMsg) {

            }
        });
    }

    /**
     * 更新日历列表
     */
    public static void updateTripList() {

        long version = IMDatabaseManager.getInstance().selectUserTripVersion();
//        version = 0;
        HttpUtil.getUserTripList(version, new ProtocolCallback.UnitCallback<CalendarTrip>() {
            @Override
            public void onCompleted(CalendarTrip calendarTrip) {
                IMDatabaseManager.getInstance().InsertTrip(calendarTrip);
                IMDatabaseManager.getInstance().insertUserTripVersion(Long.parseLong(calendarTrip.getData().getUpdateTime()));
                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.UPDATE_TRIP);
                CalendarSynchronousUtil.bulkTrip(calendarTrip);

            }

            @Override
            public void onFailure(String errMsg) {

            }
        });
    }

    /**
     * 通知服务器消息发送状态
     */
    public static void updateMessageStateNoticeServer() {
        //只查询当前数据库中不是自己发送的消息并且阅读状态为0的
        List<MessageStateSendJsonBean> list = IMDatabaseManager.getInstance().getMessageStateSendNotXmppIdJson(CurrentPreference.getInstance().getPreferenceUserId(), "1");
        if (list == null || list.size() < 1) {
            return;
        }
        Logger.i("json查到的状态为0的数据 第一次 " + JsonUtils.getGson().toJson(list));
        Logger.i("开始发送收到消息已送达状态,同时更新客户端本地消息状态");
        for (int i = 0; i < list.size(); i++) {
            ProtoMessageOuterClass.ProtoMessage receive = PbAssemblyUtil.getBeenNewReadStateMessage(MessageStatus.STATUS_SINGLE_DELIVERED + "", list.get(i).getJsonArray(), list.get(i).getUserid(), null);
            IMLogicManager.getInstance().sendMessage(receive);
            IMDatabaseManager.getInstance().updateMessageStateByJsonArray(list.get(i).getJsonArray());

        }
    }

    public static void updateQuickReply(boolean isForce) {
        int gversion = 0;
        int cversion = 0;
        if (isForce) {
            gversion = 0;
            cversion = 0;
            IMDatabaseManager.getInstance().deleteQuickReply();
        } else {
            gversion = IMDatabaseManager.getInstance().selectQuickReplyGroupMaxVersion();
            cversion = IMDatabaseManager.getInstance().selectQuickReplyContentMaxVersion();
        }
        HttpUtil.getQuickReplies(gversion, cversion, new ProtocolCallback.UnitCallback<QuickReplyResult>() {
            @Override
            public void onCompleted(QuickReplyResult quickReplyResult) {
                if (quickReplyResult != null && quickReplyResult.data != null) {
                    ConnectionUtil.getInstance().refreshTheQuickReply(quickReplyResult.data);
                }
            }

            @Override
            public void onFailure(String errMsg) {

            }
        });
    }


    private static void updateMyOPSMessage() {
        HttpUtil.getUnreadCountFromOps(new ProtocolCallback.UnitCallback<OpsUnreadResult>() {
            @Override
            public void onFailure(String errMsg) {
            }

            @Override
            public void onCompleted(OpsUnreadResult opsUnreadResult) {
                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.refreshOPSUnRead, opsUnreadResult.getData().isHasUnread());
            }
        });
    }

    private static void updateMyPushSetting() {
        HttpUtil.getPushMsgSettings(new ProtocolCallback.UnitCallback<PushSettingResponseBean>() {
            @Override
            public void onCompleted(PushSettingResponseBean pushSettingResponseBean) {
                if (pushSettingResponseBean.isRet()) {
                    IMDatabaseManager.getInstance().updatePushSettingAllState(pushSettingResponseBean.getData().getPush_flag());
                    com.qunar.im.protobuf.common.CurrentPreference.getInstance().setTurnOnMsgSound(ConnectionUtil.getInstance().getPushStateBy(PushSettinsStatus.SOUND_INAPP));
                    com.qunar.im.protobuf.common.CurrentPreference.getInstance().setTurnOnMsgShock(ConnectionUtil.getInstance().getPushStateBy(PushSettinsStatus.VIBRATE_INAPP));

                }
            }

            @Override
            public void onFailure(String errMsg) {

            }
        });
    }

    private static void updateMucInfoList(long time) {
        Logger.i("开始更新群信息");
        try {
            IMUserCardManager.getInstance().updateMucCardSync(time);
        } catch (Exception e) {
            Logger.e("updateMucInfoList error:" + e.getLocalizedMessage());
        }
//        if(time == 0){//只有第一次安装 全量更新一次
//            try {
//                IMUserCardManager.getInstance().updateMucCardSync(IMDatabaseManager.getInstance().SelectIMGroupId());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
    }

    /**
     * 获取用户配置
     */
    public static void updateUserServiceConfig(boolean isForce) {
//        if(true){
//            return;
//        }
        int version = 0;
        //这么写虽然没意义 但为了逻辑清晰明了 强制时 清苦数据 拉全量,否则 正常增量迭代
        if (isForce) {
            version = 0;
            IMDatabaseManager.getInstance().deleteUserConfig();
        } else {
            version = IMDatabaseManager.getInstance().selectUserConfigVersion();
        }
//        int version = 0;
        HttpUtil.getUserConfig(version, new ProtocolCallback.UnitCallback<NewRemoteConfig>() {
            @Override
            public void onCompleted(NewRemoteConfig newRemoteConfigs) {
                if (newRemoteConfigs.getData().getClientConfigInfos().size() > 0) {
                    ConnectionUtil.getInstance().refreshTheConfig(newRemoteConfigs);
                }
            }

            @Override
            public void onFailure(String errMsg) {

            }
        });

    }

    /**
     * 请求网络更新当前所在群组列表,这个接口只关乎你是否在群离开群,无关群名片更新
     */
    private static long updateMucList() {

        long time = -1;
        try {
            Logger.i("更新群组数据列表");
            XMPPJID mySelf = IMLogicManager.getInstance().getMyself();
            time = IMDatabaseManager.getInstance().getGroupLastUpdateTime();
            String destUrl = String.format("%s/muc/get_increment_mucs.qunar?u=%s&k=%s",
                    QtalkNavicationService.getInstance().getHttpUrl(),
                    mySelf.getUser(),
                    IMLogicManager.getInstance().getRemoteLoginKey());
            JSONObject inputBody = new JSONObject();
            inputBody.put("u", mySelf.getUser());
            inputBody.put("d", mySelf.getDomain());
            inputBody.put("t", time);
            Logger.i("群组请求地址:" + destUrl + "请求参数:" + inputBody);
            JSONObject response = QtalkHttpService.postJson(destUrl, inputBody);
            if (response == null) {
                return time;
            }
            BaseJsonResult baseResponse = JsonUtils.getGson().fromJson(response.toString(), BaseJsonResult.class);
            if (!baseResponse.ret) {
                return time;
            }
            MucListResponse mucListResponse = JsonUtils.getGson().fromJson(response.toString(), MucListResponse.class);

            Logger.i("群组接口数据返回:" + JsonUtils.getGson().toJson(mucListResponse));
//            if (!mucListResponse.getRet()) {
//                return;
//            }
            if (!(mucListResponse.ret && mucListResponse.getData() != null && mucListResponse.getData().size() > 0)) {
                return time;
            }
            List<MucListResponse.Data> list = mucListResponse.getData();
            List<MucListResponse.Data> okList = new ArrayList<>();
            List<MucListResponse.Data> noList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                MucListResponse.Data data = list.get(i);
                if (data.getF().equals("1")) {
                    okList.add(data);
                } else {
                    noList.add(data);
                }
            }
            Logger.i("更新群组数据库数据");
            IMDatabaseManager.getInstance().updateMucList(okList, noList);

        } catch (Exception e) {

        } finally {
            return time;
        }
    }


    /**
     * 把全部所有的发送中数据更新成发送失败
     */
    private static void updateMessageState() {
        Logger.i("更新所有发送中消息设置为失败");
        IMDatabaseManager.getInstance().updateMessageStateFailed();

    }

    /**
     * 拉取历史消息
     */
    private static void updateOfflineMessages() {
        try {
            IMMessageManager.getInstance().updateOfflineMessage();
        } catch (IOException e) {
            Logger.e(e, "updateOfflineMessages crashed for io");
        } catch (JSONException e) {
            Logger.e(e, "updateOfflineMessages crashed for json");
        }
    }

    private static void updateMyCard() {
        String userId = IMLogicManager.getInstance().getMyself().bareJID().fullname();

        try {
            IMUserCardManager.getInstance().updateUserCard(userId, true);
            //原本在这里设置自身名字,现在更改位置 改为获取body后也获取一次
            String myNickName = IMDatabaseManager.getInstance().selectUserByJID(userId).optString("Name");
            CurrentPreference.getInstance().setUserName(myNickName);
            IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.SHOW_MY_INFO, "");
        } catch (JSONException e) {
            Logger.e(e, "updateMyCard failed.");
        }
    }

    /**
     * qchat 通知后台客服上线
     */
    private static void notifyOnLine() {
        HttpUtil.notifyOnline();
    }

    /**
     * 获取全公司的组织架构人员 这个有异步有同步,现在先改为同步测试一下
     */
    public static void processBuddy() {

//        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Update_Buddy);//更新好友列表
        int version = IMDatabaseManager.getInstance().getLastIncrementUsersVersion();

        HttpUtil.getIncrementUsers(version, new ProtocolCallback.UnitCallback<IncrementUsersResult>() {
            @Override
            public void onCompleted(IncrementUsersResult incrementUsersResult) {
                IMDatabaseManager.getInstance().InsertUserCardInIncrementUser(incrementUsersResult);
                try {
                    //获取完组织架构人员后, 进行一次登陆本人名字赋值
                    String userId = IMLogicManager.getInstance().getMyself().bareJID().fullname();
                    String myNickName = IMDatabaseManager.getInstance().selectUserByJID(userId).getString("Name");
                    CurrentPreference.getInstance().setUserName(myNickName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(String errMsg) {

            }
        });

    }

    public static void getQchatDepInfo() {
//        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Update_Buddy);//更新好友列表
        String time = IMUserDefaults.getStandardUserDefaults().getStringValue(CommonConfig.globalContext, com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                + QtalkNavicationService.getInstance().getXmppdomain()
                + Constants.Preferences.buddytime);
//        long tiem = IMUserDefaults.getStandardUserDefaults().get
        if (!TextUtils.isEmpty(time)) {
            long lastTime = Long.parseLong(time);
            long newTime = System.currentTimeMillis();
            if ((newTime - lastTime) > 24 * 60 * 60 * 1000) {
//                IMUserDefaults.getStandardUserDefaults().newEditor(CommonConfig.globalContext)
//                        .putObject(com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
//                                + QtalkNavicationService.getInstance().getXmppdomain()
//                                + "buddyTime", String.valueOf(newTime))
//                        .synchronize();
            } else {
                //// TODO: 2017/10/26 注释下面这行代码可以每次都获取boddy
                return;
            }
        }

        Logger.i("发送了请求QCHAT组织架构人员请求");
        //不知道这段代码有啥用 先注释
//        int version = 0;// IMDatabaseManager.getInstance().getIncrementUsersCount();
//        String qchatorg = DataUtils.getInstance(CommonConfig.globalContext).getPreferences(com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
//                + QtalkNavicationService.getInstance().getXmppdomain()
//                + Constants.Preferences.qchat_org, "");
//        if (!TextUtils.isEmpty(qchatorg)) {
//            return;
//        }
        Protocol.getQchatDeptInfo(new ProtocolCallback.UnitCallback<DepartmentResult>() {
            @Override
            public void onCompleted(DepartmentResult departmentResult) {
                if (departmentResult != null) {
                    //qchat组织架构缓存
                    DataUtils.getInstance(CommonConfig.globalContext).putPreferences(com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                            + QtalkNavicationService.getInstance().getXmppdomain()
                            + Constants.Preferences.qchat_org, JsonUtils.getGson().toJson(departmentResult));

                    String time = String.valueOf(System.currentTimeMillis());

                    IMUserDefaults.getStandardUserDefaults().newEditor(CommonConfig.globalContext)
                            .putObject(com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                                    + QtalkNavicationService.getInstance().getXmppdomain()
                                    + Constants.Preferences.buddytime, time)
                            .synchronize();
                }
            }

            @Override
            public void onFailure(String errMsg) {

            }
        });
    }

    /**
     * 清理旧日志
     */
    private static void clearLogFile() {
        //上次清理时间，大于7点进行清理
        long lastClearTime = DataUtils.getInstance(CommonConfig.globalContext).getPreferences("lastClearTime", 0L);
        Logger.i(TAG + " clearLogFile lastClearTime = " + DateTimeUtils.getTime(lastClearTime, true, true) + "  当前时间" + DateTimeUtils.getTime(System.currentTimeMillis(), false, true));
        if (lastClearTime > 0) {
            if (System.currentTimeMillis() - lastClearTime > 7 * 24 * 60 * 60 * 1000) {
                ClearLogService.runClearLogService(CommonConfig.globalContext);
                DataUtils.getInstance(CommonConfig.globalContext).putPreferences("lastClearTime", System.currentTimeMillis());
            }
        } else {
            DataUtils.getInstance(CommonConfig.globalContext).putPreferences("lastClearTime", System.currentTimeMillis());
        }
    }

    public static void get_virtual_user_role() {
        Logger.i("开始获取虚拟账户列表");
        HttpUtil.getHotlineList(new ProtocolCallback.UnitCallback<HotlinesResult.DataBean>() {
            @Override
            public void onCompleted(HotlinesResult.DataBean hotlines) {
                if(hotlines != null){
                    ConnectionUtil.getInstance().cacheHotlines(hotlines.allhotlines);
                    CurrentPreference.getInstance().setHotLineList(hotlines.allhotlines);
                    CurrentPreference.getInstance().setMyHotlines(hotlines.myhotlines);
                }
            }

            @Override
            public void onFailure(String errMsg) {

            }
        });

    }

    //更新设置选项
//    private static void setConfigProfile() {
//        CurrentPreference.ProFile f = IMDatabaseManager.getInstance().getProFile();
//        CurrentPreference.Preference preference = IMDatabaseManager.getInstance().getPreference();
//        if (f != null)
//            CurrentPreference.getInstance().setProFile(f);
//        if (preference != null){
//            CurrentPreference.getInstance().setPreference(preference);
//        }
//    }


    /**
     * 检查是否有朋友圈权限
     */
    private static void checkWorkWorldPermissions() {

        HttpUtil.checkWorkWorldPermissionsV2(new ProtocolCallback.UnitCallback<Boolean>() {
            @Override
            public void onCompleted(Boolean aBoolean) {
                boolean workworldState = IMDatabaseManager.getInstance().SelectWorkWorldPremissions();
                IMDatabaseManager.getInstance().InsertWorkWorldPremissions(aBoolean);
                if (aBoolean == workworldState) {
                    //证明当前朋友圈权限没有改变不进行更改
                } else {
                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.RESTART);
                }


            }

            @Override
            public void onFailure(String errMsg) {

            }
        });


    }


    public static void getWordWorldNoticeHistory() {
        final String navurl = DataUtils.getInstance(CommonConfig.globalContext).getPreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_URL, "");

        boolean show = IMUserDefaults.getStandardUserDefaults().getBooleanValue(CommonConfig.globalContext,
                com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                        + QtalkNavicationService.getInstance().getXmppdomain()
                        + CommonConfig.isDebug
                        + MD5.hex(navurl)
                        + "WORKWORLDSHOWUNREAD", false);
        if (show) {

            IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.WORK_WORLD_NOTICE);
        } else {


            HttpUtil.refreshWorkWorldV2(1, 0, WorkWorldItemState.normal, "", "", 0, false, new ProtocolCallback.UnitCallback<WorkWorldResponse>() {
                @Override
                public void onCompleted(WorkWorldResponse workWorldResponse) {
                    if (workWorldResponse != null && workWorldResponse.getData().getNewPost() != null && workWorldResponse.getData().getNewPost().size() > 0) {
                        boolean isHave = IMDatabaseManager.getInstance().selectHistoryWorkWorldItemIsHave(workWorldResponse.getData().getNewPost().get(0));


                        if (!isHave) {

                            IMUserDefaults.getStandardUserDefaults().newEditor(CommonConfig.globalContext)
                                    .putObject(com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                                            + QtalkNavicationService.getInstance().getXmppdomain()
                                            + CommonConfig.isDebug
                                            + MD5.hex(navurl)
                                            + "WORKWORLDSHOWUNREAD", true)
                                    .synchronize();

                            IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.WORK_WORLD_NOTICE);
//
                        }
                    }
                }

                @Override
                public void onFailure(String errMsg) {
                    Logger.i("获取帖子失败:" + errMsg);
//                mView.workworldcloseRefresh();
                }
            });
        }

        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.WORK_WORLD_NOTICE);
        //// TODO: 2017/9/4 实际上应该把最后一条消息的时间提前获取,提前到连接建立之前


//        long start = System.currentTimeMillis();

        String timeId = IMUserDefaults.getStandardUserDefaults().getStringValue(CommonConfig.globalContext,
                com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                        + QtalkNavicationService.getInstance().getXmppdomain()
                        + CommonConfig.isDebug
                        + MD5.hex(navurl)
                        + "lastwwuuid");
//        long lastMessageTime = IMDatabaseManager.getInstance().getLastestMessageTime();
        String timeStr = IMUserDefaults.getStandardUserDefaults().getStringValue(CommonConfig.globalContext,
                com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                        + QtalkNavicationService.getInstance().getXmppdomain()
                        + CommonConfig.isDebug
                        + MD5.hex(navurl)
                        + "lastwwtime");

        HttpUtil.getWorkWorldHistory(timeId, timeStr, new ProtocolCallback.UnitCallback<WorkWorldNoticeHistoryResponse>() {
            @Override
            public void onCompleted(WorkWorldNoticeHistoryResponse workWorldNoticeHistoryResponse) {

                IMUserDefaults.getStandardUserDefaults().newEditor(CommonConfig.globalContext)
                        .removeObject(com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                                + QtalkNavicationService.getInstance().getXmppdomain()
                                + CommonConfig.isDebug
                                + MD5.hex(navurl)
                                + "lastwwuuid")
                        .synchronize();

                IMUserDefaults.getStandardUserDefaults().newEditor(CommonConfig.globalContext)
                        .removeObject(com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                                + QtalkNavicationService.getInstance().getXmppdomain()
                                + CommonConfig.isDebug
                                + MD5.hex(navurl)
                                + "lastwwtime")
                        .synchronize();


//                if(workWorldNoticeHistoryResponse.getData().getMsgList().size()>0){
                //通知
                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.WORK_WORLD_NOTICE);
//                }
            }

            @Override
            public void onFailure(String errMsg) {

            }
        });


    }


    public static void getWorkWorldRemind() {
        HttpUtil.getWorkWorldRemind(new ProtocolCallback.UnitCallback<SetWorkWorldRemindResponse>() {
            @Override
            public void onCompleted(SetWorkWorldRemindResponse setWorkWorldRemindResponse) {

            }

            @Override
            public void onFailure(String errMsg) {

            }
        });
    }

}