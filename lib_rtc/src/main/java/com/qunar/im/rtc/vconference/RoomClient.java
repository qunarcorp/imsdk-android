package com.qunar.im.rtc.vconference;

import android.text.TextUtils;
import android.util.SparseArray;

import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.rtc.vconference.rpc.messages.MediaError;
import com.qunar.im.rtc.vconference.rpc.messages.ParticipantEvict;
import com.qunar.im.rtc.vconference.rpc.messages.ParticipantJoined;
import com.qunar.im.rtc.vconference.rpc.messages.ParticipantLeft;
import com.qunar.im.rtc.vconference.rpc.messages.ParticipantPublished;
import com.qunar.im.rtc.vconference.rpc.messages.ParticipantUnpublish;
import com.qunar.im.rtc.vconference.rpc.messages.ReceiveIceCandadite;
import com.qunar.im.rtc.vconference.rpc.messages.RpcJson;
import com.qunar.im.rtc.vconference.rpc.results.JoinRoomResp;
import com.qunar.im.rtc.vconference.rpc.results.LeaveRoomResp;
import com.qunar.im.rtc.vconference.rpc.results.PublishVideoResp;
import com.qunar.im.rtc.vconference.rpc.results.ReceiveVideoResp;
import com.qunar.im.rtc.vconference.rpc.results.SendIceCandidateResp;
import com.qunar.im.rtc.vconference.rpc.results.UnpublishVideoResp;
import com.qunar.im.rtc.vconference.rpc.results.UnsubscribeVideoResp;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Created by xinbo.wang on 2017-04-05.
 */
public class RoomClient {
    private String wss;
    private Room room;

    protected AtomicInteger autoIncreaseId = new AtomicInteger(0);

    private String userName;
    private OkHttpClient wssClient;

    private WebSocket webSocket;

    private SparseArray<String> sendedMsg = new SparseArray<String>();

    public RoomClient(String wss)
    {
        this.wss = wss;
    }

    public void sendRpcMessage(RpcJson json) throws IOException {
        String jsonText = JsonUtils.getGson().toJson(json);
        json.id = autoIncreaseId.incrementAndGet();
        sendedMsg.append(json.id,json.method);
//        RequestBody body = RequestBody.create(WebSocket.TEXT,jsonText);
//        webSocket.sendMessage(body);
        webSocket.send(jsonText);
    }

    public void initJsonRpcClient(final RpcResponse response, final RpcEvent event)
    {
        wssClient = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
                                .url(wss)
                                .build();
        webSocket = wssClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                RpcJson json = JsonUtils.getGson().fromJson(text,RpcJson.class);
                String sendedMsgMethod = sendedMsg.get(json.id);
                if(!TextUtils.isEmpty(sendedMsgMethod))
                {
                    sendedMsg.delete(json.id);
                    if("joinRoom".equals(sendedMsgMethod))
                    {
                        JoinRoomResp roomResp = JsonUtils.getGson().fromJson(text,JoinRoomResp.class);
                        response.onJoinRoomResp(roomResp);
                    }
                    else if("publishVideo".equals(sendedMsgMethod))
                    {
                        PublishVideoResp resp = JsonUtils.getGson().fromJson(text,PublishVideoResp.class);
                        response.onPublishVideoResp(resp);
                    }
                    else if("unpublishVideo".equals(sendedMsgMethod))
                    {
                        UnpublishVideoResp resp = JsonUtils.getGson().fromJson(text,UnpublishVideoResp.class);
                        response.onUnpublishVideoResp(resp);
                    }
                    else if("receiveVideoFrom".equals(sendedMsgMethod))
                    {
                        ReceiveVideoResp receiveVideoResp = JsonUtils.getGson().fromJson(text,ReceiveVideoResp.class);
                        response.onReceiveVideoResp(receiveVideoResp);
                    }
                    else if("unsubscribeFromVideo".equals(sendedMsgMethod))
                    {
                        UnsubscribeVideoResp resp = JsonUtils.getGson().fromJson(text,UnsubscribeVideoResp.class);
                        response.onUnsubscribeVideoResp(resp);
                    }
                    else if("onIceCandidate".equals(sendedMsgMethod))
                    {
                        SendIceCandidateResp resp = JsonUtils.getGson().fromJson(text,SendIceCandidateResp.class);
                        response.onSendIceCandidateResp(resp);
                    }
                    else if("leaveRoom".equals(sendedMsgMethod))
                    {
                        LeaveRoomResp resp = JsonUtils.getGson().fromJson(text,LeaveRoomResp.class);
                        response.onLeaveRoomResp(resp);
                    }
                }
                else {
                    if("participantJoined".equals(json.method))
                    {
                        ParticipantJoined participantJoined = JsonUtils.getGson().fromJson(text,ParticipantJoined.class);
                        event.onParticipantAdd(participantJoined);
                    }
                    else if("participantPublished".equals(json.method))
                    {
                        ParticipantPublished participantPublished = JsonUtils.getGson().fromJson(text,ParticipantPublished.class);
                        event.onParticipantPublish(participantPublished);
                    }
                    else if("participantUnpublished".equals(json.method))
                    {
                        ParticipantUnpublish unpublishVideo = JsonUtils.getGson().fromJson(text,ParticipantUnpublish.class);
                        event.onParticipantUnpublish(unpublishVideo);
                    }
                    else if("iceCandidate".equals(json.method))
                    {
                        ReceiveIceCandadite iceCandadite = JsonUtils.getGson().fromJson(text,ReceiveIceCandadite.class);
                        event.onReceiveIceCandidate(iceCandadite);
                    }
                    else if("participantLeft".equals(json.method))
                    {
                        ParticipantLeft participantLeft = JsonUtils.getGson().fromJson(text,ParticipantLeft.class);
                        event.onParticipantLeft(participantLeft);
                    }
                    else if("participantEvicted".equals(json.method))
                    {
                        ParticipantEvict participantEvict = JsonUtils.getGson().fromJson(text,ParticipantEvict.class);
                        event.onParticipantEvict(participantEvict);
                    }
                    else if("mediaError".equals(json.method))
                    {
                        MediaError mediaError = JsonUtils.getGson().fromJson(text,MediaError.class);
                        event.onMediaError(mediaError);
                    }
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
            }
        });

    }
}
