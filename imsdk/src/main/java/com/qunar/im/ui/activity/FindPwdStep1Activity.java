package com.qunar.im.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.qunar.im.base.util.Constants;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.QtNewActionBar;

/**
 * Created by froyomu on 2019/2/14
 * <p>
 * Describe:密码找回step1
 */
public class FindPwdStep1Activity extends IMBaseActivity {
    private TextView atom_ui_company_name;
    private String domainId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_find_pwd_step1);
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setActionBarTitle(R.string.atom_ui_forget_pwd);

        initView();
    }

    private void initView(){
        atom_ui_company_name = (TextView) findViewById(R.id.atom_ui_company_name);
        atom_ui_company_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FindPwdStep1Activity.this,QtalkUserHostActivity.class);
                startActivityForResult(intent,QtalkUserHostActivity.HOST_REQUEST_CODE);
            }
        });
    }

    public void nextStep(View view){
        if(TextUtils.isEmpty(atom_ui_company_name.getText())){
            toast(getString(R.string.atom_ui_login_compnay_hint));
            return;
        }
        Intent intent = new Intent(this,FindPwdStep2Activity.class);
        intent.putExtra(Constants.BundleKey.RESULT_DOMAIN_ID,domainId);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == QtalkUserHostActivity.HOST_RESPONSE_CODE && data != null){
            atom_ui_company_name.setText(data.getStringExtra(Constants.BundleKey.RESULT_HOST_NAME));
            domainId = data.getStringExtra(Constants.BundleKey.RESULT_DOMAIN_ID);
        }
    }
}
