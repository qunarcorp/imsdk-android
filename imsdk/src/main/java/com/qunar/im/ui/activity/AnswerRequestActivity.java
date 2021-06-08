package com.qunar.im.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.ui.presenter.IAnswerBuddyPresenter;
import com.qunar.im.ui.presenter.IPersonalInfoPresenter;
import com.qunar.im.ui.presenter.impl.BuddyPresenter;
import com.qunar.im.ui.presenter.impl.QchatPersonalInfoPresenter;
import com.qunar.im.ui.presenter.views.IAnswerBuddyRequestView;
import com.qunar.im.ui.presenter.views.MyPersonalView;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.base.util.graphics.BitmapHelper;
import com.qunar.im.base.util.graphics.ImageUtils;
import com.qunar.im.base.util.graphics.MyDiskCache;
import com.qunar.im.common.CurrentPreference;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.QtNewActionBar;

import de.greenrobot.event.EventBus;

/**
 * Created by saber on 15-12-9.
 */
public class AnswerRequestActivity extends IMBaseActivity implements IAnswerBuddyRequestView {
    EditText edit_deny_reason;
    RadioButton radio_allow,radio_deny;
    TextView operation_btn,sign;
    SimpleDraweeView user_gravatar;
    RelativeLayout rl_header;

    ProgressDialog progressDialog;

    String jid;
    IPersonalInfoPresenter personalInfopresenter;
    IAnswerBuddyPresenter answerBuddyPresenter;

    HandleAnserRequestlEvent handleAnserRequestlEvent = new HandleAnserRequestlEvent();

    @Override
    public void onCreate(Bundle saved)
    {
        super.onCreate(saved);
        injectExtras();
        setContentView(R.layout.atom_ui_activity_answer_request);
        bindViews();
        personalInfopresenter = new QchatPersonalInfoPresenter();
        answerBuddyPresenter = new BuddyPresenter();
        EventBus.getDefault().register(handleAnserRequestlEvent);
        initViews();
    }

    private void injectExtras() {
        Bundle extras_ = getIntent().getExtras();
        if (extras_!= null) {
            if (extras_.containsKey("jid")) {
                jid = extras_.getString("jid");
            }
        }
    }

    private void bindViews() {
        rl_header = (RelativeLayout) findViewById(R.id.rl_header);
        user_gravatar = (com.facebook.drawee.view.SimpleDraweeView) findViewById(R.id.user_gravatar);
        sign = (TextView) findViewById(R.id.sign);
        radio_allow = (RadioButton) findViewById(R.id.radio_allow);
        radio_deny = (RadioButton) findViewById(R.id.radio_deny);
        edit_deny_reason = (EditText) findViewById(R.id.edit_deny_reason);
        operation_btn = (TextView) findViewById(R.id.operation_btn);
    }

    void initViews()
    {
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        actionBar.setBackgroundResource(R.drawable.atom_ui_gradient_linear_actionbar_selector);
        setNewActionBar(actionBar);
//        actionBar.getLeftIcon().setBackground(null);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getText(R.string.atom_ui_add_buddy_response));
        progressDialog.setMessage(getText(R.string.atom_ui_add_buddy_response_friend_request));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        personalInfopresenter.setPersonalInfoView(new MyPersonalView() {
            @Override
            public SimpleDraweeView getImagetView() {
                return user_gravatar;
            }

            @Override
            public String getJid() {
                return jid;
            }

            @Override
            public void setNickName(final String nick) {
                getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        setActionBarTitle(nick);
//                        myActionBar.getTitleTextview().setText(nick);
                    }
                });
            }
            @Override
            public void setLargeGravatarInfo(final String url, final String thumbPath) {
                getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(AnswerRequestActivity.this,ImageBrowersingActivity.class);
                        intent.putExtra(Constants.BundleKey.IMAGE_URL, url);
                        intent.putExtra(Constants.BundleKey.IMAGE_ON_LOADING, thumbPath);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public Context getContext() {
                return AnswerRequestActivity.this;
            }

        });
        answerBuddyPresenter.setAnswerView(this);
        radio_allow.setChecked(true);
        radio_deny.setChecked(false);
        radio_allow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    radio_deny.setChecked(false);
            }
        });
        radio_deny.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    radio_allow.setChecked(false);
                    edit_deny_reason.setVisibility(View.VISIBLE);
                } else {
                    edit_deny_reason.setVisibility(View.GONE);
                }
            }
        });
        operation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                BackgroundExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        answerBuddyPresenter.answerForRequest();
                    }
                });
            }
        });
    }

    @Override
    public void onStart()
    {
        super.onStart();
        personalInfopresenter.loadPersonalInfo();
    }

    @Override
    public void onDestroy()
    {
        EventBus.getDefault().unregister(handleAnserRequestlEvent);
        super.onDestroy();
    }

    @Override
    public boolean getFriendRequstResult() {
        return radio_allow.isChecked();
    }

    @Override
    public String getResean() {
        if(radio_allow.isChecked())return String.valueOf(getText(R.string.atom_ui_message_agree_friend));
        return edit_deny_reason.getText().length()==0?
            CurrentPreference.getInstance().getPreferenceUserId()+getText(R.string.atom_ui_message_refuse_friend):
                edit_deny_reason.getText().toString();
    }

    @Override
    public String getJid() {
        return jid;
    }

    @Override
    public void setStatus(boolean status) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                finish();
            }
        });
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    class HandleAnserRequestlEvent
    {
        public void onEvent(EventBusEvent.GravtarGot gravtarGot) {
            if(gravtarGot.jid!=null&&gravtarGot.jid.equals(jid)) {
                Bitmap gravantarBg = BitmapHelper.decodeFile(MyDiskCache.getSmallFile(gravtarGot.murl).getPath());
                if (gravantarBg != null) {
                    ImageUtils.blur(gravantarBg, rl_header);
                    gravantarBg.recycle();
                }
            }
        }
    }

}
