package com.qunar.im.rtc.scheme;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.qunar.im.base.jsonbean.WebRtcJson;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.MessageUtils;
import com.qunar.im.permission.PermissionCallback;
import com.qunar.im.permission.PermissionDispatcher;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.rtc.activity.RtcActivity;
import com.qunar.im.utils.ConnectionUtil;

import java.util.Map;

/**
 * Created by xinbo.wang on 2017-01-09.
 */
public class RTCSchemeImpl implements QChatSchemaService, PermissionCallback {
    public static final RTCSchemeImpl instance = new RTCSchemeImpl();
    public boolean BUSY = false;
    protected final int RTC = PermissionDispatcher.getRequestCode();

    private Context mContext;
    private Map<String,String> params;

    @Override
    public boolean startActivityAndNeedWating(AppCompatActivity context, Map<String, String> map) {
        if(BUSY) {
            WebRtcJson webRtcJson = new WebRtcJson();
            webRtcJson.type = "busy";
            webRtcJson.payload = null;
            IMMessage message = MessageUtils.generateSingleIMMessage(map.get("fromid"), map.get("toid"), map.get("chattype"), map.get("realjid"), "");
            message.setBody("video command");
            message.setType(ProtoMessageOuterClass.SignalType.SignalTypeWebRtc_VALUE);
            message.setExt(JsonUtils.getGson().toJson(webRtcJson));
            ConnectionUtil.getInstance().sendWebrtcMessage(message);
//            ConnectionUtil.getInstance().sendTextOrEmojiMessage(message);
        } else {
            int[] grant= new int[]{PermissionDispatcher.REQUEST_CAMERA,PermissionDispatcher.REQUEST_RECORD_AUDIO};
            if(!Boolean.valueOf(map.get("video"))) {
                grant = new int[]{PermissionDispatcher.REQUEST_RECORD_AUDIO};
            }
            mContext = context;
            params = map;
            PermissionDispatcher.
                    requestPermissionWithCheck(context, grant, this, RTC);
        }
        return false;
    }

    @Override
    public void responsePermission(int requestCode, boolean granted) {
        if(requestCode == RTC && granted) {
            BUSY = true;
            Intent intent = new Intent(mContext, RtcActivity.class);
            intent.putExtra(RtcActivity.INTENT_KEY_FROM, params.get("fromid"));
            intent.putExtra(RtcActivity.INTENT_KEY_TO, params.get("toid"));
            intent.putExtra(RtcActivity.INTENT_KEY_CHATTYPE, params.get("chattype"));
            intent.putExtra(RtcActivity.INTENT_KEY_REALJID, params.get("realjid"));
            intent.putExtra(RtcActivity.INTENT_KEY_CREATEOFFER, Boolean.valueOf(params.get("offer")));
            intent.putExtra(RtcActivity.INTENT_KEY_VIDEO_ENABLE, Boolean.valueOf(params.get("video")));
            mContext.startActivity(intent);
            mContext = null;
            params = null;
        }
        else {
            BUSY = false;
        }
    }

}
