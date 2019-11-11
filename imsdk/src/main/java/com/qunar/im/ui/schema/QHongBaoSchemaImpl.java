package com.qunar.im.ui.schema;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.qunar.im.base.util.Constants;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.core.utils.GlobalConfigManager;
import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.activity.QunarWebActvity;
import com.qunar.im.ui.util.ReflectUtil;

import java.util.Map;

/**
 * Created by hubin on 2018/4/3.
 */

public class QHongBaoSchemaImpl implements QChatSchemaService {
    public final static QHongBaoSchemaImpl instance = new QHongBaoSchemaImpl();
    //这个方法后面都要返回false , 否则会出现白屏
    @Override
    public boolean startActivityAndNeedWating(final IMBaseActivity context, Map<String, String> map) {
        if(!TextUtils.isEmpty(QtalkNavicationService.getInstance().getPayurl())){
            Intent intent = ReflectUtil.getQtalkServiceRNActivityIntent(context);
            if(intent == null){

            }else {
                intent.putExtra("module", Constants.RNKey.PAY);
                intent.putExtra("Screen", "RedPackRecod");
                context.startActivity(intent);
            }
        }else {
            if (!TextUtils.isEmpty(QtalkNavicationService.MY_HONGBAO)) {
                Uri uri = Uri.parse(QtalkNavicationService.MY_HONGBAO);
                Intent intent = new Intent(context.getApplication(), QunarWebActvity.class);
                intent.putExtra(Constants.BundleKey.WEB_FROM, Constants.BundleValue.HONGBAO);
                intent.setData(uri);
                context.startActivity(intent);
            }
        }
        return false;
    }
}
