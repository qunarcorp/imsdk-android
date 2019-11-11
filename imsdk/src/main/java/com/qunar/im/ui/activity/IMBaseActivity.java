package com.qunar.im.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.qunar.im.permission.PermissionDispatcher;
import com.qunar.im.ui.view.OnDoubleClickListener;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.jsonbean.NoticeBean;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.RunningApp;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.ui.R;
import com.qunar.im.ui.broadcastreceivers.HomeWatcherReceiver;
import com.qunar.im.ui.presenter.ISystemPresenter;
import com.qunar.im.ui.presenter.impl.SystemPresenter;
import com.qunar.im.ui.util.StatusBarUtil;
import com.qunar.im.ui.view.CommonDialog;
import com.qunar.im.ui.view.CustomAnimation;
import com.qunar.im.ui.view.NoticeView;
import com.qunar.im.ui.view.QtActionBar;
import com.qunar.im.ui.view.QtNewActionBar;

/**
 * Created by jiang.cheng on 2014/10/30.
 */
public class IMBaseActivity extends AppCompatActivity implements IMNotificaitonCenter.NotificationCenterDelegate {
    private final String className = getClass().getSimpleName();

    //pb连接工具
//    public ConnectionUtil connectionUtil;
    public ISystemPresenter presenter;
    //    protected int activityId = -1;
    protected String title;
    protected QtActionBar myActionBar;
    protected QtNewActionBar mNewActionBar;
    protected long resumeTime;
    private String errorTitle = "连接中断";
    private String newErrorTitle = "";
    private HomeWatcherReceiver mHomeKeyReceiver = null;
    protected boolean titleShow = true;
    protected boolean isFront = false;//是否前台显示
    //仿ios风格弹窗
    protected CommonDialog.Builder commonDialog;
    protected Vibrator vibrator;
    protected ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
//        }
//        @color/atom_ui_primary_color
        // TODO: 2018/1/17 不支持状态栏字体变色的默认为黑色
        if (StatusBarUtil.getStatusBarLightMode(getWindow()) == 4) {
            StatusBarUtil.setColorNoTranslucent(this, getResources().getColor(R.color.atom_ui_action_bar_new_bc));
        } else {
            StatusBarUtil.setColorNoTranslucent(this, getResources().getColor(R.color.atom_ui_white));
        }
        StatusBarUtil.setStatusBarLightMode(getWindow());
        presenter = new SystemPresenter();
//        activityId = QtalkApplicationLike.putActivity(this);
        registerHomeKeyReceiver(this);
        commonDialog = new CommonDialog.Builder(this);

        ConnectionUtil.getInstance().addEvent(this, QtalkEvent.GLOBALNOTICE);
        ConnectionUtil.getInstance().addEvent(this, QtalkEvent.SHAKE_WINDOW);
//        EventBus.getDefault().register(loginComplete);
//        PBinit();

    }


    protected void showProgressDialog(String str) {
        if (dialog == null) {
            dialog = new ProgressDialog(this);
        }
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog.setCancelable(false);// 设置是否可以通过点击Back键取消
        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        dialog.setTitle(str);
        dialog.show();

    }

    protected void dismissProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    //    public void setActionBar(QtActionBar bar) {
//        myActionBar = bar;
//        setSupportActionBar(myActionBar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        if (myActionBar != null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                int height = getMyStatusBarHeight();
//                if (height > 0)
//                    myActionBar.setPaddingTop(height);
//            }
//            myActionBar.initLeftBg();
//            myActionBar.getLeftButton().setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onBackPressed();
//                }
//            });
//        }
//    }
    private int SearchWhat = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
            if (msg.what == SearchWhat) {
                if(onSearch!=null){
                    onSearch.onSearch(mNewActionBar.getSearchBarSearchEdittext().getText().toString().trim());
                }
            }
        }
    };

    public interface OnSearch {
        void onSearch(String str);
    }
    private OnSearch onSearch;

    public void setOnSearch(OnSearch onSearch){
        this.onSearch = onSearch;
    }

    /**
     * 设置actionbar
     *
     * @param bar
     */
    public void setNewActionBar(QtNewActionBar bar) {
        mNewActionBar = bar;
        setSupportActionBar(mNewActionBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (mNewActionBar != null) {
            mNewActionBar.setTitleMargin(0, 0, 0, 0);
            mNewActionBar.getLeftLayout().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            mNewActionBar.getSearchLayoutLeftLayout().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    public void setShowSearchBack(boolean isShow){
        if(isShow){
            mNewActionBar.getSearchLayoutLeftLayout().setVisibility(View.VISIBLE);
        }else{
            mNewActionBar.getSearchLayoutLeftLayout().setVisibility(View.GONE);
        }
    }

    public void setSearchCancleClickLin(View.OnClickListener onClickListener){
        mNewActionBar.getSearchBarCancleLayout().setOnClickListener(onClickListener);
    }

    public void setShowSearchBar(boolean show) {
        if (show) {
            mNewActionBar.getSearchLayout().setVisibility(View.VISIBLE);
            mNewActionBar.getTitleBarLayout().setVisibility(View.GONE);

            mNewActionBar.getSearchBarCleanView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mNewActionBar.getSearchBarSearchEdittext().setText("");
                    mNewActionBar.getSearchBarCleanView().setVisibility(View.GONE);
                }
            });
            mNewActionBar.getSearchBarSearchEdittext().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (TextUtils.isEmpty(s)) {
                        mNewActionBar.getSearchBarCleanView().setVisibility(View.GONE);
                    } else {
                        mNewActionBar.getSearchBarCleanView().setVisibility(View.VISIBLE);
                    }

                    if (mHandler.hasMessages(SearchWhat)) {
                        mHandler.removeMessages(SearchWhat);
                    }
                    mHandler.sendEmptyMessageDelayed(SearchWhat, 500);

                }
            });
        } else {
            mNewActionBar.getSearchLayout().setVisibility(View.GONE);
            mNewActionBar.getTitleBarLayout().setVisibility(View.VISIBLE);
        }
    }

    public void setActionBarVisibility(boolean show) {
        if (show) {
            mNewActionBar.setVisibility(View.VISIBLE);
        } else {
            mNewActionBar.setVisibility(View.GONE);
        }
    }

    public void setActionBarLeftClick(View.OnClickListener onClickListener) {
        mNewActionBar.getLeftLayout().setOnClickListener(onClickListener);
    }

    public void setActionBarRightIconClick(View.OnClickListener onClickListener) {
        mNewActionBar.getRightIcon().setOnClickListener(onClickListener);
//        mNewActionBar.getRightLayout().setOnClickListener(onClickListener);
    }

    public void setActionBarRightIconSpecialClick(View.OnClickListener onClickListener) {
        mNewActionBar.getRightIconSpecial().setOnClickListener(onClickListener);
    }

    public void setActionBarRightTextClick(View.OnClickListener onClickListener) {
        mNewActionBar.getRightText().setOnClickListener(onClickListener);
    }

    public void setActionBarRigthClick(View.OnClickListener onClickListener) {
        mNewActionBar.getRightLayout().setOnClickListener(onClickListener);
    }


    public void setActionBarTitleClick(View.OnClickListener onClickListener) {
        mNewActionBar.getTextTitle().setOnClickListener(onClickListener);
    }

    public void setActionBarTitleDoubleClick(OnDoubleClickListener.DoubleClickCallback doubleClick) {
        mNewActionBar.getTitleLayout().setOnTouchListener(new OnDoubleClickListener(doubleClick));
    }

    public void setActionBarSingleTitle(String str) {
        if (mNewActionBar == null) {
            return;
        }
        if (!TextUtils.isEmpty(newErrorTitle)) {
            str = newErrorTitle;
        }
        actionBarGone();
        mNewActionBar.getTextTitle().setText(str);
        mNewActionBar.getTextTitle().setVisibility(View.VISIBLE);
    }

    public void setActionBarSingleTitle(@StringRes int str) {
        if (mNewActionBar == null) {
            return;
        }
        actionBarGone();
        mNewActionBar.getTextTitle().setText(str);
        mNewActionBar.getTextTitle().setVisibility(View.VISIBLE);
    }

    public void setActionBarTitle(String str) {
        if (mNewActionBar == null) {
            return;
        }
        if (!TextUtils.isEmpty(newErrorTitle)) {
            str = newErrorTitle;
        }
        mNewActionBar.getTextTitle().setVisibility(View.VISIBLE);
        mNewActionBar.getTextTitle().setText(str);
    }

    public void setActionBarTitleRightImage(int imageResource) {
        if (mNewActionBar == null) {
            return;
        }
        mNewActionBar.getTextTitle().setVisibility(View.VISIBLE);
        mNewActionBar.getTextTitle().setCompoundDrawablesWithIntrinsicBounds(0, 0, imageResource, 0);
    }

    public void setActionBarTitle(@StringRes int str) {
        if (mNewActionBar == null) {
            return;
        }
        if (!TextUtils.isEmpty(newErrorTitle)) {
            mNewActionBar.getTextTitle().setText(newErrorTitle);
        }
        if (str == 0) {
            mNewActionBar.getTextTitle().setVisibility(View.GONE);
        } else {
            mNewActionBar.getTextTitle().setVisibility(View.VISIBLE);
            mNewActionBar.getTextTitle().setText(str);
        }
    }


    public void actionBarGone() {
        if (mNewActionBar == null) {
            return;
        }
        mNewActionBar.getLeftLayout().setVisibility(View.GONE);
        mNewActionBar.getRightLayout().setVisibility(View.GONE);
        mNewActionBar.getTextTitle().setVisibility(View.GONE);
    }

    public void setActionBarRight(@StringRes int rightIcon, @StringRes int rightIconSpecial, @StringRes int rightStr) {
        if (mNewActionBar == null) {
            return;
        }
        setActionBarRightIcon(rightIcon);
        setActionBarRightSpecial(rightIconSpecial);
        setActionBarRightText(rightStr);
    }

    public void setActionBarRight(@StringRes int rightIcon, @StringRes int rightIconSpecial, String rightStr) {
        if (mNewActionBar == null) {
            return;
        }
        setActionBarRightIcon(rightIcon);
        setActionBarRightSpecial(rightIconSpecial);
        setActionBarRightText(rightStr);
    }

    public void setActionBarRightSpecial(@StringRes int rightIcon) {
        if (mNewActionBar == null) {
            return;
        }
        mNewActionBar.getRightLayout().setVisibility(View.VISIBLE);
        if (rightIcon != 0) {
            mNewActionBar.getRightIconSpecial().setVisibility(View.VISIBLE);
            mNewActionBar.getRightIconSpecial().setText(rightIcon);
        } else {
            mNewActionBar.getRightIconSpecial().setVisibility(View.GONE);

        }
    }

    public void setActionBarRightIconSize(int size) {
        if (mNewActionBar == null) {
            return;
        }
        mNewActionBar.getRightLayout().setVisibility(View.VISIBLE);
        if (size != 0) {
            mNewActionBar.getRightIcon().setVisibility(View.VISIBLE);
            mNewActionBar.getRightIcon().setTextSize(size);
        }
    }


    public void setActionBarRightSpecialIconSize(int size) {
        if (mNewActionBar == null) {
            return;
        }
        mNewActionBar.getRightLayout().setVisibility(View.VISIBLE);
        if (size != 0) {
            mNewActionBar.getRightIconSpecial().setVisibility(View.VISIBLE);
            mNewActionBar.getRightIconSpecial().setTextSize(size);
        }
    }


    public void setActionBarRightIcon(@StringRes int rightIcon) {
        if (mNewActionBar == null) {
            return;
        }
        mNewActionBar.getRightLayout().setVisibility(View.VISIBLE);
        if (rightIcon != 0) {
            mNewActionBar.getRightIcon().setVisibility(View.VISIBLE);
            mNewActionBar.getRightIcon().setText(rightIcon);
        } else {
            mNewActionBar.getRightIcon().setVisibility(View.GONE);

        }
    }

    public void setActionBarRightIconColor(int color) {
        mNewActionBar.getRightIcon().setTextColor(color);
    }

    public void setActionBarLeftIcon(@StringRes int leftIcon) {
        if (mNewActionBar == null) {
            return;
        }
        mNewActionBar.getLeftLayout().setVisibility(View.VISIBLE);
        if (leftIcon != 0) {
            mNewActionBar.getLeftIcon().setVisibility(View.VISIBLE);
            mNewActionBar.getLeftIcon().setText(leftIcon);
        } else {
            mNewActionBar.getLeftIcon().setVisibility(View.GONE);

        }
    }


    public void setActionBarLeft(boolean isShow, String str, int count) {
        if (mNewActionBar == null) {
            return;
        }
        setActionBarLeftIcon(isShow);
        setActionBarLeftText(str);
        setActionBarLeftCount(count);
    }

    public void setActionBarLeft(@ColorInt int leftColor, boolean isShow, String str, int count) {
        if (mNewActionBar == null) {
            return;
        }
        setActionBarLeftColor(leftColor);
        setActionBarLeftIcon(isShow);
        setActionBarLeftText(str);
        setActionBarLeftCount(count);
    }

    public void setActionBarLeft(@StringRes int iconStr, String str, int count) {
        if (mNewActionBar == null) {
            return;
        }
        setActionBarLeftIcon(iconStr);
        setActionBarLeftText(str);
        setActionBarLeftCount(count);
    }

    public void setActionBarLeft(@StringRes int iconStr, @StringRes int str, int count) {
        if (mNewActionBar == null) {
            return;
        }
        setActionBarLeftIcon(iconStr);
        setActionBarLeftText(str);
        setActionBarLeftCount(count);
    }

    public void setActionBarLeftColor(@ColorInt int color) {
        if (mNewActionBar == null) {
            return;
        }
        mNewActionBar.getLeftLayout().setVisibility(View.VISIBLE);
        mNewActionBar.getLeftText().setTextColor(color);
        mNewActionBar.getLeftIcon().setTextColor(color);


    }

    public void setActionBarLeftIcon(boolean isShow) {
        if (mNewActionBar == null) {
            return;
        }
        mNewActionBar.getLeftLayout().setVisibility(View.VISIBLE);
        if (isShow) {
            mNewActionBar.getLeftIcon().setVisibility(View.VISIBLE);
        } else {
            mNewActionBar.getLeftIcon().setVisibility(View.GONE);
        }
    }

    public void setActionBarLeftCount(int count) {
        if (mNewActionBar == null) {
            return;
        }
        mNewActionBar.getLeftLayout().setVisibility(View.VISIBLE);
        mNewActionBar.getLeftUnReadText().setVisibility(View.VISIBLE);
        if (count > 0 && count < 100) {
            mNewActionBar.getLeftUnReadText().setText(count + "");
        } else if (count > 99) {
            mNewActionBar.getLeftUnReadText().setText("99+");
        } else {
            mNewActionBar.getLeftUnReadText().setVisibility(View.GONE);
        }

    }

    public void setActionBarRightText(String str) {
        if (mNewActionBar == null) {
            return;
        }
        mNewActionBar.getRightLayout().setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(str)) {
            mNewActionBar.getRightText().setVisibility(View.GONE);
        } else {
            mNewActionBar.getRightText().setVisibility(View.VISIBLE);
            mNewActionBar.getRightText().setText(str);
        }
    }


    public void setActionBarLeftText(String str) {
        if (mNewActionBar == null) {
            return;
        }
        mNewActionBar.getLeftLayout().setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(str)) {
            mNewActionBar.getLeftText().setVisibility(View.GONE);
        } else {
            mNewActionBar.getLeftText().setVisibility(View.VISIBLE);
            mNewActionBar.getLeftText().setText(str);
        }
    }

    public void setActionBarRightText(@StringRes int str) {
        if (mNewActionBar == null) {
            return;
        }
        mNewActionBar.getRightLayout().setVisibility(View.VISIBLE);
        if (str == 0) {
            mNewActionBar.getRightText().setVisibility(View.GONE);
        } else {
            mNewActionBar.getRightText().setVisibility(View.VISIBLE);
            mNewActionBar.getRightText().setText(str);
        }
    }


    public void setActionBarLeftText(@StringRes int str) {
        if (mNewActionBar == null) {
            return;
        }
        mNewActionBar.getLeftLayout().setVisibility(View.VISIBLE);
        if (str == 0) {
            mNewActionBar.getLeftText().setVisibility(View.GONE);
        } else {
            mNewActionBar.getLeftText().setVisibility(View.VISIBLE);
            mNewActionBar.getLeftText().setText(str);
        }
    }

    public void setActionBarMood(String mood) {
        if (mNewActionBar == null) {
            return;
        }
        mNewActionBar.getMood().setVisibility(View.VISIBLE);
        mNewActionBar.getMood().setText(mood);
    }

    /**
     * 设置异常title
     *
     * @param title
     */
    public void setErrorTitle(String title) {
        newErrorTitle = title;
        if (!TextUtils.isEmpty(newErrorTitle)) {
            setActionBarTitle(newErrorTitle);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        isFront = true;
        resumeTime = System.currentTimeMillis();
        //PushServiceUtils.stopAMDService(this.getApplicationContext());
        ConnectionUtil.getInstance().setUserState("online");
        CommonConfig.leave = false;
//        IMLogic.instance().reback();
        if (!ConnectionUtil.getInstance().isLoginStatus()) {
//            changeTitle2Error();
            setErrorTitle(newErrorTitle);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isFront = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        //, "com.qunar.im.ui.activity.MainActivity"
        if (!RunningApp.isForeground(this) && CurrentPreference.getInstance().isTurnOnPsuh()) {
            //PushServiceUtils.startAMDService(this.getApplicationContext());
            ConnectionUtil.getInstance().setUserState("away");
//            IMLogic.instance().leave();
            CommonConfig.leave = true;
        }
    }

    @Override
    protected void onDestroy() {
//        EventBus.getDefault().unregister(loginComplete);
        unregisterHomeKeyReceiver(this);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        QtalkApplicationLike.removeActivity(activityId);

        ConnectionUtil.getInstance().removeEvent(this, QtalkEvent.GLOBALNOTICE);
        ConnectionUtil.getInstance().removeEvent(this, QtalkEvent.SHAKE_WINDOW);

        super.onDestroy();
    }

    public Handler getHandler() {
        return QunarIMApp.mainHandler;
    }

    private void registerHomeKeyReceiver(Context context) {
        LogUtil.i("Home Reg", "registerHomeKeyReceiver");
        mHomeKeyReceiver = new HomeWatcherReceiver();
        final IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

        context.registerReceiver(mHomeKeyReceiver, homeFilter);
    }

    private void unregisterHomeKeyReceiver(Context context) {
        LogUtil.i("Home Reg", "unregisterHomeKeyReceiver");
        if (null != mHomeKeyReceiver) {
            context.unregisterReceiver(mHomeKeyReceiver);
        }
    }

    PopupWindow popupWindow;
    NoticeView contentView;

    protected void showNoticePopupWindow(NoticeBean noticeBean) {
        // 加载指定布局作为PopupWindow的显示内容
        if (popupWindow == null) {
            contentView = new NoticeView(this);
            popupWindow = new PopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
        }
        contentView.setCloseListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null && popupWindow.isShowing())
                    popupWindow.dismiss();
            }
        });
        contentView.setData(noticeBean);
        // setPopFoucus();
//        popupWindow.showAtLocation(mNewActionBar, Gravity.NO_GRAVITY, 0, mNewActionBar.getHeight());
        if (!popupWindow.isShowing())
            popupWindow.showAsDropDown(mNewActionBar);
    }

    @Override
    public void didReceivedNotification(String key, Object... args) {
        switch (key) {
            case QtalkEvent.GLOBALNOTICE:
                if (!isFront) return;
                if (args != null && args.length > 0) {
                    if (args[0] instanceof NoticeBean) {
                        NoticeBean noticeBean = (NoticeBean) args[0];
                        showNoticePopupWindow(noticeBean);
                    }
                }
                break;
            case QtalkEvent.SHAKE_WINDOW:

                if (!isFront) return;
                shakeWindow();
        }
    }

    //    protected void restoreTitle() {
//        if (myActionBar != null) {
//            if (!titleShow) {
//                myActionBar.getTitleTextview().setVisibility(View.INVISIBLE);
//            }
//            if (title != null) {
//
//                if (errorTitle.equals(title)) {
//                    title = "QTalk";
//                }
//                myActionBar.getTitleTextview().setText(title);
//                title = null;
//            }
//        }
//    }

//    protected void changeTitle2Error() {
//        if (myActionBar == null) return;
//        String curTitle = myActionBar.getTitleTextview().getText().toString();
//        if (!curTitle.equals(errorTitle)) {
//            title = curTitle;
//        }
//        myActionBar.getTitleTextview().setVisibility(View.VISIBLE);
//        myActionBar.getTitleTextview().setText(errorTitle);
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_MENU) {
//            return true;
//        }
        return super.onKeyDown(keyCode, event);
    }

    protected int getMyStatusBarHeight() {
        Rect r = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        return r.top;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // delegate the permission handling to generated method
        PermissionDispatcher.onRequestPermissionsResult(requestCode, grantResults);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.atom_ui_in_from_right, R.anim.atom_ui_out_to_left);
    }


    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.atom_ui_in_from_right, R.anim.atom_ui_out_to_left);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.atom_ui_in_from_left, R.anim.atom_ui_out_to_right);
    }

    protected void toast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(IMBaseActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void shakeWindow() {
        CustomAnimation customAnimation = new CustomAnimation();
        customAnimation.setDuration(2000);
        try {
            View view = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
            if (view != null)
                view.startAnimation(customAnimation);
        } catch (Exception e) {
            Logger.i("获取根view异常" + e.getLocalizedMessage());
        }
        /*
         * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
         * */
        long[] pattern = {100, 400, 100, 400, 100, 400};   // 停止 开启 停止 开启
        if (vibrator == null)
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(pattern, -1);//重复两次上面的pattern 如果只想震动一次，index设为-1
    }

}
