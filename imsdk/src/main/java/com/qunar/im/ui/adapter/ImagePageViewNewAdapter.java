package com.qunar.im.ui.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.rastermill.FrameSequence;
import android.support.rastermill.FrameSequenceDrawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.qunar.im.ui.presenter.views.IBrowsingConversationImageView;
import com.qunar.im.base.util.MemoryCache;
import com.qunar.im.base.util.Utils;
import com.qunar.im.ui.R;
import com.qunar.im.ui.imagepicker.zoomview.DragPhotoView;
import com.qunar.im.ui.imagepicker.zoomview.PhotoViewAttacher;
import com.qunar.im.ui.view.progress.CircleProgress;
import com.qunar.im.ui.view.progress.glide.ProgressModelLoader;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ImagePageViewNewAdapter extends PagerAdapter {

    List<IBrowsingConversationImageView.PreImage> images = new ArrayList<>();
    private Activity mActivity;
    public DragPhotoView.OnTapListener mOnTapListener;
    public PhotoViewAttacher.OnPhotoTapListener mOnPhotoTapListener;

    private int screenWidth;
    private int screenHeight;
    private View.OnLongClickListener onLonglickListener;
    private DragPhotoView.OnExitListener onExitListener;
    private PhotoViewAttacher.OnFlingListener onFlingListener;

    public void setListener(View.OnCreateContextMenuListener listener) {
        this.menulistener = listener;
    }

    private View.OnCreateContextMenuListener menulistener = null;

    public ImagePageViewNewAdapter(Activity activity){
        this.mActivity = activity;
        initWidthHeight(this.mActivity);
    }

    public ImagePageViewNewAdapter(Activity activity, List<IBrowsingConversationImageView.PreImage> images) {
        this.mActivity = activity;
        this.images = images;
        initWidthHeight(activity);
    }

    public void initWidthHeight(Activity activity){
        if(activity == null){
            screenWidth = 720;
            screenHeight = 1280;
            return;
        }
        DisplayMetrics dm = com.qunar.im.ui.imagepicker.util.Utils.getScreenPix(activity);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
    }

    public void setDatas(List<IBrowsingConversationImageView.PreImage> images) {
        this.images = images;
    }

    public void setOnTapClickListener(DragPhotoView.OnTapListener listener) {
        this.mOnTapListener = listener;
    }
public void setOnPhotoTapListener(PhotoViewAttacher.OnPhotoTapListener listener) {
        this.mOnPhotoTapListener = listener;
    }

    @SuppressLint("HandlerLeak")
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if(screenWidth == 0 || screenHeight == 0){
            initWidthHeight(mActivity);
        }
        final DragPhotoView photoView = (DragPhotoView) View.inflate(mActivity, R.layout.atom_ui_viewpager_image_item, null);
        IBrowsingConversationImageView.PreImage imageItem = images.get(position);
        CircleProgress.Builder builder = new CircleProgress.Builder();
        final CircleProgress circleProgress = builder.build();
        circleProgress.inject(photoView);
        final String url = TextUtils.isEmpty(imageItem.localPath) ? imageItem.originUrl : imageItem.localPath;
        if(imageItem.height!=0&&imageItem.width!=0){
            photoView.setLayoutParams(new LinearLayout.LayoutParams(imageItem.width,imageItem.height));
        }else{
            photoView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        if (Utils.isGifUrl(url)) {
//          //glide 3+
            Glide.with(mActivity)                             //配置上下文
                    .using(new ProgressModelLoader(new Handler(){//添加加载进度条
                        @Override
                        public void handleMessage(Message msg) {
                            circleProgress.setLevel(msg.arg1);
                            circleProgress.setMaxValue(msg.arg2);
                        }
                    }))
                    .load(url)      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    .asGif()
                    .toBytes()
//                    .error(R.drawable.atom_ui_ic_default_image)           //设置错误图片
//                    .placeholder(R.drawable.atom_ui_ic_default_image)     //设置占位图片

                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)//缓存全尺寸
                    .dontAnimate()
                    .into(new ViewTarget<DragPhotoView, byte[]>(photoView) {
                        @Override
                        public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
                            WeakReference<Parcelable> cached = new WeakReference<>(MemoryCache.getMemoryCache(url));
                            if(cached.get() == null){
                                FrameSequence fs = FrameSequence.decodeByteArray(resource);
                                if(fs != null){
                                    FrameSequenceDrawable drawable = new FrameSequenceDrawable(fs);
                                    drawable.setByteCount(resource.length);
                                    view.setImageDrawable(drawable);
                                    MemoryCache.addObjToMemoryCache(url,drawable);
                                }
                            }else {
                                if(cached.get() instanceof FrameSequenceDrawable)
                                    view.setImageDrawable((FrameSequenceDrawable)cached.get());
                            }
                        }

                    });
        } else {
            //glide 3+
            Glide.with(mActivity)                             //配置上下文
                    .using(new ProgressModelLoader(new Handler(){//添加加载进度条
                        @Override
                        public void handleMessage(Message msg) {
                            circleProgress.setLevel(msg.arg1);
                            circleProgress.setMaxValue(msg.arg2);
                        }
                    }))
                    .load(url)      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    .error(R.drawable.atom_ui_ic_default_image)           //设置错误图片
                    .placeholder(R.drawable.atom_ui_ic_default_image)     //设置占位图片
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)//缓存全尺寸
                    .override(screenWidth, screenHeight)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            photoView.setTag(R.id.tag_glide_image_w, resource.getIntrinsicWidth());
                            photoView.setTag(R.id.tag_glide_image_h, resource.getIntrinsicHeight());
                            return false;
                        }
                    })
                    .dontAnimate()
                    .into(photoView);
        }
//        Drawable defaultDrawable = mActivity.getResources().getDrawable(R.drawable.ic_gf_default_photo);//defaultDrawable,
//        imagePicker.getImageLoader().displayImage(mActivity, imageItem.path, photoView, screenWidth, screenHeight);
        photoView.setOnLongClickListener(onLonglickListener);
        photoView.setOnExitListener(onExitListener);
        photoView.setOnFlingListenter(onFlingListener);
        if(menulistener != null){
            photoView.setOnCreateContextMenuListener(menulistener);
        }
//        photoView.setOnTapListener(mOnTapListener);
        photoView.setOnPhotoTapListener(mOnPhotoTapListener);
        photoView.setOnDoubleTapListener(null);
        container.addView(photoView);
        return photoView;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
    private View mCurrentView;

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        mCurrentView = (View)object;
    }

    public View getPrimaryItem() {
        return mCurrentView;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void setOnLonglickListener(View.OnLongClickListener onLonglickListener) {
        this.onLonglickListener = onLonglickListener;
    }

    public void setOnExitListener(DragPhotoView.OnExitListener onExitListener) {
        this.onExitListener = onExitListener;
    }

    public void setonFlingListener(PhotoViewAttacher.OnFlingListener onFlingListener) {
        this.onFlingListener = onFlingListener;
    }
}
