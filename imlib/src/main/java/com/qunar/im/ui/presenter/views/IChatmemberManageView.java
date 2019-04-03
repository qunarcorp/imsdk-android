package com.qunar.im.ui.presenter.views;

import android.content.Context;

import com.qunar.im.base.module.GroupMember;

import java.util.List;
import java.util.Map;

/**
 * Created by saber on 16-3-3.
 */
public interface IChatmemberManageView {
    /**
     * 返回选中群成员的nick->jid,map的key是nick,value是jid
     * @return
     */
    Map<String,String> getSelectNick2Jid();
    String getRoomId();
    void setMembers(List<GroupMember> members, int myPower);
    Context getContext();
}
