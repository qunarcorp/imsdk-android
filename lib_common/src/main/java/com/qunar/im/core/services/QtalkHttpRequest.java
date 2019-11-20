package com.qunar.im.core.services;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.R;
import com.qunar.im.base.util.NetworkUtils;
import com.qunar.im.common.CommonConfig;

import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

/**
 * Created by may on 2017/8/8.
 */

public class QtalkHttpRequest {
    public static final int NO_NETWORK_CODE = -100;

    private String _url;
    private FormBody.Builder _requestBodyBuilder;

    public QtalkHttpRequest(String url) {
        _url = url;
        _requestBodyBuilder = new FormBody.Builder();
    }

    public QtalkHttpRequest addParam(String key, String value) {
        _requestBodyBuilder.add(key, value);

        return this;
    }

    public JSONObject post() {
        RequestBody requestBody = _requestBodyBuilder.build();

        try {
            //无网络连接
            if(NetworkUtils.isConnection(CommonConfig.globalContext) == NetworkUtils.ConnectStatus.disconnected){
                JSONObject result = new JSONObject();
                result.put("status_id", NO_NETWORK_CODE);
                result.put("msg", CommonConfig.globalContext.getString(R.string.atom_base_no_network));
                return result;
            }

            Request request = new Request.Builder()
                    .url(_url)
                    .post(requestBody)
                    .build();

            OkHttpClient httpClient = new OkHttpClient
                    .Builder()
                    .build();

            Response response = null;
            try {
                response = httpClient.newCall(request).execute();
            } catch (IOException e) {
                Logger.e(e, "HTTP GET FAILED, request:\n%s", request);
            }

            if (response != null) {
                try {
                    Reader streamReader = response.body().charStream();
                    StringBuilder responseStrBuilder = new StringBuilder();
                    CharBuffer cb = CharBuffer.allocate(2048);
                    int length;

                    while ((length = streamReader.read(cb)) > 0) {
                        responseStrBuilder.append(cb.array(), 0, length);
                        cb.clear();
                    }

                    JSONObject body = new JSONObject(responseStrBuilder.toString());
                    return body;
                } catch (Exception e) {
                    Logger.e(e, "HTTP GET & PARSE response FAILED, request:\n%s, \nresponse:\n%s", request, response);
                } finally {
                    response.body().close();
                    response.close();
                }
            } else {
                JSONObject result = new JSONObject();
                result.put("res", true);
                result.put("code", response.code());
                result.put("errmsg", response.message());
            }
        } catch (Exception e) {
            Logger.e(e, "QtalkHttpRequest post body crashed");
        }
        return null;
    }
}
