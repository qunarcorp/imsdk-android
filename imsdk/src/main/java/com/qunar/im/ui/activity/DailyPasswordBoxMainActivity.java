package com.qunar.im.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.qunar.im.base.common.DailyMindConstants;
import com.qunar.im.base.jsonbean.DailyMindMain;
import com.qunar.im.ui.presenter.IDailyMindPresenter;
import com.qunar.im.ui.presenter.impl.DailyMindPresenter;
import com.qunar.im.ui.presenter.views.IDailyMindMainView;
import com.qunar.im.ui.R;
import com.qunar.im.ui.fragment.BaseFragment;
import com.qunar.im.ui.fragment.DailyCreatePasswordBoxFragment;
import com.qunar.im.ui.fragment.DailyPasswordBoxFragment;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 密码箱主密码
 * Created by lihaibin.li on 2017/8/22.
 */

public class DailyPasswordBoxMainActivity extends SwipeBackActivity implements IDailyMindMainView {
    private final String TAG = DailyPasswordBoxMainActivity.class.getSimpleName();

    FrameLayout root_container;
    DailyCreatePasswordBoxFragment daliyCreatePasswordBoxFragment;
    DailyPasswordBoxFragment daliyPasswordBoxFragment;

    public int offset = 0;
    public int number = 10;
    IDailyMindPresenter passwordBoxPresenter;

    public QtNewActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_daily_mind_password_box);

        bindViews();
        initViews();

        passwordBoxPresenter = new DailyMindPresenter();
        passwordBoxPresenter.setView(this);

        getCloundChatPasswordBox();
        getCloudMain();

    }

    private void bindViews() {
        root_container = (FrameLayout) findViewById(R.id.root_container);
    }

    private void initViews() {
        actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setActionBarTitle(R.string.atom_ui_explore_title_passwords);
        setActionBarRightText(R.string.atom_ui_btn_note_new);
        setActionBarRightTextClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(daliyCreatePasswordBoxFragment);
            }
        });

        daliyCreatePasswordBoxFragment = new DailyCreatePasswordBoxFragment();
        daliyPasswordBoxFragment = new DailyPasswordBoxFragment();
        switchFragment(daliyPasswordBoxFragment);

    }

    private void refeshViews() {
        List<DailyMindMain> dailyMindMains = passwordBoxPresenter.getDailyMainFromDB(DailyMindConstants.PASSOWRD, offset, number);
        daliyPasswordBoxFragment.setDailyMindMains(dailyMindMains);

        DailyMindMain dailyMindMain = passwordBoxPresenter.getDailyMainByTitleFromDB();
        if (dailyMindMain != null)
            daliyPasswordBoxFragment.addPasswordBoxMain(dailyMindMain);
    }

    private void switchFragment(BaseFragment fragment) {
        if (fragment instanceof DailyCreatePasswordBoxFragment)
            getSupportFragmentManager().beginTransaction().replace(R.id.root_container, fragment).addToBackStack(null)
                    .commit();
        else getSupportFragmentManager().beginTransaction().replace(R.id.root_container, fragment)
                .commit();
    }

    private void getCloudMain() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("version", "0");
        params.put("type", String.valueOf(DailyMindConstants.PASSOWRD));
        passwordBoxPresenter.operateDailyMindFromHttp(DailyMindConstants.GET_CLOUD_MAIN, params);
    }

    private void getCloundChatPasswordBox() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("version", "0");
        params.put("type", String.valueOf(DailyMindConstants.CHATPASSWORD));
        passwordBoxPresenter.operateDailyMindFromHttp(DailyMindConstants.GET_CLOUD_MAIN, params);
    }

    @Override
    public void setCloudMain() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refeshViews();
            }
        });
    }

    @Override
    public void addDailyMain(DailyMindMain dailyMindMain) {
        daliyPasswordBoxFragment.addPasswordBoxMain(dailyMindMain);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onBackPressed();
            }
        });
    }

    @Override
    public void showErrMsg(final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DailyPasswordBoxMainActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public IDailyMindPresenter getPasswordBoxPresenter() {
        return passwordBoxPresenter;
    }

    @Override
    public void onBackPressed() {
        if (root_container.getChildCount() == 0) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

}
