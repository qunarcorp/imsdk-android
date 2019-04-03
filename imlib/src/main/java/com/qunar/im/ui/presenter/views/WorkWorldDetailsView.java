package com.qunar.im.ui.presenter.views;

import android.icu.util.IslamicCalendar;

import com.qunar.im.base.module.MultiItemEntity;
import com.qunar.im.base.module.WorkWorldDeleteResponse;
import com.qunar.im.base.module.WorkWorldNewCommentBean;
import com.qunar.im.base.module.WorkWorldOutCommentBean;

import java.util.List;

public interface WorkWorldDetailsView {
    public boolean isOK();
    public String getPostOwner();
    public String getPostOwnerHost();
    public String getPostUUid();
    public String getParentCommentUUID();
    public String getContent();
//    public String getToUser();
//    public String getToHost();
    public int isAnonymous();
    public String getAnonymousPhoto();
    public String getAnonymousName();
    public void showNewData(List<? extends MultiItemEntity> list, boolean isScroll, boolean isShowInput);
    public void updateNewCommentData(List<? extends MultiItemEntity > list, boolean isScroll,boolean isShowInput);
    public void showHotNewData(List<? extends  MultiItemEntity> list);
    public void showMoreData(List<? extends  MultiItemEntity> list);
    public WorkWorldNewCommentBean getToData();
    public String getCommentsNum();

    public void saveData();

    public void startRefresh();

    WorkWorldNewCommentBean getLastItem();

    boolean isCheck();
   void showToast(String str, boolean refresh);

    int getListCount();

    void removeWorkWorldCommentItem(WorkWorldDeleteResponse deleteWorkWorldItem);

    void updateLikeNum(int num);
    void updateCommentNum(int num);
    void updateLikeState(int isLike);


    //额外接口,更新数据用
    void updateOutCommentList(List<? extends MultiItemEntity> list);
}
