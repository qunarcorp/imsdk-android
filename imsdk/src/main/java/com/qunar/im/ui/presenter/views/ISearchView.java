package com.qunar.im.ui.presenter.views;

import java.util.List;

/**
 * Created by zhaokai on 15-9-16.
 */
public interface ISearchView<T> {
        public String getTerm();
        public void setSearchResult(List<T> results);
}
