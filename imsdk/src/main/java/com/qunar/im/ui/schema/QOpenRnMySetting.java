package com.qunar.im.ui.schema;

import android.content.Intent;

import com.qunar.im.base.module.UserHaveMedalStatus;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.core.manager.IMDatabaseManager;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.util.ReflectUtil;

import java.util.List;
import java.util.Map;

public class QOpenRnMySetting implements QChatSchemaService{
    private QOpenRnMySetting(){

    }
    @Override
    public boolean startActivityAndNeedWating(IMBaseActivity context, Map<String, String> map) {
        if(map != null){
            Intent intent = ReflectUtil.getQtalkServiceRNActivityIntent(context);
            if(intent == null){
                return false;
            }
            intent.putExtra("module","MySetting");
            intent.putExtra("Screen","Setting");
            context.startActivity(intent);
        }
        return false;
    }

    private static class LazyHolder{
        private static final QOpenRnMySetting INSTANCE = new QOpenRnMySetting();
    }

    public static QOpenRnMySetting getInstance(){
        return QOpenRnMySetting.LazyHolder.INSTANCE;
    }
}