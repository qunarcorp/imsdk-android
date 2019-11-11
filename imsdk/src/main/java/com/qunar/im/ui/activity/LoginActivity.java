package com.qunar.im.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
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
import com.qunar.im.core.utils.GlobalConfigManager;
import com.qunar.im.permission.PermissionCallback;
import com.qunar.im.permission.PermissionDispatcher;
import com.qunar.im.ui.presenter.ILoginPresenter;
import com.qunar.im.ui.presenter.factory.LoginFactory;
import com.qunar.im.ui.presenter.views.ILoginView;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.Utils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.services.QtalkHttpRequest;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.other.IQTalkLoginDelegate;
import com.qunar.im.other.TestAccount;
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
public class  LoginActivity extends IMBaseLoginActivity implements View.OnClickListener, PermissionCallback,
        CompoundButton.OnCheckedChangeListener, ILoginView {
    private final String TAG = LoginActivity.class.getSimpleName();
    private final int WEB_LOGIN = 0x02;
    private final int LOGIN_TYPE = 10001;

    private static final int LOGIN_REQUIRE = PermissionDispatcher.getRequestCode();
    private static final int CONFIG_REQUIRE = PermissionDispatcher.getRequestCode();
    //    //pb工具
    private ConnectionUtil connectionUtil;
    CheckBox remember_me_cbx, auto_login_cbx,atom_ui_eula_checkbox;
    EditText verify_code, editText_username, editText2;
    TextView verify_code_btn, tv_version;
    LinearLayout verify_code_container;
    LinearLayout login_password_container;
    ILoginPresenter loginPresenter;
    Button btnlogin;
    ImageView img_show_pwd;
    TextView iv_nav_config;
    ImageView atom_ui_icon;

    ProgressDialog progressDialog;
    LinearLayout login_layout;

    View atom_ui_user_line,atom_ui_code_line,atom_ui_password_line;

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


    @SuppressLint("ClickableViewAccessibility")
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
        atom_ui_eula_checkbox = (CheckBox) findViewById(R.id.atom_ui_eula_checkbox);
        tv_version = (TextView) findViewById(R.id.tv_version);
        btnlogin = (Button) findViewById(R.id.btnlogin);
        img_show_pwd = (ImageView) findViewById(R.id.img_show_pwd);
        iv_nav_config= (TextView) findViewById(R.id.iv_nav_config);
        atom_ui_user_line = (View) findViewById(R.id.atom_ui_user_line);
        atom_ui_code_line = (View) findViewById(R.id.atom_ui_code_line);
        atom_ui_password_line = (View) findViewById(R.id.atom_ui_password_line);
        btnlogin.setOnClickListener(this);
        tv_version.setOnClickListener(this);
        auto_login_cbx.setOnCheckedChangeListener(this);
        verify_code_btn.setOnClickListener(this);
        iv_nav_config.setOnClickListener(this);
//        iv_nav_config.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
//        iv_nav_config.getPaint().setAntiAlias(true);//抗锯齿
        img_show_pwd.setOnTouchListener((v, event) -> {
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
        if (CommonConfig.isQtalk) {
            login_password_container.setVisibility(View.GONE);
            editText2.setText(R.string.atom_ui_common_password);
            verify_code_container.setVisibility(View.VISIBLE);
            editText_username.setOnFocusChangeListener((v, hasFocus) -> {
                atom_ui_user_line.setBackgroundColor(hasFocus ? getResources().getColor(R.color.atom_ui_button_primary_color) : getResources().getColor(R.color.atom_ui_light_gray_ee));
                if(!isClickSend) {
                    if (!hasFocus) {
                        if (!TextUtils.isEmpty(editText_username.getText().toString().trim())) {
                            if (!isTimerRunning) {
                                resendCode();
                            }

                        }
                    }
                }
            });
            verify_code.setOnFocusChangeListener((v, hasFocus) -> atom_ui_code_line.setBackgroundColor(hasFocus ? getResources().getColor(R.color.atom_ui_button_primary_color) : getResources().getColor(R.color.atom_ui_light_gray_ee)));
            editText2.setOnFocusChangeListener((v,hasFocus)-> atom_ui_password_line.setBackgroundColor(hasFocus ? getResources().getColor(R.color.atom_ui_button_primary_color) : getResources().getColor(R.color.atom_ui_light_gray_ee)));
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
        tv_version.setText(getText(R.string.atom_ui_title_current_version)+":" + QunarIMApp.getQunarIMApp().getVersionName() +
                " (" + QunarIMApp.getQunarIMApp().getVersion() + ")");

        atom_ui_icon = (ImageView) findViewById(R.id.atom_ui_icon);
        if(GlobalConfigManager.isQtalkPlat()){
            atom_ui_icon.setImageResource(R.drawable.atom_ui_qtalk_login);
        }else {
            atom_ui_icon.setImageResource(CommonConfig.globalContext.getApplicationInfo().icon);
        }
        bindCheckUpdateView(atom_ui_icon);
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
                getHandler().post(() -> {
                    verify_code_btn.setText(String.valueOf(counter));
                    if (--counter == -1) {
                        verify_code_btn.setEnabled(true);
                        verify_code_btn.setText(R.string.atom_ui_get_verify_code);

                        timer.cancel();
                        isTimerRunning = false;
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
                getHandler().post(() -> {
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
                                .setPositiveButton(getString(R.string.atom_ui_ok), (dialog, which) -> startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS)))
                                .create().show();
                    } else {
                        Toast.makeText(LoginActivity.this, errCode+";status_id:"+code, Toast.LENGTH_SHORT).show();
                        timer.cancel();
                        verify_code_btn.setEnabled(true);
                        verify_code_btn.setText(R.string.atom_ui_get_verify_code);
                        isTimerRunning = false;
                    }
                });
            }
        });

    }

    @Override
    public void responsePermission(int requestCode, boolean granted) {
        if (requestCode == LOGIN_REQUIRE) {
            if (!granted) {
                Toast.makeText(this, R.string.atom_ui_tip_request_permission, Toast.LENGTH_LONG).show();
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
        if(!atom_ui_eula_checkbox.isChecked()){
            Toast.makeText(this, "请勾选同意条款！", Toast.LENGTH_SHORT).show();
            return;
        }
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

        } else if (i == R.id.tv_version) {
            mClickTime++;
            if (mClickTime == 5) {
                Intent intentNav = new Intent(this, NavConfigActivity.class);
                startActivity(intentNav);
                mClickTime = 0;
            }
        } else if(i == R.id.iv_nav_config){
            PermissionDispatcher.requestPermissionWithCheck(this, new int[]{PermissionDispatcher.REQUEST_CAMERA}, this,
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

    @Override
    public void setLoginResult(final boolean success, int errcode) {

        runOnUiThread(() -> {
            progressDialog.dismiss();
            if (success) {
                Toast.makeText(LoginActivity.this, R.string.atom_ui_tip_login_successful, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(LoginActivity.this, R.string.atom_ui_login_faild, Toast.LENGTH_SHORT).show();
            }
        });
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
                if(!LoginType.SMSLogin.equals(QtalkNavicationService.getInstance().getLoginType())){
                    Intent intent = new Intent(LoginActivity.this, QTalkUserLoginActivity.class);
                    intent.putExtra(Constants.BundleKey.IS_SWITCH_ACCOUNT,isSwitchAccount);
                    startActivity(intent);
                    finish();
                }
            }
        }
    }

    public void eulaView(View view){
        Intent intent = new Intent(this,QunarWebActvity.class);
        intent.putExtra(QunarWebActvity.IS_HIDE_BAR, false);
        intent.setData(Uri.parse("file:///android_asset/" + (GlobalConfigManager.isStartalkPlat() ? "eula_startalk.html" : "eula_qtalk.html")));
        startActivity(intent);
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
        runOnUiThread(()->{
            if(b){
                progressDialog.dismiss();
                finish();
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
}
