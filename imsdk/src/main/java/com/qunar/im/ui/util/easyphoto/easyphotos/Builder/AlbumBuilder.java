package com.qunar.im.ui.util.easyphoto.easyphotos.Builder;

import android.app.Activity;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.View;

import com.qunar.im.ui.util.easyphoto.easyphotos.callback.SelectCallback;
import com.qunar.im.ui.util.easyphoto.easyphotos.callback.VideoPlayCallback;
import com.qunar.im.ui.util.easyphoto.easyphotos.constant.Capture;
import com.qunar.im.ui.util.easyphoto.easyphotos.constant.Type;
import com.qunar.im.ui.util.easyphoto.easyphotos.engine.ImageEngine;
import com.qunar.im.ui.util.easyphoto.easyphotos.models.ad.AdListener;
import com.qunar.im.ui.util.easyphoto.easyphotos.models.album.entity.Photo;
import com.qunar.im.ui.util.easyphoto.easyphotos.result.Result;
import com.qunar.im.ui.util.easyphoto.easyphotos.setting.Setting;
import com.qunar.im.ui.util.easyphoto.easyphotos.utils.result.EasyResult;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * EasyPhotos的启动管理器
 * Created by huan on 2017/10/18.
 */
public class AlbumBuilder {

    /**
     * 启动模式
     * CAMERA-相机
     * ALBUM-相册专辑
     * ALBUM_CAMERA-带有相机按钮的相册专辑
     */
    private enum StartupType {
        CAMERA, ALBUM, ALBUM_CAMERA
    }

    private static AlbumBuilder instance;
    private WeakReference<Activity> mActivity;
    private WeakReference<Fragment> mFragmentV;
    private StartupType startupType;
    private WeakReference<AdListener> adListener;

    //私有构造函数，不允许外部调用，真正实例化通过静态方法实现
    private AlbumBuilder(FragmentActivity activity, StartupType startupType) {
        mActivity = new WeakReference<Activity>(activity);
        this.startupType = startupType;
    }

    private AlbumBuilder(Fragment fragment, StartupType startupType) {
        mFragmentV = new WeakReference<Fragment>(fragment);
        this.startupType = startupType;
    }

    /**
     * 内部处理相机和相册的实例
     *
     * @param activity Activity的实例
     * @return AlbumBuilder EasyPhotos的实例
     */
    private static AlbumBuilder with(FragmentActivity activity, StartupType startupType) {
        clear();
        instance = new AlbumBuilder(activity, startupType);
        return instance;
    }

    private static AlbumBuilder with(Fragment fragmentV, StartupType startupType) {
        clear();
        instance = new AlbumBuilder(fragmentV, startupType);
        return instance;
    }


    /**
     * 创建相机
     *
     * @param activity 上下文
     * @return AlbumBuilder
     */
    public static AlbumBuilder createCamera(FragmentActivity activity) {
        return AlbumBuilder.with(activity, StartupType.CAMERA);
    }

    public static AlbumBuilder createCamera(Fragment fragmentV) {
        return AlbumBuilder.with(fragmentV, StartupType.CAMERA);
    }

    /**
     * 创建相册
     *
     * @param activity     上下文
     * @param isShowCamera 是否显示相机按钮
     * @param imageEngine  图片加载引擎的具体实现
     * @return
     */
    public static AlbumBuilder createAlbum(FragmentActivity activity, boolean isShowCamera, @NonNull ImageEngine imageEngine) {
        if (Setting.imageEngine != imageEngine) {
            Setting.imageEngine = imageEngine;
        }
        if (isShowCamera) {
            return AlbumBuilder.with(activity, StartupType.ALBUM_CAMERA);
        } else {
            return AlbumBuilder.with(activity, StartupType.ALBUM);
        }
    }

    public static AlbumBuilder createAlbum(Fragment fragmentV, boolean isShowCamera, @NonNull ImageEngine imageEngine) {
        if (Setting.imageEngine != imageEngine) {
            Setting.imageEngine = imageEngine;
        }
        if (isShowCamera) {
            return AlbumBuilder.with(fragmentV, StartupType.ALBUM_CAMERA);
        } else {
            return AlbumBuilder.with(fragmentV, StartupType.ALBUM);
        }
    }

    /**
     * 设置选择数
     *
     * @param selectorMaxCount 最大选择数
     * @return AlbumBuilder
     */
    public AlbumBuilder setCount(int selectorMaxCount) {
        Setting.count = selectorMaxCount;
        return AlbumBuilder.this;
    }

    /**
     * 设置选择图片数(设置此参数后setCount失效)
     *
     * @param pictureCount 图片最大选择数
     * @return AlbumBuilder
     */
    public AlbumBuilder setPictureCount(int pictureCount) {
        Setting.pictureCount = pictureCount;
        return AlbumBuilder.this;
    }

    /**
     * 设置选择视频数(设置此参数后setCount失效)
     *
     * @param videoCount 视频最大选择数
     * @return AlbumBuilder
     */
    public AlbumBuilder setVideoCount(int videoCount) {
        Setting.videoCount = videoCount;
        return AlbumBuilder.this;
    }

    /**
     * 设置相机按钮位置
     *
     * @param cLocation 使用Material Design风格相机按钮 默认 BOTTOM_RIGHT
     * @return AlbumBuilder
     */
    public AlbumBuilder setCameraLocation(@Setting.Location int cLocation) {
        Setting.cameraLocation = cLocation;
        return AlbumBuilder.this;
    }

    /**
     * 设置显示的最小文件大小
     *
     * @param minFileSize 最小文件大小，单位Bytes
     * @return AlbumBuilder
     */
    public AlbumBuilder setMinFileSize(long minFileSize) {
        Setting.minSize = minFileSize;
        return AlbumBuilder.this;
    }

    /**
     * 设置显示的最大文件大小
     *
     * @param maxFileSize 最小文件大小，单位Bytes
     * @return AlbumBuilder
     */
    public AlbumBuilder setMaxFileSize(long maxFileSize) {
        Setting.maxSize = maxFileSize;
        return AlbumBuilder.this;
    }

    /**
     * 设置图片和视频选择是否相互排斥
     *
     * @param exclusion 默认false
     * @return AlbumBuilder
     */
    public AlbumBuilder setSelectMutualExclusion(boolean exclusion) {
        Setting.selectMutualExclusion = exclusion;
        return AlbumBuilder.this;
    }

    /**
     * 是否分开计算video与pic 的个数
     * @param isDistinguish
     * @return
     */
    public AlbumBuilder setIsDistinguish(boolean isDistinguish){
        Setting.isDistinguish = isDistinguish;
        return AlbumBuilder.this;
    }

    /**
     * 是否需要自定义的video播放器
     * @param videoPlayCallback
     * @return
     */
    public AlbumBuilder setVideoPlayModule(VideoPlayCallback videoPlayCallback){
        Setting.videoPlayCallback = videoPlayCallback;
        return AlbumBuilder.this;
    }

    /**
     * 设置显示照片的最小宽度
     *
     * @param minWidth 照片的最小宽度，单位Px
     * @return AlbumBuilder
     */
    public AlbumBuilder setMinWidth(int minWidth) {
        Setting.minWidth = minWidth;
        return AlbumBuilder.this;
    }

    /**
     * 设置显示照片的最小高度
     *
     * @param minHeight 显示照片的最小高度，单位Px
     * @return AlbumBuilder
     */
    public AlbumBuilder setMinHeight(int minHeight) {
        Setting.minHeight = minHeight;
        return AlbumBuilder.this;
    }

    /**
     * 设置默认选择图片集合
     *
     * @param selectedPhotos 默认选择图片集合
     * @return AlbumBuilder
     */
    public AlbumBuilder setSelectedPhotos(ArrayList<Photo> selectedPhotos) {
        Setting.selectedPhotos.clear();
        if (selectedPhotos.isEmpty()) {
            return AlbumBuilder.this;
        }
        Setting.selectedPhotos.addAll(selectedPhotos);
        Setting.selectedOriginal = selectedPhotos.get(0).selectedOriginal;
        return AlbumBuilder.this;
    }

    /**
     * 设置默认选择图片地址集合
     *
     * @param selectedPhotoPaths 默认选择图片地址集合
     * @return AlbumBuilder
     */
    public AlbumBuilder setSelectedPhotoPaths(ArrayList<String> selectedPhotoPaths) {
        if (selectedPhotoPaths.isEmpty()) {
            return AlbumBuilder.this;
        }
        Setting.selectedPhotos.clear();
        ArrayList<Photo> selectedPhotos = new ArrayList<>();
        for (String path : selectedPhotoPaths) {
            Photo photo = new Photo(null, path, 0, 0, 0, 0, 0, null);
            selectedPhotos.add(photo);
        }
        Setting.selectedPhotos.addAll(selectedPhotos);
        return AlbumBuilder.this;
    }


    /**
     * 原图按钮设置,不调用该方法不显示原图按钮
     *
     * @param isChecked    原图选项默认状态是否为选中状态
     * @param usable       原图按钮是否可使用
     * @param unusableHint 原图按钮不可使用时给用户的文字提示
     * @return AlbumBuilder
     */
    public AlbumBuilder setOriginalMenu(boolean isChecked, boolean usable, String unusableHint) {
        Setting.showOriginalMenu = true;
        Setting.selectedOriginal = isChecked;
        Setting.originalMenuUsable = usable;
        Setting.originalMenuUnusableHint = unusableHint;
        return AlbumBuilder.this;
    }


    /**
     * 是否显示拼图按钮
     *
     * @param shouldShow 是否显示
     * @return AlbumBuilder
     */
    public AlbumBuilder setPuzzleMenu(boolean shouldShow) {
        Setting.showPuzzleMenu = shouldShow;
        return AlbumBuilder.this;
    }

    /**
     * 过滤类型
     *
     * @param types {@link Type} 默认只有图片Type.image()
     * @return AlbumBuilder
     */
    public AlbumBuilder filter(String... types) {
        Setting.filterTypes = Arrays.asList(types);
        return AlbumBuilder.this;
    }

    /**
     * 是否显示gif图
     *
     * @param shouldShow 是否显示
     * @return AlbumBuilder
     */
    public AlbumBuilder setGif(boolean shouldShow) {
        Setting.showGif = shouldShow;
        return AlbumBuilder.this;
    }

    /**
     * 设置相机功能
     *
     * @param capture 相机功能 默认Capture.ALL
     * @return AlbumBuilder
     */
    public AlbumBuilder setCapture(String capture) {
        Setting.captureType = capture;
        return AlbumBuilder.this;
    }

    /**
     * 设置相机录像时间
     *
     * @param duration 单位ms 默认15000ms
     * @return AlbumBuilder
     */
    public AlbumBuilder setRecordDuration(int duration) {
        Setting.recordDuration = duration;
        return AlbumBuilder.this;
    }

    /**
     * 设置相机顶层覆盖View
     *
     * @param coverView 默认null
     * @return AlbumBuilder
     */
    public AlbumBuilder setCameraCoverView(View coverView) {
        Setting.cameraCoverView = new WeakReference<View>(coverView);
        return AlbumBuilder.this;
    }

    /**
     * 是否显示相机提示文字
     *
     * @param enable 默认true
     * @return AlbumBuilder
     */
    public AlbumBuilder enableCameraTips(boolean enable) {
        Setting.enableCameraTip = enable;
        return AlbumBuilder.this;
    }

    /**
     * 是否裁切(仅单选结果有效)
     *
     * @param isCrop 默认否
     * @return AlbumBuilder
     */
    public AlbumBuilder isCrop(boolean isCrop) {
        Setting.isCrop = isCrop;
        return AlbumBuilder.this;
    }

    /**
     * 设置裁剪质量
     *
     * @param quality 裁剪质量 默认90
     * @return AlbumBuilder
     */
    public AlbumBuilder setCompressionQuality(@IntRange(from = 0) int quality) {
        Setting.compressQuality = quality;
        return AlbumBuilder.this;
    }

    /**
     * 是否圆形裁剪
     *
     * @param isCircle 默认否
     * @return AlbumBuilder
     */
    public AlbumBuilder setCircleDimmedLayer(boolean isCircle) {
        Setting.isCircle = isCircle;
        return AlbumBuilder.this;
    }

    /**
     * 是否显示裁剪框
     *
     * @param isShowCropCropFrame 默认是
     * @return AlbumBuilder
     */
    public AlbumBuilder setShowCropFrame(boolean isShowCropCropFrame) {
        Setting.isShowCropCropFrame = isShowCropCropFrame;
        return AlbumBuilder.this;
    }

    /**
     * 是否显示裁剪网格
     *
     * @param isShowCropGrid 默认是
     * @return AlbumBuilder
     */
    public AlbumBuilder setShowCropGrid(boolean isShowCropGrid) {
        Setting.isShowCropGrid = isShowCropGrid;
        return AlbumBuilder.this;
    }

    /**
     * 是否自由裁剪
     *
     * @param isFreeStyleCrop 默认否
     * @return AlbumBuilder
     */
    public AlbumBuilder setFreeStyleCropEnabled(boolean isFreeStyleCrop) {
        Setting.isFreeStyleCrop = isFreeStyleCrop;
        return AlbumBuilder.this;
    }

    /**
     * 是否显示UCrop底部控制
     *
     * @param isHideUCropControls 默认是
     * @return AlbumBuilder
     */
    public AlbumBuilder setHideBottomControls(boolean isHideUCropControls) {
        Setting.isHideUCropControls = isHideUCropControls;
        return AlbumBuilder.this;
    }

    /**
     * 设置裁剪比例
     *
     * @param aspectRatioX 默认1
     * @param aspectRatioY 默认1
     * @return AlbumBuilder
     */
    public AlbumBuilder setAspectRatio(float aspectRatioX, float aspectRatioY) {
        float[] ar = new float[2];
        ar[0] = aspectRatioX;
        ar[1] = aspectRatioY;
        Setting.aspectRatio = ar;
        return AlbumBuilder.this;
    }


    /**
     * 显示最少多少秒的视频
     *
     * @param second 秒
     * @return AlbumBuilder
     */
    public AlbumBuilder setVideoMinSecond(int second) {
        Setting.videoMinSecond = second * 1000;
        return AlbumBuilder.this;
    }

    /**
     * 显示最多多少秒的视频
     *
     * @param second 秒
     * @return AlbumBuilder
     */
    public AlbumBuilder setVideoMaxSecond(int second) {
        Setting.videoMaxSecond = second * 1000;
        return AlbumBuilder.this;
    }

    /**
     * 相册选择页是否显示清空按钮
     *
     * @param shouldShow
     * @return
     */
    public AlbumBuilder setCleanMenu(boolean shouldShow) {
        Setting.showCleanMenu = shouldShow;
        return AlbumBuilder.this;
    }

    private void setSettingParams() {
        switch (startupType) {
            case CAMERA:
                Setting.onlyStartCamera = true;
                Setting.isShowCamera = true;
                break;
            case ALBUM:
                Setting.isShowCamera = false;
                Setting.captureType = Capture.IMAGE;
                break;
            case ALBUM_CAMERA:
                Setting.isShowCamera = true;
                if (Setting.isOnlyVideo()) {
                    Setting.captureType = Capture.VIDEO;
                    setPuzzleMenu(false);
                    Setting.isCrop = false;
                }
                if (Setting.isOnlyImage()) Setting.captureType = Capture.IMAGE;
                break;
        }
        if (Setting.pictureCount != -1 || Setting.videoCount != -1) {
            Setting.count = Setting.pictureCount + Setting.videoCount;
        }
        if (Setting.isOnlyGif()) {
            Setting.isShowCamera = false;
            setPuzzleMenu(false);
            Setting.isCrop = false;
        }
        if (Setting.count > 1) Setting.isCrop = false;
    }

    /**
     * 设置启动属性
     *
     * @param callback SelectCallback 选择回调
     */
    public void start(SelectCallback callback) {
        setSettingParams();
        if (null != mActivity && null != mActivity.get() && mActivity.get() instanceof FragmentActivity) {
            EasyResult.get((FragmentActivity) mActivity.get()).startEasyPhoto(callback);
            return;
        }
        if (null != mFragmentV && null != mFragmentV.get()) {
            EasyResult.get(mFragmentV.get()).startEasyPhoto(callback);
            return;
        }
        throw new RuntimeException("mActivity or mFragmentV maybe null, you can not use this method... ");
    }

    /**
     * 清除所有数据
     */
    private static void clear() {
        Result.clear();
        Setting.clear();
        instance = null;
    }

//*********************AD************************************

    /**
     * 设置广告(不设置该选项则表示不使用广告)
     *
     * @param photosAdView         使用图片列表的广告View
     * @param photosAdIsLoaded     图片列表广告是否加载完毕
     * @param albumItemsAdView     使用专辑项目列表的广告View
     * @param albumItemsAdIsLoaded 专辑项目列表广告是否加载完毕
     * @return AlbumBuilder
     */
    public AlbumBuilder setAdView(View photosAdView, boolean photosAdIsLoaded, View albumItemsAdView, boolean albumItemsAdIsLoaded) {
        Setting.photosAdView = new WeakReference<View>(photosAdView);
        Setting.albumItemsAdView = new WeakReference<View>(albumItemsAdView);
        Setting.photoAdIsOk = photosAdIsLoaded;
        Setting.albumItemsAdIsOk = albumItemsAdIsLoaded;
        return AlbumBuilder.this;
    }

    /**
     * 设置广告监听
     * 内部使用，无需关心
     *
     * @param adListener 广告监听
     */
    public static void setAdListener(AdListener adListener) {
        if (null == instance) return;
        if (instance.startupType == StartupType.CAMERA) return;
        instance.adListener = new WeakReference<AdListener>(adListener);
    }

    /**
     * 刷新图片列表广告数据
     */
    public static void notifyPhotosAdLoaded() {
        if (Setting.photoAdIsOk) {
            return;
        }
        if (null == instance) {
            return;
        }
        if (instance.startupType == StartupType.CAMERA) {
            return;
        }
        if (null == instance.adListener) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (null != instance && null != instance.adListener) {
                        Setting.photoAdIsOk = true;
                        instance.adListener.get().onPhotosAdLoaded();
                    }
                }
            }).start();
            return;
        }
        Setting.photoAdIsOk = true;
        instance.adListener.get().onPhotosAdLoaded();
    }

    /**
     * 刷新专辑项目列表广告
     */
    public static void notifyAlbumItemsAdLoaded() {
        if (Setting.albumItemsAdIsOk) {
            return;
        }
        if (null == instance) {
            return;
        }
        if (instance.startupType == StartupType.CAMERA) {
            return;
        }
        if (null == instance.adListener) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (null != instance && null != instance.adListener) {
                        Setting.albumItemsAdIsOk = true;
                        instance.adListener.get().onAlbumItemsAdLoaded();
                    }
                }
            }).start();
            return;
        }
        Setting.albumItemsAdIsOk = true;
        instance.adListener.get().onAlbumItemsAdLoaded();
    }

}
