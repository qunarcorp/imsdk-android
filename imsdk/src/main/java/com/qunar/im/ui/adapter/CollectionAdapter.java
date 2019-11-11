package com.qunar.im.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.qunar.im.base.module.MultiItemEntity;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.base.util.ChatTextHelper;
import com.qunar.im.base.util.DateTimeUtils;
import com.qunar.im.ui.util.ProfileUtils;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.AccountSwitchActivity;
import com.qunar.im.ui.activity.CollectionChatActivity;
import com.qunar.im.ui.activity.PbChatActivity;
import com.qunar.im.base.module.CollectionConvItemDate;
import com.qunar.im.base.module.CollectionUserDate;
import com.qunar.im.ui.view.recyclerview.BaseMultiItemQuickAdapter;
import com.qunar.im.ui.view.recyclerview.BaseViewHolder;
import com.qunar.im.utils.QtalkStringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.qunar.im.base.module.CollectionAdapterConstant.TYPE_LEVEL_0;
import static com.qunar.im.base.module.CollectionAdapterConstant.TYPE_LEVEL_1;

/**
 * Created by hubin on 2017/11/21.
 */

public class CollectionAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {


    private Context context;

    public Map<Integer,Boolean> map;


    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public CollectionAdapter(List<MultiItemEntity> data, Context context) {
        super(data);
        map = new TreeMap<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs.compareTo(lhs);
            }
        });
        this.context = context;
        addItemType(TYPE_LEVEL_0, R.layout.atom_ui_collection_user_item);
        addItemType(TYPE_LEVEL_1, R.layout.atom_ui_rosteritem_collection);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            case TYPE_LEVEL_0:

                final CollectionUserDate lv0 = (CollectionUserDate) item;
                ConnectionUtil.getInstance().getCollectionUserCard(lv0.getUserId(), new IMLogicManager.NickCallBack() {
                    @Override
                    public void onNickCallBack(Nick nick) {
                        helper.setText(R.id.collection_user_name, nick.getName()+ "("+ QtalkStringUtils.parseDomain(nick.getXmppId())+")");
                        ProfileUtils.displayGravatarByImageSrc(((Activity) context), nick.getHeaderSrc(),
                                (SimpleDraweeView) helper.getView(R.id.collection_gravantar),
                                context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size),
                                context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
                    }
                }, false, false);


                if (lv0.getUnRead() > 0) {
                    helper.getView(R.id.collection_textView_new_msg).setVisibility(View.VISIBLE);
                    if (lv0.getUnRead() > 99) {
                        helper.setText(R.id.collection_textView_new_msg, "99+");
                    } else {
                        helper.setText(R.id.collection_textView_new_msg, lv0.getUnRead() + "");
                    }
                } else {
                    helper.getView(R.id.collection_textView_new_msg).setVisibility(View.GONE);
                }

                helper.getView(R.id.switch_account).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, AccountSwitchActivity.class);
                        context.startActivity(intent);
                    }
                });
                helper.setImageResource(R.id.iv,lv0.isExpanded()?R.drawable.atom_ui_ic_bottom_collection:R.drawable.atom_ui_ic_right_collection);
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = helper.getAdapterPosition();
//                        Log.d(TAG, "Level 0 item pos: " + pos);
                        if (lv0.isExpanded()) {
                            collapse(pos);
                            map.put(lv0.getPosition(),false);
                        } else {
//                            if (pos % 3 == 0) {
//                                expandAll(pos, false);
//                            } else {
                            map.put(lv0.getPosition(),true);
                            expand(pos);
//                            }
                        }
                    }
                });
                helper.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return false;
                    }
                });
                break;
            case TYPE_LEVEL_1:
                final CollectionConvItemDate lv1 = (CollectionConvItemDate) item;
                Logger.i("二级消息:" + new Gson().toJson(lv1));
                helper.setText(R.id.textview_time, DateTimeUtils.getTime(Long.parseLong(lv1.getLastUpdateTime()), false, true));
                if (lv1.getUnCount() > 0) {
                    helper.getView(R.id.textView_new_msg).setVisibility(View.VISIBLE);
                    if (lv1.getUnCount() > 99) {
                        helper.setText(R.id.textView_new_msg, "99+");
                    } else {
                        helper.setText(R.id.textView_new_msg, lv1.getUnCount() + "");
                    }
                } else {
                    helper.getView(R.id.textView_new_msg).setVisibility(View.GONE);
                }

                if ((ConversitionType.MSG_TYPE_CHAT + "").equals(lv1.getOriginType())) {

                    ConnectionUtil.getInstance().getCollectionUserCard(lv1.getOriginFrom(), new IMLogicManager.NickCallBack() {
                        @Override
                        public void onNickCallBack(Nick nick) {
                            helper.setText(android.R.id.text1, nick.getName());
                            ProfileUtils.displayGravatarByImageSrc(((Activity) context), nick.getHeaderSrc(),
                                    (SimpleDraweeView) helper.getView(R.id.conversation_gravantar),
                                    context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size),
                                    context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
//                        helper.setText(R.id.collection_user_name, nick.getName());
//                        ProfileUtils.displayGravatarByImageSrc(((Activity) context),nick.getHeaderSrc(),
//                                (SimpleDraweeView)helper.getView(R.id.collection_gravantar),
//                                context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size),
//                                context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
                        }
                    }, false, false);


                } else if ((ConversitionType.MSG_TYPE_GROUP + "").equals(lv1.getOriginType())) {
                    ConnectionUtil.getInstance().getCollectionMucCard(lv1.getOriginFrom(), new IMLogicManager.NickCallBack() {
                        @Override
                        public void onNickCallBack(Nick nick) {
                            helper.setText(android.R.id.text1, nick.getName());
                            ProfileUtils.displayGravatarByImageSrc(((Activity) context), nick.getHeaderSrc(),
                                    (SimpleDraweeView) helper.getView(R.id.conversation_gravantar),
                                    context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size),
                                    context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
//                        helper.setText(R.id.collection_user_name, nick.getName());
//                        ProfileUtils.displayGravatarByImageSrc(((Activity) context),nick.getHeaderSrc(),
//                                (SimpleDraweeView)helper.getView(R.id.collection_gravantar),
//                                context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size),
//                                context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
                        }
                    }, false, false);
                }

                String latestMsg = ChatTextHelper.showContentType(lv1.getContent(), Integer.parseInt(lv1.getType()));

                helper.setText(android.R.id.text2,latestMsg);

                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startChatActvity(lv1);
                    }
                });

                break;
        }

    }


    public void startChatActvity(CollectionConvItemDate date) {
        Intent intent = new Intent(context, CollectionChatActivity.class);
        //设置jid 就是当前会话对象
        intent.putExtra(PbChatActivity.KEY_JID, date.getFrom());
//
        intent.putExtra(PbChatActivity.KEY_REAL_JID, date.getRealJid());
//        }
        intent.putExtra(PbChatActivity.KEY_CHAT_TYPE, date.getOriginType());

        intent.putExtra(CollectionChatActivity.ORIGINFROM, date.getOriginFrom());

        intent.putExtra(CollectionChatActivity.ORIGINTO, date.getOriginTo());

        intent.putExtra(PbChatActivity.KEY_RIGHTBUTTON, true);
        //设置是否是群聊
        boolean isChatRoom = Integer.parseInt(date.getOriginType()) == ConversitionType.MSG_TYPE_GROUP;
        intent.putExtra(PbChatActivity.KEY_IS_CHATROOM, isChatRoom);
        if (isChatRoom) {
//            intent.putExtra(PbChatActivity.KEY_ATMSG_INDEX, item.getAtMsgIndex());
        }


        context.startActivity(intent);
    }

    public Map<Integer, Boolean> getMap() {
        return map;
    }

    public void setMap(Map<Integer, Boolean> map) {
        this.map = map;
    }
}
