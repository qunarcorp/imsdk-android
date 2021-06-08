package com.qunar.im.ui.view.baseView.processor;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.qunar.im.base.jsonbean.RemindDataBean;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.module.WorkWorldItem;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.ui.activity.WorkWorldDetailsActivity;
import com.qunar.im.ui.view.baseView.ClickRemindView;
import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.ViewPool;
import com.qunar.im.utils.ConnectionUtil;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static com.qunar.im.ui.activity.WorkWorldDetailsActivity.WORK_WORLD_DETAILS_ITEM;

public class WorkWorldRemindProcessor extends DefaultMessageProcessor {

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
            final String postUUID = meetingDataBean.getParams().get("postUUID");
//            final String gotoUrl = meetingDataBean.getUrl() + "username=" + CurrentPreference.getInstance().getUserid() + "&meeting_id=" + meetingDataBean.getParams().get("id");
            if(!TextUtils.isEmpty(postUUID)) {


                meetingRemindView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                    if(!TextUtils.isEmpty(gotoUrl)){
//                        Uri uri = Uri.parse(gotoUrl);
//                        Intent intent = new Intent(context, QunarWebActvity.class);
//                        intent.setData(uri);
//                        context.startActivity(intent);
//                    }
                        if (!TextUtils.isEmpty(postUUID)) {
                            ConnectionUtil.getInstance().getWorkWorldByUUID(postUUID, new ConnectionUtil.WorkWorldCallBack() {
                                @Override
                                public void callBack(WorkWorldItem item) {
                                    if (!TextUtils.isEmpty(item.getUuid())) {
                                        Intent intent = new Intent(context, WorkWorldDetailsActivity.class);
                                        intent.putExtra(WORK_WORLD_DETAILS_ITEM, (Serializable) item);
                                        context.startActivity(intent);
                                    }
                                }

                                @Override
                                public void goToNetWork() {
                                    Toast.makeText(context,"加载中,请稍后...",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
            parent.setVisibility(View.VISIBLE);
            parent.addView(meetingRemindView);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
