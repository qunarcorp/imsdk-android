package com.qunar.im.ui.presenter.model;

import com.qunar.im.base.module.PublishPlatformNews;

import java.util.List;

/**
 * Created by zhaokai on 15-9-11.
 */
public interface IPublishPlatformNewsDataModel {
    List<PublishPlatformNews> getMsgWithLimit(String id, int count, int offset);
    PublishPlatformNews getLatestMsg(String id);
    boolean selectById(PublishPlatformNews t);
    boolean delMsgByPlatformId(String id);
    boolean insertOrUpdateNews(PublishPlatformNews news);
    boolean insertOrUpdateListNews(List<PublishPlatformNews> list);
    PublishPlatformNews getLatestMsg();
}
