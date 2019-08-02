package com.qunar.im.ui.schema;

import android.content.Intent;

import com.qunar.im.utils.QtalkStringUtils;
import com.qunar.im.ui.activity.ChatroomInvitationActivity;
import com.qunar.im.ui.activity.IMBaseActivity;

import java.util.Map;

/**
 * Created by xingchao.song on 3/1/2016.
 */
public class QSelectUserImpl implements QChatSchemaService {
    public final static QSelectUserImpl instance = new QSelectUserImpl();

    @Override
    public boolean startActivityAndNeedWating(IMBaseActivity context, Map<String, String> map) {
        Intent intent = new Intent(context,ChatroomInvitationActivity.class);
        if(map.containsKey(ChatroomInvitationActivity.ROOM_ID_EXTRA))
            intent.putExtra(ChatroomInvitationActivity.ROOM_ID_EXTRA, map.get(ChatroomInvitationActivity.ROOM_ID_EXTRA));
        if(map.containsKey(ChatroomInvitationActivity.USER_ID_EXTRA))
            intent.putExtra(ChatroomInvitationActivity.USER_ID_EXTRA,
                    QtalkStringUtils.parseLocalpart(map.get(ChatroomInvitationActivity.USER_ID_EXTRA)));
        if(map.containsKey(ChatroomInvitationActivity.ACTION_TYPE_EXTRA))
            intent.putExtra(ChatroomInvitationActivity.ACTION_TYPE_EXTRA,
                    Integer.parseInt(map.get(ChatroomInvitationActivity.ACTION_TYPE_EXTRA)));
        if(map.containsKey(ChatroomInvitationActivity.KEY_SELECTED_USER))
            intent.putExtra(ChatroomInvitationActivity.KEY_SELECTED_USER,
                    map.get(ChatroomInvitationActivity.KEY_SELECTED_USER));
        context.startActivityForResult(intent,QchatSchemeActivity.SELECT_MULTI_USER);
        return true;
    }
}
