package com.qunar.im.ui.view.baseView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qunar.im.base.jsonbean.RbtSuggestionListJson;
import com.qunar.im.ui.R;

import java.util.ArrayList;
import java.util.List;

public class RobotQuestionAdapter extends BaseAdapter {

    private List<RbtSuggestionListJson.Item> data = new ArrayList<>();
    private LayoutInflater mInflater;

    public RobotQuestionAdapter(List<RbtSuggestionListJson.Item> datalist, Context context) {
        this.data = datalist;
        this.mInflater = LayoutInflater.from(context);


    }

    public void changeList(List<RbtSuggestionListJson.Item> dataList){
        this.data = dataList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.atom_ui_item_question_list_item, parent, false); //加载布局
            holder = new ViewHolder();
            holder.itemString = (TextView) convertView.findViewById(R.id.question_details);
            convertView.setTag(holder);
        } else {   //else里面说明，convertView已经被复用了，说明convertView中已经设置过tag了，即holder
            holder = (ViewHolder) convertView.getTag();
        }
        String  item = data.get(position).text;
        holder.itemString.setText(item);
//

        return convertView;
    }

    private class ViewHolder {
        TextView itemString;

    }
}
