package com.qunar.im.ui.fragment;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.rastermill.FrameSequence;
import android.support.rastermill.FrameSequenceDrawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.facebook.imagepipeline.request.ImageRequest;
import com.qunar.im.base.common.FacebookImageUtil;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.FileUtils;
import com.qunar.im.base.util.Utils;
import com.qunar.im.base.util.graphics.ImageUtils;
import com.qunar.im.base.util.graphics.MyDiskCache;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.ImageBrowersingActivity;
import com.qunar.im.ui.imagepicker.zoomview.DragPhotoView;
import com.qunar.im.ui.imagepicker.zoomview.PhotoView;
import com.qunar.im.ui.imagepicker.zoomview.PhotoViewAttacher;
import com.qunar.im.ui.util.QRUtil;
import com.qunar.im.ui.view.CommonDialog;

import java.io.File;

public class ImageBroswingFragment extends BaseFragment {
    private static final int SAVE_TO_GALLERY = 0x1;
    private static final int SIGNOTIFICATE_QR_CODE = 0X3;
    private static final int CANCEL = 0x2;

    private int screenWidth;
    private int screenHeight;

    DragPhotoView mImageView;
    String mImageUrl;
    private Drawable placeHolderDrawable;

    File imageFile;
    String localPath;
    ImageBrowersingActivity parent;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parent = (ImageBrowersingActivity) context;
    }

    public static ImageBroswingFragment newInstance(Bundle args) {
        ImageBroswingFragment fragment = new ImageBroswingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.atom_ui_activity_image_browser, container, false);
        mImageView = (DragPhotoView) view.findViewById(R.id.my_image_view);

        mImageUrl = getArguments().getString(Constants.BundleKey.IMAGE_URL);
        localPath = getArguments().getString(Constants.BundleKey.IMAGE_ON_LOADING);
        imageFile = MyDiskCache.getFile(mImageUrl);
        File file = new File(localPath);
        if (!file.exists() || imageFile.getAbsolutePath().equals(localPath)) {
            placeHolderDrawable = this.getResources().getDrawable(R.drawable.atom_ui_sharemore_picture);
        }
        if (placeHolderDrawable == null) {
            placeHolderDrawable = Drawable.createFromPath(localPath);
        }
//        mImageView.setOnClickListener(onClickListener);
        mImageView.setOnExitListener(new DragPhotoView.OnExitListener() {
            @Override
            public void onExit(DragPhotoView view, float translateX, float translateY, float w, float h, float scale) {
                if(getActivity() != null){
//                    getActivity().finish();
                    finishWithAnimation(view);
                }
//                performExitAnimation(view, translateX, translateY, w, h, scale);
            }
        });
        mImageView.setOnFlingListenter(new PhotoViewAttacher.OnFlingListener() {
            @Override
            public void onFlingExit(View view, float x, float y) {
                finishWithAnimation((DragPhotoView) view);
//                    performExitAnimation((DragPhotoView) view, x, y, 0, 0, 0);
            }
        });
        DisplayMetrics dm = com.qunar.im.ui.imagepicker.util.Utils.getScreenPix(getActivity());
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setLargeImage();
        initAnimal();
    }

    private void initAnimal(){
        mImageView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        mOriginLeft = getActivity().getIntent().getIntExtra("left", 0);
                        mOriginTop = getActivity().getIntent().getIntExtra("top", 0);
                        mOriginHeight = getActivity().getIntent().getIntExtra("height", 0);
                        mOriginWidth = getActivity().getIntent().getIntExtra("width", 0);
                        mOriginCenterX = mOriginLeft + mOriginWidth / 2;
                        mOriginCenterY = mOriginTop + mOriginHeight / 2;

                        int[] location = new int[2];

                        final DragPhotoView photoView = mImageView;
                        photoView.getLocationOnScreen(location);

                        mTargetHeight = (float) photoView.getHeight();
                        mTargetWidth = (float) photoView.getWidth();
                        mScaleX = (float) mOriginWidth / mTargetWidth;
                        mScaleY = (float) mOriginHeight / mTargetHeight;

                        float targetCenterX = location[0] + mTargetWidth / 2;
                        float targetCenterY = location[1] + mTargetHeight / 2;

                        mTranslationX = mOriginCenterX - targetCenterX;
                        mTranslationY = mOriginCenterY - targetCenterY;
                        photoView.setTranslationX(mTranslationX);
                        photoView.setTranslationY(mTranslationY);

                        photoView.setScaleX(mScaleX);
                        photoView.setScaleY(mScaleY);

                        performEnterAnimation(mImageView);

                    }
                });
    }

    @SuppressWarnings("unchecked")
    private void setLargeImage() {
        if (Utils.isGifUrl(mImageUrl)) {
//          //glide 3+
            Glide.with(getActivity())                             //配置上下文
                    .load(mImageUrl)      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    .asGif()
                    .toBytes()
//                    .error(R.drawable.atom_ui_ic_default_image)           //设置错误图片
//                    .placeholder(R.drawable.atom_ui_ic_default_image)     //设置占位图片
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                    .dontAnimate()
                    .into(new ViewTarget<PhotoView, byte[]>(mImageView) {
                        @Override
                        public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
//                            try {
                            FrameSequence fs = FrameSequence.decodeByteArray(resource);
                            FrameSequenceDrawable drawable = new FrameSequenceDrawable(fs);
                            view.setImageDrawable(drawable);
                        }

                    });
        } else {
            //glide 3+
            Glide.with(getActivity())                             //配置上下文
                    .load(mImageUrl)      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    .error(R.drawable.atom_ui_ic_default_image)           //设置错误图片
                    .placeholder(R.drawable.atom_ui_ic_default_image)     //设置占位图片
                    .thumbnail(Glide.with(getActivity()).load(mImageUrl))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                    .override(screenHeight, screenHeight)
                    .dontAnimate()
                    .into(mImageView);
        }

        mImageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                finishWithAnimation(mImageView);
            }
        });

        mImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                commonDialog.setItems(getActivity().getResources().getStringArray(R.array.atom_ui_image_menu)).setOnItemClickListener(new CommonDialog.Builder.OnItemClickListener() {
                    @Override
                    public void OnItemClickListener(Dialog dialog, int postion) {
                        switch (postion){
                            case 0:
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        savePicture();
                                    }
                                }).start();
                                break;
                            case 1:
                                FacebookImageUtil.getBitmapByUrl(mImageUrl, ImageRequest.CacheChoice.DEFAULT, getContext(), new FacebookImageUtil.GetBitampCallback() {
                                    @Override
                                    public void onSuccess(Bitmap bitmap) {
                                        String decode = QRUtil.cognitiveQR(bitmap);
                                        if (TextUtils.isEmpty(decode)) {
                                            Toast.makeText(getContext(), R.string.atom_ui_tip_parse_failed, Toast.LENGTH_SHORT).show();
                                        } else {
                                            QRUtil.handleQRCode(decode, getContext());
                                            if(getActivity() != null){
                                                getActivity().finish();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure() {
                                        Toast.makeText(getContext(), R.string.atom_ui_tip_parse_failed, Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                        }
                    }
                }).create().show();
                return true;
            }
        });
        mImageView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, SAVE_TO_GALLERY, 0, getString(R.string.atom_ui_menu_save_image));
                menu.add(0, SIGNOTIFICATE_QR_CODE, 0, getString(R.string.atom_ui_menu_scan_qrcode));
                menu.add(0, CANCEL, 0, getString(R.string.atom_ui_common_cancel));
            }
        });
//        mImageView.setmHomeActvity(parent);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case SAVE_TO_GALLERY:
                savePicture();
                break;
            case SIGNOTIFICATE_QR_CODE:
                FacebookImageUtil.getBitmapByUrl(mImageUrl, ImageRequest.CacheChoice.DEFAULT, parent, new FacebookImageUtil.GetBitampCallback() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        String decode = QRUtil.cognitiveQR(bitmap);
                        if (TextUtils.isEmpty(decode)) {
                            Toast.makeText(parent, R.string.atom_ui_tip_parse_failed, Toast.LENGTH_SHORT).show();
                        } else {
                            QRUtil.handleQRCode(decode, parent);
                            parent.finish();
                        }
                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(parent, R.string.atom_ui_tip_parse_failed, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case CANCEL:
                break;
        }
        return true;
    }

    private void savePicture() {
        if (imageFile!=null&&imageFile.exists()) {
            byte[] bytes = FileUtils.toByteArray(imageFile, 4);//new byte[4];
            ImageUtils.ImageType type = ImageUtils.adjustImageType(bytes);
            if(type == ImageUtils.ImageType.GIF)
            {
                ImageUtils.saveToGallery(getContext(),imageFile,true);
            }
            else {
                ImageUtils.saveToGallery(getContext(),imageFile,false);
            }
        }
    }
    /**-------------------进出场动画-------------------*/
    int mOriginLeft;
    int mOriginTop;
    int mOriginHeight;
    int mOriginWidth;
    int mOriginCenterX;
    int mOriginCenterY;
    private float mTargetHeight;
    private float mTargetWidth;
    private float mScaleX;
    private float mScaleY;
    private float mTranslationX;
    private float mTranslationY;

    private void performExitAnimation(final DragPhotoView view, float x, float y, float w, float h, float scale) {
        view.finishAnimationCallBack();
        float viewX = mTargetWidth / 2 + x - mTargetWidth * mScaleX / 2;
        float viewY = mTargetHeight / 2 + y - mTargetHeight * mScaleY / 2;
        view.setX(viewX);
        view.setY(viewY);
        int wid = (int) view.getTag(R.id.tag_glide_image_w);
        int hei = (int) view.getTag(R.id.tag_glide_image_h);
        float centerX = view.getX() + mOriginWidth / 2;
        float centerY = view.getY() + mOriginHeight / 2;

        float translateX = mOriginCenterX - viewX;//centerX;
        float translateY = mOriginCenterY - viewY;//centerY;


        ValueAnimator translateXAnimator = ValueAnimator.ofFloat(view.getX(), view.getX() + translateX);
        translateXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setX((Float) valueAnimator.getAnimatedValue());
            }
        });
        translateXAnimator.setDuration(300);
        translateXAnimator.start();
        ValueAnimator translateYAnimator = ValueAnimator.ofFloat(view.getY(), view.getY() + translateY);
        translateYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setY((Float) valueAnimator.getAnimatedValue());
            }
        });
        translateYAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animator.removeAllListeners();
                if(getActivity() != null){
                    getActivity().finish();
                    getActivity().overridePendingTransition(0, 0);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        translateYAnimator.setDuration(300);
        translateYAnimator.start();

        if(scale > 0 && hei > 0 && wid > 0){
            //缩放动画
            ValueAnimator scaleYAnimator = ValueAnimator.ofFloat(1, mOriginHeight / (hei * scale));
            scaleYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    view.setScaleY((Float) valueAnimator.getAnimatedValue());
                }
            });
            scaleYAnimator.setDuration(300);
            scaleYAnimator.start();

            ValueAnimator scaleXAnimator = ValueAnimator.ofFloat(1, mOriginWidth / (wid * scale));
            scaleXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    view.setScaleX((Float) valueAnimator.getAnimatedValue());
                }
            });

            scaleXAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    animator.removeAllListeners();
                    if(getActivity() != null){
                        getActivity().finish();
                        getActivity().overridePendingTransition(0, 0);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            scaleXAnimator.setDuration(300);
            scaleXAnimator.start();
        }
    }

    private void finishWithAnimation(final View photoView) {
//        final DragPhotoView photoView = mPhotoViews[0];
        ValueAnimator translateXAnimator = ValueAnimator.ofFloat(0, mTranslationX);
        translateXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                photoView.setX((Float) valueAnimator.getAnimatedValue());
            }
        });
        translateXAnimator.setDuration(300);
        translateXAnimator.start();

        ValueAnimator translateYAnimator = ValueAnimator.ofFloat(0, mTranslationY);
        translateYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                photoView.setY((Float) valueAnimator.getAnimatedValue());
            }
        });
        translateYAnimator.setDuration(300);
        translateYAnimator.start();

        ValueAnimator scaleYAnimator = ValueAnimator.ofFloat(1, mScaleY);
        scaleYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                photoView.setScaleY((Float) valueAnimator.getAnimatedValue());
            }
        });
        scaleYAnimator.setDuration(300);
        scaleYAnimator.start();

        ValueAnimator scaleXAnimator = ValueAnimator.ofFloat(1, mScaleX);
        scaleXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                photoView.setScaleX((Float) valueAnimator.getAnimatedValue());
            }
        });

        scaleXAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animator.removeAllListeners();
                if(getActivity() != null){
                    getActivity().finish();
                    getActivity().overridePendingTransition(0, 0);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        scaleXAnimator.setDuration(300);
        scaleXAnimator.start();
    }

    private void performEnterAnimation(final DragPhotoView photoView) {
//        final DragPhotoView photoView = mPhotoViews[0];
        ValueAnimator translateXAnimator = ValueAnimator.ofFloat(photoView.getX(), 0);
        translateXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                photoView.setX((Float) valueAnimator.getAnimatedValue());
            }
        });
        translateXAnimator.setDuration(300);
        translateXAnimator.start();

        ValueAnimator translateYAnimator = ValueAnimator.ofFloat(photoView.getY(), 0);
        translateYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                photoView.setY((Float) valueAnimator.getAnimatedValue());
            }
        });
        translateYAnimator.setDuration(300);
        translateYAnimator.start();

        ValueAnimator scaleYAnimator = ValueAnimator.ofFloat(mScaleY, 1);
        scaleYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                photoView.setScaleY((Float) valueAnimator.getAnimatedValue());
            }
        });
        scaleYAnimator.setDuration(300);
        scaleYAnimator.start();

        ValueAnimator scaleXAnimator = ValueAnimator.ofFloat(mScaleX, 1);
        scaleXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                photoView.setScaleX((Float) valueAnimator.getAnimatedValue());
            }
        });
        scaleXAnimator.setDuration(300);
        scaleXAnimator.start();
    }
}
