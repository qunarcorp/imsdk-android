package com.qunar.im.ui.schema;

import android.content.Intent;

import com.qunar.im.base.presenter.ILoginPresenter;
import com.qunar.im.base.presenter.impl.LoginPresenter;
import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.activity.TabMainActivity;
import com.qunar.im.ui.services.PushServiceUtils;

import java.util.Map;

/**
 * Created by hubin on 2018/4/10.
 */

public class QLogoutImpl implements QChatSchemaService {
    public final static QLogoutImpl instance = new QLogoutImpl();
    //这个方法后面都要返回false , 否则会出现白屏
    @Override
    public boolean startActivityAndNeedWating(final IMBaseActivity context, Map<String, String> map) {

        PushServiceUtils.stopAMDService(context);
        final ILoginPresenter loginPresenter = new LoginPresenter();
        loginPresenter.logout();

        Intent intent = new Intent(context, TabMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        return false;
    }
}



