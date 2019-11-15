package com.qunar.im.ui.view.baseView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qunar.im.base.jsonbean.RbtNewSuggestionList;
import com.qunar.im.ui.R;

import java.util.ArrayList;
import java.util.List;

public class RobotNewQuestionAdapter extends BaseAdapter {


    private List<RbtNewSuggestionList.ListAreaBean.ItemsBean> data = new ArrayList<>();
    private LayoutInflater mInflater;
    private int count = 3;
    private boolean isMore;


    public RobotNewQuestionAdapter(List<RbtNewSuggestionList.ListAreaBean.ItemsBean> datalist, Context context, boolean isMore) {
        this.data = datalist;
        this.mInflater = LayoutInflater.from(context);
        this.isMore = isMore;

    }

    public void changeList(List<RbtNewSuggestionList.ListAreaBean.ItemsBean> dataList) {
        this.data = dataList;
        notifyDataSetChanged();
    }

    public void setDefCount(int count) {
        this.count = count;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (data.size() > count) {
            return count;
        } else {
            return data.size();
        }
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
        RobotNewQuestionAdapter.ViewHolder holder = null;
        if (convertView == null) {
            if(isMore) {
                convertView = mInflater.inflate(R.layout.atom_ui_item_question_list_more_new_item, parent, false); //加载布局
            }else{
                convertView = mInflater.inflate(R.layout.atom_ui_item_question_list_new_item, parent, false); //加载布局
            }
            holder = new RobotNewQuestionAdapter.ViewHolder();
            holder.itemString = (TextView) convertView.findViewById(R.id.question_details);
            convertView.setTag(holder);
        } else {   //else里面说明，convertView已经被复用了，说明convertView中已经设置过tag了，即holder
            holder = (RobotNewQuestionAdapter.ViewHolder) convertView.getTag();
        }
        String item = data.get(position).getText();
        holder.itemString.setText(item);
//

        return convertView;
    }

    private class ViewHolder {
        TextView itemString;

    }
}
