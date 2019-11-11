package com.qunar.im.ui.presenter.impl;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.base.util.Constants;
import com.qunar.im.ui.R;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.HttpUtil;
import com.qunar.im.base.jsonbean.AutoDestroyMessageExtention;
import com.qunar.im.base.jsonbean.NoticeBean;
import com.qunar.im.base.jsonbean.TransferConsult;
import com.qunar.im.base.jsonbean.TransferWebChat;
import com.qunar.im.base.jsonbean.UserStatusResult;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.protocol.VCardAPI;
import com.qunar.im.base.structs.EncryptMessageType;
import com.qunar.im.base.structs.MessageStatus;
import com.qunar.im.base.structs.MessageType;
import com.qunar.im.base.structs.UserStatus;
import com.qunar.im.base.util.ChatTextHelper;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.ListUtil;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.MessageUtils;
import com.qunar.im.base.util.Utils;
import com.qunar.im.base.view.faceGridView.EmoticonEntity;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.enums.LoginStatus;
import com.qunar.im.core.manager.IMDatabaseManager;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.ui.R;
import com.qunar.im.ui.presenter.ICloudRecordPresenter;
import com.qunar.im.ui.presenter.IShakeMessagePresenter;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.HttpUtil;
import com.qunar.im.utils.QtalkStringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by xinbo.wang on 2015/2/9.
 */
//用于处理单人聊天会话逻辑
public class SingleSessionPresenter extends ChatPresenter implements ICloudRecordPresenter, IShakeMessagePresenter {

    private String cn;

    @Override
    public void propose() {
        //加载历史消息的方法
//        initReloadMessage();
        reloadMessages();
        //初始化时 显示左上角未读消息数
        showUnReadCount();

    }

    @Override
    protected IMMessage send2Server(String msg) {
        //拼出一个完整IMMessage
        IMMessage message = generateIMMessage();
        message.setBody(msg);
        //emotion showall false特殊处理
        boolean specialMsgType = false;
        List<Map<String, String>> list = ChatTextHelper.getObjList(msg);
        EmoticonEntity emoticonEntity = null;
        if (list != null && !list.isEmpty() && list.size() == 1) {
            Map<String, String> map = list.get(0);
            if ("emoticon".equals(map.get("type"))) {
                emoticonEntity = getEmotinEntry(map);
                if (!emoticonEntity.showAll) {
                    specialMsgType = true;
                }
            }
        }
        if (specialMsgType) {
            message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeImageNew_VALUE);
            message.setExt(getEmojiExtendInfo(emoticonEntity));
        } else {
            message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeText_VALUE);
        }
        curMsgNum++;
        chatView.setNewMsg2DialogueRegion(message);
        HttpUtil.addEncryptMessageInfo(chatView.getToId(), message, ProtoMessageOuterClass.MessageType.MessageTypeText_VALUE);
        if (snapStatus) {
            HttpUtil.handleSnapMessage(message);
        }
        connectionUtil.sendTextOrEmojiMessage(message);
        latestTypingTime = 0;
        return message;
    }


    //qchat 如果个人配置的众包参数含有该会话id 则在发送的url后面拼接相应的参数
    JSONObject params;

    @Override
    protected String addParams2Url(String msg) {
        if (!CommonConfig.isQtalk && !TextUtils.isEmpty(msg) && Utils.IsUrl(msg)) {
            if (params == null) {
                params = IMDatabaseManager.getInstance().selectConversationParam(chatView.getToId());
            }
            if (params != null) {
                try {
                    JSONArray urlAppends = new JSONArray(params.optString("urlappend"));
                    int count = urlAppends == null ? 0 : urlAppends.length();
                    StringBuilder builder = new StringBuilder(msg);
                    for (int i = 0; i < count; i++) {
                        JSONObject urlJson = urlAppends.optJSONObject(i);
                        String filter = urlJson.optString("filter");
                        if (!TextUtils.isEmpty(filter) && msg.contains(filter)) {
                            Protocol.addParams2Url(builder, urlJson);
                        }
                    }
                    return builder.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return msg;
    }

    //发送加密信令消息
    public IMMessage sendEncryptSignalMsg(String msg, int msgType) {
        IMMessage message = generateIMMessage();
        message.setType(ConversitionType.MSG_TYPE_ENCRYPT);
        message.setBody(msg);
        message.setMsgType(msgType);
        message.setDirection(IMMessage.DIRECTION_MIDDLE);
        curMsgNum++;
        connectionUtil.sendEncryptSignalMessage(message);
        if (msgType != EncryptMessageType.BEGIN) {
            chatView.setNewMsg2DialogueRegion(message);
        }
        latestTypingTime = 0;
        return message;
    }


    @Override
    public void receiveMsg(IMMessage message) {
    }


    @Override
    public void showMoreOldMsg(boolean isFromGroup) {
        //原版代码
//        List<IMMessage> historyMessage = messageRecordDataModel.getSingleMsg(chatView.getToId(), curMsgNum, numPerPage);
        int count = chatView.getListSize();
        if (!TextUtils.isEmpty(chatView.getAutoReply())) {
            count--;
        }
        if (tip) {
            count--;
        }

        connectionUtil.SelectHistoryChatMessage(chatView.getChatType(), chatView.getToId(), chatView.getRealJid(), count, numPerPage, new ConnectionUtil.HistoryMessage() {
            @Override
            public void onMessageResult(List<IMMessage> messageList) {
                if (messageList.size() > 0) {
                    curMsgNum += messageList.size();
                    historyTime = messageList.get(0).getTime().getTime() - 1;
                    Collections.reverse(messageList);
                } else {
                    Logger.i("没有数据了:");
                    //没有数据
                }
                chatView.addHistoryMessage(messageList);
            }
        });

    }

    @Override
    public void showMoreOldMsgUp(boolean isFromGroup) {

    }

    protected void updateUserStatus() {
        VCardAPI.getUserStatus(QtalkStringUtils.parseBareJid(chatView.getToId()),
                new ProtocolCallback.UnitCallback<UserStatusResult>() {
                    @Override
                    public void onCompleted(UserStatusResult userStatusResult) {
                        if (userStatusResult != null && !ListUtil.isEmpty(userStatusResult.data)) {
                            UserStatusResult.UsersStatus usersStatus = userStatusResult.data.get(0);
                            if (!ListUtil.isEmpty(usersStatus.ul)) {
                                UserStatus userStatus = UserStatus.offline;
                                try {
                                    userStatus = UserStatus.valueOf(usersStatus.ul.get(0).o);
                                } catch (Exception e) {
                                    LogUtil.e(TAG, "error", e);
                                }
                                chatView.setCurrentStatus(userStatus.strByVal());
                            }
                        }
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        //chatView.setCurrentStatus(UserStatus.offline.strByVal());
                    }
                });
    }

    //发送正在发送状态
    @Override
    public void sendTypingStatus() {
        //如果距离上一次发送正在发送状态 大于5秒,才再次发送
        if (System.currentTimeMillis() - latestTypingTime > 5000) {
            IMMessage typingMsg = generateIMMessage();
            typingMsg.setType(ConversitionType.MSG_TYPING);
            //发送消息
            connectionUtil.sendTypingStatus(typingMsg);
//            IMLogic.instance().sendMessage(typingMsg, null);
            latestTypingTime = System.currentTimeMillis();
        }
    }

    //会话转移 先发送1002到同事客服
    public void transferConversation() {
        IMMessage toServer = generateIMMessage();
//        toServer.setType(ProtoMessageOuterClass.MessageType.MessageTypeTransChatToCustomerService_VALUE);//
        toServer.setType(ConversitionType.MSG_TYPE_CONSULT);//
        toServer.setToID(chatView.getToId());
        toServer.setDirection(2);
        toServer.setRealto(chatView.getTransferId());
        TransferConsult transferConsult = new TransferConsult();
        transferConsult.u = chatView.getRealJid();
        transferConsult.r = chatView.getInputMsg();
        transferConsult.d = QtalkNavicationService.getInstance().getXmppdomain();
        transferConsult.f = com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid();
        transferConsult.rt = QtalkStringUtils.parseLocalpart(chatView.getTransferId());
        transferConsult.toId = toServer.getToID();
        transferConsult.retId = UUID.randomUUID().toString();
        toServer.setBody(JsonUtils.getGson().toJson(transferConsult));//json串
//        toServer.setBody("向同事发起会话转移请求");//json串

        toServer.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeTransChatToCustomerService_VALUE);
        toServer.setExt(JsonUtils.getGson().toJson(transferConsult));
//        curMsgNum++;
//        chatView.setNewMsg2DialogueRegion(toServer);
        connectionUtil.sendTextOrEmojiMessage(toServer);
        Logger.i("发送会话转移消息到客服：" + JsonUtils.getGson().toJson(toServer));
    }

    //会话转移 先发送1001到客户
    private void send2WebChat() {
        IMMessage toWebchat = generateIMMessage();
//        toWebchat.setType(ProtoMessageOuterClass.MessageType.MessageTypeTransChatToCustomer_VALUE);//
        toWebchat.setType(ConversitionType.MSG_TYPE_CONSULT);//
        toWebchat.setToID(chatView.getToId());
        toWebchat.setDirection(2);
        toWebchat.setRealto(chatView.getRealJid());
        TransferWebChat transferWebChat = new TransferWebChat();
        transferWebChat.TransReson = chatView.getInputMsg();
        transferWebChat.realtoId = QtalkStringUtils.parseLocalpart(chatView.getTransferId());
        transferWebChat.toId = QtalkStringUtils.parseLocalpart(toWebchat.getToID());
        transferWebChat.realfromIdNickName = connectionUtil.getNickById(toWebchat.realfrom).getName();// ProfileUtils.getNickByKey(QtalkStringUtils.userId2Jid(toServerMsg.realfrom));
        transferWebChat.realtoIdNickName = connectionUtil.getNickById(chatView.getTransferId()).getName();//ProfileUtils.getNickByKey(QtalkStringUtils.userId2Jid(chatView.getRealJid()));
        transferWebChat.retId = UUID.randomUUID().toString();
        toWebchat.setBody(JsonUtils.getGson().toJson(transferWebChat));//json串
//        toWebchat.setBody("向客户发送会话转移通知");//json串
        toWebchat.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeTransChatToCustomer_VALUE);
//        curMsgNum++;
//        chatView.setNewMsg2DialogueRegion(toWebchat);
        connectionUtil.sendTextOrEmojiMessage(toWebchat);
        Logger.i("发送会话转移消息到客户：" + JsonUtils.getGson().toJson(toWebchat));
    }

    //会话转移 先发送1004返回给A客服
    private void send2AClientt() {
        IMMessage toWebchat = generateIMMessage();
//        toWebchat.setType(ProtoMessageOuterClass.MessageType.MessageTypeTransChatToCustomer_VALUE);//
        toWebchat.setType(ConversitionType.MSG_TYPE_CONSULT);//
        toWebchat.setToID(chatView.getToId());
        toWebchat.setDirection(2);
        toWebchat.setRealto(chatView.getRealJid());
        TransferWebChat transferWebChat = new TransferWebChat();
        transferWebChat.TransReson = chatView.getInputMsg();
        transferWebChat.realtoId = QtalkStringUtils.parseLocalpart(chatView.getRealJid());
        transferWebChat.toId = QtalkStringUtils.parseLocalpart(toWebchat.getToID());
        transferWebChat.realfromIdNickName = connectionUtil.getNickById(toWebchat.realfrom).getName();// ProfileUtils.getNickByKey(QtalkStringUtils.userId2Jid(toServerMsg.realfrom));
        transferWebChat.realtoIdNickName = connectionUtil.getNickById(chatView.getRealJid()).getName();//ProfileUtils.getNickByKey(QtalkStringUtils.userId2Jid(chatView.getRealJid()));
        transferWebChat.retId = UUID.randomUUID().toString();
        toWebchat.setBody(JsonUtils.getGson().toJson(transferWebChat));//json串
        toWebchat.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeTransChatToCustomerService_Feedback_VALUE);
//        curMsgNum++;
//        chatView.setNewMsg2DialogueRegion(toWebchat);
        connectionUtil.sendTextOrEmojiMessage(toWebchat);
        Logger.i("发送会话转移消息到客户：" + JsonUtils.getGson().toJson(toWebchat));
    }

    private boolean tip = false;

    //加载历史消息
    @Override
    public void reloadMessages() {
        int start = 0;
        int firstLoadCount = curMsgNum > 0 ? curMsgNum : numPerPage;
        int unreadCount = chatView.getUnreadMsgCount();//改成从view获取
        if(unreadCount < 0){
            unreadCount = connectionUtil.SelectUnReadCountByConvid(chatView.getToId(), chatView.getRealJid(), chatView.getChatType());
        }
        if(unreadCount > MAX_UNREAD_MSG_LOAD_COUNT){
            firstLoadCount = MAX_UNREAD_MSG_LOAD_COUNT;
        }else if (unreadCount > firstLoadCount) {
            firstLoadCount = unreadCount;
        }
        List<IMMessage> historyMessage = connectionUtil.SelectInitReloadChatMessage(chatView.getToId(), chatView.getRealJid(), chatView.getChatType(), start, firstLoadCount);
        if (!TextUtils.isEmpty(chatView.getAutoReply())) {
            IMMessage autoReply = new IMMessage();
            String uid = UUID.randomUUID().toString();
            autoReply.setId(uid);
            autoReply.setMessageID(uid);
            autoReply.setBody(chatView.getAutoReply());
            autoReply.setDirection(2);
            historyMessage.add(0, autoReply);
        }
        curMsgNum = historyMessage.size();
        if (curMsgNum > 0) {
            Collections.reverse(historyMessage);
            if (unreadCount > 0) {
                //该条显示在未读消息上面
                IMMessage hisDivide = new IMMessage();
                String uid = UUID.randomUUID().toString();
                hisDivide.setId(uid);
                hisDivide.setMessageID(uid);
                hisDivide.setType(ConversitionType.MSG_TYPE_CHAT);
                hisDivide.setMsgType(MessageType.MSG_HISTORY_SPLITER);
                hisDivide.setTime(historyMessage.get(0).getTime());
                if(curMsgNum >= unreadCount)//防止数组越界
                    historyMessage.add(curMsgNum - unreadCount, hisDivide);
                tip = true;
            }

            chatView.setHistoryMessage(historyMessage, unreadCount);
        } else {
            chatView.setHistoryMessage(historyMessage, 0);
        }
    }

    @Override
    public void reloadMessagesFromTime(long time) {
        List<IMMessage> historyMessage = connectionUtil.selectChatMessageAfterSearch(chatView.getToId(),chatView.getRealJid(),time);
        Collections.reverse(historyMessage);
        chatView.setHistoryMessage(historyMessage, 0);
    }

    @Override
    public void sendRobotMsg(String msg) {
        String backupinfo = chatView.getBackupInfo();
        //拼出一个完整IMMessage
        IMMessage message = generateIMMessage();
        message.setBody(msg);
        message.setBackUp(backupinfo);

        curMsgNum++;
        chatView.setNewMsg2DialogueRegion(message);
        HttpUtil.addEncryptMessageInfo(chatView.getToId(), message, ProtoMessageOuterClass.MessageType.MessageTypeText_VALUE);
        if (snapStatus) {
            HttpUtil.handleSnapMessage(message);
        }
        connectionUtil.sendTextOrEmojiMessage(message);
        latestTypingTime = 0;
    }

    @Override
    public void setMessage(String msg) {

    }

    @Override
    public void clearAndReloadMessages() {
        int start = 0;
        int firstLoadCount = curMsgNum > 0 ? curMsgNum : numPerPage;
        int unreadCount = connectionUtil.SelectUnReadCountByConvid(chatView.getToId(), chatView.getRealJid(), chatView.getChatType());
        if(unreadCount > MAX_UNREAD_MSG_LOAD_COUNT){
            firstLoadCount = MAX_UNREAD_MSG_LOAD_COUNT;
        }else if (unreadCount > firstLoadCount) {
            firstLoadCount = unreadCount;
        }
        List<IMMessage> historyMessage = connectionUtil.SelectInitReloadChatMessage(chatView.getToId(), chatView.getRealJid(), chatView.getChatType(), start, firstLoadCount);

        curMsgNum = historyMessage.size();
        if (curMsgNum > 0) {
            Collections.reverse(historyMessage);
            if (unreadCount > 0) {
                //该条显示在未读消息上面
                IMMessage hisDivide = new IMMessage();
                String uid = UUID.randomUUID().toString();
                hisDivide.setId(uid);
                hisDivide.setMessageID(uid);
                hisDivide.setType(ConversitionType.MSG_TYPE_CHAT);
                hisDivide.setMsgType(MessageType.MSG_HISTORY_SPLITER);
                hisDivide.setTime(historyMessage.get(0).getTime());
                if(curMsgNum >= unreadCount)
                    historyMessage.add(curMsgNum - unreadCount, hisDivide);
            }
            chatView.clearAndAddMsgs(historyMessage, unreadCount);
        } else {
            chatView.clearAndAddMsgs(historyMessage, 0);
        }
    }

    @Override
    protected void updateDbOnSuccess(IMMessage message, boolean updateRc) {
    }


    @Override
    protected IMMessage generateIMMessage() {
        IMMessage imMessage = MessageUtils.generateSingleIMMessage(chatView.getFromId(), chatView.getToId(), chatView.getChatType(), chatView.getRealJid(), cn);
        return imMessage;
    }

    @Override
    protected void handleSnapMessage(IMMessage message) {
        AutoDestroyMessageExtention ade = new AutoDestroyMessageExtention();
        ade.descStr = message.getBody();
        ade.message = message.getBody();
        ade.msgType = Integer.valueOf(message.getMsgType());
        message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeBurnAfterRead_VALUE);

        if (!TextUtils.isEmpty(String.valueOf(message.getMsgType())) &&
                !TextUtils.isEmpty(message.getExt())) {
            ade.message = message.getExt();
            ade.msgType = message.getMsgType();
        }
        message.setExt(JsonUtils.getGson().toJson(ade));

        message.setBody("此消息为阅后即焚消息，当前客户端不支持");
    }


    @Override
    public void setShakeMessage() {
        IMMessage message = generateIMMessage();
        message.setBody("[窗口抖动]");
        message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeShock_VALUE);
        connectionUtil.sendTextOrEmojiMessage(message);
        chatView.setNewMsg2DialogueRegion(message);
    }

//    @Override
//    public void startVideoRtc() {
//        IMMessage message = generateIMMessage();
//        WebRtcJson webRtcJson = new WebRtcJson();
//        webRtcJson.type = "create";
//        message.setBody("video command");
//        message.setType(ProtoMessageOuterClass.SignalType.SignalTypeWebRtc_VALUE);
//        message.setExt(JsonUtils.getGson().toJson(webRtcJson));
//        message.setMsgType(ProtoMessageOuterClass.MessageType.WebRTC_MsgType_Video_VALUE);
////        curMsgNum++;
//
//        connectionUtil.sendTextOrEmojiMessage(message);
////        chatView.setNewMsg2DialogueRegion(message);
//    }
//
//    @Override
//    public void startAudioRtc() {
//        IMMessage message = generateIMMessage();
//        WebRtcJson webRtcJson = new WebRtcJson();
//        webRtcJson.type = "create";
//        message.setBody("audio command");
//        message.setType(ProtoMessageOuterClass.SignalType.SignalTypeWebRtc_VALUE);
//        message.setExt(JsonUtils.getGson().toJson(webRtcJson));
//        message.setMsgType(ProtoMessageOuterClass.MessageType.WebRTC_MsgType_Audio_VALUE);
////        curMsgNum++;
//        connectionUtil.sendTextOrEmojiMessage(message);
////        chatView.setNewMsg2DialogueRegion(message);
//    }

    @Override
    public void didReceivedNotification(String key, Object... args) {
        Logger.i("返回的消息类型:" + key);
//        Logger.i("返回的消息对象:" + args[0]);
//        IMMessage imMessage = ((IMMessage) args[0]);

        if(chatView.getSearching()){
            return;
        }
        switch (key) {

            case QtalkEvent.SPECIFYNOTICE:
                if (args != null && args.length > 0) {
                    NoticeBean noticeBean = (NoticeBean) args[0];
//                    showNoticePopupWindow(noticeBean);
                    if (noticeBean.isIsCouslt()) {
                        if (Integer.valueOf(chatView.getChatType()) != ConversitionType.MSG_TYPE_CONSULT
                                && Integer.valueOf(chatView.getChatType()) != ConversitionType.MSG_TYPE_CONSULT_SERVER) {
//                            showUnReadCount();
                            return;
                        }
                        //新增客服排队系统，不能使用realfrom判断了
                        if (chatView.getToId().equalsIgnoreCase(noticeBean.getFrom())) {
//                        if (!chatView.getRealJid().equals(QtalkStringUtils.parseIdAndDomain(noticeBean.getRealTo()))) {
//                            showUnReadCount();
                            return;
                        }
                    } else {
                        if (!chatView.getToId().equals(QtalkStringUtils.parseIdAndDomain(noticeBean.getTo()))) {
//                            showUnReadCount();
                            return;
                        }
                    }
                    chatView.showNoticePopupWindow(noticeBean);
                }

                break;
            //二人消息类型 只做界面操作
            case QtalkEvent.CHAT_MESSAGE_ENCRYPT:
            case QtalkEvent.Chat_Message_Text:
                try {
                    IMMessage imMessage = ((IMMessage) args[0]);
                    //如果是抄送
                    if (imMessage.isCarbon()) {
                        //是客服消息
                        if (imMessage.getSignalType() == ProtoMessageOuterClass.SignalType.SignalTypeConsult_VALUE) {
                            if (Integer.valueOf(chatView.getChatType()) != ConversitionType.MSG_TYPE_CONSULT
                                    && Integer.valueOf(chatView.getChatType()) != ConversitionType.MSG_TYPE_CONSULT_SERVER) {
                                return;
                            }
                            //新增客服排队系统，不能使用realfrom判断了
                            if (!chatView.getRealJid().equals(QtalkStringUtils.parseIdAndDomain(imMessage.getRealto()))) {
                                return;
                            }
//                            if (!chatView.getToId().equalsIgnoreCase(imMessage.getFromID())) {
//                                return;
//                            }
                        } else {//不是客服消息
                            if (!chatView.getToId().equals(imMessage.getConversationID())) {
                                return;
                            }
                        }
                        //不是抄送
                    } else {
                        //是客服消息
                        if (imMessage.getSignalType() == ProtoMessageOuterClass.SignalType.SignalTypeConsult_VALUE) {
                            if (Integer.valueOf(chatView.getChatType()) != ConversitionType.MSG_TYPE_CONSULT
                                    && Integer.valueOf(chatView.getChatType()) != ConversitionType.MSG_TYPE_CONSULT_SERVER) {
                                return;
                            }
                            //新增客服排队系统，不能使用realfrom判断了
                            if (!chatView.getRealJid().equals(QtalkStringUtils.parseIdAndDomain(imMessage.getRealfrom()))) {
                                return;
                            }
//                            if (!chatView.getToId().equalsIgnoreCase(imMessage.getFromID())) {
//                                return;
//                            }
                        } else {//不是客服消息
                            if (!chatView.getToId().equals(QtalkStringUtils.parseIdAndDomain(imMessage.getFromID()))) {
                                return;
                            }
                        }
                    }
                    if (key.equals(QtalkEvent.CHAT_MESSAGE_ENCRYPT)) {//加密信令消息
                        imMessage.setDirection(IMMessage.DIRECTION_MIDDLE);
                        chatView.parseEncryptSignal(imMessage);
                        if (imMessage.getMsgType() == EncryptMessageType.BEGIN) return;
                    }
                    chatView.setNewMsg2DialogueRegion(imMessage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case QtalkEvent.Chat_Message_Text_After_DB://消息入库后的操作
                IMMessage imMessage = ((IMMessage) args[0]);
                if(!imMessage.isCarbon()){
                    if (imMessage.getSignalType() == ProtoMessageOuterClass.SignalType.SignalTypeConsult_VALUE) {
                        if (Integer.valueOf(chatView.getChatType()) != ConversitionType.MSG_TYPE_CONSULT
                                && Integer.valueOf(chatView.getChatType()) != ConversitionType.MSG_TYPE_CONSULT_SERVER) {
                            showUnReadCount();
                            return;
                        }
                        //新增客服排队系统，不能使用realfrom判断了
                        if (!chatView.getToId().equalsIgnoreCase(imMessage.getFromID())) {
//                        if (!chatView.getRealJid().equals(QtalkStringUtils.parseIdAndDomain(imMessage.getRealfrom()))) {
                            showUnReadCount();
                        }else {
                            connectionUtil.setSingleRead(imMessage, MessageStatus.STATUS_SINGLE_READED + "");
                        }
                        //不是客服消息
                    } else {
                        if (!chatView.getToId().equals(QtalkStringUtils.parseIdAndDomain(imMessage.getFromID()))) {
                            showUnReadCount();
                        }else {
                            connectionUtil.setSingleRead(imMessage, MessageStatus.STATUS_SINGLE_READED + "");
                        }
                    }
                }
                break;
            case QtalkEvent.CHAT_MESSAGE_SUBSCRIPTION:
                IMMessage imMessage_subcription = (IMMessage) args[0];
                if (imMessage_subcription.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeNotice_VALUE || imMessage_subcription.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeSystem_VALUE
                        || imMessage_subcription.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeGrabMenuVcard_VALUE || imMessage_subcription.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeGrabMenuResult_VALUE) {
                    if (!chatView.getToId().equals(QtalkStringUtils.parseIdAndDomain(imMessage_subcription.getFromID()))) {
                        showUnReadCount();
                    }else {
                        connectionUtil.setSingleRead(imMessage_subcription, MessageStatus.STATUS_SINGLE_READED + "");
                        chatView.setNewMsg2DialogueRegion(imMessage_subcription);
                    }
                }
                break;
            case QtalkEvent.LOGIN_EVENT:
                if (args[0].equals(LoginStatus.Login)) {
                    chatView.initActionBar();
                    clearAndReloadMessages();
                    showUnReadCount();


                }
                break;
            case QtalkEvent.Update_Voice_Message:
                updateVoiceMessage((IMMessage) args[0]);
                break;
            case QtalkEvent.Chat_Message_Input:
                IMMessage inputMessage = (IMMessage) args[0];
                if (inputMessage.getConversationID().equals(chatView.getToId())) {
                    chatView.setTitleState(CommonConfig.globalContext.getString(R.string.atom_ui_typing));
                }

                break;
            case QtalkEvent.Chat_Message_Revoke:
                chatView.revokeItem((IMMessage) args[0]);
                break;
            case QtalkEvent.SEND_PHOTO_AFTER_EDIT:
                chatView.sendEditPic((String) args[0]);
                break;
            case QtalkEvent.Message_Read_Mark:
                //当有消息已读标记时
//                if (args[0].equals("HaveUpdate")) {
                //获取消息条目数
                showUnReadCount();
//                }
                break;
            case QtalkEvent.Group_Chat_Message_Text_After_DB:
                //当有群组新消息时
                //获取消息条目数
                showUnReadCount();
                break;
            case QtalkEvent.Remove_Session:
                //收到一个删除sessionlist的通知,更新条目数
                showUnReadCount();
                break;

//            case QtalkEvent.ERROR_MESSAGE:
//                if(CommonConfig.isDebug) {
//                    showErrorMessage((String) args[0]);
//                }
            case QtalkEvent.Update_ReMind:
                showUnReadCount();
                break;
            case QtalkEvent.UPDATE_QUICK_REPLY:

                break;
            case QtalkEvent.REFRESH_NICK:
                chatView.initActionBar();
                break;
            case QtalkEvent.CLEAR_MESSAGE:
                String xmppid = (String) args[0];
                if(chatView.getToId().equals(xmppid)){
                    chatView.clearAndAddMsgs(new ArrayList<IMMessage>(),0);
                }

                break;
            case QtalkEvent.NOTIFY_RTCMSG:
                boolean isVideo = (boolean) args[0];
//                if(isVideo) {
//                    startVideoRtc();
//                } else {
//                    startAudioRtc();
//                }
                break;
            case QtalkEvent.Chat_Message_Read_State:
                showUnReadCount();
                break;
//            case QtalkEvent.SPECIFYNOTICE:
//                if(args!=null && args.length>0){
//                    NoticeBean noticeBean = (NoticeBean) args[0];
//                    if(noticeBean != null && chatView.getToId().equals(QtalkStringUtils.userId2Jid(noticeBean.getFrom())))
//                        chatView.popNotice(noticeBean);
//                }
//                break;
            case QtalkEvent.SEND_MESSAGE_RENDER:
                IMMessage sendMesg = ((IMMessage) args[0]);
                if(sendMesg != null) {
                    chatView.setNewMsg2DialogueRegion(sendMesg);
                }
                break;
            case QtalkEvent.PAY_RED_ENVELOP_CHOICE:
                String result = (String) args[0];
                String rid = "";
                if(args.length > 1){
                    rid = (String) args[1];
                }
                chatView.payRedEnvelopChioce(result,rid);
                break;
            case QtalkEvent.PAY_AUTH:
                String authInfo = (String) args[0];
                chatView.payAuth(authInfo);
                break;
            case QtalkEvent.PAY_ORDER:
                String orderInfo = (String) args[0];
                chatView.payOrder(orderInfo);
                break;
            case QtalkEvent.PAY_FAIL:
                String fail = (String) args[0];
                chatView.showToast(Constants.Alipay.AUTH.equals(fail) ? "账户校验失败！" : "支付失败！");
                break;
        }
    }


}
