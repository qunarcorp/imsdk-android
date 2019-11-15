package com.qunar.im.ui.schema;

import android.content.Intent;

import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.activity.NavConfigActivity;

import java.util.Map;

/**
 * Created by froyomu on 2019-09-06
 * <p>
 * Describe:
 */
public class QOpenNavConfigImpl implements QChatSchemaService{
    private  QOpenNavConfigImpl(){

    }
    @Override
    public boolean startActivityAndNeedWating(IMBaseActivity context, Map<String, String> map) {
        Intent scanQRCodeIntent = new Intent(context, NavConfigActivity.class);
        context.startActivity(scanQRCodeIntent);
        return false;
    }

    private static class LazyHolder{
        private static final QOpenNavConfigImpl INSTANCE = new QOpenNavConfigImpl();
    }

    public static QOpenNavConfigImpl getInstance(){
        return QOpenNavConfigImpl.LazyHolder.INSTANCE;
    }
}
