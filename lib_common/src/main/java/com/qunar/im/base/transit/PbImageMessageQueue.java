package com.qunar.im.base.transit;

import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.module.IMMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * 发送图片消息队列
 * Created by saber on 16-4-1.
 */
public class PbImageMessageQueue {

    public static Map<String,ImgMsgPacket> packetMap = new HashMap<String,ImgMsgPacket>();

    public static class ImgMsgPacket{
        public String key;
        public boolean isFirst;
        public ImgMsgPacket next;
        public IMMessage message;
        private boolean uploadState;
        private boolean failure;

        public boolean approveSend()
        {
            uploadState = true;
            if(isFirst)
            {
              sendImageMessage();
            }
            return uploadState;
        }

        public void removed()
        {
            failure = true;
            uploadState = true;
            if(isFirst)
            {
                sendImageMessage();
            }
        }

        public void sendImageMessage() {
            if (uploadState) {
                if(!failure&&message!=null) {
                    if (message.getType() == ConversitionType.MSG_TYPE_GROUP)
                        ConnectionUtil.getInstance().sendGroupTextOrEmojiMessage(message);
                    else {
                        ConnectionUtil.getInstance().sendTextOrEmojiMessage(message);
                    }
                }
                synchronized (this) {
                    if (next != null) {
                        PbImageMessageQueue.packetMap.put(key, next);
                        next.isFirst = true;
                        next.sendImageMessage();
                        next = null;
                    } else {
                        PbImageMessageQueue.packetMap.remove(key);
                    }
                }
            }
        }
    }
}
