package com.qunar.im.ui.schema;

import android.content.Intent;
import android.text.TextUtils;
import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.activity.LocalChatRecordActivity;

import java.util.Map;

/**
 * Created by xinbo.wang on 2017-01-03.
 */
public class SearchDetailsImpl implements QChatSchemaService {
    public final static SearchDetailsImpl instance = new SearchDetailsImpl();
    @Override
    public boolean startActivityAndNeedWating(IMBaseActivity context, Map<String, String> map) {
        Intent intent = new Intent(context.getApplication(),LocalChatRecordActivity.class);
        if(map.containsKey(LocalChatRecordActivity.KEY_JID)) {
            String jid = map.get(LocalChatRecordActivity.KEY_JID);
            if(!TextUtils.isEmpty(jid)) {
                intent.putExtra(LocalChatRecordActivity.KEY_JID,jid );
                intent.putExtra(LocalChatRecordActivity.KEY_SELECTED_TIME,Long.valueOf(map.get("time")));
                intent.putExtra(LocalChatRecordActivity.KEY_FROM_CLOUD,true);
                intent.putExtra(LocalChatRecordActivity.KEY_MSG_ID,map.get("msgid"));
            }
        }
        context.startActivity(intent);
        return false;
    }
}
