package com.qunar.im.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.qunar.im.base.common.DailyMindConstants;
import com.qunar.im.base.jsonbean.DailyMindMain;
import com.qunar.im.base.presenter.IDailyMindPresenter;
import com.qunar.im.base.presenter.impl.DailyMindPresenter;
import com.qunar.im.base.presenter.views.IDailyMindMainView;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.DailyNoteListAdapter;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lihaibin.li on 2017/11/16.
 */

public class DailyNoteListActivity extends SwipeBackActivity implements PullToRefreshBase.OnRefreshListener, AdapterView.OnItemClickListener, IDailyMindMainView {
    private PullToRefreshListView notelist_listview;
    private DailyNoteListAdapter adapter;

    private QtNewActionBar actionBar;

    private List<DailyMindMain> dailyMindMains = new ArrayList<>();

    private IDailyMindPresenter evernotePresenter;
    public int offset = 0;
    public int number = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_daily_note);

        evernotePresenter = new DailyMindPresenter();
        evernotePresenter.setView(this);

        initView();

        getCloudMain();
    }

    private void initView() {
        actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setActionBarTitle(R.string.atom_ui_note_title_notebook);
        setActionBarRightText(R.string.atom_ui_btn_note_new);
        setActionBarRightTextClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNoteDialogDialog();
            }
        });

        notelist_listview = (PullToRefreshListView) findViewById(R.id.note_listview);
        adapter = new DailyNoteListAdapter(this, dailyMindMains, R.layout.atom_ui_item_note);
        notelist_listview.getRefreshableView().setAdapter(adapter);
        notelist_listview.getRefreshableView().setOnItemClickListener(this);
        notelist_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        notelist_listview.getRefreshableView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                showEditDialog(adapter.getItem(i-1));
                return true;
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
                evernotePresenter.operateDailyMindFromHttp(DailyMindConstants.DELETE_MAIN,params);
            }
        });
        builder.create().show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(DailyNoteListActivity.this, DailyNoteSubListActivity.class);
        intent.putExtra("data", dailyMindMains.get(i - 1));
        startActivity(intent);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        offset = dailyMindMains.size();
        refeshViews();
    }

    private void refeshViews() {
        final List<DailyMindMain> data = evernotePresenter.getDailyMainFromDB(DailyMindConstants.EVERNOTE, offset, number);
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
        notelist_listview.onRefreshComplete();
        notelist_listview.setMode(datas == null || datas.size() < number ? PullToRefreshBase.Mode.DISABLED : PullToRefreshBase.Mode.PULL_FROM_END);
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
    public void addDailyMain(final DailyMindMain dailyMindMain) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dailyMindMains.add(dailyMindMain);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void showErrMsg(final String error) {
        toast(error);
    }

    private AlertDialog noteDialog;

    /**
     * 插入链接Dialog
     */
    private void showNoteDialogDialog() {

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        noteDialog = adb.create();

        View view = getLayoutInflater().inflate(R.layout.atom_ui_dialog_note_create, null);

        final EditText noteTitle = (EditText) view.findViewById(R.id.note_title);
        final EditText noteDesc = (EditText) view.findViewById(R.id.note_desc);

        //点击确实的监听
        view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(noteTitle.getText().toString())){
                    toast((String) getText(R.string.atom_ui_note_input_title_hint));
                    return;
                }
                noteDialog.dismiss();
                saveEverNote(noteTitle.getText().toString(),noteDesc.getText().toString());

            }
        });
        //点击取消的监听
        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteDialog.dismiss();
            }
        });
        noteDialog.setCancelable(false);
        noteDialog.setView(view, 0, 0, 0, 0); // 设置 view
        noteDialog.show();
    }


    private void getCloudMain() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("version", "0");
        params.put("type", String.valueOf(DailyMindConstants.EVERNOTE));
        evernotePresenter.operateDailyMindFromHttp(DailyMindConstants.GET_CLOUD_MAIN, params);
    }

    private void saveEverNote(String title,String desc) {
        Map<String, String> params = new LinkedHashMap<String, String>();

        params.put("type", DailyMindConstants.EVERNOTE + "");
        params.put("title", title);
        params.put("desc", desc);
        params.put("content", desc);
        evernotePresenter.operateDailyMindFromHttp(DailyMindConstants.SAVE_TO_MAIN, params);
    }

}
