package com.qunar.im.core.services;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.protocol.HttpRequestCallback;
import com.qunar.im.base.protocol.HttpUrlConnectionHandler;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.common.QChatRSA;
import com.qunar.im.base.jsonbean.NavConfigResult;
import com.qunar.im.base.jsonbean.PubKeyBean;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.utils.GlobalConfigManager;
import com.qunar.im.cache.IMUserCacheManager;
import com.qunar.im.protobuf.common.LoginType;
import com.qunar.im.utils.PubKeyUtil;

import org.json.JSONObject;

import java.io.InputStream;


/**
 * 有反射调用updateNavicationConfig方法 不要修改类&包名
 * Created by may on 2017/7/3.
 */

public class QtalkNavicationService {
    private static volatile QtalkNavicationService instance;

    public static final String NAV_CONFIG_UPDATETIME = "NavConfigUpdateTime";//配置导航时间
    public static final String NAV_CONFIG_JSON = "NAV_CONFIG_JSON";//配置导航列表集合
    public static final String NAV_CONFIG_CURRENT_NAME = "NAV_CONFIG_CURRENT_NAME";//当前导航配置名称
    public static final String NAV_CONFIG_CURRENT_URL = "NAV_CONFIG_CURRENT_URL";//当前导航配置URL
    public static final String NAV_CHECKCONFIG_UPDATETIME = "NavCheckConfigUpdateTime";//checkconfig时间
    public static final String NAV_CONFIG_CHECK_CONFIG_CAPABILITY = "NAV_CONFIG_CHECK_CONFIG_CAPABILITY_NEW";//当前导航配置checkconfig内容

    public static final String NAV_CONFIG_PUBLIC_DEFAULT = "";
    private String navicationUrl = "";
    private String navicationUrlForQchat = "";

    public static final String VIDEO_CALL_URL = "";
    private NavConfigResult mNavConfigResult;

    private LoginType loginType;
    private NavConfigResult.Baseaddess mBaseAddress;
    private String hosts;
    //baseaddess
    private String simpleapiurl;
    private String pubkey;
    private String tokenSmsUrl;
    private String verifySmsUrl;
    private String javaUrl;
    private String xmppHost;
    private String xmppdomain;
    private int protobufPort;
    private String httpHost;
    private int httpPort;
    private int xmppport;
    private String checkconfig;
    private int checkconfigVersion;
    private String innerFiltHttpHost;
    private String new_searchurl;
    private String httpUrl;
    private String wikiurl;
    private String mobileurl;
    private String leaderurl;
    private String shareurl;
    private String domainhost;
    private String resetPwdUrl;
    private String appWeb;
    private String payurl;

    private String uploadFile;
    private String uploadCheckLink;
    private String persistentImage;

    //ability
    private String qCloudHost;
    private String qGrabOrder;
    private String qcZhongbao;
    private String searchurl;
    private String mconfig;
    private boolean showmsgstat;//是否显示阅读状态

    private String qcadminHost;

    //imConfig
    private boolean showOrganizational;
    private String uploadLog;
    private String email;
    private String foundConfigUrl;
    private boolean isToC;

    private String videoHost;

    //checkconfig配置url
    public static String HONGBAO_URL = "";
    public static String HONGBAO_BALANCE = "";
    public static String MY_HONGBAO = "";
    public static String AA_PAY_URL = "";
    public static String THANKS_URL = "";
    public static String SEND_ACTIVITY = "";
    public static String COMPANY = "qunar";

    //qcadmin
    public static String QCADMIN_HOST_DEFAULT = "";

    //ability
    private static String ABILITY_GET_PUSH_STATE = "";
    private static String ABILITY_Q_CLOUD_HOST = "";
    private static String ABILITY_SET_PUSH_STATE = "";
    private static String ABILITY_Q_GRAB_ORDER = "";//qchat抢单
    private static String ABILITY_SEARCHURL = "";
    //默认hash url
    private static String HOSTS = "";
    //baseaddess
    private static final String tokenSmsUrlDefault = "";
    private static final String takeSmsUrlDefault = "";
    private static final String javaurl = "";
    private static final String SimpleApiUrl = "";
    private static String PUB_NET_XMPP_Host = "qt.qunar.com";
    private static String PUB_NET_XMPP_Domain = "ejabhost1";
    private static int PUBLIC_XMPP_PORT = 5223;
    private static int PUBLIC_PROTOBUF_PORT = 5202;
    private static String PUB_FILE_SERVER = "";
    private static  String PUB_NEW_SEARCH_URL="";
    private static String HTTP_SERV_URL = "";
    private static int HTTPS_PORT = 443;
    private static String CHECK__CONFIG = "";
    private static String UPLOAD_FILE_LINK_ONLINE ="/file/v2/upload/";
    private static String UPLOAD_CHECK_LINK = "/file/v2/inspection/";
    private static String PERSISTENT_IMAGE = "/file/v2/stp";
    private static String HTTP_URL ="httpurl";




    private boolean debugEnvironment = CommonConfig.isDebug;

    private void configDefaultNav() {
        final long currentTime = System.currentTimeMillis();
        String navname = DataUtils.getInstance(CommonConfig.globalContext).getPreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_NAME, "");
        String oldNavString = DataUtils.getInstance(CommonConfig.globalContext).getPreferences(navname, "");
        if (!TextUtils.isEmpty(oldNavString)) {
            NavConfigResult result = JsonUtils.getGson().fromJson(oldNavString, NavConfigResult.class);
            configNav(result);
            IMUserCacheManager.getInstance().putConfig(GlobalConfigManager.getGlobalContext(), QtalkNavicationService.NAV_CONFIG_UPDATETIME, currentTime);
        } else {
            String navurl = DataUtils.getInstance(CommonConfig.globalContext).getPreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_URL, "");
            //没有导航先给个默认的
            final String navConfigUrl = TextUtils.isEmpty(navurl) ? this.getNavicationUrl() : navurl;
            DataUtils.getInstance(CommonConfig.globalContext).putPreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_URL, navConfigUrl);
            makeDefault();
        }
    }

    /**
     * 清理导航缓存
     */
    public void clearNavconfig() {
        String navname = DataUtils.getInstance(CommonConfig.globalContext).getPreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_NAME, "");
        if (!TextUtils.isEmpty(navname)) {
            DataUtils.getInstance(CommonConfig.globalContext).removePreferences(navname);
            DataUtils.getInstance(CommonConfig.globalContext).removePreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_NAME);
            DataUtils.getInstance(CommonConfig.globalContext).removePreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_URL);
            DataUtils.getInstance(CommonConfig.globalContext).removePreferences(QtalkNavicationService.NAV_CONFIG_UPDATETIME);
        }
    }

    private void makeDefault() {
        Logger.i("初始化默认导航");
        loginType = LoginType.SMSLogin;

        NavConfigResult nav = new NavConfigResult();
        nav.baseaddess.xmpp = xmppHost;
        nav.baseaddess.domain = xmppdomain;
        nav.baseaddess.protobufPort = protobufPort;
        mBaseAddress = nav.baseaddess;
        mNavConfigResult = nav;
        hosts = HOSTS;
        //baseaddess
        simpleapiurl = SimpleApiUrl;
        tokenSmsUrl = QtalkNavicationService.tokenSmsUrlDefault;
        verifySmsUrl = QtalkNavicationService.takeSmsUrlDefault;
        javaUrl = QtalkNavicationService.javaurl;
        xmppHost = PUB_NET_XMPP_Host;
        xmppdomain = PUB_NET_XMPP_Domain;
        protobufPort = PUBLIC_PROTOBUF_PORT;
        httpHost = HTTP_SERV_URL;
        httpPort = HTTPS_PORT;
        xmppport = PUBLIC_XMPP_PORT;
        checkconfig = CHECK__CONFIG;
        innerFiltHttpHost = PUB_FILE_SERVER;
        new_searchurl=PUB_NEW_SEARCH_URL;
        httpUrl = HTTP_URL;

        uploadFile = PUB_FILE_SERVER + UPLOAD_FILE_LINK_ONLINE;
        uploadCheckLink = PUB_FILE_SERVER + UPLOAD_CHECK_LINK;
        persistentImage = PUB_FILE_SERVER + PERSISTENT_IMAGE;

        //ability
        qCloudHost = ABILITY_Q_CLOUD_HOST;
        qGrabOrder = ABILITY_Q_GRAB_ORDER;
        //设置搜索地址
        searchurl = ABILITY_SEARCHURL;
        //设置代收地址
//        mconfig = ABILITY_MCONFIG;

        qcadminHost = QCADMIN_HOST_DEFAULT;

        videoHost = VIDEO_CALL_URL;

    }

    public void configNav(NavConfigResult result) {
        mNavConfigResult = result;

        if (result.Login != null) {
            if (!TextUtils.isEmpty(result.Login.loginType)) {
                if(result.Login.loginType.equalsIgnoreCase("password")){
                    loginType = LoginType.PasswordLogin;
                }else if(result.Login.loginType.equalsIgnoreCase("newpassword")){
                    loginType = LoginType.NewPasswordLogin;
                }else {
                    loginType = LoginType.SMSLogin;
                }
            } else {
                loginType = LoginType.SMSLogin;
            }
        } else {
            loginType = LoginType.SMSLogin;
        }
        mBaseAddress = result.baseaddess;
        hosts = result.hosts;
        //baseaddess
        simpleapiurl = result.baseaddess.simpleapiurl;
        pubkey = result.baseaddess.pubkey;
        tokenSmsUrl = result.baseaddess.sms_token;
        verifySmsUrl = result.baseaddess.sms_verify;
        javaUrl = result.baseaddess.javaurl;
        xmppHost = result.baseaddess.xmpp;
        xmppdomain = result.baseaddess.domain;
        protobufPort = result.baseaddess.protobufPort;
        httpHost = result.baseaddess.apiurl;
        httpPort = HTTPS_PORT;
        xmppport = result.baseaddess.xmppmport;
        checkconfig = result.baseaddess.checkconfig;
        innerFiltHttpHost = result.baseaddess.fileurl;
        new_searchurl = result.ability.new_searchurl;
        httpUrl = result.baseaddess.httpurl;
        wikiurl = result.baseaddess.wikiurl;
        mobileurl = result.baseaddess.mobileurl;
        leaderurl = result.baseaddess.leaderurl;
        shareurl = result.baseaddess.shareurl;
        domainhost = result.baseaddess.domainhost;
        resetPwdUrl = result.baseaddess.resetPwdUrl;

        checkconfigVersion = result.versions.checkconfig;

        uploadFile = result.baseaddess.fileurl + UPLOAD_FILE_LINK_ONLINE;
        uploadCheckLink = result.baseaddess.fileurl + UPLOAD_CHECK_LINK;
        persistentImage = result.baseaddess.fileurl + PERSISTENT_IMAGE;
        appWeb = result.baseaddess.appWeb;
        payurl = result.baseaddess.payurl;

        //ability
        qCloudHost = result.ability.qCloudHost;
        qGrabOrder = result.ability.qcGrabOrder;
        qcZhongbao = result.ability.qcZhongbao;
        //设置搜索地址
        searchurl = result.ability.searchurl;
        mconfig = result.ability.mconfig;
        showmsgstat = result.ability.showmsgstat;

        showOrganizational = result.imConfig.showOrganizational;
        uploadLog = result.imConfig.uploadLog;
        email = result.imConfig.mail;
        foundConfigUrl = result.imConfig.foundConfigUrl;
        isToC = result.imConfig.isToC;

        qcadminHost = result.qcadmin.host;

        videoHost = result.baseaddess.videourl;

        Logger.i("配置导航：" + result.toString());

        Logger.i("导航pub_key:" + result.baseaddess.pubkey);

        //qchat pubkey
        setPubKey(result.baseaddess.pubkey);
    }

    private void setPubKey(String keyFromNav){
        if(!CommonConfig.isQtalk){
            if (!TextUtils.isEmpty(keyFromNav)) {
                QChatRSA.pub_key = QChatRSA.readPubkey(CommonConfig.globalContext, keyFromNav);
                Logger.i("导航pub_key content:" + QChatRSA.pub_key);
            }
        }
    }

    public void updateNavicationConfig(final boolean withCheck) {

        long navConfigUpdateTime = IMUserCacheManager.getInstance().getLongConfig(QtalkNavicationService.NAV_CONFIG_UPDATETIME);

        final long currentTime = System.currentTimeMillis();
        String navurl = DataUtils.getInstance(CommonConfig.globalContext).getPreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_URL, "");

        Logger.i("初始化导航当前URL:" + navurl);
        if (withCheck /*|| currentTime - navConfigUpdateTime > 2 * 60 * 60 * 10001*/ || this.isDebugEnvironment()) {

            String navConfigUrl = TextUtils.isEmpty(navurl) ? this.getNavicationUrl() : navurl;
            if(TextUtils.isEmpty(navConfigUrl) || !navConfigUrl.startsWith("http")){
                return;
            }else if(!navConfigUrl.contains("nauth=")){
                if(navConfigUrl.indexOf("?") == -1){
                    navConfigUrl += "?nauth=true";
                }else {
                    navConfigUrl += "&nauth=true";
                }
            }

            Logger.i("初始化导航重新获取URL:" + navConfigUrl);
            getServerConfig(navConfigUrl, new ProtocolCallback.UnitCallback<NavConfigResult>() {
                @Override
                public void onCompleted(final NavConfigResult navConfigResult) {
                    if (navConfigResult != null) {
                        int version = (TextUtils.isEmpty(navConfigResult.version)) ? Integer.valueOf(navConfigResult.version) : 0;

                        String navname = DataUtils.getInstance(CommonConfig.globalContext).getPreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_NAME, "");
                        String oldNavString = DataUtils.getInstance(CommonConfig.globalContext).getPreferences(navname, "");
                        Logger.i("初始化导航旧导航配置:version = " + version + "config = " + oldNavString + "withCheck" + withCheck);
                        if (!TextUtils.isEmpty(oldNavString)) {
                            NavConfigResult oldNav = JsonUtils.getGson().fromJson(oldNavString, NavConfigResult.class);
                            int oldVersion = (TextUtils.isEmpty(oldNav.version)) ? Integer.valueOf(oldNav.version) : 0;

                            NavConfigResult nav;
                            if (withCheck || version > oldVersion || isDebugEnvironment()) {
                                nav = navConfigResult;
//                                configNav(navConfigResult);
                                DataUtils.getInstance(CommonConfig.globalContext).putPreferences(navname, JsonUtils.getGson().toJson(navConfigResult));
                            } else {
                                nav = oldNav;
//                                configNav(oldNav);
                            }


                            configNav(nav);

                        } else {
                            configNav(navConfigResult);
                            DataUtils.getInstance(CommonConfig.globalContext).putPreferences(navname, JsonUtils.getGson().toJson(navConfigResult));
                        }

                        IMUserCacheManager.getInstance().putConfig(GlobalConfigManager.getGlobalContext(), QtalkNavicationService.NAV_CONFIG_UPDATETIME, currentTime);
                    } else {
                        configDefaultNav();
                    }

                    checkPubKey();
                }

                @Override
                public void onFailure(String errMsg) {
                    configDefaultNav();
                    checkPubKey();
                }
            });
        }
    }

    /**
     * 同步获取导航
     *
     * @param url
     * @param callback
     */
    private static void getServerConfig(final String url, final ProtocolCallback.UnitCallback<NavConfigResult> callback) {
        StringBuilder stringBuilder = new StringBuilder(url);

        Protocol.addBasicParamsOnHead(stringBuilder);
        HttpUrlConnectionHandler.executeGetSync(stringBuilder.toString(), new HttpRequestCallback() {
            @Override
            public void onComplete(final InputStream response) {
                try {
                    String resultString = Protocol.parseStream(response);
                    if (resultString != null) {
                        JSONObject jsonObject = new JSONObject(resultString);
                        if (jsonObject.has("ret") && !Boolean.valueOf(jsonObject.get("ret").toString())) {
                            callback.onFailure("");
                            return;
                        }
                        final NavConfigResult result = JsonUtils.getGson().fromJson(resultString, NavConfigResult.class);
                        if (result == null) {
                            callback.onFailure("");
                            return;
                        }

                        if (TextUtils.isEmpty(result.hosts)) {
                            callback.onCompleted(result);
                        } else {
                            if (url.contains("debug=true")) {
                                if (result.hosts.contains("?")) {
                                    result.hosts += "&debug=true";
                                } else {
                                    result.hosts += "?debug=true";
                                }
                            }
                            Logger.i("开始请求二级导航:" + result);
//                            result.hosts+="?debug=true";
                            getHashBaseAddress(result, new ProtocolCallback.UnitCallback<NavConfigResult>() {
                                @Override
                                public void onCompleted(NavConfigResult baseResult) {
                                    if (baseResult == null) {
                                        callback.onFailure("");
                                        return;
                                    }
                                    result.baseaddess = baseResult.baseaddess;
                                    callback.onCompleted(result);
                                }

                                @Override
                                public void onFailure(String errMsg) {
                                    callback.onFailure("");
                                }
                            });
                        }
                    } else {
                        callback.onFailure("");
                    }
                } catch (Exception e) {
                    callback.onFailure("");
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure("");
            }
        });
    }

    private static void getHashBaseAddress(final NavConfigResult result, final ProtocolCallback.UnitCallback<NavConfigResult> callback) {
        if (result == null || TextUtils.isEmpty(result.hosts)) {
            callback.onFailure("");
            return;
        }

        StringBuilder stringBuilder = new StringBuilder(result.hosts);
        Protocol.addBasicParamsOnHead(stringBuilder);
        Logger.i("二级导航请求地址加参数:" + stringBuilder.toString());
        HttpUrlConnectionHandler.executeGetSync(stringBuilder.toString(), new HttpRequestCallback() {
            @Override
            public void onComplete(InputStream response) {
                try {
                    String resultString = Protocol.parseStream(response);
                    if (resultString != null) {
                        Logger.i("二级导航请求完成:" + resultString);
                        NavConfigResult baseResult = JsonUtils.getGson().fromJson(resultString, NavConfigResult.class);
                        callback.onCompleted(baseResult);
                    } else {
                        callback.onFailure("");
                    }
                } catch (Exception e) {
                    callback.onFailure("");
                }
            }

            @Override
            public void onFailure(Exception e) {
                Logger.i("getHashBaseAddress->>" + result.hosts + e.getLocalizedMessage());
                callback.onFailure("");
            }
        });
    }

    /**
     * 同步获取pubkey
     *
     * @param url
     * @param callback
     */
    private static void getPubKey(String url, String dommain, final ProtocolCallback.UnitCallback<PubKeyBean> callback) {
        StringBuilder stringBuilder = new StringBuilder(url);

        Protocol.addBasicParamsOnHead(stringBuilder, dommain);
        HttpUrlConnectionHandler.executeGetSync(stringBuilder.toString(), new HttpRequestCallback() {
            @Override
            public void onComplete(final InputStream response) {
                try {
                    String resultString = Protocol.parseStream(response);
                    if (resultString != null) {
                        JSONObject jsonObject = new JSONObject(resultString);
                        if (jsonObject.has("ret") && !Boolean.valueOf(jsonObject.get("ret").toString())) {
                            callback.onFailure("");
                            return;
                        }
                        Logger.i("pubkey返回的值:" + resultString);
                        PubKeyBean result = JsonUtils.getGson().fromJson(resultString, PubKeyBean.class);
                        if (result == null) {
                            callback.onFailure("");
                            return;
                        }
                        Logger.i("pubkey'返回的对象:" + result);
//                        result.hosts+="?debug=true";
                        callback.onCompleted(result);
                    } else {
                        callback.onFailure("");
                    }
                } catch (Exception e) {
                    callback.onFailure("");
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure("");
            }
        });
    }

    public String getCurrentNavUrl(){
        String navurl = DataUtils.getInstance(CommonConfig.globalContext).getPreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_URL, "");
        return navurl;
    }

    /**
     * 检车获取pubkey
     */
    private void checkPubKey() {
        String pubkey = PubKeyUtil.getPUBKEY(getXmppdomain());
        Logger.i("获取缓存key :" + pubkey);
        if (!TextUtils.isEmpty(pubkey)) {
            QChatRSA.pub_key = pubkey;
            return;
        } else {
            if(TextUtils.isEmpty(getJavaUrl())){
                return;
            }
            String url = getJavaUrl() + "/qtapi/nck/rsa/get_public_key.do";
            Logger.i("缓存key为null,重新获取:" + url);
            getPubKey(url,getXmppdomain(), new ProtocolCallback.UnitCallback<PubKeyBean>() {
                @Override
                public void onCompleted(PubKeyBean pubKeyBean) {
                    Logger.i("从网络上获取key:" + JsonUtils.getGson().toJson(pubKeyBean));
                    if (pubKeyBean.isRet()) {
                        String key = pubKeyBean.getData().getPub_key_shortkey();
                        PubKeyUtil.setPUBKEY(getXmppdomain(), key);
                        QChatRSA.pub_key = key;
                    }
                }

                @Override
                public void onFailure(String errMsg) {

                }
            });
        }
    }

    private QtalkNavicationService() {
        configDefaultNav();
    }

    public static QtalkNavicationService getInstance() {
        synchronized (QtalkNavicationService.class) {
            if (instance == null){
                instance = new QtalkNavicationService();
            }
            return instance;
        }
    }

    /**
     * 获取导航地址
     *
     * @return
     */
    public String getSearchurl() {
//        return TextUtils.isEmpty(searchurl) ? ABILITY_SEARCHURL : searchurl;
        return searchurl;
    }

    public String getTokenSmsUrl() {
        return tokenSmsUrl;
    }

    public String getVerifySmsUrl() {
        return verifySmsUrl;
    }

    public String getJavaUrl() {
        if (TextUtils.isEmpty(javaUrl)) {
            return javaurl;
        }
        return javaUrl;
    }

    public void setJavaUrl(String url) {
        javaUrl = url;
    }

    public String getSimpleapiurl() {
        if (TextUtils.isEmpty(simpleapiurl))
            return SimpleApiUrl;
        return simpleapiurl;
    }

    public boolean isDebugEnvironment() {
        return debugEnvironment;
    }

    public String getNavicationUrl() {
        return CommonConfig.isQtalk ? navicationUrl : navicationUrlForQchat;
    }

    public void setNavicationUrl(String navicationUrl) {
        this.navicationUrl = navicationUrl;
    }

    public String getNavicationUrlForQchat() {
        return navicationUrlForQchat;
    }

    public void setNavicationUrlForQchat(String navicationUrlForQchat) {
        this.navicationUrlForQchat = navicationUrlForQchat;
    }

    public NavConfigResult getNavConfigResult() {
        return mNavConfigResult;
    }

    public NavConfigResult.Baseaddess getBaseAddess() {
        return mBaseAddress;
    }

    public String getHttpHost() {
        return httpHost;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public LoginType getLoginType() {
        return loginType;
    }


    public String getInnerFiltHttpHost() {
        return innerFiltHttpHost;
    }

    public String getNewSerarchUrl(){

        return TextUtils.isEmpty(new_searchurl)?searchurl:new_searchurl;
    }


    public String getPubkey() {
        return pubkey;
    }

    public String getXmppHost() {
        return xmppHost;
    }

    public int getProtobufPort() {
        return protobufPort;
    }

    public int getXmppport() {
        return xmppport;
    }


    public String getXmppdomain() {
        return xmppdomain;
    }

    public String getUploadFile() {
        return uploadFile;
    }

    public String getUploadCheckLink() {
        return uploadCheckLink;
    }

    public String getPersistentImage() {
        return persistentImage;
    }

    public String getCheckconfig() {
        if (TextUtils.isEmpty(checkconfig)) {
            return CHECK__CONFIG;
        }
        return checkconfig;
    }

    public String getHosts() {
        return hosts;
    }

    public String getqCloudHost() {
        return qCloudHost;
    }

    public String getqGrabOrder() {
        return TextUtils.isEmpty(qGrabOrder) ? ABILITY_Q_GRAB_ORDER : qGrabOrder;
    }

    public String getQcZhongbao() {
        return qcZhongbao;
    }

    //获取代收地址
    public String getMconfig() {
        return mconfig;
    }

    public boolean isShowOrganizational() {
        return showOrganizational;
    }

    public boolean isShowmsgstat() {
        return showmsgstat;
    }

    public String getQcadminHost() {
        return TextUtils.isEmpty(qcadminHost) ? QCADMIN_HOST_DEFAULT : qcadminHost;
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public void setHttpUrl(String httpUrl) {
        this.httpUrl = httpUrl;
    }

    public String getDomainSearchUrl() {
        return getHttpUrl() + "/domain/get_domain_list.qunar?t=" + GlobalConfigManager.getAppName().toLowerCase();
    }

    public String getWikiurl() {
        return wikiurl;
    }

    public int getCheckconfigVersion() {
        return checkconfigVersion;
    }

    public String getUploadLog() {
        return uploadLog;
    }

    public String getEmail() {
        return email;
    }

    public String getLeaderurl() {
        return leaderurl;
    }

    public void setLeaderurl(String leaderurl) {
        this.leaderurl = leaderurl;
    }

    public void setMobileurl(String mobileurl) {
        this.mobileurl = mobileurl;
    }

    public String getMobileurl() {
        return mobileurl;
    }

    public String getFoundConfigUrl() {
        return foundConfigUrl;
    }

    public void setFoundConfigUrl(String foundConfigUrl) {
        this.foundConfigUrl = foundConfigUrl;
    }

    public String getVideoHost() {
        return TextUtils.isEmpty(videoHost) ? VIDEO_CALL_URL : videoHost;
    }

    public String getShareurl() {
        return shareurl;
    }

    public String getDomainhost() {
        if(!TextUtils.isEmpty(domainhost))
            return domainhost;
        else return ".qunar.com";
    }

    public String getResetPwdUrl() {
        return resetPwdUrl;
    }

    public boolean isToC() {
        return isToC;
    }

    public String getAppWeb() {
        return appWeb;
    }

    public String getPayurl() {
        return payurl;
    }
}
