package com.qunar.im.ui.schema;

import android.content.Intent;

import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.activity.MindWorkWorldActivity;
import com.qunar.im.ui.activity.PbChatActivity;
import com.qunar.im.ui.activity.SelfWorkWorldActivity;
import com.qunar.im.ui.activity.WorkWorldActivity;

import java.util.Map;

import static com.qunar.im.ui.fragment.WorkWorldFragment.ISSTARTREFRESH;
import static com.qunar.im.ui.fragment.WorkWorldFragment.WorkWordJID;
import static com.qunar.im.ui.fragment.WorkWorldNoticeFragment.isMindMessageState;

public class QOpenUserWorkWorld implements QChatSchemaService {
    public final static QOpenUserWorkWorld instance = new QOpenUserWorkWorld();

//    private final static String[] devs = new String[]{"hubin.hu@" + QtalkNavicationService.getInstance().getXmppdomain(), "hubo.hu@" + QtalkNavicationService.getInstance().getXmppdomain(), "lihaibin.li@" + QtalkNavicationService.getInstance().getXmppdomain()};//ejabhost1

    //这个方法后面都要返回false , 否则会出现白屏
    @Override
    public boolean startActivityAndNeedWating(final IMBaseActivity context, Map<String, String> map) {


        Intent intent = null;
        if(map.get(WorkWordJID)!=null){
            String jid = map.get(WorkWordJID);
            if(jid.equals(CurrentPreference.getInstance().getPreferenceUserId())){
                intent = new Intent(context.getApplicationContext(),SelfWorkWorldActivity.class);
//                intent = new Intent(context.getApplicationContext(), WorkWorldActivity.class);
                intent.putExtra(ISSTARTREFRESH,true);
                intent.putExtra(isMindMessageState,false);
                intent.putExtra(WorkWordJID, jid);
            }else{
                intent = new Intent(context.getApplicationContext(),MindWorkWorldActivity.class);
                intent.putExtra(ISSTARTREFRESH,true);
                intent.putExtra(WorkWordJID, jid);
            }

        }


        context.startActivity(intent);
        return false;
    }
}