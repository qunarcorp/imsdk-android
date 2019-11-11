package com.qunar.im.ui.presenter;

import com.qunar.im.base.jsonbean.ExtendMessageEntity;
import com.qunar.im.base.jsonbean.HongbaoContent;
import com.qunar.im.base.jsonbean.QunarLocation;
import com.qunar.im.base.jsonbean.VideoMessageResult;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.ui.presenter.views.IChatView;

import java.util.List;

/**
 * Created by xinbo.wang on 2015/2/2.
 */
public interface IChatingPresenter extends IVoiceMessagePresenter {
    void setView(IChatView view);

    void removeEventForSearch();

    void addEventForSearch();

    //删除注册事件
    void removeEvent();

    void clearAndReloadMessages();
    /**
     * 加载初始化时的历史记录
     */
    void propose();
    void sendMsg();
    void receiveMsg(IMMessage message);
    void close();
    void sendImage();
    void sendFile(String file);
    void sendVideo(String file);
    void sendVideo(VideoMessageResult videoMessageResult);
    void reset();
    void transferMessage();
    void resendMessage();
    void sendLocation(QunarLocation location);
    void sendTypingStatus();
    void deleteMessge();
    void transferConversation();
    void reloadMessages();
    void reloadMessagesFromTime(long time);
    void hongBaoMessage(HongbaoContent content);
    void revoke();
    void sendExtendMessage(ExtendMessageEntity ext);
    void shareMessage(List<IMMessage> shareMsgs);
    void sendSyncConversation();

    void sendRobotMsg(String msg);

    void setMessage(String msg);

    void checkAlipayAccount();
}
