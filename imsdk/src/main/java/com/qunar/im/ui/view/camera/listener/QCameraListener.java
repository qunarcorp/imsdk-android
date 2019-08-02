package com.qunar.im.ui.view.camera.listener;

import android.graphics.Bitmap;

public interface QCameraListener {

    void captureSuccess(Bitmap bitmap);

    void recordSuccess(String url, Bitmap firstFrame);

}
