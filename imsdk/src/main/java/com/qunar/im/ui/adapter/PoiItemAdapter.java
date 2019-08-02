package com.qunar.im.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.qunar.im.ui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaokai on 16-1-26.
 */
public class PoiItemAdapter extends BaseAdapter {

    private List<PoiInfo> infos;
    private Context context;

    public void setCheckPosition(int checkPosition) {
        this.checkPosition = checkPosition;
    }

    private int checkPosition = -1;

    public PoiItemAdapter(Context context) {
        this.context = context;
        infos = new ArrayList<>();
    }

    public void setPoiInfo(List<PoiInfo> info) {

        infos.clear();
        addPoiInfo(info);
    }

    public void addPoiInfo(List<PoiInfo> info) {
        if (info != null) {
            infos.addAll(info);
        }
    }

    @Override
    public int getCount() {
        if (infos != null) {
            return infos.size();
        }
        return -1;
    }

    @Override
    public Object getItem(int position) {
        if (-1 != position && !infos.isEmpty()) {
            return infos.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.atom_ui_item_poi, null);
            convertView.setTag(holder);
            holder.address = (TextView) convertView.findViewById(R.id.address);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.checked = (TextView) convertView.findViewById(R.id.checked);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        PoiInfo info = (PoiInfo) getItem(position);
        holder.checked.setVisibility(position == this.checkPosition ? View.VISIBLE : View.GONE);
        holder.address.setText(info.address);
        holder.name.setText(info.name);
        return convertView;
    }

    private final class ViewHolder {
        TextView address;
        TextView name;
        TextView checked;
    }
}
