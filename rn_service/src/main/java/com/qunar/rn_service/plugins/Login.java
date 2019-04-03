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

        /*
        k值为  base64(k1).
        k1的值为 u=用户名&k=md5(k2)
        k2的值为从qtalk客户端拿到的k值(k3)字符串拼接t的数值
        */
       /* String t = System.currentTimeMillis()+"";
        String k2 = CommonConfig.verifyKey+t;
        String k1 = "u="+ CurrentPreference.getInstance().getUserId()+"&k="+ BinaryUtil.MD5(k2)+"&t="+System.currentTimeMillis();
        String k = android.util.Base64.encodeToString(k1.getBytes(), android.util.Base64.NO_WRAP |
                                                                     android.util.Base64.URL_SAFE);

        return k;*/
        return Protocol.getCKEY();
    }

}
