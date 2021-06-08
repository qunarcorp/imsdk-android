package com.qunar.im.ui.presenter.impl;

import com.qunar.im.common.CurrentPreference;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.module.GroupMember;
import com.qunar.im.ui.presenter.IChatMemberPresenter;
import com.qunar.im.ui.presenter.views.IChatmemberManageView;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.utils.QtalkStringUtils;

import java.util.List;

/**
 * Created by xinbo.wang on 2015/3/24.
 * <p>
 * 群成员操作
 */
public class ChatMemberPresenter implements IChatMemberPresenter {
    private static final String TAG = ChatMemberPresenter.class.getSimpleName();
    private ConnectionUtil connectionUtil;
    private IChatmemberManageView chatmemberManageView;

    @Override
    public void kickUser() {
        connectionUtil.delGroupMember(chatmemberManageView.getRoomId(), chatmemberManageView.getSelectNick2Jid());
    }

    @Override
    public void revokeUser() {

    }

    @Override
    public void grantVoice() {

    }

    @Override
    public void loadMembers() {
        List<GroupMember> memberList =  connectionUtil.SelectGroupMemberByGroupId(chatmemberManageView.getRoomId());
        JsonUtils.getGson().toJson(memberList);
        int i = 0;
        int myPower = GroupMember.NONE;
        for (; i < memberList.size(); i++) {
            GroupMember member = memberList.get(i);
            if (member.getMemberId().equals(
                    QtalkStringUtils.userId2Jid(CurrentPreference.getInstance().getPreferenceUserId()))) {
                myPower = Integer.parseInt(member.getAffiliation());
            }
            if (Integer.parseInt(member.getAffiliation()) > GroupMember.ADMIN) {
                break;
            }
        }
        GroupMember admin = new GroupMember();
        admin.setAffiliation(String.valueOf(-1));
        admin.setName("管理员");
        memberList.add(0, admin);
        GroupMember member = new GroupMember();
        member.setAffiliation(String.valueOf(-2));
        member.setName("群成员");
        if (i == memberList.size() - 1) {
            memberList.add(member);
        } else {
            memberList.add(i + 1, member);
        }
        chatmemberManageView.setMembers(memberList, myPower);
    }

    @Override
    public void setChatmemberManageView(IChatmemberManageView view) {
        chatmemberManageView = view;
        connectionUtil = ConnectionUtil.getInstance();
    }
}
