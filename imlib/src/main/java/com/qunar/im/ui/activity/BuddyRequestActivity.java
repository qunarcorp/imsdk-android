package com.qunar.im.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.BuddyRequestAdapter;
import com.qunar.im.base.module.BuddyRequest;
import com.qunar.im.base.presenter.IAnswerBuddyPresenter;
import com.qunar.im.base.presenter.IShowBuddyRequestPresenter;
import com.qunar.im.base.presenter.impl.BuddyPresenter;
import com.qunar.im.base.presenter.impl.ShowBuddyRequestPresenter;
import com.qunar.im.base.presenter.views.IAnswerBuddyRequestView;
import com.qunar.im.base.presenter.views.IAnswerForResultView;
import com.qunar.im.ui.view.QtActionBar;
import com.qunar.im.ui.view.QtNewActionBar;

import java.util.List;

/**
 * Created by zhaokai on 15-12-9.
 */
public class BuddyRequestActivity extends IMBaseActivity implements IAnswerBuddyRequestView, IAnswerForResultView {
    ListView list;
    TextView empty;
    IAnswerBuddyPresenter answerBuddyPresenter;
    IShowBuddyRequestPresenter showBuddyRequestPresenter;
    private BuddyRequestAdapter adapter;
    private String jid;
    private BuddyRequest buddyRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activty_buddy_request);
        bindViews();
        adapter = new BuddyRequestAdapter(this, this);
        answerBuddyPresenter = new BuddyPresenter();
        showBuddyRequestPresenter = new ShowBuddyRequestPresenter();
        initView();
    }
    private void bindViews() {

        list = (ListView) findViewById(R.id.list);
        empty = (TextView) findViewById(R.id.empty);
    }


    void initView() {
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setActionBarTitle(R.string.atom_ui_add_buddy_request_title);
        answerBuddyPresenter.setAnswerView(this);
        showBuddyRequestPresenter.setAnswerForResultView(this);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BuddyRequest request = (BuddyRequest) adapter.getItem(position);
                if (request.getDirection() == BuddyRequest.Direction.RECEIVE &&
                        request.getStatus() == BuddyRequest.Status.PENDING) {
                    Intent intent =new Intent(BuddyRequestActivity.this,
                            AnswerRequestActivity.class);
                    intent.putExtra("jid", request.getId());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(BuddyRequestActivity.this,
                            PersonalInfoActivity.class);
                    intent.putExtra("jid", request.getId());
                    startActivity(intent);
                }
            }
        });
        list.setEmptyView(empty);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        showBuddyRequestPresenter.listBuddyRequests();
        showBuddyRequestPresenter.initFriendRequestPropmt();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(getText(R.string.atom_ui_add_buddy_clear_request_list));


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getTitle().equals(getText(R.string.atom_ui_add_buddy_clear_request_list))){
            showBuddyRequestPresenter.clearBuddyRequests();
            adapter.clearRequests();
            adapter.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean getFriendRequstResult() {
        return true;
    }

    @Override
    public String getResean() {
        return (String) getText(R.string.atom_ui_message_agree_friend);
    }

    @Override
    public String getJid() {
        return jid;
    }

    @Override
    public void setStatus(boolean status) {
        if (status) {
            showBuddyRequestPresenter.listBuddyRequests();
        }
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void doAnswerForResult(BuddyRequest request) {
        this.jid = request.getId();
        buddyRequest = request;
        answerBuddyPresenter.answerForRequest();
    }

    @Override
    public void setRequestsList(List<BuddyRequest> list) {
        adapter.setRequests(list);
    }

    @Override
    public BuddyRequest getBuddyRequest() {
        return buddyRequest;
    }
}
