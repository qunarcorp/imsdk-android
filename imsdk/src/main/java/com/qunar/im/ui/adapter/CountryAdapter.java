package com.qunar.im.ui.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.qunar.im.ui.R;
import com.qunar.im.base.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by saber on 16-2-25.
 */
public class CountryAdapter extends CommonAdapter<CountryNode> implements SectionIndexer,Filterable {
    private final int rootHeight;
    private final int nodeHeight;
    public List<CountryNode> mContries = new ArrayList<>();
    private Map<Integer,Integer> index = new HashMap<>();
    public int selectId;

    String[] sections = new String[]{"^","A","B","C","D","E","F","G","H","I","J","K","L","M",
            "N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

    public CountryAdapter(Context cxt, List<CountryNode> datas, int itemLayoutId) {
        super(cxt, datas, itemLayoutId);
        rootHeight = Utils.dipToPixels(mContext, 18);
        nodeHeight = Utils.dipToPixels(mContext,48);
    }

    @Override
    public int getCount() {
        return mContries.size();
    }

    @Override
    public CountryNode getItem(int position) {
        return mContries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void convert(CommonViewHolder viewHolder, CountryNode item) {
        final TextView tv_name = viewHolder.getView(R.id.name);
        final ImageView iv_sel_icon = viewHolder.getView(R.id.icon);
        if(item.isRoot)
        {
            viewHolder.getConvertView().setLayoutParams(
                    new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, rootHeight));
            viewHolder.getConvertView().setBackgroundResource(R.color.atom_ui_light_gray_ee);
            tv_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            iv_sel_icon.setVisibility(View.GONE);
            tv_name.setText(item.name);

        }
        else {
            tv_name.setText(item.name);
            tv_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            viewHolder.getConvertView().setLayoutParams(
                    new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, nodeHeight));
            if(item.resourceId == selectId)
            {
                iv_sel_icon.setVisibility(View.VISIBLE);
            }
            else {
                iv_sel_icon.setVisibility(View.INVISIBLE);
            }
            viewHolder.getConvertView().setBackgroundResource(R.color.atom_ui_white);
        }
    }

    @Override
    public Object[] getSections() {
        return sections;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        int idx = 0;
        if(index.containsKey(sectionIndex)) {
            idx = index.get(sectionIndex);
        }
        return idx;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    public void setNodes(Map<Integer, List<CountryNode>> contacts) {
        mContries.clear();
        for(int i=0;i<28;i++)
        {
            if(i==0) {
                index.put(i,0);
                continue;
            }
            index.put(i,1+mContries.size());
            List<CountryNode> list = contacts.get(i);
            if(list==null)
            {
                index.put(i,index.get(i-1));
            }
            else {
                CountryNode node = new CountryNode();
                node.name = sections[i];
                node.isRoot = true;
                mContries.add(node);
                mContries.addAll(list);
            }
        }
        changeData(mContries);
        notifyDataSetChanged();
    }

    Filter filter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            results.values = mContries;
            results.count = mContries.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

        }
    };
    @Override
    public Filter getFilter() {
        return  filter;
    }
}
