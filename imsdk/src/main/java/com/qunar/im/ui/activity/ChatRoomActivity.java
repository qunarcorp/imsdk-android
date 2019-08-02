package com.qunar.im.ui.activity;

import android.os.Bundle;

import com.qunar.im.ui.R;
import com.qunar.im.ui.fragment.ChatRoomFragment;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;

/**
 * Created by saber on 15-7-2.
 */
public class ChatRoomActivity extends SwipeBackActivity {

    @Override
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.atom_ui_activity_blank);
        //bugly tag
//        CrashReportUtils.getInstance().setUserTag(55094);
        QtNewActionBar actionBar = (QtNewActionBar) findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setActionBarTitle(R.string.atom_ui_common_my_group);
        initViews();
    }

    void initViews()
    {
        ChatRoomFragment chatRoomFragment = new ChatRoomFragment();
        chatRoomFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().
                replace(R.id.layout_blanck_content, chatRoomFragment).commit();
    }
}
