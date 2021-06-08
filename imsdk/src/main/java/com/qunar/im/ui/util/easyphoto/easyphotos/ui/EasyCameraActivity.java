package com.qunar.im.ui.util.easyphoto.easyphotos.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.qunar.im.ui.util.easyphoto.cameralibrary.JCameraView;
import com.qunar.im.ui.util.easyphoto.cameralibrary.listener.ClickListener;
import com.qunar.im.ui.util.easyphoto.cameralibrary.listener.ErrorListener;
import com.qunar.im.ui.util.easyphoto.cameralibrary.listener.JCameraListener;
import com.qunar.im.ui.util.easyphoto.cameralibrary.util.FileUtil;
import com.qunar.im.ui.R;
import com.qunar.im.ui.util.easyphoto.easyphotos.constant.Capture;
import com.qunar.im.ui.util.easyphoto.easyphotos.constant.Key;
import com.qunar.im.ui.util.easyphoto.easyphotos.setting.Setting;

import java.io.File;

public class EasyCameraActivity extends AppCompatActivity {
    private JCameraView jCameraView;
    private String applicationName = "EasyPhotos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        if (Build.VERSION.SDK_INT >= 28) {
//            WindowManager.LayoutParams lp = getWindow().getAttributes();
//            // 始终允许窗口延伸到屏幕短边上的刘海区域
//            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
//            getWindow().setAttributes(lp);
//        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_camera);
        jCameraView = findViewById(R.id.jCameraView);
        jCameraView.enableCameraTip(Setting.enableCameraTip);
        if (Setting.cameraCoverView != null && Setting.cameraCoverView.get() != null) {
            View coverView = Setting.cameraCoverView.get();
            RelativeLayout rlCoverView = findViewById(R.id.rl_cover_view);
            coverView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            rlCoverView.addView(coverView);
        }
        try {
            PackageManager packageManager = getApplicationContext().getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            applicationName = (String) packageManager.getApplicationLabel(applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        init();
    }

    private int getFeature() {
        switch (Setting.captureType) {
            case Capture.ALL:
                return JCameraView.BUTTON_STATE_BOTH;
            case Capture.IMAGE:
                return JCameraView.BUTTON_STATE_ONLY_CAPTURE;
            default:
                return JCameraView.BUTTON_STATE_ONLY_RECORDER;
        }
    }

    private void init() {
        //视频存储路径
        jCameraView.setSaveVideoPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + applicationName);
        jCameraView.setFeatures(getFeature());
        jCameraView.setMediaQuality(JCameraView.MEDIA_QUALITY_MIDDLE);
        //fixme 录像时间+800ms 修复录像时间少1s问题
        jCameraView.setDuration(Setting.recordDuration + 800);
        jCameraView.setErrorLisenter(new ErrorListener() {
            @Override
            public void onError() {
                //错误监听
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }

            @Override
            public void AudioPermissionError() {
                Toast.makeText(EasyCameraActivity.this, "没有录音权限", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
        //JCameraView监听
        jCameraView.setJCameraListener(new JCameraListener() {
            @Override
            public void captureSuccess(Bitmap bitmap) {
                //获取图片bitmap
                String path = FileUtil.saveBitmap(applicationName, bitmap);
                Intent intent = new Intent();
                intent.putExtra(Key.EXTRA_RESULT_CAPTURE_IMAGE_PATH, path);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void recordSuccess(String url, Bitmap firstFrame) {
                //获取视频路径
                //String path = FileUtil.saveBitmap(applicationName, firstFrame);
                Intent intent = new Intent();
                //intent.putExtra(Key.EXTRA_RESULT_CAPTURE_IMAGE_PATH, path);
                intent.putExtra(Key.EXTRA_RESULT_CAPTURE_VIDEO_PATH, url);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        jCameraView.setLeftClickListener(new ClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });
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
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        } else {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        jCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        jCameraView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Setting.cameraCoverView = null;
    }
}
