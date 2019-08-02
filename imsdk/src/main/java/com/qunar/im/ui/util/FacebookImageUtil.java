package com.qunar.im.ui.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.base.util.FileUtils;

import java.io.File;

/**
 * Created by xingchao.song on 10/9/2015.
 */
public class FacebookImageUtil {

    private static final String TAG = FacebookImageUtil.class.getSimpleName();
    private static Handler handler = new Handler(Looper.getMainLooper());

    public interface GetBitampCallback {
        void onSuccess(Bitmap bitmap);
        void onFailure();
    }


    public static void loadLocalImage(File file, SimpleDraweeView target) {
        loadLocalImage(file, target, 0, 0);
    }

    public static void loadLocalImage(File file, SimpleDraweeView target,int widthResId, int heightResId) {
        loadLocalImage(file, target, widthResId, heightResId, false, EMPTY_CALLBACK);
    }
    public static void loadLocalImage(File file, final SimpleDraweeView target,int widthResId, int heightResId,boolean playAnim
            , final ImageLoadCallback callback)
    {
        if (target == null) return;

        Uri uri = FileUtils.toUri(file.getAbsolutePath());

        ControllerListener listener = new BaseControllerListener(){
            @Override
            public void onFinalImageSet(String id, Object imageInfo, Animatable animatable) {
                if(callback != null) {
                    callback.onSuccess();
                }
            }
            @Override
            public void onFailure(String id, Throwable throwable) {
                if(callback != null) {
                    callback.onError();
                }
            }

            @Override
            public void onIntermediateImageFailed(String id, Throwable throwable) {
                super.onIntermediateImageFailed(id, throwable);
            }
        };

        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(uri);
        builder.setProgressiveRenderingEnabled(true);

        if(widthResId>0&&heightResId>0)
        {
            builder.setResizeOptions(new ResizeOptions(widthResId, heightResId));
            builder.setLocalThumbnailPreviewsEnabled(true);
        }
        ImageRequest request = builder.build();
        target.setAspectRatio(1.0f);

        final PipelineDraweeControllerBuilder pipelineDraweeControllerBuilder = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setAutoPlayAnimations(playAnim)
                .setOldController(target.getController())
                .setControllerListener(listener);
        handler.post(new Runnable() {
            @Override
            public void run() {
                DraweeController controller = pipelineDraweeControllerBuilder.build();
                target.setController(controller);
            }
        });
    }
    public static void loadWithCache(String url,SimpleDraweeView target,boolean playAnim){
        loadWithCache(url, target, ImageRequest.CacheChoice.DEFAULT, playAnim);
    }
    public static void loadWithCache(String url,SimpleDraweeView target){
        loadWithCache(url, target, false);
    }

    public static void loadWithCache(String url, final SimpleDraweeView target,ImageRequest.CacheChoice type,boolean playAnim)
    {
        if (target == null) return;

        Uri uri = Uri.parse(url);
        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(uri);
        ImageRequest request = builder
                .setCacheChoice(type)
                .build();
        final PipelineDraweeControllerBuilder pipelineDraweeControllerBuilder = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setAutoPlayAnimations(playAnim)
                .setOldController(target.getController());
        handler.post(new Runnable() {
            @Override
            public void run() {
                DraweeController controller = pipelineDraweeControllerBuilder.build();
                target.setController(controller);
            }
        });

    }

    public static void loadWithCache(String url, final SimpleDraweeView target,
                                     boolean thumb, boolean isPlay,ImageRequest.CacheChoice type,final ImageLoadCallback callback)
    {
        ImageRequestBuilder builder =  ImageRequestBuilder.newBuilderWithSource(Uri.parse(url));
        if (target == null|| TextUtils.isEmpty(url)) return;
        if (thumb) {
            int indexW,indexH,width = 0,height = 0;
            if((indexW = url.indexOf("w=")) >0){
                for(int index = indexW + 2;index < url.length() && Character.isDigit(url.charAt(index));index++){
                    width *= 10;
                    width += Character.digit(url.charAt(index),10);
                }
            }
            if((indexH = url.indexOf("h=")) > 0){
                for(int index = indexH + 2;index < url.length() && Character.isDigit(url.charAt(index));index++){
                    height *= 10;
                    height += Character.digit(url.charAt(index),10);
                }
            }
            if(width != 0 && height != 0){

                builder =  builder .setResizeOptions(new ResizeOptions(width, height));
            }
        }
        final ImageRequest request = builder
                .setCacheChoice(type)
                .build();
        final ControllerListener listener = new BaseControllerListener(){
            @Override
            public void onFinalImageSet(String id, Object imageInfo, Animatable animatable) {
                if (callback != null) {
                    callback.onSuccess();
                }
            }
            @Override
            public void onFailure(String id, Throwable throwable) {
                if(callback != null) {
                    callback.onError();
                }
            }

            @Override
            public void onIntermediateImageFailed(String id, Throwable throwable) {
                super.onIntermediateImageFailed(id, throwable);
            }
        };
        final PipelineDraweeControllerBuilder pipelineDraweeControllerBuilderbuilder = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setControllerListener(listener)
                .setAutoPlayAnimations(isPlay)
                .setOldController(target.getController());
        handler.post(new Runnable() {
            @Override
            public void run() {
                DraweeController controller = pipelineDraweeControllerBuilderbuilder.build();
                target.setController(controller);
            }
        });
    }

    public static void loadWithCache(String url, final SimpleDraweeView target,
                                     boolean thumb, ImageRequest.CacheChoice type,final ImageLoadCallback callback) {
        loadWithCache(url,target,thumb,false,type,callback);
    }

    public static void loadWithCache(String url, SimpleDraweeView target,
                              boolean thumb, final ImageLoadCallback callback) {
        loadWithCache(url, target, thumb, ImageRequest.CacheChoice.DEFAULT, callback);
    }

    public static void loadFromResource(int resourceId, final SimpleDraweeView target)
    {
        ImageRequest request =  ImageRequestBuilder.newBuilderWithResourceId(resourceId).build();
        final PipelineDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(target.getController());
        handler.post(new Runnable() {
            @Override
            public void run() {
                DraweeController controller = builder.build();
                target.setController(controller);
            }
        });
    }

    /**
     * 对jif不起作用 ，bitmap的可用范围只有onNewResultImpl方法内， 该方法执行完bitmap就会被回收
     * @param imageUrl
     * @param context
     * @param callback
     */
    public static void getBitmapByUrl(String imageUrl, ImageRequest.CacheChoice type,final Context context, final GetBitampCallback callback) {
        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(imageUrl))
                .setCacheChoice(type)
                .build();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.evictFromMemoryCache(Uri.parse(imageUrl));
        DataSource dataSource = imagePipeline.fetchDecodedImage(request, context);
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
                                 @Override
                                 public void onNewResultImpl(Bitmap bitmap) {
                                     if (bitmap != null) {
                                         callback.onSuccess(bitmap);
                                     }
                                 }

                                 @Override
                                 public void onFailureImpl(DataSource dataSource) {
                                     callback.onFailure();
                                 }
                             },
                BackgroundExecutor.getExecutor());
    }


    public static void evictCache(String url)
    {
        Fresco.getImagePipeline().evictFromCache(Uri.parse(url));
    }

    public interface ImageLoadCallback {

        void onSuccess();

        void onError();

        class EmptyCallback implements ImageLoadCallback {

            @Override
            public void onSuccess() {
            }

            @Override
            public void onError() {
            }
        }

    }

    public static int[] computeSize(String srcImg) {
        int[] size = new int[2];

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;

        BitmapFactory.decodeFile(srcImg, options);
        size[0] = options.outWidth;
        size[1] = options.outHeight;

        return size;
    }

    public final static ImageLoadCallback EMPTY_CALLBACK = new ImageLoadCallback.EmptyCallback();
}
