package com.qunar.im.ui.view.treeView.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.qunar.im.ui.R;
import com.qunar.im.ui.view.IconView;
import com.qunar.im.ui.view.treeView.model.TreeNode;

/**
 * Created by Bogdan Melnychuk on 2/13/15.
 */
public class SDHolder extends TreeNode.BaseNodeViewHolder<IconTreeItemHolder.IconTreeItem> {

    protected IconView iconView;
    private boolean isFirstLevel;
    public SDHolder(Context context) {
        super(context);
    }

//    public SDHolder(Context context,boolean isFirstLevel) {
//        super(context);
//        this.isFirstLevel = isFirstLevel;
//    }

    @Override
    public View createNodeView(TreeNode node, IconTreeItemHolder.IconTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.atom_ui_view_tree_node_profile, null, false);
        TextView tvValue = view.findViewById(R.id.node_value);
        tvValue.setText(value.text);

        iconView = view.findViewById(R.id.node_icon);
        iconView.setText(context.getResources().getString(value.icon));

        return view;
    }

    @Override
    public void toggle(boolean active) {
        iconView.setText(context.getResources().getString(active?R.string.atom_ui_new_arrow_down:R.string.atom_ui_new_arrow_right));
    }

    @Override
    public int getContainerStyle() {
        return R.style.TreeNodeStyleDivided;
    }
}
