package com.qunar.im.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.module.GroupMember;
import com.qunar.im.base.module.Nick;
import com.qunar.im.ui.presenter.IChatroomInfoPresenter;
import com.qunar.im.ui.presenter.impl.ChatroomInfoPresenter;
import com.qunar.im.ui.presenter.views.IChatMemberAdapter;
import com.qunar.im.base.util.ListUtil;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.common.CurrentPreference;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.MembersAdapter;
import com.qunar.im.ui.view.MySearchView;
import com.qunar.im.ui.view.QtSearchActionBar;

import java.util.List;

/**
 * Created by xinbo.wang on 2015/4/2.
 */
public class AtListActivity extends IMBaseActivity{
    PullToRefreshListView menber_of_chatroom_at;
    String jid;
    MembersAdapter adapter;

    IChatroomInfoPresenter chatroomInfoPresenter;
    private QtSearchActionBar searchActionBar;

    @Override
    public void onCreate(Bundle saveInstancedState)
    {
        super.onCreate(saveInstancedState);
        injectExtras();
        setContentView(R.layout.atom_ui_activity_at_list);
        bindViews();
        chatroomInfoPresenter = new ChatroomInfoPresenter();
        chatroomInfoPresenter.setView(new IChatMemberAdapter(){
            @Override
            public void setMemberList(final List<GroupMember> members, final int myPowerLevel ,boolean enforce) {
                getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        List<GroupMember> excludeMe = ListUtil.filter(members, new ListUtil.ListFilter<GroupMember>() {
                            @Override
                            public boolean accept(GroupMember source) {
                                if(source != null && source.getName() != null && source.getName().equals(CurrentPreference.getInstance().getFullName())
                                        || (TextUtils.isEmpty(source.getMemberId()) || source.getMemberId().equals(CurrentPreference.getInstance().getPreferenceUserId()))) {
                                    return false;
                                }
                                return true;
                            }
                        });
                        adapter.setMembers(excludeMe);
                    }
                });
            }

            @Override
            public String getRoomId() {
                return jid;
            }

            @Override
            public String getChatType() {
                return null;
            }

            @Override
            public void closeActivity() {
                AtListActivity.this.finish();
            }

            @Override
            public Context getContext() {
                return AtListActivity.this.getApplicationContext();
            }

        });

        initViews();
    }

    private void bindViews()
    {
        menber_of_chatroom_at = (PullToRefreshListView) findViewById(R.id.menber_of_chatroom_at);
    }

    private void injectExtras() {
        Bundle extras_ = getIntent().getExtras();
        if (extras_!= null) {
            if (extras_.containsKey("jid")) {
                jid = extras_.getString("jid");
            }
        }
    }

    void initViews()
    {
        searchActionBar = (QtSearchActionBar) this.findViewById(R.id.my_action_bar);
        setSupportActionBar(searchActionBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        searchActionBar.getLeftLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        searchActionBar.getSearchView().changeQueryHint(getString(R.string.atom_ui_title_select_at));
        if(adapter == null)
        {
            adapter = new MembersAdapter(this);
            menber_of_chatroom_at.setAdapter(adapter);
            menber_of_chatroom_at.setMode(PullToRefreshBase.Mode.MANUAL_REFRESH_ONLY);
        }
        searchActionBar.getSearchView().setOnQueryChangeListener(new MySearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        menber_of_chatroom_at.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final GroupMember crm = ((GroupMember) parent.getAdapter().getItem(position));
                if(crm!= null)
                {
                    if(position == 0){//@所有人
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("atJid",crm.getMemberId());
                        returnIntent.putExtra("atName",crm.getName());
                        if(getParent()==null) {
                            setResult(RESULT_OK,returnIntent);
                        }
                        else {
                            getParent().setResult(RESULT_OK,returnIntent);
                        }
                        return;
                    }
                    ConnectionUtil.getInstance().getUserCard(crm.getMemberId(), new IMLogicManager.NickCallBack() {
                        @Override
                        public void onNickCallBack(Nick nick) {
                            Intent returnIntent = new Intent();
                            //@的人的名字
                            //@的人的xx@xxx
                            returnIntent.putExtra("atName",(nick == null)?crm.getMemberId():nick.getName());
                            returnIntent.putExtra("atJid",crm.getMemberId());
                            if(getParent()==null) {
                                setResult(RESULT_OK,returnIntent);
                            }
                            else {
                                getParent().setResult(RESULT_OK,returnIntent);
                            }
                            AtListActivity.this.finish();
                        }
                    }, false, false);
                }
            }
        });
    }

    @Override
    public void onStart()
    {
        super.onStart();
        chatroomInfoPresenter.showMembers(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chatroomInfoPresenter.close();
    }
}
