package com.qunar.im.ui.view.baseView.processor;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.qunar.im.base.jsonbean.RichText;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.RichActionView;
import com.qunar.im.ui.view.baseView.ViewPool;

/**
 * Created by zhaokai on 15-9-16.
 */
public class RichTextProcessor extends DefaultMessageProcessor {
    @Override
    public void processChatView(ViewGroup parent, IMessageItem item) {
        try {
            final RichText text = JsonUtils.getGson().fromJson(item.getMessage().getBody(), RichText.class);
            RichActionView view = ViewPool.getView(RichActionView.class,item.getContext());
            view.bindData(text);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            parent.addView(view,layoutParams);
            parent.setVisibility(View.VISIBLE);
        }
        catch (Exception ex)
        {
            LogUtil.e(TAG,"ERROR",ex);
        }
    }

}
