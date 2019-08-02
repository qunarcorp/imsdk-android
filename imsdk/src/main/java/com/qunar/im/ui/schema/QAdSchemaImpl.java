package com.qunar.im.ui.schema;

import android.content.Intent;

import com.qunar.im.ui.activity.AdActivity;
import com.qunar.im.ui.activity.IMBaseActivity;

import java.util.Map;

/**
 * Created by xinbo.wang on 2016-09-23.
 */
public class QAdSchemaImpl implements QChatSchemaService {
    public final static QAdSchemaImpl instance = new QAdSchemaImpl();
    @Override
    public boolean startActivityAndNeedWating(IMBaseActivity context, Map<String, String> map) {
        Intent intent = new Intent(context.getApplicationContext(),AdActivity.class);
        if(map.containsKey(AdActivity.KEY_AD_JSON))
        {
            intent.putExtra(AdActivity.KEY_AD_JSON,map.get(AdActivity.KEY_AD_JSON));
        }
        context.startActivity(intent);
        return false;
    }
}
