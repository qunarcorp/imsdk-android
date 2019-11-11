package com.qunar.im.rtc.vconference;

import com.qunar.im.rtc.vconference.rpc.messages.MediaError;
import com.qunar.im.rtc.vconference.rpc.messages.ParticipantEvict;
import com.qunar.im.rtc.vconference.rpc.messages.ParticipantJoined;
import com.qunar.im.rtc.vconference.rpc.messages.ParticipantLeft;
import com.qunar.im.rtc.vconference.rpc.messages.ParticipantPublished;
import com.qunar.im.rtc.vconference.rpc.messages.ParticipantUnpublish;
import com.qunar.im.rtc.vconference.rpc.messages.ReceiveIceCandadite;

/**
 * Created by xinbo.wang on 2017-04-10.
 */
public interface RpcEvent {
    void onParticipantAdd(ParticipantJoined participantJoined);
    void onParticipantPublish(ParticipantPublished participantPublished);
    void onParticipantUnpublish(ParticipantUnpublish unpublishVideo);
    void onReceiveIceCandidate(ReceiveIceCandadite iceCandadite);
    void onParticipantLeft(ParticipantLeft participantLeft);
    void onParticipantEvict(ParticipantEvict evict);
    void onMediaError(MediaError error);
}
