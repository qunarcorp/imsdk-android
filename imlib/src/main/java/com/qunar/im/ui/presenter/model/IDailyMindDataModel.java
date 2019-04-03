package com.qunar.im.ui.presenter.model;

import com.qunar.im.base.jsonbean.DailyMindMain;
import com.qunar.im.base.jsonbean.DailyMindSub;

import java.util.List;

/**
 * Created by lihaibin.li on 2017/8/22.
 */

public interface IDailyMindDataModel {
    void dropPasswordBoxMainTable();
    List<DailyMindMain> getCloudMain(int type,int offset, int number);//from DB
    DailyMindMain getCloudMainByTitle();
    List<DailyMindSub> getCloudSub(int offset, int number, int qid);
    DailyMindSub getCloudSubByTitle(String title, String qid);
    void insertBoxMain(DailyMindMain dailyMindMain);
    void insertMultiBoxMain(List<DailyMindMain> dailyMindMains);
    void deleteBoxMain(String qid);

    void insertBoxSub(DailyMindSub dailyMindSub);
    void updateBoxSub(DailyMindSub dailyMindSub);
    void insertMultiBoxSub(List<DailyMindSub> dailyMindSubs);
}
