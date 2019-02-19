package com.qunar.im.ui.schema;

import android.content.Intent;
import android.text.TextUtils;

import com.qunar.im.base.presenter.messageHandler.ConversitionType;
import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.activity.PbChatActivity;
import com.qunar.im.ui.activity.WorkWorldActivity;
import com.qunar.im.utils.ConnectionUtil;

import java.util.Map;

public class QOpenUserWorkWorld implements QChatSchemaService {
    public final static QOpenUserWorkWorld instance = new QOpenUserWorkWorld();

//    private final static String[] devs = new String[]{"hubin.hu@" + QtalkNavicationService.getInstance().getXmppdomain(), "hubo.hu@" + QtalkNavicationService.getInstance().getXmppdomain(), "lihaibin.li@" + QtalkNavicationService.getInstance().getXmppdomain()};//ejabhost1

    //这个方法后面都要返回false , 否则会出现白屏
    @Override
    public boolean startActivityAndNeedWating(final IMBaseActivity context, Map<String, String> map) {


        Intent intent = new Intent(context.getApplicationContext(), WorkWorldActivity.class);
        if(map.get(PbChatActivity.KEY_JID)!=null){
            String jid = map.get(PbChatActivity.KEY_JID);
            intent.putExtra(PbChatActivity.KEY_JID, jid);
        }
        context.startActivity(intent);
        return false;
    }
}