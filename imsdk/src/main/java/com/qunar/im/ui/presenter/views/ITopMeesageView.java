package com.qunar.im.ui.presenter.views;

/**
 * Created by saber on 15-8-17.
 */
public abstract class ITopMeesageView implements IChatingPanelView{
    @Override
    public void setTop(boolean isTop){}

    @Override
    public void setReMind(boolean isReMind) {}


    @Override
    public boolean getDnd(){return false;}
    @Override
    public void setDnd(boolean isDnd){}
}
