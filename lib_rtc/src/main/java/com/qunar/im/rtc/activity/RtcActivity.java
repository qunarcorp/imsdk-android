package com.qunar.im.rtc.activity;

import android.graphics.Point;
import android.media.AudioManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.orhanobut.logger.Logger;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.protocol.HttpRequestCallback;
import com.qunar.im.base.protocol.HttpUrlConnectionHandler;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.util.DateTimeUtils;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.MediaUtils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.rtc.scheme.RTCSchemeImpl;
import com.qunar.im.rtc.webrtc.AppRTCAudioManager;
import com.qunar.im.rtc.webrtc.PeerConnectionParameters;
import com.qunar.im.rtc.webrtc.WebRTCStatus;
import com.qunar.im.rtc.webrtc.WebRtcClient;
import com.qunar.im.rtc.webrtc.WebRtcIce;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.QtalkStringUtils;

import org.webrtc.MediaStream;
import org.webrtc.RendererCommon;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;

import java.io.InputStream;
import java.util.Set;

import de.greenrobot.event.EventBus;
import im.qunar.com.rtc.R;

public class RtcActivity extends AppCompatActivity implements WebRtcClient.RtcListener,View.OnClickListener {
    private static final String TAG ="RtcActivity" ;

    public final static String INTENT_KEY_FROM ="fromJid";
    public final static String INTENT_KEY_TO ="toJid";
    public final static String INTENT_KEY_CHATTYPE ="chattype";
    public final static String INTENT_KEY_REALJID ="realJid";

    public final static String INTENT_KEY_CREATEOFFER= "createOffer";
    public final static String INTENT_KEY_VIDEO_ENABLE = "videoEnable";

    // Local preview screen position before call is connected.
    private static final int LOCAL_X_CONNECTING = 0;
    private static final int LOCAL_Y_CONNECTING = 0;
    private static final int LOCAL_WIDTH_CONNECTING = 100;
    private static final int LOCAL_HEIGHT_CONNECTING = 100;
    // Local preview screen position after call is connected.
    private static final int LOCAL_X_CONNECTED = 72;
    private static final int LOCAL_Y_CONNECTED = 5;
    private static final int LOCAL_WIDTH_CONNECTED = 25;
    private static final int LOCAL_HEIGHT_CONNECTED = 25;
    // Remote video screen position
    private static final int REMOTE_X = 0;
    private static final int REMOTE_Y = 0;
    private static final int REMOTE_WIDTH = 100;
    private static final int REMOTE_HEIGHT = 100;
    private static final long MAX_TIMEOUT = 2 * 60 * 1000;
    private RendererCommon.ScalingType scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FILL;
    //private VideoRendererGui.ScalingType scalingType = VideoRendererGui.ScalingType.SCALE_ASPECT_FILL;
    //private SurfaceViewRenderer localRender,remoteRender;
    private VideoRenderer.Callbacks localRender;
    private VideoRenderer.Callbacks remoteRender;
    private WebRtcClient client;
    boolean videoEnable = true;

    private String from, to, chatType, realJid;

    boolean isCaller;//发起者是false
    boolean isReady;

    boolean isStart=false;

    //EglBase rootContext;

    AppRTCAudioManager audioManager;
    private Vibrator vibrator;

    private GLSurfaceView vsv;
    LinearLayout videoTop, videoLayout, audioLayout, preBottom;
    TextView rtcMute, rtc_hangup, rtcCameraToggle, preAudioPickup, preVideoPickup, preDeny, audioHangup, audioMute, audioMicrophone;
    ImageView rtcCameraTurn;
    SimpleDraweeView rtcGravantar;
    TextView rtcNick,rtcStatus;
    private Chronometer chronometer;
    private WebRTCStatus mWebrtcStatus;

    private static final int WEBRTC_TIMEOUT_NOANSWER = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg != null) {
                if(msg.what == WEBRTC_TIMEOUT_NOANSWER) {
                    if (client!=null) {
                        client.sendTimeoutMessage();
                    }
                    finish();
                }
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON
                        | LayoutParams.FLAG_DISMISS_KEYGUARD
                        | LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | LayoutParams.FLAG_TURN_SCREEN_ON);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.atom_rtc_activity_webrtc);
        videoEnable = getIntent().getBooleanExtra(INTENT_KEY_VIDEO_ENABLE,true);
        isCaller = getIntent().getBooleanExtra(INTENT_KEY_CREATEOFFER,false);
        from =  getIntent().getExtras().getString(INTENT_KEY_FROM);
        to =  getIntent().getExtras().getString(INTENT_KEY_TO);
        chatType =  getIntent().getExtras().getString(INTENT_KEY_CHATTYPE);
        realJid =  getIntent().getExtras().getString(INTENT_KEY_REALJID);
        LogUtil.d(TAG,"onCreate Load Tone");
        initView();
        EventBus.getDefault().register(this);
        if(videoEnable) {
            VideoRendererGui.setView(vsv, new Runnable() {
                @Override
                public void run() {
                    init();
                }
            });
        } else {
            init();
        }
        initCamera();
        initInfo();
    }

    protected void initInfo() {
        //暂时注释
//        ProfileUtils.displayGravatarByUserId(to, rtcGravantar);
//        ProfileUtils.loadNickName(to, rtcNick,false);
        ConnectionUtil.getInstance().getUserCard(QtalkStringUtils.parseIdAndDomain(to), new IMLogicManager.NickCallBack() {
            @Override
            public void onNickCallBack(Nick nick) {
                rtcNick.setText(nick.getShowName());
                rtcGravantar.setImageURI(nick.getHeaderSrc());
            }
        }, false, false);
    }

    protected void initView()
    {
        //localRender = (SurfaceViewRenderer) findViewById(R.id.atom_rtc_glview_call_surface);
        //remoteRender = (SurfaceViewRenderer) findViewById(R.id.atom_rtc_glview_remote_surface);
        vsv = (GLSurfaceView) findViewById(R.id.pub_imsdk_glview_call);
        videoTop = (LinearLayout) findViewById(R.id.pub_imsdk_rtc_top);
        videoLayout = (LinearLayout) findViewById(R.id.pub_imsdk_video_bottom);
        audioLayout = (LinearLayout) findViewById(R.id.pub_imsdk_audio_bottom);
        preBottom = (LinearLayout) findViewById(R.id.pub_imsdk_pre_bottom);

        rtcGravantar = (SimpleDraweeView) findViewById(R.id.pub_imsdk_rtc_gravantar);
        rtcNick = (TextView) findViewById(R.id.pub_imsdk_rtc_nick);
        rtcStatus = (TextView) findViewById(R.id.pub_imsdk_rtc_status);
        rtcMute = (TextView) findViewById(R.id.pub_imsdk_rtc_mute);
        rtcCameraToggle = (TextView) findViewById(R.id.pub_imsdk_rtc_camera_toggle);
        rtcCameraTurn = (ImageView) findViewById(R.id.pub_imsdk_rtc_camera_turn);

        audioHangup = (TextView) findViewById(R.id.pub_imsdk_audio_hangup);
        audioMute = (TextView) findViewById(R.id.pub_imsdk_audio_mute);
        audioMicrophone = (TextView) findViewById(R.id.pub_imsdk_audio_microphone);

        rtc_hangup = (TextView) findViewById(R.id.pub_imsdk_rtc_hangup);
        preAudioPickup = (TextView) findViewById(R.id.pub_imsdk_rtc_pickup_audio);
        preVideoPickup = (TextView) findViewById(R.id.pub_imsdk_rtc_pickup_video);
        preDeny = (TextView) findViewById(R.id.pub_imsdk_rtc_deny);
        chronometer = (Chronometer) findViewById(R.id.pub_imsdk_rtc_time);

        preAudioPickup.setOnClickListener(this);
        preVideoPickup.setOnClickListener(this);
        preDeny.setOnClickListener(this);
        rtc_hangup.setOnClickListener(this);
        rtcMute.setOnClickListener(this);
        rtcCameraToggle.setOnClickListener(this);
        rtcCameraTurn.setOnClickListener(this);

        audioHangup.setOnClickListener(this);
        audioMute.setOnClickListener(this);
        audioMicrophone.setOnClickListener(this);
        /**
         * mute，摄像头翻转,听筒模式暂时禁用

        videoMute.setVisibility(View.INVISIBLE);
        audioMute.setVisibility(View.INVISIBLE);
        audioTing.setVisibility(View.INVISIBLE);
        videoCamera.setVisibility(View.INVISIBLE);*/

        if(!isCaller)
        {
            hidePreBottom();
            if (videoEnable) {
                hidenAudio();
                shownVideo();
            } else {
                hidenVideo();
                showAudio();
            }
        }
        else {
            shownPreBottom();
            hidenVideo();
            hidenAudio();
            videoTop.setVisibility(View.VISIBLE);
//            if (videoEnable) {
            //localRender.setVisibility(View.VISIBLE);
            //remoteRender.setVisibility(View.VISIBLE);
//            } else {
//                audioGravantar.setVisibility(View.VISIBLE);
//                audioStatus.setVisibility(View.VISIBLE);
//                audioNick.setVisibility(View.VISIBLE);
//            }
        }
    }

    private void playAudio() {
        switch (audioManager.getRingMode()) {
            case AudioManager.RINGER_MODE_NORMAL://响铃
            case AudioManager.MODE_IN_COMMUNICATION://音频通话
                MediaUtils.playRtcSound(RtcActivity.this, R.raw.atom_rtc_video_prompt);
                if(!isCaller) {
                    audioManager.changeToEarpieceMode();
                } else {
                    audioManager.changeToSpeakerMode();
                }
//                        MediaUtils.loadRtcSound(RtcActivity.this, R.raw.atom_rtc_video_prompt);
//                        MediaUtils.playRtcTone(RtcActivity.this);
                break;
            case AudioManager.RINGER_MODE_SILENT://静音
            case AudioManager.RINGER_MODE_VIBRATE://震动
                if(!isCaller) {
                    MediaUtils.playRtcSound(RtcActivity.this, R.raw.atom_rtc_video_prompt);
                    audioManager.changeToEarpieceMode();
                } else {
                    vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    //按照指定的模式去震动。
//				vibrator.vibrate(1000);
                    //数组参数意义：第一个参数为等待指定时间后开始震动，震动时间为第二个参数。后边的参数依次为等待震动和震动的时间
                    //第二个参数为重复次数，-1为不重复，0为一直震动
                    vibrator.vibrate( new long[]{1000,1000},0);
                }
                break;
        }
    }

    protected void hidePreBottom(){
        preBottom.setVisibility(View.GONE);
    }

    protected void shownPreBottom()
    {
        preBottom.setVisibility(View.VISIBLE);
        if(videoEnable) {
            preVideoPickup.setVisibility(View.VISIBLE);
            preAudioPickup.setVisibility(View.GONE);
        } else {
            preVideoPickup.setVisibility(View.GONE);
            preAudioPickup.setVisibility(View.VISIBLE);
        }
    }

    protected void hidenVideo()
    {
        videoLayout.setVisibility(View.GONE);
        //localRender.setVisibility(View.GONE);
        //remoteRender.setVisibility(View.GONE);
        //vsv.setVisibility(View.GONE);
    }

    protected void shownVideo() {
        //localRender.setVisibility(View.VISIBLE);
        //remoteRender.setVisibility(View.VISIBLE);
        //vsv.setVisibility(View.VISIBLE);
        videoLayout.setVisibility(View.VISIBLE);
        audioLayout.setVisibility(View.GONE);

        if(audioManager == null) return;
        if(audioManager.getMode() == AudioManager.MODE_IN_COMMUNICATION) {
            rtcMute.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.pub_imsdk_rtc_audio_close), null, null);
        } else {
            rtcMute.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.pub_imsdk_rtc_audio_open), null, null);
        }
    }

    protected void hidenAudio() {
        audioLayout.setVisibility(View.GONE);
    }

    protected void showAudio() {
        videoLayout.setVisibility(View.GONE);
        audioLayout.setVisibility(View.VISIBLE);
        if(audioManager == null) return;
        if(audioManager.getMode() == AudioManager.MODE_IN_COMMUNICATION) {
            audioMute.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.pub_imsdk_rtc_audio_close), null, null);
        } else {
            audioMute.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.pub_imsdk_rtc_audio_open), null, null);
        }
    }


    private void init() {
        if(client!=null)return;
        //rootContext = EglBase.create();
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);

        final PeerConnectionParameters params = new PeerConnectionParameters.PCBuilder()
                .videoEnable(videoEnable)
                .videoCodec(WebRtcClient.VIDEO_CODEC_VP9)
                .audioCodec(WebRtcClient.AUDIO_CODEC_OPUS)
                .enableLevelControl(true)
                .videoFlexfecEnabled(false)
                .videoWidth(displaySize.x)
                .videoHeight(displaySize.y).build();
        client = new WebRtcClient(RtcActivity.this, RtcActivity.this, params, getIntent().getExtras(), isCaller);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                audioManager = AppRTCAudioManager.create(RtcActivity.this);
                audioManager.start(new AppRTCAudioManager.AudioManagerEvents() {
                    @Override
                    public void onAudioDeviceChanged(AppRTCAudioManager.AudioDevice selectedAudioDevice, Set<AppRTCAudioManager.AudioDevice> availableAudioDevices) {
                    }
                });
                playAudio();
                client.setCamera();
            }
        });

        HttpUrlConnectionHandler.executeGet(QtalkNavicationService.getInstance().getVideoHost() + "rtc?username=" + Protocol.getCKEY()+"&plat="+ CommonConfig.currentPlat,
                new HttpRequestCallback() {
                    @Override
                    public void onComplete(InputStream inputStream) {
                        try {
                            String result = Protocol.parseStream(inputStream);
                            Logger.i("音视频接口 url = "
                                    + QtalkNavicationService.getInstance().getVideoHost() + "rtc?username=" + Protocol.getCKEY()+"&plat="+ CommonConfig.currentPlat + "   reuslt = " + result);
                            final WebRtcIce iceServers = JsonUtils.getGson().fromJson(result,WebRtcIce.class);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    client.connect(VideoRendererGui.getEglBaseContext(), iceServers);
                                    if(!isCaller) {
                                        //自己发起的音视频发送消息
                                        if(videoEnable) {
                                            client.startVideoRtc();
                                        } else {
                                            client.startAudioRtc();
                                        }
                                    }
                                    isReady = true;
                                }
                            });
                        } catch (Exception e) {
                            onFailure(e);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        LogUtil.e(e.getMessage());
                        Logger.i("音视频接口 失败" + e.getMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RtcActivity.this, "视频连接失败", Toast.LENGTH_SHORT).show();
                                stopPlayTone();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 1500);
                            }
                        });
                    }
                });

    }

    protected void initCamera()
    {
        /*localRender.init(rootContext.getEglBaseContext(),null);
        localRender.setZOrderMediaOverlay(true);
        localRender.setMirror(true);
        localRender.setScalingType(scalingType);

        remoteRender.setMirror(false);
        remoteRender.setScalingType(scalingType);
        remoteRender.init(rootContext.getEglBaseContext(),null);*/
        // local and remote render
        if(videoEnable) {
            vsv.setVisibility(View.VISIBLE);
            remoteRender = VideoRendererGui.create(
                    REMOTE_X, REMOTE_Y,
                    REMOTE_WIDTH, REMOTE_HEIGHT, scalingType, false);
            localRender = VideoRendererGui.create(
                    LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING,
                    LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING, scalingType, true);
        } else {
            vsv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        if(isFinishing()) {
            stopPlayTone();
            preAudioPickup.setOnClickListener(null);
            preDeny.setOnClickListener(null);
            rtc_hangup.setOnClickListener(null);
            rtcMute.setOnClickListener(null);
            rtcCameraToggle.setOnClickListener(null);
            rtcCameraTurn.setOnClickListener(null);
        }
        //vsv.onPause();

        if(client != null) {

            client.onPause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        //fullView.onResume();
        if(client != null) {
            client.onResume();
        }
    }

    private void stopPlayTone()
    {
        LogUtil.d(TAG,"stop play tone");
//        MediaUtils.stopRtcTone();
//        MediaUtils.unloadRtcTone();
        MediaUtils.stopRtcSound();
        if(vibrator != null) vibrator.cancel();
    }

    @Override
    public void onDestroy() {
        RTCSchemeImpl.instance.BUSY = false;
        EventBus.getDefault().unregister(this);
        //rootContext.release();
        mHandler.removeMessages(WEBRTC_TIMEOUT_NOANSWER);
        if(client!=null) {
            client.onDestroy();
        }
        stopPlayTone();
        if(audioManager!=null)
            audioManager.stop();
        super.onDestroy();
    }

    private void startTimer() {
        chronometer.setVisibility(View.VISIBLE);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
    }

    private void stopTimer() {
        chronometer.setVisibility(View.GONE);
        chronometer.stop();
    }

    @Override
    public void onStatusChanged(final WebRTCStatus newStatus) {
        this.mWebrtcStatus = newStatus;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String status = "";
                if(newStatus == WebRTCStatus.CONNECTING) {
                    if(isCaller) {
                        status = getString(R.string.atom_rtc_rtcinvite) + (videoEnable ? getString(R.string.atom_rtc_rtcinvitevideo) : getString(R.string.atom_rtc_rtcinviteaudio)) + getString(R.string.atom_rtc_rtcinvitechat);
                    } else {
                        status = getString(R.string.atom_rtc_calling);
                        //超时结束
                        mHandler.sendEmptyMessageDelayed(WEBRTC_TIMEOUT_NOANSWER, MAX_TIMEOUT);
                    }
                } else if(newStatus == WebRTCStatus.CONNECT) {
                    stopPlayTone();
                    mHandler.removeMessages(WEBRTC_TIMEOUT_NOANSWER);
                    LogUtil.d(TAG, "音视频连接状态：" + "连接成功");
                    startTimer();
                    rtcGravantar.setVisibility(View.GONE);
                    rtcNick.setVisibility(View.GONE);
                    if(rtcStatus.getVisibility() == View.VISIBLE)
                        rtcStatus.setVisibility(View.GONE);
                    return;
                } else if(newStatus == WebRTCStatus.DENY) {
                    stopTimer();
                    mHandler.removeMessages(WEBRTC_TIMEOUT_NOANSWER);
                    stopPlayTone();
                    status = "对方已拒绝";
                    CommonConfig.mainhandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 500);
                } else if(newStatus == WebRTCStatus.DISCONNECT) {
                    if(!isCaller && client != null) {
                        client.sendNormalMessage(WebRTCStatus.CLOSE.getType(), DateTimeUtils.TimeForString(chronometer.getText().toString()), "");
                    }
                    stopTimer();
                    mHandler.removeMessages(WEBRTC_TIMEOUT_NOANSWER);
                    Logger.i("音视频连接状态：" + "断开连接");
                    finish();
                    return;
                } else if(newStatus == WebRTCStatus.CLOSE) {
                    if(!isCaller && client != null) {
                        client.sendNormalMessage(WebRTCStatus.CLOSE.getType(), DateTimeUtils.TimeForString(chronometer.getText().toString()), "");
                    }
                    stopTimer();
                    mHandler.removeMessages(WEBRTC_TIMEOUT_NOANSWER);
                    Logger.i("音视频连接状态：" + "对方关闭断开连接");
                    finish();
                } else if(newStatus == WebRTCStatus.BUSY) {
                    stopPlayTone();
                    status = getString(R.string.atom_rtc_trylater);
                } else if(newStatus == WebRTCStatus.PICKUP) {
                    //收到carbon的pickup ，表示已有客户端接听，关闭当前客户端视频
                    finish();
                } else if(newStatus == WebRTCStatus.CANCEL) {
                    finish();
                } else if(newStatus == WebRTCStatus.TIMEOUT) {
                    finish();
                }
                rtcStatus.setText(status);
                Logger.i("音视频连接状态：" + newStatus + "  " + status);
            }
        });
    }

    @Override
    public void onLocalStream(final MediaStream localStream) {
        /*getHandler().post(new Runnable() {
            @Override
            public void run() {
                if(videoEnable) {

                }
            }
        });*/
        if(client==null||localRender ==  null) return;
        if(!videoEnable&&localStream.audioTracks.size()==1)
        {
            localStream.audioTracks.get(0).setEnabled(true);
        }
        if(localStream.videoTracks.size()==1) {
            localStream.videoTracks.get(0).addRenderer(new VideoRenderer(localRender));
            localStream.videoTracks.get(0).setEnabled(videoEnable);
            if (videoEnable) {
                VideoRendererGui.update(localRender,
                        LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING,
                        LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING,
                        scalingType, true);
            }
        }
    }

    @Override
    public void onAddRemoteStream(final MediaStream remoteStream) {
        if(client == null||remoteRender ==  null) return;
        stopPlayTone();
        if(remoteStream.audioTracks.size()>1||
                remoteStream.videoTracks.size()>1)
        {
            LogUtil.e("Wired looking stream: "+remoteStream);
            return;
        }
        if(!videoEnable&&remoteStream.audioTracks.size()==1)
        {
            remoteStream.audioTracks.get(0).setEnabled(true);
        }
        if(remoteStream.videoTracks.size() == 1)
        {
            remoteStream.videoTracks.get(0).addRenderer(new VideoRenderer(remoteRender));
            remoteStream.videoTracks.get(0).setEnabled(videoEnable);
            if(videoEnable) {
                VideoRendererGui.update(remoteRender,
                        REMOTE_X, REMOTE_Y,
                        REMOTE_WIDTH, REMOTE_HEIGHT, scalingType, false);
                VideoRendererGui.update(localRender,
                        LOCAL_X_CONNECTED, LOCAL_Y_CONNECTED,
                        LOCAL_WIDTH_CONNECTED, LOCAL_HEIGHT_CONNECTED,
                        scalingType, true);
            }
        }
    }

    @Override
    public void onRemoveRemoteStream() {
        /*if(videoEnable) {
            VideoRendererGui.update(localRender,
                    LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING,
                    LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING,
                    scalingType, true);
        }*/
    }

    private void switchRender() {
        VideoRendererGui.update(localRender,
                REMOTE_X, REMOTE_Y,
                REMOTE_WIDTH, REMOTE_HEIGHT, scalingType, true);
        VideoRendererGui.update(remoteRender,
                LOCAL_X_CONNECTED, LOCAL_Y_CONNECTED,
                LOCAL_WIDTH_CONNECTED, LOCAL_HEIGHT_CONNECTED,
                scalingType, false);
    }

    public void onEvent(EventBusEvent.WebRtcMessage webRtcMessage)
    {
        if(webRtcMessage.message!=null
//                &&webRtcMessage.message.getType() == ConversitionType.MSG_TYPE_WEBRTC
                && client!=null) {
//            if(webRtcMessage.message.isCarbon()) {
//                finish();
//                return;
//            }
            client.onReciveMessage(webRtcMessage.message);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.pub_imsdk_rtc_hangup
                || v.getId() == R.id.pub_imsdk_audio_hangup) {//挂断
            if(!isCaller) {
                if(mWebrtcStatus != null && mWebrtcStatus == WebRTCStatus.CONNECT) {
                    if (client!=null) {
                        client.sendCloseMessage(DateTimeUtils.TimeForString(chronometer.getText().toString()));
                    }
                } else {
                    if (client!=null) {
                        client.sendCancelMessage();
                    }
                }
            } else {
                if(mWebrtcStatus != null && mWebrtcStatus == WebRTCStatus.CONNECT) {
                    if (client!=null) {
                        client.sendCloseMessage(DateTimeUtils.TimeForString(chronometer.getText().toString()));
                    }
                } else {
                    if (client!=null) {
                        client.sendDenyMessage();
                    }
                }
            }
            finish();
        } else if (v.getId() == R.id.pub_imsdk_rtc_deny) {//拒绝
            if (client!=null) {
                client.sendDenyMessage();
            }
            finish();
        } else if (v.getId() == R.id.pub_imsdk_rtc_pickup_audio
                || v.getId() == R.id.pub_imsdk_rtc_pickup_video) {//接听
            if(client == null||isStart||!isReady) return;
            isStart = true;
            hidePreBottom();
            if (videoEnable) {
                hidenAudio();
                shownVideo();
            } else {
                hidenVideo();
                showAudio();
            }
            if(isCaller) {
                client.sendPickupMessage();
                client.start();
            }
        } else if (v.getId() == R.id.pub_imsdk_rtc_camera_turn) {//前后摄像头切换
            client.switchCameraInternal();
        } else if (v.getId() == R.id.pub_imsdk_rtc_mute
                || v.getId() == R.id.pub_imsdk_audio_mute) {//免提
            switchAudioMute();
        } else if(v.getId() == R.id.pub_imsdk_audio_microphone) {//麦克风开关
            switchMicrophone();
        } else if (v.getId() == R.id.pub_imsdk_rtc_camera_toggle) {//摄像头开关
            switchCamera();
        }
    }

    /**
     * 免提，切换外放和听筒
     */
    private void switchAudioMute() {
        int result = audioManager.ting();
        if(result == AudioManager.MODE_NORMAL) {
            if(videoEnable) {
                rtcMute.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.pub_imsdk_rtc_audio_open), null, null);
            } else {
                audioMute.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.pub_imsdk_rtc_audio_open), null, null);
            }
        } else if(result == AudioManager.MODE_IN_COMMUNICATION) {
            if(videoEnable) {
                rtcMute.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.pub_imsdk_rtc_audio_close), null, null);
            } else {
                audioMute.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.pub_imsdk_rtc_audio_close), null, null);
            }
        }

    }

    /**
     * 麦克风开关
     */
    private void switchMicrophone() {
        boolean mute = !audioManager.isMute();
        mute = audioManager.setMicrophoneMute(mute);
        if (mute) {
            audioMicrophone.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.pub_imsdk_rtc_micro_close), null, null);
        } else {
            audioMicrophone.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.pub_imsdk_rtc_micro_open), null, null);
        }
    }

    /**
     * 切换摄像头开关
     */
    private void switchCamera() {
        boolean isEnable = client.isVideoEnable();
        client.changeVideoTrack(!isEnable);
        if(client.isVideoEnable()) {
            rtcCameraToggle.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.pub_imsdk_rtc_video_open), null, null);
        } else {
            rtcCameraToggle.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.pub_imsdk_rtc_video_close), null, null);
        }
    }

    public void onEventMainThread(EventBusEvent.ReloginEvent reloginEvent)
    {
        Toast.makeText(this,"连接断开",Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onBackPressed() {
        return;
    }
}