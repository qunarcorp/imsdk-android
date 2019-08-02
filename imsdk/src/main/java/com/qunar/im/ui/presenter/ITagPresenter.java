package com.qunar.im.ui.presenter;

import com.qunar.im.ui.presenter.views.ITagView;

/**
 * Created by xinbo.wang on 2016/5/27.
 */
public interface ITagPresenter {
    void setTagView(ITagView view);
    void addTag();
    void deleteTag();
    void showTag();
}
