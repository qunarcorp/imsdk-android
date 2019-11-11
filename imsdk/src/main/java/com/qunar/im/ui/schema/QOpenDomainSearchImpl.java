package com.qunar.im.ui.schema;

import android.content.Intent;

import com.qunar.im.base.util.Constants;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.util.ReflectUtil;

import java.util.Map;

/**
 * Created by froyomu on 2019/5/7
 * <p>
 * Describe:
 */
public class QOpenDomainSearchImpl implements QChatSchemaService{
    private QOpenDomainSearchImpl(){

    }

    @Override
    public boolean startActivityAndNeedWating(IMBaseActivity context, Map<String, String> map) {
        Intent intent = ReflectUtil.getQtalkServiceRNActivityIntent(context);
        if(intent == null){
            return false;
        }
        intent.putExtra("module", Constants.RNKey.CONTACTS);
        intent.putExtra("Screen", "Search");
        intent.putExtra(Constants.BundleKey.DOMAIN_LIST_URL, QtalkNavicationService.getInstance().getDomainSearchUrl());
        context.startActivity(intent);
        return false;
    }

    private static class LazyHolder{
        private static final QOpenDomainSearchImpl INSTANCE = new QOpenDomainSearchImpl();
    }

    public static QOpenDomainSearchImpl getInstance(){
        return QOpenDomainSearchImpl.LazyHolder.INSTANCE;
    }
}
