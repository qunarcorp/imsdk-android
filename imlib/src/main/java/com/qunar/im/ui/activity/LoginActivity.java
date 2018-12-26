package com.qunar.im.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.presenter.ILoginPresenter;
import com.qunar.im.base.presenter.factory.LoginFactory;
import com.qunar.im.base.presenter.views.ILoginView;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.Utils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.services.QtalkHttpRequest;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.other.IQTalkLoginDelegate;
import com.qunar.im.other.TestAccount;
import com.qunar.im.permission.PermissionCallback;
import com.qunar.im.permission.PermissionDispatcher;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.protobuf.common.LoginType;
import com.qunar.im.ui.R;
import com.qunar.im.ui.util.ParseErrorEvent;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.utils.ConnectionUtil;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by jiang.cheng on 2014/10/27.
 */
public class  LoginActivity extends IMBaseActivity implements View.OnClickListener, PermissionCallback,
        CompoundButton.OnCheckedChangeListener, ILoginView {
    private final String TAG = LoginActivity.class.getSimpleName();
    private final int WEB_LOGIN = 0x02;
    private final int LOGIN_TYPE = 10001;

    private static final int LOGIN_REQUIRE = PermissionDispatcher.getRequestCode();
    private static final int CONFIG_REQUIRE = PermissionDispatcher.getRequestCode();
    //    //pb工具
    private ConnectionUtil connectionUtil;
    CheckBox remember_me_cbx, auto_login_cbx;
    EditText verify_code, editText_username, editText2;
    TextView verify_code_btn, tv_version;
    LinearLayout verify_code_container;
    LinearLayout login_password_container;
    ILoginPresenter loginPresenter;
    Button btnlogin;
    TextView tv_reset_account;
    ImageView img_show_pwd;
    ImageButton iv_nav_config;

    ProgressDialog progressDialog;
    LinearLayout login_layout;

    private int mClickTime = 0;

    private volatile boolean isTimerRunning = false;

    private boolean canBack;
    private boolean isSwitchAccount;
    private boolean isClickSend = false;

    private Timer timer;
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putBoolean(Constants.BundleKey.CAN_BACK, canBack);
        super.onSaveInstanceState(bundle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_login);
        connectionUtil = ConnectionUtil.getInstance();

        bindViews();
        if (savedInstanceState != null &&
                savedInstanceState.containsKey(Constants.BundleKey.CAN_BACK)) {
            canBack = savedInstanceState.getBoolean(Constants.BundleKey.CAN_BACK);
        } else {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            if (bundle != null && bundle.containsKey(Constants.BundleKey.CAN_BACK)) {
                canBack = bundle.getBoolean(Constants.BundleKey.CAN_BACK);
            }
            isSwitchAccount = intent.getBooleanExtra(Constants.BundleKey.IS_SWITCH_ACCOUNT,false);
        }
        loginPresenter = LoginFactory.createLoginPresenter();
        loginPresenter.setLoginView(this);
//        connectionUtil = ConnectionUtil.getInstance(getApplicationContext());
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.atom_ui_tip_dialog_prompt);
        progressDialog.setMessage(getString(R.string.atom_ui_tip_login_logining));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        initViews();
        if (!TextUtils.isEmpty(com.qunar.im.protobuf.common.CurrentPreference.getInstance().getQvt())) {
            com.qunar.im.protobuf.common.CurrentPreference.getInstance().setQvt("");
        }
    }


    private void bindViews() {
        login_layout = (LinearLayout) findViewById(R.id.login_layout);
        editText2 = (EditText) findViewById(R.id.editText2);
        editText_username = (EditText) findViewById(R.id.editText_username);
        verify_code_container = (LinearLayout) findViewById(R.id.verify_code_container);
        verify_code = (EditText) findViewById(R.id.verify_code);
        verify_code_btn = (TextView) findViewById(R.id.verify_code_btn);
        login_password_container = (LinearLayout) findViewById(R.id.login_password_container);
        remember_me_cbx = (CheckBox) findViewById(R.id.remember_me_cbx);
        auto_login_cbx = (CheckBox) findViewById(R.id.auto_login_cbx);
        tv_version = (TextView) findViewById(R.id.tv_version);
        btnlogin = (Button) findViewById(R.id.btnlogin);
        img_show_pwd = (ImageView) findViewById(R.id.img_show_pwd);
        iv_nav_config= (ImageButton) findViewById(R.id.iv_nav_config);
        btnlogin.setOnClickListener(this);
        tv_version.setOnClickListener(this);
        auto_login_cbx.setOnCheckedChangeListener(this);
        verify_code_btn.setOnClickListener(this);
        tv_reset_account = (TextView) findViewById(R.id.tv_reset_account);
        tv_reset_account.setOnClickListener(this);
        iv_nav_config.setOnClickListener(this);
        img_show_pwd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                        editText2.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        //edit_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        editText2.setInputType(InputType.TYPE_CLASS_TEXT |
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
        //默认false
//        CurrentPreference.getInstance().setIsIt(false);
//        CurrentPreference.getInstance().savePreference();
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
        if (canBack || isSwitchAccount) {
            super.onBackPressed();
        } else {
            Utils.jump2Desktop(LoginActivity.this);
            this.finish();
        }
    }

    @Override
    public void onDestroy() {
        if(timer != null){
            getHandler().removeCallbacksAndMessages(null);
                timer.cancel();
        }
        getHandler().removeCallbacksAndMessages(null);
        loginPresenter.release();
        super.onDestroy();
    }

    //you can use those filed( declared view by @ViewById) on this method, MUST NOT use those in onCreate()
    //the method will be invoked after onCreate(), and before invoking onStart()

    /**
     * initViews时 根据logintype进行判断短信验证码登陆或密码登陆
     */
    void initViews() {
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        if (!canBack) {
            setActionBarLeftIcon(0);
//            myActionBar.getLeftButton().setVisibility(View.GONE);
        }
//        switch (connectionUtil.getLoginType()) {
//            case PasswordLogin:
//                //todo: 密码登陆
//                login_password_container.setVisibility(View.VISIBLE);
//                editText2.setText("");
//                verify_code_container.setVisibility(View.GONE);
//                remember_me_cbx.setEnabled(false);
//                break;
//            case SMSLogin:
//                //todo: 短信验证码登陆
//                login_password_container.setVisibility(View.GONE);
//                editText2.setText("password");
//                verify_code_container.setVisibility(View.VISIBLE);
//                editText_username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                    @Override
//                    public void onFocusChange(View v, boolean hasFocus) {
//                        if (!hasFocus) {
//                            if (!TextUtils.isEmpty(editText_username.getText().toString().trim())) {
//                                if (!isTimerRunning) {
//
//                                    resendCode();
//                                }
//
//                            }
//                        }
//                    }
//                });
//                break;
//        }
        if (CommonConfig.isQtalk) {
            login_password_container.setVisibility(View.GONE);
            editText2.setText(R.string.atom_ui_common_password);
            verify_code_container.setVisibility(View.VISIBLE);
            editText_username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(!isClickSend) {
                        if (!hasFocus) {
                            if (!TextUtils.isEmpty(editText_username.getText().toString().trim())) {
                                if (!isTimerRunning) {
                                    resendCode();
                                }

                            }
                        }
                    }
                }
            });
        } else {
            login_password_container.setVisibility(View.VISIBLE);
            editText2.setText("");
            verify_code_container.setVisibility(View.GONE);
            remember_me_cbx.setEnabled(false);
        }
        if (com.qunar.im.protobuf.common.CurrentPreference.getInstance().isRememberMe()) {
            final String userName = CurrentPreference.getInstance().getUserid();
            if (userName != null) {
                editText_username.setText(userName);
            }
        }
        if (CommonConfig.isQtalk) {
            tv_reset_account.setVisibility(View.GONE);
        }
        tv_version.setText(getText(R.string.atom_ui_title_current_version)+":" + QunarIMApp.getQunarIMApp().getVersionName() +
                " (" + QunarIMApp.getQunarIMApp().getVersion() + ")");
    }


    void resendCode() {
        if (TextUtils.isEmpty(editText_username.getText().toString().trim())) {
            Toast.makeText(this, R.string.atom_ui_common_input_username, Toast.LENGTH_SHORT).show();
            return;
        }

        verify_code_btn.setEnabled(false);
        timer = new Timer();
        final TimerTask timerTask = new TimerTask() {
            int counter = 60;

            @Override
            public void run() {
                getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        verify_code_btn.setText(String.valueOf(counter));
                        if (--counter == -1) {
                            verify_code_btn.setEnabled(true);
                            verify_code_btn.setText(R.string.atom_ui_btn_resend_code);

                            timer.cancel();
                            isTimerRunning = false;
                        }
                    }
                });
            }
        };

        timer.schedule(timerTask, 0, 1000);
        pbTakeCode(getUserName().trim().toLowerCase(), timer);
//        sendSMSCode(editText_username.getText().toString().trim().toLowerCase(), timer);
    }

    public void pbTakeCode(String username, final Timer timer) {
        if(TestAccount.isTestAccount(username)){//测试账号不获取验证码
            return;
        }
        connectionUtil.takeSmsCode(username, new IQTalkLoginDelegate() {
            @Override
            public void onSmsCodeReceived(final int code, final String errCode) {
                getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (code == 0) {
                            Toast.makeText(LoginActivity.this, R.string.atom_ui_common_sent, Toast.LENGTH_SHORT).show();
                            isClickSend = true;
                            verify_code.setFocusable(true);
                            verify_code.setFocusableInTouchMode(true);
                            verify_code.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(verify_code,0);
                            isTimerRunning = true;
                        } else if(code == QtalkHttpRequest.NO_NETWORK_CODE){
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setTitle(getString(R.string.atom_ui_common_prompt))
                                    .setMessage(errCode)
                                    .setPositiveButton(getString(R.string.atom_ui_ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                                        }
                                    })
                                    .create().show();
                        } else {
                            Toast.makeText(LoginActivity.this, errCode+";status_id:"+code, Toast.LENGTH_SHORT).show();
                            timer.cancel();
                            verify_code_btn.setEnabled(true);
                            verify_code_btn.setText(R.string.atom_ui_btn_resend_code);
                            isTimerRunning = false;
                        }
                    }
                });
            }
        });

    }

//    private void sendSMSCode(String rtxId, final Timer timer) {
//        LoginAPI.getSmsCode(rtxId, new ProtocolCallback.UnitCallback<GetSMSCodeResult>() {
//            @Override
//            public void onCompleted(final GetSMSCodeResult getSMSCodeResult) {
//                if (getSMSCodeResult != null) {
//                    getHandler().post(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (getSMSCodeResult.status_id == 0) {
//                                Toast.makeText(LoginActivity.this, R.string.atom_ui_common_sent, Toast.LENGTH_SHORT).show();
//                                isTimerRunning = true;
//                            } else {
//                                Toast.makeText(LoginActivity.this, getSMSCodeResult.msg, Toast.LENGTH_SHORT).show();
//                                timer.cancel();
//                                verify_code_btn.setEnabled(true);
//                                verify_code_btn.setText(R.string.atom_ui_resend_verification_code);
//                                isTimerRunning = false;
//                            }
//                        }
//                    });
//
//                } else {
//                    getHandler().post(new Runnable() {
//                        @Override
//                        public void run() {
//                            timer.cancel();
//                            verify_code_btn.setEnabled(true);
//                            verify_code_btn.setText(R.string.atom_ui_resend_verification_code);
//                            Toast.makeText(LoginActivity.this, R.string.atom_ui_sms_code_sent_failure, Toast.LENGTH_SHORT).show();
//                            isTimerRunning = false;
//                        }
//                    });
//
//                }
//
//            }
//
//            @Override
//            public void onFailure() {
//                getHandler().post(new Runnable() {
//                    @Override
//                    public void run() {
//                        timer.cancel();
//                        verify_code_btn.setEnabled(true);
//                        verify_code_btn.setText(R.string.atom_ui_resend_verification_code);
//                        Toast.makeText(LoginActivity.this, R.string.atom_ui_network_error, Toast.LENGTH_SHORT).show();
//                        isTimerRunning = false;
//                    }
//                });
//            }
//        });
//    }

    @Override
    public void responsePermission(int requestCode, boolean granted) {
        if (requestCode == LOGIN_REQUIRE) {
            if (!granted) {
                Toast.makeText(this, R.string.atom_ui_tip_no_file_permissions, Toast.LENGTH_LONG).show();
            }
            loginListener();
        }else if(requestCode == CONFIG_REQUIRE){
            Intent intentNav = new Intent(this, NavConfigActivity.class);
            startActivityForResult(intentNav, LOGIN_TYPE);
        }
    }

    protected void loginCheck() {
        PermissionDispatcher.requestPermissionWithCheck(this, new int[]{PermissionDispatcher.REQUEST_WRITE_EXTERNAL_STORAGE,
                        PermissionDispatcher.REQUEST_READ_EXTERNAL_STORAGE}, this,
                LOGIN_REQUIRE);
    }

    void loginListener() {
        //todo:暂时先不验证非空字段
        LogUtil.d("performance", "login start:" + System.currentTimeMillis() + "");
        if (TextUtils.isEmpty(editText_username.getText().toString().trim())) {
            Toast.makeText(this, R.string.atom_ui_common_input_username, Toast.LENGTH_SHORT).show();
            return;
        }

        if (CommonConfig.isQtalk) {
            if (TextUtils.isEmpty(verify_code.getText().toString().trim())) {
                Toast.makeText(this, R.string.atom_ui_login_input_smscode, Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            if (TextUtils.isEmpty(editText2.getText().toString().trim())) {
                Toast.makeText(this, R.string.atom_ui_tip_pwdbox_input_pwd, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        editText_username.clearFocus();
        editText2.clearFocus();
        progressDialog.show();
        loginPresenter.login();

    }


    void autoLoginHandler(CompoundButton cbx, boolean isChecked) {
        if (isChecked) {
            remember_me_cbx.setChecked(isChecked);
            remember_me_cbx.setEnabled(false);
        } else {
            remember_me_cbx.setEnabled(true);
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
                    if (remember_me_cbx.isChecked()) {
                        com.qunar.im.protobuf.common.CurrentPreference.getInstance().setRememberMe(true);
                    } else {
                        com.qunar.im.protobuf.common.CurrentPreference.getInstance().setRememberMe(false);
                    }

                    if (auto_login_cbx.isChecked()) {
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
        if (i == R.id.btnlogin) {
            loginCheck();
        } else if (i == R.id.verify_code_btn) {
            resendCode();

        } else if (i == R.id.tv_reset_account) {
            go2WebLogin();
        } else if (i == R.id.tv_version) {
            mClickTime++;
            if (mClickTime == 5) {
                Intent intentNav = new Intent(this, NavConfigActivity.class);
                startActivity(intentNav);
                mClickTime = 0;
            }
        } else if(i == R.id.iv_nav_config){
            PermissionDispatcher.requestPermissionWithCheck(this, new int[]{PermissionDispatcher.REQUEST_WRITE_EXTERNAL_STORAGE,
                            PermissionDispatcher.REQUEST_READ_EXTERNAL_STORAGE}, this,
                    CONFIG_REQUIRE);
        }
    }

    @Override
    public String getUserName() {
        String userName = editText_username.getText().toString();
        return userName.replaceAll(" ","");
    }

    @Override
    public String getPassword() {
        String password;
        if (CommonConfig.isQtalk) {
            password = verify_code.getText().toString();
        } else {
            password = editText2.getText().toString();
        }
        return password.replaceAll(" ","");
    }

    public String getSMS() {
        return verify_code.getText().toString();
    }

    @Override
    public void setLoginResult(final boolean success, int errcode) {
        if (!success && (errcode == 10 || errcode == 20 || errcode == 30 ||
                errcode == 100 || errcode == 200)) {
            QunarIMApp.mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this, "保障帐号安全，需要增强验证", Toast.LENGTH_SHORT).show();
                    go2WebLogin();
                }
            });
            return;
        }
        if (!success && errcode == 300) {
            QunarIMApp.mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this, "不允许登陆，强制重置密码后登陆", Toast.LENGTH_SHORT).show();

                }
            });
            return;
        }

        if (!success && errcode == 400)

        {
            QunarIMApp.mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this, "不允许登陆，强制绑定手机后登陆", Toast.LENGTH_SHORT).show();
                }
            });

            return;
        }

        if (!success && errcode == 500)

        {
            QunarIMApp.mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this, "不允许登陆，强制回答密保问题后登陆", Toast.LENGTH_SHORT).show();
                }
            });

            return;
        }

        if (!success && errcode == 1000)

        {
            QunarIMApp.mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this, "该帐号禁止登陆", Toast.LENGTH_SHORT).show();
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
                                      Toast.makeText(LoginActivity.this, R.string.atom_ui_tip_login_successful, Toast.LENGTH_SHORT).show();
                                      getVirtualUser();
                                      finish();
                                  } else {
                                      Toast.makeText(LoginActivity.this, R.string.atom_ui_login_faild, Toast.LENGTH_SHORT).show();
                                  }
                              }
                          }

        );
    }

    private void getVirtualUser() {
        //获取虚拟账号身份
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent bundle) {
        if (resultCode == RESULT_OK) {
            if (requestCode == WEB_LOGIN) {
                if (bundle != null && bundle.getExtras() != null &&
                        bundle.getExtras().containsKey(Constants.BundleKey.WEB_LOGIN_RESULT)) {
                    String jsonStr = bundle.getExtras().getString(Constants.BundleKey.WEB_LOGIN_RESULT);
                    if (!TextUtils.isEmpty(jsonStr)) {
                        com.qunar.im.protobuf.common.CurrentPreference.getInstance().setQvt(jsonStr);
                        DataUtils.getInstance(CommonConfig.globalContext).putPreferences(Constants.Preferences.qchat_qvt, jsonStr);
                        loginPresenter.login();
                    }
                }
            }else if(requestCode == LOGIN_TYPE){
                if(LoginType.PasswordLogin.equals(QtalkNavicationService.getInstance().getLoginType())){
                    Intent intent = new Intent(LoginActivity.this, QTalkUserLoginActivity.class);
                    intent.putExtra(Constants.BundleKey.IS_SWITCH_ACCOUNT,isSwitchAccount);
                    startActivity(intent);
                    finish();
                }
            }
        }
    }

    protected void go2WebLogin() {
        Intent intent = new Intent(LoginActivity.this, QunarWebActvity.class);
        intent.putExtra(Constants.BundleKey.WEB_FROM, Constants.BundleValue.UC_LOGIN);
        intent.putExtra(WebMsgActivity.IS_HIDE_BAR, true);
        intent.setData(Uri.parse("https://user.qunar.com/mobile/login.jsp?ret=" +
                Constants.BundleKey.WEB_LOGIN_RESULT + "&loginType=mobile&onlyLogin=true"));
        startActivityForResult(intent, WEB_LOGIN);
    }

    @Override
    public String getPrenum() {
        return "86";
    }

    //获取Context
    @Override
    public Context getContext() {
        return this.getApplicationContext();
    }

    @Override
    public void getVirtualUserRole(final boolean b) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(b){
                    progressDialog.dismiss();
//                    Toast.makeText(LoginActivity.this, "自动登录成功", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    @Override
    public void setHeaderImage(Nick nick) {

    }

    @Override
    public void LoginFailure(int str) {
        if(progressDialog!=null){
            progressDialog.dismiss();
        }


//        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
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
    //    @Override
//    public void didReceivedNotification(final String key, final Object... args) {
//        Logger.d("返回状态:" + key + ",返回值:" + args);
//
//        if (key.equals(QtalkEvent.LOGIN_FAILED)){
//            Logger.i("登陆失败:"+args[0]);
//            mProgressDialog.dismiss();
//            Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
//        }
//
//        if (key.equals(QtalkEvent.LOGIN_EVENT) && args[0].equals("succeeded")) {
//            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
//            Logger.d("登陆返回:" + args[0]);
////            connectionUtil.get_virtual_user_role_list(new IQGetVirtualUserRoleDelegate() {
////                @Override
////                public void onVirtualUserResult(ProtoMessageOuterClass.IQMessage iqMessage, String err) {
////                    Logger.i("获取虚拟用户返回状态:"+err);
////                    if(err.equals("success")){
////                        Logger.i("获取虚拟用户的消息:"+iqMessage);
////                        mProgressDialog.dismiss();
////                        finish();
////                        //todo: 获取虚拟用户成功操作
////                    }else{
////                        //todo: 获取虚拟用户失败操作
////                    }
////                }
////            });
//
//        } else {
//            mProgressDialog.dismiss();
//            Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
//        }
//
//
//    }
}
