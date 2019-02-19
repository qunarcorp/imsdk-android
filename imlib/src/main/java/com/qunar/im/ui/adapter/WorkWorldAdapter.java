package com.qunar.im.ui.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.module.ImageItemWorkWorldItem;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.module.ReleaseContentData;
import com.qunar.im.base.module.SetLikeData;
import com.qunar.im.base.module.SetLikeDataResponse;
import com.qunar.im.base.module.WorkWorldItem;
import com.qunar.im.base.presenter.views.IBrowsingConversationImageView;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.ProfileUtils;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.ImageBrowersingActivity;
import com.qunar.im.ui.activity.WorkWorldDetailsActivity;
import com.qunar.im.ui.imagepicker.util.Utils;
import com.qunar.im.ui.imagepicker.view.GridSpacingItemDecoration;
import com.qunar.im.ui.view.IconView;
import com.qunar.im.ui.view.recyclerview.BaseQuickAdapter;
import com.qunar.im.ui.view.recyclerview.BaseViewHolder;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.HttpUtil;
import com.qunar.im.utils.QtalkStringUtils;
import com.qunar.rn_service.protocal.NativeApi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.qunar.im.ui.activity.WorkWorldDetailsActivity.WORK_WORLD_DETAILS_COMMENT;
import static com.qunar.im.ui.activity.WorkWorldDetailsActivity.WORK_WORLD_DETAILS_ITEM;

public class WorkWorldAdapter extends BaseQuickAdapter<WorkWorldItem, BaseViewHolder> {

    private String plusImg = "https://qt.qunar.com/file/v2/download/temp/new/f798efc14a64e9abb7a336e8de283e5e.png?name=f798efc14a64e9abb7a336e8de283e5e.png&amp;file=file/f798efc14a64e9abb7a336e8de283e5e.png&amp;FileName=file/f798efc14a64e9abb7a336e8de283e5e.png";
    private static String defaultHeadUrl = QtalkNavicationService.getInstance().getSimpleapiurl() + "/file/v2/download/perm/3ca05f2d92f6c0034ac9aee14d341fc7.png";
    private Activity mActivity;

    private final int MAX_LINE_COUNT = 4;//最大显示行数

    private final int STATE_UNKNOW = -1;//未知状态

    private final int STATE_NOT_OVERFLOW = 1;//文本行数小于最大可显示行数

    private final int STATE_COLLAPSED = 2;//折叠状态

    private final int STATE_EXPANDED = 3;//展开状态

    private GridSpacingItemDecoration g3;

    private GridSpacingItemDecoration g2;

    private GridSpacingItemDecoration g1;

    private GridSpacingItemDecoration gridSpacingItemDecoration;

    private View.OnClickListener onClickListener;

    private View.OnClickListener openDetailsListener;

    private RecyclerView mRecyclerView;

    /**
     * 注意：保存文本状态集合的key一定要是唯一的，如果用position。
     * 如果使用position作为key，则删除、增加条目的时候会出现显示错乱
     */
    private HashMap<String, Integer> mTextStateList;//保存文本状态集合


    public WorkWorldAdapter(Activity activity) {
        super(R.layout.atom_ui_work_world_item);
        this.mActivity = activity;
        mTextStateList = new HashMap<>();
        g3 = new GridSpacingItemDecoration(3, Utils.dp2px(mActivity, 4), false);

        g2 = new GridSpacingItemDecoration(2, Utils.dp2px(mActivity, 4), false);

        g1 = new GridSpacingItemDecoration(1, Utils.dp2px(mActivity, 4), false);
    }

    public WorkWorldAdapter(Activity activity,RecyclerView recyclerView){
        super(R.layout.atom_ui_work_world_item);
        this.mActivity = activity;
        mTextStateList = new HashMap<>();
        g3 = new GridSpacingItemDecoration(3, Utils.dp2px(mActivity, 4), false);

        g2 = new GridSpacingItemDecoration(2, Utils.dp2px(mActivity, 4), false);

        g1 = new GridSpacingItemDecoration(1, Utils.dp2px(mActivity, 4), false);
        mRecyclerView = recyclerView;
    }


    public void setOnClickListener(View.OnClickListener listener){
        this.onClickListener = listener;
    }

    public void setOpenDetailsListener(View.OnClickListener listener){
        this.openDetailsListener = listener;
    }



    @SuppressLint("ResourceAsColor")
    @Override
    protected void convert(final BaseViewHolder helper, final WorkWorldItem item) {
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

            helper.getView(R.id.img_rc).setVisibility(View.VISIBLE);
            helper.getView(R.id.text_content).setVisibility(View.VISIBLE);
            ((TextView) helper.getView(R.id.tv_expand_or_fold)).setVisibility(View.VISIBLE);
            contentData = contentData1;
            if (!TextUtils.isEmpty(contentData.getContent())) {
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
                    if (!TextUtils.isEmpty(contentData.getContent())) {
                        ((TextView) helper.getView(R.id.text_content)).setText(contentData.getContent());
                    }

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
                    if (!TextUtils.isEmpty(contentData.getContent())) {
                        ((TextView) helper.getView(R.id.text_content)).setText(contentData.getContent());
                    }

                }
            } else {
                ((TextView) helper.getView(R.id.tv_expand_or_fold)).setVisibility(View.GONE);//显示“全文”
                helper.getView(R.id.text_content).setVisibility(View.GONE);
            }

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
                    gridSpacingItemDecoration = g1;
                } else if (i < 5) {
                    column = 2;
                    gridSpacingItemDecoration = g2;
                } else {
                    column = 3;
                    gridSpacingItemDecoration = g3;
                }
                size = Utils.getImageItemWidthForWorld(mActivity, column);
                for (int j = contentData.getImgList().size()-1; j >=0; j--) {
                    if(contentData.getImgList().get(j)==null ){
                        contentData.getImgList().remove(i);
                    }
                }

               
                ReleaseCircleGridAdapter itemAdapter = new ReleaseCircleGridAdapter(contentData.getImgList(), mActivity, column);
                GridLayoutManager manager = new GridLayoutManager(mActivity, column);
                final int finalSize = size;
                itemAdapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        Intent intent = new Intent(mActivity, ImageBrowersingActivity.class);
                        int location[] = new int[2];
                        view.getLocationOnScreen(location);
                        intent.putExtra("left", location[0]);
                        intent.putExtra("top", location[1]);
                        intent.putExtra("height", finalSize);
                        intent.putExtra("width", finalSize);

                        String url = contentData.getImgList().get(position).getData();
                        if (!(url.startsWith("http") || url.startsWith("https"))) {
                            url = QtalkNavicationService.getInstance().getInnerFiltHttpHost() + "/" + url;
                        }
                        intent.putExtra(Constants.BundleKey.IMAGE_URL, url);
                        intent.putExtra(Constants.BundleKey.IMAGE_ON_LOADING, url);
                        intent.putExtra(Constants.BundleKey.WORK_WORLD_BROWERSING, (Serializable) parseList(contentData.getImgList()));
                        mActivity.startActivity(intent);
                        ((Activity) mActivity).overridePendingTransition(0, 0);
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


            if(TextUtils.isEmpty(contentData.getContent())&&!(contentData.getImgList().size()>0)){
                ((TextView) helper.getView(R.id.text_content)).setText("有新类型消息,请升级客户端版本查看!");
            }

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
                    data.setLikeId("2-"+UUID.randomUUID().toString().replace("-",""));

                    HttpUtil.setLike(data, new ProtocolCallback.UnitCallback<SetLikeDataResponse>() {
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
                                            item.setIsLike(setLikeDataResponse.getData().getIsLike() + "");
                                            item.setLikeNum(setLikeDataResponse.getData().getLikeNum() + "");
                                            showLikeState(item, helper);
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
            showLikeState(item, helper);


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
            if(CurrentPreference.getInstance().getPreferenceUserId().equals(item.getOwner()+"@"+item.getOwnerHost())){
                helper.getView(R.id.right_special).setVisibility(View.VISIBLE);
            }else{
                helper.getView(R.id.right_special).setVisibility(View.GONE);
            }
            helper.getView(R.id.right_special).setTag(item);
            helper.getView(R.id.right_special).setOnClickListener( onClickListener);
            mActivity.registerForContextMenu(helper.getView(R.id.right_special));









        }catch (Exception e){
            Logger.i("朋友圈列表页出错:"+e.getMessage());
        }

    }

    //设置头像点击事件
    private void setClickHeadName(final WorkWorldItem item, BaseViewHolder helper){
//        helper.getView(R.id.user_header)
        if(item.getIsAnonymous().equals("0")){
            helper.getView(R.id.user_header).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NativeApi.openUserCardVCByUserId(item.getOwner()+"@"+item.getOwnerHost());
                }
            });
            helper.getView(R.id.user_name).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NativeApi.openUserCardVCByUserId(item.getOwner()+"@"+item.getOwnerHost());
                }
            });
        }else{
            helper.getView(R.id.user_header).setOnClickListener(null);
            helper.getView(R.id.user_name).setOnClickListener(null);
        }
    }

    private void showLikeState(final WorkWorldItem item, final BaseViewHolder helper){
        if (item.getIsLike().equals("1")) {
            ((IconView) helper.getView(R.id.like_icon)).setTextColor(mActivity.getResources().getColor(R.color.atom_ui_new_like_select));
            ((IconView) helper.getView(R.id.like_icon)).setText(R.string.atom_ui_new_like_select);
//            #00CABE

        } else {
            ((IconView) helper.getView(R.id.like_icon)).setText(R.string.atom_ui_new_like);
            ((IconView) helper.getView(R.id.like_icon)).setTextColor(mActivity.getResources().getColor(R.color.atom_ui_light_gray_99));
        }
    }

    private void showLikeNum(final WorkWorldItem item, final BaseViewHolder helper){
        if (!TextUtils.isEmpty(item.getLikeNum()+"")) {
            try {
                if (Integer.parseInt(item.getLikeNum()) > 0) {
                    ((TextView) helper.getView(R.id.like_num)).setText(item.getLikeNum());
                } else {
                    ((TextView) helper.getView(R.id.like_num)).setText("顶");

                }
            }catch (Exception e){
                ((TextView) helper.getView(R.id.like_num)).setText("顶");
            }



        } else {
            ((TextView) helper.getView(R.id.like_num)).setText("顶");
        }
    }

    private List<IBrowsingConversationImageView.PreImage> parseList(List<ImageItemWorkWorldItem> list){
        List<IBrowsingConversationImageView.PreImage> preImageList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            IBrowsingConversationImageView.PreImage image = new IBrowsingConversationImageView.PreImage();
            String url = list.get(i).getData();
            if(!(url.startsWith("http")||url.startsWith("https"))){
                url = QtalkNavicationService.getInstance().getInnerFiltHttpHost()+"/"+url;
            }
            image.smallUrl = url;
            image.originUrl = url;
            image.localPath = url;
            preImageList.add(image);
        }

        return preImageList;
    }


//    @Override
//    public int getItemViewType(int position) {
//        return position;
//    }
}
