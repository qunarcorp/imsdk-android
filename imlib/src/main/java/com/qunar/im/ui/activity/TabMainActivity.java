package com.qunar.im.ui.activity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.orhanobut.logger.Logger;
import com.qunar.im.base.jsonbean.ExtendMessageEntity;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.presenter.ILoginPresenter;
import com.qunar.im.base.presenter.IMainPresenter;
import com.qunar.im.base.presenter.factory.LoginFactory;
import com.qunar.im.base.presenter.impl.MainPresenter;
import com.qunar.im.base.presenter.messageHandler.ConversitionType;
import com.qunar.im.base.presenter.views.ILoginView;
import com.qunar.im.base.presenter.views.IMainView;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.shortutbadger.ShortcutBadger;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.FileUtils;
import com.qunar.im.base.util.IMUserDefaults;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.ListUtil;
import com.qunar.im.base.util.Utils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.permission.PermissionCallback;
import com.qunar.im.permission.PermissionDispatcher;
import com.qunar.im.protobuf.Event.ConnectionErrorEvent;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.protobuf.common.LoginType;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.protobuf.dispatch.DispatchHelper;
import com.qunar.im.thirdpush.core.QPushClient;
import com.qunar.im.ui.R;
import com.qunar.im.ui.broadcastreceivers.ConnectionStateReceiver;
import com.qunar.im.ui.broadcastreceivers.ShareReceiver;
import com.qunar.im.ui.fragment.BuddiesFragment;
import com.qunar.im.ui.fragment.ConversationFragment;
import com.qunar.im.ui.fragment.MineFragment;
import com.qunar.im.ui.schema.QOpenHomeTabImpl;
import com.qunar.im.ui.services.PullPatchService;
import com.qunar.im.ui.services.PushServiceUtils;
import com.qunar.im.ui.util.NotificationUtils;
import com.qunar.im.ui.util.ParseErrorEvent;
import com.qunar.im.ui.util.QRRouter;
import com.qunar.im.ui.util.UpdateManager;
import com.qunar.im.ui.view.CommonDialog;
import com.qunar.im.ui.view.OnDoubleClickListener;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.tableLayout.CommonTabLayout;
import com.qunar.im.ui.view.tableLayout.bean.TabEntity;
import com.qunar.im.ui.view.tableLayout.listener.CustomTabEntity;
import com.qunar.im.ui.view.tableLayout.listener.OnTabSelectListener;
import com.qunar.im.ui.view.tableLayout.utils.ViewFindUtils;
import com.qunar.im.ui.view.zxing.activity.CaptureActivity;
import com.qunar.im.utils.AppFrontBackHelper;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.HttpUtil;
import com.qunar.im.utils.QtalkStringUtils;
import com.qunar.rn_service.activity.QtalkServiceRNActivity;
import com.qunar.rn_service.fragment.RNCalendarFragment;
import com.qunar.rn_service.fragment.RNContactsFragment;
import com.qunar.rn_service.fragment.RNFoundFragment;
import com.qunar.rn_service.fragment.RNMineFragment;
import com.qunar.rn_service.protocal.NativeApi;
import com.qunar.rn_service.rnmanage.QtalkServiceRNViewInstanceManager;
import com.qunar.rn_service.viewmodel.RNViewModel;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.qunar.im.common.CommonConfig.globalContext;

/**
 * Created by hubin on 2017/12/18.
 * 新版本ui 创建新MainActivity
 */

public class TabMainActivity extends IMBaseActivity implements PermissionCallback, IMainView, PopupMenu.OnMenuItemClickListener,DefaultHardwareBackBtnHandler {


    private static final int CHECK_UPDATE = PermissionDispatcher.getRequestCode();
    private static final int SCAN_REQUEST = PermissionDispatcher.getRequestCode();
    private static final int LOCATION_REQUIRE = PermissionDispatcher.getRequestCode();

    //tab标签页
    private CommonTabLayout mCommonTabLayou;
    //??
    private View mDecorView;
    //用于fragment显示的viewpager
    private ViewPager mViewPager;
    //adaptr
    private FragmentStatePagerAdapter mAdapter;
    //底部标签list
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    //fragment List
    private ArrayList<Fragment> mFragments = new ArrayList<>();

    private ConnectionUtil connectionUtil;
    //主界面主持
    private IMainPresenter mMainPresenter;
    //登陆界面主持
    private ILoginPresenter loginPresenter;

    private boolean startActivity;

    private ConversationFragment conversationFragment;

    private String[] mTitles;
    private int[] mTitleUnSelectIcons = {R.string.atom_ui_new_message_unselect, R.string.atom_ui_new_contact_unselect, R.string.atom_ui_new_found_unselect, R.string.atom_ui_new_my_unselect};
    private int[] mTitleSelectIcons = {R.string.atom_ui_new_message_select, R.string.atom_ui_new_contact_select, R.string.atom_ui_new_found_select, R.string.atom_ui_new_my_select};

    private int[] mTitleUnSelectIconsQtalk = {R.string.atom_ui_new_message_unselect, R.string.atom_ui_new_calendar_unselect,R.string.atom_ui_new_contact_unselect, R.string.atom_ui_new_found_unselect, R.string.atom_ui_new_my_unselect};
    private int[] mTitleSelectIconsQtalk = {R.string.atom_ui_new_message_select, R.string.atom_ui_new_calendar_select,R.string.atom_ui_new_contact_select, R.string.atom_ui_new_found_select, R.string.atom_ui_new_my_select};


    private ReactInstanceManager mReactInstanceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_tabmainactivity);
        connectionUtil = ConnectionUtil.getInstance();

        //在页面创建之出,初始化备注
        final Map<String, String> markups = ConnectionUtil.getInstance().selectMarkupNames();
        Logger.i("initreload map:" + JsonUtils.getGson().toJson(markups));
        com.qunar.im.protobuf.common.CurrentPreference.getInstance().setMarkupNames(markups);
        if(CommonConfig.isQtalk){
            mTitles = getResources().getStringArray(R.array.atom_ui_tab_title_qtalk);
        }else {
            mTitles = getResources().getStringArray(R.array.atom_ui_tab_title);
        }

//        initAction();
        initViewPager();
        initView();
        initActionBar();
        initMyCommTabLayout();

        initData();

        mReactInstanceManager = QtalkServiceRNViewInstanceManager.getInstanceManager(this);
//        if(Build.MANUFACTURER.equals("Xiaomi")) {
//            Intent intent = new Intent();intent.setAction("miui.intent.action.OP_AUTO_START");
//            intent.addCategory(Intent.CATEGORY_DEFAULT);startActivity(intent);
//        }
//        openJobService();
//        showConfigNotice();

//        nnon();
//        showConfigNotice();
    }

    private void showConfigNotice(){
        String image = IMUserDefaults.getStandardUserDefaults().getStringValue(CommonConfig.globalContext, "config");
        if("success".equals(image)){
            return;
        }
        CommonDialog.Builder configDialog = new CommonDialog.Builder(this);

        Spanned strC = Html.fromHtml("    本次升级针对客户端（Windows/Linux/Mac/iOS/Android）的启动和数据迁移做了大量优化（不兼容旧版客户端），会影响到置顶、表情收藏、备注等功能，<strong><font color=#ff0000>" + "为了不影响您的使用，请尽早更新使用中的所有" + CommonConfig.currentPlat + "客户端！" + "</font></strong>");
        configDialog.setTitle(getString(R.string.atom_ui_tip_dialog_prompt));
        configDialog.setMessageHtml(strC);
        configDialog.setPositiveButton(getString(R.string.atom_ui_common_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                ConnectionUtil.clearLastUserInfo();
                IMUserDefaults.getStandardUserDefaults().newEditor(CommonConfig.globalContext)
                                                .putObject("config", "success")
                                                .synchronize();
                dialog.dismiss();
            }
        });
        configDialog.setCancelable(false);
        configDialog.create().show();

    }

    private void initData() {
        loginPresenter = LoginFactory.createLoginPresenter();
        mMainPresenter = new MainPresenter(this);

        loginPresenter.setLoginView(new ILoginView() {
            @Override
            public String getUserName() {
                return com.qunar.im.protobuf.common.CurrentPreference.getInstance().getPreferenceUserId();
            }

            @Override
            public String getPassword() {
                return com.qunar.im.protobuf.common.CurrentPreference.getInstance().getToken();
            }

            @Override
            public void setLoginResult(final boolean success, int errcode) {
                if (!success) {
                    startLoginView();
                }else {
                    if (commonDialog != null && commonDialog.isShowing()) {
                        commonDialog.dismiss();
                    }
                }
            }

            @Override
            public boolean isSwitchAccount() {
                return false;
            }

            @Override
            public String getPrenum() {
                return "";
            }

            @Override
            public Context getContext() {
                return getApplicationContext();
            }

            @Override
            public void getVirtualUserRole(boolean b) {
                if (b) {
                    getHandler().post(new Runnable() {
                        @Override
                        public void run() {

                            Logger.i("自动登录成功");
//                            Toast.makeText(MainActivity.this, "自动登录成功", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }

            @Override
            public void setHeaderImage(final Nick nick) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        myActionBar.getSelfGravatarImage().setImageUrl(nick.getHeaderSrc(), true);
                    }
                });

            }

            @Override
            public void LoginFailure(int str) {
                if (TabMainActivity.this.isFinishing()) {
                    return;
                }
                if (commonDialog != null && commonDialog.isShowing()) {
                    commonDialog.dismiss();
//                    return;
                }

                if(str == ConnectionErrorEvent.fire){//已经离职
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//清除程序数据
                        ((ActivityManager)getSystemService(Context.ACTIVITY_SERVICE)).clearApplicationUserData();
                    }
                }

                commonDialog.setTitle(getString(R.string.atom_ui_tip_dialog_prompt));
                commonDialog.setMessage(getString(R.string.atom_ui_tip_login_failed) + ParseErrorEvent.getError(str, TabMainActivity.this));
                commonDialog.setPositiveButton(getString(R.string.atom_ui_common_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ConnectionUtil.clearLastUserInfo();
                        setLoginResult(false, 0);

                        dialog.dismiss();
                    }
                });
                commonDialog.setCancelable(false);
                commonDialog.create().show();
//                commonDialog = dialog.show();
            }

            @Override
            public void connectInterrupt() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setErrorTitle(getString(R.string.atom_ui_tip_disconnected));
                    }
                });
            }

            @Override
            public void noNetWork() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setErrorTitle(getString(R.string.atom_ui_tip_disconnected));

                    }
                });
            }

            @Override
            public void tryToConnect(final String str) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setErrorTitle(getString(R.string.atom_ui_tip_connecting));
                    }
                });
            }
        });
        handleShareAction(getIntent());

        injectExtra(getIntent());

        initActivityLifeCycle();

        File groupGravatar = new File(FileUtils.getExternalFilesDir(globalContext), "gravatar");
        if (!groupGravatar.exists()) {
            groupGravatar.mkdirs();
        }

        checkNotificationDialog();

    }

    void scanQrCode() {
        Intent scanQRCodeIntent = new Intent(getApplicationContext(), CaptureActivity.class);
        startActivityForResult(scanQRCodeIntent, SCAN_REQUEST);
    }


    @Override
    public void onBackPressed() {
        Utils.jump2Desktop(TabMainActivity.this);
        if (com.qunar.im.protobuf.common.CurrentPreference.getInstance().isTurnOnPsuh()) {
            //PushServiceUtils.startDPushService(this.getApplicationContext());
            PushServiceUtils.startAMDService(this);
        }
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent clearIntent = new Intent();
        clearIntent.setAction("com.qunar.ops.push.CLEAR_NOTIFY");
        clearIntent.setPackage(this.getApplicationContext().getPackageName());
        Utils.sendLocalBroadcast(clearIntent, this.getApplicationContext());

        //push清理
        QPushClient.clearNotification(this);

        regiestNetWorkChangeListener();

        //获取未读消息数量
        mMainPresenter.getUnreadConversationMessage();

        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostResume(this, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostPause(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        handleLogin();
    }

    private void handleLogin() {
        if (startActivity) {
            startActivity(getIntent());
            startActivity = false;
        }
    }

    ConnectionStateReceiver connectionStateReceiver;

    private void regiestNetWorkChangeListener() {
        if (connectionStateReceiver == null)
            connectionStateReceiver = new ConnectionStateReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(connectionStateReceiver, filter);
    }

    public synchronized void login() {
        //写死在配置类里面自动登录为true
        //TODO qchatv7.20 bug 需要判断没有qvt重新登录 正常不会没有无qvt的情况
        if (!CommonConfig.isQtalk && TextUtils.isEmpty(DataUtils.getInstance(this).getPreferences(Constants.Preferences.qchat_qvt, ""))) {
            IMUserDefaults.getStandardUserDefaults().newEditor(this).removeObject(Constants.Preferences.usertoken).synchronize();
        }
        boolean autoLogin = com.qunar.im.protobuf.common.CurrentPreference.getInstance().isAutoLogin();
        if (autoLogin) {
            Logger.i("判断是否可以自动登录:"+connectionUtil.isCanAutoLogin());
            if (!connectionUtil.isCanAutoLogin()) {
                startLoginView();
                return;
            } else {
                presenter.loadPreference(this, false);
                loginPresenter.autoLogin();
            }
        } else {
            startLoginView();
        }
    }


    public void setTabViewUnReadCount(int count) {
        if (count > 0) {
            mCommonTabLayou.showMsg(0, count);
        } else {
            mCommonTabLayou.hideMsg(0);
        }

//        TextView view = (TextView) tab.getTabAt(0).findViewById(R.id.textView_new_msg);
    }

    public void setTabViewOPSUnRead(boolean isShow) {
        if ("ejabhost1".equals(QtalkNavicationService.getInstance().getXmppdomain())
                || "ejabhost2".equals(QtalkNavicationService.getInstance().getXmppdomain())) {
            if (isShow) {
                if(CommonConfig.isQtalk){
                    mCommonTabLayou.showMsg(3, 0);
                }else{
                    mCommonTabLayou.showMsg(2, 0);
                }

            } else {
                if(CommonConfig.isQtalk){
                    mCommonTabLayou.hideMsg(3);
                }else{
                    mCommonTabLayou.hideMsg(2);
                }

//                mCommonTabLayou.showMsg(2, 0);
            }
        } else {
            if(CommonConfig.isQtalk){
                mCommonTabLayou.hideMsg(3);
            }else{
                mCommonTabLayou.hideMsg(2);
            }

        }
    }

    private void checkUpdate() {
        PermissionDispatcher.
                requestPermissionWithCheck(this, new int[]{PermissionDispatcher.REQUEST_WRITE_EXTERNAL_STORAGE,
                                PermissionDispatcher.REQUEST_READ_EXTERNAL_STORAGE}, this,
                        CHECK_UPDATE);
    }


    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        //分享
        handleShareAction(intent);
        injectExtra(intent);

        handleLogin();

        //退出登录
        if (!connectionUtil.isLoginStatus() || !connectionUtil.isConnected()) {
            login();
        }

    }

    /**
     * 检查应用通知
     */
    private void checkNotificationDialog() {
        //不在提醒
        boolean isNoneedCheck = DataUtils.getInstance(this).getPreferences("CheckNotification", false);
        //上次点击不再提醒时间
        long lastCheckTime = DataUtils.getInstance(this).getPreferences("lastCheckTime", 0);
        if (isNoneedCheck && lastCheckTime - System.currentTimeMillis() > 1000 * 60 * 60 * 24 * 7) {
            //三天清空状态，如通知未打开，继续提示
            DataUtils.getInstance(TabMainActivity.this).putPreferences("CheckNotification", false);
        }
        if (!NotificationUtils.areNotificationsEnabled(this) && !isNoneedCheck) {
            CommonDialog.Builder remindDialog = new CommonDialog.Builder(this);
            remindDialog.setTitle(getString(R.string.atom_ui_tip_dialog_prompt));
            remindDialog.setMessage(getString(R.string.atom_ui_open_notification_switch));
            remindDialog.setPositiveButton(getString(R.string.atom_ui_setting_title), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {
                    dialog.dismiss();
//                    Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS, Uri.parse("package:" + MainActivity.this.getPackageName()));
//                    startActivity(intent);
                    NotificationUtils.startNotificationSettings(TabMainActivity.this);

                }
            });
            remindDialog.setNeutralButton(getString(R.string.atom_ui_btn_not_remind), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {
                    DataUtils.getInstance(TabMainActivity.this).putPreferences("CheckNotification", true);
                    DataUtils.getInstance(TabMainActivity.this).putPreferences("lastCheckTime", System.currentTimeMillis());
                    dialog.dismiss();
                }
            });
            remindDialog.setNegativeButton(getString(R.string.atom_ui_common_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            remindDialog.create().show();
        }
    }

    private void injectExtra(Intent intent) {
        if(intent == null) return;
        Uri uri = intent.getData();
        Logger.i("injectExtra  uri = " + uri);
        Logger.i("injectExtra  intent = " + intent.getExtras());
        if (uri != null) {
            String jid = uri.getQueryParameter("jid");
            if (!TextUtils.isEmpty(jid) && !jid.equals("null")) {
                if (jid.equals("headline")) {
                    intent.setClass(this, RobotExtendChatActivity.class);
                    intent.putExtra(PbChatActivity.KEY_JID, Constants.SYS.SYSTEM_MESSAGE);
                    intent.putExtra(PbChatActivity.KEY_REAL_JID, Constants.SYS.SYSTEM_MESSAGE);
                    startActivity = true;
                } else {
                    boolean isFromChatRoom = jid.contains("@conference");
                    intent.setClass(this, PbChatActivity.class);
                    intent.putExtra(PbChatActivity.KEY_JID, jid);
                    intent.putExtra(PbChatActivity.KEY_IS_CHATROOM,
                            isFromChatRoom);
                    startActivity = true;
                }
            }
        } else if (intent.getExtras() != null) {//新版本接其他push跳转
            String jid = intent.getExtras().getString("jid");
            int type = intent.getExtras().getInt("type");
            if (!TextUtils.isEmpty(jid) && !jid.equals("null") && type >= 0) {
                if (type == ProtoMessageOuterClass.SignalType.SignalTypeHeadline_VALUE) {
                    intent.setClass(this, RobotExtendChatActivity.class);
                    intent.putExtra(PbChatActivity.KEY_JID, Constants.SYS.SYSTEM_MESSAGE);
                    intent.putExtra(PbChatActivity.KEY_REAL_JID, Constants.SYS.SYSTEM_MESSAGE);
                    startActivity = true;
                } else if ((type == ProtoMessageOuterClass.SignalType.SignalTypeChat_VALUE
                        || type == ProtoMessageOuterClass.SignalType.SignalTypeGroupChat_VALUE
                        || type == ProtoMessageOuterClass.SignalType.SignalTypeConsult_VALUE)
                        && jid.contains("@")) {
                    int converType = 0;
//                    if(ConnectionUtil.getInstance().isHotline(jid)) {
//                        converType = ConversitionType.MSG_TYPE_CONSULT;
//                    } else {
                        String chatid = intent.getExtras().getString("chatid");
                        converType = ConversitionType.getConversitionType(type, chatid);
                        if(converType == ConversitionType.MSG_TYPE_CONSULT) {
                            converType = ConversitionType.MSG_TYPE_CONSULT_SERVER;
                            intent.putExtra(PbChatActivity.KEY_REAL_JID, intent.getExtras().getString("realjid"));
                        } else if (converType == ConversitionType.MSG_TYPE_CONSULT_SERVER) {
                            converType = ConversitionType.MSG_TYPE_CONSULT;
                        }
//                    }
                    intent.putExtra(PbChatActivity.KEY_CHAT_TYPE, converType);

                    boolean isFromChatRoom = jid.contains("@conference");
                    intent.setClass(this, PbChatActivity.class);
                    intent.putExtra(PbChatActivity.KEY_JID, jid);
                    intent.putExtra(PbChatActivity.KEY_IS_CHATROOM,
                            isFromChatRoom);
                    startActivity = true;
                }
            }
        }

        //打开第几个tab
        String key = intent.getStringExtra(Constants.BundleKey.HOME_TAB);
        int index = QOpenHomeTabImpl.getInstance().getTabIndex(this,key,mTitles);
        if (index != -1 && mCommonTabLayou.getCurrentTab() != index) {
            mViewPager.setCurrentItem(index, false);
        }

        //来自shortcut的扫一扫 搜索
        if(intent.getBooleanExtra(Constants.BundleKey.IS_SHORTCUT_SCAN,false)){
            PermissionDispatcher.requestPermissionWithCheck(TabMainActivity.this, new int[]{PermissionDispatcher.REQUEST_CAMERA}, TabMainActivity.this, SCAN_REQUEST);
        }else if(intent.getBooleanExtra(Constants.BundleKey.IS_SHORTCUT_SEARCH,false)){
            startSearchActivity();
        }
    }

    int titileClickCount;
    private void initActionBar() {
        mNewActionBar = (QtNewActionBar) findViewById(R.id.my_new_action_bar);
        setNewActionBar(mNewActionBar);
//        setActionBarSingleTitle("QTalk");
        mNewActionBar.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
                if(conversationFragment != null) conversationFragment.moveToTop();
            }

            @Override
            public void onSingleClick() {

            }
        }));
        mNewActionBar.getTextTitle().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titileClickCount++;
                if(titileClickCount > 10){
                    titileClickCount = 0;
                    connectionUtil.resetUnreadCount();
                    toast("unread count reseted");
                }
            }
        });
    }

    private void initAction() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();
    }

    /**
     * 初始化vewpager页面数据
     */
    private void initViewPager() {
        mFragments.clear();
        conversationFragment = new ConversationFragment();
        mFragments.add(conversationFragment);

        //判断是qtalk 添加日历页
        if(CommonConfig.isQtalk){
            mFragments.add(new RNCalendarFragment());
        }
//        mFragments.add(new Fragment());
        //判断是启动rn页面还是原生页面
        if (QtalkNavicationService.getInstance().getNavConfigResult().RNAndroidAbility.RNContactView) {
            mFragments.add(new RNContactsFragment());
//            mFragments.add(new TestRNFragment());
//            mFragments.add(new BuddiesFragment());
        } else {
            mFragments.add(new BuddiesFragment());
        }



//        mFragments.add(new DiscoverFragment());

        if ("ejabhost1".equals(QtalkNavicationService.getInstance().getXmppdomain())
                || "ejabhost2".equals(QtalkNavicationService.getInstance().getXmppdomain())) {
            mFragments.add(getDiscoverFragment());

        } else {
            mFragments.add(new RNFoundFragment());
        }

        //判断是启动rn页面还是原生页面
        if (QtalkNavicationService.getInstance().getNavConfigResult().RNAndroidAbility.RNMineView) {
            mFragments.add(new RNMineFragment());
        } else {
            mFragments.add(new MineFragment());
        }


    }

    /**
     * 反射获取ops发现页面
     * @return
     */
    private Fragment getDiscoverFragment(){
        try{
            Class<?> cls = Class.forName("com.qunar.im.camelhelp.DiscoverFragment");
            return (Fragment) cls.newInstance();
        }catch (Exception e){

        }
        return new Fragment();
    }

    /**
     * 初始化view
     */
    private void initView() {
        mCommonTabLayou = (CommonTabLayout) findViewById(R.id.tab_common_tablayout);


        for (int i = 0; i < mTitles.length; i++) {
            if(CommonConfig.isQtalk){
                mTabEntities.add(new TabEntity(mTitles[i], mTitleSelectIconsQtalk[i], mTitleUnSelectIconsQtalk[i]));
            }else{
                mTabEntities.add(new TabEntity(mTitles[i], mTitleSelectIcons[i], mTitleUnSelectIcons[i]));
            }

        }
        mDecorView = getWindow().getDecorView();
        mViewPager = ViewFindUtils.find(mDecorView, R.id.tab_fragment_viewpager);
        mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);

    }

    /**
     * 初始化commtablayou选择器
     */
    private void initMyCommTabLayout() {
        mCommonTabLayou.setTabData(mTabEntities);
        mCommonTabLayou.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(final int position) {
//                mViewPager.setCurrentItem(position);
                mViewPager.setCurrentItem(position, false);

            }

            @Override
            public void onTabReselect(int position) {
//                if (position == 0) {
//                    mTabLayout_2.showMsg(0, mRandom.nextInt(100) + 1);
////                    UnreadMsgUtils.show(mTabLayout_2.getMsgView(0), mRandom.nextInt(100) + 1);
//                }
            }
        });

        View view = mCommonTabLayou.getTabView(0);

        view.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
//                            Toast.makeText(MainActivity.this,"双击了标签",Toast.LENGTH_LONG).show();
                conversationFragment.MoveToUnread();
            }

            @Override
            public void onSingleClick() {

            }
        }));


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCommonTabLayou.setCurrentTab(position);
                setTitleContentByIndex(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
//        mCommonTabLayou.setMsgMargin(0,-10,0);

        mViewPager.setOffscreenPageLimit(4);
        setTitleContentByIndex(0);

        //启动程序默认进来显示 已断开连接
        setActionBarTitle(getString(R.string.atom_ui_tip_disconnected));
    }

    private void startSearchActivity(){
        Intent intent = new Intent(TabMainActivity.this, SearchUserActivity.class);
        startActivity(intent);
//        if (CommonConfig.isQtalk) {
//            try{
//                Class clazz = Class.forName("com.qunar.im.camelhelp.activity.QTalkSearchActivity");
//                Intent i = new Intent(TabMainActivity.this, clazz);
//                startActivity(i);
//            }catch (ClassNotFoundException e){
//
//            }
//        } else {
//            Intent intent = new Intent(TabMainActivity.this, SearchUserActivity.class);
//            startActivity(intent);
//        }
    }

    @SuppressLint("RestrictedApi")
    private void setTitleContentByIndex(int position) {
        switch (position) {
            case 0:
                setActionBarSingleTitle(mTitles[mViewPager.getCurrentItem()]);
                setActionBarRightIcon(R.string.atom_ui_new_add);
                setActionBarLeftIcon(false);
                final PopupMenu popupMenu = new PopupMenu(TabMainActivity.this, mNewActionBar.getRightIcon());
                popupMenu.inflate(CommonConfig.isQtalk ? R.menu.atom_ui_menu_main_qtalk : R.menu.atom_ui_menu_main_qchat);
                try {
                    Field field = PopupMenu.class.getDeclaredField("mPopup");
                    field.setAccessible(true);
                    MenuPopupHelper helper = (MenuPopupHelper) field.get(popupMenu);
                    helper.setForceShowIcon(true);
                } catch (NoSuchFieldException e) {
                    Logger.i("nosuchfield", e);
                } catch (IllegalAccessException e) {
                    Logger.i("access", e);
                }
                popupMenu.setOnMenuItemClickListener(TabMainActivity.this);
                setActionBarRightIconClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupMenu.show();
                    }
                });
                setActionBarRightSpecial(R.string.atom_ui_new_search);
                setActionBarRightIconSpecialClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startSearchActivity();
                    }
                });
                break;
            case 1:
                if(CommonConfig.isQtalk){
                    setActionBarRightSpecial(0);
                    setActionBarSingleTitle(mTitles[mViewPager.getCurrentItem()]);
                    setActionBarRightIcon(R.string.atom_ui_new_select_calendar);
                    setActionBarRightIconClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TimePickerView tpv = new TimePickerBuilder(TabMainActivity.this, new OnTimeSelectListener() {
                                @Override
                                public void onTimeSelect(Date date, View v) {
                                    String time = new SimpleDateFormat("yyyy-MM-dd").format(date);
//                                    String dateString = String.valueOf(date.getTime());
                                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.SELECT_DATE,time);
// Toast.makeText(MainActivity.this, getTime(date), Toast.LENGTH_SHORT).show();
                                }
                            }).build();

                            tpv.show();
//                            Intent intent = new Intent(TabMainActivity.this, QtalkServiceRNActivity.class);
//                            intent.putExtra("module", QtalkServiceRNActivity.CONTACTS);
//                            intent.putExtra("Screen", "Search");
//                            intent.putExtra(Constants.BundleKey.DOMAIN_LIST_URL,QtalkNavicationService.getInstance().getDomainSearchUrl());
//                            intent.putExtra(Constants.BundleKey.FILE_URL,QtalkNavicationService.getInstance().getInnerFiltHttpHost());
//                            startActivity(intent);
                        }
                    });
                }else{
                    setActionBarRightSpecial(0);
                    setActionBarSingleTitle(mTitles[mViewPager.getCurrentItem()]);
                    setActionBarRightIcon(R.string.atom_ui_new_addfriend);
                    setActionBarRightIconClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            TimePickerView tpv = new TimePickerBuilder(TabMainActivity.this, new OnTimeSelectListener() {
//                                @Override
//                                public void onTimeSelect(Date date, View v) {
//// Toast.makeText(MainActivity.this, getTime(date), Toast.LENGTH_SHORT).show();
//                                }
//                            }).build();
//
//                            tpv.show();
                            Intent intent = new Intent(TabMainActivity.this, QtalkServiceRNActivity.class);
                            intent.putExtra("module", QtalkServiceRNActivity.CONTACTS);
                            intent.putExtra("Screen", "Search");
                            intent.putExtra(Constants.BundleKey.DOMAIN_LIST_URL,QtalkNavicationService.getInstance().getDomainSearchUrl());
                            startActivity(intent);
                        }
                    });
                }

                break;
            case 2:
                if(CommonConfig.isQtalk){
                    setActionBarRightSpecial(0);
                    setActionBarSingleTitle(mTitles[mViewPager.getCurrentItem()]);
                    setActionBarRightIcon(R.string.atom_ui_new_addfriend);
                    setActionBarRightIconClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(TabMainActivity.this, QtalkServiceRNActivity.class);
                            intent.putExtra("module", QtalkServiceRNActivity.CONTACTS);
                            intent.putExtra("Screen", "Search");
                            intent.putExtra(Constants.BundleKey.DOMAIN_LIST_URL,QtalkNavicationService.getInstance().getDomainSearchUrl());
                            startActivity(intent);
                        }
                    });
                }else{
                    setActionBarRightSpecial(0);
                    setActionBarSingleTitle(mTitles[mViewPager.getCurrentItem()]);
                    setActionBarRightIcon(R.string.atom_ui_new_qr);
                    setActionBarRightIconClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PermissionDispatcher.requestPermissionWithCheck(TabMainActivity.this, new int[]{PermissionDispatcher.REQUEST_CAMERA}, TabMainActivity.this, SCAN_REQUEST);
                        }
                    });
                }

                break;
            case 3:
                if(CommonConfig.isQtalk){
                    setActionBarRightSpecial(0);
                    setActionBarSingleTitle(mTitles[mViewPager.getCurrentItem()]);
                    setActionBarRightIcon(R.string.atom_ui_new_qr);
                    setActionBarRightIconClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PermissionDispatcher.requestPermissionWithCheck(TabMainActivity.this, new int[]{PermissionDispatcher.REQUEST_CAMERA}, TabMainActivity.this, SCAN_REQUEST);
                        }
                    });
                }else{
                    setActionBarRightSpecial(0);
                    setActionBarSingleTitle(mTitles[mViewPager.getCurrentItem()]);
                    setActionBarRightIcon(0);
                }

                break;
            case 4:
                setActionBarRightSpecial(0);
                setActionBarSingleTitle(mTitles[mViewPager.getCurrentItem()]);
                setActionBarRightIcon(0);
                break;
        }
    }


    /**
     * 简单pagerview适配器
     */
    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
//            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);

        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }
    }

    public void showMyInfo() {
        String userId = CurrentPreference.getInstance().getPreferenceUserId();
        Intent intent = new Intent(this, PersonalInfoMyActivity.class);
        intent.putExtra("jid", QtalkStringUtils.userId2Jid(userId));
        startActivity(intent);
    }

    public void showAccountInfo() {
        try{
            Class classHyMain = Class.forName("com.qunar.im.camelhelp.HyMainActivity");
            Intent i = new Intent(this, classHyMain);
            i.putExtra("module", "user-info");
            i.putExtra("data", "");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }catch (ClassNotFoundException e){

        }
    }

    public void showSetting() {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.atom_ui_slide_right, R.anim.atom_ui_slide_right);
    }

    public void showClockIn() {
//        Intent intent  = new Intent(this, QtalkServiceRNActivity.class);
//        startActivity(intent);
        checkLocation();
    }

    public void startClockIn() {
        Intent intent = new Intent(this, QtalkServiceRNActivity.class);
        intent.putExtra("module", QtalkServiceRNActivity.CLOCKIN);
        startActivity(intent);
    }

    public void showTOTP() {
        Intent intent = new Intent(this, QtalkServiceRNActivity.class);
        intent.putExtra("module", QtalkServiceRNActivity.TOTP);
        startActivity(intent);
    }

    public void gotoNote(){
        Intent intent = new Intent(this, DailyMindActivity.class);
        startActivity(intent);
    }

    /**
     * 检查权限
     */
    private void checkLocation() {
        PermissionDispatcher.requestPermissionWithCheck(this, new int[]{PermissionDispatcher.REQUEST_ACCESS_COARSE_LOCATION,
                        PermissionDispatcher.REQUEST_ACCESS_FINE_LOCATION}, this,
                LOCATION_REQUIRE);
    }


    public void showHongBao() {
        if (!TextUtils.isEmpty(QtalkNavicationService.MY_HONGBAO)) {
            Uri uri = Uri.parse(QtalkNavicationService.MY_HONGBAO);
            Intent intent = new Intent(this, QunarWebActvity.class);
            intent.putExtra(Constants.BundleKey.WEB_FROM, Constants.BundleValue.HONGBAO);
            intent.setData(uri);
            startActivity(intent);
        }
    }

    public void showHongBaoBalance() {
        if (!TextUtils.isEmpty(QtalkNavicationService.HONGBAO_BALANCE)) {
            Uri uri = Uri.parse(QtalkNavicationService.HONGBAO_BALANCE);
            Intent intent = new Intent(this, QunarWebActvity.class);
            intent.putExtra(Constants.BundleKey.WEB_FROM, Constants.BundleValue.HONGBAO);
            intent.setData(uri);
            startActivity(intent);
        }
    }

    public void showFeedBack() {
        Intent intent = new Intent(this, BugreportActivity.class);
        startActivity(intent);
    }

    private void startLoginView() {
        if (commonDialog != null && commonDialog.isShowing()) {
            commonDialog.dismiss();
        }
        if (!CommonConfig.loginViewHasShown) {
            if (CommonConfig.isQtalk) {
                if (LoginType.PasswordLogin.equals(QtalkNavicationService.getInstance().getLoginType())) {
                    startActivity(new Intent(this, QTalkUserLoginActivity.class));
                } else {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                }
            } else {
                Intent intent = new Intent(this, QChatLoginActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onDestroy() {
//        EventBus.getDefault().unregister(handleMainEvent);
        super.onDestroy();
        if (connectionStateReceiver != null)
            unregisterReceiver(connectionStateReceiver);

        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostDestroy(this);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SCAN_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                if (!TextUtils.isEmpty(data.getStringExtra("content"))) {
                    String content = data.getStringExtra("content");
                    QRRouter.handleQRCode(content, this);
                }
            }
        }
    }


    @Override
    public void responsePermission(int requestCode, boolean granted) {
        if (!granted) return;
        if (requestCode == SCAN_REQUEST) {
            scanQrCode();
        } else if (requestCode == CHECK_UPDATE) {
            UpdateManager.getUpdateManager().checkAppUpdate(this, false);
            //热修复加载补丁
            PullPatchService.runPullPatchService(this);
        } else if (requestCode == LOCATION_REQUIRE) {
            startClockIn();
        }
    }

    @Override
    public void setUnreadConversationMessage(final int unreadNumbers) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                setTabViewUnReadCount(unreadNumbers);
            }
        });
    }

    @Override
    public void loginSuccess() {
        //登陆成功更改界面ui
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setErrorTitle("");
                setActionBarTitle(mTitles[mViewPager.getCurrentItem()]);
            }
        });

        //获取push状态
        presenter.getPushState();
        //登录成功，启动push
        boolean isRegister = presenter.checkUnique();
        if (CurrentPreference.getInstance().isTurnOnPsuh()) {
            if (isRegister) {
                PushServiceUtils.stopAMDService(TabMainActivity.this);
            }
            PushServiceUtils.startAMDService(TabMainActivity.this);
        } else {
            PushServiceUtils.stopAMDService(TabMainActivity.this);
        }
        //检查更新
        checkUpdate();
//        DiscoverFragment.clearBridge();
        //刷新发现页
//        DiscoverFragment.clearBridge();

//        RNContactsFragment.clearBridge();
//        RNFoundFragment.clearBridge();
//        RNMineFragment.clearBridge();
    }

    //同步中方法
    @Override
    public void synchronousing() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setErrorTitle(getString(R.string.atom_ui_tip_synchronizing));
            }
        });
    }

    @Override
    public void refreshShortcutBadger(final int count) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if(!getFragmentManager().isDestroyed()){//fix IllegalStateException: Can't access ViewModels from
                RNViewModel rnViewModel = ViewModelProviders.of(this).get("RnVM",RNViewModel.class);
                rnViewModel.getUnreadCountLD().postValue(count);
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //刷新未读角标，小米逻辑在pushReceiver里单独处理
                if (!Utils.isMIUI()) {
                    boolean success = ShortcutBadger.applyCount(TabMainActivity.this, count);
                    Logger.i("ShortcutBadger", "Set count=" + count + ", success=" + success);
                } else {
                    Notification.Builder builder = new Notification.Builder(CommonConfig.globalContext);
                    Notification notification = builder.build();
                    //小米系统未读角标
//                    int total = ConnectionUtil.getInstance().SelectUnReadCount();
                    ShortcutBadger.applyNotification(CommonConfig.globalContext.getApplicationContext(), notification, count);
                }
            }
        });


    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void showDialog(String str) {

    }

    @Override
    public void refresh() {
//        initViewPager();
//        initView();
        if (CurrentPreference.getInstance().isSwitchAccount()) {

            //切换过账号后,将值设置为默认情况 false
            CurrentPreference.getInstance().setSwitchAccount(false);
//            ((RNBaseFragment)(mFragments.get(2))).unbundling();
            if(CommonConfig.isQtalk){
                mFragments.remove(3);
                if ("ejabhost1".equals(QtalkNavicationService.getInstance().getXmppdomain())
                        || "ejabhost2".equals(QtalkNavicationService.getInstance().getXmppdomain())) {
                    mFragments.add(3, getDiscoverFragment());
//            mFragments.add(new RNFoundFragment());
                } else {
                    mFragments.add(3, new RNFoundFragment());
                }
            }else{
                mFragments.remove(2);
                if ("ejabhost1".equals(QtalkNavicationService.getInstance().getXmppdomain())
                        || "ejabhost2".equals(QtalkNavicationService.getInstance().getXmppdomain())) {
                    mFragments.add(2, getDiscoverFragment());
//            mFragments.add(new RNFoundFragment());
                } else {
                    mFragments.add(2, new RNFoundFragment());
                }
            }

//            mAdapter = new MyPagerAdapter(getSupportFragmentManager());
//            mViewPager.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void refreshOPSUnRead(final boolean isShow) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                setTabViewOPSUnRead(isShow);
            }
        });
    }

    @Override
    public void startOPS() {
    }



    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_scanning) {
            PermissionDispatcher.requestPermissionWithCheck(this, new int[]{PermissionDispatcher.REQUEST_CAMERA}, this, SCAN_REQUEST);
            Logger.i("扫码");
        } else if (id == R.id.action_add_buddy) {
            Intent addBuddyIntent = new Intent(this, AddBuddyActivity.class);
            startActivity(addBuddyIntent);
        } /*else if (id == R.id.action_show_request) {
            Intent intent = new Intent(this, BuddyRequestActivity.class);
            startActivity(intent);
        }*/ else if (id == R.id.weilvxing) {
            Intent shareIntent = new Intent(TabMainActivity.this, QunarWebActvity.class);
            shareIntent.setData(Uri.parse(Constants.SHARE_TRAVER));
            shareIntent.putExtra(QunarWebActvity.IS_HIDE_BAR,
                    true);
            startActivity(shareIntent);
        } else if (id == R.id.fav_item) {
            Intent intent = new Intent(TabMainActivity.this, MyFavourityMessageActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_wiki) {
            String wikiUrl = QtalkNavicationService.getInstance().getWikiurl();
            if(!TextUtils.isEmpty(wikiUrl)){
                Uri uri = Uri.parse(wikiUrl);
                Intent intent = new Intent(this, QunarWebActvity.class);
                intent.setData(uri);startActivity(intent);
            }else {
                toast(getString(R.string.atom_ui_look_forward));
            }

        }else if(id ==R.id.action_creat_group){
            NativeApi.openCreateGroup();
        }else if(id == R.id.action_ever_note){
            gotoNote();
        }else if(id == R.id.action_read){
            //一键已读同时清除at
            Map<String,String> atMap = connectionUtil.getAtMessageMap();
            if(atMap != null){
                atMap.clear();
            }
            DispatchHelper.Async("OneKeyRead", new Runnable() {
                @Override
                public void run() {
                    connectionUtil.setAllMsgRead();
                    mMainPresenter.getUnreadConversationMessage();
                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Show_List);
                }
            });
        }
        return true;
    }


    public void handleShareAction(Intent intent) {
        if (intent == null) return;
        if (intent.hasExtra(ShareReceiver.SHARE_EXTRA_KEY)) {
            intent.setClass(this, SearchUserActivity.class);
            intent.putExtra(Constants.BundleKey.IS_TRANS, true);
            intent.putExtra(Constants.BundleKey.IS_FROM_SHARE, true);
//            if(intent.hasExtra(Constants.BundleKey.TRANS_MSG_JSON)){//内部DownloadActivity文件分享
//                intent.putExtra(Constants.BundleKey.TRANS_MSG,JsonUtils.getGson().fromJson(intent.getStringExtra(Constants.BundleKey.TRANS_MSG_JSON), IMMessage.class));
//            }
            startActivity = true;
        } else {
            String title = "";
            String content = "";
            ArrayList<String> iconPaths = new ArrayList<String>();
            ArrayList<String> videoPaths = new ArrayList<String>();
            ArrayList<String> filePaths = new ArrayList<String>();
            if (intent.getStringExtra(Intent.EXTRA_SUBJECT) != null) {
                title = intent.getStringExtra(Intent.EXTRA_SUBJECT);
            }
            if (intent.getStringExtra(Intent.EXTRA_TEXT) != null) {
                content = intent.getStringExtra(Intent.EXTRA_TEXT);
            }

            if (Intent.ACTION_SEND_MULTIPLE.equals(intent.getAction())) {
                final String type = intent.getType();
                if (type != null) {
                    if (type.startsWith("image/")) {
                        final ArrayList<Uri> icons = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                        if (icons != null) {
                            for (Uri icon : icons) {
                                final String imagePath = FileUtils.getPath(this, icon);
                                if (!TextUtils.isEmpty(imagePath)) {
                                    iconPaths.add(imagePath);
                                }
                            }
                        }
                    } else if (type.startsWith("video/")) {
                        final ArrayList<Uri> icons = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                        if (icons != null) {
                            for (Uri icon : icons) {
                                final String imagePath = FileUtils.getPath(this, icon);
                                if (!TextUtils.isEmpty(imagePath)) {
                                    videoPaths.add(imagePath);
                                }
                            }
                        }
                    }
                }
            } else if (Intent.ACTION_SEND.equals(intent.getAction())) {//处理分析逻辑

                // chrome分享会有截图
                final Uri screenshot_as_stream = intent.getParcelableExtra("share_screenshot_as_stream");
                if (screenshot_as_stream != null) {
                    final String imagePath = FileUtils.getPath(this, screenshot_as_stream);
                    if (imagePath != null) {
                        iconPaths.add(imagePath);
                    }
                }
                // UC浏览器分享会有截图
                final String file = intent.getStringExtra("file");
                if (file != null) {
                    final String imagePath = new File(file).getAbsolutePath();
                    iconPaths.add(imagePath);
                }

                final String type = intent.getType();
                if (type != null) {
                    if (type.startsWith("image")) {
                        final Uri icon = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                        final String imagePath = FileUtils.getPath(this, icon);
                        if (imagePath != null) {
                            iconPaths.add(imagePath);
                        }
                    } else if (type.startsWith("video/")) {
                        final Uri icon = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                        if (icon != null) {

                            final String imagePath = FileUtils.getPath(this, icon);
                            if (!TextUtils.isEmpty(imagePath)) {
                                videoPaths.add(imagePath);
                            }

                        }
                    } else if (type.equals("text/x-vcard")) {
                        title = getString(R.string.atom_ui_textbar_button_share_card);
                        content = "";
                        if (intent.getExtras().containsKey("vnd.android.cursor.item/name")) {
                            ArrayList<String> names = intent.getExtras()
                                    .getStringArrayList("vnd.android.cursor.item/name");
                            if (!ListUtil.isEmpty(names))
                                content += getString(R.string.atom_ui_tip_contact_name) + "：" + names.get(0) + "\n";
                        }
                        if (intent.getExtras().containsKey("vnd.android.cursor.item/phone_v2")) {
                            ArrayList<String> phones = intent.getExtras()
                                    .getStringArrayList("vnd.android.cursor.item/phone_v2");
                            if (!ListUtil.isEmpty(phones))
                                content += getString(R.string.atom_ui_tip_contact_mobile) + ": " + phones.get(0);
                        }
                    } else {// if (type.equalsIgnoreCase("text/plain"))
                        Uri turi = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                        String filepath = FileUtils.getPath(this, turi);
                        if(!TextUtils.isEmpty(filepath)) {
                            filePaths.add(filepath);
                        }
                    }
                }
                //有些分享链接再content中
                String url = intent.getStringExtra("url");//分享链接
                if(TextUtils.isEmpty(url)){
                    //判断content内是否有url
                    try {
                        String regex = "(\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])";
//                        String regex = "\\b(http[s]{0,1}|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
                        Pattern _pattern = Pattern.compile(regex);
                        Matcher _match   = _pattern.matcher(content);
                        if(_match.find()){
                            url = _match.group();
                            content.replace(url, "");
                        }
                    } catch (Exception e) {
                        Logger.e("分享url解析异常：" + e.getMessage() + "  content=" + content);
                        url = "";
                    }
                }

                if (TextUtils.isEmpty(url)) {
                    if (!TextUtils.isEmpty(title)) {
                        content = title + "\n\r" + content;
                    }
                    intent.putExtra(ShareReceiver.SHARE_TAG, true);
                } else {
                    ExtendMessageEntity entity = new ExtendMessageEntity();
                    entity.title = title;
                    entity.linkurl = url;
                    entity.img = file;
                    entity.desc = content;
                    String jsonStr = JsonUtils.getGson().toJson(entity);
                    intent.putExtra(ShareReceiver.SHARE_EXTRA_KEY, jsonStr);
                }

                intent.putExtra(ShareReceiver.SHARE_TEXT, content);
                if (!ListUtil.isEmpty(iconPaths)) {
                    intent.putStringArrayListExtra(ShareReceiver.SHARE_IMG, iconPaths);
                }

                if (!ListUtil.isEmpty(videoPaths)) {
                    intent.putStringArrayListExtra(ShareReceiver.SHARE_VIDEO, videoPaths);
                }
                if (!ListUtil.isEmpty(filePaths)) {
                    intent.putStringArrayListExtra(ShareReceiver.SHARE_FILE, filePaths);
                }
                intent.setClass(this, SearchUserActivity.class);
                intent.putExtra(Constants.BundleKey.IS_TRANS, true);
                intent.putExtra(Constants.BundleKey.IS_FROM_SHARE, true);
                startActivity = true;
            } else if(Intent.ACTION_VIEW.equals(intent.getAction())) {//处理文件打开方式使用qtalk发送好友
                Uri uri = intent.getData();
                if(uri == null) {
                    return;
                }
                if(CommonConfig.schema.equalsIgnoreCase(uri.getScheme())) {
                    return;
                }
                final String type = intent.getType();
                if(TextUtils.isEmpty(type)) {
                    String filepath = FileUtils.getPath(this, uri);
                    if(!TextUtils.isEmpty(filepath)) {
                        filePaths.add(filepath);
                    }
                } else {
                    if (type.startsWith("image")) {
                        String imagePath = FileUtils.getPath(this, uri);
                        if(!TextUtils.isEmpty(imagePath)) {
                            iconPaths.add(imagePath);
                        }
                    } else if (type.startsWith("video/")) {
                        final String videoPath = FileUtils.getPath(this, uri);
                        if (!TextUtils.isEmpty(videoPath)) {
                            videoPaths.add(videoPath);
                        }
                    } else {// if (type.equalsIgnoreCase("text/plain"))
                        String filepath = FileUtils.getPath(this, uri);
                        if(!TextUtils.isEmpty(filepath)) {
                            filePaths.add(filepath);
                        }
                    }
                }
                intent.putExtra(ShareReceiver.SHARE_TAG, true);
                if (!ListUtil.isEmpty(iconPaths)) {
                    intent.putStringArrayListExtra(ShareReceiver.SHARE_IMG, iconPaths);
                }

                if (!ListUtil.isEmpty(videoPaths)) {
                    intent.putStringArrayListExtra(ShareReceiver.SHARE_VIDEO, videoPaths);
                }
                if (!ListUtil.isEmpty(filePaths)) {
                    intent.putStringArrayListExtra(ShareReceiver.SHARE_FILE, filePaths);
                }
                intent.setClass(this, SearchUserActivity.class);
                intent.putExtra(Constants.BundleKey.IS_TRANS, true);
                intent.putExtra(Constants.BundleKey.IS_FROM_SHARE, true);
                startActivity = true;
            }
        }
    }

    private int retry = 0;

    private void initActivityLifeCycle() {
        AppFrontBackHelper helper = new AppFrontBackHelper();
        helper.register(getApplication(), new AppFrontBackHelper.OnAppStatusListener() {
            @Override
            public void onFront() {
                //应用切到前台处理
                Logger.i("应用切到前台");
                CurrentPreference.getInstance().setBack(false);


                if (!CommonConfig.isQtalk && TextUtils.isEmpty(DataUtils.getInstance(TabMainActivity.this).getPreferences(Constants.Preferences.qchat_qvt, ""))) {
                    IMUserDefaults.getStandardUserDefaults().newEditor(TabMainActivity.this).removeObject(Constants.Preferences.usertoken).synchronize();
                }
                boolean autoLogin = com.qunar.im.protobuf.common.CurrentPreference.getInstance().isAutoLogin();

                if (autoLogin) {
                    Logger.i("检查是否可以自动登录:"+connectionUtil.isCanAutoLogin());
                    if (!connectionUtil.isCanAutoLogin()) {
                        startLoginView();
                        return;
                    } else {

                        if (!ConnectionUtil.getInstance().isLoginStatus()) {
                            retry = 0;
                            com.qunar.im.protobuf.common.CurrentPreference.getInstance().setQvt(DataUtils.getInstance(CommonConfig.globalContext).getPreferences(Constants.Preferences.qchat_qvt, ""));
                            checkHealth();
                            presenter.loadPreference(TabMainActivity.this, false);
                        }
//                        presenter.loadPreference(TabMainActivity.this, false);
//                        loginPresenter.autoLogin();
                    }
                } else {
                    startLoginView();
                }


            }

            @Override
            public void onBack() {
                //应用切到后台处理
                Logger.i("应用切到后台");
                CurrentPreference.getInstance().setBack(true);
            }
        });
    }

    private void checkHealth() {
        Logger.i("网络出现问题第"+retry+"次");
        if (retry > 5) {
            Logger.i("应用检查网络超过5次出现问题");
            return;
        }
        HttpUtil.checkHealth(new ProtocolCallback.UnitCallback<Boolean>() {
            @Override
            public void onCompleted(Boolean aBoolean) {
                retry = 0;
                ConnectionUtil.getInstance().reConnectionForce();
            }

            @Override
            public void onFailure(String errMsg) {
                retry++;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                            checkHealth();
                        }catch (Exception e){
                            Logger.i("检查网络时出错"+e.getMessage());
                        }
                    }
                }).start();

            }
        });
    }

    @Override
    public void invokeDefaultOnBackPressed() {
        onBackPressed();
    }

}
