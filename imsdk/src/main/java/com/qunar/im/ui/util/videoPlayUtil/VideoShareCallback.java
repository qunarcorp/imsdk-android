package com.qunar.im.ui.util.videoPlayUtil;

import android.view.View;

public interface VideoShareCallback {

    void onClickShare(View v,String playPath, String downloadPath, String fileName);
}
