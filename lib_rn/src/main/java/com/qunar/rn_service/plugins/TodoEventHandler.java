package com.qunar.rn_service.plugins;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.uimanager.IllegalViewOperationException;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;
import com.qunar.im.base.protocol.NativeApi;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.protobuf.utils.XmlUtils;
import com.qunar.im.base.protocol.NativeApi;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by wangyu.wang on 16/5/9.
 */
public class TodoEventHandler extends ReactContextBaseJavaModule {

    public static final String LOOK_BACK_SPLITER = "#";

    public TodoEventHandler(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "HandleEventQTalkSuggest";
    }

    @ReactMethod
    public void handleEvent(
            int type,
            String key,
            Callback successCallback,
            Callback errorCallback) {

        WritableNativeMap map = new WritableNativeMap();
        boolean isSingle = false;
        try {
            switch (type) {
                case 0:
                    // TODO 打开用户名片
                    NativeApi.openUserCardVCByUserId(key);
                    break;
                case 1:
                    // TODO 打开群组聊天
                    NativeApi.openGroupChat(key,key);
                    break;
                case 2:
                    // TODO 打开好友
                    NativeApi.openUserFriendsVC();
                    break;
                case 3:
                    // TODO 打开群组
                    NativeApi.openGroupListVC();
                    break;
                case 4:
                    // TODO 打开未读消息
                    NativeApi.openQtalkWebViewForUrl(Constants.SEARCH_HISTORY_PREFIX, true);
                    break;
                case 5:
                    // TODO 打开公众号
                    NativeApi.openPublicNumberVC();
                    break;
                case 6:
                    // TODO 打开webview
                    NativeApi.openQtalkWebViewForUrl(key, true);
                    break;
                case 7:
                    // TODO 打开单人聊天
                    NativeApi.openSingleChat(key,key);
                    break;
                case 8:
                    // TODO 打开机器人会话
                    NativeApi.openRobotChatByRobotId(key);
                    break;
                case 9:
                    isSingle = true;
                case 10:
                    // TODO 打开单人聊天聊天记录上下文
                    Map<String, Object> single = JsonUtils.getGson().fromJson(key,
                            new TypeToken<Map<String, Object>>() {
                            }.getType());
                    String userId = String.valueOf(single.get("jid"));
                    String t = String.valueOf(single.get("t"));
                    try {
                        JSONObject resultObj = XmlUtils.parseMessageObject(String.valueOf(single.get("B")), "", "", "","");
                        NativeApi.openSearchDetailActivity(userId, t, resultObj.getString("MsgId"),isSingle?"single_details":"muc_details");
                    }  catch (Exception e){
                        Logger.i("rn异常:todoevent:"+e);
                    }

                    break;
                default:
                    break;
            }


            map.putBoolean("is_ok", true);
            map.putString("errorMsg", "");

            successCallback.invoke(map);
        } catch (IllegalViewOperationException e) {
            map.putBoolean("is_ok", false);
            map.putString("errorMsg", e.toString());

            errorCallback.invoke(e.getMessage());
        }
    }

    @ReactMethod
    public void openWebPage(
            String page,
            boolean showNavBar,
            Callback callback) {

        NativeApi.openQtalkWebViewForUrl(page, showNavBar);

        WritableNativeMap map = new WritableNativeMap();
        map.putBoolean("is_ok", true);
        map.putString("errorMsg", "");

        callback.invoke(map);
    }

}
