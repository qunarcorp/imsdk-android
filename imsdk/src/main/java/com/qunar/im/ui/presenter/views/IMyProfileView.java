package com.qunar.im.ui.presenter.views;

/**
 * Created by saber on 16-2-2.
 */
public interface IMyProfileView {
    String getMood();
    void setMood(String mood);
    String getJid();

    String getMarkup();
    void setMarkup(boolean isScuess);
}
