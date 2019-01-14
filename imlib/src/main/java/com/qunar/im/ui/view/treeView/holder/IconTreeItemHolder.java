package com.qunar.im.ui.view.treeView.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.qunar.im.base.jsonbean.GetDepartmentResult;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.IconView;
import com.qunar.im.ui.view.treeView.model.TreeNode;

/**
 * Created by Bogdan Melnychuk on 2/12/15.
 */
public class IconTreeItemHolder extends TreeNode.BaseNodeViewHolder<IconTreeItemHolder.IconTreeItem> {
    private TextView tvValue;
    private IconView arrowView;

    public IconTreeItemHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(final TreeNode node, IconTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.atom_ui_view_tree_node, null, false);
        tvValue = view.findViewById(R.id.node_value);
        tvValue.setText(value.text);

        final IconView iconView = view.findViewById(R.id.node_icon);
        iconView.setText(context.getResources().getString(value.icon));

        arrowView = view.findViewById(R.id.arrow_icon);

        view.findViewById(R.id.btn_addFolder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TreeNode newFolder = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.atom_ui_new_my_file, "New Folder"));
                getTreeView().addNode(node, newFolder);
            }
        });

        view.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTreeView().removeNode(node);
            }
        });

        //if My computer
        if (node.getLevel() == 1) {
            view.findViewById(R.id.btn_delete).setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void toggle(boolean active) {
        arrowView.setText(context.getResources().getString(active ? R.string.atom_ui_new_arrow_down : R.string.atom_ui_new_arrow_right));
    }

    public static class IconTreeItem {
        public int icon;
        public String text;
        public GetDepartmentResult.UserItem item;

        public IconTreeItem(int icon, String text) {
            this.icon = icon;
            this.text = text;
        }

        public IconTreeItem(int icon, GetDepartmentResult.UserItem item) {
            this.icon = icon;
            this.item = item;
        }
    }
}
