package com.qunar.im.ui.activity;

import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.orhanobut.logger.Logger;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.base.structs.ImageFloder;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.base.util.ListUtil;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.CommonViewHolder;
import com.qunar.im.ui.adapter.PictureSelectorAdapter;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.popMemuOfDirSel.ListImageDirPopupWindow;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by xinbo.wang on 2015/5/22.
 */
public class PictureSelectorActivity extends IMBaseActivity implements ListImageDirPopupWindow.OnImageDirSelected, IMNotificaitonCenter.NotificationCenterDelegate {
    public final static String KEY_SELECTED_PIC = "sel_pics";
    public static final String TYPE = "type";
    public static final String TYPE_EMOJICON = "emojicon";
    private int MAX_SELECT = 9;
    public static final String SHOW_EDITOR = "showEditor";

    GridView imageContainer;
    TextView dir_selector, image_count;
    RelativeLayout selpic_bottom_container;

    boolean isMultiSel = true, isGravantarSel = false;

    //图片数量
    private int picCount;
    private int totalCount;
    //图片最多文件夹
    private File defaultDir;
    //搜索到的图片
    private final List<String> mAllImages = new LinkedList<>();

    private PictureSelectorAdapter adapter;

    ProgressDialog progressDialog;

    /**
     * 临时的辅助类，用于防止同一个文件夹的多次扫描
     */
    private HashSet<String> mDirPaths = new HashSet<String>();

    /**
     * 扫描拿到所有的图片文件夹
     */
    private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();

    private int mScreenHeight;

    private ListImageDirPopupWindow mListImageDirPopupWindow;
    private boolean showEditor;

    /**
     * 为View绑定数据
     */
    private void data2View() {
        if (defaultDir == null) {
            Toast.makeText(getApplicationContext(), R.string.atom_ui_tip_not_find_picture,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        adapter = new PictureSelectorAdapter(this, mAllImages,
                R.layout.atom_ui_item_mul_pic_sel, showEditor);
        adapter.setMaxSelect(MAX_SELECT);
        adapter.setMultiSelector(isMultiSel);
        if (isMultiSel) {
            adapter.setImageClickHandler(new PictureSelectorAdapter.ImageClickHandler() {
                @Override
                public void imageClickEvent(String selectFilePath) {
                    if (adapter.getSelectedCount() == 0) {
                        mNewActionBar.getRightText().setEnabled(false);
                        mNewActionBar.getRightText().setText(getString(R.string.atom_ui_common_confirm)+"(0/9)");
                    } else {
                        mNewActionBar.getRightText().setEnabled(true);
                        mNewActionBar.getRightText().setText(getString(R.string.atom_ui_common_confirm) + "(" + String.valueOf(adapter.getSelectedCount()) + "/9)");
                    }
                }
            });
        } else {
            setActionBarRightText(0);
            adapter.setImageClickHandler(new PictureSelectorAdapter.ImageClickHandler() {
                @Override
                public void imageClickEvent(String selectFilePath) {
                    Intent resultIntent = new Intent();
                    ArrayList<String> list = new ArrayList<String>(1);
                    list.add(selectFilePath);
                    resultIntent.putStringArrayListExtra(KEY_SELECTED_PIC, list);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            });
        }
        if (isGravantarSel) {
            setActionBarRightText(0);
            adapter.setImageClickHandler(new PictureSelectorAdapter.ImageClickHandler() {
                @Override
                public void imageClickEvent(String selectFilePath) {
                    Intent intent = new Intent(PictureSelectorActivity.this,
                            ImageClipActivity.class);
                    intent.putExtra(ImageClipActivity.KEY_SEL_BITMAP, selectFilePath);
                    intent.putExtra(ImageClipActivity.KEY_CLIP_ENABLE, true);
                    startActivity(intent);
                }
            });
        }
        imageContainer.setAdapter(adapter);
        imageContainer.setRecyclerListener(new AbsListView.RecyclerListener() {
            @Override
            public void onMovedToScrapHeap(View view) {
                CommonViewHolder holder = (CommonViewHolder) view.getTag();
                if (holder != null) {
                    SimpleDraweeView img = holder.getView(R.id.show_local_image);
                    if (img != null) {
                        if (img.getController() != null) {
                            img.getController().onDetach();
                        }
                        img.setImageDrawable(null);
                        Logger.i("release cache");
                    }
                }

            }
        });
        image_count.setText(totalCount + (String) getText(R.string.atom_ui_common_piece));
    }

    /**
     * 初始化展示文件夹的popupWindw
     */
    private void initListDirPopupWindw() {
        mListImageDirPopupWindow = new ListImageDirPopupWindow(
                ViewGroup.LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
                mImageFloders, LayoutInflater.from(PictureSelectorActivity.this)
                .inflate(R.layout.atom_ui_layout_popmenu_list_dir, null));

        mListImageDirPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        // 设置选择文件夹的回调
        mListImageDirPopupWindow.setOnImageDirSelected(this);
    }


    private ConnectionUtil connectionUtil;

    private void addEvent() {
        connectionUtil.addEvent(this, QtalkEvent.SEND_PHOTO_AFTER_EDIT);
    }

    private void removeEvent() {
        connectionUtil.removeEvent(this, QtalkEvent.SEND_PHOTO_AFTER_EDIT);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_mutil_pic_selector);
        connectionUtil = ConnectionUtil.getInstance();
        addEvent();
        injectExtras();
        bindViews();
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        mScreenHeight = outMetrics.heightPixels;
        EventBus.getDefault().register(this);
        initViews();
    }

    private void bindViews() {
        selpic_bottom_container = (RelativeLayout) findViewById(R.id.selpic_bottom_container);
        dir_selector = (TextView) findViewById(R.id.dir_selector);
        image_count = (TextView) findViewById(R.id.image_count);
        imageContainer = (GridView) findViewById(R.id.show_pic_region);
    }

    protected void injectExtras() {
        Bundle extras_ = getIntent().getExtras();
        if (extras_ != null) {
            if (extras_.containsKey("isMultiSel")) {
                isMultiSel = extras_.getBoolean("isMultiSel");
            }
            if (extras_.containsKey("isGravantarSel")) {
                isGravantarSel = extras_.getBoolean("isGravantarSel");
            }
            if (extras_.containsKey(SHOW_EDITOR)) {
                showEditor = extras_.getBoolean(SHOW_EDITOR);
            }
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        removeEvent();
        if (adapter != null) {
            adapter.releaseDraweeView();
        }
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    void initViews() {
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        if (TYPE_EMOJICON.equals(getIntent().getStringExtra(TYPE))) {
            setActionBarTitle(R.string.atom_ui_title_choose_emojicon);

            MAX_SELECT = 9;
        } else {
            setActionBarTitle(R.string.atom_ui_title_choose_picture);
        }
        if (isMultiSel) {
            setActionBarRightText(getText(R.string.atom_ui_common_confirm) + "(0/9)");
            mNewActionBar.getRightText().setBackgroundResource(R.drawable.atom_ui_bg_chat_time);
            setActionBarRightTextClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (adapter != null && adapter.getSelectedCount() > 0) {
                        Intent resultIntent = new Intent();
                        resultIntent.putStringArrayListExtra(KEY_SELECTED_PIC, (ArrayList<String>) adapter.getSelectedImages());
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                }
            });

        }
        getImages();
        initEvent();
    }

    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
     */
    private void getImages() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, R.string.atom_ui_tip_no_external_storage, Toast.LENGTH_SHORT).show();
            return;
        }
        // 显示进度条

        progressDialog = ProgressDialog.show(this, null, getText(R.string.atom_ui_tip_image_loading));
        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media._ID};
        final String orderBy = MediaStore.Images.Media._ID + " desc";
        final String selector = MediaStore.Images.Media.SIZE + ">?";
        final String[] selectionArgs = {"0"};
        final CursorLoader loader = new CursorLoader(this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                columns, selector, selectionArgs, orderBy);

        loader.registerListener(0x0110, new Loader.OnLoadCompleteListener<Cursor>() {
            @Override
            public void onLoadComplete(Loader<Cursor> loader, Cursor data) {
                data.close();
                progressDialog.dismiss();
                // 为View绑定数据
                data2View();
                // 初始化展示文件夹的popupWindw
                initListDirPopupWindw();
            }
        });

        BackgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final Cursor mCursor = loader.loadInBackground();

                LogUtil.e("TAG", mCursor.getCount() + "");
                while (mCursor.moveToNext()) {
                    // 获取图片的路径
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));
                    String type = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
                    if ("image/webp".equals(type)) {
                        continue;
                    }
                    LogUtil.e("TAG", path);
                    mAllImages.add(path);

                    // 获取该图片的父路径名
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null)
                        continue;
                    String dirPath = parentFile.getAbsolutePath();
                    ImageFloder imageFloder = null;
                    // 利用一个HashSet防止多次扫描同一个文件夹
                    if (mDirPaths.contains(dirPath)) {
                        continue;
                    } else {
                        mDirPaths.add(dirPath);
                        // 初始化imageFloder
                        imageFloder = new ImageFloder();
                        imageFloder.setDir(dirPath);
                        imageFloder.setFirstImagePath(path);
                    }
                    String[] tempList = parentFile.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            String lowFileName = filename.toLowerCase();
                            return lowFileName.endsWith(".jpg")
                                    || lowFileName.endsWith(".png")
                                    || lowFileName.endsWith(".jpeg")
                                    || lowFileName.endsWith(".gif");
                        }
                    });
                    int picSize = 0;
                    if (tempList != null)
                        picSize = tempList.length;

                    totalCount += picSize;

                    imageFloder.setCount(picSize);
                    mImageFloders.add(imageFloder);

                    if (picSize > picCount) {
                        picCount = picSize;
                        defaultDir = parentFile;
                    }
                }

                // 扫描完成，辅助的HashSet也就可以释放内存了
                mDirPaths = null;

                // 通知Handler扫描图片完成
                getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        loader.startLoading();
                        loader.deliverResult(mCursor);
                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    private void initEvent() {
        /**
         * 为底部的布局设置点击事件，弹出popupWindow
         */
        selpic_bottom_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListImageDirPopupWindow
                        .setAnimationStyle(R.style.atom_ui_anim_popup_dir);
                mListImageDirPopupWindow.showAsDropDown(selpic_bottom_container, 0, 0);

                // 设置背景颜色变暗
//                WindowManager.LayoutParams lp = getWindow().getAttributes();
//                lp.alpha = .3f;
//                getWindow().setAttributes(lp);
            }
        });
    }

    @Override
    public void selected(ImageFloder floder) {

        defaultDir = new File(floder.getDir());
        List dirImages = ListUtil.filter(mAllImages, new ListUtil.ListFilter<String>() {
            @Override
            public boolean accept(String source) {
                if (source.startsWith(defaultDir.getAbsolutePath()) && source.lastIndexOf("/") == defaultDir.getAbsolutePath().length()) {
                    return true;
                }
                return false;
            }
        });

        adapter.chageDirAndDatas(dirImages);
        adapter.notifyDataSetChanged();
        image_count.setText(floder.getCount() + (String) getText(R.string.atom_ui_common_piece));
        dir_selector.setText(floder.getName());
        mListImageDirPopupWindow.dismiss();

    }

    public void onEventMainThread(EventBusEvent.GravanterSelected selected) {
        this.finish();
    }

    public void onEventMainThread(EventBusEvent.NewPictureEdit edit) {
        this.finish();
    }

    @Override
    public void didReceivedNotification(String key, Object... args) {
        switch (key) {
            case QtalkEvent.SEND_PHOTO_AFTER_EDIT:
                this.finish();
                break;
        }
    }
}