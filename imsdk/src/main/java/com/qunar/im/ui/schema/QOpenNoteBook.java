package com.qunar.im.ui.schema;

import android.content.Intent;

import com.qunar.im.ui.activity.DailyNoteListActivity;
import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.view.zxing.activity.CaptureActivity;

import java.util.Map;

public class QOpenNoteBook implements QChatSchemaService{
    private  QOpenNoteBook(){

    }
    @Override
    public boolean startActivityAndNeedWating(IMBaseActivity context, Map<String, String> map) {
        Intent scanQRCodeIntent = new Intent(context, DailyNoteListActivity.class);
        context.startActivity(scanQRCodeIntent);
        return false;
    }

    private static class LazyHolder{
        private static final QOpenNoteBook INSTANCE = new QOpenNoteBook();
    }

    public static QOpenNoteBook getInstance(){
        return QOpenNoteBook.LazyHolder.INSTANCE;
    }
}
