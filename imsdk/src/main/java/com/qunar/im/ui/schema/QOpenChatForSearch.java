package com.qunar.im.ui.schema;

import android.content.Intent;
import android.text.TextUtils;

import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.activity.PbChatActivity;
import com.qunar.im.ui.activity.PbChatLocalSearchActivity;

import java.util.Map;

public class QOpenChatForSearch implements QChatSchemaService {
    public final static QOpenChatForSearch instance = new QOpenChatForSearch();


    //这个方法后面都要返回false , 否则会出现白屏
    @Override
    public boolean startActivityAndNeedWating(final IMBaseActivity context, Map<String, String> map) {

        Intent intent = new Intent(context.getApplicationContext(), PbChatLocalSearchActivity.class);
        intent.putExtra(PbChatActivity.KEY_JID, map.get(PbChatActivity.KEY_JID));
        intent.putExtra(PbChatActivity.KEY_REAL_JID, map.get(PbChatActivity.KEY_REAL_JID));
        String chatType = map.get(PbChatActivity.KEY_CHAT_TYPE);
        intent.putExtra(PbChatActivity.KEY_CHAT_TYPE, chatType);
        intent.putExtra(PbChatActivity.KEY_IS_CHATROOM, "1".equals(chatType));
        intent.putExtra(PbChatActivity.KEY_ATMSG_INDEX, 0);
        String time = map.get(PbChatLocalSearchActivity.KEY_START_TIME);
        intent.putExtra(PbChatLocalSearchActivity.KEY_START_TIME, TextUtils.isEmpty(time) ? 0 : Long.parseLong(time));
        context.startActivity(intent);
        return false;
    }
}
