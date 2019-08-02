package com.qunar.im.ui.view.baseView.processor;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.qunar.im.base.module.IMMessage;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.RobOrderView;
import com.qunar.im.ui.view.baseView.ViewPool;

/**
 * Created by lihaibin.li on 2017/10/25.
 */

public class RobOrderProcessor extends DefaultMessageProcessor {
    @Override
    public void processChatView(ViewGroup parent, IMessageItem item) {
        IMMessage imMessage = item.getMessage();
        if (imMessage.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeGrabMenuResult_VALUE){
            parent.setVisibility(View.GONE);
            return;
        }
        RobOrderView robOrderView = ViewPool.getView(RobOrderView.class, item.getContext());
        IMMessage message = item.getMessage();
        robOrderView.bindData(message);
        parent.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        parent.addView(robOrderView, layoutParams);
    }
}
