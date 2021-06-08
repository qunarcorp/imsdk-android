package com.qunar.im.log;


import com.orhanobut.logger.Logger;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.jsonbean.BaseJsonResult;
import com.qunar.im.base.jsonbean.Log;
import com.qunar.im.base.jsonbean.LogInfo;
import com.qunar.im.base.protocol.HttpRequestCallback;
import com.qunar.im.base.protocol.HttpUrlConnectionHandler;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.ListUtil;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.core.utils.GlobalConfigManager;
import com.qunar.im.common.CurrentPreference;
import com.qunar.im.utils.DeviceUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 日志上传
 */
public class LogUploadApi {

    public static void upload(final List<LogInfo> infos, final LogUploadStateListener listener){
        if(ListUtil.isEmpty(infos)){
            return;
        }
        Log log = new Log();
        log.infos = infos;
        log.user = createUserInstance();
        log.device = createDeviceInstance();

        String url = QtalkNavicationService.getInstance().getUploadLog();
        String json = JsonUtils.getGson().toJson(log);
        Logger.i("日志上传信息：" + url + "\n" + json);
        HttpUrlConnectionHandler.executePostJsonSync(url, json, new HttpRequestCallback() {
            @Override
            public void onComplete(InputStream response) throws IOException {
                try{
                    String resultString = Protocol.parseStream(response);
                    BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(resultString,BaseJsonResult.class);
                    if(baseJsonResult != null && baseJsonResult.ret && listener != null){
                        listener.onSuccess(infos);
                        Logger.i("日志上传成功：" + resultString);
                    }else {
                        if(listener != null){
                            listener.onFail(resultString);
                        }
                        Logger.i("日志上传失败：" + resultString);
                    }
                }catch (Exception e){
                    if(listener != null){
                        listener.onFail(e.getLocalizedMessage());
                    }
                    Logger.i("日志上传失败：" + e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(Exception e) {
                if(listener != null){
                    listener.onFail(e.getLocalizedMessage());
                }
                Logger.i("日志上传失败：" + e.getLocalizedMessage());
            }
        });
    }

    private static Log.User createUserInstance(){
        Log.User user = new Log.User();
        user.domain = QtalkNavicationService.getInstance().getXmppdomain();
        user.uid = CurrentPreference.getInstance().getUserid();
        user.nav = QtalkNavicationService.getInstance().getCurrentNavUrl();
        return user;
    }

    private static Log.Device createDeviceInstance(){
        Log.Device device = new Log.Device();
        device.versionCode = GlobalConfigManager.getAppVersion();
        device.versionName = QunarIMApp.getQunarIMApp().getVersionName();
        device.plat = GlobalConfigManager.getAppName();
        device.os = GlobalConfigManager.getAppPlatform();
        device.osBrand = DeviceUtil.getPhoneBrand();
        device.osModel = DeviceUtil.getPhoneModel();
        device.osVersion = DeviceUtil.getPhoneAndroidVersion();
        return device;
    }
}
