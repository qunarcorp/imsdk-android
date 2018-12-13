package com.qunar.im.ui.schema;

import android.content.Intent;

import com.qunar.im.common.CommonConfig;
import com.qunar.im.ui.activity.DepartmentActivity;
import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.activity.PbChatActivity;

import java.util.Map;

public class QOpenOrganizationalImpl implements QChatSchemaService {
    public final static QOpenOrganizationalImpl instance = new QOpenOrganizationalImpl();


    //这个方法后面都要返回false , 否则会出现白屏
    @Override
    public boolean startActivityAndNeedWating(final IMBaseActivity context, Map<String, String> map) {

        Intent intent = new Intent(context, DepartmentActivity.class);
        intent.putExtra(DepartmentActivity.IS_NEW_DEPT, CommonConfig.isQtalk);
        context.startActivity(intent);

        return false;
    }
}
