package com.qunar.im.base.protocol;

import com.qunar.im.base.jsonbean.GeneralJson;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.core.services.QtalkNavicationService;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xinbo.wang on 2016/7/1.
 */
public class OpsAPI {
    private static final String TAG = OpsAPI.class.getSimpleName();
    public static void getUserMobilePhoneNumber(final String userName,final String qtalkId, final ProtocolCallback.UnitCallback<GeneralJson> callback) {
        try {
            String url = QtalkNavicationService.getInstance().getMobileurl();
            Map<String,String> params = new HashMap<String,String>();
            params.put("user_id", userName);
            params.put("qtalk_id",qtalkId);
            params.put("platform","android");
            params.put("ckey",Protocol.getCKEY());
            HttpUrlConnectionHandler.executePostJson(url, JsonUtils.getGson().toJson(params), new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    GeneralJson result = null;
                    try {
                        String resultString = Protocol.parseStream(response); //parseGZIPStream(response.getEntity().getContent());
                        LogUtil.d("OpsAPI",resultString);
                        result = JsonUtils.getGson().fromJson(resultString, GeneralJson.class);
                    } catch (Exception e) {
                        LogUtil.e(TAG,"error",e);
                    }
                    callback.onCompleted(result);
                }

                @Override
                public void onFailure(Exception e) {
                    callback.doFailure();
                }
            });

        } catch (Exception e) {
            LogUtil.e(TAG,"error",e);
        }
    }
}
