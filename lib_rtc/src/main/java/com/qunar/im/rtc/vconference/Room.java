package com.qunar.im.rtc.vconference;


import com.qunar.im.rtc.vconference.rpc.messages.JoinRoom;
import com.qunar.im.rtc.vconference.rpc.messages.MediaError;
import com.qunar.im.rtc.vconference.rpc.messages.ParticipantEvict;
import com.qunar.im.rtc.vconference.rpc.messages.ParticipantJoined;
import com.qunar.im.rtc.vconference.rpc.messages.ParticipantLeft;
import com.qunar.im.rtc.vconference.rpc.messages.ParticipantPublished;
import com.qunar.im.rtc.vconference.rpc.messages.ParticipantUnpublish;
import com.qunar.im.rtc.vconference.rpc.messages.ReceiveIceCandadite;
import com.qunar.im.rtc.vconference.rpc.results.JoinRoomResp;
import com.qunar.im.rtc.vconference.rpc.results.LeaveRoomResp;
import com.qunar.im.rtc.vconference.rpc.results.PublishVideoResp;
import com.qunar.im.rtc.vconference.rpc.results.ReceiveVideoResp;
import com.qunar.im.rtc.vconference.rpc.results.SendIceCandidateResp;
import com.qunar.im.rtc.vconference.rpc.results.UnpublishVideoResp;
import com.qunar.im.rtc.vconference.rpc.results.UnsubscribeVideoResp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xinbo.wang on 2017-04-05.
 */
public class Room implements RpcResponse,RpcEvent {

    protected Map<String,Participant> participantMap;

    protected Participant localParticipant;

    protected RoomClient roomClient;

    protected JoinRoom.JoinRoomParams params;

    protected String localUserId;

    protected String sessionId;

    public volatile String speakerId;

    protected IConferenceView conferenceView;

    public Room(RoomClient client, JoinRoom.JoinRoomParams joinRoomParams,IConferenceView view)
    {
        this.roomClient = client;
        this.conferenceView = view;
        this.params = joinRoomParams;
        participantMap = new HashMap<>();
    }

    public void connect() throws IOException {
        JoinRoom joinRoom = new JoinRoom();
        joinRoom.params = this.params;
        joinRoom.method = "joinRoom";
        roomClient.sendRpcMessage(joinRoom);
    }

    public void leaveRoom()
    {

    }

    @Override
    public void onParticipantAdd(ParticipantJoined participantJoined) {
        Participant participant = new Participant(participantJoined.params.id);
        participantMap.put(participantJoined.params.id,participant);
    }

    @Override
    public void onParticipantPublish(ParticipantPublished participantPublished) {
        Participant participant = participantMap.get(participantPublished.params.id);
        if(participant.stream!=null) participant.stream.dispose();
        participant.stream =  new Stream(false,conferenceView);
    }

    @Override
    public void onParticipantUnpublish(ParticipantUnpublish unpublishVideo) {
        Participant participant =  participantMap.remove(unpublishVideo.params.name);
        if(participant == null)return;
        if(participant.stream!=null)participant.stream.dispose();
        if(speakerId.equals(participant.userId))
        {
            for (Participant p : participantMap.values()) {
                if (p.stream != null) {
                    p.stream.updateMainSpeaker();
                    speakerId = p.userId;
                    break;
                }
            }
        }
    }

    @Override
    public void onReceiveIceCandidate(ReceiveIceCandadite iceCandadite) {

    }

    @Override
    public void onParticipantLeft(ParticipantLeft participantLeft) {

    }

    @Override
    public void onParticipantEvict(ParticipantEvict evict) {

    }

    @Override
    public void onMediaError(MediaError error) {

    }

    @Override
    public void onJoinRoomResp(JoinRoomResp response) {

    }

    @Override
    public void onLeaveRoomResp(LeaveRoomResp response) {

    }

    @Override
    public void onPublishVideoResp(PublishVideoResp response) {

    }

    @Override
    public void onReceiveVideoResp(ReceiveVideoResp videoResp) {

    }

    @Override
    public void onSendIceCandidateResp(SendIceCandidateResp response) {

    }

    @Override
    public void onUnpublishVideoResp(UnpublishVideoResp response) {

    }

    @Override
    public void onUnsubscribeVideoResp(UnsubscribeVideoResp response) {

    }
}
