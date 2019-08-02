package com.qunar.im.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.module.GroupMember;
import com.qunar.im.base.module.Nick;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.ui.R;
import com.qunar.im.base.module.ChatRoomMember;
import com.qunar.im.ui.util.ProfileUtils;
import com.qunar.im.utils.QtalkStringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinbo.wang on 2015/5/14.
 */
public class MembersGridAdapter extends BaseAdapter {

    List<GroupMember> members = new ArrayList<>();
    Context context;
    GravatarHandler gravatarHandler;
    //核心连接管理类
    private ConnectionUtil connectionUtil;


    private static final String ADD_MEMBER = "ADD";
    private static final String DEL_MEMBER = "DEL";

    AddNewMemberListener addNewMemberListener;
    DelMemberListener delMemberListener;
    CheckeMemberInfoListener checkeMemberInfoListener;

    public int myPower;
    //是否强制更新
    public boolean enforce;

    public void setAddNewMemberListener(AddNewMemberListener addNewMemberListener) {
        this.addNewMemberListener = addNewMemberListener;
    }

    public void setCheckeMemberInfoListener(CheckeMemberInfoListener checkeMemberInfoListener) {
        this.checkeMemberInfoListener = checkeMemberInfoListener;
    }

    public void setDelMemberListener(DelMemberListener delMemberListener) {
        this.delMemberListener = delMemberListener;
    }

    public MembersGridAdapter(Context context) {
        this.context = context;
        connectionUtil = ConnectionUtil.getInstance();
    }


    public void setGravatarHandler(GravatarHandler gravatarHandler) {
        this.gravatarHandler = gravatarHandler;
    }

    public void setMembers(List<GroupMember> member, int power,boolean enforce) {
        this.enforce = enforce;
        this.myPower = power;
        this.members = member;
        GroupMember addMember = new GroupMember();
        addMember.setMemberId(ADD_MEMBER);
        this.members.add(addMember);
        if (myPower == GroupMember.ADMIN ||
                myPower == GroupMember.OWNER) {
            GroupMember delMember = new GroupMember();
            delMember.setMemberId(DEL_MEMBER);
            this.members.add(delMember);
        }
    }

    @Override
    public int getCount() { //每排放4个头像
        if (this.members == null) return 0;
        return ((this.members.size() >> 2) + (members.size() % 4 > 0 ? 1 : 0));
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
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.atom_ui_item_member_grid_adapter, null);
            holder = new ViewHolder();
            holder.ll_vertical_one = (LinearLayout) convertView.findViewById(R.id.ll_vertical_one);
            holder.ll_vertical_two = (LinearLayout) convertView.findViewById(R.id.ll_vertical_two);
            holder.ll_vertical_three = (LinearLayout) convertView.findViewById(R.id.ll_vertical_three);
            holder.ll_vertical_four = (LinearLayout) convertView.findViewById(R.id.ll_vertical_four);
            holder.m_name1 = (TextView) convertView.findViewById(R.id.m_name1);
            holder.m_name2 = (TextView) convertView.findViewById(R.id.m_name2);
            holder.m_name3 = (TextView) convertView.findViewById(R.id.m_name3);
            holder.m_name4 = (TextView) convertView.findViewById(R.id.m_name4);
            holder.m_gravatar1 = (SimpleDraweeView) convertView.findViewById(R.id.m_gravatar1);
            holder.m_gravatar2 = (SimpleDraweeView) convertView.findViewById(R.id.m_gravatar2);
            holder.m_gravatar3 = (SimpleDraweeView) convertView.findViewById(R.id.m_gravatar3);
            holder.m_gravatar4 = (SimpleDraweeView) convertView.findViewById(R.id.m_gravatar4);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.ll_vertical_one.setVisibility(View.VISIBLE);
        holder.ll_vertical_two.setVisibility(View.VISIBLE);
        holder.ll_vertical_three.setVisibility(View.VISIBLE);
        holder.ll_vertical_four.setVisibility(View.VISIBLE);

        bindData(holder, position);

        return convertView;
    }

    public interface GravatarHandler {
        void requestGravatarEvent(final String jid, final SimpleDraweeView view);
    }

    public class ViewHolder {
        public LinearLayout ll_vertical_one;
        public LinearLayout ll_vertical_two;
        public LinearLayout ll_vertical_three;
        public LinearLayout ll_vertical_four;
        public TextView m_name1;
        public SimpleDraweeView m_gravatar1;
        public TextView m_name2;
        public SimpleDraweeView m_gravatar2;
        public TextView m_name3;
        public SimpleDraweeView m_gravatar3;
        public TextView m_name4;
        public SimpleDraweeView m_gravatar4;
    }

    //数据源绑定
    private void bindData(final ViewHolder holder, final int position) {
        //加减人image添加ContentDescription为了appium自动化测试
        /**
         * 左边第1个
         */
        if (position * 4 < members.size()) {
            final GroupMember member = members.get(position * 4);
            switch (member.getMemberId()) {
                case ADD_MEMBER:
                    holder.m_name1.setText("");
                    holder.m_gravatar1.setImageResource(R.drawable.atom_ui_plus);
                    holder.m_gravatar1.setContentDescription("group_add_members");
                    holder.m_name1.setTag(null);
                    if (addNewMemberListener != null) {
                        holder.ll_vertical_one.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                addNewMemberListener.onClick();
                            }
                        });
                    }
                    break;
                case DEL_MEMBER:
                    holder.m_name1.setText("");
                    holder.m_gravatar1.setImageResource(R.drawable.atom_ui_ic_minus);
                    holder.m_gravatar1.setContentDescription("group_delete_members");
                    holder.m_name1.setTag(null);
                    if (delMemberListener != null) {
                        holder.ll_vertical_one.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                delMemberListener.onClick();
                            }
                        });
                    }
                    break;
                default:
                    connectionUtil.getUserCard(member.getMemberId(), new IMLogicManager.NickCallBack() {
                        @Override
                        public void onNickCallBack(final Nick nick) {
                            ((Activity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(nick!=null){
                                        //old
//                                        ProfileUtils.displayGravatarByImageSrc(nick.getXmppId(),nick.getHeaderSrc(),holder.m_gravatar1);
                                        //new
                                        ProfileUtils.displayGravatarByImageSrc((Activity) context, nick.getHeaderSrc(), holder.m_gravatar1,
                                                context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size), context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
                                        holder.m_name1.setText(nick.getName());
//                                        holder.m_gravatar3.setImageUrl(nick.getHeaderSrc(),true);
                                    }else{
                                        //old
//                                        ProfileUtils.displayGravatarByImageSrc(member.getMemberId(),headerSrc,holder.m_gravatar1);
                                        //new
                                        ProfileUtils.displayGravatarByImageSrc((Activity) context, nick.getHeaderSrc(), holder.m_gravatar1,
                                                context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size), context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
                                        holder.m_name1.setText(member.getMemberId());
                                    }
                                }
                            });
                        }
                    },enforce,false);
//                    //这里去更新群名片
//                    updateNickname(holder.m_name1, member);
//                    //这里去加载头像
//                    loadView(holder.m_gravatar1, member.getJid());
                    if (checkeMemberInfoListener != null) {
                        holder.ll_vertical_one.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                checkeMemberInfoListener.onClick(member);
                            }
                        });
                    }
                    break;
            }
        } else {
            holder.ll_vertical_one.setVisibility(View.INVISIBLE);
            holder.ll_vertical_one.setOnClickListener(null);
        }
        /**
         * 左边第2个
         */
        if (position * 4 + 1 < members.size()) {
            final GroupMember member = members.get(position * 4 + 1);
            switch (member.getMemberId()) {
                case ADD_MEMBER:
                    holder.m_name2.setText("");
                    holder.m_gravatar2.setImageResource(R.drawable.atom_ui_plus);
                    holder.m_gravatar2.setContentDescription("group_add_members");
                    holder.m_name2.setTag(null);
                    if (addNewMemberListener != null) {
                        holder.ll_vertical_two.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                addNewMemberListener.onClick();
                            }
                        });
                    }
                    break;
                case DEL_MEMBER:
                    holder.m_name2.setText("");
                    holder.m_gravatar2.setImageResource(R.drawable.atom_ui_ic_minus);
                    holder.m_gravatar2.setContentDescription("group_delete_members");
                    holder.m_name2.setTag(null);
                    if (delMemberListener != null) {
                        holder.ll_vertical_two.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                delMemberListener.onClick();
                            }
                        });
                    }
                    break;
                default:
                    connectionUtil.getUserCard(member.getMemberId(), new IMLogicManager.NickCallBack() {
                        @Override
                        public void onNickCallBack(final Nick nick) {
                            ((Activity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(nick!=null){
                                        //old
//                                        ProfileUtils.displayGravatarByImageSrc(nick.getXmppId(),nick.getHeaderSrc(),holder.m_gravatar2);
                                        //new
                                        ProfileUtils.displayGravatarByImageSrc((Activity) context, nick.getHeaderSrc(), holder.m_gravatar2,
                                                context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size), context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
                                        holder.m_name2.setText(nick.getName());
//                                        holder.m_gravatar3.setImageUrl(nick.getHeaderSrc(),true);
                                    }else{
                                        //old
//                                        ProfileUtils.displayGravatarByImageSrc(member.getMemberId(),headerSrc,holder.m_gravatar2);
                                        //new
                                        ProfileUtils.displayGravatarByImageSrc((Activity) context, nick.getHeaderSrc(), holder.m_gravatar2,
                                                context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size), context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
                                        holder.m_name2.setText(member.getMemberId());
                                    }
                                }
                            });
                        }
                    },enforce,false);
                    if (checkeMemberInfoListener != null) {
                        holder.ll_vertical_two.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                checkeMemberInfoListener.onClick(member);
                            }
                        });
                    }
                    break;
            }
        } else {
            holder.ll_vertical_two.setVisibility(View.INVISIBLE);
            holder.ll_vertical_two.setOnClickListener(null);
        }
        /**
         * 左边第3个
         */
        if (position * 4 + 2 < members.size()) {
            final GroupMember member = members.get(position * 4 + 2);
            switch (member.getMemberId()) {
                case ADD_MEMBER:
                    holder.m_name3.setText("");
                    holder.m_gravatar3.setImageResource(R.drawable.atom_ui_plus);
                    holder.m_gravatar3.setContentDescription("group_add_members");
                    holder.m_name3.setTag(null);
                    if (addNewMemberListener != null) {
                        holder.ll_vertical_three.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                addNewMemberListener.onClick();
                            }
                        });
                    }
                    break;
                case DEL_MEMBER:
                    holder.m_name3.setText("");
                    holder.m_gravatar3.setImageResource(R.drawable.atom_ui_ic_minus);
                    holder.m_gravatar3.setContentDescription("group_delete_members");
                    holder.m_name3.setTag(null);
                    if (delMemberListener != null) {
                        holder.ll_vertical_three.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                delMemberListener.onClick();
                            }
                        });
                    }
                    break;
                default:
                    connectionUtil.getUserCard(member.getMemberId(), new IMLogicManager.NickCallBack() {
                        @Override
                        public void onNickCallBack(final Nick nick) {
                            ((Activity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(nick!=null){
                                        //old
//                                        ProfileUtils.displayGravatarByImageSrc(nick.getXmppId(),nick.getHeaderSrc(),holder.m_gravatar3);
                                        //new
                                        ProfileUtils.displayGravatarByImageSrc((Activity) context, nick.getHeaderSrc(), holder.m_gravatar3,
                                                context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size), context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
                                        holder.m_name3.setText(nick.getName());
//                                        holder.m_gravatar3.setImageUrl(nick.getHeaderSrc(),true);
                                    }else{
                                        //old
//                                        ProfileUtils.displayGravatarByImageSrc(member.getMemberId(),headerSrc,holder.m_gravatar3);
                                        //new
                                        ProfileUtils.displayGravatarByImageSrc((Activity) context, nick.getHeaderSrc(), holder.m_gravatar3,
                                                context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size), context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
                                        holder.m_name3.setText(member.getMemberId());
                                    }

                                }
                            });
                        }
                    },enforce,false);
                    if (checkeMemberInfoListener != null) {
                        holder.ll_vertical_three.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                checkeMemberInfoListener.onClick(member);
                            }
                        });
                    }
                    break;
            }
        } else {
            holder.ll_vertical_three.setVisibility(View.INVISIBLE);
            holder.ll_vertical_three.setOnClickListener(null);
        }
        /**
         * 左边第4个
         */
        if (position * 4 + 3 < members.size()) {
            final GroupMember member = members.get(position * 4 + 3);
            switch (member.getMemberId()) {
                case ADD_MEMBER:
                    holder.m_name4.setText("");
                    holder.m_gravatar4.setImageResource(R.drawable.atom_ui_plus);
                    holder.m_gravatar4.setContentDescription("group_add_members");
                    holder.m_name4.setTag(null);
                    if (addNewMemberListener != null) {
                        holder.ll_vertical_four.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                addNewMemberListener.onClick();
                            }
                        });
                    }
                    break;
                case DEL_MEMBER:
                    holder.m_name4.setText("");
                    holder.m_gravatar4.setImageResource(R.drawable.atom_ui_ic_minus);
                    holder.m_gravatar4.setContentDescription("group_delete_members");
                    holder.m_name4.setTag(null);
                    if (delMemberListener != null) {
                        holder.ll_vertical_four.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                delMemberListener.onClick();
                            }
                        });
                    }
                    break;
                default:
                    connectionUtil.getUserCard(member.getMemberId(), new IMLogicManager.NickCallBack() {
                        @Override
                        public void onNickCallBack(final Nick nick) {
                            ((Activity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(nick!=null){
                                        //old
//                                        ProfileUtils.displayGravatarByImageSrc(nick.getXmppId(),nick.getHeaderSrc(),holder.m_gravatar4);
                                        //new
                                        ProfileUtils.displayGravatarByImageSrc((Activity) context, nick.getHeaderSrc(), holder.m_gravatar4,
                                                context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size), context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
                                        holder.m_name4.setText(nick.getName());
//                                        holder.m_gravatar3.setImageUrl(nick.getHeaderSrc(),true);
                                    }else{
                                        //old
//                                        ProfileUtils.displayGravatarByImageSrc(member.getMemberId(),headerSrc,holder.m_gravatar4);
                                        //new
                                        ProfileUtils.displayGravatarByImageSrc((Activity) context, nick.getHeaderSrc(), holder.m_gravatar4,
                                                context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size), context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
                                        holder.m_name4.setText(member.getMemberId());
                                    }
                                }
                            });
                        }
                    },enforce,false);
                    if (checkeMemberInfoListener != null) {
                        holder.ll_vertical_four.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                checkeMemberInfoListener.onClick(member);
                            }
                        });
                    }
                    break;
            }
        } else {
            holder.ll_vertical_four.setVisibility(View.INVISIBLE);
            holder.ll_vertical_four.setOnClickListener(null);
        }
    }

    public void loadView(SimpleDraweeView imageView, String jid) {
        if (gravatarHandler != null) {
            gravatarHandler.requestGravatarEvent(jid, imageView);

        }
    }

    /**
     * 更新昵称
     */
    public void updateNickname(final TextView tv, final ChatRoomMember chatRoomMember) {
        ProfileUtils.loadNickName(QtalkStringUtils.parseBareJid(chatRoomMember.getJid()), tv, true);
    }


    public interface CheckeMemberInfoListener {
        void onClick(GroupMember member);
    }

    public interface AddNewMemberListener {
        void onClick();
    }

    public interface DelMemberListener {
        void onClick();
    }
}




