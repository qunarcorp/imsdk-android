package com.qunar.im.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.rastermill.FrameSequence;
import android.support.rastermill.FrameSequenceDrawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.google.zxing.Result;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.Utils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.protobuf.dispatch.DispatchHelper;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.ImageBrowersingActivity;
import com.qunar.im.ui.imagepicker.zoomview.DragPhotoView;
import com.qunar.im.ui.imagepicker.zoomview.PhotoView;
import com.qunar.im.ui.imagepicker.zoomview.PhotoViewAttacher;
import com.qunar.im.ui.util.QRRouter;
import com.qunar.im.ui.util.ShareUtil;
import com.qunar.im.ui.view.CommonDialog;
import com.qunar.im.ui.view.bigimageview.ImageBrowsUtil;
import com.qunar.im.ui.view.bigimageview.tool.utility.image.DownloadPictureUtil;
import com.qunar.im.ui.view.zxing.decode.DecodeBitmap;

import java.io.File;
import java.util.concurrent.ExecutionException;

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
//        mImageView.setOnClickListener(onClickListener);
        mImageView.setOnExitListener(new DragPhotoView.OnExitListener() {
            @Override
            public void onExit(DragPhotoView view, float translateX, float translateY, float w, float h, float scale) {
                if(getActivity() != null){
//                    getActivity().finish();
                    finishWithAnimation();
                }
            }
        });
        mImageView.setOnFlingListenter(new PhotoViewAttacher.OnFlingListener() {
            @Override
            public void onFlingExit(View view, float x, float y) {
                finishWithAnimation();
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
    }

    @SuppressWarnings("unchecked")
    private void setLargeImage() {
        if(TextUtils.isEmpty(mImageUrl)){
            com.orhanobut.logger.Logger.i("图片崩溃错误3");
            return;
        }
        if (Utils.isGifUrl(mImageUrl)) {
//          //glide 3+
            Glide.with(getActivity())                             //配置上下文
                    .load(mImageUrl)
//                    .load(new MyGlideUrl(mImageUrl))      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    .asGif()
                    .toBytes()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)//缓存全尺寸
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
            Glide.with(getActivity())
                    //配置上下文
                    .load(mImageUrl)
//                    .load(new MyGlideUrl(mImageUrl))      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    .error(R.drawable.atom_ui_ic_default_image)           //设置错误图片
                    .placeholder(R.drawable.atom_ui_ic_default_image)     //设置占位图片
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)//缓存全尺寸
                    .override(screenHeight, screenHeight)
                    .dontAnimate()
                    .into(mImageView);
        }

        mImageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                finishWithAnimation();
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
                                DownloadPictureUtil.downloadPicture(getActivity(), mImageUrl, new DownloadPictureUtil.PicCallBack() {
                                    @Override
                                    public void onDownLoadSuccess(String str) {

                                    }
                                }, true);
                                break;
                            case 1:
                                DispatchHelper.Async("scanQrcode", true, new Runnable() {
                                    @Override
                                    public void run() {
                                        scanQRCode();
                                    }
                                });
                                break;
                            case 2:
                                DownloadPictureUtil.downloadPicture(getActivity(), mImageUrl, new DownloadPictureUtil.PicCallBack() {
                                    @Override
                                    public void onDownLoadSuccess(String str) {
                                        File file = new File(str);

                                        if (file != null && file.exists()) {
//                                            ImageBrowsUtil.externalShare(file, CommonConfig.globalContext);
                                            ShareUtil.shareImage(CommonConfig.globalContext,file,"分享图片");
                                        }
                                    }
                                }, false);
                                break;
                        }
                    }
                }).create().show();
                return true;
            }
        });
    }

    private void scanQRCode() {
        try {
            File imageFile = Glide.with(getActivity())
//                    .load(new MyGlideUrl(mImageUrl))
                    .load(mImageUrl)
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();
            Result result = DecodeBitmap.scanningImage(imageFile.getPath());
            if (result == null) {

            }else{
                QRRouter.handleQRCode(result.toString(), getContext());
                getActivity().finish();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    private void finishWithAnimation() {
        if(getActivity() != null){
            getActivity().finish();
            return;
        }
    }
}
