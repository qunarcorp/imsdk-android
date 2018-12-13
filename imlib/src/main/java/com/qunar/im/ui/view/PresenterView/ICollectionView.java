package com.qunar.im.ui.view.PresenterView;

import android.content.Context;

import com.qunar.im.ui.view.recyclerview.entity.MultiItemEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hubin on 2017/11/21.
 */

public interface ICollectionView {

    //获取上下文
    Context getContext();
    void setList(ArrayList<MultiItemEntity> list);
}
