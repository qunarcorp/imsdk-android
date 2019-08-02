package com.qunar.im.ui.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qunar.im.base.common.DailyMindConstants;
import com.qunar.im.base.jsonbean.DailyMindSub;
import com.qunar.im.ui.presenter.IDailyMindPresenter;
import com.qunar.im.ui.presenter.impl.DailyMindPresenter;
import com.qunar.im.ui.presenter.views.IDailyMindSubEditView;
import com.qunar.im.base.util.AESTools;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;

/**
 * Created by lihaibin.li on 2017/8/25.
 */

public class DailyPasswordBoxSubEditActivity extends SwipeBackActivity implements IDailyMindSubEditView {
    private String TAG = DailyPasswordBoxSubEditActivity.class.getSimpleName();

    public static int PASSWORD_BOX_EDIT_REQUEST_CODE = 0x11;
    public static int PASSWORD_BOX_EDIT_RESULT_CODE = 0x12;

    private EditText sub_name;
    private EditText sub_password;
    private TextView copy_sub_password;
    private ImageView password_eye;

    private DailyMindSub dailyMindSub;
    private String passwordMain;

    private IDailyMindPresenter passwordPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_daily_password_box_sub_edit);

        Intent intent = getIntent();
        dailyMindSub = (DailyMindSub) intent.getSerializableExtra("dailyMindSub");
        passwordMain = intent.getStringExtra("passwordMain");

        passwordPresenter = new DailyMindPresenter();
        passwordPresenter.setView(this);

        initView();
    }

    private void initView() {
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setActionBarTitle(dailyMindSub.title);
        setActionBarRightText(R.string.atom_ui_common_save);
        setActionBarRightTextClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dailyMindSub.title = sub_name.getText().toString();
                    dailyMindSub.P = sub_password.getText().toString();
                    dailyMindSub.U = sub_name.getText().toString();
                    dailyMindSub.state = DailyMindConstants.UPDATE;
                    dailyMindSub.content = "";
                    dailyMindSub.content = AESTools.encodeToBase64(passwordMain, JsonUtils.getGson().toJson(dailyMindSub));
                    passwordPresenter.operateDailyMindFromHttp(DailyMindConstants.UPDATE_SUB, dailyMindSub);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        sub_name = (EditText) findViewById(R.id.sub_name);
        sub_password = (EditText) findViewById(R.id.sub_password);
        copy_sub_password = (TextView) findViewById(R.id.copy_sub_password);
        copy_sub_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCopy(sub_password.getText().toString());
            }
        });
        sub_name.setText(dailyMindSub.title);
        try {
            DailyMindSub pbs = JsonUtils.getGson().fromJson(AESTools.decodeFromBase64(passwordMain, dailyMindSub.content), DailyMindSub.class);
            sub_password.setText(pbs.P);
        } catch (Exception e) {
            e.printStackTrace();
        }
        password_eye = (ImageView) findViewById(R.id.password_eye);
        password_eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = sub_password.getSelectionStart();
                sub_password.setTransformationMethod(sub_password.getTransformationMethod() instanceof PasswordTransformationMethod ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
                sub_password.setSelection(pos);
            }
        });
    }

    @Override
    public void updatePasswordBoxSub(DailyMindSub dailyMindSub) {
        Intent intent = new Intent();
        intent.putExtra("data", dailyMindSub);
        setResult(PASSWORD_BOX_EDIT_RESULT_CODE, intent);
        finish();
    }

    public void onClickCopy(String s) {
        // 从API11开始android推荐使用android.content.ClipboardManager
        // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(s);
        Toast.makeText(this, R.string.atom_ui_tip_copied, Toast.LENGTH_SHORT).show();
    }
}
