package com.qunar.im.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.qunar.im.base.jsonbean.BaseJsonResult;
import com.qunar.im.base.protocol.LoginAPI;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.QtNewActionBar;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by froyomu on 2019/2/14
 * <p>
 * Describe:密码找回step3
 */
public class FindPwdStep3Activity extends IMBaseActivity {
    private EditText atom_ui_reset_pwd,atom_ui_reset_pwd_confirm;
    private String domainId;
    private String mobile;
    private String smscode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_find_pwd_step3);

        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setActionBarTitle(R.string.atom_ui_forget_pwd);

        Intent intent = getIntent();
        domainId = intent.getStringExtra(Constants.BundleKey.RESULT_DOMAIN_ID);
        mobile = intent.getStringExtra(Constants.BundleKey.MOBILE);
        smscode = intent.getStringExtra(Constants.BundleKey.SMS_CODE);

        atom_ui_reset_pwd = (EditText) findViewById(R.id.atom_ui_reset_pwd);
        atom_ui_reset_pwd_confirm = (EditText) findViewById(R.id.atom_ui_reset_pwd_confirm);
    }

    public void complete(View view){
        String newpassword = atom_ui_reset_pwd.getText().toString();
        String confirmpassword = atom_ui_reset_pwd_confirm.getText().toString();

        if(TextUtils.isEmpty(newpassword) || TextUtils.isEmpty(confirmpassword)){
            toast(getString(R.string.atom_ui_tip_pwdbox_input_pwd));
            return;
        }
        Map<String,String> params = new HashMap<>();
        params.put("domainId",domainId);
        params.put("phonenumber",mobile);
        params.put("messagecode",smscode);
        params.put("newpassword",newpassword);
        params.put("confirmpassword",confirmpassword);
        LoginAPI.resetPwd(JsonUtils.getGson().toJson(params), new ProtocolCallback.UnitCallback<BaseJsonResult>() {
            @Override
            public void onCompleted(BaseJsonResult baseJsonResult) {
                if(baseJsonResult != null){
                    if(baseJsonResult.ret){
                        toast("密码已重置！");
                        Intent intent = new Intent(FindPwdStep3Activity.this,QTalkUserLoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }else {
                        toast(baseJsonResult.errmsg);
                    }
                }else {
                    toast(getString(R.string.atom_ui_tip_operation_failed));
                }
            }

            @Override
            public void onFailure(String errMsg) {
                toast(errMsg);
            }
        });
    }
}
