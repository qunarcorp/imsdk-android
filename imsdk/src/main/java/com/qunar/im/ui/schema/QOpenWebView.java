package com.qunar.im.ui.schema;

import android.content.Intent;
import android.net.Uri;

import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.activity.QunarWebActvity;

import java.util.Map;

public class QOpenWebView implements QChatSchemaService {
    public final static QOpenWebView instance = new QOpenWebView();


    //这个方法后面都要返回false , 否则会出现白屏
    @Override
    public boolean startActivityAndNeedWating(final IMBaseActivity context, Map<String, String> map) {


        String action = map.get("action");
        String time = map.get("time");
        String from, to, muc;
        String url = "" + action + "&time=" + time + "&method=showDetails";
        if ("single_details".equals(action)) {
            from = CurrentPreference.getInstance().getUserid();
            to = map.get("jid");
            url += "&from=" + from + "&to=" + to;
        } else {
            muc = map.get("jid");
            url += "&muc=" + muc;
        }


        Intent intent = new Intent(context.getApplicationContext(), QunarWebActvity.class);
        intent.setData(Uri.parse(url));
        intent.putExtra(QunarWebActvity.IS_HIDE_BAR, false);
        context.startActivity(intent);

        return false;
    }
}