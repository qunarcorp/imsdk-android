package com.qunar.im.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.base.module.GroupMember;
import com.qunar.im.ui.presenter.IChatMemberPresenter;
import com.qunar.im.ui.presenter.impl.ChatMemberPresenter;
import com.qunar.im.ui.presenter.views.IChatmemberManageView;
import com.qunar.im.ui.util.ProfileUtils;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.GroupManagerMembersAdapter;
import com.qunar.im.ui.view.MySearchView;
import com.qunar.im.ui.view.QtSearchActionBar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatroomMemeberManagerActivity extends IMBaseActivity implements IChatmemberManageView {
    public static String GROUP_LEVEL = "group_level";
    private QtSearchActionBar searchActionBar;
    private IChatMemberPresenter chatMemberPresenter;
    private PullToRefreshListView menber_of_chatroom_at;
    private GroupManagerMembersAdapter adapter;
    private String jid;
    private int myLevel = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_chatroom_memeber_manager_actvity);
        //bugly tag
//        CrashReportUtils.getInstance().setUserTag(55095);
        injectExtras();
        chatMemberPresenter = new ChatMemberPresenter();
        chatMemberPresenter.setChatmemberManageView(this);
        initViews();
    }

    void initViews() {
        menber_of_chatroom_at = (PullToRefreshListView) this.findViewById(R.id.menber_of_chatroom_at);
        searchActionBar = (QtSearchActionBar) this.findViewById(R.id.my_action_bar);
        TextView tv_delete = (TextView) searchActionBar.findViewById(R.id.tv_delete);
        tv_delete.setText(R.string.atom_ui_nav_item_del);
        tv_delete.setVisibility(View.VISIBLE);
        //删除方法 删除选中的人员
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatMemberPresenter.kickUser();
                adapter.deleteUser();
            }
        });
        setSupportActionBar(searchActionBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        searchActionBar.getLeftLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        searchActionBar.getSearchView().changeQueryHint(getString(R.string.atom_ui_title_select_at));
        if (adapter == null) {
            adapter = new GroupManagerMembersAdapter(this);
            adapter.setGravatarHandler(new GroupManagerMembersAdapter.GravatarHandler() {
                @Override
                public void requestGravatarEvent(final String userJid,final String imageSrc, final SimpleDraweeView view) {
                    //old
//                    ProfileUtils.displayGravatarByImageSrc(userJid,imageSrc,view);
                    //new
                    ProfileUtils.displayGravatarByImageSrc(ChatroomMemeberManagerActivity.this, imageSrc, view,
                            ChatroomMemeberManagerActivity.this.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size), ChatroomMemeberManagerActivity.this.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
                }
            });
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


    }

    private void injectExtras() {
        Bundle extras_ = getIntent().getExtras();
        if (extras_ != null) {
            if (extras_.containsKey("jid")) {
                jid = extras_.getString("jid");
            }
            if (extras_.containsKey(GROUP_LEVEL)) {
                myLevel = extras_.getInt(GROUP_LEVEL);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        loadMembers();
    }

    void loadMembers() {
        BackgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                chatMemberPresenter.loadMembers();
            }
        });
    }

    @Override
    public Map<String, String> getSelectNick2Jid() {
        List<GroupMember> selectedMembers = adapter.getSelectedMembers();
        HashMap<String, String> mapOfSelected = new HashMap();
        for (GroupMember member : selectedMembers) {
            mapOfSelected.put(member.getName(),member.getMemberId() );
        }
        return mapOfSelected;
    }

    @Override
    public String getRoomId() {
        return jid;
    }

    @Override
    public void setMembers(List<GroupMember> members, int myLevel) {
        adapter.setMembers(members,myLevel);
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }
}
