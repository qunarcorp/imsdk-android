package com.qunar.im.ui.presenter.views;

import com.qunar.im.base.module.IMMessage;

import java.util.List;

/**
 * Created by xinbo.wang on 2015/6/8.
 */
public interface IAnnounceView {
    void setAnnounceList(List<IMMessage> msgs);
    void setTitle(String name);
    void addHistoryMessage(List<IMMessage> historyMessage);
}
