package com.qunar.im.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.SeekBar;
import android.widget.TextView;

import com.qunar.im.ui.presenter.IProfilePresenter;
import com.qunar.im.ui.presenter.impl.ProfilePresenter;
import com.qunar.im.ui.presenter.views.IChangeFontSizeView;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.ui.R;
import com.qunar.im.ui.util.ResourceUtils;
import com.qunar.im.ui.view.QtNewActionBar;

public class FontSizeActivity extends IMBaseActivity implements SeekBar.OnSeekBarChangeListener, IChangeFontSizeView {
    private TextView tvMessage1;
    private TextView tvMessage2;
    private TextView tvMessage3;
    private SeekBar sbChangeFontSize;
    private IProfilePresenter profilePresenter;
    private int selectMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_font_size);
        initView();
        profilePresenter = new ProfilePresenter();
        CurrentPreference.ProFile proFile = CurrentPreference.getInstance().getProFile();
        int fontSizeMode = proFile.getFontSizeMode();
        float fontSize=this.getResources().getDimensionPixelSize(R.dimen.atom_ui_text_size_medium);
        switch (fontSizeMode) {
            case 1:
                sbChangeFontSize.setProgress(0);
                fontSize-=ResourceUtils.getFontSizeIntervalPX(this);
                break;
            case 2:
                sbChangeFontSize.setProgress(50);
                break;
            case 3:
                sbChangeFontSize.setProgress(100);
                fontSize+=ResourceUtils.getFontSizeIntervalPX(this);
                break;
        }
        tvMessage1.setTextSize(TypedValue.COMPLEX_UNIT_PX,fontSize);
        tvMessage2.setTextSize(TypedValue.COMPLEX_UNIT_PX,fontSize);
        tvMessage3.setTextSize(TypedValue.COMPLEX_UNIT_PX,fontSize);
    }

    private void initView() {
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setActionBarTitle(R.string.atom_ui_title_fontsize);
        tvMessage1 = (TextView) findViewById(R.id.tv_message1);
        tvMessage2 = (TextView) findViewById(R.id.tv_message2);
        tvMessage3 = (TextView) findViewById(R.id.tv_message3);
        sbChangeFontSize = (SeekBar) findViewById(R.id.sb_change_font_size);
        sbChangeFontSize.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        profilePresenter.setProfileView(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int process = seekBar.getProgress();
        if (process <= 25) {
            seekBar.setProgress(0);
            selectMode = 1;
        } else if (process > 25 && process <= 75) {
            seekBar.setProgress(50);
            selectMode = 2;
        } else {
            seekBar.setProgress(100);
            selectMode = 3;
        }
        int fontSize=this.getResources().getDimensionPixelSize(R.dimen.atom_ui_text_size_medium);
        switch (selectMode) {
            case 1:
                sbChangeFontSize.setProgress(0);
                fontSize-= ResourceUtils.getFontSizeIntervalPX(this);
                break;
            case 2:
                sbChangeFontSize.setProgress(50);
                break;
            case 3:
                sbChangeFontSize.setProgress(100);
                fontSize+=ResourceUtils.getFontSizeIntervalPX(this);
                break;
        }
        tvMessage1.setTextSize(TypedValue.COMPLEX_UNIT_PX,fontSize);
        tvMessage2.setTextSize(TypedValue.COMPLEX_UNIT_PX,fontSize);
        tvMessage3.setTextSize(TypedValue.COMPLEX_UNIT_PX,fontSize);
        if (CurrentPreference.getInstance().getFontSizeMode() != selectMode) {
            profilePresenter.changeFontSize();
        }
    }

    @Override
    public boolean getMsgSoundState() {
        return false;
    }

    @Override
    public boolean getMsgShockState() {
        return false;
    }

    @Override
    public boolean getPushMsgState() {
        return false;
    }

    @Override
    public boolean getPushShowContent() {
        return false;
    }

    @Override
    public boolean getOfflinePush() {
        return false;
    }

    @Override
    public boolean getShakeEvent() {
        return false;
    }

    @Override
    public boolean getLandscape() {
        return false;
    }

    @Override
    public int getFontSizeMode() {
        return selectMode;
    }

    @Override
    public Context getContext() {
        return this;
    }
}