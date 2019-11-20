package com.qunar.im.base.protocol;

import android.text.TextUtils;

import com.qunar.im.base.jsonbean.RemoteConfig;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.common.CurrentPreference;
import com.qunar.im.core.services.QtalkNavicationService;

import java.io.InputStream;
import java.util.List;

/**
 * Created by saber on 16-4-19.
 */
public class ConfigAPI {
    public static final String TAG = "ConfigAPI";
    public static void getRemoteConfig(List<RemoteConfig.ConfigItem> jsonDatas,final ProtocolCallback.UnitCallback<RemoteConfig> callback)
    {
        try {
            StringBuilder queryString = new StringBuilder("conf/get_person?");
            if (TextUtils.isEmpty(CurrentPreference.getInstance().getVerifyKey())) {
                callback.doFailure();
                return;
            }
            Protocol.addBasicParamsOnHead(queryString);
            String jsonParams = JsonUtils.getGson().toJson(jsonDatas);
            LogUtil.d(TAG,jsonParams);
            String url = Protocol.makeGetUri(QtalkNavicationService.getInstance().getHttpHost(), QtalkNavicationService.getInstance().getHttpPort(), queryString.toString(), true);
            HttpUrlConnectionHandler.executePostJson(url, jsonParams, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    RemoteConfig result = null;
                    try {

                        String resultString = Protocol.parseStream(response);
                        LogUtil.d(TAG,resultString);
                        result = JsonUtils.getGson().fromJson(resultString, RemoteConfig.class);
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

    public static void setRemoteConfig(List<RemoteConfig.ConfigItem> jsonDatas,final ProtocolCallback.UnitCallback<RemoteConfig> callback)
    {
        try {
            StringBuilder queryString = new StringBuilder("conf/set_person?");
            if (TextUtils.isEmpty(CurrentPreference.getInstance().getVerifyKey())) {
                if(callback!=null)callback.doFailure();
                return;
            }
            Protocol.addBasicParamsOnHead(queryString);
            String jsonParams = JsonUtils.getGson().toJson(jsonDatas);
            LogUtil.d(TAG,jsonParams);
            String url = Protocol.makeGetUri(QtalkNavicationService.getInstance().getHttpHost(), QtalkNavicationService.getInstance().getHttpPort(), queryString.toString(), true);
            HttpUrlConnectionHandler.executePostJson(url, jsonParams, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    RemoteConfig result = null;
                    try {
                        String resultString = Protocol.parseStream(response);
                        com.orhanobut.logger.Logger.i("设置备注漫游返回:"+resultString);
//                        LogUtil.d(TAG,resultString);
                        result = JsonUtils.getGson().fromJson(resultString, RemoteConfig.class);
                    } catch (Exception e) {
//                        LogUtil.e(TAG,"error",e);
                        com.orhanobut.logger.Logger.i("设置备注漫游返回catch:"+e.getMessage());
                    }
                    if(callback!=null)
                    callback.onCompleted(result);
                }

                @Override
                public void onFailure(Exception e) {
                    com.orhanobut.logger.Logger.i("设置备注漫游返回cfailure:"+e.getMessage());
                    if(callback!=null)callback.doFailure();
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG,"error",e);
        }
    }
}
