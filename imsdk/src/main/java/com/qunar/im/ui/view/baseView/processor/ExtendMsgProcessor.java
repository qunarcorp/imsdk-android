package com.qunar.im.ui.view.baseView.processor;

import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qunar.im.base.jsonbean.ActivityMessageEntity;
import com.qunar.im.base.jsonbean.ExtendMessageEntity;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.base.structs.MessageType;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.baseView.ExtendMsgView;
import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.ViewPool;
import com.qunar.im.ui.view.bubbleLayout.BubbleLayout;
import com.qunar.im.utils.QtalkStringUtils;

/**
 * Created by xinbo.wang on 2016/5/26.
 */
public class ExtendMsgProcessor extends DefaultMessageProcessor {
    private static final String TAG = ExtendMsgProcessor.class.getSimpleName();
    @Override
    public void processChatView(ViewGroup parent, IMessageItem item) {
        ExtendMsgView extendMsgView = ViewPool.getView(ExtendMsgView.class,item.getContext());
        IMMessage message = item.getMessage();
        String jsonStr = "";
        try {
            jsonStr =  TextUtils.isEmpty(message.getExt())?message.getBody():message.getExt();
            ExtendMessageEntity data = null;
            if (message.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeActivity_VALUE){//511 活动类型，暂时放这里
                ActivityMessageEntity activityMessageEntity = JsonUtils.getGson().fromJson(jsonStr, ActivityMessageEntity.class);
                data = new ExtendMessageEntity();
                data.title = activityMessageEntity.title;
                data.desc = activityMessageEntity.type + "-" + activityMessageEntity.intro;
                data.img = activityMessageEntity.img;
                data.linkurl = activityMessageEntity.url;
            }else{
                data = JsonUtils.getGson().fromJson(jsonStr, ExtendMessageEntity.class);
            }
            String id = QtalkStringUtils.parseBareJid(message.getConversationID());
            extendMsgView.bindData(data,id,message.getType() == ConversitionType.MSG_TYPE_GROUP,message.getMsgType() == MessageType.EXTEND_OPS_MSG);
//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT);
//            extendMsgView.setMinimumWidth(Utils.dipToPixels(item.getContext(),192));
            parent.addView(extendMsgView/*,layoutParams*/);
        }
        catch (Exception e) {
            TextView textView = ViewPool.getView(TextView.class,item.getContext());
            textView.setText(jsonStr);
            parent.addView(textView);
            LogUtil.e(TAG,"ERROR",e);
        }
    }

    @Override
    public void processBubbleView(BubbleLayout bubbleLayout, IMessageItem item) {
        bubbleLayout.setBubbleColor(ContextCompat.getColor(item.getContext(), R.color.atom_ui_chat_bubble_left_bg));
        bubbleLayout.setStrokeColor(ContextCompat.getColor(item.getContext(),R.color.atom_ui_chat_bubble_left_stoken_color));
    }
}
