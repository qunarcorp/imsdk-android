package com.qunar.rn_service.rnplugins;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.orhanobut.logger.Logger;
import com.qunar.im.base.jsonbean.SetWorkWorldRemindResponse;
import com.qunar.im.base.module.CityLocal;
import com.qunar.im.base.jsonbean.LogInfo;
import com.qunar.im.base.module.MedalsInfo;
import com.qunar.im.base.module.UserHaveMedalStatus;
import com.qunar.im.base.protocol.HttpRequestCallback;
import com.qunar.im.core.utils.GlobalConfigManager;
import com.qunar.im.log.LogConstans;
import com.qunar.im.log.LogService;
import com.qunar.im.log.QLog;
import com.qunar.im.other.CacheDataType;
import com.qunar.im.permission.PermissionCallback;
import com.qunar.im.permission.PermissionDispatcher;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.HttpUtil;
import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.base.common.CommonUploader;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.jsonbean.LeadInfo;
import com.qunar.im.base.jsonbean.LogInfo;
import com.qunar.im.base.jsonbean.NewRemoteConfig;
import com.qunar.im.base.jsonbean.SeatStatusResult;
import com.qunar.im.base.jsonbean.SetMucVCardResult;
import com.qunar.im.base.jsonbean.SetVCardResult;
import com.qunar.im.base.jsonbean.SetWorkWorldRemindResponse;
import com.qunar.im.base.jsonbean.UploadImageResult;
import com.qunar.im.base.module.AreaLocal;
import com.qunar.im.base.module.AvailableRoomRequest;
import com.qunar.im.base.module.AvailableRoomResponse;
import com.qunar.im.base.module.CalendarTrip;
import com.qunar.im.base.module.CityLocal;
import com.qunar.im.base.module.GroupMember;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.module.MedalsInfo;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.module.TripMemberCheckResponse;
import com.qunar.im.base.module.UserConfigData;
import com.qunar.im.base.protocol.HttpRequestCallback;
import com.qunar.im.base.protocol.NativeApi;
import com.qunar.im.base.protocol.PayApi;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.protocol.ThirdProviderAPI;
import com.qunar.im.base.protocol.VCardAPI;
import com.qunar.im.base.structs.PushSettinsStatus;
import com.qunar.im.base.structs.SetMucVCardData;
import com.qunar.im.base.structs.SetVCardData;
import com.qunar.im.base.transit.IUploadRequestComplete;
import com.qunar.im.base.transit.UploadImageRequest;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.base.util.FileUtils;
import com.qunar.im.base.util.HanziToPinyin;
import com.qunar.im.base.util.IMUserDefaults;
import com.qunar.im.base.util.InternDatas;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.ListUtil;
import com.qunar.im.base.util.graphics.MyDiskCache;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.manager.IMDatabaseManager;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.core.utils.GlobalConfigManager;
import com.qunar.im.google.auth.OtpProvider;
import com.qunar.im.log.LogConstans;
import com.qunar.im.log.LogService;
import com.qunar.im.log.QLog;
import com.qunar.im.other.CacheDataType;
import com.qunar.im.permission.PermissionCallback;
import com.qunar.im.permission.PermissionDispatcher;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.utils.CalendarSynchronousUtil;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.HttpUtil;
import com.qunar.im.utils.MD5;
import com.qunar.im.utils.QRUtil;
import com.qunar.im.utils.QtalkStringUtils;
import com.qunar.rn_service.activity.QtalkServiceRNActivity;
import com.qunar.rn_service.rnmanage.QtalkServiceExternalRNViewInstanceManager;
import com.qunar.rn_service.util.DateUtil;
import com.qunar.rn_service.util.QTalkServicePatchDownloadHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import de.greenrobot.event.EventBus;


/**
 * Created by hubin on 2018/1/18.
 */

public class QimRNBModule extends ReactContextBaseJavaModule implements IMNotificaitonCenter.NotificationCenterDelegate, PermissionCallback {

    private static final String MyMedal = "MyMedal";
    private static final String MyRedBag = "MyRedBag";
    private static final String BalanceInquiry = "BalanceInquiry";
    private static final String AccountInfo = "AccountInfo";
    private static final String MyFile = "MyFile";
    private static final String DeveloperChat = "DeveloperChat";
    private static final String DressUpVc = "DressUpVc";
    private static final String McConfig = "McConfig";
    private static final String About = "About";
    private static final String searchChatHistory = "searchChatHistory";
    private static final String NotReadMsg = "NotReadMsg";
    private static final String publicNumber = "publicNumber";
    private static final String GroupChat = "GroupChat";
    private static final String SearchContact = "SearchContact";
    private static final String Organizational = "Organizational";
    private static final String SystemSetting = "SystemSetting";
    private static final String AccountSwitch = "AccountSwitch";
    private static final String DomainSearch = "DomainSearch";
    private static final String OpenToCManager = "OpenToCManager";
    private static final String NavAddress = "NavAddress";
    private static final String OpenNavigationConfig = "NavigationConfig";


    private static final String InternalApplication = "InternalApplication";
    private static final String ExternalApplication = "ExternalApplication";
    private static final String WebApplication = "WebApplication";

    private static final String StateNotSet = "0";
    private static final String StateWorkOff = "1";
    private static final String StateWorkOn = "4";


    public static final int REQUEST_GRANT_CAMERA = PermissionDispatcher.getRequestCode();
    public static final int REQUEST_GRANT_LOCAL = PermissionDispatcher.getRequestCode();
    public static final int REQUEST_GRANT_CALL = PermissionDispatcher.getRequestCode();

    public Activity mActivity;//activity 为华为push要用
    //转发 分享 创建的群
    public static Map<String, ReadableMap> createGroups = new HashMap<>();

    @Override
    public void responsePermission(int requestCode, boolean granted) {

    }
    public enum AppEnum {
        InternalApplication("1"), ExternalApplication("2"), WebApplication("3");
        private String appType;

        AppEnum(String apptype) {
            this.appType = apptype;
        }

        public static AppEnum fromTypeName(String typeName) {
            for (AppEnum type : AppEnum.values()) {
                if (type.getAppType().equals(typeName)) {
                    return type;
                }
            }
            return null;
        }

        public String getAppType() {
            return appType;
        }

        public void setAppType(String appType) {
            this.appType = appType;
        }
    }


    public static String defaultUserImage = QtalkNavicationService.getInstance().getInnerFiltHttpHost() + "/file/v2/download/perm/3ca05f2d92f6c0034ac9aee14d341fc7.png";
    public static String defaultMucImage = QtalkNavicationService.getInstance().getInnerFiltHttpHost() + "/file/v2/download/perm/2227ff2e304cb44a1980e9c1a3d78164.png";

    private static List<String> inviteUserList = new ArrayList<>();
    private static String inviteName = new String();


    public QimRNBModule(ReactApplicationContext reactContext) {
        super(reactContext);
        addEvent();

    }

    public QimRNBModule(ReactApplicationContext reactContext, Activity mActivity) {
        super(reactContext);
        this.mActivity = mActivity;
        addEvent();
    }

    private void addEvent() {
        ConnectionUtil.getInstance().addEvent(this, QtalkEvent.Del_Muc_Register);
        ConnectionUtil.getInstance().addEvent(this, QtalkEvent.Update_Muc_Vcard);
        ConnectionUtil.getInstance().addEvent(this, QtalkEvent.Destory_Muc);
        ConnectionUtil.getInstance().addEvent(this, QtalkEvent.IQ_CREATE_MUC);
        ConnectionUtil.getInstance().addEvent(this, QtalkEvent.Muc_Invite_User_V2);
        ConnectionUtil.getInstance().addEvent(this, QtalkEvent.GravanterSelected);
        ConnectionUtil.getInstance().addEvent(this, QtalkEvent.FEED_BACK_RESULT);
        ConnectionUtil.getInstance().addEvent(this, QtalkEvent.SELECT_DATE);
        ConnectionUtil.getInstance().addEvent(this, QtalkEvent.WORK_WORLD_PERMISSIONS);
        ConnectionUtil.getInstance().addEvent(this, QtalkEvent.Group_Member_Update);
        ConnectionUtil.getInstance().addEvent(this, QtalkEvent.Remove_Session);
        ConnectionUtil.getInstance().addEvent(this,QtalkEvent.UPDATE_MEDAL_SELF);
        ConnectionUtil.getInstance().addEvent(this, QtalkEvent.PAY_SUCCESS);
    }

    @Override
    public String getName() {
        return "QimRNBModule";
    }

    private Object getApplicationMetaData(String key) {
        if (CommonConfig.globalContext != null) {
            ApplicationInfo applicationInfo = null;
            try {
                applicationInfo = CommonConfig.globalContext.getPackageManager().getApplicationInfo(CommonConfig.globalContext.getPackageName(), PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (applicationInfo == null || applicationInfo.metaData == null) {
                return null;
            } else {
                return applicationInfo.metaData.get(key);
            }
        } else {
            return null;
        }
    }

    @ReactMethod
    public void appConfig(Callback callback) {
        try {


            WritableNativeMap map = new WritableNativeMap();
            map.putString("userId", CurrentPreference.getInstance().getUserid());
            map.putString("clientIp", "192.168.0.1");
            map.putString("domain", QtalkNavicationService.getInstance().getXmppdomain());
//            map.putString("token", CurrentPreference.getInstance().getToken());
//            map.putString("q_auth", CurrentPreference.getInstance().getVerifyKey() == null ? "404" : CurrentPreference.getInstance().getVerifyKey());
            map.putString("ckey", getCKey());
            map.putString("httpHost", QtalkNavicationService.getInstance().getJavaUrl());
            map.putString("fileUrl", QtalkNavicationService.getInstance().getInnerFiltHttpHost());
            map.putString("qcAdminHost", QtalkNavicationService.getInstance().getQcadminHost());
            if (QtalkNavicationService.getInstance().isShowOrganizational()) {
                map.putInt("showOrganizational", 1);
            } else {
                map.putInt("showOrganizational", 0);
            }
            map.putBoolean("showServiceState", CurrentPreference.getInstance().isMerchants());
            map.putBoolean("isQtalk", CommonConfig.isQtalk);
            map.putBoolean("isShowWorkWorld", GlobalConfigManager.isQtalkPlat() && IMDatabaseManager.getInstance().SelectWorkWorldPremissions());

            Object metaData = getApplicationMetaData("EASY_TRIP");
            boolean isEasyTrip = metaData == null ? true : (boolean) metaData;
            map.putBoolean("isEasyTrip", isEasyTrip);
            map.putBoolean("isShowRedPackage", !GlobalConfigManager.isStartalkPlat());
            map.putBoolean("isShowGroupQRCode",true);
            map.putBoolean("isShowLocalQuickSearch",true);

            map.putBoolean("notNeedShowLeaderInfo",TextUtils.isEmpty(QtalkNavicationService.getInstance().getLeaderurl()));
            map.putBoolean("notNeedShowMobileInfo",TextUtils.isEmpty(QtalkNavicationService.getInstance().getMobileurl()));
            map.putBoolean("notNeedShowEmailInfo",TextUtils.isEmpty(QtalkNavicationService.getInstance().getEmail()));

            map.putBoolean("isToCManager",DataUtils.getInstance(CommonConfig.globalContext).getPreferences(Constants.Preferences.isAdminFlag + "_" + QtalkNavicationService.getInstance().getXmppdomain(),false));

            if(GlobalConfigManager.isQtalkPlat()){
                map.putInt("nativeAppType", 2);
            }else if(GlobalConfigManager.isQchatPlat()){
                map.putInt("nativeAppType", 1);
            }else if(GlobalConfigManager.isStartalkPlat()){
                map.putInt("nativeAppType", 0);
            }
//            map.putDouble("timestamp", System.currentTimeMillis());
            callback.invoke(map);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @ReactMethod
    public void getTOTP(final Callback callback) {
//
        if (TextUtils.isEmpty(CurrentPreference.getInstance().getVerifyKey())) {
            WritableNativeMap map = new WritableNativeMap();
            map.putString("totp", "000000");
            map.putInt("time", 0);
            callback.invoke(map);
            return;
        }
        String seret = String.format("u=%s&k=%s", CurrentPreference.getInstance().getPreferenceUserId(), CurrentPreference.getInstance().getVerifyKey());
        long timeStampSec = System.currentTimeMillis() - CurrentPreference.getInstance().getServerTimeDiff();
        long timestamp = Long.parseLong(String.format("%010d", timeStampSec));
        OtpProvider otp = new OtpProvider();
        String totp = "";
        try {
            totp = otp.computePin(seret, timestamp, null);
        } catch (Exception e) {
            totp = "000000";
            e.printStackTrace();
        }
        WritableNativeMap success = new WritableNativeMap();
        success.putString("totp", totp);
        success.putDouble("time", timestamp);
        callback.invoke(success);


    }

    //    RCT_EXPORT_METHOD(updateRemoteKey:(RCTResponseSenderBlock)success) {
//        success(@[@{@"ok":@(YES)}]);
//    }
    @ReactMethod
    public void updateRemoteKey(Callback callback) {
        WritableNativeMap map = new WritableNativeMap();

        String newKey = IMLogicManager.getInstance().getRemoteLoginKey(true);
        if (!TextUtils.isEmpty(newKey)) {
            map.putBoolean("ok", true);
        } else {
            map.putBoolean("ok", false);
        }
        callback.invoke(map);
    }


    public static String getCKey() {
        return Protocol.getCKEY();
    }

    @ReactMethod
    public void exitApp(String rnName) {

    }

    /**
     * 更新导航
     *
     * @param navTitle
     */
    @ReactMethod
    public void updateNavTitle(String navTitle) {


    }

    /**
     * 打开RN页面
     */
    @ReactMethod
    public void openRNPage(ReadableMap params, Callback success) {
        //mem.putBoolean("showNav",true);

        //mem.putString("navTitle","测试RN");
        try {
            String appType = params.hasKey("AppType") ? params.getString("AppType") : "";

            //如果不传apptype 默认当成内部应用

            if (TextUtils.isEmpty(appType)) {
                appType = "1";
            }
            AppEnum appEnum = AppEnum.fromTypeName(appType);
            switch (appEnum) {


                //外部应用需要先进行判断本地是否存在,
                //存在则打开,否则去下载!
                case ExternalApplication:
                    String bundleName = params.hasKey("Bundle") ? params.getString("Bundle") : "";
                    String moduleName = params.hasKey("Module") ? params.getString("Module") : "";
                    boolean showNav = params.hasKey("showNativeNav") ? params.getBoolean("showNativeNav") : false;
                    String navTitle = params.hasKey("navTitle") ? params.getString("navTitle") : "未知页面";
                    HashMap<String, Object> properties = params.hasKey("Properties") && !TextUtils.isEmpty(params.getString("Properties")) ? JsonUtils.getGson().fromJson(params.getString("Properties"), new HashMap<String, Object>().getClass()) : new HashMap<String, Object>();
                    String bundleVersion = params.hasKey("Version") ? params.getString("Version") : "";


                    if (TextUtils.isEmpty(bundleName)) {
                        return;
                    }
                    boolean is_ok = true;
                    String Entrance = params.getString("Entrance");
//                    String url = params.getString("BundleUrls");
//                    String bundleUrls = "";
                    String bundleUrls = params.getString("BundleUrls");
//                    try {
//                        JSONObject jb = new JSONObject(url);
//                        bundleUrls = jb.getString("android-link-url");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                    bundleName = MD5.hex(bundleUrls) + ".android.jsbundle";
//                    if (!bundleName.contains(".android.jsbundle")) {
//                        bundleName = bundleName + ".android.jsbundle";
//                    }
                    String localBundleFile = QtalkServiceExternalRNViewInstanceManager.getLocalBundleFilePath(CommonConfig.globalContext.getApplicationContext(), bundleName);


                    String oldBundleVersion = IMUserDefaults.getStandardUserDefaults().getStringValue(CommonConfig.globalContext,
                            bundleName);
                    if (!oldBundleVersion.equals(bundleVersion)) {
                        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.SHOW_PRO_DIALOG, "请稍后");
                        is_ok = QTalkServicePatchDownloadHelper.downloadPatchAndSave(bundleUrls, QtalkServiceExternalRNViewInstanceManager.getLocalBundlePath(CommonConfig.globalContext), bundleName);
                        if (is_ok) {
                            IMUserDefaults.getStandardUserDefaults().newEditor(CommonConfig.globalContext)
                                    .putObject(bundleName, bundleVersion)
                                    .synchronize();
                        }
                    }
                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.DIMISS_PRO_DIALOG);
                    if (is_ok) {
//                    NativeApi.openSingleChat();
//                    Intent intent = new Intent(CommonConfig.globalContext, QtalkServiceExternalRNActivity.class);
                        Map<String, Object> map = new HashMap<>();

                        map.put("module", moduleName);
                        map.put("Version", bundleVersion);
                        map.put("Bundle", bundleName);
                        map.put("Entrance", Entrance);
                        map.put("navTitle", navTitle);
                        map.put("showNativeNav", showNav);
                        for (Map.Entry<String, Object> entry : properties.entrySet()) {
//                        intent.putExtra(entry.getKey(), entry.getValue() + "");
                            map.put(entry.getKey(), entry.getValue());
                        }

                        NativeApi.openExternalRN(map);

//                    intent.putExtra("module", moduleName);
//                    intent.putExtra("Version", bundleVersion);
//                    intent.putExtra("Bundle", bundleName);
//                    intent.putExtra("Entrance", Entrance);
//                    intent.putExtra("navTitle",navTitle);
//                    intent.putExtra("showNav",showNav);
//                    for (Map.Entry<String, Object> entry : properties.entrySet()) {
//                        intent.putExtra(entry.getKey(), entry.getValue() + "");
//                    }
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////        intent.putExtra("Screen",rnModule);
//                    CommonConfig.globalContext.startActivity(intent);
                    }

                    break;

                case WebApplication:

                    String weburl = params.hasKey("memberAction") ? params.getString("memberAction") : "";
                    boolean shownav = params.hasKey("showNativeNav") ? params.getBoolean("showNativeNav") : true;
                    NativeApi.openWebPage(weburl, shownav);

                    //打开浏览器
//                NativeApi.openWebPage();
                    break;

                //内部应用和web应用打开方式
                case InternalApplication:
                default:
                    String nmoduleName = params.hasKey("Module") ? params.getString("Module") : "";
                    HashMap<String, Object> nproperties = params.hasKey("Properties") ? params.getMap("Properties").toHashMap() : new HashMap<String, Object>();
                    String nbundleVersion = params.hasKey("Version") ? params.getString("Version") : "";


                    Intent intent = new Intent(CommonConfig.globalContext, QtalkServiceRNActivity.class);
                    intent.putExtra("module", nmoduleName);
                    intent.putExtra("Version", nbundleVersion);
                    for (Map.Entry<String, Object> entry : nproperties.entrySet()) {
                        intent.putExtra(entry.getKey(), entry.getValue() + "");
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("Screen",rnModule);
                    CommonConfig.globalContext.startActivity(intent);
                    break;
            }

        } catch (Exception e) {

        }

//        new String();
    }


    private void openGroupChat(ReadableMap params, String groupId) {
        if (params.hasKey(Constants.BundleKey.IS_TRANS) && params.getBoolean(Constants.BundleKey.IS_TRANS)) {//转发
            Serializable transMsg = JsonUtils.getGson().fromJson(params.getString(Constants.BundleKey.TRANS_MSG), IMMessage.class);
            EventBus.getDefault().post(new EventBusEvent.SendTransMsg(transMsg, groupId));
        } else if (params.hasKey(Constants.BundleKey.IS_FROM_SHARE) && params.getBoolean(Constants.BundleKey.IS_FROM_SHARE)) {//分享
            String shareMsg = params.getString(Constants.BundleKey.SHARE_EXTRA_KEY);
            NativeApi.openGroupChatForShare(groupId, groupId, shareMsg);
        } else {
            NativeApi.openGroupChat(groupId, groupId);
        }
    }

    /**
     * 打开Native页面
     *
     * @param params
     */
    @ReactMethod
    public void openNativePage(ReadableMap params) {
        String nativeName = params.getString("NativeName");

        switch (nativeName) {
            case MyMedal:
                String userid = params.getString("userId");
                NativeApi.openUserMedal(QtalkStringUtils.addIdDomain(userid));
                break;
            case MyRedBag:
                NativeApi.openUserHongBao();
                break;
            case BalanceInquiry:
                NativeApi.openUserHongBaoBalance();
                break;

            case AccountInfo:
                NativeApi.openAccountInfo();
                break;
            case MyFile:
                NativeApi.openMyFile();
                break;

            case DeveloperChat:
                NativeApi.openDeveloperChat();
                break;

            case DressUpVc:
                NativeApi.openDressUpVc();
                break;

            case McConfig:
                NativeApi.openMcConfig();
                break;
            case About:
                NativeApi.openAbout();
                break;
            case searchChatHistory:
                NativeApi.openWebPage(Constants.SEARCH_HISTORY_PREFIX, true);
                break;
            case NotReadMsg:
                NativeApi.openUnReadListActivity();
                break;
            case publicNumber:
                NativeApi.openPublicNumber();
                break;
            case GroupChat:
                String groupId = params.getString("GroupId");
                openGroupChat(params, groupId);
                break;
            case SearchContact:
                NativeApi.openSearchActivty();
                break;
            case Organizational:
                NativeApi.openOrganizational();
                break;
            case SystemSetting:
                NativeApi.openSystemSetting();
                break;
            case AccountSwitch:
                NativeApi.openAccountSwitch();
                break;
            case DomainSearch:
                NativeApi.openDomainSearch();
                break;
            case OpenToCManager:
                String appweb = QtalkNavicationService.getInstance().getAppWeb();
                String domain = QtalkNavicationService.getInstance().getXmppdomain();
                NativeApi.openWebPage(appweb + "/manage#/audit_user?domain=" + domain, true);
                break;
            case NavAddress:
                String weburl = QtalkNavicationService.getInstance().getAppWeb();
                NativeApi.openWebPage(weburl + "/manage#/nav_code", true);
                break;
            case OpenNavigationConfig:
                NativeApi.openNavConfig();
                break;
            default:
                break;

        }


    }

//    /**
//     * 打开公众号
//     */
//    @ReactMethod
//    public void getPublicNumberList(){
//
//    }


    /**
     * 清空消息列表
     */
    @ReactMethod
    public void clearSessionList() {
        ConnectionUtil.getInstance().DeleteSessionList();
        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Show_List, "SUCCESS");
    }

    /**
     * 退出登录
     */
    @ReactMethod
    public void logout() {
        NativeApi.logout();

        saveRNActLog("log out", "退出登录", "设置页");
    }


    /**
     * 展示手机号
     *
     * @param params
     */
    @ReactMethod
    public void showUserPhoneNumber(ReadableMap params) {
        String userId = params.getString("UserId");
        NativeApi.openPhoneNumber(userId);


    }

    @ReactMethod
    public void setServiceState(final ReadableMap map, final Callback callback) {
        if (map == null) {
            return;
        }
        final String st = map.getString("state");
        final String sid = map.getString("sid");

        ThirdProviderAPI.setServiceStatus(CurrentPreference.getInstance().getUserid(), st, sid, new ProtocolCallback.UnitCallback<Boolean>() {
            @Override
            public void onCompleted(Boolean aBoolean) {
                WritableNativeMap writableNativeMap = new WritableNativeMap();
                writableNativeMap.putBoolean("result", aBoolean);
                callback.invoke(writableNativeMap);
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

    @ReactMethod
    public void getServiceState(final Callback callback) {
        ThirdProviderAPI.getServiceStatus(CurrentPreference.getInstance().getUserid(), new ProtocolCallback.UnitCallback<List<SeatStatusResult.SeatStatus>>() {
            @Override
            public void onCompleted(List<SeatStatusResult.SeatStatus> seatStatuses) {
                if (seatStatuses != null && seatStatuses.size() > 0) {
                    WritableNativeMap writableNativeMap = new WritableNativeMap();
                    WritableNativeArray array = new WritableNativeArray();
                    StringBuilder sb = new StringBuilder();
                    for (SeatStatusResult.SeatStatus status : seatStatuses) {
                        WritableMap map = Arguments.createMap();
                        map.putInt("st", Integer.parseInt(status.st));
                        map.putString("sname", status.sname);
                        map.putString("sid", status.sid);
                        array.pushMap(map);
                        sb.append(status.sname + "->" + Code2ServiceState(status.st) + "\n");
                    }
                    if (sb.length() > 0) {
                        String s = sb.substring(0, sb.lastIndexOf("\n"));
                        writableNativeMap.putString("ServiceState", s);
                        writableNativeMap.putArray("JsonData", array);
                        callback.invoke(writableNativeMap);
                    }
                }

            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

    private String Code2ServiceState(String code) {
        String state = "标准模式";
        if (code != null && !TextUtils.isEmpty(code)) {
            if (StateWorkOff.equals(code)) {
                state = "勿扰模式";
            } else if (StateWorkOn.equals(code)) {
                state = "超人模式";
            }
        }
        return state;
    }


    /**
     * 获取用户签名
     *
     * @param userId
     * @param callback
     */
    @ReactMethod
    public void getUserMood(final String userId, final Callback callback) {
//        VCardAPI.getUserProfile(userId, new ProtocolCallback.UnitCallback<GetMoodResult>() {
//            @Override
//            public void onCompleted(GetMoodResult getMoodResult) {
//                if (getMoodResult.ret) {
//                    String mood = "这家伙很懒,什么都没留下";
//                    if (!ListUtil.isEmpty(getMoodResult.data)) {
//                        mood = getMoodResult.data.get(0).M;
//                    }
//                    WritableMap hm = new WritableNativeMap();
//                    hm.putString("Mood", mood);
//                    WritableNativeMap map = new WritableNativeMap();
//                    map.putMap("UserInfo", hm);
//                    callback.invoke(map);
//                }
//            }
//
//            @Override
//            public void onFailure(String errMsg) {
//                String mood = "这家伙很懒,什么都没留下";
//                WritableMap hm = new WritableNativeMap();
//                hm.putString("Mood", mood);
//                WritableNativeMap map = new WritableNativeMap();
//                map.putMap("UserInfo", hm);
//                callback.invoke(map);
//            }
//        });
//        callback.invoke(null);
    }

    /**
     * 获取用户领导及员工号
     *
     * @param userId
     * @param callback
     */
    @ReactMethod
    public void getUserLead(final String userId, final Callback callback) {
        HttpUtil.getUserLead(userId, new ProtocolCallback.UnitCallback<LeadInfo>() {
            @Override
            public void onCompleted(LeadInfo leadInfo) {
                WritableMap hm = new WritableNativeMap();

                hm.putString("Leader", leadInfo.getData().getLeader());
                hm.putString("Empno", leadInfo.getData().getSn());
                String leaderId = leadInfo.getData().getQtalk_id();
                if (!leaderId.contains("@")) {
                    leaderId += "@" + QtalkNavicationService.getInstance().getXmppdomain();
                }
                hm.putString("LeaderId", leaderId);
                WritableNativeMap map = new WritableNativeMap();
                map.putMap("UserInfo", hm);
                callback.invoke(map);
            }

            @Override
            public void onFailure(String errMsg) {

            }
        });
    }

    /**
     * 获取用户信息
     *
     * @param userId
     * @param callback
     */
    @ReactMethod
    public void getUserInfo(final String userId, final Callback callback) {

        ConnectionUtil.getInstance().getUserCard(userId, new IMLogicManager.NickCallBack() {
            @Override
            public void onNickCallBack(Nick nick) {
                WritableMap hm = new WritableNativeMap();

                hm.putString("Name", nick.getName());
                hm.putString("Remarks", nick.getMark());
                hm.putString("HeaderUri", nick.getHeaderSrc());
                hm.putString("Department", nick.getDescInfo());
                hm.putString("UserId", nick.getXmppId());
                WritableNativeMap map = new WritableNativeMap();
                map.putMap("UserInfo", hm);
                callback.invoke(map);
            }
        }, false, true);
    }

    @ReactMethod
    public void getUserInfoByUserCard(final String userId, final Callback callback) {


        Nick cache = ConnectionUtil.getInstance().getNickById(userId);
        WritableMap hm = new WritableNativeMap();

        hm.putString("Name", cache.getName());
        hm.putString("Remarks", cache.getMark());
        hm.putString("HeaderUri", cache.getHeaderSrc());
        hm.putString("Department", cache.getDescInfo());
        hm.putString("UserId", cache.getXmppId());
        hm.putString("Mood", cache.getMood());
        WritableNativeMap map = new WritableNativeMap();
        map.putMap("UserInfo", hm);
        List<UserHaveMedalStatus> list = IMDatabaseManager.getInstance().selectUserHaveMedalStatusByUserid(QtalkStringUtils.parseId(userId),QtalkStringUtils.parseDomain(userId));
        WritableArray medalList = new WritableNativeArray();
        for (int i = 0; i < list.size(); i++) {
            medalList.pushString(list.get(i).getSmallIcon());
        }
        map.putArray("medalList",medalList);
        callback.invoke(map);

        ConnectionUtil.getInstance().getUserCard(userId, new IMLogicManager.NickCallBack() {
            @Override
            public void onNickCallBack(Nick nick) {
                WritableMap hm = new WritableNativeMap();

                hm.putString("Name", nick.getName());
                hm.putString("Remarks", nick.getMark());
                hm.putString("HeaderUri", nick.getHeaderSrc());
                hm.putString("Department", nick.getDescInfo());
                hm.putString("UserId", nick.getXmppId());
                hm.putString("Mood", nick.getMood());
                WritableNativeMap map = new WritableNativeMap();
                map.putMap("UserInfo", hm);
                map.putString("UserId", nick.getXmppId());

                sendEvent("updateNick", map);
//                netCallback.invoke(map);
            }
        }, true, false);
    }


    /**
     * 设置备注名
     *
     * @param params
     * @param callback
     */
    @ReactMethod
    public void saveRemark(ReadableMap params, final Callback callback) {
        final String userId = params.getString("UserId");
        final String Remark = params.getString("Remark");
        String Name = params.getString("Name");

        final WritableNativeMap re = new WritableNativeMap();
        final UserConfigData userConfigData = new UserConfigData();
        userConfigData.setKey(CacheDataType.kMarkupNames);
        userConfigData.setSubkey(userId);
        userConfigData.setValue(Remark);
        userConfigData.setIsdel(CacheDataType.Y);
        userConfigData.setType(CacheDataType.set);


        HttpUtil.setUserConfig(userConfigData, new ProtocolCallback.UnitCallback<NewRemoteConfig>() {
            @Override
            public void onCompleted(NewRemoteConfig newRemoteConfig) {
                Logger.i("新版个人配置接口 set");
                if (newRemoteConfig.isRet()) {
                    if (newRemoteConfig.getData().getClientConfigInfos().size() > 0) {
                        ConnectionUtil.refreshTheConfig(newRemoteConfig);
//                        IMDatabaseManager.getInstance().insertUserConfigVersion(newRemoteConfig.getData().getVersion());
//                        IMDatabaseManager.getInstance().bulkUserConfig(newRemoteConfig);
                        Nick nick = ConnectionUtil.getInstance().getNickById(userId);
                        nick.setMark(Remark);
                        ConnectionUtil.getInstance().setNickToCache(nick);
                        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.REFRESH_NICK);
                        re.putBoolean("ok", true);
                        com.orhanobut.logger.Logger.i("设置备注漫游返回成功");
//                        com.orhanobut.logger.Logger.i("设置备注漫游返回为null失败");
                    } else {
//                                userConfigData.setVersion(newRemoteConfigs.getData().getVersion());
//                    IMDatabaseManager.getInstance().insertUserConfigVersion(newRemoteConfig.getData().getVersion());
//                    IMDatabaseManager.getInstance().insertUserConfigVersion(userConfigData);
//
//                        Nick nick = ConnectionUtil.getInstance().getNickById(userId);
//                        nick.setMark(Remark);
//                        ConnectionUtil.getInstance().setNickToCache(nick);
//                        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.REFRESH_NICK);
//                        re.putBoolean("ok", true);
//                        com.orhanobut.logger.Logger.i("设置备注漫游返回成功");
                        //todo 这里应该做出一些什么通知
                    }
                } else {
                    re.putBoolean("ok", false);
                }

                callback.invoke(re);
            }

            @Override
            public void onFailure(String errMsg) {
                re.putBoolean("ok", false);
                com.orhanobut.logger.Logger.i("设置备注漫游返回为null失败");
                callback.invoke(re);
            }
        });


    }


    /**
     * 评论
     *
     * @param params
     * @param callback
     */
    @ReactMethod
    public void comment(ReadableMap params, Callback callback) {

    }

    /**
     * 查看大头像
     *
     * @param params
     * @param callback
     */
    @ReactMethod
    public void browseBigHeader(ReadableMap params, Callback callback) {
        String id = params.getString("UserId");
        ConnectionUtil.getInstance().getUserCard(id, new IMLogicManager.NickCallBack() {
            @Override
            public void onNickCallBack(Nick nick) {
                if (nick != null) {
                    String url = nick.getHeaderSrc();
                    NativeApi.openBigImage(url, MyDiskCache.getSmallFile(url + "&w=96&h=96").getAbsolutePath());
                }

            }
        }, false, false);
    }

    /**
     * 打开单人会话
     *
     * @param params
     */
    @ReactMethod
    public void openUserChat(ReadableMap params) {
        String jid = params.getString("UserId");
        NativeApi.openSingleChat(jid, jid);
    }

    /**
     * 打开指定人朋友圈
     *
     * @param params
     */
    @ReactMethod
    public void openUserWorkWorld(ReadableMap params) {
        String jid = params.getString("UserId");
        NativeApi.openUserWorkWorld(jid, jid);
    }

    /**
     * 打开开发人员会话
     */
    @ReactMethod
    public void openDeveloperChat() {
//        new String();
        NativeApi.openDeveloperChat();

    }

    /**
     * 添加好友
     *
     * @param params
     */
    @ReactMethod
    public void addUserFriend(ReadableMap params) {
        String jid = params.getString("UserId");
        NativeApi.openAddFriend(jid);
        saveRNActLog("add friends", "添加好友", "好友名片页");
    }

    /**
     * 删除好友
     *
     * @param params
     */
    @ReactMethod
    public void deleteUserFriend(ReadableMap params) {
        String jid = params.getString("UserId");
        //这个好友可能是跨域的 所以domain不能取内存的 要从jid截取
        ConnectionUtil.getInstance().deleteFriend(QtalkStringUtils.parseId(jid), QtalkStringUtils.parseDomain(jid));
        saveRNActLog("delete friend", "删除好友", "好友名片页");
    }

    /**
     * 判断是否为好友
     *
     * @param userId
     * @param callback
     */
    @ReactMethod
    public void getFriend(String userId, Callback callback) {
        boolean isfriend = ConnectionUtil.getInstance().isMyFriend(userId);
        WritableNativeMap map = new WritableNativeMap();
        map.putBoolean("FriendBOOL", isfriend);
        callback.invoke(map);
    }

    /**
     * 评论
     *
     * @param params
     */
    @ReactMethod
    public void commentUser(ReadableMap params) {

    }

    /**
     * 发送邮件
     *
     * @param params
     */
    @ReactMethod
    public void sendEmail(ReadableMap params) {
        String userId = params.getString("UserId");
        //qtalk 暂时这么写 目前没有借口获取email后缀地址
        userId = userId.substring(0, userId.lastIndexOf("@") + 1) + "qunar.com";
        NativeApi.openEmail(userId);
    }

    private void startHelpChat(String logFilePath) {
//        Random random = new Random();
//        int r = random.nextInt(devs.length);
//        if (r < 0) {
//            r = 0;
//        } else if (r >= devs.length) {
//            r = devs.length - 1;
//        }
//        Intent intent = new Intent(, PbChatActivity.class);
//        String jid = devs[r];
//        intent.putExtra("jid", jid);
//        intent.putExtra("content", content);
//        intent.putExtra("isFromChatRoom", false);
//        intent.putExtra("sendLogFile", logFilePath);
//        BugreportActivity.this.startActivity(intent);
    }

    /**
     * 获取群二维码图片
     *
     * @param groupId
     * @param callback
     */
    @ReactMethod
    public void getGroupQRCode(String groupId, Callback callback) {
        Logger.i("RNModule:获取用户二维码:" + groupId);
        String imageBase64 = QRUtil.generateQRBase64(Constants.Config.QR_SCHEMA + "://group?id=" + groupId);
        WritableNativeMap map = new WritableNativeMap();
        map.putString("qrCode", imageBase64);
        callback.invoke(map);
    }


    /**
     * 设置群名称
     *
     * @param params
     * @param callback
     */
    @ReactMethod
    public void saveGroupName(ReadableMap params, final Callback callback) {
        String groupId = params.getString("GroupId");
        final String groupName = params.getString("GroupName");
        final WritableNativeMap map = new WritableNativeMap();
        ConnectionUtil.getInstance().getMucCard(groupId, new IMLogicManager.NickCallBack() {
            @Override
            public void onNickCallBack(Nick nick) {
                if (nick == null)
                    return;
                // TODO: 2017/8/24 updataMucInfo
                SetMucVCardData setMucVCardData = new SetMucVCardData();
                setMucVCardData.nick = groupName;
                setMucVCardData.muc_name = nick.getGroupId();
                setMucVCardData.desc = nick.getIntroduce();
                setMucVCardData.title = nick.getTopic();
                setMucVCardData.pic = nick.getHeaderSrc();
                List<SetMucVCardData> groups = new ArrayList();
                groups.add(setMucVCardData);
                HttpUtil.setMucVCard(groups, new ProtocolCallback.UnitCallback<SetMucVCardResult>() {
                    @Override
                    public void onFailure(String errMsg) {
                        map.putBoolean("ok", false);
//                        chatRoomInfoView.setUpdateResult(false, "更改失败");
                        callback.invoke(map);
                    }

                    @Override
                    public void onCompleted(SetMucVCardResult s) {
                        if (s != null && s.data != null && s.data.size() > 0) {
//                            chatRoomInfoView.setUpdateResult(true, "更改成功");
                            map.putBoolean("ok", true);
//                        chatRoomInfoView.setUpdateResult(false, "更改失败");
                            callback.invoke(map);
                        } else {

//                            chatRoomInfoView.setUpdateResult(false, "更改失败");
                            map.putBoolean("ok", false);
//                        chatRoomInfoView.setUpdateResult(false, "更改失败");
                            callback.invoke(map);
                        }
                    }
                });

            }
        }, false, false);


    }

    /**
     * 设置群公告
     *
     * @param params
     * @param params
     * @param callback
     */
    @ReactMethod
    public void saveGroupTopic(ReadableMap params, final Callback callback) {
        String groupId = params.getString("GroupId");
        final String groupTopic = params.getString("GroupTopic");
        final WritableNativeMap map = new WritableNativeMap();
        ConnectionUtil.getInstance().getMucCard(groupId, new IMLogicManager.NickCallBack() {
            @Override
            public void onNickCallBack(Nick nick) {
                if (nick == null)
                    return;
                // TODO: 2017/8/24 updataMucInfo
                SetMucVCardData setMucVCardData = new SetMucVCardData();
                setMucVCardData.nick = nick.getName();
                setMucVCardData.muc_name = nick.getGroupId();
                setMucVCardData.desc = nick.getIntroduce();
                setMucVCardData.title = groupTopic;
                setMucVCardData.pic = nick.getHeaderSrc();
                List<SetMucVCardData> groups = new ArrayList();
                groups.add(setMucVCardData);
                HttpUtil.setMucVCard(groups, new ProtocolCallback.UnitCallback<SetMucVCardResult>() {
                    @Override
                    public void onFailure(String errMsg) {
                        map.putBoolean("ok", false);
//                        chatRoomInfoView.setUpdateResult(false, "更改失败");
                        callback.invoke(map);
                    }

                    @Override
                    public void onCompleted(SetMucVCardResult s) {
                        if (s != null && s.data != null && s.data.size() > 0) {
//                            chatRoomInfoView.setUpdateResult(true, "更改成功");
                            map.putBoolean("ok", true);
//                        chatRoomInfoView.setUpdateResult(false, "更改失败");
                            callback.invoke(map);
                        } else {

//                            chatRoomInfoView.setUpdateResult(false, "更改失败");
                            map.putBoolean("ok", false);
//                        chatRoomInfoView.setUpdateResult(false, "更改失败");
                            callback.invoke(map);
                        }
                    }
                });

            }
        }, false, false);


    }

    /**
     * 添加群成员
     *
     * @param params
     * @param callback
     */
    @ReactMethod
    public void addGroupMember(ReadableMap params, Callback callback) {
        boolean isGroup = params.getBoolean("isGroup");
        //如果是群组情况直接添加
        if (isGroup) {
            String groupId = params.getString("groupId");
            ReadableMap map = params.getMap("members");
            ReadableMapKeySetIterator keySet = map.keySetIterator();
            inviteUserList = new ArrayList<>();
            inviteName = new String();
            while (keySet.hasNextKey()) {
                ReadableMap item = map.getMap(keySet.nextKey());
                String userId = item.getString("xmppId");
                String name = item.getString("name");
                inviteUserList.add(userId);
                if (userId.equals(CurrentPreference.getInstance().getPreferenceUserId())) {
                    continue;
                }
                inviteName += " " + name;

            }


            ConnectionUtil.getInstance().inviteMessageV2(groupId, inviteUserList);
        } else {
            //如果是单人发起 创建群并添加好友
            String id = UUID.randomUUID().toString().replace("-", "");
            final String roomId = QtalkStringUtils.roomId2Jid(id);
            ConnectionUtil.getInstance().createGroup(roomId);
            if (params.hasKey(Constants.BundleKey.IS_FROM_SHARE) //转发或者是分享
                    || params.hasKey(Constants.BundleKey.IS_TRANS)
                    && createGroups != null) {
                createGroups.put(roomId, params);
            }

            //提前将一会要加入群的成员数据准备好
            ReadableMap map = params.getMap("members");
            ReadableMapKeySetIterator keySet = map.keySetIterator();
            inviteUserList = new ArrayList<>();
            inviteName = new String();
            while (keySet.hasNextKey()) {
                ReadableMap item = map.getMap(keySet.nextKey());
                String userId = item.getString("xmppId");
                String name = item.getString("name");

                inviteUserList.add(userId);
                if (userId.equals(CurrentPreference.getInstance().getPreferenceUserId())) {
                    continue;
                }
                inviteName += " " + name;
            }


        }

    }

    /**
     * 群角色管理
     * @param params
     * @param callback
     */
    @ReactMethod
    public void setGroupAdmin(ReadableMap params, Callback callback){
        Logger.i("setGroupAdmin:" + params.toString());
        String groupId = params.getString("groupId");
        String xmppid = params.getString("xmppid");
        String name = params.getString("name");
        boolean isAdmin = params.getBoolean("isAdmin");
        ConnectionUtil.getInstance().setGroupAdmin(groupId,xmppid,name,isAdmin);
    }


    public String getLocalImage(String image) {

        try {
            File imageFile = Glide.with(CommonConfig.globalContext)
                    .load(image)
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();
            return imageFile.getAbsolutePath();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 根据xmppid清空会话
     *
     * @param params
     */
    @ReactMethod
    public void clearImessage(ReadableMap params) {
        String xmppid = params.getString("xmppId");
        ConnectionUtil.getInstance().deleteIMmessageByXmppId(xmppid);
    }


    /**
     * 获取自己的签名
     *
     * @param callback
     */
    @ReactMethod
    public void getMyMood(final Callback callback) {
//        String user = IMUserDefaults.getStandardUserDefaults().getStringValue(CommonConfig.globalContext, Constants.Preferences.lastuserid);
//        String domain = QtalkNavicationService.getInstance().getXmppdomain();
//        if (TextUtils.isEmpty(user) || TextUtils.isEmpty(domain)) {
//            return;
//        }
//        final String lastid = user + "@" + domain;
//        VCardAPI.getUserProfile(lastid, new ProtocolCallback.UnitCallback<GetMoodResult>() {
//            @Override
//            public void onCompleted(GetMoodResult getMoodResult) {
//                if (getMoodResult.ret) {
//                    String mood = "这家伙很懒,什么都没留下";
//                    if (!ListUtil.isEmpty(getMoodResult.data)) {
//                        mood = getMoodResult.data.get(0).M;
//                        WritableNativeMap map = new WritableNativeMap();
//                        map.putString("mood", mood);
//                        callback.invoke(map);
//                    }
////                    ListUtil.isEmpty(getMoodResult.data);
////                    final String finalMood = mood;
//
////                    Nick nick = ConnectionUtil.getInstance().getNickById(lastid);
//
//                }
//            }
//
//            @Override
//            public void onFailure(String errMsg) {
//
//            }
//        });
    }


    /**
     * 获取自己的信息
     *
     * @param callback
     */
    @ReactMethod
    public void getMyInfo(final Callback callback) {

        String user = IMUserDefaults.getStandardUserDefaults().getStringValue(CommonConfig.globalContext, Constants.Preferences.lastuserid);
        String domain = QtalkNavicationService.getInstance().getXmppdomain();
        if (TextUtils.isEmpty(user) || TextUtils.isEmpty(domain)) {
            return;
        }
        final String lastid = user + "@" + domain;
        ConnectionUtil.getInstance().getUserCard(lastid, new IMLogicManager.NickCallBack() {
            @Override
            public void onNickCallBack(Nick nick) {
                WritableMap hm = new WritableNativeMap();

                hm.putString("Name", nick.getName());
                hm.putString("HeaderUri", nick.getHeaderSrc());
                String depar = "未知";
                if (!TextUtils.isEmpty(nick.getDescInfo())) {
                    try {
                        String strs[] = nick.getDescInfo().split("/");
                        if (strs.length > 1) {


                            if (TextUtils.isEmpty(strs[0])) {
                                if(TextUtils.isEmpty(strs[2])){
                                    if(TextUtils.isEmpty(strs[1])){
                                        if(TextUtils.isEmpty(strs[0])){
                                            depar ="未知";
                                        }else{
                                            depar = strs[0];
                                        }
                                    }else{
                                        depar = strs[1];
                                    }
                                }else{
                                    depar = strs[2];
                                }

                            } else {
                                if(TextUtils.isEmpty(strs[1])){
                                    if(TextUtils.isEmpty(strs[0])){
                                        depar = "未知";
                                    }else{
                                        depar = strs[0];
                                    }
                                }else{
                                    depar = strs[1];
                                }
                            }
                        }else{
                            depar = strs[0];
                        }
                    } catch (Exception e) {

                    }
                }

                hm.putString("Department", depar);
                hm.putString("UserId", nick.getXmppId());
                hm.putString("Mood", nick.getMood());
                WritableNativeMap map = new WritableNativeMap();
                map.putMap("MyInfo", hm);
                List<UserHaveMedalStatus> list = IMDatabaseManager.getInstance().selectUserWearMedalStatusByUserid(CurrentPreference.getInstance().getUserid(),QtalkNavicationService.getInstance().getXmppdomain());
                WritableArray medalList = new WritableNativeArray();
                for (int i = 0; i < list.size(); i++) {
                    medalList.pushString(list.get(i).getSmallIcon());
                }
                map.putArray("medalList",medalList);

                callback.invoke(map);
            }
        }, true, true);


//

//


    }

    /**
     * 设置个性签名
     *
     * @param params
     * @param callback
     */
    @ReactMethod
    public void savePersonalSignature(ReadableMap params, final Callback callback) {
        Logger.i("RNModule:设置个性签名+++:" + params.toString());
        String userId = params.getString("UserId");
        final String mood = params.getString("PersonalSignature");
        final WritableNativeMap map = new WritableNativeMap();
        VCardAPI.setMyUserProfile(mood, new ProtocolCallback.UnitCallback<SetVCardResult>() {
            @Override
            public void onCompleted(SetVCardResult setMoodResult) {
                if (setMoodResult.ret) {
                    map.putBoolean("ok", true);
                } else {
                    map.putBoolean("ok", false);
                    map.putString("message", "无权访问");
                }
                callback.invoke(map);
            }

            @Override
            public void onFailure(String errMsg) {
                map.putBoolean("ok", false);
                map.putString("message", "设置失败");
                callback.invoke(map);
            }
        });

    }


    /**
     * 获取用户二维码
     *
     * @param userId
     * @param callback
     */
    @ReactMethod
    public void getUserQRCode(String userId, Callback callback) {
        Logger.i("RNModule:获取用户二维码:" + userId);
        String imageBase64 = QRUtil.generateQRBase64(Constants.Config.QR_SCHEMA + "://user?id=" + userId);
        WritableNativeMap map = new WritableNativeMap();
        map.putString("qrCode", imageBase64);
        callback.invoke(map);
    }

    /**
     * 发送反馈意见
     *
     * @param params
     */
    @ReactMethod
    public void sendAdviceMessage(ReadableMap params, final Callback callback) {
        String adviceMsg = params.getString("adviceText");
        boolean isUploadDb = params.getBoolean("logSelected");
        Logger.i("RNModule:发送反馈意见+++:" + adviceMsg);
        try {
            IMDatabaseManager.getInstance().manualCheckPoint();//手动checkpoint数据库
        }catch (Exception e){
            Logger.e("manualCheckPoint exception:" + e.getLocalizedMessage());
        }
//        FeedBackServcie.runFeedBackServcieService(CommonConfig.globalContext, new String[]{adviceMsg});
        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.FEED_BACK, new String[]{adviceMsg},true,isUploadDb);
        WritableNativeMap map = new WritableNativeMap();
        map.putBoolean("ok", true);
        callback.invoke(map);

        saveRNActLog("Suggestions", "建议反馈", "我的页");
    }

    private void toast(final String msg) {
        CommonConfig.mainhandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CommonConfig.globalContext, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 获取联系人页展示的用户
     *
     * @param callback
     */
    @ReactMethod
    public void getContacts(Callback callback) {
        List<Nick> fList = ConnectionUtil.getInstance().SelectFriendListForRN();
        WritableNativeMap map = new WritableNativeMap();
        WritableArray array = new WritableNativeArray();
        if (fList != null && fList.size() > 0) {
//            WritableMap map = new WritableNativeMap();

            for (int i = 0; i < fList.size(); i++) {
                Nick nick = fList.get(i);
                WritableNativeMap item = new WritableNativeMap();
                String name = nick.getName();
                String pinyin = nick.getXmppId();
                if (!TextUtils.isEmpty(name)) {
                    pinyin = HanziToPinyin.zh2Abb(name);
                }

                item.putString("Name", TextUtils.isEmpty(name) ? nick.getXmppId() : name);
                item.putString("HeaderUri", TextUtils.isEmpty(nick.getHeaderSrc()) ? "" : nick.getHeaderSrc());
                item.putString("SearchIndex", pinyin);
                item.putString("XmppId", nick.getXmppId());
                item.putString("Remark", nick.getMark());
                item.putString("Mood",nick.getMood());
                array.pushMap(item);
            }

        }
        map.putArray("contacts", array);
        callback.invoke(map);

//        WritableNativeMap map2= new WritableNativeMap();
//        map2.putString("aaa","aaaa");
//        sendEvent("EventName",map2);

    }

//    /**
//     *
//     * @param params
//     * @param callback
//     */
//    @ReactMethod
//    public void addGroupMember(ReadableMap params, Callback callback){
//
//    }

    /**
     * 获取指定联系人nick
     *
     * @param xmppId
     * @param callback
     */
    @ReactMethod
    public void getContactsNick(String xmppId, final Callback callback) {
        ConnectionUtil.getInstance().getUserCard(xmppId, new IMLogicManager.NickCallBack() {
            @Override
            public void onNickCallBack(Nick nick) {
                WritableNativeMap item = new WritableNativeMap();
                String name = nick.getName();
                String pinyin = nick.getXmppId();
                if (!TextUtils.isEmpty(name)) {
                    pinyin = HanziToPinyin.zh2Abb(name);
                }

                item.putString("Name", TextUtils.isEmpty(name) ? nick.getXmppId() : name);
                item.putString("HeaderUri", TextUtils.isEmpty(nick.getHeaderSrc()) ? "" : nick.getHeaderSrc());
                item.putString("SearchIndex", pinyin);
                item.putString("XmppId", nick.getXmppId());
                item.putString("Remark", nick.getMark());
                item.putString("Mood",nick.getMood());
                WritableMap map = new WritableNativeMap();
                map.putMap("nick", item);
                callback.invoke(map);
            }
        }, true, false);
    }


    /**
     * 获取群列表
     *
     * @param callback
     */
    @ReactMethod
    public void getGroupList(Callback callback) {
        List<Nick> nickList = ConnectionUtil.getInstance().SelectAllGroup();
        WritableNativeArray array = new WritableNativeArray();
        for (int i = 0; i < nickList.size(); i++) {
            Nick n = nickList.get(i);
            WritableNativeMap map = new WritableNativeMap();
            map.putString("title", n.getName());
            map.putString("GroupId", n.getGroupId());
            String url = n.getHeaderSrc();
            if (TextUtils.isEmpty(url)) {
                url = defaultMucImage;
            }
            map.putString("HeaderUri", url);
            map.putString("Name", n.getName());
            array.pushMap(map);
        }
        WritableNativeMap re = new WritableNativeMap();
        re.putArray("groupList", array);
        callback.invoke(re);
    }

    /**
     * 根据搜索字段获取群组
     *
     * @param searchText
     * @param callback
     */
    @ReactMethod
    public void searchGroupListWithKey(String searchText, Callback callback) {
        List<Nick> nickList = ConnectionUtil.getInstance().SelectGroupListBySearchText(searchText, 100);
        WritableNativeArray array = new WritableNativeArray();
        for (int i = 0; i < nickList.size(); i++) {
            Nick n = nickList.get(i);
            WritableNativeMap map = new WritableNativeMap();
            map.putString("title", n.getName());
            map.putString("GroupId", n.getGroupId());
            String url = n.getHeaderSrc();
            if (TextUtils.isEmpty(url)) {
                url = defaultMucImage;
            }
            map.putString("HeaderUri", url);
            map.putString("Name", n.getName());
            array.pushMap(map);
        }
        WritableNativeMap re = new WritableNativeMap();
        re.putArray("groupList", array);
        callback.invoke(re);
    }


    public void sendEvent(String eventName, WritableMap params) {
        getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    /**
     * 清空app缓存
     *
     * @param callback
     */
    @ReactMethod
    public void clearAndroidAppCache(Callback callback) {
        WritableMap map = new WritableNativeMap();
        try {
            final List<File> files = Arrays.asList(MyDiskCache.getAllCacheDir());
            for (File dir : files) {
                FileUtils.removeDir(dir);
            }
            map.putBoolean("ok", true);
        } catch (Exception e) {
            map.putBoolean("ok", false);
        }
        callback.invoke(map);
    }

    /**
     * 获取用户缓存数据大小
     *
     * @param callback
     */
    @ReactMethod
    public void getAppCache(final Callback callback) {
        BackgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final List<File> files = Arrays.asList(MyDiskCache.getAllCacheDir());
                long tmpCost = 0;
                for (File dir : files) {
                    tmpCost += FileUtils.getDirSize(dir);
                }
                final long totalBytes = FileUtils.calculateDiskSize(FileUtils.getExternalFilesDir(QunarIMApp.getContext()));
                final long costBytes = tmpCost;
                final long freeBytes = FileUtils.calculateDiskFree(FileUtils.getExternalFilesDir(QunarIMApp.getContext()));
                final long otherBytes = totalBytes - freeBytes - costBytes;
                final String costStr = FileUtils.formatByteSize(costBytes);
                final String freeStr = FileUtils.formatByteSize(freeBytes);
                final String otherStr = FileUtils.formatByteSize(otherBytes);
                final float costRate = 1.0f * costBytes / totalBytes;
                final float freeRate = 1.0f * freeBytes / totalBytes;
                final float otherRate = 1.0f * otherBytes / totalBytes;
                final float costedRate = 0f;
                final float freedRate = freeRate + costRate;
                WritableMap map = new WritableNativeMap();
                map.putString("AppCache", costStr);
                callback.invoke(map);
            }
        });
    }


    /**
     * 获取客户端版本号
     *
     * @param callback
     */
    @ReactMethod
    public void getAppVersion(Callback callback) {
        String version = "" + QunarIMApp.getQunarIMApp().getVersion();
        WritableMap map = new WritableNativeMap();
        map.putString("AppVersion", version);
        callback.invoke(map);
    }

    /**
     * 同步在线通知状态
     *
     * @param callback
     */
    @ReactMethod
    public void syncOnLineNotifyState(Callback callback) {
//        CurrentPreference.ProFile proFile = CurrentPreference.getInstance().getProFile();
//        WritableMap map = new WritableNativeMap();
////        WritableMap params = new WritableNativeMap();
////        params.putBoolean("syncOnLineNotifyState",proFile.isOfflinePush());
//        map.putBoolean("state", proFile.isOfflinePush());
//        callback.invoke(map);


        WritableMap map = new WritableNativeMap();
        boolean state = ConnectionUtil.getInstance().getPushStateBy(PushSettinsStatus.PUSH_ONLINE);
        map.putBoolean("state", state);
        callback.invoke(map);

    }

    /**
     * 获取通知声音状态
     *
     * @param callback
     */
    @ReactMethod
    public void getNotifySoundState(Callback callback) {
//        CurrentPreference.ProFile proFile = CurrentPreference.getInstance().getProFile();
//        WritableMap map = new WritableNativeMap();
////        WritableMap params = new WritableNativeMap();
////        params.putBoolean("getNotifySoundState",proFile.isTurnOnMsgSound());
//        map.putBoolean("state", proFile.isTurnOnMsgSound());
//        callback.invoke(map);

        WritableMap map = new WritableNativeMap();
        boolean state = ConnectionUtil.getInstance().getPushStateBy(PushSettinsStatus.SOUND_INAPP);
        map.putBoolean("state", state);
        callback.invoke(map);

    }

    /**
     * 获取用户展示心情短语状态
     *
     * @param callback
     */
    @ReactMethod
    public void getShowUserModState(Callback callback) {

    }

    /**
     * 获取用户通知是否显示详情
     *
     * @param callback
     */
    @ReactMethod
    public void getNotifyPushDetailsState(Callback callback) {

//        CurrentPreference.ProFile proFile = CurrentPreference.getInstance().getProFile();
//        WritableMap map = new WritableNativeMap();
////        WritableMap params = new WritableNativeMap();
////        params.putBoolean("getNotifySoundState",proFile.isTurnOnMsgSound());
//        map.putBoolean("state", proFile.isShowContentPush());
//        callback.invoke(map);

        WritableMap map = new WritableNativeMap();
        boolean state = ConnectionUtil.getInstance().getPushStateBy(PushSettinsStatus.SHOW_CONTENT);
        map.putBoolean("state", state);
        callback.invoke(map);
    }

    /**
     * 获取用户是否开启推送
     *
     * @param callback
     */
    @ReactMethod
    public void getStartPushState(Callback callback) {
//        CurrentPreference.ProFile proFile = CurrentPreference.getInstance().getProFile();
//        WritableMap map = new WritableNativeMap();
////        WritableMap params = new WritableNativeMap();
////        params.putBoolean("getNotifySoundState",proFile.isTurnOnMsgSound());
//        map.putBoolean("state", proFile.isTurnOnPsuh());
//        callback.invoke(map);

        WritableMap map = new WritableNativeMap();
        boolean state = ConnectionUtil.getInstance().getPushStateBy(PushSettinsStatus.PUSH_SWITCH);
        map.putBoolean("state", state);
        callback.invoke(map);
    }

    /**
     * 获取用户通知震动状态
     *
     * @param callback
     */
    @ReactMethod
    public void getNotifyVibrationState(Callback callback) {
//        CurrentPreference.ProFile proFile = CurrentPreference.getInstance().getProFile();
//        WritableMap map = new WritableNativeMap();
////        WritableMap params = new WritableNativeMap();
////        params.putBoolean("getNotifySoundState",proFile.isTurnOnMsgSound());
//        map.putBoolean("state", proFile.isTurnOnMsgShock());
//        callback.invoke(map);

        WritableMap map = new WritableNativeMap();
        boolean state = ConnectionUtil.getInstance().getPushStateBy(PushSettinsStatus.VIBRATE_INAPP);
        map.putBoolean("state", state);
        callback.invoke(map);
    }

    /**
     * 更新在线收通知状态
     *
     * @param onlineState
     * @param callback
     */
    @ReactMethod
    public void updateOnLineNotifyState(boolean onlineState, final Callback callback) {

        final boolean state = onlineState;
        final WritableMap map = new WritableNativeMap();
        HttpUtil.setPushMsgSettings(PushSettinsStatus.PUSH_ONLINE, state ? 1 : 0, new ProtocolCallback.UnitCallback<Boolean>() {
            @Override
            public void onCompleted(Boolean aBoolean) {
                ConnectionUtil.getInstance().setPushState(PushSettinsStatus.PUSH_ONLINE, state ? 1 : 0);
                map.putBoolean("ok", true);
                callback.invoke(map);
            }

            @Override
            public void onFailure(String errMsg) {
                map.putBoolean("ok", false);
                callback.invoke(map);
            }
        });


//        CurrentPreference.ProFile proFile = CurrentPreference.getInstance().getProFile();
//
//
//
//        Protocol.setPushState(state, new ProtocolCallback.UnitCallback<String>() {
//            @Override
//            public void onCompleted(String s) {
//                Logger.i("changeOfflinePush  onCompleted  " + s);
//                CurrentPreference.getInstance().setOfflinePush(state);
//                IMDatabaseManager.getInstance().updateConfig();
//                map.putBoolean("ok", true);
//                callback.invoke(map);
//            }
//
//            @Override
//            public void onFailure() {
//                Logger.i("changeOfflinePush  onFailure  ");
//                map.putBoolean("ok", false);
//                callback.invoke(map);
//            }
//        });


    }

    /**
     * 更新通知声音状态
     *
     * @param notifySoundState
     * @param callback
     */
    @ReactMethod
    public void updateNotifySoundState(boolean notifySoundState, final Callback callback) {

//        boolean state = notifySoundState;
//        CurrentPreference.getInstance().setTurnOnMsgSound(state);
//        IMDatabaseManager.getInstance().updateConfig();
//        WritableMap map = new WritableNativeMap();
//        map.putBoolean("ok", true);
//        callback.invoke(map);

        final boolean state = notifySoundState;

        final WritableMap map = new WritableNativeMap();
        HttpUtil.setPushMsgSettings(PushSettinsStatus.SOUND_INAPP, state ? 1 : 0, new ProtocolCallback.UnitCallback<Boolean>() {
            @Override
            public void onCompleted(Boolean aBoolean) {
                ConnectionUtil.getInstance().setPushState(PushSettinsStatus.SOUND_INAPP, state ? 1 : 0);
                com.qunar.im.protobuf.common.CurrentPreference.getInstance().setTurnOnMsgSound(ConnectionUtil.getInstance().getPushStateBy(PushSettinsStatus.SOUND_INAPP));
                map.putBoolean("ok", true);
                callback.invoke(map);
            }

            @Override
            public void onFailure(String errMsg) {
                map.putBoolean("ok", false);
                callback.invoke(map);
            }
        });
    }

    /**
     * 更新通知震动状态
     *
     * @param stateVibration
     * @param callback
     */
    @ReactMethod
    public void updateNotifyVibrationState(boolean stateVibration, final Callback callback) {
//        boolean state = stateVibration;
//        CurrentPreference.getInstance().setTurnOnMsgShock(state);
//        IMDatabaseManager.getInstance().updateConfig();
//        WritableMap map = new WritableNativeMap();
//        map.putBoolean("ok", true);
//        callback.invoke(map);

        final boolean state = stateVibration;

        final WritableMap map = new WritableNativeMap();
        HttpUtil.setPushMsgSettings(PushSettinsStatus.VIBRATE_INAPP, state ? 1 : 0, new ProtocolCallback.UnitCallback<Boolean>() {
            @Override
            public void onCompleted(Boolean aBoolean) {
                ConnectionUtil.getInstance().setPushState(PushSettinsStatus.VIBRATE_INAPP, state ? 1 : 0);
                com.qunar.im.protobuf.common.CurrentPreference.getInstance().setTurnOnMsgShock(ConnectionUtil.getInstance().getPushStateBy(PushSettinsStatus.VIBRATE_INAPP));
                map.putBoolean("ok", true);
                callback.invoke(map);
            }

            @Override
            public void onFailure(String errMsg) {
                map.putBoolean("ok", false);
                callback.invoke(map);
            }
        });
    }

    /**
     * 更新是否开启推送状态
     *
     * @param startState
     * @param callback
     */
    @ReactMethod
    public void updateStartNotifyState(boolean startState, final Callback callback) {


//        boolean state = startState;
//        if (state) {
//            QTPushConfiguration.registPush(mActivity);
////            PushServiceUtils.startAMDService(getActivity());
//        } else {
//            QTPushConfiguration.unRegistPush(mActivity);
////            PushServiceUtils.stopAMDService(getActivity());
//        }
//
//
//        CurrentPreference.getInstance().setTurnOnPsuh(state);
//        IMDatabaseManager.getInstance().updateConfig();
//        WritableMap map = new WritableNativeMap();
//        map.putBoolean("ok", true);
//        callback.invoke(map);


        final boolean state = startState;
//        if (state) {
//            QTPushConfiguration.registPush(CommonConfig.globalContext);
////            PushServiceUtils.startAMDService(getActivity());
//        } else {
//            QTPushConfiguration.unRegistPush(CommonConfig.globalContext);
////            PushServiceUtils.stopAMDService(getActivity());
//        }

        final WritableMap map = new WritableNativeMap();
        HttpUtil.setPushMsgSettings(PushSettinsStatus.PUSH_SWITCH, state ? 1 : 0, new ProtocolCallback.UnitCallback<Boolean>() {
            @Override
            public void onCompleted(Boolean aBoolean) {
                ConnectionUtil.getInstance().setPushState(PushSettinsStatus.PUSH_SWITCH, state ? 1 : 0);
                map.putBoolean("ok", true);
                callback.invoke(map);
            }

            @Override
            public void onFailure(String errMsg) {
                map.putBoolean("ok", false);
                callback.invoke(map);
            }
        });


    }

    /**
     * 更新消息推送是否显示详情
     *
     * @param state
     * @param callback
     */
    @ReactMethod
    public void updateNotifyPushDetailsState(final boolean state, final Callback callback) {

//        final WritableMap map = new WritableNativeMap();
//        Protocol.setPushShowcontent(state ? 1 : 0, new Protocol.PushListening() {
//            @Override
//            public void onSuccess() {
//                CurrentPreference.getInstance().setShowContent(state);
//                IMDatabaseManager.getInstance().updateConfig();
//
//                map.putBoolean("ok", true);
//                callback.invoke(map);
//            }
//
//            @Override
//            public void onFailure() {
//                map.putBoolean("ok", false);
//                callback.invoke(map);
//            }
//        });


        final WritableMap map = new WritableNativeMap();
        HttpUtil.setPushMsgSettings(PushSettinsStatus.SHOW_CONTENT, state ? 1 : 0, new ProtocolCallback.UnitCallback<Boolean>() {
            @Override
            public void onCompleted(Boolean aBoolean) {
                ConnectionUtil.getInstance().setPushState(PushSettinsStatus.SHOW_CONTENT, state ? 1 : 0);
                map.putBoolean("ok", true);
                callback.invoke(map);
            }

            @Override
            public void onFailure(String errMsg) {
                map.putBoolean("ok", false);
                callback.invoke(map);
            }
        });
    }

    /**
     * 更新用户是否显示签名状态
     *
     * @param userModState
     * @param callback
     */
    @ReactMethod
    public void updateShowUserModState(boolean userModState, Callback callback) {

    }

    public void openAbout() {

    }

    /**
     * 根据文字搜索联系人
     *
     * @param params
     * @param callback
     */
    @ReactMethod
    public void selectUserListByText(ReadableMap params, Callback callback) {
        String groupId = params.getString("groupId");
        String searchText = params.getString("searchText");
        List<Nick> userList = ConnectionUtil.getInstance().SelectUserListBySearchText(groupId, searchText);
        WritableNativeArray array = new WritableNativeArray();

        for (int i = 0; i < userList.size(); i++) {
            Nick nick = userList.get(i);
            WritableNativeMap map = new WritableNativeMap();
            map.putString("name", TextUtils.isEmpty(nick.getName()) ? nick.getXmppId() : nick.getName());
            map.putString("xmppId", nick.getXmppId());
            map.putString("headerUri", TextUtils.isEmpty(nick.getHeaderSrc()) ? defaultUserImage : nick.getHeaderSrc());
            map.putBoolean("hasInGroup", nick.isInGroup());
            map.putString("desc",nick.getDescInfo());
            array.pushMap(map);

        }
        WritableNativeMap re = new WritableNativeMap();
        re.putArray("UserList", array);
        re.putBoolean("ok", true);
        callback.invoke(re);
    }

    /**
     * 根据好友
     *
     * @param params
     * @param callback
     */
    @ReactMethod
    public void selectFriendsForGroupAdd(ReadableMap params, Callback callback) {
        String groupId = params.getString("groupId");
        List<Nick> userList = ConnectionUtil.getInstance().selectFriendsForGroupAdd(groupId);
        WritableNativeArray array = new WritableNativeArray();
        for (int i = 0; i < userList.size(); i++) {
            Nick nick = userList.get(i);
            WritableNativeMap map = new WritableNativeMap();
            map.putString("name", TextUtils.isEmpty(nick.getName()) ? nick.getXmppId() : nick.getName());
            map.putString("xmppId", nick.getXmppId());
            map.putString("headerUri", TextUtils.isEmpty(nick.getHeaderSrc()) ? defaultUserImage : nick.getHeaderSrc());
            map.putString("desc",nick.getDescInfo());
            map.putBoolean("friend", true);
            array.pushMap(map);

        }
        WritableNativeMap re = new WritableNativeMap();
        re.putArray("UserList", array);
        re.putBoolean("ok", true);
        callback.invoke(re);

    }

    @ReactMethod
    public void selectGroupMemberForKick(ReadableMap params, Callback callback) {
        String groupId = params.getString("groupId");
        List<Nick> userList = ConnectionUtil.getInstance().selectGroupMemberForKick(groupId);
        WritableNativeArray array = new WritableNativeArray();
        for (int i = 0; i < userList.size(); i++) {
            Nick nick = userList.get(i);
            WritableNativeMap map = new WritableNativeMap();
            map.putString("name", TextUtils.isEmpty(nick.getName()) ? nick.getXmppId() : nick.getName());
            map.putString("xmppId", nick.getXmppId());
            map.putString("headerUri", TextUtils.isEmpty(nick.getHeaderSrc()) ? defaultUserImage : nick.getHeaderSrc());
            array.pushMap(map);

        }
        WritableNativeMap re = new WritableNativeMap();
        re.putArray("UserList", array);
        re.putBoolean("ok", true);
        callback.invoke(re);
    }

    /**
     * 群踢人
     *
     * @param params
     */
    @ReactMethod
    public void kickGroupMember(ReadableMap params, Callback callback) {
        final String groupId = params.getString("groupId");
        ReadableMap map = params.getMap("members");
        ReadableMapKeySetIterator keySet = map.keySetIterator();
        Map<String, String> memberMap = new HashMap<>();
        while (keySet.hasNextKey()) {
            ReadableMap item = map.getMap(keySet.nextKey());
            String userId = item.getString("xmppId");
            String name = item.getString("name");
            memberMap.put(name, userId);
        }
        if (TextUtils.isEmpty(groupId) || memberMap.size() == 0) {
            return;
        }
        ConnectionUtil.getInstance().delGroupMember(groupId, memberMap);
        sendEvent("closeKickMembers", new WritableNativeMap());
        toast("成员已移除");
    }

    @ReactMethod
    public void selectMemberFromGroup(ReadableMap params, Callback callback) {
        String groupId = params.getString("groupId");
        String searchText = params.getString("searchText");
        List<Nick> userList = ConnectionUtil.getInstance().selectMemberFromGroup(groupId, searchText);
        WritableNativeArray array = new WritableNativeArray();

        for (int i = 0; i < userList.size(); i++) {
            Nick nick = userList.get(i);
            WritableNativeMap map = new WritableNativeMap();
            map.putString("name", TextUtils.isEmpty(nick.getName()) ? nick.getXmppId() : nick.getName());
            map.putString("xmppId", nick.getXmppId());
            map.putString("headerUri", TextUtils.isEmpty(nick.getHeaderSrc()) ? defaultUserImage : nick.getHeaderSrc());
            array.pushMap(map);

        }
        WritableNativeMap re = new WritableNativeMap();
        re.putArray("UserList", array);
        re.putBoolean("ok", true);
        callback.invoke(re);
    }

    /**
     * 获取群成员
     */
    @ReactMethod
    public void getGroupMember(String groupId, Callback callback) {
        getGroupMemberFromDB(groupId, callback);
        ConnectionUtil.getInstance().getMembersAfterJoin(groupId);
    }

    public void getGroupMemberFromDB(String groupId, Callback callback) {
        List<GroupMember> groupMemberList = ConnectionUtil.getInstance().SelectGroupMemberByGroupId(groupId);
        if (ListUtil.isEmpty(groupMemberList)) {
            return;
        }
        WritableNativeArray array = new WritableNativeArray();
        int per = 2;
        for (int i = 0; i < groupMemberList.size(); i++) {
            GroupMember gm = groupMemberList.get(i);
            WritableNativeMap map = new WritableNativeMap();
            String affiliation = gm.getAffiliation();
            map.putString("affiliation", affiliation);
            map.putString("headerUri", TextUtils.isEmpty(gm.getHeaderSrc()) ? defaultUserImage : gm.getHeaderSrc());
            String xmppid = gm.getMemberId();
            if (CurrentPreference.getInstance().getPreferenceUserId().equals(xmppid)) {
                if (!TextUtils.isEmpty(affiliation)) {
                    per = Integer.parseInt(affiliation);
                }
            }
            map.putString("xmppjid", xmppid);
            map.putString("jid", gm.getGroupId());
            map.putString("name", gm.getName());
            array.pushMap(map);

        }
        WritableNativeMap re = new WritableNativeMap();
        re.putArray("GroupMembers", array);
        re.putBoolean("ok", true);
        re.putString("GroupId", groupId);
        re.putInt("permissions", per);
        if (callback != null) {
            callback.invoke(re);
        } else {
            sendEvent("updateGroupMember", re);
        }
    }

    /**
     * 获取群信息
     *
     * @param groupId
     * @param callback
     */
    @ReactMethod
    public void getGroupInfo(final String groupId, final Callback callback) {
//        Nick mucNick =  ConnectionUtil.getInstance().getMucNickById(groupId);
        ConnectionUtil.getInstance().getMucCard(groupId, new IMLogicManager.NickCallBack() {
            @Override
            public void onNickCallBack(Nick nick) {
                WritableNativeMap map = new WritableNativeMap();
//                WritableNativeArray array = new WritableNativeArray();
                map.putString("GroupId", nick.getGroupId());
                map.putString("Name", nick.getName());
                map.putString("HeaderSrc", nick.getHeaderSrc());
                map.putString("Topic", nick.getTopic());
                map.putString("Introduce", nick.getIntroduce());

                WritableNativeMap re = new WritableNativeMap();
                re.putBoolean("ok", true);
                re.putMap("GroupInfo", map);
                callback.invoke(re);


            }
        }, true, true);
    }


    /**
     * 获取会话PushState
     *
     * @param groupId
     * @param callback
     */
    @ReactMethod
    public void syncPushState(String groupId, Callback callback) {
//        RecentConversation rc = new RecentConversation();
//        rc.setId(groupId);
//        rc.setRealUser(groupId);
//        rc = ConnectionUtil.getInstance().SelectConversationByRC(rc);

        UserConfigData userConfigData = new UserConfigData();
        userConfigData.setSubkey(groupId);
        userConfigData.setKey(CacheDataType.kNoticeStickJidDic);
        UserConfigData ucd = ConnectionUtil.getInstance().selectUserConfigValueForKey(userConfigData);

//        recentConvDataModel.selectRecentConvById(rc);
//        panelView.setReMind(rc.getRemind()>0);
        WritableNativeMap map = new WritableNativeMap();
        map.putBoolean("state", ucd == null);
        callback.invoke(map);

    }

    /**
     * 更新检查配置
     */
    @ReactMethod
    public void updateCheckConfig() {

    }


    /**
     * 更新会话PushState
     *
     * @param groupId
     * @param state
     * @param callback
     */
    @ReactMethod
    public void updatePushState(String groupId, boolean state, final Callback callback) {


        UserConfigData userConfigData = new UserConfigData();
        userConfigData.setKey(CacheDataType.kNoticeStickJidDic);
        userConfigData.setSubkey(groupId);
//        UserConfigData.TopInfo topInfo = IMDatabaseManager.getInstance().selectSessionChatType(userConfigData);
//        userConfigData.setTopInfo(topInfo);
        final WritableNativeMap map = new WritableNativeMap();
        ConnectionUtil.getInstance().setConversationReMindOrCancel(userConfigData, new ConnectionUtil.CallBackByUserConfig() {
            @Override
            public void onCompleted() {
                map.putBoolean("ok", true);
                callback.invoke(map);
            }

            @Override
            public void onFailure() {
                map.putBoolean("ok", false);
                callback.invoke(map);
            }
        });


//        final RecentConversation rc = new RecentConversation();
//        rc.setId(groupId);
//        rc.setRealUser(groupId);
//
//        RecentConversation recentConversation = IMDatabaseManager.getInstance().SelectConversationByRC(rc);

//        HttpUtil.setGroupPushSettings(groupId, recentConversation.getRemind(), new ProtocolCallback.UnitCallback<Boolean>() {
//            @Override
//            public void onCompleted(Boolean aBoolean) {
//
//                if (aBoolean) {
//                    ConnectionUtil.getInstance().setConversationReMindOrCancel(rc);


//                } else {
//                    map.putBoolean("ok", false);
//                    callback.invoke(map);
//                }
//
//            }

////            @Override
////            public void onFailure() {
////                map.putBoolean("ok", false);
////                callback.invoke(map);
////            }
//        });


    }

    /**
     * 同步群置顶信息
     *
     * @param groupId
     * @param callback
     */
    @ReactMethod
    public void syncGroupStickyState(String groupId, Callback callback) {
//        RecentConversation rc = new RecentConversation();
//        rc.setId(groupId);
//        rc.setRealUser(groupId);
//        rc = ConnectionUtil.getInstance().SelectConversationByRC(rc);
        UserConfigData userConfigData = new UserConfigData();
        userConfigData.setSubkey(groupId + "<>" + groupId);
        userConfigData.setKey(CacheDataType.kStickJidDic);
        UserConfigData ucd = ConnectionUtil.getInstance().selectUserConfigValueForKey(userConfigData);
//        recentConvDataModel.selectRecentConvById(rc);
//        panelView.setTop(rc.getTop()>0);
        WritableNativeMap map = new WritableNativeMap();
        map.putBoolean("state", ucd != null);
        callback.invoke(map);
    }


    /**
     * 更新群置顶
     *
     * @param groupId
     * @param callback
     */
    @ReactMethod
    public void updateGroupStickyState(String groupId, final Callback callback) {
        try {
//            RecentConversation rc = new RecentConversation();
//            rc.setId(groupId);
//            rc.setRealUser(groupId);

            UserConfigData userConfigData = new UserConfigData();
            userConfigData.setKey(CacheDataType.kStickJidDic);
            userConfigData.setSubkey(groupId + "<>" + groupId);
            UserConfigData.TopInfo topInfo = new UserConfigData.TopInfo();
            topInfo.setChatType("1");
            userConfigData.setTopInfo(topInfo);
            final WritableNativeMap map = new WritableNativeMap();
            ConnectionUtil.getInstance().setConversationTopOrCancel(userConfigData, new ConnectionUtil.CallBackByUserConfig() {
                @Override
                public void onCompleted() {
                    map.putBoolean("ok", true);
                    callback.invoke(map);
                }

                @Override
                public void onFailure() {
                    map.putBoolean("ok", false);
                    callback.invoke(map);
                }
            });

//            ConnectionUtil.getInstance().setConversationTopSession(rc);


        } catch (Exception e) {
            WritableNativeMap map = new WritableNativeMap();
            map.putBoolean("ok", false);
            callback.invoke(map);
        }
    }


    /**
     * 同步单人聊天置顶信息
     *
     * @param params
     * @param callback
     */
    @ReactMethod
    public void syncChatStickyState(ReadableMap params, Callback callback) {
        String xmppId = params.getString("xmppId");
        String realJid = params.getString("realJid");
        if (TextUtils.isEmpty(realJid)) {
            realJid = xmppId;
        }
//        RecentConversation rc = new RecentConversation();
//        rc.setId(xmppId);
//        rc.setRealUser(realJid);
//        rc = ConnectionUtil.getInstance().SelectConversationByRC(rc);

        UserConfigData userConfigData = new UserConfigData();
        userConfigData.setSubkey(xmppId + "<>" + realJid);
        userConfigData.setKey(CacheDataType.kStickJidDic);
        UserConfigData ucd = ConnectionUtil.getInstance().selectUserConfigValueForKey(userConfigData);
//        recentConvDataModel.selectRecentConvById(rc);
//        panelView.setTop(rc.getTop()>0);
        WritableNativeMap map = new WritableNativeMap();
        map.putBoolean("state", ucd != null);
        callback.invoke(map);
//
//        WritableNativeMap map = new WritableNativeMap();
//        map.putBoolean("state", rc.getTop() > 0);
//        callback.invoke(map);
    }

    /**
     * 更新单人置顶状态
     *
     * @param params
     * @param callback
     */
    @ReactMethod
    public void updateUserChatStickyState(ReadableMap params, final Callback callback) {
        String xmppId = params.getString("xmppId");
        String realJid = params.getString("realJid");
//        RecentConversation rc = new RecentConversation();
//        rc.setId(xmppId);
//        rc.setRealUser(realJid);

        if (TextUtils.isEmpty(realJid)) {
            realJid = xmppId;
        }
        UserConfigData userConfigData = new UserConfigData();
        userConfigData.setKey(CacheDataType.kStickJidDic);
        userConfigData.setSubkey(xmppId + "<>" + realJid);
        UserConfigData.TopInfo topInfo = new UserConfigData.TopInfo();
        topInfo.setChatType("0");
        userConfigData.setTopInfo(topInfo);
        final WritableNativeMap map = new WritableNativeMap();
        ConnectionUtil.getInstance().setConversationTopOrCancel(userConfigData, new ConnectionUtil.CallBackByUserConfig() {
            @Override
            public void onCompleted() {

                map.putBoolean("ok", true);
                callback.invoke(map);
            }

            @Override
            public void onFailure() {
                map.putBoolean("ok", false);
                callback.invoke(map);
            }
        });

//        ConnectionUtil.getInstance().setConversationReMindOrCancel(rc);
//        ConnectionUtil.getInstance().setConversationTopSession(rc);

    }

    /**
     * 查询不在星标联系人的好友
     *
     * @param callback
     */
    @ReactMethod
    public void selectFriendsNotInStarContacts(Callback callback) {
        List<Nick> list = IMDatabaseManager.getInstance().selectFriendsNotInStarContacts();
        WritableNativeArray array = new WritableNativeArray();
        for (int i = 0; i < list.size(); i++) {
            Nick nick = list.get(i);
            WritableNativeMap map = new WritableNativeMap();
            map.putString("Name", TextUtils.isEmpty(nick.getName()) ? nick.getXmppId() : nick.getName());
            map.putString("XmppId", nick.getXmppId());
            map.putString("HeaderUri", TextUtils.isEmpty(nick.getHeaderSrc()) ? defaultUserImage : nick.getHeaderSrc());
            array.pushMap(map);

        }
        WritableNativeMap re = new WritableNativeMap();
        re.putArray("contacts", array);
        callback.invoke(re);
    }

    @ReactMethod
    public void selectUserNotInStartContacts(String key, Callback callback) {
        List<Nick> list = IMDatabaseManager.getInstance().selectUserNotInStartContacts(key);
        WritableNativeArray array = new WritableNativeArray();
        for (int i = 0; i < list.size(); i++) {
            Nick nick = list.get(i);
            WritableNativeMap map = new WritableNativeMap();
            map.putString("Name", TextUtils.isEmpty(nick.getName()) ? nick.getXmppId() : nick.getName());
            map.putString("XmppId", nick.getXmppId());
            map.putString("HeaderUri", TextUtils.isEmpty(nick.getHeaderSrc()) ? defaultUserImage : nick.getHeaderSrc());
            array.pushMap(map);

        }
        WritableNativeMap re = new WritableNativeMap();
        re.putArray("users", array);
        callback.invoke(re);
    }

    @ReactMethod
    public void selectStarOrBlackContacts(String pkey, Callback callback) {
        List<Nick> list = IMDatabaseManager.getInstance().selectStarOrBlackContactsAsNick(pkey);
        WritableNativeArray array = new WritableNativeArray();
        for (int i = 0; i < list.size(); i++) {
            Nick nick = list.get(i);
            WritableNativeMap map = new WritableNativeMap();
            map.putString("Name", TextUtils.isEmpty(nick.getName()) ? nick.getXmppId() : nick.getName());
            map.putString("XmppId", nick.getXmppId());
            map.putString("HeaderUri", TextUtils.isEmpty(nick.getHeaderSrc()) ? defaultUserImage : nick.getHeaderSrc());
            array.pushMap(map);

        }
        WritableNativeMap re = new WritableNativeMap();
        re.putArray("data", array);
        callback.invoke(re);
    }

    @ReactMethod
    public void setStarOrBlackContacts(ReadableMap map, String pkey, boolean isAdd, final Callback callback) {
        if (map.toHashMap().isEmpty()) {
            WritableNativeMap wnm = new WritableNativeMap();
            wnm.putBoolean("ok", true);
            callback.invoke(wnm);
            return;
        }
        UserConfigData data = new UserConfigData();
        data.setType(isAdd ? CacheDataType.set : CacheDataType.cancel);
        List<UserConfigData.Info> rl = new ArrayList<>();

        ReadableMapKeySetIterator keySet = map.keySetIterator();
        while (keySet.hasNextKey()) {
            ReadableMap item = map.getMap(keySet.nextKey());
            String xmppid = item.getString("XmppId");
            UserConfigData.Info info = new UserConfigData.Info();
            info.setKey(pkey);
            info.setSubkey(xmppid);
            info.setValue(isAdd ? String.valueOf(CacheDataType.Y) : String.valueOf(CacheDataType.N));
            rl.add(info);
        }
        data.setBatchProcess(rl);
        HttpUtil.setUserConfig(data, new ProtocolCallback.UnitCallback<NewRemoteConfig>() {
            @Override
            public void onCompleted(NewRemoteConfig newRemoteConfig) {
                ConnectionUtil.getInstance().refreshTheConfig(newRemoteConfig);
                WritableNativeMap map = new WritableNativeMap();
                map.putBoolean("ok", newRemoteConfig.isRet());
                callback.invoke(map);
            }

            @Override
            public void onFailure(String errMsg) {
                WritableNativeMap map = new WritableNativeMap();
                map.putBoolean("ok", false);
                callback.invoke(map);
            }
        });
    }

    @ReactMethod
    public void setStarOrblackContact(String xmppid, String pkey, boolean isAdd, final Callback callback) {
        if (TextUtils.isEmpty(xmppid)) {
            return;
        }
        UserConfigData data = new UserConfigData();
        data.setType(isAdd ? CacheDataType.set : CacheDataType.cancel);
        data.setValue(isAdd ? String.valueOf(CacheDataType.Y) : String.valueOf(CacheDataType.N));
        data.setIsdel(isAdd ? CacheDataType.N : CacheDataType.Y);
        data.setKey(pkey);
        data.setSubkey(xmppid);
        HttpUtil.setUserConfig(data, new ProtocolCallback.UnitCallback<NewRemoteConfig>() {
            @Override
            public void onCompleted(NewRemoteConfig newRemoteConfig) {
                ConnectionUtil.getInstance().refreshTheConfig(newRemoteConfig);
                WritableNativeMap map = new WritableNativeMap();
                map.putBoolean("ok", newRemoteConfig.isRet());
                callback.invoke(map);
            }

            @Override
            public void onFailure(String errMsg) {
                WritableNativeMap map = new WritableNativeMap();
                map.putBoolean("ok", false);
                callback.invoke(map);
            }
        });
    }

    @ReactMethod
    public void setWaterMark(boolean isOpen) {
        DataUtils.getInstance(CommonConfig.globalContext).putPreferences(CacheDataType.kWaterMark, isOpen);
    }

    @ReactMethod
    public void getWaterMark(Callback callback) {
        boolean isOpen = DataUtils.getInstance(CommonConfig.globalContext).getPreferences(CacheDataType.kWaterMark, true);
        callback.invoke(isOpen);
    }
    @ReactMethod
    public  void getworkWorldRemind(Callback callback){


//        UserConfigData userConfigData = new UserConfigData();
//        userConfigData.setSubkey(String.valueOf(CacheDataType.kCricleCamelNotify_Type));
//        userConfigData.setKey(CacheDataType.kCricleCamelNotify);
//        UserConfigData ucd = ConnectionUtil.getInstance().selectUserConfigValueForKey(userConfigData);
//
//        recentConvDataModel.selectRecentConvById(rc);
//        panelView.setReMind(rc.getRemind()>0);
        WritableNativeMap map = new WritableNativeMap();
        boolean isOpen = IMDatabaseManager.getInstance().SelectWorkWorldRemind();
//        if(ucd !=null){
//           isOpen = ucd.getValue().equals(CacheDataType.Y+"")? true:false;
//        }
        map.putBoolean("state", isOpen);
        callback.invoke(map);




    }
    @ReactMethod
    public void updateWorkWorldRemind(boolean isOpen, final Callback callback){





        String i = isOpen ? String.valueOf(CacheDataType.Y) : String.valueOf(CacheDataType.N);


//        UserConfigData data = new UserConfigData();
//        data.setType(CacheDataType.set );
//        data.setValue(isOpen ? String.valueOf(CacheDataType.Y) : String.valueOf(CacheDataType.N));
//        data.setIsdel( CacheDataType.N );
//        data.setKey(CacheDataType.kCricleCamelNotify);
//        data.setSubkey(String.valueOf(CacheDataType.kCricleCamelNotify_Type));
//        HttpUtil.setUserConfig(data, new ProtocolCallback.UnitCallback<NewRemoteConfig>() {
//            @Override
//            public void onCompleted(NewRemoteConfig newRemoteConfig) {
//                ConnectionUtil.getInstance().refreshTheConfig(newRemoteConfig);
//                WritableNativeMap map = new WritableNativeMap();
//                map.putBoolean("ok", newRemoteConfig.isRet());
//                callback.invoke(map);
//            }
//
//            @Override
//            public void onFailure(String errMsg) {
//                WritableNativeMap map = new WritableNativeMap();
//                map.putBoolean("ok", false);
//                callback.invoke(map);
//            }
//        });


        HttpUtil.setWorkWorldRemind(Integer.parseInt(i), new ProtocolCallback.UnitCallback<SetWorkWorldRemindResponse>() {
            @Override
            public void onCompleted(SetWorkWorldRemindResponse setWorkWorldRemindResponse) {
                WritableNativeMap map = new WritableNativeMap();
                map.putBoolean("ok", setWorkWorldRemindResponse.isRet());
                callback.invoke(map);
            }

            @Override
            public void onFailure(String errMsg) {
                WritableNativeMap map = new WritableNativeMap();
                map.putBoolean("ok", false);
                callback.invoke(map);
            }
        });


    }

    /**
     * 是否是星标联系人 黑名单
     *
     * @param xmppid
     * @param pkey
     * @param callback
     */
    @ReactMethod
    public void isStarOrBlackContact(String xmppid, String pkey, Callback callback) {
        boolean flag = IMDatabaseManager.getInstance().isStarContact(xmppid, pkey);
        WritableNativeMap map = new WritableNativeMap();
        map.putBoolean("ok", flag);
        callback.invoke(map);
    }


    /**
     * 退出群组
     *
     * @param groupId
     * @param callback
     */
    @ReactMethod
    public void quitGroup(String groupId, Callback callback) {
//        connectionUtil.leaveGroup(key);

        ConnectionUtil.getInstance().leaveGroup(groupId);
        WritableNativeMap map = new WritableNativeMap();
        map.putBoolean("ok", true);
        callback.invoke(map);
    }


    /**
     * 销毁群组
     *
     * @param groupId
     * @param callback
     */
    @ReactMethod
    public void destructionGroup(String groupId, Callback callback) {
        ConnectionUtil.getInstance().destroyGroup(groupId);
        WritableNativeMap map = new WritableNativeMap();
        map.putBoolean("ok", true);
        callback.invoke(map);
    }

    /**
     * 相册上传图像
     */
    @ReactMethod
    public void updateMyPhotoFromImagePicker() {
//        PermissionDispatcher.requestPermissionWithCheck(CommonConfig.globalContext, new int[]{PermissionDispatcher.REQUEST_WRITE_EXTERNAL_STORAGE,
//                        PermissionDispatcher.REQUEST_READ_EXTERNAL_STORAGE},
//                QimRNBModule.this, REQUEST_GRANT_LOCAL);
        NativeApi.openPictureSelector();
    }

    /**
     * 拍照上传
     */
    @ReactMethod
    public void takePhoto() {
        NativeApi.openCamerSelecter();
    }

    /************************** Search start************************/

    @ReactMethod
    public void searchLocalMessageByKeyword(String keyword, String xmppid, String realjid, Callback callback) {
        try {
            WritableNativeArray writableNativeArray = new WritableNativeArray();
            JSONArray list = IMDatabaseManager.getInstance().selectMessageByKeyWord(keyword, xmppid, realjid);
            Map<String, List<JSONObject>> map = new LinkedHashMap<>();
            for (int i = 0; i < list.length(); i++) {
                JSONObject imMessage = list.getJSONObject(i);
                String timeStr = getTimeStr(imMessage.getLong("timeLong"));
                List<JSONObject> oldList = map.get(timeStr);
                if (oldList != null) {
                    oldList.add(imMessage);
                } else {
                    List<JSONObject> newList = new ArrayList<>();
                    newList.add(imMessage);
                    map.put(timeStr, newList);

                }
            }

            WritableNativeMap cMap = new WritableNativeMap();
            WritableNativeArray array = new WritableNativeArray();
            for (Map.Entry<String, List<JSONObject>> entry : map.entrySet()) {
                WritableNativeArray itemArray = new WritableNativeArray();
                for (int i = 0; i < entry.getValue().size(); i++) {
                    JSONObject jsonObject = entry.getValue().get(i);
                    WritableNativeMap writableNativeMap = new WritableNativeMap();
                    writableNativeMap.putString("time", jsonObject.optString("time"));
                    writableNativeMap.putString("timeLong", jsonObject.optString("timeLong"));
                    writableNativeMap.putString("content", jsonObject.optString("content"));
                    writableNativeMap.putString("nickName", jsonObject.optString("nickName"));
                    writableNativeMap.putString("headerUrl", jsonObject.optString("headerUrl"));
                    writableNativeMap.putString("msgId", jsonObject.optString("msgId"));
                    writableNativeMap.putString("from", jsonObject.optString("from"));
                    itemArray.pushMap(writableNativeMap);
                }
                WritableNativeMap itemMap = new WritableNativeMap();
                itemMap.putArray("data", itemArray);
                itemMap.putString("key", entry.getKey());
                array.pushMap(itemMap);

            }
            cMap.putBoolean("ok", true);
            cMap.putArray("data", array);
            callback.invoke(cMap);


        } catch (Exception e) {
            Logger.i("会话内搜索:" + e.getMessage());
        }

    }

    /**
     * 搜索远程文本数据
     *
     * @param params
     * @param callback
     */
    @ReactMethod
    public void searchRemoteMessageByKeyword(ReadableMap params, Callback callback) {
        String keyword = params.getString("search");
        //暂时测试数据 搜索我自己
        keyword = "我";
        String xmppid = params.getString("xmppid");
        String realjid = params.getString("realjid");
        String time = params.getString("time");
        String chatType = params.getString("chatType");
        try {
//            WritableNativeArray writableNativeArray = new WritableNativeArray();
            JSONArray list = IMDatabaseManager.getInstance().selectMessageByKeyWord(keyword, xmppid, realjid);
            Map<String, List<JSONObject>> map = new LinkedHashMap<>();
            for (int i = 0; i < list.length(); i++) {
                JSONObject imMessage = list.getJSONObject(i);
                String timeStr = getTimeStr(imMessage.getLong("timeLong"));
                List<JSONObject> oldList = map.get(timeStr);
                if (oldList != null) {
                    oldList.add(imMessage);
                } else {
                    List<JSONObject> newList = new ArrayList<>();
                    newList.add(imMessage);
                    map.put(timeStr, newList);

                }
            }

            WritableNativeMap cMap = new WritableNativeMap();
            WritableNativeArray array = new WritableNativeArray();
            for (Map.Entry<String, List<JSONObject>> entry : map.entrySet()) {
                WritableNativeArray itemArray = new WritableNativeArray();
                for (int i = 0; i < entry.getValue().size(); i++) {
                    JSONObject jsonObject = entry.getValue().get(i);
                    WritableNativeMap writableNativeMap = new WritableNativeMap();
                    writableNativeMap.putString("time", jsonObject.optString("time"));
                    writableNativeMap.putString("timeLong", jsonObject.optString("timeLong"));
                    writableNativeMap.putString("content", jsonObject.optString("content"));
                    writableNativeMap.putString("nickName", jsonObject.optString("nickName"));
                    writableNativeMap.putString("headerUrl", jsonObject.optString("headerUrl"));
                    writableNativeMap.putString("msgId", jsonObject.optString("msgId"));
                    writableNativeMap.putString("from", jsonObject.optString("from"));
                    itemArray.pushMap(writableNativeMap);
                }
                WritableNativeMap itemMap = new WritableNativeMap();
                itemMap.putArray("data", itemArray);
                itemMap.putString("key", entry.getKey());
                array.pushMap(itemMap);

            }
            cMap.putBoolean("ok", true);
            cMap.putArray("data", array);
            callback.invoke(cMap);


        } catch (Exception e) {
            Logger.i("会话内搜索:" + e.getMessage());
        }
    }

    @ReactMethod
    public void openChatForLocalSearch(String xmppid, String realjid, String chatType, String time) {
        NativeApi.openChatForLocalSearch(xmppid, realjid, chatType, time);
    }

    @ReactMethod
    public void openLocalSearchImage(String xmppid, String realjid) {
        NativeApi.openLocalSearchImage(xmppid, realjid);
    }

    @ReactMethod
    public void searchFilesByXmppId(String xmppid, Callback callback) {
        WritableNativeArray writableNativeArray = new WritableNativeArray();
        JSONArray jsonArray = ConnectionUtil.getInstance().searchFilesMsgByXmppid(xmppid);
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                WritableNativeMap writableNativeMap = new WritableNativeMap();
                writableNativeMap.putString("from", jsonObject.optString("from"));
                writableNativeMap.putString("content", jsonObject.optString("content"));
                writableNativeMap.putString("time", jsonObject.optString("time"));
                writableNativeMap.putString("name", jsonObject.optString("name"));
                writableNativeMap.putString("headerSrc", jsonObject.optString("headerSrc"));
                writableNativeArray.pushMap(writableNativeMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        if (callback != null) {
            WritableNativeMap map = new WritableNativeMap();
            map.putArray("files", writableNativeArray);
            callback.invoke(map);
        }
    }

    /************************** Search end************************/


    public void updateMyPersonalInfo(final String selGravatarPath) {
//        if (gravatarView == null || TextUtils.isEmpty(gravatarView.getGravatarPath())) {
//            personalInfoView.setUpdateResult(false);
//        }

        final WritableNativeMap failure = new WritableNativeMap();
        failure.putBoolean("ok", false);
        sendEvent("imageUpdateStart", failure);
        if (TextUtils.isEmpty(selGravatarPath)) {
            sendEvent("imageUpdateEnd", failure);
            return;
        }
        final UploadImageRequest request = new UploadImageRequest();
        request.FileType = UploadImageRequest.LOGO;
        request.id = QtalkStringUtils.parseLocalpart(CurrentPreference.getInstance().getPreferenceUserId()) + ".gravatar";
        request.filePath = selGravatarPath;
        request.requestComplete = new IUploadRequestComplete() {
            @Override
            public void onRequestComplete(String id, final UploadImageResult result) {

                if (result != null && !TextUtils.isEmpty(result.httpUrl) && !result.httpUrl.contains("error")) {
                    final List<SetVCardData> datas = generateSetData(result.httpUrl, CurrentPreference.getInstance().getPreferenceUserId());
                    VCardAPI.setVCardInfo(datas, new ProtocolCallback.UnitCallback<SetVCardResult>() {
                        @Override
                        public void onCompleted(SetVCardResult setVCardResult) {
                            if (setVCardResult != null && !ListUtil.isEmpty(setVCardResult.data)) {
                                SetVCardResult.SetVCardItem data = setVCardResult.data.get(0);
                                if (data == null || TextUtils.isEmpty(data.version) ||
                                        data.version.equals("-1")) {

                                    sendEvent("imageUpdateEnd", failure);
                                    //失败
//                                    personalInfoView.setUpdateResult(false);
//                                    Toast.makeText(CommonConfig.globalContext,"更新失败",Toast.LENGTH_SHORT);
                                } else {
//                                    ProfileUtils.updateGVer(result.httpUrl,
//                                            data.version, null, CurrentPreference.getInstance().getPreferenceUserId());
                                    //此数据为更新成功后的头像地址
                                    String cacheUrl = QtalkStringUtils.
                                            getGravatar(result.httpUrl, true);
                                    File targetFile = MyDiskCache.getSmallFile(cacheUrl);
                                    File file = new File(selGravatarPath);
                                    file.renameTo(targetFile);
                                    InternDatas.JidToUrl.put(CurrentPreference.getInstance().getPreferenceUserId(), cacheUrl);
                                    WritableNativeMap success = new WritableNativeMap();
                                    ConnectionUtil.getInstance().updateUserImage(CurrentPreference.getInstance().getPreferenceUserId(), cacheUrl);
                                    success.putBoolean("ok", true);
                                    success.putString("headerUrl", cacheUrl);
                                    sendEvent("imageUpdateEnd", success);
//                                    personalInfoView.setUpdateResult(true);
//                                    Toast.makeText(CommonConfig.globalContext,"更新成功",Toast.LENGTH_SHORT);
                                    //成功
                                }
                            }
                        }

                        @Override
                        public void onFailure(String errMsg) {
//                            Toast.makeText(CommonConfig.globalContext,"更新失败",Toast.LENGTH_SHORT);
                            //失败
                            sendEvent("imageUpdateEnd", failure);
//                            personalInfoView.setUpdateResult(false);
                        }
                    });
                } else {
//                    Toast.makeText(CommonConfig.globalContext,"更新失败",Toast.LENGTH_SHORT);
                    //失败
                    sendEvent("imageUpdateEnd", failure);
//                    personalInfoView.setUpdateResult(false);
                }
            }

            @Override
            public void onError(String msg) {
                sendEvent("imageUpdateEnd", failure);
            }
        };

        CommonUploader.getInstance().setUploadImageRequest(request);
    }

    private static List<SetVCardData> generateSetData(String url, String userId) {
        SetVCardData vCardData = new SetVCardData();
        vCardData.url = url;
        vCardData.user = QtalkStringUtils.parseLocalpart(userId);
        vCardData.domain = QtalkNavicationService.getInstance().getXmppdomain();
        List<SetVCardData> list = new ArrayList<SetVCardData>(1);
        list.add(vCardData);
        return list;
    }

    /**
     * 格式化行程对象为rn map对象
     *
     * @param bean
     * @return
     */

    public WritableNativeMap getRNDataByTrip(CalendarTrip.DataBean.TripsBean bean) {
        WritableNativeMap map = new WritableNativeMap();
        map.putString("beginTime", bean.getBeginTime());
        map.putString("endTime", bean.getEndTime());
        map.putString("scheduleTime", bean.getScheduleTime());
        map.putString("tripType", bean.getTripType());
        map.putString("appointment", bean.getAppointment());
        map.putString("tripDate", bean.getTripDate());
        map.putString("tripId", bean.getTripId());
        map.putString("tripIntr", bean.getTripIntr());
        map.putString("tripInviter", bean.getTripInviter());
        map.putString("tripLocale", bean.getTripLocale());
        map.putString("tripLocaleNumber", bean.getTripLocaleNumber());
        map.putString("tripName", bean.getTripName());
        map.putString("tripRemark", bean.getTripRemark());
        map.putString("tripRoomNumber", bean.getTripRoomNumber());
        map.putString("tripRoom", bean.getTripRoom());
        WritableNativeArray members = new WritableNativeArray();
        for (int i = 0; i < bean.getMemberList().size(); i++) {
            CalendarTrip.DataBean.TripsBean.MemberListBean mb = bean.getMemberList().get(i);

            WritableNativeMap member = new WritableNativeMap();
            member.putString("memberId", mb.getMemberId());
            member.putString("memberState", mb.getMemberState());
            member.putString("memberStateDescribe", mb.getMemberStateDescribe());
            Nick nick = ConnectionUtil.getInstance().getNickById(mb.getMemberId());
            member.putString("memberName", nick.getName());
            member.putString("headerUrl", nick.getHeaderSrc());
            members.pushMap(member);
        }
        map.putArray("memberList", members);


        return map;
    }

    /**
     * 创建新的议程
     *
     * @param params
     * @param callback
     */
    @ReactMethod
    public void createTrip(ReadableMap params, final Callback callback) {
        CalendarTrip.DataBean.TripsBean bean = new CalendarTrip.DataBean.TripsBean();
        if (params.hasKey("tripId")) {
            bean.setTripId(params.getString("tripId"));
        }
        bean.setOperateType(params.getString("operateType"));
        bean.setTripRoom(params.hasKey("tripRoom") ? params.getString("tripRoom") : "");
        bean.setBeginTime(params.getString("beginTime"));
        bean.setTripIntr(params.getString("tripIntr"));
        bean.setScheduleTime(params.getString("scheduleTime"));
        bean.setTripRoomNumber(params.hasKey("tripRoomNumber") ? params.getInt("tripRoomNumber") + "" : "");
        bean.setTripType(params.getInt("tripType") + "");
        bean.setAppointment(params.getString("appointment"));
        bean.setEndTime(params.getString("endTime"));
        bean.setTripDate(params.getString("tripDate"));
        bean.setTripLocale(params.hasKey("tripLocale") ? params.getString("tripLocale") : "");
        bean.setTripLocaleNumber(params.hasKey("tripLocaleNumber") ? params.getInt("tripLocaleNumber") + "" : "");
        bean.setTripName(params.getString("tripName"));
        bean.setTripInviter(CurrentPreference.getInstance().getPreferenceUserId());
        List<CalendarTrip.DataBean.TripsBean.MemberListBean> memberListBeanList = new ArrayList<>();
        boolean checkMe = false;
        for (int i = 0; i < params.getArray("memberList").size(); i++) {
            CalendarTrip.DataBean.TripsBean.MemberListBean member = new CalendarTrip.DataBean.TripsBean.MemberListBean();
            String memberId = params.getArray("memberList").getMap(i).getString("memberId");
            member.setMemberId(memberId);
            memberListBeanList.add(member);
            if (memberId.equals(CurrentPreference.getInstance().getPreferenceUserId())) {
                checkMe = true;
            }

        }
        if (!checkMe) {
            CalendarTrip.DataBean.TripsBean.MemberListBean my = new CalendarTrip.DataBean.TripsBean.MemberListBean();
            my.setMemberId(CurrentPreference.getInstance().getPreferenceUserId());
            memberListBeanList.add(my);
        }

        bean.setMemberList(memberListBeanList);
        bean.setUpdateTime(IMDatabaseManager.getInstance().selectUserTripVersion() + "");
        final WritableNativeMap map = new WritableNativeMap();
        HttpUtil.createTrip(bean, new ProtocolCallback.UnitCallback<CalendarTrip>() {
            @Override
            public void onCompleted(CalendarTrip calendarTrip) {
                IMDatabaseManager.getInstance().InsertTrip(calendarTrip);
                IMDatabaseManager.getInstance().insertUserTripVersion(Long.parseLong(calendarTrip.getData().getUpdateTime()));
                CalendarSynchronousUtil.bulkTrip(calendarTrip);
                map.putBoolean("ok", true);
                if (!TextUtils.isEmpty(calendarTrip.getErrmsg())) {
                    map.putString("errMsg", calendarTrip.getErrmsg());
                }
                callback.invoke(map);
            }

            @Override
            public void onFailure(String errMsg) {
                map.putBoolean("ok", false);
                map.putString("errMsg", errMsg);
                callback.invoke(map);
            }
        });
    }


    /**
     * 查询用户的行车根据 整月查询
     *
     * @param params
     * @param callback
     */
    @ReactMethod
    public void selectUserTripByDate(ReadableMap params, Callback callback) {
        String date = params.getString("showDate");
        List<CalendarTrip.DataBean.TripsBean> list = IMDatabaseManager.getInstance().SelectTripByYearMonth(date);
        Collections.sort(list, new Comparator<CalendarTrip.DataBean.TripsBean>() {
            @Override
            public int compare(CalendarTrip.DataBean.TripsBean lhs, CalendarTrip.DataBean.TripsBean rhs) {
                try {
                    if (DateUtil.string2Time(lhs.getBeginTime()).getTime() > DateUtil.string2Time(rhs.getBeginTime()).getTime()) {
                        return 1;
                    }
                    if (DateUtil.string2Time(lhs.getBeginTime()).getTime() < DateUtil.string2Time(rhs.getBeginTime()).getTime()) {
                        return -1;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
        WritableNativeMap map = new WritableNativeMap();
        WritableNativeMap dataMap = new WritableNativeMap();
        WritableNativeMap.setUseNativeAccessor(true);
        WritableNativeArray.setUseNativeAccessor(true);
        Map<String, List<WritableNativeMap>> localMap = new HashMap<>();

//        map.putBoolean("ok", true);

        for (int i = 0; i < list.size(); i++) {
            CalendarTrip.DataBean.TripsBean bean = list.get(i);
            if (!localMap.containsKey(bean.getTripDate())) {
//                WritableNativeArray newArray = new WritableNativeArray();
                List<WritableNativeMap> newArray = new ArrayList<>();
                newArray.add(getRNDataByTrip(bean));
                localMap.put(bean.getTripDate(), newArray);

            } else {
                List<WritableNativeMap> lastArray = localMap.get(bean.getTripDate());
                lastArray.add(getRNDataByTrip(bean));
                localMap.put(bean.getTripDate(), lastArray);
            }

        }

        for (Map.Entry<String, List<WritableNativeMap>> entry : localMap.entrySet()) {
            WritableNativeArray array = new WritableNativeArray();
            for (int i = 0; i < entry.getValue().size(); i++) {
                array.pushMap(entry.getValue().get(i));
            }
            dataMap.putArray(entry.getKey(), array);
        }


        map.putBoolean("ok", true);
        map.putMap("data", dataMap);
        callback.invoke(map);

    }

    /**
     * 根据时间区域查询可用会议室
     *
     * @param params
     * @param callback
     */
    @ReactMethod
    public void getTripAreaAvailableRoom(ReadableMap params, final Callback callback) {
        final AvailableRoomRequest arr = new AvailableRoomRequest();
        arr.setDate(params.getString("date"));
        arr.setAreaId(params.getInt("areaId"));
        arr.setStartTime(params.getString("startTime"));
        arr.setEndTime(params.getString("endTime"));

        HttpUtil.getTripAreaAvailableRoom(arr, new ProtocolCallback.UnitCallback<AvailableRoomResponse>() {
            @Override
            public void onCompleted(AvailableRoomResponse availableRoomResponse) {
                WritableNativeMap map = new WritableNativeMap();
                WritableNativeArray array = new WritableNativeArray();
                for (int i = 0; i < availableRoomResponse.getData().size(); i++) {

                    WritableNativeMap item = new WritableNativeMap();
                    AvailableRoomResponse.DataBean data = availableRoomResponse.getData().get(i);
                    if (data.getCanUse() == 0) {
                        item.putInt("AddressNumber", arr.getAreaId());
                        item.putString("RoomName", data.getRoomName());
                        item.putInt("RoomNumber", data.getRoomId());
                        String str = data.getDescription();
                        if (str.length() > 15) {
                            str = str.substring(0, 15) + "...";
                        }
                        item.putString("RoomDetails", str);
                        item.putInt("RoomCapacity", data.getCapacity());
                        array.pushMap(item);
                    }

                }
                map.putBoolean("ok", true);
                map.putArray("roomList", array);
                callback.invoke(map);
            }

            @Override
            public void onFailure(String errMsg) {
                WritableNativeMap noMap = new WritableNativeMap();
                noMap.putBoolean("ok", false);
                noMap.putString("errMsg", errMsg);
                callback.invoke(noMap);
            }
        });


    }


    /**
     * 获取日历功能城市列表
     *
     * @param callback
     */
    @ReactMethod
    public void getTripCity(Callback callback) {

        List<CityLocal.DataBean> list = IMDatabaseManager.getInstance().getCityList();
        WritableNativeMap map = new WritableNativeMap();
        WritableNativeArray array = new WritableNativeArray();
        for (int i = 0; i < list.size(); i++) {
            WritableNativeMap item = new WritableNativeMap();
            item.putString("CityName", list.get(i).getCityName());
            item.putInt("CityId", list.get(i).getId());
            array.pushMap(item);
        }
        map.putBoolean("ok", true);
        map.putArray("cityList", array);
        callback.invoke(map);

    }


    /**
     * 获取日历功能区域列表
     *
     * @param callback
     */
    @ReactMethod
    public void getTripArea(Callback callback) {
        List<AreaLocal.DataBean.ListBean> list = IMDatabaseManager.getInstance().getAreaList();
        WritableNativeMap map = new WritableNativeMap();
        WritableNativeArray array = new WritableNativeArray();
        for (int i = 0; i < list.size(); i++) {
            WritableNativeMap item = new WritableNativeMap();
            item.putString("AddressName", list.get(i).getAreaName());
            item.putInt("AddressNumber", list.get(i).getAreaID());
            item.putString("rStartTime", list.get(i).getMorningStarts());
            item.putString("rEndTime", list.get(i).getEveningEnds());
            array.pushMap(item);
        }
        map.putBoolean("ok", true);
        map.putArray("areaList", array);
        callback.invoke(map);


    }

    @ReactMethod
    public void getHotlineSeats(String customerName,String hotlineName,final Callback callback){
        HttpUtil.getHotlineSeats(customerName,hotlineName, new HttpRequestCallback() {
            @Override
            public void onComplete(InputStream response) {
                WritableNativeMap map = new WritableNativeMap();
                try{
                    String resultString = Protocol.parseStream(response);

                    if(!TextUtils.isEmpty(resultString)){
                        JSONObject jsonObject = new JSONObject(resultString);
                        map.putBoolean("ret",jsonObject.optBoolean("ret"));
                        map.putString("errmsg",jsonObject.optString("errmsg"));
                        JSONArray array = jsonObject.optJSONArray("data");
                        int size = array == null ? 0: array.length();
                        WritableNativeArray datas = new WritableNativeArray();
                        for(int i = 0;i<size;i++){
                            JSONObject item = array.getJSONObject(i);
                            WritableNativeMap data = new WritableNativeMap();
                            data.putString("userId",item.optString("userId"));
                            data.putString("userName",item.optString("userName"));
                            datas.pushMap(data);
                        }
                        map.putArray("data",datas);
                        callback.invoke(map);
                    }
                }catch (Exception e){

                }
            }

            @Override
            public void onFailure(Exception e) {
                Logger.e(e.getLocalizedMessage());
            }
        });
    }

    @ReactMethod
    public void transArtificial(String customerName,String hotlineName,String newCsrName,String reason,final Callback callback){
        HttpUtil.transArtificial(customerName,hotlineName,newCsrName,reason, new HttpRequestCallback() {
            @Override
            public void onComplete(InputStream response) {
                WritableNativeMap map = new WritableNativeMap();
                try{
                    String resultString = Protocol.parseStream(response);

                    if(!TextUtils.isEmpty(resultString)){
                        JSONObject jsonObject = new JSONObject(resultString);
                        map.putBoolean("ret",jsonObject.optBoolean("ret"));
                        map.putString("errmsg",jsonObject.optString("errmsg"));
                        callback.invoke(map);
                    }
                }catch (Exception e){

                }
            }

            @Override
            public void onFailure(Exception e) {
                Logger.e(e.getLocalizedMessage());
            }
        });
    }

    /**
     * 获取跨域列表
     * @param callback
     */
    @ReactMethod
    public void getDomainList(final Callback callback){
        HttpUtil.getDomainList(new HttpRequestCallback() {
            @Override
            public void onComplete(InputStream response) {
                WritableNativeMap map = new WritableNativeMap();
                try{
                    String resultString = Protocol.parseStream(response);

                    if(!TextUtils.isEmpty(resultString)){
                        JSONObject jsonObject = new JSONObject(resultString);
                        map.putBoolean("ret",jsonObject.optBoolean("ret"));
                        map.putString("errmsg",jsonObject.optString("errmsg"));
                        JSONArray array = jsonObject.optJSONObject("data").optJSONArray("domains");
                        int size = array == null ? 0: array.length();
                        WritableNativeArray domains = new WritableNativeArray();
                        for(int i = 0;i<size;i++){
                            JSONObject item = array.getJSONObject(i);
                            WritableNativeMap domain = new WritableNativeMap();
                            domain.putString("name",item.optString("name"));
                            domain.putString("description",item.optString("description"));
                            domain.putString("id",item.optString("id"));
                            domain.putString("url",item.optString("url"));
                            domains.pushMap(domain);
                        }
                        map.putArray("domains",domains);
                        callback.invoke(map);
                    }
                }catch (Exception e){

                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    @ReactMethod
    public static void searchDomainUser(String url,String id,String key,int offset,int limit,final Callback callback){
        Map<String,String> params = new HashMap<>();
        params.put("id",id);
        params.put("key",key);
        params.put("offset",String.valueOf(offset));
        params.put("limit",String.valueOf(limit));
        HttpUtil.searchDomainUser(url, params, new HttpRequestCallback() {
            @Override
            public void onComplete(InputStream response) {
                try{
                    String resultString = Protocol.parseStream(response);
                    WritableNativeMap map = new WritableNativeMap();
                    if(!TextUtils.isEmpty(resultString)){
                        JSONObject jsonObject = new JSONObject(resultString);
                        map.putBoolean("ret",jsonObject.optBoolean("ret"));
                        map.putString("errmsg",jsonObject.optString("errmsg"));
                        JSONArray array = jsonObject.optJSONObject("data").optJSONArray("users");
                        int size = array == null ? 0: array.length();
                        WritableNativeArray users = new WritableNativeArray();
                        for(int i = 0;i<size;i++){
                            JSONObject item = array.getJSONObject(i);
                            WritableNativeMap user = new WritableNativeMap();
                            user.putString("icon",item.optString("icon"));
                            user.putString("label",item.optString("label"));
                            if(item.has("content"))
                                user.putString("content",item.optString("content"));
                            user.putString("uri",item.optString("uri"));
                            if(item.has("name"))
                                user.putString("name",item.optString("name"));
                            users.pushMap(user);
                        }
                        map.putArray("users",users);
                    }
                    callback.invoke(map);
                }catch (Exception e){

                }
            }

            @Override
            public void onFailure(Exception e) {
                Logger.i("searchDomainUser:" + e.getLocalizedMessage());
            }
        });
    }

    /**
     * 获取日历功能区域列表
     *
     * @param callback
     */
    @ReactMethod
    public void getNewTripArea(ReadableMap params, final Callback callback) {
        int cityId = params.getInt("cityId");

        HttpUtil.getNewArea(cityId, new ProtocolCallback.UnitCallback<AreaLocal>() {
            @Override
            public void onCompleted(AreaLocal areaLocal) {
                WritableNativeMap map = new WritableNativeMap();
                WritableNativeArray array = new WritableNativeArray();
                for (int i = 0; i < areaLocal.getData().getList().size(); i++) {
                    WritableNativeMap item = new WritableNativeMap();
                    item.putString("AddressName", areaLocal.getData().getList().get(i).getAreaName());
                    item.putInt("AddressNumber", areaLocal.getData().getList().get(i).getAreaID());
                    item.putString("rStartTime", areaLocal.getData().getList().get(i).getMorningStarts());
                    item.putString("rEndTime", areaLocal.getData().getList().get(i).getEveningEnds());
                    array.pushMap(item);
                }
                map.putBoolean("ok", true);
                map.putArray("areaList", array);
                callback.invoke(map);
            }

            @Override
            public void onFailure(String errMsg) {

            }
        });


//        List<AreaLocal.DataBean.ListBean> list = IMDatabaseManager.getInstance().getAreaList();
//        WritableNativeMap map = new WritableNativeMap();
//        WritableNativeArray array = new WritableNativeArray();
//        for (int i = 0; i < list.size(); i++) {
//            WritableNativeMap item = new WritableNativeMap();
//            item.putString("AddressName", list.get(i).getAreaName());
//            item.putInt("AddressNumber", list.get(i).getAreaID());
//            item.putString("rStartTime", list.get(i).getMorningStarts());
//            item.putString("rEndTime", list.get(i).getEveningEnds());
//            array.pushMap(item);
//        }
//        map.putBoolean("ok", true);
//        map.putArray("areaList", array);
//        callback.invoke(map);

    }

    /**
     * 查询本地会话内link
     *
     * @param params
     * @param callback
     */
    @ReactMethod
    public void searchLocalLink(ReadableMap params, Callback callback) {
        try {
            String xmppId = params.getString("xmppid");
            String realjid = params.getString("realjid");
            String chatType = params.getString("chatType");
            String searchText = "";
            if (params.hasKey("searchText")) {
                searchText = params.getString("searchText");
            }

            Map<String, List<JSONObject>> map = new LinkedHashMap<>();
            JSONArray list = IMDatabaseManager.getInstance().searchLocalLinkMessageByXmppId(xmppId, realjid);
            for (int i = 0; i < list.length(); i++) {
                JSONObject imMessage = list.getJSONObject(i);
                JSONObject file = new JSONObject(imMessage.optString("ext"));
                String fileName = file.optString("title").toLowerCase();
                String nickName = imMessage.optString("nickName");
                if (!TextUtils.isEmpty(searchText)) {
                    if (!fileName.contains(searchText) && !nickName.contains(searchText)) {
                        continue;
                    }
                }
                String timeStr = getTimeStr(imMessage.getLong("timeLong"));
                List<JSONObject> oldList = map.get(timeStr);
                if (oldList != null) {
                    oldList.add(imMessage);
                } else {
                    List<JSONObject> newList = new ArrayList<>();
                    newList.add(imMessage);
                    map.put(timeStr, newList);

                }
            }

            WritableNativeMap cMap = new WritableNativeMap();
            WritableNativeArray array = new WritableNativeArray();
            for (Map.Entry<String, List<JSONObject>> entry : map.entrySet()) {
                WritableNativeArray itemArray = new WritableNativeArray();
                for (int i = 0; i < entry.getValue().size(); i++) {
                    JSONObject imMessage = entry.getValue().get(i);
                    WritableNativeMap item = new WritableNativeMap();
                    item.putString("linkDate", imMessage.optString("time"));
                    item.putString("timeLong", imMessage.optString("timeLong"));
                    JSONObject link = new JSONObject(imMessage.optString("ext"));
                    item.putString("linkTitle", link.optString("title"));
                    item.putString("linkUrl", link.optString("linkurl"));
                    String linkIcon = link.optString("img");
                    if (TextUtils.isEmpty(linkIcon)) {
                        linkIcon = defaultUserImage;
                    }
                    item.putString("linkIcon", linkIcon);

                    item.putString("content", imMessage.optString("ext"));
                    item.putString("nickName", imMessage.optString("nickName"));
                    item.putString("headerUrl", imMessage.optString("headerUrl"));
                    item.putString("msgId", imMessage.optString("msgId"));
                    item.putString("from", imMessage.optString("from"));
                    itemArray.pushMap(item);
                }
                WritableNativeMap itemMap = new WritableNativeMap();
                itemMap.putArray("data", itemArray);
                itemMap.putString("key", entry.getKey());
                array.pushMap(itemMap);

            }
            cMap.putBoolean("ok", true);
            cMap.putArray("data", array);
            callback.invoke(cMap);

        } catch (Exception e) {
            Logger.i("查询本地link出错:" + e.getMessage());
        }
    }

    /**
     * 查询本地会话内文件
     *
     * @param params
     * @param callback
     */
    @ReactMethod
    public void searchLocalFile(ReadableMap params, Callback callback) {
        try {
            String xmppId = params.getString("xmppid");
            String realjid = params.getString("realjid");
            String chatType = params.getString("chatType");
            String searchText = "";
            if (params.hasKey("searchText")) {
                searchText = params.getString("searchText");
            }

            Map<String, List<JSONObject>> map = new LinkedHashMap<>();
            JSONArray list = IMDatabaseManager.getInstance().searchLocalFileMessageByXmppId(xmppId, realjid);
            for (int i = 0; i < list.length(); i++) {
                JSONObject imMessage = list.getJSONObject(i);
                JSONObject file = new JSONObject(imMessage.optString("ext"));
                String fileName = file.optString("FileName").toLowerCase();
                String nickName = imMessage.optString("nickName");
                if (!TextUtils.isEmpty(searchText)) {
                    if (!fileName.contains(searchText) && !nickName.contains(searchText)) {
                        continue;
                    }
                }
                String timeStr = getTimeStr(imMessage.getLong("timeLong"));
                List<JSONObject> oldList = map.get(timeStr);
                if (oldList != null) {
                    oldList.add(imMessage);
                } else {
                    List<JSONObject> newList = new ArrayList<>();
                    newList.add(imMessage);
                    map.put(timeStr, newList);

                }
            }

            WritableNativeMap cMap = new WritableNativeMap();
            WritableNativeArray array = new WritableNativeArray();
            for (Map.Entry<String, List<JSONObject>> entry : map.entrySet()) {
                WritableNativeArray itemArray = new WritableNativeArray();
                for (int i = 0; i < entry.getValue().size(); i++) {
                    JSONObject imMessage = entry.getValue().get(i);
                    WritableNativeMap item = new WritableNativeMap();
                    item.putString("time", imMessage.optString("time"));
                    item.putString("timeLong", imMessage.optString("timeLong"));
                    JSONObject file = new JSONObject(imMessage.optString("ext"));
                    item.putString("fileId", file.optString("FILEID"));
                    String fileName = file.optString("FileName").toLowerCase();

                    String fileType = "file";
                    if (fileName.endsWith("docx") || fileName.endsWith("doc")) {
                        fileType = "word";
                    } else if (fileName.endsWith("jpg") || fileName.endsWith("jpeg") || fileName.endsWith("gif") || fileName.endsWith("png")) {
                        fileType = "image";
                    } else if (fileName.endsWith("xlsx")) {
                        fileType = "excel";
                    } else if (fileName.endsWith("pptx") || fileName.endsWith("ppt")) {
                        fileType = "powerPoint";
                    } else if (fileName.endsWith("pdf")) {
                        fileType = "pdf";
                    } else if (fileName.endsWith("apk")) {
                        fileType = "apk";
                    } else if (fileName.endsWith("txt")) {
                        fileType = "txt";
                    } else if (fileName.endsWith("zip")) {
                        fileType = "zip";
                    }
                    item.putString("fileType", fileType);
                    item.putString("fileName", fileName);
                    item.putString("fileSize", file.optString("FileSize"));
                    String url = file.optString("HttpUrl");
                    if (url.startsWith("http")) {
                        item.putString("fileUrl", file.optString("HttpUrl"));
                    } else {
                        item.putString("fileUrl", QtalkNavicationService.getInstance().getInnerFiltHttpHost() + "/" + file.optString("HttpUrl"));
                    }

                    item.putString("content", imMessage.optString("ext"));
                    item.putString("nickName", imMessage.optString("nickName"));
                    item.putString("headerUrl", imMessage.optString("headerUrl"));
                    item.putString("msgId", imMessage.optString("msgId"));
                    item.putString("from", imMessage.optString("from"));
                    itemArray.pushMap(item);
                }
                WritableNativeMap itemMap = new WritableNativeMap();
                itemMap.putArray("data", itemArray);
                itemMap.putString("key", entry.getKey());
                array.pushMap(itemMap);

            }
            cMap.putBoolean("ok", true);
            cMap.putArray("data", array);
            callback.invoke(cMap);
        } catch (Exception e) {
            Logger.i("查找文件出错:" + e.getMessage());
        }
//        Logger.i("分组数据:" + JsonUtils.getGson().toJson(map));
//        new String();
    }

    /**
     * 获取时间文本
     *
     * @param time
     * @return
     */
    public String getTimeStr(long time) {
        //在这个方法里获取出来 如何获取今天,本周,本月的逻辑.
//        Long now = System.currentTimeMillis();
        if (com.qunar.im.utils.DateUtil.getToDayBeginTime() <= time && time <= com.qunar.im.utils.DateUtil.getToDayEndTime()) {
            return "今天";
        } else if (com.qunar.im.utils.DateUtil.getWeekStartTime() <= time && time <= com.qunar.im.utils.DateUtil.getWeekEndTime()) {
            Logger.i("是本周内的时间:" + time);
            return "本周";
        } else if (com.qunar.im.utils.DateUtil.getMonthBegin() <= time && time <= com.qunar.im.utils.DateUtil.getMonthEnd()) {
            Logger.i("是本月内的时间:" + time);
            return "本月";
        } else {

            SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月");

            String ymTime = df.format(new Date(time));

            return ymTime;
        }

    }


    /**
     * 打开日期选择器
     *
     * @param params
     * @param callback
     */
    @ReactMethod
    public void openDatePicker(final ReadableMap params, final Callback callback) {
        TimePickerView tpv = new TimePickerBuilder(mActivity, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
//                Toast.makeText(MainActivity.this, getTime(date), Toast.LENGTH_SHORT).show();
            }
        }).build();

        tpv.show();
    }

    /**
     * 检查该用户此时间段内是否有冲突会议
     *
     * @param params
     * @param callback
     */
    @ReactMethod
    public void tripMemberCheck(final ReadableMap params, final Callback callback) {
        String checkId = params.getString("checkId");
        String beginTime = params.getString("beginTime");
        String endTime = params.getString("endTime");

        CalendarTrip.DataBean.TripsBean bean = new CalendarTrip.DataBean.TripsBean();
        bean.setCheckId(checkId);
        bean.setBeginTime(beginTime);
        bean.setEndTime(endTime);
        final WritableNativeMap map = new WritableNativeMap();
        HttpUtil.tripMemberCheck(bean, new ProtocolCallback.UnitCallback<TripMemberCheckResponse>() {
            @Override
            public void onCompleted(TripMemberCheckResponse tripMemberCheckResponse) {
                map.putBoolean("ok", true);
                map.putBoolean("isConform", tripMemberCheckResponse.getData().isIsConform());
                callback.invoke(map);
            }

            @Override
            public void onFailure(String errMsg) {
                map.putBoolean("ok", false);
                callback.invoke(map);
            }
        });

    }

    /**
     * 获取用户勋章
     *
     * @param xmppid
     * @param callback
     */
    @ReactMethod
    public void getUserMedal(String xmppid, Callback callback) {
//        xmppid = "dongzd.zhang@ejabhost1";
        final WritableNativeMap map = new WritableNativeMap();
        WritableNativeArray dataArray = parseUserMedalData(IMDatabaseManager.getInstance().getUserMedalsWithXmppId(xmppid));
        if (dataArray.size() > 0) {
            map.putArray("UserMedal", dataArray);
            callback.invoke(map);
        }

        if (!TextUtils.isEmpty(xmppid)) {
            final String finalXmppid = xmppid;
            HttpUtil.getRemoteUserMedalWithXmppJid(xmppid, new ProtocolCallback.UnitCallback<List<MedalsInfo>>() {
                @Override
                public void onCompleted(List<MedalsInfo> medalsInfos) {
                    if (medalsInfos.size() > 0) {


                        IMDatabaseManager.getInstance().bulkInsertUserMedalsWithData(medalsInfos);
                        WritableNativeArray array = parseUserMedalData(medalsInfos);
                        map.putString("UserId", finalXmppid);
                        map.putArray("UserMedals", array);
                        sendEvent("updateMedal", map);
                    }
                }

                @Override
                public void onFailure(String errMsg) {
                    Logger.i("获取勋章接口出现错误:" + errMsg);
                }
            });
        }
    }

    public WritableNativeArray parseUserMedalData(List<MedalsInfo> list) {
        WritableNativeArray array = new WritableNativeArray();
        for (int i = 0; i < list.size(); i++) {
            MedalsInfo medalsInfo = list.get(i);
            WritableNativeMap item = new WritableNativeMap();
            item.putString("UserId", medalsInfo.getXmppId());
            item.putString("type", medalsInfo.getType());
            item.putString("url", medalsInfo.getUrl());
            item.putString("desc", medalsInfo.getDesc());
            item.putString("LastUpdateTime", medalsInfo.getUpt());
            array.pushMap(item);
        }
        return array;
    }

    /**
     * 是否显示红点
     *
     * @param callback
     */
    @ReactMethod
    public void showRedView(Callback callback) {
        boolean show = DataUtils.getInstance(CommonConfig.globalContext).getPreferences("searchlocal", true);
        WritableNativeMap map = new WritableNativeMap();
        map.putBoolean("show", show);
//        map.putString("test","aaa");
        callback.invoke(map);
    }

    /**
     * 设置红点不在显示
     */
    @ReactMethod
    public void isShowRedView() {
        DataUtils.getInstance(CommonConfig.globalContext).putPreferences("searchlocal", false);
    }

    /**
     * RN点击埋点统计
     *
     * @param desc
     */
    @ReactMethod
    public void saveRNActLog(String eventId, String desc, String currentPage) {
        LogInfo logInfo = QLog.build(LogConstans.LogType.ACT, LogConstans.LogSubType.CLICK).eventId(eventId).describtion(desc).currentPage(currentPage);
        LogService.getInstance().saveLog(logInfo);
    }

    /**
     * 红包详情
     * @param rid
     * @param xmppid
     * @param isChatRoom
     * @param callback
     */
    @ReactMethod
    public void redEnvelopeGet(String rid,String xmppid,boolean isChatRoom,final Callback callback){
        final WritableNativeMap map = new WritableNativeMap();
        PayApi.red_envelope_get(xmppid, rid, isChatRoom, new HttpRequestCallback() {
            @Override
            public void onComplete(InputStream response) {
                try{
                    String resultString = Protocol.parseStream(response);
                    JSONObject result = new JSONObject(resultString);
                    if(result != null && result.optInt("ret") == 1){
                        JSONObject data = result.optJSONObject("data");
                        map.putBoolean("ok",true);
                        map.putString("credit",data.optString("credit"));
                        String user_id = data.optString("user_id");
                        ConnectionUtil.getInstance().getUserCard(QtalkStringUtils.userId2Jid(user_id), nick->{
                            map.putString("user_img",nick.getHeaderSrc());
                            map.putString("user_name",nick.getName());
                        });
                        map.putString("red_content",data.optString("red_content"));
                        map.putString("red_type",data.optString("red_type"));
                        map.putInt("over_time",data.optInt("grab_over_time"));
                        map.putInt("red_number",data.optInt("red_number"));
                        JSONArray array = data.optJSONArray("draw_record");
                        WritableNativeArray lists = new WritableNativeArray();
                        for(int i = 0; i<array.length(); i++){
                            JSONObject item = array.optJSONObject(i);
                            WritableNativeMap writableNativeMap = new WritableNativeMap();
                            writableNativeMap.putString("Credit",item.optString("credit"));
                            ConnectionUtil.getInstance().getUserCard(QtalkStringUtils.userId2Jid(item.optString("host_user_id")), nick->{
                                writableNativeMap.putString("Name",nick.getName());
                                writableNativeMap.putString("HeaderUri",nick.getHeaderSrc());
                            });
                            writableNativeMap.putString("Time",item.optString("draw_time"));
                            int rank = item.optInt("rank");
                            writableNativeMap.putString("Rank",rank == 1 ? "手气最佳" :"");
                            lists.pushMap(writableNativeMap);
                        }
                        map.putArray("redPackList",lists);
                        callback.invoke(map);
                    }else {
                        map.putBoolean("ok",false);
                        callback.invoke(map);
                    }
                }catch (Exception e){
                    map.putBoolean("ok",false);
                    callback.invoke(map);
                }
            }

            @Override
            public void onFailure(Exception e) {
                map.putBoolean("ok",false);
                callback.invoke(map);
            }
        });
    }

    /**
     * 我收到的红包
     * @param page
     * @param pagesie
     * @param year
     * @param callback
     */
    @ReactMethod
    public void redEnvelopeReceive(int page,int pagesie,int year,final Callback callback){
        final WritableNativeMap map = new WritableNativeMap();
        PayApi.red_envelope_receive(page, pagesie, year, new HttpRequestCallback() {
            @Override
            public void onComplete(InputStream response) {
                try{
                    String resultString = Protocol.parseStream(response);
                    JSONObject result = new JSONObject(resultString);
                    if(result != null && result.optInt("ret") == 1){
                        JSONObject data = result.optJSONObject("data");
                        JSONObject count = data.optJSONObject("count");
                        map.putBoolean("ok",true);
                        map.putString("total_credit",count.optString("total_credit"));
                        map.putString("count",count.optString("count"));
                        ConnectionUtil.getInstance().getUserCard(CurrentPreference.getInstance().getPreferenceUserId(), nick->{
                            map.putString("user_img",nick.getHeaderSrc());
                        });
                        JSONArray array = data.optJSONArray("list");
                        WritableNativeArray lists = new WritableNativeArray();
                        for(int i = 0; i<array.length(); i++){
                            JSONObject item = array.optJSONObject(i);
                            WritableNativeMap writableNativeMap = new WritableNativeMap();
                            writableNativeMap.putString("Credit",item.optString("credit"));
                            writableNativeMap.putString("Name",item.optString("realname"));
                            String host_user_id = item.optString("host_user_id");
                            ConnectionUtil.getInstance().getUserCard(host_user_id, nick-> {
                                writableNativeMap.putString("HeaderUri",nick.getHeaderSrc());
                            });
                            writableNativeMap.putString("Time",item.optString("draw_time"));
                            writableNativeMap.putString("Type",item.optString("red_type"));
                            lists.pushMap(writableNativeMap);
                        }
                        map.putArray("redPackList",lists);
                        callback.invoke(map);
                    }else {
                        map.putBoolean("ok",false);
                        callback.invoke(map);
                    }
                }catch (Exception e){
                    map.putBoolean("ok",false);
                    callback.invoke(map);
                }
            }

            @Override
            public void onFailure(Exception e) {
                map.putBoolean("ok",false);
                callback.invoke(map);
            }
        });
    }

    /**
     * 我发出去的红包
     * @param page
     * @param pagesie
     * @param year
     * @param callback
     */
    @ReactMethod
    public void redEnvelopeSend(int page,int pagesie,int year,final Callback callback){
        final WritableNativeMap map = new WritableNativeMap();
        PayApi.red_envelope_send(page, pagesie, year, new HttpRequestCallback() {
            @Override
            public void onComplete(InputStream response) {
                try{
                    String resultString = Protocol.parseStream(response);
                    JSONObject result = new JSONObject(resultString);
                    if(result != null && result.optInt("ret") == 1){
                        JSONObject data = result.optJSONObject("data");
                        JSONObject count = data.optJSONObject("count");
                        map.putBoolean("ok",true);
                        map.putString("total_credit",count.optString("total_credit"));
                        map.putString("count",count.optString("count"));
                        ConnectionUtil.getInstance().getUserCard(CurrentPreference.getInstance().getPreferenceUserId(), nick->{
                            map.putString("user_img",nick.getHeaderSrc());
                        });
                        JSONArray array = data.optJSONArray("list");
                        WritableNativeArray lists = new WritableNativeArray();
                        for(int i = 0; i<array.length(); i++){
                            JSONObject item = array.optJSONObject(i);
                            WritableNativeMap writableNativeMap = new WritableNativeMap();
                            writableNativeMap.putString("Credit",item.optString("credit"));
                            writableNativeMap.putString("Name",CurrentPreference.getInstance().getUserName());
                            writableNativeMap.putString("Time",item.optString("create_time"));
                            writableNativeMap.putString("Type",item.optString("red_type"));
                            writableNativeMap.putInt("Expire",item.optInt("is_expire"));
                            writableNativeMap.putInt("Number",item.optInt("red_number"));
                            writableNativeMap.putInt("Draw",item.optInt("draw_number"));
                            lists.pushMap(writableNativeMap);
                        }
                        map.putArray("redPackList",lists);
                        callback.invoke(map);
                    }else {
                        map.putBoolean("ok",false);
                        callback.invoke(map);
                    }
                }catch (Exception e){
                    map.putBoolean("ok",false);
                    callback.invoke(map);
                }
            }

            @Override
            public void onFailure(Exception e) {
                map.putBoolean("ok",false);
                callback.invoke(map);
            }
        });
    }

    private void sendPayFailNotification(){
        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.PAY_FAIL,Constants.Alipay.PAY);
    }

    @ReactMethod
    public void createRedEnvelope(final ReadableMap params, final Callback callback){
        final WritableNativeMap map = new WritableNativeMap();
        PayApi.send_red_envelope(params.toHashMap(), new HttpRequestCallback() {
            @Override
            public void onComplete(InputStream response) {
                try{
                    String resultString = Protocol.parseStream(response);
                    JSONObject result = new JSONObject(resultString);
                    if(result != null && result.optInt("ret") == 1){
                        JSONObject data = result.optJSONObject("data");
                        String params = data.optString("pay_parmas");
                        if(!TextUtils.isEmpty(params)){//唤起支付宝授权登录
                            map.putBoolean("ok",true);
                            callback.invoke(map);
                            IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.PAY_ORDER,params);
                        }else {
                            callback.invoke(map);
                        }
                    }else {
                        callback.invoke(map);
                    }
                }catch (Exception e){
                    callback.invoke(map);
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.invoke(map);
            }
        });
    }


    @Override
    public void didReceivedNotification(String key, Object... args) {
        WritableNativeMap map = new WritableNativeMap();
        switch (key) {
            case QtalkEvent.UPDATE_MEDAL_SELF:

                List<UserHaveMedalStatus> userMedalList = IMDatabaseManager.getInstance().selectUserWearMedalStatusByUserid(CurrentPreference.getInstance().getUserid(),QtalkNavicationService.getInstance().getXmppdomain());
                WritableArray medalList = new WritableNativeArray();
                for (int i = 0; i < userMedalList.size(); i++) {
                    medalList.pushString(userMedalList.get(i).getSmallIcon());
                }
                map.putArray("medalList",medalList);
                map.putString("UserId",CurrentPreference.getInstance().getPreferenceUserId());
                sendEvent("updateMedalList",map);
//                map.putArray();
                break;

            case QtalkEvent.Remove_Session:
            case QtalkEvent.Destory_Muc:
                map.putString("groupId", (String) args[0]);
                sendEvent("Remove_Session", map);

                break;
            case QtalkEvent.Del_Muc_Register:
                map.putString("groupId", (String) args[0]);
                sendEvent("Del_Destory_Muc", map);
                break;
            case QtalkEvent.Update_Muc_Vcard:
                //更新群名片
                break;

            case QtalkEvent.IQ_CREATE_MUC:
                //收到创建群的返回,在sdk中判断了是否成功
                //成功才返回,这里直接返回创建群的id在根据id去设
                // 置一次群名片
                final String groupId = (String) args[0];
                SetMucVCardData setMucVCardData = new SetMucVCardData();
                setMucVCardData.muc_name = groupId;
                setMucVCardData.desc = "没有公告";

                inviteName = CurrentPreference.getInstance().getUserName() + "," + inviteName.trim().replaceAll(" ", ",");
                inviteName = inviteName.endsWith(",") ? inviteName.substring(0, inviteName.length() - 1) : inviteName;
                setMucVCardData.nick = inviteName;
                setMucVCardData.title = "欢迎加入";
//                //这张图片是各大群都默认的图,我也用!
                setMucVCardData.pic = QtalkNavicationService.getInstance().getInnerFiltHttpHost() + "/file/v2/download/perm/2227ff2e304cb44a1980e9c1a3d78164.png";
                List<SetMucVCardData> list = new ArrayList<>();
                list.add(setMucVCardData);

                HttpUtil.setMucVCard(list, new ProtocolCallback.UnitCallback<SetMucVCardResult>() {
                    @Override
                    public void onCompleted(SetMucVCardResult setMucVCardResult) {
                        if (setMucVCardResult != null && setMucVCardResult.data != null && setMucVCardResult.data.size() > 0) {
//                            chatroomCreatedView.setResult(true, groupId);
                            //邀请人员

                            //用之前准备好的数据进行加人入群操作
                            ConnectionUtil.getInstance().inviteMessageV2(groupId, inviteUserList);
                        }
                    }

                    @Override
                    public void onFailure(String errMsg) {
//                        chatroomCreatedView.setResult(true, groupId);
                    }
                });
                break;

            //当加人完成时,通知rn界面
            case QtalkEvent.Muc_Invite_User_V2:
                map.putBoolean("createMuc", true);
                sendEvent("closeAddMembers", map);
                String opg = (String) args[0];
                if (createGroups != null && createGroups.containsKey(opg)) {
                    ReadableMap params = createGroups.get(opg);
                    if (params != null) {
                        openGroupChat(params, opg);
                        createGroups.remove(opg);
                    }
                } else {
                    NativeApi.openGroupChat(opg, opg);
                }
                break;
            case QtalkEvent.GravanterSelected:
                File tempFile = (File) args[0];
                if (tempFile != null && tempFile.exists()) {
//                    progressDialog.show();
                    String selGravatarPath = tempFile.getPath();
                    updateMyPersonalInfo(selGravatarPath);
                }
                break;
            case QtalkEvent.FEED_BACK_RESULT:
                boolean result = (boolean) args[0];
                toast(result ? "反馈成功，谢谢您的反馈！" : "oops反馈失败，请重试！");
                break;

            case QtalkEvent.SELECT_DATE:
                String date = (String) args[0];
                map.putString("date", date);
                sendEvent("nativeSelectDate", map);
                break;
            case QtalkEvent.Group_Member_Update:
                String gid = (String) args[0];
                getGroupMemberFromDB(gid, null);
                break;

            case QtalkEvent.WORK_WORLD_PERMISSIONS:
                String workWorldPermissions = (String) args[0];
                //todo这里发通知  告知一下
                break;
            case QtalkEvent.PAY_SUCCESS:
                sendEvent("paySuccessNotify",map);
                break;

        }
    }
}
