package com.qunar.im.ui.presenter.impl;

import android.text.TextUtils;

import com.qunar.im.base.jsonbean.DepartmentResult;
import com.qunar.im.base.module.DepartmentItem;
import com.qunar.im.common.CurrentPreference;
import com.qunar.im.ui.presenter.IFriendsManagePresenter;
import com.qunar.im.ui.presenter.views.IFriendsManageView;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.InternDatas;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.view.multilLevelTreeView.Node;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.utils.QtalkStringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaokai on 15-7-29.
 */
public class QChatGetOrganizationPresenter implements IFriendsManagePresenter {
    private static final String TAG = QChatGetOrganizationPresenter.class.getSimpleName();
    IFriendsManageView friendsManageView;
    private volatile int staticId = 0;
    boolean isSaving = false;
    boolean isLoadingDept = false;

    Map<Integer, List<Node>> expandList = new HashMap<>();
    Map<String, DepartmentItem> jid2Nick = new HashMap<>();

    public QChatGetOrganizationPresenter() {
    }

    @Override
    public void setFriendsView(IFriendsManageView view) {
        friendsManageView = view;
    }

    @Override
    public void updateContacts() {
        if (isLoadingDept)
            return;
        isLoadingDept = true;
        List<DepartmentItem> mDeptDataList;
        String qchatorg = DataUtils.getInstance(CommonConfig.globalContext).getPreferences(CurrentPreference.getInstance().getUserid()
                + QtalkNavicationService.getInstance().getXmppdomain()
                + Constants.Preferences.qchat_org, "");
        if(!TextUtils.isEmpty(qchatorg)){
            try {
//                mDeptDataList = friendsDataModel.getOrganizationOfFriends();
                DepartmentResult departmentResult = JsonUtils.getGson().fromJson(qchatorg, DepartmentResult.class);
               mDeptDataList = new ArrayList<DepartmentItem>();
                expandList.clear();
                jid2Nick.clear();
                parseDeptNode(departmentResult, -1, mDeptDataList, null);
                //load data
            }catch (Exception e){
                mDeptDataList = new ArrayList<DepartmentItem>();
            }
        }else{
            mDeptDataList = new ArrayList<DepartmentItem>();
        }

        setExpandList(mDeptDataList);
        isLoadingDept = false;
    }

    private void setExpandList(List<DepartmentItem> mDeptDataList) {
        if (mDeptDataList.size() > 0) {
            expandList.clear();
            jid2Nick.clear();
            LogUtil.d("debug", "load from localdb");
            for (int i = 0; i < mDeptDataList.size(); i++) {
                DepartmentItem item = mDeptDataList.get(i);
                putExpandList(item);
            }
            updateView();
        }
    }

    private void updateView() {
        if(friendsManageView!=null) {
            friendsManageView.resetListView();
            friendsManageView.setFrineds(expandList);
        }
        InternDatas.addData(Constants.SYS.CONTACTS_MAP, jid2Nick);
        Map<String, DepartmentItem> fullNameMap = new HashMap<String, DepartmentItem>();
        InternDatas.addData(Constants.SYS.NICK_UID_MAP, fullNameMap);
    }

    @Override
    public void forceUpdateContacts() {
        LogUtil.d("debug", "forceUpdateContacts entrance");
        if (TextUtils.isEmpty(CurrentPreference.getInstance().getVerifyKey())) {
            if(friendsManageView!=null)
                friendsManageView.resetListView();
            return;
        }
        updateFriends();
    }

    private void updateFriends() {
        isLoadingDept = true;
        staticId = 0;
        //qchat组织架构缓存
        String qchatorg = DataUtils.getInstance(CommonConfig.globalContext).getPreferences(CurrentPreference.getInstance().getUserid()
                + QtalkNavicationService.getInstance().getXmppdomain()
                + Constants.Preferences.qchat_org, "");
        if(!TextUtils.isEmpty(qchatorg)){
            try {
                DepartmentResult departmentResult = JsonUtils.getGson().fromJson(qchatorg, DepartmentResult.class);
                if(departmentResult != null){
                    if(friendsManageView!=null){
                        friendsManageView.resetListView();
                    }
                    updateView(departmentResult);
                    isLoadingDept = false;
                    return;
                }
            }catch (Exception e){

            }
        }

        Protocol.getQchatDeptInfo(new ProtocolCallback.UnitCallback<DepartmentResult>() {
            @Override
            public void onCompleted(DepartmentResult departmentResult) {
                if(friendsManageView!=null)
                    friendsManageView.resetListView();
                if (departmentResult != null) {
                    updateView(departmentResult);
                }
                isLoadingDept = false;
            }

            @Override
            public void onFailure(String errMsg) {
                isLoadingDept = false;
                if(friendsManageView!=null)
                    friendsManageView.resetListView();
            }
        });
    }

    private void updateView(DepartmentResult departmentResult) {
        //add root node
        LogUtil.d("debug", "node parse");

        List<DepartmentItem> mDeptDataList = new ArrayList<DepartmentItem>();
        expandList.clear();
        jid2Nick.clear();
        parseDeptNode(departmentResult, -1, mDeptDataList, null);
        //load data

        if (mDeptDataList.size() == 0) {
            return;
        }
        updateView();
        if (!isSaving) {
            isSaving = true;
            saveFriendsList2LocalDB(mDeptDataList);
        }
    }

    void saveFriendsList2LocalDB(List<DepartmentItem> friends) {
        LogUtil.d("debug", "local complete");
        isSaving = false;
    }

    private void putExpandList(DepartmentItem item) {
        try {
            int key = item.parentId;
            if (!expandList.containsKey(key)) {
                expandList.put(key, new ArrayList<Node>());
            }
            Node node = new Node();
            node.setId(item.id);
            node.setpId(key);
            node.setName(item.fullName);
            node.setKey(item.userId);
            if (TextUtils.isEmpty(node.getKey())) {
                node.setRoot(true);
            } else {
                jid2Nick.put(item.userId, item);
            }
            expandList.get(key).add(node);
        } catch (Exception e) {
            LogUtil.e(TAG,"error",e);
        }
    }

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
            putExpandList(groupItem);

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
                    putExpandList(contactItem);
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
