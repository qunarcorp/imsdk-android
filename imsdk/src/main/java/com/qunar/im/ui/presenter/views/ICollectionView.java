package com.qunar.im.ui.presenter.views;

import android.content.Context;

import com.qunar.im.base.module.MultiItemEntity;

import java.util.ArrayList;

/**
 * Created by hubin on 2017/11/21.
 */

public interface ICollectionView {

    //获取上下文
    Context getContext();
    void setList(ArrayList<MultiItemEntity> list);
}
