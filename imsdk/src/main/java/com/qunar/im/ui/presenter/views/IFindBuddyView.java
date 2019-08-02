package com.qunar.im.ui.presenter.views;

import com.qunar.im.base.jsonbean.SearchUserResult;

import java.util.List;

/**
 * Created by saber on 15-12-17.
 */
public interface IFindBuddyView {
    String getKeyword();
    void setSearchResults(List<SearchUserResult.SearchUserInfo> results);
}
