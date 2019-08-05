package com.qunar.im.ui.view.treeView.holder;

import android.content.Context;
import android.view.View;

import com.qunar.im.base.jsonbean.GetDepartmentResult;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.treeView.model.TreeNode;
import com.qunar.im.base.protocol.NativeApi;

/**
 * Created by lihaibin.li on 2018/1/4.
 */

public class ULHolder extends SDHolder {

    Context context;
    public ULHolder(Context context){
        super(context);
        this.context = context;
    }
    @Override
    public View createNodeView(TreeNode node, IconTreeItemHolder.IconTreeItem value) {
        final GetDepartmentResult.UserItem item = value.item;
        value.text = item.N;
        View view = super.createNodeView(node, value);
        iconView.setText(R.string.atom_ui_new_person);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NativeApi.openUserCardVCByUserId(item.U);
            }
        });
        return view;
    }

    @Override
    public void toggle(boolean active) {
        super.toggle(active);
    }

    @Override
    public int getContainerStyle() {
        return super.getContainerStyle();
    }
}
