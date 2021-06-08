package com.qunar.im.base.protocol;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;

import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.jsonbean.DepartmentResult;
import com.qunar.im.base.jsonbean.GetDepInfo;
import com.qunar.im.base.jsonbean.GetUserAbridgeResult;
import com.qunar.im.base.jsonbean.GetUserStatus;
import com.qunar.im.base.jsonbean.IPAddressResult;
import com.qunar.im.base.jsonbean.InviteInfo;
import com.qunar.im.base.jsonbean.Profile4mUCenter;
import com.qunar.im.base.jsonbean.QVTResponseResult;
import com.qunar.im.base.jsonbean.SearchUserResult;
import com.qunar.im.base.jsonbean.UserStatusResult;
import com.qunar.im.base.jsonbean.VCardResult;
import com.qunar.im.base.util.BinaryUtil;
import com.qunar.im.base.util.ListUtil;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.common.CurrentPreference;
import com.qunar.im.core.services.QtalkNavicationService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.qunar.im.base.util.JsonUtils.getGson;


/**
 * Created by wujunwen on 14-5-9.
 * modify: 有其它module反射调用getCKEY方法 keep class
 */
public class Protocol {
    private static final String TAG = "Protocol";

    private static String user_key = "u";
    private static String verify_key = "k";
    private static String domain_key = "d";
    private static final String PLATFORMKEY = "p";
    private static final String PLATFORMVALUE = "qtadr";
    private static final String VERSIONKEY = "v";

    public static String getCKEY() {
        long t = System.currentTimeMillis() / 1000;
        String sk =  getRemoteLoginKey();
        String seed = sk + t;
        String key = BinaryUtil.MD5(seed);
        if (!TextUtils.isEmpty(key)) key = key.toUpperCase();
        String queryStr = "t=" + t + "&u=" + CurrentPreference.getInstance().getUserid()
                + "&k=" + key + "&d=" + QtalkNavicationService.getInstance().getXmppdomain()
                +"&sk="+sk;
        Logger.i("李海彬专用加密前："+queryStr);
        String ckey = Base64.encodeToString(queryStr.getBytes(), Base64.NO_WRAP);

        return ckey;
    }

    public static String getRemoteLoginKey(){
        try {
            Class class1 = Class.forName("com.qunar.im.core.manager.IMLogicManager");
            Method method = class1.getMethod("getInstance");

            Object instance = method.invoke(new Object());
            Method method1 = class1.getMethod("getRemoteLoginKey");
            Object result = method1.invoke(instance);
            if(result != null){
                return result.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String makeQVTHeader(){
        String qvtStr = CurrentPreference.getInstance().getQvt();
        if (TextUtils.isEmpty(qvtStr)) {
            return null;
        }
        QVTResponseResult qvtResponseResult = getGson().fromJson(qvtStr, QVTResponseResult.class);
        if (qvtResponseResult == null || qvtResponseResult.data == null) {
            return null;
        }
        String tCookie = qvtResponseResult.data.tcookie;//"24180933";
        String qCookie = qvtResponseResult.data.qcookie;//"U.xinwemk3120";
        String vCookie = qvtResponseResult.data.vcookie;
        String nameAndValues = "_q=" + qCookie + ";_v=" + vCookie +
                ";_t=" + tCookie;

        String ckey = Protocol.getCKEY();
        if (!TextUtils.isEmpty(ckey)){
            nameAndValues += ";q_ckey=" + ckey;
        }
        return  nameAndValues;
    }

    public static String getUrl(String host, String params) {
        return String.format("%s/%s", host, params);
    }

    public static String makeGetUri(String host, int port, String params, boolean isHttps) {
        if (host.startsWith("https://") || host.startsWith("http://")) {
            return String.format("%s/%s", host, params);
        }

        String method = isHttps ? "https" : "http";
        return String.format("%s://%s:%s/%s", method, host, port, params);
    }

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


    public static void crossDomainSearchUser(String keyworkd, final ProtocolCallback.UnitCallback<SearchUserResult> callback) {
        try {
            if (TextUtils.isEmpty(CurrentPreference.getInstance().getVerifyKey())) {
                return;
            }
            StringBuilder params = new StringBuilder("domain/search_person?");
            addBasicParamsOnHead(params);
            String postJson = "{\"keyword\":\"" + keyworkd + "\"}";
            String url = makeGetUri(QtalkNavicationService.getInstance().getHttpHost(), QtalkNavicationService.getInstance().getHttpPort(), params.toString(), true);
            HttpUrlConnectionHandler.executePostJson(url, postJson, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    SearchUserResult result = null;
                    try {
                        String resultString = parseStream(response);
                        result = getGson().fromJson(resultString, SearchUserResult.class);
                    } catch (Exception e) {
                        LogUtil.e(TAG, "error", e);
                    }

                    callback.onCompleted(result);
                }

                @Override
                public void onFailure(Exception e) {
                    callback.doFailure();
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG, "error", e);
        }
    }

    public static void getUserStatus(String jid, final ProtocolCallback.UnitCallback<UserStatusResult> callback) {
        try {
            StringBuilder queryString = new StringBuilder("domain/get_user_status.qunar?");
            if (TextUtils.isEmpty(CurrentPreference.getInstance().getVerifyKey())) {
                callback.doFailure();
                return;
            }
            Protocol.addBasicParamsOnHead(queryString);
            GetUserStatus status = new GetUserStatus();
            status.users = new ArrayList<>();
            status.users.add(jid);
            String jsonParams = getGson().toJson(status);
            String url = Protocol.makeGetUri(QtalkNavicationService.getInstance().getHttpUrl(), QtalkNavicationService.getInstance().getHttpPort(), queryString.toString(), true);
            HttpUrlConnectionHandler.executePostJson(url, jsonParams, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    UserStatusResult result = null;
                    try {

                        String resultString = Protocol.parseStream(response);
                        result = getGson().fromJson(resultString, UserStatusResult.class);
                    } catch (Exception e) {
                        LogUtil.e(TAG, "error", e);
                    }
                    callback.onCompleted(result);
                }

                @Override
                public void onFailure(Exception e) {
                    callback.doFailure();
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG, "error", e);
        }
    }

    public static void addBasicParamsOnHead(StringBuilder url) {
        addBasicParamsOnHead(url,null);

    }

    public static void addBasicParamsOnHead(StringBuilder url,String domain) {
        if (url.indexOf("?") == -1) {
            url.append("?");
        }
        char last = url.charAt(url.length() - 1);
        if (last != '&' && last != '?') {
            url.append('&');
        }
        if (url.indexOf("u=") == -1) {
            url.append(user_key);
            url.append("=");
            url.append(CurrentPreference.getInstance().getUserid());
            url.append("&");
        }
        if (url.indexOf("k=") == -1) {
            url.append(verify_key);
            url.append("=");
            url.append(CurrentPreference.getInstance().getVerifyKey());
            url.append("&");
        }
        if (url.indexOf("d=") == -1) {
            url.append(domain_key);
            url.append("=");
            if(TextUtils.isEmpty(domain)){
                url.append(QtalkNavicationService.getInstance().getXmppdomain());
            }else {
                url.append(domain);
            }
            url.append("&");
        }
        if (url.indexOf("p=") == -1) {
            url.append(PLATFORMKEY);
            url.append("=");
            url.append(PLATFORMVALUE);
            url.append("&");
        }
        if (url.indexOf("v=") == -1) {
            url.append(VERSIONKEY);
            url.append("=");
            url.append(QunarIMApp.getQunarIMApp().getVersion());
        }
        char last1 = url.charAt(url.length() - 1);
        if (last1 == '&') {
            url.deleteCharAt(url.length() - 1);
        }

    }

    public static void addParams2Url(StringBuilder url, JSONObject params) {
        if (!TextUtils.isEmpty(url) && params != null) {
            Map<String, String> urlMap = splitParams(Uri.parse(url.toString()));
            Map<String, String> paramsMap = new HashMap<>();
            JSONArray paramsArray = params.optJSONArray("param");
            if (paramsArray != null && paramsArray.length() > 0) {
                for (int i = 0; i < paramsArray.length(); i++) {
                    JSONObject jsonObject = paramsArray.optJSONObject(i);
                    Map<String, String> m = getGson().fromJson(jsonObject.toString(), Map.class);
                    for (Map.Entry<String, String> entry : m.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue().toString();
                        paramsMap.put(key, value);
                    }
                }
            }
            if (paramsMap != null && paramsMap.size() > 0 && urlMap != null && urlMap.size() > 0) {//如果url中已包含需要放的参数则覆盖掉
                for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue().toString();
                    urlMap.put(key, value);
                }
                url = url.delete(url.indexOf("?"), url.length());
                spiltJointUrl(url, urlMap);
            } else {
                spiltJointUrl(url, paramsMap);
            }
        }
    }

    public static void spiltJointUrl(StringBuilder url, Map<String, String> params) {
        if (params == null || params.size() == 0) return;
        if (url.indexOf("?") == -1) {
            url.append("?");
        }
        char last = url.charAt(url.length() - 1);
        if (last != '&' && last != '?') {
            url.append('&');
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            url.append(entry.getKey());
            url.append("=");
            url.append(entry.getValue());
            url.append("&");
        }
        char lastChar = url.charAt(url.length() - 1);
        if (lastChar == '&') {//删除最后一个&
            url.deleteCharAt(url.length() - 1);
        }
    }

    public static HashMap<String, String> splitParams(Uri uri) {
        if (uri == null) {
            return new HashMap<String, String>();
        }
        Set<String> keys = getQueryParameterNames(uri);
        HashMap<String, String> map = new HashMap<String, String>(keys.size());
        for (String key : keys) {
            map.put(key, uri.getQueryParameter(key));
        }
        return map;
    }

    public static Map<String,String> splitParams(String params){
        if (TextUtils.isEmpty(params)) {
            return new HashMap<String, String>();
        }
        Map<String,String> keyValues = new HashMap<>();
        String[] s = params.split("&");
        int length = (s == null ? 0 : s.length);
        for(int i = 0;i<length;i++){
            String[] ss = s[i].split("=");
            if(ss != null && ss.length < 2){
                continue;
            }
            keyValues.put(ss[0],ss[1]);
            Logger.i("splitParams:" + ss[0] + ":" + ss[1]);
        }
        return keyValues;
    }

    public static Set<String> getQueryParameterNames(Uri uri) {
        if (uri.isOpaque()) {
            throw new UnsupportedOperationException("This isn't a hierarchical URI.");
        }

        String query = uri.getEncodedQuery();
        if (query == null) {
            return Collections.emptySet();
        }

        Set<String> names = new LinkedHashSet<String>();
        int start = 0;
        do {
            int next = query.indexOf('&', start);
            int end = next == -1 ? query.length() : next;

            int separator = query.indexOf('=', start);
            if (separator > end || separator == -1) {
                separator = end;
            }

            String name = query.substring(start, separator);
            names.add(Uri.decode(name));

            // Move start to end of name.
            start = end + 1;
        } while (start < query.length());

        return Collections.unmodifiableSet(names);
    }


    /**
     * 获取qchat组织架构
     *
     * @param callback
     */
    public static void getQchatDeptInfo(final ProtocolCallback.UnitCallback<DepartmentResult> callback) {
        try {
            StringBuilder params = new StringBuilder("get_dep_info?");
            if (TextUtils.isEmpty(CurrentPreference.getInstance().getVerifyKey())) {
                callback.doFailure();
                return;
            }
            addBasicParamsOnHead(params);
            String url = makeGetUri(QtalkNavicationService.getInstance().getHttpHost(), QtalkNavicationService.getInstance().getHttpPort(), params.toString(), true);
            Map<String, String> postParams = new HashMap<String, String>();
            postParams.put("strid", CurrentPreference.getInstance().getUserid());
            Logger.i("qchat组织架构地址:" + url + ",请求参数:" + postParams);

            HttpUrlConnectionHandler.executePostForm(url, postParams, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    DepartmentResult result = null;
                    try {
                        String resultString = parseStream(response);
                        if (TextUtils.isEmpty(resultString)) {
                            callback.onFailure("");
                            return;
                        }
                        result = getGson().fromJson(resultString, DepartmentResult.class);
                    } catch (Exception e) {
                        LogUtil.e(TAG, "error", e);
                        result = null;
                    }
                    callback.onCompleted(result);
                }

                @Override
                public void onFailure(Exception e) {
                    callback.doFailure();
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG, "error", e);
        }
    }

    /**集成push后新版注册push接口  start*/
    /**集成push后新版注册push接口  end*/
    public static void getAbility(String clientName, String userid, int checkVersion,
                                  final ProtocolCallback.UnitCallback<String> callback) {
        HttpUrlConnectionHandler.executeGet(QtalkNavicationService.getInstance().getCheckconfig() +
                "?v=" + clientName + "&ver=" + QunarIMApp.getQunarIMApp().getVersion() + "&p=" +
                "android&u=" + userid + "&cv=" + checkVersion, new HttpRequestCallback() {
            @Override
            public void onComplete(InputStream response) {
                String resultString = null;
                try {
                    resultString = parseStream(response);
                } catch (IOException e) {
                    LogUtil.e(TAG, "error", e);
                }
                callback.onCompleted(resultString);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure("");
            }
        });
    }

    public static void getInviteInfo(String userId, long time, final ProtocolCallback.UnitCallback<InviteInfo> callback) {
        try {
            StringBuilder params = new StringBuilder("base/get_invite_info.qunar?");
            if (TextUtils.isEmpty(CurrentPreference.getInstance().getVerifyKey())) {
                callback.doFailure();
                return;
            }
            addBasicParamsOnHead(params);
            String url = makeGetUri(QtalkNavicationService.getInstance().getHttpHost(), QtalkNavicationService.getInstance().getHttpPort(), params.toString(), true);

            String postBody = "{\"user\":\"" + userId + "\",\"" + "d" + "\":\"" + QtalkNavicationService.getInstance().getXmppdomain() + "\",\"time\":" + time + "}";
            HttpUrlConnectionHandler.executePostJson(url, postBody, new HttpRequestCallback() {
                InviteInfo result = null;

                @Override
                public void onComplete(InputStream response) {
                    LogUtil.d("debug", "complete");
                    try {
                        String resultString = parseStream(response);
                        if (!TextUtils.isEmpty(resultString)) {
                            result = getGson().fromJson(resultString, InviteInfo.class);
                        }
                    } catch (Exception e) {
                        LogUtil.e(TAG, "error", e);
                    }
                    callback.onCompleted(result);
                }

                @Override
                public void onFailure(Exception e) {
                    callback.doFailure();
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG, "error", e);
        }
    }

    public static void getOwnIpAddress(final ProtocolCallback.UnitCallback<IPAddressResult> callback) {
        StringBuilder params = new StringBuilder("/php/ucproxy/index.php/rest/tellmeip");
        try {
            String url = makeGetUri(QtalkNavicationService.getInstance().getHttpHost(), QtalkNavicationService.getInstance().getHttpPort(), params.toString(), true);
            HttpUrlConnectionHandler.executeGet(url, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    IPAddressResult result = null;
                    try {
                        String resultString = parseStream(response);
                        result = getGson().fromJson(resultString, IPAddressResult.class);
                    } catch (Exception e) {
                        LogUtil.e(TAG, "error", e);
                    }
                    callback.onCompleted(result);
                }

                @Override
                public void onFailure(Exception e) {
                    callback.doFailure();
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG, "error", e);
        }
    }

    @Deprecated
    public static void getDepartmentInfo(final ProtocolCallback.UnitCallback<List<GetDepInfo>> callback) {
        StringBuilder params = new StringBuilder("getusers?");

        if (TextUtils.isEmpty(CurrentPreference.getInstance().getVerifyKey())) {
            callback.doFailure();
            return;
        }
        addBasicParamsOnHead(params);
        try {

            String url = makeGetUri(QtalkNavicationService.getInstance().getHttpHost(), QtalkNavicationService.getInstance().getHttpPort(), params.toString(), true);
            LogUtil.d("debug", "getDeptInfo");
            HttpUrlConnectionHandler.executeGet(url, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    List<GetDepInfo> result = null;
                    try {
                        String resultString = parseStream(response);
                        result = getGson().fromJson(resultString, new TypeToken<List<GetDepInfo>>() {
                        }.getType());
                    } catch (Exception e) {
                        LogUtil.e(TAG, "error", e);
                    }
                    callback.onCompleted(result);
                }

                @Override
                public void onFailure(Exception e) {
                    callback.doFailure();
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG, "error", e);
        }
    }

    @Deprecated
    public static void getAbridgeName(final ProtocolCallback.UnitCallback<List<GetUserAbridgeResult>> callback) {
        StringBuilder params = new StringBuilder("getusersuoxie?");
        if (TextUtils.isEmpty(CurrentPreference.getInstance().getVerifyKey())) {
            callback.doFailure();
            return;
        }
        addBasicParamsOnHead(params);

        try {

            String url = makeGetUri(QtalkNavicationService.getInstance().getHttpHost(), QtalkNavicationService.getInstance().getHttpPort(), params.toString(), true);
            HttpUrlConnectionHandler.executeGet(url, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    List<GetUserAbridgeResult> result = null;
                    try {
                        String resultString = parseStream(response);
                        result = getGson().fromJson(resultString, new TypeToken<List<GetUserAbridgeResult>>() {
                        }.getType());
                    } catch (Exception e) {
                        LogUtil.e(TAG, "error", e);
                    }
                    callback.onCompleted(result);
                }

                @Override
                public void onFailure(Exception e) {
                    callback.doFailure();
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG, "error", e);
        }
    }

    @Deprecated
    public static void getVCard(String users, final ProtocolCallback.UnitCallback<VCardResult> callback) {
        try {
            StringBuilder queryString = new StringBuilder("getvcardversion?");
            queryString.append("&users=");
            queryString.append(users);
            queryString.append("&");


            if (TextUtils.isEmpty(CurrentPreference.getInstance().getVerifyKey())) {
                callback.doFailure();
                return;
            }

            addBasicParamsOnHead(queryString);
            String url = makeGetUri(QtalkNavicationService.getInstance().getHttpHost(), QtalkNavicationService.getInstance().getHttpPort(), queryString.toString(), true);
            HttpUrlConnectionHandler.executeGet(url, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    VCardResult result = null;
                    try {

                        String resultString = parseStream(response);
                        result = getGson().fromJson(resultString, VCardResult.class);
                    } catch (Exception e) {
                        LogUtil.e(TAG, "error", e);
                    }
                    callback.onCompleted(result);
                }

                @Override
                public void onFailure(Exception e) {
                    callback.doFailure();
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG, "error", e);
        }
    }


    public static void getDepartment(final ProtocolCallback.UnitCallback<String> callback) {
        try {
            StringBuilder params = new StringBuilder("getdeps?");
            if (TextUtils.isEmpty(CurrentPreference.getInstance().getVerifyKey())) {
                callback.doFailure();
                return;
            }
            addBasicParamsOnHead(params);
            String url = makeGetUri(QtalkNavicationService.getInstance().getHttpHost(), QtalkNavicationService.getInstance().getHttpPort(), params.toString(), true);
            final long getTime = System.currentTimeMillis();
            HttpUrlConnectionHandler.executeGet(url, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {

                    String resultString = null;
                    LogUtil.d("debug", "complete");
                    try {
                        resultString = parseStream(response);
                        Logger.i("获取组织架构结果:"+resultString);
                    } catch (Exception e) {
                        LogUtil.e(TAG, "error", e);
                    }
                    callback.onCompleted(resultString);
                }

                @Override
                public void onFailure(Exception e) {
                    callback.doFailure();
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG, "error", e);
        }
    }

    /**
     * 获取用户中心个人信息(缓存数据1小时)
     *
     * @param uid
     * @param callback
     */
    @Deprecated
    public static void getProfileFromUCenter(final String uid, final ProtocolCallback.UnitCallback<Profile4mUCenter> callback) {
        try {
            StringBuilder params = new StringBuilder("get_user_vcard_info?");
            if (TextUtils.isEmpty(CurrentPreference.getInstance().getVerifyKey())) {
                callback.doFailure();
                return;
            }
            addBasicParamsOnHead(params);
            String url = makeGetUri(QtalkNavicationService.getInstance().getHttpHost(), QtalkNavicationService.getInstance().getHttpPort(), params.toString(), true);
            Map<String, String> postParams = new HashMap<String, String>();
            postParams.put(user_key, CurrentPreference.getInstance().getUserid());
            postParams.put(verify_key, CurrentPreference.getInstance().getVerifyKey());
            postParams.put("strid", uid);
            HttpUrlConnectionHandler.executePostForm(url, postParams, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    Profile4mUCenter result = null;
                    LogUtil.d("debug", "complete");
                    try {
                        String resultString = parseStream(response);
                        result = getGson().fromJson(resultString, Profile4mUCenter.class);
                    } catch (Exception e) {
                        LogUtil.e(TAG, "error", e);
                    }
                    if (result != null && !ListUtil.isEmpty(result.data)) {
                        callback.onCompleted(result);
                    } else {
                        callback.doFailure();
                    }

                }

                @Override
                public void onFailure(Exception e) {
                    callback.doFailure();
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG, "error", e);
        }
    }
}
