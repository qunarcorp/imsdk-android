package com.qunar.im.ui.presenter.model.impl;

import com.qunar.im.base.module.PublishPlatformNews;
import com.qunar.im.ui.presenter.model.IPublishPlatformNewsDataModel;
import com.qunar.im.core.manager.IMDatabaseManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaokai on 15-9-11.
 */
public class PublishPlatformNewsDataModel implements IPublishPlatformNewsDataModel {
    @Override
    public List<PublishPlatformNews> getMsgWithLimit(String id, int count, int offset) {
        return IMDatabaseManager.getInstance().selectPublishPlatformNews(id,count,offset);
    }

    @Override
    public PublishPlatformNews getLatestMsg(String id) {
        return IMDatabaseManager.getInstance().selectLastPublishPlatformNewsById(id);
    }

    @Override
    public boolean selectById(PublishPlatformNews t) {
        return false;
    }

    @Override
    public boolean delMsgByPlatformId(String id) {
        return IMDatabaseManager.getInstance().deletPlatformNewsById(id);
    }

    @Override
    public boolean insertOrUpdateNews(PublishPlatformNews news) {
        if(news == null) return false;
        List<PublishPlatformNews> lists = new ArrayList<>();
        lists.add(news);
        return IMDatabaseManager.getInstance().insertPublishPlatformNews(lists);
    }

    @Override
    public boolean insertOrUpdateListNews(List<PublishPlatformNews> list) {
        return IMDatabaseManager.getInstance().insertPublishPlatformNews(list);
    }

    @Override
    public PublishPlatformNews getLatestMsg() {
        return IMDatabaseManager.getInstance().selectLastPublishPlatformNews();
    }
}
