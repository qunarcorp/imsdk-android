package com.qunar.im.ui.view.baseView;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qunar.im.base.jsonbean.WebrtcMessageExtention;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.util.DateTimeUtils;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.permission.PermissionCallback;
import com.qunar.im.permission.PermissionDispatcher;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.rtc.webrtc.WebRTCStatus;
import com.qunar.im.ui.R;
import com.qunar.im.ui.presenter.views.IChatView;
import com.qunar.im.utils.ConnectionUtil;

/**
 * Created by wangxinbo on 2017/1/23.
 */
public class RTCView extends LinearLayout implements PermissionCallback {
    TextView textView;
    ImageView iconView;
    LinearLayout atom_ui_rtcview;
    private Context mContext;

    private String tojid = "";
    private String roomId = "";

    protected final int VIDEO_CALL = PermissionDispatcher.getRequestCode();
    protected final int AUDIO_CALL = VIDEO_CALL + 1;
    protected final int VIDEO_CALL_GROUP = AUDIO_CALL + 1;

    public RTCView(Context context) {
        this(context,null);
    }

    public RTCView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RTCView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RTCView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    protected void init(Context context) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.atom_ui_item_rtc_view, this, true);
        atom_ui_rtcview = findViewById(R.id.atom_ui_rtcview);
        iconView = findViewById(R.id.atom_ui_left_icon);
        textView = findViewById(R.id.atom_ui_text);

    }

    public void bind(final Context context, final int msgtype, final IMMessage imMessage) {
        if(ProtoMessageOuterClass.MessageType.WebRTC_MsgType_VideoCall_VALUE == imMessage.getMsgType()) {
            String ext = imMessage.getExt();
            WebrtcMessageExtention extention = JsonUtils.getGson().fromJson(ext, WebrtcMessageExtention.class);
            if(extention != null && !TextUtils.isEmpty(extention.type)) {
                //自己发的
                if(imMessage.getFromID() != null && imMessage.getFromID().equals(CurrentPreference.getInstance().getPreferenceUserId())){
                    if(WebRTCStatus.CANCEL.getType().equals(extention.type)) {
                        textView.setText(context.getString(R.string.atom_rtc_canceled));
                    } else if(WebRTCStatus.CLOSE.getType().equals(extention.type)) {
                        textView.setText(context.getString(R.string.atom_rtc_duration, DateTimeUtils.stringForTime(extention.time)));
                    } else if(WebRTCStatus.DENY.getType().equals(extention.type)) {
                        textView.setText(context.getString(R.string.atom_rtc_deny_other));
                    } else if(WebRTCStatus.TIMEOUT.getType().equals(extention.type)) {
                        textView.setText(context.getString(R.string.atom_rtc_timeout));
                    }
                } else {
                    if(extention != null && WebRTCStatus.CANCEL.getType().equals(extention.type)) {
                        textView.setText(context.getString(R.string.atom_rtc_canceled_by_caller));
                    } else if(WebRTCStatus.CLOSE.getType().equals(extention.type)) {
                        textView.setText(context.getString(R.string.atom_rtc_duration, DateTimeUtils.stringForTime(extention.time)));
                    } else if(WebRTCStatus.DENY.getType().equals(extention.type)) {
                        textView.setText(context.getString(R.string.atom_rtc_deny));
                    } else if(WebRTCStatus.TIMEOUT.getType().equals(extention.type)) {
                        textView.setText(context.getString(R.string.atom_rtc_canceled_by_caller));
                    }
                }
            } else {
                if(ProtoMessageOuterClass.MessageType.WebRTC_MsgType_VideoCall_VALUE == imMessage.getMsgType()){
                    textView.setText(R.string.atom_ui_rtc_video_call);
                } else {
                    textView.setText(R.string.atom_ui_rtc_call);
                }
            }
            iconView.setBackgroundResource(R.drawable.pub_imsdk_rtc_video_chat);
            atom_ui_rtcview.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(imMessage.getDirection() == IMMessage.DIRECTION_SEND) {
                        tojid = imMessage.getToID();
                    } else {
                        tojid = imMessage.getFromID();
                    }
                    if(context instanceof IChatView) {
                        boolean isconsult = "4".equals(((IChatView)context).getChatType()) || "5".equals(((IChatView)context).getChatType());
                        if(!isconsult) {
                            if(context instanceof Activity) {
                                int[] grant= new int[]{PermissionDispatcher.REQUEST_CAMERA,PermissionDispatcher.REQUEST_RECORD_AUDIO};

                                mContext = context;
                                PermissionDispatcher.
                                        requestPermissionWithCheck((Activity)context, grant, RTCView.this, VIDEO_CALL);

//                                Intent intent = new Intent("android.intent.action.VIEW",
//                                        Uri.parse(CommonConfig.schema + "://qcrtc/webrtc?fromid="
//                                                + CurrentPreference.getInstance().getPreferenceUserId()
//                                                + "&toid=" + tojid
//                                                + "&chattype=" + ((IChatView) context).getChatType()
//                                                + "&realjid=" + tojid
//                                                + "&isFromChatRoom=" + false
//                                                + "&offer=false&video=true"));
//                                context.startActivity(intent);
//
//                                ConnectionUtil.getInstance().lanuchChatVideo(true,CurrentPreference.getInstance().getUserid(),tojid);
//                                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.NOTIFY_RTCMSG, isVideo);
                            }
                        }
                    }
                }
            });
        } else if(ProtoMessageOuterClass.MessageType.WebRTC_MsgType_AudioCall_VALUE == imMessage.getMsgType()){
            String ext = imMessage.getExt();
            WebrtcMessageExtention extention = JsonUtils.getGson().fromJson(ext, WebrtcMessageExtention.class);
            if(extention != null && !TextUtils.isEmpty(extention.type)) {
                //自己发的
                if(imMessage.getFromID() != null && imMessage.getFromID().equals(CurrentPreference.getInstance().getPreferenceUserId())){
                    if(WebRTCStatus.CANCEL.getType().equals(extention.type)) {
                        textView.setText(context.getString(R.string.atom_rtc_canceled));
                    } else if(WebRTCStatus.CLOSE.getType().equals(extention.type)) {
                        textView.setText(context.getString(R.string.atom_rtc_duration, DateTimeUtils.stringForTime(extention.time)));
                    } else if(WebRTCStatus.DENY.getType().equals(extention.type)) {
                        textView.setText(context.getString(R.string.atom_rtc_deny_other));
                    } else if(WebRTCStatus.TIMEOUT.getType().equals(extention.type)) {
                        textView.setText(context.getString(R.string.atom_rtc_timeout));
                    }
                } else {
                    if(extention != null && WebRTCStatus.CANCEL.getType().equals(extention.type)) {
                        textView.setText(context.getString(R.string.atom_rtc_canceled_by_caller));
                    } else if(WebRTCStatus.CLOSE.getType().equals(extention.type)) {
                        textView.setText(context.getString(R.string.atom_rtc_duration, DateTimeUtils.stringForTime(extention.time)));
                    } else if(WebRTCStatus.DENY.getType().equals(extention.type)) {
                        textView.setText(context.getString(R.string.atom_rtc_deny));
                    } else if(WebRTCStatus.TIMEOUT.getType().equals(extention.type)) {
                        textView.setText(context.getString(R.string.atom_rtc_canceled_by_caller));
                    }
                }
            } else {
                if(ProtoMessageOuterClass.MessageType.WebRTC_MsgType_VideoCall_VALUE == imMessage.getMsgType()){
                    textView.setText(R.string.atom_ui_rtc_video_call);
                } else {
                    textView.setText(R.string.atom_ui_rtc_call);
                }
            }
            iconView.setBackgroundResource(R.drawable.pub_imsdk_rtc_audio_chat);
            atom_ui_rtcview.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(imMessage.getDirection() == IMMessage.DIRECTION_SEND) {
                        tojid = imMessage.getToID();
                    } else {
                        tojid = imMessage.getFromID();
                    }
                    if(context instanceof IChatView) {
                        boolean isconsult = "4".equals(((IChatView) context).getChatType()) || "5".equals(((IChatView) context).getChatType());
                        if (!isconsult) {
                            if(context instanceof Activity) {
                                int[] grant= new int[]{PermissionDispatcher.REQUEST_RECORD_AUDIO};

                                mContext = context;
                                PermissionDispatcher.
                                        requestPermissionWithCheck((Activity)context, grant, RTCView.this, AUDIO_CALL);


//                                Intent intent = null;
//                                intent = new Intent("android.intent.action.VIEW",
//                                        Uri.parse(CommonConfig.schema + "://qcrtc/webrtc?fromid="
//                                                + CurrentPreference.getInstance().getPreferenceUserId()
//                                                + "&toid=" + tojid
//                                                + "&chattype=" + ((IChatView) context).getChatType()
//                                                + "&realjid=" + tojid
//                                                + "&isFromChatRoom=" + false
//                                                + "&offer=false&video=true"));
//                                context.startActivity(intent);

//                                ConnectionUtil.getInstance().lanuchChatVideo(false,CurrentPreference.getInstance().getUserid(),tojid);
//                                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.NOTIFY_RTCMSG, isVideo);
                            }
                        }
                    }
                }
            });
        } else if(ProtoMessageOuterClass.MessageType.WebRTC_MsgType_Audio_VALUE == imMessage.getMsgType()){
            iconView.setBackgroundResource(R.drawable.pub_imsdk_rtc_audio_chat);
            textView.setText(R.string.atom_ui_tip_client_too_low);
        } else if(ProtoMessageOuterClass.MessageType.WebRTC_MsgType_Video_VALUE == imMessage.getMsgType()){
            iconView.setBackgroundResource(R.drawable.pub_imsdk_rtc_video_chat);
            textView.setText(R.string.atom_ui_tip_client_too_low);
        }

    }

    public void bindGroupVideo(final Context context, final IMMessage imMessage){
        iconView.setBackgroundResource(R.drawable.pub_imsdk_rtc_video_chat);
        textView.setText(R.string.atom_ui_rtc_video_call);
        atom_ui_rtcview.setOnClickListener((v) -> {
            if(context instanceof Activity) {
                mContext = context;
                int[] grant= new int[]{PermissionDispatcher.REQUEST_CAMERA,PermissionDispatcher.REQUEST_RECORD_AUDIO};
                PermissionDispatcher.
                        requestPermissionWithCheck((Activity) context, grant, RTCView.this, VIDEO_CALL_GROUP);
                roomId = imMessage.getConversationID();
            }

        });
    }

    @Override
    public void responsePermission(int requestCode, boolean granted) {
        if(requestCode == VIDEO_CALL_GROUP) {
            ConnectionUtil.getInstance().getMucCard(roomId, nick -> {
                ConnectionUtil.getInstance().lanuchGroupVideo(roomId,nick.getName());
            },false,false);
        } else {
            Intent intent = new Intent("android.intent.action.VIEW",
                    Uri.parse(CommonConfig.schema + "://qcrtc/webrtc?fromid="
                            + CurrentPreference.getInstance().getPreferenceUserId()
                            + "&toid=" + tojid
                            + "&chattype=" + ((IChatView) mContext).getChatType()
                            + "&realjid=" + tojid
                            + "&isFromChatRoom=" + false
                            + "&offer=false&video=" + (requestCode == VIDEO_CALL)));
            mContext.startActivity(intent);
        }
    }
}
