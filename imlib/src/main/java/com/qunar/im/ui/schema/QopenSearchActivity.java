package com.qunar.im.ui.schema;

import android.app.Fragment;
import android.content.Intent;

import com.qunar.im.common.CommonConfig;
import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.activity.PbChatActivity;
import com.qunar.im.ui.activity.SearchUserActivity;

import java.util.Map;

public class QopenSearchActivity implements QChatSchemaService {
    public final static QopenSearchActivity instance = new QopenSearchActivity();

//    private final static String[] devs = new String[]{"hubin.hu@" + QtalkNavicationService.getInstance().getXmppdomain(), "hubo.hu@" + QtalkNavicationService.getInstance().getXmppdomain(), "lihaibin.li@" + QtalkNavicationService.getInstance().getXmppdomain()};//ejabhost1

    //这个方法后面都要返回false , 否则会出现白屏
    @Override
    public boolean startActivityAndNeedWating(final IMBaseActivity context, Map<String, String> map) {


        if (CommonConfig.isQtalk) {
            Intent i = null;
            try {
                i = new Intent(context.getApplicationContext(), (Class<? extends Fragment>) Class.forName("com.qunar.im.camelhelp.activity.QTalkSearchActivity"));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            context.startActivity(i);
        } else {
            Intent intent = new Intent(context.getApplicationContext(), SearchUserActivity.class);
            context.startActivity(intent);
        }
        return false;
    }
}