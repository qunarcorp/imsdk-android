package com.qunar.im.ui.schema;

import android.content.Intent;

import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.activity.MyFilesActivity;

import java.util.Map;

/**
 * Created by Lex lex on 2018/5/30.
 */

public class QMyFileImpl implements QChatSchemaService  {
    public final static QMyFileImpl instance = new QMyFileImpl();
    @Override
    public boolean startActivityAndNeedWating(IMBaseActivity context, Map<String, String> map) {

        context.startActivity(new Intent(context, MyFilesActivity.class));
        return false;
    }
}
