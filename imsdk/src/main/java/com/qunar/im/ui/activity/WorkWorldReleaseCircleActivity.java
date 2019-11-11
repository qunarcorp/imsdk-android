package com.qunar.im.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.ui.util.easyphoto.easyphotos.callback.SelectCallback;
import com.qunar.im.ui.util.easyphoto.easyphotos.models.album.entity.Photo;
import com.qunar.im.ui.util.easyphoto.easyphotos.utils.PhotoUtil;
import com.qunar.im.ui.util.easyphoto.easyphotos.utils.media.DurationUtils;
import com.orhanobut.logger.Logger;
import com.qunar.im.base.jsonbean.ExtendMessageEntity;
import com.qunar.im.base.module.AnonymousData;
import com.qunar.im.base.module.MultiItemEntity;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.module.ReleaseCircleImageItemDate;
import com.qunar.im.base.module.ReleaseCircleNoChangeItemDate;
import com.qunar.im.base.module.WorkWorldItem;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.ui.presenter.ReleaseCirclePresenter;
import com.qunar.im.ui.presenter.impl.ReleaseCircleManagerPresenter;
import com.qunar.im.ui.presenter.views.ReleaseCircleView;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.IMUserDefaults;
import com.qunar.im.ui.util.ImageSelectGlideEngine;
import com.qunar.im.ui.util.ImageSelectUtil;
import com.qunar.im.ui.util.ProfileUtils;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.ListUtil;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.ReleaseCircleGridAdapter;
import com.qunar.im.ui.broadcastreceivers.ShareReceiver;
import com.qunar.im.ui.imagepicker.DataHolder;
import com.qunar.im.ui.imagepicker.ImagePicker;
import com.qunar.im.base.module.ImageItem;
import com.qunar.im.ui.imagepicker.ui.ImageGridActivity;
import com.qunar.im.ui.imagepicker.ui.ImagePreviewActivity;
import com.qunar.im.ui.imagepicker.util.Utils;
import com.qunar.im.ui.imagepicker.view.GridSpacingItemDecoration;
import com.qunar.im.ui.util.atmanager.AtManager;
import com.qunar.im.ui.util.atmanager.WorkWorldAtManager;
import com.qunar.im.ui.util.videoPlayUtil.VideoPlayUtil;
import com.qunar.im.ui.view.Control;
import com.qunar.im.ui.view.IconView;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.RSoftInputLayout;
import com.qunar.im.ui.view.recyclerview.BaseQuickAdapter;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;
import com.qunar.im.utils.ConnectionUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import static com.qunar.im.base.module.ReleaseCircleType.TYPE_UNCLICKABLE;
import static com.qunar.im.ui.activity.PbChatActivity.AT_MEMBER;
import static com.qunar.im.ui.presenter.impl.ReleaseCircleManagerPresenter.ANONYMOUS_NAME;
import static com.qunar.im.ui.presenter.impl.ReleaseCircleManagerPresenter.REAL_NAME;
import static com.qunar.im.ui.activity.IdentitySelectActivity.ANONYMOUS_DATA;
import static com.qunar.im.ui.activity.IdentitySelectActivity.now_identity_type;

public class WorkWorldReleaseCircleActivity extends SwipeBackActivity implements AtManager.AtTextChangeListener, ReleaseCircleView {

    public static final int ACTIVITY_SELECT_PHOTO_Release = 1;
    public static final int REQUEST_CODE_PREVIEW_Release = 101;
    public static final int REQUEST_CODE_PERVIEW_DELETE = 102;
    public static final int REQUEST_CODE_IDENTITY = 103;
    public static final int REQUEST_CODE_AT = 104;


    public static final String EXTRA_IDENTITY = "identity_type";

    public static final String UUID_STR = "UUID_STR";

    //@消息管理
    private WorkWorldAtManager mAtManager;
    protected boolean canShowAtActivity = true;

    protected IconView release_image_layout;
    protected IconView release_video_layout;

    Control mControl;


    protected QtNewActionBar qtNewActionBar;//头部导航
    protected EditText release_text;//朋友圈文字内容
    protected RecyclerView mRecyclerView;//图片排版
    protected ReleaseCircleGridAdapter releaseCircleGridAdapter;//图片排版用
    protected SimpleDraweeView re_video_image;
    protected ItemTouchHelper itemTouchHelper;
    protected List<MultiItemEntity> picList;
    protected LinearLayout release_identity_layout;
    protected RelativeLayout re_video_ll;
    protected TextView re_video_time;
    protected IconView release_at_layout;
    protected TextView release_identity;
    protected SimpleDraweeView an_header;
    protected LinearLayout re_link_ll;
    protected SimpleDraweeView re_link_icon;
    protected TextView re_link_title;
    protected ImageView clear_video;
    private int imgSize = 9;
    private int mImageSize;
    private ProgressDialog dialog;
    private ExtendMessageEntity entity;//分享用
    private TextView release_number_words;
    private int releaseType = -1;

    public static int imageType = 1;
    public static int videoType = 2;
    public static int linkType = 3;

    private Photo videoData;

    private boolean check = true;

    private ReleaseCirclePresenter releaseCirclePresenter;
    private String uuid;
    private String anonymous;
    private String anonymousPhoto;
    private int identityType = 0;
    private String plusImg = "https://qim.qunar.com/file/v2/download/temp/new/f798efc14a64e9abb7a336e8de283e5e.png?name=f798efc14a64e9abb7a336e8de283e5e.png&amp;file=file/f798efc14a64e9abb7a336e8de283e5e.png&amp;FileName=file/f798efc14a64e9abb7a336e8de283e5e.png";
    private AnonymousData mAnonymousData;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_release_circle);
        bindView();
        bindData();
        mControl = new Control((RSoftInputLayout) findViewById(R.id.soft_input_layout),
                this);

        mAtManager = new WorkWorldAtManager(this, CurrentPreference.getInstance().getPreferenceUserId());
        mAtManager.setTextChangeListener(this);

        shareIntent(getIntent());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void shareIntent(Intent shareIntent) {
        boolean fromShare = shareIntent.getBooleanExtra(Constants.BundleKey.IS_FROM_SHARE, false);
        boolean isMultiImg = shareIntent.getBooleanExtra(Constants.BundleKey.IS_TRANS_MULTI_IMG, false);
        if (fromShare) {
            if (shareIntent.getExtras() != null && shareIntent.getBooleanExtra(ShareReceiver.SHARE_TAG, false)) {
                String content = shareIntent.getStringExtra(ShareReceiver.SHARE_TEXT);
                ArrayList<String> icons = shareIntent.getStringArrayListExtra(ShareReceiver.SHARE_IMG);
                ArrayList<String> videos = shareIntent.getStringArrayListExtra(ShareReceiver.SHARE_VIDEO);
                ArrayList<String> files = shareIntent.getStringArrayListExtra(ShareReceiver.SHARE_FILE);
                if (!TextUtils.isEmpty(content)) {
                    Logger.i("分享的文字:" + content);
                    release_text.setText(content);
//                    edit_msg.setText(content);
//                    chatingPresenter.sendMsg();
//                    edit_msg.setText("");
                }
                if (!ListUtil.isEmpty(icons)) {


//                    ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
//                    if (images.size() > 0) {
                    picList.remove(picList.size() - 1);
//                        for (ImageItem image : images) {
//                            picList.add(image);
//                        }
//                        if (picList.size() < 9) {
//                            ReleaseCircleNoChangeItemDate no = new ReleaseCircleNoChangeItemDate();
//                            no.setImgUrl(plusImg);
//                            picList.add(no);
//                        }

//                    }
                    mRecyclerView.setVisibility(View.VISIBLE);
                    re_video_ll.setVisibility(View.GONE);

                    for (String img : icons) {
                        ImageItem i = new ImageItem();
                        if (!new File(img).exists()) {
                            Toast.makeText(this, "分享失败", Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                        }
                        i.path = img;
                        picList.add(i);
                        Logger.i("分享的图片:" + img);
//                        imageUrl = img;
//                        chatingPresenter.sendImage();
//                        imageUrl = "";
                    }
                    releaseCircleGridAdapter.setNewData(picList);
                    releaseType = imageType;
                }
                if (!ListUtil.isEmpty(videos)) {
                    for (String video : videos) {
                        if (!new File(video).exists()) {
                            Toast.makeText(this, "分享失败", Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                        }
                        Photo photo = PhotoUtil.getPhoto(video);
                        mRecyclerView.setVisibility(View.GONE);
                        re_video_ll.setVisibility(View.VISIBLE);
                        List<Photo> list = new ArrayList<>();
                        list.add(photo);
                        parseVideoResult((ArrayList<Photo>) list);

                        Logger.i("分享的视频:" + video);
                        break;
//                        chatingPresenter.sendVideo(video);
                    }
                }
                if (!ListUtil.isEmpty(files)) {
                    for (String file : files) {
                        Logger.i("分享的文件:" + file);
//                        chatingPresenter.sendFile(file);
                    }
                }
            } else if (shareIntent.getExtras() != null && shareIntent.getExtras().containsKey(ShareReceiver.SHARE_EXTRA_KEY)) {
                try {
                    String jsonStr = shareIntent.getExtras().getString(ShareReceiver.SHARE_EXTRA_KEY);
                    entity = JsonUtils.getGson().fromJson(jsonStr, ExtendMessageEntity.class);

                    Logger.i("分享的json:" + jsonStr);
                    mRecyclerView.setVisibility(View.GONE);
                    re_link_ll.setVisibility(View.VISIBLE);
                    String title = entity.title;
                    if (TextUtils.isEmpty(title)) {
                        if (TextUtils.isEmpty(entity.desc)) {
                            title = "分享链接";
                            entity.title = "分享链接";
                        } else {
                            title = entity.desc;
                            entity.title = entity.desc;
                        }

                    } else {
                        title = entity.title;
                    }

                    re_link_title.setText(title);
                    releaseType = linkType;
                    int size = Utils.dp2px(this, 40);
                    if (TextUtils.isEmpty(entity.img)) {
                        re_link_icon.setBackground(getDrawable(R.drawable.atom_ui_link_default));
                    } else {
                        ProfileUtils.displayLinkImgByImageSrc(this, entity.img, getDrawable(R.drawable.atom_ui_link_default), (ImageView) re_link_icon,
                                size, size);
                    }

//                    chatingPresenter.sendExtendMessage(entity);
                } catch (Exception ex) {
//                    LogUtil.e(TAG, "ERROR", ex);
                }
            }
//            Toast.makeText(PbChatActivity.this, R.string.atom_ui_notice_already_share, Toast.LENGTH_SHORT).show();
            shareIntent.putExtra(Constants.BundleKey.IS_FROM_SHARE, false);//分享后false，防止重复发
        }
    }

    private void bindView() {
        qtNewActionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(qtNewActionBar);
        an_header = (SimpleDraweeView) findViewById(R.id.an_header);
        release_text = (EditText) findViewById(R.id.release_text);
        mRecyclerView = (RecyclerView) findViewById(R.id.collection_rv);
        release_identity = (TextView) findViewById(R.id.release_identity);
        release_identity_layout = (LinearLayout) findViewById(R.id.release_identity_layout);
        release_at_layout = (IconView) findViewById(R.id.release_at_layout);
        re_link_ll = (LinearLayout) findViewById(R.id.re_link_ll);
        re_link_icon = (SimpleDraweeView) findViewById(R.id.re_link_icon);
        re_link_title = (TextView) findViewById(R.id.re_link_title);
        release_number_words = (TextView) findViewById(R.id.release_number_words);
        release_image_layout = (IconView) findViewById(R.id.release_image_layout);
        release_video_layout = (IconView) findViewById(R.id.release_video_layout);
        re_video_ll = (RelativeLayout) findViewById(R.id.re_video_ll);
        re_video_image = (SimpleDraweeView) findViewById(R.id.re_video_image);
        clear_video = (ImageView) findViewById(R.id.clear_video);
        re_video_time = (TextView) findViewById(R.id.re_video_time);
    }


    private void initReleaseHeader() {
        String user = IMUserDefaults.getStandardUserDefaults().getStringValue(CommonConfig.globalContext, Constants.Preferences.lastuserid);
        String domain = QtalkNavicationService.getInstance().getXmppdomain();
        if (TextUtils.isEmpty(user) || TextUtils.isEmpty(domain)) {
            return;
        }
        final String lastid = user + "@" + domain;
        ConnectionUtil.getInstance().getUserCard(lastid, new IMLogicManager.NickCallBack() {
            @Override
            public void onNickCallBack(Nick nick) {


                ProfileUtils.displayGravatarByImageSrc(WorkWorldReleaseCircleActivity.this, nick.getHeaderSrc(), an_header,
                        getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size), getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
            }
        }, false, true);
    }

    private void bindData() {
        initReleaseHeader();

        //初始化自身头像
//        CurrentPreference.getInstance().get

        releaseCirclePresenter = new ReleaseCircleManagerPresenter(this);
        releaseCirclePresenter.setView(this);

        mImageSize = Utils.getImageItemWidth(this);
        setActionBarTitle(getString(R.string.atom_ui_share_moments));
        setActionBarRightText(getString(R.string.atom_ui_post));
        setActionBarRightTextClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releaseCirclePresenter.release();
            }
        });
        setActionBarLeftText(getString(R.string.atom_ui_cancel));
        setActionBarLeftClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initPicList();

        //清空video数据 隐藏界面
        clear_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releaseType = -1;
                videoData = null;
                re_video_ll.setVisibility(View.GONE);
            }
        });


        release_image_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                ImagePicker.getInstance().setSelectLimit();
//                Intent intent1 = new Intent(WorkWorldReleaseCircleActivity.this, ImageGridActivity.class);
                /* 如果需要进入选择的时候显示已经选中的图片，
                 * 详情请查看ImagePickerActivity
                 * */
//                                intent1.putExtra(ImageGridActivity.EXTRAS_IMAGES,images);
//                startActivityForResult(intent1, ACTIVITY_SELECT_PHOTO_Release);
                if (releaseType == imageType) {

                    int size = 0;
                    if (picList.get((picList.size() - 1)) instanceof ReleaseCircleNoChangeItemDate) {
                        size = (imgSize - picList.size() + 1);
                    } else {
                        size = (imgSize - picList.size());
                    }

                    if (size > 0) {
                        ImageSelectUtil.startSelectWorkWorld(WorkWorldReleaseCircleActivity.this, size, false, new SelectCallback() {
                            @Override
                            public void onResult(ArrayList<Photo> photos, ArrayList<String> paths, boolean isOriginal) {
                                parseImageResult(photos);

                            }
                        });
                    } else {
                        Toast.makeText(WorkWorldReleaseCircleActivity.this, "最多只可选择9张图片", Toast.LENGTH_LONG).show();
                    }
                } else if (releaseType == videoType) {
                    if(videoData!=null){
                        Toast.makeText(WorkWorldReleaseCircleActivity.this, "最多只可选择1个视频", Toast.LENGTH_LONG).show();
                    }

                } else if (releaseType == linkType) {
                    Toast.makeText(WorkWorldReleaseCircleActivity.this, "分享连接时,不可添加图片/视频", Toast.LENGTH_LONG).show();
                } else {
                    ImageSelectUtil.startSelectWorkWorld(WorkWorldReleaseCircleActivity.this, (imgSize - picList.size() + 1), true, new SelectCallback() {
                        @Override
                        public void onResult(final ArrayList<Photo> photos, ArrayList<String> paths, boolean isOriginal) {
                            if (photos.get(0).type.contains("image")) {
                                mRecyclerView.setVisibility(View.VISIBLE);
                                re_video_ll.setVisibility(View.GONE);
                                parseImageResult(photos);
                            } else if (photos.get(0).type.contains("video")) {

                                mRecyclerView.setVisibility(View.GONE);
                                re_video_ll.setVisibility(View.VISIBLE);
                                parseVideoResult(photos);
//                                ProfileUtils.displayGravatarByImageSrc(WorkWorldReleaseCircleActivity.this, photos.get(0).path, re_video_image,
//                                        getResources().getDimensionPixelSize(R.dimen.atom_ui_video_image), getResources().getDimensionPixelSize(R.dimen.atom_ui_video_image));
                            } else {
                                mRecyclerView.setVisibility(View.GONE);
                                re_video_ll.setVisibility(View.GONE);

                                Toast.makeText(WorkWorldReleaseCircleActivity.this, "所选项目,不可使用", Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                }

            }
        });

        release_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mAtManager.beforeTextChanged(s, start, count, after);
                //这一套操作的意思不明确 应该为在删除状态时,不让@界面出现
                if (count > 0 && after == 0) {
                    canShowAtActivity = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAtManager.onTextChanged(s, start, before, count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                mAtManager.afterTextChanged(s);
                release_number_words.setText(s.length() + "/1000");
                //根据当前是删除了字段或者新写字段来判断是否开启@界面
                if (s.length() > 1000) {
                    showToast("请输入不超过1000个字符的票圈");
                    check = false;
                } else {
                    check = true;
                }
                if (!canShowAtActivity) {
                    canShowAtActivity = true;
                    return;
                }



            }
        });


        releaseCircleGridAdapter = new ReleaseCircleGridAdapter(picList, this);
        final GridLayoutManager manager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(releaseCircleGridAdapter);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(3, Utils.dp2px(this, 2), false));
        releaseCircleGridAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (picList.get(position) instanceof ReleaseCircleNoChangeItemDate) {
                    ImagePicker.getInstance().setSelectLimit(imgSize - picList.size() + 1);
                    Intent intent1 = new Intent(WorkWorldReleaseCircleActivity.this, ImageGridActivity.class);
                    /* 如果需要进入选择的时候显示已经选中的图片，
                     * 详情请查看ImagePickerActivity
                     * */
//                                intent1.putExtra(ImageGridActivity.EXTRAS_IMAGES,images);
                    startActivityForResult(intent1, ACTIVITY_SELECT_PHOTO_Release);
                } else if (picList.get(position) instanceof ReleaseCircleImageItemDate) {

                    Intent intent = new Intent(WorkWorldReleaseCircleActivity.this, ImagePreviewActivity.class);
                    intent.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                    List<ImageItem> array = new ArrayList<>();
                    for (int i = 0; i < picList.size(); i++) {
                        if (picList.get(i) instanceof ImageItem) {
                            array.add((ImageItem) picList.get(i));
                        }
                    }


                    ImagePicker.getInstance().clearSelectedImages();
                    DataHolder.getInstance().save(DataHolder.DH_CURRENT_IMAGE_FOLDER_ITEMS, array);
                    intent.putExtra(ImagePreviewActivity.ISORIGIN, false);
                    intent.putExtra(ImagePreviewActivity.ISDELETE, true);
                    intent.putExtra(ImagePreviewActivity.ISEDIT, false);
                    ImagePicker.getInstance().setSelectLimit(array.size());
                    startActivityForResult(intent, REQUEST_CODE_PERVIEW_DELETE);

                }


            }
        });


        final List<String> list = new ArrayList<>();
        list.add("/storage/emulated/0/Huawei/MagazineUnlock/magazine-unlock-06-2.3.1208-_0872573221910358AD7E46C92D5B9C0B.jpg");
        list.add("/storage/emulated/0/Huawei/MagazineUnlock/magazine-unlock-06-2.3.1208-_0872573221910358AD7E46C92D5B9C0B.jpg");
        list.add("/storage/emulated/0/Huawei/MagazineUnlock/magazine-unlock-06-2.3.1208-_0872573221910358AD7E46C92D5B9C0B.jpg");
        list.add("/storage/emulated/0/Huawei/MagazineUnlock/magazine-unlock-06-2.3.1208-_0872573221910358AD7E46C92D5B9C0B.jpg");
        list.add("/storage/emulated/0/Huawei/MagazineUnlock/magazine-unlock-06-2.3.1208-_0872573221910358AD7E46C92D5B9C0B.jpg");
        list.add("/storage/emulated/0/Huawei/MagazineUnlock/magazine-unlock-06-2.3.1208-_0872573221910358AD7E46C92D5B9C0B.jpg");
        list.add("/storage/emulated/0/Huawei/MagazineUnlock/magazine-unlock-06-2.3.1208-_0872573221910358AD7E46C92D5B9C0B.jpg");
        list.add("/storage/emulated/0/Huawei/MagazineUnlock/magazine-unlock-06-2.3.1208-_0872573221910358AD7E46C92D5B9C0B.jpg");
        list.add("/storage/emulated/0/Huawei/MagazineUnlock/magazine-unlock-06-2.3.1208-_0872573221910358AD7E46C92D5B9C0B.jpg");
//        releaseCircleGridAdapter.setNewData(list);

        itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                com.orhanobut.logger.Logger.i("发布-flags");
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;//拖拽
                if (viewHolder.itemView.getTag() == "noChange") {
                    dragFlags = 0;
                }

                int swipeFlags = 0;//侧滑删除
                return makeMovementFlags(dragFlags, swipeFlags);

            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                com.orhanobut.logger.Logger.i("发布-moveA:" + viewHolder.getAdapterPosition() + " b:" + target.getAdapterPosition());
                //滑动事件
//                Collections.swap(picList, viewHolder.getAdapterPosition(), target.getAdapterPosition());
//               ReleaseCircleImageItemDate cache = (ReleaseCircleImageItemDate) picList.get(viewHolder.getAdapterPosition());
                picList.add(target.getAdapterPosition(), picList.remove(viewHolder.getAdapterPosition()));
//                viewHolder.itemView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mImageSize+5));
                releaseCircleGridAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return false;


            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//侧滑事件
                com.orhanobut.logger.Logger.i("发布-swiped");
                picList.remove(viewHolder.getAdapterPosition());
//                viewHolder.itemView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mImageSize+5));
                releaseCircleGridAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());

            }

            @Override
            public boolean isLongPressDragEnabled() {
                com.orhanobut.logger.Logger.i("发布-long");
                return true;
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
//                viewHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(mImageSize + 10, mImageSize + 10));
//                if(){
//                    return;
//                }
                if (viewHolder == null || viewHolder.getItemViewType() == TYPE_UNCLICKABLE) {
                    return;
                }
                if (picList.get(picList.size() - 1) instanceof ReleaseCircleNoChangeItemDate) {
                    releaseCircleGridAdapter.remove(picList.size() - 1);
                }

                super.onSelectedChanged(viewHolder, actionState);
                com.orhanobut.logger.Logger.i("发布-select");
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//                viewHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(mImageSize, mImageSize));
                if (picList.size() < 9) {
                    ReleaseCircleNoChangeItemDate data = new ReleaseCircleNoChangeItemDate();
                    data.setImgUrl(plusImg);
                    picList.add(data);
                }

                releaseCircleGridAdapter.notifyDataSetChanged();
                super.clearView(recyclerView, viewHolder);
                com.orhanobut.logger.Logger.i("发布-clear");
            }
        });

        itemTouchHelper.attachToRecyclerView(mRecyclerView);
//        mRecyclerView


        release_identity_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkWorldReleaseCircleActivity.this, IdentitySelectActivity.class);
                intent.putExtra(UUID_STR, releaseCirclePresenter.getUUID());
                intent.putExtra(now_identity_type, identityType);
                if (identityType == ANONYMOUS_NAME) {
                    intent.putExtra(ANONYMOUS_DATA, mAnonymousData);
                }
                startActivityForResult(intent, REQUEST_CODE_IDENTITY);
            }
        });

        release_at_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAtManager.startAtList(true);
//                Intent intent = new Intent(ReleaseCircleActivity.this,WorkWorldAtListActivity.class);
//                startActivityForResult(intent,REQUEST_CODE_AT);
            }
        });

//        AA


    }

    private void parseVideoResult(final ArrayList<Photo> photos) {
        videoData = photos.get(0);
        releaseType = videoType;
        re_video_time.setText(DurationUtils.format(videoData.duration));
        ImageSelectGlideEngine.getInstance().loadPhoto(WorkWorldReleaseCircleActivity.this, photos.get(0).path, re_video_image);
        re_video_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoPlayUtil.openLocalVideo(WorkWorldReleaseCircleActivity.this,photos.get(0).path,photos.get(0).name,photos.get(0).path);
            }
        });
    }

    private void parseImageResult(ArrayList<Photo> photos) {
        try {
            if (photos != null) {
                //新版图片选择器
//                                        ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (photos.size() > 0) {
                    picList.remove(picList.size() - 1);
                    for (Photo image : photos) {

                        picList.add(ImageSelectUtil.parseImageItemForPhotos(image));
                    }
                    if (picList.size() < 9) {
                        ReleaseCircleNoChangeItemDate no = new ReleaseCircleNoChangeItemDate();
                        no.setImgUrl(plusImg);
                        picList.add(no);
                    }
                    releaseType = imageType;
                    releaseCircleGridAdapter.setNewData(picList);
                }
            }
        } catch (Exception e) {

        }
    }

    private void initPicList() {
        picList = new ArrayList<>();

//        ReleaseCircleImageItemDate a = new ReleaseCircleImageItemDate();
//        ReleaseCircleImageItemDate b = new ReleaseCircleImageItemDate();
//        a.setImgUrl("/storage/emulated/0/Huawei/MagazineUnlock/magazine-unlock-06-2.3.1208-_0872573221910358AD7E46C92D5B9C0B.jpg");
//        b.setImgUrl("/storage/emulated/0/Huawei/MagazineUnlock/magazine-unlock-06-2.3.1208-_0872573221910358AD7E46C92D5B9C0B.jpg");
//        picList.add(a);
//        picList.add(b);
        ReleaseCircleNoChangeItemDate data = new ReleaseCircleNoChangeItemDate();
        data.setImgUrl(plusImg);
        picList.add(data);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case ACTIVITY_SELECT_PHOTO_Release:
                selectPhotoResult(data);
                break;
            case REQUEST_CODE_IDENTITY:
                updateIdentity(data);
                break;
            case REQUEST_CODE_PERVIEW_DELETE:
                selectPhotoDelete(data);
                break;
            case AT_MEMBER:
                release_text.requestFocus();
                mAtManager.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void updateIdentity(Intent data) {
        int i = data.getIntExtra(EXTRA_IDENTITY, 0);
        switch (i) {
            case REAL_NAME:
                identityType = REAL_NAME;
                release_identity.setText(getString(R.string.atom_ui_use_my_real_name));
                initReleaseHeader();
                break;

            case ANONYMOUS_NAME:
                identityType = ANONYMOUS_NAME;
                release_identity.setText(getString(R.string.atom_ui_anonymous));
                mAnonymousData = (AnonymousData) data.getSerializableExtra(ANONYMOUS_DATA);
                setAnonymousData(mAnonymousData);
//                releaseCirclePresenter.getAnonymous();
                break;
        }
    }

    public void selectPhotoDelete(Intent data) {
        try {
            if (data != null) {
                //新版图片选择器
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images.size() > 0) {
                    boolean isPlus = false;
                    for (int i = 0; i < images.size(); i++) {
                        for (int j = 0; j < picList.size(); j++) {
                            if (picList.get(j) instanceof ReleaseCircleImageItemDate) {
                                if (images.get(i).path.equals(((ImageItem) picList.get(j)).path)) {
                                    picList.remove(j);
                                    break;
                                }
                            }

                        }
                    }

                    if (picList.size() < 9) {
                        if (!(picList.get(picList.size() - 1) instanceof ReleaseCircleNoChangeItemDate)) {
                            ReleaseCircleNoChangeItemDate no = new ReleaseCircleNoChangeItemDate();
                            no.setImgUrl(plusImg);
                            picList.add(no);
                        }
                    }
                    if (picList.size() == 1 && picList.get(0) instanceof ReleaseCircleNoChangeItemDate) {
                        mRecyclerView.setVisibility(View.GONE);
                        releaseType = -1;
                    }
                    releaseCircleGridAdapter.setNewData(picList);
                }
            }
        } catch (Exception e) {
        }
    }

    public void selectPhotoResult(Intent data) {
        try {
            if (data != null) {
                //新版图片选择器
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images.size() > 0) {
                    picList.remove(picList.size() - 1);
                    for (ImageItem image : images) {
                        picList.add(image);
                    }
                    if (picList.size() < 9) {
                        ReleaseCircleNoChangeItemDate no = new ReleaseCircleNoChangeItemDate();
                        no.setImgUrl(plusImg);
                        picList.add(no);
                    }
                    releaseCircleGridAdapter.setNewData(picList);
                }
            }
        } catch (Exception e) {
        }
    }


    @Override
    public void closeActivitvAndResult(ArrayList<WorkWorldItem> list) {
        if (list == null) {
//            showToast("发布失败");
        }
        Intent intent = new Intent();
//        Collections.reverse(list);
        intent.putExtra(WorkWorldActivity.WORK_WORLD_RESULT_DATA, list);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public List<MultiItemEntity> getUpdateImageList() {
        try {
//            if (picList.get(picList.size() - 1) instanceof ReleaseCircleNoChangeItemDate) {
//                picList.remove(picList.size() - 1);
//            }
            return picList;
        } catch (Exception e) {
            return new ArrayList<>();
        }

    }

    @Override
    public Photo getUpdateVideo() {
        return videoData;
    }

    @Override
    public String getContent() {
        return release_text.getText().toString().trim();
    }

    @Override
    public void showProgress() {
        if (dialog == null) {
            dialog = new ProgressDialog(this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置水平进度条
            dialog.setCancelable(false);// 设置是否可以通过点击Back键取消
            dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
            dialog.setTitle("正在发布");
        }
        dialog.show();
    }

    @Override
    public void dismissProgress() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public int getIdentityType() {
        return identityType;
    }

    @Override
    public AnonymousData getAnonymousData() {
        return mAnonymousData;
    }

    @Override
    public void setAnonymousData(AnonymousData anonymousData) {

        ProfileUtils.displayGravatarByImageSrc(WorkWorldReleaseCircleActivity.this, anonymousData.getData().getAnonymousPhoto(), an_header,
                getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size), getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
    }

    @Override
    public boolean isCheck() {
        return check;
    }

    @Override
    public ExtendMessageEntity getEntity() {
        return entity;
    }

    @Override
    public Map<String, String> getAtList() {
        return mAtManager.getAtBlocks();
    }

    @Override
    public void showToast(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(WorkWorldReleaseCircleActivity.this, str, Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onBackPressed() {
        if (mControl.onBackPressed()) {
            super.onBackPressed();
        }
    }


    @Override
    public void onTextAdd(String content, int stat, int length) {

        Editable edit = release_text.getEditableText();
        edit.insert(stat, content);
    }

    @Override
    public void onTextDelete(int start, int length) {
        Editable edit = release_text.getEditableText();
        edit.delete(start, start + length);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);  //设置横屏
            Logger.i("发布页横评");
//            Toast.makeText(WorkWorldReleaseCircleActivity.this,"当前屏幕为横屏",Toast.LENGTH_SHORT).show();
        } else {
            Logger.i("发布页竖评");
//            Toast.makeText(WorkWorldReleaseCircleActivity.this, "当前屏幕为竖屏", Toast.LENGTH_SHORT).show();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//设置竖屏

        }
        super.onConfigurationChanged(newConfig);

    }
}
