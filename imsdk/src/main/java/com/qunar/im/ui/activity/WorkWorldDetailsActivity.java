package com.qunar.im.ui.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.ui.util.easyphoto.easyphotos.utils.media.DurationUtils;
import com.orhanobut.logger.Logger;
import com.qunar.im.base.common.QunarIMApp;
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
import com.qunar.im.base.module.WorkWorldOutCommentBean;
import com.qunar.im.ui.presenter.WorkWorldDetailsPresenter;
import com.qunar.im.ui.presenter.impl.WorkWorldDetailsManagerPresenter;
import com.qunar.im.ui.presenter.views.IBrowsingConversationImageView;
import com.qunar.im.ui.presenter.views.WorkWorldDetailsView;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.structs.MessageType;
import com.qunar.im.base.util.ChatTextHelper;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.ui.util.EmotionUtils;
import com.qunar.im.base.util.FileUtils;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.MemoryCache;
import com.qunar.im.ui.util.ProfileUtils;
import com.qunar.im.base.util.graphics.ImageUtils;
import com.qunar.im.base.view.faceGridView.EmoticonEntity;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.ReleaseCircleGridAdapter;
import com.qunar.im.ui.adapter.WorkWorldDetailsAdapter;
import com.qunar.im.ui.imagepicker.util.Utils;
import com.qunar.im.ui.imagepicker.view.GridSpacingItemDecoration;
import com.qunar.im.ui.util.atmanager.AtManager;
import com.qunar.im.ui.util.atmanager.WorkWorldAtManager;
import com.qunar.im.ui.util.videoPlayUtil.VideoPlayUtil;
import com.qunar.im.ui.view.IconView;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.WorkWorldLinkTouchMovementMethod;
import com.qunar.im.ui.view.WorkWorldSpannableTextView;
import com.qunar.im.ui.view.baseView.AnimatedGifDrawable;
import com.qunar.im.ui.view.baseView.AnimatedImageSpan;
import com.qunar.im.ui.view.baseView.processor.TextMessageProcessor;
import com.qunar.im.ui.view.bigimageview.ImageBrowsUtil;
import com.qunar.im.ui.view.recyclerview.BaseQuickAdapter;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.HttpUtil;
import com.qunar.im.utils.QtalkStringUtils;
import com.qunar.im.utils.SoftKeyboardStateHelper;
import com.qunar.im.base.protocol.NativeApi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.qunar.im.base.structs.MessageType.image;
import static com.qunar.im.base.structs.MessageType.link;
import static com.qunar.im.base.structs.MessageType.video;
import static com.qunar.im.ui.activity.PbChatActivity.AT_MEMBER;
import static com.qunar.im.ui.presenter.impl.ReleaseCircleManagerPresenter.ANONYMOUS_NAME;
import static com.qunar.im.ui.presenter.impl.ReleaseCircleManagerPresenter.REAL_NAME;
import static com.qunar.im.ui.activity.IdentitySelectActivity.ANONYMOUS_DATA;
import static com.qunar.im.ui.activity.IdentitySelectActivity.now_identity_type;
import static com.qunar.im.ui.activity.WorkWorldReleaseCircleActivity.EXTRA_IDENTITY;
import static com.qunar.im.ui.activity.WorkWorldReleaseCircleActivity.REQUEST_CODE_IDENTITY;
import static com.qunar.im.ui.activity.WorkWorldReleaseCircleActivity.UUID_STR;

public class WorkWorldDetailsActivity extends SwipeBackActivity implements AtManager.AtTextChangeListener, WorkWorldDetailsView {

    public static String WORK_WORLD_DETAILS_ITEM = "WORK_WORLD_DETAILS_ITEM";
    public static String WORK_WORLD_DETAILS_COMMENT = "WORK_WORLD_DETAILS_COMMENT";
    private static String defaultHeadUrl = QtalkNavicationService.getInstance().getInnerFiltHttpHost() + "/file/v2/download/perm/3ca05f2d92f6c0034ac9aee14d341fc7.png";


    //@消息管理
    private WorkWorldAtManager mAtManager;
    protected boolean canShowAtActivity = true;

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

    private LinearLayoutManager linearLayoutManager;

    private int iconSize;
    private int defaultSize;

    private View headView;
    //头部view
    private SimpleDraweeView user_header;//帖子头像
    private TextView user_name;//帖子姓名
    private TextView user_architecture;//帖子部门
    private IconView right_special;//帖子功能按钮
    private WorkWorldSpannableTextView text_content;//帖子内容
    private RecyclerView img_rc;//九宫格
    private TextView time;//时间
    private LinearLayout like_layout;//点赞按钮
    private LinearLayout comment_layout;//评论按钮
    private LinearLayout comment_list_layout;//评论区域
    private TextView comment_count;//总计评论数
    private LinearLayout link_ll;//连接区域
    private TextView link_title;//连接 标题
    private SimpleDraweeView link_icon; //连接图标
    private RelativeLayout video_ll;//视频区域
    private SimpleDraweeView video_image;//视频图片
    private TextView video_time;//视频时常


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

    private boolean allIsOk;
    private boolean localMore;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_work_world_details_activity);

        mAtManager = new WorkWorldAtManager(this, CurrentPreference.getInstance().getPreferenceUserId());
        mAtManager.setTextChangeListener(this);
        bindView();

        if (getIntent().hasExtra(WORK_WORLD_DETAILS_COMMENT)) {
            showInput = getIntent().getBooleanExtra(WORK_WORLD_DETAILS_COMMENT, false);
        }

        if (getIntent().hasExtra(WORK_WORLD_DETAILS_ITEM)) {
            workWorldItem = (WorkWorldItem) getIntent().getSerializableExtra(WORK_WORLD_DETAILS_ITEM);
            startInit();
        } else {
            //此处应该请求借口开始操作
        }

        initIdentity();
        defaultSize = com.qunar.im.base.util.Utils.dipToPixels(QunarIMApp.getContext(), 96);
        iconSize = com.qunar.im.base.util.Utils.dpToPx(QunarIMApp.getContext(), 32);

        softKeyboardStateHelper = new SoftKeyboardStateHelper(findViewById(R.id.work_world_activity_main_layout));
        softKeyboardStateHelper.addSoftKeyboardStateListener(new SoftKeyboardStateHelper.SoftKeyboardStateListener() {
            @Override
            public void onSoftKeyboardOpened(int keyboardHeightInPx) {

            }

            @Override
            public void onSoftKeyboardClosed() {
                hiddenInput(false);
            }
        });

    }

    public void initIdentity() {
        try {
            String data = DataUtils.getInstance(this).getPreferences("workworldAnonymous" + workWorldItem.getUuid(), "");
            if (!TextUtils.isEmpty(data)) {
                mAnonymousData = JsonUtils.getGson().fromJson(data, AnonymousData.class);
                identityType = ANONYMOUS_NAME;
                initAnonymousHeader();
            }
        } catch (Exception e) {

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startInit() {
        bindHeadData();
        bindSelfData();
        initAdapter();
        workWorldDetailsPresenter.loadingHistory();
        qtNewActionBar.setFocusableInTouchMode(true);
        qtNewActionBar.requestFocus();

    }

    private void bindSelfData() {
        workWorldDetailsPresenter = new WorkWorldDetailsManagerPresenter();
        workWorldDetailsPresenter.setView(this);
        showLikeNum(workWorldItem);
        showLikeState(workWorldItem);
        comment_send_icon.setVisibility(View.GONE);


//        comment_edittext.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//
//            }
//        });


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
                mAtManager.beforeTextChanged(s, start, count, after);
                //这一套操作的意思不明确 应该为在删除状态时,不让@界面出现
                if (count > 0 && after == 0) {
                    canShowAtActivity = false;
                }
            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAtManager.onTextChanged(s, start, before, count);
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
                mAtManager.afterTextChanged(s);
                //根据当前是删除了字段或者新写字段来判断是否开启@界面
                if (!canShowAtActivity) {
                    canShowAtActivity = true;
                    return;
                }
                if (s.length() > 200) {
                    showToast("请输入不超过200个字符的评论", false);
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
                if (identityType == ANONYMOUS_NAME) {
                    intent.putExtra(ANONYMOUS_DATA, mAnonymousData);
                }
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

            case AT_MEMBER:
                comment_edittext.requestFocus();
                mAtManager.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateIdentity(Intent data) {
        int i = data.getIntExtra(EXTRA_IDENTITY, 0);
        switch (i) {
            case REAL_NAME:
                identityType = REAL_NAME;

                DataUtils.getInstance(this).removePreferences("workworldAnonymous" + workWorldItem.getUuid());
                initRealHeader();
                break;

            case ANONYMOUS_NAME:
                identityType = ANONYMOUS_NAME;
                mAnonymousData = (AnonymousData) data.getSerializableExtra(ANONYMOUS_DATA);
                DataUtils.getInstance(this).putPreferences("workworldAnonymous" + workWorldItem.getUuid(), JsonUtils.getGson().toJson(mAnonymousData));
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


    View.OnClickListener dialogClick = new View.OnClickListener() {
        @Override
        public void onClick(final View cView) {


            final WorkWorldNewCommentBean item = (WorkWorldNewCommentBean) cView.getTag();
            if (item == null) {
                return;
            }

//


            bottomSheetDialog = new BottomSheetDialog(WorkWorldDetailsActivity.this);
            View view = LayoutInflater.from(WorkWorldDetailsActivity.this).inflate(R.layout.atom_ui_work_world_special_popwindow, null);
//            TextView delete =

            TextView delete = view.findViewById(R.id.work_world_popwindow_delete);
            TextView reply = view.findViewById(R.id.work_world_popwindow_reply);
            TextView cancle = view.findViewById(R.id.work_world_popwindow_cancle);
            TextView copy = view.findViewById(R.id.work_world_popwindow_copy);


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

            copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //获取剪贴板管理器：
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    // 创建普通字符型ClipData
                    ClipData mClipData = ClipData.newPlainText("Label", item.getContent());
                    // 将ClipData内容放到系统剪贴板里。
                    cm.setPrimaryClip(mClipData);
                    Toast.makeText(WorkWorldDetailsActivity.this, "复制", Toast.LENGTH_LONG).show();
                    bottomSheetDialog.dismiss();
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
    };


    private void initAdapter() {
        workWorldDetailsAdapter = new WorkWorldDetailsAdapter(new ArrayList<MultiItemEntity>(), this);

        workWorldDetailsAdapter.bindToRecyclerView(comment_rc);
//        comment_rc.setAdapter(workWorldDetailsAdapter);
        linearLayoutManager = new LinearLayoutManager(this);
        comment_rc.setLayoutManager(linearLayoutManager);
        workWorldDetailsAdapter.addHeaderView(headView);
        workWorldDetailsAdapter.setLinearLayoutManager(linearLayoutManager);
        workWorldDetailsAdapter.setPostOwnerAndHost(workWorldItem.getOwner(), workWorldItem.getOwnerHost());
        workWorldDetailsAdapter.setOnClickListener(dialogClick);

        workWorldDetailsAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (allIsOk) {
                    loadMore(localMore);
                }

            }
        });
    }

    public void loadMore(boolean localMore) {
        workWorldDetailsPresenter.loadingMore(localMore);
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
                        comment_edittext.setHint("回复: " + nick.getName() + " (200字以内)");
                    } else {
                        comment_edittext.setHint("回复: " + nick.getXmppId() + " (200字以内)");
                    }
//                    }
//                });


                }
            }, false, false);

        } else {
            comment_edittext.setHint("回复: " + data.getAnonymousName() + " (200字以内)");
        }

        comment_edittext.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(comment_edittext, 0);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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

//            img_rc.setVisibility(View.VISIBLE);
            text_content.setVisibility(View.VISIBLE);
            contentData = contentData1;
            String str = contentData.getExContent();
            if (TextUtils.isEmpty(str)) {
                str = contentData.getContent();
            }
            if (!TextUtils.isEmpty(str)) {
//                text_content.setText(contentData.getContent());
                setContent(contentData);
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


            showFunction(contentData);
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
//                        Intent intent = new Intent(WorkWorldDetailsActivity.this, ImageBrowersingActivity.class);
//                        int location[] = new int[2];
//                        view.getLocationOnScreen(location);
//                        intent.putExtra("left", location[0]);
//                        intent.putExtra("top", location[1]);
//                        intent.putExtra("height", finalSize);
//                        intent.putExtra("width", finalSize);
//
//                        String url = contentData.getImgList().get(position).getData();
//                        if (!(url.startsWith("http") || url.startsWith("https"))) {
//                            url = QtalkNavicationService.getInstance().getInnerFiltHttpHost() + "/" + url;
//                        }
//                        intent.putExtra(Constants.BundleKey.IMAGE_URL, url);
//                        intent.putExtra(Constants.BundleKey.IMAGE_ON_LOADING, url);
//                        intent.putExtra(Constants.BundleKey.WORK_WORLD_BROWERSING, (Serializable) parseList(contentData.getImgList()));
//                        WorkWorldDetailsActivity.this.startActivity(intent);
//                        ((Activity) WorkWorldDetailsActivity.this).overridePendingTransition(0, 0);
                        ImageBrowsUtil.openImageWorkWorld(position, contentData.getImgList(), WorkWorldDetailsActivity.this);
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showFunction(final ReleaseContentData contentData) {

        //初始情况全部控件不展示
        img_rc.setVisibility(View.GONE);

        try {
            switch (contentData.getType()) {
                case video:
                    video_ll.setVisibility(View.VISIBLE);
                    final String thumbUrl = QtalkStringUtils.addFilePathDomain(contentData.getVideoContent().ThumbUrl, true);
                    final String fileUrl = QtalkStringUtils.addFilePathDomain(contentData.getVideoContent().FileUrl, true);
                    final String fileSize = contentData.getVideoContent().FileSize;
                    final String downLoadPath = contentData.getVideoContent().FileUrl;
                    final String fileName = contentData.getVideoContent().FileName;
                    final String localPath = contentData.getVideoContent().LocalVideoOutPath;
                    final boolean onlyDownLoad = contentData.getVideoContent().newVideo;
                    ProfileUtils.displayLinkImgByImageSrc(WorkWorldDetailsActivity.this, thumbUrl,getDrawable(R.drawable.atom_ui_link_default), (ImageView) video_image,
                            Utils.dp2px(this,144), Utils.dp2px(this,144));
//                    ProfileUtils.displayGravatarByImageSrc(mActivity,thumbUrl ,   (ImageView)helper.getView(R.id.re_video_image),
//                                       mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_video_image), mActivity.getResources().getDimensionPixelSize(R.dimen.atom_ui_video_image));
                    video_time.setText(DurationUtils.format(Integer.parseInt(contentData.getVideoContent().Duration)));
                    video_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!TextUtils.isEmpty(localPath)&&new File(localPath).exists()){
                                VideoPlayUtil.conAndWwOpen(WorkWorldDetailsActivity.this,localPath,fileName,thumbUrl,downLoadPath, !onlyDownLoad, fileSize);
                            }else {

                            VideoPlayUtil.conAndWwOpen(WorkWorldDetailsActivity.this,fileUrl,fileName,thumbUrl,downLoadPath, !onlyDownLoad, fileSize);
                            }
//                            Intent intent = new Intent(WorkWorldDetailsActivity.this, VideoPlayActivity.class);
//                            intent.putExtra(VideoPlayActivity.PLAYPATH,fileUrl);
//                            intent.putExtra(VideoPlayActivity.PLAYTHUMB,thumbUrl);
//                            intent.putExtra(VideoPlayActivity.DOWNLOADPATH,downLoadPath);
//                            intent.putExtra(VideoPlayActivity.SHOWSHARE,true);
//                            intent.putExtra(VideoPlayActivity.FILENAME,fileName);
//                            startActivity(intent);
                        }
                    });
                    break;
                case MessageType.link:
                    link_ll.setVisibility(View.VISIBLE);
                    link_title.setText(contentData.getLinkContent().title);
                    int imgsize = Utils.dp2px(this, 70);
                    if (TextUtils.isEmpty(contentData.getLinkContent().img)) {
                        link_icon.setBackground(getDrawable(R.drawable.atom_ui_link_default));
                    } else {
                        ProfileUtils.displayLinkImgByImageSrc(this, contentData.getLinkContent().img, getDrawable(R.drawable.atom_ui_link_default), (ImageView) link_icon,
                                imgsize, imgsize);
                    }
                    link_ll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String url = contentData.getLinkContent().linkurl;
                            Intent intent = new Intent(WorkWorldDetailsActivity.this, QunarWebActvity.class);
                            intent.setData(Uri.parse(url));
                            startActivity(intent);
                        }
                    });


                    break;
                case MessageType.image:
                default:
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
                        size = Utils.getImageItemWidthForWorld(this, column);
                        for (int j = contentData.getImgList().size() - 1; j >= 0; j--) {
                            if (contentData.getImgList().get(j) == null) {
                                contentData.getImgList().remove(i);
                            }
                        }


                        ReleaseCircleGridAdapter itemAdapter = new ReleaseCircleGridAdapter(contentData.getImgList(), this, column);
                        GridLayoutManager manager = new GridLayoutManager(this, column);
                        final int finalSize = size;
                        itemAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                                Intent intent = new Intent(WorkWorldDetailsActivity.this, ImageBrowersingActivity.class);
//                                int location[] = new int[2];
//                                view.getLocationOnScreen(location);
//                                intent.putExtra("left", location[0]);
//                                intent.putExtra("top", location[1]);
//                                intent.putExtra("height", finalSize);
//                                intent.putExtra("width", finalSize);
//
//                                String url = contentData.getImgList().get(position).getData();
//                                if (!(url.startsWith("http") || url.startsWith("https"))) {
//                                    url = QtalkNavicationService.getInstance().getInnerFiltHttpHost() + "/" + url;
//                                }
//                                intent.putExtra(Constants.BundleKey.IMAGE_URL, url);
//                                intent.putExtra(Constants.BundleKey.IMAGE_ON_LOADING, url);
//                                intent.putExtra(Constants.BundleKey.WORK_WORLD_BROWERSING, (Serializable) parseList(contentData.getImgList()));
//                                WorkWorldDetailsActivity.this.startActivity(intent);
//                                WorkWorldDetailsActivity.this.overridePendingTransition(0, 0);

                                ImageBrowsUtil.openImageWorkWorld(position, contentData.getImgList(), WorkWorldDetailsActivity.this);

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
                    break;
            }


        } catch (Exception e) {
            Logger.i("朋友圈功能展示出错:" + e.getMessage());
        }
    }


    private void setContent(ReleaseContentData contentData) {

        String exstr = contentData.getExContent();

        WorkWorldSpannableTextView textView = text_content;
        textView.setTag(contentData);
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View cView) {
//                final WorkWorldNewCommentBean item = (WorkWorldNewCommentBean) cView.getTag();
//                if (item == null) {
//                    return true;
//                }

//


                bottomSheetDialog = new BottomSheetDialog(WorkWorldDetailsActivity.this);
                View view = LayoutInflater.from(WorkWorldDetailsActivity.this).inflate(R.layout.atom_ui_work_world_special_popwindow, null);
//            TextView delete =

                TextView delete = view.findViewById(R.id.work_world_popwindow_delete);
                TextView reply = view.findViewById(R.id.work_world_popwindow_reply);
                TextView cancle = view.findViewById(R.id.work_world_popwindow_cancle);
                TextView copy = view.findViewById(R.id.work_world_popwindow_copy);


                delete.setVisibility(View.GONE);
                reply.setVisibility(View.GONE);
                copy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //获取剪贴板管理器：
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        // 创建普通字符型ClipData
                        ClipData mClipData = ClipData.newPlainText("Label", ((WorkWorldSpannableTextView) cView).getText());
                        // 将ClipData内容放到系统剪贴板里。
                        cm.setPrimaryClip(mClipData);
                        Toast.makeText(WorkWorldDetailsActivity.this, "复制", Toast.LENGTH_LONG).show();
                        bottomSheetDialog.dismiss();
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

                return true;
            }
        });
        SpannableStringBuilder sb = new SpannableStringBuilder();

        switch (contentData.getType()) {
            case link:
            case image:
            case video:
                sb = getSpannableString(exstr, textView, sb);
                textView.setText(sb);
                break;
            default:
//                sb.append(contentData.getContent());
                String msg = ChatTextHelper.textToHTML(contentData.getContent());
                sb = getSpannableString(msg, textView, sb);
                textView.setText(sb);
                break;
        }

        WorkWorldLinkTouchMovementMethod linkTouchMovementMethod = new WorkWorldLinkTouchMovementMethod();
        textView.setLinkTouchMovementMethod(linkTouchMovementMethod);
        textView.setMovementMethod(linkTouchMovementMethod);


//        if (TextUtils.isEmpty(exstr)) {
////            str = contentData.getContent();
//            textView.setText(contentData.getContent());
//            return;
//        }

//
//
//
//        ((TextView) helper.getView(R.id.text_content)).setText(contentData.getContent());
    }

    private SpannableStringBuilder getSpannableString(String exstr, WorkWorldSpannableTextView textView, SpannableStringBuilder sb) {
        boolean newTextView = true;

        List<Map<String, String>> list = ChatTextHelper.getObjList(exstr);
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
                                        is = getAssets().open(path);
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
                                    Logger.i("error:" + e.getMessage());
                                } finally {
                                    if (is != null) {
                                        try {
                                            is.close();
                                        } catch (IOException e) {
//                                            LogUtil.e(TAG, "ERROR", e);
                                            Logger.i("error:" + e.getMessage());
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
                                    span = new ImageSpan(WorkWorldDetailsActivity.this, (Bitmap) cached);
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
//                            if (widget instanceof EmojiconTextView) {
                            Intent intent = new Intent(WorkWorldDetailsActivity.this, QunarWebActvity.class);
                            intent.setData(Uri.parse(url));
                            WorkWorldDetailsActivity.this.startActivity(intent);
//                            }
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
        return sb;
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

        link_ll = headView.findViewById(R.id.re_link_ll);
        link_icon = headView.findViewById(R.id.re_link_icon);
        link_title = headView.findViewById(R.id.re_link_title);

        video_ll = headView.findViewById(R.id.re_video_ll);
        video_image = headView.findViewById(R.id.re_video_image);
        video_time = headView.findViewById(R.id.re_video_time);

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
    public void onTextAdd(String content, int stat, int length) {
        Editable edit = comment_edittext.getEditableText();
        edit.insert(stat, content);
    }

    @Override
    public void onTextDelete(int start, int length) {
        Editable edit = comment_edittext.getEditableText();
        edit.delete(start, start + length);
    }


    public static class TopSmoothScroller extends LinearSmoothScroller {
        public TopSmoothScroller(Context context) {
            super(context);
        }

        @Override
        protected int getHorizontalSnapPreference() {
            return SNAP_TO_START;//具体见源码注释
        }

        @Override
        protected int getVerticalSnapPreference() {
            return SNAP_TO_START;//具体见源码注释
        }

    }


    @Override
    public void updateNewCommentData(final List<? extends MultiItemEntity> list, boolean isScroll, boolean isShowInput) {
//        workWorldDetailsAdapter.getData()
//        for (int i = 0; i < workWorldDetailsAdapter.getData().size(); i++) {
//
//        }


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {

                    WorkWorldNewCommentBean data = (WorkWorldNewCommentBean) list.get(0);
                    for (int i = 0; i < workWorldDetailsAdapter.getData().size(); i++) {
                        if (workWorldDetailsAdapter.getData().get(i) instanceof WorkWorldNewCommentBean) {

                            if (((WorkWorldNewCommentBean) workWorldDetailsAdapter.getData().get(i)).getCommentUUID().equals(data.getCommentUUID())) {
//                                comment_rc.scrollToPosition(i);
                                LinearSmoothScroller smoothScroller = new TopSmoothScroller(WorkWorldDetailsActivity.this);
                                smoothScroller.setTargetPosition(i + workWorldDetailsAdapter.getHeaderLayoutCount());
                                linearLayoutManager.startSmoothScroll(smoothScroller);
//                                comment_rc.smoothScrollToPosition(i +workWorldDetailsAdapter.getHeaderLayoutCount());

//                                while (comment_rc.isComputingLayout()){
//
//                                }
                                changeItem(data, i);
//                                comment_rc.scrollTo(40,0);
//                                final int finalI = i;
//                                new Thread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        try {
//                                            Thread.sleep(500);
//
//                                        }catch (Exception e){
//                                            Logger.i("稍等一下移动view");
//                                        }
////
//                                    }
//                                }).start();

//                                        new String();
//                                        w
                                break;
//
//                                workWorldDetailsAdapter.notifyItemChanged();
                            }
                        }

                    }

                    for (int i = 0; i < allData.size(); i++) {
                        if (allData.get(i) instanceof WorkWorldNewCommentBean) {
                            if (((WorkWorldNewCommentBean) allData.get(i)).getCommentUUID().equals(data.getCommentUUID())) {
//                                if(allData.size()==2){
//                                    allData.clear();
//                                }else{
                                allData.remove(i);
                                allData.add(i, data);

//                                }

                            }
                        }
                    }
                } catch (Exception e) {
                    Logger.i("有可能出错" + e.getMessage());
                }
            }
        });

        hiddenInput(true);


    }

    private void changeItem(final WorkWorldNewCommentBean data, final int i) {
        if (comment_rc.isComputingLayout()) {
            comment_rc.postDelayed(new Runnable() {
                @Override
                public void run() {
                    changeItem(data, i);
                }
            }, 500);
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    workWorldDetailsAdapter.getData().set(i, data);
//                                workWorldDetailsAdapter.getData().remove(i);
//                                workWorldDetailsAdapter.getData().add(i,data);
//                                workWorldDetailsAdapter.notifyItemChanged(i);
//                                workWorldDetailsAdapter.remove(i);
//                                workWorldDetailsAdapter.addData(i,data);
//                                workWorldDetailsAdapter.addData();
                    workWorldDetailsAdapter.updateCommentItem(data, i);
                }
            });

        }
    }

    @Override
    public void showNewData(List<? extends MultiItemEntity> list, boolean isScroll, boolean isShowInput, boolean isLocal) {
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
//            if (list.size() > 0) {
            showNewDataHandle(list, isScroll, isLocal);
//            } else {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mSwipeRefreshLayout.setRefreshing(false);
//                    }
//                });


//                if(showInput){
//                    comment_edittext.requestFocus();
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.showSoftInput(comment_edittext, 0);
//                    showInput= false;
//                }else {
//                    hiddenInput();
//                }
            hiddenInput(true);
//            }
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
            hiddenInput(true);
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
    public void showMoreData(List<? extends MultiItemEntity> list, boolean localMore) {
//        if (list != null && list.size() > 0) {
//            workWorldAdapter.setNewData(list);
//            work_world_rc.scrollToPosition(0);
        showMoreNewDataHandle(list, localMore);
//        }
    }

    private void showMoreNewDataHandle(final List<? extends MultiItemEntity> list, final boolean localMore) {
        if (comment_rc.isComputingLayout()) {
            comment_rc.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showMoreNewDataHandle(list, localMore);
                }
            }, 500);
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (localMore != WorkWorldDetailsActivity.this.localMore) {
                        return;
                    }

                    if (list != null && list.size() > 0) {
                        checkHotRepeat(list, false);
                        workWorldDetailsAdapter.addData((List<MultiItemEntity>) list);
                        if (list.size() > 0) {
                            workWorldDetailsAdapter.loadMoreComplete();
                        } else {
                            workWorldDetailsAdapter.loadMoreEnd();
                        }

                    } else {
                        workWorldDetailsAdapter.loadMoreEnd();
                    }
                }
            });

        }
    }


    private List<? extends MultiItemEntity> checkHotRepeat(List<? extends MultiItemEntity> list, boolean cleanTip) {
//        if(hotData==null||hotData.size()==0){
//            return list;
//        }else{
//            for (int i = 0; i < hotData.size(); i++) {
//                if(hotData.get(i) instanceof  WorkWorldNewCommentBean) {
//                    for (int j = list.size() - 1; j >= 0; j--) {
//                        if (list.get(j) instanceof WorkWorldNewCommentBean) {
//                            if (((WorkWorldNewCommentBean) hotData.get(i)).getCommentUUID().equals(((WorkWorldNewCommentBean) list.get(j)).getCommentUUID())) {
//                                list.remove(j);
//                                break;
//                            }
//                        }
//                    }
//                }
//            }
//            if(cleanTip&&list.size()==1) {
//                list.remove(0);
//            }
//                return list;
//        }

        return list;
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
                    if (list.size() > 0) {


                        WorkWorldDetailsLabelData commentDetails = new WorkWorldDetailsLabelData();
                        commentDetails.setName("热评");
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

                    ((List<MultiItemEntity>) list).addAll(checkHotRepeat(allData, true));
                    //此处-1是为了确定元素下标
                    workWorldDetailsAdapter.setDeleteLine(hotData.size() - 1);
                    workWorldDetailsAdapter.setNewData((List<MultiItemEntity>) list);
                    workWorldDetailsAdapter.loadMoreComplete();
                    comment_rc.scrollToPosition(0);
                    mSwipeRefreshLayout.setRefreshing(false);


                    hiddenInput(true);
                }
            });

        }
    }

    public void showNewDataHandle(final List<? extends MultiItemEntity> list, final boolean isScroll, final boolean isLocal) {
        if (comment_rc.isComputingLayout()) {
            comment_rc.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showNewDataHandle(list, isScroll, isLocal);
                }
            }, 500);
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    datalist= list;
                    WorkWorldDetailsLabelData commentDetails = new WorkWorldDetailsLabelData();
                    commentDetails.setName("评论");
                    commentDetails.setType(WorkWorldDetailsLabelData.all);
                    if (commentNum >= 0) {
                        commentDetails.setCount(commentNum);
                    } else {
                        commentDetails.setCount(Integer.parseInt(workWorldItem.getCommentsNum()));
                    }
                    if (list.size() > 0) {
                        ((List<MultiItemEntity>) list).add(0, commentDetails);
                    }


                    try {
                        allData = deepcopy(list);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    checkHotRepeat(list, true);
                    ((List<MultiItemEntity>) list).addAll(0, hotData);
                    workWorldDetailsAdapter.setNewData((List<MultiItemEntity>) list);
                    if (!allIsOk) {
                        if (isLocal) {
                            localMore = true;
                        } else {
                            localMore = false;

                        }
                        allIsOk = true;
                    }
                    workWorldDetailsAdapter.loadMoreComplete();
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
                    hiddenInput(true);
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
                try {

                    if (TextUtils.isEmpty(deleteWorkWorldItem.getData().getDeleteCommentData().getSuperParentCommentUUID())) {


                        for (int i = 0; i < workWorldDetailsAdapter.getData().size(); i++) {
                            if (workWorldDetailsAdapter.getData().get(i) instanceof WorkWorldNewCommentBean) {
                                if (((WorkWorldNewCommentBean) workWorldDetailsAdapter.getData().get(i)).getCommentUUID().equals(deleteWorkWorldItem.getData().getDeleteCommentData().getCommentUUID())) {
                                    workWorldDetailsAdapter.deleteCommentItem(i, deleteWorkWorldItem.getData().getDeleteCommentData());
//                                    workWorldDetailsAdapter.remove(i);
                                }
                            }

                        }
                    } else {
                        for (int i = 0; i < workWorldDetailsAdapter.getData().size(); i++) {
                            if (workWorldDetailsAdapter.getData().get(i) instanceof WorkWorldNewCommentBean) {
                                if (((WorkWorldNewCommentBean) workWorldDetailsAdapter.getData().get(i)).getCommentUUID().equals(deleteWorkWorldItem.getData().getDeleteCommentData().getSuperParentCommentUUID())) {
                                    workWorldDetailsAdapter.deleteCommentItem(i, deleteWorkWorldItem.getData().getDeleteCommentData());
//                                    workWorldDetailsAdapter.remove(i);
                                }
                            }

                        }
                    }

//                    if (TextUtils.isEmpty(workworlddeleteWorkWorldItem.getData().getDeleteCommentData().getSuperParentCommentUUID())) {
//                        for (int i = 0; i < allData.size(); i++) {
//                            if (allData.get(i) instanceof WorkWorldNewCommentBean) {
//                                if (((WorkWorldNewCommentBean) allData.get(i)).getCommentUUID().equals(workworlddeleteWorkWorldItem.getData().getDeleteCommentData().getCommentUUID())) {
//                                    if (allData.size() == 2) {
//                                        allData.clear();
//                                    } else {
//                                        allData.remove(i);
//                                    }
//
//                                }
//                            }
//                        }
//                    } else {
//                        for (int i = 0; i < allData.size(); i++) {
//                            if (allData.get(i) instanceof WorkWorldNewCommentBean) {
//                                if (((WorkWorldNewCommentBean) allData.get(i)).getCommentUUID().equals(workworlddeleteWorkWorldItem.getData().getDeleteCommentData().getSuperParentCommentUUID())) {
//                                    if (allData.size() == 2) {
//                                        allData.clear();
//                                    } else {
//                                        allData.remove(i);
//                                    }
//
//                                }
//                            }
//                        }
//                    }


                } catch (Exception e) {
                    Logger.i("有可能出错" + e.getMessage());
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
                workWorldItem.setLikeNum(likeNum + "");
            }
        });


    }

    @Override
    public void updateCommentNum(final int num) {
        this.commentNum = num;

        workWorldItem.setCommentsNum(commentNum + "");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < workWorldDetailsAdapter.getData().size(); i++) {
                        if (workWorldDetailsAdapter.getData().get(i) instanceof WorkWorldDetailsLabelData) {
                            if (((WorkWorldDetailsLabelData) workWorldDetailsAdapter.getData().get(i)).getType() == WorkWorldDetailsLabelData.all) {
                                ((WorkWorldDetailsLabelData) workWorldDetailsAdapter.getData().get(i)).setCount(num);
                                LinearLayoutManager l = (LinearLayoutManager) comment_rc.getLayoutManager();
                                if (l.findFirstCompletelyVisibleItemPosition() <= (i + 1) && (i + 1) <= l.findLastVisibleItemPosition()) {
                                    workWorldDetailsAdapter.notifyItemChanged(i + 1);
                                }

                            }
                        }
                    }
                } catch (Exception e) {
                    Logger.i("删除评论更新数量可能会出错" + e.getMessage());
                }


            }
        });


    }

    @Override
    public void updateLikeState(final int isLike) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (isLike == 1) {
                    comment_like_icon.setTextColor(getResources().getColor(R.color.atom_ui_new_like_select));
                    comment_like_icon.setText(R.string.atom_ui_new_like_select);
//            #00CABE

                } else {
                    comment_like_icon.setText(R.string.atom_ui_new_like);
                    comment_like_icon.setTextColor(getResources().getColor(R.color.atom_ui_light_gray_99));
                }

                workWorldItem.setIsLike(isLike + "");
            }
        });

    }

    @Override
    public void updateOutCommentList(List<? extends MultiItemEntity> list) {
        workWorldItem.setAttachCommentList((List<WorkWorldOutCommentBean>) list);

    }

    @Override
    public Map<String, String> getAtList() {
        return mAtManager.getAtBlocks();
    }

    @Override
    public void deleteAtList() {
        mAtManager.reset();
    }


    public void hiddenInput(final boolean clearText) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                // 隐藏软键盘
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                if (clearText) {
                    comment_edittext.setText("");
                    toData = null;
                    comment_edittext.setHint("快来说几句...(200字以内)");
                }


                comment_edittext.clearFocus();

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
                if (refresh) {
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
