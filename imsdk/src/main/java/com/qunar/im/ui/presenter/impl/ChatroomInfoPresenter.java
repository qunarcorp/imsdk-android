package com.qunar.im.ui.presenter.impl;

import android.text.TextUtils;

import com.qunar.im.base.jsonbean.GetMucVCardData;
import com.qunar.im.base.jsonbean.GetMucVCardResult;
import com.qunar.im.base.jsonbean.SetMucVCardResult;
import com.qunar.im.base.module.ChatRoom;
import com.qunar.im.base.module.ChatRoomMember;
import com.qunar.im.base.module.GroupMember;
import com.qunar.im.base.module.Nick;
import com.qunar.im.common.CurrentPreference;
import com.qunar.im.ui.presenter.IChatroomInfoPresenter;
import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.ui.presenter.views.IChatRoomInfoView;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.protocol.VCardAPI;
import com.qunar.im.base.structs.SetMucVCardData;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.base.util.InternDatas;
import com.qunar.im.base.util.ListUtil;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.HttpUtil;
import com.qunar.im.utils.QtalkStringUtils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

import static com.qunar.im.protobuf.Event.QtalkEvent.Muc_Invite_User_V2;

/**
 * Created by xinbo.wang on 2015/2/11.
 * <p>
 * 群信息逻辑
 */
public class ChatroomInfoPresenter implements IChatroomInfoPresenter, IMNotificaitonCenter.NotificationCenterDelegate {
    private static final String TAG = ChatroomInfoPresenter.class.getSimpleName();
    IChatRoomInfoView chatRoomInfoView;
    //核心连接管理类
    private ConnectionUtil connectionUtil;

    public ChatroomInfoPresenter() {
    }

    @Override
    public void setView(IChatRoomInfoView view) {
        chatRoomInfoView = view;
        connectionUtil = ConnectionUtil.getInstance();
        addEvent();
    }

    private void addEvent() {
        connectionUtil.addEvent(this, QtalkEvent.Group_Member_Update);
        connectionUtil.addEvent(this, QtalkEvent.Invite_User);
        connectionUtil.addEvent(this, QtalkEvent.Del_Muc_Register);
        connectionUtil.addEvent(this, Muc_Invite_User_V2);
    }

    public void removeEvent() {
        connectionUtil.removeEvent(this, QtalkEvent.Group_Member_Update);
        connectionUtil.removeEvent(this, QtalkEvent.Invite_User);
        connectionUtil.removeEvent(this, QtalkEvent.Del_Muc_Register);
        connectionUtil.removeEvent(this, Muc_Invite_User_V2);
    }

    @Override
    public void showMembers(boolean onlyFromDB) {
        //获取群id
        final String key = chatRoomInfoView.getRoomId();
        if (TextUtils.isEmpty(key)) {
            return;
        }
        //根据群id查出群成员
        List<GroupMember> groupMemberList = connectionUtil.SelectGroupMemberByGroupId(key);
        //设置默认权限
        int myPowerLevel = GroupMember.NONE;
        //便利循环一遍找到自身,并把自身的权限赋值
        for (GroupMember member : groupMemberList) {
            if (QtalkStringUtils.userId2Jid(CurrentPreference.getInstance().getPreferenceUserId()).equals(
                    member.getMemberId())) {
//                if (member.getAffiliation().equals("owner")) {
                myPowerLevel = Integer.parseInt(member.getAffiliation());
//                }
                break;
            }
        }
        if (groupMemberList.size() > 0) {
            //设置上面显示群成员及头像 群组情况下, 不去强制更新头像
            chatRoomInfoView.setMemberList(groupMemberList, myPowerLevel, false);
            //设置foot中人数
            chatRoomInfoView.setMemberCount(groupMemberList.size());
        }
        if (onlyFromDB) {
            return;
        }

        connectionUtil.getMembersAfterJoin(key);

    }

    @Override
    public void showSingler() {
        List<GroupMember> members = new ArrayList<GroupMember>();
        GroupMember member = new GroupMember();
        String str ="";
        if((ConversitionType.MSG_TYPE_CONSULT+"").equals(chatRoomInfoView.getChatType())){
            str = chatRoomInfoView.getRoomId();
        }else{
            str=chatRoomInfoView.getRealJid();
        }
        member.setMemberId(str);
        members.add(member);
        //单人的 所以要强制获取一下
        chatRoomInfoView.setMemberList(members, ChatRoomMember.NONE, true);
    }


    /**
     * 销毁群组
     */
    @Override
    public void destroy() {
        final String key = chatRoomInfoView.getRoomId();
        connectionUtil.destroyGroup(key);
    }

    /**
     * 退出群组
     */
    @Override
    public void leave() {
        final String key = chatRoomInfoView.getRoomId();
        connectionUtil.leaveGroup(key);
    }

    @Override
    public void showInfo() {
        //获取会话key
        final String key = chatRoomInfoView.getRoomId();
        connectionUtil.getMucCard(key, new IMLogicManager.NickCallBack() {
            @Override
            public void onNickCallBack(Nick nick) {
                chatRoomInfoView.setChatroomInfo(nick);
            }
        }, false, false);


        final ChatRoom cr = new ChatRoom();
//        从数据库拿
//        connectionUtil


        cr.setJid(key);

        List<GetMucVCardData> datas = new ArrayList<GetMucVCardData>();
        GetMucVCardData data = new GetMucVCardData();
        data.mucs = new ArrayList<>();
        GetMucVCardData.MucInfo mucInfo = new GetMucVCardData.MucInfo();
        data.mucs.add(mucInfo);
        data.domain = QtalkStringUtils.parseDomain(key);
        mucInfo.muc_name = QtalkStringUtils.parseBareJid(key);
        mucInfo.version = String.valueOf(cr.getVersion());
        datas.add(data);
        VCardAPI.getMucVCard(datas, new ProtocolCallback.UnitCallback<GetMucVCardResult>() {
            @Override
            public void onCompleted(GetMucVCardResult getMucVCardResult) {
                if (getMucVCardResult != null && !ListUtil.isEmpty(getMucVCardResult.data)) {
                    GetMucVCardResult.ExtMucVCard extMucVCard = getMucVCardResult.data.get(0);
                    if (!ListUtil.isEmpty(extMucVCard.mucs)) {
                        GetMucVCardResult.MucVCard vCard = extMucVCard.mucs.get(0);
                        cr.setName(vCard.SN);
                        cr.setDescription(vCard.MD);
                        cr.setSubject(vCard.MT);
                        cr.setPicUrl(vCard.MP);
                        cr.setVersion(Integer.valueOf(vCard.VS));
                        InternDatas.saveName(cr.getJid(), cr.getName());

                        Nick nick = chatRoomInfoView.getChatroomInfo();
                        if(nick != null){
                            nick.setTopic(cr.getSubject());
                            nick.setName(cr.getName());
                            nick.setHeaderSrc(cr.getPicUrl());
                            chatRoomInfoView.setChatroomInfo(nick);
                        }
                    }
                }

            }

            @Override
            public void onFailure(String errMsg) {
                //拿失败了
//                chatRoomInfoView.setChatroomInfo(cr);
            }
        });

    }

    @Override
    public void joinChatRoom() {
        List<String> selectList = new ArrayList<>();
        selectList.add(IMLogicManager.getInstance().getMyself().fullname());
        connectionUtil.inviteMessageV2(chatRoomInfoView.getRoomId(),selectList);
    }

    @Override
    public void joinChatRoomWithPwd(String pwd) {

    }

    @Override
    public void cleanGroup() {
        chatRoomInfoView.setExitResult(true);
    }

    @Override
    public void clearHistoryMsg() {
        EventBus.getDefault().post(new EventBusEvent.CleanHistory(chatRoomInfoView.getRoomId()));
    }

    @Override
    public void updataMucInfo() {
        final Nick nick = chatRoomInfoView.getChatroomInfo();
        if (nick == null)
            return;
        // TODO: 2017/8/24 updataMucInfo
        SetMucVCardData setMucVCardData = new SetMucVCardData();


        setMucVCardData.nick = nick.getName();
        setMucVCardData.muc_name = nick.getGroupId();
        setMucVCardData.desc = nick.getIntroduce();
        setMucVCardData.title = nick.getTopic();
        setMucVCardData.pic = nick.getHeaderSrc();
        List<SetMucVCardData> groups = new ArrayList();
        groups.add(setMucVCardData);
        HttpUtil.setMucVCard(groups, new ProtocolCallback.UnitCallback<SetMucVCardResult>() {
            @Override
            public void onFailure(String errMsg) {
                chatRoomInfoView.setUpdateResult(false, "更改失败");
            }

            @Override
            public void onCompleted(SetMucVCardResult s) {
                if (s != null && s.data != null && s.data.size() > 0) {
//                    chatRoom.setVersion(Integer.parseInt(s.data.get(0).get("version")));
//                    updateRecentConversationName(chatRoom);
                    chatRoomInfoView.setUpdateResult(true, "更改成功");
                } else {

                    chatRoomInfoView.setUpdateResult(false, "更改失败");

                }
            }
        });

    }

    @Override
    public void close() {
        removeEvent();
    }



    @Override
    public void didReceivedNotification(String key, Object... args) {
        switch (key) {
            case QtalkEvent.Group_Member_Update:
                //如果有更新从新去拿一下数据,只从数据库结构拿
                showMembers(true);
                break;
            case QtalkEvent.Invite_User:
                showMembers(true);
                break;
            case QtalkEvent.Del_Muc_Register:
                showMembers(true);
                break;
            case QtalkEvent.Muc_Invite_User_V2:
                chatRoomInfoView.setJoinResult(true, "加入聊天室成功");
                break;

        }

    }
}
