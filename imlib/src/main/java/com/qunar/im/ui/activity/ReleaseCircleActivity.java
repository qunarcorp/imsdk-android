package com.qunar.im.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.base.module.AnonymousData;
import com.qunar.im.base.module.MultiItemEntity;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.module.ReleaseCircleImageItemDate;
import com.qunar.im.base.module.ReleaseCircleNoChangeItemDate;
import com.qunar.im.base.module.WorkWorldItem;
import com.qunar.im.base.presenter.ReleaseCirclePresenter;
import com.qunar.im.base.presenter.impl.ReleaseCircleManagerPresenter;
import com.qunar.im.base.presenter.views.ReleaseCircleView;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.IMUserDefaults;
import com.qunar.im.base.util.ProfileUtils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.ReleaseCircleGridAdapter;
import com.qunar.im.ui.imagepicker.DataHolder;
import com.qunar.im.ui.imagepicker.ImagePicker;
import com.qunar.im.base.module.ImageItem;
import com.qunar.im.ui.imagepicker.ui.ImageGridActivity;
import com.qunar.im.ui.imagepicker.ui.ImagePreviewActivity;
import com.qunar.im.ui.imagepicker.util.Utils;
import com.qunar.im.ui.imagepicker.view.GridSpacingItemDecoration;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.recyclerview.BaseQuickAdapter;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;
import com.qunar.im.utils.ConnectionUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.qunar.im.base.module.ReleaseCircleType.TYPE_UNCLICKABLE;
import static com.qunar.im.base.presenter.impl.ReleaseCircleManagerPresenter.ANONYMOUS_NAME;
import static com.qunar.im.base.presenter.impl.ReleaseCircleManagerPresenter.REAL_NAME;
import static com.qunar.im.ui.activity.IdentitySelectActivity.ANONYMOUS_DATA;
import static com.qunar.im.ui.activity.IdentitySelectActivity.now_identity_type;

public class ReleaseCircleActivity extends SwipeBackActivity implements ReleaseCircleView {

    public static final int ACTIVITY_SELECT_PHOTO_Release = 1;
    public static final int REQUEST_CODE_PREVIEW_Release = 101;
    public static final int REQUEST_CODE_PERVIEW_DELETE = 102;
    public static final int REQUEST_CODE_IDENTITY = 103;


    public static final String EXTRA_IDENTITY = "identity_type";

    public static final String UUID_STR = "UUID_STR";

    protected QtNewActionBar qtNewActionBar;//头部导航
    protected EditText release_text;//朋友圈文字内容
    protected RecyclerView mRecyclerView;//图片排版
    protected ReleaseCircleGridAdapter releaseCircleGridAdapter;//图片排版用
    protected ItemTouchHelper itemTouchHelper;
    protected List<MultiItemEntity> picList;
    protected RelativeLayout release_identity_layout;
    protected TextView release_identity;
    protected SimpleDraweeView an_header;
    private int imgSize = 9;
    private int mImageSize;
    private ProgressDialog dialog;

    private boolean check = true;

    private ReleaseCirclePresenter releaseCirclePresenter;
    private String uuid;
    private String anonymous;
    private String anonymousPhoto;
    private int identityType = 0;
    private String plusImg = "https://qt.qunar.com/file/v2/download/temp/new/f798efc14a64e9abb7a336e8de283e5e.png?name=f798efc14a64e9abb7a336e8de283e5e.png&amp;file=file/f798efc14a64e9abb7a336e8de283e5e.png&amp;FileName=file/f798efc14a64e9abb7a336e8de283e5e.png";
    private AnonymousData mAnonymousData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_release_circle);
        bindView();
        bindData();
    }

    private void bindView() {
        qtNewActionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(qtNewActionBar);
        an_header = (SimpleDraweeView) findViewById(R.id.an_header);
        release_text = (EditText) findViewById(R.id.release_text);
        mRecyclerView = (RecyclerView) findViewById(R.id.collection_rv);
        release_identity = (TextView) findViewById(R.id.release_identity);
        release_identity_layout = (RelativeLayout) findViewById(R.id.release_identity_layout);
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


                ProfileUtils.displayGravatarByImageSrc(ReleaseCircleActivity.this, nick.getHeaderSrc(), an_header,
                        getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size), getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
            }
        }, false, true);
    }

    private void bindData() {
        initReleaseHeader();

        //初始化自身头像
//        CurrentPreference.getInstance().get

        releaseCirclePresenter = new ReleaseCircleManagerPresenter();
        releaseCirclePresenter.setView(this);

        mImageSize = Utils.getImageItemWidth(this);
        setActionBarTitle("发布动态");
        setActionBarRightText("发布");
        setActionBarRightTextClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releaseCirclePresenter.release();
            }
        });
        setActionBarLeftText("取消");
        setActionBarLeftClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initPicList();

        release_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 500) {
                    showToast("请输入不超过500个字符的票圈");
                    check = false;
                } else {
                    check = true;
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
                    Intent intent1 = new Intent(ReleaseCircleActivity.this, ImageGridActivity.class);
                    /* 如果需要进入选择的时候显示已经选中的图片，
                     * 详情请查看ImagePickerActivity
                     * */
//                                intent1.putExtra(ImageGridActivity.EXTRAS_IMAGES,images);
                    startActivityForResult(intent1, ACTIVITY_SELECT_PHOTO_Release);
                } else if (picList.get(position) instanceof ReleaseCircleImageItemDate) {

                    Intent intent = new Intent(ReleaseCircleActivity.this, ImagePreviewActivity.class);
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
                Intent intent = new Intent(ReleaseCircleActivity.this, IdentitySelectActivity.class);
                intent.putExtra(UUID_STR, releaseCirclePresenter.getUUID());
                intent.putExtra(now_identity_type, identityType);
                startActivityForResult(intent, REQUEST_CODE_IDENTITY);
            }
        });
//        AA


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
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void updateIdentity(Intent data) {
        int i = data.getIntExtra(EXTRA_IDENTITY, 0);
        switch (i) {
            case REAL_NAME:
                identityType = REAL_NAME;
                release_identity.setText("实名发布");
                initReleaseHeader();
                break;

            case ANONYMOUS_NAME:
                identityType = ANONYMOUS_NAME;
                release_identity.setText("匿名发布");
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
    public String getContent() {
        return release_text.getText().toString();
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

        ProfileUtils.displayGravatarByImageSrc(ReleaseCircleActivity.this, anonymousData.getData().getAnonymousPhoto(), an_header,
                getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size), getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
    }

    @Override
    public boolean isCheck() {
        return check;
    }

    @Override
    public void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();

    }
}
