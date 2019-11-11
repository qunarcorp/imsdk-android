package com.qunar.im.rtc.vconference;

import org.webrtc.MediaStream;

/**
 * Created by xinbo.wang on 2017-04-10.
 */
public interface IConferenceView {
    void updateMainSpeaker(MediaStream remoteStream);
}
