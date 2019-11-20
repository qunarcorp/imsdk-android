package com.qunar.im.base.common;

import com.qunar.im.core.services.QtalkNavicationService;

/**
 * daily mind constants
 * Created by lihaibin.li on 2017/8/22.
 */

public class DailyMindConstants {
    //线网
    public static String DAILY_MIND_BASE_URL = QtalkNavicationService.getInstance().getJavaUrl() + "/qtapi/qcloud/";

    //主记录相关
    public static final String SAVE_TO_MAIN = "saveToMain.qunar";//主记录保存

    public static final String UPDATE_MAIN = "updateMain.qunar";//修改主记录

    public static final String DELETE_MAIN = "deleteMain.qunar";//删除主记录

    public static final String COLLECTION_MAIN = "collectionMain.qunar";//收藏主记录

    public static final String CANCEL_COLLECTION_MAIN = "cancelCollectionMain.qunar";//取消收藏主记录

    public static final String MOVE_TO_BASKET_MAIN = "moveToBasketMain.qunar";//主记录移动到废纸篓

    public static final String MOVE_OUT_BASKET_MAIN = "moveOutBasketMain.qunar";//主记录移出废纸篓

    public static final String GET_CLOUD_MAIN = "getCloudMain.qunar";//获取主记录

    public static final String GET_CLOUD_MAIN_HISTORY = "getCloudMainHistory.qunar";//获取主记录操作历史

    public static final String SYNC_CLOUD_MAIN_LIST = "syncCloudMainList.qunar";//批量插入更新主记录

    //子记录相关
    public static final String SAVE_TO_SUB = "saveToSub.qunar";//子记录保存

    public static final String UPDATE_SUB = "updateSub.qunar";//子记录更新

    public static final String DELETE_SUB = "deleteSub.qunar";//自己录删除

    public static final String COLLECTION_SUB = "collectionSub.qunar";//收藏子记录

    public static final String CANCEL_COLLECTION_SUB = "cancelCollectionSub.qunar";//取消收藏子记录

    public static final String MOVE_TO_BASKET_SUB = "moveToBasketSub.qunar";//子记录移动到废纸篓

    public static final String MOVE_OUT_BASKET_SUB = "moveOutBasketSub.qunar";//子记录移出废纸篓

    public static final String GET_CLOUD_SUB = "getCloudSub.qunar";//获取子记录

    public static final String GET_CLOUD_SUB_HISTORY = "getCloudSubHistory.qunar";//子记录操作历史

    public static final String SYNC_CLOUD_SUB_LIST = "syncCloudSubList.qunar";//批量更新插入子记录

    public static final int DELETE = -1;
    public static final int NORMAL = 1;
    public static final int COLLECTION = 2;
    public static final int BASKET = 3;
    public static final int CREATE = 4;
    public static final int UPDATE = 4;


    public static final int PASSOWRD = 1;
    public static final int TODOLIST = 2;
    public static final int EVERNOTE = 3;

    public static final int CHATPASSWORD = 100;

}
