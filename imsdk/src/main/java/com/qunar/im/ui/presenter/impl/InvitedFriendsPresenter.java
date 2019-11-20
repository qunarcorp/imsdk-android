package com.qunar.im.ui.presenter.impl;

import android.content.Context;
import android.text.TextUtils;

import com.qunar.im.base.jsonbean.DepartmentResult;
import com.qunar.im.base.module.DepartmentItem;
import com.qunar.im.base.module.Nick;
import com.qunar.im.common.CurrentPreference;
import com.qunar.im.ui.presenter.IInvitedFriendsPresenter;
import com.qunar.im.ui.presenter.views.IInvitedFriendsView;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.view.multilLevelTreeView.Node;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.QtalkStringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by xinbo.wang on 2015/3/5.
 */
//邀请好友入群实现
public class InvitedFriendsPresenter implements IInvitedFriendsPresenter ,IMNotificaitonCenter.NotificationCenterDelegate {

    IInvitedFriendsView invitedFriendsView;
    Context context;
    //核心连接管理类
    private ConnectionUtil connectionUtil;


    public InvitedFriendsPresenter(Context context) {
        this.context = context;
        //初始化核心连接管理类
        connectionUtil = ConnectionUtil.getInstance();
        connectionUtil.addEvent(this, QtalkEvent.Muc_Invite_User_V2);
    }

    @Override
    public void setInvitedFriendsView(IInvitedFriendsView view) {
        invitedFriendsView = view;
    }

    @Override
    public void release() {
        connectionUtil.removeEvent(this, QtalkEvent.Muc_Invite_User_V2);
    }

    /**
     * 加载所有联系人,并设置数据
     */
    @Override
    public void loadAllContacts() {
        List<Nick> list = connectionUtil.SelectAllContacts();
        invitedFriendsView.setAllContacts(list);
    }

    @Override
    public void invited() {
        //获取选中的成员
        List<Node> selectedItem = invitedFriendsView.getSelectedFriends();
        //邀请人员
        List<String> selectList = new ArrayList<>();
        for (int i = 0; i <selectedItem.size() ; i++) {
            selectList.add(selectedItem.get(i).getKey());
        }
        //邀请群成员
        connectionUtil.inviteMessageV2(invitedFriendsView.getRoomId(),selectList);
    }

    @Override
    public void loadTargetContacts() {
        int id = 0;
        Node rootNode = new Node();
        rootNode.setRoot(true);
        rootNode.setName("选择联系人");
        rootNode.setExpand(true);
        rootNode.setId(++id);
        rootNode.setpId(-1);
        Map<Integer, List<Node>> map = new HashMap<>();
        map.put(-1, new LinkedList<Node>());
        map.get(-1).add(rootNode);
        map.put(rootNode.getId(), new LinkedList<Node>());
        Node recentNode = new Node();
        recentNode.setRoot(true);
        recentNode.setName("最近联系人");
        recentNode.setExpand(false);
        recentNode.setId(++id);
        recentNode.setpId(rootNode.getpId());
        map.get(rootNode.getId()).add(recentNode);
        map.put(recentNode.getId(), new LinkedList<Node>());
        Node myFriends = new Node();
        myFriends.setRoot(true);
        myFriends.setName("我的好友");
        myFriends.setExpand(false);
        myFriends.setId(++id);
        myFriends.setpId(rootNode.getpId());
        map.get(rootNode.getId()).add(myFriends);
        map.put(myFriends.getId(), new LinkedList<Node>());
        if (!CommonConfig.isQtalk && CurrentPreference.getInstance().isMerchants()) {
            List<DepartmentItem> departmentItems = null;//dataModel.getOrganizationOfFriends();

            staticId = 0;
            String qchatorg = DataUtils.getInstance(CommonConfig.globalContext).getPreferences(CurrentPreference.getInstance().getUserid()
                    + QtalkNavicationService.getInstance().getXmppdomain()
                    + Constants.Preferences.qchat_org, "");
            if(!TextUtils.isEmpty(qchatorg)){
                try {
                    DepartmentResult departmentResult = JsonUtils.getGson().fromJson(qchatorg, DepartmentResult.class);
                    departmentItems = new ArrayList<DepartmentItem>();
                    parseDeptNode(departmentResult, -1, departmentItems, null);
                    //load data
                }catch (Exception e){
                    departmentItems = new ArrayList<DepartmentItem>();
                }
            }else{
                departmentItems = new ArrayList<DepartmentItem>();
            }

            Node orgList = new Node();
            orgList.setRoot(true);
            orgList.setName("组织架构");
            orgList.setExpand(false);
            orgList.setId(++id);
            orgList.setpId(rootNode.getpId());
            map.get(rootNode.getId()).add(orgList);
            map.put(orgList.getId(), new LinkedList<Node>());
            for (DepartmentItem item : departmentItems) {
                if (item.parentId == -1) {
                    continue;
                }
                if (item.parentId == 1) {
                    item.parentId = orgList.getId();
                } else {
                    item.parentId += orgList.getId();
                }
                Node node = new Node();
                node.setpId(item.parentId);
                node.setId(item.id + orgList.getId());
                node.setName(item.fullName);
                node.setKey(item.userId);
                node.setRoot(TextUtils.isEmpty(item.userId));
                if (!map.containsKey(item.parentId)) map.put(item.parentId, new LinkedList<Node>());
                map.get(item.parentId).add(node);
                id = node.getId();
            }
        }
        invitedFriendsView.initTreeView(map);
    }

    @Override
    public void didReceivedNotification(String key, Object... args) {
        switch (key){
            case QtalkEvent.Muc_Invite_User_V2:
                invitedFriendsView.setResult(true);
                break;
        }
    }
    private volatile int staticId = 0;

    void parseDeptNode(DepartmentResult deptItem, int pid, List<DepartmentItem> mDeptDataList, StringBuilder parentName) {
        if (deptItem != null) {
            staticId++;
            DepartmentItem groupItem = new DepartmentItem();
            groupItem.id = staticId;
            groupItem.fullName = deptItem.D;
            groupItem.parentId = pid;
            groupItem.userId = null;
            groupItem.status = 0;
            mDeptDataList.add(groupItem);

            if (parentName != null) {
                parentName.append("/").append(deptItem.D);
            } else {
                parentName = new StringBuilder("");
            }

            if (deptItem.UL != null) {
                for (DepartmentResult.PersonResult userItem : deptItem.UL) {
                    final DepartmentItem contactItem = new DepartmentItem();
                    staticId++;

                    contactItem.id = staticId;
                    contactItem.fullName = userItem.N;
                    contactItem.userId = QtalkStringUtils.userId2Jid(userItem.U);
                    contactItem.parentId = groupItem.id;
                    contactItem.fuzzyCol = userItem.U+"|"+userItem.N+"|"+userItem.W;
                    contactItem.deptName = parentName.toString();
                    //qchat的名称显示规则， 优先显示fullname，没有fullname显示webname，webname也没有则显示userid
                    if (!TextUtils.isEmpty(userItem.N)) {
                        contactItem.fullName = userItem.N;
                    } else if (!TextUtils.isEmpty(userItem.W)) {
                        contactItem.fullName = userItem.W;
                    } else {
                        contactItem.fullName = userItem.U;
                    }
                    mDeptDataList.add(contactItem);
                }
            }
            if (deptItem.SD != null) {
                for (DepartmentResult item : deptItem.SD) {
                    StringBuilder stringBuilder = new StringBuilder(parentName.toString());
                    parseDeptNode(item, groupItem.id, mDeptDataList, stringBuilder);
                }
            }
        }
    }
}
