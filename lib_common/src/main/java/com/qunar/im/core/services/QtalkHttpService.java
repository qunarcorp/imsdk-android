package com.qunar.im.core.services;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.jsonbean.NavConfigResult;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.util.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by may on 2017/7/3.
 */

public class QtalkHttpService {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static final MediaType MIXED = MediaType.parse("multipart/mixed");
    public static final MediaType ALTERNATIVE = MediaType.parse("multipart/alternative");
    public static final MediaType DIGEST = MediaType.parse("multipart/digest");
    public static final MediaType PARALLEL = MediaType.parse("multipart/parallel");
    public static final MediaType FORM = MediaType.parse("multipart/form-data");

    private static final String COOKIE = "Cookie";

    private static final String Q_CKEY = "q_ckey=";

    private static int num = 1;
    private static int time = 6;


    public static JSONObject getUserToken(String userName, String verifyCode) throws IOException, JSONException {
        String destUrl = QtalkNavicationService.getInstance().getTokenSmsUrl();
        return postJson(destUrl, String.format("rtx_id=%s|verify_code=%s", userName, verifyCode),null);
    }

    public interface CallbackJson {
        void onJsonResponse(JSONObject jsonObject) throws JSONException;
        void onFailure(Call call, IOException e);

    }


    private static void asyncPostJson(String requestUrl, String body, Headers header, final CallbackJson callbackJson, int connectTimeOut, int readTimeOut) {
        Logger.i("请求地址:" + requestUrl + ";请求参数:" + body);
        OkHttpClient httpClient = new OkHttpClient
                .Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.SECONDS).build();

        RequestBody requestBody = RequestBody.create(JSON, body);
        Request request = new Request.Builder()
                .url(requestUrl)
                .post(requestBody)
                .build();
        if (header!=null){
            request =  request.newBuilder().headers(header).build();
        }

        final Request finalRequest = request;
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callbackJson.onFailure( call,  e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null) {
                    if (response.code() == 200) {
                        try {
                            String responseBody = response.body().string();
                            JSONObject jsonBody = new JSONObject(responseBody);
                            callbackJson.onJsonResponse(jsonBody);
//                            return jsonBody;
                        } catch (JSONException e) {
                            Logger.e(e, "postJson parse body crashed, request:\n%s\nresponse:\n%s", finalRequest, response);
//                            return null;
                        } catch (IOException e) {
                            Logger.e(e, "postJson parse body crashed, request:\n%s\nresponse:\n%s", finalRequest, response);
//                            return null;
                        }
                    } else {
                        Logger.e("postJson error, request is \n%s, \nresponse is \n%s", finalRequest, response);
                    }
                    response.body().close();
                    response.close();
                } else {
                    Logger.e("postJson response is null, request is \n%s", finalRequest);
                }


            }
        });

    }

    //拉取历史记录
    @Nullable
    private static JSONObject postJson(String requestUrl, String body, Headers header) {
        OkHttpClient httpClient = new OkHttpClient
                .Builder()
                .connectTimeout(time * num, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.SECONDS).build();
        Logger.i("http请求次数;"+num);
        RequestBody requestBody = RequestBody.create(JSON, body);
        Request request = new Request.Builder()
                .url(requestUrl)
                .post(requestBody)
                .build();
        if (header!=null){
            request =  request.newBuilder().headers(header).build();
        }

        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
            if (response != null) {

                if (response.code() == 200) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonBody = new JSONObject(responseBody);
                        return jsonBody;
                    } catch (JSONException e) {
                        Logger.e(e, "postJson parse body crashed, request:\n%s\nresponse:\n%s", request, response);
                        return null;
                    } catch (IOException e) {
                        Logger.e(e, "postJson parse body crashed, request:\n%s\nresponse:\n%s", request, response);
                        return null;
                    }
                } else {
                    Logger.e("postJson error, request is \n%s, \nresponse is \n%s", request, response);
                }
                response.body().close();
                response.close();
            } else {
                Logger.e("postJson response is null, request is \n%s", request);
            }
        } catch (IOException e) {
            if ( num < 3) {
                num++;
                Logger.e(e, "postJson calling crashed, request:\n%s", request);
//                return postJson(requestUrl, body,header);
            }
//                httpClient.newCall(request).execute();
            Logger.e(e, "postJson calling crashed, request:\n%s", request);
//            return null;
        }


        return null;
    }
    //异步post方法


    private static JSONObject postJson(String requestUrl, String body, Headers header,int timeout) {
        OkHttpClient httpClient = new OkHttpClient
                .Builder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS).build();
        Logger.i("http请求次数;"+num);
        RequestBody requestBody = RequestBody.create(JSON, body);
        Request request = new Request.Builder()
                .url(requestUrl)
                .post(requestBody)
                .build();
        if (header!=null){
            request =  request.newBuilder().headers(header).build();
        }

        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
            if (response != null) {

                if (response.code() == 200) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonBody = new JSONObject(responseBody);
                        return jsonBody;
                    } catch (JSONException e) {
                        Logger.e(e, "postJson parse body crashed, request:\n%s\nresponse:\n%s", request, response);
                        return null;
                    } catch (IOException e) {
                        Logger.e(e, "postJson parse body crashed, request:\n%s\nresponse:\n%s", request, response);
                        return null;
                    }
                } else {
                    Logger.e("postJson error, request is \n%s, \nresponse is \n%s", request, response);
                }
                response.body().close();
                response.close();
            } else {
                Logger.e("postJson response is null, request is \n%s", request);
            }
        } catch (IOException e) {
            if ( num < 3) {
                num++;
                Logger.e(e, "postJson calling crashed, request:\n%s", request);
//                return postJson(requestUrl, body,header,timeout);
            }
//                httpClient.newCall(request).execute();
            Logger.e(e, "postJson calling crashed, request:\n%s", request);
//            return null;
        }


        return null;
    }

    private static void setHeaders(Map<String ,String > header){
        if(header != null && header.containsKey(COOKIE)){
            String cookie = header.get(COOKIE);
            if(TextUtils.isEmpty(cookie)){
                header.put(COOKIE,Q_CKEY + Protocol.getCKEY());
            }else {
                if(!cookie.contains(Q_CKEY)){//有Cookie 没有q_ckey
                    header.put(COOKIE,cookie + ";" + Q_CKEY + Protocol.getCKEY());
                }
            }
        }else if(header == null){
            header = new HashMap<>();
            header.put(COOKIE,Q_CKEY + Protocol.getCKEY());
        }else if(!header.containsKey(COOKIE)){
            header.put(COOKIE,Q_CKEY + Protocol.getCKEY());
        }
    }

    public static JSONObject postJson(String requestUrl, JSONArray input, Map<String ,String > header){
        setHeaders(header);
        Headers headers = Headers.of(header);
        return postJson(requestUrl,input.toString(),headers);
    }
    public static JSONObject postJson(String requestUrl, JSONArray input, Map<String ,String > header,int timeout){
        setHeaders(header);
        Headers headers = Headers.of(header);
        return postJson(requestUrl,input.toString(),headers,timeout);
    }

    public static JSONObject postJson(String requestUrl, JSONObject input, Map<String ,String > header)throws IOException, JSONException{
        setHeaders(header);
        Headers headers = Headers.of(header);
        num = 1;
        return postJson(requestUrl,input.toString(),headers);
    }

    public static JSONObject postJson(String requestUrl, JSONArray input) {
        Map<String ,String > header = new HashMap<>();
        setHeaders(header);
        Headers headers = Headers.of(header);
        return postJson(requestUrl, input.toString(),headers);
    }

    public static JSONObject postJson(String requestUrl, JSONObject input) throws IOException, JSONException {
        //
        //重置请求次数
        num = 1;
        Map<String ,String > header = new HashMap<>();
        setHeaders(header);
        Headers headers = Headers.of(header);
        return postJson(requestUrl, input.toString(),headers);
    }

    public static void asyncPostJson(String requestUrl, JSONArray input, CallbackJson callbackJson) {
        Map<String ,String > header = new HashMap<>();
        setHeaders(header);
        Headers headers = Headers.of(header);
        asyncPostJson(requestUrl, input.toString(),headers, callbackJson, 10, 40);
    }

    public static void asyncPostJson(String requestUrl, JSONObject input, CallbackJson callbackJson) {
        Map<String ,String > header = new HashMap<>();
        setHeaders(header);
        Headers headers = Headers.of(header);
        asyncPostJson(requestUrl, input.toString(),headers, callbackJson, 10, 40);
    }
    public static void asyncPostJson(String requestUrl,JSONObject input,Map<String,String> header, CallbackJson callbackJson){
        setHeaders(header);
        Headers headers = Headers.of(header);
        asyncPostJson(requestUrl,input.toString(),headers,callbackJson, 10, 40);
    }

    public static void asyncPostJsonforString(String requestUrl, String input, Map<String, String> header, CallbackJson callbackJson, int connectTimeOut, int readTimeOut){
        setHeaders(header);
        Headers headers = Headers.of(header);
        asyncPostJson(requestUrl,input,headers,callbackJson, 10, 40);
    }


    public static JSONObject takeSmsCode(String userName) throws IOException, JSONException {
        String destUrl = QtalkNavicationService.getInstance().getVerifySmsUrl();
        return postJson(destUrl, String.format("rtx_id=%s", userName),null);
    }

    public static void asyncGetJson(String url,Map<String,String> header,CallbackJson callbackJson){
        setHeaders(header);
        Headers headers = Headers.of(header);
        asyncGetJson(url,headers,10,callbackJson);

    }
    private static void asyncGetJson(String url, Headers header, int timeout, final CallbackJson callbackJson){
        Logger.i("请求地址:" + url + ";请求cookie:" + header.toString());
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json; encoding=utf-8")
                .addHeader("Accept", "application/json")
                .build();
        if (header!=null){
            request =  request.newBuilder().headers(header).build();
        }

        OkHttpClient httpClient = new OkHttpClient
                .Builder()
                .connectTimeout(timeout < 10 ? timeout : 10, TimeUnit.SECONDS)
                .readTimeout(timeout < 10 ? timeout : timeout - 10, TimeUnit.SECONDS).build();
        final Request finalRequest = request;
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null) {
                    if (response.code() == 200) {
                        try {
                            String responseBody = response.body().string();
                            JSONObject jsonBody = new JSONObject(responseBody);
                            callbackJson.onJsonResponse(jsonBody);
//                            return jsonBody;
                        } catch (JSONException e) {
                            Logger.e(e, "postJson parse body crashed, request:\n%s\nresponse:\n%s", finalRequest, response);
//                            return null;
                        } catch (IOException e) {
                            Logger.e(e, "postJson parse body crashed, request:\n%s\nresponse:\n%s", finalRequest, response);
//                            return null;
                        }
                    } else {
                        Logger.e("postJson error, request is \n%s, \nresponse is \n%s", finalRequest, response);
                    }
                    response.body().close();
                    response.close();
                } else {
                    Logger.e("postJson response is null, request is \n%s", finalRequest);
                }


            }
        });
    }

    public interface SimpleCallback {
        void onSussecss(boolean isSussecss);
        void onFailure();

    }


    public static void asyncGetSimple(String url, Headers header, int timeout, final SimpleCallback callbackJson){
        Logger.i("请求地址:" + url + ";请求cookie:" + header.toString());
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json; encoding=utf-8")
                .addHeader("Accept", "application/json")
                .build();
        if (header!=null){
            request =  request.newBuilder().headers(header).build();
        }

        OkHttpClient httpClient = new OkHttpClient
                .Builder()
                .connectTimeout(timeout < 10 ? timeout : 10, TimeUnit.SECONDS)
                .readTimeout(timeout < 10 ? timeout : timeout - 10, TimeUnit.SECONDS).build();
        final Request finalRequest = request;
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Logger.i("checkhelat-"+e.getMessage());
                callbackJson.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null) {
                    if (response.code() == 200) {
                        try {
                            String responseBody = response.body().string();
                            JSONObject jsonBody = new JSONObject();
                            callbackJson.onSussecss(true);
//                            return jsonBody;
                        }  catch (IOException e) {
                            Logger.e(e, "checkhelat-ostJson parse body crashed, request:\n%s\nresponse:\n%s", finalRequest, response);
//                            return null;
                        }
                    } else {
                        callbackJson.onFailure();
                        Logger.e("checkhelat-postJson error, request is \n%s, \nresponse is \n%s", finalRequest, response);
                    }
                    response.body().close();
                    response.close();
                } else {
                    callbackJson.onFailure();
                    Logger.e("checkhelat-postJson response is null, request is \n%s", finalRequest);
                }


            }
        });
    }

    public static JSONObject getJson(String url) throws IOException, JSONException {
        Map<String ,String > header = new HashMap<>();
        setHeaders(header);
        Headers headers = Headers.of(header);
        return getJson(url, 50,headers);
    }

    private static JSONObject getJson(String url, int timeout, Headers header) {

        try {
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Content-Type", "application/json; encoding=utf-8")
                    .addHeader("Accept", "application/json")
                    .build();
            if (header!=null){
                request =  request.newBuilder().headers(header).build();
            }

            OkHttpClient httpClient = new OkHttpClient
                    .Builder()
                    .connectTimeout(timeout < 10 ? timeout : 10, TimeUnit.SECONDS)
                    .readTimeout(timeout < 10 ? timeout : timeout - 10, TimeUnit.SECONDS).build();

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

                    NavConfigResult result = JsonUtils.getGson().fromJson(responseStrBuilder.toString(), NavConfigResult.class);

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
            e.printStackTrace();
        }
        return null;
    }

    public static QtalkHttpRequest buildFormRequest(String url) {
        return new QtalkHttpRequest(url);
    }


}
