package com.qunar.im.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qunar.im.base.jsonbean.DailyMindMain;
import com.qunar.im.ui.R;

import java.util.List;

/**
 * 密码箱adapter
 * Created by lihaibin.li on 2017/8/23.
 */

public class DailyPasswordBoxAdapter extends CommonAdapter<DailyMindMain> {
    public DailyPasswordBoxAdapter(Context cxt, List<DailyMindMain> datas, int itemLayoutId) {
        super(cxt, datas, itemLayoutId);
    }

    @Override
    protected void changeData(List datas) {
        super.changeData(datas);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public DailyMindMain getItem(int position) {
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
    public void convert(CommonViewHolder viewHolder, DailyMindMain item) {
        TextView name = viewHolder.getView(R.id.password_box_name);
        if (item != null)
            name.setText(item.title);
    }
}
