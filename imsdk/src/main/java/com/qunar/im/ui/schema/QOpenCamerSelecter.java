package com.qunar.im.ui.schema;

import android.content.Intent;

import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.activity.ImageClipActivity;

import java.util.Map;

public class QOpenCamerSelecter implements QChatSchemaService {
    public final static QOpenCamerSelecter instance = new QOpenCamerSelecter();


    //这个方法后面都要返回false , 否则会出现白屏
    @Override
    public boolean startActivityAndNeedWating(final IMBaseActivity context, Map<String, String> map) {
        Intent intentCamera = new Intent(context.getApplicationContext(), ImageClipActivity.class);
        context.startActivity(intentCamera);
        return false;
    }
}
