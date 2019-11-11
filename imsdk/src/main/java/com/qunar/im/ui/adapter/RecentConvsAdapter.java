package com.qunar.im.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.ui.util.FacebookImageUtil;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.module.RecentConversation;
import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.base.util.ChatTextHelper;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DateTimeUtils;
import com.qunar.im.base.util.InternDatas;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.ui.util.ProfileUtils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.ui.R;
import com.qunar.im.ui.util.ResourceUtils;
import com.qunar.im.ui.view.IconView;
import com.qunar.im.ui.view.recentView.ChatRender;
import com.qunar.im.ui.view.recentView.CommonHolderView;
import com.qunar.im.ui.view.recentView.ConsultRender;
import com.qunar.im.ui.view.recentView.DefaultRender;
import com.qunar.im.ui.view.recentView.FriendRequestRender;
import com.qunar.im.ui.view.recentView.GroupRender;
import com.qunar.im.ui.view.recentView.HeadLineRender;
import com.qunar.im.ui.view.recentView.IRecentRender;
import com.qunar.im.ui.view.recentView.RobotRender;
import com.qunar.im.utils.QtalkStringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by xinbo.wang on 2015/2/9.
 */
public class RecentConvsAdapter extends BaseAdapter {
    private static final String TAG = RecentConvsAdapter.class.getSimpleName();
    //核心连接管理类
    private ConnectionUtil connectionUtil;
    List<RecentConversation> recentConversationList;
    SparseArray<IRecentRender> renderMap = new SparseArray<IRecentRender>();
    IRecentRender defaultRender = new DefaultRender();
    Context context;
    private static String defaultMucImage = QtalkNavicationService.getInstance().getInnerFiltHttpHost() + "/file/v2/download/perm/2227ff2e304cb44a1980e9c1a3d78164.png";
    private static String defaultUserImage = QtalkNavicationService.getInstance().getInnerFiltHttpHost() + "/file/v2/download/perm/3ca05f2d92f6c0034ac9aee14d341fc7.png";
    //是否从db层从新获取数据
    private boolean toDB;
    private boolean enforce;

    public RecentConvsAdapter(Context cxt) {
        connectionUtil = ConnectionUtil.getInstance();

        context = cxt;
        renderMap.append(ConversitionType.MSG_TYPE_CHAT, new ChatRender());
        renderMap.append(ConversitionType.MSG_TYPE_COLLECTION,new ChatRender());
        renderMap.append(ConversitionType.MSG_TYPE_GROUP, new GroupRender());
        renderMap.append(ConversitionType.MSG_TYPE_HEADLINE, new HeadLineRender());
        IRecentRender robot = new RobotRender();
        renderMap.append(ConversitionType.MSG_TYPE_IMPORTANT_SUBSCRIPT, robot);
        renderMap.append(ConversitionType.MSG_TYPE_SUBSCRIPT, robot);
        renderMap.append(ConversitionType.MSG_TYPE_FRIENDS_REQUEST, new FriendRequestRender());
        IRecentRender consult = new ConsultRender();
        renderMap.append(ConversitionType.MSG_TYPE_CONSULT, consult);
        renderMap.append(ConversitionType.MSG_TYPE_CONSULT_SERVER, consult);
    }

    public void setRecentConversationList(List<RecentConversation> recentConversationList) {
        this.recentConversationList = recentConversationList;
        this.notifyDataSetChanged();
    }

    public int getFirstUnreadIndex(){
        for(int i = 0; i < getCount();i++){
            RecentConversation item = getItem(i);
            if(item.getUnread_msg_cont() > 0 && item.getRemind() != 1){
                return i;
            }
        }
        return 0;
    }

//    public void setRecentConversationList(List<RecentConversation> recentConversationList,boolean isFirst) {
//        this.isFirst = isFirst;
//        this.recentConversationList = recentConversationList;
//        this.notifyDataSetChanged();
//    }

//    public List<RecentConversation> getRecentConversationList() {
//        return recentConversationList;
//    }

    @Override
    public int getCount() {
        if (recentConversationList == null)
            return 0;
        return recentConversationList.size();
    }

    @Override
    public RecentConversation getItem(int position) {
        if (recentConversationList == null || position >= recentConversationList.size())
            return null;
        return recentConversationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final CommonHolderView holder;
        if (convertView == null) {
            holder = new CommonHolderView();
            convertView = LayoutInflater.from(context).inflate(R.layout.atom_ui_rosteritem, parent, false);

            holder.mNameTextView = (TextView) convertView.findViewById(android.R.id.text1);

            holder.mConsultImageView = (ImageView) convertView.findViewById(R.id.imageview_type);
//            holder.mConsultTextView = (TextView) convertView.findViewById(R.id.textview_type);
            holder.mImageView = (SimpleDraweeView) convertView.findViewById(R.id.conversation_gravantar);
            holder.mTimeTextView = (TextView) convertView.findViewById(R.id.textview_time);
            holder.mLastMsgTextView = (TextView) convertView.findViewById(android.R.id.text2);
            holder.mNotifyTextView = (TextView) convertView.findViewById(R.id.textView_new_msg);
            holder.mNotifyIconview = (IconView) convertView.findViewById(R.id.conversation_close_notifacation);
            holder.not_remind = (IconView) convertView.findViewById(R.id.not_remind);
            holder.mCenterLayout = (LinearLayout) convertView.findViewById(R.id.centerLayout);
            holder.mTopIcon = (ImageView) convertView.findViewById(R.id.atom_ui_top_icon);
            holder.mConverLineView = convertView.findViewById(R.id.atom_ui_conversation_line);
            convertView.setTag(holder);
        } else {
            holder = (CommonHolderView) convertView.getTag();
        }
        holder.mConverLineView.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
//        int fontSizeMode = com.qunar.im.protobuf.common.CurrentPreference.getInstance().getFontSizeMode();
//        float text1FontSize = context.getResources().getDimensionPixelSize(R.dimen.atom_ui_text_size_large);
//        float textViewTimeFontSize = context.getResources().getDimensionPixelSize(R.dimen.atom_ui_text_size_smaller);
//        float text2FontSize = context.getResources().getDimensionPixelSize(R.dimen.atom_ui_text_size_small);
//        switch (fontSizeMode) {
//            case 1://small font size
//                text1FontSize -= ResourceUtils.getFontSizeIntervalPX(context);
//                textViewTimeFontSize -= ResourceUtils.getFontSizeIntervalPX(context);
//                text2FontSize -= ResourceUtils.getFontSizeIntervalPX(context);
//                break;
//            case 2://middle font size
//                break;
//            case 3://big font size
//                text1FontSize += ResourceUtils.getFontSizeIntervalPX(context);
//                textViewTimeFontSize += ResourceUtils.getFontSizeIntervalPX(context);
//                text2FontSize += ResourceUtils.getFontSizeIntervalPX(context);
//                break;
//        }
//        holder.mNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, text1FontSize);
//        holder.mTimeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textViewTimeFontSize);
////        holder.mConsultTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textViewTimeFontSize);
//        holder.mLastMsgTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, text2FontSize);
        final RecentConversation data = getItem(position);



        if (data.getTop() > 0) {
            //这个判断是用来判断如果是置顶情况下, 要更改颜色 很恶心的一个操作方法差评
//            holder.mImageView.setVisibility(View.GONE);
//            convertView.setBackgroundResource(R.color.atom_ui_dark_white);
//            holder.mImageView = (SimpleDraweeView) convertView.findViewById(R.id.conversation_gravantar_top);
//            holder.mImageView.setVisibility(View.VISIBLE);
            holder.mTopIcon.setVisibility(View.VISIBLE);
        } else {
//            holder.mImageView.setVisibility(View.GONE);
//            convertView.setBackgroundResource(R.color.atom_ui_white);
//            holder.mImageView = (SimpleDraweeView) convertView.findViewById(R.id.conversation_gravantar);
//            holder.mImageView.setVisibility(View.VISIBLE);
            holder.mTopIcon.setVisibility(View.GONE);
        }

        if (data.getConversationType() == ConversitionType.MSG_TYPE_GROUP ) {
            connectionUtil.getMucCard(data.getId(), nick -> {
                data.setNick(nick);
                showCard(nick,holder,data.getId(),true);
                if (data.isChan().indexOf("send") != -1) {
                    holder.mConsultImageView.setVisibility(View.GONE);
                }

            },data.isToNetWork(), data.isToDB());

            connectionUtil.getUserCard(data.getLastFrom(), new IMLogicManager.NickCallBack() {
                @Override
                public void onNickCallBack(Nick nick) {
                    if (nick != null) {
                        data.setFullname((TextUtils.isEmpty(nick.getName()) ? nick.getXmppId() : nick.getName()));
                    }


                }
            }, false, false);

        } else if (data.getConversationType() == ConversitionType.MSG_TYPE_CHAT) {
            final String jid = data.getId();//consult消息 jid为realuser
            connectionUtil.getUserCard(jid, new IMLogicManager.NickCallBack() {
                @Override
                public void onNickCallBack(Nick nick) {
                    data.setNick(nick);
                    showCard(nick,holder,jid,false);
                    if (data.isChan().indexOf("send") != -1) {
                        holder.mConsultImageView.setVisibility(View.GONE);
                    }

                }
            }, data.isToNetWork(), data.isToDB());
        } else if (data.getConversationType() == ConversitionType.MSG_TYPE_CONSULT
                || data.getConversationType() == ConversitionType.MSG_TYPE_CONSULT_SERVER) {
            final String jid = data.getRealUser();//consult消息 jid为realuser
            connectionUtil.getUserCard(jid, new IMLogicManager.NickCallBack() {
                @Override
                public void onNickCallBack(final Nick nick) {
                    if (nick != null) {
//                                Logger.i(new Gson().toJson(nick));
                        ProfileUtils.displayGravatarByImageSrc((Activity) context, TextUtils.isEmpty(nick.getHeaderSrc()) ? defaultUserImage : nick.getHeaderSrc(), holder.mImageView,
                                context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size), context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
                        //设置会话名,先用会话id显示
                        if (data.getConversationType() == 4) {
                            holder.mNameTextView.setText(nick.getShowName());
                        } else if (data.getConversationType() == 5) {
                            connectionUtil.getUserCard(data.getId(), new IMLogicManager.NickCallBack() {
                                @Override
                                public void onNickCallBack(Nick nick1) {
                                    holder.mNameTextView.setText(nick.getShowName() + "-" + nick1.getShowName());
                                }
                            }, false, false);
                        } else {
                            holder.mNameTextView.setText(nick.getName());
                        }

                    } else {
                        holder.mNameTextView.setText(jid);
                        ProfileUtils.displayGravatarByImageSrc((Activity) context, defaultUserImage, holder.mImageView,
                                context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size), context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
                    }

                    if (data.getConversationType() == ConversitionType.MSG_TYPE_CONSULT) {
                        holder.mConsultImageView.setVisibility(View.GONE);
                    } else if (data.getConversationType() == ConversitionType.MSG_TYPE_CONSULT_SERVER) {
                        holder.mConsultImageView.setVisibility(View.VISIBLE);
                    }

                }
            }, data.isToNetWork(), data.isToDB());
        }else if(data.getConversationType() == ConversitionType.MSG_TYPE_COLLECTION){
            final String jid = data.getId();
            connectionUtil.getUserCard(jid, new IMLogicManager.NickCallBack() {
                @Override
                public void onNickCallBack(Nick nick) {
                    showCard(nick,holder,jid,false);
                    holder.mConsultImageView.setVisibility(View.GONE);

                }
            }, data.isToNetWork(), data.isToDB());
        }else if(data.getConversationType() == ConversitionType.MSG_TYPE_SUBSCRIPT){
            String id = data.getId();
            if(id.startsWith("rbt-system")){
                holder.mNameTextView.setText("系统消息");
                holder.mConsultImageView.setVisibility(View.GONE);
                FacebookImageUtil.loadFromResource(R.drawable.atom_ui_rbt_system, holder.mImageView);
            }else if(id.startsWith("rbt-notice")){
                holder.mNameTextView.setText("公告消息");
                holder.mConsultImageView.setVisibility(View.GONE);
                FacebookImageUtil.loadFromResource(R.drawable.atom_ui_rbt_notice, holder.mImageView);
            }else if(id.startsWith("rbt-qiangdan")){
                holder.mNameTextView.setText("抢单消息");
                holder.mConsultImageView.setVisibility(View.GONE);
                FacebookImageUtil.loadFromResource(R.drawable.atom_ui_robot_qiangdan, holder.mImageView);
            }
        }else if(data.getConversationType() == ConversitionType.MSG_TYPE_HEADLINE){
            holder.mNameTextView.setText("系统消息");
            holder.mConsultImageView.setVisibility(View.GONE);
            FacebookImageUtil.loadFromResource(R.drawable.atom_ui_rbt_system, holder.mImageView);
        }
        holder.mTimeTextView.setText(DateTimeUtils.getTimeForSeesionAndChat(data.getLastMsgTime(), false));
        showMessage(data, holder);

        return convertView;
    }

    private void showCard(Nick nick, CommonHolderView holder, String jid,boolean isGroup){
        if (nick != null) {
            if(TextUtils.isEmpty(nick.getMark())){
                holder.mNameTextView.setText(nick.getName());
            }else{
                holder.mNameTextView.setText(nick.getMark());
            }

            ProfileUtils.displayGravatarByImageSrc((Activity) context, TextUtils.isEmpty(nick.getHeaderSrc()) ? (isGroup ? defaultMucImage : defaultUserImage) : nick.getHeaderSrc(), holder.mImageView,
                    context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size), context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
        } else {
            holder.mNameTextView.setText(jid);
            ProfileUtils.displayGravatarByImageSrc((Activity) context, defaultUserImage, holder.mImageView,
                    context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size), context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
        }
    }

    private void handleMessage(final RecentConversation data, final CommonHolderView holder) {
        IRecentRender recentRender = renderMap.get(data.getConversationType(), defaultRender);
        recentRender.render(holder, data, context);
    }

    private void showMessage(RecentConversation data, CommonHolderView holder) {
        String latestMsg = ChatTextHelper.showContentType(data.getLastMsg(), data.getMsgType());
        if (data.getRemind() > 0) {
            holder.mNotifyIconview.setVisibility(View.VISIBLE);
            if (data.getUnread_msg_cont() > 0) {
                holder.mNotifyTextView.setBackgroundResource(R.drawable.atom_ui_unread_circle_bg);
                holder.mNotifyTextView.setVisibility(View.GONE);
                holder.not_remind.setVisibility(View.VISIBLE);
            } else {
                holder.mNotifyTextView.setVisibility(View.GONE);
                holder.not_remind.setVisibility(View.INVISIBLE);
            }
        } else {
            if(data.getUnread_msg_cont() > 0) {
                holder.mNotifyIconview.setVisibility(View.GONE);
                if (data.getUnread_msg_cont() < 100) {
                    holder.mNotifyTextView.setText(String.valueOf(data.getUnread_msg_cont()));
                } else {
                    //todo

                    holder.mNotifyTextView.setText("99+");
//                    holder.mNotifyTextView.setText(String.valueOf(data.getUnread_msg_cont()));


                }
                holder.mNotifyTextView.setBackgroundResource(R.drawable.atom_ui_unread_circle_bg);
                holder.mNotifyTextView.setVisibility(View.VISIBLE);
                holder.not_remind.setVisibility(View.GONE);
            } else {
                holder.mNotifyIconview.setVisibility(View.INVISIBLE);
                holder.mNotifyTextView.setVisibility(View.GONE);
                holder.not_remind.setVisibility(View.INVISIBLE);
            }
        }
        //处理消息
        data.setLastMsg(latestMsg);
        handleMessage(data, holder);
        String draft = InternDatas.getDraft(QtalkStringUtils.parseBareJid(data.getId()) + QtalkStringUtils.parseBareJid(data.getRealUser()));
        if (!TextUtils.isEmpty(draft)) {
            //草稿不为空
            draft = ChatTextHelper.showDraftContent(draft);
            SpannableStringBuilder sb = new SpannableStringBuilder("[草稿] ");
            ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
            sb.setSpan(redSpan, 0, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.append(draft);
            holder.mLastMsgTextView.setText(sb);
        }
    }
}