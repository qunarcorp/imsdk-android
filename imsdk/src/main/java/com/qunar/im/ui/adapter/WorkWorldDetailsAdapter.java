package com.qunar.im.ui.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
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
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.module.MultiItemEntity;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.module.ReleaseContentData;
import com.qunar.im.base.module.SetLikeData;
import com.qunar.im.base.module.SetLikeDataResponse;
import com.qunar.im.base.module.WorkWorldChildCommentBean;
import com.qunar.im.base.module.WorkWorldDeleteResponse;
import com.qunar.im.base.module.WorkWorldDetailsLabelData;
import com.qunar.im.base.module.WorkWorldItem;
import com.qunar.im.base.module.WorkWorldNewCommentBean;
import com.qunar.im.base.module.WorkWorldNoticeItem;
import com.qunar.im.base.module.WorkWorldOutOpenDetails;
import com.qunar.im.base.util.Constants;
import com.qunar.im.ui.presenter.views.WorkWorldDetailsView;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.structs.MessageStatus;
import com.qunar.im.base.structs.WorkWorldItemState;
import com.qunar.im.base.util.ChatTextHelper;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.ui.util.EmotionUtils;
import com.qunar.im.base.util.FileUtils;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.MemoryCache;
import com.qunar.im.ui.util.ProfileUtils;
import com.qunar.im.base.util.graphics.ImageUtils;
import com.qunar.im.base.view.faceGridView.EmoticonEntity;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.QunarWebActvity;
import com.qunar.im.ui.activity.WorkWorldDetailsActivity;
import com.qunar.im.ui.view.IconView;
import com.qunar.im.ui.view.WorkWorldLinkTouchMovementMethod;
import com.qunar.im.ui.view.WorkWorldSpannableTextView;
import com.qunar.im.ui.view.baseView.AnimatedGifDrawable;
import com.qunar.im.ui.view.baseView.AnimatedImageSpan;
import com.qunar.im.ui.view.baseView.processor.TextMessageProcessor;
import com.qunar.im.ui.view.recyclerview.BaseMultiItemQuickAdapter;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.qunar.im.base.structs.MessageType.image;
import static com.qunar.im.base.structs.MessageType.link;
import static com.qunar.im.base.structs.MessageType.video;
import static com.qunar.im.ui.activity.WorkWorldDetailsActivity.WORK_WORLD_DETAILS_ITEM;

public class WorkWorldDetailsAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {


    public static final int COMMENT = 1;
    public static final int NOTICE = 2;
    public static final int INSTRUCTIONS = 3;
    public static final int OUT_COMMETN = 4;
    public static final int CHILD_COMMENT = 5;
    public static final int OUT_OPEN_DETAILS = 6;

    public int deleteLine = 0;
    private static String defaultHeadUrl = QtalkNavicationService.getInstance().getInnerFiltHttpHost() + "/file/v2/download/perm/3ca05f2d92f6c0034ac9aee14d341fc7.png";

    public String postOwner;
    public String postOwnerHost;


    private View.OnClickListener onClickListener;
    private View.OnClickListener setLikeOnClickListener;
    private View.OnClickListener openDetailsListener;
    private Activity mActivity;
    private LinearLayoutManager linearLayoutManager;

    private WorkWorldItem baseItem;

    private int commentCount = 0;

    private int iconSize;
    private int defaultSize;


    private boolean isMindMessage = true;


    /**
     * 分别为我的消息,或者自身查询相关数据 我的帖子 我的回复 @提到我
     *
     * @param flag
     */
    public void setIsMindMessage(boolean flag) {
        this.isMindMessage = flag;
    }

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public WorkWorldDetailsAdapter(List<? extends MultiItemEntity> data, Activity mActivity) {
        super((List<MultiItemEntity>) data);
        addItemType(COMMENT, R.layout.atom_ui_work_world_details_comment_item);
        addItemType(NOTICE, R.layout.atom_ui_work_world_notice_item);
        addItemType(INSTRUCTIONS, R.layout.atom_ui_work_world_details_comment_layout_show);
        addItemType(OUT_COMMETN, R.layout.atom_ui_work_world_out_comment_item);
        addItemType(CHILD_COMMENT, R.layout.atom_ui_work_world_details_comment_child_item);
        addItemType(OUT_OPEN_DETAILS, R.layout.atom_ui_work_world_out_open_details);
        this.mActivity = mActivity;

        defaultSize = com.qunar.im.base.util.Utils.dipToPixels(QunarIMApp.getContext(), 96);
        iconSize = com.qunar.im.base.util.Utils.dpToPx(QunarIMApp.getContext(), 32);

    }

    public void setDeleteLine(int i) {
        this.deleteLine = i;
    }

    public void setPostOwnerAndHost(String owner, String host) {
        this.postOwner = owner;
        this.postOwnerHost = host;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.onClickListener = listener;
    }

    public void setSetLikeOnClickListener(View.OnClickListener likeOnClickListener) {
        this.setLikeOnClickListener = likeOnClickListener;
    }

    public void setOnOpenDetailsListener(View.OnClickListener openDetailsListener) {
        this.openDetailsListener = openDetailsListener;

    }

    public void setWorkWorldItem(WorkWorldItem item) {
        this.baseItem = item;
    }


    //设置头像点击事件
    private void setClickHeadName(final WorkWorldNewCommentBean item, BaseViewHolder helper) {
//        helper.getView(R.id.user_header)
        if (item.getIsAnonymous().equals("0")) {
            helper.getView(R.id.user_header).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NativeApi.openUserCardVCByUserId(item.getFromUser() + "@" + item.getFromHost());
                }
            });
            helper.getView(R.id.user_name).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NativeApi.openUserCardVCByUserId(item.getFromUser() + "@" + item.getFromHost());
                }
            });
        } else {
            helper.getView(R.id.user_header).setOnClickListener(null);
            helper.getView(R.id.user_name).setOnClickListener(null);
        }
    }


    //设置头像点击事件
    private void setClickHeadName(final WorkWorldNoticeItem item, final BaseViewHolder helper) {
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

    @SuppressLint("ResourceAsColor")
    @Override
    protected void convert(final BaseViewHolder helper, final MultiItemEntity item) {
        switch (helper.getItemViewType()) {

            case OUT_OPEN_DETAILS:
                WorkWorldOutOpenDetails outOpenDetails = (WorkWorldOutOpenDetails) item;
                ((TextView) helper.getView(R.id.more_text)).setText(outOpenDetails.getText());
                helper.itemView.setTag(baseItem);
                helper.itemView.setOnClickListener(openDetailsListener);

                break;

            case CHILD_COMMENT:
                final WorkWorldNewCommentBean dataC = (WorkWorldNewCommentBean) item;
                final String xmppidC = dataC.getFromUser() + "@" + dataC.getFromHost();


                if (dataC.getIsAnonymous().equals("0")) {
                    ((TextView) helper.getView(R.id.user_architecture)).setVisibility(View.VISIBLE);
                    ConnectionUtil.getInstance().getUserCard(xmppidC, new IMLogicManager.NickCallBack() {
                        @Override
                        public void onNickCallBack(final Nick nick) {
//                ((Activity) mContext).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
                            if (nick != null) {

                                ProfileUtils.displayGravatarByImageSrc(mActivity, nick.getHeaderSrc(), (ImageView) helper.getView(R.id.user_header),
                                        mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head), mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head));
                                ((TextView) helper.getView(R.id.user_name)).setText(TextUtils.isEmpty(nick.getName()) ? xmppidC : nick.getName());
                            } else {
                                ProfileUtils.displayGravatarByImageSrc(mActivity, defaultHeadUrl, (ImageView) helper.getView(R.id.user_header),
                                        mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head), mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head));
                                ((TextView) helper.getView(R.id.user_name)).setText(xmppidC);
                            }
                            ((TextView) helper.getView(R.id.user_architecture)).setText(QtalkStringUtils.architectureParsing(nick.getDescInfo()));

//                    }
//                });


                        }
                    }, false, false);

                    ((TextView) helper.getView(R.id.user_name)).setTextColor(mActivity.getResources().getColor(R.color.atom_ui_new_color_4DC1B5));
                } else {
                    ProfileUtils.displayGravatarByImageSrc(mActivity, dataC.getAnonymousPhoto(), (ImageView) helper.getView(R.id.user_header),
                            mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head), mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head));
                    ((TextView) helper.getView(R.id.user_name)).setText(dataC.getAnonymousName());
                    ((TextView) helper.getView(R.id.user_name)).setTextColor(mActivity.getResources().getColor(R.color.atom_ui_light_gray_99));
                    ((TextView) helper.getView(R.id.user_architecture)).setVisibility(View.GONE);
                }
                showIsLike(dataC, helper);
                showLikeNum(dataC, helper);
                showContentText(dataC, helper);
                setClickHeadName(dataC, helper);
//                helper.getView(R.id.comment_item_text).setTag(data);
//                helper.getView(R.id.comment_item_text).setOnClickListener(onClickListener);
//                ((TextView)helper.getView(R.id.comment_item_text)).setTag(dataC);
//                ((TextView)helper.getView(R.id.comment_item_text)).setOnClickListener(onClickListener);
                helper.itemView.setTag(dataC);
                helper.itemView.setOnClickListener(onClickListener);
//


                //设置点赞单击事件
                helper.getView(R.id.comment_item_like_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final SetLikeData setLikeData = new SetLikeData();
                        setLikeData.setLikeType(dataC.getIsLike().equals("1") ? 0 : 1);
                        setLikeData.setOpType(2);
                        setLikeData.setCommentId(dataC.getCommentUUID());
                        setLikeData.setPostId(dataC.getPostUUID());
                        setLikeData.setLikeId("2-" + UUID.randomUUID().toString().replace("-", ""));
                        setLikeData.setPostOwner(postOwner);
                        setLikeData.setPostOwnerHost(postOwnerHost);
                        setLikeData.setSuperParentUUID(dataC.getSuperParentUUID());
                        HttpUtil.setLike(setLikeData, new ProtocolCallback.UnitCallback<SetLikeDataResponse>() {
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
                                        dataC.setIsLike(setLikeDataResponse.getData().getIsLike() + "");
                                        dataC.setLikeNum(setLikeDataResponse.getData().getLikeNum() + "");
                                        showIsLike(dataC, helper);
                                        showLikeNum(dataC, helper);
                                        ((WorkWorldDetailsView) mActivity).updateOutCommentList(setLikeDataResponse.getData().getAttachCommentList());
                                        ((WorkWorldDetailsView) mActivity).saveData();
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


                break;

            case OUT_COMMETN:
                final WorkWorldNewCommentBean data = (WorkWorldNewCommentBean) item;

                showLikeNumOutComment(data, helper);
                showOutContentText(data, helper);


                helper.itemView.setTag(baseItem);
                helper.itemView.setOnClickListener(openDetailsListener);

//                setClickHeadName(data,helper);
//                helper.getView(R.id.comment_item_text).setTag(data);
//                helper.getView(R.id.comment_item_text).setOnClickListener(onClickListener);
//                helper.itemView.setTag(data);
//                helper.itemView.setOnClickListener(onClickListener);
//


                //设置点赞单击事件
//                helper.getView(R.id.comment_item_like_layout).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        SetLikeData setLikeData = new SetLikeData();
//                        setLikeData.setLikeType(data.getIsLike().equals("1") ? 0 : 1);
//                        setLikeData.setOpType(2);
//                        setLikeData.setCommentId(data.getCommentUUID());
//                        setLikeData.setPostId(data.getPostUUID());
//                        setLikeData.setLikeId("2-"+UUID.randomUUID().toString().replace("-",""));
//                        setLikeData.setPostOwner(postOwner);
//                        setLikeData.setPostOwnerHost(postOwnerHost);
//                        HttpUtil.setLike(setLikeData, new ProtocolCallback.UnitCallback<SetLikeDataResponse>() {
//                            @Override
//                            public void onCompleted(final SetLikeDataResponse setLikeDataResponse) {
////                            mActivity.runOnUiThread(new Runnable() {
////                                @Override
////                                public void run() {
//                                if(setLikeDataResponse==null){
//                                    return;
//                                }
//                                mActivity.runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        data.setIsLike(setLikeDataResponse.getData().getIsLike() + "");
//                                        data.setLikeNum(setLikeDataResponse.getData().getLikeNum() + "");
//                                        showIsLike(data, helper);
//                                        showLikeNum(data, helper);
//                                    }
//                                });
//
////                                }
////                            });
//                            }
//
//                            @Override
//                            public void onFailure(String errMsg) {
//
//                            }
//                        });
//                    }
//                });
                break;
            case COMMENT:
                final WorkWorldNewCommentBean data1 = (WorkWorldNewCommentBean) item;
                final String xmppid1 = data1.getFromUser() + "@" + data1.getFromHost();


                if (data1.getIsAnonymous().equals("0")) {
                    ((TextView) helper.getView(R.id.user_architecture)).setVisibility(View.VISIBLE);
                    ConnectionUtil.getInstance().getUserCard(xmppid1, new IMLogicManager.NickCallBack() {
                        @Override
                        public void onNickCallBack(final Nick nick) {
//                ((Activity) mContext).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
                            if (nick != null) {

                                ProfileUtils.displayGravatarByImageSrc(mActivity, nick.getHeaderSrc(), (ImageView) helper.getView(R.id.user_header),
                                        mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head), mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head));
                                ((TextView) helper.getView(R.id.user_name)).setText(TextUtils.isEmpty(nick.getName()) ? xmppid1 : nick.getName());
                            } else {
                                ProfileUtils.displayGravatarByImageSrc(mActivity, defaultHeadUrl, (ImageView) helper.getView(R.id.user_header),
                                        mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head), mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head));
                                ((TextView) helper.getView(R.id.user_name)).setText(xmppid1);
                            }
                            ((TextView) helper.getView(R.id.user_architecture)).setText(QtalkStringUtils.architectureParsing(nick.getDescInfo()));

//                    }
//                });


                        }
                    }, false, false);

                    ((TextView) helper.getView(R.id.user_name)).setTextColor(mActivity.getResources().getColor(R.color.atom_ui_new_color_4DC1B5));
                } else {
                    ProfileUtils.displayGravatarByImageSrc(mActivity, data1.getAnonymousPhoto(), (ImageView) helper.getView(R.id.user_header),
                            mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head), mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head));
                    ((TextView) helper.getView(R.id.user_name)).setText(data1.getAnonymousName());
                    ((TextView) helper.getView(R.id.user_name)).setTextColor(mActivity.getResources().getColor(R.color.atom_ui_light_gray_99));
                    ((TextView) helper.getView(R.id.user_architecture)).setVisibility(View.GONE);
                }
                showIsLike(data1, helper);
                showLikeNum(data1, helper);
                showContentText(data1, helper);
                setClickHeadName(data1, helper);
//                helper.getView(R.id.comment_item_text).setTag(data);
//                helper.getView(R.id.comment_item_text).setOnClickListener(onClickListener);
                helper.itemView.setTag(data1);
                helper.itemView.setOnClickListener(onClickListener);
//


                //设置点赞单击事件
                helper.getView(R.id.comment_item_like_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SetLikeData setLikeData = new SetLikeData();
                        setLikeData.setLikeType(data1.getIsLike().equals("1") ? 0 : 1);
                        setLikeData.setOpType(2);
                        setLikeData.setCommentId(data1.getCommentUUID());
                        setLikeData.setPostId(data1.getPostUUID());
                        setLikeData.setLikeId("2-" + UUID.randomUUID().toString().replace("-", ""));
                        setLikeData.setPostOwner(postOwner);
                        setLikeData.setPostOwnerHost(postOwnerHost);
                        HttpUtil.setLike(setLikeData, new ProtocolCallback.UnitCallback<SetLikeDataResponse>() {
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
                                        data1.setIsLike(setLikeDataResponse.getData().getIsLike() + "");
                                        data1.setLikeNum(setLikeDataResponse.getData().getLikeNum() + "");
                                        showIsLike(data1, helper);
                                        showLikeNum(data1, helper);
                                        ((WorkWorldDetailsView) mActivity).updateOutCommentList(setLikeDataResponse.getData().getAttachCommentList());
                                        ((WorkWorldDetailsView) mActivity).saveData();
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


                WorkWorldDetailsAdapter outAdapter = new WorkWorldDetailsAdapter(new ArrayList<MultiItemEntity>(), mActivity);
//                if(data1.getNewChild()!=null&&data1.getNewChild().size()>0){
                helper.getView(R.id.child_comment_rc).setVisibility(View.VISIBLE);
                outAdapter.setOnClickListener(onClickListener);
                ((RecyclerView) helper.getView(R.id.child_comment_rc)).setLayoutManager(new LinearLayoutManager(mActivity));
                ((RecyclerView) helper.getView(R.id.child_comment_rc)).setAdapter(outAdapter);
                ((RecyclerView) helper.getView(R.id.child_comment_rc)).setNestedScrollingEnabled(false);

//                }else{
//                    helper.getView(R.id.child_comment_rc).setVisibility(View.GONE);
//                }
                helper.getView(R.id.child_comment_rc).setTag(outAdapter);

                //判断如何展示子级评论
                if (data1.getNewChild() != null && data1.getNewChild().size() > 0) {
//                    helper.getView(R.id.child_comment_rc).setVisibility(View.VISIBLE);
//                    WorkWorldDetailsAdapter outAdapter = new WorkWorldDetailsAdapter(data1.getNewChild(), mActivity);
                    List<? extends MultiItemEntity> list = data1.getNewChild();
                    outAdapter.setNewData((List<MultiItemEntity>) list);
//                    helper.getView(R.id.child_comment_rc).setTag(outAdapter);

                } else {
//                    helper.getView(R.id.child_comment_rc).setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(data1.getNewChildString())) {


                        List<WorkWorldChildCommentBean> outList = JsonUtils.getGson().fromJson(data1.getNewChildString(), new TypeToken<List<WorkWorldChildCommentBean>>() {
                        }.getType());
                        data1.setNewChild(outList);
                        if (data1.getNewChild() != null && data1.getNewChild().size() > 0) {
                            helper.getView(R.id.child_comment_rc).setVisibility(View.VISIBLE);
//                            WorkWorldDetailsAdapter outAdapter = new WorkWorldDetailsAdapter(data1.getNewChild(), mActivity);
//                            outAdapter.setOnClickListener(onClickListener);
//                            ((RecyclerView) helper.getView(R.id.child_comment_rc)).setLayoutManager(new LinearLayoutManager(mActivity));
//                            ((RecyclerView) helper.getView(R.id.child_comment_rc)).setAdapter(outAdapter);
//                            ((RecyclerView) helper.getView(R.id.child_comment_rc)).setNestedScrollingEnabled(false);
                            List<? extends MultiItemEntity> list = data1.getNewChild();
                            outAdapter.setNewData((List<MultiItemEntity>) list);


                        }
                    }
                }

                //此处-1是因为还有head 要把head 减去
                if (((helper.getAdapterPosition() - 1) == deleteLine) || ((helper.getAdapterPosition() - 1) == getData().size() - 1)) {
                    helper.getView(R.id.deleteLine).setVisibility(View.GONE);
                } else {
                    helper.getView(R.id.deleteLine).setVisibility(View.VISIBLE);
                }


//                child_comment_rc


//                user_header
//                        user_name
//                user_architecture
//                        comment_item_like_layout
//                comment_item_like_icon
//                        comment_item_like_text
//                comment_item_text
                break;
            case NOTICE:

                PublicWorkWorldAdapterDraw.showNoticeInit(helper, (WorkWorldNoticeItem) item, mActivity);
//                showNoticeInit(helper, (WorkWorldNoticeItem) item);
                break;

            case INSTRUCTIONS:
                WorkWorldDetailsLabelData commentDetails = (WorkWorldDetailsLabelData) item;
                ((TextView) helper.getView(R.id.comment_text)).setText(commentDetails.getName());
//                if (commentDetails.getCount() > 0) {
//                    ((TextView) helper.getView(R.id.comment_count)).setText("(" + commentDetails.getCount() + ")");
//                } else {
//                    ((TextView) helper.getView(R.id.comment_count)).setText("");
//                }

                break;
        }
    }

    private void showNoticeInit(final BaseViewHolder helper, WorkWorldNoticeItem item) {
        String ixmppid = "";
        final WorkWorldNoticeItem workWorldNoticeItem = item;
        if (isMindMessage) {


//                if (workWorldNoticeItem.getEventType().equals(Constants.WorkWorldState.NOTICE)||workWorldNoticeItem.getEventType().equals(Constants.WorkWorldState.COMMENTATMESSAGE)) {
//                    ixmppid = workWorldNoticeItem.getUserFrom() + "@" + workWorldNoticeItem.getUserFromHost();
//                } else if (workWorldNoticeItem.getEventType().equals(Constants.WorkWorldState.WORKWORLDATMESSAGE)) {
//                    ixmppid = workWorldNoticeItem.getOwner() + "@" + workWorldNoticeItem.getOwnerHost();
//                }

            setClickHeadName(workWorldNoticeItem, helper);

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
                                        helper.getView(R.id.text_item).setVisibility(View.VISIBLE);
                                        helper.getView(R.id.play_button).setVisibility(View.GONE);
                                        break;
                                    case image:
                                        String url = contentData1.getImgList().get(0).getData();
//                                img_item


                                        ProfileUtils.displaySquareByImageSrc(mActivity, url, (ImageView) helper.getView(R.id.img_item),
                                                mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_56dp), mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_56dp));
                                        helper.getView(R.id.play_button).setVisibility(View.GONE);
                                        helper.getView(R.id.img_item).setVisibility(View.VISIBLE);
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
                                Toast.makeText(mActivity, "加载中,请稍后...", Toast.LENGTH_SHORT).show();
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

        } else {

        }
    }


    private void showIsLike(final WorkWorldNewCommentBean item, final BaseViewHolder helper) {
        if (item.getIsLike().equals("1")) {
            ((IconView) helper.getView(R.id.comment_item_like_icon)).setTextColor(mActivity.getResources().getColor(R.color.atom_ui_new_like_select));
            ((IconView) helper.getView(R.id.comment_item_like_icon)).setText(R.string.atom_ui_new_like_select);
//            #00CABE

        } else {
            ((IconView) helper.getView(R.id.comment_item_like_icon)).setText(R.string.atom_ui_new_like);
            ((IconView) helper.getView(R.id.comment_item_like_icon)).setTextColor(mActivity.getResources().getColor(R.color.atom_ui_light_gray_99));
        }
    }

    private void showLikeNum(final WorkWorldNewCommentBean item, final BaseViewHolder helper) {
        if (!TextUtils.isEmpty(item.getLikeNum())) {
            try {
                if (Integer.parseInt(item.getLikeNum()) > 0) {
                    ((TextView) helper.getView(R.id.comment_item_like_text)).setText(item.getLikeNum());
                } else {
                    ((TextView) helper.getView(R.id.comment_item_like_text)).setText("顶");

                }
            } catch (Exception e) {
                ((TextView) helper.getView(R.id.comment_item_like_text)).setText("顶");
            }


        } else {
            ((TextView) helper.getView(R.id.comment_item_like_text)).setText("顶");
        }
    }

    private void showLikeNumOutComment(final WorkWorldNewCommentBean item, final BaseViewHolder helper) {
        if (!TextUtils.isEmpty(item.getLikeNum())) {
            try {
                if (Integer.parseInt(item.getLikeNum()) > 0) {
                    ((TextView) helper.getView(R.id.comment_item_like_text)).setText(item.getLikeNum() + " 赞");
                } else {
                    ((TextView) helper.getView(R.id.comment_item_like_text)).setText("0 赞");

                }
            } catch (Exception e) {
                ((TextView) helper.getView(R.id.comment_item_like_text)).setText("0 赞");
            }


        } else {
            ((TextView) helper.getView(R.id.comment_item_like_text)).setText("0 赞");
        }
    }


//    //展示评论内容
//    private void showChildContentText(WorkWorldNewCommentBean data, final BaseViewHolder helper){
//        if(MessageStatus.isExistStatus(Integer.parseInt(data.getCommentStatus()),WorkWorldItemState.commentShow)) {
////            TextView textView = linearLayoutManager.findViewByPosition(position + getHeaderLayoutCount()).findViewById(R.id.comment_item_text);
//
//
//            final String content = data.getContent();
//            final String isAnonymous = data.getToisAnonymous();
//            final String xmppid = data.getToUser() + "@" + data.getToHost();
//            final String anonymousName = data.getToAnonymousName();
//            if (TextUtils.isEmpty(data.getToUser())) {
//                ((TextView) helper.getView(R.id.comment_item_text)).setText(data.getContent());
//            } else {
//                com.orhanobut.logger.Logger.i("data有可能为null:" + data);
//
//                if (CurrentPreference.getInstance().getPreferenceUserId().equals(xmppid)) {
//
//                    ((TextView) helper.getView(R.id.comment_item_text)).setText(Html.fromHtml("<font color='#999999'>回复</font> <font color='#4dc1b5'>我</font> " + content));
//                } else {
//
//
//                    if ("0".equals(isAnonymous)) {
//
//                        ((TextView) helper.getView(R.id.comment_item_text)).setText(data.getContent());
//                        ConnectionUtil.getInstance().getUserCard(xmppid, new IMLogicManager.NickCallBack() {
//                            @Override
//                            public void onNickCallBack(final Nick nick) {
////                ((Activity) mContext).runOnUiThread(new Runnable() {
////                    @Override
////                    public void run() {
//                                if (nick != null) {
//                                    ((TextView) helper.getView(R.id.comment_item_text)).setText(Html.fromHtml("<font color='#999999'>回复</font> <font color='#4dc1b5'>" + nick.getName() + "</font> " + content));
//                                } else {
//                                    ((TextView) helper.getView(R.id.comment_item_text)).setText(Html.fromHtml("<font color='#999999'>回复</font> <font color='#4dc1b5'>" + nick.getXmppId() + "</font> " + content));
//                                }
////                    }
////                });
//
//
//                            }
//                        }, false, false);
//
//
//                    } else {
//                        ((TextView) helper.getView(R.id.comment_item_text)).setText(Html.fromHtml("<font color='#999999'>回复</font> <font color='#999999'>" + anonymousName + "</font> " + content));
//                    }
//                }
//            }
//        }else{
//            ((TextView)helper.getView(R.id.comment_item_text)).setText("该评论已删除!");
//////
//        }
//
//    }


    //展示评论内容
    private void showOutContentText(WorkWorldNewCommentBean data, final BaseViewHolder helper) {

        final SpannableStringBuilder A = new SpannableStringBuilder();
        final SpannableStringBuilder B = new SpannableStringBuilder();
        final SpannableStringBuilder C = new SpannableStringBuilder();
        final SpannableStringBuilder all = new SpannableStringBuilder();

        final String xmppid = data.getFromUser() + "@" + data.getFromHost();


//                ((TextView) helper.getView(R.id.user_architecture)).setVisibility(View.GONE);
        if (data.getIsAnonymous().equals("0")) {

            ConnectionUtil.getInstance().getUserCard(xmppid, new IMLogicManager.NickCallBack() {
                @Override
                public void onNickCallBack(final Nick nick) {
//                ((Activity) mContext).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
                    A.clear();
                    if (nick != null) {

                        A.append(Html.fromHtml("<font color='#999999'>" + (TextUtils.isEmpty(nick.getName()) ? xmppid : nick.getName()) + ": </font>"));
//                        ((TextView) helper.getView(R.id.user_name)).setText();
                    } else {
//                        ((TextView) helper.getView(R.id.user_name)).setText();
                        A.append(Html.fromHtml("<font color='#999999'>" + xmppid + ": </font>"));
                    }
                    showSpann(all, A, B, C, helper);
//                    }
//                });


                }
            }, false, false);

//            ((TextView) helper.getView(R.id.user_name)).setTextColor(mActivity.getResources().getColor(R.color.atom_ui_new_color_4DC1B5));

        } else {
            A.clear();
            A.append(Html.fromHtml("<font color='#999999'>" + data.getAnonymousName() + ": </font>"));
            all.append(A);
            all.append(B);
            all.append(C);
            ((TextView) helper.getView(R.id.comment_item_text)).setText(all);
//            ((TextView) helper.getView(R.id.user_name)).setText(data.getAnonymousName() + ":");
//            ((TextView) helper.getView(R.id.user_name)).setTextColor(mActivity.getResources().getColor(R.color.atom_ui_light_gray_99));
        }


        String msg = ChatTextHelper.textToHTML(data.getContent());


        WorkWorldSpannableTextView textView = ((WorkWorldSpannableTextView) helper.getView(R.id.comment_item_text));
        textView.setTag(baseItem);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDetailsListener.onClick(v);
            }
        });
//        if (TextUtils.isEmpty(msg)) {
////            str = contentData.getContent();
//            textView.setText(contentData.getContent());
//            return;
//        }
        boolean newTextView = true;

//        final SpannableStringBuilder B = new SpannableStringBuilder();
        C.clear();
        List<Map<String, String>> list = ChatTextHelper.getObjList(msg);
        for (Map<String, String> map : list) {
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
                                        is = mContext.getAssets().open(path);
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
                                    LogUtil.e(TAG, "ERROR", e);
                                } finally {
                                    if (is != null) {
                                        try {
                                            is.close();
                                        } catch (IOException e) {
                                            LogUtil.e(TAG, "ERROR", e);
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
                                    span = new ImageSpan(mContext, (Bitmap) cached);
                                }
                                SpannableString spannableString = new SpannableString(shortcut);
                                spannableString.setSpan(span, 0, spannableString.length(),
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                B.append(spannableString);
                            }
                        } else if (path != null) {
                            //牛驼表情和其他非默认表情
                            //每当新创建ImageView的时候都要将当前的TextView放入parent
                            if (textView != null && B.length() > 0) {
                                newTextView = true;
                                textView.setText(B);
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
                            C.append(value);
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
                    ClickableSpan span = new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {

                            View v = (View) widget.getParent();
                            if (v.getTag(R.string.atom_ui_voice_hold_to_talk) != null) {
                                v.setTag(R.string.atom_ui_voice_hold_to_talk, null);
                                return;
                            }

                                Intent intent = new Intent(mContext, QunarWebActvity.class);
                                intent.setData(Uri.parse(url));
                                mContext.startActivity(intent);
                        }
                    };
                    SpannableString spannableString = new SpannableString(url);
                    spannableString.setSpan(span, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    C.append(spannableString);
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
                        C.append(v);
                    break;
            }


        }


        if ((Integer.parseInt(data.getIsDelete()) == 0)) {
//            TextView textView = linearLayoutManager.findViewByPosition(position + getHeaderLayoutCount()).findViewById(R.id.comment_item_text);


            final String content = msg;
            final String isAnonymous = data.getToisAnonymous();
            String xmppid2 = data.getToUser() + "@" + data.getToHost();
            final String anonymousName = data.getToAnonymousName();
//            C.clear();
            if (TextUtils.isEmpty(data.getToUser())) {
                B.clear();

                showSpann(all, A, B, C, helper);
//                all.clear();
//                all.append(A);
//                all.append(B);
//                all.append(C);
////                ((TextView) helper.getView(R.id.comment_item_text)).setText(B);
//                ((TextView) helper.getView(R.id.comment_item_text)).setText(all);
            } else {
                com.orhanobut.logger.Logger.i("data有可能为null:" + data);

                if (CurrentPreference.getInstance().getPreferenceUserId().equals(xmppid2)) {

//                    SpannableString st = (SpannableString) ;
//                    SpannableStringBuilder C = new SpannableStringBuilder();
                    B.clear();
                    B.append(Html.fromHtml("<font color='#999999'>回复</font> <font color='#999999'>我</font> "));
//                    C.append(B);
                    showSpann(all, A, B, C, helper);
                } else {


                    if ("0".equals(isAnonymous)) {

                        showSpann(all, A, B, C, helper);
                        ConnectionUtil.getInstance().getUserCard(xmppid2, new IMLogicManager.NickCallBack() {
                            @Override
                            public void onNickCallBack(final Nick nick) {
//                ((Activity) mContext).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                                SpannableStringBuilder newSB = new SpannableStringBuilder();
                                B.clear();
                                if (nick != null) {
//                                    ((TextView) helper.getView(R.id.comment_item_text)).setText(Html.fromHtml("<font color='#999999'>回复</font> <font color='#4dc1b5'>" + nick.getName() + "</font> " + sb));
                                    B.append(Html.fromHtml("<font color='#999999'>回复</font> <font color='#999999'>" + nick.getName() + "</font> "));
                                } else {
//                                    ((TextView) helper.getView(R.id.comment_item_text)).setText(Html.fromHtml("<font color='#999999'>回复</font> <font color='#4dc1b5'>" + nick.getXmppId() + "</font> " + sb));
                                    B.append(Html.fromHtml("<font color='#999999'>回复</font> <font color='#999999'>" + nick.getXmppId() + "</font> "));
                                }
                                showSpann(all, A, B, C, helper);
//                                newSB.append(B);
//                                ((TextView) helper.getView(R.id.comment_item_text)).setText(newSB);
//                    }
//                });


                            }
                        }, false, false);


                    } else {
//                        ((TextView) helper.getView(R.id.comment_item_text)).setText(Html.fromHtml("<font color='#999999'>回复</font> <font color='#999999'>" + anonymousName + "</font> " + sb));
//                        SpannableStringBuilder newSB = new SpannableStringBuilder();
                        B.clear();
                        B.append(Html.fromHtml("<font color='#999999'>回复</font> <font color='#999999'>" + anonymousName + "</font> "));
                        showSpann(all, A, B, C, helper);
                    }
                }
            }
        } else if ((Integer.parseInt(data.getIsDelete()) == 1) && MessageStatus.isExistStatus(Integer.parseInt(data.getCommentStatus()), WorkWorldItemState.commentShow)) {
            ((TextView) helper.getView(R.id.comment_item_text)).setText(Html.fromHtml("<font color='#FF0000'>该评论已删除!</font> "));
////
        } else {
            ((TextView) helper.getView(R.id.comment_item_text)).setText("ERROR!");
        }

        WorkWorldLinkTouchMovementMethod linkTouchMovementMethod = new WorkWorldLinkTouchMovementMethod();
        textView.setLinkTouchMovementMethod(linkTouchMovementMethod);
        textView.setMovementMethod(linkTouchMovementMethod);

    }

    private void showSpann(final SpannableStringBuilder all, SpannableStringBuilder a, SpannableStringBuilder b, SpannableStringBuilder c, final BaseViewHolder helper) {
        all.clear();
        all.append(a);
        all.append(b);
        all.append(c);
//        ViewTreeObserver observer = ((TextView) helper.getView(R.id.comment_item_text)).getViewTreeObserver();
//        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                ((TextView) helper.getView(R.id.comment_item_text)).setText(all);
//                if(((TextView) helper.getView(R.id.comment_item_text)).getLineCount()>3){
//                    int lineEndIndex = ((TextView) helper.getView(R.id.comment_item_text)).getLayout().getLineEnd(3 - 1);
//                    //下面这句代码中：我在项目中用数字3发现效果不好，改成1了
//                    String text = all.subSequence(0, lineEndIndex - 3) + "...";
//                    ((TextView) helper.getView(R.id.comment_item_text)).setText(text);
//
//                }else{
//                    removeGlobalOnLayoutListener( ((TextView) helper.getView(R.id.comment_item_text)).getViewTreeObserver(), this);
//                }
//
//            }
//        });
        ((TextView) helper.getView(R.id.comment_item_text)).setText(all);
    }


    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    private void removeGlobalOnLayoutListener(ViewTreeObserver obs, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (obs == null)
            return;
        if (Build.VERSION.SDK_INT < 16) {
            obs.removeGlobalOnLayoutListener(listener);
        } else {
            obs.removeOnGlobalLayoutListener(listener);
        }
    }

    //展示评论内容
    private void showContentText(final WorkWorldNewCommentBean data, final BaseViewHolder helper) {
        String msg = ChatTextHelper.textToHTML(data.getContent());


        WorkWorldSpannableTextView textView = ((WorkWorldSpannableTextView) helper.getView(R.id.comment_item_text));
        textView.setTag(data);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(v);
            }
        });
//        textView.setTag(data);
//        textView.setOnClickListener(onClickListener);
//        if (TextUtils.isEmpty(msg)) {
////            str = contentData.getContent();
//            textView.setText(contentData.getContent());
//            return;
//        }
        boolean newTextView = true;
        final SpannableStringBuilder sb = new SpannableStringBuilder();
        List<Map<String, String>> list = ChatTextHelper.getObjList(msg);
        for (Map<String, String> map : list) {
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
                                        is = mContext.getAssets().open(path);
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
                                    LogUtil.e(TAG, "ERROR", e);
                                } finally {
                                    if (is != null) {
                                        try {
                                            is.close();
                                        } catch (IOException e) {
                                            LogUtil.e(TAG, "ERROR", e);
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
                                    span = new ImageSpan(mContext, (Bitmap) cached);
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
                    ClickableSpan span = new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {

                            View v = (View) widget.getParent();
                            if (v.getTag(R.string.atom_ui_voice_hold_to_talk) != null) {
                                v.setTag(R.string.atom_ui_voice_hold_to_talk, null);
                                return;
                            }
                                Intent intent = new Intent(mContext, QunarWebActvity.class);
                                intent.setData(Uri.parse(url));
                                mContext.startActivity(intent);
                        }
                    };
                    SpannableString spannableString = new SpannableString(url);
                    spannableString.setSpan(span, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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
                    break;
            }


        }


        if ((Integer.parseInt(data.getIsDelete()) == 0)) {
//            TextView textView = linearLayoutManager.findViewByPosition(position + getHeaderLayoutCount()).findViewById(R.id.comment_item_text);


            final String content = msg;
            final String isAnonymous = data.getToisAnonymous();
            final String xmppid = data.getToUser() + "@" + data.getToHost();
            final String anonymousName = data.getToAnonymousName();
            if (TextUtils.isEmpty(data.getToUser())) {
                ((TextView) helper.getView(R.id.comment_item_text)).setText(sb);
            } else {
                com.orhanobut.logger.Logger.i("data有可能为null:" + data);

                if (CurrentPreference.getInstance().getPreferenceUserId().equals(xmppid)) {

//                    SpannableString st = (SpannableString) ;
                    SpannableStringBuilder newSB = new SpannableStringBuilder();
                    newSB.append(Html.fromHtml("<font color='#999999'>回复</font> <font color='#4dc1b5'>我</font> "));
                    newSB.append(sb);


                    ((TextView) helper.getView(R.id.comment_item_text)).setText(newSB);
                } else {


                    if ("0".equals(isAnonymous)) {

                        ((TextView) helper.getView(R.id.comment_item_text)).setText(sb);
                        ConnectionUtil.getInstance().getUserCard(xmppid, new IMLogicManager.NickCallBack() {
                            @Override
                            public void onNickCallBack(final Nick nick) {
//                ((Activity) mContext).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
                                SpannableStringBuilder newSB = new SpannableStringBuilder();
                                if (nick != null) {
//                                    ((TextView) helper.getView(R.id.comment_item_text)).setText(Html.fromHtml("<font color='#999999'>回复</font> <font color='#4dc1b5'>" + nick.getName() + "</font> " + sb));
                                    newSB.append(Html.fromHtml("<font color='#999999'>回复</font> <font color='#4dc1b5'>" + nick.getName() + "</font> "));
                                } else {
//                                    ((TextView) helper.getView(R.id.comment_item_text)).setText(Html.fromHtml("<font color='#999999'>回复</font> <font color='#4dc1b5'>" + nick.getXmppId() + "</font> " + sb));
                                    newSB.append(Html.fromHtml("<font color='#999999'>回复</font> <font color='#4dc1b5'>" + nick.getXmppId() + "</font> "));
                                }
                                newSB.append(sb);
                                ((TextView) helper.getView(R.id.comment_item_text)).setText(newSB);
//                    }
//                });


                            }
                        }, false, false);


                    } else {
//                        ((TextView) helper.getView(R.id.comment_item_text)).setText(Html.fromHtml("<font color='#999999'>回复</font> <font color='#999999'>" + anonymousName + "</font> " + sb));
                        SpannableStringBuilder newSB = new SpannableStringBuilder();
                        newSB.append(Html.fromHtml("<font color='#999999'>回复</font> <font color='#999999'>" + anonymousName + "</font> "));
                        newSB.append(sb);
                        ((TextView) helper.getView(R.id.comment_item_text)).setText(newSB);
                    }
                }
            }
        } else if ((Integer.parseInt(data.getIsDelete()) == 1) && MessageStatus.isExistStatus(Integer.parseInt(data.getCommentStatus()), WorkWorldItemState.commentShow)) {
            ((TextView) helper.getView(R.id.comment_item_text)).setText(Html.fromHtml("<font color='#FF0000'>该评论已删除!</font> "));
////
        } else {
            ((TextView) helper.getView(R.id.comment_item_text)).setText("ERROR!");
        }

        WorkWorldLinkTouchMovementMethod linkTouchMovementMethod = new WorkWorldLinkTouchMovementMethod();
        ((WorkWorldSpannableTextView) helper.getView(R.id.comment_item_text)).setLinkTouchMovementMethod(linkTouchMovementMethod);
        ((WorkWorldSpannableTextView) helper.getView(R.id.comment_item_text)).setMovementMethod(linkTouchMovementMethod);

    }


    //展示评论内容
    private void showContentText(WorkWorldNoticeItem data, final BaseViewHolder helper) {
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
                                    ((TextView) helper.getView(R.id.comment_item_text)).setText(Html.fromHtml("<font color='#999999'>回复</font> <font color='#59918A'>" + nick.getName() + "</font> " + content));
                                } else {
                                    ((TextView) helper.getView(R.id.comment_item_text)).setText(Html.fromHtml("<font color='#999999'>回复</font> <font color='#59918A'>" + nick.getXmppId() + "</font> " + content));
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


    public void deleteCommentItem(int position, WorkWorldDeleteResponse.CommentDeleteInfo data) {

        if (data.getDeleteType() == 1) {
            try {
                RecyclerView recyclerView = linearLayoutManager.findViewByPosition(position + getHeaderLayoutCount()).findViewById(R.id.child_comment_rc);
                WorkWorldDetailsAdapter adapter = (WorkWorldDetailsAdapter) recyclerView.getTag();
                for (int i = 0; i < adapter.getData().size(); i++) {
                    if (((WorkWorldChildCommentBean) adapter.getData().get(i)).getCommentUUID().equals(data.getCommentUUID())) {
                        adapter.remove(i);
                    }
                }

            } catch (Exception e) {
                notifyItemChanged(position + getHeaderLayoutCount());
                compatibilityDataSizeChanged(1);
            }
        } else {
            if (MessageStatus.isExistStatus(data.getSuperParentStatus(), WorkWorldItemState.commentShow)) {
                TextView textView = linearLayoutManager.findViewByPosition(position + getHeaderLayoutCount()).findViewById(R.id.comment_item_text);
                textView.setText("该评论已删除!");
////
            } else {
                remove(position);
            }
        }
//        if(MessageStatus.isExistStatus(data.getSuperParentStatus(),WorkWorldItemState.commentShow)){
//
//        }else{
//
//        }
    }

    public void updateCommentItem(WorkWorldNewCommentBean workWorldNewCommentBean, int position) {
//        RecyclerView recyclerView = (RecyclerView) getViewByPosition(position+getHeaderLayoutCount(),R.id.child_comment_rc);
//       TextView textView = (TextView) getViewByPosition(position+getHeaderLayoutCount(),R.id.comment_item_text);
//       int a = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
//        linearLayoutManager.findViewByPosition()
        try {
            TextView textView = linearLayoutManager.findViewByPosition(position + getHeaderLayoutCount()).findViewById(R.id.comment_item_text);
////
            RecyclerView recyclerView = linearLayoutManager.findViewByPosition(position + getHeaderLayoutCount()).findViewById(R.id.child_comment_rc);

//
            textView.setText(workWorldNewCommentBean.getContent());
            recyclerView.setVisibility(View.VISIBLE);
//       LinearLayoutManager linearLayoutManager = getl
            WorkWorldDetailsAdapter adapter = (WorkWorldDetailsAdapter) recyclerView.getTag();
            List<? extends MultiItemEntity> list = workWorldNewCommentBean.getNewChild();
            adapter.setNewData((List<MultiItemEntity>) list);
        } catch (Exception e) {
//            notifyItemInserted(position + getHeaderLayoutCount());
            notifyItemChanged(position + getHeaderLayoutCount());
            compatibilityDataSizeChanged(1);
//            addData(i,data);
//            notifyItemChanged(position);
        }

    }


    public void setLinearLayoutManager(LinearLayoutManager linearLayoutManager) {
        this.linearLayoutManager = linearLayoutManager;
    }

}
