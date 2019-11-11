package com.qunar.im.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.jsonbean.NavConfigResult;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.NetworkUtils;
import com.qunar.im.base.util.Utils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.core.utils.GlobalConfigManager;
import com.qunar.im.permission.PermissionCallback;
import com.qunar.im.permission.PermissionDispatcher;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.protobuf.common.LoginType;
import com.qunar.im.protobuf.dispatch.DispatchHelper;
import com.qunar.im.ui.R;
import com.qunar.im.ui.presenter.ILoginPresenter;
import com.qunar.im.ui.presenter.factory.LoginFactory;
import com.qunar.im.ui.presenter.impl.QTalkPublicLoginPresenter;
import com.qunar.im.ui.presenter.views.ILoginView;
import com.qunar.im.ui.sdk.QIMSdk;
import com.qunar.im.ui.presenter.ILoginPresenter;
import com.qunar.im.ui.presenter.impl.QTalkPublicLoginPresenter;
import com.qunar.im.ui.presenter.views.ILoginView;
import com.qunar.im.ui.util.NavConfigUtils;
import com.qunar.im.ui.util.ParseErrorEvent;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.zxing.activity.CaptureActivity;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.HttpUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * 公共域登陆
 */
public class QTalkUserLoginActivity extends IMBaseLoginActivity implements View.OnClickListener, PermissionCallback,
        CompoundButton.OnCheckedChangeListener,ILoginView{

    private static final int LOGIN_TYPE = 10002;
    private final int WEB_LOGIN = 0x02;

    private static final int LOGIN_REQUIRE = PermissionDispatcher.getRequestCode();

    private static final int SCAN_REQUEST = PermissionDispatcher.getRequestCode();


    private CheckBox qtuer_remember_me_cbx, qtuser_auto_login_cbx;
    private EditText qtuser_username_et, qtuser_password_et;
    private TextView qtuser_company_et,atom_ui_forget_pwd,atom_ui_nav_name,atom_ui_eula_text,atom_ui_to_c_regiest,text_login;
    private TextView qtuser_tv_version,regiest_txt;
    private TextView qtuser_iv_nav_config;
    private LinearLayout qtuser_login_password_container,atom_ui_nav_layouot,atom_ui_nav_config_add_scan_layout;
    private ILoginPresenter loginPresenter;
    private Button qtuer_btnlogin;
    private ImageView qtuser_img_show_pwd;
    private FrameLayout qt_login_layout;
    private RelativeLayout title_bar;
    private LinearLayout qtuser_company_layout;

    private View atom_ui_user_line,atom_ui_password_line;

    private CheckBox atom_ui_eula_checkbox;

    ProgressDialog progressDialog;
    private boolean canBack;
    private boolean isSwitchAccount;

    private String navName;

    private String navUrl;

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

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.atom_ui_tip_dialog_prompt);
        progressDialog.setMessage(getText(R.string.atom_ui_tip_login_logining));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        initViews();
        if(!TextUtils.isEmpty(CurrentPreference.getInstance().getQvt()))
        {
            CurrentPreference.getInstance().setQvt("");
        }

        dispatchScheme(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        dispatchScheme(intent);
    }

    /**
     * 配置导航scheme处理
     */
    private void dispatchScheme(Intent intent){
        if(intent != null){
            Uri data = intent.getData();
            if(data == null){
                return;
            }
            String host = data.getHost();
            if("start_nav_config".equals(host)){
                Intent temp = new Intent(this,NavConfigActivity.class);
                temp.setData(data);
                startActivityForResult(temp, LOGIN_TYPE);
            }
        }
    }

    private void bindViews() {
        title_bar = (RelativeLayout) findViewById(R.id.title_bar);
        qtuser_password_et = (EditText) findViewById(R.id.qtuser_password_et);
        qtuser_username_et = (EditText) findViewById(R.id.qtuser_username_et);
        qtuser_company_et = (TextView) findViewById(R.id.qtuser_company_et);
        atom_ui_forget_pwd = (TextView) findViewById(R.id.atom_ui_forget_pwd);
        atom_ui_nav_name = (TextView) findViewById(R.id.atom_ui_nav_name);
        atom_ui_eula_text = (TextView) findViewById(R.id.atom_ui_eula_text);
        qtuser_company_layout = (LinearLayout) findViewById(R.id.qtuser_company_layout);
        atom_ui_nav_layouot = (LinearLayout) findViewById(R.id.atom_ui_nav_layouot);

        atom_ui_to_c_regiest = (TextView) findViewById(R.id.atom_ui_to_c_regiest);

        qtuser_login_password_container = (LinearLayout) findViewById(R.id.qtuser_login_password_container);
        qtuer_remember_me_cbx = (CheckBox) findViewById(R.id.qtuer_remember_me_cbx);
        qtuser_auto_login_cbx = (CheckBox) findViewById(R.id.qtuser_auto_login_cbx);
        qtuser_tv_version = (TextView) findViewById(R.id.qtuser_tv_version);
        qtuer_btnlogin = (Button) findViewById(R.id.qtuer_btnlogin);
        qtuser_img_show_pwd = (ImageView) findViewById(R.id.qtuser_img_show_pwd);
        qtuser_iv_nav_config = (TextView) findViewById(R.id.qtuser_iv_nav_config);
        qt_login_layout = (FrameLayout) findViewById(R.id.qt_login_layout);
        atom_ui_eula_checkbox = (CheckBox) findViewById(R.id.atom_ui_eula_checkbox);
        text_login = (TextView) findViewById(R.id.text_login);
        boolean isEulaChecked = DataUtils.getInstance(CommonConfig.globalContext).getPreferences(Constants.Preferences.EULA_CHECK_TAG,false);
        if(isEulaChecked) {
            atom_ui_eula_checkbox.setChecked(true);
        }
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
//        qtuser_company_layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(QTalkUserLoginActivity.this,NavConfigActivity.class);
//                startActivityForResult(intent, LOGIN_TYPE);
//            }
//        });
        atom_ui_forget_pwd.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        atom_ui_forget_pwd.getPaint().setAntiAlias(true);//抗锯齿
        atom_ui_forget_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(QTalkUserLoginActivity.this,FindPwdStep1Activity.class);
//                startActivity(intent);
                if(TextUtils.isEmpty(NavConfigUtils.getCurrentNavDomain())){
                    showConfigNavDialog();
                    return;
                }
                String resetPwdUrl = QtalkNavicationService.getInstance().getResetPwdUrl();
                if(TextUtils.isEmpty(resetPwdUrl)){
                    return;
                }
                Map<String,String> params = new HashMap<>();
                params.put("domain",QtalkNavicationService.getInstance().getXmppdomain());
                StringBuilder sb = new StringBuilder(resetPwdUrl);
                Protocol.spiltJointUrl(sb,params);
                Intent intent = new Intent(QTalkUserLoginActivity.this,QunarWebActvity.class);
                intent.putExtra(QunarWebActvity.IS_HIDE_BAR, true);
                intent.setData(Uri.parse(sb.toString()));
                startActivity(intent);
            }
        });
        regiest_txt = (TextView) findViewById(R.id.regiest_txt);
        regiest_txt.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        regiest_txt.getPaint().setAntiAlias(true);//抗锯齿
        regiest_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QTalkUserLoginActivity.this,QunarWebActvity.class);
                intent.putExtra(QunarWebActvity.IS_HIDE_BAR, false);
                intent.setData(Uri.parse("https://im.qunar.com/new/#/register"));
                startActivity(intent);
            }
        });

        atom_ui_user_line = findViewById(R.id.atom_ui_user_line);
        atom_ui_password_line = findViewById(R.id.atom_ui_password_line);
//        qtuser_iv_nav_config.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
//        qtuser_iv_nav_config.getPaint().setAntiAlias(true);//抗锯齿
        if(GlobalConfigManager.isStartalkPlat()){
            title_bar.setVisibility(View.VISIBLE);
            atom_ui_forget_pwd.setVisibility(View.VISIBLE);
            atom_ui_nav_layouot.setVisibility(View.VISIBLE);
            regiest_txt.setVisibility(View.VISIBLE);
            qtuser_iv_nav_config.setVisibility(View.GONE);
        }else {
            title_bar.setVisibility(View.GONE);
            atom_ui_forget_pwd.setVisibility(View.GONE);
            atom_ui_nav_layouot.setVisibility(View.GONE);
            regiest_txt.setVisibility(View.GONE);
            qtuser_iv_nav_config.setVisibility(View.VISIBLE);
        }

        qtuser_username_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                atom_ui_user_line.setBackgroundColor(hasFocus ? getResources().getColor(R.color.atom_ui_button_primary_color) : getResources().getColor(R.color.atom_ui_light_gray_ee));
            }
        });

        qtuser_password_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                atom_ui_password_line.setBackgroundColor(hasFocus ? getResources().getColor(R.color.atom_ui_button_primary_color) : getResources().getColor(R.color.atom_ui_light_gray_ee));
            }
        });

        String company = DataUtils.getInstance(this).getPreferences(Constants.Preferences.COMPANY,"");
        freshCompany(company);

        atom_ui_nav_layouot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(QTalkUserLoginActivity.this,QtalkUserHostActivity.class);
//                startActivityForResult(intent,QtalkUserHostActivity.HOST_REQUEST_CODE);
                Intent intent = new Intent(QTalkUserLoginActivity.this,NavConfigActivity.class);
                startActivityForResult(intent, LOGIN_TYPE);
            }
        });

        atom_ui_eula_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QTalkUserLoginActivity.this,QunarWebActvity.class);
                intent.putExtra(QunarWebActvity.IS_HIDE_BAR, false);
                if(NetworkUtils.isConnection(QunarIMApp.getContext()) != NetworkUtils.ConnectStatus.connected) {
                    intent.setData(Uri.parse("file:///android_asset/eula.html"));
                } else {
                    intent.setData(Uri.parse("https://im.qunar.com/termsuse/#/"));
                }
                startActivity(intent);
            }
        });
        ((ImageView)findViewById(R.id.atom_ui_app_icon)).setImageResource(getApplicationInfo().icon);

        atom_ui_nav_config_add_scan_layout = (LinearLayout) findViewById(R.id.atom_ui_nav_config_add_scan_layout);
        atom_ui_nav_config_add_scan_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionCheck();
            }
        });
        atom_ui_to_c_regiest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QTalkUserLoginActivity.this,QunarWebActvity.class);
                intent.putExtra(QunarWebActvity.IS_HIDE_BAR, false);
                intent.setData(Uri.parse(QtalkNavicationService.getInstance().getAppWeb() + "/entry/#/?domain=" + QtalkNavicationService.getInstance().getXmppdomain()));
                startActivity(intent);
            }
        });
        freshRightTitle(QtalkNavicationService.getInstance().isToC());

        bindCheckUpdateView(text_login);

    }


    @Override
    protected void onResume() {
        CommonConfig.loginViewHasShown = true;
        super.onResume();
        setActionBarTitle(R.string.atom_ui_title_login);
        freshRightTitle(QtalkNavicationService.getInstance().isToC());
        if(!TextUtils.isEmpty(NavConfigUtils.getCurrentNavDomain()))
            freshCompany(NavConfigUtils.getCurrentNavDomain());

        loginPresenter = LoginFactory.createLoginPresenter();
        loginPresenter.setLoginView(this);
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
                Toast.makeText(this,R.string.atom_ui_tip_request_permission,Toast.LENGTH_LONG).show();
                return;
            }

            Intent scanQRCodeIntent = new Intent(this, CaptureActivity.class);
            scanQRCodeIntent.putExtra(Constants.BundleKey.SCAN_QR_GET_NAV,true);
            startActivityForResult(scanQRCodeIntent, SCAN_REQUEST);
        }
    }

    protected void permissionCheck()
    {
        PermissionDispatcher.requestPermissionWithCheck(this, new int[]{PermissionDispatcher.REQUEST_CAMERA}, this,
                        LOGIN_REQUIRE);
    }

    private boolean isTestAccount(){
        return "".equals(qtuser_username_et.getText().toString());
    }

    void loginListener() {
        LogUtil.d("performance", "login start:" + System.currentTimeMillis() + "");
        if(!isTestAccount() && TextUtils.isEmpty(NavConfigUtils.getCurrentNavDomain())){
            showConfigNavDialog();
            return;
        }else if(isTestAccount()){
            QIMSdk.getInstance().setNavigationUrl(QtalkNavicationService.NAV_CONFIG_PUBLIC_DEFAULT + "?c=froyomu.com");
        }
        if(!atom_ui_eula_checkbox.isChecked()){
            Toast.makeText(this, "请勾选同意条款！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(qtuser_username_et.getText().toString().trim())) {
            Toast.makeText(this, R.string.atom_ui_common_input_username, Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(qtuser_password_et.getText().toString().trim()))
        {
            Toast.makeText(this, R.string.atom_ui_tip_pwdbox_input_pwd, Toast.LENGTH_SHORT).show();
            return;
        }
//        if(qtuser_company_layout.getVisibility() == View.VISIBLE && TextUtils.isEmpty(qtuser_company_et.getText().toString().trim()))
//        {
//            Toast.makeText(this, R.string.atom_ui_login_compnay_hint, Toast.LENGTH_SHORT).show();
//            return;
//        }
        //保存使用条款勾选状态
        DataUtils.getInstance(CommonConfig.globalContext).putPreferences(Constants.Preferences.EULA_CHECK_TAG + "_" + QtalkNavicationService.getInstance().getXmppdomain(),atom_ui_eula_checkbox.isChecked());
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
                        CurrentPreference.getInstance().setRememberMe(true);
                    } else {
                        CurrentPreference.getInstance().setRememberMe(false);
                    }

                    if (qtuser_auto_login_cbx.isChecked()) {
                        CurrentPreference.getInstance().setAutoLogin(true);
                    } else {
                        CurrentPreference.getInstance().setAutoLogin(false);
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
            loginListener();
        }
        else if(i == R.id.qtuser_tv_version) {
        }else if(i == R.id.qtuser_iv_nav_config){
            permissionCheck();
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

//        super.presenter.getMyCapability();
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
            }else if(requestCode == LOGIN_TYPE){
                if(bundle != null){
                    String company = bundle.getStringExtra(Constants.BundleKey.NAV_ADD_NAME);
                    freshCompany(company);
                    freshRightTitle(QtalkNavicationService.getInstance().isToC());
                }
                if(LoginType.SMSLogin.equals(QtalkNavicationService.getInstance().getLoginType())){
                    startActivity(new Intent(QTalkUserLoginActivity.this, LoginActivity.class));
                    finish();
                }

            }else if (requestCode == SCAN_REQUEST) {
                if (bundle != null) {
                    String content = bundle.getStringExtra("content");
                    if (!TextUtils.isEmpty(content)) {
                        if(content.contains("~")){
                            String[] strs = content.split("~");
                            navName = strs[0];
                            navUrl = strs[1];
                            getServerConfig(navName,navUrl);
                        }else{
                            navUrl = content;

                            Uri uri = Uri.parse(navUrl);
                            HashMap<String, String> map = Protocol.splitParams(uri);
                            String configurl = map.get("configurl");
                            String configname = map.get("configname");
                            if(!TextUtils.isEmpty(configurl)){
                                String url = new String(Base64.decode(configurl,Base64.NO_WRAP));
                                navName = configname;
                                navUrl = url;
                                getServerConfig(navName,navUrl);
                            }else {
                                redirectUrl(navUrl);
                            }

                            if(!TextUtils.isEmpty(configurl)){
                                String url = new String(Base64.decode(configurl,Base64.NO_WRAP));
                                navName = configname;
                                navUrl = url;
                            }
                        }
                    }
                }else {
                    if(LoginType.SMSLogin.equals(QtalkNavicationService.getInstance().getLoginType())){
                        startActivity(new Intent(QTalkUserLoginActivity.this, LoginActivity.class));
                        finish();
                    }
                }
            }
        } else{
            if(LoginType.SMSLogin.equals(QtalkNavicationService.getInstance().getLoginType())){
                startActivity(new Intent(QTalkUserLoginActivity.this, LoginActivity.class));
                finish();
            }
        }
    }

    private void redirectUrl(final String originUrl) {
        DispatchHelper.Async("redirectUrl",true, new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                try {
                    conn = (HttpURLConnection) new URL(originUrl).openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                conn.setInstanceFollowRedirects(false);
                conn.setConnectTimeout(5000);
                String url = conn.getHeaderField("Location");
                conn.disconnect();
                if(TextUtils.isEmpty(url)){
                    url = originUrl;
                }
                Uri uri = Uri.parse(url);
                HashMap<String, String> map = Protocol.splitParams(uri);
                String configurl = map.get("configurl");
                String configname = map.get("configname");
                if(!TextUtils.isEmpty(configurl)){
                    String realUrl = new String(Base64.decode(configurl,Base64.NO_WRAP));
                    navName = configname;
                    navUrl = realUrl;
                }
                getServerConfig(navName,navUrl);
            }
        });
    }

    private void freshCompany(final String company){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                atom_ui_nav_name.setText(company);
                NavConfigUtils.saveCurrentNavDomain(company);
                if(!TextUtils.isEmpty(company)){
                    atom_ui_nav_layouot.setVisibility(View.VISIBLE);
                    atom_ui_nav_name.setText(company);
                    qtuser_company_layout.setVisibility(View.GONE);
                }else {
                    atom_ui_nav_layouot.setVisibility(View.GONE);
                }
            }
        });

    }

    private void freshRightTitle(final boolean isToC){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                atom_ui_to_c_regiest.setVisibility(isToC ? View.VISIBLE : View.GONE);
                atom_ui_nav_config_add_scan_layout.setVisibility(isToC ? View.GONE : View.VISIBLE);
            }
        });
    }

    private void getServerConfig(final String name,final String url) {
        HttpUtil.getServerConfigAsync(url, new ProtocolCallback.UnitCallback<NavConfigResult>() {
            @Override
            public void onCompleted(final NavConfigResult navConfigResult) {
                String configStr = JsonUtils.getGson().toJson(navConfigResult);
                Logger.i("切换导航成功:" + configStr);
                final String navName = TextUtils.isEmpty(name) ? navConfigResult.baseaddess.domain : name;
                freshCompany(navName);
                freshRightTitle(navConfigResult.imConfig.isToC);
                //保存导航信息
                NavConfigUtils.saveNavInfo(navName,url);
                //保存当前配置
                NavConfigUtils.saveCurrentNavJSONInfo(navName,configStr);
                //配置导航
                QtalkNavicationService.getInstance().configNav(navConfigResult);
            }

            @Override
            public void onFailure(String errMsg) {
                toast(getString(R.string.atom_ui_tip_switch_navigation_failed));
            }
        });
    }

    private void showConfigNavDialog(){
        final Dialog dialog = new Dialog(this,R.style.myiosstyle);
        View view = getLayoutInflater().inflate(R.layout.atom_ui_dialog_nav_config_notice,null);
        view.findViewById(R.id.atom_ui_manu_config).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(QTalkUserLoginActivity.this,NavConfigActivity.class);
                startActivityForResult(intent, LOGIN_TYPE);
            }
        });
        view.findViewById(R.id.atom_ui_nav_config_scan_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                permissionCheck();
            }
        });
        view.findViewById(R.id.atom_ui_next_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(view);
        dialog.show();
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
