package com.qunar.im.ui.imagepicker.loader;

import android.app.Activity;
import android.net.Uri;
import android.support.rastermill.FrameSequence;
import android.support.rastermill.FrameSequenceDrawable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.qunar.im.ui.R;

import java.io.File;


/**
 *
 */
public class GlideImageLoader implements ImageLoader {

    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
        //glide 4+
//        RequestOptions requestOptions = new RequestOptions()
//                .error(R.drawable.atom_ui_sharemore_picture)//设置错误图片
//                .placeholder(R.drawable.atom_ui_sharemore_picture)//设置占位图片
//                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
//                .dontAnimate();
//        Glide.with(activity)                             //配置上下文
//                .load(Uri.fromFile(new File(path)))      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
//                .apply(requestOptions)
//                .into(imageView/*new GlideDrawableImageViewTarget(imageView, 0)*/);
        //glide 3+
        Glide.with(activity)                             //配置上下文
                .load(Uri.fromFile(new File(path)))      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                .asBitmap()
                .override(width, height)
                .error(R.drawable.atom_ui_ic_default_image)           //设置错误图片
                .placeholder(R.drawable.atom_ui_ic_default_image)     //设置占位图片
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)//缓存全尺寸
                .into(imageView);
    }

    @Override
    public void displaygGif(Activity activity, String path, ImageView imageView, int width, int height) {
        if(TextUtils.isEmpty(path)){
            com.orhanobut.logger.Logger.i("图片崩溃错误2");
            return;
        }
        Glide.with(activity)                             //配置上下文
                .load(path)
//                .load(new MyGlideUrl(path))      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                .asGif()
                .toBytes()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)//缓存全尺寸
                .dontAnimate()
                .into(new ViewTarget<ImageView, byte[]>(imageView) {
                    @Override
                    public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
//                            try {
                        FrameSequence fs = FrameSequence.decodeByteArray(resource);
                        FrameSequenceDrawable drawable = new FrameSequenceDrawable(fs);
                        view.setImageDrawable(drawable);
                    }
                });
    }

    @Override
    public void displayImagePreview(Activity activity, String path, ImageView imageView, int width, int height) {
        //glide 4+
//        RequestOptions requestOptions = new RequestOptions()
//                .error(R.drawable.atom_ui_sharemore_picture)//设置错误图片
//                .placeholder(R.drawable.atom_ui_sharemore_picture)//设置占位图片
//                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
//                .dontAnimate();
//        Glide.with(activity)                             //配置上下文
//                .load(Uri.fromFile(new File(path)))      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
//                .apply(requestOptions)
//                .into(imageView);
        //glide 3+
        Glide.with(activity)                             //配置上下文
                .load(Uri.fromFile(new File(path)))      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                .asBitmap()
                .override(width, height)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)//缓存全尺寸
                .into(imageView);
    }

    @Override
    public void clearMemoryCache() {
    }
}
