package com.qunar.im.ui.presenter;

import com.qunar.im.base.module.PublishPlatform;
import com.qunar.im.ui.presenter.impl.RobotListPresenter;

/**
 * Created by zhaokai on 15-9-14.
 */
public interface IRobotListPresenter {
    void init();

    void setIRobotListView(RobotListPresenter.UpdateableView view);
    void loadRobotList();
    void updateRobotList(PublishPlatform publishPlatform);
    void loadRobotIdList4mNet();
    void searchRobot4mNet();
    void selectRobot();
}
