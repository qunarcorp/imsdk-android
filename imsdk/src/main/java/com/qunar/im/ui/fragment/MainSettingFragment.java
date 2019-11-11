package com.qunar.im.ui.fragment;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.jsonbean.SeatStatusResult;
import com.qunar.im.ui.presenter.ILoginPresenter;
import com.qunar.im.ui.presenter.IServiceStatePresenter;
import com.qunar.im.ui.presenter.impl.LoginPresenter;
import com.qunar.im.ui.presenter.impl.ServiceStatePresenter;
import com.qunar.im.ui.presenter.views.IServiceStateView;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.AboutActivity;
import com.qunar.im.ui.activity.BugreportActivity;
import com.qunar.im.ui.activity.CalculateCacheActivity;
import com.qunar.im.ui.activity.DailyMindActivity;
import com.qunar.im.ui.activity.FontSizeActivity;
import com.qunar.im.ui.activity.PbChatActivity;
import com.qunar.im.ui.activity.QunarWebActvity;
import com.qunar.im.ui.activity.SettingActivity;
import com.qunar.im.ui.activity.WebMsgActivity;
import com.qunar.im.ui.services.PushServiceUtils;
import com.qunar.im.ui.util.UpdateManager;

import java.util.List;

/**
 * Created by zhaokai on 15-9-18.
 */
public class MainSettingFragment extends BaseFragment implements View.OnClickListener,IServiceStateView {
    TextView logout, common, rl_privacy_setting, clear_file, txt_copy_right, txt_font_size, txt_help, check_new_show, daily_mind, system_setting, collection_setting, txt_about;
    LinearLayout setting_level3_container, setting_level2_container, main_container, setting_level1_container,setting_service_mode_container;
    CallBack callBack;
    LinearLayout check_new_version;

    TextView service_state_text;

    SettingActivity activity;
    int times = 0;

    private static String StateNotSet = "0";
    private static String StateWorkOff = "1";
    private static String StateWorkOn = "4";
    private String tvServiceState;
    private List<SeatStatusResult.SeatStatus> seatStatuses;
    SeatStatusResult.SeatStatus status;

    IServiceStatePresenter serviceStatePresenter;

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (SettingActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceStatePresenter = new ServiceStatePresenter();
        serviceStatePresenter.setServiceStateView(this);
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (CurrentPreference.getInstance().getIsShowVersion()) {
//            check_new_show.setVisibility(View.VISIBLE);
//        }else {
//            check_new_show.setVisibility(View.INVISIBLE);
//        }
        showMerchant();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.atom_ui_fragmet_main_setting, container, false);
        main_container = (LinearLayout) view.findViewById(R.id.main_container);
        setting_level1_container = (LinearLayout) view.findViewById(R.id.setting_level1_container);
        clear_file = (TextView) view.findViewById(R.id.clear_file);
        common = (TextView) view.findViewById(R.id.common);
        setting_level2_container = (LinearLayout) view.findViewById(R.id.setting_level2_container);
        check_new_version = (LinearLayout) view.findViewById(R.id.check_new_version);
        check_new_show = (TextView) view.findViewById(R.id.check_new_show);
        setting_level3_container = (LinearLayout) view.findViewById(R.id.setting_level3_container);
        setting_service_mode_container = (LinearLayout) view.findViewById(R.id.setting_service_mode_container);
        rl_privacy_setting = (TextView) view.findViewById(R.id.rl_privacy_setting);
        logout = (TextView) view.findViewById(R.id.logout);
        txt_copy_right = (TextView) view.findViewById(R.id.txt_copy_right);
        txt_font_size = (TextView) view.findViewById(R.id.txt_font_size);
        txt_help = (TextView) view.findViewById(R.id.txt_help);
        daily_mind = (TextView) view.findViewById(R.id.daily_mind);
        system_setting = (TextView) view.findViewById(R.id.system_setting);
        txt_about = (TextView) view.findViewById(R.id.txt_about);
        service_state_text = (TextView) view.findViewById(R.id.service_state_text);
        //代收
        collection_setting = (TextView) view.findViewById(R.id.collection);
        if(TextUtils.isEmpty(QtalkNavicationService.getInstance().getMconfig())){
            collection_setting.setVisibility(View.GONE);
        }else{
            collection_setting.setVisibility(View.VISIBLE);
        }
        main_container.setOnClickListener(this);
        clear_file.setOnClickListener(this);
        common.setOnClickListener(this);
        check_new_version.setOnClickListener(this);
        check_new_version.setOnLongClickListener(longclick);
        rl_privacy_setting.setOnClickListener(this);
        logout.setOnClickListener(this);
        txt_copy_right.setOnClickListener(this);
        txt_font_size.setOnClickListener(this);
        txt_help.setOnClickListener(this);
        daily_mind.setOnClickListener(this);
        daily_mind.setVisibility(CommonConfig.isQtalk ? View.VISIBLE : View.GONE);
        system_setting.setOnClickListener(this);
        collection_setting.setOnClickListener(this);
        txt_about.setOnClickListener(this);
        setting_service_mode_container.setOnClickListener(this);
        return view;
    }

    void logoutEvent() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        commonDialog.setTitle(R.string.atom_ui_common_prompt);
        commonDialog.setMessage(R.string.atom_ui_tip_logout);
        commonDialog.setPositiveButton(getString(R.string.atom_ui_common_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout();
                dialog.dismiss();
            }
        });
        commonDialog.setNegativeButton(getString(R.string.atom_ui_common_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        commonDialog.show();
    }

    private void logout() {
        PushServiceUtils.stopAMDService(getContext());
        final ILoginPresenter loginPresenter = new LoginPresenter();
        loginPresenter.logout();
//        QtalkApplicationLike.finishAllActivity();

        Intent i = getContext().getPackageManager()
                .getLaunchIntentForPackage(getContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);

        Intent intent = new Intent("android.intent.action.VIEW",
                Uri.parse(CommonConfig.schema + "://qchatplatform"));
        PendingIntent restartIntent = PendingIntent.getActivity(QunarIMApp.getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager mgr = (AlarmManager) QunarIMApp.getContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);
        System.exit(2);
    }

    void checkingUpgradeEnventHandler() {
        activity.requestUpdate();
    }

    void common() {
        callBack.commonHasClicked();
    }

    void rl_privacy_setting() {
        callBack.privacySettingClicked();
    }

    void talkToSomeone() {
        times++;
        if (times > 5) {
            times = 0;
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
            View view = LayoutInflater.from(this.getActivity()).inflate(R.layout.atom_ui_secret_talk, null);
            final EditText etContext = (EditText) view.findViewById(R.id.et_content);
            etContext.setText(CommonConfig.verifyKey);
            builder.setView(view);
            builder.setPositiveButton(getString(R.string.atom_ui_common_confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!TextUtils.isEmpty(etContext.getText().toString().trim())) {
                        Intent intent = new Intent(getContext(), PbChatActivity.class);
                        intent.putExtra("jid", etContext.getText().toString().trim().toLowerCase());
                        intent.putExtra("isFromChatRoom", false);
                        MainSettingFragment.this.getActivity().startActivity(intent);
                    }
                }
            });
            builder.setNegativeButton(getString(R.string.atom_ui_common_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.create().show();
        }
    }

    void clearFileEvent() {
        Intent intent = new Intent(getContext(), CalculateCacheActivity.class);
        startActivity(intent);
    }

    void fontSize() {
    }

    Dialog servceDialog;
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.logout) {
            logoutEvent();

        } else if (i == R.id.clear_file) {
            clearFileEvent();

        } else if (i == R.id.main_container) {
            talkToSomeone();

        } else if (i == R.id.rl_privacy_setting) {
            rl_privacy_setting();

        } else if (i == R.id.common) {
            common();

        } else if (i == R.id.check_new_version) {
            checkingUpgradeEnventHandler();

        } else if (i == R.id.txt_copy_right) {
            Intent intent = new Intent(getContext(), QunarWebActvity.class);
            intent.setData(Uri.parse(com.qunar.im.base.util.Constants.THANKS_URL));
            startActivity(intent);

        } else if (i == R.id.txt_font_size) {
            //// TODO: 2016/5/20
            Intent intent = new Intent(getContext(), FontSizeActivity.class);
            startActivity(intent);
        } else if (i == R.id.txt_help) {
            Intent intent = new Intent(getContext(), BugreportActivity.class);
            startActivity(intent);
        } else if (i == R.id.daily_mind) {
            Intent intent = new Intent(getContext(), DailyMindActivity.class);
            startActivity(intent);
        } else if (i == R.id.system_setting) {
            Uri packageURI = Uri.parse("package:" + getActivity().getApplicationInfo().packageName);
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
            startActivity(intent);
            //代收
        } else if (i == R.id.collection) {
            String url = String.format(QtalkNavicationService.getInstance().getMconfig()+"?u=%s&d=%s&navBarBg=208EF2",
                    CurrentPreference.getInstance().getUserid(),QtalkNavicationService.getInstance().getXmppdomain());
            Intent intent = new Intent(getContext(), QunarWebActvity.class);
            intent.setData(Uri.parse(url));
            intent.putExtra(WebMsgActivity.IS_HIDE_BAR, true);
            startActivity(intent);
        } else if(i == R.id.txt_about){
            Intent intent = new Intent(getContext(), AboutActivity.class);
            startActivity(intent);
        } else if(i == R.id.setting_service_mode_container){
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

    View.OnLongClickListener longclick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View views = LayoutInflater.from(getActivity()).inflate(R.layout.atom_ui_secret_talk, null);
            TextView title = (TextView) views.findViewById(R.id.secret_talk_title);
            title.setText(R.string.atom_ui_title_current_version);//用来测试已当前版本号申请更新
            final EditText etContext = (EditText) views.findViewById(R.id.et_content);
            etContext.setInputType(InputType.TYPE_CLASS_NUMBER);
            etContext.setText(QunarIMApp.getQunarIMApp().getVersion() + "");
            builder.setView(views);
            builder.setPositiveButton(getString(R.string.atom_ui_common_confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!TextUtils.isEmpty(etContext.getText().toString().trim())) {
                        int vc = Integer.valueOf(etContext.getText().toString());
                        UpdateManager.getUpdateManager().checkAppUpdate(getActivity(), true, true, vc);
                    }
                }
            });
            builder.setNegativeButton(getString(R.string.atom_ui_common_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.create().show();
            return true;
        }
    };

    public interface CallBack {
        void commonHasClicked();

        void privacySettingClicked();
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
        if(seatStatuses == null){
            return;
        }
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
            service_state_text.setText(s);
        }
    }

    /**
     * 显示客服状态
     */
    public void showMerchant() {
        if (com.qunar.im.protobuf.common.CurrentPreference.getInstance().isMerchants()) {
            serviceStatePresenter.getServiceState();
        } else {
            setting_service_mode_container.setVisibility(View.GONE);
        }
    }
}