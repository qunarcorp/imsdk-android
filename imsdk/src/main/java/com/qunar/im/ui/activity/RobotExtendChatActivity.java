package com.qunar.im.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.qunar.im.base.util.Constants;
import com.qunar.im.ui.R;

/**
 * Created by lihaibin.li on 2017/10/25.
 */

public class RobotExtendChatActivity extends PbChatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        edit_region.setVisibility(View.GONE);
    }

    @Override
    public void initViews() {
//        msgType = getIntent().getIntExtra(BUNDLE_KEY_MSG_TYPE, 1);
//        if (msgType == MessageType.MSG_TYPE_ROB_ORDER || msgType == MessageType.MSG_TYPE_ROB_ORDER_RESPONSE){//qchat抢单
//            pbChatViewAdapter = new RobOrderAdapter(this, jid, getHandler(), isFromChatRoom);
//        }

        super.initViews();
    }


    @Override
    public void setActionBarTitle(String str) {
        super.setActionBarTitle(str);
        //qtalk
        if(Constants.SYS.SYSTEM_MESSAGE.equals(jid)){//qtalk 系统消息 不显示右侧按钮 title显示系统消息
            setActionBarRightIcon(0);
            setActionBarTitle(R.string.atom_ui_system_message);
        }
        //qchat
        if(!TextUtils.isEmpty(jid)){
            if(jid.contains("rbt-system")){
                setActionBarRightIcon(0);
                setActionBarTitle(R.string.atom_ui_system_message);
            }else if(jid.contains("rbt-notice")){
                setActionBarRightIcon(0);
                setActionBarTitle(R.string.atom_ui_notice);
            }
        }
    }

//    public void setActionBarTitle(@StringRes int str){
//       super.setActionBarTitle(str);
//    }

}
