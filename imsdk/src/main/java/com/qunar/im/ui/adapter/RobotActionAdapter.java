package com.qunar.im.ui.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qunar.im.base.jsonbean.RobotInfoResult.Action;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaokai on 15-9-2.
 */
public class RobotActionAdapter extends BaseAdapter {
    private Context context;
    private List<Action.SubAction> list = new ArrayList<>();
    private int width ;
    private int height;


    public void setTextViewWidth(int width) {
        this.width = width;
    }

    public void setTextViewHeight(int height) {
        this.height = height;
    }

    public RobotActionAdapter(Context context, List<Action.SubAction> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView =null;
        if(convertView == null) {
            textView = new TextView(context);
            textView.setWidth(width);
            textView.setHeight(height);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            String s = list.get(position).subaction;
            textView.setText(s);
        }
        else {
            textView = (TextView) convertView;
        }
        return textView;
    }
}
