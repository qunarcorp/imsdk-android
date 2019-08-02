package com.qunar.im.ui.schema;

import android.content.Intent;

import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.activity.PictureSelectorActivity;

import java.util.Map;

public class QOpenPictureSelector implements QChatSchemaService {
    public final static QOpenPictureSelector instance = new QOpenPictureSelector();


    //这个方法后面都要返回false , 否则会出现白屏
    @Override
    public boolean startActivityAndNeedWating(final IMBaseActivity context, Map<String, String> map) {
        Intent intentPic = new Intent(context.getApplicationContext(), PictureSelectorActivity.class);
        intentPic.putExtra("isGravantarSel", true);
        intentPic.putExtra("isMultiSel", false);
        context.startActivity(intentPic);
        return false;
    }
}