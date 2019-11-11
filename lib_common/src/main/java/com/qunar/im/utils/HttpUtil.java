package com.qunar.im.utils;

import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.common.CommonUploader;
import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.jsonbean.AutoDestroyMessageExtention;
import com.qunar.im.base.jsonbean.BaseJsonResult;
import com.qunar.im.base.jsonbean.CapabilityResult;
import com.qunar.im.base.jsonbean.CollectionUserBindData;
import com.qunar.im.base.jsonbean.EncryptMsg;
import com.qunar.im.base.jsonbean.GetGroupOfflineMessageRequest;
import com.qunar.im.base.jsonbean.GetSingleConvRecord;
import com.qunar.im.base.jsonbean.GroupChatOfflineResult;
import com.qunar.im.base.jsonbean.GroupPushDataBean;
import com.qunar.im.base.jsonbean.HotlinesResult;
import com.qunar.im.base.jsonbean.IncrementUsersResult;
import com.qunar.im.base.jsonbean.JSONChatHistorys;
import com.qunar.im.base.jsonbean.JSONMucHistorys;
import com.qunar.im.base.jsonbean.LeadInfo;
import com.qunar.im.base.jsonbean.NavConfigResult;
import com.qunar.im.base.jsonbean.NewRemoteConfig;
import com.qunar.im.base.jsonbean.OfflineSingleMsgResult;
import com.qunar.im.base.jsonbean.OpsUnreadResult;
import com.qunar.im.base.jsonbean.PushSettingResponseBean;
import com.qunar.im.base.jsonbean.QchatCousltIdBean;
import com.qunar.im.base.jsonbean.QuickReplyResult;
import com.qunar.im.base.jsonbean.RemoteConfig;
import com.qunar.im.base.jsonbean.SeatList;
import com.qunar.im.base.jsonbean.SetMucVCardResult;
import com.qunar.im.base.jsonbean.SetWorkWorldRemindResponse;
import com.qunar.im.base.jsonbean.UploadImageResult;
import com.qunar.im.base.jsonbean.VideoMessageResult;
import com.qunar.im.base.jsonbean.WorkWorldEntrance;
import com.qunar.im.base.module.AnonymousData;
import com.qunar.im.base.module.AreaLocal;
import com.qunar.im.base.module.AvailableRoomRequest;
import com.qunar.im.base.module.AvailableRoomResponse;
import com.qunar.im.base.module.CalendarTrip;
import com.qunar.im.base.module.CityLocal;
import com.qunar.im.base.module.DownLoadFileResponse;
import com.qunar.im.base.module.FoundConfiguration;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.module.MedalListResponse;
import com.qunar.im.base.module.MedalSingleUserStatusResponse;
import com.qunar.im.base.module.MedalUserStatusResponse;
import com.qunar.im.base.module.MedalsInfo;
import com.qunar.im.base.module.MedalsInfoResponse;
import com.qunar.im.base.module.ReleaseDataRequest;
import com.qunar.im.base.module.SetLikeData;
import com.qunar.im.base.module.SetLikeDataResponse;
import com.qunar.im.base.module.TripMemberCheckResponse;
import com.qunar.im.base.module.UserConfigData;
import com.qunar.im.base.module.VideoDataResponse;
import com.qunar.im.base.module.VideoSetting;
import com.qunar.im.base.module.WorkWorldAtShowResponse;
import com.qunar.im.base.module.WorkWorldDeleteResponse;
import com.qunar.im.base.module.WorkWorldDetailsCommenData;
import com.qunar.im.base.module.WorkWorldDetailsCommentHotData;
import com.qunar.im.base.module.WorkWorldItem;
import com.qunar.im.base.module.WorkWorldMyReply;
import com.qunar.im.base.module.WorkWorldNewCommentBean;
import com.qunar.im.base.module.WorkWorldNoticeHistoryResponse;
import com.qunar.im.base.module.WorkWorldResponse;
import com.qunar.im.base.module.WorkWorldSearchShowResponse;
import com.qunar.im.base.module.WorkWorldSingleResponse;
import com.qunar.im.base.protocol.HttpRequestCallback;
import com.qunar.im.base.protocol.HttpUrlConnectionHandler;
import com.qunar.im.base.protocol.ProgressRequestListener;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.structs.MessageStatus;
import com.qunar.im.base.structs.SetMucVCardData;
import com.qunar.im.base.structs.TransitFileJSON;
import com.qunar.im.base.structs.TransitSoundJSON;
import com.qunar.im.base.transit.IUploadRequestComplete;
import com.qunar.im.base.transit.PbImageMessageQueue;
import com.qunar.im.base.transit.UploadImageRequest;
import com.qunar.im.base.util.AESTools;
import com.qunar.im.base.util.BinaryUtil;
import com.qunar.im.base.util.ChatTextHelper;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DataCenter;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.FileUtils;
import com.qunar.im.base.util.IMUserDefaults;
import com.qunar.im.base.util.InternDatas;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.MessageUtils;
import com.qunar.im.base.util.PhoneInfoUtils;
import com.qunar.im.base.util.graphics.ImageUtils;
import com.qunar.im.base.util.graphics.MyDiskCache;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.enums.MessageState;
import com.qunar.im.core.manager.IMDatabaseManager;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.core.manager.IMMessageManager;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.core.manager.LoginComplateManager;
import com.qunar.im.core.services.FileProgressRequestBody;
import com.qunar.im.core.services.FileProgressResponseBody;
import com.qunar.im.core.services.QtalkHttpService;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.core.utils.GlobalConfigManager;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;


/**
 * Created by hubin on 2017/8/29.
 */

public class HttpUtil {
    private static String TAG = "PB-HTTP";

    /**
     * 设置群名片
     *
     * @param datas
     * @param callback
     */
    public static void setMucVCard(List<SetMucVCardData> datas, final ProtocolCallback.UnitCallback<SetMucVCardResult> callback) {
        try {
            if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
                callback.doFailure();
                return;
            }
            final String requestUrl = String.format("%s/muc/set_muc_vcard.qunar?u=%s&k=%s&p=android&v=%s",
                    QtalkNavicationService.getInstance().getHttpUrl(),
                    IMLogicManager.getInstance().getMyself().getUser(),
                    IMLogicManager.getInstance().getRemoteLoginKey(),
                    GlobalConfigManager.getAppVersion()
            );
            String postBody = JsonUtils.getGson().toJson(datas);
            Logger.i("请求的地址:" + requestUrl + ";请求的参数" + postBody);

            HttpUrlConnectionHandler.executePostJson(requestUrl, postBody, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    SetMucVCardResult result = null;
                    try {

                        String resultString = Protocol.parseStream(response);
                        result = JsonUtils.getGson().fromJson(resultString, SetMucVCardResult.class);
                        Logger.i("设置群组的返回值:" + resultString);

                    } catch (Exception e) {
                        callback.doFailure();
                        Logger.i(TAG, e);
                        LogUtil.e(TAG, "error", e);
                    }
                    callback.onCompleted(result);
                }

                @Override
                public void onFailure(Exception e) {
                    callback.doFailure();
                }
            });
        } catch (Exception e) {
            callback.doFailure();
            Logger.i(TAG, e);
        }
    }

    /**
     * 异步获取导航
     *
     * @param url
     * @param callback
     */
    public static void getServerConfigAsync(final String url, final ProtocolCallback.UnitCallback<NavConfigResult> callback) {
        StringBuilder stringBuilder = new StringBuilder(url);

        Protocol.addBasicParamsOnHead(stringBuilder);
        HttpUrlConnectionHandler.executeGet(stringBuilder.toString(), new HttpRequestCallback() {
            @Override
            public void onComplete(final InputStream response) {
                try {
                    String resultString = Protocol.parseStream(response);
                    if (resultString != null) {
                        JSONObject jsonObject = new JSONObject(resultString);
                        if (jsonObject.has("ret") && !Boolean.valueOf(jsonObject.get("ret").toString())) {
                            callback.onFailure("");
                            return;
                        }
                        final NavConfigResult result = JsonUtils.getGson().fromJson(resultString, NavConfigResult.class);
                        if (TextUtils.isEmpty(result.hosts)) {
                            if (result == null) {
                                callback.onFailure("");
                                return;
                            }
                            callback.onCompleted(result);
                        } else {
                            if (url.contains("debug=true")) {
                                if (result.hosts.contains("?")) {
                                    result.hosts += "&debug=true";
                                } else {
                                    result.hosts += "?debug=true";
                                }
                            }
                            getHashBaseAddress(result, new ProtocolCallback.UnitCallback<NavConfigResult>() {
                                @Override
                                public void onCompleted(NavConfigResult baseResult) {
                                    if (baseResult == null) {
                                        callback.onFailure("");
                                        return;
                                    }
                                    result.baseaddess = baseResult.baseaddess;
                                    callback.onCompleted(result);
                                }

                                @Override
                                public void onFailure(String errMsg) {
                                    callback.onFailure("");
                                }
                            });
                        }
                    } else {
                        callback.onFailure("");
                    }
                } catch (Exception e) {
                    LogUtil.e(TAG, "error", e);
                    callback.onFailure("");
                }
            }

            @Override
            public void onFailure(Exception e) {
                Logger.i("getServerConfigAsync->>" + url + e.getLocalizedMessage());
                callback.onFailure("");
            }
        });
    }

    public static void getHashBaseAddress(final NavConfigResult result, final ProtocolCallback.UnitCallback<NavConfigResult> callback) {
        if (result == null || TextUtils.isEmpty(result.hosts)) {
            callback.onFailure("");
            return;
        }

        StringBuilder stringBuilder = new StringBuilder(result.hosts);
        Protocol.addBasicParamsOnHead(stringBuilder);
        Logger.i("二级导航请求地址加参数:" + stringBuilder.toString());
        HttpUrlConnectionHandler.executeGetSync(stringBuilder.toString(), new HttpRequestCallback() {
            @Override
            public void onComplete(InputStream response) {
                try {
                    String resultString = Protocol.parseStream(response);
                    if (resultString != null) {
                        Logger.i("二级导航请求完成:" + resultString);
                        NavConfigResult baseResult = JsonUtils.getGson().fromJson(resultString, NavConfigResult.class);
                        callback.onCompleted(baseResult);
                    } else {
                        callback.onFailure("");
                    }
                } catch (Exception e) {
                    LogUtil.e(TAG, "error", e);
                    callback.onFailure("");
                }
            }

            @Override
            public void onFailure(Exception e) {
                Logger.i("getHashBaseAddress->>" + result.hosts + e.getLocalizedMessage());
                callback.onFailure("");
            }
        });
    }

    /**
     * 获取consult类型消息历史记录
     *
     * @param chatType
     * @param xmppid
     * @param realJid
     * @param timestamp
     * @param count
     * @param num
     * @param direction
     * @param callback
     */

    public static void getConsultChatOfflineMsg(final String chatType, final String xmppid, final String realJid,
                                                long timestamp, final int count, final int num, int direction,
                                                final ProtocolCallback.UnitCallback<List<IMMessage>> callback) {

        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        String str = "";
        if (CommonConfig.isQtalk) {
            str = "%s/api/getcmsginfo2?v=%s&p=android&u=%s&k=%s&d=%s&from=%s&to=%s&timestamp=%s&virtual=%s&limitnum=%s&direction=%s";
        } else {
            str = "%s/getcmsginfo4?v=%s&p=android&u=%s&k=%s&d=%s&from=%s&to=%s&timestamp=%s&virtual=%s&limitnum=%s&direction=%s";
        }
        final String requestUrl = String.format(str,
                QtalkNavicationService.getInstance().getHttpHost(),//http:~~~
                GlobalConfigManager.getAppVersion(),
                IMLogicManager.getInstance().getMyself().getUser(),
                IMLogicManager.getInstance().getRemoteLoginKey(),
                QtalkNavicationService.getInstance().getXmppdomain(),//ejbahost1
                IMLogicManager.getInstance().getMyself().getUser(),
                QtalkStringUtils.parseLocalpart(realJid),
                timestamp,
                QtalkStringUtils.parseLocalpart(xmppid),
                num,
                direction

                //版本
        );

        HttpUrlConnectionHandler.executeGet(requestUrl, new HttpRequestCallback() {
            @Override
            public void onComplete(InputStream response) {
                try {
                    String resultString = Protocol.parseStream(response);

                    Logger.i("请求的云端数据返回:" + resultString);
//                    if(CommonConfig.isQtalk){
                    JSONObject jsonBody = new JSONObject(resultString);
                    if (jsonBody != null) {
                        boolean result = jsonBody.getBoolean("ret");
                        if (result) {
                            JSONArray msgList = jsonBody.getJSONArray("data");
                            if (msgList != null && msgList.length() > 0) {
                                String user = IMLogicManager.getInstance().getMyself().getUser();
                                IMDatabaseManager.getInstance().bulkInsertChatHistory(msgList, user, MessageState.didRead);
                                List<IMMessage> messageList;
                                if ((ConversitionType.MSG_TYPE_CONSULT + "").equals(chatType)) {
                                    messageList = IMDatabaseManager.getInstance().SelectHistoryChatMessage(xmppid, xmppid, count, num);
                                } else if ((ConversitionType.MSG_TYPE_CONSULT_SERVER + "").equals(chatType)) {
                                    messageList = IMDatabaseManager.getInstance().SelectHistoryChatMessage(xmppid, realJid, count, num);
                                } else {
                                    messageList = IMDatabaseManager.getInstance().SelectHistoryChatMessage(xmppid, realJid, count, num);
                                }
//                                    messageList = IMDatabaseManager.getInstance().SelectHistoryChatMessage(xmppid,realJid, count, num);
                                callback.onCompleted(messageList);
                            } else {
                                callback.onCompleted(null);
                            }
                        }
                    }
//                    }else{
//                        JSONArray jsonArray = new JSONArray(resultString);
//                        if(jsonArray.length()>0){
//                            IMDatabaseManager.getInstance().bulkInsertChatHistory(jsonArray, null, MessageState.didRead);
//                            List<IMMessage> messageList;
//                            if ((ConversitionType.MSG_TYPE_CONSULT + "").equals(chatType)) {
//                                messageList = IMDatabaseManager.getInstance().SelectHistoryChatMessage(xmppid, xmppid, count, num);
//                            } else if ((ConversitionType.MSG_TYPE_CONSULT_SERVER + "").equals(chatType)) {
//                                messageList = IMDatabaseManager.getInstance().SelectHistoryChatMessage(xmppid, realJid, count, num);
//                            } else {
//                                messageList = IMDatabaseManager.getInstance().SelectHistoryChatMessage(xmppid, realJid, count, num);
//                            }
////                                    messageList = IMDatabaseManager.getInstance().SelectHistoryChatMessage(xmppid,realJid, count, num);
//                            callback.onCompleted(messageList);
//                        }else{
//                            callback.onCompleted(null);
//                        }
//                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.doFailure();
            }
        });
    }

    public static void getJsonConsultChatOfflineMsg(final String chatType, final String xmppId, final String realJid, long timestamp,
                                                    final int count, final int num, final int direction, final boolean isSave, boolean isInclude, final ProtocolCallback.UnitCallback<List<IMMessage>> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");
            final String requestUrl = String.format("%s/qtapi/getconsultmsgs.qunar?v=%s&p=%s&u=%s&k=%s&d=%s",
                    QtalkNavicationService.getInstance().getJavaUrl(),
                    GlobalConfigManager.getAppVersion(),
                    "Android",
                    IMLogicManager.getInstance().getMyself().getUser(),
                    IMLogicManager.getInstance().getRemoteLoginKey(),
                    QtalkNavicationService.getInstance().getXmppdomain()

            );

            JSONObject inputObject = new JSONObject();

            inputObject.put("from", CurrentPreference.getInstance().getUserid());
            inputObject.put("to", QtalkStringUtils.parseId(realJid));
            inputObject.put("virtual", QtalkStringUtils.parseId(xmppId));
            inputObject.put("direction", direction + "");
            inputObject.put("time", timestamp);
            inputObject.put("domain", QtalkNavicationService.getInstance().getXmppdomain());
            inputObject.put("num", num);
            inputObject.put("f", "t");
            inputObject.put("fhost", QtalkNavicationService.getInstance().getXmppdomain());
            inputObject.put("thost", QtalkStringUtils.parseDomain(xmppId));
            if (isInclude) {
                inputObject.put("include", "t");
            }
            Logger.i("请求地址:" + requestUrl + ";请求参数:" + inputObject + ":cookie:" + cookie);
            QtalkHttpService.asyncPostJson(requestUrl, inputObject, cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        Logger.i("单聊接口返回:" + jsonObject.toString());
                        BaseJsonResult baseJson = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                        if (baseJson.ret) {
                            JSONChatHistorys chatJson = JsonUtils.getGson().fromJson(jsonObject.toString(), JSONChatHistorys.class);
                            if (isSave) {


                                if (chatJson.getData().size() > 0) {
                                    IMDatabaseManager.getInstance().bulkInsertChatHistoryFroJson(chatJson.getData(), CurrentPreference.getInstance().getPreferenceUserId(), true);
                                    LoginComplateManager.updateMessageStateNoticeServer();
                                    List<IMMessage> messageList;
                                    messageList = IMDatabaseManager.getInstance().SelectHistoryChatMessage(xmppId, realJid, count, num);

                                    callback.onCompleted(messageList);
                                } else {
                                    callback.onCompleted(null);
                                }
                            } else {
                                if (chatJson.getData().size() > 0) {
//                                    new String();
                                    List<IMMessage> messageList = ConnectionUtil.getInstance().ParseHistoryChatData(chatJson.getData(), CurrentPreference.getInstance().getPreferenceUserId());
                                    callback.onCompleted(messageList);
                                } else {
                                    callback.onCompleted(null);
                                }
                            }


                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * json版本获取俩人对话历史消息
     *
     * @param chatType
     * @param xmppId
     * @param realJid
     * @param timestamp
     * @param count
     * @param num
     * @param isSave
     * @param direction
     * @param isInclude
     * @param callback
     */
    public static void getJsonSingleChatOfflineMsg(final String chatType, final String xmppId, final String realJid, long timestamp,
                                                   final int count, final int num, final boolean isSave, final int direction, final boolean isInclude, final ProtocolCallback.UnitCallback<List<IMMessage>> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

            String requestUrl;
            JSONObject inputObject = new JSONObject();
            //// TODO: 2017/12/26  json版本上翻历史 和拉取历史记录未写
            if ((ConversitionType.MSG_TYPE_HEADLINE + "").equals(chatType)) {
                requestUrl = String.format("%s/qtapi/get_system_msgs.qunar", QtalkNavicationService.getInstance().getJavaUrl());
//

            } else {
                requestUrl = String.format("%s/qtapi/getmsgs.qunar", QtalkNavicationService.getInstance().getJavaUrl());

            }
            inputObject.put("from", CurrentPreference.getInstance().getUserid());
            inputObject.put("to", QtalkStringUtils.parseId(xmppId));
            inputObject.put("direction", direction + "");
            inputObject.put("time", timestamp);
            inputObject.put("domain", QtalkNavicationService.getInstance().getXmppdomain());
            inputObject.put("num", num);
            inputObject.put("f", "t");
            inputObject.put("fhost", QtalkNavicationService.getInstance().getXmppdomain());
            inputObject.put("thost", QtalkStringUtils.parseDomain(xmppId));
            if (isInclude) {
                inputObject.put("include", "t");
            }

//


            QtalkHttpService.asyncPostJson(requestUrl, inputObject, cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        Logger.i("单聊接口返回:" + jsonObject.toString());
                        BaseJsonResult baseJson = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                        if (baseJson.ret) {
                            JSONChatHistorys chatJson = JsonUtils.getGson().fromJson(jsonObject.toString(), JSONChatHistorys.class);
                            if (isSave) {
                                if (chatJson.getData().size() > 0) {
                                    IMDatabaseManager.getInstance().bulkInsertChatHistoryFroJson(chatJson.getData(), CurrentPreference.getInstance().getPreferenceUserId(), true);
                                    LoginComplateManager.updateMessageStateNoticeServer();
                                    List<IMMessage> messageList;
                                    if ((ConversitionType.MSG_TYPE_CONSULT + "").equals(chatType)) {
                                        messageList = IMDatabaseManager.getInstance().SelectHistoryChatMessage(xmppId, xmppId, count, num);
                                    } else {
                                        messageList = IMDatabaseManager.getInstance().SelectHistoryChatMessage(xmppId, realJid, count, num);
                                    }

                                    callback.onCompleted(messageList);
                                } else {
                                    callback.onCompleted(null);
                                }
                            } else {
                                if (chatJson.getData().size() > 0) {
//                                    new String();
                                    List<IMMessage> messageList = ConnectionUtil.getInstance().ParseHistoryChatData(chatJson.getData(), CurrentPreference.getInstance().getPreferenceUserId());
                                    callback.onCompleted(messageList);
                                } else {
                                    callback.onCompleted(null);
                                }
                            }


                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取单人聊天云端记录
     *
     * @param chattype  qtalk的headline系统通知消息 也算单人的二人会话 但是接口地址不一样 所有根绝chattype区分一下接口地址
     * @param xmppid
     * @param realJid
     * @param timestamp
     * @param num
     * @param direction
     * @param callback
     */
    public static void getSingleChatOfflineMsg(final String chattype, final String xmppid, final String realJid, long timestamp, final int count, final int num, final int direction,
                                               final ProtocolCallback.UnitCallback<List<IMMessage>> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }


        GetSingleConvRecord singleConvRecord = new GetSingleConvRecord();

//        singleConvRecord.from = IMLogicManager.getInstance().getMyself().getUser();
//        singleConvRecord.to = QtalkStringUtils.parseLocalpart(realJid);
//        singleConvRecord.from_host = IMLogicManager.getInstance().getMyself().getDomain();
//        singleConvRecord.to_host = QtalkStringUtils.parseDomain(realJid);
//        singleConvRecord.timestamp = timestamp;
//        singleConvRecord.limitnum = num;
//        singleConvRecord.direction = String.valueOf(direction);
//        singleConvRecord.u = IMLogicManager.getInstance().getMyself().getUser();
//        singleConvRecord.k = IMLogicManager.getInstance().getRemoteLoginKey();
        try {
//            LogUtil.d("debug", "get");
            String url = "";
            String requestUrl = "";
            if ((ConversitionType.MSG_TYPE_HEADLINE + "").equals(chattype)) {
                singleConvRecord.from = IMLogicManager.getInstance().getMyself().getUser();
                singleConvRecord.to = QtalkStringUtils.parseLocalpart(realJid);
                singleConvRecord.from_host = IMLogicManager.getInstance().getMyself().getDomain();
                singleConvRecord.to_host = QtalkStringUtils.parseDomain(realJid);
                singleConvRecord.timestamp = timestamp;
                singleConvRecord.limitnum = num;
                singleConvRecord.direction = String.valueOf(direction);
                singleConvRecord.u = IMLogicManager.getInstance().getMyself().getUser();
                singleConvRecord.k = IMLogicManager.getInstance().getRemoteLoginKey();
                requestUrl = String.format("%s/get_notice_msg?u=%s&k=%s&p=android&v=%s",
                        QtalkNavicationService.getInstance().getHttpHost(),
                        IMLogicManager.getInstance().getMyself().getUser(),
                        IMLogicManager.getInstance().getRemoteLoginKey(),
                        GlobalConfigManager.getAppVersion());
            } else if ((ConversitionType.MSG_TYPE_CONSULT + "").equals(chattype)) {
                singleConvRecord.from = IMLogicManager.getInstance().getMyself().getUser();
                singleConvRecord.to = QtalkStringUtils.parseLocalpart(xmppid);
                singleConvRecord.from_host = IMLogicManager.getInstance().getMyself().getDomain();
                singleConvRecord.to_host = QtalkStringUtils.parseDomain(xmppid);
                singleConvRecord.timestamp = timestamp;
                singleConvRecord.limitnum = num;
                singleConvRecord.direction = String.valueOf(direction);
                singleConvRecord.u = IMLogicManager.getInstance().getMyself().getUser();
                singleConvRecord.k = IMLogicManager.getInstance().getRemoteLoginKey();
                requestUrl = String.format("%s/domain/get_msgs?u=%s&k=%s&p=android&v=%sd=%s",
                        QtalkNavicationService.getInstance().getHttpHost(),
                        IMLogicManager.getInstance().getMyself().getUser(),
                        IMLogicManager.getInstance().getRemoteLoginKey(),
                        GlobalConfigManager.getAppVersion(),
                        QtalkNavicationService.getInstance().getXmppdomain()
                );
//                requestUrl = String.format("%s/domain/get_msgs?u=%s&k=%s&p=android&v=%s",
            } else {
                singleConvRecord.from = IMLogicManager.getInstance().getMyself().getUser();
                singleConvRecord.to = QtalkStringUtils.parseLocalpart(realJid);
                singleConvRecord.from_host = IMLogicManager.getInstance().getMyself().getDomain();
                singleConvRecord.to_host = QtalkStringUtils.parseDomain(realJid);
                singleConvRecord.timestamp = timestamp;
                singleConvRecord.limitnum = num;
                singleConvRecord.direction = String.valueOf(direction);
                singleConvRecord.u = IMLogicManager.getInstance().getMyself().getUser();
                singleConvRecord.k = IMLogicManager.getInstance().getRemoteLoginKey();
                requestUrl = String.format("%s/domain/get_msgs?u=%s&k=%s&p=android&v=%s",
                        QtalkNavicationService.getInstance().getHttpHost(),
                        IMLogicManager.getInstance().getMyself().getUser(),
                        IMLogicManager.getInstance().getRemoteLoginKey(),
                        GlobalConfigManager.getAppVersion()
                );
            }

//            final String requestUrl = String.format(String.valueOf(ConversitionType.MSG_TYPE_HEADLINE).equals(chattype)?"%s/get_notice_msg?u=%s&k=%s&p=android&v=%s":"%s/domain/get_msgs?u=%s&k=%s&p=android&v=%s",
//                    QtalkNavicationService.getInstance().getHttpHost(),
//                    IMLogicManager.getInstance().getMyself().getUser(),
//                    IMLogicManager.getInstance().getRemoteLoginKey(),
//                    GlobalConfigManager.getAppVersion()
//            );


            String jsonParams = JsonUtils.getGson().toJson(singleConvRecord);
//            String url = Protocol.makeGetUri(QtalkNavicationService.getInstance().getHttpHost(),
//                    Protocol.configuration.getHttpPort(), queryString.toString(), true);
            HttpUrlConnectionHandler.executePostJson(requestUrl, jsonParams, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    try {
                        String resultString = Protocol.parseStream(response);
                        JSONObject jsonBody = new JSONObject(resultString);
                        Logger.i("请求的云端数据返回:" + resultString);
                        if (jsonBody != null) {
                            boolean result = jsonBody.getBoolean("ret");
                            if (result) {
                                JSONArray msgList = jsonBody.getJSONArray("data");
                                if (msgList != null && msgList.length() > 0) {
                                    String user = IMLogicManager.getInstance().getMyself().getUser();
                                    IMDatabaseManager.getInstance().bulkInsertChatHistory(msgList, user, MessageState.didRead);
                                    List<IMMessage> messageList;
                                    if ((ConversitionType.MSG_TYPE_CONSULT + "").equals(chattype)) {
                                        messageList = IMDatabaseManager.getInstance().SelectHistoryChatMessage(xmppid, xmppid, count, num);
                                    } else {
                                        messageList = IMDatabaseManager.getInstance().SelectHistoryChatMessage(xmppid, realJid, count, num);
                                    }
//                                    messageList = IMDatabaseManager.getInstance().SelectHistoryChatMessage(xmppid,realJid, count, num);
                                    callback.onCompleted(messageList);
                                } else {
                                    callback.onCompleted(null);
                                }
                            }
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Exception e) {
                    callback.doFailure();
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG, "error", e);
        }
    }

    public static void getJsonMultiChatOffLineMsg(final String mucId, final String realJid, long timestamp, final int count, final int num, int direction, final boolean isSave, boolean isInclude, final ProtocolCallback.UnitCallback<List<IMMessage>> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");
            String requestUrl = String.format("%s/qtapi/getmucmsgs.qunar", QtalkNavicationService.getInstance().getJavaUrl());
            JSONObject inputObject = new JSONObject();

            inputObject.put("muc", QtalkStringUtils.parseId(mucId));
            inputObject.put("direction", direction + "");
            inputObject.put("num", num);
            inputObject.put("time", timestamp);
            inputObject.put("domain", QtalkNavicationService.getInstance().getXmppdomain());
            if (isInclude) {
                inputObject.put("include", "t");
            }
            QtalkHttpService.asyncPostJson(requestUrl, inputObject, cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        Logger.i("单聊接口返回:" + jsonObject.toString());
                        BaseJsonResult baseJson = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                        if (baseJson.ret) {
                            JSONMucHistorys chatJson = JsonUtils.getGson().fromJson(jsonObject.toString(), JSONMucHistorys.class);
                            if (isSave) {
                                if (chatJson.getData().size() > 0) {
                                    IMDatabaseManager.getInstance().bulkInsertGroupHistoryFroJson(chatJson.getData(), CurrentPreference.getInstance().getPreferenceUserId(), true);
                                    List<IMMessage> messageList;
                                    messageList = IMDatabaseManager.getInstance().SelectHistoryGroupChatMessage(mucId, realJid, count, num);
                                    callback.onCompleted(messageList);
                                } else {
                                    callback.onCompleted(null);
                                }
                            } else {
                                if (chatJson.getData().size() > 0) {
                                    List<IMMessage> noSaveMessageList = ConnectionUtil.getInstance().ParseHistoryGroupChatData(chatJson.getData(), CurrentPreference.getInstance().getPreferenceUserId());
                                    callback.onCompleted(noSaveMessageList);
                                } else {
                                    callback.onCompleted(null);
                                }

                            }


                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * xmpp版本获取历史记录
     *
     * @param xmppid
     * @param realjid
     * @param timestamp
     * @param count
     * @param num
     * @param direction
     * @param callback
     */
    public static void getMultiChatOfflineMsg(final String xmppid, final String realjid, long timestamp, final int count, final int num, int direction, final ProtocolCallback.UnitCallback<List<IMMessage>> callback) {
//        StringBuilder queryString = new StringBuilder("domain/get_muc_msg?");
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
//        Protocol.addBasicParamsOnHead(queryString);
        try {
            GetGroupOfflineMessageRequest request = new GetGroupOfflineMessageRequest();
            request.muc_name = QtalkStringUtils.parseLocalpart(realjid);
            request.timestamp = String.valueOf(timestamp);
            request.limitnum = String.valueOf(num);
            request.direction = String.valueOf(direction);
            request.domain = QtalkStringUtils.parseDomain(realjid);
            request.u = IMLogicManager.getInstance().getMyself().getUser();
            request.k = IMLogicManager.getInstance().getRemoteLoginKey();
//            request.u = CurrentPreference.getInstance().getUserId();
//            request.k = CommonConfig.verifyKey;

            final String requestUrl = String.format("%s/domain/get_muc_msg?u=%s&k=%s&p=android&v=%s",
                    QtalkNavicationService.getInstance().getHttpHost(),
                    IMLogicManager.getInstance().getMyself().getUser(),
                    IMLogicManager.getInstance().getRemoteLoginKey(),
                    GlobalConfigManager.getAppVersion()
            );

//
//            String url =Protocol.makeGetUri(Protocol.configuration.getHttpUrl(),
//                    Protocol.configuration.getHttpPort(), queryString.toString(), true);
            String json = JsonUtils.getGson().toJson(request);
            HttpUrlConnectionHandler.executePostJson(requestUrl, json, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
//                    String resultString = Protocol.parseStream(response);
//                    JSONObject jsonBody = new JSONObject(resultString);
//                    if (jsonBody != null) {
//                        boolean result = jsonBody.getBoolean("ret");
//                        if (result) {
//                            JSONArray msgList = jsonBody.getJSONArray("data");
//                            if (msgList != null && msgList.length() > 0) {
//                                String user = IMLogicManager.getInstance().getMyself().getUser();
//                                IMDatabaseManager.getInstance().bulkInsertChatHistory(msgList, user, MessageState.didRead);
//                                List<IMMessage> messageList = IMDatabaseManager.getInstance().SelectHistoryChatMessage(to, count, num);
//                                callback.onCompleted(messageList);
//                            }
//                        }
//                    }
//                    GroupChatOfflineResult orignalResult = null;
                    try {
                        String resultString = Protocol.parseStream(response);
                        JSONObject jsonBody = new JSONObject(resultString);
                        if (jsonBody != null) {
                            boolean result = jsonBody.getBoolean("ret");
                            if (result) {
                                JSONObject data = jsonBody.getJSONObject("data");
                                if (data != null && data.length() > 0) {
                                    JSONArray dataList = data.getJSONArray("Msg");
                                    long readMarkT = data.optLong("Time");
                                    if (dataList != null && dataList.length() > 0) {
                                        String myNickName = IMMessageManager.getInstance().getGroupNickNameByGroupId(xmppid);
                                        IMDatabaseManager.getInstance().bulkInsertGroupHistory(dataList, xmppid, myNickName, readMarkT, MessageState.didRead, "");
                                        List<IMMessage> messageList = IMDatabaseManager.getInstance().SelectHistoryGroupChatMessage(xmppid, realjid, count, num);
                                        callback.onCompleted(messageList);
                                    } else {
                                        callback.onCompleted(null);
                                    }
                                }
                            }
                        }
//                        orignalResult = JsonUtils.getGson().fromJson(resultString, GroupChatOfflineResult.class);
                        Logger.i("群聊云端返回值:" + resultString);
                    } catch (Exception e) {
                        LogUtil.e(TAG, "error", e);
                    }
//                    if (orignalResult != null) {
//                        callback.onCompleted(orignalResult);
//                    }

                }

                @Override
                public void onFailure(Exception e) {
                    callback.doFailure();
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG, "error", e);
        }
    }


    /**
     * 拿到全部组织架构人员
     *
     * @param version
     * @param callback
     */
    public static void getIncrementUsers(int version, final ProtocolCallback.UnitCallback<IncrementUsersResult> callback) {
        try {
            if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
                callback.doFailure();
                return;
            }

            final String requestUrl = String.format("%s/update/getUpdateUsers.qunar?u=%s&k=%s&p=qtadr&v=%s&d=%s",
                    QtalkNavicationService.getInstance().getHttpUrl(),
                    IMLogicManager.getInstance().getMyself().getUser(),
                    IMLogicManager.getInstance().getRemoteLoginKey(),
                    QunarIMApp.getQunarIMApp().getVersion(),
                    QtalkNavicationService.getInstance().getXmppdomain()
            );

            String param = "{\"version\":" + version + "}";
            Logger.i("获取组织架构人员地址:" + requestUrl + ";参数:" + param);
            HttpUrlConnectionHandler.executePostJson(requestUrl, param, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    IncrementUsersResult result = null;
                    try {
                        String resultString = Protocol.parseStream(response);
                        result = JsonUtils.getGson().fromJson(resultString, IncrementUsersResult.class);
                    } catch (Exception e) {
                        LogUtil.e(TAG, "error", e);
                    }
                    callback.onCompleted(result);
                }

                @Override
                public void onFailure(Exception e) {
                    callback.doFailure();
                }
            });
        } catch (Exception e) {
            Logger.i("获取组织架构人员异常:" + e.getLocalizedMessage());
        }
    }

    /**
     * 上传并发送语音
     *
     * @param message
     * @param filePath
     * @param json
     */
    public static void uploadAndSendVoice(final IMMessage message, final String filePath, final TransitSoundJSON json, final boolean isFromChatRoom) {

        addEncryptMessageInfo(message.getToID(), message, ProtoMessageOuterClass.MessageType.MessageTypeVoice_VALUE);

        IMDatabaseManager.getInstance().InsertChatMessage(message, false);
        IMDatabaseManager.getInstance().InsertIMSessionList(message, false);

        final UploadImageRequest request = new UploadImageRequest();
        request.filePath = filePath;
        request.FileType = UploadImageRequest.FILE;
        request.id = message.getId();
        request.requestComplete = new IUploadRequestComplete() {
            @Override
            public void onRequestComplete(String id, UploadImageResult result) {
                if (result != null && !TextUtils.isEmpty(result.httpUrl)) {
                    Logger.i("上传语音成功  msg url = " + result.httpUrl);
                    json.HttpUrl = result.httpUrl;
                    message.setBody(JsonUtils.getGson().toJson(json));
                    addEncryptMessageInfo(message.getToID(), message, ProtoMessageOuterClass.MessageType.MessageTypeVoice_VALUE);
//                    bodyExtension.setId(message.getId());
//                    bodyExtension.setMsgType(String.valueOf(MessageType.VOICE_MESSAGE));
//                    bodyExtension.setMaType(MachineType.MachineTypeAndroid);
//                    bodyExtension.setExtendInfo(message.getBody());
//                    if (message.getMsgType() == MessageType.READ_TO_DESTROY_MESSAGE) {
//                        handleSnapMessage(message, bodyExtension);
//                        bodyExtension.setMsgType(String.valueOf(MessageType.READ_TO_DESTROY_MESSAGE));
//                    }
                    // TODO: 2017/8/22  InternDatas.sendingLine这个缓存逻辑没细看
                    if (isFromChatRoom) {
                        ConnectionUtil.getInstance().sendGroupTextOrEmojiMessage(message);
                    } else {
                        ConnectionUtil.getInstance().sendTextOrEmojiMessage(message);
                    }
//                    if (!sendMessage(message, bodyExtension)) {
//                        message.setReadState(MessageStatus.STATUS_FAILED);
//                        InternDatas.sendingLine.remove(message.getId());
//                        updateDbOnSuccess(message, false);
//                    }
                } else {
                    Logger.i("上传语音失败  msg id = " + message.getId());
                    message.setMessageState(MessageStatus.LOCAL_STATUS_FAILED);
                    IMDatabaseManager.getInstance().UpdateChatStateMessage(message, false);
                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Send_Failed, message.getMessageId());
                    InternDatas.sendingLine.remove(message.getId());
//                    updateDbOnSuccess(message, false);
                }
            }

            @Override
            public void onError(String msg) {
                Logger.i("上传语音失败  msg id = " + msg);
                message.setMessageState(MessageStatus.LOCAL_STATUS_FAILED);
                IMDatabaseManager.getInstance().UpdateChatStateMessage(message, false);
                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Send_Failed, message.getMessageId());
                InternDatas.sendingLine.remove(message.getId());
//                updateDbOnSuccess(message, false);
            }
        };
        CommonUploader.getInstance().setUploadImageRequest(request);
    }

    /**
     * 上传并发送文件
     *
     * @param message
     * @param filePath
     * @param fileName
     */
    public static void uploadAndSendFile(final IMMessage message, final String filePath, final String fileName, final boolean isFromChatRoom, final SendCallback callback) {

        addEncryptMessageInfo(message.getToID(), message, ProtoMessageOuterClass.MessageType.MessageTypeFile_VALUE);

        IMDatabaseManager.getInstance().InsertChatMessage(message, false);
        IMDatabaseManager.getInstance().InsertIMSessionList(message, false);

        final UploadImageRequest request = new UploadImageRequest();
        request.filePath = filePath;
        request.id = message.getId();
        request.FileType = UploadImageRequest.FILE;
        request.progressRequestListener = new ProgressRequestListener() {
            @Override
            public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
                LogUtil.i("lex uploadAndSendImage  progress = " + (int) (bytesWritten * 100 / contentLength) + "   done = " + done);
                callback.updataProgress((int) (bytesWritten * 100 / contentLength), done);
            }
        };
        request.requestComplete = new IUploadRequestComplete() {
            @Override
            public void onRequestComplete(String id, UploadImageResult result) {
                if (result != null && !TextUtils.isEmpty(result.httpUrl)) {
                    Logger.i("上传文件成功  msg url = " + result.httpUrl);
                    String fileSize = FileUtils.getFormatFileSize(filePath);
                    TransitFileJSON json = new TransitFileJSON(result.httpUrl, fileName, fileSize, message.getId(), "");
                    message.setBody(JsonUtils.getGson().toJson(json));
                    message.setExt(JsonUtils.getGson().toJson(json));
                    addEncryptMessageInfo(message.getToID(), message, ProtoMessageOuterClass.MessageType.MessageTypeFile_VALUE);
//                    bodyExtension.setId(message.getId());
//                    bodyExtension.setMsgType(String.valueOf(MessageType.FILE_MESSAGE));
//                    bodyExtension.setMaType(MachineType.MachineTypeAndroid);
//                    bodyExtension.setExtendInfo(message.getBody());
//                    if (message.getMsgType() == MessageType.READ_TO_DESTROY_MESSAGE) {
//                        handleSnapMessage(message, bodyExtension);
//                        bodyExtension.setMsgType(String.valueOf(MessageType.READ_TO_DESTROY_MESSAGE));
//                    }
                    if (isFromChatRoom) {

                        ConnectionUtil.getInstance().sendGroupTextOrEmojiMessage(message);
                    } else {

                        ConnectionUtil.getInstance().sendTextOrEmojiMessage(message);
                    }
                    // TODO: 2017/8/22 缓存逻辑还没细看
//                    if (!sendMessage(message, bodyExtension)) {
//                        message.setReadState(MessageStatus.STATUS_FAILED);
//                        InternDatas.sendingLine.remove(message.getId());
//                        updateDbOnSuccess(message, false);
//                    }
                } else {
                    Logger.i("上传文件失败  msg = " + message.toString());
                    message.setMessageState(MessageStatus.LOCAL_STATUS_FAILED);
                    IMDatabaseManager.getInstance().UpdateChatStateMessage(message, false);
                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Send_Failed, message.getMessageId());
                    InternDatas.sendingLine.remove(message.getId());
//                    updateDbOnSuccess(message, false);
                }
            }

            @Override
            public void onError(String msg) {
                message.setMessageState(MessageStatus.LOCAL_STATUS_FAILED);
                IMDatabaseManager.getInstance().UpdateChatStateMessage(message, false);
                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Send_Failed, message.getMessageId());
                InternDatas.sendingLine.remove(message.getId());
//                updateDbOnSuccess(message, false);
                Logger.i("上传文件失败  msg = " + msg);
            }
        };
        CommonUploader.getInstance().setUploadImageRequest(request);
    }

    public static void getUserPushConfig(final ProtocolCallback.UnitCallback<GroupPushDataBean> callback) {
        String q_ckey = Protocol.getCKEY();
        if (TextUtils.isEmpty(q_ckey)) return;
        final Map<String, String> cookie = new HashMap<>();
        cookie.put("Cookie", "q_ckey=" + q_ckey + ";p_user=" + CurrentPreference.getInstance().getUserid());
        String url = String.format("%s/qtapi/push/get_subscribe.qunar?p=qtadr&v=%s&u=%s&k=%s",
                QtalkNavicationService.getInstance().getJavaUrl(),
                QunarIMApp.getQunarIMApp().getVersion(),
                IMLogicManager.getInstance().getMyself().getUser(),
                IMLogicManager.getInstance().getRemoteLoginKey()
        );
        JSONObject params = new JSONObject();
        try {
            params.put("host", QtalkNavicationService.getInstance().getXmppdomain());
            params.put("username", IMLogicManager.getInstance().getMyself().getUser());
        } catch (Exception e) {

        }
        QtalkHttpService.asyncPostJson(url, params, cookie, new QtalkHttpService.CallbackJson() {
            @Override
            public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                Logger.i("getPushConfig增加排错判断:" + jsonObject.toString());
                BaseJsonResult result = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);

                if (result.ret) {
                    GroupPushDataBean bean = JsonUtils.getGson().fromJson(jsonObject.toString(), GroupPushDataBean.class);
                    if (bean.isRet()) {
                        callback.onCompleted(bean);
                    } else {
                        callback.onFailure("");
                    }
                } else {
                    callback.onFailure("");
                }


            }

            @Override
            public void onFailure(Call call, Exception e) {

            }
        });

    }

    /**
     * 获取绑定用户
     */
    public static void getBindUser() {
        String q_ckey = Protocol.getCKEY();
        if (TextUtils.isEmpty(q_ckey)) return;
        final Map<String, String> cookie = new HashMap<>();
        cookie.put("Cookie", "q_ckey=" + q_ckey + ";p_user=" + CurrentPreference.getInstance().getUserid());
        final String requestUrl = String.format("%s/qtapi/common/collection/get.qunar",
                QtalkNavicationService.getInstance().getJavaUrl()
        );
        QtalkHttpService.asyncGetJson(requestUrl, cookie, new QtalkHttpService.CallbackJson() {
            @Override
            public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                BaseJsonResult data = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                if (data.ret) {
                    CollectionUserBindData bindData = JsonUtils.getGson().fromJson(jsonObject.toString(), CollectionUserBindData.class);
                    if (bindData.getData() != null && bindData.getData().size() > 0) {
                        IMDatabaseManager.getInstance().InsertCollectionUserByData(bindData);
                        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Collection_Bind_User_Update, "success");
                    }
                }


                Logger.i("getBindUser:" + jsonObject.toString());
            }

            @Override
            public void onFailure(Call call, Exception e) {

            }
        });
    }

    /**
     * 注册push，通知服务器
     */
    public static void registPush(final String key, final String name) {
        try {
            if (TextUtils.isEmpty(CurrentPreference.getInstance().getVerifyKey())) {
                return;
            }
            String username = CurrentPreference.getInstance().getUserid();
            StringBuilder params = new StringBuilder("push/qtapi/token/setpersonmackey.qunar?username=");
            params.append(username).append("&domain=")
                    .append(QtalkNavicationService.getInstance().getXmppdomain())
                    .append("&os=android&version=").append(Protocol.VERSIONVALUE);
            params.append("&mac_key=").append(key);
            params.append("&platname=").append(name);
            params.append("&pkgname=").append(CommonConfig.globalContext.getApplicationInfo().packageName);
            Protocol.addBasicParamsOnHead(params);
            String rooturl = QtalkNavicationService.getInstance().getJavaUrl();
            String url = Protocol.makeGetUri(rooturl, QtalkNavicationService.getInstance().getHttpPort(), params.toString(), true);
            Logger.i(TAG + " 注册push  registPush:" + url);

            String q_ckey = Protocol.getCKEY();
            if (TextUtils.isEmpty(q_ckey)) return;
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";p_user=" + CurrentPreference.getInstance().getUserid());

            HttpUrlConnectionHandler.executeGet(url, cookie, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    try {
                        String resultString = Protocol.parseStream(response);
//                        JSONObject jb = new JSONObject(resultString);
//                        if (jb.optInt("errcode") == 5000) {
//                            Protocol.i++;
//                            if (Protocol.i > 3) {
//                                Protocol.i = 0;
//                                return;
//                            }
//                            IMLogicManager.getInstance().clearAndGetRemoteLoginKey();
//                            registPush(key, name);
//                        }else {
//                            Protocol.i = 0;
//                        }
                        Logger.i("注册push成功：" + key + "  " + name + "   " + resultString);
                        LogUtil.d(TAG, "result" + resultString);
                    } catch (Exception e) {
                        Logger.i("注册push失败：" + key + "  " + name + "   " + e.getMessage());
                        LogUtil.e(TAG, "error", e);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Logger.i("注册push失败：" + key + "  " + name + "   " + e.getMessage());
                }
            });
        } catch (Exception e) {
            Logger.i("注册push失败：" + key + "  " + name + "   " + e.getMessage());
            LogUtil.e(TAG, "error", e);
        }
    }

    /**
     * 注销push
     */

    public static void unregistPushinfo(final String uuid, final String name, final boolean again) {
        try {
            if (TextUtils.isEmpty(CurrentPreference.getInstance().getVerifyKey())) {
                return;
            }
            String muUsernam = CurrentPreference.getInstance().getUserid();
            StringBuilder params = new StringBuilder("push/qtapi/token/delpersonmackey.qunar?username=");
            params.append(muUsernam).append("&domain=")
                    .append(QtalkNavicationService.getInstance().getXmppdomain())
                    .append("&os=android&version=").append(Protocol.VERSIONVALUE);
            params.append("&mac_key=").append(uuid);
            params.append("&platname=").append(name);
            Protocol.addBasicParamsOnHead(params);
            String rooturl = QtalkNavicationService.getInstance().getJavaUrl();
            String url = Protocol.makeGetUri(rooturl, QtalkNavicationService.getInstance().getHttpPort(), params.toString(), true);
            Logger.i("清除push info：" + url);
            LogUtil.d(TAG, "upload:" + url);

            String q_ckey = Protocol.getCKEY();
            if (TextUtils.isEmpty(q_ckey)) return;
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";p_user=" + CurrentPreference.getInstance().getUserid());
            HttpUrlConnectionHandler.executeGet(url, cookie, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    try {
                        String resultString = Protocol.parseStream(response);
                        JSONObject jb = new JSONObject(resultString);
//                        if (jb.optInt("errcode") == 5000 && again) {
//                            Protocol.j++;
//                            if (Protocol.j > 3) {
//                                Protocol.j = 0;
//                                return;
//                            }
//                            IMLogicManager.getInstance().clearAndGetRemoteLoginKey();
//                            unregistPushinfo(uuid, name, false);
//                        }else {
//                            Protocol.j = 0;
//                        }
                        Logger.i("清除push成功：" + uuid + resultString);
                        PhoneInfoUtils.delUniqueID();
                        LogUtil.d(TAG, "result" + resultString);
                    } catch (Exception e) {
                        LogUtil.e(TAG, "error", e);
                        Logger.i("清除push异常：" + ((e != null) ? e.getMessage() : ""));
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Logger.i("清除push失败：" + ((e != null) ? e.getMessage() : ""));
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG, "error", e);
        }
    }


    /**
     * 设置单个群是否收到push
     *
     * @param mucJid
     * @param status
     * @param callback
     */
    public static void setGroupPushSettings(final String mucJid, final int status, final ProtocolCallback.UnitCallback<Boolean> callback) {
        try {
            String muUsernam = CurrentPreference.getInstance().getUserid();
            StringBuilder params = new StringBuilder("push/qtapi/token/setgroupnotification.qunar?username=");
            params.append(muUsernam).append("&domain=")
                    .append(QtalkNavicationService.getInstance().getXmppdomain())
                    .append("&os=android&version=").append(Protocol.VERSIONVALUE);
            params.append("&muc_name=").append(QtalkStringUtils.parseId(mucJid));
            params.append("&subscribe_flag=").append(status);
            params.append("&muc_domain=").append(QtalkStringUtils.parseDomain(mucJid));
            Protocol.addBasicParamsOnHead(params);
            String rooturl = QtalkNavicationService.getInstance().getJavaUrl();
            String url = Protocol.makeGetUri(rooturl, QtalkNavicationService.getInstance().getHttpPort(), params.toString(), true);
            LogUtil.d(TAG, "upload:" + url);

            String q_ckey = Protocol.getCKEY();
            if (TextUtils.isEmpty(q_ckey)) return;
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";p_user=" + CurrentPreference.getInstance().getUserid());
            Logger.i("设置push开关 url：" + url);

            HttpUrlConnectionHandler.executeGet(url, cookie, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    try {
                        String resultString = Protocol.parseStream(response);
                        BaseJsonResult result = JsonUtils.getGson().fromJson(resultString, BaseJsonResult.class);
                        if (result.ret) {
                            callback.onCompleted(true);
                        } else {
                            callback.onCompleted(false);
                        }
//
                        Logger.i("设置群push开关 成功 group：" + mucJid + " status：" + status + " resultString：" + resultString);
                        LogUtil.d(TAG, "result" + resultString);
                    } catch (Exception e) {
                        LogUtil.e(TAG, "error", e);
                        callback.onFailure("");
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    callback.onFailure("");
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG, "error", e);
            callback.onFailure("");
        }
    }

    /**
     * 设置push开关
     *
     * @param index  标志位
     * @param status 状态 1开 0关
     */
    public static void setPushMsgSettings(final int index, final int status, final ProtocolCallback.UnitCallback<Boolean> callback) {
        try {
            String muUsernam = CurrentPreference.getInstance().getUserid();
            StringBuilder params = new StringBuilder("push/qtapi/token/setmsgsettings.qunar?username=");
            params.append(muUsernam).append("&domain=")
                    .append(QtalkNavicationService.getInstance().getXmppdomain())
                    .append("&os=android&version=").append(Protocol.VERSIONVALUE);
            params.append("&index=").append(index);
            params.append("&status=").append(status);
            Protocol.addBasicParamsOnHead(params);
            String rooturl = QtalkNavicationService.getInstance().getJavaUrl();
            String url = Protocol.makeGetUri(rooturl, QtalkNavicationService.getInstance().getHttpPort(), params.toString(), true);
            LogUtil.d(TAG, "upload:" + url);

            String q_ckey = Protocol.getCKEY();
            if (TextUtils.isEmpty(q_ckey)) return;
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";p_user=" + CurrentPreference.getInstance().getUserid());
            Logger.i("设置push开关 url：" + url);

            HttpUrlConnectionHandler.executeGet(url, cookie, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    try {
                        String resultString = Protocol.parseStream(response);
                        BaseJsonResult result = JsonUtils.getGson().fromJson(resultString, BaseJsonResult.class);
                        if (result.ret) {
                            callback.onCompleted(true);
                        } else {
                            callback.onCompleted(false);
                        }

                        Logger.i("设置push开关 成功 index：" + index + " status：" + status + " resultString：" + resultString);
                        LogUtil.d(TAG, "result" + resultString);
                    } catch (Exception e) {
                        LogUtil.e(TAG, "error", e);
                        callback.onFailure("");
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    callback.onFailure("");
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG, "error", e);
            callback.onFailure("");
        }
    }

    /**
     * 获取push消息设置
     */
    public static void getPushMsgSettings(final ProtocolCallback.UnitCallback<PushSettingResponseBean> callback) {
        try {

            String muUsernam = CurrentPreference.getInstance().getUserid();
            StringBuilder params = new StringBuilder("push/qtapi/token/getmsgsettings.qunar?username=");
            params.append(muUsernam).append("&domain=")
                    .append(QtalkNavicationService.getInstance().getXmppdomain())
                    .append("&os=android&version=").append(Protocol.VERSIONVALUE);
            Protocol.addBasicParamsOnHead(params);
            String rooturl = QtalkNavicationService.getInstance().getJavaUrl();
            if (TextUtils.isEmpty(rooturl)) {
                callback.onFailure("");
                return;
            }
            String url = Protocol.makeGetUri(rooturl, QtalkNavicationService.getInstance().getHttpPort(), params.toString(), true);
            LogUtil.d(TAG, "upload:" + url);

            String q_ckey = Protocol.getCKEY();
            if (TextUtils.isEmpty(q_ckey)) return;
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";p_user=" + CurrentPreference.getInstance().getUserid());

            HttpUrlConnectionHandler.executeGet(url, cookie, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    try {
                        String resultString = Protocol.parseStream(response);
                        PushSettingResponseBean bean = JsonUtils.getGson().fromJson(resultString, PushSettingResponseBean.class);
                        Logger.i("getPushMsgSettings onCompleted s = " + resultString);
                        callback.onCompleted(bean);
                        Logger.i("获取push getPushMsgSettings成功  resultString：" + resultString);
                        LogUtil.d(TAG, "result" + resultString);
                    } catch (Exception e) {
                        Logger.i("getPushMsgSettings error s = " + e.getMessage());
                        LogUtil.e(TAG, "error", e);
                        callback.onFailure("");
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Logger.i("getPushMsgSettings onFailure s = " + e.getMessage());
                    callback.onFailure("");
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG, "error", e);
            callback.onFailure("");
        }
    }

    /**
     * 检查网络健康状况
     */
    public static void checkHealth(final ProtocolCallback.UnitCallback<Boolean> callback) {
        String url = "http://qim.qunar.com/healthcheck.html";
        Headers headers = Headers.of(new HashMap<String, String>());
        QtalkHttpService.asyncGetSimple(url, headers, 2, new QtalkHttpService.SimpleCallback() {
            @Override
            public void onSussecss(boolean isSussecss) {
                callback.onCompleted(isSussecss);
            }

            @Override
            public void onFailure() {
                callback.onFailure("");
            }
        });
    }

    public static void getFoundConfiguration(final ProtocolCallback.UnitCallback<FoundConfiguration> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");
            Map<String, Object> map = new HashMap<>();
            map.put("platform", "Android");
            map.put("version", QunarIMApp.getQunarIMApp().getVersion());

            String requestUrl = QtalkNavicationService.getInstance().getFoundConfigUrl();
            if (TextUtils.isEmpty(requestUrl)) {
                return;
            }
            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(map), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        Logger.i("新版个人配置接口set:" + jsonObject.toString());
                        BaseJsonResult baseJson = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                        if (baseJson.ret) {
                            FoundConfiguration nrc = JsonUtils.getGson().fromJson(jsonObject.toString(), FoundConfiguration.class);
                            IMDatabaseManager.getInstance().insertFoundConfigurationToCacheData(jsonObject.toString());
                            callback.onCompleted(nrc);


                        } else {
                            callback.onFailure("");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    Logger.i("新版个人配置接口set" + e.getMessage());
                    callback.onFailure("");

                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public interface SendCallback {
        void send();

        void updataProgress(int progress, boolean isDone);
    }

    /**
     * 上传并发送视频文件
     *
     * @param file
     */
    public static void uploadAndSendVideo(final String file, final IMMessage message, final SendCallback callback, final boolean isFromChatRoom) {
        final UploadImageRequest request = new UploadImageRequest();
        final String firstFramPath = FileUtils.getFristFrameOfFile(file);

        if (!TextUtils.isEmpty(firstFramPath)) {
            //获取video信息展示在页面，防止失败无法展示消息
            BitmapFactory.Options option = ImageUtils.getImageSize(firstFramPath);
            final int width = option.outWidth;
            final int height = option.outHeight;

            final String fileName = file.substring(file.lastIndexOf("/") + 1);
            final VideoMessageResult videoInfo = MessageUtils.getBasicVideoInfo(file);
            videoInfo.FileName = fileName;
            videoInfo.ThumbUrl = firstFramPath;
            videoInfo.FileUrl = file;
//            videoInfo.Width = String.valueOf(width);
//            videoInfo.Height = String.valueOf(height);

            message.setBody(firstFramPath);
            message.setExt(JsonUtils.getGson().toJson(videoInfo));

            //发送消息，更新页面
            callback.send();
            addEncryptMessageInfo(message.getToID(), message, ProtoMessageOuterClass.MessageType.MessageTypeSmallVideo_VALUE);

            IMDatabaseManager.getInstance().InsertChatMessage(message, false);
            IMDatabaseManager.getInstance().InsertIMSessionList(message, false);

            request.filePath = firstFramPath;
            request.FileType = UploadImageRequest.IMAGE;
            request.id = message.getId();
            request.requestComplete = new IUploadRequestComplete() {
                @Override
                public void onRequestComplete(String id, UploadImageResult result) {
                    if (result != null && !TextUtils.isEmpty(result.httpUrl)) {
                        Logger.i("上传视频截图成功  msg url = " + result.httpUrl);
                        File targetFile = MyDiskCache.getFile(QtalkStringUtils.addFilePathDomain(result.httpUrl, true));
                        File sourceFile = new File(firstFramPath);
                        FileUtils.copy(sourceFile, targetFile);
                        sendVideoFile(file, message, result.httpUrl, width, height, callback, isFromChatRoom);
                    } else {
                        Logger.i("上传视频第一帧失败  msg id = " + id);
                        message.setMessageState(MessageStatus.LOCAL_STATUS_FAILED);
                        IMDatabaseManager.getInstance().UpdateChatStateMessage(message, false);
                        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Send_Failed, message.getMessageId());
                    }
                }

                @Override
                public void onError(String msg) {
                    Logger.i("上传视频第一帧失败  msg url = " + msg);
                    message.setMessageState(MessageStatus.LOCAL_STATUS_FAILED);
                    IMDatabaseManager.getInstance().UpdateChatStateMessage(message, false);
                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Send_Failed, message.getMessageId());
                    InternDatas.sendingLine.remove(message.getId());
//                    updateDbOnSuccess(message, false);
                }
            };
            CommonUploader.getInstance().setUploadImageRequest(request);
        } else {
            File f = new File(file);
            if (f.exists()) {
                sendVideoFile(file, message, "", 0, 0, callback, isFromChatRoom);
            }
        }
    }

    /**
     * 上传视频的文件本身
     */
    protected static void sendVideoFile(final String sourceFilePath, final IMMessage message, final String frameUrl, int videoW, int videoH, final SendCallback callback, final boolean isFromChatRoom) {
        final String fileName = sourceFilePath.substring(sourceFilePath.lastIndexOf("/") + 1);
//        message.setBody("发送视频:" + fileName);
//        message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeSmallVideo_VALUE);
        final VideoMessageResult videoInfo = MessageUtils.getBasicVideoInfo(sourceFilePath);
        videoInfo.FileName = fileName;
        videoInfo.ThumbUrl = frameUrl;
        videoInfo.FileUrl = sourceFilePath;
//        videoInfo.Height = String.valueOf(videoH);
//        videoInfo.Width = String.valueOf(videoW);

//        File path = Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_MOVIES);
//        File videoFile = new File(path, fileName);
//        File sourceFile = new File(sourceFilePath);
//        String uploadPath = sourceFilePath;
//        if (sourceFile.renameTo(videoFile)) {
//            uploadPath = videoFile.getPath();
//        }
//        message.setBody("发送了一段视频");
//        message.setExt(JsonUtils.getGson().toJson(videoInfo));

//        final BodyExtension bodyExtension = new BodyExtension();
//        if (snapStatus) {
//            handleSnapMessage(message, bodyExtension);
//        }

//        updateDbOnSuccess(message, true);
        final UploadImageRequest request = new UploadImageRequest();
        request.filePath = sourceFilePath;
        request.FileType = UploadImageRequest.FILE;
        request.id = message.getId();
        request.progressRequestListener = new ProgressRequestListener() {
            @Override
            public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
                callback.updataProgress((int) (bytesWritten / contentLength), done);
            }
        };
        request.requestComplete = new IUploadRequestComplete() {
            @Override
            public void onRequestComplete(String id, UploadImageResult result) {
                if (result != null && !TextUtils.isEmpty(result.httpUrl)) {
                    Logger.i("上传视频文件成功  msg url = " + result.httpUrl);
//                    message.setBody("发送了一段视频. [obj type=\"url\" value=\"" +
//                            QtalkStringUtils.addFilePathDomain(result.httpUrl) + "\"]");
                    videoInfo.FileUrl = result.httpUrl;

//                    bodyExtension.setId(message.getId());
//                    bodyExtension.setMsgType(String.valueOf(MessageType.VIDEO_MESSAGE));
//                    bodyExtension.setMaType(MachineType.MachineTypeAndroid);
//                    bodyExtension.setExtendInfo(JsonUtils.getGson().toJson(videoInfo));
                    String jsonVideo = JsonUtils.getGson().toJson(videoInfo);
                    message.setBody(jsonVideo);
                    message.setExt(jsonVideo);
                    addEncryptMessageInfo(message.getToID(), message, ProtoMessageOuterClass.MessageType.MessageTypeSmallVideo_VALUE);
//                    if (message.getMsgType() == MessageType.READ_TO_DESTROY_MESSAGE) {
//                        handleSnapMessage(message, bodyExtension);
//                        bodyExtension.setMsgType(String.valueOf(MessageType.READ_TO_DESTROY_MESSAGE));
//                    }
                    if (isFromChatRoom) {
                        ConnectionUtil.getInstance().sendGroupTextOrEmojiMessage(message);
                    } else {
                        ConnectionUtil.getInstance().sendTextOrEmojiMessage(message);
                    }
//                    if (!sendMessage(message, bodyExtension)) {
//                        message.setReadState(MessageStatus.STATUS_FAILED);
//                        InternDatas.sendingLine.remove(message.getId());
//                        updateDbOnSuccess(message, false);
//                    }
                } else {
                    message.setMessageState(MessageStatus.LOCAL_STATUS_FAILED);
                    IMDatabaseManager.getInstance().UpdateChatStateMessage(message, false);
                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Send_Failed, message.getMessageId());
                    InternDatas.sendingLine.remove(message.getId());
//                    updateDbOnSuccess(message, false);
                }
            }

            @Override
            public void onError(String msg) {
                Logger.i("上传视频文件失败  msg url = " + msg);
                message.setMessageState(MessageStatus.LOCAL_STATUS_FAILED);
                IMDatabaseManager.getInstance().UpdateChatStateMessage(message, false);
                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Send_Failed, message.getMessageId());
                callback.send();
                InternDatas.sendingLine.remove(message.getId());
//                updateDbOnSuccess(message, false);
            }
        };
        CommonUploader.getInstance().setUploadImageRequest(request);
    }

    /**
     * 上传并发送图片
     *
     * @param message
     * @param filePath
     * @param toid
     * @param callback
     */
    public static void uploadAndSendImage(final IMMessage message, String filePath, final String toid, final SendCallback callback) {
        final File origalFile = new File(filePath);

        BitmapFactory.Options option = ImageUtils.getImageSize(origalFile.getPath());
        final int width = option.outWidth;
        final int height = option.outHeight;
        //webp转jpg发送
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
//        String type = options.outMimeType;
//        if(type.equals("image/webp")){
//            File tempFile = new File(origalFile.getParent(),
//                    UUID.randomUUID().toString()+".jpg");
//            ImageUtils.saveBitmap(bmp, tempFile);
//        }
        final String img = ChatTextHelper.textToImgHtml("file://" + origalFile.getAbsolutePath(), width, height);
        message.setBody(img);
        message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypePhoto_VALUE);
//        final BodyExtension bodyExtension = new BodyExtension();
//        if (snapStatus) {
//            handleSnapMessage(message, bodyExtension);
//        }

        callback.send();

        addEncryptMessageInfo(toid, message, ProtoMessageOuterClass.MessageType.MessageTypePhoto_VALUE);

        IMDatabaseManager.getInstance().InsertChatMessage(message, false);
        IMDatabaseManager.getInstance().InsertIMSessionList(message, false);

        final PbImageMessageQueue.ImgMsgPacket packet = new PbImageMessageQueue.ImgMsgPacket();
        packet.key = toid;
        if (PbImageMessageQueue.packetMap.containsKey(toid)) {
            PbImageMessageQueue.ImgMsgPacket header = PbImageMessageQueue.packetMap.get(toid);
            while (header.next != null) {
                header = header.next;
            }
            header.next = packet;
        } else {
            packet.isFirst = true;
            PbImageMessageQueue.packetMap.put(packet.key, packet);
        }
//        updateDbOnSuccess(message, true);
        final UploadImageRequest request = new UploadImageRequest();
        request.filePath = origalFile.getPath();
        request.FileType = UploadImageRequest.IMAGE;
        request.id = message.getId();
        request.progressRequestListener = new ProgressRequestListener() {
            @Override
            public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
                LogUtil.i("lex uploadAndSendImage  progress = " + (int) (bytesWritten * 100 / contentLength) + "   done = " + done);
                callback.updataProgress((int) (bytesWritten * 100 / contentLength), done);
            }
        };
        request.requestComplete = new IUploadRequestComplete() {
            @Override
            public void onRequestComplete(String id, UploadImageResult result) {

                if (result != null && !TextUtils.isEmpty(result.httpUrl)) {
                    Logger.i("上传图片成功  msg url = " + result.httpUrl);
//                    IMMessage newMsg = BeanCloneUtil.cloneTo(message);
                    File file = MyDiskCache.getFile(QtalkStringUtils.addFilePathDomain(
                            result.httpUrl, true));
                    FileUtils.copy(origalFile, file);
                    String origal = ChatTextHelper.textToImgHtml(result.httpUrl, width, height);
                    message.setBody(origal);
//
                    addEncryptMessageInfo(toid, message, ProtoMessageOuterClass.MessageType.MessageTypePhoto_VALUE);
//                    bodyExtension.setId(message.getId());
//                    bodyExtension.setMsgType(String.valueOf(MessageType.IMAGE_MESSAGE));
//                    bodyExtension.setMaType(MachineType.MachineTypeAndroid);
//                    bodyExtension.setExtendInfo(message.getBody());
//                    if (message.getMsgType() == MessageType.READ_TO_DESTROY_MESSAGE) {
//                        handleSnapMessage(message, bodyExtension);
//                        bodyExtension.setMsgType(String.valueOf(MessageType.READ_TO_DESTROY_MESSAGE));
//                    }
                    packet.message = message;
//                    packet.bodyExtension = bodyExtension;
                    packet.approveSend();
                } else {
                    Logger.i("上传图片失败  msg url = " + message.getId());
                    packet.removed();
                    message.setMessageState(MessageStatus.LOCAL_STATUS_FAILED);
                    IMDatabaseManager.getInstance().UpdateChatStateMessage(message, false);
                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Send_Failed, message.getMessageId());
                    InternDatas.sendingLine.remove(message.getId());
//                    updateDbOnSuccess(message, false);
                }
            }

            @Override
            public void onError(String msg) {
                Logger.i("上传图片失败  msg url = " + msg);
                packet.removed();
                message.setMessageState(MessageStatus.LOCAL_STATUS_FAILED);
                IMDatabaseManager.getInstance().UpdateChatStateMessage(message, false);
                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Send_Failed, message.getMessageId());
                InternDatas.sendingLine.remove(message.getId());
//                updateDbOnSuccess(message, false);
            }

        };

        CommonUploader.getInstance().setUploadImageRequest(request);
    }


//    /**
//     * 上传并发送图片
//     *
//     * @param message
//     * @param callback
//     */
//    public static void uploadAndSendImage(final IMMessage message,final SendCallback callback) {
//        String img = message.getBody();
//        message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypePhoto_VALUE);
//
//        callback.send();
//
//        addEncryptMessageInfo(message.getToID(), message, img, ProtoMessageOuterClass.MessageType.MessageTypePhoto_VALUE);
//
//        IMDatabaseManager.getInstance().InsertChatMessage(message);
//        IMDatabaseManager.getInstance().InsertIMSessionList(message);
//
//        final PbImageMessageQueue.ImgMsgPacket packet = new PbImageMessageQueue.ImgMsgPacket();
//        packet.key = message.getToID();
//        if (PbImageMessageQueue.packetMap.containsKey(message.getToID())) {
//            PbImageMessageQueue.ImgMsgPacket header = PbImageMessageQueue.packetMap.get(message.getToID());
//            while (header.next != null) {
//                header = header.next;
//            }
//            header.next = packet;
//        } else {
//            packet.isFirst = true;
//            PbImageMessageQueue.packetMap.put(packet.key, packet);
//        }
////        updateDbOnSuccess(message, true);
//        final UploadImageRequest request = new UploadImageRequest();
//        request.filePath = origalFile.getPath();
//        request.FileType = UploadImageRequest.IMAGE;
//        request.id = message.getId();
//        request.progressRequestListener = new ProgressRequestListener() {
//            @Override
//            public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
//                LogUtil.i("lex uploadAndSendImage  progress = " + (int) (bytesWritten * 100 / contentLength) + "   done = " + done);
//                callback.updataProgress((int) (bytesWritten * 100 / contentLength), done);
//            }
//        };
//        request.requestComplete = new IUploadRequestComplete() {
//            @Override
//            public void onRequestComplete(String id, UploadImageResult result) {
//
//                if (result != null && !TextUtils.isEmpty(result.httpUrl)) {
//                    Logger.i("上传图片成功  msg url = " + result.httpUrl);
////                    IMMessage newMsg = BeanCloneUtil.cloneTo(message);
//                    File file = MyDiskCache.getFile(QtalkStringUtils.addFilePathDomain(
//                            result.httpUrl));
//                    FileUtils.copy(origalFile, file);
//                    String origal = ChatTextHelper.textToImgHtml(result.httpUrl, width, height);
//                    message.setBody(origal);
////
//                    addEncryptMessageInfo(toid, message, origal, ProtoMessageOuterClass.MessageType.MessageTypePhoto_VALUE);
////                    bodyExtension.setId(message.getId());
////                    bodyExtension.setMsgType(String.valueOf(MessageType.IMAGE_MESSAGE));
////                    bodyExtension.setMaType(MachineType.MachineTypeAndroid);
////                    bodyExtension.setExtendInfo(message.getBody());
////                    if (message.getMsgType() == MessageType.READ_TO_DESTROY_MESSAGE) {
////                        handleSnapMessage(message, bodyExtension);
////                        bodyExtension.setMsgType(String.valueOf(MessageType.READ_TO_DESTROY_MESSAGE));
////                    }
//                    packet.message = message;
////                    packet.bodyExtension = bodyExtension;
//                    packet.approveSend();
//                } else {
//                    Logger.i("上传图片失败  msg url = " + message.getId());
//                    packet.removed();
//                    message.setReadState(MessageStatus.STATUS_FAILED);
//                    IMDatabaseManager.getInstance().UpdateChatStateMessage(message);
//                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Send_Failed, message.getMessageId());
//                    InternDatas.sendingLine.remove(message.getId());
////                    updateDbOnSuccess(message, false);
//                }
//            }
//
//            @Override
//            public void onError(String msg) {
//                Logger.i("上传图片失败  msg url = " + msg);
//                packet.removed();
//                message.setReadState(MessageStatus.STATUS_FAILED);
//                IMDatabaseManager.getInstance().UpdateChatStateMessage(message);
//                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Send_Failed, message.getMessageId());
//                InternDatas.sendingLine.remove(message.getId());
////                updateDbOnSuccess(message, false);
//            }
//
//        };
//
//        CommonUploader.getInstance().setUploadImageRequest(request);
//    }


    /**
     * qchat获取店铺背后真实id
     */
    public static void getQchatCousltId(String shopId, final ProtocolCallback.UnitCallback<QchatCousltIdBean> callback) {
        StringBuilder queryString = new StringBuilder(QtalkNavicationService.getInstance().getQcadminHost() + "/api/seat/judgmentOrRedistribution.json?shopId=" + shopId + "&userQName=" + CurrentPreference.getInstance().getFullName()
                + "&seatQName=gunjern9357");
        try {
            HttpUrlConnectionHandler.executeGet(queryString.toString(), new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    try {
                        String resultString = Protocol.parseStream(response);
                        Logger.i("qchathou:" + resultString);
                        BaseJsonResult bean = JsonUtils.getGson().fromJson(resultString, BaseJsonResult.class);
                        if (bean.ret) {
                            QchatCousltIdBean qchatCousltIdBean = JsonUtils.getGson().fromJson(resultString, QchatCousltIdBean.class);
                            callback.onCompleted(qchatCousltIdBean);
                        }
//                        else{

//                        }

                        return;
                    } catch (Exception e) {
                        LogUtil.e(TAG, "error", e);
                    }
                    QchatCousltIdBean errQchat = new QchatCousltIdBean();
                    errQchat.setRet(false);
                    callback.onCompleted(errQchat);


                }

                @Override
                public void onFailure(Exception e) {
                    QchatCousltIdBean errQchat = new QchatCousltIdBean();
                    errQchat.setRet(false);
                    callback.onCompleted(errQchat);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取店铺客服列表
     *
     * @param jid      shop323@ejabhost2
     * @param callback
     */
    public static void getSeatList(String jid, final ProtocolCallback.UnitCallback<SeatList> callback) {
        if (TextUtils.isEmpty(jid)) {
            return;
        }
        String url = QtalkNavicationService.getInstance().getQcadminHost() + "/seat/getSeatList.json?shopId=" + jid;
        HttpUrlConnectionHandler.executeGet(url, new HttpRequestCallback() {
            @Override
            public void onComplete(InputStream response) {
                String resultString = Protocol.parseStream(response);
                SeatList seats = JsonUtils.getGson().fromJson(resultString, SeatList.class);
                callback.onCompleted(seats);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure("");
            }
        });

    }

    /**
     * 群组云端消息记录搜索
     *
     * @param chatName
     * @param timestamp
     * @param num
     * @param direction
     * @param callback
     */
    public static void getMultiChatOfflineMsg(String chatName, long timestamp, int num, int direction, final ProtocolCallback.UnitCallback<GroupChatOfflineResult> callback) {
        StringBuilder queryString = new StringBuilder("domain/get_muc_msg?");
        if (TextUtils.isEmpty(com.qunar.im.protobuf.common.CurrentPreference.getInstance().getVerifyKey())) {
            callback.doFailure();
            return;
        }
        Protocol.addBasicParamsOnHead(queryString);
        try {
            GetGroupOfflineMessageRequest request = new GetGroupOfflineMessageRequest();
            request.muc_name = QtalkStringUtils.parseLocalpart(chatName);
            request.timestamp = String.valueOf(timestamp);
            request.limitnum = String.valueOf(num);
            request.direction = String.valueOf(direction);
            request.domain = QtalkStringUtils.parseDomain(chatName);
            request.u = CurrentPreference.getInstance().getUserid();
            request.k = CommonConfig.verifyKey;

            String url = Protocol.makeGetUri(QtalkNavicationService.getInstance().getHttpHost(),
                    QtalkNavicationService.getInstance().getHttpPort(), queryString.toString(), true);
            String json = JsonUtils.getGson().toJson(request);
            HttpUrlConnectionHandler.executePostJson(url, json, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    GroupChatOfflineResult orignalResult = null;
                    try {
                        String resultString = Protocol.parseStream(response);
                        orignalResult = JsonUtils.getGson().fromJson(resultString, GroupChatOfflineResult.class);
                    } catch (Exception e) {
                        LogUtil.e(TAG, "error", e);
                    }
                    if (orignalResult != null) {
                        callback.onCompleted(orignalResult);
                    }

                }

                @Override
                public void onFailure(Exception e) {
                    callback.doFailure();
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG, "error", e);
        }
    }

    /**
     * 设置用户配置
     *
     * @param jsonDatas
     * @param callback
     */
    public static void setRemoteConfig(List<RemoteConfig.ConfigItem> jsonDatas, final ProtocolCallback.UnitCallback<RemoteConfig> callback) {
        try {
            StringBuilder queryString = new StringBuilder("conf/set_person?");
            if (TextUtils.isEmpty(com.qunar.im.protobuf.common.CurrentPreference.getInstance().getVerifyKey())) {
                if (callback != null) callback.doFailure();
                return;
            }
            Protocol.addBasicParamsOnHead(queryString);
            String jsonParams = JsonUtils.getGson().toJson(jsonDatas);
            LogUtil.d(TAG, jsonParams);
            String url = Protocol.makeGetUri(QtalkNavicationService.getInstance().getHttpHost(),
                    QtalkNavicationService.getInstance().getHttpPort(), queryString.toString(), true);
            HttpUrlConnectionHandler.executePostJson(url, jsonParams, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    RemoteConfig result = null;
                    try {
                        String resultString = Protocol.parseStream(response);
                        LogUtil.d(TAG, resultString);
                        result = JsonUtils.getGson().fromJson(resultString, RemoteConfig.class);
                    } catch (Exception e) {
                        LogUtil.e(TAG, "error", e);
                    }
                    if (callback != null)
                        callback.onCompleted(result);
                }

                @Override
                public void onFailure(Exception e) {
                    if (callback != null) callback.doFailure();
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG, "error", e);
        }
    }

    public static void requestGet(String url, final ProtocolCallback.UnitCallback<String> callback) {
        StringBuilder sb = new StringBuilder(url);
        Protocol.addBasicParamsOnHead(sb);
        HttpUrlConnectionHandler.executeGet(sb.toString(), new HttpRequestCallback() {
            @Override
            public void onComplete(InputStream response) {
                String resultString = Protocol.parseStream(response);
                callback.onCompleted(resultString);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure("");
            }
        });

    }


    /**
     * 获取用户配置
     *
     * @param jsonDatas
     * @param callback
     */
    public static void getRemoteConfig(List<RemoteConfig.ConfigItem> jsonDatas, final ProtocolCallback.UnitCallback<RemoteConfig> callback) {
        try {
            StringBuilder queryString = new StringBuilder("conf/get_person?");
            if (TextUtils.isEmpty(com.qunar.im.protobuf.common.CurrentPreference.getInstance().getVerifyKey())) {
                if (callback != null) callback.doFailure();
                return;
            }
            Protocol.addBasicParamsOnHead(queryString);
            String jsonParams = JsonUtils.getGson().toJson(jsonDatas);
            LogUtil.d(TAG, jsonParams);
            String url = Protocol.makeGetUri(QtalkNavicationService.getInstance().getHttpHost(),
                    QtalkNavicationService.getInstance().getHttpPort(), queryString.toString(), true);
            HttpUrlConnectionHandler.executePostJson(url, jsonParams, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    RemoteConfig result = null;
                    try {

                        String resultString = Protocol.parseStream(response);
                        Logger.i("用户配置:" + resultString);
                        result = JsonUtils.getGson().fromJson(resultString, RemoteConfig.class);
                    } catch (Exception e) {
                        Logger.e("获取用户配置异常:" + e.getLocalizedMessage());
                    }
                    callback.onCompleted(result);
                }

                @Override
                public void onFailure(Exception e) {
                    callback.doFailure();
                }
            });
        } catch (Exception e) {
            Logger.e("获取用户配置异常:" + e.getLocalizedMessage());
        }
    }


    /**
     * 设置用户配置
     *
     * @param callback
     */
    public static void setUserConfig(UserConfigData userConfigData, final ProtocolCallback.UnitCallback<NewRemoteConfig> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        int version = ConnectionUtil.getInstance().selectUserConfigVersion();
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");
            String requestUrl = String.format("%s//configuration/setclientconfig.qunar", QtalkNavicationService.getInstance().getHttpUrl());
            JSONObject inputObject = new JSONObject();


            userConfigData.setUsername(CurrentPreference.getInstance().getUserid());
            userConfigData.setHost(QtalkNavicationService.getInstance().getXmppdomain());
            userConfigData.setOperate_plat("android");
            userConfigData.setVersion(version);
            userConfigData.setResource(CurrentPreference.getInstance().getResource());
            String json = JsonUtils.getGson().toJson(userConfigData);
            Logger.i("新版个人配置接口set 参数:" + JsonUtils.getGson().toJson(userConfigData));
            QtalkHttpService.asyncPostJsonforString(requestUrl, json, cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        Logger.i("新版个人配置接口set:" + jsonObject.toString());
                        BaseJsonResult baseJson = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                        if (baseJson.ret) {
                            NewRemoteConfig nrc = JsonUtils.getGson().fromJson(jsonObject.toString(), NewRemoteConfig.class);
                            callback.onCompleted(nrc);


                        } else {
                            callback.onFailure("");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    Logger.i("新版个人配置接口set" + e.getMessage());
                    callback.onFailure("");

                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 新版获取用户配置
     */
    public static void getUserConfig(long version, final ProtocolCallback.UnitCallback<NewRemoteConfig> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");
            String requestUrl = String.format("%s//configuration/getincreclientconfig.qunar", QtalkNavicationService.getInstance().getHttpUrl());
            JSONObject inputObject = new JSONObject();

            inputObject.put("username", CurrentPreference.getInstance().getUserid());
            inputObject.put("host", QtalkNavicationService.getInstance().getXmppdomain());
            inputObject.put("version", version);
            QtalkHttpService.asyncPostJson(requestUrl, inputObject, cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        Logger.i("新版个人配置接口get:" + jsonObject.toString());
                        BaseJsonResult baseJson = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                        if (baseJson.ret) {
                            NewRemoteConfig nrc = JsonUtils.getGson().fromJson(jsonObject.toString(), NewRemoteConfig.class);
                            callback.onCompleted(nrc);
                        } else {
                            callback.onFailure("");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("");
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    Logger.i("新版个人配置接口get" + e.getMessage());
                    callback.onFailure("");
                }


            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 快捷回复
     *
     * @param gversion 组版本号
     * @param cversion 内容版本号
     * @param callback
     */
    public static void getQuickReplies(long gversion, long cversion, final ProtocolCallback.UnitCallback<QuickReplyResult> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String requestUrl = String.format("%s/quickreply/quickreplylist.qunar", QtalkNavicationService.getInstance().getHttpUrl());
            JSONObject inputObject = new JSONObject();

            inputObject.put("username", CurrentPreference.getInstance().getUserid());
            inputObject.put("host", QtalkNavicationService.getInstance().getXmppdomain());
            inputObject.put("groupver", gversion);
            inputObject.put("contentver", cversion);
            QtalkHttpService.asyncPostJson(requestUrl, inputObject, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        Logger.i("快捷回复接口get成功:" + jsonObject.toString());
                        BaseJsonResult baseJson = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                        if (baseJson.ret) {
                            QuickReplyResult quickReplyResult = JsonUtils.getGson().fromJson(jsonObject.toString(), QuickReplyResult.class);
                            callback.onCompleted(quickReplyResult);
                        } else {
                            callback.onFailure("");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("");
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    Logger.i("快捷回复接口get失败" + e.getMessage());
                    callback.onFailure("");
                }


            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取热线列表
     */
    public static void getHotlineList(final ProtocolCallback.UnitCallback<HotlinesResult.DataBean> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String requestUrl = String.format("%s/admin/outer/qtalk/getHotlineList", QtalkNavicationService.getInstance().getHttpUrl());
            Map<String,String> params = new HashMap<>();
            params.put("username", CurrentPreference.getInstance().getUserid());
            HttpUrlConnectionHandler.executePostJson(requestUrl, JsonUtils.getGson().toJson(params), new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response){
                    try {
                        String resultString = Protocol.parseStream(response);
                        Logger.i("获取热线列表接口get成功:" + resultString);
                        BaseJsonResult baseJson = JsonUtils.getGson().fromJson(resultString, BaseJsonResult.class);
                        if (baseJson.ret) {
                            HotlinesResult hotlinesResult = JsonUtils.getGson().fromJson(resultString, HotlinesResult.class);
                            callback.onCompleted(hotlinesResult.data);
                        } else {
                            callback.onFailure("");
                            Logger.i("获取热线列表接口get失败");
                        }
                    } catch (Exception e) {
                        callback.onFailure("");
                        Logger.i("获取热线列表接口get失败" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Logger.i("获取热线列表接口get失败" + e.getMessage());
                    callback.onFailure("");
                }
            });
        } catch (Exception e) {
            Logger.i("获取热线列表接口get失败" + e.getMessage());
            callback.onFailure("");
        }

    }


    /**
     * 单人消息云端漫游记录
     *
     * @param from
     * @param to
     * @param timestamp
     * @param num
     * @param direction
     * @param callback
     */
    public static void getSingleChatOfflineMsg(String from, String to, long timestamp, int num, final int direction,
                                               final ProtocolCallback.UnitCallback<OfflineSingleMsgResult> callback) {
        if (TextUtils.isEmpty(CurrentPreference.getInstance().getVerifyKey())) {
            callback.doFailure();
            return;
        }
        StringBuilder queryString = new StringBuilder("domain/get_msgs?");
        Protocol.addBasicParamsOnHead(queryString);
        GetSingleConvRecord singleConvRecord = new GetSingleConvRecord();
        singleConvRecord.from = QtalkStringUtils.parseLocalpart(from);
        singleConvRecord.to = QtalkStringUtils.parseLocalpart(to);
        singleConvRecord.from_host = QtalkStringUtils.parseDomain(from);
        singleConvRecord.to_host = QtalkStringUtils.parseDomain(to);
        singleConvRecord.timestamp = timestamp;
        singleConvRecord.limitnum = num;
        singleConvRecord.direction = String.valueOf(direction);
        singleConvRecord.u = CurrentPreference.getInstance().getUserid();
        singleConvRecord.k = CurrentPreference.getInstance().getVerifyKey();
        try {
            LogUtil.d("debug", "get");
            String jsonParams = JsonUtils.getGson().toJson(singleConvRecord);
            String url = Protocol.makeGetUri(QtalkNavicationService.getInstance().getHttpHost(),
                    QtalkNavicationService.getInstance().getHttpPort(), queryString.toString(), true);
            HttpUrlConnectionHandler.executePostJson(url, jsonParams, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    LogUtil.d("debug", "get complete");
                    OfflineSingleMsgResult result = null;
                    try {
                        String resultString = Protocol.parseStream(response);
                        result = JsonUtils.getGson().fromJson(resultString, OfflineSingleMsgResult.class);
                    } catch (Exception e) {
                        LogUtil.e(TAG, "error", e);
                    }
                    callback.onCompleted(result);
                }

                @Override
                public void onFailure(Exception e) {
                    callback.doFailure();
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG, "error", e);
        }
    }

    public static boolean isEncrypt(String toid) {
        return DataCenter.encryptUsers.containsKey(toid);
    }

    public static String getEncryptPassword(String toid) {
        return DataCenter.encryptUsers.get(toid);
    }

    public static void addEncryptMessageInfo(String toid, IMMessage message, int msgType) {
        if (!isEncrypt(toid)) return;
        String password = getEncryptPassword(toid);
        if (!TextUtils.isEmpty(password))
            try {
                EncryptMsg encryptMsg = new EncryptMsg();
                encryptMsg.MsgType = msgType;
                if (msgType == ProtoMessageOuterClass.MessageType.MessageTypeSmallVideo_VALUE ||
                        msgType == ProtoMessageOuterClass.MessageType.MessageTypeLocalShare_VALUE) {//视频 位置取ext
                    encryptMsg.Content = message.getExt();
                } else {
                    encryptMsg.Content = message.getBody();
                }
                String encryptJson = JsonUtils.getGson().toJson(encryptMsg);
                String ext = AESTools.encodeToBase64(password, encryptJson);
                message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeEncrypt_VALUE);
                message.setBody("【加密消息】");
                message.setExt(ext);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    /**
     * 阅后即焚
     *
     * @param message
     */
    public static void handleSnapMessage(IMMessage message) {
        AutoDestroyMessageExtention ade = new AutoDestroyMessageExtention();
        ade.descStr = message.getBody();
        ade.message = message.getBody();
        ade.msgType = Integer.valueOf(message.getMsgType());

//        if (!TextUtils.isEmpty(String.valueOf(message.getMsgType())) &&
//                !TextUtils.isEmpty(message.getExt())) {
//            ade.message = message.getExt();
//            ade.msgType = message.getMsgType();
//        }
        message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeBurnAfterRead_VALUE);
        message.setExt(JsonUtils.getGson().toJson(ade));

        message.setBody("此消息为阅后即焚消息，当前客户端不支持");
    }

    /**
     * 获取checkconfig配置
     */
    public static void getMyCapability(boolean isForce) {
        long navConfigUpdateTime = DataUtils.getInstance(CommonConfig.globalContext).getPreferences(QtalkNavicationService.NAV_CHECKCONFIG_UPDATETIME, 0L);
        long currentTime = System.currentTimeMillis();
        int cv = -1;
        CapabilityResult result = CapabilityUtil.getInstance().getCurrentCapabilityData();
        if (result != null) {
            cv = result.version;
        }

        /**
         * 判断语言是否有更改
         */
        boolean isLangageChange = false;
        String currSaveLangage = DataUtils.getInstance(CommonConfig.globalContext).getPreferences(Constants.Preferences.System_Langage,"zh-CN");
        String currSysLangage = DeviceUtil.getSystemLangage(CommonConfig.globalContext);
        if(!TextUtils.isEmpty(currSysLangage) && !currSysLangage.equals(currSaveLangage)){
            isLangageChange = true;
            DataUtils.getInstance(CommonConfig.globalContext).putPreferences(Constants.Preferences.System_Langage,currSysLangage);
        }

        if (isForce || isLangageChange || cv < QtalkNavicationService.getInstance().getCheckconfigVersion() ||
                currentTime - navConfigUpdateTime > 24 * 60 * 60 * 1000) {
            getAbility(cv,
                    new ProtocolCallback.UnitCallback<String>() {

                        @Override
                        public void onFailure(String errMsg) {
                            Logger.i("getMyCapability_error" + errMsg);
                        }

                        @Override
                        public void onCompleted(String s) {
                            if (!TextUtils.isEmpty(s)) {

                                DataUtils.getInstance(CommonConfig.globalContext).putPreferences(QtalkNavicationService.NAV_CHECKCONFIG_UPDATETIME, System.currentTimeMillis());
                                //老版本通过sp存错 新版变换为数据库存储
                                IMDatabaseManager.getInstance().InsertCapability(s);

                                CapabilityResult ability = JsonUtils.getGson().fromJson(s, CapabilityResult.class);
                                CapabilityUtil.getInstance().saveExtConfig(ability);
                            }
                        }
                    });
        }
    }

    private static void getAbility(int checkVersion, final ProtocolCallback.UnitCallback<String> callback) {
        Map<String, String> cookie = new HashMap<>();
        cookie.put("Cookie", "q_ckey=" + Protocol.getCKEY() + ";");
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("cv", String.valueOf(checkVersion));
            requestBody.put("ver", GlobalConfigManager.getAppName());
            requestBody.put("p", "android");
            requestBody.put("v", QunarIMApp.getQunarIMApp().getVersion());
            requestBody.put("language",DeviceUtil.getSystemLangage(CommonConfig.globalContext));
        } catch (JSONException e) {

        }
        Logger.i("getAbility-request" + "requestBody:" + requestBody.toString() + "\n" + "url:" + QtalkNavicationService.getInstance().getHttpUrl() + "/config/check_config.qunar");
        QtalkHttpService.asyncPostJson(QtalkNavicationService.getInstance().getHttpUrl() + "/config/check_config.qunar", requestBody, cookie, new QtalkHttpService.CallbackJson() {
            @Override
            public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                BaseJsonResult data = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                if (data.ret) {
                    String resultdata = jsonObject.get("data").toString();
                    Logger.i("getAbility-response" + resultdata);
                    callback.onCompleted(resultdata);
                } else {
                    callback.onFailure("");
                }
            }

            @Override
            public void onFailure(Call call, Exception e) {

            }
        });
    }

    /**
     * 发送capability请求
     *
     * @param requestBody
     */
    public static void sendCapabilityRequest(String url, JSONObject requestBody) {
        Map<String, String> cookie = new HashMap<>();
        cookie.put("Cookie", "q_ckey=" + Protocol.getCKEY() + ";");
        QtalkHttpService.asyncPostJson(url, requestBody, cookie, new QtalkHttpService.CallbackJson() {
            @Override
            public void onJsonResponse(JSONObject jsonObject) throws JSONException {

            }

            @Override
            public void onFailure(Call call, Exception e) {

            }
        });
    }


    public static void serverCloseSession(String username, String seatname, String virtualname, final ProtocolCallback.UnitCallback<String> callback) {
        String q_ckey = Protocol.getCKEY();
        if (TextUtils.isEmpty(q_ckey)) return;
        final Map<String, String> cookie = new HashMap<>();
        cookie.put("Cookie", "q_ckey=" + q_ckey + ";p_user=" + CurrentPreference.getInstance().getUserid());

        StringBuilder params = new StringBuilder("admin/api/seat/closeSession.qunar?");
        params.append("userName=")
                .append(username)
                .append("&seatName=")
                .append(seatname)
                .append("&virtualname=").append(QtalkStringUtils.parseBareJid(virtualname));
        Protocol.addBasicParamsOnHead(params);
        String requestUrl = Protocol.makeGetUri(QtalkNavicationService.getInstance().getJavaUrl(), QtalkNavicationService.getInstance().getHttpPort(), params.toString(), true);

        Logger.i(TAG + "客服挂断会话  requestUrl:" + requestUrl);

        QtalkHttpService.asyncGetJson(requestUrl, cookie, new QtalkHttpService.CallbackJson() {
            @Override
            public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                BaseJsonResult data = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                Logger.i("客服挂断会话:" + jsonObject.toString());
                if (data.ret) {
                    String resultdata = jsonObject.get("data").toString();
                    callback.onCompleted(resultdata);
                } else {
                    callback.onFailure("");
                }
            }

            @Override
            public void onFailure(Call call, Exception e) {

            }


        });
    }


    public static void getUserLead(final String jid, final ProtocolCallback.UnitCallback<LeadInfo> callback) {


        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            String requestUrl = QtalkNavicationService.getInstance().getLeaderurl();
            JSONObject inputObject = new JSONObject();

            inputObject.put("platform", "android");
            inputObject.put("qtalk_id", QtalkStringUtils.parseId(jid));
            inputObject.put("user_id", QtalkStringUtils.parseId(jid));
            inputObject.put("ckey", q_ckey);

            Logger.i("请求地址:" + requestUrl + ";请求参数:" + inputObject);
            QtalkHttpService.asyncPostJson(requestUrl, inputObject, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        LeadInfo baseJson;
                        Logger.i("getuserlead返回:" + jsonObject.toString());
                        baseJson = JsonUtils.getGson().fromJson(jsonObject.toString(), LeadInfo.class);
                        if (baseJson.getErrcode() == 0) {
                            callback.onCompleted(baseJson);

                        } else {
                            baseJson = new LeadInfo();
                            LeadInfo.DataBean dataBean = new LeadInfo.DataBean();
                            dataBean.setEmail(jid);
                            dataBean.setLeader("直属上级");
                            dataBean.setQtalk_id("");
                            dataBean.setSn("员工号");
                            callback.onCompleted(baseJson);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {

                }


            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * qchat 通知后台客服上线
     */
    public static void notifyOnline() {
        HttpUrlConnectionHandler.executeGet(Protocol.getUrl(QtalkNavicationService.getInstance().getQcadminHost(), "css/online"), new HttpRequestCallback() {
            @Override
            public void onComplete(InputStream response){
                if (response != null) {
                    String resultString = Protocol.parseStream(response);
                    Logger.i("notifyOnline:" + resultString);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Logger.i("notifyOnline:" + e.getLocalizedMessage());
            }
        });
    }

    /**
     * 获取ops通知小红点
     *
     * @param callback
     */
    public static String opsUrl = "";

    public static void getUnreadCountFromOps(final ProtocolCallback.UnitCallback<OpsUnreadResult> callback) {
        if (TextUtils.isEmpty(CurrentPreference.getInstance().getUserid()) ||
                TextUtils.isEmpty(CurrentPreference.getInstance().getVerifyKey()) ||
                TextUtils.isEmpty(opsUrl)) {
            callback.onFailure("");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(opsUrl);
        String t = System.currentTimeMillis() + "";
        sb.append("?p=android");
        sb.append("&v=").append(Protocol.VERSIONVALUE);
        sb.append("&t=").append(t);
/*        k值为  base64(k1).
        k1的值为 u=用户名&k=md5(k2)
        k2的值为从qtalk客户端拿到的k值(k3)字符串拼接t的数值*/
        String k2 = CurrentPreference.getInstance().getVerifyKey() + t;
        String k1 = "u=" + CurrentPreference.getInstance().getUserid() + "&k=" + BinaryUtil.MD5(k2);
        String k = android.util.Base64.encodeToString(k1.getBytes(), android.util.Base64.DEFAULT);
        sb.append("&c=").append(k);

        HttpUrlConnectionHandler.executeGet(sb.toString(), new HttpRequestCallback() {
            @Override
            public void onComplete(InputStream response) {
                String resultString = null;
                OpsUnreadResult result = new OpsUnreadResult();
                result.setData(new OpsUnreadResult.DataBean());
                try {
                    resultString = Protocol.parseStream(response);
                    Logger.i("ThirdAPI getUnreadCountFromOps" + resultString);
                    result = JsonUtils.getGson().fromJson(resultString
                            , OpsUnreadResult.class);
                } catch (Exception e) {
                    Logger.i("ThirdAPI getUnreadCountFromOps" + e.getLocalizedMessage());
                }

                callback.onCompleted(result);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure("");
            }
        });
    }


    /**
     * 获取用户会议行程
     */
    public static void getUserTripList(long version, final ProtocolCallback.UnitCallback<CalendarTrip> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

//            String requestUrl = String.format("%s//configuration/getincreclientconfig.qunar", QtalkNavicationService.getInstance().getHttpUrl());
            String requestUrl = String.format("%s/scheduling/get_update_list.qunar", QtalkNavicationService.getInstance().getHttpUrl());
            JSONObject inputObject = new JSONObject();

            inputObject.put("updateTime", version + "");
            inputObject.put("userName", CurrentPreference.getInstance().getPreferenceUserId());
            Logger.i("日历请求数据:" + JsonUtils.getGson().toJson(inputObject));
            QtalkHttpService.asyncPostJson(requestUrl, inputObject, cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        Logger.i("日历列表get:" + jsonObject.toString());

                        BaseJsonResult baseJson = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                        if (baseJson.ret) {
                            CalendarTrip nrc = JsonUtils.getGson().fromJson(jsonObject.toString(), CalendarTrip.class);
                            callback.onCompleted(nrc);
                        } else {
                            callback.onFailure("");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("");
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    Logger.i("日历列表get" + e.getMessage());
                    callback.onFailure("");
                }


            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取用户会议行程
     */
    public static void createTrip(CalendarTrip.DataBean.TripsBean bean, final ProtocolCallback.UnitCallback<CalendarTrip> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

//            String requestUrl = String.format("%s//configuration/getincreclientconfig.qunar", QtalkNavicationService.getInstance().getHttpUrl());
            String requestUrl = String.format("%s/scheduling/reserve_scheduling.qunar", QtalkNavicationService.getInstance().getHttpUrl());
//            JSONObject inputObject = new JSONObject();
//
//            inputObject.put("updateTime", version+"");
//            inputObject.put("userName", CurrentPreference.getInstance().getPreferenceUserId());
            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(bean), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        Logger.i("日历列表get:" + jsonObject.toString());

                        BaseJsonResult baseJson = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                        if (baseJson.ret || Double.parseDouble(baseJson.errcode + "") == 3) {
                            CalendarTrip nrc = JsonUtils.getGson().fromJson(jsonObject.toString(), CalendarTrip.class);
                            callback.onCompleted(nrc);
                        } else {

                            callback.onFailure(baseJson.errmsg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("");
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    Logger.i("日历列表get" + e.getMessage());
                    callback.onFailure("");
                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取可用房间列表
     *
     * @param arr
     * @param callback
     */
    public static void getTripAreaAvailableRoom(AvailableRoomRequest arr, final ProtocolCallback.UnitCallback<AvailableRoomResponse> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

//            String requestUrl = String.format("%s//configuration/getincreclientconfig.qunar", QtalkNavicationService.getInstance().getHttpUrl());
            String requestUrl = String.format("%s/scheduling/room_list.qunar", QtalkNavicationService.getInstance().getHttpUrl());
//            JSONObject inputObject = new JSONObject();
//
//            inputObject.put("updateTime", version+"");
//            inputObject.put("userName", CurrentPreference.getInstance().getPreferenceUserId());
            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(arr), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        Logger.i("可用房间列表get:" + jsonObject.toString());

                        BaseJsonResult baseJson = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                        if (baseJson.ret) {
                            AvailableRoomResponse nrc = JsonUtils.getGson().fromJson(jsonObject.toString(), AvailableRoomResponse.class);
                            callback.onCompleted(nrc);
                        } else {
                            callback.onFailure(baseJson.errmsg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("");
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    Logger.i("可用房间列表get" + e.getMessage());
                    callback.onFailure("");
                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查该用户是否此时间段冲突
     */
    public static void tripMemberCheck(CalendarTrip.DataBean.TripsBean bean, final ProtocolCallback.UnitCallback<TripMemberCheckResponse> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

//            String requestUrl = String.format("%s//configuration/getincreclientconfig.qunar", QtalkNavicationService.getInstance().getHttpUrl());
            String requestUrl = String.format("%s/scheduling/get_scheduling_conflict.qunar", QtalkNavicationService.getInstance().getHttpUrl());
//            JSONObject inputObject = new JSONObject();
//
//            inputObject.put("updateTime", version+"");
//            inputObject.put("userName", CurrentPreference.getInstance().getPreferenceUserId());
            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(bean), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        Logger.i("日历列表get:" + jsonObject.toString());

                        BaseJsonResult baseJson = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                        if (baseJson.ret) {
                            TripMemberCheckResponse nrc = JsonUtils.getGson().fromJson(jsonObject.toString(), TripMemberCheckResponse.class);
                            callback.onCompleted(nrc);
                        } else {
                            callback.onFailure("");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("");
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    Logger.i("日历列表get" + e.getMessage());
                    callback.onFailure("");
                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getHotlineSeats(String customerName,String hotlineName,HttpRequestCallback callback){
        String url = QtalkNavicationService.getInstance().getHttpUrl() + "/admin/outer/qtalk/hotlineSeatList.json";
        Map<String,String> params = new HashMap<>();
        params.put("customerName",customerName);
        params.put("hotlineName",hotlineName);
        params.put("host",QtalkNavicationService.getInstance().getXmppdomain());
        HttpUrlConnectionHandler.executePostJson(url,JsonUtils.getGson().toJson(params),callback);
    }

    public static void transArtificial(String customerName,String hotlineName,String newCsrName,String reason,HttpRequestCallback callback){
        String url = QtalkNavicationService.getInstance().getHttpUrl() + "/admin/outer/qtalk/transformSeat.json";
        Map<String,String> params = new HashMap<>();
        params.put("customerName",customerName);
        params.put("hotlineName",hotlineName);
        params.put("currentCsrName",CurrentPreference.getInstance().getUserid());
        params.put("newCsrName",newCsrName);
        params.put("reason",reason);
        params.put("host",QtalkNavicationService.getInstance().getXmppdomain());
        HttpUrlConnectionHandler.executePostJson(url,JsonUtils.getGson().toJson(params),callback);
    }

    public static void getDomainList(HttpRequestCallback callback){
        String requestUrl = QtalkNavicationService.getInstance().getDomainSearchUrl();
        Map<String,String> params = new HashMap<>();
        params.put("version","0");
        HttpUrlConnectionHandler.executePostForm(requestUrl, params, callback);
    }

    public static void searchDomainUser(String url,Map<String,String> params,HttpRequestCallback callback){
        HttpUrlConnectionHandler.executePostForm(url, params, callback);
    }


    /**
     * 检查该用户是否此时间段冲突
     */
    public static void getCity(final ProtocolCallback.UnitCallback<CityLocal> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

//            String requestUrl = String.format("%s//configuration/getincreclientconfig.qunar", QtalkNavicationService.getInstance().getHttpUrl());
            String requestUrl = String.format("%s/scheduling/allCitys.qunar", QtalkNavicationService.getInstance().getHttpUrl());
//            JSONObject inputObject = new JSONObject();
//
//            inputObject.put("updateTime", version+"");
//            inputObject.put("userName", CurrentPreference.getInstance().getPreferenceUserId());
            QtalkHttpService.asyncGetJson(requestUrl, cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        Logger.i("城市列表get:" + jsonObject.toString());

                        BaseJsonResult baseJson = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                        if (baseJson.ret) {
                            CityLocal nrc = JsonUtils.getGson().fromJson(jsonObject.toString(), CityLocal.class);
                            callback.onCompleted(nrc);
                        } else {
                            callback.onFailure("");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("");
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    Logger.i("城市列表get" + e.getMessage());
                    callback.onFailure("");
                }


            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 检查该用户是否此时间段冲突
     */
    public static void getNewArea(int cityId, final ProtocolCallback.UnitCallback<AreaLocal> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");
            Map<String, Object> map = new HashMap<>();
            map.put("cityId", cityId);
//            String requestUrl = String.format("%s//configuration/getincreclientconfig.qunar", QtalkNavicationService.getInstance().getHttpUrl());
            String requestUrl = String.format("%s/scheduling/getAreaByCityId.qunar", QtalkNavicationService.getInstance().getHttpUrl());
//


            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(map), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        Logger.i("地址列表get:" + jsonObject.toString());

                        BaseJsonResult baseJson = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                        if (baseJson.ret) {
                            AreaLocal nrc = JsonUtils.getGson().fromJson(jsonObject.toString(), AreaLocal.class);
                            callback.onCompleted(nrc);
                        } else {
                            callback.onFailure("");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("");
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    Logger.i("日历列表get" + e.getMessage());
                    callback.onFailure("");
                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 检查该用户是否此时间段冲突
     */
    public static void getArea(final ProtocolCallback.UnitCallback<AreaLocal> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

//            String requestUrl = String.format("%s//configuration/getincreclientconfig.qunar", QtalkNavicationService.getInstance().getHttpUrl());
            String requestUrl = String.format("%s/scheduling/area_list.qunar", QtalkNavicationService.getInstance().getHttpUrl());
//            JSONObject inputObject = new JSONObject();
//
//            inputObject.put("updateTime", version+"");
//            inputObject.put("userName", CurrentPreference.getInstance().getPreferenceUserId());
            QtalkHttpService.asyncGetJson(requestUrl, cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        Logger.i("地址列表get:" + jsonObject.toString());

                        BaseJsonResult baseJson = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                        if (baseJson.ret) {
                            AreaLocal nrc = JsonUtils.getGson().fromJson(jsonObject.toString(), AreaLocal.class);
                            callback.onCompleted(nrc);
                        } else {
                            callback.onFailure("");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("");
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    Logger.i("地址列表get" + e.getMessage());
                    callback.onFailure("");
                }


            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 小拿按钮点击有用没用
     */
    public static void robotConfirm(String url, Map<String, String> bean, final ProtocolCallback.UnitCallback<Boolean> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

//            String requestUrl = String.format("%s//configuration/getincreclientconfig.qunar", QtalkNavicationService.getInstance().getHttpUrl());
            String requestUrl = url;
//            JSONObject inputObject = new JSONObject();
//
//            inputObject.put("updateTime", version+"");
//            inputObject.put("userName", CurrentPreference.getInstance().getPreferenceUserId());
            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(bean), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {

                        callback.onCompleted(true);
//                        Logger.i("日历列表get:" + jsonObject.toString());
//
//                        BaseJsonResult baseJson = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
//                        if (baseJson.ret) {
//                            TripMemberCheckResponse nrc = JsonUtils.getGson().fromJson(jsonObject.toString(), TripMemberCheckResponse.class);
//                            callback.onCompleted(nrc);
//                        } else {
//                            callback.onFailure("");
//                        }
// if (baseJson.ret) {
// JSONMucHistorys chatJson = JsonUtils.getGson().fromJson(jsonObject.toString(), JSONMucHistorys.class);
// if (chatJson.getData().size() > 0) {
// IMDatabaseManager.getInstance().bulkInsertGroupHistoryFroJson(chatJson.getData(), CurrentPreference.getInstance().getPreferenceUserId());
// List<IMMessage> messageList;
// messageList = IMDatabaseManager.getInstance().SelectHistoryGroupChatMessage(mucId, realJid, count, num);
// callback.onCompleted(messageList);
// } else {
// callback.onCompleted(null);
// }
//
//
// }
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("");
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    callback.onCompleted(false);
                    Logger.i("日历列表get" + e.getMessage());
//                    callback.onFailure("");
                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 小拿进会话接口
     */
    public static void robotSessionPost(String url, Map<String, String> bean, final ProtocolCallback.UnitCallback<Boolean> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

            String requestUrl = url;
            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(bean), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {

                        callback.onCompleted(true);
//
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("");
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    callback.onCompleted(false);
                    Logger.i("小拿进会话接口" + e.getMessage());
//                    callback.onFailure("");
                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取勋章接口
     */
    public static void getRemoteUserMedalWithXmppJid(String xmppId, final ProtocolCallback.UnitCallback<List<MedalsInfo>> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

            String requestUrl = String.format("%s/user/get_user_decoration.qunar", QtalkNavicationService.getInstance().getHttpUrl());
            Map<String, String> map = new HashMap<>();
            map.put("userId", QtalkStringUtils.parseId(xmppId));
            map.put("host", QtalkStringUtils.parseDomain(xmppId));
            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(map), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        List<MedalsInfo> list = new ArrayList<>();
                        if (!TextUtils.isEmpty(jsonObject.toString())) {
                            BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                            if (baseJsonResult.ret) {
                                MedalsInfoResponse medalsInfoResponse = JsonUtils.getGson().fromJson(jsonObject.toString(), MedalsInfoResponse.class);

                                if (medalsInfoResponse.getData().size() > 0) {
                                    list = medalsInfoResponse.getData();
                                }
                            }


                        }
                        callback.onCompleted(list);
//
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    callback.onFailure(e.getMessage());
                    Logger.i("获取勋章接口出错" + e.getMessage());
//                    callback.onFailure("");
                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取匿名用户
     *
     * @param uuid
     * @param callback
     */
    public static void getAnonymous(String uuid, final ProtocolCallback.UnitCallback<AnonymousData> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");
            String requestUrl = String.format("%s/cricle_camel/anonymouse/getAnonymouse", QtalkNavicationService.getInstance().getHttpUrl());
            Map<String, String> map = new HashMap<>();
            map.put("user", CurrentPreference.getInstance().getPreferenceUserId());
            map.put("postId", uuid);
            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(map), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        AnonymousData anonymousData = null;
                        if (!TextUtils.isEmpty(jsonObject.toString())) {
                            BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                            if (baseJsonResult.ret) {
                                anonymousData = JsonUtils.getGson().fromJson(jsonObject.toString(), AnonymousData.class);

                                callback.onCompleted(anonymousData);
                                return;
                            }


                        }
                        callback.onFailure("获取失败");

//
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    callback.onFailure(e.getMessage());
                    Logger.i("获取勋章接口出错" + e.getMessage());
//                    callback.onFailure("");
                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 进行发布帖子
     *
     * @param callback
     */
    public static void releaseWorkWorld(ReleaseDataRequest releaseDataRequest, final ProtocolCallback.UnitCallback<WorkWorldResponse> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

            String requestUrl = String.format("%s/cricle_camel/post", QtalkNavicationService.getInstance().getHttpUrl());
//
            releaseDataRequest.setOwner(CurrentPreference.getInstance().getUserid());
            releaseDataRequest.setOwner_host(QtalkNavicationService.getInstance().getXmppdomain());
            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(releaseDataRequest), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        WorkWorldResponse workWorldResponse = null;
                        if (!TextUtils.isEmpty(jsonObject.toString())) {
                            BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                            if (baseJsonResult.ret) {
                                workWorldResponse = JsonUtils.getGson().fromJson(jsonObject.toString(), WorkWorldResponse.class);
                                IMDatabaseManager.getInstance().InsertWorkWorldByList(workWorldResponse.getData().getNewPost());
                                IMDatabaseManager.getInstance().UpdateWorkWorldDeleteState(workWorldResponse.getData().getDeletePost());
                            }


                        }
                        callback.onCompleted(workWorldResponse);

//
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    callback.onFailure(e.getMessage());
                    Logger.i("获取勋章接口出错" + e.getMessage());
//                    callback.onFailure("");
                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 进行发布帖子V2
     *
     * @param callback
     */
    public static void releaseWorkWorldV2(ReleaseDataRequest releaseDataRequest, final ProtocolCallback.UnitCallback<WorkWorldResponse> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

            String requestUrl = String.format("%s/cricle_camel/post/V2", QtalkNavicationService.getInstance().getHttpUrl());
//
            releaseDataRequest.setOwner(CurrentPreference.getInstance().getUserid());
            releaseDataRequest.setOwner_host(QtalkNavicationService.getInstance().getXmppdomain());
            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(releaseDataRequest), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        WorkWorldResponse workWorldResponse = null;
                        if (!TextUtils.isEmpty(jsonObject.toString())) {
                            BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                            if (baseJsonResult.ret) {
                                workWorldResponse = JsonUtils.getGson().fromJson(jsonObject.toString(), WorkWorldResponse.class);
                                IMDatabaseManager.getInstance().InsertWorkWorldByList(workWorldResponse.getData().getNewPost());
                                IMDatabaseManager.getInstance().UpdateWorkWorldDeleteState(workWorldResponse.getData().getDeletePost());
                                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.WORK_WORLD_REFRESH);
                            } else {
                                callback.onCompleted(null);
                            }


                        }
                        callback.onCompleted(workWorldResponse);

//
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    callback.onFailure(e.getMessage());
                    Logger.i("获取勋章接口出错" + e.getMessage());
//                    callback.onFailure("");
                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取最新的帖子V2
     *
     * @param isInsert
     * @param callback
     */
    public static void refreshWorkWorldV2(int count, int attachCommentCount, int postType, final String owner, final String ownerHost, final long createTime, final boolean isInsert, final ProtocolCallback.UnitCallback<WorkWorldResponse> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

            String requestUrl = String.format("%s/cricle_camel/post/getPostList/v2", QtalkNavicationService.getInstance().getHttpUrl());
//
            final Map<String, Object> map = new HashMap<>();
            map.put("pageSize", count);
            map.put("postCreateTime", createTime);
            map.put("attachCommentCount", attachCommentCount);
            map.put("postType", postType);
            if (!TextUtils.isEmpty(owner) && !TextUtils.isEmpty(ownerHost)) {
                map.put("owner", owner);
                map.put("ownerHost", ownerHost);
            }
//            map.put("getTop",getTop);
//            releaseDataRequest.setOwner( CurrentPreference.getInstance().getUserid());
//            releaseDataRequest.setOwner_host(QtalkNavicationService.getInstance().getXmppdomain());
            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(map), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        WorkWorldResponse workWorldResponse = null;
                        if (!TextUtils.isEmpty(jsonObject.toString())) {
                            BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                            if (baseJsonResult.ret) {
                                workWorldResponse = JsonUtils.getGson().fromJson(jsonObject.toString(), WorkWorldResponse.class);
                                if (isInsert) {


                                    if (workWorldResponse.getData().getNewPost().size() == 0 && createTime == 0) {
                                        IMDatabaseManager.getInstance().DeleteWorkWorldDeleteByAll(owner, ownerHost);
                                    } else {
                                        IMDatabaseManager.getInstance().InsertWorkWorldByList(workWorldResponse.getData().getNewPost());
                                        IMDatabaseManager.getInstance().UpdateWorkWorldDeleteState(workWorldResponse.getData().getDeletePost());
                                    }
                                }
                            }


                        }
                        callback.onCompleted(workWorldResponse);

//
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    callback.onFailure(e.getMessage());
                    Logger.i("获取最新朋友圈接口出错" + e.getMessage());
//                    callback.onFailure("");
                }


            }, 3, 6);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取我的消息
     *
     * @param callback
     */
    public static void refreshWorkWorldMyReply(int count, final long createTime, final ProtocolCallback.UnitCallback<WorkWorldMyReply> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

//            /newapi/cricle_camel/ownerCamel/getMyReply
            String requestUrl = String.format("%s/cricle_camel/ownerCamel/getMyReply", QtalkNavicationService.getInstance().getHttpUrl());
//
            Map<String, Object> map = new HashMap<>();
            map.put("pageSize", count);
            map.put("createTime", createTime);
            map.put("owner", CurrentPreference.getInstance().getUserid());
            map.put("ownerHost", QtalkNavicationService.getInstance().getXmppdomain());
            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(map), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        WorkWorldMyReply workWorldResponse = null;
                        if (!TextUtils.isEmpty(jsonObject.toString())) {
                            BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                            if (baseJsonResult.ret) {
                                workWorldResponse = JsonUtils.getGson().fromJson(jsonObject.toString(), WorkWorldMyReply.class);
                                if (workWorldResponse.getData().getNewComment().size() == 0 && createTime == 0) {
                                    IMDatabaseManager.getInstance().DeleteWorkWorldNoticeByEventType(Constants.WorkWorldState.MYREPLYCOMMENT);
                                } else {
                                    IMDatabaseManager.getInstance().InsertWorkWorldNoticeByList(workWorldResponse.getData().getNewComment(), true);
                                    IMDatabaseManager.getInstance().UpdateWorkWorldNoticeDeleteState(workWorldResponse.getData().getDeleteComments());
                                }
//                                IMDatabaseManager.getInstance().InsertWorkWorldByList(workWorldResponse.getData().getNewPost());
//                                IMDatabaseManager.getInstance().UpdateWorkWorldDeleteState(workWorldResponse.getData().getDeletePost());
                            }


                        }
                        callback.onCompleted(workWorldResponse);

//
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    callback.onFailure(e.getMessage());
                    Logger.i("获取最新朋友圈接口出错" + e.getMessage());
//                    callback.onFailure("");
                }


            }, 3, 6);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取@我的消息
     *
     * @param callback
     */
    public static void refreshWorkWorldAtMe(int count, final long createTime, final ProtocolCallback.UnitCallback<WorkWorldAtShowResponse> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }

        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

//            /newapi/cricle_camel/ownerCamel/getMyReply
            String requestUrl = String.format("%s/cricle_camel/ownerCamel/getAtList", QtalkNavicationService.getInstance().getHttpUrl());
//
            Map<String, Object> map = new HashMap<>();
            map.put("pageSize", count);
            map.put("createTime", createTime);
            map.put("owner", CurrentPreference.getInstance().getUserid());
            map.put("ownerHost", QtalkNavicationService.getInstance().getXmppdomain());
            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(map), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        WorkWorldAtShowResponse workWorldResponse = null;
                        if (!TextUtils.isEmpty(jsonObject.toString())) {
                            BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                            if (baseJsonResult.ret) {
                                workWorldResponse = JsonUtils.getGson().fromJson(jsonObject.toString(), WorkWorldAtShowResponse.class);
                                if (workWorldResponse.getData().getNewAtList().size() == 0 && createTime == 0) {
                                    IMDatabaseManager.getInstance().DeleteWorkWorldNoticeByEventType(Constants.WorkWorldState.COMMENTATMESSAGE);
                                    IMDatabaseManager.getInstance().DeleteWorkWorldNoticeByEventType(Constants.WorkWorldState.WORKWORLDATMESSAGE);
                                } else {
                                    IMDatabaseManager.getInstance().InsertWorkWorldNoticeByList(workWorldResponse.getData().getNewAtList(), true);
                                    IMDatabaseManager.getInstance().UpdateWorkWorldNoticeDeleteState(workWorldResponse.getData().getDeleteAtList());
                                }
//                                IMDatabaseManager.getInstance().InsertWorkWorldByList(workWorldResponse.getData().getNewPost());
//                                IMDatabaseManager.getInstance().UpdateWorkWorldDeleteState(workWorldResponse.getData().getDeletePost());
                            }


                        }
                        callback.onCompleted(workWorldResponse);

//
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    callback.onFailure(e.getMessage());
                    Logger.i("获取最新朋友圈接口出错" + e.getMessage());
//                    callback.onFailure("");
                }


            }, 3, 6);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取@我的消息
     *
     * @param callback
     */
    public static void getSearchWorkWorldMessage(int startNum, int pageNum, final long searchTime, String key, String searchType, final ProtocolCallback.UnitCallback<WorkWorldSearchShowResponse> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }

        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

//            /newapi/cricle_camel/ownerCamel/getMyReply
            String requestUrl = String.format("%s/cricle_camel/search", QtalkNavicationService.getInstance().getHttpUrl());
//
            Map<String, Object> map = new HashMap<>();
            map.put("key", key);
            map.put("searchTime", searchTime);
            map.put("startNum", startNum);
            map.put("pageNum", pageNum);
            map.put("searchType", searchType);
            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(map), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        WorkWorldSearchShowResponse workWorldResponse = null;
                        if (!TextUtils.isEmpty(jsonObject.toString())) {
                            BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                            if (baseJsonResult.ret) {
                                workWorldResponse = JsonUtils.getGson().fromJson(jsonObject.toString(), WorkWorldSearchShowResponse.class);
//                                if (workWorldResponse.getData().getNewAtList().size() == 0 && createTime == 0) {
//                                    IMDatabaseManager.getInstance().DeleteWorkWorldNoticeByEventType(Constants.WorkWorldState.COMMENTATMESSAGE);
//                                    IMDatabaseManager.getInstance().DeleteWorkWorldNoticeByEventType(Constants.WorkWorldState.WORKWORLDATMESSAGE);
//                                } else {
//                                    IMDatabaseManager.getInstance().InsertWorkWorldNoticeByList(workWorldResponse.getData().getNewAtList(), true);
//                                    IMDatabaseManager.getInstance().UpdateWorkWorldNoticeDeleteState(workWorldResponse.getData().getDeleteAtList());
//                                }
//                                IMDatabaseManager.getInstance().InsertWorkWorldByList(workWorldResponse.getData().getNewPost());
//                                IMDatabaseManager.getInstance().UpdateWorkWorldDeleteState(workWorldResponse.getData().getDeletePost());
                            }


                        }
                        callback.onCompleted(workWorldResponse);

//
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    callback.onFailure(e.getMessage());
                    Logger.i("获取最新朋友圈接口出错" + e.getMessage());
//                    callback.onFailure("");
                }


            }, 3, 6);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取最新的帖子
     *
     * @param callback
     * @param getTop
     */
    public static void refreshWorkWorld(int count, String owner, String ownerHost, long createTime, final ProtocolCallback.UnitCallback<WorkWorldResponse> callback, int getTop) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

            String requestUrl = String.format("%s/cricle_camel/post/getPostList", QtalkNavicationService.getInstance().getHttpUrl());
//
            Map<String, Object> map = new HashMap<>();
            map.put("pageSize", count);
            map.put("postCreateTime", createTime);
            if (!TextUtils.isEmpty(owner) && !TextUtils.isEmpty(ownerHost)) {
                map.put("owner", owner);
                map.put("ownerHost", ownerHost);
            }
            map.put("getTop", getTop);
//            releaseDataRequest.setOwner( CurrentPreference.getInstance().getUserid());
//            releaseDataRequest.setOwner_host(QtalkNavicationService.getInstance().getXmppdomain());
            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(map), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        WorkWorldResponse workWorldResponse = null;
                        if (!TextUtils.isEmpty(jsonObject.toString())) {
                            BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                            if (baseJsonResult.ret) {
                                workWorldResponse = JsonUtils.getGson().fromJson(jsonObject.toString(), WorkWorldResponse.class);
                                IMDatabaseManager.getInstance().InsertWorkWorldByList(workWorldResponse.getData().getNewPost());
                                IMDatabaseManager.getInstance().UpdateWorkWorldDeleteState(workWorldResponse.getData().getDeletePost());
                            }


                        }
                        callback.onCompleted(workWorldResponse);

//
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    callback.onFailure(e.getMessage());
                    Logger.i("获取最新朋友圈接口出错" + e.getMessage());
//                    callback.onFailure("");
                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取最新的帖子
     *
     * @param callback
     */
    public static void loadMoreWorkWorld(int count, int curPostId, final ProtocolCallback.UnitCallback<WorkWorldResponse> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

            String requestUrl = String.format("%s/cricle_camel/post/history", QtalkNavicationService.getInstance().getHttpUrl());
//
            Map<String, Integer> map = new HashMap<>();
            map.put("curPostId", curPostId);
            map.put("pageSize", count);
//            releaseDataRequest.setOwner( CurrentPreference.getInstance().getUserid());
//            releaseDataRequest.setOwner_host(QtalkNavicationService.getInstance().getXmppdomain());
            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(map), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        WorkWorldResponse workWorldResponse = null;
                        if (!TextUtils.isEmpty(jsonObject.toString())) {
                            BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                            if (baseJsonResult.ret) {
                                workWorldResponse = JsonUtils.getGson().fromJson(jsonObject.toString(), WorkWorldResponse.class);
                                IMDatabaseManager.getInstance().InsertWorkWorldByList(workWorldResponse.getData().getNewPost());
                                IMDatabaseManager.getInstance().UpdateWorkWorldDeleteState(workWorldResponse.getData().getDeletePost());

                            }


                        }
                        callback.onCompleted(workWorldResponse);

//
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    callback.onFailure(e.getMessage());
                    Logger.i("获取最新朋友圈接口出错" + e.getMessage());
//                    callback.onFailure("");
                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取加载更多评论
     *
     * @param callback
     */
    public static void loadMoreWorkWorldCommentV2(List<String> hotId, final int count, int curPostId, String postUUID, final String lastCreateTime, final ProtocolCallback.UnitCallback<WorkWorldDetailsCommenData> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

            String requestUrl = String.format("%s/cricle_camel/getHistoryComment/V2", QtalkNavicationService.getInstance().getHttpUrl());
//
            Map<String, Object> map = new HashMap<>();
            map.put("curCommentId", curPostId);
            map.put("pgSize", count);
            map.put("postUUID", postUUID);
            map.put("hotCommentUUID", hotId);
//            releaseDataRequest.setOwner( CurrentPreference.getInstance().getUserid());
//            releaseDataRequest.setOwner_host(QtalkNavicationService.getInstance().getXmppdomain());
            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(map), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        WorkWorldDetailsCommenData workWorldResponse = null;
                        if (!TextUtils.isEmpty(jsonObject.toString())) {
                            BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                            if (baseJsonResult.ret) {
                                workWorldResponse = JsonUtils.getGson().fromJson(jsonObject.toString(), WorkWorldDetailsCommenData.class);
                                IMDatabaseManager.getInstance().InsertWorkWorldCommentDataByList(workWorldResponse.getData().getNewComment());
                                IMDatabaseManager.getInstance().UpdateWorkWorldCommentState(workWorldResponse.getData().getDeleteComments());

                                if (workWorldResponse.getData().getNewComment().size() < count) {
                                    if (workWorldResponse.getData().getNewComment().size() > 0) {
                                        IMDatabaseManager.getInstance().UpdateWorkWorldCommentStateByCreateTime(workWorldResponse.getData().getNewComment().get(workWorldResponse.getData().getNewComment().size() - 1).getCreateTime(), 1);
                                    } else {
                                        IMDatabaseManager.getInstance().UpdateWorkWorldCommentStateByCreateTime(lastCreateTime, 1);
                                    }


                                }
                            }


                        }
                        callback.onCompleted(workWorldResponse);

//
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    callback.onFailure(e.getMessage());
                    Logger.i("获取最新朋友圈接口出错" + e.getMessage());
//                    callback.onFailure("");
                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取加载更多评论
     *
     * @param callback
     */
    public static void loadMoreWorkWorldComment(final int count, int curPostId, String postUUID, final String lastCreateTime, final ProtocolCallback.UnitCallback<WorkWorldDetailsCommenData> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

            String requestUrl = String.format("%s/cricle_camel/getHistoryComment", QtalkNavicationService.getInstance().getHttpUrl());
//
            Map<String, Object> map = new HashMap<>();
            map.put("curCommentId", curPostId);
            map.put("pgSize", count);
            map.put("postUUID", postUUID);
//            releaseDataRequest.setOwner( CurrentPreference.getInstance().getUserid());
//            releaseDataRequest.setOwner_host(QtalkNavicationService.getInstance().getXmppdomain());
            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(map), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        WorkWorldDetailsCommenData workWorldResponse = null;
                        if (!TextUtils.isEmpty(jsonObject.toString())) {
                            BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                            if (baseJsonResult.ret) {
                                workWorldResponse = JsonUtils.getGson().fromJson(jsonObject.toString(), WorkWorldDetailsCommenData.class);
                                IMDatabaseManager.getInstance().InsertWorkWorldCommentDataByList(workWorldResponse.getData().getNewComment());
                                IMDatabaseManager.getInstance().UpdateWorkWorldCommentState(workWorldResponse.getData().getDeleteComments());

                                if (workWorldResponse.getData().getNewComment().size() < count) {
                                    if (workWorldResponse.getData().getNewComment().size() > 0) {
                                        IMDatabaseManager.getInstance().UpdateWorkWorldCommentStateByCreateTime(workWorldResponse.getData().getNewComment().get(workWorldResponse.getData().getNewComment().size() - 1).getCreateTime(), 1);
                                    } else {
                                        IMDatabaseManager.getInstance().UpdateWorkWorldCommentStateByCreateTime(lastCreateTime, 1);
                                    }


                                }
                            }


                        }
                        callback.onCompleted(workWorldResponse);

//
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    callback.onFailure(e.getMessage());
                    Logger.i("获取最新朋友圈接口出错" + e.getMessage());
//                    callback.onFailure("");
                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 设置点赞
     *
     * @param callback
     */
    public static void setLike(SetLikeData setLikeData, final ProtocolCallback.UnitCallback<SetLikeDataResponse> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

            String requestUrl = String.format("%s/cricle_camel/like", QtalkNavicationService.getInstance().getHttpUrl());
//
            setLikeData.setUserId(CurrentPreference.getInstance().getUserid());
            setLikeData.setUserHost(QtalkNavicationService.getInstance().getXmppdomain());
//            Map<String,Integer> map = new HashMap<>();
//            map.put("curPostId",curPostId);
//            map.put("pageSize",count);
//            releaseDataRequest.setOwner( CurrentPreference.getInstance().getUserid());
//            releaseDataRequest.setOwner_host(QtalkNavicationService.getInstance().getXmppdomain());
            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(setLikeData), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        SetLikeDataResponse workWorldResponse = null;
                        if (!TextUtils.isEmpty(jsonObject.toString())) {
                            BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                            if (baseJsonResult.ret) {
                                workWorldResponse = JsonUtils.getGson().fromJson(jsonObject.toString(), SetLikeDataResponse.class);
                                IMDatabaseManager.getInstance().UpdateWorkWorldLikeState(workWorldResponse);

                            }


                        }
                        callback.onCompleted(workWorldResponse);

//
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    callback.onFailure(e.getMessage());
                    Logger.i("获取最新点赞接口出错" + e.getMessage());
//                    callback.onFailure("");
                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 删除评论
     *
     * @param callback
     */
    public static void deleteWorkWorldCommentItem(String uuid, String postUUID, final ProtocolCallback.UnitCallback<WorkWorldDeleteResponse> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

            String requestUrl = String.format("%s/cricle_camel/deleteComment", QtalkNavicationService.getInstance().getHttpUrl());
            Map<String, String> map = new HashMap<>();
            map.put("commentUUID", uuid);
            map.put("postUUID", postUUID);
//            map.put("pageSize",count);
//            releaseDataRequest.setOwner( CurrentPreference.getInstance().getUserid());
//            releaseDataRequest.setOwner_host(QtalkNavicationService.getInstance().getXmppdomain());
            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(map), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {

                        WorkWorldDeleteResponse workWorldResponse = null;
                        if (!TextUtils.isEmpty(jsonObject.toString())) {
                            BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                            if (baseJsonResult.ret) {
                                workWorldResponse = JsonUtils.getGson().fromJson(jsonObject.toString(), WorkWorldDeleteResponse.class);
//                                List<WorkWorldDeleteResponse.Data> list = new ArrayList<>();
//                                list.add(workWorldResponse.getData());
//                                IMDatabaseManager.getInstance().UpdateWorkWorldCommentDeleteState(workWorldResponse.getData().getSuperParentComment());
//                                IMDatabaseManager.getInstance().UpdateWorkWorldCommentDeleteState(workWorldResponse.getData().getChildComment());

                            }


                        }
                        callback.onCompleted(workWorldResponse);

//
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    callback.onFailure(e.getMessage());
                    Logger.i("获取最新点赞接口出错" + e.getMessage());
//                    callback.onFailure("");
                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 删除评论V2
     *
     * @param callback
     */
    public static void deleteWorkWorldCommentItemV2(String superUUID, String uuid, String postUUID, final ProtocolCallback.UnitCallback<WorkWorldDeleteResponse> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

            String requestUrl = String.format("%s/cricle_camel/deleteComment/V2", QtalkNavicationService.getInstance().getHttpUrl());
            Map<String, String> map = new HashMap<>();
            map.put("commentUUID", uuid);
            map.put("postUUID", postUUID);
            if (!TextUtils.isEmpty(superUUID)) {
                map.put("superParentUUID", superUUID);
            }

//            map.put("pageSize",count);
//            releaseDataRequest.setOwner( CurrentPreference.getInstance().getUserid());
//            releaseDataRequest.setOwner_host(QtalkNavicationService.getInstance().getXmppdomain());
            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(map), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {

                        WorkWorldDeleteResponse workWorldResponse = null;
                        if (!TextUtils.isEmpty(jsonObject.toString())) {
                            BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                            if (baseJsonResult.ret) {
                                //todo 这里面都是 单体对象 所以要改
                                workWorldResponse = JsonUtils.getGson().fromJson(jsonObject.toString(), WorkWorldDeleteResponse.class);
                                List<WorkWorldDeleteResponse.CommentDeleteInfo> list = new ArrayList<>();
//                                List<WorkWorldDeleteResponse.Data> list = new ArrayList<>();
//                                list.add(workWorldResponse.getData());
//                                list.add(workWorldResponse.getData().getSuperParentComment());
//                                list.add(workWorldResponse.getData().getChildComment());
                                IMDatabaseManager.getInstance().UpdateWorkWorldCommentDeleteState(workWorldResponse.getData().getDeleteCommentData());
//                                IMDatabaseManager.getInstance().UpdateWorkWorldCommentDeleteState();

                            }


                        }
                        callback.onCompleted(workWorldResponse);

//
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    callback.onFailure(e.getMessage());
                    Logger.i("获取最新点赞接口出错" + e.getMessage());
//                    callback.onFailure("");
                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 删除帖子
     *
     * @param callback
     */
    public static void deleteWorkWorldItem(String uuid, final ProtocolCallback.UnitCallback<WorkWorldDeleteResponse> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

            String requestUrl = String.format("%s/cricle_camel/deletePost", QtalkNavicationService.getInstance().getHttpUrl());
            Map<String, String> map = new HashMap<>();
            map.put("uuid", uuid);
//            map.put("pageSize",count);
//            releaseDataRequest.setOwner( CurrentPreference.getInstance().getUserid());
//            releaseDataRequest.setOwner_host(QtalkNavicationService.getInstance().getXmppdomain());
            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(map), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        WorkWorldDeleteResponse workWorldResponse = null;
                        if (!TextUtils.isEmpty(jsonObject.toString())) {
                            BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                            if (baseJsonResult.ret) {
                                workWorldResponse = JsonUtils.getGson().fromJson(jsonObject.toString(), WorkWorldDeleteResponse.class);
                                List<WorkWorldDeleteResponse.Data> list = new ArrayList<>();
                                list.add(workWorldResponse.getData());
                                IMDatabaseManager.getInstance().UpdateWorkWorldDeleteState(list);

                            }


                        }
                        callback.onCompleted(workWorldResponse);

//
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    callback.onFailure(e.getMessage());
                    Logger.i("获取最新点赞接口出错" + e.getMessage());
//                    callback.onFailure("");
                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 发布评论V2
     *
     * @param callback
     */
    public static void releaseCommentV2(WorkWorldNewCommentBean data, final ProtocolCallback.UnitCallback<WorkWorldDetailsCommenData> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

            String requestUrl = String.format("%s/cricle_camel/uploadComment/V2", QtalkNavicationService.getInstance().getHttpUrl());
//            Map<String,String> map = new HashMap<>();
            data.setFromUser(CurrentPreference.getInstance().getUserid());
            data.setFromHost(QtalkNavicationService.getInstance().getXmppdomain());
//            map.put("uuid",uuid);
//            map.put("pageSize",count);
//            releaseDataRequest.setOwner( CurrentPreference.getInstance().getUserid());
//            releaseDataRequest.setOwner_host(QtalkNavicationService.getInstance().getXmppdomain());
            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(data), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        WorkWorldDetailsCommenData workWorldResponse = null;
                        if (!TextUtils.isEmpty(jsonObject.toString())) {
                            BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                            if (baseJsonResult.ret) {


                                workWorldResponse = JsonUtils.getGson().fromJson(jsonObject.toString(), WorkWorldDetailsCommenData.class);
//                                List<WorkWorldDeleteResponse.Data> list = new ArrayList<>();
//                                list.add(workWorldResponse.getData());
                                IMDatabaseManager.getInstance().UpdateWorkWorldCommentState(workWorldResponse.getData().getDeleteComments());
                                IMDatabaseManager.getInstance().InsertWorkWorldCommentDataByList(workWorldResponse.getData().getNewComment());
                                callback.onCompleted(workWorldResponse);
                            } else {
                                callback.onFailure("error");
                            }


                        } else {
                            callback.onFailure("error");
                        }


//
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    callback.onFailure(e.getMessage());
                    Logger.i("获取最新点赞接口出错" + e.getMessage());
//                    callback.onFailure("");
                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 发布评论
     *
     * @param callback
     */
    public static void releaseComment(WorkWorldNewCommentBean data, final ProtocolCallback.UnitCallback<WorkWorldDetailsCommenData> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

            String requestUrl = String.format("%s/cricle_camel/uploadComment", QtalkNavicationService.getInstance().getHttpUrl());
//            Map<String,String> map = new HashMap<>();
            data.setFromUser(CurrentPreference.getInstance().getUserid());
            data.setFromHost(QtalkNavicationService.getInstance().getXmppdomain());
//            map.put("uuid",uuid);
//            map.put("pageSize",count);
//            releaseDataRequest.setOwner( CurrentPreference.getInstance().getUserid());
//            releaseDataRequest.setOwner_host(QtalkNavicationService.getInstance().getXmppdomain());
            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(data), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        WorkWorldDetailsCommenData workWorldResponse = null;
                        if (!TextUtils.isEmpty(jsonObject.toString())) {
                            BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                            if (baseJsonResult.ret) {
                                workWorldResponse = JsonUtils.getGson().fromJson(jsonObject.toString(), WorkWorldDetailsCommenData.class);
//                                List<WorkWorldDeleteResponse.Data> list = new ArrayList<>();
//                                list.add(workWorldResponse.getData());
                                IMDatabaseManager.getInstance().UpdateWorkWorldCommentState(workWorldResponse.getData().getDeleteComments());
                                IMDatabaseManager.getInstance().InsertWorkWorldCommentDataByList(workWorldResponse.getData().getNewComment());

                            }


                        }
                        callback.onCompleted(workWorldResponse);

//
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    callback.onFailure(e.getMessage());
                    Logger.i("获取最新点赞接口出错" + e.getMessage());
//                    callback.onFailure("");
                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取最新的评论v2
     *
     * @param callback
     */
    public static void refreshWorkWorldNewCommentV2(List<String> hotId, final int count, String uuid, final ProtocolCallback.UnitCallback<WorkWorldDetailsCommenData> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

            String requestUrl = String.format("%s/cricle_camel/getNewComment/V2", QtalkNavicationService.getInstance().getHttpUrl());
//
            Map<String, Object> map = new HashMap<>();
            map.put("pgSize", count);
            map.put("postUUID", uuid);
            map.put("hotCommentUUID", hotId);
//            releaseDataRequest.setOwner( CurrentPreference.getInstance().getUserid());
//            releaseDataRequest.setOwner_host(QtalkNavicationService.getInstance().getXmppdomain());
            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(map), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        WorkWorldDetailsCommenData workWorldResponse = null;
                        if (!TextUtils.isEmpty(jsonObject.toString())) {
                            BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                            if (baseJsonResult.ret) {
                                workWorldResponse = JsonUtils.getGson().fromJson(jsonObject.toString(), WorkWorldDetailsCommenData.class);

                                IMDatabaseManager.getInstance().InsertWorkWorldCommentDataByList(workWorldResponse.getData().getNewComment());
                                IMDatabaseManager.getInstance().UpdateWorkWorldCommentState(workWorldResponse.getData().getDeleteComments());
                                if (workWorldResponse.getData().getNewComment().size() < count) {
                                    if (workWorldResponse.getData().getNewComment().size() > 0) {
                                        IMDatabaseManager.getInstance().UpdateWorkWorldCommentStateByCreateTime(workWorldResponse.getData().getNewComment().get(workWorldResponse.getData().getNewComment().size() - 1).getCreateTime(), 1);
                                    } else {
                                        IMDatabaseManager.getInstance().UpdateWorkWorldCommentStateByCreateTime(String.valueOf(System.currentTimeMillis()), 1);
                                    }


                                }
                            }


                        }
                        callback.onCompleted(workWorldResponse);

//
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    callback.onFailure(e.getMessage());
                    Logger.i("获取最新朋友圈评论接口出错" + e.getMessage());
//                    callback.onFailure("");
                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取最新的评论
     *
     * @param callback
     */
    public static void refreshWorkWorldNewComment(final int count, String uuid, final ProtocolCallback.UnitCallback<WorkWorldDetailsCommenData> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

            String requestUrl = String.format("%s/cricle_camel/getNewComment", QtalkNavicationService.getInstance().getHttpUrl());
//
            Map<String, Object> map = new HashMap<>();
            map.put("pgSize", count);
            map.put("postUUID", uuid);
//            releaseDataRequest.setOwner( CurrentPreference.getInstance().getUserid());
//            releaseDataRequest.setOwner_host(QtalkNavicationService.getInstance().getXmppdomain());
            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(map), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        WorkWorldDetailsCommenData workWorldResponse = null;
                        if (!TextUtils.isEmpty(jsonObject.toString())) {
                            BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                            if (baseJsonResult.ret) {
                                workWorldResponse = JsonUtils.getGson().fromJson(jsonObject.toString(), WorkWorldDetailsCommenData.class);
                                IMDatabaseManager.getInstance().InsertWorkWorldCommentDataByList(workWorldResponse.getData().getNewComment());
                                IMDatabaseManager.getInstance().UpdateWorkWorldCommentState(workWorldResponse.getData().getDeleteComments());
                                if (workWorldResponse.getData().getNewComment().size() < count) {
                                    if (workWorldResponse.getData().getNewComment().size() > 0) {
                                        IMDatabaseManager.getInstance().UpdateWorkWorldCommentStateByCreateTime(workWorldResponse.getData().getNewComment().get(workWorldResponse.getData().getNewComment().size() - 1).getCreateTime(), 1);
                                    } else {
                                        IMDatabaseManager.getInstance().UpdateWorkWorldCommentStateByCreateTime(String.valueOf(System.currentTimeMillis()), 1);
                                    }


                                }
                            }


                        }
                        callback.onCompleted(workWorldResponse);

//
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    callback.onFailure(e.getMessage());
                    Logger.i("获取最新朋友圈评论接口出错" + e.getMessage());
//                    callback.onFailure("");
                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取最热的评论V2
     *
     * @param callback
     */
    public static void refreshWorkWorldNewCommentHotV2(int count, String uuid, final ProtocolCallback.UnitCallback<WorkWorldDetailsCommentHotData> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

            String requestUrl = String.format("%s/cricle_camel/getHotComment/V2", QtalkNavicationService.getInstance().getHttpUrl());
//
            Map<String, Object> map = new HashMap<>();
            //不再需要传数量 由服务器控制
            //  map.put("itemNum", count);
            map.put("uuid", uuid);
//            releaseDataRequest.setOwner( CurrentPreference.getInstance().getUserid());
//            releaseDataRequest.setOwner_host(QtalkNavicationService.getInstance().getXmppdomain());
            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(map), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        WorkWorldDetailsCommentHotData data = null;
                        if (!TextUtils.isEmpty(jsonObject.toString())) {
                            BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                            if (baseJsonResult.ret) {
                                data = JsonUtils.getGson().fromJson(jsonObject.toString(), WorkWorldDetailsCommentHotData.class);

                                IMDatabaseManager.getInstance().InsertWorkWorldCommentDataByList(data.getData().getNewComment());
                            }


                        }
                        callback.onCompleted(data);

//
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    callback.onFailure(e.getMessage());
                    Logger.i("获取最新朋友圈评论接口出错" + e.getMessage());
//                    callback.onFailure("");
                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取最热的评论
     *
     * @param callback
     */
    public static void refreshWorkWorldNewCommentHot(int count, String uuid, final ProtocolCallback.UnitCallback<WorkWorldDetailsCommentHotData> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

            String requestUrl = String.format("%s/cricle_camel/getHotComment", QtalkNavicationService.getInstance().getHttpUrl());
//
            Map<String, Object> map = new HashMap<>();
            map.put("item", count);
            map.put("uuid", uuid);
//            releaseDataRequest.setOwner( CurrentPreference.getInstance().getUserid());
//            releaseDataRequest.setOwner_host(QtalkNavicationService.getInstance().getXmppdomain());
            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(map), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        WorkWorldDetailsCommentHotData data = null;
                        if (!TextUtils.isEmpty(jsonObject.toString())) {
                            BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                            if (baseJsonResult.ret) {
                                data = JsonUtils.getGson().fromJson(jsonObject.toString(), WorkWorldDetailsCommentHotData.class);

//                                IMDatabaseManager.getInstance().InsertWorkWorldCommentDataByList(data.getData());
                            }


                        }
                        callback.onCompleted(data);

//
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    callback.onFailure(e.getMessage());
                    Logger.i("获取最新朋友圈评论接口出错" + e.getMessage());
//                    callback.onFailure("");
                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取指定id帖子
     *
     * @param callback
     */
    public static void getWorkWorldItemByUUID(String uuid, final ProtocolCallback.UnitCallback<WorkWorldSingleResponse> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

            String requestUrl = String.format("%s/cricle_camel/getPostDetail", QtalkNavicationService.getInstance().getHttpUrl());
//
            Map<String, Object> map = new HashMap<>();
            map.put("uuid", uuid);
//            releaseDataRequest.setOwner( CurrentPreference.getInstance().getUserid());
//            releaseDataRequest.setOwner_host(QtalkNavicationService.getInstance().getXmppdomain());
            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(map), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        WorkWorldSingleResponse data = null;
                        if (!TextUtils.isEmpty(jsonObject.toString())) {
                            BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                            if (baseJsonResult.ret) {
                                data = JsonUtils.getGson().fromJson(jsonObject.toString(), WorkWorldSingleResponse.class);
                                List<WorkWorldItem> list = new ArrayList<>();
                                list.add(data.getData());
                                IMDatabaseManager.getInstance().InsertWorkWorldByList(list);
                            }


                        }
                        callback.onCompleted(data);

//
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    callback.onFailure(e.getMessage());
                    Logger.i("获取指定朋友圈接口出错" + e.getMessage());
//                    callback.onFailure("");
                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取指定id帖子
     *
     * @param callback
     */
    public static void getWorkWorldHistory(String messageId, String messageTime, final ProtocolCallback.UnitCallback<WorkWorldNoticeHistoryResponse> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

            String requestUrl = String.format("%s/cricle_camel/message/getMessageList", QtalkNavicationService.getInstance().getHttpUrl());
//
            Map<String, Object> map = new HashMap<>();


            map.put("user", CurrentPreference.getInstance().getUserid());
            map.put("userHost", QtalkNavicationService.getInstance().getXmppdomain());
            map.put("messageId", messageId);
            map.put("messageTime", Long.parseLong(messageTime));
//            releaseDataRequest.setOwner( CurrentPreference.getInstance().getUserid());
//            releaseDataRequest.setOwner_host(QtalkNavicationService.getInstance().getXmppdomain());
            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(map), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        WorkWorldNoticeHistoryResponse data = null;
                        if (!TextUtils.isEmpty(jsonObject.toString())) {
                            BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                            if (baseJsonResult.ret) {
                                data = JsonUtils.getGson().fromJson(jsonObject.toString(), WorkWorldNoticeHistoryResponse.class);
//                                List<> list = new ArrayList<>();
//                                list.add(data.getData());
                                IMDatabaseManager.getInstance().InsertWorkWorldNoticeByList(data.getData().getMsgList(), false);
                            }


                        }
                        callback.onCompleted(data);

//
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    callback.onFailure(e.getMessage());
                    Logger.i("获取指定朋友圈接口出错" + e.getMessage());
//                    callback.onFailure("");
                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取指定id帖子
     *
     * @param callback
     */
    public static void setWorkWorldNoticeReadTime(final String messageTime, final ProtocolCallback.UnitCallback<WorkWorldNoticeHistoryResponse> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

            String requestUrl = String.format("%s/cricle_camel/message/readMark", QtalkNavicationService.getInstance().getHttpUrl());
//
            Map<String, Object> map = new HashMap<>();


            map.put("time", Long.parseLong(messageTime));
//            releaseDataRequest.setOwner( CurrentPreference.getInstance().getUserid());
//            releaseDataRequest.setOwner_host(QtalkNavicationService.getInstance().getXmppdomain());
            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(map), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        WorkWorldNoticeHistoryResponse data = null;
                        if (!TextUtils.isEmpty(jsonObject.toString())) {
                            BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                            if (baseJsonResult.ret) {
                                IMDatabaseManager.getInstance().UpdateWorkWorldNoticeReadTime(messageTime);
                                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.WORK_WORLD_NOTICE);
//                                data = JsonUtils.getGson().fromJson(jsonObject.toString(), WorkWorldNoticeHistoryResponse.class);
////                                List<> list = new ArrayList<>();
////                                list.add(data.getData());
//                                IMDatabaseManager.getInstance().InsertWorkWorldNoticeByList(data.getData().getMsgList());
                            }


                        }
                        callback.onCompleted(data);

//
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    callback.onFailure(e.getMessage());
                    Logger.i("获取指定朋友圈接口出错" + e.getMessage());
//                    callback.onFailure("");
                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 检查用户是否有用朋友圈权限
     */
    public static void checkWorkWorldPermissions(final ProtocolCallback.UnitCallback<Boolean> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }


        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

            String requestUrl = String.format("%s/cricle_camel/entrance", QtalkNavicationService.getInstance().getHttpUrl());
//
            QtalkHttpService.asyncGetJson(requestUrl, cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    BaseJsonResult data = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
//                    if (data.ret) {
                    callback.onCompleted(data.ret);
//                    }


                    Logger.i("getBindUser:" + jsonObject.toString());
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    Logger.i("检查权限失败");
                }
            });
        } catch (Exception e) {
            Logger.i("检查朋友圈权限出错" + e.getMessage());
        }
    }


    /**
     * 检查用户是否有用朋友圈权限V2
     */
    public static void checkWorkWorldPermissionsV2(final ProtocolCallback.UnitCallback<Boolean> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }


        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

            String requestUrl = String.format("%s/cricle_camel/entranceV2", QtalkNavicationService.getInstance().getHttpUrl());
//
            QtalkHttpService.asyncGetJson(requestUrl, cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    BaseJsonResult data = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
//                    if (data.ret) {
                    if (data.ret) {
                        WorkWorldEntrance entrance = JsonUtils.getGson().fromJson(jsonObject.toString(), WorkWorldEntrance.class);
                        //todo 等待接口完成 2019.5.6
                        callback.onCompleted(entrance.getData().isAuthSign());
                    }


                    Logger.i("getBindUser:" + jsonObject.toString());
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    Logger.i("检查权限失败");
                }
            });
        } catch (Exception e) {
            Logger.i("检查朋友圈权限出错" + e.getMessage());
        }
    }


    /**
     * 设置用户配置
     *
     * @param callback
     */
    public static void setWorkWorldRemind(int isopen, final ProtocolCallback.UnitCallback<SetWorkWorldRemindResponse> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");
            String requestUrl = String.format("%s/cricle_camel/notify_config/updateNotifyConfig", QtalkNavicationService.getInstance().getHttpUrl());
//

            Map<String, Object> map = new HashMap<>();
            map.put("notifyUser", CurrentPreference.getInstance().getUserid());
            map.put("host", QtalkNavicationService.getInstance().getXmppdomain());
            map.put("flag", isopen);

            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(map), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        Logger.i("新版个人配置接口set:" + jsonObject.toString());
                        BaseJsonResult baseJson = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                        if (baseJson.ret) {
                            SetWorkWorldRemindResponse nrc = JsonUtils.getGson().fromJson(jsonObject.toString(), SetWorkWorldRemindResponse.class);
                            IMDatabaseManager.getInstance().InsertWorkWorldRemind(nrc.getData().getFlag() == 1);
                            IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Work_World_Remind);
                            callback.onCompleted(nrc);


                        } else {
                            callback.onFailure("");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    Logger.i("新版个人配置接口set" + e.getMessage());
                    callback.onFailure("");

                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取用户驼圈提醒配置
     *
     * @param callback
     */
    public static void getWorkWorldRemind(final ProtocolCallback.UnitCallback<SetWorkWorldRemindResponse> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");
            String requestUrl = String.format("%s/cricle_camel/notify_config/getNotifyConfig", QtalkNavicationService.getInstance().getHttpUrl());
//
            Map<String, Object> map = new HashMap<>();
            map.put("notifyUser", CurrentPreference.getInstance().getUserid());
            map.put("host", QtalkNavicationService.getInstance().getXmppdomain());

            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(map), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        Logger.i("新版个人配置接口set:" + jsonObject.toString());
                        BaseJsonResult baseJson = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                        if (baseJson.ret) {
                            SetWorkWorldRemindResponse nrc = JsonUtils.getGson().fromJson(jsonObject.toString(), SetWorkWorldRemindResponse.class);
                            IMDatabaseManager.getInstance().InsertWorkWorldRemind(nrc.getData().getFlag() == 1);
                            IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Work_World_Remind);
                            callback.onCompleted(nrc);


                        } else {
                            callback.onFailure("");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    Logger.i("新版个人配置接口set" + e.getMessage());
                    callback.onFailure("");

                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 进行发布帖子V2
     */
    public static void PostUrl(String requestUrl, Map<String, Object> params, Map<String, Object> cookie, final ProtocolCallback.UnitCallback<String> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            Map<String, String> cookies = new HashMap<>();
            for (Map.Entry<String, Object> entry : cookie.entrySet()) {
                cookies.put(entry.getKey(), entry.getValue() + "");
            }
            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(params), cookies, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) {
                    try {
                        if (!TextUtils.isEmpty(jsonObject.toString())) {

                            BaseJsonResult baseJson = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                            if (baseJson.ret) {
                                callback.onCompleted(jsonObject.toString());
                            } else {
                                callback.onFailure(baseJson.errmsg);
                            }
                        } else {
                            callback.onFailure("The request failed");
                        }

//
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    callback.onFailure(e.getMessage());
                    Logger.i("接口出错" + e.getMessage());
//                    callback.onFailure("");
                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void fileDownload(String fileUrl, final String fileName, FileProgressResponseBody.ProgressResponseListener progressResponseListener, final ProtocolCallback.UnitCallback<DownLoadFileResponse> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");
            QtalkHttpService.downLoad(fileUrl, cookie, progressResponseListener, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Logger.i("文件下载失败");
                    callback.onFailure(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response == null || response.code() != 200) {

                        callback.onFailure("下载失败");
                        return;
                    }
                    Logger.i("http文件下载成功");
                    InputStream is = null;
                    byte[] buf = new byte[2048];
                    int len = 0;
                    FileOutputStream fos = null;
                    //储存下载文件的目录
                    try {
                        is = response.body().byteStream();
                        long total = response.body().contentLength();
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
//                        File file=new File(savePath,getNameFromUrl(url));
                        File file = new File(path, fileName);
                        fos = new FileOutputStream(file);
                        long sum = 0;
                        while ((len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                            ;
                        }
                        fos.flush();
                        DownLoadFileResponse downLoadFileResponse = new DownLoadFileResponse();
                        downLoadFileResponse.setFileName(fileName);
                        downLoadFileResponse.setFilePath(file.getAbsolutePath());
                        downLoadFileResponse.setFileMd5(com.qunar.im.utils.FileUtils.getFileMD5(file));
                        callback.onCompleted(downLoadFileResponse);
                        Logger.i("文件保存完成:" + file.getAbsolutePath());
                        //下载完成
                    } catch (Exception e) {
                        callback.onFailure(e.getMessage());
                        Logger.i("文件下载失败:" + e.getMessage());
                    } finally {
                        try {
                            if (is != null)
                                is.close();
                        } catch (IOException e) {

                        }
                        try {
                            if (fos != null) {
                                fos.close();
                            }
                        } catch (IOException e) {

                        }
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(e.getMessage());
        }
    }


    /**
     * 文件上传接口
     *
     * @param progressRequestListener
     * @param callback
     * @param needTrans
     */
    public static void videoUpLoad(String filePath, final FileProgressRequestBody.ProgressRequestListener progressRequestListener, boolean needTrans, final ProtocolCallback.UnitCallback<VideoDataResponse> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");
            Map<String, String> params = new HashMap<>();
            boolean high = IMUserDefaults.getStandardUserDefaults().getBooleanValue(CommonConfig.globalContext,
                    com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                            + QtalkNavicationService.getInstance().getXmppdomain()
                            + CommonConfig.isDebug
                            + "videoHigh", false);
            params.put("highDefinition", high + "");
            params.put("needTrans", needTrans + "");

            String requestUrl = String.format("%s/video/upload", QtalkNavicationService.getInstance().getHttpUrl());
//
            QtalkHttpService.upLoadFile(requestUrl, filePath, params, cookie, progressRequestListener, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        VideoDataResponse videoDataResponse = null;
                        if (!TextUtils.isEmpty(jsonObject.toString())) {
                            BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                            if (baseJsonResult.ret) {

                                videoDataResponse = JsonUtils.getGson().fromJson(jsonObject.toString(), VideoDataResponse.class);
                                if (videoDataResponse.getData().isReady()) {
                                    callback.onCompleted(videoDataResponse);
                                } else {
                                    //此时应该发起文件上传接口
                                    callback.onFailure("数据未准备成功");
                                }
                            } else {
                                callback.onFailure(baseJsonResult.errmsg);
                            }


                        }
//                        callback.onCompleted(videoDataResponse);

//
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    callback.onFailure(e.getMessage());
                    Logger.i("获取最新朋友圈接口出错" + e.getMessage());
//                    callback.onFailure("");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传视频检查接口
     *  @param needTrans
     * @param callback
     */
    public static void videoCheckAndUpload(final String filePath, final boolean needTrans, final FileProgressRequestBody.ProgressRequestListener progressRequestListener, final ProtocolCallback.UnitCallback<VideoDataResponse> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

            String requestUrl = String.format("%s/video/check", QtalkNavicationService.getInstance().getHttpUrl());
//
            Map<String, Object> map = new HashMap<>();
            map.put("videoMd5", com.qunar.im.utils.FileUtils.getFileMD5(new File(filePath)));


//            releaseDataRequest.setOwner( CurrentPreference.getInstance().getUserid());
//            releaseDataRequest.setOwner_host(QtalkNavicationService.getInstance().getXmppdomain());

            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(map), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        VideoDataResponse videoDataResponse = null;
                        if (!TextUtils.isEmpty(jsonObject.toString())) {
                            BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                            Logger.i("上传视频检查:" + jsonObject.toString());
                            if (baseJsonResult.ret) {

                                videoDataResponse = JsonUtils.getGson().fromJson(jsonObject.toString(), VideoDataResponse.class);
                                if (videoDataResponse.getData().isReady()) {
                                    callback.onCompleted(videoDataResponse);
                                } else {
                                    //此时应该发起文件上传接口
                                    videoUpLoad(filePath, progressRequestListener,needTrans,callback );
                                }
                            } else {
                                callback.onFailure(baseJsonResult.errmsg);
                            }


                        }
//                        callback.onCompleted(videoDataResponse);

//
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    callback.onFailure(e.getMessage());
                    Logger.i("获取最新朋友圈接口出错" + e.getMessage());
//                    callback.onFailure("");
                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获得用户上传视频权限
     *
     * @param callback
     */
    public static void videoSetting(final ProtocolCallback.UnitCallback<VideoSetting> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

            String requestUrl = String.format("%s/video/getUserVideoConfig", QtalkNavicationService.getInstance().getHttpUrl());
//

//            releaseDataRequest.setOwner( CurrentPreference.getInstance().getUserid());
//            releaseDataRequest.setOwner_host(QtalkNavicationService.getInstance().getXmppdomain());
            QtalkHttpService.asyncPostJsonforString(requestUrl, "", cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        VideoSetting videoDataResponse = null;
                        if (!TextUtils.isEmpty(jsonObject.toString())) {
                            BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                            if (baseJsonResult.ret) {

                                videoDataResponse = JsonUtils.getGson().fromJson(jsonObject.toString(), VideoSetting.class);


                                IMUserDefaults.getStandardUserDefaults().newEditor(CommonConfig.globalContext)
                                        .putObject(com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                                                + QtalkNavicationService.getInstance().getXmppdomain()
                                                + CommonConfig.isDebug

                                                + "videoMaxTime", videoDataResponse.getData().getVideoMaxTimeLen()+"")
                                        .synchronize();

                                IMUserDefaults.getStandardUserDefaults().newEditor(CommonConfig.globalContext)
                                        .putObject(com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                                                + QtalkNavicationService.getInstance().getXmppdomain()
                                                + CommonConfig.isDebug

                                                + "videoUseAble", videoDataResponse.getData().isUseAble())
                                        .synchronize();


                                IMUserDefaults.getStandardUserDefaults().newEditor(CommonConfig.globalContext)
                                        .putObject(com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                                                + QtalkNavicationService.getInstance().getXmppdomain()
                                                + CommonConfig.isDebug

                                                + "videoHigh", videoDataResponse.getData().isHighDefinition())
                                        .synchronize();

                                IMUserDefaults.getStandardUserDefaults().newEditor(CommonConfig.globalContext)
                                        .putObject(com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                                                + QtalkNavicationService.getInstance().getXmppdomain()
                                                + CommonConfig.isDebug

                                                + "videoSize", videoDataResponse.getData().getVideoFileSize() + "")
                                        .synchronize();

                                IMUserDefaults.getStandardUserDefaults().newEditor(CommonConfig.globalContext)
                                        .putObject(com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                                                + QtalkNavicationService.getInstance().getXmppdomain()
                                                + CommonConfig.isDebug

                                                + "videoTime", videoDataResponse.getData().getVideoTimeLen() + "")
                                        .synchronize();
                                callback.onCompleted(videoDataResponse);

//                                if(videoDataResponse.getData().isReady()){
////                                    callback.onCompleted(videoDataResponse);
////                                }else{
////                                    //此时应该发起文件上传接口
////                                    videoUpLoad(filePath,progressRequestListener,callback);
////                                }
                            } else {
                                callback.onFailure(baseJsonResult.errmsg);
                            }


                        }
//                        callback.onCompleted(videoDataResponse);

//
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    callback.onFailure(e.getMessage());
                    Logger.i("获取视频权限接口出错:" + e.getMessage());
//                    callback.onFailure("");
                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void getUrl(String url, final ProtocolCallback.UnitCallback<Boolean> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

            String requestUrl = url;
            QtalkHttpService.asyncGetJson(requestUrl, cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {

                        callback.onCompleted(true);
//
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("");
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    callback.onCompleted(false);
                    Logger.i("小拿进会话接口" + e.getMessage());
//                    callback.onFailure("");
                }


            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 获取全部勋章列表
     *  @param
     * @param callback
     */
    public static void getMedal(long version,  final ProtocolCallback.UnitCallback<MedalListResponse> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

            String requestUrl = String.format("%s/medal/medalList.qunar", QtalkNavicationService.getInstance().getHttpUrl());
            Map<String, Object> map = new HashMap<>();
            map.put("version",version+"");
//            map.put()

//            releaseDataRequest.setOwner( CurrentPreference.getInstance().getUserid());
//            releaseDataRequest.setOwner_host(QtalkNavicationService.getInstance().getXmppdomain());

            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(map), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        MedalListResponse medalListResponse = null;
                        if (!TextUtils.isEmpty(jsonObject.toString())) {
                            BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                            if (baseJsonResult.ret) {
                                medalListResponse = JsonUtils.getGson().fromJson(jsonObject.toString(),MedalListResponse.class);
                                IMDatabaseManager.getInstance().InsertMedalList(medalListResponse);

                                IMDatabaseManager.getInstance().updateMedalListVersion(medalListResponse.getData().getVersion());
                            }
                        }

//                        new String();
//                        callback.onCompleted(videoDataResponse);

//
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    callback.onFailure(e.getMessage());
                    Logger.i("获取最新朋友圈接口出错" + e.getMessage());
//                    callback.onFailure("");
                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 获取勋章用户状态
     *  @param
     * @param callback
     */
    public static void getUserMedalStatus(long version,  final ProtocolCallback.UnitCallback<MedalUserStatusResponse> callback) {
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

            String requestUrl = String.format("%s/medal/userMedalList.qunar", QtalkNavicationService.getInstance().getHttpUrl());
//
            Map<String, Object> map = new HashMap<>();
            map.put("version",version+"");
//            map.put()

//            releaseDataRequest.setOwner( CurrentPreference.getInstance().getUserid());
//            releaseDataRequest.setOwner_host(QtalkNavicationService.getInstance().getXmppdomain());

            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(map), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        MedalUserStatusResponse medalListResponse = null;
                        if (!TextUtils.isEmpty(jsonObject.toString())) {
                            BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                            if (baseJsonResult.ret) {
                                medalListResponse = JsonUtils.getGson().fromJson(jsonObject.toString(),MedalUserStatusResponse.class);
                                IMDatabaseManager.getInstance().InsertUserMedalStatusList(medalListResponse);
                                IMLogicManager.getInstance().deleteMedalCache(medalListResponse);
                                IMDatabaseManager.getInstance().updateUserMedalStatusListVersion(medalListResponse.getData().getVersion());
                            }
                        }

//                        new String();
//                        callback.onCompleted(videoDataResponse);

//
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    callback.onFailure(e.getMessage());
                    Logger.i("获取最新朋友圈接口出错" + e.getMessage());
//                    callback.onFailure("");
                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改勋章佩戴状态
     * @param status 状态
     * @param medalId 勋章id
     */
    public static void userMedalStatusModifyWithStatus(int status,int medalId,final ProtocolCallback.UnitCallback<MedalSingleUserStatusResponse> callback){
        if (TextUtils.isEmpty(IMLogicManager.getInstance().getRemoteLoginKey())) {
            callback.doFailure();
            return;
        }
        try {
            String q_ckey = Protocol.getCKEY();
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie", "q_ckey=" + q_ckey + ";");

            String requestUrl = String.format("%s/medal/userMedalStatusModify.qunar", QtalkNavicationService.getInstance().getHttpUrl());
//
            Map<String, Object> map = new HashMap<>();
            map.put("userId",CurrentPreference.getInstance().getUserid());
            map.put("host",QtalkNavicationService.getInstance().getXmppdomain());
            map.put("medalStatus",status);
            map.put("medalId",medalId);

//            map.put()

//            releaseDataRequest.setOwner( CurrentPreference.getInstance().getUserid());
//            releaseDataRequest.setOwner_host(QtalkNavicationService.getInstance().getXmppdomain());

            QtalkHttpService.asyncPostJsonforString(requestUrl, JsonUtils.getGson().toJson(map), cookie, new QtalkHttpService.CallbackJson() {
                @Override
                public void onJsonResponse(JSONObject jsonObject) throws JSONException {
                    try {
                        new String();
                        MedalSingleUserStatusResponse medalSingleUserStatusResponse = null;
                        if (!TextUtils.isEmpty(jsonObject.toString())) {
                            BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
                            if (baseJsonResult.ret) {
                                medalSingleUserStatusResponse = JsonUtils.getGson().fromJson(jsonObject.toString(),MedalSingleUserStatusResponse.class);
                                IMDatabaseManager.getInstance().updateUserMedalStatus(medalSingleUserStatusResponse);
                                callback.onCompleted(medalSingleUserStatusResponse);

                                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.UPDATE_MEDAL_SELF);
//                                IMDatabaseManager.getInstance().InsertUserMedalStatusList(medalListResponse);

//                                IMDatabaseManager.getInstance().updateUserMedalStatusListVersion(medalListResponse.getData().getVersion());
                            }else{
                                callback.onFailure("失败");
                            }
                        }else{
                            callback.onFailure("失败");
                        }

//                        MedalUserStatusResponse medalListResponse = null;
//                        if (!TextUtils.isEmpty(jsonObject.toString())) {
//                            BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(jsonObject.toString(), BaseJsonResult.class);
//                            if (baseJsonResult.ret) {
//                                medalListResponse = JsonUtils.getGson().fromJson(jsonObject.toString(),MedalUserStatusResponse.class);
//                                IMDatabaseManager.getInstance().InsertUserMedalStatusList(medalListResponse);
//
//                                IMDatabaseManager.getInstance().updateUserMedalStatusListVersion(medalListResponse.getData().getVersion());
//                            }
//                        }

//                        new String();
//                        callback.onCompleted(videoDataResponse);

//
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Exception e) {
                    callback.onFailure(e.getMessage());
                    Logger.i("获取最新朋友圈接口出错" + e.getMessage());
//                    callback.onFailure("");
                }


            }, 10, 40);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

