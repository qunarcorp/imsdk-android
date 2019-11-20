package com.qunar.im.base.protocol;


import android.text.TextUtils;

import com.qunar.im.base.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by saber on 15-8-13.
 */
public class HttpUrlConnectionHandler {
    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("text/x-markdown; charset=utf-8");
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public static final MediaType MULTIPART_DATA
            = MediaType.parse("multipart/form-data; charset=utf-8");

    private static final String COOKIE = "Cookie";

    private static final String Q_CKEY = "q_ckey=";

    private static final OkHttpClient client = new OkHttpClient();

    private static final Map<String,HttpNamedDownload> runningCall = new HashMap<>();
    public static final OkHttpClient DEFAULT_CLIENT = client;


    public static boolean checkRunning(String url, final ProgressResponseListener listener)
    {
        if(runningCall.containsKey(url))
        {
            HttpNamedDownload download = runningCall.get(url);
            if(download.client!=null&&listener!=null)
            {
                download.client.networkInterceptors().clear();
                download.client.networkInterceptors().add(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        //拦截
                        Response originalResponse = chain.proceed(chain.request());
                        //包装响应体并返回
                        return originalResponse.newBuilder()
                                .body(new ProgressResponseBody(originalResponse.body(), listener))
                                .build();
                    }
                });
            }
            return true;
        }
        return false;
    }

    public static void excuteDownload(final String url, final ProgressResponseListener progressResponseListener, final HttpContinueDownloadCallback callback) {
        LogUtil.d("HttpUrlConnectionHandler1", "url:" + url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        OkHttpClient cloneClient = client;
        final HttpNamedDownload httpNamedDownload = new HttpNamedDownload();
        if (progressResponseListener != null) {
            cloneClient = addProgressResponseListener(client, progressResponseListener);
            httpNamedDownload.client = cloneClient;
        }
        Call call = cloneClient.newCall(request);
        httpNamedDownload.call = call;
        runningCall.put(url, httpNamedDownload);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call request, IOException e) {
                runningCall.remove(url);
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call,Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        callback.onFailure(new Exception("faild, code is :" + response.code()));
                        return;
                    }
                    callback.onComplete(response.body().byteStream(), false);
                    response.body().close();
                } finally {
                    runningCall.remove(url);
                }
            }
        });
    }

    public static void cancelDownload(String url)
    {
        HttpNamedDownload call = runningCall.get(url);
        if(call!=null&&call.call!=null)
        {
            runningCall.remove(url);
            if(call.call.isCanceled())
                call.call.cancel();
        }
    }

    public static void excuteContinueDownload(final String url,ProgressResponseListener progressResponseListener,String firstByte,final HttpContinueDownloadCallback callback) {
        LogUtil.d("HttpUrlConnectionHandler2", "url:" + url);
        Request request = new Request.Builder().url(url)
                .addHeader("Range", "bytes=" + firstByte + "-")
                .build();
        OkHttpClient cloneClient = client;
        final HttpNamedDownload httpNamedDownload = new HttpNamedDownload();
        if (progressResponseListener != null) {
            cloneClient = addProgressResponseListener(client, progressResponseListener);
            httpNamedDownload.client = cloneClient;
        }
        Call call = cloneClient.newCall(request);
        httpNamedDownload.call = call;
        runningCall.put(url, httpNamedDownload);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call request, IOException e) {
                runningCall.remove(url);
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call request,Response response) throws IOException {
                runningCall.remove(url);
                if (!response.isSuccessful()) {
                    callback.onFailure(new Exception("faild, code is :" + response.code()));
                    return;
                }
                if (response.code() == 206 && !TextUtils.isEmpty(response.header("Content-Range"))) {
                    callback.onComplete(response.body().byteStream(), true);
                } else {
                    callback.onComplete(response.body().byteStream(), false);
                }
                response.body().close();
            }
        });
    }

    public static OkHttpClient addProgressResponseListener(OkHttpClient client,final ProgressResponseListener progressListener){

        return client.newBuilder().addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                //拦截
                Response originalResponse = chain.proceed(chain.request());
                //包装响应体并返回
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                        .build();
            }
        }).build();
    }

    public static void executeGetSync(final String url,final HttpRequestCallback callback)
    {
        LogUtil.d("HttpUrlConnectionHandler3","url:"+url);
        Map<String,String> header = new HashMap<>();
        Headers headers = Headers.of(header);
        header.put(COOKIE, Q_CKEY + get_qckey());
        Request request = new Request.Builder().url(url).headers(headers).build();
        try {
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()) {
                callback.onComplete(response.body().byteStream());
                return;
            }
            callback.onFailure(new Exception("status is not 200"));
        } catch (IOException e) {
            e.printStackTrace();
            callback.onFailure(e);
        }
    }
    public static InputStream excuteGetSync(final String url){
        LogUtil.d("HttpUrlConnectionHandler3","url:"+url);
        Map<String,String> header = new HashMap<>();
        Headers headers = Headers.of(header);
        header.put(COOKIE, Q_CKEY + get_qckey());
        Request request = new Request.Builder().url(url).headers(headers).build();
        try {
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()) {

                return response.body().byteStream();
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static void executeGet(final String url,final HttpRequestCallback callback) {
        LogUtil.d("HttpUrlConnectionHandler4","url:"+url);
        executeGet(url,null,callback);
    }

    public static void executeGet(final String url,Map<String,String> header,final HttpRequestCallback callback) {
        LogUtil.d("HttpUrlConnectionHandler5","url:"+url);
        if(header != null && header.containsKey(COOKIE)){
            String cookie = header.get(COOKIE);
            if(TextUtils.isEmpty(cookie)){
                header.put(COOKIE,Q_CKEY + get_qckey());
            }else {
                if(!cookie.contains(Q_CKEY)){//有Cookie 没有q_ckey
                    header.put(COOKIE,cookie + ";" + Q_CKEY + get_qckey());
                }
            }
        }else if(header == null){
            header = new HashMap<>();
            header.put(COOKIE,Q_CKEY + get_qckey());
        }else if(!header.containsKey(COOKIE)){
            header.put(COOKIE,Q_CKEY + get_qckey());
        }
        Headers headers = Headers.of(header);
        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call request, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call request,Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure(new Exception("faild, code is :" + response.code()));
                    return;
                }
                callback.onComplete(response.body().byteStream());
                response.body().close();
            }
        });
    }

    public static void executePostForm(final String url, final Map<String,String> params, final HttpRequestCallback callback) {
        LogUtil.d("HttpUrlConnectionHandler6","url:"+url);
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : params.keySet()) {
            builder.add(key, params.get(key));
            LogUtil.d("HttpUrlConnectionHandler7",key+ ":" + params.get(key));
        }
        RequestBody body = builder.build();
        Map<String,String> header = new HashMap<>();
        header.put(COOKIE, Q_CKEY + get_qckey());
        Headers headers = Headers.of(header);
        Request request = new Request.Builder().url(url)
                .post(body)
                .headers(headers)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call request, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call request,Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure(new Exception("faild, code is :" + response.code()));
                    return;
                }
                callback.onComplete(response.body().byteStream());
                response.body().close();
            }
        });
    }

    public static void executePostJson(final String url,Map<String,String> header,final String json, final HttpRequestCallback callback) {
        LogUtil.d("HttpUrlConnectionHandler8","url:"+url);
        RequestBody body = RequestBody.create(JSON, json);
        if(header != null && header.containsKey(COOKIE)){
            String cookie = header.get(COOKIE);
            if(TextUtils.isEmpty(cookie)){
                header.put(COOKIE,Q_CKEY + get_qckey());
            }else {
                if(!cookie.contains(Q_CKEY)){//有Cookie 没有q_ckey
                    header.put(COOKIE,cookie + ";" + Q_CKEY + get_qckey());
                }
            }
        }else if(header == null){
            header = new HashMap<>();
            header.put(COOKIE,Q_CKEY + get_qckey());
        }else if(!header.containsKey(COOKIE)){
            header.put(COOKIE,Q_CKEY + get_qckey());
        }
        Headers headers = Headers.of(header);
        Request request = new Request.Builder().url(url)
                .post(body)
                .headers(headers)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call request, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call request,Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure(new Exception("faild, code is :" + response.code()));
                    return;
                }
                callback.onComplete(response.body().byteStream());
                response.body().close();
            }
        });
    }

    public static void executePostJson(String url, String json, HttpRequestCallback callback) {
        LogUtil.d("HttpUrlConnectionHandler9","url:"+url);
        executePostJson(url,null,json,callback);
    }

    public static void executePostString(final String url,final String raw,final HttpRequestCallback callback) {
        LogUtil.d("HttpUrlConnectionHandler10","url:"+url);
        RequestBody body = RequestBody.create(MEDIA_TYPE_MARKDOWN, raw);
        Map<String,String> header = new HashMap<>();
        Headers headers = Headers.of(header);
        header.put(COOKIE, Q_CKEY + get_qckey());
        Request request = new Request.Builder().url(url).post(body).headers(headers).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call request, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call request,Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure(new Exception("faild, code is :" + response.code()));
                    return;
                }
                callback.onComplete(response.body().byteStream());
                response.body().close();
            }
        });
    }

    public static void executeUpload(final String url,final File file,final String key,
                                     final String fileName,ProgressRequestListener listener,Map<String,String> params
            ,final HttpRequestCallback callback) {
        LogUtil.d("HttpUrlConnectionHandler11","url:"+url);
        if(url == null) callback.onFailure(new NullPointerException());
        LogUtil.d("HttpUrlConnectionHandler12","url:"+url);
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart(key, fileName, RequestBody.create(MULTIPART_DATA, file));

        if(params!=null&&params.size()>0)
        {
            for(String pKey:params.keySet())
            {
                builder.addFormDataPart(pKey,params.get(pKey));
            }
        }
        RequestBody body = builder.build();
        if(listener!=null)
        {
            body = new ProgressRequestBody(body,listener);
        }
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call request, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call request,Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure(new Exception("faild, code is :" + response.code()));
                    return;
                }
                callback.onComplete(response.body().byteStream());
                response.body().close();
            }
        });
    }

    public static void executePostJsonSync(final String url, final String json,final HttpRequestCallback callback) {
        LogUtil.d("HttpUrlConnectionHandler13","url:"+url);
        RequestBody body = RequestBody.create(JSON, json);
        Map<String,String> header = new HashMap<>();
        Headers headers = Headers.of(header);
        header.put(COOKIE, Q_CKEY + get_qckey());
        Request request = new Request.Builder().url(url)
                .post(body)
                .headers(headers)
                .build();
        try{
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                callback.onFailure(new Exception("faild, code is :" + response.code()));
                return;
            }
            callback.onComplete(response.body().byteStream());
            response.body().close();
        }catch (IOException e){
            callback.onFailure(e);
        }
    }

    private static String get_qckey(){
        try {
            Class<?> clazz = Class.forName("com.qunar.im.base.protocol.Protocol");
            Method method = clazz.getMethod("getCKEY");
            Object result = method.invoke(clazz.newInstance());
            if(result != null){
                return result.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
