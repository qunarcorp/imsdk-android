package com.qunar.im.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.qunar.im.base.view.BaseInfoBinderable;
import com.qunar.im.ui.view.baseView.BaseInfoView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinbo.wang on 2015/2/12.
 */
public class BaseInfoAdapter extends BaseAdapter {
    Context context;
    ViewClickHandler handler;
    private List<BaseInfoBinderable> dataInfoList
            = new ArrayList<BaseInfoBinderable>();

    public BaseInfoAdapter(Context context) {
        this.context = context;
    }

    public void setClickHandler(ViewClickHandler h) {
        handler = h;
    }



    public void clear() {
        dataInfoList.clear();
    }

    @Override
    public int getCount() {
        return dataInfoList.size();
    }

    @Override
    public BaseInfoBinderable getItem(int position) {
        if (position < 0) {
            return null;
        }
        return dataInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseInfoView baseInfoView;
        if (convertView != null) {
            if(convertView instanceof BaseInfoView) {
                baseInfoView = (BaseInfoView) convertView;
            }
            else {
                baseInfoView = new BaseInfoView(context);
                baseInfoView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        } else {
            baseInfoView = new BaseInfoView(context);
            baseInfoView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        final BaseInfoBinderable binderable = getItem(position);
        if(binderable != null) {
            baseInfoView.bind(binderable);
            baseInfoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handler.ItemClickEvent(binderable);
                }
            });
        }
        return baseInfoView;
    }


    public void update(List<BaseInfoBinderable> list) {
        dataInfoList.addAll(list);
        notifyDataSetChanged();
    }

    public interface ViewClickHandler {
        void ItemClickEvent(BaseInfoBinderable o);
    }
}