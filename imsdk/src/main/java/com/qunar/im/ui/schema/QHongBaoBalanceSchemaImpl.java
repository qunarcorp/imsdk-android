package com.qunar.im.ui.schema;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.qunar.im.base.util.Constants;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.activity.QunarWebActvity;

import java.util.Map;

/**
 * Created by hubin on 2018/4/4.
 */

public class QHongBaoBalanceSchemaImpl implements QChatSchemaService {
    public final static QHongBaoBalanceSchemaImpl instance = new QHongBaoBalanceSchemaImpl();
    //这个方法后面都要返回false , 否则会出现白屏
    @Override
    public boolean startActivityAndNeedWating(final IMBaseActivity context, Map<String, String> map) {


        if (!TextUtils.isEmpty(QtalkNavicationService.HONGBAO_BALANCE)) {
            Uri uri = Uri.parse(QtalkNavicationService.HONGBAO_BALANCE);
            Intent intent = new Intent(context.getApplication(), QunarWebActvity.class);
            intent.putExtra(Constants.BundleKey.WEB_FROM, Constants.BundleValue.HONGBAO);
            intent.setData(uri);
            context.startActivity(intent);

        }
        return false;
    }
}

