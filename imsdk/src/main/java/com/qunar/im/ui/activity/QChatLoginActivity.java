package com.qunar.im.ui.activity;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.module.Nick;
import com.qunar.im.permission.PermissionCallback;
import com.qunar.im.permission.PermissionDispatcher;
import com.qunar.im.ui.presenter.ILoginPresenter;
import com.qunar.im.ui.presenter.factory.LoginFactory;
import com.qunar.im.ui.presenter.views.ILoginView;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.base.util.Utils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.ui.R;
import com.qunar.im.ui.util.CountryUtil;
import com.qunar.im.ui.util.ParseErrorEvent;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.utils.ConnectionUtil;

import de.greenrobot.event.EventBus;

/**
 * Created by saber on 16-2-26.
 */
public class QChatLoginActivity extends IMBaseActivity implements
        View.OnClickListener, ILoginView, View.OnFocusChangeListener, PermissionCallback {
    private final int SELECT_COUNTRY_CODE = 0x01;
    private final int WEB_LOGIN = 0x02;

    private static final int LOGIN_REQUIRE = PermissionDispatcher.getRequestCode();

    TextView tv_reset_account, tv_other_login_type, tv_sel_country_region;
    LinearLayout ll_country_region, ll_username, ll_password;
    EditText editText_username, edit_password;
    ProgressDialog progressDialog;
    Button btnlogin;
    ImageView img_show_pwd;

    ILoginPresenter loginPresenter;

    int selectCountryId = R.string.atom_ui_china;

    int lr_padding;
    int tb_padding;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.atom_ui_activity_complex_login);
        lr_padding = Utils.dipToPixels(QChatLoginActivity.this, 16);
        tb_padding = Utils.dipToPixels(QChatLoginActivity.this, 10);
        bindViews();
        initViews();
        if (!TextUtils.isEmpty(com.qunar.im.protobuf.common.CurrentPreference.getInstance().getQvt())) {
            com.qunar.im.protobuf.common.CurrentPreference.getInstance().setQvt("");
        }

        go2WebLogin();
    }

    @Override
    public void onPause() {
        CommonConfig.loginViewHasShown = false;
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        loginPresenter.release();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        CommonConfig.loginViewHasShown = true;
        super.onResume();
        setActionBarTitle(R.string.atom_ui_title_login);
    }

    private void bindViews() {
        tv_reset_account = (TextView) findViewById(R.id.tv_reset_account);
        tv_other_login_type = (TextView) findViewById(R.id.tv_other_login_type);
        tv_sel_country_region = (TextView) findViewById(R.id.tv_sel_country_region);
        ll_country_region = (LinearLayout) findViewById(R.id.ll_country_region);
        editText_username = (EditText) findViewById(R.id.editText_username);
        edit_password = (EditText) findViewById(R.id.edit_password);
        btnlogin = (Button) findViewById(R.id.btnlogin);
        ll_username = (LinearLayout) findViewById(R.id.ll_username);
        ll_password = (LinearLayout) findViewById(R.id.ll_password);
        img_show_pwd = (ImageView) findViewById(R.id.img_show_pwd);

        ll_country_region.setOnClickListener(this);
        tv_other_login_type.setOnClickListener(this);
        btnlogin.setOnClickListener(this);
        tv_reset_account.setOnClickListener(this);

        editText_username.setOnFocusChangeListener(this);
        edit_password.setOnFocusChangeListener(this);

        img_show_pwd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                        edit_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        //edit_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        edit_password.setInputType(InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        //edit_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        return true;
                }
                return false;
            }
        });
    }

    private void initViews() {
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setTitle("QChat");
        setActionBarLeftIcon(false);
        loginPresenter = LoginFactory.createLoginPresenter();
        loginPresenter.setLoginView(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.atom_ui_tip_dialog_prompt);
        progressDialog.setMessage(getText(R.string.atom_ui_tip_login_logining));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    @Override
    public void responsePermission(int requestCode, boolean granted) {
        if (requestCode == LOGIN_REQUIRE) {
            if (!granted) {
                Toast.makeText(this, R.string.atom_ui_tip_no_file_permissions, Toast.LENGTH_LONG).show();
            }
            loginHandler();
        }
    }

    protected void loginCheck() {
        PermissionDispatcher.
                requestPermissionWithCheck(this, new int[]{PermissionDispatcher.REQUEST_WRITE_EXTERNAL_STORAGE,
                                PermissionDispatcher.REQUEST_READ_EXTERNAL_STORAGE}, this,
                        LOGIN_REQUIRE);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.ll_country_region) {
            Intent intent = new Intent(this, CountrySelectorActivity.class);
            intent.putExtra(Constants.BundleKey.SELECT_COUNTRY_ID, selectCountryId);
            startActivityForResult(intent, SELECT_COUNTRY_CODE);

        } else if (i == R.id.tv_other_login_type) {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
            Intent intent1 = new Intent(this, LoginActivity.class);
            intent1.putExtra(Constants.BundleKey.CAN_BACK, true);
            startActivity(intent1);

        } else if (i == R.id.btnlogin) {
            loginCheck();
        } else if (i == R.id.tv_reset_account) {
            go2WebLogin();
        }
    }

    protected void loginHandler() {
        if (TextUtils.isEmpty(editText_username.getText().toString().trim())) {
            Toast.makeText(this, R.string.atom_ui_common_input_username, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(edit_password.getText().toString().trim())) {
            Toast.makeText(this, R.string.atom_ui_tip_pwdbox_input_pwd, Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.show();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        loginPresenter.login();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent bundle) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_COUNTRY_CODE) {
                if (bundle != null && bundle.getExtras() != null &&
                        bundle.getExtras().containsKey(Constants.BundleKey.SELECT_COUNTRY_ID)) {
                    selectCountryId = bundle.getExtras().getInt(Constants.BundleKey.SELECT_COUNTRY_ID);
                    String text = getString(selectCountryId) + "(" +
                            CountryUtil.countries.get(selectCountryId) + ")";
                    tv_sel_country_region.setText(text);
                }
            } else if (requestCode == WEB_LOGIN) {
                if (bundle != null && bundle.getExtras() != null &&
                        bundle.getExtras().containsKey(Constants.BundleKey.WEB_LOGIN_RESULT)) {
                    String jsonStr = bundle.getExtras().getString(Constants.BundleKey.WEB_LOGIN_RESULT);
                    if (!TextUtils.isEmpty(jsonStr)) {
                        DataUtils.getInstance(CommonConfig.globalContext).putPreferences(Constants.Preferences.qchat_qvt, jsonStr);
                        com.qunar.im.protobuf.common.CurrentPreference.getInstance().setQvt(jsonStr);
                        com.qunar.im.protobuf.common.CurrentPreference.getInstance().setRememberMe(true);
                        com.qunar.im.protobuf.common.CurrentPreference.getInstance().setAutoLogin(true);
                        loginPresenter.login();
                    }
                }
            }
        } else {
            Utils.jump2Desktop(this);
            this.finish();
        }
    }

    //收到登录的结果
    public void onEventMainThread(EventBusEvent.LoginComplete loginComplete) {
        if (loginComplete.loginStatus) {
            finish();
        }
    }

    protected void go2WebLogin() {
        Intent intent = new Intent(QChatLoginActivity.this, QunarWebActvity.class);
        intent.putExtra(Constants.BundleKey.WEB_FROM, Constants.BundleValue.UC_LOGIN);
        intent.putExtra(WebMsgActivity.IS_HIDE_BAR, !CommonConfig.isDebug);
        intent.setData(Uri.parse("https://user.qunar.com/mobile/login.jsp?ret=" +
                Constants.BundleKey.WEB_LOGIN_RESULT + "&loginType=mobile&onlyLogin=true"));
        startActivityForResult(intent, WEB_LOGIN);
    }

    @Override
    public String getUserName() {
        return editText_username.getText().toString().trim();
    }

    @Override
    public String getPassword() {
        return edit_password.getText().toString().trim();
    }

    @Override
    public void setLoginResult(final boolean success, int errcode) {
        if (!success && (errcode == 10 || errcode == 20 || errcode == 30 ||
                errcode == 100 || errcode == 200)) {
            QunarIMApp.mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(QChatLoginActivity.this, "保障帐号安全，需要增强验证", Toast.LENGTH_SHORT).show();
                    go2WebLogin();
                }
            });

            return;
        }
        if (!success && errcode == 300) {
            QunarIMApp.mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(QChatLoginActivity.this, "不允许登陆，强制重置密码后登陆", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        if (!success && errcode == 400) {
            QunarIMApp.mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(QChatLoginActivity.this, "不允许登陆，强制绑定手机后登陆", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        if (!success && errcode == 500) {
            QunarIMApp.mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(QChatLoginActivity.this, "不允许登陆，强制回答密保问题后登陆", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        if (!success && errcode == 1000) {
            QunarIMApp.mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(QChatLoginActivity.this, "该帐号禁止登陆", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
//        super.presenter.getMyCapability();

        com.qunar.im.protobuf.common.CurrentPreference.getInstance().setRememberMe(true);
        com.qunar.im.protobuf.common.CurrentPreference.getInstance().setAutoLogin(true);

        getHandler().post(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                if (success) {
                    Toast.makeText(QChatLoginActivity.this, R.string.atom_ui_tip_login_successful, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(QChatLoginActivity.this, R.string.atom_ui_login_faild, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public String getPrenum() {
        return CountryUtil.countries.get(selectCountryId).substring(1);
    }

    @Override
    public Context getContext() {
        return this;
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

//        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        commonDialog.setTitle(R.string.atom_ui_tip_dialog_prompt);
        commonDialog.setMessage(getText(R.string.atom_ui_tip_login_failed) + ParseErrorEvent.getError(str,this));
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
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int i = v.getId();
        if (i == R.id.editText_username) {
            if (hasFocus) {
                ll_username.setBackgroundResource(R.drawable.atom_ui_bottom_border_primary_color);
                ll_username.setPadding(lr_padding, tb_padding, lr_padding, tb_padding);
            } else {
                ll_username.setBackgroundResource(R.drawable.atom_ui_bottom_border_gray_color);
                ll_username.setPadding(lr_padding, tb_padding, lr_padding, tb_padding);
            }

        } else if (i == R.id.edit_password) {
            if (hasFocus) {
                ll_password.setBackgroundResource(R.drawable.atom_ui_bottom_border_primary_color);
                ll_password.setPadding(lr_padding, tb_padding, lr_padding, tb_padding);
            } else {
                ll_password.setBackgroundResource(R.drawable.atom_ui_bottom_border_gray_color);
                ll_password.setPadding(lr_padding, tb_padding, lr_padding, tb_padding);
            }

        }
    }

    @Override
    public void onBackPressed() {
        Utils.jump2Desktop(this);
        this.finish();
    }

}
