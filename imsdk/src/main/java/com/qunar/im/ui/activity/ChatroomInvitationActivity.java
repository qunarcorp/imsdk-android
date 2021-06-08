package com.qunar.im.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orhanobut.logger.Logger;
import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.base.module.DepartmentItem;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.module.UserVCard;
import com.qunar.im.ui.presenter.IChatroomCreatedPresenter;
import com.qunar.im.ui.presenter.IInvitedFriendsPresenter;
import com.qunar.im.ui.presenter.impl.ChatroomCreatedPresenter;
import com.qunar.im.ui.presenter.impl.InvitedFriendsPresenter;
import com.qunar.im.ui.presenter.views.IChatroomCreatedView;
import com.qunar.im.ui.presenter.views.IInvitedFriendsView;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.ui.util.ProfileUtils;
import com.qunar.im.base.view.multilLevelTreeView.Node;
import com.qunar.im.common.CurrentPreference;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.InvitationAdapter;
import com.qunar.im.ui.adapter.InviteToChatroomHorizonalListviewAdapter;
import com.qunar.im.ui.adapter.InviteTreeAdapter;
import com.qunar.im.ui.view.HorizontalListView;
import com.qunar.im.ui.view.MySearchView;
import com.qunar.im.ui.view.QtSearchActionBar;
import com.qunar.im.utils.QtalkStringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xinbo.wang on 2015/3/4.
 */
public class ChatroomInvitationActivity extends IMBaseActivity implements
        View.OnClickListener, IInvitedFriendsView {
    private static final String TAG = ChatroomInvitationActivity.class.getSimpleName();
    public static final int ACTION_TYPE_CHREATE_GROUP = 0x01;
    public static final int ACTION_TYPE_INVITATION = 0x02;
    public static final int ACTION_TYPE_MULTI_SEL = 0x03;
    public final static String KEY_SELECTED_USER = "seluser";
    public final static String USER_ID_EXTRA = "userId";
    public final static String ACTION_TYPE_EXTRA = "actionType";
    public final static String ROOM_ID_EXTRA = "roomId";
    public final static String M_NOT_CHANGE_IDS_EXTRA = "mNotChangeIds";
    String userId;
    String roomId;
    String[] selectedUserIds;
    int actionType;
    List<String> mNotChangeIds = new ArrayList<>();
    QtSearchActionBar searchBar;
    ViewGroup search_panel;
    PullToRefreshListView lv_all_contacts;
    ListView lv_search_contacts;
    TextView btn_create;
    HorizontalListView hlv_selected_contacts;
    InvitationAdapter mInvationAdapter;
    IInvitedFriendsPresenter mInvitedFriendsPresenter;
    IChatroomCreatedPresenter mChatroomCreatedPresenter;
    InviteTreeAdapter mTreeAdapter;
    List<Node> mSelectedNodes;
    ProgressDialog progressDialog;

    InviteToChatroomHorizonalListviewAdapter mInviteToChatroomHorizonalListviewAdapter;

    private void injectExtras() {
        Bundle extras_ = getIntent().getExtras();
        if (extras_ != null) {
            if (extras_.containsKey(USER_ID_EXTRA)) {
                userId = extras_.getString(USER_ID_EXTRA);//QtalkStringUtils.userId2Jid(extras_.getString(USER_ID_EXTRA));
                Logger.i("创建群 userId:" + userId);
            }
            if (extras_.containsKey(ACTION_TYPE_EXTRA)) {
                actionType = extras_.getInt(ACTION_TYPE_EXTRA);
                Logger.i("创建群 actionType:" + actionType);
            }
            if (extras_.containsKey(ROOM_ID_EXTRA)) {
                roomId = extras_.getString(ROOM_ID_EXTRA);
                Logger.i("创建群 roomId:" + roomId);
            }
            if (extras_.containsKey(M_NOT_CHANGE_IDS_EXTRA)) {
                mNotChangeIds = ((List<String>) extras_.getSerializable(M_NOT_CHANGE_IDS_EXTRA));
                Logger.i("创建群 mNotChangeIds:" + mNotChangeIds);
            }
            if (extras_.containsKey(KEY_SELECTED_USER)) {
                String temp = extras_.getString(KEY_SELECTED_USER);
                if (!TextUtils.isEmpty(temp))
                    selectedUserIds = temp.replace("_", ".").split("-");
                Logger.i("创建群 selectedUserIds:" + selectedUserIds);
            }
        }
    }

    private void bindViews() {

        hlv_selected_contacts = (com.qunar.im.ui.view.HorizontalListView) findViewById(R.id.hlv_selected_contacts);
        lv_all_contacts = (com.handmark.pulltorefresh.library.PullToRefreshListView) findViewById(R.id.lv_all_contacts);
        btn_create = (TextView) findViewById(R.id.btn_create);
        search_panel = (LinearLayout) findViewById(R.id.search_panel);
        lv_search_contacts = (ListView) findViewById(R.id.lv_search_contacts);
        btn_create.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_invitation_chatroom);
        //bugly tag
//        CrashReportUtils.getInstance().setUserTag(55096);
        bindViews();
        injectExtras();
        mInvationAdapter = new InvitationAdapter(this);
        mInvitedFriendsPresenter = new InvitedFriendsPresenter(this);
        mInvitedFriendsPresenter.setInvitedFriendsView(this);
        mSelectedNodes = new ArrayList<>();
        if (!TextUtils.isEmpty(userId)) {
            mNotChangeIds.add(userId);
            Node node = new Node();
            node.setKey(userId);
            node.setXmppId(userId);
            mSelectedNodes.add(node);
        }
        if (selectedUserIds != null && selectedUserIds.length > 0) {
            for (String str : selectedUserIds) {
                str = QtalkStringUtils.userId2Jid(str);
                if (!str.equals(userId)) {
                    Node node = new Node();
                    node.setKey(str);
                    node.setName(ProfileUtils.getNickByKey(str));
                    mSelectedNodes.add(node);
                }
            }
        }
        mInviteToChatroomHorizonalListviewAdapter = new InviteToChatroomHorizonalListviewAdapter(this, new ICheckboxClickedListener() {
            @Override
            public void CheckobxClicked() {
                mTreeAdapter.notifyDataSetChanged();
                mInviteToChatroomHorizonalListviewAdapter.notifyDataSetChanged();
            }
        });
        mInviteToChatroomHorizonalListviewAdapter.setSelectedNodes(mSelectedNodes);
        mInviteToChatroomHorizonalListviewAdapter.setNoChangeIds(mNotChangeIds);
        if (actionType == ACTION_TYPE_CHREATE_GROUP) {
            mChatroomCreatedPresenter = new ChatroomCreatedPresenter();
            mChatroomCreatedPresenter.setView(new IChatroomCreatedView() {
                String cName = null;

                @Override
                public String getSubject() {
                    return "";
                }

                @Override
                public String getChatrooName() {
                    StringBuilder chatroomName = new StringBuilder();
                    int len = Math.min(3, mSelectedNodes.size());
                    for (int i = 0; i < len; i++) {
                        String othername = mSelectedNodes.get(i).getName();
                        if (TextUtils.isEmpty(othername)) {
                            othername = QtalkStringUtils.parseLocalpart(mSelectedNodes.get(i).getKey());
                        }
                        if (CurrentPreference.getInstance().getPreferenceUserId().equalsIgnoreCase(othername) ||
                                CurrentPreference.getInstance().getUserid().equalsIgnoreCase(othername))
                            continue;
                        // 只添加不是自己的人
                        chatroomName.append(othername);
                        chatroomName.append(",");
                    }
                    // 最后追加上自己的名字，方便搜索
                    chatroomName.append(CurrentPreference.getInstance().getPreferenceUserId());

                    cName = chatroomName.toString();
                    return cName;
                }

                @Override
                public boolean isPersist() {
                    return true;
                }

                @Override
                public void setResult(boolean isSuccess, final String roomId) {
                    if (isSuccess) {
                        //创建群成功后设置群id
                        ChatroomInvitationActivity.this.roomId = roomId;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (progressDialog != null) {
                                    progressDialog.dismiss();
                                }
//                                mProgressDialog.dismiss();
                                if (mSelectedNodes.size() > 0) {


                                    AlertDialog.Builder dialog = new AlertDialog.Builder(ChatroomInvitationActivity.this);
                                    commonDialog.setTitle(R.string.atom_ui_tip_dialog_prompt);
                                    commonDialog.setMessage(getString(R.string.atom_ui_tip_join_group_inquiry_message));
                                    commonDialog.setPositiveButton(getString(R.string.atom_ui_common_confirm), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            progressDialog.show();
                                            mInvitedFriendsPresenter.invited();
                                            dialog.dismiss();
                                        }
                                    });
                                    commonDialog.setNegativeButton(getString(R.string.atom_ui_common_cancel), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    if (!isFinishing()) {
                                        commonDialog.show();
                                    }

                                }
                            }
                        });

                        //创建成功了
                    }
//                    if (isSuccess) {
//                        ChatroomInvitationActivity.this.roomId = roomId;
//                        // 邀请人
//                        mInvitedFriendsPresenter.invited();
//                        // 有转发请求发个转发请求
//                        boolean isSelectTransUser = getIntent().getBooleanExtra(Constants.BundleKey.IS_TRANS, false);
//                        IMMessage transMsg = null;
//                        if (isSelectTransUser) {
//                            transMsg = (IMMessage) getIntent().getSerializableExtra(Constants.BundleKey.TRANS_MSG);
//                        }
//                        if (null != transMsg) {
//                            EventBus.getDefault().
//                                    post(new EventBusEvent.SendTransMsg(transMsg,roomId));
//                        }
//                        getHandler().post(new Runnable() {
//                            @Override
//                            public void run() {
//                                Intent intent = new Intent(ChatroomInvitationActivity.this,PbChatActivity.class);
//                                intent.putExtra("jid",roomId);
//                                intent.putExtra("isFromChatRoom", true);
//                                ChatroomInvitationActivity.this.startActivity(intent);
//                            }
//                        });
//                    }
//                    else {
//                        getHandler().post(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(ChatroomInvitationActivity.this, R.string.atom_ui_create_chatroom_failed, Toast.LENGTH_SHORT).show();
//                                ChatroomInvitationActivity.this.setResult(false);
//                            }
//                        });
//                    }
                }
            });
        }
        initViews();
    }

    @Override
    protected void onDestroy() {
        mInvitedFriendsPresenter.release();
        super.onDestroy();
    }

    void initViews() {
        lv_all_contacts.setMode(PullToRefreshBase.Mode.MANUAL_REFRESH_ONLY);
        //初始化搜索框,这里就是actionBar的初始化位置
        searchBar = (QtSearchActionBar) this.findViewById(R.id.my_action_bar);
        setSupportActionBar(searchBar);
        searchBar.getSearchView().changeQueryHint((String) getText(R.string.atom_ui_tip_search_and_choose));
        searchBar.getSearchView().requestFocus();
        //当文字改变时,去查询各个人
        searchBar.getSearchView().setOnQueryChangeListener(new MySearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mInvationAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mInvationAdapter.getFilter().filter(newText);
                return false;
            }
        });
        searchBar.getLeftLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        searchBar.getDeleteText().setVisibility(View.VISIBLE);
        searchBar.getDeleteText().setText(R.string.atom_ui_common_confirm);
        searchBar.getDeleteText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionByOptional();
            }
        });
        lv_search_contacts.setAdapter(mInvationAdapter);

        mInvationAdapter.setSearchResultChangeListner(new InvitationAdapter.SearchResultChangeListerner() {
            @Override
            public void onSearchChange(int count) {
                search_panel.setVisibility(0 == count ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onSelected(Nick item) {
                Node node = new Node();
                node.setKey(item.getXmppId());
                Logger.i("选中的人的nick:" + new Gson().toJson(item));
                node.setHeaderSrc(item.getHeaderSrc());
//                node.setXmppId(item.getXmppId());
                if (!mSelectedNodes.contains(node)) {
                    mSelectedNodes.add(node);
                    mInviteToChatroomHorizonalListviewAdapter.notifyDataSetChanged();
                    mTreeAdapter.notifyDataSetChanged();
                }
                resetSelectedPanel();
            }
        });

        // 头像功能
        InvitationAdapter.ProtrailHandle handle = new InvitationAdapter.ProtrailHandle() {
            @Override
            public void onLoadUserProtrailLocal(final SimpleDraweeView imageView, final String jid, final String headerSrc) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //old
//                        ProfileUtils.displayGravatarByImageSrc(jid, headerSrc, imageView);
                        //new
                        ProfileUtils.displayGravatarByImageSrc(ChatroomInvitationActivity.this, headerSrc, imageView,
                                ChatroomInvitationActivity.this.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size), ChatroomInvitationActivity.this.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
                    }
                });

            }
        };
        mInvationAdapter.setProtrailHandle(handle);

        if (actionType == ACTION_TYPE_CHREATE_GROUP) {
//            btn_create.setText("创建并邀请群成员");
        } else if (actionType == ACTION_TYPE_INVITATION) {
//            btn_create.setText("邀请以上成员");
        } else if (actionType == ACTION_TYPE_MULTI_SEL) {
        }
//            btn_create.setText("选好了");
        btn_create.setText(R.string.atom_ui_common_confirm);
        // 二人会话升级的群需要默认把对方加上
        if (!TextUtils.isEmpty(userId)) {
            UserVCard vCard = ProfileUtils.getLocalVCard(userId);
            DepartmentItem item = new DepartmentItem();
            item.fullName = vCard.nickname;
            item.userId = userId;
        }
        try {
            mTreeAdapter = new InviteTreeAdapter(lv_all_contacts, this, new ICheckboxClickedListener() {
                @Override
                public void CheckobxClicked() {
                    mInviteToChatroomHorizonalListviewAdapter.notifyDataSetChanged();
                }
            });
        } catch (IllegalAccessException e) {
            LogUtil.e(TAG, "ERROR", e);
        }
        hlv_selected_contacts.setAdapter(mInviteToChatroomHorizonalListviewAdapter);
        mInvitedFriendsPresenter.loadTargetContacts();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getText(R.string.atom_ui_tip_loading));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    @Override
    public void initTreeView(Map<Integer, List<Node>> mAllNodes) {
        mTreeAdapter.setNodes(mAllNodes);
        mTreeAdapter.setSelectedList(mSelectedNodes);
        mTreeAdapter.setNotChangeList(mNotChangeIds);
        lv_all_contacts.setAdapter(mTreeAdapter);
    }

    @Override
    public void setResult(final boolean b) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if (b) {
//                    AlertDialog.Builder dialog = new AlertDialog.Builder(ChatroomInvitationActivity.this);
                    commonDialog.setTitle(R.string.atom_ui_tip_dialog_prompt);
                    commonDialog.setMessage(getString(R.string.atom_ui_tip_join_group_invite_success));
                    commonDialog.setNegativeButton(null,null);
                    commonDialog.setPositiveButton(R.string.atom_ui_common_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(ChatroomInvitationActivity.this, PbChatActivity.class);
                            intent.putExtra(PbChatActivity.KEY_JID, roomId);
                            intent.putExtra(PbChatActivity.KEY_REAL_JID, roomId);
                            intent.putExtra(PbChatActivity.KEY_CHAT_TYPE, "1");
                            intent.putExtra(PbChatActivity.KEY_IS_CHATROOM, true);
                            startActivity(intent);
                            setResult(RESULT_OK);
                            ChatroomInvitationActivity.this.finish();

                    }
                });
//                    dialog.setNegativeButton("再想想", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
                if (!isFinishing()) {
                    commonDialog.show();
                }
//                    dialog.show();
            }
        }
    });

}

    @Override
    public void onStart() {
        super.onStart();
        //加载所有联系人
        loadAllContactsOnBackground();
    }

    void loadAllContactsOnBackground() {
        BackgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mInvitedFriendsPresenter.loadAllContacts();
            }
        });
    }

    @Override
    public List<Node> getSelectedFriends() {
        return mSelectedNodes;
    }

    @Override
    public void setAllContacts(final List<Nick> contacts) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                mInvationAdapter.setAllContacts(contacts);
            }
        });
    }

    void actionByOptional() {
        progressDialog.show();
        if (ACTION_TYPE_CHREATE_GROUP == actionType) {
            //创建群组 彬彬邀请成员
            mChatroomCreatedPresenter.createChatroom();
        } else if (ACTION_TYPE_INVITATION == actionType) {
            mInvitedFriendsPresenter.invited();
        } else if (ACTION_TYPE_MULTI_SEL == actionType) {
            progressDialog.dismiss();
            List<Node> list = getSelectedFriends();
            List<Map<String, String>> results = new ArrayList<>();
            for (Node node : list) {
                Map<String, String> map = new HashMap<>();
                map.put("userId", node.getKey());
                map.put("nick", node.getName());
                results.add(map);
            }
            Intent resultIntent = new Intent();
            resultIntent.putExtra(KEY_SELECTED_USER, JsonUtils.getGson().toJson(results));
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }

    @Override
    public String getRoomId() {
        return roomId;
    }

    @Override
    public String getFullName() {
        return CurrentPreference.getInstance().getFullName();
    }

    public void resetSelectedPanel() {
        searchBar.getSearchView().setQuery("", false);
        search_panel.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_create) {
            actionByOptional();
        }
    }


public interface ICheckboxClickedListener {
    void CheckobxClicked();
}
}
