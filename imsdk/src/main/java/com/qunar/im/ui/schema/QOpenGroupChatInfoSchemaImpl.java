package com.qunar.im.ui.schema;

import android.content.Intent;

import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.rn_service.activity.QtalkServiceRNActivity;

import java.util.Map;
import java.util.logging.Logger;

public class QOpenGroupChatInfoSchemaImpl implements QChatSchemaService{
    private QOpenGroupChatInfoSchemaImpl(){

    }
    @Override
    public boolean startActivityAndNeedWating(IMBaseActivity context, Map<String, String> map) {
        if(map != null){
            String groupId = map.get("groupId");
            Intent intent = new Intent(context, QtalkServiceRNActivity.class);
            intent.putExtra("module", QtalkServiceRNActivity.GROUPCARD);
            intent.putExtra("groupId", groupId);
            int permissions = ConnectionUtil.getInstance().selectGroupMemberPermissionsByGroupIdAndMemberId(groupId, CurrentPreference.getInstance().getPreferenceUserId());
            com.orhanobut.logger.Logger.i("权限："+permissions);
            intent.putExtra("permissions", permissions);
            context.startActivity(intent);
        }
        return false;
    }

    private static class LazyHolder{
        private static final QOpenGroupChatInfoSchemaImpl INSTANCE = new QOpenGroupChatInfoSchemaImpl();
    }

    public static QOpenGroupChatInfoSchemaImpl getInstance(){
        return QOpenGroupChatInfoSchemaImpl.LazyHolder.INSTANCE;
    }
}
