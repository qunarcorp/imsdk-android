package com.qunar.im.ui.util.emoticon;

import com.google.gson.reflect.TypeToken;
import com.qunar.im.base.jsonbean.EmotionEntry;
import com.qunar.im.base.protocol.HttpRequestCallback;
import com.qunar.im.base.protocol.HttpUrlConnectionHandler;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.core.utils.GlobalConfigManager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaokai on 15-8-4.
 */

public class EmotionDownloader {
    private static final String TAG = EmotionDownloader.class.getSimpleName();

    private String DEFAULT_URL = QtalkNavicationService.getInstance().getSimpleapiurl() + "/file/v1/emo/d/e/config?p="
            + GlobalConfigManager.getAppName().toLowerCase();

    public interface gotEmojiListCallback
    {
        void onComplete(List<EmotionEntry> list);
    }

    public void getJSON(final gotEmojiListCallback callback) {
        HttpUrlConnectionHandler.executeGet(DEFAULT_URL, new HttpRequestCallback() {
            @Override
            public void onComplete(InputStream response) {
                List<EmotionEntry> list = new ArrayList<>();
                if(response!=null) {
                    try {
                        String str = Protocol.parseStream(response);
                        list = JsonUtils.getGson().fromJson(str, new TypeToken<List<EmotionEntry>>() {
                        }.getType());
                    } catch (Exception e) {
                        LogUtil.e(TAG,"ERROR",e);
                    }
                }
                callback.onComplete(list);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onComplete(new ArrayList<EmotionEntry>(0));
            }
        });
    }
}
