package com.qunar.im.ui.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by xinbo.wang on 2015/5/22.
 */
public class CommonViewHolder {
    private final SparseArray<View> mViews;
    private int mPostion;
    private View mConvertView;

    private CommonViewHolder(Context cxt,ViewGroup parent,int layoutId,int postion)
    {
        this.mPostion = postion;
        this.mViews = new SparseArray<>();
        mConvertView = LayoutInflater.from(cxt).inflate(layoutId,parent,false);
        mConvertView.setTag(this);
    }

    public static CommonViewHolder get(Context context,View convertView,ViewGroup parent,int layoutId,int position)
    {
        CommonViewHolder holder = null;
        if(convertView == null)
        {
            holder = new CommonViewHolder(context,parent,layoutId,position);
        }
        else {
            holder = (CommonViewHolder) convertView.getTag();
            if(holder==null)
            {
                holder = new CommonViewHolder(context,parent,layoutId,position);
            }
            else {
                holder.mPostion = position;
            }
        }
        return holder;
    }

    public View getConvertView()
    {
        return mConvertView;
    }

    public <T extends View> T getView(int viewId)
    {
        View view = mViews.get(viewId);
        if(view == null)
        {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId,view);
        }
        return (T) view;
    }
}
