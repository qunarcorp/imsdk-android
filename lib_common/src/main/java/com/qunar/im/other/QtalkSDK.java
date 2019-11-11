package com.qunar.im.other;

import android.content.Context;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.CsvFormatStrategy;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.DiskLogStrategy;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.qunar.im.base.common.QChatRSA;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.module.WorkWorldNoticeTimeData;
import com.qunar.im.base.structs.PushSettinsStatus;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.IMUserDefaults;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.MemoryCache;
import com.qunar.im.base.util.PhoneInfoUtils;
import com.qunar.im.base.util.graphics.MyDiskCache;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.manager.IMCoreManager;
import com.qunar.im.core.manager.IMDatabaseManager;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.core.services.QtalkHttpService;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.protobuf.common.LoginType;
import com.qunar.im.protobuf.common.ParamIsEmptyException;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.protobuf.dispatch.DispatchHelper;
import com.qunar.im.protobuf.dispatch.DispatcherQueue;
import com.qunar.im.protobuf.utils.StringUtils;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.DeviceInfoManager;
import com.qunar.im.utils.MD5;
import com.qunar.im.protobuf.stream.PbAssemblyUtil;
import com.qunar.im.utils.PubKeyUtil;
import com.qunar.im.utils.QtalkDiskLogStrategy;
import com.qunar.im.utils.QtalkStringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

public class QtalkSDK {

    private static volatile QtalkSDK instance = new QtalkSDK();

    public static QtalkSDK getInstance() {
        return instance;
    }

//    public static void setInstance(QtalkSDK instance) {
//        QtalkSDK.instance = instance;
//    }

    private IMCoreManager coreManager;
    private IMLogicManager logicManager;
    public static final String CONNECTING_DISPATCHER_NAME = "connecting";

    private QtalkSDK() {
//        GlobalConfigManager.setGlobalContext(context);
        coreManager = IMCoreManager.BuildDefaultInstance(CommonConfig.globalContext);

        initialize();
    }

    public void initialize() {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(0)         // (Optional) How many method line to show. Default 2
                .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
                // (Optional) Changes the log strategy to print out. Default LogCat
                .tag("My custom tag")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();

        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return CommonConfig.isDebug;
            }
        });
        //自定义日志本地存储
        String folder = MyDiskCache.CACHE_LOG_DIR;//save path
        final int MAX_BYTES = 10 * 1024 * 1024;//10M  per file
        HandlerThread ht = new HandlerThread("AndroidFileLogger." + folder);
        ht.start();
        Logger.addLogAdapter(new DiskLogAdapter(CsvFormatStrategy.newBuilder()
                .logStrategy(new DiskLogStrategy(new QtalkDiskLogStrategy.WriteHandler(ht.getLooper(), folder, MAX_BYTES))).tag((CommonConfig.isQtalk ? "qtalk" : "qchat")).build()));
        //save log end
        logicManager = IMLogicManager.getInstance();
        String userName = IMUserDefaults.getStandardUserDefaults().getStringValue(CommonConfig.globalContext, Constants.Preferences.lastuserid);
        if (TextUtils.isEmpty(userName)) {

            userName = "test";
        }
        IMDatabaseManager.getInstance().initialize(userName, CommonConfig.globalContext);
        //在数据库初始化之出 把 声音震动提醒放入内存
        com.qunar.im.protobuf.common.CurrentPreference.getInstance().setTurnOnMsgSound(ConnectionUtil.getInstance().getPushStateBy(PushSettinsStatus.SOUND_INAPP));
        com.qunar.im.protobuf.common.CurrentPreference.getInstance().setTurnOnMsgShock(ConnectionUtil.getInstance().getPushStateBy(PushSettinsStatus.VIBRATE_INAPP));

    }

    //注册事件
    public void addEvent(IMNotificaitonCenter.NotificationCenterDelegate object, String key) {
        IMNotificaitonCenter.getInstance().addObserver(object, key);
    }

    //删除注册事件
    public void removeEvent(IMNotificaitonCenter.NotificationCenterDelegate object, String key) {
        IMNotificaitonCenter.getInstance().removeObserver(object, key);
    }


    /**
     * 退出登录
     *
     * @param userName
     */
    public void logout(String userName) {
        Logger.i("退出登录");
        //删除push imei
        try {
            Class clazzAd = Class.forName("com.qunar.im.thirdpush.QTPushConfiguration");
            Object adPresenter = clazzAd.newInstance();
            Method adMethod = clazzAd.getMethod("unRegistPush", Context.class);
            adMethod.invoke(adPresenter, CommonConfig.globalContext);
        } catch (Exception e) {
            Logger.i("删除push 异常：" + e.getMessage());
        }
        //删除push end
        MemoryCache.emptyCache();
        try{
            Class clazzEmoji = Class.forName("com.qunar.im.ui.util.EmotionUtils");
            Method method = clazzEmoji.getMethod("clearEmoticonCache");
            method.invoke(null,new Object[]{});
        }catch (Exception e){
            Logger.i("清除emoji缓存 异常：" + e.getMessage());
        }

//        Protocol.unregistPushinfo(PhoneInfoUtils.getUniqueID(), QTPushConfiguration.getPlatName(), true);
//        Protocol.deleteSelfImei(PhoneInfoUtils.getUniqueID(), true);
        //注销push
//        QTPushConfiguration.unRegistPush(CommonConfig.globalContext);

        //清理名片缓存
        IMLogicManager.getInstance().clearCache();
        CookieSyncManager.createInstance(QunarIMApp.getContext());
        CookieManager cookieManager = CookieManager.getInstance();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeAllCookies(null);
            cookieManager.removeSessionCookies(null);
        }else {
            cookieManager.removeAllCookie();
        }
        CookieSyncManager.getInstance().sync();

        //清空token
        IMUserDefaults.getStandardUserDefaults().newEditor(CommonConfig.globalContext)
                .removeObject(Constants.Preferences.usertoken)
                .synchronize();
        IMLogicManager.getInstance().setLoginStatus(false);
        IMLogicManager.getInstance().logout(userName);
        //
        clearSmscode();
    }

    public void sendMessage(final ProtoMessageOuterClass.ProtoMessage protoMessage) {
        DispatchHelper.Async("sendMessage",false, new Runnable() {
            @Override
            public void run() {

                coreManager.sendMessage(protoMessage);
            }
        });
    }

    /**
     * 同步发消息
     * @param protoMessage
     */
    public void sendMessageSync(ProtoMessageOuterClass.ProtoMessage protoMessage){
        coreManager.sendMessage(protoMessage);
    }

    /**
     * 发送心跳
     */
    public void sendHeartMessage() {
        IMLogicManager.getInstance().sendHeartMessage(PbAssemblyUtil.getHeartBeatMessage());
    }

    //是否需要获取短信验证码
    public boolean needTalkSmscode() {
        String token = IMUserDefaults.getStandardUserDefaults().getStringValue(CommonConfig.globalContext, Constants.Preferences.usertoken);
        String userName = IMUserDefaults.getStandardUserDefaults().getStringValue(CommonConfig.globalContext, Constants.Preferences.lastuserid);
        Logger.i("查看本地缓存登录信息:"+token+"---"+userName);
        return !StringUtils.isEmpty(token) && !StringUtils.isEmpty(userName);
    }

    //清空短信验证码
    public void clearSmscode() {
        //token放入sp
        IMUserDefaults.getStandardUserDefaults().newEditor(CommonConfig.globalContext)
                .removeObject(Constants.Preferences.usertoken);

        //username放入sp
        IMUserDefaults.getStandardUserDefaults().newEditor(CommonConfig.globalContext)
                .removeObject(Constants.Preferences.lastuserid);
    }

    Runnable loginRunnable = new Runnable() {
        @Override
        public void run(){
            try {
                Logger.i("进入自动登录方法,开始登陆,当前app版本:" + QunarIMApp.getQunarIMApp().getVersion() +
                        "当前热发版本：" + DataUtils.getInstance(CommonConfig.globalContext).getPreferences(Constants.Preferences.PATCH_TIMESTAMP + "_" + QunarIMApp.getQunarIMApp().getVersionName(), "0"));

                if (!needTalkSmscode()) {
                    Logger.i("用户名或密码为空,拒绝登陆");
                    //用户名或密码为null,拒绝登陆
                    return;
                }
                final String userName = IMUserDefaults.getStandardUserDefaults().getStringValue(CommonConfig.globalContext, Constants.Preferences.lastuserid);
                //记住 这个token就是密码, 没有经过base64的密码 没有加 /0xxx/0的密码
                final String token = IMUserDefaults.getStandardUserDefaults().getStringValue(CommonConfig.globalContext, Constants.Preferences.usertoken);
                com.qunar.im.protobuf.common.CurrentPreference.getInstance().setToken(token);
                com.qunar.im.protobuf.common.CurrentPreference.getInstance().setUserid(userName);

                IMDatabaseManager.getInstance().initialize(userName, CommonConfig.globalContext);
                IMDatabaseManager.getInstance().insertUserIdToCacheData(QtalkStringUtils.userId2Jid(userName));
                //在初始化数据库,还没建立连接之前,获取到时间戳,并保存
                String navurl = DataUtils.getInstance(CommonConfig.globalContext).getPreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_URL, "");
                String str = IMUserDefaults.getStandardUserDefaults().getStringValue(CommonConfig.globalContext,
                        com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                                + QtalkNavicationService.getInstance().getXmppdomain()
                                + CommonConfig.isDebug
                                + MD5.hex(navurl)
                                + "lastMessageTime");

                //获取朋友圈时间戳
                String wwuuid = IMUserDefaults.getStandardUserDefaults().getStringValue(CommonConfig.globalContext,
                        com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                                + QtalkNavicationService.getInstance().getXmppdomain()
                                + CommonConfig.isDebug
                                + MD5.hex(navurl)
                                + "lastwwuuid");

                //获取朋友圈时间戳
                String wwtime = IMUserDefaults.getStandardUserDefaults().getStringValue(CommonConfig.globalContext,
                        com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                                + QtalkNavicationService.getInstance().getXmppdomain()
                                + CommonConfig.isDebug
                                + MD5.hex(navurl)
                                + "lastwwtime");

                if (TextUtils.isEmpty(str)) {
                    Logger.i("上一次没有历史记录失败" + str);
                    long lastMessageTime = IMDatabaseManager.getInstance().getLastestMessageTime();
                    if (lastMessageTime <= 0) {
                        lastMessageTime = System.currentTimeMillis() - 3600 * 48 * 1000;
                        Logger.i("获取历史记录时间戳为空,初始化两天时间时间戳");
                    }
                    IMUserDefaults.getStandardUserDefaults().newEditor(CommonConfig.globalContext)
                            .putObject(com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                                    + QtalkNavicationService.getInstance().getXmppdomain()
                                    + CommonConfig.isDebug
                                    + MD5.hex(navurl)
                                    + "lastMessageTime", lastMessageTime + "")
                            .synchronize();
                    Logger.i("保存本次历史记录时间戳时间戳" + lastMessageTime);
                }


                if (TextUtils.isEmpty(wwtime)|| TextUtils.isEmpty(wwuuid)) {
                    Logger.i("上一次没有朋友圈历史记录失败" + wwtime);

                    WorkWorldNoticeTimeData data =IMDatabaseManager.getInstance().getLastestWorkWorldTime();
//
//                        }
                    IMUserDefaults.getStandardUserDefaults().newEditor(CommonConfig.globalContext)
                            .putObject(com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                                    + QtalkNavicationService.getInstance().getXmppdomain()
                                    + CommonConfig.isDebug
                                    + MD5.hex(navurl)
                                    + "lastwwuuid", data.getUuid() + "")
                            .synchronize();

                    IMUserDefaults.getStandardUserDefaults().newEditor(CommonConfig.globalContext)
                            .putObject(com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                                    + QtalkNavicationService.getInstance().getXmppdomain()
                                    + CommonConfig.isDebug
                                    + MD5.hex(navurl)
                                    + "lastwwtime", data.getCreateTime() + "")
                            .synchronize();
                    Logger.i("保存本次朋友圈历史记录时间戳时间戳" + data.getUuid()+","+data.getCreateTime());
                }

                //设置最新的群readmark时间戳
                String rmt = IMUserDefaults.getStandardUserDefaults().getStringValue(CommonConfig.globalContext,
                        com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                                + QtalkNavicationService.getInstance().getXmppdomain()
                                + CommonConfig.isDebug
                                + MD5.hex(navurl)
                                + "lastGroupReadMarkTime");
                if(TextUtils.isEmpty(rmt)){
                    String localGroupRMTime = IMDatabaseManager.getInstance().getLatestGroupRMTime();
                    IMUserDefaults.getStandardUserDefaults().newEditor(CommonConfig.globalContext)
                            .putObject(com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                                    + QtalkNavicationService.getInstance().getXmppdomain()
                                    + CommonConfig.isDebug
                                    + MD5.hex(navurl)
                                    + "lastGroupReadMarkTime", localGroupRMTime)
                            .synchronize();
                }
                coreManager.login(userName, token);
            } catch (IOException e) {
                Logger.e(e, "login failed - IOException");
            } catch (ParamIsEmptyException e) {
                Logger.e(e, "login failed - ParamIsEmptyException");
            }
        }
    };

    public void login(boolean isInterrupt) {
        try {
            DispatcherQueue connectingDispatcherQueue = DispatchHelper.getInstance().takeDispatcher(CONNECTING_DISPATCHER_NAME,false);
            if(connectingDispatcherQueue!=null){
                /**
                 * 移除未执行的任务
                 */
                connectingDispatcherQueue.removeCallbacks(loginRunnable);

                if(isInterrupt && !IMLogicManager.getInstance().isForceConnect()){
                    IMLogicManager.getInstance().setForceConnect();
                    IMLogicManager.getInstance().wakeup();
                }
                Logger.i("login-start" + isInterrupt);
            }

        } catch (Exception e) {
            Logger.i("连接线程发生中断异常");
        } finally {
            /**
             * 重新开启线程执行连接操作
             */
            DispatchHelper.Async(CONNECTING_DISPATCHER_NAME, false,loginRunnable);
            Logger.i("login-start");
        }

    }

    public boolean isConnected() {
        return logicManager.isConnected();
    }

    public boolean isLoginStatus() {
        return logicManager.isLoginStatus();
    }

//    public void setLoginStatus(boolean b) {
//        IMLogicManager.getInstance().setLoginStatus(b);
//    }

    public void newLogin(String userName, String password){
        Logger.i("开始新登陆,账号:" + userName + ",密码:" + password);
        try{
            JSONObject nauth = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("p",password);
            data.put("u",userName);
            data.put("mk",PhoneInfoUtils.getUniqueID());
            nauth.put("nauth",data);
            password = nauth.toString();
            Logger.i("开始新类型登陆,加密前密码 password = " + password);
            IMUserDefaults.getStandardUserDefaults().newEditor(CommonConfig.globalContext)
                    .putObject(Constants.Preferences.usertoken, password)
                    .synchronize();
            com.qunar.im.protobuf.common.CurrentPreference.getInstance().setToken(password);
            //username放入sp
            IMUserDefaults.getStandardUserDefaults().newEditor(CommonConfig.globalContext)
                    .putObject(Constants.Preferences.lastuserid, userName)
                    .synchronize();

            login(false);
        }catch (Exception e){

        }
    }

    public void publicLogin(String userName, String password) {
        Logger.i("开始公共域类型登陆,账号:" + userName + ",密码:" + password);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String timeStr = simpleDateFormat.format(date);

        password = "{\"d\":\"" + timeStr + "\", \"p\":\"" + password + "\", \"u\":\"" + userName + "\", \"a\":\"testapp\"}";
        Logger.i("开始公共域类型登陆,加密前密码 password = " + password);
        try {
            password = QChatRSA.QTalkEncodePassword(password);
            Logger.i("开始公共域类型登陆,加密后密码 password = " + password);
        } catch (Exception e) {
            LogUtil.e(e + "");
            PubKeyUtil.deletePUBKEY(QtalkNavicationService.getInstance().getXmppdomain());
            IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.LOGIN_FAILED, 0);
            return;
        }
        IMUserDefaults.getStandardUserDefaults().newEditor(CommonConfig.globalContext)
                .putObject(Constants.Preferences.usertoken, password)
                .synchronize();
        com.qunar.im.protobuf.common.CurrentPreference.getInstance().setToken(password);
        //username放入sp
        IMUserDefaults.getStandardUserDefaults().newEditor(CommonConfig.globalContext)
                .putObject(Constants.Preferences.lastuserid, userName)
                .synchronize();

        login(false);
    }

    //进行登录 登录时会获取token,和username存入sp中
    public void login(final String userName, final String password) {
        final boolean[] succeeded = {false};
        final String[] errorMessage = new String[1];

        if(TestAccount.isTestAccount(userName)){
            saveToken(succeeded,userName,TestAccount.getTestAccountToken(userName));
        }else {
            DispatchHelper.sync("takePassword", new Runnable() {
                @Override
                public void run() {
                    String tokenReqeustUrl = QtalkNavicationService.getInstance().getTokenSmsUrl();

                    JSONObject result =
                            QtalkHttpService.buildFormRequest(tokenReqeustUrl)
                                    .addParam("rtx_id", userName)
                                    .addParam("verify_code", password)
                                    .post();

                    if (result != null) {
                        int statusId = -100;
                        try {
                            statusId = result.getInt("status_id");
                        } catch (JSONException e) {
                            Logger.e(e, "json parse failed");
                        }
                        if (statusId == 0) {
                            String token = null;
                            try {
                                token = result.getJSONObject("data").getString("token");

                            } catch (JSONException e) {
                                Logger.e(e, "json parse failed");
                            }
                            if (StringUtils.isNotEmpty(token)) {
                                saveToken(succeeded,userName,token);
                            }
                        } else {
                            try {
                                errorMessage[0] = result.getString("msg");
                            } catch (JSONException e) {
                                Logger.e(e, "json parse failed");
                            }
                        }
                    }
                }
            });
        }
        if (succeeded[0] == false) {
            //默认错误类型设置为0
            IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.LOGIN_FAILED, 0);
        } else {
            login(false);
        }
    }

    private void saveToken(boolean[] succeeded,String userName,String token){
        //token放入sp
        String pwd = String.format("%s@%s", DeviceInfoManager.getInstance().getDeviceId(CommonConfig.globalContext), token);
        if(TestAccount.isTestAccount(userName)){
            pwd = token;
        }
        IMUserDefaults.getStandardUserDefaults().newEditor(CommonConfig.globalContext)
                .putObject(Constants.Preferences.usertoken, pwd)
                .synchronize();
        com.qunar.im.protobuf.common.CurrentPreference.getInstance().setToken(token);
        //username放入sp
        IMUserDefaults.getStandardUserDefaults().newEditor(CommonConfig.globalContext)
                .putObject(Constants.Preferences.lastuserid, userName)
                .synchronize();
        succeeded[0] = true;
    }

    public void takeSmsCode(final String userName, final IQTalkLoginDelegate delegate) {
        DispatchHelper.Async("takeSmsCode", new Runnable() {
            @Override
            public void run() {
                String tokenReqeustUrl = QtalkNavicationService.getInstance().getVerifySmsUrl();
                Logger.i("获取验证码 ： url = " + tokenReqeustUrl + "  username = " + userName);
                JSONObject result =
                        QtalkHttpService.buildFormRequest(tokenReqeustUrl)
                                .addParam("rtx_id", userName)
                                .post();

                if (delegate != null) {
                    int resultCode = -1;
                    if (result == null) {
                        delegate.onSmsCodeReceived(resultCode, "验证码获取失败，请重试！");
                        return;
                    }
                    String errMessage = null;

                    try {
                        if (result != null) {
                            resultCode = result.getInt("status_id");
                        }

                        if (resultCode != 0) {
                            errMessage = result.getString("msg");
                        }

                    } catch (JSONException e) {
                        Logger.e(e, "parse json failed");
                    }
                    delegate.onSmsCodeReceived(resultCode, errMessage);
                }
            }
        });
    }

    /**
     * 初始化导航配置
     */
    public void initNavConfig(final boolean isForce) {
        Logger.i("初始化导航:" + isForce);
        DispatchHelper.sync("updateNav", new Runnable() {
            @Override
            public void run() {
                //更新导航
                QtalkNavicationService.getInstance().updateNavicationConfig(isForce);
            }
        });
    }

}
