package com.qunar.rn_service.plugins;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableNativeMap;

/**
 * Created by wangyu.wang on 2016/10/10.
 */
public class QChatUserSearch extends ReactContextBaseJavaModule {

    public QChatUserSearch(ReactApplicationContext reactContext) {
        super(reactContext);
    }
    @Override
    public String getName() {
        return "QChatUserSearch";
    }

    @ReactMethod
    public void show(
            Callback callback) {

        boolean is_ok = true;
        WritableNativeMap map = new WritableNativeMap();

        try {

            // TODO show native search activity

        } catch (Exception e) {
            is_ok = false;
            map.putString("errorMsg", e.toString());
        }

        map.putBoolean("is_ok", is_ok);

        callback.invoke(map);
    }
}
