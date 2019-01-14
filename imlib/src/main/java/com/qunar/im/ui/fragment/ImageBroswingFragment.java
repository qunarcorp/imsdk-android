package com.qunar.im.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.rastermill.FrameSequence;
import android.support.rastermill.FrameSequenceDrawable;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.facebook.imagepipeline.request.ImageRequest;
import com.google.zxing.Result;
import com.qunar.im.base.common.FacebookImageUtil;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.FileUtils;
import com.qunar.im.base.util.Utils;
import com.qunar.im.base.util.graphics.ImageUtils;
import com.qunar.im.protobuf.dispatch.DispatchHelper;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.ImageBrowersingActivity;
import com.qunar.im.ui.imagepicker.util.ProviderUtil;
import com.qunar.im.ui.imagepicker.zoomview.DragPhotoView;
import com.qunar.im.ui.imagepicker.zoomview.PhotoView;
import com.qunar.im.ui.imagepicker.zoomview.PhotoViewAttacher;
import com.qunar.im.ui.util.QRRouter;
import com.qunar.im.ui.view.CommonDialog;
import com.qunar.im.ui.view.zxing.decode.DecodeBitmap;
import com.qunar.im.utils.QRUtil;

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
        if (Utils.isGifUrl(mImageUrl)) {
//          //glide 3+
            Glide.with(getActivity())                             //配置上下文
                    .load(mImageUrl)      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    .asGif()
                    .toBytes()
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
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
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
                                DispatchHelper.Async("saveImageToLocal", true,new Runnable() {
                                    @Override
                                    public void run() {
                                        savePicture();
                                    }
                                });
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
                                DispatchHelper.Async("saveImageToLocal", true,new Runnable() {
                                    @Override
                                    public void run() {
                                        File file = savePicture();
                                        if(file != null){
                                            externalShare(file);
                                        }
                                    }
                                });
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

    private File savePicture() {
        File imageFile = null;
        try {
            imageFile = Glide.with(getActivity())
                    .load(mImageUrl)
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if(imageFile!=null&&imageFile.exists())
        {
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

        return imageFile;
    }

    private void externalShare(File file){
        Intent share_intent = new Intent();
        share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
        share_intent.setType("image/*");  //设置分享内容的类型
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            share_intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(getActivity(), ProviderUtil.getFileProviderName(getActivity()), file);//android 7.0以上
        }else {
            uri = Uri.fromFile(file);
        }
        share_intent.putExtra(Intent.EXTRA_STREAM, uri);
        //创建分享的Dialog
        share_intent = Intent.createChooser(share_intent, "分享");
        startActivity(share_intent);
    }

    private void finishWithAnimation() {
        if(getActivity() != null){
            getActivity().finish();
            return;
        }
    }
}
