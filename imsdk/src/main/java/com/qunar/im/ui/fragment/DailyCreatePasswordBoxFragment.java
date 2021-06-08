package com.qunar.im.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.qunar.im.base.common.DailyMindConstants;
import com.qunar.im.ui.presenter.IDailyMindPresenter;
import com.qunar.im.base.util.AESTools;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.DailyPasswordBoxMainActivity;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 新建主密码
 * Created by lihaibin.li on 2017/8/22.
 */

public class DailyCreatePasswordBoxFragment extends BaseFragment {
    private String TAG = DailyCreatePasswordBoxFragment.class.getSimpleName();

    private EditText passbox_name;
    private EditText passbox_pwd;
    private EditText passbox_pwd_again;
    private CheckBox checkBox;
    private Button btnCreate;

    private DailyPasswordBoxMainActivity activity;

    private IDailyMindPresenter iDailyMindPresenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (DailyPasswordBoxMainActivity) getActivity();
        iDailyMindPresenter = activity.getPasswordBoxPresenter();
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.actionBar.getRightText().setVisibility(View.GONE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.atom_ui_fragment_daily_create_password_box, null);
        passbox_name = (EditText) view.findViewById(R.id.passbox_name);
        passbox_pwd = (EditText) view.findViewById(R.id.passbox_pwd);
        passbox_pwd_again = (EditText) view.findViewById(R.id.passbox_pwd_again);
        checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        btnCreate = (Button) view.findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkInput() || iDailyMindPresenter == null) return;
                try {
                    Map<String, String> params = new LinkedHashMap<String, String>();
                    params.put("type", DailyMindConstants.PASSOWRD + "");
                    params.put("title", passbox_name.getText().toString());
                    params.put("desc", "");
                    params.put("content", passbox_pwd.getText().toString());
                    String content = AESTools.encodeToBase64(passbox_pwd.getText().toString(), JsonUtils.getGson().toJson(params));
                    params.put("content", content);
                    iDailyMindPresenter.operateDailyMindFromHttp(DailyMindConstants.SAVE_TO_MAIN, params);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    private boolean checkInput() {
        if (TextUtils.isEmpty(passbox_name.getText().toString())) {
            toast("请输入" + passbox_name.getHint().toString());
            return false;
        }
        if (TextUtils.isEmpty(passbox_pwd.getText().toString())) {
            toast("请输入" + passbox_pwd.getHint().toString());
            return false;
        }
        if (!passbox_pwd.getText().toString().equals(passbox_pwd_again.getText().toString())) {
            toast("两次输入的密码箱密码不一致！");
            return false;
        }
        if (!checkBox.isChecked()) {
            toast("请勾选上方提示，表示您已同意！");
            return false;
        }
        return true;
    }

    private void toast(String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }


}
