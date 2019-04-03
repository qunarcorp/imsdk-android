package com.qunar.im.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.qunar.im.base.common.DailyMindConstants;
import com.qunar.im.base.jsonbean.DailyMindMain;
import com.qunar.im.ui.presenter.IDailyMindPresenter;
import com.qunar.im.ui.presenter.impl.DailyMindPresenter;
import com.qunar.im.ui.presenter.views.IDailyMindMainView;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.DailyTodolistAdapter;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * todolist
 * Created by lihaibin.li on 2017/10/16.
 */

public class DailyToDoListActivity extends SwipeBackActivity implements PullToRefreshBase.OnRefreshListener, AdapterView.OnItemClickListener, IDailyMindMainView {
    private PullToRefreshListView todolist_listview;
    private DailyTodolistAdapter adapter;
    private ImageView add_todolist;

    private QtNewActionBar actionBar;

    private List<DailyMindMain> dailyMindMains = new ArrayList<>();

    private IDailyMindPresenter todolistPresenter;
    public int offset = 0;
    public int number = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_daily_todolist);

        todolistPresenter = new DailyMindPresenter();
        todolistPresenter.setView(this);

        initView();

        getCloudMain();
    }

    private void initView() {
        actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setActionBarTitle(R.string.atom_ui_title_todolist);
        setActionBarRightText(R.string.atom_ui_btn_note_new);
        setActionBarRightTextClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(DailyToDoListActivity.this, DailyToDoListCreateActivity.class), DailyToDoListCreateActivity.REQUEST_CODE);
            }
        });

        todolist_listview = (PullToRefreshListView) findViewById(R.id.todolist_listview);
        adapter = new DailyTodolistAdapter(this, dailyMindMains, R.layout.atom_ui_item_todolist);
        todolist_listview.getRefreshableView().setAdapter(adapter);
        todolist_listview.getRefreshableView().setOnItemClickListener(this);
        todolist_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        todolist_listview.getRefreshableView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                showEditDialog(adapter.getItem(i-1));
                return true;
            }
        });
        add_todolist = (ImageView) findViewById(R.id.add_todolist);
        add_todolist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(DailyToDoListActivity.this, DailyToDoListCreateActivity.class), DailyToDoListCreateActivity.REQUEST_CODE);
            }
        });
    }

    private void showEditDialog(final DailyMindMain dailyMindMain) {
        String items[] = {(String) getText(R.string.atom_ui_common_delete)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Map<String,String> params = new HashMap<String, String>();
                params.put("qid",String.valueOf(dailyMindMain.qid));
                todolistPresenter.operateDailyMindFromHttp(DailyMindConstants.DELETE_MAIN,params);
            }
        });
        builder.create().show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(DailyToDoListActivity.this, DailyToDoListCreateActivity.class);
        intent.putExtra("data", dailyMindMains.get(i - 1));
        startActivityForResult(intent, DailyToDoListCreateActivity.REQUEST_CODE);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        offset = dailyMindMains.size();
        refeshViews();
    }

    private void refeshViews() {
        final List<DailyMindMain> data = todolistPresenter.getDailyMainFromDB(DailyMindConstants.TODOLIST, offset, number);
        dailyMindMains.addAll(data);
        adapter.notifyDataSetChanged();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onComplete(data);
            }
        });
    }

    private void onComplete(List<DailyMindMain> datas) {
        todolist_listview.onRefreshComplete();
        todolist_listview.setMode(datas == null || datas.size() < number ? PullToRefreshBase.Mode.DISABLED : PullToRefreshBase.Mode.PULL_FROM_END);
    }

    @Override
    public void setCloudMain() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dailyMindMains.clear();
                refeshViews();
            }
        });
    }

    @Override
    public void addDailyMain(DailyMindMain dailyMindMain) {

    }

    @Override
    public void showErrMsg(final String error) {
        toast(error);
    }

    private void getCloudMain() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("version", "0");
        params.put("type", String.valueOf(DailyMindConstants.TODOLIST));
        todolistPresenter.operateDailyMindFromHttp(DailyMindConstants.GET_CLOUD_MAIN, params);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DailyToDoListCreateActivity.REQUEST_CODE) {
            if (data != null) {
                offset = 0;
                dailyMindMains.clear();
                refeshViews();
            }
        }
    }
}
