package com.qunar.im.ui.presenter.views;

import android.content.Context;

import com.qunar.im.base.module.GroupMember;
import com.qunar.im.base.module.Nick;

import java.util.List;

/**
 * Created by xinbo.wang on 2015/2/11.
 */
public interface IChatRoomInfoView {
    void setMemberList(List<GroupMember> members, int myPowerLevel,boolean enforce);
    String getRoomId();
    String getRealJid();//二人会话真实id
    String getChatType();
    void closeActivity();
    void setChatroomInfo(Nick nick);
    void setMemberCount(int count);
    void setExitResult(boolean re);
    void setJoinResult(boolean re, String mesg);
    void setUpdateResult(boolean re, String msg);
    Nick getChatroomInfo();
    Context getContext();
}
