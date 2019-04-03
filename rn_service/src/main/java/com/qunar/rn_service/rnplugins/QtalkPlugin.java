package com.qunar.rn_service.rnplugins;

import android.text.TextUtils;
import android.util.Base64;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.orhanobut.logger.Logger;
import com.qunar.im.base.jsonbean.LogInfo;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.module.ReleaseContentData;
import com.qunar.im.base.module.WorkWorldItem;
import com.qunar.im.base.module.WorkWorldNoticeItem;
import com.qunar.im.base.module.WorkWorldResponse;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.structs.TransitFileJSON;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.HanziToPinyin;
import com.qunar.im.base.util.IMUserDefaults;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.graphics.MyDiskCache;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.manager.IMDatabaseManager;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.log.LogConstans;
import com.qunar.im.log.LogService;
import com.qunar.im.log.QLog;
import com.qunar.im.permission.PermissionCallback;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.HttpUtil;
import com.qunar.im.utils.MD5;
import com.qunar.im.utils.QtalkStringUtils;
import com.qunar.rn_service.protocal.NativeApi;

import java.util.List;

public class QtalkPlugin extends ReactContextBaseJavaModule implements IMNotificaitonCenter.NotificationCenterDelegate, PermissionCallback {


    public QtalkPlugin(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "QtalkPlugin";
    }

    @Override
    public void didReceivedNotification(String key, Object... args) {

    }

    @Override
    public void responsePermission(int requestCode, boolean granted) {

    }

    /**
     * 浏览大图 图片
     *
     * @param params   imageUrl为图片地址
     * @param callback
     */
    @ReactMethod
    public void browseBigImage(ReadableMap params, Callback callback) {
        if (params.hasKey("imageUrl")) {
            String imageUrl = params.getString("imageUrl");
            if (TextUtils.isEmpty(imageUrl)) {

            } else {
                NativeApi.openBigImage(imageUrl, MyDiskCache.getSmallFile(imageUrl + "&w=96&h=96").getAbsolutePath());
            }
        }
    }

    /**
     * 打开文件下载
     *
     * @param params   fileUrl为文件下载地址
     * @param callback
     */
    @ReactMethod
    public void openDownLoad(ReadableMap params, Callback callback) {


        if (params.hasKey("httpUrl") && params.hasKey("fileName") && params.hasKey("fileSize")) {
            String httpUrl = params.getString("httpUrl");
            String baseHttpUrl = Base64.encodeToString(httpUrl.replace(" ", "").getBytes(), Base64.NO_PADDING);
            String aaa = new String(Base64.decode(baseHttpUrl, Base64.NO_PADDING));
            String bbb = new String(Base64.decode(baseHttpUrl.getBytes(), Base64.NO_PADDING));
            String fileName = params.getString("fileName");

            String md5 = "";
            boolean nogetMd5 = true;
            if (params.hasKey("fileMD5")) {
                md5 = params.getString("fileMD5");
            }

            if (params.hasKey("isQtalkNative")) {
                nogetMd5 = !params.getBoolean("isQtalkNative");
            }

            String fileSize = "";
            try {
                fileSize = params.getString("fileSize");
            } catch (Exception e) {
                fileSize = params.getInt("fileSize") + "";
            }
            if (!TextUtils.isEmpty(httpUrl) && !TextUtils.isEmpty(fileName) && !TextUtils.isEmpty(fileSize)) {
                //        String fileID = params.getString("fileId");
//        String fileMD5 = params.getString("fileMD5");
                TransitFileJSON transitFileJSON = new TransitFileJSON();
                transitFileJSON.HttpUrl = baseHttpUrl;
                transitFileJSON.FileName = fileName;
                transitFileJSON.FileSize = fileSize;
                transitFileJSON.FILEMD5 = md5;
                transitFileJSON.noMD5 = nogetMd5;
                NativeApi.openFileDownLoad(transitFileJSON);
            }

        }


    }

    /**
     * 打开链接
     */
    @ReactMethod
    public void openNativeWebView(ReadableMap params) {
        String linkurl = params.getString("linkurl");
        if (!TextUtils.isEmpty(linkurl)) {
            NativeApi.openWebPage(linkurl, true);
        }
    }


    /**
     * 获取用户信息回传
     *
     * @param callback
     */
    public void getUserInfo(Callback callback) {

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
//        if (!("ejabhost1".equals(QtalkNavicationService.getInstance().getXmppdomain()))) {
//            map.putInt("showOrganizational", 1);
//        } else {
//            map.putInt("showOrganizational", 0);
//        }
        map.putBoolean("showServiceState", CurrentPreference.getInstance().isMerchants());
        map.putBoolean("isQtalk", CommonConfig.isQtalk);

//            map.putDouble("timestamp", System.currentTimeMillis());
        callback.invoke(map);
    }

    public static String getCKey() {

        /*
        k值为  base64(k1).
        k1的值为 u=用户名&k=md5(k2)
        k2的值为从qtalk客户端拿到的k值(k3)字符串拼接t的数值
        */
       /* String t = System.currentTimeMillis()+"";
        String k2 = CommonConfig.verifyKey+t;
        String k1 = "u="+ CurrentPreference.getInstance().getUserId()+"&k="+ BinaryUtil.MD5(k2)+"&t="+System.currentTimeMillis();
        String k = android.util.Base64.encodeToString(k1.getBytes(), android.util.Base64.NO_WRAP |
                                                                     android.util.Base64.URL_SAFE);

        return k;*/
        return Protocol.getCKEY();
    }

    /**
     * 打开单人会话
     */
    @ReactMethod
    public void openSignleChat(ReadableMap params) {
        String jid = params.getString("UserId");
        NativeApi.openSingleChat(jid, jid);
    }

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
                WritableMap map = new WritableNativeMap();
                map.putMap("nick", item);
                callback.invoke(map);
            }
        }, true, false);
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

    /**
     * 发送邮件
     *
     * @param params
     */
    @ReactMethod
    public void sendEmail(ReadableMap params) {
        String userId = params.getString("UserId");
        //qtalk 暂时这么写 目前没有借口获取email后缀地址
        userId = userId.substring(0, userId.lastIndexOf("@") + 1) + QtalkNavicationService.getInstance().getEmail();
        NativeApi.openEmail(userId);
    }

    @ReactMethod
    public void getWorkWorldNotRead(ReadableMap params, final Callback callback) {
        if(!IMDatabaseManager.getInstance().SelectWorkWorldPremissions()){
            return;
        }
//        boolean workWorldUnReadState = IMDatabaseManager.getInstance().SelectWorkWorldUnRead();
        boolean workWorldUnReadState = false;
        int workWorldNoticeUnRead = IMDatabaseManager.getInstance().selectWorkWorldNotice();
        final WritableMap map = new WritableNativeMap();
        map.putString("notReadMsgCount", workWorldNoticeUnRead + "");
        map.putString("showNewPost", workWorldUnReadState + "");
        callback.invoke(map);
    }

    @ReactMethod
    public void getWorkWorldItem(ReadableMap params, final Callback callback) {

        if(!IMDatabaseManager.getInstance().SelectWorkWorldPremissions()){
            return;
        }

        List<WorkWorldItem> list = ConnectionUtil.getInstance().selectHistoryWorkWorldItem(1, 0);
        if (list != null && list.size() > 0) {
            WorkWorldItem data = list.get(0);
            final WritableMap map = new WritableNativeMap();
            final String owner = data.getOwner();
            final String ownerHost = data.getOwnerHost();
            map.putString("createTime", data.getCreateTime());
            ReleaseContentData contentData = JsonUtils.getGson().fromJson(data.getContent(), ReleaseContentData.class);
            if (TextUtils.isEmpty(contentData.getContent())) {
                map.putString("content", "分享图片");
            } else {
                map.putString("content", contentData.getContent());
            }

            map.putString("postUUID", data.getUuid());
            if (data.getIsAnonymous().equals("1")) {
                map.putString("name", data.getAnonymousName());
                map.putString("photo", data.getAnonymousPhoto());
//                    sendEvent(cammReactInstanceManager.getCurrentReactContext(), "updateMomentsInfo", map);
                callback.invoke(map);
            } else {
                ConnectionUtil.getInstance().getUserCard(owner + "@" + ownerHost, new IMLogicManager.NickCallBack() {
                    @Override
                    public void onNickCallBack(Nick nick) {
                        if (nick != null) {
                            map.putString("name", nick.getName());
                            map.putString("photo", nick.getHeaderSrc());
                            map.putString("architecture", QtalkStringUtils.architectureParsing(nick.getDescInfo()));
                        } else {
                            map.putString("name", owner + "@" + ownerHost);
                            map.putString("photo", ConnectionUtil.defaultUserImage);
                        }
//                            sendEvent(cammReactInstanceManager.getCurrentReactContext(), "updateMomentsInfo", map);
                        callback.invoke(map);
                    }
                }, false, false);
            }
        }

        HttpUtil.refreshWorkWorldV2(1, 0,1,"", "", 0, new ProtocolCallback.UnitCallback<WorkWorldResponse>() {
            @Override
            public void onCompleted(WorkWorldResponse workWorldResponse) {
                try {
                    WorkWorldItem data = workWorldResponse.getData().getNewPost().get(0);
                    final WorkWorldNoticeItem item = new WorkWorldNoticeItem();
                    item.setOwner(data.getOwner());
                    item.setOwnerHost(data.getOwnerHost());
                    item.setCreateTime(data.getCreateTime());
                    ReleaseContentData contentData = JsonUtils.getGson().fromJson(data.getContent(), ReleaseContentData.class);
                    if (TextUtils.isEmpty(contentData.getContent())) {
                        item.setContent("分享图片");
                    } else {
                        item.setContent(contentData.getContent());
                    }
                    item.setPostUUID(data.getUuid());
                    item.setIsAnyonous(data.getIsAnonymous());
                    if (data.getIsAnonymous().equals("1")) {
                        item.setAnyonousName(data.getAnonymousName());
                        item.setAnyonousPhoto(data.getAnonymousPhoto());
                    }
                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.WORK_WORLD_FIND_NOTICE, item);


                    final String navurl = DataUtils.getInstance(CommonConfig.globalContext).getPreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_URL, "");

                    boolean show = IMUserDefaults.getStandardUserDefaults().getBooleanValue(CommonConfig.globalContext,
                            com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                                    + QtalkNavicationService.getInstance().getXmppdomain()
                                    + CommonConfig.isDebug
                                    + MD5.hex(navurl)
                                    + "WORKWORLDSHOWUNREAD", false);
                    if (show) {

                        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.WORK_WORLD_NOTICE);
                    } else {
                        boolean isHave = IMDatabaseManager.getInstance().selectHistoryWorkWorldItemIsHave(data);


                        if (!isHave) {

                            IMUserDefaults.getStandardUserDefaults().newEditor(CommonConfig.globalContext)
                                    .putObject(com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                                            + QtalkNavicationService.getInstance().getXmppdomain()
                                            + CommonConfig.isDebug
                                            + MD5.hex(navurl)
                                            + "WORKWORLDSHOWUNREAD", true)
                                    .synchronize();

                            IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.WORK_WORLD_NOTICE);
//
                        }
                    }


                } catch (Exception e) {
                    Logger.i("拿取最新一条朋友圈");
                }
            }

            @Override
            public void onFailure(String errMsg) {

            }
        });
    }

    @ReactMethod
    public void openWorkWorld(ReadableMap params) {

        NativeApi.openUserWorkWorld("", "");
        //埋点统计
        LogInfo logInfo = QLog.build(LogConstans.LogType.ACT,LogConstans.LogSubType.CLICK).describtion("打开驼圈");
        LogService.getInstance().saveLog(logInfo);
    }

    /**
     * 打开扫一扫
     * @param params
     */
    @ReactMethod
    public void openScan(ReadableMap params){
        NativeApi.openScan();
    }

    /**
     * 打开笔记本
     * @param params
     */
    @ReactMethod
    public void openNoteBook(ReadableMap params){
        NativeApi.openNoteBook();
    }

    /**
     * 打开文件传输
     * @param params
     */
    @ReactMethod
    public void openFileTransfer(ReadableMap params){
        NativeApi.openSingleChat("file-transfer@ejabhost1","file-transfer@ejabhost1");
    }


    /**
     * 打开行程
     * @param params
     */
    @ReactMethod
    public void openTravelCalendar(ReadableMap params){
//        NativeApi.openSingleChat("file-transfer@ejabhost1","file-transfer@ejabhost1");
        NativeApi.openTravelCalendar();
    }
}
