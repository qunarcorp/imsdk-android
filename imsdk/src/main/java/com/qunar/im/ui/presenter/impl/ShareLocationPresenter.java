package com.qunar.im.ui.presenter.impl;

import com.qunar.im.base.jsonbean.ShareLocationData;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.ui.presenter.IShareLocationPresenter;
import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.ui.presenter.views.IShareLocationView;
import com.qunar.im.base.structs.MessageStatus;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.ListUtil;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.common.CurrentPreference;
import com.qunar.im.utils.QtalkStringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by zhaokai on 16-2-19.
 */
public class ShareLocationPresenter implements IShareLocationPresenter {

    private IShareLocationView view;
    @Override
    public void joinShareLocation() {
        //自己创建的位置共享不需要加入
        if (view == null ||
                view.getFromId()==null||
                view.getFromId().equals(QtalkStringUtils.userId2Jid(CurrentPreference.getInstance().getUserid()))){
            return;
        }
        IMMessage message = generateIMMessage(view.getFromId());
        message.setBody("Join Share Location");
    }

    @Override
    public void quitShareLocation() {
        if (view == null || ListUtil.isEmpty(view.getMembers())){
            return;
        }
        for(String id:view.getMembers()) {
            IMMessage message = generateIMMessage(id);
            message.setBody("Quit Share Location");
        }
    }

    @Override
    public void sendLocationData(ShareLocationData data) {
        if (view == null || data == null || ListUtil.isEmpty(view.getMembers())){
            return;
        }
        for(String id:view.getMembers()) {
            if (id.equals(QtalkStringUtils.userId2Jid(CurrentPreference.getInstance().getUserid()))){
                //不向自己发送位置消息
                continue;
            }
            String json = JsonUtils.getGson().toJson(data, ShareLocationData.class);
            IMMessage message = generateIMMessage(id);
            message.setBody(json);
        }
    }

    @Override
    public void setShareLocationView(IShareLocationView view) {
        this.view = view;
    }

    private IMMessage generateIMMessage(String toId)
    {
        IMMessage message = new IMMessage();
        Date time = Calendar.getInstance().getTime();
        time.setTime(time.getTime() + CommonConfig.divideTime);
        String id = UUID.randomUUID().toString();
        message.setId(id);
        message.setType(ConversitionType.MSG_TYPE_SHARE_LOCATION);
        message.setFromID(QtalkStringUtils.userId2Jid(CurrentPreference.getInstance().getUserid()));
        message.setToID(toId);
        message.setMessageID(id);
        message.setTime(time);
        message.setDirection(IMMessage.DIRECTION_SEND);
        message.setIsRead(IMMessage.MSG_READ);
        message.setReadState(MessageStatus.REMOTE_STATUS_CHAT_SUCCESS);
        message.setConversationID(toId);
        return message;
    }
}