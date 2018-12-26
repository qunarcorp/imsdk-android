package com.qunar.im.ui.view.baseView.processor;

import android.view.ViewGroup;

import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.RTCView;

/**
 * Created by wangxinbo on 2017/1/23.
 */
public class RTCProcessor extends DefaultMessageProcessor {

    @Override
    public void processChatView(ViewGroup parent, IMessageItem item) {
        RTCView rtcView = new RTCView(item.getContext());
        if(item.getMessage().getMsgType() == ProtoMessageOuterClass.MessageType.WebRTC_MsgType_Video_VALUE)
        {
            rtcView.bind(item.getContext(), true, item.getMessage());
        }
        else if(item.getMessage().getMsgType() == ProtoMessageOuterClass.MessageType.WebRTC_MsgType_Audio_VALUE)
        {
            rtcView.bind(item.getContext(), false, item.getMessage());
        }
        parent.addView(rtcView);
    }
}
