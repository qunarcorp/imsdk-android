package com.qunar.im.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.qunar.im.ui.R;
import com.qunar.im.ui.util.ProfileUtils;
import com.qunar.im.base.view.multilLevelTreeView.Node;
import com.qunar.im.ui.view.multilLevelTreeView.TreeListViewAdapter;


public class SimpleTreeAdapter<T extends Node> extends TreeListViewAdapter<T> {
    public SimpleTreeAdapter(PullToRefreshListView mTree, Context context) throws IllegalArgumentException,
            IllegalAccessException {
        super(mTree, context);
        mContext = context;
    }

    @Override
    public View getConvertView(final Node node, int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.atom_ui_tree_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.icon = (SimpleDraweeView) convertView
                    .findViewById(R.id.treenode_icon);
            viewHolder.label = (TextView) convertView
                    .findViewById(R.id.treenode_label);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(node.isRoot())
        {
            viewHolder.icon.setVisibility(View.GONE);
            viewHolder.label.setText(node.getName());
        }
        else {
            ProfileUtils.loadNickName(node.getKey(),viewHolder.label,  true);
            viewHolder.icon.setVisibility(View.VISIBLE);
            ProfileUtils.displayGravatarByUserId(node.getKey(),viewHolder.icon);
        }

        return convertView;
    }

    private final class ViewHolder {
        SimpleDraweeView icon;
        TextView label;
    }

}
