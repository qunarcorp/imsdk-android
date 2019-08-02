package com.qunar.im.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qunar.im.base.jsonbean.DailyMindSub;
import com.qunar.im.ui.R;

import java.util.List;

/**
 * Created by lihaibin.li on 2017/11/20.
 */

public class DailyNoteSubAdapter extends CommonAdapter<DailyMindSub> {
    public DailyNoteSubAdapter(Context cxt, List<DailyMindSub> datas, int itemLayoutId) {
        super(cxt, datas, itemLayoutId);
    }

    @Override
    public void changeData(List<DailyMindSub> datas) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    @Override
    public void convert(CommonViewHolder viewHolder, DailyMindSub item) {
        TextView note_title = viewHolder.getView(R.id.note_title);
        note_title.setText(item.title);

    }
}
