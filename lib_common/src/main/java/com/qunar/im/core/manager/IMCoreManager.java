package com.qunar.im.core.manager;

import android.content.Context;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.util.Constants;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.cache.IMUserCacheManager;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.protobuf.utils.StringUtils;
import com.qunar.im.utils.DeviceInfoManager;

import java.io.IOException;

/**
 * Created by may on 2017/6/28.
 */

public class IMCoreManager {


    class Define {
        protected static final String ClearCacheCheck = "kClearCacheCheck";
        protected static final int ClearCacheVersion = 1;
        public static final String MyHeaderImageSrc = "kMyHeaderImageSrc";
    }

    public void login(String userName, String password) throws IOException {
        Logger.i("将登陆数据放入相对应内存中,username:"+userName+",password:"+password);
        DeviceInfoManager.getInstance().setUserName(userName.toLowerCase());
        IMUserCacheManager.getInstance().putConfig(Constants.Preferences.lastuserid, userName.toLowerCase());
        IMUserCacheManager.getInstance().putConfig(Constants.Preferences.lastuserdomain, QtalkNavicationService.getInstance().getXmppdomain());
        IMUserCacheManager.getInstance().putConfig("LastPassword", password);
        IMLogicManager.getInstance().login(userName, password);
    }

    public void logout(String userName) {
        IMLogicManager.getInstance().logout(userName);

    }

    //发送pb消息
    public void sendMessage(ProtoMessageOuterClass.ProtoMessage protoMessage){
        IMLogicManager.getInstance().sendMessage(protoMessage);
    }

    private static volatile IMCoreManager INSTANCE = null;

    private String _remoteKey;


    protected IMCoreManager() {
        initManager();
    }

    private void initManager() {
        this.checkClearCache();
    }



    private void checkClearCache() {
        int clearCacheVersion = IMUserCacheManager.getInstance().getIntConfig(Define.ClearCacheCheck);

        if (clearCacheVersion < Define.ClearCacheVersion) {
            clearCache();
            IMUserCacheManager.getInstance().putConfig(Define.ClearCacheCheck, Define.ClearCacheVersion);
        }
    }

    private void clearCache() {
    }

    public String getRemoteKey() {

        synchronized (this) {
            if (StringUtils.isEmpty(_remoteKey)) {
                updateRemoteLoginKey();
            }
            return _remoteKey;
        }
    }

    private String updateRemoteLoginKey() {
        _remoteKey = IMLogicManager.getInstance().getRemoteLoginKey();
        return _remoteKey;
    }

    public static IMCoreManager BuildDefaultInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (IMCoreManager.class) {
                if (INSTANCE == null)
                    INSTANCE = new IMCoreManager();
            }
        }
        return INSTANCE;
    }

    public static IMCoreManager getInstance() {

        return INSTANCE;
    }

}
