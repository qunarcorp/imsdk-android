package com.qunar.im.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by xinbo.wang on 2015/5/22.
 */
public abstract class CommonAdapter<T> extends BaseAdapter {
    protected Context mContext;
    protected List<T> mDatas;
    protected final int mItemLayoutId;

    public CommonAdapter(Context cxt,List<T> datas,int itemLayoutId)
    {
        this.mContext = cxt;
        this.mDatas = datas;
        this.mItemLayoutId = itemLayoutId;
    }

    protected void changeData(List<T> datas)
    {
        this.mDatas = datas;
    }

    protected void addTail(List<T> datas)
    {
        this.mDatas.addAll(datas);
    }

    protected void addHead(List<T> datas)
    {
        this.mDatas.addAll(0,datas);
    }

    @Override
    public int getCount() {
        if(mDatas==null) return 0;
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final CommonViewHolder viewHolder = getViewHolder(position,convertView,parent);
        convert(viewHolder,getItem(position));
        return viewHolder.getConvertView();
    }

    public abstract void convert(CommonViewHolder viewHolder,T item);

    private CommonViewHolder getViewHolder(int position,View convertView,ViewGroup parent)
    {
        return CommonViewHolder.get(mContext,convertView,parent,mItemLayoutId,position);
    }
}
