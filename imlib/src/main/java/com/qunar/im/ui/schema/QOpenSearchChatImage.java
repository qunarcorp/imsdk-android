package com.qunar.im.ui.schema;

import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.activity.PbChatActivity;
import com.qunar.im.ui.activity.SearchChatPicVideoActivity;

import java.util.Map;

public class QOpenSearchChatImage implements QChatSchemaService{
    public final static QOpenSearchChatImage instance = new QOpenSearchChatImage();


    //这个方法后面都要返回false , 否则会出现白屏
    @Override
    public boolean startActivityAndNeedWating(final IMBaseActivity context, Map<String, String> map) {
        SearchChatPicVideoActivity.launch(context,map.get(PbChatActivity.KEY_JID),map.get(PbChatActivity.KEY_REAL_JID));
        return false;
    }
}
