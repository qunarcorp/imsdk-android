package com.qunar.im.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.module.GroupMember;
import com.qunar.im.base.module.Nick;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.ui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinbo.wang on 2015/2/11.
 */
public class GroupManagerMembersAdapter extends BaseAdapter implements Filterable {

    List<GroupMember> members = new ArrayList<>();
    List<GroupMember> orgMembers = new ArrayList<>();
    Context context;
    GravatarHandler gravatarHandler;
    int myLevel = GroupMember.NONE;
    private List<GroupMember> selectedMembers = new ArrayList<>();
    //核心连接管理器
    private ConnectionUtil connectionUtil;

    public void setGravatarHandler(GravatarHandler gravatarHandler) {
        this.gravatarHandler = gravatarHandler;
    }

    public List<GroupMember> getSelectedMembers() {
        return selectedMembers;
    }

    public void setMembers(List<GroupMember> member, int level) {
        myLevel = level;
        this.members = member;
        orgMembers = member;
        this.notifyDataSetChanged();
    }

    public void deleteUser(){
        members.removeAll(selectedMembers);
        this.notifyDataSetChanged();
    }

    public GroupManagerMembersAdapter(Context context) {
        this.context = context;
        this.connectionUtil = ConnectionUtil.getInstance();
    }

    @Override
    public int getCount() {
        return members.size();
    }

    @Override
    public GroupMember getItem(int position) {
        return members.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.atom_ui_item_group_manage, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.m_name);
            holder.gravatar = (SimpleDraweeView) convertView.findViewById(R.id.m_gravatar);
            holder.cb_to_delete = (CheckBox) convertView.findViewById(R.id.cb_to_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.cb_to_delete.setVisibility(View.VISIBLE);
        holder.cb_to_delete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedMembers.add(members.get(position));
                } else {
                    selectedMembers.remove(members.get(position));
                }
            }
        });
        if (selectedMembers.contains(members.get(position))) {
            holder.cb_to_delete.setChecked(true);
        } else {
            holder.cb_to_delete.setChecked(false);
        }
        final GroupMember occupant = getItem(position);
        if (!TextUtils.isEmpty(occupant.getMemberId())) {
            //// TODO: 2017/9/18 拿名片
            final ViewHolder finalHolder = holder;
            connectionUtil.getUserCard(occupant.getMemberId(), new IMLogicManager.NickCallBack() {
                @Override
                public void onNickCallBack(Nick nick) {
                    if(nick!=null){
                        finalHolder.name.setText(nick.getName());
                        if (gravatarHandler != null ) {
                            gravatarHandler.requestGravatarEvent(occupant.getMemberId(),nick.getHeaderSrc(), finalHolder.gravatar);
                        }
                    }else{
                        finalHolder.name.setText(occupant.getName());
                    }

                }
            },false,false);
//            ProfileUtils.loadNickName(occupant.getJid(), holder.name, true);
            holder.gravatar.setVisibility(View.VISIBLE);
            holder.cb_to_delete.setVisibility(View.VISIBLE);
        } else {
            // TODO: 2017/9/18 拿名片
            holder.name.setText(members.get(position).getName());
            holder.gravatar.setVisibility(View.GONE);
            holder.cb_to_delete.setVisibility(View.GONE);
        }
        //自己和同级的，或者大于自己的隐藏
        if(myLevel<Integer.parseInt(occupant.getAffiliation())){
            holder.cb_to_delete.setVisibility(View.VISIBLE);
        }else{
            holder.cb_to_delete.setVisibility(View.GONE);
        }


        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (orgMembers == null)
                    return null;

                if (TextUtils.isEmpty(constraint)) {
                    results.count = orgMembers.size();
                    results.values = orgMembers;
                    return results;
                }
                List<GroupMember> resultValue = new ArrayList<>();

                for (int i = 0; i < orgMembers.size(); i++) {
                    GroupMember item = orgMembers.get(i);
                    if ((item.getFuzzy() != null && item.getFuzzy().contains(constraint)) ||
                            (item.getName() != null && item.getName().contains(constraint))) {
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
                    members = (List<GroupMember>) results.values;
                    notifyDataSetChanged();
                }
            }
        };
        return filter;
    }

    public class ViewHolder {
        public TextView name;
        public SimpleDraweeView gravatar;
        public CheckBox cb_to_delete;
    }

    public interface GravatarHandler {
        void requestGravatarEvent(final String jid,final String imageSrc, final SimpleDraweeView view);
    }
}
