package com.qunar.im.ui.presenter;

import com.qunar.im.ui.presenter.views.IChatingPanelView;
import com.qunar.im.ui.presenter.views.IShowNickView;

/**
 * Created by saber on 15-7-16.
 */
public interface IChatingPanelPresenter {
    void setPanelView(IChatingPanelView view);
    void setShowNickView(IShowNickView view);
    void topMessage();
    void setConversationTopOrCancel();
    void setConversationReMindOrCancel();
    void showIsTop();
    void showIsDnd();
    void showIsNick();
    void dnd();
    void nick(boolean param);
}
