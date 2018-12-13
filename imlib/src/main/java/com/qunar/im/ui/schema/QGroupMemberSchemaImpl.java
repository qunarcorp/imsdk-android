package com.qunar.im.ui.schema;

import android.content.Intent;
import android.text.TextUtils;

import com.qunar.im.ui.activity.ChatroomMembersActivity;
import com.qunar.im.ui.activity.IMBaseActivity;

import java.util.Map;

/**
 * Created by xinbo.wang on 2016-09-22.
 */
public class QGroupMemberSchemaImpl implements QChatSchemaService {
    public final static QGroupMemberSchemaImpl instance = new QGroupMemberSchemaImpl();
    @Override
    public boolean startActivityAndNeedWating(IMBaseActivity context, Map<String, String> map) {
        Intent intent = new Intent(context.getApplication(),ChatroomMembersActivity.class);
        if(map.containsKey(ChatroomMembersActivity.JID))
        {
            String jid = map.get(ChatroomMembersActivity.JID);
            if(!TextUtils.isEmpty(jid))
            {
                intent.putExtra(ChatroomMembersActivity.JID,jid);
                intent.putExtra(ChatroomMembersActivity.IS_FROM_GROUP,jid.contains("@conference"));
            }
        }
        context.startActivity(intent);
        return false;
    }
}
