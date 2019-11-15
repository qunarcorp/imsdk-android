package com.qunar.im.ui.schema;

import com.qunar.im.ui.activity.IMBaseActivity;

import java.util.Map;

public class QOpenFlutterView implements QChatSchemaService {
    public final static QOpenFlutterView instance = new QOpenFlutterView();

//    private final static String[] devs = new String[]{"hubin.hu@" + QtalkNavicationService.getInstance().getXmppdomain(), "hubo.hu@" + QtalkNavicationService.getInstance().getXmppdomain(), "lihaibin.li@" + QtalkNavicationService.getInstance().getXmppdomain()};//ejabhost1

    //这个方法后面都要返回false , 否则会出现白屏
    @Override
    public boolean startActivityAndNeedWating(final IMBaseActivity context, Map<String, String> map) {


//        context.startActivity(  FlutterMedalActivity.makeIntent(context,map.get(PbChatActivity.KEY_JID)));
        return false;
    }
}