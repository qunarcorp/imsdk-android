package com.qunar.im.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.ui.presenter.IProfilePresenter;
import com.qunar.im.ui.presenter.impl.ProfilePresenter;
import com.qunar.im.ui.presenter.views.IProfileView;
import com.qunar.im.base.util.MediaUtils;
import com.qunar.im.common.CurrentPreference;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.services.PushServiceUtils;

/**
 * Created by zhaokai on 15-9-18.
 */
public class CommonSettingFragment extends BaseFragment implements IProfileView,CompoundButton.OnCheckedChangeListener {
    IProfilePresenter profilePresenter;
    CheckBox chk_new_msg, chk_push_msg, shock_new_msg, shake_event, cbx_landscape,cbx_gif, show_push_content, offline_push;

    private RelativeLayout offline_push_layout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profilePresenter = new ProfilePresenter();
        profilePresenter.setProfileView(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.atom_ui_fragment_common_setting, container, false);
        chk_new_msg = (CheckBox) view.findViewById(R.id.chk_new_msg);
        shock_new_msg = (CheckBox) view.findViewById(R.id.shock_new_msg);
        chk_push_msg = (CheckBox) view.findViewById(R.id.chk_push_msg);
        shake_event = (CheckBox) view.findViewById(R.id.shake_event);
        cbx_landscape = (CheckBox) view.findViewById(R.id.cbx_landscape);
        cbx_gif = (CheckBox) view.findViewById(R.id.cbx_support_gif);
        show_push_content = (CheckBox) view.findViewById(R.id.show_push_content);
        offline_push = (CheckBox) view.findViewById(R.id.offline_push);
        offline_push_layout = (RelativeLayout) view.findViewById(R.id.offline_push_layout);
//        if(CommonConfig.isQtalk){
//            offline_push_layout.setVisibility(View.VISIBLE);
//        }else{
//            offline_push_layout.setVisibility(View.GONE);
//        }
        initView();
        return view;
    }



    void initView(){
        CurrentPreference.ProFile proFile = CurrentPreference.getInstance().getProFile();
        chk_new_msg.setChecked(proFile.isTurnOnMsgSound());
        chk_new_msg.setOnCheckedChangeListener(this);
        shock_new_msg.setChecked(proFile.isTurnOnMsgShock());
        shock_new_msg.setOnCheckedChangeListener(this);
        chk_push_msg.setChecked(proFile.isTurnOnPsuh());
        chk_push_msg.setOnCheckedChangeListener(this);
        show_push_content.setChecked(proFile.isShowContentPush());
        show_push_content.setOnCheckedChangeListener(this);
        offline_push.setChecked(proFile.isOfflinePush());
        offline_push.setOnCheckedChangeListener(this);
//        shake_event.setChecked(CurrentPreference.getInstance().isShakeEvent());
//        shake_event.setOnCheckedChangeListener(this);

//        cbx_landscape.setChecked(CurrentPreference.getInstance().isLandscape());
//        cbx_landscape.setOnCheckedChangeListener(this);
//        cbx_gif.setChecked(CurrentPreference.getInstance().isSupportGifGravantar());
//        cbx_gif.setOnCheckedChangeListener(this);
    }


    @Override
    public boolean getMsgSoundState() {
        return chk_new_msg.isChecked();
    }

    @Override
    public boolean getMsgShockState() {
        return shock_new_msg.isChecked();
    }

    @Override
    public boolean getPushMsgState() {
        return chk_push_msg.isChecked();
    }

    @Override
    public boolean getPushShowContent() {
        return show_push_content.isChecked();
    }

    @Override
    public boolean getOfflinePush() {
        return offline_push.isChecked();
    }

    @Override
    public boolean getShakeEvent() {
        return shake_event.isChecked();
    }

    @Override
    public boolean getLandscape() {
        return cbx_landscape.isChecked();
    }

    @Override
    public Context getContext() {
        return getActivity().getApplicationContext();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int i = buttonView.getId();
        if (i == R.id.chk_new_msg) {
            profilePresenter.changeMsgSoundState();
            if (isChecked) {
                MediaUtils.loadNewMsgSound(QunarIMApp.getContext(), R.raw.atom_ui_new_msg);
            } else {
                MediaUtils.unLoadNewMsgSound();
            }

        } else if (i == R.id.shock_new_msg) {
            profilePresenter.changeMsgShockState();

        } else if (i == R.id.chk_push_msg) {
            profilePresenter.changePushState();
            if (isChecked) {
                PushServiceUtils.startAMDService(getActivity());
            } else {
                PushServiceUtils.stopAMDService(getActivity());
            }
            ((IMBaseActivity) getActivity()).presenter.checkUnique();

        } else if (i == R.id.shake_event) {
            profilePresenter.changeShakeEvent();

        } else if (i == R.id.cbx_landscape) {
            profilePresenter.changeLandscapeState();

        } else if (i == R.id.cbx_support_gif) {
//            if (isChecked) {
//                CurrentPreference.getInstance().enableGifGravantar();
//            } else {
//                CurrentPreference.getInstance().disableGifGravantar();
//            }

        }else if (i == R.id.show_push_content){
            profilePresenter.changePushShowContent();
        }else if (i == R.id.offline_push){
            profilePresenter.changeOfflinePush();
        }
    }
}
