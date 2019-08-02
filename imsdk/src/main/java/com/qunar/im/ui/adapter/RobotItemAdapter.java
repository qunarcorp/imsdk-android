package com.qunar.im.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qunar.im.ui.R;
import com.qunar.im.base.jsonbean.RobotInfoResult.Action;

import java.util.List;

/**
 * Created by zhaokai on 15-9-1.
 */
public class RobotItemAdapter extends BaseAdapter {

    private static final int MAX_SIZE = 3;
    private List<Action> list;
    private Context context;

    public RobotItemAdapter(Context context, List<Action> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        if (position < MAX_SIZE) {
            return list.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (position <= MAX_SIZE) {
            return position;
        }
        return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout lin = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.atom_ui_item_robot_tab, parent, false);
        final TextView textView = (TextView) lin.findViewById(R.id.content);
        final View line = lin.findViewById(R.id.line);
        Action item = (Action) getItem(position);
        if (position == 0) {
            line.setVisibility(View.INVISIBLE);
        }
        textView.setText(item.mainaction);
        textView.setTextSize(16);
        textView.setWidth(parent.getWidth() / getCount());
        textView.setFocusable(true);
        textView.setClickable(true);
        if (item.subactions != null && item.subactions.size() > 0) {
            Drawable drawable = context.getResources().getDrawable(R.drawable.atom_ui_has_child_action);
            if (drawable != null) {
                drawable.setBounds(10, 0, 20, 20);
                textView.setCompoundDrawables(drawable, null, null, null);
            }
        }
        return lin;
    }
}