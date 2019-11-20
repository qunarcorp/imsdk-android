package com.qunar.im.ui.view.baseView.processor;

import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.qunar.im.base.jsonbean.NoteMsgJson;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.NoteActionView;
import com.qunar.im.ui.view.baseView.ViewPool;
import com.qunar.im.ui.view.bubbleLayout.BubbleLayout;

/**
 * Created by saber on 16-1-27.
 */
public class NoteMessageProcessor  extends DefaultMessageProcessor {
    @Override
    public void processChatView(ViewGroup parent, IMessageItem item) {
        NoteActionView noteActionView = ViewPool.getView(NoteActionView.class,item.getContext());
        IMMessage message = item.getMessage();
        try {
            String jsonStr;
            if(TextUtils.isEmpty(message.getExt()))
            {
                jsonStr = message.getBody();
            }
            else {
                jsonStr = message.getExt();
            }
            NoteMsgJson json = JsonUtils.getGson().fromJson(jsonStr, NoteMsgJson.class);
            noteActionView.bindData(json);
            parent.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            parent.addView(noteActionView,layoutParams);
        }
        catch (Exception e)
        {
            LogUtil.e(TAG,"ERROR",e);
        }
    }

    @Override
    public void processBubbleView(BubbleLayout bubbleLayout, IMessageItem item) {
        bubbleLayout.setBubbleColor(ContextCompat.getColor(item.getContext(), R.color.atom_ui_chat_bubble_left_bg));
        bubbleLayout.setStrokeColor(ContextCompat.getColor(item.getContext(),R.color.atom_ui_chat_bubble_left_stoken_color));
    }
}
