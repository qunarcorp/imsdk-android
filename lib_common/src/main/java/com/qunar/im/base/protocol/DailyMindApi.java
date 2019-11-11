package com.qunar.im.base.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.qunar.im.base.common.DailyMindConstants;
import com.qunar.im.base.jsonbean.BaseJsonResult;
import com.qunar.im.base.jsonbean.DailyMindMain;
import com.qunar.im.base.jsonbean.DailyMindMainlList;
import com.qunar.im.base.jsonbean.DailyMindSub;
import com.qunar.im.base.jsonbean.DailyMindSubList;
import com.qunar.im.base.jsonbean.GeneralJson;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.protobuf.common.CurrentPreference;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.qunar.im.base.util.JsonUtils.getGson;

/**
 * 密码箱api
 * Created by lihaibin.li on 2017/8/22.
 */

public class DailyMindApi {
    private static String TAG = DailyMindApi.class.getSimpleName();

    //密码操作相关
    public static void operatePassword(final String method, final Map<String, String> requestParams, final ProtocolCallback.UnitCallback unitCallback) {
        String q_ckey = Protocol.getCKEY();
        if (TextUtils.isEmpty(q_ckey)) return;
        final String requestJson = getGson().toJson(requestParams);
        Map<String, String> cookie = new HashMap<>();
        cookie.put("Cookie", "q_ckey=" + q_ckey + ";p_user=" + CurrentPreference.getInstance().getUserid());
        HttpUrlConnectionHandler.executePostJson(DailyMindConstants.DAILY_MIND_BASE_URL + method, cookie, requestJson, new HttpRequestCallback() {
            @Override
            public void onComplete(InputStream response) {
                try {
                    String resultString = Protocol.parseStream(response);
                    BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(resultString, BaseJsonResult.class);
                    if (baseJsonResult == null || !baseJsonResult.ret) {
                        unitCallback.onCompleted(baseJsonResult);
                        return;
                    }
//                    Log.d(TAG, resultString);
                    if (DailyMindConstants.SAVE_TO_MAIN.equals(method) || DailyMindConstants.UPDATE_MAIN.equals(method)) {
                        GeneralJson stringJson = JsonUtils.getGson().fromJson(resultString, GeneralJson.class);
                        if (stringJson != null && stringJson.data != null) {
                            DailyMindMain dailyMindMain = new DailyMindMain();
                            dailyMindMain.qid = Integer.parseInt(stringJson.data.get("qid"));
                            dailyMindMain.version = stringJson.data.get("version");
                            dailyMindMain.desc = requestParams.get("desc");
                            dailyMindMain.title = requestParams.get("title");
                            dailyMindMain.content = requestParams.get("content");
                            dailyMindMain.type = Integer.parseInt(requestParams.get("type"));
                            dailyMindMain.state = DailyMindConstants.SAVE_TO_MAIN.equals(method) ? DailyMindConstants.CREATE : DailyMindConstants.UPDATE;
                            unitCallback.onCompleted(dailyMindMain);
                        }
                    } else if (DailyMindConstants.GET_CLOUD_MAIN.equals(method)) {
                        DailyMindMainlList stringJson = JsonUtils.getGson().fromJson(resultString, DailyMindMainlList.class);
                        List<DailyMindMain> dailyMindMains = stringJson.data;
                        if (dailyMindMains != null) {
                            unitCallback.onCompleted(dailyMindMains);
                        }

                    } else if (DailyMindConstants.SAVE_TO_SUB.equals(method) || DailyMindConstants.UPDATE_SUB.equals(method)) {//保存 更新子密码
                        GeneralJson stringJson = JsonUtils.getGson().fromJson(resultString, GeneralJson.class);
                        if (stringJson != null && stringJson.data != null) {
                            DailyMindSub dailyMindSub = new DailyMindSub();
                            dailyMindSub.qid = Integer.parseInt(requestParams.get("qid"));
                            dailyMindSub.qsid = Integer.parseInt(stringJson.data.get("qsid"));
                            dailyMindSub.version = stringJson.data.get("version");
                            dailyMindSub.content = requestParams.get("content");
                            dailyMindSub.type = Integer.parseInt(requestParams.get("type"));
                            dailyMindSub.state = DailyMindConstants.SAVE_TO_SUB.equals(method) ? DailyMindConstants.CREATE : DailyMindConstants.UPDATE;
                            dailyMindSub.desc = requestParams.get("desc");
                            dailyMindSub.title = requestParams.get("title");
                            unitCallback.onCompleted(dailyMindSub);
                        }
                    } else if (DailyMindConstants.GET_CLOUD_SUB.equals(method)) {//获取子密码
                        DailyMindSubList stringJson = JsonUtils.getGson().fromJson(resultString, DailyMindSubList.class);
                        List<DailyMindSub> dailyMindSubs = stringJson.data;
                        if (dailyMindSubs != null) {
                            unitCallback.onCompleted(dailyMindSubs);
                        }
                    } else if (DailyMindConstants.DELETE_MAIN.equals(method)) {//删除
                        String qid = requestParams.get("qid");
                        unitCallback.onCompleted(qid);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                unitCallback.onFailure("");
            }
        });
    }

    public static void operatePassword(final String method, final DailyMindSub dailyMindSub, final ProtocolCallback.UnitCallback unitCallback) {
        String q_ckey = Protocol.getCKEY();
        if (TextUtils.isEmpty(q_ckey)) return;
        final String requestJson = getGson().toJson(dailyMindSub);
        Map<String, String> cookie = new HashMap<>();
        cookie.put("Cookie", "q_ckey=" + q_ckey + ";p_user=" + CurrentPreference.getInstance().getUserid());
        HttpUrlConnectionHandler.executePostJson(DailyMindConstants.DAILY_MIND_BASE_URL + method, cookie, requestJson, new HttpRequestCallback() {
            @Override
            public void onComplete(InputStream response) {
                try {
                    String resultString = Protocol.parseStream(response);
                    BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(resultString, BaseJsonResult.class);
                    if (baseJsonResult == null || !baseJsonResult.ret) {
                        unitCallback.onCompleted(baseJsonResult);
                        return;
                    }
//                    Log.d(TAG, resultString);
                    if (DailyMindConstants.SAVE_TO_SUB.equals(method) || DailyMindConstants.UPDATE_SUB.equals(method)) {//更新子密码
                        GeneralJson stringJson = JsonUtils.getGson().fromJson(resultString, GeneralJson.class);
                        if (stringJson != null && stringJson.data != null) {
                            dailyMindSub.version = stringJson.data.get("version");
                            unitCallback.onCompleted(dailyMindSub);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                unitCallback.onFailure("");
            }
        });
    }
}
