package com.qunar.im.ui.view.baseView.processor;

import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.qunar.im.base.jsonbean.RbtSuggestionListJson;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.RbtSugesstionListView;
import com.qunar.im.ui.view.bubbleLayout.BubbleLayout;


/**
 * Created by xinbo.wang on 2016-12-06.
 */
public class RbtSuggesstionProccessor extends DefaultMessageProcessor {
    @Override
    public void processChatView(ViewGroup parent, IMessageItem item) {
        String ex = item.getMessage().getExt();
        if(!TextUtils.isEmpty(ex)&&item.getMessage().getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeRobotQuestionList_VALUE)
        {
            RbtSuggestionListJson suggestionListJson
                    = JsonUtils.getGson().fromJson(ex,RbtSuggestionListJson.class);
            if (null!=suggestionListJson){
                RbtSugesstionListView view = new RbtSugesstionListView(item.getContext());
                view.bindData(suggestionListJson,item);
                view.setBackgroundResource(R.color.atom_ui_white);
                parent.addView(view);
            }
        }
    }

    @Override
    public void processBubbleView(BubbleLayout bubbleLayout, IMessageItem item) {
        bubbleLayout.setBubbleColor(ContextCompat.getColor(item.getContext(), R.color.atom_ui_white));
    }
}
