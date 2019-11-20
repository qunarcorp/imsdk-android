package com.qunar.im.base.protocol;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.jsonbean.BaseJsonResult;
import com.qunar.im.base.jsonbean.RequestRobotInfo;
import com.qunar.im.base.jsonbean.RobotInfoResult;
import com.qunar.im.base.jsonbean.RobotInfoResult2;
import com.qunar.im.base.jsonbean.StringJsonResult;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.common.CurrentPreference;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by saber on 16-3-1.
 */
public class RobotAPI {

    private static final String TAG = RobotAPI.class.getSimpleName();
    /**
     * 获取公众号名片
     *
     * @param requestRobotInfos
     * @param callback
     */
    public static void getRobotInfo(List<RequestRobotInfo> requestRobotInfos, final ProtocolCallback.UnitCallback<RobotInfoResult> callback) {
        try {
            StringBuilder params = new StringBuilder("get_robot?");
            if (TextUtils.isEmpty(CurrentPreference.getInstance().getVerifyKey())) {
                callback.doFailure();
                return;
            }
            Protocol.addBasicParamsOnHead(params);
            String url = Protocol.makeGetUri(QtalkNavicationService.getInstance().getHttpHost(),
                    QtalkNavicationService.getInstance().getHttpPort(), params.toString(), true);

            String postBody = JsonUtils.getGson().toJson(requestRobotInfos);
            LogUtil.d("RobotAPI", url);
            LogUtil.d("RobotAPI", postBody);
            HttpUrlConnectionHandler.executePostJson(url, postBody, new HttpRequestCallback() {
                RobotInfoResult result = null;
                String resultString;
                @Override
                public void onComplete(InputStream response) {

                    try {
                        resultString = Protocol.parseStream(response);
                        LogUtil.d("RobotAPI", resultString);
                        if(!TextUtils.isEmpty(resultString)) {
                            result = JsonUtils.getGson().fromJson(resultString, RobotInfoResult.class);
                        }
                    } catch (Exception e) {
                        LogUtil.e(TAG,"error",e);
                    }
                    if(result == null)
                    {
                        result = new RobotInfoResult();
                        result.ret = false;
                    }
                    callback.onCompleted(result);
                }

                @Override
                public void onFailure(Exception e) {
                    callback.doFailure();
                }
            });
        }
        catch (Exception e) {
            LogUtil.e(TAG,"error",e);
        }
    }


    /**
     * 返回我关注的公众号
     * @param userId
     * @param callback
     */
    public static void getMyRobotList(String userId,final  ProtocolCallback.UnitCallback<StringJsonResult> callback)
    {
        try {
            StringBuilder params = new StringBuilder("user_robot?");
            if (TextUtils.isEmpty(CurrentPreference.getInstance().getVerifyKey())) {
                callback.doFailure();
                return;
            }
            Protocol.addBasicParamsOnHead(params);
            String url = Protocol.makeGetUri(QtalkNavicationService.getInstance().getHttpHost(),
                    QtalkNavicationService.getInstance().getHttpPort(), params.toString(), true);
            Logger.d(TAG + "--getMyRobotList--" + url);
            String postBody = "{\"user\":\""+userId+"\",\"method\":\"get\"}";
            Logger.d(TAG + "--getMyRobotList--" + postBody);
            final long postTime = System.currentTimeMillis();
            HttpUrlConnectionHandler.executePostJson(url, postBody, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    long currTime = System.currentTimeMillis();
                    StringJsonResult result = null;
                    try {
                        String resultString = Protocol.parseStream(response);
                        Logger.d(TAG + "--getMyRobotList--" + resultString);
                        JSONObject jsonObject = new JSONObject(resultString);
                        if(jsonObject.has("ret") && !Boolean.valueOf(jsonObject.get("ret").toString())){
                            callback.onFailure("");
                            return;
                        }
                        result = JsonUtils.getGson().fromJson(resultString, StringJsonResult.class);
                    } catch (Exception e) {
                        Logger.e(TAG + "error" + e.getLocalizedMessage());
                    }
                    if (result == null) {
                        result = new StringJsonResult();
                        result.ret = false;
                    }
                    callback.onCompleted(result);
                }

                @Override
                public void onFailure(Exception e) {
                    callback.doFailure();
                }
            });
        }
        catch (Exception e) {
            Logger.e(TAG + "error" + e.getLocalizedMessage());
        }
    }


    /**
     * 取消关注
     * @param userId
     * @param robotName
     * @param callback
     */
    public static void delRobot(String userId,String robotName,final  ProtocolCallback.UnitCallback<BaseJsonResult> callback)
    {
        try {
            StringBuilder params = new StringBuilder("user_robot?");
            if (TextUtils.isEmpty(CurrentPreference.getInstance().getVerifyKey())) {
                callback.doFailure();
                return;
            }
            Protocol.addBasicParamsOnHead(params);
            String url = Protocol.makeGetUri(QtalkNavicationService.getInstance().getHttpHost(),
                    QtalkNavicationService.getInstance().getHttpPort(), params.toString(), true);

            String postBody = "{\"user\":\""+userId+"\",\"rbt\":\""+robotName+"\",\"method\":\"del\"}";
            final long postTime = System.currentTimeMillis();
            HttpUrlConnectionHandler.executePostJson(url, postBody, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    BaseJsonResult result = null;
                    LogUtil.d("debug", "complete");
                    try {
                        String resultString = Protocol.parseStream(response);
                        result = JsonUtils.getGson().fromJson(resultString, BaseJsonResult.class);
                    } catch (Exception e) {
                        LogUtil.e(TAG,"error",e);
                    }
                    if (result == null) {
                        result = new BaseJsonResult();
                        result.ret = false;
                    }
                    callback.onCompleted(result);
                }

                @Override
                public void onFailure(Exception e) {
                    callback.doFailure();
                }
            });
        }
        catch (Exception e) {
            LogUtil.e(TAG,"error",e);
        }
    }

    /**
     * 关注公众号
     * @param userId
     * @param robotName
     * @param callback
     */
    public static void addRobot(String userId,String robotName,final  ProtocolCallback.UnitCallback<BaseJsonResult> callback)
    {
        try {
            StringBuilder params = new StringBuilder("user_robot?");
            if (TextUtils.isEmpty(CurrentPreference.getInstance().getVerifyKey())) {
                callback.doFailure();
            }
            Protocol.addBasicParamsOnHead(params);
            String url = Protocol.makeGetUri(QtalkNavicationService.getInstance().getHttpHost(),
                    QtalkNavicationService.getInstance().getHttpPort(), params.toString(), true);

            String postBody = "{\"user\":\""+userId+"\",\"rbt\":\""+robotName+"\",\"method\":\"add\"}";
            final long postTime = System.currentTimeMillis();
            Logger.i("addRobot:" + "url:" + url + "\n" + "postBody:" + postBody);
            HttpUrlConnectionHandler.executePostJson(url, postBody, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    BaseJsonResult result = null;
                    try {
                        String resultString = Protocol.parseStream(response);
                        Logger.i("addRobot:" + resultString);
                        result = JsonUtils.getGson().fromJson(resultString, BaseJsonResult.class);
                    } catch (Exception e) {
                        LogUtil.e(TAG,"error",e);
                    }
                    if (result == null) {
                        result = new BaseJsonResult();
                        result.ret = false;
                    }
                    callback.onCompleted(result);
                }

                @Override
                public void onFailure(Exception e) {
                    Logger.i("addRobot:" + e.getLocalizedMessage());
                    callback.doFailure();
                }
            });
        } catch (Exception e) {
            Logger.i("addRobot:" + e.getLocalizedMessage());
        }
    }

    /**
     * 搜索公众号
     *
     * @param keyword
     * @param callback
     */
    public static void searchRobotInfo(String keyword, final ProtocolCallback.UnitCallback<RobotInfoResult> callback) {
        try {
            StringBuilder params = new StringBuilder("search_robot?");
            if (TextUtils.isEmpty(CurrentPreference.getInstance().getVerifyKey())) {
                callback.doFailure();
                return;
            }
            Protocol.addBasicParamsOnHead(params);
            String url = Protocol.makeGetUri(QtalkNavicationService.getInstance().getHttpHost(),
                    QtalkNavicationService.getInstance().getHttpPort(), params.toString(), true);
            String postBody = "{\"keyword\":\""+keyword+"\"}";
            HttpUrlConnectionHandler.executePostJson(url, postBody, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    RobotInfoResult result =  new RobotInfoResult();
                    String resultString;
                    LogUtil.d("debug", "complete");
                    try {
                        resultString = Protocol.parseStream(response);
                        if (!TextUtils.isEmpty(resultString)) {
                            RobotInfoResult2 result2 =
                                    JsonUtils.getGson().fromJson(resultString, RobotInfoResult2.class);
                            result.ret = result2.ret;
                            result.errmsg = result2.errmsg;
                            result.data = new ArrayList<RobotInfoResult.RobotItemResult>();
                            for(RobotInfoResult2.RobotItemResult2 itemResult2:result2.data)
                            {
                                RobotInfoResult.RobotItemResult item =
                                        new RobotInfoResult.RobotItemResult();
                                item.rbt_name = itemResult2.rbt_name;
                                item.rbt_ver = itemResult2.rbt_ver;
                                item.rbt_body = JsonUtils.getGson().fromJson(itemResult2.rbt_body.toString(),
                                        RobotInfoResult.RobotBody.class);
                                result.data.add(item);
                            }
                        }
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
        }
        catch (Exception e) {
            LogUtil.e(TAG,"error",e);
        }
    }
}
