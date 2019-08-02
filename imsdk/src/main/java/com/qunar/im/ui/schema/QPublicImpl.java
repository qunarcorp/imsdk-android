package com.qunar.im.ui.schema;

import android.content.Intent;

import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.activity.RobotListActivity;

import java.util.Map;

public class QPublicImpl implements QChatSchemaService {
    public static final QPublicImpl instance = new QPublicImpl();
    @Override
    public boolean startActivityAndNeedWating(IMBaseActivity context, Map<String, String> map) {
        Intent intent = new Intent(context, RobotListActivity.class);
        context.startActivity(intent);
        return false;
    }
}



