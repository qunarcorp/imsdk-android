package com.qunar.im.ui.presenter.views;

import com.qunar.im.base.module.UserConfigData;

import java.util.List;

/**
 * Created by saber on 16-4-20.
 */
public interface IManageEmotionView {
    List<UserConfigData> getDeletedEmotions();
    List<String> getAddedEmotions();
    void updateSuccessful();
    void setEmotionList(List<String> list);
    void setEmotionNewList(List<UserConfigData> list);
}
