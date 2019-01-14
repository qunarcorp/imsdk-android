package com.qunar.im.ui.adapter;

import android.content.Context;
import android.os.Handler;

import com.qunar.im.base.jsonbean.RobOrderMsgJson;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by lihaibin.li on 2017/10/25.
 */

public class RobOrderAdapter extends ExtendChatViewAdapter {
    private Map<String, IMMessage> satusMessages = new HashMap<>();

    public RobOrderAdapter(Context context, String toId, Handler uiHandler, boolean showNick) {
        super(context, toId, uiHandler, showNick);
    }

    @Override
    public void notifyDataSetChanged() {
        handleMessage(messages);
        super.notifyDataSetChanged();
    }

    private void handleMessage(List<IMMessage> messages) {
        for (IMMessage message : messages) {
            if (message.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeGrabMenuResult_VALUE) {
                RobOrderMsgJson robOrderMsgJson = JsonUtils.getGson().fromJson(message.getExt(), RobOrderMsgJson.class);
                if (robOrderMsgJson != null)
                    satusMessages.put(robOrderMsgJson.getMsgId(), message);
            }
        }

        //不展示订单已被抢的消息
        Iterator<IMMessage> it = messages.iterator();
        while(it.hasNext()){
            IMMessage imMessage = it.next();
            if(satusMessages.containsKey(imMessage.getId()) && imMessage.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeGrabMenuVcard_VALUE){
                it.remove();
            }
        }
        //显示已被抢的订单
//        for(IMMessage message : messages){
//            if(satusMessages.containsKey(message.getId()) && message.getMsgType() == MessageType.MSG_TYPE_ROB_ORDER){
//                RobOrderMsgJson robOrderMsgJson = JsonUtils.getGson().fromJson(message.getExt(), RobOrderMsgJson.class);
//                robOrderMsgJson.setStatus("1");
//
//                IMMessage statusMessage = satusMessages.get(message.getId());
//                RobOrderMsgJson statusJson = JsonUtils.getGson().fromJson(statusMessage.getExt(), RobOrderMsgJson.class);
//
//                robOrderMsgJson.setBtnDisplay(statusJson.getBtnDisplay());
//
//                message.setExt(JsonUtils.getGson().toJson(robOrderMsgJson));
//            }
//        }
    }
}
