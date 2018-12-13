package com.qunar.im.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qunar.im.base.presenter.impl.BuddyPresenter;
import com.qunar.im.base.presenter.views.IBuddyView;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.utils.QtalkStringUtils;

import de.greenrobot.event.EventBus;

/**
 * 发送好友验证信息页面 包括问题验证和答案验证
 */
public class AddAuthMessageActivity extends IMBaseActivity implements IBuddyView, View.OnClickListener {
    LinearLayout ll_manul_auth;
    LinearLayout ll_question_auth;
    EditText et_reason;
    EditText et_answer;
    TextView tv_notify;

    TextView tv_question;
    Button btn_send_auth;

    String jid;

    int mType;

    BuddyPresenter mBuddyPresenter;
    HanldleBuddyPresence hanldleBuddyPresence = new HanldleBuddyPresence();

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        injectExtras();
        setContentView(R.layout.atom_ui_activity_add_auth_message);
        bindViews();
        init();

        EventBus.getDefault().register(hanldleBuddyPresence);
    }

    private void injectExtras() {
        Bundle extras_ = getIntent().getExtras();
        if (extras_ != null) {
            if (extras_.containsKey("jid")) {
                jid = extras_.getString("jid");
            }
        }
    }

    private void bindViews() {

        ll_manul_auth = (LinearLayout) findViewById(R.id.ll_manul_auth);
        et_reason = (EditText) findViewById(R.id.et_reason);
        ll_question_auth = (LinearLayout) findViewById(R.id.ll_question_auth);
        tv_question = (TextView) findViewById(R.id.tv_question);
        et_answer = (EditText) findViewById(R.id.et_answer);
        tv_notify = (TextView) findViewById(R.id.tv_notify);
        btn_send_auth = (Button) findViewById(R.id.btn_send_auth);
        btn_send_auth.setOnClickListener(this);
    }

    public void init() {
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setActionBarTitle(R.string.atom_ui_title_add_buddy);
        initOriginalView();
        mBuddyPresenter = new BuddyPresenter();
        mBuddyPresenter.setBuddyView(this);
        mBuddyPresenter.addFriend();
        et_reason.setText(getText(R.string.atom_ui_my_is) + com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserName());

    }

    /**
     * 默认隐藏所有控件
     */
    public void initOriginalView() {
        ll_manul_auth.setVisibility(View.GONE);
        ll_question_auth.setVisibility(View.GONE);
        tv_notify.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onStop() {
        if (EventBus.getDefault().isRegistered(hanldleBuddyPresence))
            EventBus.getDefault().unregister(hanldleBuddyPresence);
        super.onStop();
    }


    @Override
    public void setQuestion(final String question) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                tv_question.setText(question);
            }
        });
    }

    void btn_send_auth() {
        mBuddyPresenter.sendAddBuddyRequest();
    }


    @Override
    public String updateView(int mode) {
        //0 全部拒绝 1.人工认证 2.答案认证 3.全部接收
        switch (mode) {
            case 0:
                mType = 0;
                getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        setNofity(false, (String) getText(R.string.atom_ui_message_refuse_friend));
                    }
                });
                break;
            case 1:
                mType = 1;
                getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        initOriginalView();
                        btn_send_auth.setVisibility(View.VISIBLE);
                        ll_manul_auth.setVisibility(View.VISIBLE);
                    }
                });

                break;
            case 2:
                mType = 2;
                getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        initOriginalView();
                        btn_send_auth.setVisibility(View.VISIBLE);
                        ll_question_auth.setVisibility(View.VISIBLE);
                    }
                });

                break;
            case 3:
                mType = 3;
                getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        initOriginalView();
                        btn_send_auth.setVisibility(View.VISIBLE);
                        ll_manul_auth.setVisibility(View.VISIBLE);
                    }
                });
                break;
        }
        return null;
    }

    public void setNofity(boolean isSuccess, String message) {
        if (isSuccess) {
            Toast.makeText(AddAuthMessageActivity.this, message, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            tv_notify.setVisibility(View.VISIBLE);
            tv_notify.setTextColor(Color.RED);
            tv_notify.setText(message);
        }
    }

    @Override
    public int getAuthType() {
        return mType;
    }

    @Override
    public String getAnswerForQuestion() {
        return et_answer.getText().toString().trim();
    }

    @Override
    public String getTargetId() {
        return jid;
    }

    @Override
    public String getRequestReason() {
        return et_reason.getText().toString().trim();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_send_auth) {
            btn_send_auth();

        }
    }

    class HanldleBuddyPresence {
        public void onEventMainThread(EventBusEvent.FriendsChange presence) {
            if (presence.result) {
                Toast.makeText(AddAuthMessageActivity.this, R.string.atom_ui_tip_add_buddy_success, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            setNofity(presence.result, (String) getText(R.string.atom_ui_tip_add_buddy_wrong_answer));
        }

        public void onEventMainThread(EventBusEvent.VerifyFriend verifyFriend) {
            if (verifyFriend.mode != null) {
                String value = verifyFriend.mode.get(QtalkStringUtils.parseId(jid));
                String Question = verifyFriend.mode.get("Question");
                if (!TextUtils.isEmpty(value)) {
                    updateView(Integer.parseInt(value));
                }
                if(!TextUtils.isEmpty(Question)){
                    setQuestion(Question);
                }
            }
        }
    }
}
