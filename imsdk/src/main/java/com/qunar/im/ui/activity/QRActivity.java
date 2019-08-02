package com.qunar.im.ui.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.qunar.im.ui.R;
import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.base.util.graphics.ImageUtils;
import com.qunar.im.utils.QRUtil;


/**
 * Created by xinbo.wang on 2015/5/18.
 */
public class QRActivity extends IMBaseActivity implements View.OnClickListener {

    private static final int SAVE_TO_GALLERY = 0x1;
    private static final int CANCEL = 0x2;
    ImageView qr_show_img;
    ProgressBar qr_loading;
    LinearLayout root_container;
    String qrString;

    private Bitmap bitmap;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.atom_ui_activity_qrshow);
        bindViews();
        injectExtras();
        initViews();
    }

    private void bindViews() {

        root_container = (LinearLayout) findViewById(R.id.root_container);
        qr_show_img = (ImageView) findViewById(R.id.qr_show_img);
        qr_loading = (ProgressBar) findViewById(R.id.qr_loading);
        root_container.setOnClickListener(this);
    }

    private void injectExtras() {
        Bundle extras_ = getIntent().getExtras();
        if (extras_ != null) {
            if (extras_.containsKey("qrString")) {
                qrString = extras_.getString("qrString");
            }
        }
    }

    void initViews() {
        generateQRCode();
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
                bitmap = QRUtil.generateQRImage(qrString);
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
        ImageUtils.saveToGallery(this, bitmap);
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
