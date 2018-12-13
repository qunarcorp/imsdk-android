package com.qunar.im.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.jsonbean.SeatStatusResult;
import com.qunar.im.base.presenter.IEditMyProfilePresenter;
import com.qunar.im.base.presenter.IPersonalInfoPresenter;
import com.qunar.im.base.presenter.IServiceStatePresenter;
import com.qunar.im.base.presenter.factory.PersonalInfoFactory;
import com.qunar.im.base.presenter.impl.EditMyProfilePresenter;
import com.qunar.im.base.presenter.impl.ServiceStatePresenter;
import com.qunar.im.base.presenter.views.IMyProfileView;
import com.qunar.im.base.presenter.views.IPersonalInfoView;
import com.qunar.im.base.presenter.views.IServiceStateView;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.base.util.ProfileUtils;
import com.qunar.im.base.util.Utils;
import com.qunar.im.base.util.graphics.BitmapHelper;
import com.qunar.im.base.util.graphics.ImageUtils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.MainActivity;
import com.qunar.im.utils.QtalkStringUtils;

import java.io.File;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by zhaokai on 15-5-12.
 */
public class PersonalInfoFragment extends BaseFragment implements IPersonalInfoView, IMyProfileView, IServiceStateView,
        View.OnClickListener {
    TextView user_id, user_info, user_desc, dept_name, settings, tv_bug_report, cur_version, setting_show_version;

    LinearLayout ll_dept;
    SimpleDraweeView personalphoto;
    LinearLayout gravatar_bg;
    LinearLayout ll_hongbao_content;
    TextView tv_all_hongbao;
    TextView tv_hongbao_balance;
    IPersonalInfoPresenter presenter;
    IEditMyProfilePresenter editMyProfilePresenter;
    IServiceStatePresenter serviceStatePresenter;
    MainActivity mainActivity;
    LinearLayout ll_service_state;
    LinearLayout ll_account_switch;
    View line4;
    TextView service_state;
    boolean hasSetBg;
    private int saveSize;
    private int smallSize;
    private static String StateNotSet = "0";
    private static String StateWorkOff = "1";
    private static String StateWorkOn = "4";
    private String tvServiceState;
    private List<SeatStatusResult.SeatStatus> seatStatuses;
    SeatStatusResult.SeatStatus status;
    private int clickCount;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
        smallSize = Utils.dipToPixels(activity, 38);
        saveSize = Utils.dipToPixels(activity, 78);
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
        EventBus.getDefault().register(this);
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
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.atom_ui_fragment_personal, container, false);
        ll_account_switch = (LinearLayout) view.findViewById(R.id.ll_account_switch);
        ll_account_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.switchAccount();
            }
        });
        ll_account_switch.setVisibility(CommonConfig.isQtalk ? View.VISIBLE : View.GONE);
        gravatar_bg = (LinearLayout) view.findViewById(R.id.gravatar_bg);
        personalphoto = (com.facebook.drawee.view.SimpleDraweeView) view.findViewById(R.id.my_gravantar);
        user_info = (TextView) view.findViewById(R.id.user_info);
        user_desc = (TextView) view.findViewById(R.id.user_desc);
        user_id = (TextView) view.findViewById(R.id.user_id);
        setting_show_version = (TextView) view.findViewById(R.id.setting_show_version);
        ll_dept = (LinearLayout) view.findViewById(R.id.ll_dept);
        dept_name = (TextView) view.findViewById(R.id.dept_name);
        ll_hongbao_content = (LinearLayout) view.findViewById(R.id.ll_hongbao_content);
        tv_all_hongbao = (TextView) view.findViewById(R.id.tv_all_hongbao);
        tv_hongbao_balance = (TextView) view.findViewById(R.id.tv_hongbao_balance);
        settings = (TextView) view.findViewById(R.id.settings);
        tv_bug_report = (TextView) view.findViewById(R.id.tv_bug_report);
        cur_version = (TextView) view.findViewById(R.id.cur_version);
        ll_service_state = (LinearLayout) view.findViewById(R.id.ll_service_state);
        service_state = (TextView) view.findViewById(R.id.service_state);
        line4 = view.findViewById(R.id.line4);
        initViews();
        return view;
    }


    void initViews() {
        if (!CommonConfig.isQtalk) {
            ll_dept.setVisibility(View.GONE);
        }
        tv_bug_report.setOnClickListener(this);
        settings.setOnClickListener(this);
        String vn = QunarIMApp.getQunarIMApp().getVersionName();
        cur_version.setText("当前版本:" + vn +
                " (" + QunarIMApp.getQunarIMApp().getVersion() + ")" + "-" + DataUtils.getInstance(getActivity()).getPreferences(Constants.Preferences.PATCH_TIMESTAMP + "_" + vn, "0"));
        personalphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.showMyInfo();
            }
        });
        resetSize();
        resetLayout();
        initHongbaoView();

        ll_service_state.setOnClickListener(this);
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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dept_name.setText(deptName);
            }
        });
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
                user_desc.setText(mood);
            }
        });
    }


    @Override
    public void setUpdateResult(boolean result) {

    }

    @Override
    public void setLargeGravatarInfo(final String url, String thumbPath) {
        ProfileUtils.displayGravatarByImageSrc(getActivity(), url, personalphoto, 0, 0);
        if (!hasSetBg) {
            BackgroundExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try{
                        File imageFile = Glide.with(getActivity())
                                .load(url)
                                .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                .get();
                        final Bitmap gravantarBg = BitmapHelper.decodeFile(imageFile.getAbsolutePath());
                        if(gravantarBg!=null&&!gravantarBg.isRecycled()){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ImageUtils.blur(gravantarBg, gravatar_bg);
                                    gravantarBg.recycle();
                                    hasSetBg = true;
                                }
                            });
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    @Override
    public Context getContext() {
        return super.getContext();
    }

    public void resetBackground() {
        hasSetBg = false;
    }

    public void changeLayout(boolean isOpend) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(gravatar_bg);
            ViewGroup.LayoutParams params = personalphoto.getLayoutParams();
            if (!isOpend) {
                params.width = saveSize;
                params.height = saveSize;
            } else {
                params.width = smallSize;
                params.height = smallSize;
            }
            personalphoto.setLayoutParams(params);
        }
    }

    private void resetSize() {
        ViewGroup.LayoutParams params = personalphoto.getLayoutParams();
        params.width = saveSize;
        params.height = saveSize;
        personalphoto.setLayoutParams(params);
    }

    public void changePosition(boolean isOpend) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(gravatar_bg);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) personalphoto.getLayoutParams();
            if (!isOpend) {
                lp.gravity = Gravity.LEFT;
            } else {
                lp.gravity = Gravity.RIGHT;
            }
            personalphoto.setLayoutParams(lp);
        }
    }

    private void resetLayout() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) personalphoto.getLayoutParams();

        lp.gravity = Gravity.LEFT;

        personalphoto.setLayoutParams(lp);
    }

    Dialog servceDialog;
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_all_hongbao) {
            mainActivity.showHongBao();

        } else if (i == R.id.tv_hongbao_balance) {
            mainActivity.showHongBaoBalance();

        } else if (i == R.id.settings) {
            mainActivity.showSetting();

        } else if (i == R.id.tv_bug_report) {
            mainActivity.showHelp();
        } else if (i == R.id.ll_service_state) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            servceDialog = builder.create();
            servceDialog.show();
            View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.atom_ui_dialog_seat_status,null);
            final RadioButton rb0 = (RadioButton) contentView.findViewById(R.id.radio0);
            final RadioButton rb1 = (RadioButton) contentView.findViewById(R.id.radio1);
            final RadioButton rb4 = (RadioButton) contentView.findViewById(R.id.radio4);
            rb0.setOnClickListener(serviceClickListener);
            rb1.setOnClickListener(serviceClickListener);
            rb4.setOnClickListener(serviceClickListener);
            RadioGroup group = (RadioGroup)contentView.findViewById(R.id.seatGroup);
            group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                    status = seatStatuses.get(i);
                    if(StateNotSet.equals(status.st)){
                        rb0.setChecked(true);
                    }else if(StateWorkOff.equals(status.st)){
                        rb1.setChecked(true);
                    }else if(StateWorkOn.equals(status.st)){
                        rb4.setChecked(true);
                    }
                }
            });
            if(seatStatuses != null){
                int count = seatStatuses == null ? 0 : seatStatuses.size();
                for(int k = 0;k<count;k++){
                    SeatStatusResult.SeatStatus status = seatStatuses.get(k);
                    RadioButton radioButton = new RadioButton(getActivity());
                    radioButton.setId(k);
                    radioButton.setPadding(10,10,10,10);
                    radioButton.setBackgroundResource(R.drawable.atom_ui_gray_white_selector);
                    radioButton.setText(status.sname);
                    radioButton.setButtonDrawable(R.color.translate);
                    radioButton.setChecked(k==0);
                    group.addView(radioButton);
                }
            }
            servceDialog.setContentView(contentView);
            servceDialog.setCanceledOnTouchOutside(true);
        }
    }

    View.OnClickListener serviceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(status == null) return;
            if(servceDialog != null && servceDialog.isShowing())
                servceDialog.dismiss();

            if(view.getId() == R.id.radio0){
                tvServiceState = StateNotSet;
            }else if(view.getId() == R.id.radio1){
                tvServiceState = StateWorkOff;
            }else if(view.getId() == R.id.radio4){
                tvServiceState = StateWorkOn;
            }
            serviceStatePresenter.setServiceState();
        }
    };

    public void onEventMainThread(EventBusEvent.ChangeMood mood) {
        user_desc.setText(mood.mood);
    }

    @Override
    public String getUerId() {
        return com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid();
    }

    public String Code2ServiceState(String code) {
        String state = "标准模式";
        if (code != null && !TextUtils.isEmpty(code)) {
            if (StateWorkOff.equals(code)) {
                state = "离线模式";
            } else if (StateWorkOn.equals(code)) {
                state = "超人模式";
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
        return status == null?"":status.sid;
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

    private void setServiceStatusText(){
        StringBuilder sb = new StringBuilder();
        for(SeatStatusResult.SeatStatus status: seatStatuses){
            sb.append(status.sname + "->" + Code2ServiceState(status.st) + "\n");
        }
        if(sb.length()>0){
            String s = sb.substring(0,sb.lastIndexOf("\n"));
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

    @Override
    public String getMarkup() {
        return null;
    }

    @Override
    public void setMarkup(boolean isScuess) {

    }
}
