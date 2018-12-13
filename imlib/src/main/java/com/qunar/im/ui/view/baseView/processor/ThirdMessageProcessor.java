package com.qunar.im.ui.view.baseView.processor;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.qunar.im.base.module.IMMessage;
import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.ViewPool;
import com.qunar.im.ui.view.baseView.ThirdMessageView;


/**
 * 微信过来的消息
 */
public class ThirdMessageProcessor extends DefaultMessageProcessor {
    @Override
    public void processChatView(ViewGroup parent, IMessageItem item) {
        ThirdMessageView weixinMessageView = ViewPool.getView(ThirdMessageView.class, item.getContext());
        IMMessage message = item.getMessage();
        weixinMessageView.bindData(message);
        parent.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        parent.addView(weixinMessageView,layoutParams);
    }
}
