package com.qunar.im.ui.presenter.impl;

import com.qunar.im.base.jsonbean.SearchUserResult;
import com.qunar.im.ui.presenter.IFindBuddyPresenter;
import com.qunar.im.ui.presenter.views.IFindBuddyView;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.util.ListUtil;

/**
 * Created by saber on 15-12-17.
 */
public class FindBuddyPresenter implements IFindBuddyPresenter {
    IFindBuddyView buddyView;
    public FindBuddyPresenter()
    {
    }

    @Override
    public void setIFindBuddyView(IFindBuddyView view) {
        buddyView = view;
    }

    @Override
    public void doSearch() {
        String keyWord = buddyView.getKeyword();
        Protocol.crossDomainSearchUser(keyWord, new ProtocolCallback.UnitCallback<SearchUserResult>() {
            @Override
            public void onCompleted(SearchUserResult searchUserResult) {
                if(searchUserResult!=null&&!ListUtil.isEmpty(searchUserResult.data))
                {
                    buddyView.setSearchResults(searchUserResult.data);
                }
                else {
                    buddyView.setSearchResults(null);
                }
            }

            @Override
            public void onFailure(String errMsg) {
                buddyView.setSearchResults(null);
            }
        });
    }
}
