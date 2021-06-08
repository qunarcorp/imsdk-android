package com.qunar.im.ui.util.easyphoto.easyphotos.setting;

import androidx.annotation.IntDef;
import android.view.View;

import com.qunar.im.ui.util.easyphoto.easyphotos.callback.VideoPlayCallback;
import com.qunar.im.ui.util.easyphoto.easyphotos.constant.Capture;
import com.qunar.im.ui.util.easyphoto.easyphotos.constant.Type;
import com.qunar.im.ui.util.easyphoto.easyphotos.engine.ImageEngine;
import com.qunar.im.ui.util.easyphoto.easyphotos.models.album.entity.Photo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * EasyPhotos的设置值
 * Created by huan on 2017/10/24.
 */

public class Setting {
    public static boolean isDistinguish = false;
    public static VideoPlayCallback videoPlayCallback=null;
    public static int minWidth = 1;
    public static int minHeight = 1;
    public static long minSize = 1;
    public static long maxSize = Long.MAX_VALUE;
    public static boolean selectMutualExclusion = false;
    public static int count = 1;
    public static int pictureCount = -1;
    public static int videoCount = -1;
    public static WeakReference<View> photosAdView = null;
    public static WeakReference<View> albumItemsAdView = null;
    public static boolean photoAdIsOk = false;
    public static boolean albumItemsAdIsOk = false;
    public static ArrayList<Photo> selectedPhotos = new ArrayList<>();
    public static boolean showOriginalMenu = false;
    public static boolean originalMenuUsable = false;
    public static String originalMenuUnusableHint = "";
    public static boolean selectedOriginal = false;
    public static String fileProviderAuthority = null;
    public static boolean isShowCamera = false;
    public static boolean onlyStartCamera = false;
    public static boolean showPuzzleMenu = true;
    public static List<String> filterTypes = new ArrayList<>(Arrays.asList(Type.image()));
    public static boolean showGif = false;
    public static boolean showCleanMenu = true;
    public static long videoMinSecond = 0L;
    public static long videoMaxSecond = Long.MAX_VALUE;
    public static ImageEngine imageEngine = null;
    // 相机按钮位置
    public static final int LIST_FIRST = 0;
    public static final int BOTTOM_RIGHT = 1;
    public static int cameraLocation = BOTTOM_RIGHT;
    // 相机功能
    public static String captureType = Capture.ALL;
    public static int recordDuration = 15000;
    public static WeakReference<View> cameraCoverView = null;
    public static boolean enableCameraTip = true;
    // 裁剪相关参数
    public static boolean isCrop = false;
    public static int compressQuality = 90;
    public static boolean isCircle = false;
    public static boolean isShowCropCropFrame = true;
    public static boolean isShowCropGrid = true;
    public static boolean isFreeStyleCrop = false;
    public static boolean isHideUCropControls = false;
    public static float[] aspectRatio = new float[]{1, 1};

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {LIST_FIRST, BOTTOM_RIGHT})
    public @interface Location {

    }

    public static void clear() {
        videoPlayCallback = null;
        isDistinguish = false;
        minWidth = 1;
        minHeight = 1;
        minSize = 1;
        maxSize = Long.MAX_VALUE;
        selectMutualExclusion = false;
        count = 1;
        pictureCount = -1;
        videoCount = -1;
        photosAdView = null;
        albumItemsAdView = null;
        photoAdIsOk = false;
        albumItemsAdIsOk = false;
        selectedPhotos.clear();
        showOriginalMenu = false;
        originalMenuUsable = false;
        originalMenuUnusableHint = "";
        selectedOriginal = false;
        cameraLocation = BOTTOM_RIGHT;
        isShowCamera = false;
        onlyStartCamera = false;
        showPuzzleMenu = true;
        filterTypes = new ArrayList<>(Arrays.asList(Type.image()));
        showGif = false;
        showCleanMenu = true;
        videoMinSecond = 0L;
        videoMaxSecond = Long.MAX_VALUE;
        captureType = Capture.ALL;
        recordDuration = 15000;
        cameraCoverView = null;
        enableCameraTip = true;
        isCrop = false;
        compressQuality = 90;
        isCircle = false;
        isShowCropCropFrame = true;
        isShowCropGrid = true;
        isFreeStyleCrop = false;
        isHideUCropControls = false;
        aspectRatio = new float[]{1, 1};
    }

    public static boolean isOnlyGif() {
        //Setting.filterTypes.containsAll(Arrays.asList(Type.image()))
        return Arrays.asList(Type.gif()).containsAll(Setting.filterTypes);
    }

    public static boolean isOnlyImage() {
        //Setting.filterTypes.containsAll(Arrays.asList(Type.image()))
        return Arrays.asList(Type.image()).containsAll(Setting.filterTypes);
    }

    public static boolean isOnlyVideo() {
        //Setting.filterTypes.containsAll(Arrays.asList(Type.video()))
        return Arrays.asList(Type.video()).containsAll(Setting.filterTypes);
    }

    public static boolean isAll() {
        //Setting.filterTypes.containsAll(Arrays.asList(Type.all()))
        return Arrays.asList(Type.all()).containsAll(Setting.filterTypes);
    }

    public static boolean showVideo() {
        return !isOnlyImage();
    }

    public static boolean hasPhotosAd() {
        return photosAdView != null && photosAdView.get() != null;
    }

    public static boolean hasAlbumItemsAd() {
        return albumItemsAdView != null && albumItemsAdView.get() != null;
    }

    public static boolean isBottomRightCamera() {
        return cameraLocation == BOTTOM_RIGHT;
    }
}
