package com.qunar.im.ui.presenter;

import com.qunar.im.ui.presenter.views.IManageEmotionView;

/**
 * Created by saber on 16-4-20.
 */
public interface IManageEmotionPresenter {
    boolean deleteEmotions();
    boolean addEmotions();
    void setView(IManageEmotionView view);
    void loadLocalEmotions();
}
