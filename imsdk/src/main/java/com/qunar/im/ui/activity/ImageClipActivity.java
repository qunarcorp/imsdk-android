package com.qunar.im.ui.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.base.util.FileUtils;
import com.qunar.im.base.util.Utils;
import com.qunar.im.base.util.graphics.BitmapHelper;
import com.qunar.im.base.util.graphics.ImageUtils;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.ClipView;
import com.qunar.im.ui.view.QtNewActionBar;

import java.io.File;

import de.greenrobot.event.EventBus;

/**
 * Created by xinbo.wang on 2015/5/25.
 */
public class ImageClipActivity extends IMBaseActivity implements View.OnTouchListener, IMNotificaitonCenter.NotificationCenterDelegate {
    public final static String KEY_SEL_BITMAP = "sel_bitmap";
    public final static String KEY_CLIP_ENABLE = "clip_enable";
    public final static String KEY_CAMERA_PATH = "camera_path";
    /**
     * 动作标志：无
     */
    private static final int NONE = 0;
    /**
     * 动作标志：拖动
     */
    private static final int DRAG = 1;
    /**
     * 动作标志：缩放
     */
    private static final int ZOOM = 2;
    ImageView source_pic;
    RelativeLayout ll_option_origin;
    CheckBox chk_use_origin;
    TextView editImage;
    boolean enableClip = true;
    String cameraPath = null;
    boolean inited;
    private Bitmap selectedImg;
    private ClipView clipView;
    /**
     * 初始化动作标志
     */
    private int mode = NONE;
    /**
     * 记录起始坐标
     */
    private PointF start = new PointF();
    /**
     * 记录缩放时两指中间点坐标
     */
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private ConnectionUtil connectionUtil;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.atom_ui_activity_gravantar_clip);
        connectionUtil = ConnectionUtil.getInstance();
        addEvent();
        bindViews();
        Bundle options = getIntent().getExtras();
        if (options != null) {
            if (options.containsKey(KEY_SEL_BITMAP)) {
                String selectFilePath = options.getString(KEY_SEL_BITMAP);
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                selectedImg = BitmapHelper.decodeFile(selectFilePath, metrics.widthPixels, metrics.heightPixels);
            }
            if (options.containsKey(KEY_CLIP_ENABLE)) {
                enableClip = options.getBoolean(KEY_CLIP_ENABLE);
                if (enableClip) {
                    ll_option_origin.setVisibility(View.GONE);
                }
            }
            if (options.containsKey(KEY_CAMERA_PATH)) {
                cameraPath = options.getString(KEY_CAMERA_PATH);
            }
        }
        initViews();
        EventBus.getDefault().register(this);
    }

    private void bindViews() {
        source_pic = (ImageView) findViewById(R.id.source_pic);
        ll_option_origin = (RelativeLayout) findViewById(R.id.ll_option_origin);
        chk_use_origin = (CheckBox) findViewById(R.id.chk_use_origin);
        editImage = (TextView) findViewById(R.id.edit_msg);
    }
    private void addEvent(){
        connectionUtil.addEvent(this,QtalkEvent.SEND_PHOTO_AFTER_EDIT);
    }
    private void removeEvent(){
        connectionUtil.removeEvent(this,QtalkEvent.SEND_PHOTO_AFTER_EDIT);

    }


    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }

    public void callCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //申请权限 WRITE_EXTERNAL_STORAGE
        ContentValues contentValues = new ContentValues(1);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        Uri mCapturePath = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        cameraPath = getPath(mCapturePath);
        File file = new File(cameraPath);
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturePath);
        startActivityForResult(intent, PbChatActivity.ACTIVITY_GET_CAMERA_IMAGE);
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(column_index);
        cursor.close();
        return s;
    }

    private void startCamera() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
            callCamera();
        } else {
            Toast.makeText(this, "没有SD卡", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    void initViews() {
        QtNewActionBar qtNewActionBar = (QtNewActionBar)findViewById(R.id.my_action_bar);
        setNewActionBar(qtNewActionBar);
        setActionBarRightText(R.string.atom_ui_common_use);
        setActionBarRightTextClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultData = new Intent();
                if (enableClip) {
                    Bitmap clipBitmap = getBitmap();
                    File tempFile = new File(FileUtils.getExternalFilesDir(ImageClipActivity.this),
                            "temp_gravatar.jpeg");
                    ImageUtils.saveBitmap(clipBitmap, tempFile);
                    EventBus.getDefault().post(new EventBusEvent.GravanterSelected(tempFile));
                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.GravanterSelected,tempFile);
                } else {
                    if (!chk_use_origin.isChecked()) {
                        File target = new File(cameraPath.substring(0, cameraPath.lastIndexOf("."))
                                + "_cmp.jpeg");
                        target = ImageUtils.compressFile(selectedImg, target);
                        if (target.exists()) {
                            cameraPath = target.getAbsolutePath();
                        }
                    }
                    resultData.putExtra(KEY_CAMERA_PATH, cameraPath);
                    setResult(RESULT_OK, resultData);
                }
                finish();
            }
        });
        if (!inited) {
            source_pic.setOnTouchListener(this);
            if (selectedImg != null) {
                initClipView();
            } else {
                startCamera();
            }
            inited = true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.isFinishing()) {
            if (selectedImg != null) {
                selectedImg.recycle();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        boolean isCacel = true;
        if (resultCode == RESULT_OK && requestCode == PbChatActivity.ACTIVITY_GET_CAMERA_IMAGE) {
            if (cameraPath != null) {
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                selectedImg = BitmapHelper.decodeFile(cameraPath, metrics.widthPixels, metrics.heightPixels);
                if (selectedImg != null) {
                    isCacel = false;
                    if (enableClip) {
                        initClipView();
                    } else {
                        ll_option_origin.setVisibility(View.VISIBLE);
                        chk_use_origin.append("(" + FileUtils.getFormatFileSize(cameraPath) + ")");
                        source_pic.setImageBitmap(selectedImg);
                        editImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ImageClipActivity.this, EditPictureActivity.class);
                                File target = new File(cameraPath.substring(0, cameraPath.lastIndexOf(".")) + "_cmp.jpeg");
                                target = ImageUtils.compressFile(selectedImg, target);
                                if (target.exists()) {
                                    cameraPath = target.getAbsolutePath();
                                }
                                intent.putExtra(KEY_CAMERA_PATH, cameraPath);
                                startActivity(intent);
                            }
                        });
                    }
                }
            }
        } else if (requestCode == PbChatActivity.ACTIVITY_GET_CAMERA_IMAGE) {
            File file = new File(cameraPath);
            if (file.exists()) file.delete();
            String[] params = new String[]{cameraPath};
            getContentResolver().delete(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    MediaStore.Images.Media.DATA + " LIKE ?", params);
        }
        if (isCacel) {
            setResult(RESULT_CANCELED);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (cameraPath != null) {
            File file = new File(cameraPath);
            if (file.exists()) {
                file.delete();
            }
        }
        finish();
    }


    /**
     * 初始化截图区域，并将源图按裁剪框比例缩放
     */
    private void initClipView() {

        clipView = new ClipView(this);
//        clipView.setCustomTopBarHeight(getStatusBarHeight());
        clipView.setClipHeight(Utils.dipToPixels(this, 192));
        clipView.setClipWidth(Utils.dipToPixels(this, 192));
        clipView.setClipRatio(1);
        clipView.addOnDrawCompleteListener(new ClipView.OnDrawListenerComplete() {

            public void onDrawCompelete() {
                clipView.removeOnDrawCompleteListener();
                int clipHeight = clipView.getClipHeight();
                int clipWidth = clipView.getClipWidth();
                int midX = clipView.getClipLeftMargin() + (clipWidth / 2);
                int midY = clipView.getClipTopMargin() + (clipHeight / 2);

                int imageWidth = selectedImg.getWidth();
                int imageHeight = selectedImg.getHeight();
                // 按裁剪框求缩放比例
                float scale = (clipWidth * 1.0f) / imageWidth;
                if (imageWidth > imageHeight) {
                    scale = (clipHeight * 1.0f) / imageHeight;
                }

                // 起始中心点
                float imageMidX = imageWidth * scale / 2;
                float imageMidY = getStatusBarHeight()
                        + imageHeight * scale / 2;
                source_pic.setScaleType(ImageView.ScaleType.MATRIX);

                // 缩放
                matrix.postScale(scale, scale);
                // 平移
                matrix.postTranslate(midX - imageMidX, midY - imageMidY);

                source_pic.setImageMatrix(matrix);
                source_pic.setImageBitmap(selectedImg);
            }
        });

        this.addContentView(clipView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * 获取裁剪框内截图
     *
     * @return
     */
    private Bitmap getBitmap() {
        // 获取截屏
        View view = this.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

//        int actionBarHeight = 0;
//        if (getSupportActionBar() != null) {
//            actionBarHeight = getSupportActionBar().getHeight();
//        }
        int statusBarHeight = getStatusBarHeight();
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // 获取状态栏高度
//            Rect frame = new Rect();
//            this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
//            statusBarHeight = frame.top;
//        }
        Bitmap finalBitmap = Bitmap.createBitmap(view.getDrawingCache(),
                clipView.getClipLeftMargin(), clipView.getClipTopMargin()
                        + statusBarHeight, clipView.getClipWidth(),
                clipView.getClipHeight());

        // 释放资源
        view.destroyDrawingCache();
        return finalBitmap;
    }

    private int getStatusBarHeight(){
        Rect frame = new Rect();
        this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                // 设置开始点位置
                start.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY()
                            - start.y);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }
        view.setImageMatrix(matrix);
        return true;
    }

    /**
     * 多点触控时，计算最先放下的两指距离
     *
     * @param event
     * @return
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        removeEvent();
        super.onDestroy();
    }

    /**
     * 多点触控时，计算最先放下的两指中心坐标
     *
     * @param point
     * @param event
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
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