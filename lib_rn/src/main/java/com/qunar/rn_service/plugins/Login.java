package com.qunar.rn_service.plugins;


import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.uimanager.IllegalViewOperationException;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.protobuf.common.CurrentPreference;

/**
 * Created by wangyuwang on 16-3-11.
 */
public class Login extends ReactContextBaseJavaModule {

    public Login(ReactApplicationContext reactContext) {
        super(reactContext);
    }
    @Override
    public String getName() {
        return "Login";
    }

    @ReactMethod
    public void getLoginInfo(
            Callback successCallback,
            Callback errorCallback) {
        try {
            WritableNativeMap map = new WritableNativeMap();
            map.putString("userid", CurrentPreference.getInstance().getUserid());

            map.putString("token", CurrentPreference.getInstance().getToken());
            map.putString("q_auth", CurrentPreference.getInstance().getVerifyKey() == null ? "404" : CurrentPreference.getInstance().getVerifyKey());
            map.putString("c_key", getCKey());
            map.putDouble("timestamp", System.currentTimeMillis());

            successCallback.invoke(map);
        } catch (IllegalViewOperationException e) {
            errorCallback.invoke(e.getMessage());
        }
    }

    public static String getCKey(){
        return Protocol.getCKEY();
    }

}
