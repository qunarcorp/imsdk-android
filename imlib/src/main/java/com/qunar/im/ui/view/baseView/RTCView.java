package com.qunar.im.ui.view.baseView;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.presenter.views.IChatView;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.ui.R;

/**
 * Created by wangxinbo on 2017/1/23.
 */
public class RTCView extends LinearLayout {
    TextView textView;
    ImageView iconView;
    LinearLayout atom_ui_rtcview;
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

    public void bind(final Context context, final boolean isVideo, final IMMessage imMessage) {
        if(isVideo) {
            iconView.setBackgroundResource(R.drawable.pub_imsdk_rtc_video_chat);
            textView.setText(R.string.atom_ui_rtc_video_call);
            atom_ui_rtcview.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tojid = "";
                    if(imMessage.getDirection() == IMMessage.DIRECTION_SEND) {
                        tojid = imMessage.getRealto();
                    } else {
                        tojid = imMessage.getRealfrom();
                    }
                    if(context instanceof IChatView) {
                        boolean isconsult = "4".equals(((IChatView)context).getChatType()) || "5".equals(((IChatView)context).getChatType());
                        if(!isconsult) {
                            if(context instanceof Activity) {
                                Intent intent = null;
                                intent = new Intent("android.intent.action.VIEW",
                                        Uri.parse(CommonConfig.schema + "://qcrtc/webrtc?fromid="
                                                + CurrentPreference.getInstance().getPreferenceUserId()
                                                + "&toid=" + tojid
                                                + "&chattype" + imMessage.getQchatid()
                                                + "&realjid" + tojid
                                                + "&isFromChatRoom" + false
                                                + "&offer=false&video=true"));
                                context.startActivity(intent);
                                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.NOTIFY_RTCMSG, isVideo);
//                        ((IP2pRTC)(IChatingPresenter)context).startVideoRtc();
                            }
                        }
                    }
                }
            });
        } else {
            iconView.setBackgroundResource(R.drawable.pub_imsdk_rtc_audio_chat);
            textView.setText(R.string.atom_ui_rtc_call);
            atom_ui_rtcview.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tojid = "";
                    if(imMessage.getDirection() == IMMessage.DIRECTION_SEND) {
                        tojid = imMessage.getRealto();
                    } else {
                        tojid = imMessage.getRealfrom();
                    }
                    if(context instanceof IChatView) {
                        boolean isconsult = "4".equals(((IChatView) context).getChatType()) || "5".equals(((IChatView) context).getChatType());
                        if (!isconsult) {
                            if(context instanceof Activity) {
                                Intent intent = null;
//                        boolean isconsult = (Integer.valueOf(imMessage.getQchatid()) == ConversitionType.MSG_TYPE_CONSULT
//                                || Integer.valueOf(imMessage.getQchatid()) == ConversitionType.MSG_TYPE_CONSULT_SERVER) ? true : false;
                                intent = new Intent("android.intent.action.VIEW",
                                        Uri.parse(CommonConfig.schema + "://qcrtc/webrtc?fromid="
                                                + CurrentPreference.getInstance().getPreferenceUserId()
                                                + "&toid=" + tojid
                                                + "&chattype" + imMessage.getQchatid()
                                                + "&realjid" + tojid
                                                + "&isFromChatRoom" + false
                                                + "&offer=false&video=false"));
                                context.startActivity(intent);
                                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.NOTIFY_RTCMSG, isVideo);
//                        ((IP2pRTC) (IChatingPresenter) context).startVideoRtc();
                            }
                        }
                    }
                }
            });
        }
    }
}
