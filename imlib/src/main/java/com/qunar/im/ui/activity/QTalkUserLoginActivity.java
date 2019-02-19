package com.qunar.im.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.presenter.ILoginPresenter;
import com.qunar.im.base.presenter.impl.QTalkPublicLoginPresenter;
import com.qunar.im.base.presenter.views.ILoginView;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.Utils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.permission.PermissionCallback;
import com.qunar.im.permission.PermissionDispatcher;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.protobuf.common.LoginType;
import com.qunar.im.ui.R;
import com.qunar.im.ui.util.ParseErrorEvent;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.utils.ConnectionUtil;


/**
 * 公共域登陆
 */
public class QTalkUserLoginActivity extends IMBaseActivity implements View.OnClickListener, PermissionCallback,
        CompoundButton.OnCheckedChangeListener,ILoginView{

    private static final int LOGIN_TYPE = 10002;
    private final int WEB_LOGIN = 0x02;

    private static final int LOGIN_REQUIRE = PermissionDispatcher.getRequestCode();


    private CheckBox qtuer_remember_me_cbx, qtuser_auto_login_cbx;
    private EditText qtuser_username_et, qtuser_password_et;
    private TextView qtuser_tv_version;
    private ImageButton qtuser_iv_nav_config;
    private LinearLayout qtuser_login_password_container;
    private ILoginPresenter loginPresenter;
    private Button qtuer_btnlogin;
    private ImageView qtuser_img_show_pwd;
    private FrameLayout qt_login_layout;

    ProgressDialog progressDialog;
    private int mClickTime = 0;

    private volatile boolean isTimerRunning = false;

    private boolean canBack;
    private boolean isSwitchAccount;

    @Override
    protected void onSaveInstanceState(Bundle bundle)
    {
        if(bundle ==null)
        {
            bundle = new Bundle();
        }
        bundle.putBoolean(Constants.BundleKey.CAN_BACK,canBack);
        super.onSaveInstanceState(bundle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_login_qtuser);
        bindViews();
        if(savedInstanceState!=null&&
                savedInstanceState.containsKey(Constants.BundleKey.CAN_BACK))
        {
            canBack = savedInstanceState.getBoolean(Constants.BundleKey.CAN_BACK);
        }
        else {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            if (bundle != null&&bundle.containsKey(Constants.BundleKey.CAN_BACK)) {
                canBack = bundle.getBoolean(Constants.BundleKey.CAN_BACK);
            }
            isSwitchAccount = intent.getBooleanExtra(Constants.BundleKey.IS_SWITCH_ACCOUNT,false);
        }
        loginPresenter = new QTalkPublicLoginPresenter();
        loginPresenter.setLoginView(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.atom_ui_tip_dialog_prompt);
        progressDialog.setMessage(getText(R.string.atom_ui_tip_login_logining));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        initViews();
        if(!TextUtils.isEmpty(com.qunar.im.protobuf.common.CurrentPreference.getInstance().getQvt()))
        {
            com.qunar.im.protobuf.common.CurrentPreference.getInstance().setQvt("");
        }
    }

    private void bindViews() {
        qtuser_password_et = (EditText) findViewById(R.id.qtuser_password_et);
        qtuser_username_et = (EditText) findViewById(R.id.qtuser_username_et);

        qtuser_login_password_container = (LinearLayout) findViewById(R.id.qtuser_login_password_container);
        qtuer_remember_me_cbx = (CheckBox) findViewById(R.id.qtuer_remember_me_cbx);
        qtuser_auto_login_cbx = (CheckBox) findViewById(R.id.qtuser_auto_login_cbx);
        qtuser_tv_version = (TextView) findViewById(R.id.qtuser_tv_version);
        qtuer_btnlogin = (Button) findViewById(R.id.qtuer_btnlogin);
        qtuser_img_show_pwd = (ImageView) findViewById(R.id.qtuser_img_show_pwd);
        qtuser_iv_nav_config = (ImageButton) findViewById(R.id.qtuser_iv_nav_config);
        qt_login_layout = (FrameLayout) findViewById(R.id.qt_login_layout);
        qtuer_btnlogin.setOnClickListener(this);
        qtuser_tv_version.setOnClickListener(this);
        qtuser_iv_nav_config.setOnClickListener(this);
        qtuser_auto_login_cbx.setOnCheckedChangeListener(this);
        qt_login_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyboard();
                return false;
            }
        });
        qtuser_img_show_pwd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                        qtuser_password_et.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        //edit_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        qtuser_password_et.setInputType(InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        //edit_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        return true;
                }
                return false;
            }
        });
    }


    @Override
    protected void onResume() {
        CommonConfig.loginViewHasShown = true;
        super.onResume();
        setActionBarTitle(R.string.atom_ui_title_login);
    }

    @Override
    protected void onPause() {
        CommonConfig.loginViewHasShown = false;
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if(canBack || isSwitchAccount)
        {
            super.onBackPressed();
        }
        else {
            Utils.jump2Desktop(QTalkUserLoginActivity.this);
            this.finish();
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        loginPresenter.release();
    }

    //you can use those filed( declared view by @ViewById) on this method, MUST NOT use those in onCreate()
    //the method will be invoked after onCreate(), and before invoking onStart()
    void initViews() {
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        if(!canBack)
            setActionBarLeftIcon(0);

        qtuser_login_password_container.setVisibility(View.VISIBLE);
        qtuser_password_et.setText("");
        qtuer_remember_me_cbx.setEnabled(false);

        if (com.qunar.im.protobuf.common.CurrentPreference.getInstance().isRememberMe()) {
            final String userName = CurrentPreference.getInstance().getUserid();
            if (userName != null) {
                qtuser_username_et.setText(userName);
            }
        }

        qtuser_tv_version.setText(getText(R.string.atom_ui_about_version) + QunarIMApp.getQunarIMApp().getVersionName() +
                " (" + QunarIMApp.getQunarIMApp().getVersion() + ")");
    }

    private void hideKeyboard(){
//        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//        if (imm != null) {
//            //imm.toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);
//            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
//        }
    }

    @Override
    public void responsePermission(int requestCode, boolean granted) {
        if(requestCode == LOGIN_REQUIRE)
        {
            if(!granted) {
                Toast.makeText(this,R.string.atom_ui_tip_no_file_permissions,Toast.LENGTH_LONG).show();
            }
            loginListener();
        }
    }

    protected void loginCheck()
    {
        PermissionDispatcher.requestPermissionWithCheck(this, new int[]{PermissionDispatcher.REQUEST_WRITE_EXTERNAL_STORAGE,
                                PermissionDispatcher.REQUEST_READ_EXTERNAL_STORAGE}, this,
                        LOGIN_REQUIRE);
    }

    void loginListener() {
        LogUtil.d("performance", "login start:" + System.currentTimeMillis() + "");
        if (TextUtils.isEmpty(qtuser_username_et.getText().toString().trim())) {
            Toast.makeText(this, R.string.atom_ui_common_input_username, Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(qtuser_password_et.getText().toString().trim()))
        {
            Toast.makeText(this, R.string.atom_ui_tip_pwdbox_input_pwd, Toast.LENGTH_SHORT).show();
            return;
        }
        qtuser_username_et.clearFocus();
        qtuser_password_et.clearFocus();
        progressDialog.show();
        loginPresenter.login();
    }


    void autoLoginHandler(CompoundButton cbx, boolean isChecked) {
        if (isChecked) {
            qtuer_remember_me_cbx.setChecked(isChecked);
            qtuer_remember_me_cbx.setEnabled(false);
        } else {
            qtuer_remember_me_cbx.setEnabled(true);
        }
    }


    /**
     * login state feedback
     *
     * @param bSuccess
     */
    private void onLoginResult(final boolean bSuccess) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (bSuccess) {
                    if (qtuer_remember_me_cbx.isChecked()) {
                        com.qunar.im.protobuf.common.CurrentPreference.getInstance().setRememberMe(true);
                    } else {
                        com.qunar.im.protobuf.common.CurrentPreference.getInstance().setRememberMe(false);
                    }

                    if (qtuser_auto_login_cbx.isChecked()) {
                        com.qunar.im.protobuf.common.CurrentPreference.getInstance().setAutoLogin(true);
                    } else {
                        com.qunar.im.protobuf.common.CurrentPreference.getInstance().setAutoLogin(false);
                    }
                }
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int i = buttonView.getId();
        if (i == R.id.auto_login_cbx) {
            autoLoginHandler(buttonView, isChecked);

        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.qtuer_btnlogin) {
            loginCheck();
        }
        else if(i == R.id.qtuser_tv_version) {
            mClickTime++;
            if(mClickTime==5){
                Intent intentNav = new Intent(this, NavConfigActivity.class);
                startActivity(intentNav);
                mClickTime=0;
            }
        }else if(i == R.id.qtuser_iv_nav_config){
            Intent intentNav = new Intent(this, NavConfigActivity.class);
            startActivityForResult(intentNav, LOGIN_TYPE);
            mClickTime=0;
        }
    }

    @Override
    public String getUserName() {
        String userName = qtuser_username_et.getText().toString();
        return userName.replaceAll(" ","");
    }

    @Override
    public String getPassword() {
        String password = qtuser_password_et.getText().toString();
        return password.replaceAll(" ","");
    }

    @Override
    public void setLoginResult(final boolean success,int errcode) {
        if (!success && (errcode == 10 || errcode == 20 || errcode == 30 ||
                errcode == 100 || errcode == 200)) {
            QunarIMApp.mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(QTalkUserLoginActivity.this, "保障帐号安全，需要增强验证", Toast.LENGTH_SHORT).show();
                    go2WebLogin();
                }
            });
            return;
        }
        if (!success && errcode == 300) {
            QunarIMApp.mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(QTalkUserLoginActivity.this, "不允许登陆，强制重置密码后登陆", Toast.LENGTH_SHORT).show();

                }
            });
            return;
        }

        if (!success && errcode == 400)

        {
            QunarIMApp.mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(QTalkUserLoginActivity.this, "不允许登陆，强制绑定手机后登陆", Toast.LENGTH_SHORT).show();
                }
            });

            return;
        }

        if (!success && errcode == 500)

        {
            QunarIMApp.mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(QTalkUserLoginActivity.this, "不允许登陆，强制回答密保问题后登陆", Toast.LENGTH_SHORT).show();
                }
            });

            return;
        }

        if (!success && errcode == 1000)

        {
            QunarIMApp.mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(QTalkUserLoginActivity.this, "该帐号禁止登陆", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        super.presenter.getMyCapability();
        if (!CommonConfig.isQtalk)

        {
            onLoginResult(success);
        }

        getHandler().post(new Runnable() {
                              @Override
                              public void run() {
                                  progressDialog.dismiss();
                                  if (success) {
                                      Toast.makeText(QTalkUserLoginActivity.this, R.string.atom_ui_tip_login_successful, Toast.LENGTH_SHORT).show();
//                                      getVirtualUser();
                                      finish();
                                  } else {
                                      Toast.makeText(QTalkUserLoginActivity.this, R.string.atom_ui_login_faild, Toast.LENGTH_SHORT).show();
                                  }
                              }
                          }

        );
    }


    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent bundle) {
        if (resultCode == RESULT_OK) {
            if (requestCode == WEB_LOGIN) {
                if (bundle != null && bundle.getExtras() != null &&
                        bundle.getExtras().containsKey(Constants.BundleKey.WEB_LOGIN_RESULT)) {
                    String jsonStr = bundle.getExtras().getString(Constants.BundleKey.WEB_LOGIN_RESULT);
                    if(!TextUtils.isEmpty(jsonStr)) {
                        com.qunar.im.protobuf.common.CurrentPreference.getInstance().setQvt(jsonStr);
                        loginPresenter.login();
                    }
                }
            }if(requestCode == LOGIN_TYPE){
                if(!LoginType.PasswordLogin.equals(QtalkNavicationService.getInstance().getLoginType())){
                    startActivity(new Intent(QTalkUserLoginActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }
    }

    protected void go2WebLogin()
    {
        Intent intent = new Intent(QTalkUserLoginActivity.this, QunarWebActvity.class);
        intent.putExtra(Constants.BundleKey.WEB_FROM, Constants.BundleValue.UC_LOGIN);
        intent.putExtra(WebMsgActivity.IS_HIDE_BAR, true);
        intent.setData(Uri.parse("https://user.qunar.com/mobile/login.jsp?ret=" +
                Constants.BundleKey.WEB_LOGIN_RESULT + "&loginType=mobile"));
        startActivityForResult(intent, WEB_LOGIN);
    }

    @Override
    public String getPrenum() {
        return "86";
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void getVirtualUserRole(boolean b) {

    }

    @Override
    public void setHeaderImage(Nick nick) {

    }

    @Override
    public void LoginFailure(int str) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        commonDialog.setTitle(R.string.atom_ui_tip_dialog_prompt);
        commonDialog.setMessage(getText(R.string.atom_ui_tip_login_failed)+ ParseErrorEvent.getError(str,this));
        commonDialog.setPositiveButton(R.string.atom_ui_common_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                setLoginResult(false, 0);
                ConnectionUtil.clearLastUserInfo();
                dialog.dismiss();
            }
        });
        commonDialog.setCancelable(false);
        commonDialog.show();
    }

    @Override
    public void connectInterrupt() {

    }

    @Override
    public void noNetWork() {

    }

    @Override
    public void tryToConnect(String str) {

    }

    @Override
    public boolean isSwitchAccount() {
        return isSwitchAccount;
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
    }
}
