package com.qunar.im.ui.schema;

import android.content.Intent;

import com.qunar.im.ui.activity.AboutActivity;
import com.qunar.im.ui.activity.AddAuthMessageActivity;
import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.activity.PbChatActivity;
import com.qunar.im.ui.activity.PersonalInfoActivity;

import java.util.Map;

public class QAddFriend implements QChatSchemaService {
    public final static QAddFriend instance = new QAddFriend();

    //这个方法后面都要返回false , 否则会出现白屏
    @Override
    public boolean startActivityAndNeedWating(final IMBaseActivity context, Map<String, String> map) {
        Intent intent = new Intent(context.getApplicationContext(), AddAuthMessageActivity.class);
        intent.putExtra("jid", map.get(PbChatActivity.KEY_JID));
        context.startActivity(intent);
        return false;
    }
}