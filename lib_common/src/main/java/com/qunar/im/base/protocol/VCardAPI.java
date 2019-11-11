package com.qunar.im.base.protocol;

import android.text.TextUtils;

import com.qunar.im.base.jsonbean.GetMucVCardData;
import com.qunar.im.base.jsonbean.GetMucVCardResult;
import com.qunar.im.base.jsonbean.GetUserStatus;
import com.qunar.im.base.jsonbean.GetVCardData;
import com.qunar.im.base.jsonbean.GetVCardResult;
import com.qunar.im.base.jsonbean.SetMucVCardResult;
import com.qunar.im.base.jsonbean.SetVCardResult;
import com.qunar.im.base.jsonbean.UserStatusResult;
import com.qunar.im.base.structs.SetMucVCardData;
import com.qunar.im.base.structs.SetProfileData;
import com.qunar.im.base.structs.SetVCardData;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.ListUtil;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.protobuf.common.CurrentPreference;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.qunar.im.base.util.JsonUtils.getGson;

/**
 * Created by saber on 16-3-1.
 */
public class VCardAPI {
    private static final String TAG = "VCardAPI";

    public static void getVCardInfo(List<GetVCardData> datas, final ProtocolCallback.UnitCallback<GetVCardResult> callback) {
        try {
            if (TextUtils.isEmpty(CurrentPreference.getInstance().getVerifyKey())) {
                callback.doFailure();
                return;
            }
            StringBuilder url = new StringBuilder("domain/get_vcard_info.qunar?");
            if(ListUtil.isEmpty(datas)||ListUtil.isEmpty(datas.get(0).users)){
                callback.doFailure();
                return;
            }
            Protocol.addBasicParamsOnHead(url);

            String postUrl = Protocol.makeGetUri(QtalkNavicationService.getInstance().getHttpUrl(),
                    QtalkNavicationService.getInstance().getHttpPort(), url.toString(), true);
            String postBody = JsonUtils.getGson().toJson(datas);
            LogUtil.d(TAG, postBody);
            HttpUrlConnectionHandler.executePostJson(postUrl, postBody, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    GetVCardResult result = null;
                    try {

                        String resultString = Protocol.parseStream(response);
                        result = JsonUtils.getGson().fromJson(resultString, GetVCardResult.class);
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

    public static void setVCardInfo(List<SetVCardData> datas, final ProtocolCallback.UnitCallback<SetVCardResult> callback) {
        try {
            StringBuilder url = new StringBuilder("profile/set_profile.qunar?");

            if (TextUtils.isEmpty(CurrentPreference.getInstance().getVerifyKey())) {
                callback.doFailure();
                return;
            }
            Protocol.addBasicParamsOnHead(url);

            String postUrl = Protocol.makeGetUri(QtalkNavicationService.getInstance().getHttpUrl(),
                    QtalkNavicationService.getInstance().getHttpPort(), url.toString(), true);
            String postBody = JsonUtils.getGson().toJson(datas);
            HttpUrlConnectionHandler.executePostJson(postUrl, postBody, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    SetVCardResult result = null;
                    try {

                        String resultString = Protocol.parseStream(response);
                        result = JsonUtils.getGson().fromJson(resultString, SetVCardResult.class);
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

    public static void getMucVCard(List<GetMucVCardData> datas, final ProtocolCallback.UnitCallback<GetMucVCardResult> callback) {
        try {
            StringBuilder url = new StringBuilder("muc/get_muc_vcard.qunar?");
            if (TextUtils.isEmpty(CurrentPreference.getInstance().getVerifyKey())) {
                callback.doFailure();
                return;
            }
            Protocol.addBasicParamsOnHead(url);

            String postUrl = Protocol.makeGetUri(QtalkNavicationService.getInstance().getHttpUrl(),
                    QtalkNavicationService.getInstance().getHttpPort(), url.toString(), true);
            String postBody = JsonUtils.getGson().toJson(datas);
            HttpUrlConnectionHandler.executePostJson(postUrl, postBody, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    GetMucVCardResult result = null;
                    if(response!=null) {
                        try {
                            String resultString = Protocol.parseStream(response);
                            LogUtil.d("getmucvcard",resultString);
                            result = JsonUtils.getGson().fromJson(resultString, GetMucVCardResult.class);
                        } catch (Exception e) {
                            LogUtil.e(TAG,"error",e);
                        }
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

    //设置群名片
    public static void setMucVCard(List<SetMucVCardData> datas, final ProtocolCallback.UnitCallback<SetMucVCardResult> callback) {
        try {
            StringBuilder url = new StringBuilder("setmucvcard?");
            if (TextUtils.isEmpty(CurrentPreference.getInstance().getVerifyKey())) {
                callback.doFailure();
                return;
            }
            Protocol.addBasicParamsOnHead(url);

            String postUrl = Protocol.makeGetUri(QtalkNavicationService.getInstance().getHttpHost(),
                    QtalkNavicationService.getInstance().getHttpPort(), url.toString(), true);
            String postBody = JsonUtils.getGson().toJson(datas);


            HttpUrlConnectionHandler.executePostJson(postUrl, postBody, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    SetMucVCardResult result = null;
                    try {

                        String resultString = Protocol.parseStream(response);
                        result = JsonUtils.getGson().fromJson(resultString, SetMucVCardResult.class);

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

    /**
     * @author zhaokai
     * 获取用户个人信息（不包括头像)
     * @param mood 心情短语
     * */

    public static void setMyUserProfile(final String mood,final ProtocolCallback.UnitCallback<SetVCardResult> callback){
        StringBuilder sb = new StringBuilder();
        sb.append("profile/set_profile.qunar?");
        if (TextUtils.isEmpty(CurrentPreference.getInstance().getVerifyKey())) {callback.doFailure();return;}
        Protocol.addBasicParamsOnHead(sb);
        String url =Protocol.makeGetUri(QtalkNavicationService.getInstance().getHttpUrl(),
                QtalkNavicationService.getInstance().getHttpPort(), sb.toString(), true);
        List<SetProfileData> list = new ArrayList<>();
        SetProfileData setProfileData = new SetProfileData();
        setProfileData.domain = QtalkNavicationService.getInstance().getXmppdomain();
        setProfileData.user = CurrentPreference.getInstance().getUserid();
        setProfileData.mood = mood;
        list.add(setProfileData);
        String json = "{\"user\":\"" + CurrentPreference.getInstance().getUserid() + "\",\"" + "domain" + "\":\"" + QtalkNavicationService.getInstance().getXmppdomain() + "\",\"mood\":\"" + mood + "\"}";
        HttpUrlConnectionHandler.executePostJson(url, JsonUtils.getGson().toJson(list), new HttpRequestCallback() {

            @Override
            public void onComplete(InputStream response) {
                try {
                    if (callback == null) {
                        return;
                    }
                    String resultString = Protocol.parseStream(response);
                    SetVCardResult result = JsonUtils.getGson().fromJson(resultString, SetVCardResult.class);
                    if (result != null) {
                        callback.onCompleted(result);
                    }
                    callback.doFailure();
                } catch (Exception e) {
                    LogUtil.e(TAG, "IO Exception", e);
                }
            }

            @Override
            public void onFailure(Exception e) {
                LogUtil.d(TAG, "http exception", e);
                if (callback == null) {
                    return;
                }
                callback.doFailure();
            }
        });
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
}
