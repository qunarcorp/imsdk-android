package com.qunar.im.ui.presenter;

import com.qunar.im.ui.presenter.views.IManageEmotionView;
import com.qunar.im.ui.presenter.views.ReleaseCircleView;

public interface ReleaseCirclePresenter {
    boolean release();
    void setView(ReleaseCircleView view);
    void getAnonymous();
    String getUUID();
}
