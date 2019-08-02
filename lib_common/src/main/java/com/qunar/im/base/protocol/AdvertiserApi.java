package com.qunar.im.base.protocol;


import android.content.Context;
import android.text.TextUtils;

import com.qunar.im.base.jsonbean.AdvertiserBean;
import com.qunar.im.base.util.FileUtils;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.ListUtil;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.services.QtalkNavicationService;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by xingchao.song on 4/22/2016.
 */
public class AdvertiserApi {
    private static final String TAG = AdvertiserApi.class.getSimpleName();

    public static final String ADHISTROY = "last_advertisement";

    public static String parseStream(InputStream stream) throws IOException {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(stream, "utf-8"));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            br.close();
            return sb.toString();
        } catch (IOException e) {
            LogUtil.e(TAG, "error", e);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        return "";
    }

    public static void getAdvertiser(final String v, String user, final AdCallback callback) {
        String adUrl = QtalkNavicationService.getInstance().getSimpleapiurl();
        if(CommonConfig.isQtalk){
            adUrl = adUrl + "/advert/qtalk/advert.php";
        }else{
            adUrl = adUrl + "/advert/qchat/advert.php";
        }

        String url = adUrl + "?p=android&v=" + v + "&debug=" + String.valueOf(CommonConfig.isDebug);
        if (!TextUtils.isEmpty(user)) url += "&" + user;
        HttpUrlConnectionHandler.executeGet(url, new HttpRequestCallback() {
            @Override
            public void onComplete(InputStream response) {
                String resultString = null;
                try {
                    resultString = parseStream(response);
                    LogUtil.d("advertise result:", resultString);
                    AdvertiserBean advertiserBean = JsonUtils.getGson().fromJson(resultString, AdvertiserBean.class);
                    if (advertiserBean == null || ListUtil.isEmpty(advertiserBean.adlist)) {
                        callback.onFailure();
                    } else {
//                        advertiserBean.shown = true;
                        if (!advertiserBean.shown) {
                            deleteAdConfig(CommonConfig.globalContext);
                        } else {
                            if(advertiserBean.version > Integer.valueOf(v)){
                                saveNavConfig(resultString, CommonConfig.globalContext);
                                callback.onCompleted(advertiserBean);
                            }
                        }
                    }

                } catch (IOException e) {
                    callback.onFailure();
                    LogUtil.e(TAG, "error", e);
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure();
            }
        });
    }

    public static String loadNavConfig(Context context) {
        File configFile = new File(context.getFilesDir(), ADHISTROY);
        return FileUtils.readFirstLine(configFile, context);
    }

    public static boolean deleteAdConfig(Context context) {
        File configFile = new File(context.getFilesDir(), ADHISTROY);
        return configFile.delete();
    }

    public static void saveNavConfig(String jsonContent, Context context) {
//        File configFile = new File(FileUtils.getExternalFilesDir(context), ADHISTROY);
        File configFile = new File(context.getFilesDir(), ADHISTROY);
        FileUtils.writeToFile(jsonContent, configFile, true);
    }
}
