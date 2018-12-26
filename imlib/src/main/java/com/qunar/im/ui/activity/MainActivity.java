package com.qunar.im.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.qunar.im.ui.util.QRRouter;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.jsonbean.ExtendMessageEntity;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.presenter.ILoginPresenter;
import com.qunar.im.base.presenter.IMainPresenter;
import com.qunar.im.base.presenter.factory.LoginFactory;
import com.qunar.im.base.presenter.impl.MainPresenter;
import com.qunar.im.base.presenter.views.ILoginView;
import com.qunar.im.base.presenter.views.IMainView;
import com.qunar.im.base.shortutbadger.ShortcutBadger;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.base.util.FileUtils;
import com.qunar.im.base.util.IMUserDefaults;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.ListUtil;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.ProfileUtils;
import com.qunar.im.base.util.Utils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.permission.PermissionCallback;
import com.qunar.im.permission.PermissionDispatcher;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.protobuf.common.LoginType;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.FragmentPagerItem;
import com.qunar.im.ui.adapter.FragmentPagerItemAdapter;
import com.qunar.im.ui.adapter.FragmentPagerItems;
import com.qunar.im.ui.broadcastreceivers.ShareReceiver;
import com.qunar.im.ui.fragment.BuddiesFragment;
import com.qunar.im.ui.fragment.ConversationFragment;
import com.qunar.im.ui.fragment.PersonalInfoFragment;
import com.qunar.im.ui.services.PullPatchService;
import com.qunar.im.ui.services.PushServiceUtils;
import com.qunar.im.ui.util.ParseErrorEvent;
import com.qunar.im.ui.util.UpdateManager;
import com.qunar.im.ui.view.IconView;
import com.qunar.im.ui.view.OnDoubleClickListener;
import com.qunar.im.ui.view.QtActionBar;
import com.qunar.im.ui.view.tabview.MainTabProvider;
import com.qunar.im.ui.view.tabview.MainTabView;
import com.qunar.im.ui.view.tabview.SmartTabLayout;
import com.qunar.im.ui.view.tabview.SmartTabStrip;
import com.qunar.im.ui.view.zxing.activity.CaptureActivity;
import com.qunar.im.utils.QRUtil;
import com.qunar.im.utils.QtalkStringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

import static com.qunar.im.common.CommonConfig.globalContext;

/**
 * Created by xinbo.wang on 2015/2/28.
 */
public class MainActivity extends IMBaseActivity implements PermissionCallback, IMainView, PopupMenu.OnMenuItemClickListener {
    private static final String TAG = "MainActivity";

    private static final int SCAN_REQUEST = PermissionDispatcher.getRequestCode();
    private static final int CHECK_UPDATE = PermissionDispatcher.getRequestCode();


    private Button chatStart;

    //pb核心管理类
    private ConnectionUtil connectionUtil;
    SmartTabLayout tab;
    ViewPager pager;
    DrawerLayout drawer_layout;

    private Bundle selfBundl;
    private boolean merchant = false, startActivity;
    PersonalInfoFragment menuFragment;
    private DrawerLayout.DrawerListener mDrawerToggle;
    protected HandleMainEvent handleMainEvent = new HandleMainEvent();
    //主界面主持
    private IMainPresenter mMainPresenter;
    //登陆界面主持
    ILoginPresenter loginPresenter;

    private AlertDialog noticeLoginDialog;
    private ProgressDialog progressDialog;


//    public void updateOpsIndicator(boolean hasMsg) {
//        MainTabView view = (MainTabView) tab.getTabAt(2);
//        if (view == null) return;
//        TextView textView = view.getUnreadView();
//        if (hasMsg) {
//            textView.setVisibility(View.VISIBLE);
//            textView.setText(" ");
//        } else {
//            textView.setVisibility(View.GONE);
//        }
//
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Fresco.initialize(this);
        //bugly tag
//        CrashReportUtils.getInstance().setUserTag(55093);
        setContentView(R.layout.atom_ui_activity_main);
        connectionUtil = ConnectionUtil.getInstance();

        //隐式前台服务代码调试使用 建议保留
//        PushServiceUtils.startSaveService(this);
        bindViews();
        super.titleShow = false;
        LogUtil.i(TAG, "onCreate");
        selfBundl = savedInstanceState;
        //记载请勿打扰列表???
//        CurrentPreference.getInstance().setContext(getApplicationContext());
//        ConversationManagePresenter.getInstance().loadDoNotDisturbList();
//
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
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }

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
                        myActionBar.getSelfGravatarImage().setImageURI(nick.getHeaderSrc(), true);
                    }
                });

            }

            @Override
            public void LoginFailure(int str) {
                if (MainActivity.this.isFinishing()) {
                    return;
                }
                if (noticeLoginDialog != null && noticeLoginDialog.isShowing()) {
                    noticeLoginDialog.dismiss();
                    return;
                }

                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("温馨提示");
                dialog.setMessage("账号由于某些原因被迫下线,请重新登陆,错误代码:"+ ParseErrorEvent.getError(str,MainActivity.this));
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ConnectionUtil.clearLastUserInfo();
                        setLoginResult(false, 0);

                        dialog.dismiss();
                    }
                });
                dialog.setCancelable(false);
                noticeLoginDialog = dialog.show();
            }

            @Override
            public void connectInterrupt() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myActionBar.getTitleTextview().setText("已断开连接");
                    }
                });
            }

            @Override
            public void noNetWork() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myActionBar.getTitleTextview().setText("没有网络连接");
                    }
                });
            }

            @Override
            public void tryToConnect(final String str) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        myActionBar.getTitleTextview().setText("尝试连接..." + str);
                        myActionBar.getTitleTextview().setText("连接中...");
                    }
                });
            }
        });

        EventBus.getDefault().register(handleMainEvent);

//        initConfig();
        initView();
        initActionBarRight();
        //分享
        handleShareAction(getIntent());
        injectExtra(getIntent());

        initActivityLifeCycle();

        File groupGravatar = new File(FileUtils.getExternalFilesDir(globalContext), "gravatar");
        if (!groupGravatar.exists()) {
            groupGravatar.mkdirs();
        }

        checkNotificationDialog();
    }

    private void initActivityLifeCycle() {
        getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                //切换到前台
                LogUtil.d(TAG, "切换到前台");
                if (connectionUtil.isLoginStatus()) {
                    connectionUtil.sendHeartBeat();
                }

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                //切换到后台

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

//    public void PBinit(){
//        connectionUtil = ConnectionUtil.getInstance(getApplicationContext());
//        connectionUtil.setNavigationUrl("https://qt.qunar.com/package/static/qtalk/nav");
//    }


    private void injectExtra(Intent intent) {
        Uri uri = intent.getData();
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
        }
    }

//    private void initConfig() {
//        presenter.loadPreference(this, false);
//        presenter.changeProcess2Failed();
//        presenter.checkSendingLine();
//        if (!TextUtils.isEmpty(CurrentPreference.getInstance().getUserId())) {
////            presenter.getMyCapability();
//        }
//        merchant = CurrentPreference.getInstance().merchants();
//        changeSound();
//    }

//    private void changeSound() {
//        if (!CommonConfig.isQtalk && merchant) {
//            int soundId = -1;
//            try {
//                soundId = QtalkApplication.getContext().
//                        getResources().
//                        getIdentifier("new_consult", "raw", QtalkApplication.getContext().getPackageName());
//            } catch (Exception e) {
//                LogUtil.e(TAG, e.getMessage());
//            }
//
//            if (soundId > 0) {
//                MediaUtils.unLoadNewMsgSound();
//                MediaUtils.loadNewMsgSound(this, soundId);
//            }
//        }
//    }


    @Override
    public void onStart() {
        super.onStart();
    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.d(TAG, "on new intent");
        setIntent(intent);
        //分享
        handleShareAction(intent);
        injectExtra(intent);
    }

    public void handleShareAction(Intent intent) {
        if (intent == null) return;
        if (intent.hasExtra(ShareReceiver.SHARE_EXTRA_KEY)) {
            intent.setClass(this, SearchUserActivity.class);
            intent.putExtra(Constants.BundleKey.IS_TRANS, true);
            intent.putExtra(Constants.BundleKey.IS_FROM_SHARE, true);
            startActivity = true;
        } else {
            String title = "";
            String content = "";
            ArrayList<String> iconPaths = new ArrayList<String>();
            ArrayList<String> videoPaths = new ArrayList<String>();
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
            } else if (Intent.ACTION_SEND.equals(intent.getAction())) {

                // chrome分享会有截图
                final Uri screenshot_as_stream = intent.getParcelableExtra("share_screenshot_as_stream");
                if (screenshot_as_stream != null) {
                    final String imagePath = FileUtils.getPath(this, screenshot_as_stream);
                    if (imagePath != null) {
                        iconPaths.add(imagePath);
                    }
//                    try {
//                        content = intent.getStringExtra(Intent.EXTRA_SUBJECT) + " " + intent.getStringExtra(Intent.EXTRA_TEXT);
//                    } catch (Exception e) {
//                        // do nothing
//                    }
                }
                // UC浏览器分享会有截图
                final String file = intent.getStringExtra("file");
                if (file != null) {
                    final String imagePath = new File(file).getAbsolutePath();
                    iconPaths.add(imagePath);
//                    try {
//                        content = intent.getStringExtra(Intent.EXTRA_SUBJECT) + " " + intent.getStringExtra(Intent.EXTRA_TEXT);
//                    } catch (Exception e) {
//                        // do nothing
//                    }
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
                        title = "分享通讯录联系人";
                        content = "";
                        if (intent.getExtras().containsKey("vnd.android.cursor.item/name")) {
                            ArrayList<String> names = intent.getExtras()
                                    .getStringArrayList("vnd.android.cursor.item/name");
                            if (!ListUtil.isEmpty(names))
                                content += "姓名: " + names.get(0) + "\n";
                        }
                        if (intent.getExtras().containsKey("vnd.android.cursor.item/phone_v2")) {
                            ArrayList<String> phones = intent.getExtras()
                                    .getStringArrayList("vnd.android.cursor.item/phone_v2");
                            if (!ListUtil.isEmpty(phones))
                                content += "电话: " + phones.get(0);
                        }
                    }
                }
                //有些分享链接再content中
                String url = intent.getStringExtra("url");//分享链接
//                if(TextUtils.isEmpty(url) && !TextUtils.isEmpty(content)){
//                    String[] infos = content.split(" ");//分解content，看是否有url
//                    if(infos != null && infos.length > 1){
//                        if(Utils.IsUrl(infos[infos.length - 1])){
//                            url = infos[infos.length - 1];
//                            if(infos.length > 2){
//                                title = infos[0];
//                                content = infos[1];
//                            }
//                        }
//                    }
//                }

                if(TextUtils.isEmpty(url)){
                    if (!TextUtils.isEmpty(title)) {
                        content = title + "\n\r" + content;
                    }
                    intent.putExtra(ShareReceiver.SHARE_TAG, true);
                }else{
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
                intent.setClass(this, SearchUserActivity.class);
                intent.putExtra(Constants.BundleKey.IS_TRANS, true);
                intent.putExtra(Constants.BundleKey.IS_FROM_SHARE, true);
                startActivity = true;
            }
        }
    }

    protected String getFilePathByUri(Uri icon) {
        String filePath = "";
        if (icon != null && "content".equals(icon.getScheme())) {
            Cursor cursor = null;
            try {
                cursor = this
                        .getContentResolver()
                        .query(icon,
                                new String[]{MediaStore.Video.VideoColumns.DATA},
                                null, null, null);
                cursor.moveToFirst();
                filePath = cursor.getString(0);
            }catch (Exception e){
                filePath = icon.getPath();
            }finally {
                if(cursor != null){
                    cursor.close();
                }
            }
        } else if (icon != null) {
            filePath = icon.getPath();
        }

        return filePath;
    }


    private void bindViews() {

        drawer_layout = (android.support.v4.widget.DrawerLayout) findViewById(R.id.drawer_layout);
        tab = (com.qunar.im.ui.view.tabview.SmartTabLayout) findViewById(R.id.tab);
        pager = (android.support.v4.view.ViewPager) findViewById(R.id.pager);
    }

    private void initActionBarRight() {
        int pixel = Utils.dipToPixels(this, 24);
        int marginPixels = Utils.dipToPixels(this, 16);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(pixel, pixel);
        lp.setMargins(0, 0, marginPixels, 0);
        IconView searchView = new IconView(this);
        searchView.setTextColor(getResources().getColor(R.color.atom_ui_white));
        searchView.setText(R.string.atom_ui_ic_search);
        searchView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        searchView.setLayoutParams(lp);
        myActionBar.getRightContainer().addView(searchView);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //二维码在线调试代码 建议保留
//                Intent intent = new Intent();
//                intent.setAction("MLY");
//                intent.putExtra("info","Go");
//                sendBroadcast(intent);
//                QRUtil.handleQRCode("qtalk://robot?id=qmeetrobot&type=robot&content=159h&msgType=method", MainActivity.this);
                //// TODO: 2017/9/11 原版代码
//                if (!CommonConfig.isQtalk) {
//                    Intent i = new Intent("android.intent.action.VIEW",
//                            Uri.parse(CommonConfig.schema + "://rnsearch"));
//                    startActivity(i);
//                } else {
//                    Intent intent = new Intent(MainActivity.this, SearchUserActivity.class);
//                    startActivity(intent);
//                }
                // TODO: 2017/9/11 RN代码 应该用这个

                if (CommonConfig.isQtalk) {
                    Intent i = null;
                    try {
                        i = new Intent(MainActivity.this, (Class<? extends Fragment>) Class.forName("com.qunar.im.camelhelp.activity.QTalkSearchActivity"));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
//                    Intent i = new Intent("android.intent.action.VIEW",
//                            Uri.parse(CommonConfig.schema+"://rnsearch"));
                    startActivity(i);
                } else {
                    Intent intent = new Intent(MainActivity.this, SearchUserActivity.class);
                    startActivity(intent);
                }
            }
        });
        IconView plusMenuView = new IconView(this);
        plusMenuView.setTextColor(getResources().getColor(R.color.atom_ui_white));
        plusMenuView.setText(R.string.atom_ui_ic_plus);
        plusMenuView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        plusMenuView.setLayoutParams(new LinearLayout.LayoutParams(pixel, pixel));
        myActionBar.getRightContainer().addView(plusMenuView);
        final PopupMenu popupMenu = new PopupMenu(MainActivity.this, plusMenuView);
        popupMenu.inflate(R.menu.atom_ui_menu_main_qtalk);
        try {
            Field field = PopupMenu.class.getDeclaredField("mPopup");
            field.setAccessible(true);
            MenuPopupHelper helper = (MenuPopupHelper) field.get(popupMenu);
            helper.setForceShowIcon(true);
        } catch (NoSuchFieldException e) {
            LogUtil.e(TAG, "ERROR", e);
        } catch (IllegalAccessException e) {
            LogUtil.e(TAG, "ERROR", e);
        }
        popupMenu.setOnMenuItemClickListener(this);
        plusMenuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.show();
            }
        });

    }


    void initView() {

//        chatStart = (Button) findViewById(R.id.chatStart);
//        chatStart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, PbChatActivity.class);
////                jid:hubo.hu@ejabhost1,isfromfalse,real:null
//                intent.putExtra("jid", "hubo.hu@ejabhost1");
//                intent.putExtra("isFromChatRoom", false);
//                intent.putExtra("realUser", "");
//                startActivity(intent);
////                finish();
//            }
//        });


        QtActionBar actionBar = (QtActionBar) this.findViewById(R.id.my_action_bar);
//        setActionBar(actionBar);
        myActionBar.getLeftButton().setVisibility(View.GONE);
        myActionBar.getTitleTextview().setVisibility(View.VISIBLE);
        myActionBar.getSelfGravatarImage().setVisibility(View.VISIBLE);
        myActionBar.getSelfGravatarImage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //menu.toggle(true);
                if (drawer_layout.isDrawerOpen(Gravity.LEFT)) {
                    drawer_layout.closeDrawer(Gravity.LEFT);
                } else {
                    drawer_layout.openDrawer(Gravity.LEFT);
                }
            }
        });
        if (menuFragment == null) {
            menuFragment = new PersonalInfoFragment();
            mDrawerToggle = new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                    if (slideOffset > 0) {
                        float ratio = 1 - slideOffset;
//                        myActionBar.getSelfGravatarImage().setImageAlpha((int) (myActionBar.getSelfGravatarImage().getImageAlpha() * ratio));
                    }
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    menuFragment.showMerchant();
                    menuFragment.showUserId();
                    menuFragment.showNick();
                    menuFragment.showMood();
                    menuFragment.showGravantar();
                    //playAnimation(false);
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    //playAnimation(true);

                }

                @Override
                public void onDrawerStateChanged(int newState) {
                    if (newState == DrawerLayout.STATE_IDLE) {
//                        myActionBar.getSelfGravatarImage().setImageAlpha(255);
                    }
                }
            };
            drawer_layout.setDrawerListener(mDrawerToggle);
            getSupportFragmentManager().beginTransaction().replace(R.id.left_drawer, menuFragment)
                    .addToBackStack(null)
                    .commit();
        }

        initPageView();
        if (selfBundl != null) {
            pager.setCurrentItem(selfBundl.getInt("tab"));
        }
        checkUpdate();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putInt("tab", pager.getCurrentItem());
    }

    //初始化主界面的几个fragment 加上上边tab选项卡
    private void initPageView() {
        final FragmentPagerItems pages = new FragmentPagerItems(this);
        pages.add(FragmentPagerItem.of(getString(R.string.atom_ui_tab_message), ConversationFragment.class));
        pages.add(FragmentPagerItem.of(getString(R.string.atom_ui_tab_contacts), BuddiesFragment.class));
        Class<? extends Fragment> clazz = null;
        String thirdTab = "";
        try {
            //这里其实是去调用一个工作区片段 参照正式版工作区
            thirdTab = CommonConfig.isQtalk ? getString(R.string.atom_ui_tab_work) : getString(R.string.atom_ui_tab_explore);
            clazz = (Class<? extends Fragment>) Class.forName("com.qunar.im.camelhelp.DiscoverFragment");
        } catch (Exception e) {
            LogUtil.e(TAG, "ERROR", e);
        }
        if (clazz != null) {

            pages.add(FragmentPagerItem.of(thirdTab, clazz));
        }

        final FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), pages);
        pager.setOffscreenPageLimit(2);
        pager.setAdapter(adapter);
        tab.setCustomTabView(new MainTabProvider());
        tab.setViewPager(pager);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int selectPos = 0;
            int primary_color = getResources().getColor(R.color.atom_ui_primary_color);
            int text_color = getResources().getColor(R.color.atom_ui_light_gray_66);
            int scrollState = ViewPager.SCROLL_STATE_IDLE;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (positionOffset > 0 && scrollState == ViewPager.SCROLL_STATE_DRAGGING) {
                    if (position == selectPos) {
                        TextView curview = (TextView) tab.getTabAt(position).findViewById(R.id.tab_name);
                        TextView nextView = (TextView) tab.getTabAt(position + 1).findViewById(R.id.tab_name);
                        nextView.setTextColor(SmartTabStrip.blendColors(primary_color, text_color, positionOffset));
                        curview.setTextColor(SmartTabStrip.blendColors(text_color, primary_color, positionOffset));


//                        curview.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                            }
//                        });
                    } else if (position < selectPos) {
                        TextView curview = (TextView) tab.getTabAt(selectPos).findViewById(R.id.tab_name);
                        TextView nextView = (TextView) tab.getTabAt(position).findViewById(R.id.tab_name);
                        curview.setTextColor(SmartTabStrip.blendColors(primary_color, text_color, positionOffset));
                        nextView.setTextColor(SmartTabStrip.blendColors(text_color, primary_color, positionOffset));

                    }

                } else if (positionOffset == 0 && scrollState == ViewPager.SCROLL_STATE_SETTLING) {
                    resetTitle(position);

                }

                if (position == 0) {
                    MainTabView mainTabView = (MainTabView) tab.getTabAt(0);
//                    mainTabView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Toast.makeText(MainActivity.this,"单击了标签",Toast.LENGTH_LONG).show();
//                        }
//                    });
                    mainTabView.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
                        @Override
                        public void onDoubleClick() {
//                            Toast.makeText(MainActivity.this,"双击了标签",Toast.LENGTH_LONG).show();
                            ((ConversationFragment) adapter.getPage(0)).MoveToUnread();
                        }

                        @Override
                        public void onSingleClick() {

                        }
                    }));
                } else {
                    MainTabView mainTabView = (MainTabView) tab.getTabAt(0);
                    mainTabView.setOnTouchListener(null);
                    mainTabView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pager.setCurrentItem(0);
                        }
                    });

                }
            }

            @Override
            public void onPageSelected(int position) {
                if (scrollState == ViewPager.SCROLL_STATE_SETTLING) {
                    resetTitle(position);
                    selectPos = position;
                }
            }

            private void resetTitle(int position) {
                for (int i = 0; i < pages.size(); i++) {
                    View tabAt = tab.getTabAt(i);
                    if (tabAt != null) {
                        TextView view = (TextView) tabAt.findViewById(R.id.tab_name);
                        if (i == position) {
                            view.setTextColor(primary_color);
                        } else {
                            view.setTextColor(text_color);
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                scrollState = state;
            }
        });
        //对消息标签设置双击事件

    }

    //更新tab未读消息条目数
    public void setTabViewUnReadCount(int count) {
        MainTabView mainTabView = (MainTabView) tab.getTabAt(0);
        mainTabView.setUnreadCount(count);
//        TextView view = (TextView) tab.getTabAt(0).findViewById(R.id.textView_new_msg);
    }


    private void checkUpdate() {
        PermissionDispatcher.
                requestPermissionWithCheck(this, new int[]{PermissionDispatcher.REQUEST_WRITE_EXTERNAL_STORAGE,
                                PermissionDispatcher.REQUEST_READ_EXTERNAL_STORAGE}, this,
                        CHECK_UPDATE);
    }

    /**
     * 检查通知是否打开
     */
    private void checkNotificationDialog(){
        boolean isNoneedCheck = DataUtils.getInstance(this).getPreferences("CheckNotification", false);
        if(!NotificationManagerCompat.from(this).areNotificationsEnabled() && !isNoneedCheck){
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("建议打开通知开关");

            builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {
                    dialog.dismiss();
//                    Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS, Uri.parse("package:" + MainActivity.this.getPackageName()));
//                    startActivity(intent);

                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Intent intent = new Intent();
                        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                        intent.putExtra("app_package", MainActivity.this.getPackageName());
                        intent.putExtra("app_uid", MainActivity.this.getApplicationInfo().uid);
                        startActivity(intent);
                    } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
                        startActivity(intent);
                    }


                }
            });
            builder.setNeutralButton("不再提醒", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {
                    DataUtils.getInstance(MainActivity.this).putPreferences("CheckNotification", true);
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
    }

    void scanQrCode() {
        Intent scanQRCodeIntent = new Intent(getApplicationContext(), CaptureActivity.class);
        startActivityForResult(scanQRCodeIntent, SCAN_REQUEST);
    }

    public void switchAccount() {
        startActivity(new Intent(this, AccountSwitchActivity.class));
        drawer_layout.closeDrawer(Gravity.LEFT);
    }

    @Override
    public void onBackPressed() {
        if (drawer_layout.isDrawerOpen(Gravity.LEFT)) {
            drawer_layout.closeDrawer(Gravity.LEFT);
        } else {
            Utils.jump2Desktop(MainActivity.this);
            if (com.qunar.im.protobuf.common.CurrentPreference.getInstance().isTurnOnPsuh()) {
                //PushServiceUtils.startDPushService(this.getApplicationContext());
                PushServiceUtils.startAMDService(this);
            }
        }
    }


//    private void syncOpsPush() {
//        if (CommonConfig.isQtalk
//                && CurrentPreference.getInstance().isLogin()) {
//            Intent intent = new Intent();
//            intent.setAction(Constants.BroadcastFlag.GET_UNREAD_OPS_MSG);
//            Utils.sendLocalBroadcast(intent, this.getApplicationContext());
//        }
//
//        ThirdProviderAPI.getCountOfLeaveMessage(new ProtocolCallback.UnitCallback<Integer>() {
//            @Override
//            public void onCompleted(Integer integer) {
//                if (integer > 0) {
//                    EventBus.getDefault().post(new LeaveMsgEvent());
//                    getHandler().post(new Runnable() {
//                        @Override
//                        public void run() {
//                            updateOpsIndicator(true);
//                        }
//                    });
//
//                }
//            }
//
//            @Override
//            public void onFailure() {
//
//            }
//        });
//    }

    @Override
    protected void onResume() {
        super.onResume();
//        IMLogic.instance().reback();
        Intent clearIntent = new Intent();
        clearIntent.setAction("com.qunar.ops.push.CLEAR_NOTIFY");
        clearIntent.setPackage(this.getApplicationContext().getPackageName());
        Utils.sendLocalBroadcast(clearIntent, this.getApplicationContext());
        //判断商户?
//        if (merchant != CurrentPreference.getInstance().merchants()) {
//            merchant = CurrentPreference.getInstance().merchants();
//            initPageView();
//            changeSound();
//        }
////        mMainPresenter.getUnreadConversationMessage();
//        syncOpsPush();

        //获取未读消息数量
        mMainPresenter.getUnreadConversationMessage();
        // 原版

        //判断现在的登陆状态,如果没有登陆去登陆
        if (!connectionUtil.isLoginStatus() || !connectionUtil.isConnected()) {
            login();
        }
//        if (CurrentPreference.getInstance().isLogin()) {
////            restoreTitle();
//        } else {
//
//        }
//        if (connectionUtil.isLoginStatus()) {
//            restoreTitle();
//        } else {
////            login();
//            pbLogin();
//        }

        if (startActivity) {
            startActivity(getIntent());
            startActivity = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * 根据type 展示不同类型的登陆界面
     */
    private void showLoginView() {

    }

    private void startLoginView() {
        if (noticeLoginDialog != null && noticeLoginDialog.isShowing()) {
            noticeLoginDialog.dismiss();
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
//    private void startLoginView() {
//        if (!CommonConfig.loginViewHasShown) {
//            //动态切换客户端
//            new AlertDialog.Builder(this)
//                    .setIcon(R.drawable.atom_ui_close)
//                    .setTitle("选择打开的客户端")
//                    .setMessage("")
//                    .setCancelable(false)
//                    .setPositiveButton("Qtalk", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            CommonConfig.isQtalk = true;
//                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                            startActivity(intent);
//                        }
//                    })
//                    .setNegativeButton("Qchat", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            CommonConfig.isQtalk = false;
//                            Intent intent = new Intent(MainActivity.this, QChatLoginActivity.class);
//                            startActivity(intent);
//                        }
//                    }).show();
//        }
//    }

    //pb版本登陆
//    public synchronized void pbLogin() {
//        //判断我的登陆状态 如果登陆了为true
//        if (connectionUtil.isLoginStatus()) {
//            return;
//        }
//        //设置当前title俩接种
//        myActionBar.getTitleTextview().setText("PB连接中...");
//        //获取是否开启了 自动登录
//        boolean autoLogin = com.qunar.im.protobuf.common.CurrentPreference.getInstance().isAutoLogin();
//        //判断是不是自动登录
//        if (autoLogin) {
//            //是自动登录,但是不能自动登录
//            if (!connectionUtil.isCanAutoLogin()) {
////                connectionUtil.pbLogin("","");
//                //这句代码应该没啥用
//                presenter.loadPreference(this, false);
//                //显示登录选择dialog
//                startLoginView();
//                return;
//            }
//            initMyProfile(false);
//            //如果不是登录状态
//            if (!connectionUtil.isLoginStatus()) {
//                //开始自动登录,并挂载消息
//                mProgressDialog.show();
//                connectionUtil.pbLogin("", "");
//                connectionUtil.addEvent(this, QtalkEvent.LOGIN_EVENT);
//                connectionUtil.addEvent(this, QtalkEvent.LOGIN_FAILED);
//            } else {
//
//            }
//        } else {
//            startLoginView();
//        }
//    }

    public synchronized void login() {
//        if (CurrentPreference.getInstance().isLogin())
//            return;
        //登陆时,设置title为连接中
//        myActionBar.getTitleTextview().setText("连接中...");
        //写死在配置类里面自动登录为true
        //TODO qchatv7.20 bug 需要判断没有qvt重新登录 正常不会没有无qvt的情况
        if (!CommonConfig.isQtalk && TextUtils.isEmpty(DataUtils.getInstance(this).getPreferences(Constants.Preferences.qchat_qvt, ""))) {
            IMUserDefaults.getStandardUserDefaults().newEditor(this).removeObject(Constants.Preferences.usertoken).synchronize();
        }
        boolean autoLogin = com.qunar.im.protobuf.common.CurrentPreference.getInstance().isAutoLogin();
        if (autoLogin) {
            if (!connectionUtil.isCanAutoLogin()) {
                startLoginView();
                return;
            } else {
                loginPresenter.autoLogin();
            }
//
            //此处逻辑有问题?!!!
//            if (!CurrentPreference.getInstance().isLogin()) {
////                mProgressDialog = new ProgressDialog(this);
////                mProgressDialog.setTitle("验证");
////                mProgressDialog.setMessage("尝试自动连接中...");
////                mProgressDialog.setCanceledOnTouchOutside(false);
////                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
////                mProgressDialog.show();
//
//            } else {
//                EventBus.getDefault().post(new EventBusEvent.LoginComplete(false));
//            }
        } else {
            startLoginView();
        }
    }


//    /**
//     * 更新自己的用户名和头像
//     *
//     * @param isForce 是否强制更新
//     */
//    private void initMyProfile(boolean isForce) {
//        IPersonalInfoPresenter personalInfoPresenter = PersonalInfoFactory.getPersonalPresenter();
//        personalInfoPresenter.setPersonalInfoView(new MyPersonalView() {
//            @Override
//            public SimpleDraweeView getImagetView() {
//                return myActionBar.getSelfGravatarImage();
//            }
//
//            @Override
//            public String getJid() {
//                return QtalkStringUtils.userId2Jid(CurrentPreference.getInstance().getUserId());
//            }
//        });
//        personalInfoPresenter.getVCard(isForce);
//    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (!RunningApp.isForeground(this, "com.qunar.im.ui.activity.MainActivity") && CurrentPreference.getInstance().isTurnOnPsuh()) {
//            //PushServiceUtils.startAMDService(this.getApplicationContext());
//            IMLogic.instance().leave();
//        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(handleMainEvent);
        super.onDestroy();
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

    public void showMyInfo() {
        String userId = CurrentPreference.getInstance().getPreferenceUserId();
        Intent intent = new Intent(this, PersonalInfoActivity.class);
        intent.putExtra("jid", QtalkStringUtils.userId2Jid(userId));
        startActivity(intent);
        drawer_layout.closeDrawer(Gravity.LEFT);
    }

    public void showSetting() {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.atom_ui_slide_right, R.anim.atom_ui_slide_right);
        drawer_layout.closeDrawer(Gravity.LEFT);
    }

    public void showHongBao() {
        if (!TextUtils.isEmpty(QtalkNavicationService.MY_HONGBAO)) {
            Uri uri = Uri.parse(QtalkNavicationService.MY_HONGBAO);
            Intent intent = new Intent(this, QunarWebActvity.class);
            intent.putExtra(Constants.BundleKey.WEB_FROM, Constants.BundleValue.HONGBAO);
            intent.setData(uri);
            startActivity(intent);
        }
        drawer_layout.closeDrawer(Gravity.LEFT);
    }

    public void showHongBaoBalance() {
        if (!TextUtils.isEmpty(QtalkNavicationService.HONGBAO_BALANCE)) {
            Uri uri = Uri.parse(QtalkNavicationService.HONGBAO_BALANCE);
            Intent intent = new Intent(this, QunarWebActvity.class);
            intent.putExtra(Constants.BundleKey.WEB_FROM, Constants.BundleValue.HONGBAO);
            intent.setData(uri);
            startActivity(intent);
        }
        drawer_layout.closeDrawer(Gravity.LEFT);
    }

    public void showHelp() {
        Intent intent = new Intent(this, QunarWebActvity.class);
        intent.setData(Uri.parse(QtalkNavicationService.getInstance().getSimpleapiurl() + "/helps/faq.html"));
        startActivity(intent);
    }


    @Override
    public void responsePermission(int requestCode, boolean granted) {
        if (!granted){
            Toast.makeText(this, getString(R.string.atom_ui_tip_request_permission), Toast.LENGTH_SHORT).show();
            return;
        }
        if (requestCode == SCAN_REQUEST) {
            scanQrCode();
        } else if (requestCode == CHECK_UPDATE) {
            UpdateManager.getUpdateManager().checkAppUpdate(this, false);
            //热修复加载补丁
            PullPatchService.runPullPatchService(this);
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
                myActionBar.getTitleTextview().setText("已连接");
            }
        });
        //登录成功，启动push
        boolean isRegister = presenter.checkUnique();
        if (CurrentPreference.getInstance().isTurnOnPsuh()) {
            if (isRegister) {
                PushServiceUtils.stopAMDService(MainActivity.this);
            }
            PushServiceUtils.startAMDService(MainActivity.this);
        } else {
            PushServiceUtils.stopAMDService(MainActivity.this);
        }
    }

    //同步中方法
    @Override
    public void synchronousing() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                myActionBar.getTitleTextview().setText("同步中...");
            }
        });
    }

    @Override
    public void refreshShortcutBadger(int count) {
        //刷新未读角标，小米逻辑在pushReceiver里单独处理
        if (!Utils.isMIUI()) {
            boolean success = ShortcutBadger.applyCount(this, count);
            Logger.i("ShortcutBadger", "Set count=" + count + ", success=" + success);
        }

    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void showDialog(String str) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("温馨提示");
        dialog.setMessage(str);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void refresh() {

    }

    @Override
    public void refreshOPSUnRead(boolean isShow) {

    }

    @Override
    public void startOPS() {

    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_scanning) {
            PermissionDispatcher.requestPermissionWithCheck(this, new int[]{PermissionDispatcher.REQUEST_CAMERA}, this, SCAN_REQUEST);
        } else if (id == R.id.action_add_buddy) {
            Intent addBuddyIntent = new Intent(this, AddBuddyActivity.class);
            startActivity(addBuddyIntent);
        } /*else if (id == R.id.action_show_request) {
            Intent intent = new Intent(this, BuddyRequestActivity.class);
            startActivity(intent);
        }*/ else if (id == R.id.weilvxing) {
            Intent shareIntent = new Intent(MainActivity.this, QunarWebActvity.class);
            shareIntent.setData(Uri.parse(Constants.SHARE_TRAVER));
            shareIntent.putExtra(QunarWebActvity.IS_HIDE_BAR,
                    true);
            startActivity(shareIntent);
        } else if (id == R.id.fav_item) {
            Intent intent = new Intent(MainActivity.this, MyFavourityMessageActivity.class);
            startActivity(intent);
        }
        return true;
    }

    public class HandleMainEvent {

        //更新头像
        public void onEvent(final EventBusEvent.GravantarChanged event) {
            connectionUtil.getUserCard(CurrentPreference.getInstance().getPreferenceUserId(), new IMLogicManager.NickCallBack() {
                @Override
                public void onNickCallBack(Nick nick) {
                    if (nick != null) {
                        ProfileUtils.displayGravatarByImageSrc(MainActivity.this, nick.getHeaderSrc(), myActionBar.getSelfGravatarImage(), 0, 0);
                    }

                }
            }, true, false);
            menuFragment.resetBackground();
        }

    }
}
