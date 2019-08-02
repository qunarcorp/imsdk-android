package com.qunar.im.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qunar.im.base.jsonbean.DailyMindMain;
import com.qunar.im.ui.R;

import java.util.List;

/**
 * Created by lihaibin.li on 2017/10/16.
 */

public class DailyTodolistAdapter extends CommonAdapter<DailyMindMain> {
    public DailyTodolistAdapter(Context cxt, List<DailyMindMain> datas, int itemLayoutId) {
        super(cxt, datas, itemLayoutId);
    }

    @Override
    protected void changeData(List<DailyMindMain> datas) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    @Override
    public void convert(CommonViewHolder viewHolder, DailyMindMain item) {
        TextView todolist_title = viewHolder.getView(R.id.todolist_title);
        todolist_title.setText(item.title);

//        CheckBox is_complete = viewHolder.getView(R.id.is_complete);
//        is_complete.setChecked(item.isComplete==1);
    }
}
