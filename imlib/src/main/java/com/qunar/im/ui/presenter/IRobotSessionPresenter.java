package com.qunar.im.ui.presenter;

import com.qunar.im.ui.presenter.views.IRobotChatView;

/**
 * Created by zhaokai on 15-9-11.
 */
public interface IRobotSessionPresenter extends IChatingPresenter {
    void setIRobotChatView(IRobotChatView robotChatView);
    void sendActionMsg(String body);
}
