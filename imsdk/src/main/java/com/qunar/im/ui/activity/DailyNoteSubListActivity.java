package com.qunar.im.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.qunar.im.base.common.DailyMindConstants;
import com.qunar.im.base.jsonbean.DailyMindMain;
import com.qunar.im.base.jsonbean.DailyMindSub;
import com.qunar.im.ui.presenter.IDailyMindPresenter;
import com.qunar.im.ui.presenter.impl.DailyMindPresenter;
import com.qunar.im.ui.presenter.views.IDailyMindSubView;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.DailyNoteSubAdapter;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lihaibin.li on 2017/11/17.
 */

public class DailyNoteSubListActivity extends SwipeBackActivity implements PullToRefreshBase.OnRefreshListener, AdapterView.OnItemClickListener, IDailyMindSubView {
    private PullToRefreshListView notelist_listview;
    private DailyNoteSubAdapter adapter;

    private QtNewActionBar actionBar;

    private IDailyMindPresenter evernotePresenter;
    public int offset = 0;
    public int number = 10;

    private DailyMindMain dailyMindMain;
    private List<DailyMindSub> dailyMindSubs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_daily_note);

        evernotePresenter = new DailyMindPresenter();
        evernotePresenter.setView(this);

        initView();

        getCloudSub();
    }

    private void initView() {
        actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setActionBarTitle(R.string.atom_ui_title_ever_note);
        setActionBarRightText(R.string.atom_ui_btn_note_new);
        setActionBarRightTextClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DailyNoteSubListActivity.this, DailyNoteEditorActivity.class);
                intent.putExtra("qid",String.valueOf(dailyMindMain.qid));
                startActivityForResult(intent, DailyNoteEditorActivity.REQUEST_CODE);
            }
        });


        notelist_listview = (PullToRefreshListView) findViewById(R.id.note_listview);
        notelist_listview.setOnRefreshListener(this);
        notelist_listview.setOnItemClickListener(this);
        notelist_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_END);

        adapter = new DailyNoteSubAdapter(this, dailyMindSubs, R.layout.atom_ui_item_note);
        notelist_listview.setAdapter(adapter);

        Intent intent = getIntent();
        dailyMindMain = (DailyMindMain) intent.getSerializableExtra("data");
    }

    private void refeshViews() {
        List<DailyMindSub> smss = evernotePresenter.getDailySubFromDB(offset, number, dailyMindMain.qid);
        notelist_listview.setMode(dailyMindSubs.size() < number ? PullToRefreshBase.Mode.DISABLED : PullToRefreshBase.Mode.PULL_FROM_END);
        dailyMindSubs.addAll(smss);
        adapter.notifyDataSetChanged();
    }

    private void getCloudSub() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("qid", dailyMindMain.qid + "");
        params.put("version", "0");
        params.put("type", String.valueOf(DailyMindConstants.EVERNOTE));
        evernotePresenter.operateDailyMindFromHttp(DailyMindConstants.GET_CLOUD_SUB, params);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, DailyNoteEditorActivity.class);
        intent.putExtra("data", dailyMindSubs.get(i - 1));
        intent.putExtra("qid",String.valueOf(dailyMindMain.qid));
        startActivityForResult(intent, DailyNoteEditorActivity.REQUEST_CODE);
    }

    private void onComplete(List<DailyMindSub> datas) {
        notelist_listview.onRefreshComplete();
        notelist_listview.setMode(datas == null || datas.size() < number ? PullToRefreshBase.Mode.DISABLED : PullToRefreshBase.Mode.PULL_FROM_END);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        offset += dailyMindSubs.size();
        final List<DailyMindSub> datas = evernotePresenter.getDailySubFromDB(offset, number, dailyMindMain.qid);
        if (datas != null) {
            dailyMindSubs.addAll(datas);
            adapter.notifyDataSetChanged();
        }
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                onComplete(datas);
            }
        });

    }

    @Override
    public void showErrMsg(final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DailyNoteSubListActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void setCloudSub() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refeshViews();
            }
        });
    }

    @Override
    public void addDailySub(DailyMindSub dailyMindSub) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DailyNoteEditorActivity.REQUEST_CODE) {
            if (data != null) {
                offset = 0;
                dailyMindSubs.clear();
                refeshViews();
            }
        }
    }
}
