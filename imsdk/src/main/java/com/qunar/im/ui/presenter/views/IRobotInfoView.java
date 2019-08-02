package com.qunar.im.ui.presenter.views;

import java.util.Map;

/**
 * Created by saber on 15-9-15.
 */
public interface IRobotInfoView {
    String getRobotId();
    void setFollowRobotResult(boolean b);
    void setUnfollowRobotResult(boolean b);
    void setInfo(Map<String, String> infoMap);
    String getUserId();
    void setFollowStatus(boolean followStatus);
}
