package com.qunar.im.ui.schema;

import android.content.Intent;

import com.qunar.im.base.util.Constants;
import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.activity.QtalkOpenTravelCalendar;

import java.util.Map;

public class QOpenTravelCalendarImpl implements QChatSchemaService{
    private QOpenTravelCalendarImpl(){

    }
    @Override
    public boolean startActivityAndNeedWating(IMBaseActivity context, Map<String, String> map) {
//        if(map != null){
//            String groupId = map.get("groupId");
            Intent intent = new Intent(context, QtalkOpenTravelCalendar.class);
            intent.putExtra("module", Constants.RNKey.TRAVELCALENDAR);
//            intent.putExtra("groupId", groupId);
//            intent.putExtra("permissions", ConnectionUtil.getInstance().selectGroupMemberPermissionsByGroupIdAndMemberId(groupId, CurrentPreference.getInstance().getPreferenceUserId()));
            context.startActivity(intent);
//        }
        return false;
    }

    private static class LazyHolder{
        private static final QOpenTravelCalendarImpl INSTANCE = new QOpenTravelCalendarImpl();
    }

    public static QOpenTravelCalendarImpl getInstance(){
        return QOpenTravelCalendarImpl.LazyHolder.INSTANCE;
    }
}
