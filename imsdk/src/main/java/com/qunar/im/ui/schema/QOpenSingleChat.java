package com.qunar.im.ui.schema;

import android.content.Intent;
import android.text.TextUtils;

import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.activity.PbChatActivity;

import java.util.Map;

public class QOpenSingleChat implements QChatSchemaService {
    public final static QOpenSingleChat instance = new QOpenSingleChat();

//    private final static String[] devs = new String[]{"hubin.hu@" + QtalkNavicationService.getInstance().getXmppdomain(), "hubo.hu@" + QtalkNavicationService.getInstance().getXmppdomain(), "lihaibin.li@" + QtalkNavicationService.getInstance().getXmppdomain()};//ejabhost1

    //这个方法后面都要返回false , 否则会出现白屏
    @Override
    public boolean startActivityAndNeedWating(final IMBaseActivity context, Map<String, String> map) {
        String jid = map.get(PbChatActivity.KEY_JID);
        String type = map.get("type");
        int converType = 0;

        Intent intent = new Intent(context.getApplicationContext(), PbChatActivity.class);
        intent.putExtra(PbChatActivity.KEY_JID, jid);
        if(ConnectionUtil.getInstance().isHotline(jid)) {
            converType = ConversitionType.MSG_TYPE_CONSULT;
//            intent.putExtra(PbChatActivity.KEY_JID, ConnectionUtil.getInstance().getHotlineJid(jid));
        } else {
            if(!TextUtils.isEmpty(type)) {
                String chatid = map.get("chatid");
                converType = ConversitionType.getConversitionType(Integer.valueOf(type), chatid);
                if(converType == ConversitionType.MSG_TYPE_CONSULT) {
                    converType = ConversitionType.MSG_TYPE_CONSULT_SERVER;
                    intent.putExtra(PbChatActivity.KEY_REAL_JID, map.get("realjid"));
                } else if (converType == ConversitionType.MSG_TYPE_CONSULT_SERVER) {
                    converType = ConversitionType.MSG_TYPE_CONSULT;
                }
            }
        }
        intent.putExtra(PbChatActivity.KEY_CHAT_TYPE, String.valueOf(converType));
        intent.putExtra(PbChatActivity.KEY_IS_CHATROOM, false);
        context.startActivity(intent);
        return false;
    }
}