package com.qunar.im.ui.adapter;

import android.app.Activity;
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
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.module.GroupMember;
import com.qunar.im.base.module.Nick;
import com.qunar.im.ui.util.ProfileUtils;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.ui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinbo.wang on 2015/2/11.
 */
public class MembersAdapter extends BaseAdapter implements Filterable {

    List<GroupMember> members = new ArrayList<>();
    List<GroupMember> orgMembers = new ArrayList<>();
    Context context;
    GravatarHandler gravatarHandler;

    public void setGravatarHandler(GravatarHandler gravatarHandler) {
        this.gravatarHandler = gravatarHandler;
    }

    public void setMembers(List<GroupMember> member)
    {
        this.members =member;
        orgMembers = member;
        GroupMember chatRoomMember = new GroupMember();
        chatRoomMember.setName("ALL");
        chatRoomMember.setMemberId("all");
        members.add(0,chatRoomMember);
        this.notifyDataSetChanged();
    }

    public MembersAdapter(Context context)
    {
        this.context = context;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.atom_ui_item_member, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.m_name);
            holder.gravatar = (SimpleDraweeView) convertView.findViewById(R.id.m_gravatar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final GroupMember occupant = getItem(position);
        if(position==0 && "all".equals(occupant.getMemberId()))
        {
            holder.name.setText(occupant.getName());
            String str = "res://"+context.getPackageName()+"/"+R.drawable.atom_ui_ic_my_chatroom;
            holder.gravatar.setImageURI(str,true);
        }
        else {
            final ViewHolder finalHolder = holder;
            ConnectionUtil.getInstance().getUserCard(occupant.getMemberId(), new IMLogicManager.NickCallBack() {
                @Override
                public void onNickCallBack(final Nick nick) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (nick != null && !TextUtils.isEmpty(nick.getHeaderSrc())) {
                                //old
//                                ProfileUtils.displayGravatarByImageSrc(nick.getXmppId(),nick.getHeaderSrc(),finalHolder.gravatar);
                                //new
                                ProfileUtils.displayGravatarByImageSrc((Activity) context, nick.getHeaderSrc(),finalHolder.gravatar,
                                        context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size), context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
//                                finalHolder.gravatar.setImageUrl(nick.getHeaderSrc(), true);
                                finalHolder.name.setText(TextUtils.isEmpty(nick.getMark()) ? nick.getName() : nick.getMark());
                            } else {
                                finalHolder.name.setText(occupant.getMemberId());
//                                holder.mImageView.getHierarchy().setPlaceholderImage(R.drawable.atom_ui_ic_my_chatroom);
                                String str = "res://"+context.getPackageName()+"/"+R.drawable.atom_ui_ic_my_chatroom;
                                finalHolder.gravatar.setImageURI(str,true);
                            }

                        }
                    });

                }
            }, false, false);
//            ProfileUtils.loadNickName(occupant.getMemberId(), holder.name, true);
        }
//        if(gravatarHandler != null)
//        {
//            gravatarHandler.requestGravatarEvent(occupant.getMemberId(),holder.gravatar);
//        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if(orgMembers == null)
                    return null;

                if(TextUtils.isEmpty(constraint))
                {
                    results.count = orgMembers.size();
                    results.values = orgMembers;
                    return results;
                }
                List<GroupMember> resultValue = new ArrayList<>();

                for(int i = 0;i<orgMembers.size();i++)
                {
                    GroupMember item = orgMembers.get(i);

                    if((item.getFuzzy()!=null&&item.getFuzzy().contains(constraint))||
                            (item.getName()!=null&&item.getName().contains(constraint)))
                    {
                        resultValue.add(item);
                    }
                }
                results.count = resultValue.size();
                results.values = resultValue;

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results !=null)
                {
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
    }

    public interface GravatarHandler
    {
         void requestGravatarEvent(final String jid, final SimpleDraweeView view);
    }
}
