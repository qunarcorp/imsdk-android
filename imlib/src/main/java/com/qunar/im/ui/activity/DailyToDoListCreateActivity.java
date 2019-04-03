package com.qunar.im.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.qunar.im.base.common.DailyMindConstants;
import com.qunar.im.base.jsonbean.DailyMindMain;
import com.qunar.im.ui.presenter.IDailyMindPresenter;
import com.qunar.im.ui.presenter.impl.DailyMindPresenter;
import com.qunar.im.ui.presenter.views.IDailyMindMainView;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by lihaibin.li on 2017/10/16.
 */

public class DailyToDoListCreateActivity extends SwipeBackActivity implements IDailyMindMainView {
    public static final int REQUEST_CODE = 2017;
    private QtNewActionBar actionBar;
    private DailyMindMain dailyMindMain;

    private EditText todolist_title;
    private EditText todolist_content;

    private IDailyMindPresenter todolistPresenter;
    private boolean isUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_daily_todolist_create);

        todolistPresenter = new DailyMindPresenter();
        todolistPresenter.setView(this);

        dailyMindMain = (DailyMindMain) getIntent().getSerializableExtra("data");
        isUpdate = dailyMindMain != null;
        initView();
    }

    private void initView() {
        actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setActionBarTitle(!isUpdate? R.string.atom_ui_todolist_title_create :R.string.atom_ui_todolist_title_update);
        setActionBarRightText(R.string.atom_ui_common_save);
        setActionBarRightTextClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTodoList();
            }
        });

        todolist_title = (EditText) findViewById(R.id.todolist_title);
        todolist_content = (EditText) findViewById(R.id.todolist_content);
        if (dailyMindMain != null) {
            todolist_title.setText(dailyMindMain.title);
            todolist_content.setText(dailyMindMain.content);
        }
    }

    private void editTodoList() {
        Map<String, String> params = new LinkedHashMap<String, String>();
        if (isUpdate) {
            params.put("qid", String.valueOf(dailyMindMain.qid));
        }
        params.put("type", DailyMindConstants.TODOLIST + "");
        params.put("title", todolist_title.getText().toString());
        params.put("desc", todolist_title.getText().toString());
        params.put("content", todolist_content.getText().toString());
        todolistPresenter.operateDailyMindFromHttp(isUpdate ? DailyMindConstants.UPDATE_MAIN : DailyMindConstants.SAVE_TO_MAIN, params);
    }

    @Override
    public void setCloudMain() {

    }

    @Override
    public void addDailyMain(final DailyMindMain dailyMindMain) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toast((String) getText(R.string.atom_ui_tip_save_success));
                Intent intent = new Intent();
                intent.putExtra("data", dailyMindMain);
                setResult(-1, intent);
                finish();
            }
        });
    }

    @Override
    public void showErrMsg(final String error) {
        toast(error);
    }
}
