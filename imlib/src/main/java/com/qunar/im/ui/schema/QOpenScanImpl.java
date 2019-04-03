package com.qunar.im.ui.schema;

import android.content.Intent;

import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.activity.PbChatActivity;
import com.qunar.im.ui.activity.RobotExtendChatActivity;
import com.qunar.im.ui.view.zxing.activity.CaptureActivity;

import java.util.Map;

public class QOpenScanImpl implements QChatSchemaService{
    private  QOpenScanImpl(){

    }
    @Override
    public boolean startActivityAndNeedWating(IMBaseActivity context, Map<String, String> map) {
        Intent scanQRCodeIntent = new Intent(context, CaptureActivity.class);
        context.startActivity(scanQRCodeIntent);
//        startActivityForResult(scanQRCodeIntent, SCAN_REQUEST);
//        if(map != null){
//            Intent intent = new Intent(context, RobotExtendChatActivity.class);
//            intent.putExtra(PbChatActivity.KEY_JID, map.get("jid"));
//            //设置真实id
//            intent.putExtra(PbChatActivity.KEY_REAL_JID, map.get("realJid"));
//            //设置是否是群聊
//            intent.putExtra(PbChatActivity.KEY_IS_CHATROOM, false);
//
//            intent.putExtra(PbChatActivity.KEY_CHAT_TYPE, "3");
//            intent.putExtra(PbChatActivity.KEY_UNREAD_MSG_COUNT,-1);
//            context.startActivity(intent);
//        }
        return false;
    }

    private static class LazyHolder{
        private static final QOpenScanImpl INSTANCE = new QOpenScanImpl();
    }

    public static QOpenScanImpl getInstance(){
        return QOpenScanImpl.LazyHolder.INSTANCE;
    }
}
