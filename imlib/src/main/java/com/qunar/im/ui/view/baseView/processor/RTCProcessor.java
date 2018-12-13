package com.qunar.im.ui.view.baseView.processor;

import android.view.ViewGroup;
import android.widget.TextView;

import com.qunar.im.base.structs.MessageType;
import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.RTCView;
import com.qunar.im.ui.view.baseView.ViewPool;

/**
 * Created by wangxinbo on 2017/1/23.
 */
public class RTCProcessor extends DefaultMessageProcessor {

    @Override
    public void processChatView(ViewGroup parent, IMessageItem item) {
        RTCView rtcView = new RTCView(item.getContext());
        if(item.getMessage().getMsgType() == MessageType.MSG_TYPE_WEBRTC)
        {
            rtcView.bind(true);
        }
        else if(item.getMessage().getMsgType() == MessageType.MSG_TYPE_WEBRTC_AUDIO)
        {
            rtcView.bind(false);
        }
        parent.addView(rtcView);
    }
}
