package com.qunar.im.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.base.module.Nick;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.ui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinbo.wang on 2015/3/4.
 */
public class InvitationAdapter extends BaseAdapter implements Filterable {

    List<Nick> allContacts;
    List<Nick> showItems;
    List<Nick> selectedItems = new ArrayList<>();
    Context context;
    SelectChangeListener listener;
    SearchResultChangeListerner searchListner;

    //默认头像
    private static String headerSrc = QtalkNavicationService.getInstance().getInnerFiltHttpHost() + "/file/v2/download/perm/3ca05f2d92f6c0034ac9aee14d341fc7.png";
    ProtrailHandle protrailHandle;

    public InvitationAdapter(Context context) {
        this.context = context;
    }

    public void setSelectedItemChangeListener(SelectChangeListener l) {
        listener = l;
    }

    public void setSearchResultChangeListner(SearchResultChangeListerner listner) {
        this.searchListner = listner;
    }

    public void setProtrailHandle(ProtrailHandle handle) {
        this.protrailHandle = handle;
    }

    public void setAllContacts(List<Nick> allContacts) {
        this.allContacts = allContacts;
        this.showItems = null;
        this.notifyDataSetChanged();
    }

    public List<Nick> getSelectedItems() {
        return selectedItems;
    }

    @Override
    public int getCount() {
        if (showItems == null)
            return 0;

        return showItems.size();
    }

    @Override
    public Nick getItem(int position) {
        if (getCount() == 0)
            return null;

        if (position >= showItems.size())
            return null;
        return showItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (showItems == null)
            return 0;
        return showItems.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.atom_ui_item_invitation_chatroom, null);
            holder = new ViewHolder();
            holder.userName = (TextView) convertView.findViewById(R.id.user_name);
            holder.depName = (TextView) convertView.findViewById(R.id.user_dep);
            holder.headerImg = (SimpleDraweeView) convertView.findViewById(R.id.img_header);
            holder.panel = (ViewGroup) convertView.findViewById(R.id.search_item_panel);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Nick item = getItem(position);

        holder.panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchListner.onSelected(item);
            }
        });
        holder.userName.setText(item.getName());
        holder.depName.setText(item.getDescInfo());
        if(null != protrailHandle){
            protrailHandle.onLoadUserProtrailLocal(holder.headerImg, item.getXmppId(), headerSrc);
        }
//        ConnectionUtil.getInstance(context).getUserCard(item.getXmppId(), new IMLogicManager.NickCallBack() {
//            @Override
//            public void onNickCallBack(Nick nick) {
////                if(nick!=null){
////                    holder.headerImg.setImageUrl(item.getHeaderSrc(),false);
////                }else{
////                    holder.headerImg.setImageUrl(headerSrc,false);
////                }
//                if (null != protrailHandle) {
//                    protrailHandle.onLoadUserProtrailLocal(holder.headerImg, nick.getXmppId(), nick.getHeaderSrc());
//                }
//            }
//        }, false, false);
//
        return convertView;
    }

    private class ViewHolder {
        public TextView userName;
        public TextView depName;
        public SimpleDraweeView headerImg;
        public ViewGroup panel;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (allContacts == null)
                    return null;

                if (TextUtils.isEmpty(constraint)) {
//                    results.count = allContacts.size();
//                    results.values = allContacts;
                    results.count = 0;
                    results.values = new ArrayList<>();

                    return results;
                }
                List<Nick> resultValue = new ArrayList<>();

                for (int i = 0; i < allContacts.size(); i++) {
                    Nick item = allContacts.get(i);
                    if ((item.getName() != null && item.getName().contains(constraint)) ||
                            (item.getSearchIndex() != null && item.getSearchIndex().contains(constraint))) {
                        resultValue.add(item);
                    }
                }

                results.count = resultValue.size();
                results.values = resultValue;

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null) {
                    showItems = (List<Nick>) results.values;

                    notifyDataSetChanged();

                    if (searchListner != null) {
                        searchListner.onSearchChange(showItems.size());
                    }
                }
            }
        };
        return filter;
    }

    public interface ProtrailHandle {
        void onLoadUserProtrailLocal(SimpleDraweeView imageView, String jid, String imageSrc);
    }

    public interface SelectChangeListener {
        void onSelectChange(int count);
    }

    public interface SearchResultChangeListerner {
        void onSearchChange(int count);

        void onSelected(Nick item);
    }
}
