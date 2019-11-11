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
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.utils.ConnectionUtil;
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
import com.qunar.im.ui.view.IconView;
import com.qunar.im.ui.R;
import com.qunar.im.ui.util.ResourceUtils;
import com.qunar.im.ui.view.recentView.ChatRender;
import com.qunar.im.ui.view.recentView.ConsultRender;
import com.qunar.im.ui.view.recentView.DefaultRender;
import com.qunar.im.ui.view.recentView.FriendRequestRender;
import com.qunar.im.ui.view.recentView.GroupRender;
import com.qunar.im.ui.view.recentView.HeadLineRender;
import com.qunar.im.ui.view.recentView.IRecentRender;
import com.qunar.im.ui.view.recentView.RobotRender;
import com.qunar.im.ui.view.recyclerview.BaseQuickAdapter;
import com.qunar.im.ui.view.recyclerview.BaseViewHolder;
import com.qunar.im.utils.QtalkStringUtils;

import java.util.LinkedList;
import java.util.List;

public class RVRecentConvsAdapter extends BaseQuickAdapter<RecentConversation, BaseViewHolder> {

    //核心连接管理类
    private ConnectionUtil connectionUtil;
    List<RecentConversation> recentConversationList;
    SparseArray<IRecentRender> renderMap = new SparseArray<IRecentRender>();
    IRecentRender defaultRender = new DefaultRender();
    List<String> dndList;//免打扰
    Context context;
    private static String defaultMucImage = QtalkNavicationService.getInstance().getSimpleapiurl() + "/file/v2/download/perm/bc0fca9b398a0e4a1f981a21e7425c7a.png";
    private static String defaultUserImage = QtalkNavicationService.getInstance().getSimpleapiurl() + "/file/v2/download/perm/ff1a003aa731b0d4e2dd3d39687c8a54.png";
    //是否从db层从新获取数据
    private boolean toDB;
    private boolean enforce;

    private boolean isFirst = true;//是否是第一次加载，第一次加载走缓存 为了快速显示名片使用


    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     */
    public RVRecentConvsAdapter(Context cxt) {
        super(R.layout.atom_ui_rosteritem);
        connectionUtil = ConnectionUtil.getInstance();

        context = cxt;
        try {
            dndList = (List<String>) InternDatas.getData(Constants.SYS.DND_LIST);
        } catch (Exception ex) {
            LogUtil.e(TAG, "ERROR", ex);
        }
        if (dndList == null) dndList = new LinkedList<>();
        renderMap.append(ConversitionType.MSG_TYPE_CHAT, new ChatRender());
        renderMap.append(ConversitionType.MSG_TYPE_COLLECTION, new ChatRender());
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

    @Override
    protected void convert(final BaseViewHolder holder, RecentConversation item) {
        int fontSizeMode = com.qunar.im.protobuf.common.CurrentPreference.getInstance().getFontSizeMode();
        float text1FontSize = context.getResources().getDimensionPixelSize(R.dimen.atom_ui_text_size_large);
        float textViewTimeFontSize = context.getResources().getDimensionPixelSize(R.dimen.atom_ui_text_size_smaller);
        float text2FontSize = context.getResources().getDimensionPixelSize(R.dimen.atom_ui_text_size_small);
        switch (fontSizeMode) {
            case 1://small font size
                text1FontSize -= ResourceUtils.getFontSizeIntervalPX(context);
                textViewTimeFontSize -= ResourceUtils.getFontSizeIntervalPX(context);
                text2FontSize -= ResourceUtils.getFontSizeIntervalPX(context);
                break;
            case 2://middle font size
                break;
            case 3://big font size
                text1FontSize += ResourceUtils.getFontSizeIntervalPX(context);
                textViewTimeFontSize += ResourceUtils.getFontSizeIntervalPX(context);
                text2FontSize += ResourceUtils.getFontSizeIntervalPX(context);
                break;
        }
        ((TextView) holder.getView(android.R.id.text1)).setTextSize(TypedValue.COMPLEX_UNIT_PX, text1FontSize);
        ((TextView) holder.getView(R.id.textview_time)).setTextSize(TypedValue.COMPLEX_UNIT_PX, textViewTimeFontSize);
//        holder.mConsultTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textViewTimeFontSize);
        ((TextView) holder.getView(android.R.id.text2)).setTextSize(TypedValue.COMPLEX_UNIT_PX, text2FontSize);
        final RecentConversation data = item;


        if (data.getTop() > 0) {
            //这个判断是用来判断如果是置顶情况下, 要更改颜色 很恶心的一个操作方法差评
//            ((SimpleDraweeView)holder.getView(R.id.conversation_gravantar)).setVisibility(View.GONE);
            holder.itemView.setBackgroundResource(R.drawable.touch_bg_gray);
//            holder.itemView.setBackground();
//            ((SimpleDraweeView)holder.getView(R.id.conversation_gravantar)) = (SimpleDraweeView) convertView.findViewById(R.id.conversation_gravantar_top);
//            ((SimpleDraweeView)holder.getView(R.id.conversation_gravantar_top)).setVisibility(View.VISIBLE);
        } else {
//            ((SimpleDraweeView)holder.getView(R.id.conversation_gravantar_top)).setVisibility(View.GONE);
            holder.itemView.setBackgroundResource(R.drawable.touch_bg);
//            ((SimpleDraweeView)holder.getView(R.id.conversation_gravantar)) = (SimpleDraweeView) convertView.findViewById(R.id.conversation_gravantar);
//            ((SimpleDraweeView)holder.getView(R.id.conversation_gravantar)).setVisibility(View.VISIBLE);

        }


        showDetailed(holder, data);

        if (isFirst) {
            showCard(data.getNick(), holder, data.getId());
        } else {
            if (data.getConversationType() == 1) {
                Nick nick = connectionUtil.testgetnick(data.getId());
                if (nick != null) {
                    data.setNick(nick);
                    showCard(nick, holder, data.getId());
                    if (data.isChan().indexOf("send") != -1) {
                        if (nick != null && !TextUtils.isEmpty(nick.getName())) {
                            holder.getView(android.R.id.text1).setTag(null);
                            ((TextView) holder.getView(android.R.id.text1)).setText(nick.getName());
                            holder.getView(R.id.imageview_type).setVisibility(View.GONE);
                        }

                    }
                } else {

                    connectionUtil.getMucCard(data.getId(), new IMLogicManager.NickCallBack() {
                        @Override
                        public void onNickCallBack(final Nick nick) {
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    data.setNick(nick);
                                    showCard(nick, holder, data.getId());
                                    if (data.isChan().indexOf("send") != -1) {
                                        if (nick != null && !TextUtils.isEmpty(nick.getName())) {
                                            holder.getView(android.R.id.text1).setTag(null);
                                            ((TextView) holder.getView(android.R.id.text1)).setText(nick.getName());
                                            holder.getView(R.id.imageview_type).setVisibility(View.GONE);
                                        }

                                    }
                                }
                            });

                        }
                    }, data.isToNetWork(), data.isToDB());
                }

            } else if (data.getConversationType() == 0) {
                final String jid = data.getId();//consult消息 jid为realuser

                Nick nick = connectionUtil.testgetnick(jid);
                if (nick != null) {
                    data.setNick(nick);
                    showCard(nick, holder, jid);
                    if (data.isChan().indexOf("send") != -1) {
                        holder.getView(R.id.imageview_type).setVisibility(View.GONE);
                    }
                } else {


                    connectionUtil.getUserCard(jid, new IMLogicManager.NickCallBack() {
                        @Override
                        public void onNickCallBack(final Nick nick) {
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    data.setNick(nick);
                                    showCard(nick, holder, jid);
                                    if (data.isChan().indexOf("send") != -1) {
                                        holder.getView(R.id.imageview_type).setVisibility(View.GONE);
                                    }
                                }
                            });

                        }
                    }, data.isToNetWork(), data.isToDB());
                }
            } else if (data.getConversationType() == 4 || data.getConversationType() == 5) {
                final String jid = data.getRealUser();//consult消息 jid为realuser
                final Nick nick = connectionUtil.testgetnick(jid);
                if (nick != null) {
                    if (nick != null) {
//                                Logger.i(new Gson().toJson(nick));
                        ProfileUtils.displayGravatarByImageSrc((Activity) context, TextUtils.isEmpty(nick.getHeaderSrc()) ? defaultUserImage : nick.getHeaderSrc(), ((SimpleDraweeView) holder.getView(R.id.conversation_gravantar)),
                                context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size), context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
                        //设置会话名,先用会话id显示
                        if (data.getConversationType() == 4) {
                            ((TextView) holder.getView(android.R.id.text1)).setText(TextUtils.isEmpty(nick.getName()) ? nick.getXmppId() : nick.getName());
                        } else if (data.getConversationType() == 5) {

                            Nick nick1 = connectionUtil.testgetnick(data.getId());
                            if (nick != null) {
                                if (CommonConfig.isQtalk) {
                                    ((TextView) holder.getView(android.R.id.text1)).setText(nick1.getName() + "-" + nick.getName());
                                } else {
                                    ((TextView) holder.getView(android.R.id.text1)).setText(nick.getName() + "-" + nick1.getName());
                                }
                            }else {

                                connectionUtil.getUserCard(data.getId(), new IMLogicManager.NickCallBack() {
                                    @Override
                                    public void onNickCallBack(Nick nick1) {
                                        if (CommonConfig.isQtalk) {
                                            ((TextView) holder.getView(android.R.id.text1)).setText(nick1.getName() + "-" + nick.getName());
                                        } else {
                                            ((TextView) holder.getView(android.R.id.text1)).setText(nick.getName() + "-" + nick1.getName());
                                        }


                                    }
                                }, false, false);
                            }
                        } else {
                            ((TextView) holder.getView(android.R.id.text1)).setText(nick.getName());
                        }

                    } else {
                        ((TextView) holder.getView(android.R.id.text1)).setText(jid);
                        ProfileUtils.displayGravatarByImageSrc((Activity) context, defaultUserImage, ((SimpleDraweeView) holder.getView(R.id.conversation_gravantar)),
                                context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size), context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
                    }

                    if (data.getConversationType() == ConversitionType.MSG_TYPE_CONSULT) {
//                                holder.mConsultTextView.setText("-客服");
                        (holder.getView(R.id.imageview_type)).setVisibility(View.GONE);
                    } else if (data.getConversationType() == ConversitionType.MSG_TYPE_CONSULT_SERVER) {
//                                holder.mConsultTextView.setText("-咨询");
                        (holder.getView(R.id.imageview_type)).setVisibility(View.VISIBLE);
                    }
                } else {
                    connectionUtil.getUserCard(jid, new IMLogicManager.NickCallBack() {
                        @Override
                        public void onNickCallBack(final Nick nick) {
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    data.setNick(nick);
                                    if (nick != null) {
//                                Logger.i(new Gson().toJson(nick));
                                        ProfileUtils.displayGravatarByImageSrc((Activity) context, TextUtils.isEmpty(nick.getHeaderSrc()) ? defaultUserImage : nick.getHeaderSrc(), ((SimpleDraweeView) holder.getView(R.id.conversation_gravantar)),
                                                context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size), context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
                                        //设置会话名,先用会话id显示
                                        if (data.getConversationType() == 4) {
                                            ((TextView) holder.getView(android.R.id.text1)).setText(TextUtils.isEmpty(nick.getName()) ? nick.getXmppId() : nick.getName());
                                        } else if (data.getConversationType() == 5) {
                                            connectionUtil.getUserCard(data.getId(), new IMLogicManager.NickCallBack() {
                                                @Override
                                                public void onNickCallBack(Nick nick1) {
                                                    if (CommonConfig.isQtalk) {
                                                        ((TextView) holder.getView(android.R.id.text1)).setText(nick1.getName() + "-" + nick.getName());
                                                    } else {
                                                        ((TextView) holder.getView(android.R.id.text1)).setText(nick.getName() + "-" + nick1.getName());
                                                    }


                                                }
                                            }, false, false);
                                        } else {
                                            ((TextView) holder.getView(android.R.id.text1)).setText(nick.getName());
                                        }

                                    } else {
                                        ((TextView) holder.getView(android.R.id.text1)).setText(jid);
                                        ProfileUtils.displayGravatarByImageSrc((Activity) context, defaultUserImage, ((SimpleDraweeView) holder.getView(R.id.conversation_gravantar)),
                                                context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size), context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
                                    }

                                    if (data.getConversationType() == ConversitionType.MSG_TYPE_CONSULT) {
//                                holder.mConsultTextView.setText("-客服");
                                        (holder.getView(R.id.imageview_type)).setVisibility(View.GONE);
                                    } else if (data.getConversationType() == ConversitionType.MSG_TYPE_CONSULT_SERVER) {
//                                holder.mConsultTextView.setText("-咨询");
                                        (holder.getView(R.id.imageview_type)).setVisibility(View.VISIBLE);
                                    }
                                }
                            });

                        }
                    }, data.isToNetWork(), data.isToDB());
                }
            } else if (data.getConversationType() == 8) {
                final String jid = data.getId();
                Nick nick = connectionUtil.testgetnick(jid);
                if (nick != null) {
                    showCard(nick, holder, jid);
                    (holder.getView(R.id.imageview_type)).setVisibility(View.GONE);
                }else {


                    connectionUtil.getUserCard(jid, new IMLogicManager.NickCallBack() {
                        @Override
                        public void onNickCallBack(final Nick nick) {
                            data.setNick(nick);
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showCard(nick, holder, jid);
                                    (holder.getView(R.id.imageview_type)).setVisibility(View.GONE);
                                }
                            });

                        }
                    }, data.isToNetWork(), data.isToDB());
                }
            }
        }


    }

    private void showCard(Nick nick, BaseViewHolder holder, String jid) {
        if (nick != null) {
            if (TextUtils.isEmpty(nick.getMark())) {
                ((TextView) holder.getView(android.R.id.text1)).setText(nick.getName());
            } else {
                ((TextView) holder.getView(android.R.id.text1)).setText(nick.getMark());
            }

            ProfileUtils.displayGravatarByImageSrc((Activity) context, TextUtils.isEmpty(nick.getHeaderSrc()) ? defaultUserImage : nick.getHeaderSrc(), ((SimpleDraweeView) holder.getView(R.id.conversation_gravantar)),
                    context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size), context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
        } else {
            ((TextView) holder.getView(android.R.id.text1)).setText(jid);
            ProfileUtils.displayGravatarByImageSrc((Activity) context, defaultUserImage, ((SimpleDraweeView) holder.getView(R.id.conversation_gravantar)),
                    context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size), context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
        }
    }

    public void showDetailed(final BaseViewHolder holder, final RecentConversation data) {
        ((TextView) holder.getView(R.id.textview_time)).setText(DateTimeUtils.getTime(data.getLastMsgTime(), false, true));
        if (!isFirst && data.getConversationType() == 1) {
            connectionUtil.getUserCard(data.getLastFrom(), new IMLogicManager.NickCallBack() {
                @Override
                public void onNickCallBack(final Nick nick) {
                    if (nick != null) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                data.setFullname((TextUtils.isEmpty(nick.getName()) ? nick.getXmppId() : nick.getName()));
                                showMessage(data, holder);
                                //,(TextUtils.isEmpty(nick.getName())?nick.getXmppId():nick.getName()),data.getLastMsg()
                            }
                        });

                    }
                }
            }, false, false);
        } else {
            showMessage(data, holder);
        }
    }

    public void setRecentConversationList(List<RecentConversation> recentConversationList) {
        this.setRecentConversationList(recentConversationList, false);
    }

    public void setRecentConversationList(List<RecentConversation> recentConversationList, boolean isFirst) {
        this.isFirst = isFirst;
        this.recentConversationList = recentConversationList;
        setNewData(recentConversationList);

//        this.toDB = toDB;
//        this.enforce = toNetWork;
//        this.notifyDataSetChanged();
    }

//    public void


    private void handleMessage(final RecentConversation data, final BaseViewHolder holder) {
        IRecentRender recentRender = renderMap.get(data.getConversationType(), defaultRender);
//        recentRender.render(holder, data, context);
    }

    private void showMessage(RecentConversation data, BaseViewHolder holder) {
        String latestMsg = ChatTextHelper.showContentType(data.getLastMsg(), data.getMsgType());
        if (data.getUnread_msg_cont() > 0) {
//            if (dndList.indexOf(data.getId()) > -1) {
//
//                latestMsg = "[" +
//                        String.valueOf(data.getUnread_msg_cont())
//                        + "条]" + latestMsg;
//
//                ((TextView)holder.getView(R.id.textView_new_msg)).setVisibility(View.INVISIBLE);
//            } else {
//
////                ((TextView)holder.getView(R.id.textView_new_msg)).setVisibility(View.VISIBLE);
//            }
            if (data.getRemind() > 0) {
                //设置不提醒
//            ((TextView)holder.getView(R.id.textView_new_msg)).setBackgroundResource(R.drawable.atom_ui_unread_circle_remind_bg);

                ((TextView) holder.getView(R.id.textView_new_msg)).setBackgroundResource(R.drawable.atom_ui_unread_circle_bg);
                ((TextView) holder.getView(R.id.textView_new_msg)).setVisibility(View.GONE);
                ((IconView) holder.getView(R.id.not_remind)).setVisibility(View.VISIBLE);

            } else {
                if (data.getUnread_msg_cont() < 100) {
                    ((TextView) holder.getView(R.id.textView_new_msg)).setText(String.valueOf(data.getUnread_msg_cont()));
                } else {
                    ((TextView) holder.getView(R.id.textView_new_msg)).setText("99+");
                }
                ((TextView) holder.getView(R.id.textView_new_msg)).setBackgroundResource(R.drawable.atom_ui_unread_circle_bg);
                ((TextView) holder.getView(R.id.textView_new_msg)).setVisibility(View.VISIBLE);
                ((IconView) holder.getView(R.id.not_remind)).setVisibility(View.GONE);
            }


        } else {
            ((TextView) holder.getView(R.id.textView_new_msg)).setVisibility(View.GONE);
            ((IconView) holder.getView(R.id.not_remind)).setVisibility(View.GONE);
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

            ((TextView) holder.getView(android.R.id.text2)).setText(sb);
        }
    }

    public void refreshTheItem(BaseViewHolder holder, RecentConversation data) {
        showDetailed(holder, data);
    }

}

