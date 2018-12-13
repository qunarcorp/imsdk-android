package com.qunar.im.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.ChatroomInvitationActivity;
import com.qunar.im.base.util.ProfileUtils;
import com.qunar.im.base.view.multilLevelTreeView.Node;
import com.qunar.im.ui.view.multilLevelTreeView.TreeListViewAdapter;

import java.util.List;


public class InviteTreeAdapter<T extends Node> extends TreeListViewAdapter<T> {
    ChatroomInvitationActivity.ICheckboxClickedListener mCheckboxClickedListener;

    List<Node> mSelectedList;
    List<String> mNotChangeList;
    public InviteTreeAdapter(PullToRefreshListView mTree, Context context, ChatroomInvitationActivity.ICheckboxClickedListener checkboxClickedListener) throws IllegalArgumentException,
            IllegalAccessException {
        super(mTree, context);
        mContext = context;
        mCheckboxClickedListener = checkboxClickedListener;
    }

    public List<Node> getmSelectedList() {
        return mSelectedList;
    }

    public void setSelectedList(List<Node> selectedList) {
        this.mSelectedList = selectedList;
    }
    public void setNotChangeList(List<String> notChangeList){
        this.mNotChangeList = notChangeList;
    }

    @Override
    public View getConvertView(final Node node, int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
                convertView = mInflater.inflate(R.layout.atom_ui_item_invate_tree, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.icon = (SimpleDraweeView) convertView
                        .findViewById(R.id.treenode_icon);
                viewHolder.label = (TextView) convertView
                        .findViewById(R.id.treenode_label);
                convertView.setTag(viewHolder);
                viewHolder.cb_selected = (CheckBox) convertView.findViewById(R.id.cb_selected);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
        }

        if (node.isRoot()) {
            viewHolder.icon.setVisibility(View.GONE);
            viewHolder.label.setText(node.getName());
            viewHolder.cb_selected.setVisibility(View.GONE);
        } else {
            ProfileUtils.loadNickName(node.getKey(), viewHolder.label, true);
            viewHolder.icon.setVisibility(View.VISIBLE);
            ProfileUtils.displayGravatarByUserId(node.getKey(),viewHolder.icon);
            viewHolder.cb_selected.setVisibility(View.VISIBLE);
        }
        if(mNotChangeList.contains(node.getKey())){
            viewHolder.cb_selected.setChecked(true);
            viewHolder.cb_selected.setEnabled(false);
        }else if(mSelectedList.contains(node)){
            viewHolder.cb_selected.setEnabled(true);
            viewHolder.cb_selected.setChecked(true);
        }else{
            viewHolder.cb_selected.setEnabled(true);
            viewHolder.cb_selected.setChecked(false);
        }
        final ViewHolder finalViewHolder = viewHolder;
        viewHolder.cb_selected.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                if( finalViewHolder.cb_selected.isChecked()){
                    mSelectedList.add(node);
                }else{
                    mSelectedList.remove(node);
                }
                mCheckboxClickedListener.CheckobxClicked();
            }
        });
        return convertView;
    }


    private final class ViewHolder {
        SimpleDraweeView icon;
        TextView label;
        CheckBox cb_selected;
    }


}
