package com.qunar.im.ui.presenter.views;

import com.qunar.im.base.module.Nick;

/**
 * Created by xinbo.wang on 2015/5/27.
 */
public abstract class IChatMemberAdapter implements IChatRoomInfoView {
    public void setChatroomInfo(Nick room)
    {

    }
    public void setMemberCount(int count){

    }
    public void setExitResult(boolean re){

    }

    public void setJoinResult(boolean re,String mesg)
    {

    }

    public void setUpdateResult(boolean re, String msg) {

    }

    @Override
    public String getRealJid() {
        return null;
    }

    public Nick getChatroomInfo() {
        return null;
    }
}
