package com.qunar.im.ui.view.baseView.processor;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qunar.im.ui.adapter.ChatViewAdapter;
import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.MessageSpliterView;

/**
 * Created by xinbo.wang on 2017-02-09.
 */
public class MessageSpliterProcessor extends DefaultMessageProcessor {
    @Override
    public void processChatView(ViewGroup parent, final IMessageItem item) {
        MessageSpliterView spliterView = new MessageSpliterView(item.getContext());
        parent.addView(spliterView);
        parent.setVisibility(View.VISIBLE);
    }

    @Override
    public void processTimeText(TextView timeTextView, IMessageItem item, ChatViewAdapter adapter) {
        timeTextView.setVisibility(View.GONE);
    }
}
