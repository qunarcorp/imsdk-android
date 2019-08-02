package com.qunar.im.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.jsonbean.SeatStatusResult;
import com.qunar.im.base.module.Nick;
import com.qunar.im.ui.presenter.IEditMyProfilePresenter;
import com.qunar.im.ui.presenter.IPersonalInfoPresenter;
import com.qunar.im.ui.presenter.IServiceStatePresenter;
import com.qunar.im.ui.presenter.factory.PersonalInfoFactory;
import com.qunar.im.ui.presenter.impl.EditMyProfilePresenter;
import com.qunar.im.ui.presenter.impl.ServiceStatePresenter;
import com.qunar.im.ui.presenter.views.IMyProfileView;
import com.qunar.im.ui.presenter.views.IPersonalInfoView;
import com.qunar.im.ui.presenter.views.IServiceStateView;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.ui.util.ProfileUtils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.AppiumTestActivity;
import com.qunar.im.ui.activity.TabMainActivity;
import com.qunar.im.utils.QtalkStringUtils;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 新版UI 我的
 * Created by lihaibin.li on 2017/12/20.
 */

public class MineFragment extends BaseFragment implements IPersonalInfoView, IMyProfileView, IServiceStateView,
        View.OnClickListener {
    TextView user_id, user_info, user_desc, dept_name, tv_bug_report, cur_version;

    LinearLayout account_info;
    LinearLayout ll_dept;
    SimpleDraweeView personalphoto;
    LinearLayout tv_all_hongbao;
    LinearLayout tv_hongbao_balance;
    LinearLayout feedback_layout;
    LinearLayout setting_layout;
    LinearLayout clock_in_layout;
    LinearLayout totp_in_layout;
    LinearLayout appium_layout;
    RelativeLayout personal_info_layout;
    IPersonalInfoPresenter presenter;
    IEditMyProfilePresenter editMyProfilePresenter;
    IServiceStatePresenter serviceStatePresenter;
    TabMainActivity mainActivity;
    LinearLayout ll_service_state;
    LinearLayout ll_account_switch;
    View line4;
    TextView service_state;
    private static String StateNotSet = "0";
    private static String StateWorkOff = "1";
    private static String StateWorkOn = "4";
    private String tvServiceState;
    private List<SeatStatusResult.SeatStatus> seatStatuses;
    SeatStatusResult.SeatStatus status;

    HandleMainEvent handleMainEvent = new HandleMainEvent();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (TabMainActivity) activity;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        presenter = PersonalInfoFactory.getPersonalPresenter();
        editMyProfilePresenter = new EditMyProfilePresenter();
        serviceStatePresenter = new ServiceStatePresenter();
        presenter.setPersonalInfoView(this);
        editMyProfilePresenter.setPersonalInfoView(this);
        serviceStatePresenter.setServiceStateView(this);
        EventBus.getDefault().register(handleMainEvent);

    }

    @Override
    public void onResume() {
        super.onResume();
//        if (CurrentPreference.getInstance().getIsShowVersion()){
//            setting_show_version.setVisibility(View.VISIBLE);
//        }else {
//            setting_show_version.setVisibility(View.INVISIBLE);
//        }
    }

    @Override
    public void onDestroy() {
        mainActivity = null;
        EventBus.getDefault().unregister(handleMainEvent);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.atom_ui_fragment_mine, container, false);
//        ll_account_switch = (LinearLayout) view.findViewById(R.id.ll_account_switch);
//        ll_account_switch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mainActivity.switchAccount();
//            }
//        });
//        ll_account_switch.setVisibility(CommonConfig.isQtalk ? View.VISIBLE : View.GONE);
//        gravatar_bg = (LinearLayout) view.findViewById(R.id.gravatar_bg);
        personalphoto = (com.facebook.drawee.view.SimpleDraweeView) view.findViewById(R.id.my_gravantar);
        user_info = (TextView) view.findViewById(R.id.user_info);
        user_desc = (TextView) view.findViewById(R.id.user_desc);
//        user_id = (TextView) view.findViewById(R.id.user_id);
//        setting_show_version = (TextView) view.findViewById(R.id.setting_show_version);
//        ll_dept = (LinearLayout) view.findViewById(R.id.ll_dept);
//        dept_name = (TextView) view.findViewById(R.id.dept_name);
//        ll_hongbao_content = (LinearLayout) view.findViewById(R.id.ll_hongbao_content);
        tv_all_hongbao = (LinearLayout) view.findViewById(R.id.tv_all_hongbao);
        tv_hongbao_balance = (LinearLayout) view.findViewById(R.id.tv_hongbao_balance);
//        settings = (TextView) view.findViewById(R.id.settings);
//        tv_bug_report = (TextView) view.findViewById(R.id.tv_bug_report);
//        cur_version = (TextView) view.findViewById(R.id.cur_version);
//        cur_version.setOnClickListener(new View.OnClickListener() {//点击超过5次执行 加载补丁 测试热发
//            @Override
//            public void onClick(View view) {
//                clickCount++;
//                if (clickCount > 5) {
//                    Tinker.with(getActivity().getApplicationContext()).getPatchListener().onPatchReceived(Environment.getExternalStorageDirectory().getAbsolutePath() + "/patch_signed_7zip.apk");
//                    clickCount = 0;
//                }
//            }
//        });
//        ll_service_state = (LinearLayout) view.findViewById(R.id.ll_service_state);
//        service_state = (TextView) view.findViewById(R.id.service_state);
//        line4 = view.findViewById(R.id.line4);
        personal_info_layout = (RelativeLayout) view.findViewById(R.id.personal_info_layout);
        setting_layout = (LinearLayout) view.findViewById(R.id.setting_layout);
        clock_in_layout = (LinearLayout) view.findViewById(R.id.clock_in_layout);
        totp_in_layout = (LinearLayout) view.findViewById(R.id.totp_in_layout);
        feedback_layout = (LinearLayout) view.findViewById(R.id.feedback_layout);
        account_info = (LinearLayout) view.findViewById(R.id.account_info);
        appium_layout = (LinearLayout) view.findViewById(R.id.appium_layout);
        initViews();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showNick();
        showMood();
    }

    void initViews() {
//        if (!CommonConfig.isQtalk) {
//            ll_dept.setVisibility(View.GONE);
//        }
//        tv_bug_report.setOnClickListener(this);
//        settings.setOnClickListener(this);
//        String vn = QunarIMApp.getQunarIMApp().getVersionName();
//        cur_version.setText("当前版本:" + vn +
//                " (" + QunarIMApp.getQunarIMApp().getVersion() + ")" + "-" + DataUtils.getInstance(getActivity()).getPreferences(PullPatchService.PATCH_TIMESTAMP + "_" + vn, "0"));
        personalphoto.setOnClickListener(this);
//        resetSize();
//        resetLayout();
        initHongbaoView();
        personal_info_layout.setOnClickListener(this);
        setting_layout.setOnClickListener(this);
        clock_in_layout.setOnClickListener(this);
        totp_in_layout.setOnClickListener(this);
        feedback_layout.setOnClickListener(this);
        account_info.setOnClickListener(this);
        account_info.setVisibility(CommonConfig.isQtalk ? View.VISIBLE : View.GONE);
//        ll_service_state.setOnClickListener(this);
        clock_in_layout.setVisibility(CommonConfig.isQtalk ? View.VISIBLE : View.GONE);
        totp_in_layout.setVisibility(CommonConfig.isQtalk ? View.VISIBLE : View.GONE);
        appium_layout.setVisibility(CommonConfig.isDebug ? View.VISIBLE : View.GONE);
        appium_layout.setOnClickListener(this);
    }

    public void initHongbaoView() {
//        if (CommonConfig.isQtalk)
//            ll_hongbao_content.setVisibility(View.GONE);
        tv_all_hongbao.setOnClickListener(this);
        tv_hongbao_balance.setOnClickListener(this);
    }

    public void showNick() {
        presenter.showPersonalInfo();
    }

    public void showGravantar() {
//        presenter.loadGravatar(false);
        presenter.showLargeGravatar();
    }

    public void showUserId() {
        user_id.setText(getJid());
    }

    public void showMood() {
        editMyProfilePresenter.loadMood();
    }

    @Override
    public void setNickName(final String nick) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                user_info.setText(nick);
            }
        });
    }

    @Override
    public void setDeptName(final String deptName) {
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                dept_name.setText(deptName);
//            }
//        });
    }

    @Override
    public void setJid(final String userId) {
    }

    @Override
    public SimpleDraweeView getImagetView() {
        return personalphoto;
    }

    @Override
    public String getJid() {
        return QtalkStringUtils.userId2Jid(com.qunar.im.protobuf.common.CurrentPreference.getInstance().getPreferenceUserId());
    }

    @Override
    public String getMood() {
        return "";
    }

    @Override
    public void setMood(final String mood) {
        if (TextUtils.isEmpty(mood)) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                user_desc.setText(Html.fromHtml(mood));
            }
        });
    }


    @Override
    public void setUpdateResult(boolean result) {

    }

    @Override
    public void setLargeGravatarInfo(final String url, String thumbPath) {
        ProfileUtils.displayGravatarByImageSrc(getActivity(), url, personalphoto, 0, 0);
    }

    @Override
    public Context getContext() {
        return super.getContext();
    }

    Dialog servceDialog;

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_all_hongbao) {
            mainActivity.showHongBao();
        } else if (i == R.id.tv_hongbao_balance) {
            mainActivity.showHongBaoBalance();
        } else if (i == R.id.setting_layout) {
            mainActivity.showSetting();
        } else if (i == R.id.clock_in_layout) {
            mainActivity.showClockIn();
        } else if (i == R.id.totp_in_layout) {
            mainActivity.showTOTP();
        } else if (i == R.id.feedback_layout) {
            mainActivity.showFeedBack();
        } else if (i == R.id.ll_service_state) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            servceDialog = builder.create();
            servceDialog.show();
            View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.atom_ui_dialog_seat_status, null);
            final RadioButton rb0 = (RadioButton) contentView.findViewById(R.id.radio0);
            final RadioButton rb1 = (RadioButton) contentView.findViewById(R.id.radio1);
            final RadioButton rb4 = (RadioButton) contentView.findViewById(R.id.radio4);
            rb0.setOnClickListener(serviceClickListener);
            rb1.setOnClickListener(serviceClickListener);
            rb4.setOnClickListener(serviceClickListener);
            RadioGroup group = (RadioGroup) contentView.findViewById(R.id.seatGroup);
            group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                    status = seatStatuses.get(i);
                    if (StateNotSet.equals(status.st)) {
                        rb0.setChecked(true);
                    } else if (StateWorkOff.equals(status.st)) {
                        rb1.setChecked(true);
                    } else if (StateWorkOn.equals(status.st)) {
                        rb4.setChecked(true);
                    }
                }
            });
            if (seatStatuses != null) {
                int count = seatStatuses == null ? 0 : seatStatuses.size();
                for (int k = 0; k < count; k++) {
                    SeatStatusResult.SeatStatus status = seatStatuses.get(k);
                    RadioButton radioButton = new RadioButton(getActivity());
                    radioButton.setId(k);
                    radioButton.setPadding(10, 10, 10, 10);
                    radioButton.setBackgroundResource(R.drawable.atom_ui_gray_white_selector);
                    radioButton.setText(status.sname);
                    radioButton.setButtonDrawable(R.color.translate);
                    radioButton.setChecked(k == 0);
                    group.addView(radioButton);
                }
            }
            servceDialog.setContentView(contentView);
            servceDialog.setCanceledOnTouchOutside(true);
        } else if (i == R.id.personal_info_layout || i == R.id.my_gravantar) {
            mainActivity.showMyInfo();
        } else if (i == R.id.account_info) {
            //展示账号信息
            mainActivity.showAccountInfo();
        }else if(i == R.id.appium_layout){
            startActivity(new Intent(getActivity(), AppiumTestActivity.class));
        }
    }

    View.OnClickListener serviceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (status == null) return;
            if (servceDialog != null && servceDialog.isShowing())
                servceDialog.dismiss();

            if (view.getId() == R.id.radio0) {
                tvServiceState = StateNotSet;
            } else if (view.getId() == R.id.radio1) {
                tvServiceState = StateWorkOff;
            } else if (view.getId() == R.id.radio4) {
                tvServiceState = StateWorkOn;
            }
            serviceStatePresenter.setServiceState();
        }
    };


    @Override
    public String getUerId() {
        return com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid();
    }

    public String Code2ServiceState(String code) {
        String state = getString(R.string.atom_ui_service_state_standard);
        if (code != null && !TextUtils.isEmpty(code)) {
            if (StateWorkOff.equals(code)) {
                state = getString(R.string.atom_ui_service_state_offline);
            } else if (StateWorkOn.equals(code)) {
                state = getString(R.string.atom_ui_service_state_super);
            }
        }
        return state;
    }

    @Override
    public String getServiceState() {
        return tvServiceState;
    }

    @Override
    public String getSeatSid() {
        return status == null ? "" : status.sid;
    }

    @Override
    public void setServiceState(final String s) {
        serviceStatePresenter.getServiceState();
    }

    @Override
    public void getSeatStates(List<SeatStatusResult.SeatStatus> seatStatuses) {
        this.seatStatuses = seatStatuses;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setServiceStatusText();
            }
        });
    }

    @Override
    public String getMarkup() {
        return null;
    }

    @Override
    public void setMarkup(boolean isScuess) {

    }

    private void setServiceStatusText() {
        StringBuilder sb = new StringBuilder();
        for (SeatStatusResult.SeatStatus status : seatStatuses) {
            sb.append(status.sname + "->" + Code2ServiceState(status.st) + "\n");
        }
        if (sb.length() > 0) {
            String s = sb.substring(0, sb.lastIndexOf("\n"));
            service_state.setText(s);
        }
    }

    /**
     * 显示客服状态
     */
    public void showMerchant() {
        if (com.qunar.im.protobuf.common.CurrentPreference.getInstance().isMerchants()) {
            if (ll_service_state.getVisibility() != View.VISIBLE) {
                line4.setVisibility(View.VISIBLE);
                ll_service_state.setVisibility(View.VISIBLE);
                serviceStatePresenter.getServiceState();
            }
        } else {
            line4.setVisibility(View.GONE);
            ll_service_state.setVisibility(View.GONE);
        }
    }

    public class HandleMainEvent {

        //更新头像
        public void onEvent(final EventBusEvent.GravantarChanged event) {
            ConnectionUtil.getInstance().getUserCard(CurrentPreference.getInstance().getPreferenceUserId(), new IMLogicManager.NickCallBack() {
                @Override
                public void onNickCallBack(Nick nick) {
                    if (nick != null) {
                        ProfileUtils.displayGravatarByImageSrc(getActivity(), nick.getHeaderSrc(), personalphoto, 0, 0);
                    }

                }
            }, true, false);
        }

        public void onEventMainThread(EventBusEvent.ChangeMood mood) {
            user_desc.setText(mood.mood);
        }

    }
}
