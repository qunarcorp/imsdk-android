package com.qunar.im.base.protocol;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.jsonbean.BaseJsonResult;
import com.qunar.im.base.jsonbean.GeneralJson;
import com.qunar.im.base.jsonbean.QVTResponseResult;
import com.qunar.im.base.jsonbean.SeatStatusResult;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.core.services.QtalkNavicationService;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xinbo.wang on 2016/5/11.
 */
public class ThirdProviderAPI {
    private static final String TAG = ThirdProviderAPI.class.getSimpleName();

    public static void setServiceStatus(String userId,String statusCode,String sid,final ProtocolCallback.UnitCallback<Boolean> callback) {
        String qvtStr = com.qunar.im.protobuf.common.CurrentPreference.getInstance().getQvt();
        if (!TextUtils.isEmpty(qvtStr)) {
            QVTResponseResult qvtResponseResult = JsonUtils.getGson().fromJson(qvtStr, QVTResponseResult.class);
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie","_q="+qvtResponseResult.data.qcookie+";_v="+qvtResponseResult.data.vcookie+
                    ";_t="+ qvtResponseResult.data.tcookie);
            String url = QtalkNavicationService.getInstance().getQcadminHost() + "/api/seat/upSeatSeStatusWithSid.qunar?qName=" + userId
                    + "&st=" + statusCode+ "&sid=" + sid;
            HttpUrlConnectionHandler.executeGet(url,cookie, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    String resultString = null;
                    BaseJsonResult result = null;
                    boolean r = false;
                    try {
                        resultString = Protocol.parseStream(response);
                        Logger.i("setServiceStatus->>",resultString);
                        result = JsonUtils.getGson().fromJson(resultString
                                , BaseJsonResult.class);
                        r = result != null && result.ret;
                    } catch (Exception e) {
                        Logger.i("setServiceStatus->>",e.getLocalizedMessage());
                    }

                    callback.onCompleted(r);
                }

                @Override
                public void onFailure(Exception e) {
                    Logger.i("setServiceStatus->>",e.getLocalizedMessage());
                    callback.onFailure("");
                }
            });
        }
        else {
            Logger.i("setServiceStatus->>qvt","");
            callback.onFailure("");
        }
    }

    public static void getServiceStatus(String userId,final ProtocolCallback.UnitCallback<List<SeatStatusResult.SeatStatus>> callback) {
        String qvtStr = com.qunar.im.protobuf.common.CurrentPreference.getInstance().getQvt();
        if (!TextUtils.isEmpty(qvtStr)) {
            QVTResponseResult qvtResponseResult = JsonUtils.getGson().fromJson(qvtStr, QVTResponseResult.class);
            Map<String, String> cookie = new HashMap<>();
            cookie.put("Cookie","_q="+qvtResponseResult.data.qcookie+";_v="+qvtResponseResult.data.vcookie+
                    ";_t="+ qvtResponseResult.data.tcookie);
            String url = QtalkNavicationService.getInstance().getQcadminHost() + "/api/seat/getSeatSeStatusWithSid.qunar?qName=" + userId;
            HttpUrlConnectionHandler.executeGet(url, cookie,new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    List<SeatStatusResult.SeatStatus> seatStatuses = null;
                    try {
                        String resultString = Protocol.parseStream(response);
                        Logger.i("getServiceStatus" + resultString);
                        SeatStatusResult statusResult = JsonUtils.getGson().fromJson(resultString,SeatStatusResult.class);
                        seatStatuses = statusResult.data;
                    } catch (Exception e) {
                        LogUtil.e(TAG,"error",e);
                    }
                    callback.onCompleted(seatStatuses);
                }

                @Override
                public void onFailure(Exception e) {
                    callback.onFailure("");
                }
            });
        }
        else {
            callback.onFailure("");
        }
    }
}
