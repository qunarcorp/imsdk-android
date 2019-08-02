package com.qunar.im.ui.activity;

import android.os.Bundle;
import android.widget.ListView;

import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.AppiumTestAdapter;
import com.qunar.im.ui.entity.AppiumCase;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * appium自动化接口测试
 * Created by lihaibin.li on 2018/2/2.
 */

public class AppiumTestActivity extends SwipeBackActivity {

    private ListView appiumListView;
    private AppiumTestAdapter adapter;
    private List<AppiumCase> cases = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_appium_test);


        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setActionBarTitle("appium");

        createCase();
        appiumListView = (ListView) findViewById(R.id.appium_list);
        adapter = new AppiumTestAdapter(this,cases);
        appiumListView.setAdapter(adapter);
    }

    private void createCase(){
        AppiumCase case1 = new AppiumCase();
        case1.caseName = "拉取离线消息";
        cases.add(case1);
    }
}
