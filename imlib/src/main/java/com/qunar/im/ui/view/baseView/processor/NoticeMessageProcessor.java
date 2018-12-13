package com.qunar.im.ui.view.baseView.processor;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.qunar.im.base.jsonbean.RichText;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.RichActionView;
import com.qunar.im.ui.view.baseView.ViewPool;

/**
 * Created by zhaokai on 15-11-12.
 */
public class NoticeMessageProcessor extends DefaultMessageProcessor {
    @Override
    public void processChatView(ViewGroup parent, IMessageItem item) {
        final IMMessage message = item.getMessage();
        final RichText text = JsonUtils.getGson().fromJson(message.getBody(), RichText.class);
        RichActionView richActionView = ViewPool.getView(RichActionView.class,item.getContext());
        richActionView.bindData(text);
        richActionView.getImageRich().setVisibility(View.GONE);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        parent.addView(richActionView,layoutParams);
        parent.setVisibility(View.VISIBLE);
    }
}
