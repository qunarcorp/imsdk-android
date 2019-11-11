package com.qunar.im.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.qunar.im.ui.util.easyphoto.easyphotos.utils.media.DurationUtils;
import com.orhanobut.logger.Logger;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.module.ImageItemWorkWorldItem;
import com.qunar.im.base.module.MultiItemEntity;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.module.ReleaseContentData;
import com.qunar.im.base.module.SetLikeData;
import com.qunar.im.base.module.SetLikeDataResponse;
import com.qunar.im.base.module.WorkWorldItem;
import com.qunar.im.base.module.WorkWorldNoticeItem;
import com.qunar.im.base.module.WorkWorldOutCommentBean;
import com.qunar.im.base.module.WorkWorldOutOpenDetails;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.structs.MessageStatus;
import com.qunar.im.base.structs.WorkWorldItemState;
import com.qunar.im.base.util.ChatTextHelper;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.ui.util.EmotionUtils;
import com.qunar.im.base.util.FileUtils;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.MemoryCache;
import com.qunar.im.base.util.graphics.ImageUtils;
import com.qunar.im.base.view.faceGridView.EmoticonEntity;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.QunarWebActvity;
import com.qunar.im.ui.activity.WorkWorldDetailsActivity;
import com.qunar.im.ui.imagepicker.util.Utils;
import com.qunar.im.ui.presenter.views.IBrowsingConversationImageView;
import com.qunar.im.ui.util.ProfileUtils;
import com.qunar.im.ui.util.videoPlayUtil.VideoPlayUtil;
import com.qunar.im.ui.view.IconView;
import com.qunar.im.ui.view.WorkWorldLinkTouchMovementMethod;
import com.qunar.im.ui.view.WorkWorldSpannableTextView;
import com.qunar.im.ui.view.baseView.AnimatedGifDrawable;
import com.qunar.im.ui.view.baseView.AnimatedImageSpan;
import com.qunar.im.ui.view.baseView.processor.TextMessageProcessor;
import com.qunar.im.ui.view.bigimageview.ImageBrowsUtil;
import com.qunar.im.ui.view.recyclerview.BaseQuickAdapter;
import com.qunar.im.ui.view.recyclerview.BaseViewHolder;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.HttpUtil;
import com.qunar.im.utils.QtalkStringUtils;
import com.qunar.im.base.protocol.NativeApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.qunar.im.base.structs.MessageType.image;
import static com.qunar.im.base.structs.MessageType.link;
import static com.qunar.im.base.structs.MessageType.video;
import static com.qunar.im.ui.activity.WorkWorldDetailsActivity.WORK_WORLD_DETAILS_ITEM;

public class PublicWorkWorldAdapterDraw {

    /**
     * 朋友圈相关功能从此开始
     * 注意：保存文本状态集合的key一定要是唯一的，如果用position。
     * 如果使用position作为key，则删除、增加条目的时候会出现显示错乱
     */
    private static HashMap<String, Integer> mTextStateList = new HashMap<>();;//保存文本状态集合

    private static final int MAX_LINE_COUNT = 4;//最大显示行数

    private static final int STATE_UNKNOW = -1;//未知状态

    private static final int STATE_NOT_OVERFLOW = 1;//文本行数小于最大可显示行数

    private static final int STATE_COLLAPSED = 2;//折叠状态

    private static final int STATE_EXPANDED = 3;//展开状态

  private static int defaultSize = com.qunar.im.base.util.Utils.dipToPixels(QunarIMApp.getContext(), 96);
  private static int iconSize = com.qunar.im.base.util.Utils.dpToPx(QunarIMApp.getContext(), 32);





    private static String defaultHeadUrl = QtalkNavicationService.getInstance().getInnerFiltHttpHost() + "/file/v2/download/perm/3ca05f2d92f6c0034ac9aee14d341fc7.png";


    public static void showNoticeInit(final BaseViewHolder helper, WorkWorldNoticeItem item, final Activity mActivity) {
        String ixmppid = "";
        final WorkWorldNoticeItem workWorldNoticeItem = item;


//                if (workWorldNoticeItem.getEventType().equals(Constants.WorkWorldState.NOTICE)||workWorldNoticeItem.getEventType().equals(Constants.WorkWorldState.COMMENTATMESSAGE)) {
//                    ixmppid = workWorldNoticeItem.getUserFrom() + "@" + workWorldNoticeItem.getUserFromHost();
//                } else if (workWorldNoticeItem.getEventType().equals(Constants.WorkWorldState.WORKWORLDATMESSAGE)) {
//                    ixmppid = workWorldNoticeItem.getOwner() + "@" + workWorldNoticeItem.getOwnerHost();
//                }

            setClickHeadName(workWorldNoticeItem, helper,mActivity);

//                showIsLike(workWorldNoticeItem.helper);
//                showLikeNum(workWorldNoticeItem.helper);
            showContentText(workWorldNoticeItem, helper);
            helper.getView(R.id.comment_item_text).setTag(workWorldNoticeItem);
//                helper.getView(R.id.comment_item_text).setOnClickListener(onClickListener);

            if (!TextUtils.isEmpty(workWorldNoticeItem.getContent())) {
                ((TextView) helper.getView(R.id.text_item)).setText(workWorldNoticeItem.getContent());
            }


            if (!TextUtils.isEmpty(workWorldNoticeItem.getPostUUID())) {
                ConnectionUtil.getInstance().getWorkWorldByUUID(workWorldNoticeItem.getPostUUID(), new ConnectionUtil.WorkWorldCallBack() {
                    @Override
                    public void callBack(final WorkWorldItem item) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ReleaseContentData contentData1;
                                try {
                                    contentData1 = JsonUtils.getGson().fromJson(item.getContent(), ReleaseContentData.class);
                                } catch (Exception e) {
                                    contentData1 = new ReleaseContentData();
//                                return;
                                }


                                switch (contentData1.getType()) {
                                    case link:
                                        ((TextView) helper.getView(R.id.text_item)).setText(contentData1.getLinkContent().title);
                                        helper.getView(R.id.img_item).setVisibility(View.GONE);
                                        helper.getView(R.id.play_button).setVisibility(View.GONE);
                                        helper.getView(R.id.text_item).setVisibility(View.VISIBLE);
                                        break;
                                    case image:
                                        String url = contentData1.getImgList().get(0).getData();
//                                img_item


                                        ProfileUtils.displaySquareByImageSrc(mActivity, url, (ImageView) helper.getView(R.id.img_item),
                                                mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_56dp), mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_56dp));
                                        helper.getView(R.id.img_item).setVisibility(View.VISIBLE);
                                        helper.getView(R.id.play_button).setVisibility(View.GONE);
                                        helper.getView(R.id.text_item).setVisibility(View.GONE);
                                        break;
                                    case video:
                                        String videoImage = contentData1.getVideoContent().ThumbUrl;
                                        ProfileUtils.displaySquareByImageSrc(mActivity, videoImage, (ImageView) helper.getView(R.id.img_item),
                                                mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_56dp), mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_56dp));
                                        helper.getView(R.id.img_item).setVisibility(View.VISIBLE);
                                        helper.getView(R.id.play_button).setVisibility(View.VISIBLE);
                                        helper.getView(R.id.text_item).setVisibility(View.GONE);
                                        break;
                                    default:
                                        ((TextView) helper.getView(R.id.text_item)).setText(contentData1.getContent());
                                        helper.getView(R.id.img_item).setVisibility(View.GONE);
                                        helper.getView(R.id.text_item).setVisibility(View.VISIBLE);
                                        helper.getView(R.id.play_button).setVisibility(View.GONE);
                                        break;
                                }
                            }
                        });

//                            if (contentData1.getImgList().size() > 0) {
//                                String url = contentData1.getImgList().get(0).getData();
////                                img_item
//
//
//                                ProfileUtils.displaySquareByImageSrc(mActivity, url, (ImageView) helper.getView(R.id.img_item),
//                                        mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_56dp), mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_56dp));
//                                helper.getView(R.id.img_item).setVisibility(View.VISIBLE);
//                                helper.getView(R.id.text_item).setVisibility(View.GONE);
//                            } else {
//                                ((TextView) helper.getView(R.id.text_item)).setText(contentData1.getContent());
//                                helper.getView(R.id.img_item).setVisibility(View.GONE);
//                                helper.getView(R.id.text_item).setVisibility(View.VISIBLE);
//                            }

                    }

                    @Override
                    public void goToNetWork() {
                        ((TextView) helper.getView(R.id.text_item)).setText("加载中...");
                        helper.getView(R.id.img_item).setVisibility(View.GONE);
                        helper.getView(R.id.text_item).setVisibility(View.VISIBLE);
                    }
                });
            }

            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(workWorldNoticeItem.getPostUUID())) {
                        ConnectionUtil.getInstance().getWorkWorldByUUID(workWorldNoticeItem.getPostUUID(), new ConnectionUtil.WorkWorldCallBack() {
                            @Override
                            public void callBack(WorkWorldItem item) {

                                if (!TextUtils.isEmpty(item.getUuid())) {
                                    Intent intent = new Intent(mActivity, WorkWorldDetailsActivity.class);
                                    intent.putExtra(WORK_WORLD_DETAILS_ITEM, (Serializable) item);
                                    mActivity.startActivity(intent);
                                }
                            }

                            @Override
                            public void goToNetWork() {
                                Toast.makeText(mActivity,"加载中,请稍后...",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });


            //确定发帖时间
            if (!TextUtils.isEmpty(workWorldNoticeItem.getCreateTime())) {
                try {
                    long time = Long.parseLong(workWorldNoticeItem.getCreateTime());
                    String t = DataUtils.formationDate(time);
                    ((TextView) helper.getView(R.id.notice_time)).setText(t);
                } catch (Exception e) {
                    ((TextView) helper.getView(R.id.notice_time)).setText("未知");
                }
            } else {
                ((TextView) helper.getView(R.id.notice_time)).setText("未知");
            }

            //设置点赞单击事件

    }


    //设置头像点击事件
    private static void setClickHeadName(final WorkWorldNoticeItem item, final BaseViewHolder helper, final Activity mActivity) {
//        helper.getView(R.id.user_header)
        //目前未设计缺省值
        String isAnon = "";
        String userId = "";
        String anonymousName = "";
        String anonymousPhoto = "";
        if (item.getEventType().equals(Constants.WorkWorldState.NOTICE)
                || item.getEventType().equals(Constants.WorkWorldState.COMMENTATMESSAGE)
                || item.getEventType().equals(Constants.WorkWorldState.MYREPLYCOMMENT)) {
            isAnon = item.getFromIsAnonymous();
            userId = item.getUserFrom() + "@" + item.getUserFromHost();
            anonymousPhoto = item.getFromAnonymousPhoto();
            anonymousName = item.getFromAnonymousName();
        } else if (item.getEventType().equals(Constants.WorkWorldState.WORKWORLDATMESSAGE)) {
//            isAnon = item.getIsAnyonous();
//////            userId = item.getOwner() + "@" + item.getOwnerHost();
//////            anonymousName = item.getAnyonousName();
//////            anonymousPhoto = item.getAnyonousPhoto();
            isAnon = item.getFromIsAnonymous();
            userId = item.getUserFrom() + "@" + item.getUserFromHost();
            anonymousPhoto = item.getFromAnonymousPhoto();
            anonymousName = item.getFromAnonymousName();
        } else {
            //当前这个分支完全没有意义
        }
        final String finalUserId = userId;
        if (isAnon.equals("0")) {

            helper.getView(R.id.user_header).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NativeApi.openUserCardVCByUserId(finalUserId);
                }
            });
            helper.getView(R.id.user_name).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NativeApi.openUserCardVCByUserId(finalUserId);
                }
            });
        } else {
            helper.getView(R.id.user_header).setOnClickListener(null);
            helper.getView(R.id.user_name).setOnClickListener(null);
        }

        if (isAnon.equals("1")) {


            ((TextView) helper.getView(R.id.user_name)).setTextColor(mActivity.getResources().getColor(R.color.atom_ui_light_gray_99));

            ProfileUtils.displayGravatarByImageSrc(mActivity, anonymousPhoto, (ImageView) helper.getView(R.id.user_header),
                    mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head), mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head));
            ((TextView) helper.getView(R.id.user_name)).setText(anonymousName);

            ((TextView) helper.getView(R.id.user_architecture)).setVisibility(View.GONE);

        } else {
            ((TextView) helper.getView(R.id.user_architecture)).setVisibility(View.VISIBLE);
            ConnectionUtil.getInstance().getUserCard(finalUserId, new IMLogicManager.NickCallBack() {
                @Override
                public void onNickCallBack(final Nick nick) {
//                ((Activity) mContext).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
                    if (nick != null) {

                        ProfileUtils.displayGravatarByImageSrc(mActivity, nick.getHeaderSrc(), (ImageView) helper.getView(R.id.user_header),
                                mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head), mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head));
                        ((TextView) helper.getView(R.id.user_name)).setText(TextUtils.isEmpty(nick.getName()) ? finalUserId : nick.getName());
                    } else {
                        ProfileUtils.displayGravatarByImageSrc(mActivity, defaultHeadUrl, (ImageView) helper.getView(R.id.user_header),
                                mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head), mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head));
                        ((TextView) helper.getView(R.id.user_name)).setText(finalUserId);
                    }
                    ((TextView) helper.getView(R.id.user_architecture)).setText(QtalkStringUtils.architectureParsing(nick.getDescInfo()));
//                    }
//                });


                }
            }, false, false);


            ((TextView) helper.getView(R.id.user_name)).setTextColor(mActivity.getResources().getColor(R.color.atom_ui_new_color_4DC1B5));
        }


    }

    //展示评论内容
    public static void showContentText(WorkWorldNoticeItem data, final BaseViewHolder helper) {
        if (data.getEventType().equals(Constants.WorkWorldState.NOTICE)
                || data.getEventType().equals(Constants.WorkWorldState.COMMENTATMESSAGE)
                || data.getEventType().equals(Constants.WorkWorldState.MYREPLYCOMMENT)) {
            final String content = data.getContent();
            final String isAnonymous = data.getToIsAnonymous();
            final String xmppid = data.getUserTo() + "@" + data.getUserToHost();
            final String anonymousName = data.getToAnonymousName();
            if (TextUtils.isEmpty(data.getUserTo())) {
                ((TextView) helper.getView(R.id.comment_item_text)).setText(data.getContent());
            } else {
                com.orhanobut.logger.Logger.i("data有可能为null:" + data);

                if (CurrentPreference.getInstance().getPreferenceUserId().equals(xmppid)) {

                    ((TextView) helper.getView(R.id.comment_item_text)).setText(Html.fromHtml("<font color='#999999'>回复</font> <font color='#4dc1b5'>我</font> " + content));
                } else {


                    if ("0".equals(isAnonymous)) {

                        ((TextView) helper.getView(R.id.comment_item_text)).setText(data.getContent());
                        ConnectionUtil.getInstance().getUserCard(xmppid, new IMLogicManager.NickCallBack() {
                            @Override
                            public void onNickCallBack(final Nick nick) {
//                ((Activity) mContext).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
                                if (nick != null) {
                                    ((TextView) helper.getView(R.id.comment_item_text)).setText(Html.fromHtml("<font color='#999999'>回复</font> <font color='#4dc1b5'>" + nick.getName() + "</font> " + content));
                                } else {
                                    ((TextView) helper.getView(R.id.comment_item_text)).setText(Html.fromHtml("<font color='#999999'>回复</font> <font color='#4dc1b5'>" + nick.getXmppId() + "</font> " + content));
                                }
//                    }
//                });


                            }
                        }, false, false);


                    } else {
                        ((TextView) helper.getView(R.id.comment_item_text)).setText(Html.fromHtml("<font color='#999999'>回复</font> <font color='#999999'>" + anonymousName + "</font> " + content));
                    }
                }
            }
        } else if (data.getEventType().equals(Constants.WorkWorldState.WORKWORLDATMESSAGE)) {
            ((TextView) helper.getView(R.id.comment_item_text)).setText("Hi~你被Cue到啦,快来看一下吧~");
        } else {
            //暂时没有这个分支的逻辑
            ((TextView) helper.getView(R.id.comment_item_text)).setText("意外");
        }


    }


    //朋友圈页相关功能开始


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void showWorkWorld(final BaseViewHolder helper, final WorkWorldItem item, final Activity mActivity, final RecyclerView mRecyclerView, View.OnClickListener openDetailsListener, View.OnClickListener onClickListener, boolean isWorkWorld) {
        try {
//            if(item.getIsDelete().equals("1")){
//               remove( helper.getLayoutPosition());
//               return;
//            }


            int state = mTextStateList.get(item.getUuid()) == null ? STATE_UNKNOW : mTextStateList.get(item.getUuid());
            //第一次初始化，未知状态
            final ReleaseContentData contentData;
            ReleaseContentData contentData1;
            try {
                contentData1 = JsonUtils.getGson().fromJson(item.getContent(), ReleaseContentData.class);
            } catch (Exception e) {
                contentData1 = new ReleaseContentData();
                return;
            }


            helper.getView(R.id.text_content).setVisibility(View.VISIBLE);
            ((TextView) helper.getView(R.id.tv_expand_or_fold)).setVisibility(View.VISIBLE);

            contentData = contentData1;
            String str = contentData.getExContent();
            SpannableStringBuilder sb = new SpannableStringBuilder();
            if (TextUtils.isEmpty(str)) {
                str = contentData.getContent();
            }

            if (!TextUtils.isEmpty(str)) {
                //判断如何展示标签
                //置顶
//        helper.getView(R.id.workworld_top).setVisibility(View.GONE);
                if (MessageStatus.isExistStatus( Integer.parseInt(item.getPostType()), WorkWorldItemState.top)) {
                    sb.append(Html.fromHtml("<font color='#389CFE'>[置顶] </font>"));
//            helper.getView(R.id.workworld_top).setVisibility(View.VISIBLE);
                }

                //热帖
//        helper.getView(R.id.workworld_hot).setVisibility(View.GONE);
                if (MessageStatus.isExistStatus( Integer.parseInt(item.getPostType()), WorkWorldItemState.hot)) {
//            helper.getView(R.id.workworld_hot).setVisibility(View.VISIBLE);
                    sb.append(Html.fromHtml("<font color='#FF6916'>[热帖] </font>"));
                }
                helper.getView(R.id.text_content).setVisibility(View.VISIBLE);
                ((TextView) helper.getView(R.id.tv_expand_or_fold)).setVisibility(View.VISIBLE);//显示“全文”
                //判断是否有展开收起
                if (state == STATE_UNKNOW) {
                    helper.getView(R.id.text_content).getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            //这个回掉会调用多次，获取完行数后记得注销监听
                            helper.getView(R.id.text_content).getViewTreeObserver().removeOnPreDrawListener(this);
                            //holder.content.getViewTreeObserver().addOnPreDrawListener(null);
                            //如果内容显示的行数大于最大显示行数
                            if (((TextView) helper.getView(R.id.text_content)).getLineCount() > MAX_LINE_COUNT) {
                                ((TextView) helper.getView(R.id.text_content)).setMaxLines(MAX_LINE_COUNT);//设置最大显示行数
                                ((TextView) helper.getView(R.id.tv_expand_or_fold)).setVisibility(View.VISIBLE);//显示“全文”
                                ((TextView) helper.getView(R.id.tv_expand_or_fold)).setText("全文");
                                mTextStateList.put(item.getUuid(), STATE_COLLAPSED);//保存状态
                            } else {
                                ((TextView) helper.getView(R.id.tv_expand_or_fold)).setVisibility(View.GONE);
                                mTextStateList.put(item.getUuid(), STATE_NOT_OVERFLOW);
                            }
                            return true;
                        }
                    });

                    ((TextView) helper.getView(R.id.text_content)).setMaxLines(Integer.MAX_VALUE);//设置文本的最大行数，为整数的最大数值
//                    if (!TextUtils.isEmpty(contentData.getContent())) {
                   sb.append( getContent(helper, contentData, Integer.parseInt(item.getPostType()),mActivity,openDetailsListener,item,onClickListener));

//                    }

                } else {
                    //如果之前已经初始化过了，则使用保存的状态。
                    switch (state) {
                        case STATE_NOT_OVERFLOW:
                            ((TextView) helper.getView(R.id.tv_expand_or_fold)).setVisibility(View.GONE);
                            break;
                        case STATE_COLLAPSED:
                            ((TextView) helper.getView(R.id.text_content)).setMaxLines(MAX_LINE_COUNT);
                            ((TextView) helper.getView(R.id.tv_expand_or_fold)).setVisibility(View.VISIBLE);
                            ((TextView) helper.getView(R.id.tv_expand_or_fold)).setText("全文");
                            break;
                        case STATE_EXPANDED:
                            ((TextView) helper.getView(R.id.text_content)).setMaxLines(Integer.MAX_VALUE);
                            ((TextView) helper.getView(R.id.tv_expand_or_fold)).setVisibility(View.VISIBLE);
                            ((TextView) helper.getView(R.id.tv_expand_or_fold)).setText("收起");
                            break;
                    }
//                    if (!TextUtils.isEmpty(contentData.getContent())) {
                   sb.append(  getContent(helper, contentData, Integer.parseInt(item.getPostType()),mActivity,openDetailsListener,item,onClickListener));
//                    }

                }
            }

            if(sb.length()==0){
                ((TextView) helper.getView(R.id.tv_expand_or_fold)).setVisibility(View.GONE);
                helper.getView(R.id.text_content).setVisibility(View.GONE);
            }else{
                ((WorkWorldSpannableTextView) helper.getView(R.id.text_content)).setText(sb);
                WorkWorldLinkTouchMovementMethod linkTouchMovementMethod = new WorkWorldLinkTouchMovementMethod();
                ((WorkWorldSpannableTextView) helper.getView(R.id.text_content)).setLinkTouchMovementMethod(linkTouchMovementMethod);
                ((WorkWorldSpannableTextView) helper.getView(R.id.text_content)).setMovementMethod(linkTouchMovementMethod);


            }


//            else {
//                ((TextView) helper.getView(R.id.tv_expand_or_fold)).setVisibility(View.GONE);//隐藏“全文”
//                helper.getView(R.id.text_content).setVisibility(View.GONE);
//            }



            //全文和收起的点击事件
            ((TextView) helper.getView(R.id.tv_expand_or_fold)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int state = mTextStateList.get(item.getUuid()) == null ? STATE_UNKNOW : mTextStateList.get(item.getUuid());
//                   int sx =  mRecyclerView.getScrollX();
//                   int sy = mRecyclerView.getScrollY();
                    if (state == STATE_COLLAPSED) {
                        ((TextView) helper.getView(R.id.text_content)).setMaxLines(Integer.MAX_VALUE);
                        ((TextView) helper.getView(R.id.tv_expand_or_fold)).setText("收起");
                        mRecyclerView.scrollToPosition(helper.getLayoutPosition());
                        mTextStateList.put(item.getUuid(), STATE_EXPANDED);
                    } else if (state == STATE_EXPANDED) {
                        ((TextView) helper.getView(R.id.text_content)).setMaxLines(MAX_LINE_COUNT);
                        ((TextView) helper.getView(R.id.tv_expand_or_fold)).setText("全文");
                        mRecyclerView.scrollToPosition(helper.getLayoutPosition());
                        mTextStateList.put(item.getUuid(), STATE_COLLAPSED);
                    }
                }
            });

            //获取个人名片及相关信息
            final String xmppid = item.getOwner() + "@" + item.getOwnerHost();
            if (item.getIsAnonymous().equals("0")) {

                ((TextView) helper.getView(R.id.user_architecture)).setVisibility(View.VISIBLE);
                ConnectionUtil.getInstance().getUserCard(xmppid, new IMLogicManager.NickCallBack() {
                    @Override
                    public void onNickCallBack(final Nick nick) {
//                ((Activity) mContext).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
                        if (nick != null) {

                            ProfileUtils.displayGravatarByImageSrc(mActivity, nick.getHeaderSrc(), (ImageView) helper.getView(R.id.user_header),
                                    mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_head), mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_head));
                            ((TextView) helper.getView(R.id.user_name)).setText(TextUtils.isEmpty(nick.getName()) ? xmppid : nick.getName());
                        } else {
                            ProfileUtils.displayGravatarByImageSrc(mActivity, defaultHeadUrl, (ImageView) helper.getView(R.id.user_header),
                                    mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_head), mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_head));
                            ((TextView) helper.getView(R.id.user_name)).setText(xmppid);
                        }
                        ((TextView) helper.getView(R.id.user_architecture)).setText(QtalkStringUtils.architectureParsing(nick.getDescInfo()));
                        ((TextView) helper.getView(R.id.user_name)).setTextColor(mActivity.getResources().getColor(R.color.atom_ui_new_color_4DC1B5));
//                    }
//                });


                    }
                }, false, false);
            } else {
                ProfileUtils.displayGravatarByImageSrc(mActivity, item.getAnonymousPhoto(), (ImageView) helper.getView(R.id.user_header),
                        mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_head), mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_head));
                ((TextView) helper.getView(R.id.user_name)).setText(item.getAnonymousName());
                ((TextView) helper.getView(R.id.user_name)).setTextColor(mActivity.getResources().getColor(R.color.atom_ui_light_gray_99));
                ((TextView) helper.getView(R.id.user_architecture)).setVisibility(View.GONE);
            }

            //确定发帖时间
            if (!TextUtils.isEmpty(item.getCreateTime())) {
                try {
                    long time = Long.parseLong(item.getCreateTime());
                    String t = DataUtils.formationDate(time);
                    ((TextView) helper.getView(R.id.time)).setText(t);
                } catch (Exception e) {
                    ((TextView) helper.getView(R.id.time)).setText("未知");
                }
            } else {
                ((TextView) helper.getView(R.id.time)).setText("未知");
            }


            showFunction(helper, contentData, mActivity);


            helper.getView(R.id.comment_layout).setOnClickListener(openDetailsListener);
            helper.getView(R.id.comment_layout).setTag(item);
            helper.itemView.setOnClickListener(openDetailsListener);
            helper.itemView.setTag(item);


//            //设置评论单击事件
//            helper.getView(R.id.comment_layout).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(mActivity, WorkWorldDetailsActivity.class);
//                    intent.putExtra(WORK_WORLD_DETAILS_ITEM, (Serializable) item);
//                    intent.putExtra(WORK_WORLD_DETAILS_COMMENT,true);
//                    mActivity.startActivity(intent);
//                }
//            });
//
//            helper.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(mActivity, WorkWorldDetailsActivity.class);
//                    intent.putExtra(WORK_WORLD_DETAILS_ITEM, (Serializable) item);
//                    mActivity.startActivity(intent);
//                }
//            });

            //设置点赞单击事件
            helper.getView(R.id.like_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SetLikeData data = new SetLikeData();
                    data.setLikeType(item.getIsLike().equals("1") ? 0 : 1);
                    data.setOpType(1);
                    data.setPostId(item.getUuid());
                    data.setPostOwner(item.getOwner());
                    data.setPostOwnerHost(item.getOwnerHost());
                    data.setLikeId("2-" + UUID.randomUUID().toString().replace("-", ""));

                    HttpUtil.setLike(data, new ProtocolCallback.UnitCallback<SetLikeDataResponse>() {
                        @Override
                        public void onCompleted(final SetLikeDataResponse setLikeDataResponse) {
//                            mActivity.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
                            if (setLikeDataResponse == null) {
                                return;
                            }
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    item.setIsLike(setLikeDataResponse.getData().getIsLike() + "");
                                    item.setLikeNum(setLikeDataResponse.getData().getLikeNum() + "");
                                    showLikeState(item, helper,mActivity);
                                    showLikeNum(item, helper);
                                }
                            });

//                                }
//                            });
                        }

                        @Override
                        public void onFailure(String errMsg) {

                        }
                    });
                }
            });


            //判断显示哪种类型点赞状态
            showLikeState(item, helper,mActivity);


            //设置头像名字点击事件
            setClickHeadName(item, helper);
            //判断是否显示评论数

            if (!TextUtils.isEmpty(item.getCommentsNum())) {
                try {
                    if (Integer.parseInt(item.getCommentsNum()) > 0) {

                        ((TextView) helper.getView(R.id.comment_num)).setText(item.getCommentsNum());
                    } else {
                        ((TextView) helper.getView(R.id.comment_num)).setText("评论");
                    }
                } catch (Exception e) {
                    ((TextView) helper.getView(R.id.comment_num)).setText("评论");
                }


            } else {
                ((TextView) helper.getView(R.id.comment_num)).setText("评论");
            }
            //判断是否显示点赞数
            showLikeNum(item, helper);


            //是否显示右方多功能按钮
            if(isWorkWorld) {


                if (CurrentPreference.getInstance().getPreferenceUserId().equals(item.getOwner() + "@" + item.getOwnerHost())) {
                    helper.getView(R.id.right_special).setVisibility(View.VISIBLE);
                } else {
                    helper.getView(R.id.right_special).setVisibility(View.GONE);
                }

                if(onClickListener!=null){
                    helper.getView(R.id.right_special).setTag(item);
                    helper.getView(R.id.right_special).setOnClickListener(onClickListener);
                }else{
                    helper.getView(R.id.right_special).setTag(item);
                    helper.getView(R.id.right_special).setOnClickListener(null);
                }
            }else{
                helper.getView(R.id.right_special).setVisibility(View.GONE);
            }


            mActivity.registerForContextMenu(helper.getView(R.id.right_special));

            if(isWorkWorld) {


                showOutCommentList(helper, item, mActivity, openDetailsListener);

            }
//            WorkWorldDetailsAdapter outAdapter = new WorkWorldDetailsAdapter(item.getAttachCommentList(), mActivity);
//            outAdapter.setOnOpenDetailsListener(openDetailsListener);
//            outAdapter.setWorkWorldItem(item);
//            ((RecyclerView) helper.getView(R.id.out_comment_rc)).setLayoutManager(new LinearLayoutManager(mActivity));
//            ((RecyclerView) helper.getView(R.id.out_comment_rc)).setAdapter(outAdapter);
////            helper.getView(R.id.more_layout).setOnClickListener(openDetailsListener);
//            helper.getView(R.id.more_layout).setTag(item);


        } catch (Exception e) {
            Logger.i("朋友圈列表页出错:" + e.getMessage());
        }
    }

    private static SpannableStringBuilder getContent(BaseViewHolder helper, ReleaseContentData contentData, int postType, final Activity mActivity, final View.OnClickListener openDetailsListener, WorkWorldItem item,final View.OnClickListener onClickListener) {

        String exstr = contentData.getExContent();


        WorkWorldSpannableTextView textView = ((WorkWorldSpannableTextView)helper.getView(R.id.text_content));
        textView.setTag(item);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDetailsListener.onClick(v);
            }
        });
        SpannableStringBuilder sb = new SpannableStringBuilder();
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onClickListener.onClick(v);
                return true;
            }
        });

        switch (contentData.getType()) {
            case link:
            case image:
            case video:
//                if (TextUtils.isEmpty(exstr)) {
////            str = contentData.getContent();
//                    sb.append(contentData.getContent());
//                    textView.setText(sb);
//                    return;
//                }
                sb = getSpannableStringBuild(exstr, textView, sb,mActivity,openDetailsListener,item);
//                textView.setText(sb);
                break;

            default:


                String msg = ChatTextHelper.textToHTML(contentData.getContent());
                sb = getSpannableStringBuild(msg, textView, sb,mActivity,openDetailsListener,item);
//                textView.setText(sb);
//                sb.append( textView.getText());
                break;
        }


//
//
//
        return sb;
//        ((TextView) helper.getView(R.id.text_content)).setText(contentData.getContent());
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static void showFunction(BaseViewHolder helper, final ReleaseContentData contentData, final Activity mActivity) {

//        GridSpacingItemDecoration g3 = new GridSpacingItemDecoration(3, Utils.dp2px(mActivity, 4), false);
//
//        GridSpacingItemDecoration  g2 = new GridSpacingItemDecoration(2, Utils.dp2px(mActivity, 4), false);
//
//        GridSpacingItemDecoration g1 = new GridSpacingItemDecoration(1, Utils.dp2px(mActivity, 4), false);
//
//         GridSpacingItemDecoration gridSpacingItemDecoration;





        //初始情况全部控件不展示
        helper.getView(R.id.img_rc).setVisibility(View.GONE);
        helper.getView(R.id.re_video_ll).setVisibility(View.GONE);
        helper.getView(R.id.re_link_ll).setVisibility(View.GONE);

        try {
            switch (contentData.getType()) {
                case video:
                    helper.getView(R.id.re_video_ll).setVisibility(View.VISIBLE);
                    final String thumbUrl = QtalkStringUtils.addFilePathDomain(contentData.getVideoContent().ThumbUrl, true);
                    final String fileUrl = QtalkStringUtils.addFilePathDomain(contentData.getVideoContent().FileUrl, true);
                    final String fileSize = contentData.getVideoContent().FileSize;
                    final String downLoadPath = contentData.getVideoContent().FileUrl;
                    final String fileName = contentData.getVideoContent().FileName;
                    final String localPath = contentData.getVideoContent().LocalVideoOutPath;
                    final boolean onlyDownLoad = contentData.getVideoContent().newVideo;
                    ProfileUtils.displayLinkImgByImageSrc(mActivity, thumbUrl,mActivity.getDrawable(R.drawable.atom_ui_link_default), (ImageView) helper.getView(R.id.re_video_image),
                            Utils.dp2px(mActivity,144), Utils.dp2px(mActivity,144));
//                    ProfileUtils.displayGravatarByImageSrc(mActivity,thumbUrl ,   (ImageView)helper.getView(R.id.re_video_image),
//                                       mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_video_image), mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_video_image));
                    ((TextView)helper.getView(R.id.re_video_time)).setText(DurationUtils.format(Integer.parseInt(contentData.getVideoContent().Duration)));
                    helper.getView(R.id.re_video_image).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!TextUtils.isEmpty(localPath)&&new File(localPath).exists()){
                                VideoPlayUtil.conAndWwOpen((FragmentActivity) mActivity, localPath, fileName, thumbUrl, downLoadPath, !onlyDownLoad, fileSize);
                            }else {

                                VideoPlayUtil.conAndWwOpen((FragmentActivity) mActivity, fileUrl, fileName, thumbUrl, downLoadPath, !onlyDownLoad, fileSize);
                            }
//                            Intent intent = new Intent(mActivity, VideoPlayActivity.class);
//                            intent.putExtra(VideoPlayActivity.PLAYPATH,fileUrl);
//                            intent.putExtra(VideoPlayActivity.PLAYTHUMB,thumbUrl);
//                            intent.putExtra(VideoPlayActivity.DOWNLOADPATH,downLoadPath);
//                            intent.putExtra(VideoPlayActivity.SHOWSHARE,true);
//                            intent.putExtra(VideoPlayActivity.FILENAME,fileName);
//                            mActivity.startActivity(intent);
                        }
                    });
//                    ProfileUtils.displayLinkImgByImageSrc(mActivity, contentData.getLinkContent().img,mActivity.getDrawable(R.drawable.atom_ui_link_default), (ImageView) helper.getView(R.id.re_link_icon),
//                            imgsize, imgsize);
                    break;
                case link:
                    helper.getView(R.id.re_link_ll).setVisibility(View.VISIBLE);
                    ((TextView) helper.getView(R.id.re_link_title)).setText(contentData.getLinkContent().title);
                    int imgsize = Utils.dp2px(mActivity, 70);
                    if (TextUtils.isEmpty(contentData.getLinkContent().img)) {
                        helper.getView(R.id.re_link_icon).setBackground(mActivity.getDrawable(R.drawable.atom_ui_link_default));
                    } else {
                        ProfileUtils.displayLinkImgByImageSrc(mActivity, contentData.getLinkContent().img,mActivity.getDrawable(R.drawable.atom_ui_link_default), (ImageView) helper.getView(R.id.re_link_icon),
                                imgsize, imgsize);

                    }
                    helper.getView(R.id.re_link_ll).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String url = contentData.getLinkContent().linkurl;
                            Intent intent = new Intent(mActivity, QunarWebActvity.class);
                            intent.setData(Uri.parse(url));
                            mActivity.startActivity(intent);
                        }
                    });


                    break;
                case image:
                default:
                    //设置图片展示
                    if (contentData.getImgList().size() > 0) {
                        helper.getView(R.id.img_rc).setVisibility(View.VISIBLE);
//            List<MultiItemEntity> list = contentData.getImgList();
                        RecyclerView mRecyclerView = ((RecyclerView) helper.getView(R.id.img_rc));
                        int i = contentData.getImgList().size();
                        int column = 0;
                        int size = 0;
//            mRecyclerView.removeItemDecoration(gridSpacingItemDecoration);
                        if (i == 1) {
                            column = 1;
//                            gridSpacingItemDecoration = g1;
                        } else if (i < 5) {
                            column = 2;
//                            gridSpacingItemDecoration = g2;
                        } else {
                            column = 3;
//                            gridSpacingItemDecoration = g3;
                        }
                        size = Utils.getImageItemWidthForWorld(mActivity, column);
                        for (int j = contentData.getImgList().size() - 1; j >= 0; j--) {
                            if (contentData.getImgList().get(j) == null) {
                                contentData.getImgList().remove(i);
                            }
                        }


                        ReleaseCircleGridAdapter itemAdapter = new ReleaseCircleGridAdapter(contentData.getImgList(), mActivity, column);
                        GridLayoutManager manager = new GridLayoutManager(mActivity, column);
                        final int finalSize = size;
                        itemAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                                ImageBrowsUtil.openImageWorkWorld(position,contentData.getImgList(),mActivity);
                            }
                        });

//            mRecyclerView.removeItemDecoration();
//            mRecyclerView.invalidateItemDecorations();
//            mRecyclerView.addItemDecoration(gridSpacingItemDecoration);
                        mRecyclerView.setLayoutManager(manager);
                        mRecyclerView.setAdapter(itemAdapter);
                    } else {
                        helper.getView(R.id.img_rc).setVisibility(View.GONE);
                    }
                    break;
            }


        } catch (Exception e) {
            Logger.i("朋友圈功能展示出错:" + e.getMessage());
        }
    }


    private static List<IBrowsingConversationImageView.PreImage> parseList(List<ImageItemWorkWorldItem> list) {
        List<IBrowsingConversationImageView.PreImage> preImageList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            IBrowsingConversationImageView.PreImage image = new IBrowsingConversationImageView.PreImage();
            String url = list.get(i).getData();
            if (!(url.startsWith("http") || url.startsWith("https"))) {
                url = QtalkNavicationService.getInstance().getInnerFiltHttpHost() + "/" + url;
            }
            image.smallUrl = url;
            image.originUrl = url;
            image.localPath = url;
            preImageList.add(image);
        }

        return preImageList;
    }


    private static void showLikeState(final WorkWorldItem item, final BaseViewHolder helper, Activity mActivity) {
        if (item.getIsLike().equals("1")) {
            ((IconView) helper.getView(R.id.like_icon)).setTextColor(mActivity.getResources().getColor(R.color.atom_ui_new_like_select));
            ((IconView) helper.getView(R.id.like_icon)).setText(R.string.atom_ui_new_like_select);
//            #00CABE

        } else {
            ((IconView) helper.getView(R.id.like_icon)).setText(R.string.atom_ui_new_like);
            ((IconView) helper.getView(R.id.like_icon)).setTextColor(mActivity.getResources().getColor(R.color.atom_ui_light_gray_99));
        }
    }

    private static void showLikeNum(final WorkWorldItem item, final BaseViewHolder helper) {
        if (!TextUtils.isEmpty(item.getLikeNum() + "")) {
            try {
                if (Integer.parseInt(item.getLikeNum()) > 0) {
                    ((TextView) helper.getView(R.id.like_num)).setText(item.getLikeNum());
                } else {
                    ((TextView) helper.getView(R.id.like_num)).setText("顶");

                }
            } catch (Exception e) {
                ((TextView) helper.getView(R.id.like_num)).setText("顶");
            }


        } else {
            ((TextView) helper.getView(R.id.like_num)).setText("顶");
        }
    }

    //设置头像点击事件
    private static void setClickHeadName(final WorkWorldItem item, BaseViewHolder helper) {
//        helper.getView(R.id.user_header)
        if (item.getIsAnonymous().equals("0")) {
            helper.getView(R.id.user_header).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NativeApi.openUserCardVCByUserId(item.getOwner() + "@" + item.getOwnerHost());
                }
            });
            helper.getView(R.id.user_name).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NativeApi.openUserCardVCByUserId(item.getOwner() + "@" + item.getOwnerHost());
                }
            });
        } else {
            helper.getView(R.id.user_header).setOnClickListener(null);
            helper.getView(R.id.user_name).setOnClickListener(null);
        }
    }


    private static void showOutCommentList(BaseViewHolder helper, WorkWorldItem item, Activity mActivity,View.OnClickListener openDetailsListener) {
        //判断如何展示外部评论
        if (item.getAttachCommentList() != null && item.getAttachCommentList().size() > 0) {
            helper.getView(R.id.out_comment_rc).setVisibility(View.VISIBLE);


        } else {
            helper.getView(R.id.out_comment_rc).setVisibility(View.GONE);

//                helper.getView(R.id.out_comment_rc).setVisibility(View.GONE);
            if (!TextUtils.isEmpty(item.getAttachCommentListString())) {


                List<WorkWorldOutCommentBean> outList = JsonUtils.getGson().fromJson(item.getAttachCommentListString(), new TypeToken<List<WorkWorldOutCommentBean>>() {
                }.getType());
                item.setAttachCommentList(outList);
                if (item.getAttachCommentList() != null && item.getAttachCommentList().size() > 0) {
                    helper.getView(R.id.out_comment_rc).setVisibility(View.VISIBLE);


                } else {
                    item.setAttachCommentList(new ArrayList<WorkWorldOutCommentBean>());
                    helper.getView(R.id.out_comment_rc).setVisibility(View.GONE);
                }
            } else {
                item.setAttachCommentList(new ArrayList<WorkWorldOutCommentBean>());
                helper.getView(R.id.out_comment_rc).setVisibility(View.GONE);
            }
        }

        List<MultiItemEntity> list = new ArrayList<>();
        list.addAll(item.getAttachCommentList());
        if (Integer.parseInt(item.getCommentsNum()) > 5) {
//                helper.getView(R.id.more_layout).setVisibility(View.VISIBLE);
//                ((TextView) helper.getView(R.id.more_text)).setText("查看全部" + item.getCommentsNum() + "条评论");
//            if (!(item.getAttachCommentList().get(item.getAttachCommentList().size() - 1) instanceof WorkWorldOutOpenDetails)) {


            WorkWorldOutOpenDetails workWorldOutOpenDetails = new WorkWorldOutOpenDetails();
            ((WorkWorldOutOpenDetails) workWorldOutOpenDetails).setText("查看全部" + item.getCommentsNum() + "条评论");
            list.add(workWorldOutOpenDetails);
//                item.getAttachCommentList().add(workWorldOutOpenDetails);
//                item.setAttachCommentList(list);
//            }


        }
        WorkWorldDetailsAdapter outAdapter = new WorkWorldDetailsAdapter(list, mActivity);
        outAdapter.setOnOpenDetailsListener(openDetailsListener);
        outAdapter.setWorkWorldItem(item);
        ((RecyclerView) helper.getView(R.id.out_comment_rc)).setLayoutManager(new LinearLayoutManager(mActivity));
        ((RecyclerView) helper.getView(R.id.out_comment_rc)).setAdapter(outAdapter);
    }




    private static SpannableStringBuilder getSpannableStringBuild(String exstr, WorkWorldSpannableTextView textView, SpannableStringBuilder sb, final Activity mActivity, final View.OnClickListener openDetailsListener, final WorkWorldItem item) {


        boolean newTextView = true;
//        SpannableStringBuilder sb = new SpannableStringBuilder();
        List<Map<String, String>> list = ChatTextHelper.getObjList(exstr);
        for (final Map<String, String> map : list) {
            switch (map.get("type")) {
                case "emoticon":
                    if (newTextView) {
                        newTextView = false;
                        textView.setTag(R.string.atom_ui_title_add_emotion, null);
                    }
                    String value = map.get("value");

                    if (TextUtils.isEmpty(value)) {
                        break;
                    }
                    String ext = map.get("extra");
                    String pkgId = "";
                    if (ext != null && ext.contains("width")) {
                        String[] str = ext.trim().split("\\s+");
                        if (str.length > 1) {
                            //处理width = 240.000000　问题
                            pkgId = str[0].substring(str[0].indexOf("width") + 6);
                        }
                    }
                    String shortcut = value.substring(1, value.length() - 1);
                    EmoticonEntity emotionEntry = EmotionUtils.getEmoticionByShortCut(shortcut, pkgId, true);
                    if (emotionEntry != null) {
                        String path = emotionEntry.fileOrg;
                        if (!TextUtils.isEmpty(path)) {
                            Parcelable cached = MemoryCache.getMemoryCache(path);
                            if (cached == null) {
                                InputStream is = null;
                                int imgSize = emotionEntry.showAll ? iconSize : defaultSize;
                                try {
                                    if (path.startsWith("emoticons") || path.startsWith("Big_Emoticons")) {
                                        is = mActivity.getAssets().open(path);
                                    } else {
                                        is = new FileInputStream(path);
                                    }
                                    ImageUtils.ImageType type = ImageUtils.adjustImageType(
                                            FileUtils.toByteArray(new File(path), 4));
                                    if (emotionEntry.fileFiexd.endsWith(".gif")) {
                                        type = ImageUtils.ImageType.GIF;
                                    }

                                    if (type == ImageUtils.ImageType.GIF) {
                                        cached = new AnimatedGifDrawable(is, imgSize);
                                    } else {
                                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                                        Matrix matrix = new Matrix();
                                        matrix.postScale(imgSize / bitmap.getWidth(),
                                                imgSize / bitmap.getHeight());
                                        cached = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                                                bitmap.getHeight(), matrix, true);
                                        if (cached != bitmap) {
                                            bitmap.recycle();
                                        }
                                    }
                                    MemoryCache.addObjToMemoryCache(path, cached);
                                } catch (IOException e) {
//                                    LogUtil.e(TAG, "ERROR", e);
                                    Logger.i(e.getMessage());
                                } finally {
                                    if (is != null) {
                                        try {
                                            is.close();
                                        } catch (IOException e) {
                                            Logger.i(e.getMessage());
//                                            LogUtil.e(TAG, "ERROR", e);
                                        }
                                    }
                                }
                            }
                            if (cached != null) {
                                DynamicDrawableSpan span;
                                if (cached instanceof AnimatedGifDrawable) {
                                    WeakReference<TextView> weakReference = new WeakReference<TextView>(textView);
                                    span = new AnimatedImageSpan((Drawable) cached, weakReference);
                                    if (textView.getTag(R.string.atom_ui_title_add_emotion) == null) {
                                        ((AnimatedImageSpan) span).setListener(new TextMessageProcessor.GifListener(weakReference));
                                        textView.setTag(R.string.atom_ui_title_add_emotion, 1);
                                    }
                                } else {
                                    span = new ImageSpan(mActivity, (Bitmap) cached);
                                }
                                SpannableString spannableString = new SpannableString(shortcut);
                                spannableString.setSpan(span, 0, spannableString.length(),
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                sb.append(spannableString);
                            }
                        } else if (path != null) {
                            //牛驼表情和其他非默认表情
                            //每当新创建ImageView的时候都要将当前的TextView放入parent
                            if (textView != null && sb.length() > 0) {
                                newTextView = true;
                                textView.setText(sb);
//                                parent.addView(textView);
                                textView = null;
//                                sb.clear();
                            }
//                            if (path.startsWith("Big_Emoticons/")) {//内置大图逻辑
//                                String p = "file:///android_asset/" + path;
//                                LoadingImgView bigEmoticons = getLoadingImgView(context, 256, 256, p, p, message.getDirection());
//                                bigEmoticons.finish();
//                                parent.addView(bigEmoticons);
//                            } else {
//                                SimpleDraweeView emojiView = getSimpleDraweeView(new File(path), context);
//                                //牛驼表情的特殊处理,240 * 240px
//                                emojiView.setLayoutParams(new LinearLayout.LayoutParams(256, 256));
//                                emojiView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//                                parent.addView(emojiView);
//                            }
                        } else {
                            sb.append(value);
                        }
                    } else {
//                        SimpleDraweeView emojiView;
//                        if (TextUtils.isEmpty(pkgId)) {
//                            emojiView = getSimpleDraweeView(context,
//                                    QtalkStringUtils.addFilePathDomain("/file/v2/emo/d/oe/"
//                                            + shortcut
//                                            + "/org"), null, 128, 128, -1);
//                        } else {
//                            emojiView = getSimpleDraweeView(context,
//                                    QtalkStringUtils.addFilePathDomain("/file/v2/emo/d/e/"
//                                            + pkgId
//                                            + "/"
//                                            + shortcut + "/org"), null, 128, 128, -1);
//                        }
//                        emojiView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//                        parent.addView(emojiView);
                    }
                    break;
                case "url":
                    if (newTextView) {
                        newTextView = false;
                    }
                    final String url = map.get("value");
                    SpannableString textSpannable = new SpannableString(url);
                    ClickableSpan span = new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {

                            View v = (View) widget.getParent();
                            if (v.getTag(R.string.atom_ui_voice_hold_to_talk) != null) {
                                v.setTag(R.string.atom_ui_voice_hold_to_talk, null);
                                return;
                            }
//                            if (widget instanceof EmojiconTextView) {
                                Intent intent = new Intent(mActivity, QunarWebActvity.class);
                                intent.setData(Uri.parse(url));
                                mActivity.startActivity(intent);
//                            }
                        }
                    };
                    SpannableString spannableString = new SpannableString(url);
                    spannableString.setSpan(span, 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    sb.append(spannableString);
                    break;
                case "text":
                    String v = map.get("value");
                    if (TextUtils.isEmpty(v.trim())) {
                        break;
                    }
                    if (newTextView) {
                        newTextView = false;
//                        textView = ViewPool.getView(EmojiconTextView.class, context);
                    }

                    sb.append(v);
//                    if (v.length() > 1024) {
//                        sb.append(v);
//                    } else {
//                        SpannableString textSpannable = new SpannableString(v);
//                        ClickableSpan clickableSpan = new ClickableSpan() {
//                            @Override
//                            public void onClick(View widget) {
//                                widget.setTag(item);
//                                openDetailsListener.onClick(widget);
//                            }
//
//                            @Override
//                            public void updateDrawState(@NonNull TextPaint ds) {
//                                super.updateDrawState(ds);
//                                ds.setColor(mActivity.getResources().getColor(R.color.atom_ui_light_gray_33));
//                                ds.setUnderlineText(false);
//                                ds.clearShadowLayer();
//                            }
//                        };
//
//                        textSpannable.setSpan(clickableSpan, 0, textSpannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
////                        ClickableSpan
//
////                        Linkify.addLinks(textSpannable, Linkify.WEB_URLS |
////                                Linkify.EMAIL_ADDRESSES | Linkify.PHONE_NUMBERS);
//                        sb.append(textSpannable);
////                        textView.setMovementMethod(LinkMovementClickMethod.getInstance());//长按事件与Spannable点击冲突
//
//
////                        textView.setMovementMethod(LinkMovementClickMethod.getInstance());//长按事件与Spannable点击冲突
//                    }
                    break;
            }


        }


        return sb;
    }
}
