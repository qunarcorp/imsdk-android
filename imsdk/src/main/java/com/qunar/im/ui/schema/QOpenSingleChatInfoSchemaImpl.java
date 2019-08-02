package com.qunar.im.ui.schema;

import android.content.Intent;

import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.rn_service.activity.QtalkServiceRNActivity;

import java.util.Map;

public class QOpenSingleChatInfoSchemaImpl implements QChatSchemaService{
    private QOpenSingleChatInfoSchemaImpl(){

    }
    @Override
    public boolean startActivityAndNeedWating(IMBaseActivity context, Map<String, String> map) {
        if(map != null){
            Intent intent = new Intent(context, QtalkServiceRNActivity.class);
            intent.putExtra("module", QtalkServiceRNActivity.USERCARD);
            intent.putExtra("UserId", map.get("userId"));
            intent.putExtra("RealJid", map.get("realJid"));
            intent.putExtra("Screen", "ChatInfo");
            context.startActivity(intent);
        }
        return false;
    }

    private static class LazyHolder{
        private static final QOpenSingleChatInfoSchemaImpl INSTANCE = new QOpenSingleChatInfoSchemaImpl();
    }

    public static QOpenSingleChatInfoSchemaImpl getInstance(){
        return QOpenSingleChatInfoSchemaImpl.LazyHolder.INSTANCE;
    }
}
