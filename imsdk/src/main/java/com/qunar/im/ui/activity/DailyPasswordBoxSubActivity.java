package com.qunar.im.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.qunar.im.base.common.DailyMindConstants;
import com.qunar.im.base.jsonbean.DailyMindMain;
import com.qunar.im.base.jsonbean.DailyMindSub;
import com.qunar.im.ui.presenter.IDailyMindPresenter;
import com.qunar.im.ui.presenter.impl.DailyMindPresenter;
import com.qunar.im.ui.presenter.views.IDailyMindSubView;
import com.qunar.im.ui.R;
import com.qunar.im.ui.fragment.BaseFragment;
import com.qunar.im.ui.fragment.DailyCreatePasswordBoxSubFragment;
import com.qunar.im.ui.fragment.DailyPasswordBoxSubFragment;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 密码箱子密码
 * Created by lihaibin.li on 2017/8/23.
 */

public class DailyPasswordBoxSubActivity extends SwipeBackActivity implements IDailyMindSubView {

    public int offset;

    public int number = 10;

    public QtNewActionBar actionBar;

    private DailyMindMain dailyMindMain;
    private String main_password;

    private boolean isFromNet;

    FrameLayout root_container;
    DailyPasswordBoxSubFragment dailyPasswordBoxSubFragment;
    DailyCreatePasswordBoxSubFragment dailyCreatePasswordBoxSubFragment;

    IDailyMindPresenter passwordPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_daily_mind_password_box);

        bindViews();
        initViews();
        getCloudSub();
    }

    private void bindViews() {
        root_container = (FrameLayout) findViewById(R.id.root_container);
    }

    private void initViews() {
        actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);

        Intent intent = getIntent();
        dailyMindMain = (DailyMindMain) intent.getSerializableExtra("data");
        main_password = intent.getStringExtra("main_password");
        passwordPresenter = new DailyMindPresenter();
        passwordPresenter.setView(this);

        dailyPasswordBoxSubFragment = new DailyPasswordBoxSubFragment();
        dailyCreatePasswordBoxSubFragment = new DailyCreatePasswordBoxSubFragment();

        setActionBarTitle(dailyMindMain.title);
        setActionBarRightText(R.string.atom_ui_btn_note_new);
        setActionBarRightTextClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(dailyCreatePasswordBoxSubFragment);
            }
        });
        switchFragment(dailyPasswordBoxSubFragment);
    }

    private void refeshViews() {
        List<DailyMindSub> dailyMindSubs = passwordPresenter.getDailySubFromDB(offset, number, dailyMindMain.qid);
        dailyPasswordBoxSubFragment.setDailyMindSubs(dailyMindSubs);

    }

    private void switchFragment(BaseFragment fragment) {
        if (fragment instanceof DailyCreatePasswordBoxSubFragment) {
            getSupportFragmentManager().beginTransaction().replace(R.id.root_container, fragment).addToBackStack(null)
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.root_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (root_container.getChildCount() == 0) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    private void getCloudSub() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("qid", dailyMindMain.qid + "");
        params.put("version", "0");
        params.put("type", String.valueOf(DailyMindConstants.PASSOWRD));
        passwordPresenter.operateDailyMindFromHttp(DailyMindConstants.GET_CLOUD_SUB, params);
    }

    @Override
    public void setCloudSub() {
        isFromNet = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refeshViews();
            }
        });
    }

    @Override
    public void addDailySub(final DailyMindSub dailyMindSub) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dailyPasswordBoxSubFragment.addPasswordBoxSub(dailyMindSub);
                onBackPressed();
            }
        });
    }

    public IDailyMindPresenter getPasswordPresenter() {
        return passwordPresenter;
    }

    public DailyMindMain getDailyMindMain() {
        return dailyMindMain;
    }

    public String getMain_password() {
        return main_password;
    }

    @Override
    public void showErrMsg(final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DailyPasswordBoxSubActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
