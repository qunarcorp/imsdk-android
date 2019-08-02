package com.qunar.im.ui.presenter.model;

import com.qunar.im.base.module.PublishPlatform;

import java.util.List;

/**
 * Created by zhaokai on 15-9-11.
 */
public interface IPublishPlatformDataModel {
    /**
     * 返回空表示数据库中无此项
     **/
    PublishPlatform selectById(String id);

    boolean deleteById(String id);

    boolean insertOrUpdatePublishPlatform(PublishPlatform publishPlatform);

    boolean insertOrUpdatePublishPlatforms(List<PublishPlatform> publishPlatforms);

    /**
     * @param limit 选择前几个公众号,0 表示选取所有
     */
    List<PublishPlatform> selectAllPublishPlatforms(int limit);

    List<PublishPlatform> searchPublishPlatform(String term, int limit);
    List<PublishPlatform> searchPublishPlatform(String term, int limit,int offset);
}
