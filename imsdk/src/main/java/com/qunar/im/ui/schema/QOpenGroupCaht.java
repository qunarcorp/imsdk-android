package com.qunar.im.ui.schema;

import android.content.Intent;

import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.base.util.Constants;
import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.activity.PbChatActivity;

import java.util.Map;

public class QOpenGroupCaht implements QChatSchemaService {
    public final static QOpenGroupCaht instance = new QOpenGroupCaht();


    //这个方法后面都要返回false , 否则会出现白屏
    @Override
    public boolean startActivityAndNeedWating(final IMBaseActivity context, Map<String, String> map) {


        Intent intent = new Intent(context.getApplicationContext(), PbChatActivity.class);
        intent.putExtra(PbChatActivity.KEY_JID, map.get(PbChatActivity.KEY_JID));
        intent.putExtra(PbChatActivity.KEY_REAL_JID, map.get(PbChatActivity.KEY_REAL_JID));
        intent.putExtra(PbChatActivity.KEY_CHAT_TYPE, String.valueOf(ConversitionType.MSG_TYPE_GROUP));
        intent.putExtra(PbChatActivity.KEY_IS_CHATROOM, true);
        intent.putExtra(PbChatActivity.KEY_ATMSG_INDEX, 0);
        if(map.containsKey("shareMsg")){
            intent.putExtra(Constants.BundleKey.IS_FROM_SHARE,true);
            intent.putExtra(Constants.BundleKey.SHARE_EXTRA_KEY,map.get("shareMsg"));
        }
        context.startActivity(intent);
        return false;
    }
}