package com.qunar.im.ui.view.baseView.processor;

import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qunar.im.base.jsonbean.ActivityMessageEntity;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.baseView.ActiveMsgView;
import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.ViewPool;
import com.qunar.im.ui.view.bubbleLayout.BubbleLayout;
import com.qunar.im.utils.QtalkStringUtils;

/**
 * Created by xinbo.wang on 2016/5/26.
 */
public class ActivityMsgProcessor extends DefaultMessageProcessor {
    private static final String TAG = ActivityMsgProcessor.class.getSimpleName();
    @Override
    public void processChatView(ViewGroup parent, IMessageItem item) {
        ActiveMsgView activeMsgView = ViewPool.getView(ActiveMsgView.class,item.getContext());
        IMMessage message = item.getMessage();
        try {

            String jsonStr =  TextUtils.isEmpty(message.getExt())?message.getBody():message.getExt();
            ActivityMessageEntity activityMessageEntity = JsonUtils.getGson().fromJson(jsonStr, ActivityMessageEntity.class);

            String id = QtalkStringUtils.parseBareJid(message.getConversationID());
            activeMsgView.bindData(activityMessageEntity,id,message.getType() == ConversitionType.MSG_TYPE_GROUP);
//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT);
//            activeMsgView.setMinimumWidth(Utils.dipToPixels(item.getContext(),192));
            parent.addView(activeMsgView/*,layoutParams*/);
        }
        catch (Exception e)
        {
            TextView textView = ViewPool.getView(TextView.class,item.getContext());
            textView.setText("消息类型错误");
            parent.addView(textView);
            LogUtil.e(TAG,"ERROR",e);
        }
    }

    @Override
    public void processBubbleView(BubbleLayout bubbleLayout, IMessageItem item) {
        bubbleLayout.setBubbleColor(ContextCompat.getColor(item.getContext(), R.color.atom_ui_white));
    }
}
