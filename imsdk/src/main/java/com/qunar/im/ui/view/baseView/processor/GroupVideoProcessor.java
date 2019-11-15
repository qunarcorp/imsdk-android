package com.qunar.im.ui.view.baseView.processor;

import android.view.ViewGroup;

import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.RTCView;

/**
 * Created by froyomu on 2019-09-05
 * <p>
 * Describe:
 */
public class GroupVideoProcessor extends DefaultMessageProcessor{
    @Override
    public void processChatView(ViewGroup parent, IMessageItem item) {
        RTCView rtcView = new RTCView(item.getContext());
        rtcView.bindGroupVideo(item.getContext(), item.getMessage());
        parent.addView(rtcView);
    }
}
