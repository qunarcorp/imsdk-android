package com.qunar.im.ui.schema;

import android.content.Intent;
import android.text.TextUtils;

import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.activity.RobotInfoActivity;

import java.util.Map;

/**
 * Created by xinbo.wang on 2016-09-22.
 */
public class QRobotSchemaImpl implements QChatSchemaService {
    public static final QRobotSchemaImpl instance = new QRobotSchemaImpl();
    @Override
    public boolean startActivityAndNeedWating(IMBaseActivity context, Map<String, String> map) {
        Intent intent = new Intent(context.getApplication(), RobotInfoActivity.class);
        if(map.containsKey(RobotInfoActivity.ROBOT_ID_EXTRA))
        {
            intent.putExtra(RobotInfoActivity.ROBOT_ID_EXTRA,map.get(RobotInfoActivity.ROBOT_ID_EXTRA));
        }
        if(map.containsKey(RobotInfoActivity.IS_HIDEN_EXTRA))
        {
            String isHide = map.get(RobotInfoActivity.IS_HIDEN_EXTRA);
            if(!TextUtils.isEmpty(isHide))
            {
                intent.putExtra(RobotInfoActivity.IS_HIDEN_EXTRA,isHide.equals("true"));
            }
        }
        if(map.containsKey(RobotInfoActivity.CONTENT_EXTRA))
        {
            intent.putExtra(RobotInfoActivity.CONTENT_EXTRA,map.get(RobotInfoActivity.CONTENT_EXTRA));
        }
        if(map.containsKey(RobotInfoActivity.MSG_TYPE_EXTRA))
        {
            intent.putExtra(RobotInfoActivity.MSG_TYPE_EXTRA,map.get(RobotInfoActivity.MSG_TYPE_EXTRA));
        }
        context.startActivity(intent);
        return false;
    }
}
