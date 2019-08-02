package com.qunar.im.ui.presenter.views;

import android.content.Context;

import com.qunar.im.base.module.IMGroup;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.module.PublishPlatform;

import java.util.List;

/**
 * Created by xinbo.wang on 2015/2/14.
 */
public interface ISearchFriendView {
    String getTerm();

    //    void setSearchResult(List<DepartmentItem> results);
    void setSearchResult(List<Nick> results);

    //    void setChatRoomResult(List<ChatRoom> results);
    void setChatRoomResult(List<IMGroup> results);

    void setPublishPlatformResult(List<PublishPlatform> results);

    int getMaxCount();

    Context getContext();
}
