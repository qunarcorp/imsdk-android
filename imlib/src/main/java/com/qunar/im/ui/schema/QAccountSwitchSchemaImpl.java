package com.qunar.im.ui.schema;

import android.content.Intent;
import android.text.TextUtils;

import com.qunar.im.base.util.Constants;
import com.qunar.im.ui.activity.AccountSwitchActivity;
import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.activity.TabMainActivity;

import java.util.Map;

public class QAccountSwitchSchemaImpl implements QChatSchemaService{
    private  QAccountSwitchSchemaImpl(){

    }
    @Override
    public boolean startActivityAndNeedWating(IMBaseActivity context, Map<String, String> map) {
        Intent intent = new Intent(context, AccountSwitchActivity.class);
        context.startActivity(intent);
        return false;
    }

    private static class LazyHolder{
        private static final QAccountSwitchSchemaImpl INSTANCE = new QAccountSwitchSchemaImpl();
    }

    public static final QAccountSwitchSchemaImpl getInstance(){
        return QAccountSwitchSchemaImpl.LazyHolder.INSTANCE;
    }
}
