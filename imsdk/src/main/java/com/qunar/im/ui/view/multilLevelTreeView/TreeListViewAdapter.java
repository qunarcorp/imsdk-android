package com.qunar.im.ui.view.multilLevelTreeView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.qunar.im.base.util.Utils;
import com.qunar.im.base.view.multilLevelTreeView.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * created by xinbo.wang
 */
public abstract class TreeListViewAdapter<T extends Node> extends BaseAdapter {

    protected Context mContext;

    protected List<Node> mNodes = new LinkedList<>();
    protected Map<Integer, List<Node>> mAllNodes;
    protected LayoutInflater mInflater;
    protected Node rootNode;
    protected int defaultMargin;

    private OnTreeNodeClickListener onTreeNodeClickListener;

    public interface OnTreeNodeClickListener {
        void onClick(Node node, int position);
    }

    public void setOnTreeNodeClickListener(
            OnTreeNodeClickListener onTreeNodeClickListener) {
        this.onTreeNodeClickListener = onTreeNodeClickListener;
    }

    /**
     * @param mTree
     * @param context
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public TreeListViewAdapter(final PullToRefreshListView mTree, Context context) throws IllegalArgumentException,
            IllegalAccessException {
        mContext = context;
        defaultMargin = Utils.dipToPixels(mContext, 16);
        mInflater = LayoutInflater.from(context);

        mTree.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position == 1) {
                    return;
                }
                Node clkNode = mNodes.get(position - 1);
                if (clkNode.isRoot()) {
                    clkNode.setExpand(!clkNode.isExpand());
                    expandOrCollapse(position - 1);
                    mTree.getRefreshableView().setSelection(position - 1);
                }
                if (onTreeNodeClickListener != null) {
                    onTreeNodeClickListener.onClick(clkNode,
                            position);
                }
            }

        });

    }

    public void setNodes(Map<Integer, List<Node>> mAllNodes) {
        if (mAllNodes.size() > 0) {
            this.mAllNodes = mAllNodes;
            mNodes.clear();
            rootNode = mAllNodes.get(-1).get(0);
            rootNode.setLevel(0);
            rootNode.setRoot(true);
            rootNode.setExpand(true);
            mNodes.add(rootNode);
            expandOrCollapse(0);
        }
    }

    public void expandOrCollapse(int position) {
        Node node = mNodes.get(position);
        if (node.isRoot()) {
            if (node.isExpand()) {
                List<Node> subList = mAllNodes.get(node.getId());
                if (subList != null) {
                    for (int j = 0; j < subList.size(); j++) {
                        subList.get(j).setLevel(node.getLevel() + 1);
                    }
                    mNodes.addAll(position + 1, subList);
                }

            } else {
                while (true) {
                    if (position + 1 == mNodes.size()) {
                        break;
                    }
                    if (mNodes.get(position + 1).getpId() <= node.getpId()) {
                        break;
                    }
                    mNodes.remove(position + 1);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mNodes.size();
    }

    @Override
    public Object getItem(int position) {
        return mNodes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Node node = mNodes.get(position);
        convertView = getConvertView(node, position, convertView, parent);
        convertView.setPadding(node.getLevel() * defaultMargin + defaultMargin, 3, 3, 3);
        return convertView;
    }

    public abstract View getConvertView(Node node, int position,
                                        View convertView, ViewGroup parent);

}
