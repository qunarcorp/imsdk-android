package com.qunar.im.ui.view.baseView.processor;

import android.content.Context;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;

import com.qunar.im.base.jsonbean.MedalRemindDataBean;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.MedalClickRemindView;
import com.qunar.im.ui.view.baseView.ViewPool;

public class MedalRemindProcessor extends DefaultMessageProcessor {

    @Override
    public void processChatView(ViewGroup parent, final IMessageItem item) {
        IMMessage message = item.getMessage();
        final Context context = item.getContext();
        try {
            MedalRemindDataBean meetingDataBean = JsonUtils.getGson().fromJson(message.getExt()
                    , MedalRemindDataBean.class);
            MedalClickRemindView meetingRemindView = ViewPool.getView(MedalClickRemindView.class, context);
            SpannableStringBuilder sb = new SpannableStringBuilder();
            String[] strs = meetingDataBean.getStrMap().getAllStr().split(meetingDataBean.getStrMap().getHighlightStr());
            Spanned color = Html.fromHtml("<font color='#00cabe'>" + meetingDataBean.getStrMap().getHighlightStr() + "</font>");

            if (strs.length > 1) {
                sb.append(strs[0]);
                sb.append(color);
                sb.append(strs[1]);
            }
            meetingRemindView.setSB(sb);


            meetingRemindView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    context.startActivity(  FlutterMedalActivity.makeIntent(context,CurrentPreference.getInstance().getPreferenceUserId()));
                }
            });
            parent.setVisibility(View.VISIBLE);
            parent.addView(meetingRemindView);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
