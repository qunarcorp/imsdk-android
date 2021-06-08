package com.qunar.im.ui.util.easyphoto.easyphotos.ui;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qunar.im.ui.R;
import com.qunar.im.ui.util.easyphoto.easyphotos.EasyPhotos;
import com.qunar.im.ui.util.easyphoto.easyphotos.constant.Capture;
import com.qunar.im.ui.util.easyphoto.easyphotos.constant.Code;
import com.qunar.im.ui.util.easyphoto.easyphotos.constant.Key;
import com.qunar.im.ui.util.easyphoto.easyphotos.constant.Type;
import com.qunar.im.ui.util.easyphoto.easyphotos.models.ad.AdListener;
import com.qunar.im.ui.util.easyphoto.easyphotos.models.album.AlbumModel;
import com.qunar.im.ui.util.easyphoto.easyphotos.models.album.entity.Photo;
import com.qunar.im.ui.util.easyphoto.easyphotos.result.Result;
import com.qunar.im.ui.util.easyphoto.easyphotos.setting.Setting;
import com.qunar.im.ui.util.easyphoto.easyphotos.ui.adapter.AlbumItemsAdapter;
import com.qunar.im.ui.util.easyphoto.easyphotos.ui.adapter.PhotosAdapter;
import com.qunar.im.ui.util.easyphoto.easyphotos.ui.widget.PressedTextView;
import com.qunar.im.ui.util.easyphoto.easyphotos.utils.color.ColorUtils;
import com.qunar.im.ui.util.easyphoto.easyphotos.utils.media.DurationUtils;
import com.qunar.im.ui.util.easyphoto.easyphotos.utils.media.MediaScannerConnectionUtils;
import com.qunar.im.ui.util.easyphoto.easyphotos.utils.permission.PermissionUtil;
import com.qunar.im.ui.util.easyphoto.easyphotos.utils.settings.SettingsUtils;
import com.qunar.im.ui.util.easyphoto.easyphotos.utils.system.SystemUtils;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EasyPhotosActivity extends AppCompatActivity implements AlbumItemsAdapter.OnClickListener, PhotosAdapter.OnClickListener, AdListener, View.OnClickListener {

    private AlbumModel albumModel;
    private ArrayList<Object> photoList = new ArrayList<>();
    private ArrayList<Object> albumItemList = new ArrayList<>();

    private ArrayList<Photo> resultList = new ArrayList<>();

    private RecyclerView rvPhotos;
    private PhotosAdapter photosAdapter;
    private GridLayoutManager gridLayoutManager;

    private RecyclerView rvAlbumItems;
    private AlbumItemsAdapter albumItemsAdapter;
    private RelativeLayout rootViewAlbumItems;

    private PressedTextView tvAlbumItems, tvDone, tvPreview;
    private TextView tvOriginal;
    private AnimatorSet setHide;
    private AnimatorSet setShow;

    private int currAlbumItemIndex = 0;

    private ImageView ivCamera;

    private LinearLayout mSecondMenus;

    private RelativeLayout permissionView;
    private TextView tvPermission;
    private View mBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy_photos);
        hideActionBar();
        adaptationStatusBar();
        if (!Setting.onlyStartCamera && null == Setting.imageEngine) {
            finish();
            return;
        }
        initSomeViews();
        if (PermissionUtil.checkAndRequestPermissionsInActivity(this, getNeedPermissions())) {
            Setting.fileProviderAuthority = getPackageName() + ".provider";
            hasPermissions();
        } else {
            permissionView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Setting.clear();
    }

    public static void start(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, EasyPhotosActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void start(Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), EasyPhotosActivity.class);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void start(androidx.fragment.app.Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getContext(), EasyPhotosActivity.class);
        fragment.startActivityForResult(intent, requestCode);
    }

    private void adaptationStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int statusColor = getWindow().getStatusBarColor();
            if (statusColor == Color.TRANSPARENT) {
                statusColor = ContextCompat.getColor(this, R.color.colorPrimaryDark);
            }
            if (ColorUtils.isWhiteColor(statusColor)) {
                SystemUtils.getInstance().setStatusDark(this, true);
            }
        }
    }

    private void initSomeViews() {
        mBottomBar = findViewById(R.id.m_bottom_bar);
        permissionView = findViewById(R.id.rl_permissions_view);
        tvPermission = findViewById(R.id.tv_permission);
        rootViewAlbumItems = findViewById(R.id.root_view_album_items);
        findViewById(R.id.iv_second_menu).setVisibility(Setting.showPuzzleMenu || Setting.showCleanMenu || Setting.showOriginalMenu ? View.VISIBLE : View.GONE);
        setClick(R.id.iv_back);
    }

    private void hasPermissions() {
        permissionView.setVisibility(View.GONE);
        if (Setting.onlyStartCamera) {
            launchCamera(Code.REQUEST_CAMERA);
            return;
        }
        if (Setting.selectedPhotos.size() > Setting.count) {
            throw new RuntimeException("AlbumBuilder: 默认勾选的图片张数不能大于设置的选择数！" + "|默认勾选张数：" + Setting.selectedPhotos.size() + "|设置的选择数：" + Setting.count);
        }
        AlbumModel.CallBack albumModelCallBack = new AlbumModel.CallBack() {
            @Override
            public void onAlbumWorkedCallBack() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onAlbumWorkedDo();
                    }
                });
            }
        };
        albumModel = AlbumModel.getInstance();
        albumModel.query(this, albumModelCallBack);
        if (!Setting.selectedPhotos.isEmpty()) {
            for (Photo selectedPhoto : Setting.selectedPhotos) {
                if (TextUtils.isEmpty(selectedPhoto.name)) {
                    albumModel.fillPhoto(this, selectedPhoto);
                }
                selectedPhoto.selectedOriginal = Setting.selectedOriginal;
                Result.addPhoto(selectedPhoto);
            }
        }
    }

    protected String[] getNeedPermissions() {
        if (Setting.isShowCamera) {
            if (Setting.captureType.equals(Capture.IMAGE)) {
                return new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            } else {
                return new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            }
        } else {
            return new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        PermissionUtil.onPermissionResult(this, permissions, grantResults, new PermissionUtil.PermissionCallBack() {
            @Override
            public void onSuccess() {
                hasPermissions();
            }

            @Override
            public void onShouldShow() {
                tvPermission.setText(R.string.permissions_again_easy_photos);
                permissionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (PermissionUtil.checkAndRequestPermissionsInActivity(EasyPhotosActivity.this, getNeedPermissions())) {
                            hasPermissions();
                        } else {
                            permissionView.setVisibility(View.VISIBLE);
                        }
                    }
                });

            }

            @Override
            public void onFailed() {
                tvPermission.setText(R.string.permissions_die_easy_photos);
                permissionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SettingsUtils.startMyApplicationDetailsForResult(EasyPhotosActivity.this, getPackageName());
                    }
                });

            }
        });
    }


    /**
     * 启动相机
     *
     * @param requestCode startActivityForResult的请求码
     */
    private void launchCamera(int requestCode) {
        if (!cameraIsCanUse()) {
            permissionView.setVisibility(View.VISIBLE);
            tvPermission.setText(R.string.permissions_die_easy_photos);
            permissionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SettingsUtils.startMyApplicationDetailsForResult(EasyPhotosActivity.this, getPackageName());
                }
            });
            return;
        }
        toAndroidCamera(requestCode);
    }

    /**
     * 启动系统相机
     *
     * @param requestCode 请求相机的请求码
     */
    private void toAndroidCamera(int requestCode) {
        Intent intent = new Intent(this, EasyCameraActivity.class);
        startActivityForResult(intent, requestCode);
//系统相机功能屏蔽
//        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
//            createCameraTempImageFile();
//            if (mTempImageFile != null && mTempImageFile.exists()) {
//
//                Uri imageUri;
//                if (Build.VERSION.SDK_INT >= 24) {
//                    imageUri = FileProvider.getUriForFile(this, Setting.fileProviderAuthority,
//                            mTempImageFile);//通过FileProvider创建一个content类型的Uri
//                } else {
//                    imageUri = Uri.fromFile(mTempImageFile);
//                }
//                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //对目标应用临时授权该Uri所代表的文件
//
//                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
//                startActivityForResult(cameraIntent, requestCode);
//            } else {
//                Toast.makeText(this, R.string.camera_temp_file_error_easy_photos, Toast
//                        .LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(this, R.string.msg_no_camera_easy_photos, Toast.LENGTH_SHORT).show();
//        }
    }

//    private void createCameraTempImageFile() {
//        File dir = new File(Environment.getExternalStorageDirectory(), File.separator + "DCIM" +
//                File.separator + "Camera" + File.separator);
//        if (!dir.exists() || !dir.isDirectory()) {
//            if (!dir.mkdirs()) {
//                dir = getExternalFilesDir(null);
//                if (null == dir || !dir.exists()) {
//                    dir = getFilesDir();
//                    if (null == dir || !dir.exists()) {
//                        String cacheDirPath = File.separator + "data" + File.separator + "data" +
//                                File.separator + getPackageName() + File.separator + "cache" +
//                                File.separator;
//                        dir = new File(cacheDirPath);
//                        if (!dir.exists()) {
//                            dir.mkdirs();
//                        }
//                    }
//                }
//            }
//        }
//
//        try {
//            mTempImageFile = File.createTempFile("IMG", ".jpg", dir);
//        } catch (IOException e) {
//            e.printStackTrace();
//            mTempImageFile = null;
//        }
//
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Code.REQUEST_SETTING_APP_DETAILS) {
            if (PermissionUtil.checkAndRequestPermissionsInActivity(this, getNeedPermissions())) {
                hasPermissions();
            } else {
                permissionView.setVisibility(View.VISIBLE);
            }
            return;
        }
        String path = null;
        if (data != null) {
            String videoPath = data.getStringExtra(Key.EXTRA_RESULT_CAPTURE_VIDEO_PATH);
            String imagePath = data.getStringExtra(Key.EXTRA_RESULT_CAPTURE_IMAGE_PATH);
            if (videoPath != null && !videoPath.isEmpty()) {
                path = videoPath;
            } else {
                path = imagePath;
            }
        }
        File tempFile = null;
        if (path != null) tempFile = new File(path);

        switch (resultCode) {
            case RESULT_OK:
                if (data == null) return;
                if (Code.REQUEST_CAMERA == requestCode) {
                    if (tempFile == null || !tempFile.exists()) {
                        throw new RuntimeException("EasyPhotos拍照保存的图片不存在");
                    }
                    onResult(tempFile);
                    return;
                }
                if (Code.REQUEST_PREVIEW_ACTIVITY == requestCode) {
                    photosAdapter.change();
                    processOriginalMenu();
                    shouldShowMenuDone();
                    if (data.getBooleanExtra(Key.PREVIEW_CLICK_DONE, false)) {
                        done();
                    }
                    return;
                }
                if (Code.REQUEST_PUZZLE_SELECTOR == requestCode) {
                    Photo puzzlePhoto = data.getParcelableExtra(EasyPhotos.RESULT_PHOTOS);
                    addNewPhoto(puzzlePhoto);
                    return;
                }
                if (UCrop.REQUEST_CROP == requestCode) {
                    final Uri resultUri = UCrop.getOutput(data);
                    if (resultUri != null) {
                        Intent intent = new Intent();
                        resultList.get(0).cropPath = resultUri.getPath();
                        intent.putParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS, resultList);
                        intent.putExtra(EasyPhotos.RESULT_SELECTED_ORIGINAL, Setting.selectedOriginal);
                        ArrayList<String> resultPaths = new ArrayList<>();
                        for (Photo photo : resultList) {
                            resultPaths.add(photo.path);
                        }
                        intent.putStringArrayListExtra(EasyPhotos.RESULT_PATHS, resultPaths);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    return;
                }
                break;
            case RESULT_CANCELED:
                if (Code.REQUEST_CAMERA == requestCode) {
                    // 删除临时文件
                    while (tempFile != null && tempFile.exists()) {
                        boolean success = tempFile.delete();
                        if (success) {
                            tempFile = null;
                        }
                    }
                    if (Setting.onlyStartCamera) {
                        finish();
                    }
                    return;
                }

                if (Code.REQUEST_PREVIEW_ACTIVITY == requestCode) {
                    processOriginalMenu();
                    return;
                }

                if (UCrop.REQUEST_CROP == requestCode) {
                    if (Setting.onlyStartCamera) {
                        finish();
                    }
                }
                break;
            case UCrop.RESULT_ERROR:
                if (data != null) {
                    Log.e("EasyPhotos", "ucrop occur error: " + UCrop.getError(data));
                }
                break;
            default:
                break;
        }
    }

    private void startCrop(Activity context, String source, Intent data) {

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(source, bitmapOptions);
        if (bitmapOptions.outWidth == -1 || bitmapOptions.outHeight == -1) {
            setResult(RESULT_OK, data);
            finish();
            Log.e("EasyPhotos", "该类型不支持裁剪！");
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH:mm:ss", Locale.getDefault());
        String suffix = source.substring(source.lastIndexOf("."));
        String imageName = "IMG_CROP_%s" + suffix;
        String destinationFileName = String.format(imageName, dateFormat.format(new Date()));

        UCrop.Options options = new UCrop.Options();
        //设置相关颜色
        int statusBarColor = ContextCompat.getColor(this, R.color.easy_photos_status_bar);
        if (ColorUtils.isWhiteColor(statusBarColor)) {
            statusBarColor = Color.LTGRAY;
        }
        options.setStatusBarColor(statusBarColor);
        int barColor = ContextCompat.getColor(this, R.color.easy_photos_bar_primary);
        options.setToolbarColor(barColor);
        int widgetColor = ContextCompat.getColor(this, R.color.easy_photos_fg_primary);
        options.setToolbarWidgetColor(widgetColor);
        options.setActiveWidgetColor(Color.BLACK);
        //options.setLogoColor(Color.TRANSPARENT);
        //设置裁剪质量
        options.setCompressionQuality(Setting.compressQuality);
        //是否圆形裁剪
        options.setCircleDimmedLayer(Setting.isCircle);
        //设置网格相关
        options.setShowCropFrame(Setting.isShowCropCropFrame);
        options.setShowCropGrid(Setting.isShowCropGrid);
        //是否自由裁剪
        options.setFreeStyleCropEnabled(Setting.isFreeStyleCrop);
        //设置title
        options.setToolbarTitle("");
        //隐藏底部控制栏
        options.setHideBottomControls(Setting.isHideUCropControls);

        File cacheFile = new File(context.getCacheDir(), destinationFileName);
        UCrop.of(Uri.fromFile(new File(source)), Uri.fromFile(cacheFile))
                .withAspectRatio(Setting.aspectRatio[0], Setting.aspectRatio[1])
                .withOptions(options)
                .start(context);
    }

    private void addNewPhoto(Photo photo) {
        MediaScannerConnectionUtils.refresh(this, photo.path);
        photo.selectedOriginal = Setting.selectedOriginal;

        String albumItem_all_name = albumModel.getAllAlbumName(this);
        albumModel.album.getAlbumItem(albumItem_all_name).addImageItem(0, photo);
        final File parentFile = new File(photo.path).getParentFile();
        String folderPath = parentFile.getAbsolutePath();
        String albumName = parentFile.getName();
        albumModel.album.addAlbumItem(albumName, folderPath, photo.path);
        albumModel.album.getAlbumItem(albumName).addImageItem(0, photo);

        albumItemList.clear();
        albumItemList.addAll(albumModel.getAlbumItems());
        if (Setting.hasAlbumItemsAd()) {
            int albumItemsAdIndex = 2;
            if (albumItemList.size() < albumItemsAdIndex + 1) {
                albumItemsAdIndex = albumItemList.size() - 1;
            }
            albumItemList.add(albumItemsAdIndex, Setting.albumItemsAdView);
        }
        albumItemsAdapter.notifyDataSetChanged();

        if (Setting.count == 1) {
            Result.clear();
            int res = Result.addPhoto(photo);
            onSelectError(res);
        } else {
            if (Result.count() >= Setting.count) {
                onSelectError(null);
            } else {
                int res = Result.addPhoto(photo);
                onSelectError(res);
            }
        }
        rvAlbumItems.scrollToPosition(0);
        albumItemsAdapter.setSelectedPosition(0);
        shouldShowMenuDone();
    }

    private void onResult(File file) {
        boolean isVideo;
        int outWidth;
        int outHeight;
        String outMimeType;
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(file.getAbsolutePath());
            outMimeType = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            outWidth = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            outHeight = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            isVideo = true;
        } catch (Exception e) {
            e.printStackTrace();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            outMimeType = options.outMimeType;
            outWidth = options.outWidth;
            outHeight = options.outHeight;
            isVideo = false;
        }
        //if (code == Code.REQUEST_CAMERA) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH:mm:ss", Locale.getDefault());
        String fileName = file.getName();
        String suffix = fileName.substring(fileName.lastIndexOf("."));

        String imageName;
        if (isVideo) {
            imageName = "VIDEO_%s" + suffix;
        } else {
            imageName = "IMG_%s" + suffix;
        }

        String filename = String.format(imageName, dateFormat.format(new Date()));
        File reNameFile = new File(file.getParentFile(), filename);
        if (!reNameFile.exists()) {
            if (file.renameTo(reNameFile)) {
                file = reNameFile;
            }
        }
        //}

        Photo photo = new Photo(file.getName(), file.getAbsolutePath(), file.lastModified() / 1000, outWidth, outHeight, file.length(), DurationUtils.getDuration(file.getAbsolutePath()), outMimeType);

        if (Setting.onlyStartCamera || albumModel.getAlbumItems().isEmpty()) {
            MediaScannerConnectionUtils.refresh(this, file);// 更新媒体库

            photo.selectedOriginal = Setting.selectedOriginal;
            Result.addPhoto(photo);
            done();

//            Intent data = new Intent();

//            resultList.add(photo);
//
//            data.putParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS, resultList);
//
//            data.putExtra(EasyPhotos.RESULT_SELECTED_ORIGINAL, Setting.selectedOriginal);
//
//            ArrayList<String> pathList = new ArrayList<>();
//            pathList.add(photo.path);
//
//            data.putStringArrayListExtra(EasyPhotos.RESULT_PATHS, pathList);
//
//            if (Setting.isCrop) {
//                startCrop(this, file.getAbsolutePath(), data);
//            } else {
//                setResult(RESULT_OK, data);
//                finish();
//            }
            return;
        }
        addNewPhoto(photo);
    }

    private void onAlbumWorkedDo() {
        initView();
    }

    private void initView() {

        if (albumModel.getAlbumItems().isEmpty()) {
            Toast.makeText(this, R.string.no_photos_easy_photos, Toast.LENGTH_LONG).show();
            if (Setting.isShowCamera) launchCamera(Code.REQUEST_CAMERA);
            else finish();
            return;
        }

        EasyPhotos.setAdListener(this);
        if (Setting.hasPhotosAd()) {
            findViewById(R.id.m_tool_bar_bottom_line).setVisibility(View.GONE);
        }
        ivCamera = findViewById(R.id.fab_camera);
        if (Setting.isShowCamera && Setting.isBottomRightCamera()) {
            ivCamera.setVisibility(View.VISIBLE);
        }
        if (!Setting.showPuzzleMenu) {
            findViewById(R.id.tv_puzzle).setVisibility(View.GONE);
        }
        mSecondMenus = findViewById(R.id.m_second_level_menu);
        int columns = getResources().getInteger(R.integer.photos_columns_easy_photos);
        tvAlbumItems = findViewById(R.id.tv_album_items);
        tvAlbumItems.setText(albumModel.getAlbumItems().get(0).name);
        tvDone = findViewById(R.id.tv_done);
        rvPhotos = findViewById(R.id.rv_photos);
        ((SimpleItemAnimator) rvPhotos.getItemAnimator()).setSupportsChangeAnimations(false);
        //去除item更新的闪光
        photoList.clear();
        photoList.addAll(albumModel.getCurrAlbumItemPhotos(0));
        int index = 0;
        if (Setting.hasPhotosAd()) {
            photoList.add(index, Setting.photosAdView);
        }
        if (Setting.isShowCamera && !Setting.isBottomRightCamera()) {
            if (Setting.hasPhotosAd()) index = 1;
            photoList.add(index, null);
        }
        photosAdapter = new PhotosAdapter(this, photoList, this);

        gridLayoutManager = new GridLayoutManager(this, columns);
        if (Setting.hasPhotosAd()) {
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (position == 0) {
                        return gridLayoutManager.getSpanCount();//独占一行
                    } else {
                        return 1;//只占一行中的一列
                    }
                }
            });
        }
        rvPhotos.setLayoutManager(gridLayoutManager);
        rvPhotos.setAdapter(photosAdapter);
        tvOriginal = findViewById(R.id.tv_original);
        if (Setting.showOriginalMenu) {
            processOriginalMenu();
        } else {
            tvOriginal.setVisibility(View.GONE);
        }
        tvPreview = findViewById(R.id.tv_preview);

        initAlbumItems();
        shouldShowMenuDone();
        setClick(R.id.iv_album_items, R.id.tv_clear, R.id.iv_second_menu, R.id.tv_puzzle);
        setClick(tvAlbumItems, rootViewAlbumItems, tvDone, tvOriginal, tvPreview, ivCamera);

    }

    private void hideActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void initAlbumItems() {
        rvAlbumItems = findViewById(R.id.rv_album_items);
        albumItemList.clear();
        albumItemList.addAll(albumModel.getAlbumItems());

        if (Setting.hasAlbumItemsAd()) {
            int albumItemsAdIndex = 2;
            if (albumItemList.size() < albumItemsAdIndex + 1) {
                albumItemsAdIndex = albumItemList.size() - 1;
            }
            albumItemList.add(albumItemsAdIndex, Setting.albumItemsAdView);
        }
        albumItemsAdapter = new AlbumItemsAdapter(this, albumItemList, 0, this);
        rvAlbumItems.setLayoutManager(new LinearLayoutManager(this));
        rvAlbumItems.setAdapter(albumItemsAdapter);
    }

    @Override
    public void finish() {
        super.finish();
        Setting.clear();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.tv_album_items == id || R.id.iv_album_items == id) {
            showAlbumItems(View.GONE == rootViewAlbumItems.getVisibility());
        } else if (R.id.root_view_album_items == id) {
            showAlbumItems(false);
        } else if (R.id.iv_back == id) {
            setResult(RESULT_CANCELED);
            finish();
        } else if (R.id.tv_done == id) {
            done();
        } else if (R.id.tv_clear == id) {
            if (Result.isEmpty()) {
                processSecondMenu();
                return;
            }
            Result.removeAll();
            photosAdapter.change();
            shouldShowMenuDone();
            processSecondMenu();
        } else if (R.id.tv_original == id) {
            if (!Setting.originalMenuUsable) {
                Toast.makeText(this, Setting.originalMenuUnusableHint, Toast.LENGTH_SHORT).show();
                return;
            }
            Setting.selectedOriginal = !Setting.selectedOriginal;
            processOriginalMenu();
            processSecondMenu();
        } else if (R.id.tv_preview == id) {
            PreviewActivity.start(EasyPhotosActivity.this, -1, 0);
        } else if (R.id.fab_camera == id) {
            launchCamera(Code.REQUEST_CAMERA);
        } else if (R.id.iv_second_menu == id) {
            processSecondMenu();
        } else if (R.id.tv_puzzle == id) {
            processSecondMenu();
            PuzzleSelectorActivity.start(this);
        }
    }

    public void processSecondMenu() {
        if (mSecondMenus == null) {
            return;
        }
        if (View.VISIBLE == mSecondMenus.getVisibility()) {
            mSecondMenus.setVisibility(View.INVISIBLE);
            if (Setting.isShowCamera && Setting.isBottomRightCamera()) {
                ivCamera.setVisibility(View.VISIBLE);
            }
        } else {
            mSecondMenus.setVisibility(View.VISIBLE);
            if (Setting.isShowCamera && Setting.isBottomRightCamera()) {
                ivCamera.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void done() {
        Intent intent = new Intent();
        Result.processOriginal();
        resultList.clear();
        resultList.addAll(Result.photos);
        intent.putParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS, resultList);
        ArrayList<String> resultPaths = new ArrayList<>();
        for (Photo photo : resultList) {
            resultPaths.add(photo.path);
        }
        intent.putStringArrayListExtra(EasyPhotos.RESULT_PATHS, resultPaths);
        intent.putExtra(EasyPhotos.RESULT_SELECTED_ORIGINAL, Setting.selectedOriginal);
        if (Setting.isCrop) {
            startCrop(this, resultList.get(0).path, intent);
        } else {
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void processOriginalMenu() {
        if (!Setting.showOriginalMenu) return;
        if (Setting.selectedOriginal) {
            tvOriginal.setTextColor(ContextCompat.getColor(this, R.color.easy_photos_fg_accent));
        } else {
            if (Setting.originalMenuUsable) {
                tvOriginal.setTextColor(ContextCompat.getColor(this, R.color.easy_photos_fg_primary));
            } else {
                tvOriginal.setTextColor(ContextCompat.getColor(this, R.color.easy_photos_fg_primary_dark));
            }
        }
    }

    private void showAlbumItems(boolean isShow) {
        if (null == setShow) {
            newAnimators();
        }
        if (isShow) {
            rootViewAlbumItems.setVisibility(View.VISIBLE);
            setShow.start();
        } else {
            setHide.start();
        }

    }

    private void newAnimators() {
        newHideAnim();
        newShowAnim();
    }

    private void newShowAnim() {
        ObjectAnimator translationShow = ObjectAnimator.ofFloat(rvAlbumItems, "translationY",
                mBottomBar.getTop(), 0);
        ObjectAnimator alphaShow = ObjectAnimator.ofFloat(rootViewAlbumItems, "alpha", 0.0f, 1.0f);
        translationShow.setDuration(300);
        setShow = new AnimatorSet();
        setShow.setInterpolator(new AccelerateDecelerateInterpolator());
        setShow.play(translationShow).with(alphaShow);
    }

    private void newHideAnim() {
        ObjectAnimator translationHide = ObjectAnimator.ofFloat(rvAlbumItems, "translationY", 0,
                mBottomBar.getTop());
        ObjectAnimator alphaHide = ObjectAnimator.ofFloat(rootViewAlbumItems, "alpha", 1.0f, 0.0f);
        translationHide.setDuration(200);
        setHide = new AnimatorSet();
        setHide.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                rootViewAlbumItems.setVisibility(View.GONE);
            }
        });
        setHide.setInterpolator(new AccelerateInterpolator());
        setHide.play(translationHide).with(alphaHide);
    }

    @Override
    public void onAlbumItemClick(int position, int realPosition) {
        updatePhotos(realPosition);
        showAlbumItems(false);
        tvAlbumItems.setText(albumModel.getAlbumItems().get(realPosition).name);
    }

    private void updatePhotos(int currAlbumItemIndex) {
        this.currAlbumItemIndex = currAlbumItemIndex;
        photoList.clear();
        photoList.addAll(albumModel.getCurrAlbumItemPhotos(currAlbumItemIndex));
        int index = 0;
        if (Setting.hasPhotosAd()) {
            photoList.add(index, Setting.photosAdView);
        }
        if (Setting.isShowCamera && !Setting.isBottomRightCamera()) {
            if (Setting.hasPhotosAd()) index = 1;
            photoList.add(index, null);
        }
        photosAdapter.change();
        rvPhotos.scrollToPosition(0);
    }

    private void shouldShowMenuDone() {
        if (Result.isEmpty()) {
            if (View.VISIBLE == tvDone.getVisibility()) {
                ScaleAnimation scaleHide = new ScaleAnimation(1f, 0f, 1f, 0f);
                scaleHide.setDuration(200);
                tvDone.startAnimation(scaleHide);
            }
            tvDone.setVisibility(View.INVISIBLE);
            tvPreview.setVisibility(View.INVISIBLE);
        } else {
            if (View.INVISIBLE == tvDone.getVisibility()) {
                ScaleAnimation scaleShow = new ScaleAnimation(0f, 1f, 0f, 1f);
                scaleShow.setDuration(200);
                tvDone.startAnimation(scaleShow);
            }
            tvDone.setVisibility(View.VISIBLE);
            tvPreview.setVisibility(View.VISIBLE);
        }
        if(Setting.isDistinguish){
            if(Result.count()>0){
                if(Result.photos.get(0).type.contains(Type.VIDEO)){
                    tvDone.setText(getString(R.string.selector_action_done_easy_photos, Result.count(), Setting.videoCount));
                }else if(Result.photos.get(0).type.contains(Type.IMAGE)){
                    tvDone.setText(getString(R.string.selector_action_done_easy_photos, Result.count(), Setting.pictureCount));
                }else{
                    tvDone.setText(getString(R.string.selector_action_done_easy_photos, Result.count(), Setting.count));
                }
            }
        }else {
            tvDone.setText(getString(R.string.selector_action_done_easy_photos, Result.count(), Setting.count));
        }
    }

    @Override
    public void onCameraClick() {
        launchCamera(Code.REQUEST_CAMERA);
    }

    @Override
    public void onPhotoClick(int position, int realPosition) {
        PreviewActivity.start(EasyPhotosActivity.this, currAlbumItemIndex, realPosition);
    }

    @Override
    public void onSelectError(@Nullable Integer result) {
        if (result == null) {
            Toast.makeText(this, getString(R.string.selector_reach_max_hint_easy_photos, Setting.count), Toast.LENGTH_SHORT).show();
            return;
        }
        switch (result) {
            case -1:
                Toast.makeText(this, getString(R.string.selector_reach_max_image_hint_easy_photos, Setting.pictureCount), Toast.LENGTH_SHORT).show();
                break;
            case -2:
                Toast.makeText(this, getString(R.string.selector_reach_max_video_hint_easy_photos, Setting.videoCount), Toast.LENGTH_SHORT).show();
                break;
            case -3:
                Toast.makeText(this, getString(R.string.msg_no_file_easy_photos), Toast.LENGTH_SHORT).show();
                break;
            case -4:
                Toast.makeText(this, getString(R.string.selector_mutual_exclusion_easy_photos), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onSelectorChanged() {
        shouldShowMenuDone();
    }


    @Override
    public void onBackPressed() {

        if (null != rootViewAlbumItems && rootViewAlbumItems.getVisibility() == View.VISIBLE) {
            showAlbumItems(false);
            return;
        }

        if (null != mSecondMenus && View.VISIBLE == mSecondMenus.getVisibility()) {
            processSecondMenu();
            return;
        }

        super.onBackPressed();
    }

    @Override
    public void onPhotosAdLoaded() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                photosAdapter.change();
            }
        });
    }

    @Override
    public void onAlbumItemsAdLoaded() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                albumItemsAdapter.notifyDataSetChanged();
            }
        });
    }


    private void setClick(@IdRes int... ids) {
        for (int id : ids) {
            findViewById(id).setOnClickListener(this);
        }
    }

    private void setClick(View... views) {
        for (View v : views) {
            v.setOnClickListener(this);
        }
    }

    /**
     * 返回true 表示可以使用  返回false表示不可以使用
     */
    public boolean cameraIsCanUse() {
        boolean isCanUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            Camera.Parameters mParameters = mCamera.getParameters(); //针对魅族手机
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            isCanUse = false;
        }

        if (mCamera != null) {
            try {
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
                return isCanUse;
            }
        }
        return isCanUse;
    }
}
