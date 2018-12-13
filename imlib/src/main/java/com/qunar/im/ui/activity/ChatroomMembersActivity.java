package com.qunar.im.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.base.module.ChatRoomMember;
import com.qunar.im.base.module.GroupMember;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.module.RecentConversation;
import com.qunar.im.base.presenter.IChatingPanelPresenter;
import com.qunar.im.base.presenter.IChatroomInfoPresenter;
import com.qunar.im.base.presenter.impl.ChatingPanelPresenter;
import com.qunar.im.base.presenter.impl.ChatroomInfoPresenter;
import com.qunar.im.base.presenter.views.IChatRoomInfoView;
import com.qunar.im.base.presenter.views.IChatingPanelView;
import com.qunar.im.base.presenter.views.IShowNickView;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.base.util.Utils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.MembersGridAdapter;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;
import com.qunar.im.utils.QtalkStringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 聊天详情页
 * Modify by xinbo.wang on 2015/2/10
 */
public class ChatroomMembersActivity extends SwipeBackActivity implements IChatRoomInfoView, View.OnClickListener, IChatingPanelView, IShowNickView {
    private static final int SHOW_JUMP_VIEW_IMMEDIATELY = 0;
    private static final int HIDE_JUMP_VIEW_DELAY = 1;
    public static final String IS_FROM_GROUP = "isFromChatRoom";
    public static final String JID = "jid";
    public static final String KEY_CHAT_TYPE = "chatType";
    private List<String> mMembersId;
    private List<GroupMember> mMembers;


    TextView dontJoin;
    TextView recordOfChatroom;
    TextView chatSubject;
    //secondFooterView 单人群组共有功能,footerView群专有功能
    View secondFooterView, footerView;
    TextView showQRCode;
    TextView membersCountView;
    TextView cm_name;
    Switch switchTopProity;
    Switch switchDnd, switchShowNick;
    ListView gv_chatroom_members;
//    ImageView img_group_setting;

    private RecentConversation recentConversation;

    //当前会话id
    String jid;
    String realJid;
    String chatType;
    //是否是群
    boolean isFromGroup;
    MembersGridAdapter adapter;
    boolean inited;
    boolean footerViewShown = false;
    IChatroomInfoPresenter presenter;
    IChatingPanelPresenter panelPresenter;
    Nick mChatNick;
    int myLevel = 4;

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_chat_members);
        bindView();
        injectExtras();

//        EventBus.getDefault().register(this);
        //初始化presenter
        presenter = new ChatroomInfoPresenter();
        presenter.setView(this);
        panelPresenter = new ChatingPanelPresenter();
        panelPresenter.setPanelView(this);
        panelPresenter.setShowNickView(this);
        //初始化view
        initViews();
    }

    @Override
    public void onDestroy() {
//        EventBus.getDefault().unregister(this);
        presenter.close();
        super.onDestroy();
    }

    private void bindView() {
//        img_group_setting = (ImageView) findViewById(R.id.img_group_setting);
        gv_chatroom_members = (ListView) findViewById(R.id.gv_chatroom_members);
    }

    //获取intent中信息, 传入了jid 和 是否是群
    private void injectExtras() {

        Bundle extras_ = getIntent().getExtras();
        if (extras_ != null) {
            if (extras_.containsKey(JID)) {
                jid = extras_.getString(JID);

            }
            if(extras_.containsKey(PbChatActivity.KEY_REAL_JID)){
                realJid = extras_.getString(PbChatActivity.KEY_REAL_JID);
            }
            if(extras_.containsKey(KEY_CHAT_TYPE)){
                chatType = extras_.getString(KEY_CHAT_TYPE);
            }

            if (extras_.containsKey(IS_FROM_GROUP)) {
                isFromGroup = extras_.getBoolean(IS_FROM_GROUP);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFromGroup) {
            //如果是群组 获取群成员
            getLatestMembers();
        }
    }

    void initViews() {
//        QtActionBar actionBar = (QtActionBar) this.findViewById(R.id.my_action_bar);
//        setActionBar(actionBar);
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        if (!inited) {
//            myActionBar.getTitleTextview().setText(R.string.atom_ui_chatroom_member_title);
            setActionBarTitle(R.string.atom_ui_chat_title_detail);
            if (adapter == null) {
                adapter = new MembersGridAdapter(this);
                if (isFromGroup) {
                    initFootview();
                } else {
                    initSingleFootview();
                }
                //设置检查成员信息单击事件,开启新的activity
                adapter.setCheckeMemberInfoListener(new MembersGridAdapter.CheckeMemberInfoListener() {
                    @Override
                    public void onClick(GroupMember member) {
                        Intent intent = new Intent(ChatroomMembersActivity.this,
                                PersonalInfoActivity.class);
                        intent.putExtra("isHideBtn", false);
                        intent.putExtra("jid", member.getMemberId());
                        ChatroomMembersActivity.this.startActivity(intent);
                    }
                });
//                //设置头像
//                adapter.setGravatarHandler(new MembersGridAdapter.GravatarHandler() {
//                    @Override
//                    public void requestGravatarEvent(final String userJid, final SimpleDraweeView view) {
//                        ProfileUtils.displayGravatarByUserId(userJid, view);
//
//                    }
//                });
                //设置删除群成员
                adapter.setDelMemberListener(new MembersGridAdapter.DelMemberListener() {
                    @Override
                    public void onClick() {
                        Intent intent = new Intent(ChatroomMembersActivity.this, ChatroomMemeberManagerActivity.class);
                        intent.putExtra("jid", jid);
                        intent.putExtra(ChatroomMemeberManagerActivity.GROUP_LEVEL, myLevel);
                        startActivity(intent);
                    }
                });
                if (CommonConfig.isQtalk || CommonConfig.showQchatGroup) {
                    //设置添加新成员
                    adapter.setAddNewMemberListener(new MembersGridAdapter.AddNewMemberListener() {
                        @Override
                        public void onClick() {
                            if (isFromGroup) {
                                invitedFriends();
                            } else {
                                Intent intent = new Intent(ChatroomMembersActivity.this,
                                        ChatroomInvitationActivity.class);
                                intent.putExtra("roomId", jid);
                                intent.putExtra("userId", jid);//QtalkStringUtils.parseLocalpart(jid)
                                intent.putExtra("actionType", ChatroomInvitationActivity.ACTION_TYPE_CHREATE_GROUP);
                                ChatroomMembersActivity.this.startActivityForResult(intent, ChatroomInvitationActivity.ACTION_TYPE_CHREATE_GROUP);
                            }
                        }
                    });
                }
                gv_chatroom_members.setAdapter(adapter);
            }
            if (isFromGroup) {
//                getLatestMembers();
                loadInfo();
            } else {
                presenter.showSingler();
            }

            panelPresenter.showIsNick();
            inited = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case ChatroomInvitationActivity.ACTION_TYPE_CHREATE_GROUP:
                if(resultCode==RESULT_OK){
                    this.finish();
                }
                break;
        }
    }

    //初始化群,单人共有功能
    private void initSingleFootview() {
        secondFooterView = LayoutInflater.from(this).inflate(R.layout.atom_ui_chatroom_members_second_footerview, null, false);
        TextView tvClearChatHistory = (TextView) secondFooterView.findViewById(R.id.tv_clear_chat_history);
        TextView tvLookupFromHistory = (TextView) secondFooterView.findViewById(R.id.tv_lookup_from_history);
        switchTopProity = (Switch) secondFooterView.findViewById(R.id.switch_top_proity);
        switchDnd = (Switch) secondFooterView.findViewById(R.id.switch_dnd);
        switchShowNick = (Switch) secondFooterView.findViewById(R.id.switch_shownick);
        RelativeLayout relativeShowNick = (RelativeLayout) secondFooterView.findViewById(R.id.rl_show_nick);
        gv_chatroom_members.addFooterView(secondFooterView);
        tvClearChatHistory.setOnClickListener(this);
        //暂时屏蔽清除历史记录功能
        tvLookupFromHistory.setOnClickListener(this);
        //会话类型为 4583的没有置顶选项
        if (!("1".equals(chatType)||"0".equals(chatType)||"3".equals(chatType))){
            secondFooterView.findViewById(R.id.top_rl).setVisibility(View.GONE);
            secondFooterView.findViewById(R.id.top_line).setVisibility(View.GONE);
        }

        //当不是群组时,没有设置显示群成员名称选项
        if (!isFromGroup) {
            relativeShowNick.setVisibility(View.GONE);
        }
        //由于进来要设置好开关,所以在设置监听前设置好
        panelPresenter.showIsTop();
        switchTopProity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                panelPresenter.topMessage();
                panelPresenter.setConversationTopOrCancel();
            }
        });
        panelPresenter.showIsDnd();
        switchDnd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //这个用于设置提醒不提醒ss
                panelPresenter.setConversationReMindOrCancel();
                panelPresenter.dnd();
                Intent intent = new Intent();
                intent.setAction(CommonConfig.isQtalk?"com.qunar.corp.ops.amd.RELOAD_CONFIG"
                        :"com.qunar.qchat.corp.ops.amd.RELOAD_CONFIG");
                Utils.sendLocalBroadcast(intent, ChatroomMembersActivity.this);
            }
        });
        switchShowNick.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                panelPresenter.nick(isChecked);
            }
        });
    }
    //初始化群独有功能
    private void initFootview() {
        initSingleFootview();
        footerView = LayoutInflater.from(this).inflate(R.layout.atom_ui_chatroom_members_footerview, null, false);
        dontJoin = (TextView) footerView.findViewById(R.id.exit_chatroom);
        recordOfChatroom = (TextView) footerView.findViewById(R.id.cloud_record_of_chat);
        chatSubject = (TextView) footerView.findViewById(R.id.cm_subject);
        showQRCode = (TextView) footerView.findViewById(R.id.show_qr_code);
        membersCountView = (TextView) footerView.findViewById(R.id.cm_member_count);
        cm_name = (TextView) footerView.findViewById(R.id.cm_name);
        gv_chatroom_members.addFooterView(footerView);
        dontJoin.setOnClickListener(this);
        recordOfChatroom.setOnClickListener(this);
        showQRCode.setOnClickListener(this);
        cm_name.setOnClickListener(this);
        chatSubject.setOnClickListener(this);
    }

    void getLatestMembers() {
        presenter.showMembers(false);
    }

    //显示群组成员列表
    public void showGroupMember(){

    }

    void loadInfo() {
        BackgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                presenter.showInfo();
            }
        });
    }

    @Override
    public void setMemberList(final List<GroupMember> members, final int level, final boolean enforce) {
        mMembers = members;
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                myLevel = level;
                if (level == ChatRoomMember.OWNER) {
                    dontJoin.setText(getString(R.string.atom_ui_chat_destroy_muc));
                    dontJoin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            CommonDialog.Builder commonDialog = new CommonDialog.Builder(ChatroomMembersActivity.this);
                            commonDialog.setTitle(R.string.atom_ui_tip_dialog_prompt);
                            commonDialog.setMessage(R.string.atom_ui_tip_destruction_group);
                            commonDialog.setPositiveButton(R.string.atom_ui_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    presenter.destroy();
                                    dialog.dismiss();
                                }
                            });
                            commonDialog.setNegativeButton(R.string.atom_ui_cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            commonDialog.create().show();

//                            presenter.leave();
                        }
                    });
                }
                adapter.setMembers(members, level,enforce);
                adapter.notifyDataSetChanged();
                setUpGroupSetting();
            }
        });
    }

    void showQRCOdeActivity() {
        Intent intent = new Intent(ChatroomMembersActivity.this,
                QRActivity.class);
        intent.putExtra("qrString", Constants.Config.QR_SCHEMA + "://group?id=" + jid);
        startActivity(intent);
    }

    void cloud_record_of_chat() {
        String roomName = QtalkStringUtils.parseBareJid(jid);
        Intent intent = new Intent(ChatroomMembersActivity.this,
                CloudChatRecordActivity.class);
        intent.putExtra("toId", roomName);
        intent.putExtra("fullName", roomName);
        intent.putExtra("isFromGroup", true);
        startActivity(intent);
    }

    //更改群名称
    void changeGroupName() {
        if (mChatNick == null) return;
        View contentView = LayoutInflater.from(this).inflate(R.layout.atom_ui_dialog_change_group_name, null);
        final EditText et = (EditText) contentView.findViewById(R.id.et_group_name);
        et.setText(mChatNick.getName());
        new AlertDialog.Builder(this)
                .setTitle(R.string.atom_ui_group_name)
                .setView(contentView)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (et.getText().length() > 99) {
                            Toast.makeText(ChatroomMembersActivity.this, R.string.atom_ui_tip_group_name_lenth, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (TextUtils.isEmpty(et.getText())) {
                            Toast.makeText(ChatroomMembersActivity.this, R.string.atom_ui_tip_group_name_empty, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mChatNick.setName(et.getText().toString());
                        presenter.updataMucInfo();
                    }

                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    //更改群公告
    void changeGroupNotice() {
        if (mChatNick == null) return;
        View contentView = LayoutInflater.from(this).inflate(R.layout.atom_ui_dialog_change_group_name, null);
        final EditText et = (EditText) contentView.findViewById(R.id.et_group_name);
        et.setLines(3);
        et.setText(mChatNick.getTopic());
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.atom_ui_chat_group_topic))
                .setView(contentView)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (TextUtils.isEmpty(et.getText())) {
                            Toast.makeText(ChatroomMembersActivity.this, R.string.atom_ui_tip_group_name_empty, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mChatNick.setTopic(et.getText().toString());
                        presenter.updataMucInfo();
                    }

                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    //清空历史记录
    void clearChatHistory() {
        commonDialog
                .setTitle(R.string.atom_ui_tip_dialog_prompt)
                .setMessage("你确定要清除历史记录吗?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.clearHistoryMsg();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    void invitedFriends() {
        mMembersId = new ArrayList<>();
        for (GroupMember member : mMembers) {
            mMembersId.add(QtalkStringUtils.parseBareJid(member.getMemberJid()));
        }
        Intent intent = new Intent(ChatroomMembersActivity.this, ChatroomInvitationActivity.class);
        intent.putExtra("roomId", jid);
        intent.putExtra("mNotChangeIds", (Serializable) mMembersId);
        intent.putExtra("actionType", ChatroomInvitationActivity.ACTION_TYPE_INVITATION);
        startActivity(intent);
    }

    private void lookupFromHistory() {
        Intent intent = new Intent(this, SearchChatingActivity.class);
        intent.putExtra("jid", jid);
        startActivity(intent);
//        finish();
    }


    @Override
    public String getRoomId() {
        return jid;
    }

    @Override
    public void closeActivity() {
        ChatroomMembersActivity.this.finish();
    }

    @Override
    public void setMemberCount(final int count) {

        getHandler().post(new Runnable() {
            @Override
            public void run() {
                membersCountView.setText(String.valueOf(count));
//                myActionBar.getTitleTextview().setText(getContext().getString(R.string.atom_ui_chatroom_member_title) + "(" + count + ")");
                setActionBarTitle(getContext().getString(R.string.atom_ui_chat_title_detail) + "(" + count + ")");
            }
        });
    }

    @Override
    public void setExitResult(boolean re) {
        if (re) {
            EventBus.getDefault().post(new EventBusEvent.KinckoffChatroom(jid));
            this.finish();
        }
    }

    @Override
    public void setJoinResult(boolean re, String mesg) {

    }

    @Override
    public void setUpdateResult(final boolean re, final String msg) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (re) {
                    cm_name.setText(mChatNick.getName());
                    chatSubject.setText(mChatNick.getTopic());
                } else {
                    mChatNick.setName(cm_name.getText().toString());
                }
                Toast.makeText(ChatroomMembersActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public Nick getChatroomInfo() {
        return mChatNick;
    }

    @Override
    public void setChatroomInfo(final Nick nick) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(nick!=null){
                    mChatNick = nick;
                    cm_name.setText(nick.getName());
                    chatSubject.setText(nick.getTopic());
                }else{

                }

            }
        });

//        getHandler().post(new Runnable() {
//            @Override
//            public void run() {

//            }
//        });

    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.exit_chatroom) {
            showLeaveGroupTip();
        } else if (i == R.id.show_qr_code) {
            showQRCOdeActivity();

        } else if (i == R.id.cloud_record_of_chat) {
            cloud_record_of_chat();

        } else if (i == R.id.cm_name) {
            changeGroupName();

        } else if (i == R.id.tv_clear_chat_history) {
            clearChatHistory();

        } else if (i == R.id.tv_lookup_from_history) {
            lookupFromHistory();

        }else if( i == R.id.cm_subject){
            changeGroupNotice();
        }
    }

    private void setSettingDrawable(boolean footerViewShown) {
        if (footerViewShown != this.footerViewShown) {
            this.footerViewShown = footerViewShown;
//            if (footerViewShown) {
//                img_group_setting.setImageResource(R.drawable.atom_ui_iconfont_arrowup);
//            } else {
//                img_group_setting.setImageResource(R.drawable.atom_ui_iconfont_arrowdown);
//            }
        }
    }

    private void setUpGroupSetting() {
//        if (!isFromGroup) {
//            img_group_setting.setVisibility(View.GONE);
//        } else {
//            footerViewShown = gv_chatroom_members.getCount() < 8;
//            if (footerViewShown) {
//                groupSetting.setVisibility(View.GONE);
//            } else {
//                groupSetting.setVisibility(View.VISIBLE);
//            }
//            img_group_setting.setVisibility(View.GONE);
//            img_group_setting.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (footerViewShown) {
//                        //跳到第一项
//                        setSettingDrawable(false);
//                        gv_chatroom_members.setSelection(0);
//                    } else {
//                        //跳到设置页
//                        setSettingDrawable(true);
//                        gv_chatroom_members.setSelection(gv_chatroom_members.getCount() - 2);
//                    }
//                }
//            });
//        }
        if (isFromGroup && !footerViewShown) {
            final Handler handler = new Handler(new HandlerCallBack());
            gv_chatroom_members.setOnScrollListener(new AbsListView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if (scrollState == SCROLL_STATE_FLING) {
//                        img_group_setting.setVisibility(View.VISIBLE);
                        handler.sendEmptyMessage(SHOW_JUMP_VIEW_IMMEDIATELY);
                    } else if (scrollState == SCROLL_STATE_IDLE) {
//                        img_group_setting.setVisibility(View.VISIBLE);
                        handler.sendEmptyMessageDelayed(HIDE_JUMP_VIEW_DELAY, 5000);
                    } else if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
//                        img_group_setting.setVisibility(View.VISIBLE);
                        handler.sendEmptyMessage(SHOW_JUMP_VIEW_IMMEDIATELY);
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                    LogUtil.d(TAG, "list view top" + gridView.getTop() + "height" + gridView.getHeight() + "bottom" + gridView.getBottom());
//                    LogUtil.d(TAG, "footerView: shown:" + footerView.isShown() + "enable:" + footerView.isEnabled() + "top:" + footerView.getTop());
//                    LogUtil.d(TAG, "2footerView: shown:" + secondFooterView.isShown() + "enable:" + secondFooterView.isEnabled() + "top:" + secondFooterView.getTop());
                    setSettingDrawable(footerView.isShown() || secondFooterView.isShown());
                }
            });
        }
    }



    @Override
    public String getJid() {
        return jid;
    }

    @Override
    public String getRJid() {
        return realJid;
    }

    @Override
    public String getRealJid() {
        return realJid;
    }

    @Override
    public String getChatType() {
        return chatType;
    }

    @Override
    public boolean getTop() {
        return switchTopProity.isChecked();
    }

    @Override
    public void setReMind(boolean isReMind) {

    }

    @Override
    public boolean getReMind() {
        return false;
    }

    @Override
    public void setTop(boolean isTop) {
        switchTopProity.setChecked(isTop);
    }

    @Override
    public boolean getDnd() {
        return switchDnd.isChecked();
    }

    @Override
    public void setDnd(boolean isDnd) {
        switchDnd.setChecked(isDnd);
    }

    @Override
    public boolean getShowNick() {
        return switchShowNick.isChecked();
    }

    @Override
    public void setShowNick(boolean showNick) {
        switchShowNick.setChecked(showNick);
    }

    private class HandlerCallBack implements Handler.Callback {
        private boolean hide = true;

        @Override
        public boolean handleMessage(Message msg) {
//            if (img_group_setting != null && img_group_setting.isEnabled()) {
//                switch (msg.what) {
//                    case SHOW_JUMP_VIEW_IMMEDIATELY:
//                        hide = true;
//                        break;
//                    case HIDE_JUMP_VIEW_DELAY:
//                        if (hide) {
//                            img_group_setting.setVisibility(View.GONE);
//                        }
//                        hide = true;
//                        break;
//                }
//            }
            return true;
        }
    }

    public void onEventMainThread(EventBusEvent.KinckoffChatroom kinckoffChatroom) {
        if (jid.equals(kinckoffChatroom.roomId)) {
            finish();
        }
    }

    public void onEventMainThread(EventBusEvent.restartChat restartChat) {
        this.finish();
    }

    //当点击回退按钮时
    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
//        finish();onBackPress就会调用finish 只finish的话 没有页面关闭动画
        super.onBackPressed();
    }

    private void showLeaveGroupTip(){
        commonDialog.setTitle(R.string.atom_ui_tip_dialog_prompt)
                .setMessage(R.string.atom_ui_group_quit_tips)
                .setPositiveButton(R.string.atom_ui_common_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        presenter.leave();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.atom_ui_common_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }
}
