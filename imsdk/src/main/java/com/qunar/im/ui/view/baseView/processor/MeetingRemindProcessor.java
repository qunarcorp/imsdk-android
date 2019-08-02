package com.qunar.im.ui.view.baseView.processor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.qunar.im.base.jsonbean.RemindDataBean;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.ui.activity.QunarWebActvity;
import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.ClickRemindView;
import com.qunar.im.ui.view.baseView.ViewPool;

import java.util.List;
import java.util.Map;

/**
 * 会议室提醒
 */
public class MeetingRemindProcessor extends DefaultMessageProcessor{
    @Override
    public void processChatView(ViewGroup parent, final IMessageItem item) {
        IMMessage message = item.getMessage();
        final Context context = item.getContext();
        try {
            RemindDataBean meetingDataBean = JsonUtils.getGson().fromJson(message.getExt()
                    , RemindDataBean.class);
            ClickRemindView meetingRemindView = ViewPool.getView(ClickRemindView.class, context);
            StringBuilder sb = new StringBuilder();
            sb.append(meetingDataBean.getTitle());
            sb.append("\n\n");
            List<Map<String,String>> keyValues = meetingDataBean.getKeyValues();
            if(keyValues != null && keyValues.size() > 0){
                for(Map<String,String> map : keyValues){
                    for (Map.Entry entry : map.entrySet()) {
                        sb.append(entry.getKey().toString() + "：" + entry.getValue().toString());
                        sb.append("\n");
                    }
                }
            }
            meetingRemindView.setData(sb.toString());
            final String gotoUrl = meetingDataBean.getUrl() + "username=" + CurrentPreference.getInstance().getUserid() + "&meeting_id=" + meetingDataBean.getParams().get("id");
            meetingRemindView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!TextUtils.isEmpty(gotoUrl)){
                        Uri uri = Uri.parse(gotoUrl);
                        Intent intent = new Intent(context, QunarWebActvity.class);
                        intent.setData(uri);
                        context.startActivity(intent);
                    }
                }
            });
            parent.setVisibility(View.VISIBLE);
            parent.addView(meetingRemindView);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
