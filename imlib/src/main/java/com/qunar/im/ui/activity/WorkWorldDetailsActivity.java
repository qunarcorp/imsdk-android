package com.qunar.im.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.orhanobut.logger.Logger;
import com.qunar.im.base.module.AnonymousData;
import com.qunar.im.base.module.ImageItemWorkWorldItem;
import com.qunar.im.base.module.MultiItemEntity;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.module.ReleaseContentData;
import com.qunar.im.base.module.SetLikeData;
import com.qunar.im.base.module.SetLikeDataResponse;
import com.qunar.im.base.module.WorkWorldDeleteResponse;
import com.qunar.im.base.module.WorkWorldDetailsLabelData;
import com.qunar.im.base.module.WorkWorldItem;
import com.qunar.im.base.module.WorkWorldNewCommentBean;
import com.qunar.im.base.presenter.WorkWorldDetailsPresenter;
import com.qunar.im.base.presenter.impl.WorkWorldDetailsManagerPresenter;
import com.qunar.im.base.presenter.views.IBrowsingConversationImageView;
import com.qunar.im.base.presenter.views.WorkWorldDetailsView;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.ProfileUtils;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.ReleaseCircleGridAdapter;
import com.qunar.im.ui.adapter.WorkWorldDetailsAdapter;
import com.qunar.im.ui.imagepicker.util.Utils;
import com.qunar.im.ui.imagepicker.view.GridSpacingItemDecoration;
import com.qunar.im.ui.view.IconView;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.recyclerview.BaseQuickAdapter;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.HttpUtil;
import com.qunar.im.utils.QtalkStringUtils;
import com.qunar.im.utils.SoftKeyboardStateHelper;
import com.qunar.rn_service.protocal.NativeApi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.qunar.im.base.presenter.impl.ReleaseCircleManagerPresenter.ANONYMOUS_NAME;
import static com.qunar.im.base.presenter.impl.ReleaseCircleManagerPresenter.REAL_NAME;
import static com.qunar.im.ui.activity.IdentitySelectActivity.ANONYMOUS_DATA;
import static com.qunar.im.ui.activity.IdentitySelectActivity.now_identity_type;
import static com.qunar.im.ui.activity.ReleaseCircleActivity.EXTRA_IDENTITY;
import static com.qunar.im.ui.activity.ReleaseCircleActivity.REQUEST_CODE_IDENTITY;
import static com.qunar.im.ui.activity.ReleaseCircleActivity.UUID_STR;

public class WorkWorldDetailsActivity extends SwipeBackActivity implements WorkWorldDetailsView {

    public static String WORK_WORLD_DETAILS_ITEM = "WORK_WORLD_DETAILS_ITEM";
    public static String WORK_WORLD_DETAILS_COMMENT= "WORK_WORLD_DETAILS_COMMENT";
    private static String defaultHeadUrl = QtalkNavicationService.getInstance().getSimpleapiurl() + "/file/v2/download/perm/3ca05f2d92f6c0034ac9aee14d341fc7.png";

    private WorkWorldItem workWorldItem;

    private AnonymousData mAnonymousData;

    private List<MultiItemEntity> allData = new ArrayList<>();
    private List<MultiItemEntity> hotData = new ArrayList<>();

    protected QtNewActionBar qtNewActionBar;//头部导航
    protected WorkWorldDetailsAdapter workWorldDetailsAdapter;

    private WorkWorldNewCommentBean toData;

    private int likeNum = -1;
    private int commentNum = -1;

    private boolean showInput = false;

    private GridSpacingItemDecoration g3;

    private GridSpacingItemDecoration g2;

    private GridSpacingItemDecoration g1;

    private GridSpacingItemDecoration gridSpacingItemDecoration;

    private View headView;
    //头部view
    private SimpleDraweeView user_header;//帖子头像
    private TextView user_name;//帖子姓名
    private TextView user_architecture;//帖子部门
    private IconView right_special;//帖子功能按钮
    private TextView text_content;//帖子内容
    private RecyclerView img_rc;//九宫格
    private TextView time;//时间
    private LinearLayout like_layout;//点赞按钮
    private LinearLayout comment_layout;//评论按钮
    private LinearLayout comment_list_layout;//评论区域
    private TextView comment_count;//总计评论数

    //本体view
    private RecyclerView comment_rc;//评论列表
    private SimpleDraweeView my_heade;//用户头像 预计点击切换匿名
    private EditText comment_edittext;//评论输入框
    private LinearLayout comment_like_layout;//评论区点赞区域按钮
    private IconView comment_like_icon;//评论区点赞按钮
    private TextView comment_like_num;//评论区点赞数量统计
    private LinearLayout comment_details;//额外评论详情
    private IconView comment_send_icon;//发送评论按钮
    private SwipeRefreshLayout mSwipeRefreshLayout;


    private int identityType = 0;
    private SoftKeyboardStateHelper softKeyboardStateHelper;
    private WorkWorldDetailsPresenter workWorldDetailsPresenter;
    private BottomSheetDialog bottomSheetDialog;
    private boolean check;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_work_world_details_activity);
        bindView();

        if(getIntent().hasExtra(WORK_WORLD_DETAILS_COMMENT)){
            showInput = getIntent().getBooleanExtra(WORK_WORLD_DETAILS_COMMENT,false);
        }

        if (getIntent().hasExtra(WORK_WORLD_DETAILS_ITEM)) {
            workWorldItem = (WorkWorldItem) getIntent().getSerializableExtra(WORK_WORLD_DETAILS_ITEM);
            startInit();
        } else {
            //此处应该请求借口开始操作
        }
        softKeyboardStateHelper = new SoftKeyboardStateHelper(findViewById(R.id.work_world_activity_main_layout));
        softKeyboardStateHelper.addSoftKeyboardStateListener(new SoftKeyboardStateHelper.SoftKeyboardStateListener() {
            @Override
            public void onSoftKeyboardOpened(int keyboardHeightInPx) {

            }

            @Override
            public void onSoftKeyboardClosed() {
                hiddenInput();
            }
        });

    }

    public void startInit() {
        bindHeadData();
        bindSelfData();
        initAdapter();
        workWorldDetailsPresenter.loadingHistory();

    }

    private void bindSelfData() {
        workWorldDetailsPresenter = new WorkWorldDetailsManagerPresenter();
        workWorldDetailsPresenter.setView(this);
        showLikeNum(workWorldItem);
        showLikeState(workWorldItem);
        comment_send_icon.setVisibility(View.GONE);


        comment_edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    comment_like_layout.setVisibility(View.GONE);
                    comment_send_icon.setVisibility(View.VISIBLE);
                } else {
                    comment_like_layout.setVisibility(View.VISIBLE);
                    comment_send_icon.setVisibility(View.GONE);
                }
            }
        });

        comment_like_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetLikeData data = new SetLikeData();
                data.setLikeType(workWorldItem.getIsLike().equals("1") ? 0 : 1);
                data.setOpType(1);
                data.setPostId(workWorldItem.getUuid());
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                workWorldItem.setIsLike(setLikeDataResponse.getData().getIsLike() + "");
                                workWorldItem.setLikeNum(setLikeDataResponse.getData().getLikeNum() + "");
                                showLikeState(workWorldItem);
                                showLikeNum(workWorldItem);
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


        comment_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    comment_send_icon.setClickable(false);
                    comment_send_icon.setTextColor(getResources().getColor(R.color.send_no));
                } else {
                    comment_send_icon.setClickable(true);
                    comment_send_icon.setTextColor(getResources().getColor(R.color.atom_ui_new_like_select));

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 100) {
                    showToast("请输入不超过100个字符的评论", false);
                    check = false;
                } else {
                    check = true;
                }
            }
        });
        comment_send_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment_send_icon.setEnabled(false);
                workWorldDetailsPresenter.sendComment();
            }
        });


        initRealHeader();


        my_heade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(WorkWorldDetailsActivity.this, IdentitySelectActivity.class);
                intent.putExtra(UUID_STR, workWorldItem.getUuid());
                intent.putExtra(now_identity_type, identityType);
                startActivityForResult(intent, REQUEST_CODE_IDENTITY);
//                Intent intent = new Intent(Work.this, IdentitySelectActivity.class);
//                startActivityForResult(intent, REQUEST_CODE_IDENTITY);
            }
        });

        //设置刷新操作
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(0, 202, 190));
    }

    public void refresh() {
        workWorldDetailsAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载
        workWorldDetailsPresenter.startRefresh();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {


            case REQUEST_CODE_IDENTITY:
                updateIdentity(data);
                break;


        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateIdentity(Intent data) {
        int i = data.getIntExtra(EXTRA_IDENTITY, 0);
        switch (i) {
            case REAL_NAME:
                identityType = REAL_NAME;

                initRealHeader();
                break;

            case ANONYMOUS_NAME:
                identityType = ANONYMOUS_NAME;
                mAnonymousData = (AnonymousData) data.getSerializableExtra(ANONYMOUS_DATA);
                initAnonymousHeader();
//                releaseCirclePresenter.getAnonymous();
                break;
        }
    }

    private void initAnonymousHeader() {
        ProfileUtils.displayGravatarByImageSrc(WorkWorldDetailsActivity.this, mAnonymousData.getData().getAnonymousPhoto(), (ImageView) my_heade,
                WorkWorldDetailsActivity.this.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head), WorkWorldDetailsActivity.this.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head));
    }

    private void initRealHeader() {
        final String xmppid = CurrentPreference.getInstance().getPreferenceUserId();
        ConnectionUtil.getInstance().getUserCard(xmppid, new IMLogicManager.NickCallBack() {
            @Override
            public void onNickCallBack(final Nick nick) {
//                ((Activity) mContext).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
                if (nick != null) {

                    ProfileUtils.displayGravatarByImageSrc(WorkWorldDetailsActivity.this, nick.getHeaderSrc(), (ImageView) my_heade,
                            WorkWorldDetailsActivity.this.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head), WorkWorldDetailsActivity.this.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head));

                } else {
                    ProfileUtils.displayGravatarByImageSrc(WorkWorldDetailsActivity.this, defaultHeadUrl, (ImageView) my_heade,
                            WorkWorldDetailsActivity.this.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head), WorkWorldDetailsActivity.this.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_details_self_head));

                }
//                user_architecture.setText(QtalkStringUtils.architectureParsing(nick.getDescInfo()));
//                    }
//                });


            }
        }, false, false);
    }

    private void initAdapter() {
        workWorldDetailsAdapter = new WorkWorldDetailsAdapter(new ArrayList<MultiItemEntity>(), this);

        comment_rc.setAdapter(workWorldDetailsAdapter);
        comment_rc.setLayoutManager(new LinearLayoutManager(this));
        workWorldDetailsAdapter.addHeaderView(headView);
        workWorldDetailsAdapter.setPostOwnerAndHost(workWorldItem.getOwner(), workWorldItem.getOwnerHost());
        workWorldDetailsAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final WorkWorldNewCommentBean item = (WorkWorldNewCommentBean) v.getTag();

//


                bottomSheetDialog = new BottomSheetDialog(WorkWorldDetailsActivity.this);
                View view = LayoutInflater.from(WorkWorldDetailsActivity.this).inflate(R.layout.atom_ui_work_world_special_popwindow, null);
//            TextView delete =

                TextView delete = view.findViewById(R.id.work_world_popwindow_delete);
                TextView reply = view.findViewById(R.id.work_world_popwindow_reply);
                TextView cancle = view.findViewById(R.id.work_world_popwindow_cancle);

                if (CurrentPreference.getInstance().getPreferenceUserId().equals(item.getFromUser() + "@" + item.getFromHost())) {
                    delete.setVisibility(View.VISIBLE);
                } else {
                    delete.setVisibility(View.GONE);
                }
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        workWorldDetailsPresenter.deleteWorkWorldCommentItem(item);
                        Toast.makeText(WorkWorldDetailsActivity.this, "删除", Toast.LENGTH_LONG).show();
                        bottomSheetDialog.dismiss();
                    }
                });

                reply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                        comment_rc.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showInput(item);

                            }
                        }, 500);

                    }
                });
                cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });
//


                bottomSheetDialog.setContentView(view);
//
                //给布局设置透明背景色
                bottomSheetDialog.getDelegate().findViewById(android.support.design.R.id.design_bottom_sheet)
                        .setBackgroundColor(WorkWorldDetailsActivity.this.getResources().getColor(android.R.color.transparent));


                bottomSheetDialog.show();


            }
        });

        workWorldDetailsAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadMore();
            }
        });
    }

    public void loadMore() {
        workWorldDetailsPresenter.loadingMore();
    }

    public void showInput(WorkWorldNewCommentBean data) {
        toData = data;
        if (data.getIsAnonymous().equals("0")) {
            final String xmppid = data.getFromUser() + "@" + data.getFromHost();

            ConnectionUtil.getInstance().getUserCard(xmppid, new IMLogicManager.NickCallBack() {
                @Override
                public void onNickCallBack(final Nick nick) {
//                ((Activity) mContext).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
                    if (nick != null) {
                        comment_edittext.setHint("回复: " + nick.getName() + ": ");
                    } else {
                        comment_edittext.setHint("回复: " + nick.getXmppId() + ": ");
                    }
//                    }
//                });


                }
            }, false, false);

        } else {
            comment_edittext.setHint("回复: " + data.getAnonymousName() + ": ");
        }

        comment_edittext.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(comment_edittext, 0);

    }

    private void bindHeadData() {

        g3 = new GridSpacingItemDecoration(3, Utils.dp2px(WorkWorldDetailsActivity.this, 4), false);

        g2 = new GridSpacingItemDecoration(2, Utils.dp2px(WorkWorldDetailsActivity.this, 4), false);

        g1 = new GridSpacingItemDecoration(1, Utils.dp2px(WorkWorldDetailsActivity.this, 4), false);


        try {
//            if(item.getIsDelete().equals("1")){
//               remove( helper.getLayoutPosition());
//               return;
//            }


            //第一次初始化，未知状态
            final ReleaseContentData contentData;
            ReleaseContentData contentData1;
            try {
                contentData1 = JsonUtils.getGson().fromJson(workWorldItem.getContent(), ReleaseContentData.class);
            } catch (Exception e) {
                contentData1 = new ReleaseContentData();
                return;
            }

            img_rc.setVisibility(View.VISIBLE);
            text_content.setVisibility(View.VISIBLE);
            contentData = contentData1;
            if (!TextUtils.isEmpty(contentData.getContent())) {
                text_content.setText(contentData.getContent());
                text_content.setVisibility(View.VISIBLE);
            } else {
                text_content.setVisibility(View.GONE);
            }


            //获取个人名片及相关信息
            final String xmppid = workWorldItem.getOwner() + "@" + workWorldItem.getOwnerHost();
            if (workWorldItem.getIsAnonymous().equals("0")) {

                user_architecture.setVisibility(View.VISIBLE);
                ConnectionUtil.getInstance().getUserCard(xmppid, new IMLogicManager.NickCallBack() {
                    @Override
                    public void onNickCallBack(final Nick nick) {
//                ((Activity) mContext).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
                        if (nick != null) {

                            ProfileUtils.displayGravatarByImageSrc(WorkWorldDetailsActivity.this, nick.getHeaderSrc(), (ImageView) user_header,
                                    WorkWorldDetailsActivity.this.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_head), WorkWorldDetailsActivity.this.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_head));
                            user_name.setText(TextUtils.isEmpty(nick.getName()) ? xmppid : nick.getName());
                        } else {
                            ProfileUtils.displayGravatarByImageSrc(WorkWorldDetailsActivity.this, defaultHeadUrl, (ImageView) user_header,
                                    WorkWorldDetailsActivity.this.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_head), WorkWorldDetailsActivity.this.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_head));
                            user_name.setText(xmppid);
                        }
                        user_architecture.setText(QtalkStringUtils.architectureParsing(nick.getDescInfo()));
                        user_name.setTextColor(getResources().getColor(R.color.atom_ui_new_color_4DC1B5));
//                    }
//                });


                    }
                }, false, false);
            } else {
                ProfileUtils.displayGravatarByImageSrc(WorkWorldDetailsActivity.this, workWorldItem.getAnonymousPhoto(), user_header,
                        WorkWorldDetailsActivity.this.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_head), WorkWorldDetailsActivity.this.getResources().getDimensionPixelSize(R.dimen.atom_ui_work_world_head));
                user_name.setText(workWorldItem.getAnonymousName());
                user_name.setTextColor(getResources().getColor(R.color.atom_ui_light_gray_99));
                user_architecture.setVisibility(View.GONE);
            }

            //确定发帖时间
            if (!TextUtils.isEmpty(workWorldItem.getCreateTime())) {
                try {
                    long times = Long.parseLong(workWorldItem.getCreateTime());
                    String t = DataUtils.formationDate(times);
                    time.setText(t);
                } catch (Exception e) {
                    time.setText("未知");
                }
            } else {
                time.setText("未知");
            }

            //设置图片展示
            if (contentData.getImgList().size() > 0) {
                img_rc.setVisibility(View.VISIBLE);
//            List<MultiItemEntity> list = contentData.getImgList();
                RecyclerView mRecyclerView = img_rc;
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
                size = Utils.getImageItemWidthForWorld(WorkWorldDetailsActivity.this, column);
                ReleaseCircleGridAdapter itemAdapter = new ReleaseCircleGridAdapter(contentData.getImgList(), WorkWorldDetailsActivity.this, column);
                GridLayoutManager manager = new GridLayoutManager(WorkWorldDetailsActivity.this, column);
                final int finalSize = size;
                itemAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        Intent intent = new Intent(WorkWorldDetailsActivity.this, ImageBrowersingActivity.class);
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
                        WorkWorldDetailsActivity.this.startActivity(intent);
                        ((Activity) WorkWorldDetailsActivity.this).overridePendingTransition(0, 0);
                    }
                });
//            mRecyclerView.removeItemDecoration();
//            mRecyclerView.invalidateItemDecorations();
//            mRecyclerView.addItemDecoration(gridSpacingItemDecoration);
                mRecyclerView.setLayoutManager(manager);
                mRecyclerView.setAdapter(itemAdapter);
            } else {
                img_rc.setVisibility(View.GONE);
            }


            //设置头像名字点击事件
            if (workWorldItem.getIsAnonymous().equals("0")) {
                setClickHeadName(workWorldItem.getOwner() + "@" + workWorldItem.getOwnerHost());
            }


//            //是否显示右方多功能按钮
//            if(CurrentPreference.getInstance().getPreferenceUserId().equals(workWorldItem.getOwner()+"@"+workWorldItem.getOwnerHost())){
//            right_special.setVisibility(View.VISIBLE);
//            }else{
            right_special.setVisibility(View.GONE);
//            }
            right_special.setTag(workWorldItem);
            //
            comment_details.setVisibility(View.VISIBLE);


            comment_count.setText("(" + workWorldItem.getLikeNum() + ")");


        } catch (Exception e) {
            Logger.i("详情页出错:" + e.getMessage());
        }
    }


    //设置头像点击事件
    private void setClickHeadName(final String xmppid) {
//

        user_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NativeApi.openUserCardVCByUserId(xmppid);
            }
        });
        user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NativeApi.openUserCardVCByUserId(xmppid);
            }
        });

    }


    private void bindView() {
        qtNewActionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(qtNewActionBar);
        setActionBarTitle("动态详情");
//        setActionBarLeftClick(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.putExtra(WorkWorldActivity.WORK_WORLD_RESULT_ITEM_BACK, workWorldItem);
//                setResult(RESULT_OK, intent);
//                finish();
//            }
//        });

        comment_rc = (RecyclerView) findViewById(R.id.comment_rc);//评论列表

        my_heade = (SimpleDraweeView) findViewById(R.id.my_heade);//用户头像 预计点击切换匿名
        comment_edittext = (EditText) findViewById(R.id.comment_edittext);//评论输入框
        comment_like_layout = (LinearLayout) findViewById(R.id.comment_like_layout);//评论区点赞区域按钮
        comment_like_icon = (IconView) findViewById(R.id.comment_like_icon);//评论区点赞按钮
        comment_like_num = (TextView) findViewById(R.id.comment_like_num);//评论区点赞数量统计
        comment_send_icon = (IconView) findViewById(R.id.comment_send_icon);//fasong anniu
        comment_count = (TextView) findViewById(R.id.comment_count);//总计评论数
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);


        headView = LayoutInflater.from(this).inflate(R.layout.atom_ui_work_world_item, null);
        user_header = headView.findViewById(R.id.user_header);//帖子头像
        user_name = headView.findViewById(R.id.user_name);//帖子姓名
        user_architecture = headView.findViewById(R.id.user_architecture);//帖子部门
        right_special = headView.findViewById(R.id.right_special);//帖子功能按钮
        text_content = headView.findViewById(R.id.text_content);//帖子内容
        img_rc = headView.findViewById(R.id.img_rc);//九宫格
        time = headView.findViewById(R.id.time);//时间
        like_layout = headView.findViewById(R.id.like_layout);//点赞按钮
        like_layout.setVisibility(View.GONE);
        comment_layout = headView.findViewById(R.id.comment_layout);//评论按钮
        comment_layout.setVisibility(View.GONE);
        comment_list_layout = headView.findViewById(R.id.comment_list_layout);//评论区域

        comment_details = (LinearLayout) headView.findViewById(R.id.comment_details);//额外评论详情

    }


    private void showLikeState(WorkWorldItem item) {
        if (item.getIsLike().equals("1")) {
            comment_like_icon.setTextColor(getResources().getColor(R.color.atom_ui_new_like_select));
            comment_like_icon.setText(R.string.atom_ui_new_like_select);
//            #00CABE

        } else {
            comment_like_icon.setText(R.string.atom_ui_new_like);
            comment_like_icon.setTextColor(getResources().getColor(R.color.atom_ui_light_gray_99));
        }
    }

    private void showLikeNum(WorkWorldItem item) {


//        comment_like_num.setText("这到底是啥");

        if (!TextUtils.isEmpty(item.getLikeNum())) {
            try {
                if (Integer.parseInt(item.getLikeNum()) > 0) {
                    comment_like_num.setText(item.getLikeNum() + "");
                } else {
                    comment_like_num.setText("顶");

                }
            } catch (Exception e) {
                comment_like_num.setText("顶");
            }


        } else {
            comment_like_num.setText("顶");
        }
    }


    private List<IBrowsingConversationImageView.PreImage> parseList(List<ImageItemWorkWorldItem> list) {
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

    @Override
    public boolean isOK() {
        if (workWorldItem != null) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    public String getPostOwner() {
        return workWorldItem.getOwner();
    }

    @Override
    public String getPostOwnerHost() {
        return workWorldItem.getOwnerHost();
    }

    @Override
    public String getPostUUid() {
        return workWorldItem.getUuid();
    }

    @Override
    public String getParentCommentUUID() {
        return "";
    }

    @Override
    public String getContent() {
        return comment_edittext.getText().toString().trim();
    }


    @Override
    public int isAnonymous() {
        return identityType;
    }

    @Override
    public String getAnonymousPhoto() {
        return mAnonymousData.getData().getAnonymousPhoto();
    }

    @Override
    public String getAnonymousName() {
        return mAnonymousData.getData().getAnonymous();
    }

    @Override
    public void showNewData(List<? extends MultiItemEntity> list, boolean isScroll, boolean isShowInput) {
//
//        if (list != null) {
////            workWorldAdapter.setNewData(list);
////            work_world_rc.scrollToPosition(0);
//           if(list.size() > 0) {
//               showNewDataHandle(list);
//           }else{
//               mSwipeRefreshLayout.setRefreshing(false);
//           }
//        }
//        comment_send_icon.setEnabled(true);
        if (list != null) {
            if (list.size() > 0) {
                showNewDataHandle(list, isScroll);
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });


//                if(showInput){
//                    comment_edittext.requestFocus();
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.showSoftInput(comment_edittext, 0);
//                    showInput= false;
//                }else {
//                    hiddenInput();
//                }
                hiddenInput();
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
//            if(showInput){
//                comment_edittext.requestFocus();
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.showSoftInput(comment_edittext, 0);
//                showInput= false;
//            }else {
//                hiddenInput();
//            }
            hiddenInput();
        }
    }

    @Override
    public void showHotNewData(List<? extends MultiItemEntity> list) {
//        if (list != null && list.size() > 0) {
////            workWorldAdapter.setNewData(list);
////            work_world_rc.scrollToPosition(0);
//            showHotNewDataHandle(list);
//        }

        if (list != null) {
//            if (list.size() > 0) {
                showHotNewDataHandle(list);
//            } else {
//                mSwipeRefreshLayout.setRefreshing(false);
//            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    @Override
    public void showMoreData(List<? extends MultiItemEntity> list) {
//        if (list != null && list.size() > 0) {
//            workWorldAdapter.setNewData(list);
//            work_world_rc.scrollToPosition(0);
        showMoreNewDataHandle(list);
//        }
    }

    private void showMoreNewDataHandle(final List<? extends MultiItemEntity> list) {
        if (comment_rc.isComputingLayout()) {
            comment_rc.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showMoreNewDataHandle(list);
                }
            }, 500);
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    if (list != null && list.size() > 0) {
                        workWorldDetailsAdapter.addData((List<MultiItemEntity>) list);
                        workWorldDetailsAdapter.loadMoreComplete();
                    } else {
                        workWorldDetailsAdapter.loadMoreEnd();
                    }
                }
            });

        }
    }


    private void showHotNewDataHandle(final List<? extends MultiItemEntity> list) {
        if (comment_rc.isComputingLayout()) {
            comment_rc.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showHotNewDataHandle(list);
                }
            }, 500);
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    datalist= list;
                    if(list.size()>0) {


                        WorkWorldDetailsLabelData commentDetails = new WorkWorldDetailsLabelData();
                        commentDetails.setName("最热评论");
                        commentDetails.setCount(0);
                        ((List<MultiItemEntity>) list).add(0, commentDetails);
                    }
                        try {
                            hotData = deepcopy(list);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        ((List<MultiItemEntity>) list).addAll(allData);

                        workWorldDetailsAdapter.setNewData((List<MultiItemEntity>) list);

                        comment_rc.scrollToPosition(0);
                        mSwipeRefreshLayout.setRefreshing(false);


                    hiddenInput();
                }
            });

        }
    }

    public void showNewDataHandle(final List<? extends MultiItemEntity> list, final boolean isScroll) {
        if (comment_rc.isComputingLayout()) {
            comment_rc.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showNewDataHandle(list, isScroll);
                }
            }, 500);
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    datalist= list;
                    WorkWorldDetailsLabelData commentDetails = new WorkWorldDetailsLabelData();
                    commentDetails.setName("全部评论");
                    commentDetails.setType(WorkWorldDetailsLabelData.all);
                    if (commentNum >= 0) {
                        commentDetails.setCount(commentNum);
                    } else {
                        commentDetails.setCount(Integer.parseInt(workWorldItem.getCommentsNum()));
                    }
                    ((List<MultiItemEntity>) list).add(0, commentDetails);
                    try {
                        allData = deepcopy(list);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    ((List<MultiItemEntity>) list).addAll(0, hotData);
                    workWorldDetailsAdapter.setNewData((List<MultiItemEntity>) list);
                    if (isScroll) {
                        switch (hotData.size()) {
                            case 2:
                                comment_rc.scrollToPosition(3);
                                break;
                            case 3:
                                comment_rc.scrollToPosition(4);
                                break;
                            case 4:
                                comment_rc.scrollToPosition(5);
                                break;
                            default:
                                comment_rc.scrollToPosition(1);
                                break;
                        }
//                        if(hotData.size()>3){
//
//                        }else{
//
//                        }
                    } else {
                        comment_rc.scrollToPosition(0);
                    }

                    mSwipeRefreshLayout.setRefreshing(false);

//                    if(showInput){
//                            comment_edittext.requestFocus();
//                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                            imm.showSoftInput(comment_edittext, 0);
//                            showInput= false;
//                    }else {
//                        hiddenInput();
//                    }
                    hiddenInput();
                }
            });

        }

    }

    @Override
    public WorkWorldNewCommentBean getToData() {
        return toData;
    }

    @Override
    public String getCommentsNum() {
        return workWorldItem.getCommentsNum();
    }

    @Override
    public void saveData() {
        Intent intent = new Intent();
        intent.putExtra(WorkWorldActivity.WORK_WORLD_RESULT_ITEM_BACK, workWorldItem);
        setResult(RESULT_OK, intent);
    }

    @Override
    public void startRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public WorkWorldNewCommentBean getLastItem() {
        if (workWorldDetailsAdapter.getData() != null && workWorldDetailsAdapter.getData().size() > 0) {
            return (WorkWorldNewCommentBean) workWorldDetailsAdapter.getData().get(workWorldDetailsAdapter.getData().size() - 1);
        } else {
            return null;
        }
    }

    @Override
    public boolean isCheck() {
        return check;
    }

    @Override
    public int getListCount() {
        int i = workWorldDetailsAdapter.getData().size() - 1;

        if (hotData != null) {
            i = i - hotData.size();
        }
        return i;
    }

    @Override
    public void removeWorkWorldCommentItem(final WorkWorldDeleteResponse deleteWorkWorldItem) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{


                for (int i = 0; i < workWorldDetailsAdapter.getData().size(); i++) {
                    if (workWorldDetailsAdapter.getData().get(i) instanceof WorkWorldNewCommentBean) {
                        if (((WorkWorldNewCommentBean) workWorldDetailsAdapter.getData().get(i)).getCommentUUID().equals(deleteWorkWorldItem.getData().getCommentUUID())) {
                            workWorldDetailsAdapter.remove(i);
                        }
                    }

                }

                for (int i = 0; i < allData.size(); i++) {
                    if(allData.get(i) instanceof  WorkWorldNewCommentBean){
                        if(((WorkWorldNewCommentBean) allData.get(i)).getCommentUUID().equals(deleteWorkWorldItem.getData().getCommentUUID())){
                            if(allData.size()==2){
                                allData.clear();
                            }else{
                                allData.remove(i);
                            }

                        }
                    }
                }
                }catch (Exception e){
                    Logger.i("有可能出错"+e.getMessage());
                }
            }
        });
    }

    @Override
    public void updateLikeNum(int num) {
        this.likeNum = num;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//
                if (likeNum >= 0) {
                    try {
                        if (likeNum > 0) {
                            comment_like_num.setText(likeNum + "");
                        } else {
                            comment_like_num.setText("顶");

                        }
                    } catch (Exception e) {
                        comment_like_num.setText("顶");
                    }


                } else {
                    comment_like_num.setText("顶");
                }
                workWorldItem.setLikeNum(likeNum+"");
            }
        });



    }

    @Override
    public void updateCommentNum(final int num) {
        this.commentNum = num;

        workWorldItem.setCommentsNum(commentNum+"");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < workWorldDetailsAdapter.getData().size(); i++) {
                        if (workWorldDetailsAdapter.getData().get(i) instanceof WorkWorldDetailsLabelData) {
                            if (((WorkWorldDetailsLabelData) workWorldDetailsAdapter.getData().get(i)).getType() == WorkWorldDetailsLabelData.all) {
                                ((WorkWorldDetailsLabelData) workWorldDetailsAdapter.getData().get(i)).setCount(num);
                                LinearLayoutManager l = (LinearLayoutManager) comment_rc.getLayoutManager();
                                if(l.findFirstCompletelyVisibleItemPosition()<=(i+1)&&(i+1)<=l.findLastVisibleItemPosition()){
                                    workWorldDetailsAdapter.notifyItemChanged(i + 1);
                                }

                            }
                        }
                    }
                }catch (Exception e){
                    Logger.i("删除评论更新数量可能会出错"+e.getMessage());
                }


            }
        });


    }

    @Override
    public void updateLikeState(final int isLike) {
     runOnUiThread(new Runnable() {
         @Override
         public void run() {

             if (isLike ==1) {
                 comment_like_icon.setTextColor(getResources().getColor(R.color.atom_ui_new_like_select));
                 comment_like_icon.setText(R.string.atom_ui_new_like_select);
//            #00CABE

             } else {
                 comment_like_icon.setText(R.string.atom_ui_new_like);
                 comment_like_icon.setTextColor(getResources().getColor(R.color.atom_ui_light_gray_99));
             }

             workWorldItem.setIsLike(isLike+"");
         }
     });

    }


    public void hiddenInput() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                // 隐藏软键盘
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                comment_edittext.setText("");
                comment_edittext.setHint("快来说几句...");
                comment_edittext.clearFocus();
                toData = null;
            }
        });

    }


    public List deepcopy(List src) throws IOException,
            ClassNotFoundException {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteout);
        out.writeObject(src);
        ByteArrayInputStream bytein = new ByteArrayInputStream(byteout
                .toByteArray());
        ObjectInputStream in = new ObjectInputStream(bytein);
        List dest = (List) in.readObject();
        return dest;

    }

    @Override
    public void showToast(final String str, final boolean refresh) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(refresh){
                    comment_send_icon.setEnabled(refresh);
                }
                Toast.makeText(WorkWorldDetailsActivity.this, str, Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onDestroy() {

//        finish();
        super.onDestroy();


    }

}
