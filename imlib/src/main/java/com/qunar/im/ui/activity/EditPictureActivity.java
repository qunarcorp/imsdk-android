package com.qunar.im.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.qunar.im.base.util.FileUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.graphics.ImageUtils;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.core.imgtool.Luban;
import com.qunar.im.core.imgtool.OnCompressListener;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.EditPictureView.DrawAttribute;
import com.qunar.im.ui.view.EditPictureView.DrawingBoardView;
import com.qunar.im.ui.view.EditPictureView.ScrawlTools;
import com.qunar.im.ui.view.QtNewActionBar;

import java.io.File;
import java.util.UUID;

public class EditPictureActivity extends IMBaseActivity implements View.OnClickListener {
    public static final String TAG = "EditPictureActivity";
    private DrawingBoardView drawView;
    ScrawlTools casualWaterUtil = null;
    String mPath;
    Button btn_reset;
    Button btn_edit;
    LinearLayout ll_draw_content;
    private String path;
    private boolean isEditable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_edit_picture);
        initActionBar();
        drawView = (DrawingBoardView) findViewById(R.id.drawView);
        initViews();
        Intent intent = getIntent();
        mPath = intent.getStringExtra(ImageClipActivity.KEY_CAMERA_PATH);
        compressWithLs(mPath);

    }

    public void initViews() {
        btn_reset = (Button) findViewById(R.id.btn_reset);
        btn_edit = (Button) findViewById(R.id.btn_edit);
        ll_draw_content = (LinearLayout) findViewById(R.id.ll_draw_content);
        btn_reset.setOnClickListener(this);
        btn_edit.setOnClickListener(this);
    }

    public void initActionBar() {
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setActionBarTitle(R.string.atom_ui_title_edit_pictures);
        setActionBarRightText(R.string.atom_ui_common_send);
        setActionBarRightTextClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEditable) return;
                LogUtil.d(TAG, "time:" + System.currentTimeMillis());
                Bitmap bitmap = drawView.getDrawBitmap();
                LogUtil.d(TAG, "time:" + System.currentTimeMillis());
                File tempFile = new File(FileUtils.getExternalFilesDir(EditPictureActivity.this),
                        UUID.randomUUID().toString() + ".jpg");
                ImageUtils.saveBitmap(bitmap, tempFile);
                LogUtil.d(TAG, "time:" + System.currentTimeMillis());
                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.SEND_PHOTO_AFTER_EDIT, tempFile.getAbsolutePath());
//                EventBus.getDefault().post(new EventBusEvent.NewPictureEdit(tempFile.getAbsolutePath()));
                EditPictureActivity.this.finish();
            }
        });

    }


    private void setBackgroundBitmap(String path) {
        Bitmap resizeBmp = BitmapFactory.decodeFile(path);
//        Bitmap resizeBmp = ImageUtils.compressBimap(path, this);
//        if(resizeBmp==null||resizeBmp.isRecycled()){
//            Toast.makeText(this, "图片破损，无法编辑", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
        if (resizeBmp == null) {
            Toast.makeText(this, R.string.atom_ui_tip_image_broken, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) drawView.getLayoutParams();
        layoutParams.width = resizeBmp.getWidth();
        layoutParams.height = resizeBmp.getHeight();
        drawView.setLayoutParams(layoutParams);
        casualWaterUtil = new ScrawlTools(this, drawView, resizeBmp);
        Bitmap paintBitmap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.atom_ui_marker);
        casualWaterUtil.creatDrawPainter(DrawAttribute.DrawStatus.PEN_WATER,
                paintBitmap, 0xffff0000);
    }

    /**
     * 压缩图片 Listener 方式
     */
    private void compressWithLs(String photo) {
        Luban.with(this)
                .load(photo)
                .ignoreBy(50)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(File file) {
                        path = file.getAbsolutePath();
                        isEditable = true;
                        setBackgroundBitmap(path);

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(EditPictureActivity.this, R.string.atom_ui_tip_edit_failed, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).launch();
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btn_reset) {
            if (isEditable)
                setBackgroundBitmap(path);

        } else if (i == R.id.btn_edit) {
            if (!isEditable) return;
            String text = btn_edit.getText().toString();
            if (getText(R.string.atom_ui_btn_edit_mode).equals(text)) {
                drawView.setEditable(true);
                btn_edit.setText(R.string.atom_ui_btn_browse_mode);
            } else {
                drawView.setEditable(false);
                btn_edit.setText(R.string.atom_ui_btn_edit_mode);
            }
        } else {
        }
    }

    @Override
    protected void onDestroy() {
        if (casualWaterUtil != null) {
            casualWaterUtil.getBitmap().recycle();
        }
        super.onDestroy();
    }
}