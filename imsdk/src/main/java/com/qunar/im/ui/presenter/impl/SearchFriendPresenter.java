package com.qunar.im.ui.presenter.impl;

import android.text.TextUtils;

import com.qunar.im.base.module.DepartmentItem;
import com.qunar.im.base.module.IMGroup;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.module.PublishPlatform;
import com.qunar.im.ui.presenter.ISearchFriendPresenter;
import com.qunar.im.ui.presenter.model.IPublishPlatformDataModel;
import com.qunar.im.ui.presenter.model.impl.PublishPlatformDataModel;
import com.qunar.im.ui.presenter.views.ISearchFriendView;
import com.qunar.im.utils.ConnectionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinbo.wang on 2015/2/14.
 */
//查找用户逻辑
public class SearchFriendPresenter implements ISearchFriendPresenter {
    ISearchFriendView searchFriendView;
    IPublishPlatformDataModel publishPlatformDataModel;

    //核心连接管理类
    public ConnectionUtil connectionUtil;

    public SearchFriendPresenter() {
        publishPlatformDataModel = new PublishPlatformDataModel();
    }

    @Override
    public void setSearchFriendView(ISearchFriendView view) {
        searchFriendView = view;
        connectionUtil = ConnectionUtil.getInstance();
    }

    @Override
    public void doSearchContacts() {
        int requestCount = searchFriendView.getMaxCount();
        List<DepartmentItem> results = new ArrayList<DepartmentItem>();
        List<Nick> nickList = new ArrayList<>();
        String term = searchFriendView.getTerm();

        //在这里查询所有的联系人,原版中从两个地方进行了查询,新版直接从所有联系人中查询
        if (!TextUtils.isEmpty(term)) {
            nickList = connectionUtil.SelectContactsByLike(term, requestCount);
        }
        searchFriendView.setSearchResult(nickList);
    }

    @Override
    public void doSearchGroups() {
        int requestCount = searchFriendView.getMaxCount();
//        List<ChatRoom> chatRooms = new ArrayList<ChatRoom>();
        List<IMGroup> chatRooms = new ArrayList<>();
        String term = searchFriendView.getTerm();
        if (!TextUtils.isEmpty(term)) {
            chatRooms = connectionUtil.SelectIMGroupByLike(term, requestCount);
//            chatRooms = chatRoomDataModel.fuzzySearch(term, requestCount);
        }
        searchFriendView.setChatRoomResult(chatRooms);
    }

    @Override
    public void doSearchPublishPlatform() {
        int requestCount = searchFriendView.getMaxCount();
        List<PublishPlatform> publishPlatforms = new ArrayList<PublishPlatform>();
        String term = searchFriendView.getTerm();
        if (!TextUtils.isEmpty(term)) {
            publishPlatforms = publishPlatformDataModel.searchPublishPlatform(term, requestCount);
        }
        searchFriendView.setPublishPlatformResult(publishPlatforms);
    }

    @Override
    public void doSearchFriend() {
    }
}