package com.qunar.im.ui.presenter.views;

import android.content.Context;

import com.qunar.im.base.module.IMMessage;

import java.util.List;

/**
 * Created by saber on 15-7-7.
 */
public interface ILocalChatRecordView {

    long getCurrentMsgRecTime();
    void insertHistory2Head(List<IMMessage> list);
    String getFromId();
    String getUserId();
    void addHistoryMessage(List<IMMessage> historyMessage);
    Context getContext();
}
