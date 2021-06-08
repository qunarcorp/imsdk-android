package com.qunar.im.ui.view.baseView.processor;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qunar.im.base.module.IMMessage;
import com.qunar.im.common.CurrentPreference;
import com.qunar.im.ui.adapter.ChatViewAdapter;
import com.qunar.im.ui.view.baseView.IMessageItem;

/**
 * Created by xinbo.wang on 2016/5/10.
 */
public class RevokeMeesageProcessor extends DefaultMessageProcessor {
    @Override
    public void processTimeText(final TextView timeTextView, IMessageItem item, ChatViewAdapter adapter) {
        IMMessage message = item.getMessage();
        String from = message.getFromID();
        //根据消息的from判断是不是自己发送的 不能根据direction判断
        if(from != null && from.equals(CurrentPreference.getInstance().getPreferenceUserId())){
            timeTextView.setText("你撤回了一条消息");
            timeTextView.setVisibility(View.VISIBLE);
        }else {
            timeTextView.setText(message.getBody());
            timeTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void processChatView(ViewGroup parent, IMessageItem item) {
        parent.setVisibility(View.GONE);
    }
}
