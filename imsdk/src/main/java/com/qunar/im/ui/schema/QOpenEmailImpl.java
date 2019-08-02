package com.qunar.im.ui.schema;

import android.content.Intent;
import android.net.Uri;

import com.qunar.im.ui.activity.IMBaseActivity;

import java.util.Map;

public class QOpenEmailImpl implements QChatSchemaService {
    public final static QOpenEmailImpl instance = new QOpenEmailImpl();


    //这个方法后面都要返回false , 否则会出现白屏
    @Override
    public boolean startActivityAndNeedWating(final IMBaseActivity context, Map<String, String> map) {


        // 必须明确使用mailto前缀来修饰邮件地址,如果使用
// intent.putExtra(Intent.EXTRA_EMAIL, email)，结果将匹配不到任何应用
        String id = map.get("userId");
        Uri uri = Uri.parse("mailto:"+id);
        String[] email = {id};
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
        intent.putExtra(Intent.EXTRA_SUBJECT, "Hey"); // 主题
        intent.putExtra(Intent.EXTRA_TEXT, "正文"); // 正文
        context.startActivity(Intent.createChooser(intent, "请选择邮件类应用"));


//        Intent intent = new Intent(context.getApplicationContext(), PbChatActivity.class);
//        intent.putExtra(PbChatActivity.KEY_JID, map.get(PbChatActivity.KEY_JID));
//        intent.putExtra(PbChatActivity.KEY_REAL_JID, map.get(PbChatActivity.KEY_REAL_JID));
//        intent.putExtra(PbChatActivity.KEY_CHAT_TYPE, "1");
//        intent.putExtra(PbChatActivity.KEY_IS_CHATROOM, true);
//        intent.putExtra(PbChatActivity.KEY_ATMSG_INDEX, 0);
//        context.startActivity(intent);
        return false;
    }
}
