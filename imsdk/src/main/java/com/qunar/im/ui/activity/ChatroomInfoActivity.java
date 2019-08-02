package com.qunar.im.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.qunar.im.base.module.GroupMember;
import com.qunar.im.base.module.Nick;
import com.qunar.im.ui.presenter.IChatroomInfoPresenter;
import com.qunar.im.ui.presenter.impl.ChatroomInfoPresenter;
import com.qunar.im.ui.presenter.views.IChatRoomInfoView;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.QtNewActionBar;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by xinbo.wang on 2015/2/12.
 */
public class ChatroomInfoActivity extends IMBaseActivity implements IChatRoomInfoView,
        View.OnClickListener {
    TextView chatroom_cancel;
    TextView chatroom_join;
    TextView cm_name;

    String roomId;

    Nick mChatRoom;

    IChatroomInfoPresenter presenter;
    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_muc_info);
        presenter = new ChatroomInfoPresenter();
        presenter.setView(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.atom_ui_tip_dialog_prompt);
        progressDialog.setMessage(getText(R.string.atom_ui_title_join_group));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        bindViews();
        injectExtras();
        initViews();
    }

    private void injectExtras() {
        Bundle extras_ = getIntent().getExtras();
        if (extras_ != null) {
            if (extras_.containsKey("roomId")) {
                roomId = extras_.getString("roomId");
            }
        }
    }

    private void bindViews() {
        cm_name = (TextView) findViewById(R.id.cm_name);
        chatroom_join = (TextView) findViewById(R.id.chatroom_join);
        chatroom_cancel = (TextView) findViewById(R.id.chatroom_cancel);
        chatroom_join.setOnClickListener(this);
        chatroom_cancel.setOnClickListener(this);
    }

    void initViews() {
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);

        setActionBarTitle(R.string.atom_ui_title_join_group);
    }

    void joinChatRoom() {
        progressDialog.show();
        presenter.joinChatRoom();
    }

    void closeCurWin() {
        this.finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.showInfo();
    }

    @Override
    public void setMemberList(List<GroupMember> members, int myPowerLevel, boolean enforce) {

    }

    @Override
    public String getRoomId() {
        return roomId;
    }

    @Override
    public void closeActivity() {
        ChatroomInfoActivity.this.finish();
    }

    @Override
    public void setChatroomInfo(final Nick room) {
        mChatRoom = room;
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                cm_name.setText(room.getName());
            }
        });
    }

    @Override
    public void setMemberCount(int count) {

    }

    @Override
    public void setExitResult(boolean re) {

    }

    @Override
    public String getRealJid() {
        return null;
    }

    @Override
    public String getChatType() {
        return null;
    }

    @Override
    public void setJoinResult(final boolean re, final String mesg) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                Toast.makeText(ChatroomInfoActivity.this, mesg, Toast.LENGTH_SHORT).show();
                if (re) {
                    Intent intent = new Intent(ChatroomInfoActivity.this, PbChatActivity.class);
                    intent.putExtra(PbChatActivity.KEY_JID, roomId);
                    intent.putExtra(PbChatActivity.KEY_IS_CHATROOM, true);
                    startActivity(intent);
                    EventBus.getDefault().post(new EventBusEvent.RefreshChatroom("", ""));
                }
                finish();
            }
        });
    }

    @Override
    public void setUpdateResult(boolean re, String msg) {

    }

    @Override
    public Nick getChatroomInfo() {
        return mChatRoom;
    }

    @Override
    public Context getContext() {
        return this.getApplicationContext();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.chatroom_join) {
            joinChatRoom();

        } else if (i == R.id.chatroom_cancel) {
            closeCurWin();

        }
    }
}
