package com.qunar.im.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.qunar.im.common.CurrentPreference;
import com.qunar.im.ui.presenter.IChangeBuddyPrivacySetting;
import com.qunar.im.ui.presenter.impl.BuddyPrivacySettingPresenter;
import com.qunar.im.ui.R;


/**
 * 隐私设置页面
 */
public class PrivacySettingFragment extends BaseFragment implements View.OnClickListener ,IChangeBuddyPrivacySetting {
    RadioGroup rg_verfiy_choice;
    RadioButton radio_diny_all,radio_question_verify,radio_manual_verify,radio_allow_all;

    Button btn_sure;
    EditText et_verify_question,et_verify_answer;
    LinearLayout ll_question_container;

    BuddyPrivacySettingPresenter presenter;
    boolean flag;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new BuddyPrivacySettingPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.atom_ui_fragment_privacy_setting, container, false);
        rg_verfiy_choice = (RadioGroup) view.findViewById(R.id.rg_verfiy_choice);
        radio_allow_all = (RadioButton)view.findViewById(R.id.radio_allow_all);
        radio_manual_verify = (RadioButton) view.findViewById(R.id.radio_manual_verify);
        radio_diny_all = (RadioButton) view.findViewById(R.id.radio_diny_all);
        radio_question_verify = (RadioButton) view.findViewById(R.id.radio_question_verify);
        ll_question_container = (LinearLayout) view.findViewById(R.id.ll_question_container);
        et_verify_question = (EditText) view.findViewById(R.id.et_verify_question);
        et_verify_answer = (EditText) view.findViewById(R.id.et_verify_answer);
        btn_sure = (Button) view.findViewById(R.id.btn_sure);
        initView();
        return view;
    }

    void initView(){
        if(!flag) {
            radio_allow_all.setOnClickListener(this);
            radio_question_verify.setOnClickListener(this);
            radio_manual_verify.setOnClickListener(this);
            radio_diny_all.setOnClickListener(this);
            btn_sure.setOnClickListener(this);
            flag = true;
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        presenter.getMode(CurrentPreference.getInstance().getPreferenceUserId());
    }


    @Override
    public Context getContext() {
        return getActivity().getApplicationContext();
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.radio_question_verify) {
            ll_question_container.setVisibility(View.VISIBLE);

        } else if (i == R.id.radio_allow_all) {
            ll_question_container.setVisibility(View.INVISIBLE);

        } else if (i == R.id.radio_diny_all) {
            ll_question_container.setVisibility(View.INVISIBLE);

        } else if (i == R.id.radio_manual_verify) {
            ll_question_container.setVisibility(View.INVISIBLE);

        } else if (i == R.id.btn_sure) {
            updateServer();

        }
    }

    public void updateServer(){
        BuddyPrivacySettingPresenter.VerifyQuestion question = new BuddyPrivacySettingPresenter.VerifyQuestion();
       int id =  rg_verfiy_choice.getCheckedRadioButtonId();
        int mode = 3;
        if (id == R.id.radio_question_verify) {
            mode = 2;
            question.question = et_verify_question.getText().toString().trim();
            question.answer = et_verify_answer.getText().toString().trim();
            if (TextUtils.isEmpty(question.question) || TextUtils.isEmpty(question.answer)) {
                Toast.makeText(PrivacySettingFragment.this.getActivity(), R.string.atom_ui_tip_privacy_qa_null, Toast.LENGTH_LONG).show();
                return;
            }

        } else if (id == R.id.radio_allow_all) {
            mode = 3;

        } else if (id == R.id.radio_diny_all) {
            mode = 0;

        } else if (id == R.id.radio_manual_verify) {
            mode = 1;

        }
        presenter.updateServer(mode, question);
    }



    @Override
    public void setMode(int type, final BuddyPrivacySettingPresenter.VerifyQuestion question) {
        Handler handler = PrivacySettingFragment.this.getHandler();
        switch (type){////0 全部拒绝 1.人工认证 2.答案认证 3.全部接收
            case 0:
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        radio_diny_all.setChecked(true);
                    }
                });
                break;
            case 1:
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        radio_manual_verify.setChecked(true);
                    }
                });
                break;
            case 2:
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        radio_question_verify.setChecked(true);
                        ll_question_container.setVisibility(View.VISIBLE);
                        et_verify_answer.setText(question.answer);
                        et_verify_question.setText(question.question);
                    }
                });
                break;
            case 3:
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        radio_allow_all.setChecked(true);
                    }
                });

                break;
        }
    }


    @Override
    public void setResult(final boolean isSuccess) {
        PrivacySettingFragment.this.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if(isSuccess){
                    Toast.makeText(PrivacySettingFragment.this.getActivity(),"成功更新隐私设置",Toast.LENGTH_LONG).show();
                    PrivacySettingFragment.this.getActivity().onBackPressed();
                }else{
                    Toast.makeText(PrivacySettingFragment.this.getActivity(),"网络错误，请重新设置",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
