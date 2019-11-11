package com.qunar.im.ui.schema;

import android.content.Intent;

import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.activity.PbChatActivity;
import com.qunar.im.ui.util.ReflectUtil;

import java.util.Map;

public class QOpenUserCard implements QChatSchemaService {
    public final static QOpenUserCard instance = new QOpenUserCard();

//    private final static String[] devs = new String[]{"hubin.hu@" + QtalkNavicationService.getInstance().getXmppdomain(), "hubo.hu@" + QtalkNavicationService.getInstance().getXmppdomain(), "lihaibin.li@" + QtalkNavicationService.getInstance().getXmppdomain()};//ejabhost1

    //这个方法后面都要返回false , 否则会出现白屏
    @Override
    public boolean startActivityAndNeedWating(final IMBaseActivity context, Map<String, String> map) {

        Intent intent = ReflectUtil.getQtalkServiceRNActivityIntent(context);
        if(intent == null){
            return false;
        }
        intent.putExtra("UserId", map.get(PbChatActivity.KEY_JID));
        intent.putExtra("module", "UserCard");
        intent.putExtra("Version", "1.0.0");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        return false;
    }
}