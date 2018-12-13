package com.qunar.im.ui.util;

import android.content.Context;
import android.text.TextUtils;

import com.qunar.im.base.jsonbean.NavConfigResult;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.core.services.QtalkNavicationService;

/**
 * Created by lihaibin.li on 2017/8/31.
 */

public class NavConfigUtils {
    public static void initNavConfig(Context context) {
        String navname = DataUtils.getInstance(context).getPreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_NAME, "");
        String navString = DataUtils.getInstance(context).getPreferences(navname, "");
        if (!TextUtils.isEmpty(navString)) {
            NavConfigResult result = JsonUtils.getGson().fromJson(navString, NavConfigResult.class);
//            initNavConfig(context, result);
            QtalkNavicationService.getInstance().configNav(result);
        }
    }

    public static void initNavConfig(Context context, NavConfigResult result, String navName) {
        initNavConfig(context, result);
        DataUtils.getInstance(context).putPreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_NAME, navName);
        DataUtils.getInstance(context).putPreferences(navName, JsonUtils.getGson().toJson(result));
    }

    public static void initNavConfig(Context context, NavConfigResult result) {
        QtalkNavicationService.getInstance().configNav(result);
        if (result != null && result.baseaddess != null) {
            Constants.Config.PUB_FILE_SERVER = result.baseaddess.fileurl;
            Constants.Config.UPLOAD_FILE_LINK_ONLINE = Constants.Config.PUB_FILE_SERVER + "/file/v2/upload/";
            Constants.Config.UPLOAD_CHECK_LINK = Constants.Config.PUB_FILE_SERVER + "/file/v2/inspection/";
            Constants.Config.PERSISTENT_IMAGE = Constants.Config.PUB_FILE_SERVER + "/file/v2/stp";
            Constants.Config.HTTP_SERV_URL = result.baseaddess.apiurl;
            Constants.Config.PUB_NET_XMPP_Host = result.baseaddess.xmpp;
            Constants.Config.PUB_NET_XMPP_Domain = result.baseaddess.domain;
            Constants.Config.PUBLIC_XMPP_PORT = result.baseaddess.xmppmport;
            Constants.Config.PUB_SMS_TOKEN = result.baseaddess.sms_token;
            Constants.Config.PUB_SMS_VERIFY = result.baseaddess.sms_verify;
            Constants.Config.CHECKCONFI_URL = result.baseaddess.checkconfig;
            Constants.Config.PUB_LOGINTYPE = result.Login.loginType;
            if (!TextUtils.isEmpty(result.baseaddess.checkurl)) {
                Constants.Config.CHECKCONFI_URL = result.baseaddess.checkurl;
            }
        }
    }
}
