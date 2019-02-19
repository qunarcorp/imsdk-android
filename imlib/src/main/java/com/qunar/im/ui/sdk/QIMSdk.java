package com.qunar.im.ui.sdk;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ViewTarget;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.qunar.im.base.presenter.impl.QChatLoginPresenter;
import com.qunar.im.base.util.Constants;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.ui.fragment.ConversationFragment;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.module.RecentConversation;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.InternDatas;
import com.qunar.im.base.util.MemoryCache;
import com.qunar.im.base.util.ProfileUtils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.core.enums.LoginStatus;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.core.utils.GlobalConfigManager;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.thirdpush.QTPushConfiguration;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.PbChatActivity;
import com.qunar.im.ui.fragment.BuddiesFragment;
import com.qunar.im.ui.imagepicker.ImagePicker;
import com.qunar.im.ui.imagepicker.loader.GlideImageLoader;
import com.qunar.im.ui.imagepicker.view.CropImageView;
import com.qunar.rn_service.fragment.RNContactsFragment;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;

import java.util.List;

/**
 * 公共域sdk支持
 * Created by lihaibin.li on 2018/2/22.
 */

public class QIMSdk implements IMNotificaitonCenter.NotificationCenterDelegate {

    public Config config = new Config();

    private LoginStatesListener loginListener;

    private static volatile QIMSdk instance;

    public static QIMSdk getInstance() {
        if(instance == null){
            synchronized (QIMSdk.class){
                if (instance == null) {
                    instance = new QIMSdk();
                }
            }
        }
        return instance;
    }

    /**
     * sdk初始化
     *
     * @param application
     */
    public void init(Application application) {
        CommonConfig.globalContext = application.getApplicationContext();
        ProfileUtils.setDefaultRes(R.drawable.atom_ui_error_img);
        ViewTarget.setTagId(R.id.tag_glide);

        try {
            PackageInfo pi = CommonConfig.globalContext.getPackageManager().getPackageInfo(CommonConfig.globalContext.getPackageName(), 0);
            QunarIMApp.getQunarIMApp().setVersion(pi.versionCode);
            QunarIMApp.getQunarIMApp().setVersionName(pi.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        BackgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                QunarIMApp.getQunarIMApp().RegisterContext(CommonConfig.globalContext);
            }
        });
        CommonConfig.DEFAULT_GRAVATAR = R.drawable.atom_ui_default_gravatar;
        CommonConfig.SYS_ICON = R.drawable.atom_ui_rbt_system;
        CommonConfig.DEFAULT_NEW_MSG = R.raw.atom_ui_new_msg;
        CommonConfig.DEFAULT_GROUP = R.drawable.atom_ui_ic_my_chatroom;

        QunarIMApp.getQunarIMApp().setIsDebug(CommonConfig.isDebug);

        initDoMain(application);

        GlobalConfigManager.setGlobalContext(CommonConfig.globalContext);

        initImagePicker();

        //push日志
        LoggerInterface newLogger = new LoggerInterface() {
            @Override
            public void setTag(String tag) {
                // ignore
            }
            @Override
            public void log(String content, Throwable t) {
                com.orhanobut.logger.Logger.i("mipush日志：" + content + "  t: " + t);
            }
            @Override
            public void log(String content) {
                com.orhanobut.logger.Logger.i("mipush日志：" + content);
            }
        };
        Logger.setLogger(CommonConfig.globalContext, newLogger);
        //初始化push
        QTPushConfiguration.initPush(CommonConfig.globalContext);

        if (!ConnectionUtil.getInstance().isCanAutoLogin()) {
            QTPushConfiguration.unRegistPush(CommonConfig.globalContext);
        }

        ConnectionUtil.getInstance().addEvent(this, QtalkEvent.LOGIN_EVENT);
        ConnectionUtil.getInstance().addEvent(this, QtalkEvent.LOGIN_FAILED);
    }


    /**
     * 初始化图片选择器
     */
    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(false);                      //显示拍照按钮
        imagePicker.setCrop(false);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(9);              //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }

    /**
     * 初始化gradle配置信息
     */
    private void initDoMain(Application application) {
        //获取本应用程序信息
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = application.getPackageManager().getApplicationInfo(application.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //读取信息
        if (null != applicationInfo) {
            CommonConfig.isQtalk = applicationInfo.metaData.getBoolean("serverDoMain");
            CommonConfig.schema = applicationInfo.metaData.getString("SCHEME");
            CommonConfig.currentPlat = applicationInfo.metaData.getString("currentPlat");
        }
    }

    /**
     * 跳转到二人会话
     *
     * @param context
     * @param jid
     * @param chatType
     */
    public void goToChatConv(Context context, String jid, int chatType) {
        Intent intent = new Intent(context, PbChatActivity.class);
        intent.putExtra(PbChatActivity.KEY_JID, jid);
        intent.putExtra(PbChatActivity.KEY_CHAT_TYPE, String.valueOf(chatType));
        intent.putExtra(PbChatActivity.KEY_IS_CHATROOM, false);
        context.startActivity(intent);
    }

    /**
     * 跳转到consult会话
     *
     * @param jid
     * @param realJid
     * @param chatType
     */
    public void goToChatConvConsult(Context context, String jid, String realJid, int chatType) {
        Intent intent = new Intent(context, PbChatActivity.class);
        intent.putExtra(PbChatActivity.KEY_JID, jid);
        intent.putExtra(PbChatActivity.KEY_REAL_JID, realJid);
        intent.putExtra(PbChatActivity.KEY_CHAT_TYPE, String.valueOf(chatType));
        intent.putExtra(PbChatActivity.KEY_IS_CHATROOM, false);
        context.startActivity(intent);
    }

    /**
     * 跳转到群回话
     *
     * @param context
     * @param jid
     * @param chatType
     */
    public void goToGroupConv(Context context, String jid, int chatType) {
        Intent intent = new Intent(context, PbChatActivity.class);
        intent.putExtra(PbChatActivity.KEY_JID, jid);
        intent.putExtra(PbChatActivity.KEY_CHAT_TYPE, String.valueOf(chatType));
        intent.putExtra(PbChatActivity.KEY_IS_CHATROOM, true);
        context.startActivity(intent);
    }


    /**
     * 是否已连接到QIM
     *
     * @return
     */
    public boolean isConnected() {
        return ConnectionUtil.getInstance().isLoginStatus();
    }


    public boolean isCanAutoLogin(){
        return ConnectionUtil.getInstance().isCanAutoLogin();
    }

    /**
     * 本地 已缓存用户的用户名&Token后 可直接自动登录
     * @param loginStatesListener
     */
    public void autoLogin(LoginStatesListener loginStatesListener) {
        loginListener = loginStatesListener;
        if (!ConnectionUtil.getInstance().isCanAutoLogin()) {
            if (loginListener != null) {
                loginListener.isScuess(false, "用户名或Token不能为空!");
                return;
            }
            return;
        }
        ConnectionUtil.getInstance().pbAutoLogin();
    }

    /**
     * 使用 用户名密码登录
     * @param uid
     * @param password
     * @param loginStatesListener
     */
    public void login(String uid,String password,LoginStatesListener loginStatesListener){
        loginListener = loginStatesListener;
        if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(password)) {
            if (loginListener != null) {
                loginListener.isScuess(false, "用户名或密码不能为空!");
                return;
            }
            return;
        }
        ConnectionUtil.getInstance().pbLogin(uid,password,true);
    }

    /**
     * 使用qvt进行登录
     * @param qvt
     * @param plat
     * @param loginStatesListener
     */
    public void loginByQvt(String qvt,String plat,LoginStatesListener loginStatesListener){
        loginListener = loginStatesListener;
        if (TextUtils.isEmpty(qvt) || TextUtils.isEmpty(plat)) {
            if (loginListener != null) {
                loginListener.isScuess(false, "qvt、plat不能为空!");
                return;
            }
            return;
        }
        DataUtils.getInstance(CommonConfig.globalContext).putPreferences(Constants.Preferences.qchat_qvt, qvt);
        CurrentPreference.getInstance().setQvt(qvt);
        ConnectionUtil.getInstance().initNavConfig(true);
        QChatLoginPresenter qChatLoginPresenter = new QChatLoginPresenter();
        qChatLoginPresenter.loginByToken(plat);
    }

    /**
     * 登出
     */
    public void signOut(){
        ConnectionUtil.getInstance().pbLogout();
    }

    /**
     * 配置导航url
     *
     * @param url
     */
    public void setNavigationUrl(String url) {
        config.navigationUrl = url;
        DataUtils.getInstance(CommonConfig.globalContext).putPreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_URL, url);
    }

    /**
     * 查询未读消息数
     * @return
     */
    public int selectUnreadCount(){
        return ConnectionUtil.getInstance().SelectUnReadCount();
    }

    /**
     * 获取本地会话列表数据
     * @return
     */
    public List<RecentConversation> getRecentConversationList(boolean isOnlyUnRead){
        return ConnectionUtil.getInstance().SelectConversationList(isOnlyUnRead);
    }

    /**
     * 获取消息列表fragement
     * @return
     */
    public Fragment getConversationListFragment(){
        return new ConversationFragment();
    }

    /**
     * 获取通讯录
     * @return
     */
    public Fragment getContactsFragment(){
        return QtalkNavicationService.getInstance().getNavConfigResult().RNAndroidAbility.RNContactView ? new RNContactsFragment() : new BuddiesFragment();
    }

    /**
     * 无domain的userid
     * @return
     */
    public String getUserIDNoDomain(){
        return CurrentPreference.getInstance().getUserid();
    }

    /**
     * 带domain的userid
     * @return
     */
    public String getUserIDWithDomain(){
        return CurrentPreference.getInstance().getPreferenceUserId();
    }

    /**
     * 获取当前导航地址
     * @return
     */
    public String getCurrentNavUrl(){
        return QtalkNavicationService.getInstance().getCurrentNavUrl();
    }

    /**
     * 获取当前域 domain
     * @return
     */
    public String getCurrentDomain(){
        return QtalkNavicationService.getInstance().getXmppdomain();
    }

    /**
     * 获取单人名片
     * @param jid
     * @param callBack
     * @param enforce
     * @param todb
     */
    public void getUserCard(String jid, IMLogicManager.NickCallBack callBack, boolean enforce, boolean todb){
        ConnectionUtil.getInstance().getUserCard(jid,callBack,enforce,todb);
    }

    /**
     * 获取群名片
     * @param jid
     * @param callBack
     * @param enforce
     * @param todb
     */
    public void getMucCard(String jid, IMLogicManager.NickCallBack callBack,boolean enforce,boolean todb){
        ConnectionUtil.getInstance().getMucCard(jid,callBack,enforce,todb);
    }

    /**
     * 设置小米push key
     * @param pushKey
     */
    public void setMiPushKey(String pushKey){
        config.miPushKey = pushKey;
    }

    /**
     * 设置百度地图key
     * @param mapKey
     */
    public void setBaiduMapKey(String mapKey){
        config.baiduMapKey = mapKey;
    }

    /**
     * 清除缓存
     */
    public void clearMemoryCache() {
        InternDatas.cache.evictAll();
        MemoryCache.emptyCache();
        ImagePipelineFactory.getInstance().getImagePipeline().clearMemoryCaches();
        Glide.get(CommonConfig.globalContext).clearMemory();
    }

    @Override
    public void didReceivedNotification(String key, Object... args) {
        if (key.equals(QtalkEvent.LOGIN_EVENT)) {
            if (args[0].equals(LoginStatus.Login)) {
                if (loginListener != null) {
                    loginListener.isScuess(true, "登录成功！");
                }
            }
        } else if (key.equals(QtalkEvent.LOGIN_FAILED)) {
            if (loginListener != null) {
                if(args != null && args.length > 0){
                    if(args.length > 1){
                        loginListener.isScuess(false, "登录失败！ Error message:" + args[1].toString());
                    }else {
                        loginListener.isScuess(false, "登录失败！ Error code:" + args[0]);
                    }
                }else {
                    loginListener.isScuess(false, "登录失败");
                }
            }
        }
    }

    /**
     * 配置类
     */
    class Config {
        public String navigationUrl;//导航url
        public String miPushKey;//小米push key
        public String baiduMapKey;//百度地图 key
    }

    public interface LoginStatesListener {
        void isScuess(boolean isScuess, String message);
    }
}
