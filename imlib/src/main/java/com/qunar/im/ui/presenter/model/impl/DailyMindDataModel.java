package com.qunar.im.ui.presenter.model.impl;

import com.qunar.im.base.jsonbean.DailyMindSub;
import com.qunar.im.core.manager.IMDatabaseManager;
import com.qunar.im.base.jsonbean.DailyMindMain;
import com.qunar.im.ui.presenter.model.IDailyMindDataModel;

import java.util.List;


/**
 * 密码箱datamodel
 * Created by lihaibin.li on 2017/8/22.
 */

public class DailyMindDataModel implements IDailyMindDataModel {

    @Override
    public void dropPasswordBoxMainTable() {
    }

    @Override
    public List<DailyMindMain> getCloudMain(int type,int offset, int number) {
        return IMDatabaseManager.getInstance().getDailyMain(type,offset,number);
    }

    @Override
    public DailyMindMain getCloudMainByTitle() {
        return IMDatabaseManager.getInstance().getDailyMainByTitle();
    }

    @Override
    public void insertBoxMain(DailyMindMain dailyMindMain) {
        IMDatabaseManager.getInstance().insertDailyMain(dailyMindMain);
    }

    @Override
    public void deleteBoxMain(String qid) {
        IMDatabaseManager.getInstance().deleteDailyMain(qid);
    }

    @Override
    public void insertMultiBoxMain(List<DailyMindMain> dailyMindMains) {
        IMDatabaseManager.getInstance().insertMultiDailyMain(dailyMindMains);
    }

    @Override
    public void insertBoxSub(DailyMindSub dailyMindSub) {
        IMDatabaseManager.getInstance().insertPasswordBoxSub(dailyMindSub);
    }

    @Override
    public void updateBoxSub(DailyMindSub dailyMindSub) {
        IMDatabaseManager.getInstance().updatePasswordBoxSub(dailyMindSub);
    }

    @Override
    public List<DailyMindSub> getCloudSub(int offset, int number, int qid) {
        return IMDatabaseManager.getInstance().getPasswordBoxSub(offset,number,qid);
    }

    @Override
    public void insertMultiBoxSub(List<DailyMindSub> dailyMindSubs) {
        IMDatabaseManager.getInstance().insertMultiPasswordBoxSub(dailyMindSubs);
    }

    @Override
    public DailyMindSub getCloudSubByTitle(String title, String qid) {
        return IMDatabaseManager.getInstance().getPasswordBoxSubByTitle(title,qid);
    }
}
