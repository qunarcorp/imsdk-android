package com.qunar.im.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qunar.im.base.jsonbean.BaseJsonResult;
import com.qunar.im.base.protocol.LoginAPI;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.util.Constants;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.QtNewActionBar;

/**
 * Created by froyomu on 2019/2/14
 * <p>
 * Describe:密码找回step2
 */
public class FindPwdStep2Activity extends IMBaseActivity {
    private EditText atom_ui_mobile,atom_ui_pic_code_edt,atom_ui_sms_code_edt;
    private ImageView atom_ui_pic_code_img;
    private TextView atom_ui_get_sms_code;

    private String domainId;

    private CountDownTimer timer;

    private long millisInFuture = 60 * 1000;

    private int countTimes = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_find_pwd_step2);

        domainId = getIntent().getStringExtra(Constants.BundleKey.RESULT_DOMAIN_ID);
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setActionBarTitle(R.string.atom_ui_forget_pwd);

        timer = new CountDownTimer(millisInFuture,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                atom_ui_get_sms_code.setText(countTimes-- + "s");
            }

            @Override
            public void onFinish() {
                atom_ui_get_sms_code.setText(R.string.atom_ui_get_verify_code);
                atom_ui_get_sms_code.setClickable(true);
            }
        };

        initView();
        showPicCode();
    }

    private void initView(){
        atom_ui_mobile = (EditText) findViewById(R.id.atom_ui_mobile);
        atom_ui_pic_code_edt = (EditText) findViewById(R.id.atom_ui_pic_code_edt);
        atom_ui_sms_code_edt = (EditText) findViewById(R.id.atom_ui_sms_code_edt);

        atom_ui_pic_code_img = (ImageView) findViewById(R.id.atom_ui_pic_code_img);
        atom_ui_pic_code_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPicCode();
            }
        });
        atom_ui_get_sms_code = (TextView) findViewById(R.id.atom_ui_get_sms_code);
        atom_ui_get_sms_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSms();
            }
        });
    }

    private void showPicCode(){
        String url = "" + System.currentTimeMillis();
        Glide.with(this).load(url).into(atom_ui_pic_code_img);
    }

    private void sendSms(){
        String piccode = atom_ui_pic_code_edt.getText().toString();
        String mobile = atom_ui_mobile.getText().toString();
        if(TextUtils.isEmpty(piccode)){
            toast(getString(R.string.atom_ui_pic_code_hint));
            return;
        }
        if(TextUtils.isEmpty(mobile)){
            toast(getString(R.string.atom_ui_mobile_hint));
            return;
        }
        LoginAPI.sendForgetPwdSMS(domainId,piccode, mobile, new ProtocolCallback.UnitCallback<BaseJsonResult>() {
            @Override
            public void onCompleted(BaseJsonResult baseJsonResult) {
                if(baseJsonResult != null){
                    if(baseJsonResult.ret){
                        timer.start();
                        countTimes = 60;
                        atom_ui_get_sms_code.setClickable(false);
                        toast(getString(R.string.atom_ui_common_sent));
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

    public void nextStep(View view){
        final String smscode = atom_ui_sms_code_edt.getText().toString();
        if(TextUtils.isEmpty(smscode)){
            toast(getString(R.string.atom_ui_sms_code_hint));
            return;
        }

        final String mobile = atom_ui_mobile.getText().toString();
        if(TextUtils.isEmpty(mobile)){
            toast(getString(R.string.atom_ui_mobile_hint));
            return;
        }
        LoginAPI.checkSmsCode(mobile, smscode, new ProtocolCallback.UnitCallback<BaseJsonResult>() {
            @Override
            public void onCompleted(BaseJsonResult baseJsonResult) {
                if(baseJsonResult != null){
                    if(baseJsonResult.ret){
                        Intent intent = new Intent(FindPwdStep2Activity.this,FindPwdStep3Activity.class);
                        intent.putExtra(Constants.BundleKey.RESULT_DOMAIN_ID,domainId);
                        intent.putExtra(Constants.BundleKey.MOBILE,mobile);
                        intent.putExtra(Constants.BundleKey.SMS_CODE,smscode);
                        startActivity(intent);
                    }else {
                        toast(baseJsonResult.errmsg);
                    }
                }else {
                    toast(getString(R.string.atom_ui_tip_operation_failed));
                }
            }

            @Override
            public void onFailure(String errMsg) {

            }
        });
    }
}
