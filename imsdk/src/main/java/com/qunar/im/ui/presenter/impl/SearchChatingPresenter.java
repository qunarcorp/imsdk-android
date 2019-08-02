package com.qunar.im.ui.presenter.impl;

import android.text.TextUtils;

import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.ui.presenter.ISearchChatingPresenter;
import com.qunar.im.ui.presenter.views.ISearchChatingView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saber on 15-7-7.
 */
public class SearchChatingPresenter implements ISearchChatingPresenter {
    ISearchChatingView searchChatingView;
    @Override
    public void setSearchChatingView(ISearchChatingView view) {
        searchChatingView = view;
    }

    @Override
    public void doSearchChating() {
        String term = searchChatingView.getSearchTerm();
        String from = searchChatingView.getSearchFrom();
        List<IMMessage> list = new ArrayList<IMMessage>();
        if(!TextUtils.isEmpty(term))
        {
            list = ConnectionUtil.getInstance().searchMsg(from, term, 50);
        }
        searchChatingView.setSearchResult(list);
    }
}
