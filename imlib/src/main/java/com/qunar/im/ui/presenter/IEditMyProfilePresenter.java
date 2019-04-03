package com.qunar.im.ui.presenter;

import com.qunar.im.ui.presenter.views.IMyProfileView;

/**
 * Created by saber on 16-2-2.
 */
public interface IEditMyProfilePresenter {
    void setPersonalInfoView(IMyProfileView view);
    void updateMood();
    void loadMood();
    void updateMarkupName();
    String getMarkNames();
}
