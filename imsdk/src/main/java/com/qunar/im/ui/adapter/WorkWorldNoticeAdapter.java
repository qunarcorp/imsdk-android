package com.qunar.im.ui.adapter;

import com.qunar.im.base.module.MultiItemEntity;
import com.qunar.im.ui.view.recyclerview.BaseMultiItemQuickAdapter;
import com.qunar.im.ui.view.recyclerview.BaseViewHolder;

import java.util.List;

public class WorkWorldNoticeAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity,BaseViewHolder>{
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public WorkWorldNoticeAdapter(List<MultiItemEntity> data) {
        super(data);

    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {

    }
}
