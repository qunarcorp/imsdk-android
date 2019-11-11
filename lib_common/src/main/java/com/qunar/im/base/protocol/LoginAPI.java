package com.qunar.im.base.protocol;

import android.text.TextUtils;

import com.qunar.im.base.jsonbean.BaseJsonResult;
import com.qunar.im.base.jsonbean.DomainResult;
import com.qunar.im.base.jsonbean.GeneralJson;
import com.qunar.im.base.jsonbean.GetSMSCodeResult;
import com.qunar.im.base.jsonbean.PublicVerifyResult;
import com.qunar.im.base.jsonbean.QChatLoginResult;
import com.qunar.im.base.jsonbean.QRCodeAuthResultJson;
import com.qunar.im.base.jsonbean.QVTResponseResult;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.PhoneInfoUtils;
import com.qunar.im.core.services.QtalkNavicationService;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by saber on 16-3-1.
 */
public class LoginAPI {
    private static final String TAG = LoginAPI.class.getSimpleName();
    /***
     *  qchat http登录
     * @param strid 用户名
     * @param pwd 密码，经过rsa加密
     * @param type 类型， email|mobile|username
     * @param callback 回调
     */
    public static void QChatLogin(String strid,String pwd,String prenum,String type,final ProtocolCallback.UnitCallback<QChatLoginResult> callback)
    {
        try {
            StringBuilder params = new StringBuilder("get_power");
            String url = Protocol.makeGetUri(QtalkNavicationService.getInstance().getHttpHost(),
                    QtalkNavicationService.getInstance().getHttpPort(), params.toString(), true);
            Map<String, String> postParams = new HashMap<String, String>();
            postParams.put("password",pwd);
            postParams.put("strid", strid);
            postParams.put("type",type);
            HttpUrlConnectionHandler.executePostForm(url, postParams, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    QChatLoginResult result = null;
                    LogUtil.d("qchat", "qchat login Success");
                    try {
                        String resultString = Protocol.parseStream(response);
                        result = JsonUtils.getGson().fromJson(resultString, QChatLoginResult.class);
                    } catch (Exception e) {
                        LogUtil.e(TAG,"error",e);
                    }
                    callback.onCompleted(result);
                }

                @Override
                public void onFailure(Exception e) {
                    LogUtil.d("qchat", "qchat login fail:"+e.getMessage());
                    callback.doFailure();
                }
            });
        }
        catch (Exception e) {
            LogUtil.e(TAG,"error",e);
        }
    }


    public static void getSmsCode(final String rtxId, final ProtocolCallback.UnitCallback<GetSMSCodeResult> callback) {
        try {
            String url = QtalkNavicationService.getInstance().getVerifySmsUrl();
            Map<String,String> params = new HashMap<String,String>();
            params.put("rtx_id", rtxId);
            HttpUrlConnectionHandler.executePostForm(url, params, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    GetSMSCodeResult result = null;
                    try {
                        String resultString = Protocol.parseStream(response); //parseGZIPStream(response.getEntity().getContent());
                        result = JsonUtils.getGson().fromJson(resultString, GetSMSCodeResult.class);
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

    public static void getToken(String rtxId, String verifyCode, final ProtocolCallback.UnitCallback<PublicVerifyResult> callback) {
        try {

            String url =QtalkNavicationService.getInstance().getTokenSmsUrl();
            Map<String,String> params = new HashMap<String,String>();
            params.put("rtx_id", rtxId);
            params.put("verify_code", verifyCode);
            HttpUrlConnectionHandler.executePostForm(url, params, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    PublicVerifyResult result = null;
                    try {
                        String resultString = Protocol.parseStream(response); //parseGZIPStream(response.getEntity().getContent());
                        result = JsonUtils.getGson().fromJson(resultString, PublicVerifyResult.class);
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


    public static void getQchatToken(String macCode,String qvt,String plat,final ProtocolCallback.UnitCallback<GeneralJson> callback ){
        StringBuilder params = new StringBuilder("http_gettk");
        Protocol.addBasicParamsOnHead(params);
        String url = Protocol.makeGetUri(QtalkNavicationService.getInstance().getHttpHost(),
                QtalkNavicationService.getInstance().getHttpPort(), params.toString(), true);
        String json = "{\"macCode\":\""+macCode+"\", \"plat\":\""+plat+"\"}";
        QVTResponseResult qvtResponseResult = JsonUtils.getGson().fromJson(qvt, QVTResponseResult.class);
        Map<String, String> cookie = new HashMap<>();
        cookie.put("Cookie","_q="+qvtResponseResult.data.qcookie+";_v="+qvtResponseResult.data.vcookie+
                ";_t="+ qvtResponseResult.data.tcookie);
        HttpUrlConnectionHandler.executePostJson(url,cookie,json,new HttpRequestCallback() {
            @Override
            public void onComplete(InputStream response) {
                try {
                    String resultString = Protocol.parseStream(response);
                    GeneralJson result = JsonUtils.getGson().fromJson(resultString,GeneralJson.class);
                    callback.onCompleted(result);
                } catch (Exception e) {
                    LogUtil.e(TAG,"error",e);
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure("");
            }
        });

    }

    /***
     *  qtalk公共域 http登录
     * @param strid 用户名
     * @param pwd 密码，经过rsa加密
     * @param callback 回调
     */
    public static void QTalkPublickLogin(String strid,String pwd,final ProtocolCallback.UnitCallback<QChatLoginResult> callback)
    {
        try {
            StringBuilder params = new StringBuilder("get_power");

            String url = Protocol.makeGetUri(QtalkNavicationService.getInstance().getHttpHost(),
                    QtalkNavicationService.getInstance().getHttpPort(), params.toString(), true);
            Map<String, String> postParams = new HashMap<String, String>();
            postParams.put("password",pwd);
            postParams.put("strid", strid);
            HttpUrlConnectionHandler.executePostForm(url, postParams, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    QChatLoginResult result = null;
                    LogUtil.d("qtalk", "QTalkPublickLogin  Success");
                    try {
                        String resultString = Protocol.parseStream(response);
                        result = JsonUtils.getGson().fromJson(resultString, QChatLoginResult.class);
                    } catch (Exception e) {
                        LogUtil.e(TAG,"error",e);
                    }
                    callback.onCompleted(result);
                }

                @Override
                public void onFailure(Exception e) {
                    LogUtil.d("qtalk", "QTalkPublickLogin fail:"+e.getMessage());
                    callback.doFailure();
                }
            });
        }
        catch (Exception e) {
            LogUtil.e(TAG,"error",e);
        }
    }
    /**
     * 认证二维码1阶段
     * @param qrcodekey qrcode
     * @param authdata 认证数据
     * @param phase  1：已扫码  2：已确认认证或者取消认证
     * @param callback
     */
    public static void QChatQRLoginAuth(String qrcodekey, String authdata, int phase, final ProtocolCallback.UnitCallback<QRCodeAuthResultJson> callback){
        String url = QtalkNavicationService.getInstance().getJavaUrl() + "/qtapi/common/qrcode/auth.qunar";
        String q_ckey = Protocol.getCKEY();
        if (TextUtils.isEmpty(q_ckey)) return;
        Map<String, String> cookie = new HashMap<>();
        cookie.put("Cookie", "q_ckey=" + q_ckey);
        try {
            Map<String, Object> postParams = new HashMap<String, Object>();
            postParams.put("qrcodekey",qrcodekey);
            postParams.put("authdata",authdata);
            postParams.put("phase", phase);
            String json = JsonUtils.getGson().toJson(postParams);
            LogUtil.d("authdata", "QChatQRLoginAuth  request  json = " + json);
            HttpUrlConnectionHandler.executePostJson(url, cookie, json, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    QRCodeAuthResultJson result = null;
                    try {
                        String resultString = Protocol.parseStream(response);
                        result = JsonUtils.getGson().fromJson(resultString, QRCodeAuthResultJson.class);
                        LogUtil.d("authdata", "QChatQRLoginAuth  Success  resultString = " + resultString);
                    } catch (Exception e) {
                        LogUtil.e(TAG,"error",e);
                    }
                    if(result != null && result.ret){
                        callback.onCompleted(result);
                        return;
                    }
                    callback.doFailure();
                }

                @Override
                public void onFailure(Exception e) {
                    LogUtil.d("authdata", "QChatQRLoginAuth fail:"+e.getMessage());
                    callback.doFailure();
                }
            });
        }
        catch (Exception e) {
            LogUtil.e("authdata","error",e);
        }
    }

    public static void searchUserHost(String keyword,final ProtocolCallback.UnitCallback<DomainResult> callback){
        String url = "" + keyword;
        HttpUrlConnectionHandler.executeGet(url, new HttpRequestCallback() {
            @Override
            public void onComplete(InputStream response)  {
                try {
                    String resultString = Protocol.parseStream(response);
                    DomainResult result = JsonUtils.getGson().fromJson(resultString,DomainResult.class);
                    if(callback != null){
                        callback.onCompleted(result);
                    }
                } catch (Exception e) {
                    if(callback != null){
                        callback.onCompleted(null);
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                if(callback != null){
                    callback.onCompleted(null);
                }
            }
        });
    }

    public static void sendForgetPwdSMS(String domainId,String piccode,String phonenumber,final ProtocolCallback.UnitCallback<BaseJsonResult> callback){
        String url = "";
        Map<String,String> params = new HashMap<>();
        params.put("domainId",domainId);
        params.put("piccode",piccode);
        params.put("phonenumber",phonenumber);
        HttpUrlConnectionHandler.executePostJson(url, JsonUtils.getGson().toJson(params), new HttpRequestCallback() {
            @Override
            public void onComplete(InputStream response)  {
                try {
                    String resultString = Protocol.parseStream(response);
                    BaseJsonResult result = JsonUtils.getGson().fromJson(resultString,BaseJsonResult.class);
                    if(callback != null){
                        callback.onCompleted(result);
                    }
                } catch (Exception e) {
                    if(callback != null){
                        callback.onCompleted(null);
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                if(callback != null){
                    callback.onCompleted(null);
                }
            }
        });
    }

    public static void checkSmsCode(String mobile,String messagecheckcode,final ProtocolCallback.UnitCallback<BaseJsonResult> callback){
        String url = "";
        Map<String,String> params = new HashMap<>();
        params.put("phonenumber",mobile);
        params.put("messagecheckcode",messagecheckcode);
        HttpUrlConnectionHandler.executePostJson(url, JsonUtils.getGson().toJson(params), new HttpRequestCallback() {
            @Override
            public void onComplete(InputStream response) {
                try {
                    String resultString = Protocol.parseStream(response);
                    BaseJsonResult result = JsonUtils.getGson().fromJson(resultString,BaseJsonResult.class);
                    if(callback != null){
                        callback.onCompleted(result);
                    }
                } catch (Exception e) {
                    if(callback != null){
                        callback.onCompleted(null);
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                if(callback != null){
                    callback.onCompleted(null);
                }
            }
        });
    }

    public static void resetPwd(String params,final ProtocolCallback.UnitCallback<BaseJsonResult> callback){
        String url = "";
        HttpUrlConnectionHandler.executePostJson(url, params, new HttpRequestCallback() {
            @Override
            public void onComplete(InputStream response)  {
                try {
                    String resultString = Protocol.parseStream(response);
                    BaseJsonResult result = JsonUtils.getGson().fromJson(resultString,BaseJsonResult.class);
                    if(callback != null){
                        callback.onCompleted(result);
                    }
                } catch (Exception e) {
                    if(callback != null){
                        callback.onCompleted(null);
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                if(callback != null){
                    callback.onCompleted(null);
                }
            }
        });
    }

    public static void getNewLoginToken(String u,String p,final ProtocolCallback.UnitCallback<String[]> callback){
        String url = QtalkNavicationService.getInstance().getHttpUrl() + "/nck/qtlogin.qunar";
        Map<String,String> params = new HashMap<>();
        params.put("u",u);
        params.put("h",QtalkNavicationService.getInstance().getXmppdomain());
        params.put("p",p);
        params.put("mk", PhoneInfoUtils.getUniqueID());
        HttpUrlConnectionHandler.executePostJson(url, JsonUtils.getGson().toJson(params), new HttpRequestCallback() {
            @Override
            public void onComplete(InputStream response) {
                try {
                    String resultString = Protocol.parseStream(response);
                    GeneralJson result = JsonUtils.getGson().fromJson(resultString,GeneralJson.class);
                    if(callback != null){
                        if(result.ret){
                            callback.onCompleted(new String[]{result.data.get("u"),result.data.get("t")});
                        }else {
                            callback.onCompleted(null);
                        }
                    }
                } catch (Exception e) {
                    if(callback != null){
                        callback.onCompleted(null);
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                if(callback != null){
                    callback.onCompleted(null);
                }
            }
        });
    }

}
