package com.qunar.im.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qunar.im.ui.presenter.WorkWorldAtListPresenter;
import com.qunar.im.ui.presenter.impl.WorkWorldAtListManagerPresenter;
import com.qunar.im.ui.presenter.views.WorkWorldAtListView;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.RecycleViewDivider;
import com.qunar.im.ui.adapter.WorkWorldAtListAdapter;
import com.qunar.im.ui.adapter.WorkWorldAtListIsSelectAdapter;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;

import java.util.ArrayList;
import java.util.List;

public class WorkWorldAtListActivity extends SwipeBackActivity implements WorkWorldAtListView {

    private RecyclerView work_world_atlist_rc;
    private RecyclerView work_world_isselect_rc;
    private EditText search_text;

    private boolean showFirstAt;
    private String jid;
    protected QtNewActionBar qtNewActionBar;//头部导航

    private WorkWorldAtListPresenter workWorldAtListPresenter;
    private WorkWorldAtListAdapter adapter ;
    private WorkWorldAtListIsSelectAdapter isSelectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_work_world_at_list);
        jid = getIntent().getStringExtra("jid");
        showFirstAt = getIntent().getBooleanExtra("showFirstAt",false);
        bindView();
        initAdapter();
        bindData();

    }

    private void initAdapter() {
        workWorldAtListPresenter = new WorkWorldAtListManagerPresenter();
        workWorldAtListPresenter.setView(this);
        adapter = new WorkWorldAtListAdapter(this);
        isSelectAdapter = new WorkWorldAtListIsSelectAdapter(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        work_world_isselect_rc.setLayoutManager(manager);
        work_world_isselect_rc.setAdapter(isSelectAdapter);
        adapter.setOnSelectChanage(list -> isSelectAdapter.setNewData(list));

        isSelectAdapter.setOnCancelLis(str -> {
            //操作树形列表
            adapter.setCancelInfo(str);
        });

        work_world_atlist_rc.setLayoutManager(new LinearLayoutManager(this));
        work_world_atlist_rc.addItemDecoration(new RecycleViewDivider(
                this, LinearLayoutManager.VERTICAL, 1, R.color.atom_ui_primary_color));
        work_world_atlist_rc.setAdapter(adapter);
    }

    private void bindData() {



        search_text.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                workWorldAtListPresenter.startSearch();
            }
            return false;
        });
    }

    private void bindView() {
        qtNewActionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(qtNewActionBar);
        setActionBarTitle(getString(R.string.atom_ui_notify_somebody));
        work_world_atlist_rc = (RecyclerView) findViewById(R.id.work_world_atlist_rc);
        work_world_isselect_rc = (RecyclerView) findViewById(R.id.work_world_isselect_rc);
        search_text = (EditText) findViewById(R.id.search_text);
        setActionBarRightText(getString(R.string.atom_ui_common_confirm));
        setActionBarRightTextClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();

//                intent.putExtra("atList",isSelectAdapter.getData());
                intent.putStringArrayListExtra("atList", (ArrayList<String>) isSelectAdapter.getData());
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    @Override
    public String getSearText() {
        return search_text.getText().toString();
    }

    @Override
    public void showSearchUser(List<String> list) {
        adapter.setNewData(list);
    }

    @Override
    public void showToast(final String string ) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(WorkWorldAtListActivity.this,string,Toast.LENGTH_LONG).show();
            }
        });



    }

    @Override
    public String getJid() {
        return jid;
    }
}
