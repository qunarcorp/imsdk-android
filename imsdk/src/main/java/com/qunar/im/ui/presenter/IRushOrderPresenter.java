package com.qunar.im.ui.presenter;

import com.qunar.im.base.jsonbean.ThirdResponseMsgJson;
import com.qunar.im.base.module.IMMessage;

/**
 * Created by xinbo.wang on 2016/5/3.
 */
public interface IRushOrderPresenter {
    void rushOrder(String dealId, IMMessage message);
    void updateRushResult(ThirdResponseMsgJson responseMsgJson);
    void clearRushQueue();
    void initChanelId(String sessionId,String dealId);
}
