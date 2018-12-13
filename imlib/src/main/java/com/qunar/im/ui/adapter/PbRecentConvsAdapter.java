package com.qunar.im.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.module.RecentConversation;
import com.qunar.im.base.util.DateTimeUtils;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.ui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hubin on 2017/8/20.
 */

public class PbRecentConvsAdapter extends BaseAdapter {

    //核心连接管理类
    private ConnectionUtil connectionUtil;
    //contxt
    private Context context;
    //会话列表数据
    private List<RecentConversation> recentConversationList = new ArrayList<>();
    //布局填充器
    private LayoutInflater layoutInflater;
    private NickCallBack nickCallBack;
    //是否从db层从新获取数据
    private boolean toDB;

    public PbRecentConvsAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        connectionUtil = ConnectionUtil.getInstance();

    }

    public void setNickCallBack(NickCallBack nickCallBack) {
        this.nickCallBack = nickCallBack;
    }

    public interface NickCallBack {
        void onNickCallBack(Nick nick, CommonViewHolder viewHolder);
    }

    @Override
    public int getCount() {
        return recentConversationList.size();
    }

    @Override
    public Object getItem(int position) {
        return recentConversationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position,  View convertView, ViewGroup parent) {
        CommonViewHolder vh = null;
        if (convertView == null) {
            vh = new CommonViewHolder();
            convertView = layoutInflater.inflate(R.layout.atom_ui_rosteritem, null, false);
            //群名
            vh.mNameTextView = (TextView) convertView.findViewById(android.R.id.text1);
            //咨询类型
            vh.mConsultTextView = (TextView) convertView.findViewById(R.id.textview_type);
            //会话头像
            vh.mImageView = (SimpleDraweeView) convertView.findViewById(R.id.conversation_gravantar);
            //最后消息时间
            vh.mTimeTextView = (TextView) convertView.findViewById(R.id.textview_time);
            //最后一条消息文本
            vh.mLastMsgTextView = (TextView) convertView.findViewById(android.R.id.text2);
            //未读消息条数
            vh.mNotifyTextView = (TextView) convertView.findViewById(R.id.textView_new_msg);
            //右侧框  看不出来有什么用
            vh.mCenterLayout = (LinearLayout) convertView.findViewById(R.id.centerLayout);
            convertView.setTag(vh);
        } else {
            vh = (CommonViewHolder) convertView.getTag();
        }
        final RecentConversation data = (RecentConversation) getItem(position);
        final CommonViewHolder finalVh = vh;


        vh.mNameTextView.setText("");
        //获得占位图引用
        //==1说明是个群聊 0是单聊if
//        GenericDraweeHierarchy hierarchy = vh.mImageView.getHierarchy();
        if (data.getConversationType() == 1) {
//            hierarchy.setPlaceholderImage(R.drawable.atom_ui_ic_publish_platform);
            connectionUtil.getMucCard( data.getId(), new IMLogicManager.NickCallBack() {
                @Override
                public void onNickCallBack(final Nick nick) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (nick != null && !TextUtils.isEmpty(nick.getHeaderSrc())) {
//                                Logger.i(new Gson().toJson(nick));
                                finalVh.mImageView.setImageURI(nick.getHeaderSrc(), true);
                                //设置会话名,先用会话id显示
                                finalVh.mNameTextView.setText(nick.getName());
                            } else {
                                finalVh.mNameTextView.setText(data.getId());
//                                finalVh.mImageView.getHierarchy().setPlaceholderImage(R.drawable.atom_ui_ic_publish_platform);
                                String str = "res://"+context.getPackageName()+"/"+R.drawable.atom_ui_ic_publish_platform;
                                finalVh.mImageView.setImageURI(str,true);
                            }
                        }
                    });

                }
            }, false, toDB);
        } else if (data.getConversationType() == 0) {
//            hierarchy.setPlaceholderImage(R.drawable.atom_ui_ic_my_chatroom);
            connectionUtil.getUserCard(data.getId(), new IMLogicManager.NickCallBack() {
                @Override
                public void onNickCallBack(final Nick nick) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (nick != null && !TextUtils.isEmpty(nick.getHeaderSrc())) {
//                                Logger.i(new Gson().toJson(nick));
                                finalVh.mImageView.setImageURI(nick.getHeaderSrc(), true);
                                //设置会话名,先用会话id显示
                                finalVh.mNameTextView.setText(nick.getName());
                            } else {
                                finalVh.mNameTextView.setText(data.getId());
//                                finalVh.mImageView.getHierarchy().setPlaceholderImage(R.drawable.atom_ui_ic_my_chatroom);
                                String str = "res://"+context.getPackageName()+"/"+R.drawable.atom_ui_ic_my_chatroom;
                                finalVh.mImageView.setImageURI(str,true);
                            }
                        }
                    });

                }
            }, false, toDB);
        }
//        if (data.getConversationType() == 1) {
//
//
//
////            connectionUtil.getMucCard(data.getId(), new IMLogicManager.NickCallBack() {
////                @Override
////                public void onNickCallBack(Nick nick) {
////                    nickCallBack.onNickCallBack(nick, finalVh);
////                }
////            });
//        } else if (data.getConversationType() == 0) {
////            connectionUtil.getUserCard(data.getId(), new IMLogicManager.NickCallBack() {
////                @Override
////                public void onNickCallBack(Nick nick) {
////                    nickCallBack.onNickCallBack(nick, finalVh);
////                }
////            });
//        }
        //如果是0 为单聊
//        if(data.getConversationType()==1){
        //设置单条image
//        vh.mImageView.setImageURI();
//        if(data.getNick()){
//        Uri uri = Uri.parse("https://qt.qunar.com/file/v2/download/avatar/e7d9a7b55c7b5353d090b9a058095e78.gif?name=e7d9a7b55c7b5353d090b9a058095e78.gif&file=file/e7d9a7b55c7b5353d090b9a058095e78.jpg&fileName=file/e7d9a7b55c7b5353d090b9a058095e78.jpg");
//        if(nick!=null&& !TextUtils.isEmpty(nick.getHeaderSrc())){
//            vh.mImageView.setImageUrl(nick.getHeaderSrc(),true);
//        }else{
//            vh.mImageView.setImageUrl("");
//        }
//        DraweeController draweeController =
//                Fresco.newDraweeControllerBuilder()
//                        .setUri(uri)
//                        .setAutoPlayAnimations(true) // 设置加载图片完成后是否直接进行播放
//                        .build();
//
//        vh.mImageView.setController(draweeController);
//            vh.mImageView.setImageUrl("https://qt.qunar.com/file/v2/download/avatar/e7d9a7b55c7b5353d090b9a058095e78.gif?name=e7d9a7b55c7b5353d090b9a058095e78.gif&file=file/e7d9a7b55c7b5353d090b9a058095e78.jpg&fileName=file/e7d9a7b55c7b5353d090b9a058095e78.jpg");
//        String i = "https://qt.qunar.com/file/v2/download/avatar/d8a02975acfef3eb7623191263da5ac6.jpg?name=d8a02975acfef3eb7623191263da5ac6.jpg&file=d8a02975acfef3eb7623191263da5ac6.jpg&fileName=d8a02975acfef3eb7623191263da5ac6.jpg";
//        vh.mImageView.setTag("");
//        FacebookImageUtil.loadWithCache(i, vh.mImageView, ImageRequest.ImageType.SMALL,
//               false);
//            vh.mImageView.setImageResource(R.drawable.atom_ui_ic_personal_info);
//        }else if(data.getConversationType() ==2){
        //设置群image
//            vh.mImageView.setImageResource(R.drawable.atom_ui_ic_chatroom_info);
//        }
        //设置会话名,先用会话id显示
//        vh.mNameTextView.setText(nick.getName());
        //设置最后一条消息时间
        vh.mTimeTextView.setText(DateTimeUtils.getTime(data.getLastMsgTime(), false));
        //设置最后一条消息文本
        vh.mLastMsgTextView.setText(data.getLastMsg());
        //设置未读消息条数
        int unMsgCount = data.getUnread_msg_cont();
        if (unMsgCount > 0) {
            //如果消息数大于0
            vh.mNotifyTextView.setVisibility(View.VISIBLE);
            if (unMsgCount > 99) {
                //如果消息树大于99 就设置为99+
                vh.mNotifyTextView.setText("99+");
            } else {
                //如果不是,显示实际消息条数
                vh.mNotifyTextView.setText(unMsgCount + "");
            }
        } else {
            //如果没有未读消息不显示红点
            vh.mNotifyTextView.setVisibility(View.GONE);
        }
        return convertView;
    }


    //viewholder
    public class CommonViewHolder {
        //会话列表头像
        public SimpleDraweeView mImageView;
        public TextView mNameTextView, mTimeTextView, mLastMsgTextView, mNotifyTextView, mConsultTextView;
        public LinearLayout mCenterLayout;
    }

    //设置数据ji
    public void setRecentConversationList(List<RecentConversation> list, boolean toDB) {
        this.recentConversationList = list;
        this.toDB = toDB;
        this.notifyDataSetChanged();

    }


}
