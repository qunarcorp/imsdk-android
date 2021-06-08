package com.qunar.im.ui.presenter.impl;

import android.text.TextUtils;

import com.qunar.im.base.module.DepartmentItem;
import com.qunar.im.ui.presenter.IFriendsManagePresenter;
import com.qunar.im.ui.presenter.views.IFriendsManageView;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.InternDatas;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.common.CommonConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xinbo.wang on 2015/2/10.
 *
 * 组织架构/好友逻辑
 */
public class FriendsManagePresenter implements IFriendsManagePresenter {
    private static final String TAG=FriendsManagePresenter.class.getSimpleName();
    IFriendsManageView friendsManageView;
    Map<String,DepartmentItem> jid2Nick = new HashMap<String,DepartmentItem>();

    public FriendsManagePresenter()
    {
        
    }

    @Override
    public void setFriendsView(IFriendsManageView view) {
        friendsManageView = view;
    }

    @Override
    public void updateContacts() {
        List<DepartmentItem> mDeptDataList = new ArrayList<DepartmentItem>();

        setExpandList(mDeptDataList);
    }

    private void setExpandList(List<DepartmentItem> mDeptDataList)
    {
        if (mDeptDataList.size() > 0) {
            //expandList.clear();
            jid2Nick.clear();
            LogUtil.d("debug","load from localdb");
            for (int i = 0; i < mDeptDataList.size(); i++) {
                DepartmentItem item = mDeptDataList.get(i);
                putExpandList(item);
            }
            updateView();
        }
    }

    private void updateView()
    {
        InternDatas.addData(Constants.SYS.CONTACTS_MAP, jid2Nick);
        Map<String, DepartmentItem> fullNameMap = new HashMap<String, DepartmentItem>();
        InternDatas.addData(Constants.SYS.NICK_UID_MAP, fullNameMap);
    }


    @Override
    public void forceUpdateContacts() {
        LogUtil.d("debug", "forceUpdateContacts entrance");
        if(TextUtils.isEmpty(CommonConfig.verifyKey))
        {
            if(friendsManageView!=null)
                friendsManageView.resetListView();
            return;
        }
        updateFriends();
    }

    private void updateFriends()
    {
//        final int version = 0;
//        Protocol.getIncrementUsers(version,new ProtocolCallback.UnitCallback<IncrementUsersResult>() {
//            @Override
//            public void onCompleted(IncrementUsersResult result) {
//                if (result!=null&&result.ret&&result.data != null) {
//                }
//                updateContacts();
//            }
//
//            @Override
//            public void onFailure(String errMsg) {
//                updateContacts();
//            }
//        });
    }

    private void putExpandList(DepartmentItem item) {
        try {
            if (!TextUtils.isEmpty(item.userId)) {
                jid2Nick.put(item.userId, item);
            }
        }
        catch (Exception e)
        {
            LogUtil.e(TAG,"error",e);
        }
    }
}