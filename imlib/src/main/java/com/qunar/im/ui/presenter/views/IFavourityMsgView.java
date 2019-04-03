package com.qunar.im.ui.presenter.views;

import com.qunar.im.base.module.FavouriteMessage;

import java.util.List;

/**
 * Created by saber on 16-1-25.
 */
public interface IFavourityMsgView {
    List<FavouriteMessage> getSelectedMsgs();
    void setFavourityMessages(List<FavouriteMessage> list);
}
