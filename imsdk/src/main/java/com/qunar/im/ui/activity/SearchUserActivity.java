package com.qunar.im.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.base.module.IMGroup;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.module.PublishPlatform;
import com.qunar.im.base.module.RecentConversation;
import com.qunar.im.base.protocol.NativeApi;
import com.qunar.im.ui.presenter.ISearchFriendPresenter;
import com.qunar.im.ui.presenter.ITransferRecentCovnPresenter;
import com.qunar.im.ui.presenter.impl.SearchFriendPresenter;
import com.qunar.im.ui.presenter.impl.TransferRecentConvPresenter;
import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.ui.presenter.views.ISearchFriendView;
import com.qunar.im.ui.presenter.views.ITransferRecentConvView;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.view.BaseInfoBinderable;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.BaseInfoAdapter;
import com.qunar.im.ui.adapter.RecentConvsAdapter;
import com.qunar.im.ui.fragment.DeptFragment;
import com.qunar.im.ui.view.MySearchView;
import com.qunar.im.ui.view.QtSearchActionBar;
import com.qunar.im.utils.QtalkStringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by may on 15/2/6.
 */
public class SearchUserActivity extends IMBaseActivity implements ISearchFriendView, View.OnTouchListener {
    private final String TAG = "SearchUserActivity";
    FrameLayout fl_fragment;
    ListView recent_conversation;
    ListView searchResults;

    public static final String SEARCH_SCOPE = "search_scope";
    public static final String SEARCH_TERM = "search_term";

    private static final String IS_CURRENT_SEARCH = "isCurrentSearch";

    public static final int CONTACTS = 1;
    public static final int GROUPS = 2;
    public static final int PUBLIC_PLATFORM = 4;
    public static final int FRIENDS = 8;


    ISearchFriendPresenter presenter;
    BaseInfoAdapter adapter;
    RecentConvsAdapter simpleRecentConvsAdapter;
    ITransferRecentCovnPresenter conversationPersenter;

    private int scope = 7;
    private String searchTerm;
    private int maxCount = 5;
    private boolean isFromShare = false;
    private String shareMsgJson;

    private boolean isTransMultiImg;

    /****
     * !!暂时!!转发列表放在此处
     */
    boolean isSelectTransUser;
    boolean isCurrentSearch;
    Serializable transMsg;
    QtSearchActionBar searchActionBar;

    Intent tempIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_seach);
        bindViews();
        injectExtra(getIntent());
        presenter = new SearchFriendPresenter();
        presenter.setSearchFriendView(this);
        initViews();
    }

    @Override
    public void onNewIntent(Intent intent) {
        injectExtra(intent);
        initViews();
    }

    private void injectExtra(Intent intent) {
        tempIntent = intent;
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            if (bundle.containsKey(SEARCH_SCOPE)) {
                scope = bundle.getInt(SEARCH_SCOPE);
            }
            if (bundle.containsKey(Constants.BundleKey.IS_TRANS)) {
                isSelectTransUser = bundle.getBoolean(Constants.BundleKey.IS_TRANS);
            }
            if(bundle.containsKey(Constants.BundleKey.IS_TRANS_MULTI_IMG)){//转发多张图片&视频
                isTransMultiImg = bundle.getBoolean(Constants.BundleKey.IS_TRANS_MULTI_IMG);
            }
            if (isSelectTransUser) {
                transMsg = bundle.getSerializable(Constants.BundleKey.TRANS_MSG);
            }
            if (bundle.containsKey(SEARCH_TERM)) {
                searchTerm = bundle.getString(SEARCH_TERM);
            }
            if (bundle.containsKey(Constants.BundleKey.IS_FROM_SHARE)) {
                isFromShare = bundle.getBoolean(Constants.BundleKey.IS_FROM_SHARE);
                shareMsgJson = bundle.getString(Constants.BundleKey.SHARE_EXTRA_KEY, "");
            }
            if(bundle.containsKey(IS_CURRENT_SEARCH)){
                isCurrentSearch = bundle.getBoolean(IS_CURRENT_SEARCH);
            }
        }
        if (scope == CONTACTS || scope == GROUPS || scope == PUBLIC_PLATFORM
                || scope == FRIENDS) {
            maxCount = -1;
        }
    }

    private void bindViews() {
        recent_conversation = (ListView) findViewById(R.id.recent_conversation);
        searchResults = (ListView) findViewById(android.R.id.list);
        fl_fragment = (FrameLayout) findViewById(R.id.fl_fragment);
    }

    void initViews() {
        searchActionBar = (QtSearchActionBar) this.findViewById(R.id.my_action_bar);
        setSupportActionBar(searchActionBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        searchActionBar.getLeftLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (getIntent().getIntExtra("requestcode", 0) == PbChatActivity.TRANSFER_CONVERSATION_REQUEST_CODE) {
            fl_fragment.setVisibility(View.VISIBLE);
            DeptFragment deptFragment = new DeptFragment();
            Bundle args = new Bundle();
            args.putInt(DeptFragment.FROM_ACTION, 1);
            deptFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().add(R.id.fl_fragment, deptFragment).commit();
            return;
        }

        if (adapter == null) {
            adapter = new BaseInfoAdapter(this);
            adapter.setClickHandler(new DefaultClickHandler());
        }
        searchResults.setAdapter(adapter);

        searchActionBar.getSearchView().setOnQueryChangeListener(new MySearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                doSearchFriend();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)) {
                    searchResults.setVisibility(View.VISIBLE);
                    if (isSelectTransUser)
                        recent_conversation.setVisibility(View.GONE);
                        doSearchFriend();
                } else {
                    if (isSelectTransUser)
                        recent_conversation.setVisibility(View.VISIBLE);
                    searchResults.setVisibility(View.GONE);
                }
                return true;
            }
        });
        if (TextUtils.isEmpty(searchTerm)) {
            searchActionBar.getSearchView().getEditFocus();
        } else {
            searchActionBar.getSearchView().setQuery(searchTerm, true);
        }
        if(isCurrentSearch){
            searchActionBar.getSearchView().setVisibility(View.GONE);
        }
        searchResults.setOnTouchListener(this);
        if ((isSelectTransUser || isTransMultiImg) && !isCurrentSearch) {
            if (simpleRecentConvsAdapter == null) {
                simpleRecentConvsAdapter = new RecentConvsAdapter(this);
            }

            conversationPersenter = new TransferRecentConvPresenter();
            conversationPersenter.setView(new ITransferRecentConvView() {
                @Override
                public void setRecentConvList(List<RecentConversation> convers) {
                    simpleRecentConvsAdapter.setRecentConversationList(convers);
                }
            });

            final View headerView = LayoutInflater.from(this).inflate(R.layout.atom_ui_activity_seach_recentlist_header, null, false);
            final TextView createNewGroup = (TextView) headerView.findViewById(R.id.create_chatroom);
            final TextView joinedGroup = (TextView) headerView.findViewById(R.id.my_joind_group);

            createNewGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(SearchUserActivity.this, ChatroomInvitationActivity.class);
//                    intent.putExtra("userId", "");
//                    intent.putExtra("actionType", ChatroomInvitationActivity.ACTION_TYPE_CHREATE_GROUP);
//                    startActivity(intent);
//                    finish();
                    if(isFromShare){
                        NativeApi.openCreateGroupForShare(shareMsgJson);
                    }else if(isSelectTransUser){
                        NativeApi.openCreateGroupForTrans(transMsg);
                    }else {
                        NativeApi.openCreateGroup();
                    }
                    finish();
                }
            });

            joinedGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(SearchUserActivity.this, ChatRoomActivity.class);
//                    intent.putExtra(Constants.BundleKey.IS_TRANS, isSelectTransUser);
//                    intent.putExtra(Constants.BundleKey.TRANS_MSG, transMsg);
//                    startActivity(intent);
//                    finish();
                    if(isFromShare){
                        NativeApi.openMyGroupsForShare(shareMsgJson);
                    }else if(isSelectTransUser){
                        NativeApi.openMyGroupsForTrans(transMsg);
                    }else {
                        NativeApi.openMyGroups();
                    }
                    finish();
                }
            });
            recent_conversation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Object obj = parent.getItemAtPosition(position);
                    if (obj != null) {
                        RecentConversation item = (RecentConversation) obj;
                        if (item.getConversationType() == ConversitionType.MSG_TYPE_CHAT) {
                            startChatActivity(QtalkStringUtils.parseBareJid(item.getId()), false);
                        } else if (item.getConversationType() == ConversitionType.MSG_TYPE_GROUP) {
                            startChatActivity(item.getId(), true);
                        }
                    }
                }
            });
            //不知道啥逻辑 为啥qchat关掉头view？
//            if (!CommonConfig.isQtalk) {
//                createNewGroup.setVisibility(View.GONE);
//                joinedGroup.setVisibility(View.GONE);
//            }else{
//                recent_conversation.addHeaderView(headerView, null, false);
//            }
            recent_conversation.addHeaderView(headerView, null, false);

            recent_conversation.setAdapter(simpleRecentConvsAdapter);
            recent_conversation.setOnTouchListener(this);
            recent_conversation.setVisibility(View.VISIBLE);
            conversationPersenter.showRecentConvs();
        }

    }

    void doSearchFriend() {
        adapter.clear();
        BackgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if ((scope & CONTACTS) == CONTACTS) {
                    presenter.doSearchContacts();
                }
                if ((scope & GROUPS) == GROUPS) {
                    presenter.doSearchGroups();
                }
                if ((scope & PUBLIC_PLATFORM) == PUBLIC_PLATFORM) {

                    // TODO: 2017/9/11  //这个方法是查询公众号,现在还没有做公众号先屏蔽
//                    presenter.doSearchPublishPlatform();
                }
//                if ((scope & FRIENDS) == FRIENDS) {
//                    presenter.doSearchFriend();
//                }
                getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    public String getTerm() {
        return searchActionBar.getSearchView().getQuery().toString();
    }

    @Override
    public void setSearchResult(final List<Nick> results) {
        boolean isFirst = true;
        final List<BaseInfoBinderable> baseInfoBinderables = new ArrayList<>(results.size());
        if (results.size() > 0) {
            for (Nick item : results) {
                BaseInfoBinderable baseInfoBinderable = new BaseInfoBinderable();
                baseInfoBinderable.id = item.getXmppId();
                baseInfoBinderable.name = item.getName();
                baseInfoBinderable.desc = item.getDescInfo();
                baseInfoBinderable.type = BaseInfoBinderable.CONTACT_TYPE;
                if (isFirst) {
                    baseInfoBinderable.hint = getString(R.string.atom_ui_tab_contacts);
                    isFirst = false;
                }
                baseInfoBinderables.add(baseInfoBinderable);
            }
            if (maxCount > -1) {
                BaseInfoBinderable latest = new BaseInfoBinderable();
                latest.name = Constants.SearchExtension.MORE_CONTACT;
                latest.imageUrl = "res:///" + R.drawable.atom_ui_ic_search_more;
                latest.type = BaseInfoBinderable.SEARCH_MORE_CONTACT;
                baseInfoBinderables.add(latest);
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.update(baseInfoBinderables);
            }
        });

    }


    @Override
    public void setChatRoomResult(final List<IMGroup> results) {
        boolean isFirst = true;
        final List<BaseInfoBinderable> baseInfoBinderables = new ArrayList<>(results.size());
        if (results.size() > 0) {
            for (IMGroup item : results) {
                BaseInfoBinderable baseInfoBinderable = new BaseInfoBinderable();
                baseInfoBinderable.id = item.getGroupId();
                baseInfoBinderable.name = item.getName();
                baseInfoBinderable.desc = item.getIntroduce();
                baseInfoBinderable.type = BaseInfoBinderable.GROUP_TYPE;
//                baseInfoBinderable.connection = item.getIsJoined() == ChatRoom.JOINED;
                //判断是加入了
                baseInfoBinderable.connection = true;
                if (isFirst) {
                    baseInfoBinderable.hint = getString(R.string.atom_ui_common_groups);
                    isFirst = false;
                }
                baseInfoBinderables.add(baseInfoBinderable);
            }
            if (maxCount > -1) {
                BaseInfoBinderable latest = new BaseInfoBinderable();
                latest.name = Constants.SearchExtension.MORE_CHATROOM;
                latest.imageUrl = "res:///" + R.drawable.atom_ui_ic_search_more;
                latest.type = BaseInfoBinderable.SEARCH_MORE_GROUP;
                baseInfoBinderables.add(latest);
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.update(baseInfoBinderables);
            }
        });

    }

    @Override
    public void setPublishPlatformResult(List<PublishPlatform> results) {
        if (isSelectTransUser) return;
        boolean isFirst = true;
        final List<BaseInfoBinderable> baseInfoBinderables = new ArrayList<>(results.size());
        if (results.size() > 0) {
            for (PublishPlatform item : results) {
                BaseInfoBinderable baseInfoBinderable = new BaseInfoBinderable();
                baseInfoBinderable.id = item.getId();
                baseInfoBinderable.name = item.getName();
                baseInfoBinderable.desc = item.getDescription();
                baseInfoBinderable.type = BaseInfoBinderable.PUBLISH_TYPE;
                if (isFirst) {
                    baseInfoBinderable.hint = getString(R.string.atom_ui_contact_tab_public_number);
                    isFirst = false;
                }
                baseInfoBinderables.add(baseInfoBinderable);
            }
            if (maxCount > -1) {
                BaseInfoBinderable latest = new BaseInfoBinderable();
                latest.name = Constants.SearchExtension.MORE_PLATFORM;
                latest.imageUrl = "res:///" + R.drawable.atom_ui_ic_search_more;
                latest.type = BaseInfoBinderable.SEARCH_MORE_PUB;
                baseInfoBinderables.add(latest);
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.update(baseInfoBinderables);
            }
        });
    }

    @Override
    public int getMaxCount() {
        return maxCount;
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            //imm.toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return false;
    }

    private void startChatActivity(final String jid, boolean isMuc) {
        if (isFromShare) {//处理分享消息
            Intent i = getIntent();
            i.setClass(this, PbChatActivity.class);
            i.putExtra(Constants.BundleKey.IS_FROM_SHARE, true);
            i.putExtra(PbChatActivity.KEY_JID, jid);
            i.putExtra(PbChatActivity.KEY_IS_CHATROOM, isMuc);
            Logger.i("分享:开启"+i.getDataString());
            startActivity(i);
//            EventBus.getDefault().post(new EventBusEvent.SendShareMsg(shareMsgJson, jid));

            finish();
            return;
        }
        if (isSelectTransUser) {//处理转发消息
            EventBus.getDefault().post(new EventBusEvent.SendTransMsg(transMsg, jid));
//            IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.TRANS_MESSAGE, transMsg, jid);
            SearchUserActivity.this.finish();
        } else if(isTransMultiImg){//处理转发多条图片视频
            Intent i = getIntent();
            i.setClass(this, PbChatActivity.class);
            i.putExtra(PbChatActivity.KEY_JID, jid);
            i.putExtra(PbChatActivity.KEY_IS_CHATROOM, isMuc);

            startActivity(i);

            finish();
        } else{//处理正常搜索
            if (isMuc) {
                Intent i = new Intent(this, PbChatActivity.class);
                i.putExtra(PbChatActivity.KEY_JID, jid);
                i.putExtra(PbChatActivity.KEY_IS_CHATROOM, true);
                startActivity(i);
            } else {
                NativeApi.openUserCardVCByUserId(jid);

            }
        }
    }

    private class DefaultClickHandler implements BaseInfoAdapter.ViewClickHandler {
        @Override
        public void ItemClickEvent(BaseInfoBinderable item) {
            if (item.type == BaseInfoBinderable.CONTACT_TYPE) {
                LogUtil.d(TAG, item.id);
                if (getIntent().getIntExtra("requestcode", 0) == PbChatActivity.TRANSFER_CONVERSATION_REQUEST_CODE) {//转移会话
                    Intent intent = new Intent();
                    intent.putExtra("userid", item.id);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else {//普通搜索
                    startChatActivity(QtalkStringUtils.userId2Jid(item.id)
                            , false);
                }


            } else if (item.type == BaseInfoBinderable.GROUP_TYPE) {
                if (item.connection) {
                    startChatActivity(item.id, true);
                } else {
                    if (isSelectTransUser) {
                        Toast.makeText(getApplicationContext(), getString(R.string.atom_ui_not_add_to_the_group), Toast.LENGTH_LONG).show();
                    }
                    Intent i = new Intent(SearchUserActivity.this,
                            ChatroomInfoActivity.class);
                    i.putExtra("roomId", item.id);
                    startActivity(i);
                    SearchUserActivity.this.finish();
                }
            } else if (item.type == BaseInfoBinderable.PUBLISH_TYPE) {
                Intent intent = new Intent(SearchUserActivity.this, RobotInfoActivity.class);
                intent.putExtra("robotId", item.id);
                startActivity(intent);
            } else if (item.type == BaseInfoBinderable.SEARCH_MORE_CONTACT) {
                Intent intent = new Intent();
                intent.setClass(SearchUserActivity.this, SearchUserActivity.class);
                intent.putExtra(SearchUserActivity.SEARCH_TERM, getTerm());
                intent.putExtra(SearchUserActivity.SEARCH_SCOPE, CONTACTS);
                putIntentExtras(intent);
                startActivity(intent);
            } else if (item.type == BaseInfoBinderable.SEARCH_MORE_GROUP) {
                Intent intent = new Intent();
                intent.setClass(SearchUserActivity.this, SearchUserActivity.class);
                intent.putExtra(SearchUserActivity.SEARCH_TERM, getTerm());
                intent.putExtra(SearchUserActivity.SEARCH_SCOPE, GROUPS);
                putIntentExtras(intent);
                startActivity(intent);
            } else if (item.type == BaseInfoBinderable.SEARCH_MORE_PUB) {
                Intent intent = new Intent();
                intent.setClass(SearchUserActivity.this, SearchUserActivity.class);
                intent.putExtra(SearchUserActivity.SEARCH_TERM, getTerm());
                intent.putExtra(SearchUserActivity.SEARCH_SCOPE, PUBLIC_PLATFORM);
                startActivity(intent);
            }
        }
    }

    private void putIntentExtras(Intent intent){
        Bundle bundle = tempIntent.getExtras();
        if(bundle == null){
            return;
        }
        if (bundle.containsKey(Constants.BundleKey.IS_TRANS)) {
            isSelectTransUser = bundle.getBoolean(Constants.BundleKey.IS_TRANS);
        }
        if (isSelectTransUser) {
            transMsg = bundle.getSerializable(Constants.BundleKey.TRANS_MSG);
            intent.putExtra(Constants.BundleKey.IS_TRANS,isSelectTransUser);
            intent.putExtra(Constants.BundleKey.TRANS_MSG,transMsg);
        }
        if (bundle.containsKey(Constants.BundleKey.IS_FROM_SHARE)) {
            isFromShare = bundle.getBoolean(Constants.BundleKey.IS_FROM_SHARE);
            shareMsgJson = bundle.getString(Constants.BundleKey.SHARE_EXTRA_KEY, "");
            intent.putExtra(Constants.BundleKey.IS_FROM_SHARE,isFromShare);
            intent.putExtra(Constants.BundleKey.SHARE_EXTRA_KEY,shareMsgJson);
        }
        intent.putExtra(IS_CURRENT_SEARCH,true);
    }
}
