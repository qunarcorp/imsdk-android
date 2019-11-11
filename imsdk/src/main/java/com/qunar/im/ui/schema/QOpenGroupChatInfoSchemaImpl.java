package com.qunar.im.ui.schema;

import android.content.Intent;

import com.qunar.im.base.util.Constants;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.util.ReflectUtil;
import com.qunar.im.utils.ConnectionUtil;

import java.util.Map;

public class QOpenGroupChatInfoSchemaImpl implements QChatSchemaService{
    private QOpenGroupChatInfoSchemaImpl(){

    }
    @Override
    public boolean startActivityAndNeedWating(IMBaseActivity context, Map<String, String> map) {
        if(map != null){
            String groupId = map.get("groupId");
            Intent intent = ReflectUtil.getQtalkServiceRNActivityIntent(context);
            if(intent == null){
                return false;
            }
            intent.putExtra("module", Constants.RNKey.GROUPCARD);
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
