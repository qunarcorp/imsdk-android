package com.qunar.im.other;

/**
 * Created by may on 2017/8/8.
 */

public interface IQTalkLoginDelegate {
    void onSmsCodeReceived(int code, String errCode);
}
