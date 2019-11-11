package com.qunar.im.rtc.vconference;

import com.qunar.im.rtc.vconference.rpc.results.JoinRoomResp;
import com.qunar.im.rtc.vconference.rpc.results.LeaveRoomResp;
import com.qunar.im.rtc.vconference.rpc.results.PublishVideoResp;
import com.qunar.im.rtc.vconference.rpc.results.ReceiveVideoResp;
import com.qunar.im.rtc.vconference.rpc.results.SendIceCandidateResp;
import com.qunar.im.rtc.vconference.rpc.results.UnpublishVideoResp;
import com.qunar.im.rtc.vconference.rpc.results.UnsubscribeVideoResp;

/**
 * Created by xinbo.wang on 2017-04-05.
 */
public interface RpcResponse {
    void onJoinRoomResp(JoinRoomResp response);
    void onLeaveRoomResp(LeaveRoomResp response);
    void onPublishVideoResp(PublishVideoResp response);
    void onReceiveVideoResp(ReceiveVideoResp videoResp);
    void onSendIceCandidateResp(SendIceCandidateResp response);
    void onUnpublishVideoResp(UnpublishVideoResp response);
    void onUnsubscribeVideoResp(UnsubscribeVideoResp response);
}
