package com.qunar.im.ui.schema;

import android.content.Intent;

import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.activity.PbChatActivity;
import com.qunar.im.ui.activity.RobotExtendChatActivity;

import java.util.Map;

/**
 * 打开headline 系统消息 无输入view
 */
public class QOpenHeadLineSchemaImpl implements QChatSchemaService{
    private  QOpenHeadLineSchemaImpl(){

    }
    @Override
    public boolean startActivityAndNeedWating(IMBaseActivity context, Map<String, String> map) {
        if(map != null){
            Intent intent = new Intent(context, RobotExtendChatActivity.class);
            intent.putExtra(PbChatActivity.KEY_JID, map.get("jid"));
            //设置真实id
            intent.putExtra(PbChatActivity.KEY_REAL_JID, map.get("realJid"));
            //设置是否是群聊
            intent.putExtra(PbChatActivity.KEY_IS_CHATROOM, false);

            intent.putExtra(PbChatActivity.KEY_CHAT_TYPE, "3");
            intent.putExtra(PbChatActivity.KEY_UNREAD_MSG_COUNT,-1);
            context.startActivity(intent);
        }
        return false;
    }

    private static class LazyHolder{
        private static final QOpenHeadLineSchemaImpl INSTANCE = new QOpenHeadLineSchemaImpl();
    }

    public static QOpenHeadLineSchemaImpl getInstance(){
        return QOpenHeadLineSchemaImpl.LazyHolder.INSTANCE;
    }
}
