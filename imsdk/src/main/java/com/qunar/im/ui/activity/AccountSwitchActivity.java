package com.qunar.im.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orhanobut.logger.Logger;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.ui.presenter.impl.QTalkPublicLoginPresenterNew;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.HttpUtil;
import com.qunar.im.base.jsonbean.AccountPassword;
import com.qunar.im.base.jsonbean.NavConfigResult;
import com.qunar.im.base.module.Nick;
import com.qunar.im.ui.presenter.ILoginPresenter;
import com.qunar.im.ui.presenter.impl.LoginPresenter;
import com.qunar.im.ui.presenter.impl.QTalkPublicLoginPresenter;
import com.qunar.im.ui.presenter.views.ILoginView;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.util.AccountSwitchUtils;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.base.util.IMUserDefaults;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.protobuf.common.LoginType;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.AccountAdapter;
import com.qunar.im.ui.util.NavConfigUtils;
import com.qunar.im.ui.util.ParseErrorEvent;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;

import java.util.List;

/**
 * Created by lihaibin.li on 2017/9/7.
 */

public class AccountSwitchActivity extends SwipeBackActivity implements ILoginView {
    private static final String TAG = AccountSwitchActivity.class.getSimpleName();

    private QtNewActionBar actionBar;
    private TextView account_add;
    private PullToRefreshListView account_listview;
    private AccountAdapter adapter;
    private String userName;
    private String password;

    ProgressDialog mProgressDialog;

    ILoginPresenter loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_account_switch);

        if (LoginType.PasswordLogin.equals(QtalkNavicationService.getInstance().getLoginType())) {
            loginPresenter = new QTalkPublicLoginPresenter();
        } else if(LoginType.NewPasswordLogin.equals(QtalkNavicationService.getInstance().getLoginType())){
            loginPresenter = new QTalkPublicLoginPresenterNew();
        } else {
            loginPresenter = new LoginPresenter();
        }
        loginPresenter.setLoginView(this);
        initView();
    }

    private void initView() {
        actionBar = (QtNewActionBar) findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setActionBarTitle(R.string.atom_ui_setting_switch_account);
//        actionBar.getTitleTextview().setText();

        account_add = (TextView) findViewById(R.id.account_add);
        account_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLoginView();
                finish();
            }
        });
        account_listview = (PullToRefreshListView) findViewById(R.id.account_listview);
        account_listview.setMode(PullToRefreshBase.Mode.DISABLED);
        List<AccountPassword> list = AccountSwitchUtils.getAccounts();
        adapter = new AccountAdapter(this, list, R.layout.atom_ui_item_account);
        account_listview.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AccountPassword ap = adapter.getItem(i - 1);
                Logger.i("点击账号切换，读取数据库信息AccountInfo = " + ap.toString());
                userName = ap.userid;
                password = ap.password;
                if ((CurrentPreference.getInstance().getUserid() + getCurrentNavName()).equals(userName + ap.navname))
                    return;
                showSwitchDialog();
                String navUrl = ap.navurl;
                if (!TextUtils.isEmpty(navUrl)) {
                    CurrentPreference.getInstance().setUserid(userName);
                    getServerConfig(navUrl, ap.navname);
                }
            }
        });
        account_listview.setAdapter(adapter);
    }

    private void showSwitchDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(R.string.atom_ui_setting_switch_account);
        mProgressDialog.setMessage(getText(R.string.atom_ui_tip_login_logining));
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();
    }

    private void autoLogin() {
        IMUserDefaults.getStandardUserDefaults().newEditor(this).putObject(Constants.Preferences.lastuserid, userName).synchronize();
        IMUserDefaults.getStandardUserDefaults().newEditor(this).putObject(Constants.Preferences.usertoken, password).synchronize();
        Logger.i("切换账号开始重连");
        CurrentPreference.getInstance().setSwitchAccount(true);
        ConnectionUtil.getInstance().setInitialized(false);
        ConnectionUtil.getInstance().reConnection();
    }

    private void startLoginView() {
        if (!LoginType.SMSLogin.equals(QtalkNavicationService.getInstance().getLoginType())) {
            Intent intent = new Intent(this, QTalkUserLoginActivity.class);
            intent.putExtra(Constants.BundleKey.IS_SWITCH_ACCOUNT, true);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra(Constants.BundleKey.IS_SWITCH_ACCOUNT, true);
            startActivity(intent);
        }

    }

    private void getServerConfig(final String url, final String navName) {
        HttpUtil.getServerConfigAsync(url, new ProtocolCallback.UnitCallback<NavConfigResult>() {
            @Override
            public void onCompleted(final NavConfigResult navConfigResult) {
                NavConfigUtils.saveCurrentNavDomain(navName);
                NavConfigUtils.saveNavInfo(navName,url);
                NavConfigUtils.saveCurrentNavJSONInfo(navName, JsonUtils.getGson().toJson(navConfigResult));
                QtalkNavicationService.getInstance().configNav(navConfigResult);
                //先登出
                ConnectionUtil.getInstance().pbLogout();

                autoLogin();
            }

            @Override
            public void onFailure(String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mProgressDialog != null && mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        Toast.makeText(AccountSwitchActivity.this, R.string.atom_ui_tip_nav_load_failed, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    //获取当前share存储的导航名称
    private String getCurrentNavName() {
        return DataUtils.getInstance(CommonConfig.globalContext).getPreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_NAME, AccountSwitchUtils.defalt_nav_name);
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setLoginResult(boolean success, int errcode) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) mProgressDialog.dismiss();
        finish();

    }

    @Override
    public String getPrenum() {
        return null;
    }

    @Override
    public boolean isSwitchAccount() {
        return true;
    }

    @Override
    public void getVirtualUserRole(boolean b) {

    }

    @Override
    public void connectInterrupt() {

    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void noNetWork() {

    }

    @Override
    public void LoginFailure(int str) {
        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
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
    public void setHeaderImage(Nick nick) {

    }

    @Override
    public void tryToConnect(String str) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loginPresenter.release();
    }
}
