package com.qunar.im.core.manager;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.jsonbean.BaseJsonResult;
import com.qunar.im.base.jsonbean.JSONChatHistorys;
import com.qunar.im.base.jsonbean.JSONMucHistorys;
import com.qunar.im.base.jsonbean.JSONReadMark;
import com.qunar.im.base.jsonbean.LogInfo;
import com.qunar.im.base.jsonbean.NewReadStateByJson;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.IMUserDefaults;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.enums.MessageState;
import com.qunar.im.core.services.QtalkHttpService;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.core.utils.GlobalConfigManager;
import com.qunar.im.log.LogConstans;
import com.qunar.im.log.LogService;
import com.qunar.im.log.QLog;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.protobuf.utils.StringUtils;
import com.qunar.im.utils.MD5;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by may on 2017/7/13.
 */

public class IMMessageManager {

    private static IMMessageManager instance = new IMMessageManager();

    private static boolean chatStateRequest;
    private static boolean chatRequest;
    private static boolean groupRequest;
    private static boolean headlineRequest;
    private static boolean groupReadMarkRequest;

    private String latestGroupReadMarkTime;

    public static IMMessageManager getInstance() {
        return instance;
    }

    public void updateOfflineMessage() throws IOException, JSONException {
        //// TODO: 2017/9/4 实际上应该把最后一条消息的时间提前获取,提前到连接建立之前


        long start = System.currentTimeMillis();
        String navurl = DataUtils.getInstance(CommonConfig.globalContext).getPreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_URL, "");
        String timeId = IMUserDefaults.getStandardUserDefaults().getStringValue(CommonConfig.globalContext,
                com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                        + QtalkNavicationService.getInstance().getXmppdomain()
                        + CommonConfig.isDebug
                        + MD5.hex(navurl)
                        + "lastMessageId");
//        long lastMessageTime = IMDatabaseManager.getInstance().getLastestMessageTime();
        String timeStr = IMUserDefaults.getStandardUserDefaults().getStringValue(CommonConfig.globalContext,
                com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                        + QtalkNavicationService.getInstance().getXmppdomain()
                        + CommonConfig.isDebug
                        + MD5.hex(navurl)
                        + "lastMessageTime");

        latestGroupReadMarkTime = IMUserDefaults.getStandardUserDefaults().getStringValue(CommonConfig.globalContext,
                com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                        + QtalkNavicationService.getInstance().getXmppdomain()
                        + CommonConfig.isDebug
                        + MD5.hex(navurl)
                        + "lastGroupReadMarkTime");

        Logger.i("开始同步历史记录,同步时间点:" + timeStr + "," + timeId + "," + latestGroupReadMarkTime);
        //这段代码有问题找胡滨

        double lastMessageTime = Double.parseDouble(timeStr);
//        double lastMessageTime = 1521985232000d;
//        if (lastMessageTime - start > 2500000) {
//            IMDatabaseManager.getInstance().clearIMMessage();
//        }
//        lastMessageTime = 1516939864000d;
        //先设置两个请求的成功返回值为false
        chatRequest = false;
        groupRequest = false;
        headlineRequest = false;
        groupReadMarkRequest = false;


        //这里根据时间戳获取单聊消息
//        getUserChatlogSince(lastMessageTime);

        long groupstart = System.currentTimeMillis();
        getMucHistorysJSON(lastMessageTime);
        long groupend = System.currentTimeMillis();
        Logger.i("json群聊用时:" + (groupend - groupstart));


        long chatstart = System.currentTimeMillis();
        getChatHistorysJSON(lastMessageTime);
        long chatend = System.currentTimeMillis();
        Logger.i("json单聊用时:" + (chatend - chatstart));


        long stateStart = System.currentTimeMillis();
        getChatStateHistorysJSON(lastMessageTime);
        long stateEnd = System.currentTimeMillis();
        Logger.i("json单聊状态用时:" + (stateEnd - stateStart));


//        updateMessageState();


        if (CommonConfig.isQtalk) {
            getHeadLineMessageJson(lastMessageTime);
        } else {
            headlineRequest = true;
        }
//            getHeadLineMessageFromHistory(lastMessageTime);

        if (chatRequest && groupRequest && headlineRequest && chatStateRequest) {
            IMUserDefaults.getStandardUserDefaults().newEditor(CommonConfig.globalContext)
                    .removeObject(com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                            + QtalkNavicationService.getInstance().getXmppdomain()
                            + CommonConfig.isDebug
                            + MD5.hex(navurl)
                            + "lastMessageTime")
                    .synchronize();

            IMUserDefaults.getStandardUserDefaults().newEditor(CommonConfig.globalContext)
                    .removeObject(com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                            + QtalkNavicationService.getInstance().getXmppdomain()
                            + CommonConfig.isDebug
                            + MD5.hex(navurl)
                            + "lastMessageId")
                    .synchronize();
            Logger.i("json历史记录同步成功,删除sp中时间点");
        }
        if(groupReadMarkRequest){
            IMUserDefaults.getStandardUserDefaults().newEditor(CommonConfig.globalContext)
                    .removeObject(com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                            + QtalkNavicationService.getInstance().getXmppdomain()
                            + CommonConfig.isDebug
                            + MD5.hex(navurl)
                            + "lastGroupReadMarkTime")
                    .synchronize();
        }
        long end = System.currentTimeMillis();
        Logger.i("json-这里获取历史记录结束,用时:" + (end - start));


    }

//    public void updateMessageState() {
//        //只查询当前数据库中不是自己发送的消息并且阅读状态为0的
//        List<MessageStateSendJsonBean> list = IMDatabaseManager.getInstance().getMessageStateSendNotXmppIdJson(CurrentPreference.getInstance().getPreferenceUserId(), "1");
//        Logger.i("json查到的状态为0的数据 第一次 "+JsonUtils.getGson().toJson(list));
//        if (list == null || list.size() < 1) {
//            return;
//        }
//        Logger.i("开始发送收到消息已送达状态,同时更新客户端本地消息状态");
//        for (int i = 0; i < list.size(); i++) {
//            ProtoMessageOuterClass.ProtoMessage receive = PbAssemblyUtil.getBeenNewReadStateMessage(MessageStatus.STATUS_SINGLE_DELIVERED + "", list.get(i).getJsonArray(), list.get(i).getUserid(), null);
//            IMLogicManager.getInstance().sendMessage(receive);
//            IMDatabaseManager.getInstance().updateMessageStateByJsonArray(list.get(i).getJsonArray());
//
//        }
//    }


    /**
     * qtalk 拉取历史headline系统消息
     *
     * @param lastMessageTime
     * @return
     * @throws JSONException
     * @throws IOException
     */
    private JSONArray getHeadLineMessageFromHistory(double lastMessageTime) throws JSONException, IOException {
        String jid = IMLogicManager.getInstance().getMyself().getUser();
        String domain = IMLogicManager.getInstance().getMyself().getDomain();
        if (!StringUtils.isEmpty(jid)) {
            String destUrl = String.format("%s/get_notice_history?server=%s&c=%s&u=%s&k=%s&p=android&v=%s",
                    QtalkNavicationService.getInstance().getHttpHost(),
                    domain,
                    GlobalConfigManager.getAppName(),
                    jid,
                    IMLogicManager.getInstance().getRemoteLoginKey(),
                    GlobalConfigManager.getAppVersion());

            JSONObject inputBody = new JSONObject();
            inputBody.put("U", "SystemMessage");
            inputBody.put("D", domain);
            inputBody.put("Time", lastMessageTime);

            JSONObject response = QtalkHttpService.postJson(destUrl, inputBody);
            JSONArray msgList = null;
            if (response != null && response.has("ret")) {
                //请求成功设置为true
                boolean result = response.getBoolean("ret");
                if (result) {
                    msgList = response.getJSONArray("data");
                }
            }
            //这里获取的是历史系统消息插入客户端
            if (msgList != null && msgList.length() > 0) {
                String user = IMLogicManager.getInstance().getMyself().getUser();
                try {
                    IMDatabaseManager.getInstance().bulkInsertChatHistory(msgList, user, MessageState.didRead);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }
        return null;
    }


    /**
     * 插入单聊消息
     *
     * @param lastMessageTime
     * @return
     * @throws JSONException
     * @throws IOException
     */
    private JSONArray getUserChatlogSince(double lastMessageTime) throws JSONException, IOException {
        String jid = IMLogicManager.getInstance().getMyself().getUser();
        String domain = IMLogicManager.getInstance().getMyself().getDomain();
        if (!StringUtils.isEmpty(jid)) {
            String destUrl = String.format("%s/domain/get_history?server=%s&c=%s&u=%s&k=%s&p=android&v=%s",
                    QtalkNavicationService.getInstance().getHttpHost(),
                    domain,
                    GlobalConfigManager.getAppName(),
                    jid,
                    IMLogicManager.getInstance().getRemoteLoginKey(),
                    GlobalConfigManager.getAppVersion());

            JSONObject inputBody = new JSONObject();
            inputBody.put("User", jid);
            inputBody.put("Host", domain);
//            lastMessageTime = Long.parseLong("1512576000000");
            inputBody.put("Time", lastMessageTime);

            JSONObject response = QtalkHttpService.postJson(destUrl, inputBody);
            JSONArray msgList = null;
            if (response != null && response.has("ret")) {
                //请求成功设置为true

                boolean result = response.getBoolean("ret");
                if (result) {
                    msgList = response.getJSONArray("data");
                    chatRequest = true;
//                    return msgList;
                }
            }
            //这里获取的是全部的单聊消息并插入客户端
            if (msgList != null && msgList.length() > 0) {
                String user = IMLogicManager.getInstance().getMyself().getUser();
                try {
                    Logger.i("xmll单人消息长度:" + msgList.length());
                    IMDatabaseManager.getInstance().bulkInsertChatHistory(msgList, user, MessageState.didRead);
                    chatRequest = true;
                    Logger.i("单人历史记录同步成功");
                } catch (Exception e) {
                    chatRequest = false;
                    Logger.i("单人历史记录同步失败");
                    e.printStackTrace();
                }

            }
        }
        return null;
    }


    /**
     * 拉取历史记录获取json版本数据
     *
     * @param lastMessageTime
     */
    private void getMucHistorysJSON(double lastMessageTime) throws JSONException, IOException {
        String q_ckey = Protocol.getCKEY();
        if (TextUtils.isEmpty(q_ckey)) return;
        String jid = IMLogicManager.getInstance().getMyself().getUser();
        String domain = IMLogicManager.getInstance().getMyself().getDomain();
        if (TextUtils.isEmpty(jid)) {
            return;
        }
        String host = QtalkNavicationService.getInstance().getJavaUrl();
        String requestUrl = "%s/qtapi/getmuchistory.qunar?server=%s&c=qtalk&u=%s&k=%s&p=%s&v=%s";
        requestUrl = String.format(requestUrl,
                host,
                domain,
                IMLogicManager.getInstance().getMyself().getUser(),
                IMLogicManager.getInstance().getRemoteLoginKey(),
                GlobalConfigManager.getAppPlatform(),
                GlobalConfigManager.getAppVersion());

        int num = 1000;

        //{"user":"hubin.hu","host":"ejabhost1","num":"1000","time":"1506067658442","domain":"ejabhost1"}
        JSONObject inputObject = new JSONObject();
//        lastMessageTime = Long.parseLong("1512576000000");
        inputObject.put("time", lastMessageTime);
        inputObject.put("user", jid);
        inputObject.put("host", domain);
        inputObject.put("domain", domain);
        inputObject.put("num", num + "");
        Map<String, String> cookie = new HashMap<>();
        cookie.put("Cookie", "q_ckey=" + q_ckey + ";");
        //假设这次拉历史和插库都顺利设置为true
        groupRequest = true;

        try {
            boolean isNext = true;
            do {
                inputObject.put("time", lastMessageTime);
                Logger.i("json地址:" + requestUrl + ";json参数:" + inputObject + ";ck:" + q_ckey);
                long start = System.currentTimeMillis();
                JSONObject response = QtalkHttpService.postJson(requestUrl, inputObject, cookie);
                if (response == null) {
                    groupRequest = false;
                    return;
                }
//                Logger.i("json群接口返回的数据:" + response);
                long end = System.currentTimeMillis();
                Logger.i("json群聊单次请求时间:" + (end - start));
                BaseJsonResult baseJson = JsonUtils.getGson().fromJson(response.toString(), BaseJsonResult.class);
                long is = System.currentTimeMillis();
                if (baseJson.ret) {
                    JSONMucHistorys mucJson = JsonUtils.getGson().fromJson(response.toString(), JSONMucHistorys.class);
                    if (mucJson.getData().size() > 0) {
                        Logger.i("json群聊接口返回的数据size:" + mucJson.getData().size());
                        lastMessageTime = mucJson.getData().get(mucJson.getData().size() - 1).getT();
                        inputObject.put("time", lastMessageTime);
                        boolean success = IMDatabaseManager.getInstance().bulkInsertGroupHistoryFroJson(mucJson.getData(), com.qunar.im.protobuf.common.CurrentPreference.getInstance().getPreferenceUserId(),false);
                        if (groupRequest) {
                            groupRequest = success;
                        }
                        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Show_List);
                    }

                    if (mucJson.getData().size() < num) {
                        Logger.i("json群数据量少终止请求");
                        isNext = false;
//                        groupRequest = true;
                    }

                } else {
                    Logger.i("请求失败终止请求");
                    //获取数据出现意外
                    isNext = false;
                    groupRequest = false;
                }
                long ie = System.currentTimeMillis();
                Logger.i("json群聊单次插入时间:" + (ie - is));

                try{
                    //日志记录
                    LogInfo logInfo = QLog.build(LogConstans.LogType.CAT,LogConstans.LogSubType.HTTP)
                            .costTime(end - start)
                            .describtion("拉取群聊历史消息")
                            .method("getMucHistorysJSON");
                    logInfo.setUrl(requestUrl);
                    logInfo.setMethodParams(inputObject.toString());
                    logInfo.setResponse(baseJson.errmsg);
                    LogService.getInstance().saveLog(logInfo);
                }catch (Exception e){

                }
            } while (isNext);

        } catch (Exception e) {
            groupRequest = false;
        }

        try {

            String readmarkUrl = host + "/qtapi/get_muc_readmark1.qunar";

            if(!TextUtils.isEmpty(latestGroupReadMarkTime)){//取出readmark时间
                double latestReadmarkTime = Double.parseDouble(latestGroupReadMarkTime);
                inputObject.put("time",latestReadmarkTime);
            }else {
                inputObject.put("time",0);
            }
            long sr = System.currentTimeMillis();
            JSONObject readmarkResponse = QtalkHttpService.postJson(readmarkUrl, inputObject, cookie);
            if(readmarkResponse == null){
                return;
            }
            long er = System.currentTimeMillis();
            Logger.i("请求阅读指针时间:" + (er - sr));
            Logger.i("返回的阅读指针:" + readmarkResponse.toString());

            long rs = System.currentTimeMillis();
            BaseJsonResult rebaseJson = JsonUtils.getGson().fromJson(readmarkResponse.toString(), BaseJsonResult.class);
            if (rebaseJson.ret) {
                JSONReadMark jsonReadMark = JsonUtils.getGson().fromJson(readmarkResponse.toString(), JSONReadMark.class);
                if(jsonReadMark != null && jsonReadMark.getData() != null && jsonReadMark.getData().size() > 0){
                    IMDatabaseManager.getInstance().updateIMMessageMucRead(jsonReadMark);
                }
                groupReadMarkRequest = true;
            }
            long re = System.currentTimeMillis();
            Logger.i("json阅读指针插入完成时间:" + (re - rs));
        } catch (Exception e) {
            e.printStackTrace();
        }

//        do {

//            }else {
//                fa
//            }
//        }while (true)


//        IMDatabaseManager.getInstance().bulkInsertGroupHistory(resultList, groupId, myNickName, readMarkT, MessageState.didRead, rtxId);


//        Logger.i("json历史信息:" + response);


    }

    private void getHeadLineMessageJson(double lastMessageTime) throws JSONException {
        String q_ckey = Protocol.getCKEY();
        if (TextUtils.isEmpty(q_ckey)) return;
        String jid = IMLogicManager.getInstance().getMyself().getUser();
        String domain = IMLogicManager.getInstance().getMyself().getDomain();
        if (TextUtils.isEmpty(jid)) {
            return;
        }
        String host = QtalkNavicationService.getInstance().getJavaUrl();
        String requestUrl = "%s/qtapi/get_system_history.qunar?server=%s&c=qtalk&u=%s&k=%s&p=%s&v=%s";
        requestUrl = String.format(requestUrl,
                host,
                domain,
                IMLogicManager.getInstance().getMyself().getUser(),
                IMLogicManager.getInstance().getRemoteLoginKey(),
                GlobalConfigManager.getAppPlatform(),
                GlobalConfigManager.getAppVersion());

        int num = 1000;
        JSONObject inputObject = new JSONObject();
        inputObject.put("time", lastMessageTime);
        inputObject.put("user", jid);
        inputObject.put("host", domain);
        inputObject.put("domain", domain);
        inputObject.put("num", num + "");
        Map<String, String> cookie = new HashMap<>();
        cookie.put("Cookie", "q_ckey=" + q_ckey + ";");
        headlineRequest = true;
        try {
            boolean isNext = true;
            List<JSONChatHistorys.DataBean> list = new ArrayList<>();
            do {
                inputObject.put("time", lastMessageTime);
                Logger.i("json系统消息地址:" + requestUrl + ";json参数:" + inputObject + ";ck:" + q_ckey);
                long start = System.currentTimeMillis();
                JSONObject response = QtalkHttpService.postJson(requestUrl, inputObject, cookie);
//                Logger.i("json群接口返回的数据:" + response);
                long end = System.currentTimeMillis();
                Logger.i("系统消息单次请求时间:" + (end - start));
                if (response == null) {
                    headlineRequest = false;
                    return;
                }
                Logger.i("json headline接口返回的数据:" + response);
                BaseJsonResult baseJson = JsonUtils.getGson().fromJson(response.toString(), BaseJsonResult.class);
                if (baseJson == null) {
                    headlineRequest = false;
                    return;
                }

                if (baseJson.ret) {
                    JSONChatHistorys chatJson = JsonUtils.getGson().fromJson(response.toString(), JSONChatHistorys.class);
                    if (chatJson.getData().size() > 0) {
                        Logger.i("json系统消息接口返回的数据size:" + chatJson.getData().size());
                        lastMessageTime = chatJson.getData().get(chatJson.getData().size() - 1).getT();
                        inputObject.put("time", lastMessageTime);
                        boolean success = IMDatabaseManager.getInstance().bulkInsertChatHistoryFroJson(chatJson.getData(), com.qunar.im.protobuf.common.CurrentPreference.getInstance().getPreferenceUserId(), false);
                        if (headlineRequest) {
                            headlineRequest = success;
                        }
                        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Show_List);
                    }

                    if (chatJson.getData().size() < num) {
                        Logger.i("json系统消息数据量少终止请求");
                        isNext = false;
                    }
                    //正确获取了数据

                } else {
                    Logger.i("请求失败终止请求");
                    //获取数据出现意外
                    isNext = false;
                    headlineRequest = false;
                }

            } while (isNext);
            //// TODO: 2017/12/6 开始进行插入数据库


        } catch (Exception e) {
            e.printStackTrace();
            headlineRequest = false;
        }
    }

    /**
     * 获取单聊历史状态
     *
     * @param lastMessageTime
     */
    private void getChatStateHistorysJSON(double lastMessageTime) throws JSONException, IOException {
        String q_ckey = Protocol.getCKEY();
        if (TextUtils.isEmpty(q_ckey)) return;
        String jid = IMLogicManager.getInstance().getMyself().getUser();
        String domain = IMLogicManager.getInstance().getMyself().getDomain();
        if (TextUtils.isEmpty(jid)) {
            return;
        }
        String host = QtalkNavicationService.getInstance().getJavaUrl();
        String stateUrl = "%s/qtapi/getreadflag.qunar?server=%s&c=qtalk&u=%s&k=%s&p=%s&v=%s";
        stateUrl = String.format(stateUrl,
                host,
                domain,
                IMLogicManager.getInstance().getMyself().getUser(),
                IMLogicManager.getInstance().getRemoteLoginKey(),
                GlobalConfigManager.getAppPlatform(),
                GlobalConfigManager.getAppVersion());

        JSONObject inputObject = new JSONObject();
//        inputObject.put("time", Long.valueOf(lastMessageTime));
//        lastMessageTime = Long.parseLong("1512576000000");
        inputObject.put("time", lastMessageTime);
        inputObject.put("domain", domain);
        Map<String, String> cookie = new HashMap<>();
        cookie.put("Cookie", "q_ckey=" + q_ckey + ";");
        chatStateRequest = true;//假设拉取历史状态及插库一切顺利

        //开始进行获取状态接口
        try {


            Logger.i("json获取单人聊天消息状态地址:" + stateUrl);
            Logger.i("json获取单人聊天消息状态参数:" + inputObject.toString());
            JSONObject stateResponse = QtalkHttpService.postJson(stateUrl, inputObject, cookie);
            //缺失失败逻辑
            if(stateResponse == null){
                chatStateRequest = false;
                return;
            }
            Logger.i("json单聊状态接口返回的数据明细:" + stateResponse.toString());
            BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(stateResponse.toString(), BaseJsonResult.class);
            if (baseJsonResult == null) {
                chatStateRequest = false;
            }
            if (baseJsonResult.ret) {
                NewReadStateByJson newReadStateByJson = JsonUtils.getGson().fromJson(stateResponse.toString(), NewReadStateByJson.class);
                if (newReadStateByJson.getData().size() > 0) {
                    Logger.i("json单聊状态接口返回的数据size:" + newReadStateByJson.getData().size());
                    boolean success = IMDatabaseManager.getInstance().updateChatHistoryStateForJson(newReadStateByJson.getData());
                    if (!success) {
                        chatStateRequest = false;
                    }

                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Show_List);
//                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Read_State);
                }
            } else {
                chatStateRequest = false;
            }
            Logger.i("json获取单人聊天消息状态返回:" + stateResponse.toString());
        } catch (Exception e) {
            e.printStackTrace();
            chatStateRequest = false;
        }


    }


    /**
     * 拉取历史记录获取json版本数据
     *
     * @param lastMessageTime
     */
    private void getChatHistorysJSON(double lastMessageTime) throws JSONException, IOException {
        String q_ckey = Protocol.getCKEY();
        if (TextUtils.isEmpty(q_ckey)) return;
        String jid = IMLogicManager.getInstance().getMyself().getUser();
        String domain = IMLogicManager.getInstance().getMyself().getDomain();
        if (TextUtils.isEmpty(jid)) {
            return;
        }
        String host = QtalkNavicationService.getInstance().getJavaUrl();
        String requestUrl = "%s/qtapi/gethistory.qunar?server=%s&c=qtalk&u=%s&k=%s&p=%s&v=%s";
        requestUrl = String.format(requestUrl,
                host,
                domain,
                IMLogicManager.getInstance().getMyself().getUser(),
                IMLogicManager.getInstance().getRemoteLoginKey(),
                GlobalConfigManager.getAppPlatform(),
                GlobalConfigManager.getAppVersion());

        int num = 1000;

        //{"user":"hubin.hu","host":"ejabhost1","num":"1000","time":"1506067658442","domain":"ejabhost1"}
        JSONObject inputObject = new JSONObject();
//        inputObject.put("time", Long.valueOf(lastMessageTime));
//        lastMessageTime = Long.parseLong("1512576000000");
        inputObject.put("time", lastMessageTime);
        inputObject.put("user", jid);
        inputObject.put("host", domain);
        inputObject.put("domain", domain);
        inputObject.put("num", num + "");
        inputObject.put("f", "t");
        Map<String, String> cookie = new HashMap<>();
        cookie.put("Cookie", "q_ckey=" + q_ckey + ";");
        chatRequest = true; //假设拉取历史和插库一切顺利
        String stateTime = "";
        //// TODO: 2017/12/5 json版单聊消息
        try {
            boolean isNext = true;
            List<JSONChatHistorys.DataBean> list = new ArrayList<>();
            do {
                inputObject.put("time", lastMessageTime);
                Logger.i("json单聊地址:" + requestUrl + ";json参数:" + inputObject + ";ck:" + q_ckey);
                long start = System.currentTimeMillis();
                JSONObject response = QtalkHttpService.postJson(requestUrl, inputObject, cookie);
//                Logger.i("json群接口返回的数据:" + response);
                long end = System.currentTimeMillis();
                Logger.i("json单聊单次请求时间:" + (end - start));
                if (response == null) {
                    chatRequest = false;
                    return;
                }
//                Logger.i("json单聊接口返回的数据:" + response);
                long is = System.currentTimeMillis();
                BaseJsonResult baseJson = JsonUtils.getGson().fromJson(response.toString(), BaseJsonResult.class);
                if (baseJson == null) {
                    chatRequest = false;
                }

                if (baseJson.ret) {
                    JSONChatHistorys chatJson = JsonUtils.getGson().fromJson(response.toString(), JSONChatHistorys.class);
                    if (chatJson.getData().size() > 0) {
                        Logger.i("json单聊接口返回的数据size:" + chatJson.getData().size());
                        lastMessageTime = chatJson.getData().get(chatJson.getData().size() - 1).getT();
//                        stateTime = chatJson.getData().get(chatJson.getData().size() - 1).getMessage().getMsec_times();
                        inputObject.put("time", lastMessageTime);
                        boolean success = IMDatabaseManager.getInstance().bulkInsertChatHistoryFroJson(chatJson.getData(), com.qunar.im.protobuf.common.CurrentPreference.getInstance().getPreferenceUserId(), false);
                        if (chatRequest) {
                            chatRequest = success;
                        }
                        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Show_List);
                    }

                    if (chatJson.getData().size() < num) {
                        Logger.i("json单聊数据量少终止请求");
                        isNext = false;
//                        chatRequest = true;
                    }
                    //正确获取了数据

                } else {
                    Logger.i("请求失败终止请求");
                    //获取数据出现意外
                    isNext = false;
                    chatRequest = false;
                }
                long ie = System.currentTimeMillis();
                Logger.i("json单聊单次插入时间:" + (ie - is));

            } while (isNext);
            //// TODO: 2017/12/6 开始进行插入数据库


            //收到了单聊消息后,发送消息时间 证明消息已送达
//            if(!TextUtils.isEmpty(stateTime)) {
//                JSONObject jb = new JSONObject();
//                jb.put("t", stateTime);
//
//                ProtoMessageOuterClass.ProtoMessage receive = PbAssemblyUtil.getBeenNewReadStateForTimeMessage(MessageStatus.STATUS_SINGLE_DELIVERED_TIME + "", jb, CurrentPreference.getInstance().getPreferenceUserId(), null);
//                IMLogicManager.getInstance().sendMessage(receive);
//            }

        } catch (Exception e) {
            e.printStackTrace();
            chatRequest = false;
            chatStateRequest = false;
        }


    }


    /**
     * 插入群聊消息
     *
     * @param lastGroupTime
     * @throws JSONException
     * @throws IOException
     */
    public void getMucHistorys(double lastGroupTime) throws JSONException, IOException {
        String jid = IMLogicManager.getInstance().getMyself().getUser();
        String domain = IMLogicManager.getInstance().getMyself().getDomain();
        if (!StringUtils.isEmpty(jid)) {
            String requestUrl = String.format("%s/domain/get_muc_history_v2?server=%s&c=%s&u=%s&k=%s&p=%s&v=%s",
                    QtalkNavicationService.getInstance().getHttpHost(),
                    domain,
                    GlobalConfigManager.getAppName(),
                    IMLogicManager.getInstance().getMyself().getUser(),
                    IMLogicManager.getInstance().getRemoteLoginKey(),
                    GlobalConfigManager.getAppPlatform(),
                    GlobalConfigManager.getAppVersion());

            if (lastGroupTime <= 0) {
                lastGroupTime = System.currentTimeMillis() - 24 * 3600 * 1000;
            }

            JSONObject inputObject = new JSONObject();
//            lastGroupTime = Long.parseLong("1512576000000");
            inputObject.put("T", lastGroupTime);
            inputObject.put("u", jid);
            inputObject.put("U", jid);
            inputObject.put("D", String.format("conference.%s", domain));


            JSONObject response = QtalkHttpService.postJson(requestUrl, inputObject);

//            Logger.i("mu版本数据:", response);
            int num = 0;
            if (response != null && response.has("errcode")) {
                //请求成功 设置为true


                int errCode = response.getInt("errcode");
                if (errCode == 0) {
                    String groupId = null;
                    try {
                        groupRequest = true;
                        //返回里面又很多个data 所以需要循环一下
                        JSONArray data = response.getJSONArray("data");
                        for (int i = 0; i < data.length(); ++i) {
                            //获取其中一个data 的JsonObject
                            JSONObject groupMsgDic = data.getJSONObject(i);
                            //这里获取的是外层的domain
                            String myDomain = groupMsgDic.getString("Domain");
                            //这里是按照群消息id来分配消息的
                            groupId = String.format("%s@conference.%s",
                                    groupMsgDic.getString("ID"),
                                    myDomain);
                            //这里获取了一个时间标记
                            long readMarkT = groupMsgDic.optLong("Time");
                            JSONArray resultList = groupMsgDic.getJSONArray("Msg");
                            if (resultList == null || resultList.length() <= 0)
                                continue;

                            List msgList = null;
                            {


                                String myNickName = getGroupNickNameByGroupId(groupId);
                                String rtxId = IMLogicManager.getInstance().getMyself().getUser();
                                Logger.i("groupId:" + groupId + ";myNickName:" + myNickName);
                                num += resultList.length();
                                IMDatabaseManager.getInstance().bulkInsertGroupHistory(resultList, groupId, myNickName, readMarkT, MessageState.didRead, rtxId);
                                Logger.i("群历史记录同步成功:" + groupId);
//                                groupRequest = true;

                            }

                            updateNotReadCountCacheByJid(groupId);

                        }

                        Logger.i("xml群size:" + num);
                    } catch (Exception e) {
                        Logger.e(e, "getMucHistorys");
                        Logger.i("群历史记录同步失败:" + groupId);
                        groupRequest = false;
                    }

                }
            }
        }
    }

    private void updateNotReadCountCacheByJid(String groupId) {

    }

    @Nullable
    public String getGroupNickNameByGroupId(String groupId) {
        JSONObject userInfo = IMLogicManager.getInstance().getUserInfoByUserId(IMLogicManager.getInstance().getMyself());
        if (userInfo != null) {

            try {
                String name = userInfo.has("Name") ? userInfo.getString("Name") : (userInfo.has("UserId") ? userInfo.getString("UserId") : "");

                return name;
            } catch (JSONException e) {
                Logger.e(e, "getGroupNickNameByGroupId crashed");
            }
        }
        return null;
    }
}
