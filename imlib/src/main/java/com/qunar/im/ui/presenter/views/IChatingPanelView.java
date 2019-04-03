package com.qunar.im.ui.presenter.views;

import android.content.Context;

/**
 * Created by saber on 15-7-16.
 */
public interface IChatingPanelView {

    String getJid();
    String getRJid();
    Context getContext();
    void setTop(boolean isTop);
    boolean getTop();
    void setReMind(boolean isReMind);
    boolean getReMind();
    boolean getDnd();
    void setDnd(boolean isDnd);
}
