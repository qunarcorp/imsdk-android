package com.qunar.im.ui.view.camera;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.ui.view.camera.listener.ClickListener;
import com.qunar.im.ui.view.camera.listener.ErrorListener;
import com.qunar.im.ui.view.camera.listener.QCameraListener;
import com.qunar.im.ui.view.camera.utils.DeviceUtil;
import com.qunar.im.ui.view.camera.utils.FileUtil;
import com.qunar.im.ui.view.camera.view.QCameraView;

import java.io.File;


public class CameraActivity extends IMBaseActivity {
    private static final String TAG = "CameraActivity";
    private QCameraView qCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.atom_ui_activity_camera_layout);
        qCameraView = findViewById(R.id.qcameraview);
        //设置视频保存路径
        qCameraView.setSaveVideoPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "QCamera");
        qCameraView.setFeatures(QCameraView.BUTTON_STATE_BOTH);
        qCameraView.setTip(getString(R.string.atom_ui_function_camera_tip));
        qCameraView.setMediaQuality(QCameraView.MEDIA_QUALITY_MIDDLE);
        qCameraView.setErrorLisenter(new ErrorListener() {
            @Override
            public void onError() {
                //错误监听
                Log.i(TAG, "camera error");
                Intent intent = new Intent();
                setResult(103, intent);
                finish();
            }

            @Override
            public void AudioPermissionError() {
                Toast.makeText(CameraActivity.this, "给点录音权限可以?", Toast.LENGTH_SHORT).show();
            }
        });
        //QCameraView监听
        qCameraView.setQCameraLisenter(new QCameraListener() {
            @Override
            public void captureSuccess(Bitmap bitmap) {
                //获取图片bitmap
//                Log.i("QCameraView", "bitmap = " + bitmap.getWidth());
                String path = FileUtil.saveBitmap("QCamera", bitmap);
                Intent intent = new Intent();
                intent.putExtra("path", path);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void recordSuccess(String url, Bitmap firstFrame) {
                //获取视频路径
                String path = FileUtil.saveBitmap("QCamera", firstFrame);
                Log.i(TAG, "url = " + url + ", Bitmap = " + path);
                Intent intent = new Intent();
                intent.putExtra("path", path);
                intent.putExtra("videopath", url);

                setResult(RESULT_OK, intent);
                finish();
            }
        });

        qCameraView.setLeftClickListener(new ClickListener() {
            @Override
            public void onClick() {
                CameraActivity.this.finish();
            }
        });
        qCameraView.setRightClickListener(new ClickListener() {
            @Override
            public void onClick() {
                Toast.makeText(CameraActivity.this,"Right", Toast.LENGTH_SHORT).show();
            }
        });

        Log.i(TAG, DeviceUtil.getDeviceModel());
    }

    @Override
    protected void onStart() {
        super.onStart();
        //全屏显示
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        qCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        qCameraView.onPause();
    }
}
