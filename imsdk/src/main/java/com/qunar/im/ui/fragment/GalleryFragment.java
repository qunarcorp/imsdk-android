package com.qunar.im.ui.fragment;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.facebook.imagepipeline.request.ImageRequest;
import com.google.zxing.Result;
import com.orhanobut.logger.Logger;
import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.FileUtils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.protobuf.dispatch.DispatchHelper;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.ImagePageViewNewAdapter;
import com.qunar.im.ui.imagepicker.view.ViewPagerFixed;
import com.qunar.im.ui.imagepicker.zoomview.DragPhotoView;
import com.qunar.im.ui.imagepicker.zoomview.PhotoViewAttacher;
import com.qunar.im.ui.presenter.IBrowsingPresenter;
import com.qunar.im.ui.presenter.impl.BrowsingPresenter;
import com.qunar.im.ui.presenter.views.IBrowsingConversationImageView;
import com.qunar.im.ui.util.FacebookImageUtil;
import com.qunar.im.ui.util.QRRouter;
import com.qunar.im.ui.util.ShareUtil;
import com.qunar.im.ui.view.CommonDialog;
import com.qunar.im.ui.view.bigimageview.ImageBrowsUtil;
import com.qunar.im.ui.view.bigimageview.tool.utility.image.DownloadPictureUtil;
import com.qunar.im.ui.view.zxing.decode.DecodeBitmap;
import com.qunar.im.utils.QRUtil;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * Created by saber on 16-1-29.
 */
public class GalleryFragment extends BaseFragment implements IBrowsingConversationImageView {
    private static final int SAVE_TO_GALLERY = 0x1;
    private static final int SIGNOTIFICATE_QR_CODE = 0X3;

    boolean isLongClick =false;
    boolean isDraged =false;

//    ViewPager pager;
    ViewPagerFixed pager;
    TextView pageNum;
    String mImageUrl;
    String localPath;
    String converserId = "";
    String ofrom;
    String oto;
//    ImagePageViewAdapter imagePageViewAdapter;
    ImagePageViewNewAdapter imagePageViewAdapter;

    IBrowsingPresenter browsingPresenter;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        browsingPresenter = new BrowsingPresenter();
        browsingPresenter.setBrosingView(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        BackgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                browsingPresenter.loadImgsOfCurrentConversation();
            }
        }, "loadimage", null);
    }

    private void initAnimal(){
        pager.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        pager.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        mOriginLeft = getActivity().getIntent().getIntExtra("left", 0);
                        mOriginTop = getActivity().getIntent().getIntExtra("top", 0);
                        mOriginHeight = getActivity().getIntent().getIntExtra("height", 0);
                        mOriginWidth = getActivity().getIntent().getIntExtra("width", 0);
                        mOriginCenterX = mOriginLeft + mOriginWidth / 2;
                        mOriginCenterY = mOriginTop + mOriginHeight / 2;

                        int[] location = new int[2];
                        View view = imagePageViewAdapter.getPrimaryItem();
                        DragPhotoView photoView = null;//urls[fi];
                        if(view == null) return;
                        if(view instanceof DragPhotoView){
                            photoView = (DragPhotoView) view;
                        }
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

                        performEnterAnimation(photoView);

                    }
                });
    }

    @Override
    public void onStop()
    {
        BackgroundExecutor.cancelAll("loadimage",true);
        super.onStop();
    }

    View.OnCreateContextMenuListener longPressListener =  new View.OnCreateContextMenuListener() {
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(0, SAVE_TO_GALLERY, 0, R.string.atom_ui_menu_save_image);
            menu.add(0, SIGNOTIFICATE_QR_CODE, 0, R.string.atom_ui_menu_scan_qrcode);
            isLongClick = true;
        }
    };

    DragPhotoView.OnExitListener exitListener = new DragPhotoView.OnExitListener() {
        @Override
        public void onExit(DragPhotoView view, float translateX, float translateY, float w, float h, float scale) {
            if(getActivity() != null) {
//                getActivity().finish();
                finishWithAnimation(view);
            }
//            performExitAnimation(view, translateX, translateY, w, h, scale);
        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Logger.i("点击退出图片浏览GalleryFragment");
            getActivity().finish();
        }
    };


    View.OnTouchListener touchListener = new View.OnTouchListener() {
        private float x, y;
        private int mx, my;

        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isLongClick = false;
                    isDraged = false;
                    mx = 0;
                    my = 0;
                    x = event.getX();
                    y = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mx = (int) (event.getRawX() - x);
                    my = (int) (event.getRawY() - 50 - y);
                    if(mx > 10 || my > 10){
                        isDraged = true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if(mx < 10 && my < 10 && !isLongClick && !isDraged){
                        Logger.i("点击退出图片浏览GalleryFragment");
                        getActivity().finish();
                    }
                    break;
            }
            return false;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.atom_ui_fragment_gallery, container, false);
//        pager = (ViewPager) view.findViewById(R.id.image_gallery);
        pager = (ViewPagerFixed) view.findViewById(R.id.image_gallery);
        pageNum = (TextView) view.findViewById(R.id.page_num);
        mImageUrl = getArguments().getString(Constants.BundleKey.IMAGE_URL);
        localPath = getArguments().getString(Constants.BundleKey.IMAGE_ON_LOADING);
        converserId = getArguments().getString(Constants.BundleKey.CONVERSATION_ID);
        ofrom = getArguments().getString(Constants.BundleKey.ORIGIN_FROM);
        oto = getArguments().getString(Constants.BundleKey.ORIGIN_TO);
        return view;
    }



    @Override
    public String getConversationId() {
        return converserId;
    }

    @Override
    public String getOriginFrom() {
        return ofrom;
    }

    @Override
    public String getOriginTo() {
        return oto;
    }

    @Override
    public void setImageList(final List<PreImage> urls) {
        PreImage cur = new PreImage();
        cur.originUrl = mImageUrl;
        int curPage = urls.indexOf(cur);
        if(curPage == -1) {
            PreImage image = new PreImage();
            image.originUrl = mImageUrl;
            image.smallUrl = FileUtils.toUri(localPath).toString();
            urls.add(0,image);
            curPage = urls.size()-1;
        }
//        PreImage url = urls.get(curPage);
//        mImageUrl = url.originUrl;
        final int finalCurPage = curPage;
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                initViewPager();
                imagePageViewAdapter.setDatas(urls);
                pageNum.setText((finalCurPage + 1) + "/" + urls.size());
                pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        if(position>=urls.size()||position<0)return;
                        PreImage url = urls.get(position);
                        mImageUrl = url.originUrl;
                        pageNum.setText((position + 1) + "/" + urls.size());
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
                imagePageViewAdapter.notifyDataSetChanged();
                pager.setCurrentItem(finalCurPage,false);
                initAnimal();
            }
        });
    }

    private void initViewPager() {
        imagePageViewAdapter = new ImagePageViewNewAdapter(getActivity());
//        imagePageViewAdapter = new ImagePageViewAdapter(getContext());
        imagePageViewAdapter.setListener(longPressListener);
        imagePageViewAdapter.setOnExitListener(exitListener);
        imagePageViewAdapter.setOnLonglickListener(new View.OnLongClickListener() {
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
                return false;
            }
        });
//        imagePageViewAdapter.setOnTouchListener(touchListener);
        imagePageViewAdapter.setonFlingListener(new PhotoViewAttacher.OnFlingListener() {
            @Override
            public void onFlingExit(View view, float x, float y) {
                finishWithAnimation((DragPhotoView) view);
            }
        });
        imagePageViewAdapter.setOnTapClickListener(new DragPhotoView.OnTapListener() {
            @Override
            public void onTap(DragPhotoView view) {
//                getActivity().finish();
                finishWithAnimation(view);
            }
        });
        imagePageViewAdapter.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                finishWithAnimation(view);
            }
        });
//        imagePageViewAdapter.setOnClickListener(onClickListener);
        pager.setAdapter(imagePageViewAdapter);
        pager.setOffscreenPageLimit(3);

    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case SAVE_TO_GALLERY:
                DownloadPictureUtil.downloadPicture(getActivity(), mImageUrl, new DownloadPictureUtil.PicCallBack() {
                    @Override
                    public void onDownLoadSuccess(String str) {

                    }
                }, true);
                break;
            case SIGNOTIFICATE_QR_CODE:
                FacebookImageUtil.getBitmapByUrl(mImageUrl, ImageRequest.CacheChoice.DEFAULT, getContext(), new FacebookImageUtil.GetBitampCallback() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        String decode = QRUtil.cognitiveQR(bitmap);
                        if (TextUtils.isEmpty(decode)) {
                            Toast.makeText(getContext(), R.string.atom_ui_tip_parse_failed, Toast.LENGTH_SHORT).show();
                        } else {
                            QRRouter.handleQRCode(decode, getContext());
                            getActivity().finish();
                        }
                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(getContext(), R.string.atom_ui_tip_parse_failed, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
        return true;
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
        int wid = (int) view.getTag(R.id.tag_glide_image_w);
        int hei = (int) view.getTag(R.id.tag_glide_image_h);
        view.finishAnimationCallBack();
        float viewX = mTargetWidth / 2 + x - mTargetWidth * scale / 4;
        float viewY = mTargetHeight / 2 + y - mTargetHeight * scale / 4;
        view.setX(viewX);
        view.setY(viewY);
        float centerX = view.getX() + (mTargetWidth * scale) / 2;
        float centerY = view.getY() + (mTargetHeight * scale) / 2;

        float translateX = mOriginCenterX - centerX;
        float translateY = mOriginCenterY - centerY;


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
            ValueAnimator scaleYAnimator = ValueAnimator.ofFloat(scale, mOriginHeight / (hei * scale));
            scaleYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    view.setScaleY((Float) valueAnimator.getAnimatedValue());
                }
            });
            scaleYAnimator.setDuration(300);
            scaleYAnimator.start();

            ValueAnimator scaleXAnimator = ValueAnimator.ofFloat(scale, mOriginWidth / (wid * scale));
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