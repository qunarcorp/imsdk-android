package com.qunar.im.ui.presenter.views;

import com.qunar.im.ui.util.easyphoto.easyphotos.models.album.entity.Photo;
import com.qunar.im.base.jsonbean.ExtendMessageEntity;
import com.qunar.im.base.module.AnonymousData;
import com.qunar.im.base.module.MultiItemEntity;
import com.qunar.im.base.module.WorkWorldItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface ReleaseCircleView {
    void closeActivitvAndResult(ArrayList<WorkWorldItem> list);

    List<MultiItemEntity> getUpdateImageList();

    Photo getUpdateVideo();

    String getContent();

    void showProgress();

    void dismissProgress();

    int getIdentityType();

    AnonymousData getAnonymousData();

    void setAnonymousData(AnonymousData anonymousData);

    void showToast(String str);

    boolean isCheck();

    ExtendMessageEntity getEntity();
    Map<String,String> getAtList();

}
