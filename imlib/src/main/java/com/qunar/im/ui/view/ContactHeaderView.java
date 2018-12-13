package com.qunar.im.ui.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.qunar.im.common.CommonConfig;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.ChatRoomActivity;
import com.qunar.im.ui.activity.DepartmentActivity;
import com.qunar.im.ui.activity.RobotListActivity;



public class ContactHeaderView extends LinearLayout {
    RelativeLayout layout_noread_msg,layout_org,layout_friend,layout_chatroom,layout_robot;

    public ContactHeaderView(Context context) {
        this(context, null);
    }

    public ContactHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContactHeaderView(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.atom_ui_layout_friends_header,this,true);
        layout_chatroom = (RelativeLayout) findViewById(R.id.layout_chatroom);
        layout_robot = (RelativeLayout) findViewById(R.id.layout_robot);
        layout_org = (RelativeLayout) findViewById(R.id.layout_org);
        layout_noread_msg = (RelativeLayout) findViewById(R.id.layout_noread_msg);
        layout_friend = (RelativeLayout) findViewById(R.id.layout_friend);
        layout_chatroom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(context, ChatRoomActivity.class);
                context.startActivity(intent);
            }
        });
        layout_robot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RobotListActivity.class);
                context.startActivity(intent);
            }
        });
        layout_org.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DepartmentActivity.class);
                intent.putExtra(DepartmentActivity.IS_NEW_DEPT, CommonConfig.isQtalk);
                context.startActivity(intent);
            }
        });

    }

    public void changeRobotVisible(boolean v)
    {
        layout_robot.setVisibility(v ? VISIBLE : GONE);
    }

    public void changeOrgVisible(boolean v)
    {
        layout_org.setVisibility(v?VISIBLE:GONE);
    }

    public void changeChatRoomVisible(boolean v)
    {
        layout_chatroom.setVisibility(v?VISIBLE:GONE);
    }
}
