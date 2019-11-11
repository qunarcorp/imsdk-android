package com.qunar.im.thirdpush;

import com.orhanobut.logger.Logger;
import com.qunar.im.thirdpush.core.QPushIntentService;
import com.qunar.im.thirdpush.core.QPushMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 需要定义一个receiver 或 Service 来接收透传和通知栏点击的信息，建议使用Service，更加简单
 * Created by Wiki on 2017/6/3.
 */

public class PushIntentService extends QPushIntentService {
    @Override
    public void onReceivePassThroughMessage(QPushMessage message) {
        Logger.i(TAG + "收到透传消息 -> " + message.getPlatform());
        Logger.i(TAG + "收到透传消息 -> " + message.getContent());
    }

    @Override
    public void onNotificationMessageClicked(QPushMessage message) {
        Logger.i(TAG + "通知栏消息点击 -> " + message.getPlatform());
        Logger.i(TAG + "通知栏消息点击 -> " + message.getContent());
        try {
            JSONObject jsonObject = new JSONObject(message.getContent());
            String option = jsonObject.optString("option");
            if ("web".equals(option)) {
                String url = jsonObject.optString("url");
                openWebView(url);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 建议使用普通通知栏来实现打开URL，因为这样可以实现打开内部浏览器
     */
    private void openWebView(String url) {
        Logger.i(TAG + "打开浏览器 -> " + url);
        // 请自行实现WebViewActivity
    }
}
