package com.qunar.rn_service.plugins;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.orhanobut.logger.Logger;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.rn_service.protocal.NativeApi;

import java.util.List;

/**
 * Created by wangyu.wang on 2016/12/19.
 */
public class QTalkLoaclSearch extends ReactContextBaseJavaModule{

    public QTalkLoaclSearch(ReactApplicationContext reactContext) {
        super(reactContext);
    }
    @Override
    public String getName() {
        return "QTalkLoaclSearch";
    }

    @ReactMethod
    public void search(
            String key,
            int length,
            int start,
            String groupId,
            Promise promise) {

        WritableMap map = Arguments.createMap();
        map.putBoolean("is_ok", true);

        try {
            List ret = NativeApi.localSearch(key, start, length, groupId);

            String jsonStr =  JsonUtils.getGson().toJson(ret);
            Logger.i("QTalkLoaclSearch->"+jsonStr);
            map.putString("data", jsonStr);
            promise.resolve(map);
        } catch (Exception e) {
            Logger.i("QTalkLoaclSearch-search>"+e.getLocalizedMessage());
            //map.putBoolean("is_ok", false);
            //map.putString("errorMsg", e.toString());
            promise.reject("500", e.toString(), e);
        }
    }

    /**
     * 给rn返回搜索地址
     * @param msg
     * @param promise
     */
    @ReactMethod
    public void searchUrl(String msg,Promise promise){
        WritableMap map = Arguments.createMap();
        map.putBoolean("is_ok", true);

        map.putString("data", QtalkNavicationService.getInstance().getSearchurl());
        map.putString("Msg",msg);
        Logger.i("QTalkLoaclSearch->"+QtalkNavicationService.getInstance().getSearchurl());
        try {
            promise.resolve(map);
        }catch (Exception e){
            Logger.i("QTalkLoaclSearch-searchUrl>"+e.getLocalizedMessage());
            promise.reject("500",e.toString(),e);
        }
    }

    /**
     * 给Rn返回客户端版本号
     * @param msg
     * @param promise
     */
    @ReactMethod
    public void getVersion(String msg,Promise promise){
        WritableMap map = Arguments.createMap();
        map.putBoolean("is_ok", true);
        map.putString("data", QunarIMApp.getQunarIMApp().getVersion()+"");
        map.putString("Msg",msg);

        try {
            promise.resolve(map);
        }catch (Exception e){
            promise.reject("500",e.toString(),e);
        }
    }
}
