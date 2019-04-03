package com.qunar.im.ui.presenter.impl;

import android.text.TextUtils;

import com.qunar.im.base.jsonbean.GroupChatOfflineResult;
import com.qunar.im.base.jsonbean.MultiOfflineMsgResult;
import com.qunar.im.base.jsonbean.OfflineSingleMsgResult;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.ui.presenter.ICloudRecordPresenter;
import com.qunar.im.ui.presenter.views.IChatView;
import com.qunar.im.base.protocol.MessageAPI;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.util.ListUtil;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.utils.QtalkStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by xinbo.wang on 2015/5/13.
 */
public class CloudRecordPresent implements ICloudRecordPresenter {
    private static final String TAG = CloudRecordPresent.class.getSimpleName();

    private long historyTime = System.currentTimeMillis();
    private final int numPerPage = 20;
    private IChatView chatView;

    public CloudRecordPresent()
    {
    }


    @Override
     public void setView(IChatView view) {
        chatView = view;
        chatView.setTitle("云端历史记录");
    }

    @Override
    public void showMoreOldMsg(boolean isFromGroup) {
        if(isFromGroup)
        {
            getMultiRecord();
        }
        else {
            getSingleRecord();
        }
    }

    private void getMultiRecord()
    {
        final String roomId = chatView.getToId();
        MessageAPI.getMultiChatOfflineMsg(roomId, historyTime, numPerPage, 0, new ProtocolCallback.UnitCallback<GroupChatOfflineResult>() {
            @Override
            public void onCompleted(GroupChatOfflineResult groupChatOfflineResult) {
                if (groupChatOfflineResult == null || groupChatOfflineResult.data == null) {
                    return;
                }
                List<IMMessage> offlineMsgs = new ArrayList<IMMessage>();
                List<MultiOfflineMsgResult> offlineMsgResults = groupChatOfflineResult.data.Msg;
                if (offlineMsgResults != null && offlineMsgResults.size() > 0) {
                    long latest = 0;
                    for (int i = 0; i < offlineMsgResults.size(); i++) {
                        final MultiOfflineMsgResult result = offlineMsgResults.get(i);
                        if (TextUtils.isEmpty(result.B)) {
                            continue;
                        }
                    }
                    historyTime = latest - 1;
                    if (offlineMsgs.size() > 0) {
                        chatView.addHistoryMessage(offlineMsgs);
                    }
                    return;
                }
                IMMessage message = new IMMessage();
                message.setId(UUID.randomUUID().toString());
                message.setDirection(IMMessage.DIRECTION_MIDDLE);
                message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeText_VALUE);
                message.setBody("没有更多消息了");
                offlineMsgs.add(0, message);
                chatView.addHistoryMessage(offlineMsgs);
            }

            @Override
            public void onFailure(String errMsg) {
                chatView.setHistoryMessage(null,0);
            }
        });
    }

    private void getSingleRecord()
    {
        MessageAPI.getSingleChatOfflineMsg(QtalkStringUtils.userId2Jid(CurrentPreference.getInstance().getUserid())
                ,chatView.getToId(), historyTime, numPerPage, 0, new ProtocolCallback.UnitCallback<OfflineSingleMsgResult>() {
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
                    }
                    historyTime = latest  - 1;
                    if(offlineMsgs.size()>0) {
                        chatView.addHistoryMessage(offlineMsgs);
                    }
                    return;
                }
                IMMessage message = new IMMessage();
                message.setId(UUID.randomUUID().toString());
                message.setDirection(IMMessage.DIRECTION_MIDDLE);
                message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeText_VALUE);
                message.setBody("没有更多消息了");
                offlineMsgs.add(0, message);
                chatView.addHistoryMessage(offlineMsgs);
            }

            @Override
            public void onFailure(String errMsg) {
                chatView.setHistoryMessage(null,0);
            }
        });
    }
}
