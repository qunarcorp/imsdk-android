package com.qunar.im.ui.presenter.views;

import com.qunar.im.base.module.IMMessage;

import java.util.List;

/**
 * Created by saber on 15-7-7.
 */
public interface ISearchChatingView {
    String getSearchTerm();
    void setSearchResult(List<IMMessage> results);
    String getSearchFrom();
}
