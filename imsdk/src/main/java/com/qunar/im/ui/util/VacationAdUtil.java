package com.qunar.im.ui.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.ValueCallback;

import com.qunar.im.ui.activity.PictureSelectorActivity;
import com.qunar.im.ui.imagepicker.ImagePicker;
import com.qunar.im.ui.imagepicker.ui.ImageGridActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 签证
 *
 * @author jerry.li
 */
public class VacationAdUtil {
    public static final int SELECT_PHOTO_REQUEST = 1000;
    public static final int ACTIVITY_SELECT_PHOTO = 2;//图库选图
    Context mContext;
    public  ValueCallback<Uri[]> mUploadMessages;
    public  ValueCallback<Uri> mUploadMessage;

    public VacationAdUtil(Context context) {
        mContext = context;
    }

    /**
     * 检查SD卡是否存在
     *
     * @return
     */

    public final void selectImage() {
        Intent intent1 = new Intent(mContext, PictureSelectorActivity.class);
        intent1.putExtra("isMultiSel", false);
        ((Activity)mContext).startActivityForResult(intent1, SELECT_PHOTO_REQUEST);
    }

    public final void selectPic() {
        //新版图片选择器
        ImagePicker.getInstance().setSelectLimit(9);
        Intent intent1 = new Intent(mContext, ImageGridActivity.class);
                                /* 如果需要进入选择的时候显示已经选中的图片，
                                 * 详情请查看ImagePickerActivity
                                 * */
//                                intent1.putExtra(ImageGridActivity.EXTRAS_IMAGES,images);
        ((Activity)mContext).startActivityForResult(intent1, ACTIVITY_SELECT_PHOTO);
    }

    public  void reqChoose(Uri uri) {
        if (mUploadMessages != null && uri != null) {
            Uri[] uris = new Uri[]{uri};
            mUploadMessages.onReceiveValue(uris);
        } else if (mUploadMessage != null) {
            mUploadMessage.onReceiveValue(uri);
        }
        mUploadMessage = null;
        mUploadMessages = null;
    }

    public  void reqChooseList(List<Uri> uriList) {
        if (mUploadMessages != null && uriList != null) {
            Uri[] uris =  uriList.toArray(new Uri[uriList.size()]);
            mUploadMessages.onReceiveValue(uris);
        } else if (mUploadMessage != null) {
            mUploadMessage.onReceiveValue(null);
        }
        mUploadMessage = null;
        mUploadMessages = null;
    }




    public  void cancelUpload() {
        if (mUploadMessages != null) {
            mUploadMessages.onReceiveValue(null);
            mUploadMessages=null;
        }
        if (mUploadMessage != null) {
            mUploadMessage.onReceiveValue(null);
            mUploadMessage = null;
        }
    }

}
