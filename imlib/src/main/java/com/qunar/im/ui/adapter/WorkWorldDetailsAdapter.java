package com.qunar.im.ui.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qunar.im.base.module.MultiItemEntity;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.module.ReleaseContentData;
import com.qunar.im.base.module.SetLikeData;
import com.qunar.im.base.module.SetLikeDataResponse;
import com.qunar.im.base.module.WorkWorldDetailsLabelData;
import com.qunar.im.base.module.WorkWorldItem;
import com.qunar.im.base.module.WorkWorldNewCommentBean;
import com.qunar.im.base.module.WorkWorldNoticeItem;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.ProfileUtils;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.WorkWorldDetailsActivity;
import com.qunar.im.ui.view.IconView;
import com.qunar.im.ui.view.recyclerview.BaseMultiItemQuickAdapter;
import com.qunar.im.ui.view.recyclerview.BaseViewHolder;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.HttpUtil;
import com.qunar.im.utils.QtalkStringUtils;
import com.qunar.rn_service.protocal.NativeApi;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import static com.qunar.im.ui.activity.WorkWorldDetailsActivity.WORK_WORLD_DETAILS_ITEM;

public class WorkWorldDetailsAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {


    public static final int COMMENT=1;
    public static final int NOTICE=2;
    public static final int INSTRUCTIONS=3;
    private static String defaultHeadUrl = QtalkNavicationService.getInstance().getSimpleapiurl() + "/file/v2/download/perm/3ca05f2d92f6c0034ac9aee14d341fc7.png";

    public String postOwner;
    public String postOwnerHost;


    private View.OnClickListener onClickListener;
    private Activity mActivity;

    private int commentCount = 0;



    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public WorkWorldDetailsAdapter(List<MultiItemEntity> data, Activity mActivity) {
        super(data);
        addItemType(COMMENT, R.layout.atom_ui_work_world_details_comment_item);
        addItemType(NOTICE, R.layout.atom_ui_work_world_notice_item);
        addItemType(INSTRUCTIONS,R.layout.atom_ui_work_world_details_comment_layout_show);
        this.mActivity = mActivity;
    }

    public void setPostOwnerAndHost(String owner,String host){
        this.postOwner = owner;
        this.postOwnerHost = host;
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.onClickListener = listener;
    }





    //设置头像点击事件
    private void setClickHeadName(final WorkWorldNewCommentBean item, BaseViewHolder helper){
//        helper.getView(R.id.user_header)
        if(item.getIsAnonymous().equals("0")){
            helper.getView(R.id.user_header).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NativeApi.openUserCardVCByUserId(item.getFromUser()+"@"+item.getFromHost());
                }
            });
            helper.getView(R.id.user_name).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NativeApi.openUserCardVCByUserId(item.getFromUser()+"@"+item.getFromHost());
                }
            });
        }else{
            helper.getView(R.id.user_header).setOnClickListener(null);
            helper.getView(R.id.user_name).setOnClickListener(null);
        }
    }


    //设置头像点击事件
    private void setClickHeadName(final WorkWorldNoticeItem item, BaseViewHolder helper){
//        helper.getView(R.id.user_header)
        if(item.getFromIsAnonymous().equals("0")){
            helper.getView(R.id.user_header).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NativeApi.openUserCardVCByUserId(item.getUserFrom() + "@" + item.getUserFromHost());
                }
            });
            helper.getView(R.id.user_name).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NativeApi.openUserCardVCByUserId(item.getUserFrom() + "@" + item.getUserFromHost());
                }
            });
        }else{
            helper.getView(R.id.user_header).setOnClickListener(null);
            helper.getView(R.id.user_name).setOnClickListener(null);
        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void convert(final BaseViewHolder helper, final MultiItemEntity item) {
        switch (helper.getItemViewType()){
            case COMMENT:
                final WorkWorldNewCommentBean data = (WorkWorldNewCommentBean) item;
                final String xmppid = data.getFromUser() + "@" + data.getFromHost();



                if(data.getIsAnonymous().equals("0")){
                    ((TextView) helper.getView(R.id.user_architecture)).setVisibility(View.VISIBLE);
                    ConnectionUtil.getInstance().getUserCard(xmppid, new IMLogicManager.NickCallBack() {
                        @Override
                        public void onNickCallBack(final Nick nick) {
//                ((Activity) mContext).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
                            if (nick != null) {

                                ProfileUtils.displayGravatarByImageSrc(mActivity, nick.getHeaderSrc(), (ImageView) helper.getView(R.id.user_header),
                                        mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head), mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head));
                                ((TextView) helper.getView(R.id.user_name)).setText(TextUtils.isEmpty(nick.getName()) ? xmppid : nick.getName());
                            } else {
                                ProfileUtils.displayGravatarByImageSrc(mActivity, defaultHeadUrl, (ImageView) helper.getView(R.id.user_header),
                                        mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head), mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head));
                                ((TextView) helper.getView(R.id.user_name)).setText(xmppid);
                            }
                            ((TextView) helper.getView(R.id.user_architecture)).setText(QtalkStringUtils.architectureParsing(nick.getDescInfo()));

//                    }
//                });


                        }
                    }, false, false);

                    ((TextView) helper.getView(R.id.user_name)).setTextColor(mActivity.getResources().getColor(R.color.atom_ui_new_color_4DC1B5));
                }else{
                    ProfileUtils.displayGravatarByImageSrc(mActivity, data.getAnonymousPhoto(), (ImageView) helper.getView(R.id.user_header),
                            mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head), mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head));
                    ((TextView) helper.getView(R.id.user_name)).setText(data.getAnonymousName());
                    ((TextView) helper.getView(R.id.user_name)).setTextColor(mActivity.getResources().getColor(R.color.atom_ui_light_gray_99));
                    ((TextView) helper.getView(R.id.user_architecture)).setVisibility(View.GONE);
                }
                showIsLike(data,helper);
                showLikeNum(data,helper);
                showContentText(data,helper);
                setClickHeadName(data,helper);
//                helper.getView(R.id.comment_item_text).setTag(data);
//                helper.getView(R.id.comment_item_text).setOnClickListener(onClickListener);
                helper.itemView.setTag(data);
                helper.itemView.setOnClickListener(onClickListener);
//



                //设置点赞单击事件
                helper.getView(R.id.comment_item_like_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SetLikeData setLikeData = new SetLikeData();
                        setLikeData.setLikeType(data.getIsLike().equals("1") ? 0 : 1);
                        setLikeData.setOpType(2);
                        setLikeData.setCommentId(data.getCommentUUID());
                        setLikeData.setPostId(data.getPostUUID());
                        setLikeData.setLikeId("2-"+UUID.randomUUID().toString().replace("-",""));
                        setLikeData.setPostOwner(postOwner);
                        setLikeData.setPostOwnerHost(postOwnerHost);
                        HttpUtil.setLike(setLikeData, new ProtocolCallback.UnitCallback<SetLikeDataResponse>() {
                            @Override
                            public void onCompleted(final SetLikeDataResponse setLikeDataResponse) {
//                            mActivity.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
                                if(setLikeDataResponse==null){
                                    return;
                                }
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        data.setIsLike(setLikeDataResponse.getData().getIsLike() + "");
                                        data.setLikeNum(setLikeDataResponse.getData().getLikeNum() + "");
                                        showIsLike(data, helper);
                                        showLikeNum(data, helper);
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





//                user_header
//                        user_name
//                user_architecture
//                        comment_item_like_layout
//                comment_item_like_icon
//                        comment_item_like_text
//                comment_item_text
                break;
            case NOTICE:


                final WorkWorldNoticeItem workWorldNoticeItem = (WorkWorldNoticeItem) item;
                final String ixmppid = workWorldNoticeItem.getUserFrom() + "@" + workWorldNoticeItem.getUserFromHost();
                setClickHeadName(workWorldNoticeItem,helper);
                if(workWorldNoticeItem.getFromIsAnonymous().equals("1")){



                    ((TextView) helper.getView(R.id.user_name)).setTextColor(mActivity.getResources().getColor(R.color.atom_ui_light_gray_99));

                    ProfileUtils.displayGravatarByImageSrc(mActivity, workWorldNoticeItem.getFromAnonymousPhoto(), (ImageView) helper.getView(R.id.user_header),
                            mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head), mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head));
                    ((TextView) helper.getView(R.id.user_name)).setText(workWorldNoticeItem.getFromAnonymousName());

                    ((TextView) helper.getView(R.id.user_architecture)).setVisibility(View.GONE);

                }else{
                    ((TextView) helper.getView(R.id.user_architecture)).setVisibility(View.VISIBLE);
                    ConnectionUtil.getInstance().getUserCard(ixmppid, new IMLogicManager.NickCallBack() {
                        @Override
                        public void onNickCallBack(final Nick nick) {
//                ((Activity) mContext).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
                            if (nick != null) {

                                ProfileUtils.displayGravatarByImageSrc(mActivity, nick.getHeaderSrc(), (ImageView) helper.getView(R.id.user_header),
                                        mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head), mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head));
                                ((TextView) helper.getView(R.id.user_name)).setText(TextUtils.isEmpty(nick.getName()) ? ixmppid : nick.getName());
                            } else {
                                ProfileUtils.displayGravatarByImageSrc(mActivity, defaultHeadUrl, (ImageView) helper.getView(R.id.user_header),
                                        mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head), mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head));
                                ((TextView) helper.getView(R.id.user_name)).setText(ixmppid);
                            }
                            ((TextView) helper.getView(R.id.user_architecture)).setText(QtalkStringUtils.architectureParsing(nick.getDescInfo()));
//                    }
//                });


                        }
                    }, false, false);


                    ((TextView) helper.getView(R.id.user_name)).setTextColor(mActivity.getResources().getColor(R.color.atom_ui_new_color_4DC1B5));
                }
//                showIsLike(workWorldNoticeItem.helper);
//                showLikeNum(workWorldNoticeItem.helper);
                showContentText(workWorldNoticeItem,helper);
                helper.getView(R.id.comment_item_text).setTag(workWorldNoticeItem);
//                helper.getView(R.id.comment_item_text).setOnClickListener(onClickListener);

                if(!TextUtils.isEmpty(workWorldNoticeItem.getContent())){
                    ((TextView)helper.getView(R.id.text_item)).setText(workWorldNoticeItem.getContent());
                }



                if(!TextUtils.isEmpty(workWorldNoticeItem.getPostUUID())){
                    ConnectionUtil.getInstance().getWorkWorldByUUID(workWorldNoticeItem.getPostUUID(), new ConnectionUtil.WorkWorldCallBack() {
                        @Override
                        public void callBack(WorkWorldItem item) {
                            ReleaseContentData contentData1;
                            try {
                                contentData1 = JsonUtils.getGson().fromJson(item.getContent(), ReleaseContentData.class);
                            } catch (Exception e) {
                                contentData1 = new ReleaseContentData();
//                                return;
                            }
                            if(contentData1.getImgList().size()>0){
                                String url = contentData1.getImgList().get(0).getData();
//                                img_item


                                ProfileUtils.displaySquareByImageSrc(mActivity, url, (ImageView) helper.getView(R.id.img_item),
                                        mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_56dp), mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_56dp));
                                helper.getView(R.id.img_item).setVisibility(View.VISIBLE);
                                helper.getView(R.id.text_item).setVisibility(View.GONE);
                            }else{
                                ((TextView) helper.getView(R.id.text_item)).setText(contentData1.getContent());
                                helper.getView(R.id.img_item).setVisibility(View.GONE);
                                helper.getView(R.id.text_item).setVisibility(View.VISIBLE);
                            }

                        }
                    });
                }

                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!TextUtils.isEmpty(workWorldNoticeItem.getPostUUID())){
                            ConnectionUtil.getInstance().getWorkWorldByUUID(workWorldNoticeItem.getPostUUID(), new ConnectionUtil.WorkWorldCallBack() {
                                @Override
                                public void callBack(WorkWorldItem item) {
                                    if(!TextUtils.isEmpty(item.getUuid())){
                                        Intent intent = new Intent(mActivity, WorkWorldDetailsActivity.class);
                                        intent.putExtra(WORK_WORLD_DETAILS_ITEM, (Serializable) item);
                                        mActivity.startActivity(intent);
                                    }
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


                break;

            case INSTRUCTIONS:
                WorkWorldDetailsLabelData commentDetails = (WorkWorldDetailsLabelData) item;
                ( (TextView)helper.getView(R.id.comment_text)).setText(commentDetails.getName());
                if(commentDetails.getCount()>0){
                    ( (TextView)helper.getView(R.id.comment_count)).setText("("+commentDetails.getCount()+")");
                }else{
                    ( (TextView)helper.getView(R.id.comment_count)).setText("");
                }

                break;
        }
    }


    private void showIsLike(final WorkWorldNewCommentBean item, final BaseViewHolder helper){
        if (item.getIsLike().equals("1")) {
            ((IconView) helper.getView(R.id.comment_item_like_icon)).setTextColor(mActivity.getResources().getColor(R.color.atom_ui_new_like_select));
            ((IconView) helper.getView(R.id.comment_item_like_icon)).setText(R.string.atom_ui_new_like_select);
//            #00CABE

        } else {
            ((IconView) helper.getView(R.id.comment_item_like_icon)).setText(R.string.atom_ui_new_like);
            ((IconView) helper.getView(R.id.comment_item_like_icon)).setTextColor(mActivity.getResources().getColor(R.color.atom_ui_light_gray_99));
        }
    }

    private void showLikeNum(final WorkWorldNewCommentBean item, final BaseViewHolder helper){
        if (!TextUtils.isEmpty(item.getLikeNum())) {
            try {
                if (Integer.parseInt(item.getLikeNum()) > 0) {
                    ((TextView) helper.getView(R.id.comment_item_like_text)).setText(item.getLikeNum());
                } else {
                    ((TextView) helper.getView(R.id.comment_item_like_text)).setText("顶");

                }
            }catch (Exception e){
                ((TextView) helper.getView(R.id.comment_item_like_text)).setText("顶");
            }



        } else {
            ((TextView) helper.getView(R.id.comment_item_like_text)).setText("顶");
        }
    }

    //展示评论内容
    private void showContentText(WorkWorldNewCommentBean data, final BaseViewHolder helper){
        final String content = data.getContent();
        final String isAnonymous = data.getToisAnonymous();
        final String xmppid = data.getToUser()+"@"+data.getToHost();
        final String anonymousName = data.getToAnonymousName();
        if(TextUtils.isEmpty(data.getToUser())){
            ((TextView)helper.getView(R.id.comment_item_text)).setText(data.getContent());
        }else{
            com.orhanobut.logger.Logger.i("data有可能为null:"+data);

            if(CurrentPreference.getInstance().getPreferenceUserId().equals(xmppid)){

                ((TextView) helper.getView(R.id.comment_item_text)).setText(Html.fromHtml("<font color='#999999'>回复</font> <font color='#4dc1b5'>我</font> " + content));
            }else {


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

    }



    //展示评论内容
    private void showContentText(WorkWorldNoticeItem data, final BaseViewHolder helper){
        final String content = data.getContent();
        final String isAnonymous = data.getToIsAnonymous();
        final String xmppid = data.getUserTo()+"@"+data.getUserToHost();
        final String anonymousName = data.getToAnonymousName();
        if(TextUtils.isEmpty(data.getUserTo())){
            ((TextView)helper.getView(R.id.comment_item_text)).setText(data.getContent());
        }else{
            com.orhanobut.logger.Logger.i("data有可能为null:"+data);

            if(CurrentPreference.getInstance().getPreferenceUserId().equals(xmppid)){

                ((TextView) helper.getView(R.id.comment_item_text)).setText(Html.fromHtml("<font color='#999999'>回复</font> <font color='#4dc1b5'>我</font> " + content));
            }else {


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

    }


}
