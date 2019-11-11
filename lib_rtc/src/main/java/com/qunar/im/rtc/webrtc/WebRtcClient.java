package com.qunar.im.rtc.webrtc;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.jsonbean.WebRtcJson;
import com.qunar.im.base.jsonbean.WebrtcMessageExtention;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.structs.MessageType;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.MessageUtils;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.rtc.activity.RtcActivity;
import com.qunar.im.rtc.presenter.IP2pRTC;
import com.qunar.im.utils.ConnectionUtil;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.CameraEnumerationAndroid;
import org.webrtc.DataChannel;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.MediaStreamTrack;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;
import org.webrtc.voiceengine.WebRtcAudioManager;
import org.webrtc.voiceengine.WebRtcAudioUtils;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import org.webrtc.CameraEnumerationAndroid;
//import org.webrtc.voiceengine.WebRtcAudioManager;

public class WebRtcClient implements IP2pRTC {

    public static final String VIDEO_TRACK_ID = "ARDAMSv0";
    public static final String AUDIO_TRACK_ID = "ARDAMSa0";
    public static final String VIDEO_TRACK_TYPE = "video";
    public static final String VIDEO_CODEC_VP8 = "VP8";
    public static final String VIDEO_CODEC_VP9 = "VP9";
    public static final String VIDEO_CODEC_H264 = "H264";
    public static final String AUDIO_CODEC_OPUS = "opus";
    public static final String AUDIO_CODEC_ISAC = "ISAC";
    private static final String VIDEO_CODEC_PARAM_START_BITRATE = "x-google-start-bitrate";
    private static final String VIDEO_FLEXFEC_FIELDTRIAL = "WebRTC-FlexFEC-03/Enabled/";
    private static final String AUDIO_CODEC_PARAM_BITRATE = "maxaveragebitrate";
    private static final String AUDIO_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation";
    private static final String AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl";
    private static final String AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter";
    private static final String AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression";
    private static final String AUDIO_LEVEL_CONTROL_CONSTRAINT = "levelControl";
    private static final String DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT = "DtlsSrtpKeyAgreement";
    private static final String ICE_RESTART = "IceRestart";
    private static final String OFFER_TO_RECEIVE_VIDEO = "OfferToReceiveVideo";
    private static final String OFFER_TO_RECEIVE_AUDIO = "OfferToReceiveAudio";
    private static final int HD_VIDEO_WIDTH = 1280;
    private static final int HD_VIDEO_HEIGHT = 720;
    private static final int BPS_IN_KBPS = 1000;

//    private static final int MSG_TYPE_WEBRTC = 8;

    private final static String TAG = WebRtcClient.class.getSimpleName();
    private final boolean isCaller;
    private PeerConnectionFactory factory;
    private Peer peer;
    private LinkedList<PeerConnection.IceServer> iceServers = new LinkedList<>();
    private PeerConnectionParameters pcParams;
    private MediaConstraints pcConstraints = new MediaConstraints();
    private MediaStream localMS;
    private VideoTrack videoTrack;
    private AudioTrack audioTrack;
    private VideoSource videoSource;
    private AudioSource audioSource;
    private VideoCapturerAndroid videoCapturer;
    private RtcListener mListener;
    private HashMap<String, Command> commandMap;

    private SessionDescription localSdp;
    private boolean isError;

    private boolean preferIsac;
    private String preferredVideoCodec;

    private String from, to, chatType, realJid;

    PeerConnectionFactory.Options options = null;

    //public volatile boolean videoMute;
    //List<IceCandidate> iceCache = new ArrayList<>();

    /**
     * Implement this interface to be notified of events.
     */
    public interface RtcListener {
        void onStatusChanged(WebRTCStatus newStatus);

        void onLocalStream(MediaStream localStream);

        void onAddRemoteStream(MediaStream remoteStream);

        void onRemoveRemoteStream();
    }

    private interface Command {
        void execute(IMMessage imMessage, WebRtcJson.WebRtcPayload payload);
    }

    private class CreateOfferCommand implements Command {
        public void execute(IMMessage imMessage, WebRtcJson.WebRtcPayload payload) {
            LogUtil.d(TAG, "CreateOfferCommand");
            peer.pc.createOffer(peer, pcConstraints);
            Logger.i(TAG + "音视频连接：" + "createOffer");
            if (mListener != null) {
                mListener.onStatusChanged(WebRTCStatus.CONNECTING);
            }
        }
    }

    private class CreateAnswerCommand implements Command {
        public void execute(IMMessage imMessage, WebRtcJson.WebRtcPayload payload) {
            LogUtil.d(TAG, "CreateAnswerCommand");
            Logger.i(TAG + "音视频连接：" + "createAnswer");
            payload.sdp = payload.sdp.replace("\\r\\n","\r\n");
            SessionDescription sdp = new SessionDescription(
                    SessionDescription.Type.fromCanonicalForm(payload.type),
                    payload.sdp
            );
            setRemoteSDP(sdp);
            peer.pc.createAnswer(peer, pcConstraints);

            if (mListener != null) {
                mListener.onStatusChanged(WebRTCStatus.CONNECTING);
            }
        }
    }

    private class SetRemoteSDPCommand implements Command {
        public void execute(IMMessage imMessage, WebRtcJson.WebRtcPayload payload) {
            Logger.i(TAG + "音视频连接：" + "setRemoteSDP");
            LogUtil.d(TAG, "SetRemoteSDPCommand");
            payload.sdp = payload.sdp.replace("\\r\\n","\r\n");
            SessionDescription sdp = new SessionDescription(
                    SessionDescription.Type.fromCanonicalForm(payload.type),
                    payload.sdp
            );
            setRemoteSDP(sdp);
        }
    }

    private class AddIceCandidateCommand implements Command {
        public void execute(IMMessage imMessage, WebRtcJson.WebRtcPayload payload) {
            LogUtil.d(TAG, "AddIceCandidate: " + payload.candidate);
            Logger.i(TAG + "音视频连接：" + "AddIceCandidateCommand: " + payload.candidate);
            PeerConnection pc = peer.pc;
            IceCandidate candidate = new IceCandidate(
                    payload.id,
                    payload.label,
                    payload.candidate
            );
            if (pc.getRemoteDescription() != null) {

                pc.addIceCandidate(candidate);
            }
            /*else {
                iceCache.add(candidate);
            }*/
        }
    }

    private class CancelCommand implements Command {
        public void execute(IMMessage imMessage, WebRtcJson.WebRtcPayload payload) {
            LogUtil.d(TAG, "CancelCommand");
            Logger.i(TAG + "音视频连接：" + "CancelCommand");
            if (mListener != null) {
                mListener.onStatusChanged(WebRTCStatus.CANCEL);
            }
            if(!isCaller && !imMessage.isCarbon()) {
                sendNormalMessage(WebRTCStatus.CANCEL.getType(), 0, "");
            }
        }
    }

    private class PickupCommand implements Command {
        public void execute(IMMessage imMessage, WebRtcJson.WebRtcPayload payload) {
            LogUtil.d(TAG, "PickupCommand");
            Logger.i(TAG + "音视频连接：" + "PickupCommand");
            if (mListener != null && imMessage.isCarbon()) {//pickup的carbon需要处理，将视频页面关闭，对方发的pickup可以不用处理
                mListener.onStatusChanged(WebRTCStatus.PICKUP);
            }
        }
    }

    private class DenyCommand implements Command {
        public void execute(IMMessage imMessage, WebRtcJson.WebRtcPayload payload) {
            LogUtil.d(TAG, "DenyCommand");
            Logger.i(TAG + "音视频连接：" + "DenyCommand");
            if (mListener != null) {
                mListener.onStatusChanged(WebRTCStatus.DENY);
            }
            if(!isCaller && !imMessage.isCarbon()) {
                sendNormalMessage(WebRTCStatus.DENY.getType(), 0, "");
            }
        }
    }

    private class CloseCommand implements Command {
        public void execute(IMMessage imMessage, WebRtcJson.WebRtcPayload payload) {
            Logger.i(TAG + "音视频连接：" + "CloseCommand");
            LogUtil.d(TAG, "CloseCommand");
            if (mListener != null && !imMessage.isCarbon()) {
                mListener.onStatusChanged(WebRTCStatus.CLOSE);
            }
        }
    }


    private class BusyCommand implements Command {
        public void execute(IMMessage imMessage, WebRtcJson.WebRtcPayload payload) {
            LogUtil.d(TAG, "BusyCommand");
            Logger.i(TAG + "音视频连接：" + "BusyCommand");
            if (mListener != null) {
                mListener.onStatusChanged(WebRTCStatus.BUSY);
            }
//            if(!isCaller) {
//                sendNormalMessage(WebRTCStatus.BUSY.getType(), "", "");
//            }
        }
    }

    private class TimeoutCommand implements Command {
        public void execute(IMMessage imMessage, WebRtcJson.WebRtcPayload payload) {
            LogUtil.d(TAG, "BusyCommand");
            Logger.i(TAG + "音视频连接：" + "BusyCommand");
            if (mListener != null) {
                mListener.onStatusChanged(WebRTCStatus.TIMEOUT);
            }
//            if(!isCaller) {
//                sendNormalMessage(WebRTCStatus.BUSY.getType(), "", "");
//            }
        }
    }

    private void setRemoteSDP(SessionDescription remoteSDP) {
        if (peer.pc == null || isError) {
            return;
        }
        String sdpDescription = remoteSDP.description;
        if (preferIsac) {
            sdpDescription = preferCodec(sdpDescription, AUDIO_CODEC_ISAC, true);
        }
        if (pcParams.videoCallEnabled) {
            sdpDescription = preferCodec(sdpDescription, preferredVideoCodec, false);
        }
        if (pcParams.audioStartBitrate > 0) {
            sdpDescription = setStartBitrate(
                    AUDIO_CODEC_OPUS, false, sdpDescription, pcParams.audioStartBitrate);
        }
        if(pcParams.videoMaxBitrate >0)
        {
            sdpDescription = setStartBitrate(preferredVideoCodec,true,sdpDescription,
                    pcParams.videoMaxBitrate);
        }
        sdpDescription = setMaxFs(sdpDescription);
        LogUtil.d(TAG, "Set remote SDP.");
        Logger.i(TAG + " Set remote SDP.");
        SessionDescription sdpRemote = new SessionDescription(remoteSDP.type, sdpDescription);
        peer.pc.setRemoteDescription(peer, sdpRemote);
    }

    private static String setMaxFs(String sdpDescription) {
        String[] sdpLine = sdpDescription.split("\r\n");
        StringBuilder myNewSdp = new StringBuilder();
        for (String line : sdpLine) {
            if (line.startsWith("a=fmtp:")) {
                String[] temp = line.split("^a=fmtp:\\d+(\\s)*");

                if (temp.length==0 || temp[1].isEmpty()) {
                    myNewSdp.append(line);
                    myNewSdp.append(" max-fs=12288;max-fr=60");
                    myNewSdp.append("\r\n");
                } else {
                    myNewSdp.append(line);
                    myNewSdp.append(";max-fs=12288;max-fr=60");
                    myNewSdp.append("\r\n");
                }
            } else {
                myNewSdp.append(line);
                myNewSdp.append("\r\n");
            }
        }
        return  myNewSdp.toString();
    }

    private static String setStartBitrate(
            String codec, boolean isVideoCodec, String sdpDescription, int bitrateKbps) {
        String[] lines = sdpDescription.split("\r\n");
        int rtpmapLineIndex = -1;
        boolean sdpFormatUpdated = false;
        String codecRtpMap = null;
        // Search for codec rtpmap in format
        // a=rtpmap:<payload type> <encoding name>/<clock rate> [/<encoding parameters>]
        String regex = "^a=rtpmap:(\\d+) " + codec + "(/\\d+)+[\r]?$";
        Pattern codecPattern = Pattern.compile(regex);
        for (int i = 0; i < lines.length; i++) {
            Matcher codecMatcher = codecPattern.matcher(lines[i]);
            if (codecMatcher.matches()) {
                codecRtpMap = codecMatcher.group(1);
                rtpmapLineIndex = i;
                break;
            }
        }
        if (codecRtpMap == null) {
            Log.w(TAG, "No rtpmap for " + codec + " codec");
            return sdpDescription;
        }
        LogUtil.d(TAG, "Found " + codec + " rtpmap " + codecRtpMap + " at " + lines[rtpmapLineIndex]);

        // Check if a=fmtp string already exist in remote SDP for this codec and
        // update it with new bitrate parameter.
        regex = "^a=fmtp:" + codecRtpMap + " \\w+=\\d+.*[\r]?$";
        codecPattern = Pattern.compile(regex);
        for (int i = 0; i < lines.length; i++) {
            Matcher codecMatcher = codecPattern.matcher(lines[i]);
            if (codecMatcher.matches()) {
                LogUtil.d(TAG, "Found " + codec + " " + lines[i]);
                if (isVideoCodec) {
                    lines[i] += "; " + VIDEO_CODEC_PARAM_START_BITRATE + "=" + bitrateKbps;
                } else {
                    lines[i] += "; " + AUDIO_CODEC_PARAM_BITRATE + "=" + (bitrateKbps * 1000);
                }
                LogUtil.d(TAG, "Update remote SDP line: " + lines[i]);
                sdpFormatUpdated = true;
                break;
            }
        }

        StringBuilder newSdpDescription = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            newSdpDescription.append(lines[i]).append("\r\n");
            // Append new a=fmtp line if no such line exist for a codec.
            if (!sdpFormatUpdated && i == rtpmapLineIndex) {
                String bitrateSet;
                if (isVideoCodec) {
                    bitrateSet =
                            "a=fmtp:" + codecRtpMap + " " + VIDEO_CODEC_PARAM_START_BITRATE + "=" + bitrateKbps;
                } else {
                    bitrateSet = "a=fmtp:" + codecRtpMap + " " + AUDIO_CODEC_PARAM_BITRATE + "="
                            + (bitrateKbps * 1000);
                }
                LogUtil.d(TAG, "Add remote SDP line: " + bitrateSet);
                newSdpDescription.append(bitrateSet).append("\r\n");
            }
        }
        return newSdpDescription.toString();
    }

    /**
     * Send a message through the signaling server
     *
     * @param type    type of message
     * @param payload payload of message
     */
    public void sendWebrtcMessage(String type, WebRtcJson.WebRtcPayload payload) {
        LogUtil.d(TAG, "send message " + type);
        Logger.i(TAG + " send message " + type);
        WebRtcJson webRtcJson = new WebRtcJson();
        webRtcJson.type = type;
        webRtcJson.payload = payload;
        IMMessage message = generateIMMessage();
        message.setBody("video command");
        message.setType(ProtoMessageOuterClass.SignalType.SignalTypeWebRtc_VALUE);
        message.setExt(JsonUtils.getGson().toJson(webRtcJson));
        ConnectionUtil.getInstance().sendWebrtcMessage(message);
//        ConnectionUtil.getInstance().sendTextOrEmojiMessage(message);
    }

    public void sendNormalMessage(String type, long time, String describe) {
        WebrtcMessageExtention webrtcMessageExtention = new WebrtcMessageExtention();
        webrtcMessageExtention.type = type;
        if(!TextUtils.isEmpty(describe)) {
            webrtcMessageExtention.des = describe;
        }
        if(time > 0) {
            webrtcMessageExtention.time = time;
        }
        IMMessage message = generateIMMessage();
        message.setMsgType(pcParams.videoCallEnabled ? ProtoMessageOuterClass.MessageType.WebRTC_MsgType_VideoCall_VALUE : ProtoMessageOuterClass.MessageType.WebRTC_MsgType_AudioCall_VALUE);
        message.setBody("当前客户端版本不支持音视频");
        message.setExt(JsonUtils.getGson().toJson(webrtcMessageExtention));
        ConnectionUtil.getInstance().sendTextOrEmojiMessage(message);
        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.SEND_MESSAGE_RENDER, message);
    }

    private class Peer implements SdpObserver, PeerConnection.Observer {
        private PeerConnection pc;

        @Override
        public void onCreateSuccess(final SessionDescription sdp) {
            // TODO: modify sdp to use pcParams prefered codecs
            LogUtil.d(TAG, "onCreateSuccess");
            Logger.i(TAG + "音视频连接：" + "onCreateSuccess");
            if (localSdp != null) {
                return;
            }
            String sdpDescription = sdp.description;
            if (pcParams.audioCodec != null
                    && pcParams.audioCodec.equals(AUDIO_CODEC_ISAC)) {
                sdpDescription = preferCodec(sdpDescription, AUDIO_CODEC_ISAC, true);
            }
            if (pcParams.videoCallEnabled) {
                sdpDescription = preferCodec(sdpDescription, preferredVideoCodec, false);
            }

            sdpDescription = setMaxFs(sdpDescription);

            final SessionDescription newSdp = new SessionDescription(sdp.type, sdpDescription);
            localSdp = newSdp;
            WebRtcJson.WebRtcPayload payload = new WebRtcJson.WebRtcPayload();
            payload.sdp = newSdp.description.replace("\r\n","\\r\\n");
            payload.type = newSdp.type.canonicalForm();
            Logger.i(TAG + "音视频连接：" + "发送" + newSdp.type.canonicalForm());
            sendWebrtcMessage(newSdp.type.canonicalForm(), payload);
            pc.setLocalDescription(Peer.this, newSdp);
        }

        @Override
        public void onSetSuccess() {
            LogUtil.d(TAG,"onSetSuccess");
            Logger.i(TAG + " onSetSuccess");
            /*if(pc.getRemoteDescription()!=null&&iceCache.size()>0)
                for(IceCandidate iceCandidate:iceCache)
                    pc.addIceCandidate(iceCandidate);

            iceCache.clear();*/
        }

        @Override
        public void onCreateFailure(String s) {
            LogUtil.d(TAG,"onCreateFailure value :"+s);
            Logger.i(TAG + " onCreateFailure value :"+s);
        }

        @Override
        public void onSetFailure(String s) {
            LogUtil.d(TAG,"onSetFailure value :"+s);
            Logger.i(TAG + " onSetFailure value :"+s);
        }

        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {
            LogUtil.d(TAG,"onSignalingChange value :"+signalingState);
            Logger.i(TAG + " onSignalingChange value :"+signalingState);
        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
            if (iceConnectionState == PeerConnection.IceConnectionState.DISCONNECTED) {
                removePeer();
                if(mListener != null)
                    mListener.onStatusChanged(WebRTCStatus.DISCONNECT);
            } else if(iceConnectionState == PeerConnection.IceConnectionState.CONNECTED) {
                if(mListener != null)
                    mListener.onStatusChanged(WebRTCStatus.CONNECT);
            }
            else if(iceConnectionState == PeerConnection.IceConnectionState.FAILED) {
                if(mListener != null)
                    mListener.onStatusChanged(WebRTCStatus.CONNECTING);
            }
            Logger.d(TAG + "  ice change :"+iceConnectionState);
        }

        @Override
        public void onIceConnectionReceivingChange(boolean b) {
            LogUtil.d(TAG,"onIceConnectionReceivingChange value :"+b);
            Logger.i(TAG + " onIceConnectionReceivingChange value :"+b);
        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
            LogUtil.d(TAG,"onIceGatheringChange :"+iceGatheringState);
            Logger.i(TAG +" onIceGatheringChange :"+iceGatheringState);
        }

        @Override
        public void onIceCandidate(final IceCandidate candidate) {
            LogUtil.d(TAG + candidate.sdp);
            Logger.i(TAG + " onIceCandidate" + candidate.sdp);
            WebRtcJson.WebRtcPayload payload = new WebRtcJson.WebRtcPayload();
            payload.candidate = candidate.sdp;
            payload.id = candidate.sdpMid;
            payload.label = candidate.sdpMLineIndex;
            Logger.i(TAG + " SEND ICE: " + JsonUtils.getGson().toJson(payload));
            sendWebrtcMessage("candidate", payload);
        }

        @Override
        public void onAddStream(MediaStream mediaStream) {
            LogUtil.d(TAG, "onAddStream " + mediaStream.label());
            Logger.i(TAG + " onAddStream " + mediaStream.label());
            if(mListener != null)
                mListener.onAddRemoteStream(mediaStream);
        }

        @Override
        public void onRemoveStream(MediaStream mediaStream) {
            LogUtil.d(TAG, "onRemoveStream " + mediaStream.label());
            Logger.i(TAG + " onRemoveStream " + mediaStream.label());
            removePeer();
        }

        @Override
        public void onDataChannel(DataChannel dataChannel) {
            LogUtil.d(TAG,"onDataChannel");
            Logger.i(TAG + " onDataChannel");
        }

        @Override
        public void onRenegotiationNeeded() {
            LogUtil.d(TAG,"onRenegotiationNeeded");
            Logger.i(TAG + " onRenegotiationNeeded");
        }

        public Peer(EglBase.Context context) {
            PeerConnection.RTCConfiguration rtcConfig =
                    new PeerConnection.RTCConfiguration(iceServers);
            if(pcParams.videoCallEnabled) {
                factory.setVideoHwAccelerationOptions(context, context);
            }
//            rtcConfig.iceTransportsType = PeerConnection.IceTransportsType.ALL;
            rtcConfig.iceTransportsType = PeerConnection.IceTransportsType.RELAY;
            //禁用TCP, turn服务器ice协议使用UDP
            rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.ENABLED;
            rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
            rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.NEGOTIATE;
            rtcConfig.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY;
            // Use ECDSA encryption.
            rtcConfig.keyType = PeerConnection.KeyType.ECDSA;
            this.pc = factory.createPeerConnection(rtcConfig, pcConstraints, this);
            pc.addStream(localMS);
            if(mListener != null)
                mListener.onStatusChanged(WebRTCStatus.CONNECTING);
        }
    }

    private void removePeer() {
        if(mListener != null) {
            mListener.onRemoveRemoteStream();
        }
        peer.pc.close();
    }

    public void connect(EglBase.Context context, WebRtcIce ice) {
        if (ice != null&&ice.error==0) {
            for (WebRtcIce.IceServers serv : ice.serverses) {
                if(serv == null){
                    continue;
                }
                boolean withAuth = !TextUtils.isEmpty(serv.username)&&
                        !TextUtils.isEmpty(serv.password);
                if(serv.uris != null && serv.uris.size() > 0){
                    for (String uri:serv.uris) {
                        if(withAuth)
                        {
                            iceServers.add(new PeerConnection.IceServer(uri, serv.username, serv.password));
                        }
                        else {
                            iceServers.add(new PeerConnection.IceServer(uri));
                        }
                    }
                }
            }
        }
        peer = new Peer(context);
    }

    public WebRtcClient(Context context,
                        RtcListener listener, PeerConnectionParameters params, Bundle bundle, boolean isCaller) {
        this.isCaller = isCaller;
        from =  bundle.getString(RtcActivity.INTENT_KEY_FROM);
        to =  bundle.getString(RtcActivity.INTENT_KEY_TO);
        chatType =  bundle.getString(RtcActivity.INTENT_KEY_CHATTYPE);
        realJid =  bundle.getString(RtcActivity.INTENT_KEY_REALJID);
        mListener = listener;
        pcParams = params;
        this.commandMap = new HashMap<>();
        commandMap.put("init", new CreateOfferCommand());
        commandMap.put("offer", new CreateAnswerCommand());
        commandMap.put("answer", new SetRemoteSDPCommand());
        commandMap.put("candidate", new AddIceCandidateCommand());
        commandMap.put(WebRTCStatus.CANCEL.getType(), new CancelCommand());
        commandMap.put(WebRTCStatus.PICKUP.getType(), new PickupCommand());
        commandMap.put(WebRTCStatus.DENY.getType(), new DenyCommand());
        commandMap.put(WebRTCStatus.CLOSE.getType(), new CloseCommand());
        commandMap.put(WebRTCStatus.BUSY.getType(),new BusyCommand());
        commandMap.put(WebRTCStatus.TIMEOUT.getType(),new TimeoutCommand());
        // 声音编码
        preferIsac = pcParams.audioCodec != null
                && pcParams.audioCodec.equals(AUDIO_CODEC_ISAC);

        PeerConnectionFactory.initializeInternalTracer();
        if (pcParams.tracing) {
            PeerConnectionFactory.startInternalTracingCapture(
                    Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                            + "webrtc-trace.txt");
        }
        LogUtil.d(TAG,
                "Create peer connection factory. Use video: " + pcParams.videoCallEnabled);
        Logger.i(TAG +
                " Create peer connection factory. Use video: " + pcParams.videoCallEnabled);
        isError = false;

        // Initialize field trials.
        if (pcParams.videoFlexfecEnabled) {
            PeerConnectionFactory.initializeFieldTrials(VIDEO_FLEXFEC_FIELDTRIAL);
            LogUtil.d(TAG, "Enable FlexFEC field trial.");
            Logger.i(TAG + " Enable FlexFEC field trial.");
        } else {
            PeerConnectionFactory.initializeFieldTrials("");
        }

        // 视频编码
        preferredVideoCodec = VIDEO_CODEC_VP9;
        if (pcParams.videoCallEnabled && pcParams.videoCodec != null) {
            if (pcParams.videoCodec.equals(VIDEO_CODEC_VP8)) {
                preferredVideoCodec = VIDEO_CODEC_VP8;
            } else if (pcParams.videoCodec.equals(VIDEO_CODEC_H264)) {
                preferredVideoCodec = VIDEO_CODEC_H264;
            }
        }
        LogUtil.d(TAG, "Preferred video codec: " + preferredVideoCodec);

        pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair(OFFER_TO_RECEIVE_AUDIO, "true"));
        if (params.videoCallEnabled) {
            pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair(OFFER_TO_RECEIVE_VIDEO, "true"));
        } else {
            pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair(OFFER_TO_RECEIVE_VIDEO, "false"));
        }
        pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair(ICE_RESTART,"true"));
        pcConstraints.optional.add(new MediaConstraints.KeyValuePair(DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT, "true"));

        // Enable/disable OpenSL ES playback.
        if (!pcParams.useOpenSLES) {
            LogUtil.d(TAG, "Disable OpenSL ES audio even if device supports it");
            WebRtcAudioManager.setBlacklistDeviceForOpenSLESUsage(true);

        } else {
            LogUtil.d(TAG, "Allow OpenSL ES audio if device supports it");
            WebRtcAudioManager.setBlacklistDeviceForOpenSLESUsage(false);
        }

        if (pcParams.disableBuiltInAEC) {
            LogUtil.d(TAG, "Disable built-in AEC even if device supports it");
            WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(true);
        } else {
            LogUtil.d(TAG, "Enable built-in AEC if device supports it");
            WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(false);
        }

        if (pcParams.disableBuiltInAGC) {
            LogUtil.d(TAG, "Disable built-in AGC even if device supports it");
            WebRtcAudioUtils.setWebRtcBasedAutomaticGainControl(true);
        } else {
            LogUtil.d(TAG, "Enable built-in AGC if device supports it");
            WebRtcAudioUtils.setWebRtcBasedAutomaticGainControl(false);
        }
        if(pcParams.audioStartBitrate>0)
            WebRtcAudioUtils.setDefaultSampleRateHz(pcParams.audioStartBitrate*1000);


        if (pcParams.disableBuiltInNS) {
            LogUtil.d(TAG, "Disable built-in NS even if device supports it");
            WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(true);
        } else {
            LogUtil.d(TAG, "Enable built-in NS if device supports it");
            WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(false);
        }

        // Create peer connection factory.
        if (!PeerConnectionFactory.initializeAndroidGlobals(
                context, true, pcParams.videoCallEnabled, pcParams.videoCodecHwAcceleration)) {
        }
        //PeerConnectionFactory.initializeAndroidGlobals(listener, true, true,
        //       params.videoCodecHwAcceleration, mEGLcontext);
        factory = new PeerConnectionFactory();
    }

    public void setPeerConnectionFactoryOptions(PeerConnectionFactory.Options options) {
        this.options = options;
        if (options != null) {
            LogUtil.d(TAG, "Factory networkIgnoreMask option: " + options.networkIgnoreMask);
            factory.setOptions(options);
        }
    }


    /**
     * Call this method in Activity.onPause()
     */
    public void onPause() {
        if (videoSource != null) videoSource.stop();
    }

    /**
     * Call this method in Activity.onResume()
     */
    public void onResume() {
        if (videoSource != null) videoSource.restart();
    }

    /**
     * Call this method in Activity.onDestroy()
     */
    public void onDestroy() {
        if (peer != null && peer.pc != null) peer.pc.dispose();
        if (videoSource != null) {
            videoSource.stop();
//            videoSource.dispose();
        }
        if (videoCapturer != null) {
            videoCapturer.dispose();
        }
        if (audioSource != null) {
            audioSource.dispose();
        }
        if (factory != null) factory.dispose();
        PeerConnectionFactory.stopInternalTracingCapture();
        PeerConnectionFactory.shutdownInternalTracer();
        mListener = null;
    }

    /**
     * Start the client.
     * <p/>
     * Set up the local stream and notify the signaling server.
     * Call this method after onCallReady.
     */
    public void start() {
        commandMap.get("init").execute(null, null);
    }

    public void setCamera(){
        localMS = factory.createLocalMediaStream("ARDAMS");
        if(pcParams.videoCallEnabled){
            MediaConstraints videoConstraints = new MediaConstraints();
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxHeight", Integer.toString(pcParams.videoHeight)));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxWidth", Integer.toString(pcParams.videoWidth)));
//            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxFrameRate", Integer.toString(pcParams.videoFps)));
//            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("ideaFrameRate", Integer.toString(48/*pcParams.videoFps*/)));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxFrameRate", Integer.toString(pcParams.videoFps)));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("minFrameRate", Integer.toString(pcParams.videoFps)));

            videoCapturer = getVideoCapturer();
            videoSource = factory.createVideoSource(videoCapturer, videoConstraints);
            //videoCapturer.changeCaptureFormat(pcParams.videoWidth,pcParams.videoHeight,
            //        pcParams.videoFps);
            videoTrack = factory.createVideoTrack(VIDEO_TRACK_ID, videoSource);
            localMS.addTrack(videoTrack);
        }

        MediaConstraints audioConstraints = new MediaConstraints();
        // added for audio performance measurements
        if (pcParams.noAudioProcessing) {
            LogUtil.d(TAG, "Disabling audio processing");
            audioConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair(AUDIO_ECHO_CANCELLATION_CONSTRAINT, "false"));
            audioConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair(AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT, "false"));
            audioConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair(AUDIO_HIGH_PASS_FILTER_CONSTRAINT, "false"));
            audioConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair(AUDIO_NOISE_SUPPRESSION_CONSTRAINT, "false"));
        }
        else {
            LogUtil.d(TAG, "Enabling audio processing");
            audioConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair(AUDIO_ECHO_CANCELLATION_CONSTRAINT, "true"));
            audioConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair(AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT, "true"));
            audioConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair(AUDIO_HIGH_PASS_FILTER_CONSTRAINT, "true"));
            audioConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair(AUDIO_NOISE_SUPPRESSION_CONSTRAINT, "true"));
        }
        if (pcParams.enableLevelControl) {
            LogUtil.d(TAG, "Enabling level control.");
            audioConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair(AUDIO_LEVEL_CONTROL_CONSTRAINT, "true"));
        }
        else {
            LogUtil.d(TAG, "Disabling level control.");
            audioConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair(AUDIO_LEVEL_CONTROL_CONSTRAINT, "false"));
        }
        audioSource = factory.createAudioSource(audioConstraints);
        audioTrack = factory.createAudioTrack(AUDIO_TRACK_ID, audioSource);
        audioTrack.setEnabled(true);
        audioTrack.setState(MediaStreamTrack.State.LIVE);
        localMS.addTrack(audioTrack);
        if(mListener != null) {
            mListener.onLocalStream(localMS);
        }
    }

    private VideoCapturerAndroid getVideoCapturer() {
        String frontCameraDeviceName = CameraEnumerationAndroid.getNameOfFrontFacingDevice();
        return VideoCapturerAndroid.create(frontCameraDeviceName,new VideoCapturerAndroid.CameraEventsHandler(){

            @Override
            public void onCameraError(String s) {
                Logger.i(TAG + "摄像头回调onCameraError" + s );
            }

            @Override
            public void onCameraFreezed(String s) {
                Logger.i(TAG + "摄像头回调onCameraFreezed" + s );
            }

            @Override
            public void onCameraOpening(int i) {
                Logger.i(TAG + "摄像头回调onCameraOpening" + i );

            }

            @Override
            public void onFirstFrameAvailable() {
                Logger.i(TAG + "摄像头回调" + "onFirstFrameAvailable");

            }

            @Override
            public void onCameraClosed() {
                Logger.i(TAG + "摄像头回调" + "onCameraClosed");
            }
        });
    }


    public void switchCameraInternal() {
        if (CameraEnumerationAndroid.getDeviceCount()>1) {
            if (!pcParams.videoCallEnabled || isError || localMS == null) {
                LogUtil.e(TAG, "Failed to switch camera. Video: " + pcParams.videoCallEnabled + ". Error : " + isError);
                return; // No video is sent or only one camera is available or error happened.
            }
            LogUtil.d(TAG, "Switch camera");
            videoCapturer.switchCamera(new VideoCapturerAndroid.CameraSwitchHandler() {
                @Override
                public void onCameraSwitchDone(boolean b) {
                    Logger.i(TAG + "切换摄像头回调成功" + b );

                }

                @Override
                public void onCameraSwitchError(String s) {
                    Logger.i(TAG + "切换摄像头回调错误" + s );

                }
            });
        } else {
            LogUtil.d(TAG, "Will not switch camera, video caputurer is not a camera");
        }
    }

    public MediaStream getMediaStream() {
        return localMS;
    }

    protected  IMMessage generateIMMessage() {
        IMMessage message = MessageUtils.generateSingleIMMessage(from, to, chatType, realJid, "");
//        message.setType(MSG_TYPE_WEBRTC);
//        IMMessage message = new IMMessage();
//        Date time = Calendar.getInstance().getTime();
//        time.setTime(time.getTime() + CommonConfig.divideTime);
//        String id = UUID.randomUUID().toString();
//        message.setId(id);
//        message.setType(ConversitionType.MSG_TYPE_WEBRTC);
//        message.stFromID(from);
//        message.setToID(to);
//        message.setMessageID(id);
//        message.setTime(time);
//        message.setDirection(IMMessage.DIRECTION_SEND);
//        message.setIsRead(1);
//        message.setIsRead(IMMessage.MSG_READ);
//        message.setMessageState(MessageStatus.STATUS_PROCESSION);
//        message.setConversationID(to);
        return message;
    }

    public void onReciveMessage(IMMessage message) {
        if(message.getMsgType() == MessageType.MSG_RTC_AUDIO_PING&&!pcParams.videoCallEnabled) {
            IMMessage resp = generateIMMessage();
//            resp.setId(message.getId());
            resp.setBody("video command");
//            resp.setType(MSG_TYPE_WEBRTC);
            ConnectionUtil.getInstance().sendTextOrEmojiMessage(message);
        } else if(message.getMsgType() == MessageType.MSG_RTC_VIDEO_PING&&pcParams.videoCallEnabled) {
            IMMessage resp = generateIMMessage();
            resp.setId(message.getId());
            resp.setBody("video command");
            ConnectionUtil.getInstance().sendTextOrEmojiMessage(message);
        } else {
            String ext = message.getExt();
            if (!TextUtils.isEmpty(ext)) {
                WebRtcJson json = JsonUtils.getGson().fromJson(ext, WebRtcJson.class);
                Command command = commandMap.get(json.type);
                if (command != null) {
                    command.execute(message, json.payload);
                }
            }
        }
    }

    private String preferCodec(String sdpDescription, String codec, boolean isAudio) {
        String[] lines = sdpDescription.split("\r\n");
        int mLineIndex = -1;
        String codecRtpMap = null;
        // a=rtpmap:<payload type> <encoding name>/<clock rate> [/<encoding parameters>]
        String regex = "^a=rtpmap:(\\d+) " + codec + "(/\\d+)+[\r]?$";
        Pattern codecPattern = Pattern.compile(regex);
        String mediaDescription = "m=video ";
        if (isAudio) {
            mediaDescription = "m=audio ";
        }
        for (int i = 0; (i < lines.length) && (mLineIndex == -1 || codecRtpMap == null); i++) {
            if (lines[i].startsWith(mediaDescription)) {
                mLineIndex = i;
                continue;
            }
            Matcher codecMatcher = codecPattern.matcher(lines[i]);
            if (codecMatcher.matches()) {
                codecRtpMap = codecMatcher.group(1);
            }
        }
        if (mLineIndex == -1) {
            Log.w(TAG, "No " + mediaDescription + " line, so can't prefer " + codec);
            return sdpDescription;
        }
        if (codecRtpMap == null) {
            Log.w(TAG, "No rtpmap for " + codec);
            return sdpDescription;
        }
        LogUtil.d(TAG, "Found " + codec + " rtpmap " + codecRtpMap + ", prefer at " + lines[mLineIndex]);
        String[] origMLineParts = lines[mLineIndex].split(" ");
        if (origMLineParts.length > 3) {
            StringBuilder newMLine = new StringBuilder();
            int origPartIndex = 0;
            // Format is: m=<media> <port> <proto> <fmt> ...
            newMLine.append(origMLineParts[origPartIndex++]).append(" ");
            newMLine.append(origMLineParts[origPartIndex++]).append(" ");
            newMLine.append(origMLineParts[origPartIndex++]).append(" ");
            newMLine.append(codecRtpMap);
            for (; origPartIndex < origMLineParts.length; origPartIndex++) {
                if (!origMLineParts[origPartIndex].equals(codecRtpMap)) {
                    newMLine.append(" ").append(origMLineParts[origPartIndex]);
                }
            }
            lines[mLineIndex] = newMLine.toString();
            LogUtil.d(TAG, "Change media description: " + lines[mLineIndex]);
        } else {
            LogUtil.e(TAG, "Wrong SDP media description format: " + lines[mLineIndex]);
        }
        StringBuilder newSdpDescription = new StringBuilder();
        for (String line : lines) {
            newSdpDescription.append(line).append("\r\n");
        }
        return newSdpDescription.toString();
    }

    public void changeVideoTrack(boolean isEnable) {
        if (videoTrack != null) {
            videoTrack.setEnabled(isEnable);
        }
    }

    public boolean isVideoEnable () {
        if (videoTrack != null) {
            return videoTrack.enabled();
        }
        return false;
    }

    public void sendPickupMessage() {
        sendWebrtcMessage(WebRTCStatus.PICKUP.getType(),null);
    }

    public void sendDenyMessage() {
        sendWebrtcMessage(WebRTCStatus.DENY.getType(),null);
    }

    public void sendCloseMessage(long time) {
        sendWebrtcMessage(WebRTCStatus.CLOSE.getType(),null);
        if(!isCaller) {
            sendNormalMessage(WebRTCStatus.CLOSE.getType(), time, "");
        }
    }
    public void sendCancelMessage() {
        sendWebrtcMessage(WebRTCStatus.CANCEL.getType(),null);
        if(!isCaller) {
            sendNormalMessage(WebRTCStatus.CANCEL.getType(), 0, "");
        }
    }

    public void sendTimeoutMessage() {
        sendWebrtcMessage(WebRTCStatus.TIMEOUT.getType(),null);
        if(!isCaller) {
            sendNormalMessage(WebRTCStatus.TIMEOUT.getType(), 0, "");
        }
    }

//    @Override
//    public void startVideoRtc() {
//        IMMessage message = generateIMMessage();
//        message.setBody("当前客户端不支持实时视频");
//        message.setMsgType(ProtoMessageOuterClass.MessageType.WebRTC_MsgType_Video_VALUE);
//
//        ConnectionUtil.getInstance().sendTextOrEmojiMessage(message);
////        if(chatType.equals(ConversitionType.MSG_TYPE_CHAT + "")) {
////            IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Text, message);
////        } else {
////            IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Group_Chat_Message_Text, message);
////        }
//        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.SEND_MESSAGE_RENDER, message);
////        chatView.setNewMsg2DialogueRegion(message);
//    }
//
//    @Override
//    public void startAudioRtc() {
//        IMMessage message = generateIMMessage();
////        message.setType(ProtoMessageOuterClass.SignalType.SignalTypeChat_VALUE);
//        message.setBody("当前客户端不支持音频通话");
//        message.setMsgType(ProtoMessageOuterClass.MessageType.WebRTC_MsgType_Audio_VALUE);
//
//        ConnectionUtil.getInstance().sendTextOrEmojiMessage(message);
//        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.SEND_MESSAGE_RENDER, message);
////        chatView.setNewMsg2DialogueRegion(message);
//    }
    @Override
    public void startVideoRtc() {
        IMMessage message = generateIMMessage();
        WebRtcJson webRtcJson = new WebRtcJson();
        webRtcJson.type = "create";
        message.setBody("video command");
        message.setType(ProtoMessageOuterClass.SignalType.SignalTypeWebRtc_VALUE);
        message.setExt(JsonUtils.getGson().toJson(webRtcJson));
        message.setMsgType(ProtoMessageOuterClass.MessageType.WebRTC_MsgType_VideoCall_VALUE);
    //        curMsgNum++;

        ConnectionUtil.getInstance().sendWebrtcMessage(message);
    //        chatView.setNewMsg2DialogueRegion(message);
    }

    @Override
    public void startAudioRtc() {
        IMMessage message = generateIMMessage();
        WebRtcJson webRtcJson = new WebRtcJson();
        webRtcJson.type = "create";
        message.setBody("audio command");
        message.setType(ProtoMessageOuterClass.SignalType.SignalTypeWebRtc_VALUE);
        message.setExt(JsonUtils.getGson().toJson(webRtcJson));
        message.setMsgType(ProtoMessageOuterClass.MessageType.WebRTC_MsgType_AudioCall_VALUE);
//        curMsgNum++;
        ConnectionUtil.getInstance().sendWebrtcMessage(message);
//        chatView.setNewMsg2DialogueRegion(message);
    }
}
