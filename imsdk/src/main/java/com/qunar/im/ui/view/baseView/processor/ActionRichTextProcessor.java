package com.qunar.im.ui.view.baseView.processor;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.qunar.im.base.jsonbean.ActionRichText;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.ui.view.baseView.ActionView;
import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.ViewPool;


/**
 * Created by zhaokai on 15-9-16.
 */
public class ActionRichTextProcessor extends DefaultMessageProcessor {
    private static final String TAG = ActionRichTextProcessor.class.getSimpleName();
    @Override
    public void processChatView(ViewGroup parent,IMessageItem view) {
        try {
            final IMMessage message = view.getMessage();
            final ActionRichText action = JsonUtils.getGson().
                    fromJson(message.getBody(), ActionRichText.class);
            ActionView actionView = ViewPool.getView(ActionView.class,view.getContext());
            actionView.bindData(action);
            parent.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            parent.addView(actionView,layoutParams);
        }
        catch (Exception ex)
        {
            LogUtil.e(TAG,"ERROR",ex);
        }
    }
}
