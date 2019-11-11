package com.qunar.im.ui.presenter.impl;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.qunar.im.ui.presenter.IPGroupRtc;
import com.qunar.im.base.util.Constants;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.HttpUtil;
import com.qunar.im.base.jsonbean.AutoDestroyMessageExtention;
import com.qunar.im.base.module.AtData;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.ui.presenter.IAddEmojiconPresenter;
import com.qunar.im.ui.presenter.ICloudRecordPresenter;
import com.qunar.im.ui.presenter.IMeetingRTC;
import com.qunar.im.ui.presenter.IShowNickPresenter;
import com.qunar.im.ui.presenter.ISnapPresenter;
import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.ui.presenter.views.IShowNickView;
import com.qunar.im.base.structs.MessageType;
import com.qunar.im.base.util.ChatTextHelper;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.MessageUtils;
import com.qunar.im.base.view.faceGridView.EmoticonEntity;
import com.qunar.im.core.enums.LoginStatus;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.utils.QtalkStringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by xinbo.wang on 2015/2/10.
 */
//群会话实现
public class MultipleSessionPresenter extends ChatPresenter implements
        ICloudRecordPresenter, ISnapPresenter, IAddEmojiconPresenter,
        IShowNickPresenter, IMeetingRTC, IPGroupRtc {

    public MultipleSessionPresenter() {
    }

    //初始化的一些操作
    @Override
    public void propose() {
        reloadMessages();

        //查询所有未读消息,进行左上角未读数更新
        showUnReadCount();

    }

    @Override
    protected IMMessage send2Server(String msg) {
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

        if (snapStatus) {
            HttpUtil.handleSnapMessage(message);
        }
        curMsgNum++;
        chatView.setNewMsg2DialogueRegion(message);
        //这个判断里面是判断是否有@ 并且做好数据准备
        if (chatView.getAtList() != null && chatView.getAtList().size() > 0) {
            List<AtData> dataList = new ArrayList<>();
            AtData ad = new AtData();
            ad.setType(10001);
            List<AtData.DataBean> atList = new ArrayList<>();
            for (Map.Entry<String, String> entry : chatView.getAtList().entrySet()) {
                AtData.DataBean atdb = new AtData.DataBean();
                atdb.setJid(entry.getKey());
                atdb.setText(entry.getValue().trim());
                atList.add(atdb);
            }
            ad.setData(atList);
            dataList.add(ad);
            //12是@消息 不在pb类里面存在,应为新类型
            message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeGroupAt_VALUE);
            message.setBackUp(new Gson().toJson(dataList));
        }
        connectionUtil.sendGroupTextOrEmojiMessage(message);

        latestTypingTime = 0;
        return message;
    }

    @Override
    protected String addParams2Url(String msg) {
        return msg;
    }

    @Override
    public void receiveMsg(IMMessage message) {

    }


    @Override
    public void showMoreOldMsg(boolean isFromGroup) {
        int count = chatView.getListSize();
        if (tip) {
            count--;
        }
        connectionUtil.SelectHistoryGroupChatMessage(chatView.getToId(), chatView.getRealJid(), count, numPerPage, new ConnectionUtil.HistoryMessage() {
            @Override
            public void onMessageResult(List<IMMessage> messageList) {
//                chatView.addHistoryMessage(messageList);
                if (messageList.size() > 0) {
                    curMsgNum += messageList.size();
                    historyTime = messageList.get(0).getTime().getTime() - 1;
                    Collections.reverse(messageList);
                }
                chatView.addHistoryMessage(messageList);
            }
        });
    }

    @Override
    public void showMoreOldMsgUp(boolean isFromGroup) {

    }

    @Override
    public void clearAndReloadMessages() {
        int start = 0;
        int offset = curMsgNum > 0 ? curMsgNum : numPerPage;
        int unreadCount = connectionUtil.SelectUnReadCountByConvid(chatView.getToId(), chatView.getRealJid(), chatView.getChatType());
        if (unreadCount > MAX_UNREAD_MSG_LOAD_COUNT) {
            offset = MAX_UNREAD_MSG_LOAD_COUNT;
        } else if (unreadCount > offset) {
            offset = unreadCount;
        }
        List<IMMessage> historyMessage = connectionUtil.SelectInitReloadGroupChatMessage(chatView.getToId(), chatView.getRealJid(), start, offset);

        curMsgNum = historyMessage.size();
        if (curMsgNum > 0) {
            Collections.reverse(historyMessage);
            if (unreadCount > 0) {
                //该条显示在未读消息上面
                IMMessage hisDivide = new IMMessage();
                String uid = UUID.randomUUID().toString();
                hisDivide.setMessageID(uid);
                hisDivide.setId(uid);
                hisDivide.setType(ConversitionType.MSG_TYPE_GROUP);
                hisDivide.setMsgType(MessageType.MSG_HISTORY_SPLITER);
                hisDivide.setDirection(IMMessage.DIRECTION_RECV);
                hisDivide.setTime(historyMessage.get(0).getTime());
                if(curMsgNum >= unreadCount)
                    historyMessage.add(curMsgNum - unreadCount, hisDivide);
            }
            chatView.clearAndAddMsgs(historyMessage, unreadCount);
        } else {
            chatView.clearAndAddMsgs(historyMessage, 0);
        }
    }

    public boolean tip = false;

    @Override
    public void reloadMessages() {
        int start = 0;
        int offset = curMsgNum > 0 ? curMsgNum : numPerPage;
        int unreadCount = chatView.getUnreadMsgCount();//改成从view获取
        if (unreadCount < 0) {
            unreadCount = connectionUtil.SelectUnReadCountByConvid(chatView.getToId(), chatView.getRealJid(), chatView.getChatType());
        }
        if (unreadCount > MAX_UNREAD_MSG_LOAD_COUNT) {
            offset = MAX_UNREAD_MSG_LOAD_COUNT;
        } else if (unreadCount > offset) {
            offset = unreadCount;
        }
        List<IMMessage> historyMessage = connectionUtil.SelectInitReloadGroupChatMessage(chatView.getToId(), chatView.getRealJid(), start, offset);

        curMsgNum = historyMessage.size();
        if (curMsgNum > 0) {
            Collections.reverse(historyMessage);
            if (unreadCount > 0) {
                //该条显示在未读消息上面
                IMMessage hisDivide = new IMMessage();
                String uid = UUID.randomUUID().toString();
                hisDivide.setMessageID(uid);
                hisDivide.setId(uid);
                hisDivide.setType(ConversitionType.MSG_TYPE_GROUP);
                hisDivide.setMsgType(MessageType.MSG_HISTORY_SPLITER);
                hisDivide.setDirection(IMMessage.DIRECTION_RECV);
                hisDivide.setTime(historyMessage.get(0).getTime());
                if (curMsgNum >= unreadCount)
                    historyMessage.add(curMsgNum - unreadCount, hisDivide);
                tip = true;
            }
            chatView.setHistoryMessage(historyMessage, unreadCount);
//            if (unreadCount > 0) {
//                connectionUtil.sendGroupAllRead(chatView.getToId());
//            }
        } else {
            chatView.setHistoryMessage(historyMessage, 0);
        }
    }

    @Override
    public void reloadMessagesFromTime(long time) {
        List<IMMessage> historyMessage = connectionUtil.selectGroupMessageAfterSearch(chatView.getToId(), chatView.getRealJid(), time);
        Collections.reverse(historyMessage);
        chatView.setHistoryMessage(historyMessage, 0);
    }

    @Override
    public void startGroupVideoRtc() {
        IMMessage message = generateIMMessage();
        message.setBody("当前客户端不支持群视频");
        message.setMsgType(ProtoMessageOuterClass.MessageType.WebRTC_MsgType_Video_Group_VALUE);
        curMsgNum++;
        chatView.setNewMsg2DialogueRegion(message);
        connectionUtil.sendGroupTextOrEmojiMessage(message);
    }

    @Override
    public void sendRobotMsg(String msg) {

    }

    @Override
    public void setMessage(String msg) {

    }


    @Override
    public void transferConversation() {

    }

    @Override
    public void sendTypingStatus() {

    }


    @Override
    protected void updateDbOnSuccess(IMMessage message, boolean updateRc) {
    }


    @Override
    protected IMMessage generateIMMessage() {
        return MessageUtils.generateMucIMMessage(chatView.getFromId(), chatView.getToId());
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


    IShowNickView showNickView;

    @Override
    public void checkShowNick() {

    }

    @Override
    public void setShowNickView(IShowNickView view) {
        showNickView = view;
    }

    @Override
    public void startVideoMeeting() {

    }

    @Override
    public void startAudioMeeting() {

    }

    @Override
    public void didReceivedNotification(String key, Object... args) {
        if(chatView.getSearching()){
           return;
        }
        switch (key) {
            case QtalkEvent.Group_Chat_Message_Text://只做界面展示
                IMMessage imMessage = (IMMessage) args[0];
                if (chatView.getToId().equals(QtalkStringUtils.parseIdAndDomain(imMessage.getConversationID()))) {
                    //正常消息&抄送过来的消息
                    if (!chatView.isMessageExit(imMessage.getId())) {
                        //正常消息&抄送过来的消息
                        chatView.setNewMsg2DialogueRegion(imMessage);
                    }
                }
                break;
            case QtalkEvent.Group_Chat_Message_Text_After_DB://入库后的操作
                IMMessage imMessageDb = (IMMessage) args[0];
                if (!chatView.getToId().equals(QtalkStringUtils.parseIdAndDomain(imMessageDb.getConversationID()))) {
                    showUnReadCount();
                } else {
                    connectionUtil.setGroupRead((IMMessage) args[0]);
                }
                break;
            case QtalkEvent.LOGIN_EVENT:
                if (args[0].equals(LoginStatus.Login)) {

                    clearAndReloadMessages();
                }
//                chatView.closeActivity();
                break;
            case QtalkEvent.Update_Voice_Message:
                updateVoiceMessage((IMMessage) args[0]);
                break;
            case QtalkEvent.Chat_Message_Revoke:
                chatView.revokeItem((IMMessage) args[0]);
                break;
            case QtalkEvent.SEND_PHOTO_AFTER_EDIT:
                chatView.sendEditPic((String) args[0]);
                break;
            //收到了销毁群或离开群 群组广播
            case QtalkEvent.Destory_Muc:
            case QtalkEvent.Remove_Session:
                String deleteId = (String) args[0];
                if (deleteId != null && deleteId.equals(chatView.getToId())) {
                    chatView.closeActivity();
                }
                break;

            case QtalkEvent.Message_Read_Mark:
                //当有消息已读标记时
//                if (args[0].equals("HaveUpdate")) {
                //获取消息条目数
                showUnReadCount();
//                }
                break;
            case QtalkEvent.Chat_Message_Text_After_DB:
                //当有新消息时
                //获取消息条目数
                showUnReadCount();
                break;

            case QtalkEvent.CHAT_MESSAGE_SUBSCRIPTION:
                //收到一个删除subscription的通知,更新条目数
                showUnReadCount();
                break;
//            case QtalkEvent.ERROR_MESSAGE:
//                if(CommonConfig.isDebug) {
//                    showErrorMessage((String) args[0]);
//                }
            case QtalkEvent.Update_ReMind:
                showUnReadCount();
                break;
            case QtalkEvent.CLEAR_MESSAGE:
                String xmppid = (String) args[0];
                if (chatView.getToId().equals(xmppid)) {
                    chatView.clearAndAddMsgs(new ArrayList<IMMessage>(), 0);
                }

                break;
            case QtalkEvent.Chat_Message_Read_State:
                showUnReadCount();
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
