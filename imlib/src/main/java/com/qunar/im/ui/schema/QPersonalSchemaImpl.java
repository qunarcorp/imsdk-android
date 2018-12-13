package com.qunar.im.ui.schema;

import android.content.Intent;
import android.text.TextUtils;

import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.activity.PersonalInfoActivity;

import java.util.Map;

/**
 * Created by xinbo.wang on 2016-09-22.
 */
public class QPersonalSchemaImpl implements QChatSchemaService {
    public final static QPersonalSchemaImpl instance = new QPersonalSchemaImpl();
    //这个方法后面都要返回false , 否则会出现白屏
    @Override
    public boolean startActivityAndNeedWating(final IMBaseActivity context, Map<String, String> map) {
        final Intent intent = new Intent(context.getApplication(),PersonalInfoActivity.class);
        //判断是否有jid
        String jid = map.get("jid");
        if (TextUtils.isEmpty(jid)){
            return false;
        }
        intent.putExtra("jid",jid);


        context.startActivity(intent);
        return false;

    }
}
