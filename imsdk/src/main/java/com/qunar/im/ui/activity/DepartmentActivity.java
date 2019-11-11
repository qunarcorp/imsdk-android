package com.qunar.im.ui.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.ui.R;
import com.qunar.im.ui.fragment.BaseFragment;
import com.qunar.im.ui.fragment.DeptFragment;
import com.qunar.im.ui.fragment.OrganizationFragment;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;

/**
 * Created by saber on 15-12-16.
 */
public class DepartmentActivity extends SwipeBackActivity {
    public static final String IS_NEW_DEPT = "isNewDept";
    private boolean isNewDept;
    @Override
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.atom_ui_activity_blank);

        Intent intent = getIntent();
        isNewDept = intent.getBooleanExtra(IS_NEW_DEPT,false);
        initViews();
    }

    void initViews()
    {
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setActionBarTitle(R.string.atom_ui_common_organization);
        // Create fragment and define some of it transitions
        final BaseFragment fragment;
        if(isNewDept){
            fragment = new OrganizationFragment();
        }
        else fragment = new DeptFragment();
        // Transition for fragment1
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.layout_blanck_content, fragment)
                .commit();
    }
}
