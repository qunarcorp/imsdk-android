package com.qunar.im.ui.presenter;

import com.qunar.im.ui.presenter.views.IRobotConvView;

/**
 * Created by xinbo.wang on 2016/7/25.
 */
public interface IRobotConvPresenter {
    void setIRobotConvView(IRobotConvView view);
    void loadConvList();
}
