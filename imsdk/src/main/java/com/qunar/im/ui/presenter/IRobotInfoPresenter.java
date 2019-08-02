package com.qunar.im.ui.presenter;

import com.qunar.im.ui.presenter.views.IRobotInfoView;

/**
 * Created by saber on 15-9-15.
 */
public interface IRobotInfoPresenter {
    void followRobot();
    void unfollowRobot();
    void loadRobotInfo();
    void setRobotInfoView(IRobotInfoView view);
    void clearHistory();
}
