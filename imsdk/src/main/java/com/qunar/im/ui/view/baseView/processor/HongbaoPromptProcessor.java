package com.qunar.im.ui.view.baseView.processor;

import android.view.View;
import android.view.ViewGroup;

import com.qunar.im.base.jsonbean.HongbaoBroadcast;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.ui.view.baseView.HongbaoPromptView;
import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.ViewPool;
import com.qunar.im.utils.QtalkStringUtils;

/**
 * Created by saber on 16-1-6.
 */
public class HongbaoPromptProcessor extends DefaultMessageProcessor {
    @Override
    public void processChatView(ViewGroup parent,IMessageItem view) {
        final IMMessage message = view.getMessage();
        try {
            final HongbaoBroadcast broadcast = JsonUtils.getGson().
                    fromJson(message.getExt(), HongbaoBroadcast.class);
            HongbaoPromptView promptView = ViewPool.getView(HongbaoPromptView.class,view.getContext());
            promptView.bindData(broadcast,
                    QtalkStringUtils.parseBareJid(message.getFromID()),
                    message.getType()== ConversitionType.MSG_TYPE_GROUP,
                    message.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeAAInfo_VALUE);
            parent.setVisibility(View.VISIBLE);
            parent.addView(promptView);
        }catch (Exception e)
        {
            LogUtil.e(TAG,"ERROR",e);
        }
    }
}
