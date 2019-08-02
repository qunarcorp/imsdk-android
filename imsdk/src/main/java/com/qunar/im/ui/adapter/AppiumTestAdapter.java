package com.qunar.im.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qunar.im.ui.R;
import com.qunar.im.ui.entity.AppiumCase;

import java.util.List;

/**
 * Created by lihaibin.li on 2018/2/2.
 */

public class AppiumTestAdapter extends BaseAdapter {
    List<AppiumCase> cases;
    LayoutInflater inflater;
    public AppiumTestAdapter(Context context,List<AppiumCase> cases){
        this.cases = cases;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return cases == null?0:cases.size();
    }

    @Override
    public AppiumCase getItem(int position) {
        return cases.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.atom_ui_appium_test_list_item,null);
            holder.caseName = (TextView) convertView.findViewById(R.id.case_name);
            holder.caseStatus = (TextView) convertView.findViewById(R.id.status);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.caseName.setText(getItem(position).caseName);
        int isScuess = getItem(position).caseStatus;
        switch (isScuess){
            case -1:
                holder.caseStatus.setText("待测试");
                holder.caseStatus.setTextColor(Color.parseColor("#15b0f9"));
                break;
            case 0:
                holder.caseStatus.setText("成功");
                holder.caseStatus.setTextColor(Color.parseColor("#FF45CF8E"));
                break;
            case 1:
                holder.caseStatus.setText("失败");
                holder.caseStatus.setTextColor(Color.parseColor("#f4343d"));
                break;
        }

        return convertView;
    }

    class ViewHolder{
        TextView caseName;
        TextView caseStatus;
    }
}
