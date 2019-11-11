package com.qunar.im.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.core.utils.GlobalConfigManager;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.IconView;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;

public class BackgroundTipActivity extends SwipeBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_file_sharing);
        ((TextView)findViewById(R.id.atom_ui_text1)).setText(getString(R.string.atom_ui_file_sharing_tips1));
        IconView iconView = (IconView) findViewById(R.id.sharing_file);
        iconView.setOnClickListener(v -> {
//                Logger.i("点击了传送文件");
            Intent intent = new Intent(BackgroundTipActivity.this,PbChatActivity.class);
            intent.putExtra(PbChatActivity.KEY_JID, "file-transfer"+"@"+ QtalkNavicationService.getInstance().getXmppdomain());
            intent.putExtra(PbChatActivity.KEY_SHOW_READSTATE,false);
//                intent.putExtra(PbChatActivity.KEY_REAL_JID,"file-transfer"+"@"+ QtalkNavicationService.getInstance().getXmppdomain());
            intent.putExtra(PbChatActivity.KEY_CHAT_TYPE, 0 + "");
            intent.putExtra(PbChatActivity.KEY_IS_CHATROOM, false);

            startActivity(intent);
            finish();
        });
        initView();
    }

    @SuppressLint("ResourceAsColor")
    private void initView(){
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setActionBarTitle("");
        setActionBarLeft(getResources().getColor(R.color.atom_ui_new_color_select),false,getString(R.string.atom_ui_common_close),0);
//        setActionBarTitle(R.string.atom_ui_setting_title);
    }
}
