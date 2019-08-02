package com.qunar.im.ui.presenter.impl;

import com.qunar.im.base.module.PublishPlatform;
import com.qunar.im.ui.presenter.views.ISearchPresenter;
import com.qunar.im.ui.presenter.views.ISearchView;
import com.qunar.im.core.manager.IMDatabaseManager;

import java.util.List;

/**
 * Created by zhaokai on 15-9-16.
 */
public class SearchPublishPlatformPresenter implements ISearchPresenter {

    private ISearchView view;

    @Override
    public void doSearch() {
        if (view != null) {
            String term = view.getTerm();
            if (term != null) {
                List<PublishPlatform> publishPlatforms = IMDatabaseManager.getInstance().searchPublishPlatform(term,-1);
                view.setSearchResult(publishPlatforms);
            }
        }
    }

    @Override
    public void setSearchView(ISearchView searchView) {
        this.view = searchView;
    }
}
