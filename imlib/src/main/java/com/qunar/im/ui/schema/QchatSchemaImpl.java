package com.qunar.im.ui.schema;

import android.content.Intent;
import android.text.TextUtils;

import com.qunar.im.ui.activity.ChatActivity;
import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.activity.PbChatActivity;

import java.util.Map;

/**
 * Created by xinbo.wang on 2016-09-22.
 */
public class QchatSchemaImpl implements QChatSchemaService {
    public final static QchatSchemaImpl instance = new QchatSchemaImpl();

    @Override
    public boolean startActivityAndNeedWating(IMBaseActivity context, Map<String, String> map) {
        Intent intent = new Intent(context.getApplication(),PbChatActivity.class);
        if(map.containsKey(ChatActivity.KEY_JID)) {
            String jid = map.get(ChatActivity.KEY_JID);
            if(!TextUtils.isEmpty(jid)) {
                intent.putExtra(ChatActivity.KEY_JID,jid );
                intent.putExtra(ChatActivity.KEY_IS_CHATROOM,jid.contains("@conference"));
            }
        }
        context.startActivity(intent);
        return false;
    }
}
