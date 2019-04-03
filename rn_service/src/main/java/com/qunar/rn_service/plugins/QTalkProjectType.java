package com.qunar.rn_service.plugins;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.uimanager.IllegalViewOperationException;
import com.orhanobut.logger.Logger;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.manager.IMDatabaseManager;
import com.qunar.im.core.services.QtalkNavicationService;

/**
 * Created by wangyu.wang on 16/9/14.
 */
public class QTalkProjectType extends ReactContextBaseJavaModule {

    public QTalkProjectType(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "QTalkProjectType";
    }

    @ReactMethod
    public void getProjectType(
            Callback successCallback,
            Callback errorCallback) {
        try {
            WritableNativeMap map = new WritableNativeMap();

            map.putBoolean("isQTalk", CommonConfig.isQtalk);
            map.putString("domain", com.qunar.im.protobuf.common.CurrentPreference.getInstance().getPreferenceUserId());
            map.putString("fullname", com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserName());
            map.putString("c_key", Login.getCKey());
            // qchat
            boolean isMerchant = false;
            if(!CommonConfig.isQtalk){
                isMerchant = DataUtils.getInstance(getCurrentActivity()).getPreferences(Constants.Preferences.qchat_is_merchant,true);
                map.putBoolean("isSupplier", isMerchant);
            }else{
                map.putBoolean("showOA", QtalkNavicationService.getInstance().getNavConfigResult().imConfig.showOA);
                map.putString("checkUserKeyHost", QtalkNavicationService.getInstance().getNavConfigResult().baseaddess.apiurl);
            }

            map.putBoolean("isShowWorkWorld", IMDatabaseManager.getInstance().SelectWorkWorldPremissions());
            Logger.i("获取发现："
                    + " isQTalk=" + CommonConfig.isQtalk
                    + " domain=" + com.qunar.im.protobuf.common.CurrentPreference.getInstance().getPreferenceUserId()
                    + " fullname=" + com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserName()
                    + " isSupplier=" + isMerchant
                    + " showOA=" + QtalkNavicationService.getInstance().getNavConfigResult().imConfig.showOA);

            successCallback.invoke(map);
        } catch (IllegalViewOperationException e) {
            errorCallback.invoke(e.getMessage());
        }
    }

}