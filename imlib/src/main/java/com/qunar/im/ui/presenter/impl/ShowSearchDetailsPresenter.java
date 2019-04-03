package com.qunar.im.ui.presenter.impl;

import android.text.TextUtils;

import com.qunar.im.utils.HttpUtil;
import com.qunar.im.base.jsonbean.GroupChatOfflineResult;
import com.qunar.im.base.jsonbean.MultiOfflineMsgResult;
import com.qunar.im.base.jsonbean.OfflineSingleMsgResult;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.ui.presenter.ILocalChatRecordPresenter;
import com.qunar.im.ui.presenter.views.ILocalChatRecordView;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.ListUtil;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.protobuf.utils.XmlUtils;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.protobuf.utils.XmlUtils;
import com.qunar.im.utils.HttpUtil;
import com.qunar.im.utils.QtalkStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by xinbo.wang on 2017-01-03.
 */
public class ShowSearchDetailsPresenter implements ILocalChatRecordPresenter {
    private final static String TAG = ShowSearchDetailsPresenter.class.getCanonicalName();
    ILocalChatRecordView chatRecordView;
    int numPerPage = 20;
    long highTime;
    long lowTime;
    boolean isGroup;

    @Override
    public void setLocalChatRecordView(ILocalChatRecordView view) {
        chatRecordView = view;
        highTime = chatRecordView.getCurrentMsgRecTime();
        lowTime = highTime;
        isGroup = chatRecordView.getFromId().contains("@conference");
    }

    @Override
    public void loadOldderMsg() {
        if(isGroup)
        {
            getMultiRecord(0);
        }
        else {
            getSingleRecord(0);
        }
    }

    @Override
    public void loadNewerMsg() {
        if(isGroup)
        {
            getMultiRecord(1);
        }
        else {
            getSingleRecord(1);
        }
    }
    private void getMultiRecord(final int upFlag)
    {
        final String roomId = chatRecordView.getFromId();
        HttpUtil.getMultiChatOfflineMsg(roomId, upFlag==0?highTime:lowTime, numPerPage, upFlag, new ProtocolCallback.UnitCallback<GroupChatOfflineResult>() {
            @Override
            public void onCompleted(GroupChatOfflineResult groupChatOfflineResult) {
                if (groupChatOfflineResult == null || groupChatOfflineResult.data == null) {
                    return;
                }
                String roomJid = QtalkStringUtils.roomId2Jid(roomId);
                List<IMMessage> offlineMsgs = new ArrayList<IMMessage>();
                List<MultiOfflineMsgResult> offlineMsgResults = groupChatOfflineResult.data.Msg;
                JsonUtils.getGson().toJson(offlineMsgResults);
                if (offlineMsgResults != null && offlineMsgResults.size() > 0) {
                    long latest = 0;
                    for (int i = 0; i < offlineMsgResults.size(); i++) {
                        final MultiOfflineMsgResult result = offlineMsgResults.get(i);
                        if (TextUtils.isEmpty(result.B)) {
                            continue;
                        }
//                        try {
                            //转换对象
                            //生成对象类似 {"message":{"from":"dba632082f6b4c7f89159c47537df561@conference.ejabhost1\/胡滨hubin","to":"dba632082f6b4c7f89159c47537df561@conference.ejabhost1","msec_times":"1505371744263","realfrom":"hubin.hu@ejabhost1","type":"groupchat"},"body":{"msgType":"1","maType":"1","id":"E4E3702936264A7FB68F5C7C37A31018","_text":"教小拿 [obj type=\"image\" value=\"https:\/\/qt.qunar.com\/file\/v2\/download\/temp\/538890fa234fa6b19a0545db02e5ce4f.jpg?name=538890fa234fa6b19a0545db02e5ce4f.jpg&file=file\/538890fa234fa6b19a0545db02e5ce4f.jpg&FileName=file\/538890fa234fa6b19a0545db02e5ce4f.jpg\" width=550.000000 height=464.000000 ]"},"stime":{"stamp":"20170914T06:49:04"}}
                           IMMessage message = XmlUtils.parseXmlToIMMessage(result.B);

                        if (message == null) continue;
                        message.setNickName(result.N);

                        if (upFlag==0&&latest == 0) latest = message.getTime().getTime();
                        else if(upFlag==1) latest = message.getTime().getTime();
                        if (message.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeRedPackInfo_VALUE ||
                                ProtoMessageOuterClass.MessageType.MessageTypeBurnAfterRead_VALUE == message.getMsgType())
                            continue;
                        message.setConversationID(roomJid);
                        offlineMsgs.add(message);
                    }
                    if(upFlag ==0) {
                        chatRecordView.insertHistory2Head(offlineMsgs);
                        highTime = latest;
                    }
                    else {
                        chatRecordView.addHistoryMessage(offlineMsgs);
                        lowTime = latest;
                    }
                    return;
                }
                IMMessage message = new IMMessage();
                String uid = UUID.randomUUID().toString();
                message.setId(uid);
                message.setMessageID(uid);
                message.setDirection(IMMessage.DIRECTION_MIDDLE);
                message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeText_VALUE);
                message.setBody("没有更多消息了");
                offlineMsgs.add(0, message);
                if(upFlag ==0) {
                    chatRecordView.insertHistory2Head(offlineMsgs);
                }
                else {
                    chatRecordView.addHistoryMessage(offlineMsgs);
                }
            }

            @Override
            public void onFailure(String errMsg) {

            }
        });
    }

    private void getSingleRecord(final int upFlag)
    {
        HttpUtil.getSingleChatOfflineMsg(QtalkStringUtils.userId2Jid(CurrentPreference.getInstance().getUserid())
                ,chatRecordView.getFromId(), upFlag==0?highTime:lowTime, numPerPage, upFlag, new ProtocolCallback.UnitCallback<OfflineSingleMsgResult>() {
                    @Override
                    public void onCompleted(OfflineSingleMsgResult offlineMsgResults) {
                        List<IMMessage> offlineMsgs = new ArrayList<IMMessage>();
                        if (offlineMsgResults != null && !ListUtil.isEmpty(offlineMsgResults.data)) {
                            long latest = 0;
                            for (int i = 0; i <offlineMsgResults.data.size(); i++) {
                                final OfflineSingleMsgResult.OfflineMsgResult result = offlineMsgResults.data.get(i);
                                if(TextUtils.isEmpty(result.B))
                                {
                                    continue;
                                }
//                                Message stanza = null;
//                                try {
                                  IMMessage message =   XmlUtils.parseXmlToIMMessage(result.B);
//                                    stanza = PacketParserUtils.parseMessage(PacketParserUtils.getParserFor(result.B));
//                                } catch (XmlPullParserException e) {
//                                    LogUtil.e(TAG,"error",e);
//                                } catch (IOException e) {
//                                    LogUtil.e(TAG,"error",e);
//                                } catch (SmackException e) {
//                                    LogUtil.e(TAG,"error",e);
//                                }
//                                if(stanza == null) continue;
//                                if(TextUtils.isEmpty(stanza.getFrom()))stanza.setFrom(result.F+"@"+result.FH);
//                                if(TextUtils.isEmpty(stanza.getTo())) stanza.setTo(result.T+"@"+result.TH);
//                                IMMessage message = IMLogic.instance().getInstantMessageIsolation().parseMessage2IMMsg(stanza);
                                if(message == null) continue;
                                if(upFlag==0&&latest == 0) latest = message.getTime().getTime();
                                else if(upFlag==1) latest = message.getTime().getTime();
                                if(message.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeRedPackInfo_VALUE||
                                        ProtoMessageOuterClass.MessageType.MessageTypeBurnAfterRead_VALUE==message.getMsgType()) continue;
//                                ReloadDataAfterReconnect.preHandleSingle(message, result.F+"@" + result.FH,
//                                        result.T + "@" + result.TH);
                                message.setIsRead(result.R);
                                offlineMsgs.add(message);
                            }
                            if(offlineMsgs.size()>0) {
                                if(upFlag==0) {
                                    chatRecordView.insertHistory2Head(offlineMsgs);
                                    highTime = latest;
                                }
                                else{
                                    chatRecordView.addHistoryMessage(offlineMsgs);
                                    lowTime = latest;
                                }
                            }
                            return;
                        }
                        IMMessage message = new IMMessage();
                        String uid = UUID.randomUUID().toString();
                        message.setId(uid);
                        message.setMessageID(uid);
                        message.setDirection(IMMessage.DIRECTION_MIDDLE);
                        message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeText_VALUE);
                        message.setBody("没有更多消息了");
                        offlineMsgs.add(0, message);
                        if(upFlag==0) {
                            chatRecordView.insertHistory2Head(offlineMsgs);
                        }
                        else {
                            chatRecordView.addHistoryMessage(offlineMsgs);
                        }
                    }

                    @Override
                    public void onFailure(String errMsg) {
                    }
                });
    }

}
