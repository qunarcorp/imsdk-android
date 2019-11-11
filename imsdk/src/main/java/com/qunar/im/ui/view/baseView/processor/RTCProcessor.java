package com.qunar.im.ui.view.baseView.processor;

import android.view.ViewGroup;

import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.RTCView;

/**
 * Created by wangxinbo on 2017/1/23.
 */
public class RTCProcessor extends DefaultMessageProcessor {

    @Override
    public void processChatView(ViewGroup parent, IMessageItem item) {
        RTCView rtcView = new RTCView(item.getContext());
        rtcView.bind(item.getContext(), item.getMessage().getMsgType(), item.getMessage());

        parent.addView(rtcView);
    }
}
