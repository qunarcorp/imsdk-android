package com.qunar.im.ui.fragment;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.qunar.im.base.common.DailyMindConstants;
import com.qunar.im.ui.presenter.IDailyMindPresenter;
import com.qunar.im.base.util.AESTools;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.DailyPasswordBoxSubActivity;
import com.qunar.im.ui.util.GenerateRandomPassword;

import java.util.HashMap;
import java.util.Map;

/**
 * 子密码创建
 * Created by lihaibin.li on 2017/8/23.
 */

public class DailyCreatePasswordBoxSubFragment extends BaseFragment implements View.OnClickListener {
    private String TAG = DailyCreatePasswordBoxSubFragment.class.getSimpleName();

    private DailyPasswordBoxSubActivity activity;

    private Button btnCreate;
    private EditText sub_password;
    private EditText sub_name;
    private ImageView password_eye;
    private TextView create_pwd;

    private SeekBar seekbar_length;
    private TextView seekbar_length_text;

    private SeekBar seekbar_digit;
    private TextView seekbar_digit_text;

    private SeekBar seekbar_symbol;
    private TextView seekbar_symbol_text;

    private SeekBar seekbar_upcase;
    private TextView seekbar_upcase_text;

    private IDailyMindPresenter passwordPresenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (DailyPasswordBoxSubActivity) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.actionBar.getRightText().setVisibility(View.GONE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.atom_ui_fragment_daily_create_password_box_sub, null);
        btnCreate = (Button) view.findViewById(R.id.btnCreate);
        sub_password = (EditText) view.findViewById(R.id.sub_password);
        sub_name = (EditText) view.findViewById(R.id.sub_name);
        seekbar_length = (SeekBar) view.findViewById(R.id.seekbar_length);
        seekbar_length_text = (TextView) view.findViewById(R.id.seekbar_length_text);
        seekbar_length.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int length = i + 6;
                seekbar_length_text.setText(String.valueOf(length));
                seekbar_digit.setMax(length / 2);
                seekbar_digit.setProgress(seekbar_digit.getMax() / 2);
                seekbar_digit_text.setText(String.valueOf(seekbar_digit.getProgress()));

                seekbar_symbol.setMax(length / 3);
                seekbar_symbol.setProgress(seekbar_symbol.getMax() / 2);
                seekbar_symbol_text.setText(String.valueOf(seekbar_symbol.getProgress()));

                seekbar_upcase.setMax(length - seekbar_digit.getMax() - seekbar_symbol.getMax());
                seekbar_upcase.setProgress(seekbar_upcase.getMax() / 2);
                seekbar_upcase_text.setText(String.valueOf(seekbar_upcase.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekbar_digit = (SeekBar) view.findViewById(R.id.seekbar_digit);
        seekbar_digit_text = (TextView) view.findViewById(R.id.seekbar_digit_text);
        seekbar_digit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                seekbar_digit_text.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekbar_symbol = (SeekBar) view.findViewById(R.id.seekbar_symbol);
        seekbar_symbol_text = (TextView) view.findViewById(R.id.seekbar_symbol_text);
        seekbar_symbol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                seekbar_symbol_text.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekbar_upcase = (SeekBar) view.findViewById(R.id.seekbar_upcase);
        seekbar_upcase_text = (TextView) view.findViewById(R.id.seekbar_upcase_text);
        seekbar_upcase.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                seekbar_upcase_text.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        create_pwd = (TextView) view.findViewById(R.id.create_pwd);
        password_eye = (ImageView) view.findViewById(R.id.password_eye);
        password_eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = sub_password.getSelectionStart();
                sub_password.setTransformationMethod(sub_password.getTransformationMethod() instanceof PasswordTransformationMethod ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
                sub_password.setSelection(pos);
            }
        });
        create_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sub_password.setText(GenerateRandomPassword.creatGenerateRandomPassword(seekbar_digit.getProgress(), seekbar_length.getProgress() + 6 - seekbar_digit.getProgress() - seekbar_symbol.getProgress(), seekbar_symbol.getProgress(),seekbar_upcase.getProgress()));
            }
        });
        create_pwd.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        create_pwd.getPaint().setAntiAlias(true);//抗锯齿
        btnCreate.setOnClickListener(this);

        passwordPresenter = activity.getPasswordPresenter();
        return view;
    }

    @Override
    public void onClick(View view) {
        if (TextUtils.isEmpty(sub_name.getText().toString())) {
            toast("请输入密码名称！");
            return;
        }
        if (TextUtils.isEmpty(sub_password.getText().toString())) {
            toast("请输入密码！");
            return;
        }
        try {
            Map<String, String> params = new HashMap<>();
            params.put("qid", activity.getDailyMindMain().qid + "");
            params.put("type", DailyMindConstants.PASSOWRD + "");
            params.put("title", sub_name.getText().toString());
            params.put("desc", "");
            params.put("P", sub_password.getText().toString());
            params.put("U",sub_name.getText().toString());
            String content = AESTools.encodeToBase64(activity.getMain_password(), JsonUtils.getGson().toJson(params));
            params.put("content", content);
            passwordPresenter.operateDailyMindFromHttp(DailyMindConstants.SAVE_TO_SUB, params);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void toast(String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }


}
