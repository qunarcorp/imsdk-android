package com.qunar.im.ui.presenter.model.impl;

import com.qunar.im.base.module.PublishPlatform;
import com.qunar.im.ui.presenter.model.IPublishPlatformDataModel;
import com.qunar.im.core.manager.IMDatabaseManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saber on 15-9-14.
 */
public class PublishPlatformDataModel implements IPublishPlatformDataModel {

    @Override
    public PublishPlatform selectById(String id) {
        return IMDatabaseManager.getInstance().selectPublishPlatformById(id);
    }

    @Override
    public boolean deleteById(String id) {
        return IMDatabaseManager.getInstance().deletePublishPlatformById(id);
    }

    @Override
    public boolean insertOrUpdatePublishPlatform(PublishPlatform publishPlatform) {
        if(publishPlatform == null){
            return false;
        }
        List<PublishPlatform> publishPlatforms = new ArrayList<>();
        publishPlatforms.add(publishPlatform);
        return IMDatabaseManager.getInstance().InsertPublicNumber(publishPlatforms);
    }

    /**
     * No use
     * @param publishPlatforms
     * @return
     */
    @Override
    public boolean insertOrUpdatePublishPlatforms(List<PublishPlatform> publishPlatforms) {
        return false;
    }

    @Override
    public List<PublishPlatform> selectAllPublishPlatforms(int limit) {
        return IMDatabaseManager.getInstance().selectPublishPlatfroms(limit);
    }

    @Override
    public List<PublishPlatform> searchPublishPlatform(String term,int limit) {
        return IMDatabaseManager.getInstance().searchPublishPlatform(term,limit);
    }

    @Override
    public List<PublishPlatform> searchPublishPlatform(String term, int limit, int offset) {
        return IMDatabaseManager.getInstance().searchPublishPlatform(term,limit,offset);
    }
}
