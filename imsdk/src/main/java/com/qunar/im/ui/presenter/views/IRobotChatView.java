package com.qunar.im.ui.presenter.views;

import com.qunar.im.base.jsonbean.RobotInfoResult;

/**
 * Created by saber on 15-9-15.
 */
public interface IRobotChatView {
    String getRobotId();
    void setRobotInfo(RobotInfoResult.RobotBody body,int pubType);
}
