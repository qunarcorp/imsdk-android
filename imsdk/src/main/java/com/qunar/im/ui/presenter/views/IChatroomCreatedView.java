package com.qunar.im.ui.presenter.views;

/**
 * Created by xinbo.wang on 2015/4/10.
 */
public interface IChatroomCreatedView {
    public String getSubject();
    public String getChatrooName();
    public boolean isPersist();
    public void setResult(boolean isSuccess, String roomId);
}
