package com.qunar.im.ui.schema;

import android.content.Intent;

import com.qunar.im.ui.activity.AboutActivity;
import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.activity.UnReadChatListActivity;

import java.util.Map;

public class QOpenUnReadListImpl implements QChatSchemaService {
    public final static QOpenUnReadListImpl instance = new QOpenUnReadListImpl();

    //这个方法后面都要返回false , 否则会出现白屏
    @Override
    public boolean startActivityAndNeedWating(final IMBaseActivity context, Map<String, String> map) {


        Intent intent = new Intent(context.getApplication(), UnReadChatListActivity.class);
        context.startActivity(intent);
        return false;
    }
}
