package com.qunar.im.ui.view.facebookimageview;

/**
 * Created by xingchao.song on 10/10/2015.
 */
public interface ImageLoadCallback {

    void onSuccess();

    void onError();

    public static class EmptyCallback implements ImageLoadCallback {

        @Override
        public void onSuccess() {
        }

        @Override
        public void onError() {
        }
    }

}
