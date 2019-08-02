package com.qunar.im.ui.schema;

import android.content.Intent;

import com.qunar.im.ui.activity.IMBaseActivity;

import java.util.Map;

/**
 * Created by hubin on 2018/4/10.
 */

public class QMcConfigImpl implements QChatSchemaService {
    public final static QMcConfigImpl instance = new QMcConfigImpl();
    //这个方法后面都要返回false , 否则会出现白屏
    @Override
    public boolean startActivityAndNeedWating(final IMBaseActivity context, Map<String, String> map) {
        try{
            //反射 结偶
            Class classHyMain = Class.forName("com.qunar.im.camelhelp.HyMainActivity");
            Intent i = new Intent(context.getApplication(), classHyMain);
            i.putExtra("module", "user-info");
            i.putExtra("data", "");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }catch (ClassNotFoundException e){

        }
        return false;
    }
}