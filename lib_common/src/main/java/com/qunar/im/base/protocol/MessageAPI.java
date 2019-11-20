package com.qunar.im.base.protocol;

import android.text.TextUtils;
import com.qunar.im.base.jsonbean.GetGroupOfflineMessageRequest;
import com.qunar.im.base.jsonbean.GetSingleConvRecord;
import com.qunar.im.base.jsonbean.GroupChatOfflineResult;
import com.qunar.im.base.jsonbean.OfflineSingleMsgResult;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.common.CurrentPreference;
import com.qunar.im.utils.QtalkStringUtils;

import java.io.InputStream;

/**
 * Created by saber on 16-3-1.
 */
public class MessageAPI {
    private static final String TAG = MessageAPI.class.getSimpleName();

    public static void getSingleChatOfflineMsg(String from, String to, long timestamp, int num, final int direction,
                                               final ProtocolCallback.UnitCallback<OfflineSingleMsgResult> callback) {
        if (TextUtils.isEmpty(CommonConfig.verifyKey)) {
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
        singleConvRecord.k = CommonConfig.verifyKey;
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
                        LogUtil.e(TAG,"error",e);
                    }
                    callback.onCompleted(result);
                }

                @Override
                public void onFailure(Exception e) {
                    callback.doFailure();
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG,"error",e);
        }
    }


    public static void getMultiChatOfflineMsg(String chatName, long timestamp, int num, int direction, final ProtocolCallback.UnitCallback<GroupChatOfflineResult> callback) {
        StringBuilder queryString = new StringBuilder("domain/get_muc_msg?");
        if (TextUtils.isEmpty(CommonConfig.verifyKey)) {
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
            request.u = CurrentPreference.getInstance().getPreferenceUserId();
            request.k = CommonConfig.verifyKey;

            String url =Protocol.makeGetUri(QtalkNavicationService.getInstance().getHttpHost(),
                    QtalkNavicationService.getInstance().getHttpPort(), queryString.toString(), true);
            String json =  JsonUtils.getGson().toJson(request);
            HttpUrlConnectionHandler.executePostJson(url, json, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    GroupChatOfflineResult orignalResult = null;
                    try {
                        String resultString = Protocol.parseStream(response);
                        orignalResult = JsonUtils.getGson().fromJson(resultString, GroupChatOfflineResult.class);
                    } catch (Exception e) {
                        LogUtil.e(TAG,"error",e);
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
            LogUtil.e(TAG,"error",e);
        }
    }



}
