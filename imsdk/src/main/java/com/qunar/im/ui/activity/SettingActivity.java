package com.qunar.im.ui.activity;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.qunar.im.permission.PermissionCallback;
import com.qunar.im.permission.PermissionDispatcher;
import com.qunar.im.ui.R;
import com.qunar.im.ui.fragment.CommonSettingFragment;
import com.qunar.im.ui.fragment.MainSettingFragment;
import com.qunar.im.ui.fragment.PrivacySettingFragment;
import com.qunar.im.ui.util.UpdateManager;
import com.qunar.im.ui.view.QtNewActionBar;


/**
 * Created by xinbo.wang on 2015/4/2.
 */
public class SettingActivity extends IMBaseActivity
        implements MainSettingFragment.CallBack, PermissionCallback {
    private static final int CHECK_UPDATE = PermissionDispatcher.getRequestCode();
    FrameLayout root_container;

    private MainSettingFragment mainFragment;
    private CommonSettingFragment commonFragment;

    private PrivacySettingFragment privacySettingFragment;

    @Override
    protected void onCreate(Bundle savedBundle)
    {
        super.onCreate(savedBundle);
        setContentView(R.layout.atom_ui_activity_setting);
        bindViews();
        initViews();
    }

    private void bindViews()
    {
        root_container = (FrameLayout) findViewById(R.id.root_container);
    }

    void initViews() {
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setActionBarTitle(R.string.atom_ui_setting_title);
        mainFragment = new MainSettingFragment();
        commonFragment = new CommonSettingFragment();
        privacySettingFragment = new PrivacySettingFragment();
        mainFragment.setCallBack(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.root_container, mainFragment)
               .commit();
    }

    @Override
    public void commonHasClicked() {
        getSupportFragmentManager().beginTransaction().replace(R.id.root_container, commonFragment)
                .addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        if (root_container.getChildCount() == 0) {
            finish();
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public void privacySettingClicked() {
        getSupportFragmentManager().beginTransaction().replace(R.id.root_container, privacySettingFragment)
                .addToBackStack(null).commit();
    }

    @Override
    public void responsePermission(int requestCode, boolean granted) {
        if (!granted) return;
        if (requestCode == CHECK_UPDATE) {
            UpdateManager.getUpdateManager().checkAppUpdate(this, true,true);
        }
    }

    public void requestUpdate()
    {
        PermissionDispatcher.
                requestPermissionWithCheck(this, new int[]{PermissionDispatcher.REQUEST_WRITE_EXTERNAL_STORAGE,
                                PermissionDispatcher.REQUEST_READ_EXTERNAL_STORAGE}, this,
                        CHECK_UPDATE);
    }
}
