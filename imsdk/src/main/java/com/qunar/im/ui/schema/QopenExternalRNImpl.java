package com.qunar.im.ui.schema;

import android.content.Intent;

import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.activity.QtalkServiceExternalRNActivity;

import java.util.Map;

public class QopenExternalRNImpl implements QChatSchemaService{
    private QopenExternalRNImpl(){

    }
    @Override
    public boolean startActivityAndNeedWating(IMBaseActivity context, Map<String, String> map) {
//        if(map != null){
//            String groupId = map.get("groupId");

        Intent intent = new Intent(context, QtalkServiceExternalRNActivity.class);

        for (Map.Entry<String, String> entry : map.entrySet()) {
//                        intent.putExtra(entry.getKey(), entry.getValue() + "");
//            map.put(entry.getKey(),entry.getValue());
//            str+=entry.getKey()+"="+entry.getValue()+"&";
            intent.putExtra(entry.getKey(),entry.getValue());
        }

//            intent.putExtra("groupId", groupId);
//            intent.putExtra("permissions", ConnectionUtil.getInstance().selectGroupMemberPermissionsByGroupIdAndMemberId(groupId, CurrentPreference.getInstance().getPreferenceUserId()));
        context.startActivity(intent);
//        }
        return false;
    }

    private static class LazyHolder{
        private static final QopenExternalRNImpl INSTANCE = new QopenExternalRNImpl();
    }

    public static QopenExternalRNImpl getInstance(){
        return QopenExternalRNImpl.LazyHolder.INSTANCE;
    }
}
