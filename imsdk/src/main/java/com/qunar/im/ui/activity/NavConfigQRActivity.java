package com.qunar.im.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.core.content.FileProvider;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.base.util.graphics.ImageUtils;
import com.qunar.im.ui.R;
import com.qunar.im.ui.imagepicker.util.ProviderUtil;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;
import com.qunar.im.utils.DeviceUtil;
import com.qunar.im.utils.QRUtil;

import java.io.File;

/**
 * Created by hubo.hu on 2017/9/22.
 * 导航配置生成二维码
 */
public class NavConfigQRActivity extends SwipeBackActivity implements View.OnClickListener {

    private static final int SAVE_TO_GALLERY = 0x1;
    private static final int CANCEL = 0x2;
    ImageView qr_show_img;
    TextView configname;
    ProgressBar qr_loading;
    LinearLayout root_container;
    FrameLayout atom_qr_layout;
    String name;
    String url;
    int width;

    private Bitmap bitmap;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.atom_ui_activity_nav_config_detail_qrshow);
        bindViews();
        injectExtras();
        initViews();
    }

    private void bindViews() {
        atom_qr_layout = (FrameLayout) findViewById(R.id.atom_qr_layout);
        root_container = (LinearLayout) findViewById(R.id.config_root_container);
        qr_show_img = (ImageView) findViewById(R.id.config_qr_show_img);
        configname = (TextView) findViewById(R.id.config_name);
        qr_loading = (ProgressBar) findViewById(R.id.config_qr_loading);
        root_container.setOnClickListener(this);
        width = DeviceUtil.getWindowWidthPX(this) *2/3;
        atom_qr_layout.setLayoutParams(new LinearLayout.LayoutParams(width,width));
    }

    private void injectExtras() {
        Bundle extras_ = getIntent().getExtras();
        if (extras_ != null) {
            if (extras_.containsKey("url")) {
                url = extras_.getString("url");
            }
            if (extras_.containsKey("name")) {
                name = extras_.getString("name");
                configname.setText(name);
            }
        }
    }

    void initViews() {
        generateQRCode();

        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        if(getIntent() != null && getIntent().getExtras().containsKey("title")){
            setActionBarTitle(getIntent().getStringExtra("title"));
        }
        setActionBarRightIcon(R.string.atom_ui_new_share);
        setActionBarRightIconClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //图片保存路径
                File appDir = new File(Environment.getExternalStorageDirectory(), "Qtalk" + System.currentTimeMillis() + ".jpg");
                ImageUtils.compressFile(bitmap, appDir);
                if(appDir != null && appDir.exists()){
                    Intent imageIntent = new Intent(Intent.ACTION_SEND);
                    imageIntent.setType("image/*");
                    Uri uri;
                    if (Build.VERSION.SDK_INT >= 24) {
                        imageIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        uri = FileProvider.getUriForFile(NavConfigQRActivity.this, ProviderUtil.getFileProviderName(NavConfigQRActivity.this), appDir);//android 7.0以上
                    }else {
                        uri = Uri.fromFile(appDir);
                    }
                    imageIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(imageIntent, "分享"));
                }
            }
        });

        qr_show_img.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, SAVE_TO_GALLERY, 0, R.string.atom_ui_menu_save_image);
                menu.add(0, CANCEL, 0, R.string.atom_ui_common_cancel);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop()
    {
        onWindowFocusChanged(false);
        super.onStop();
    }

    void generateQRCode() {
        BackgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                //名称和url拼成二维码  ~解析分割用
                bitmap = QRUtil.generateQRImage(url,width,width);//name + "~" + 暂时去掉二维码中的name
                getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        qr_loading.setVisibility(View.GONE);
                        qr_show_img.setImageBitmap(bitmap);
                    }
                });
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case SAVE_TO_GALLERY:
                savePicture();
                break;
            case CANCEL:
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    void rootContainerEventHandler() {
        finish();
    }


    void savePicture() {
        ImageUtils.saveToGallery(this, ImageUtils.getViewScreenshot(this));
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.root_container) {
            rootContainerEventHandler();

        }
    }

   /* public static Uri addImageToGallery(Context context, String title, String description) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }*/

}
