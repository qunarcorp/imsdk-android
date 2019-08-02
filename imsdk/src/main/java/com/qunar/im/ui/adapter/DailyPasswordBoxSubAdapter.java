package com.qunar.im.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qunar.im.base.jsonbean.DailyMindSub;
import com.qunar.im.ui.R;

import java.util.List;

/**
 * Created by lihaibin.li on 2017/8/24.
 */

public class DailyPasswordBoxSubAdapter extends CommonAdapter<DailyMindSub> {
    public DailyPasswordBoxSubAdapter(Context cxt, List<DailyMindSub> datas, int itemLayoutId) {
        super(cxt, datas, itemLayoutId);
    }

    @Override
    protected void changeData(List<DailyMindSub> datas) {
        super.changeData(datas);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public DailyMindSub getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    @Override
    public void convert(CommonViewHolder viewHolder, DailyMindSub item) {
        TextView name = viewHolder.getView(R.id.box_sub_name);
        name.setText(item.title);
//        TextView time = viewHolder.getView(R.id.box_sub_time);
//        if(TextUtils.isEmpty(item.time))
//            time.setText(item.time);
    }
}
