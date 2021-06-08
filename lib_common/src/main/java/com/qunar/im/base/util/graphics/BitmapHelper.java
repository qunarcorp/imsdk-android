package com.qunar.im.base.util.graphics;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapHelper {

    public static Bitmap decodeFile(String pathName,int thumbWidth,int thumbHeight) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, newOpts);
        newOpts.inSampleSize =  ImageUtils.computeSampleSize(newOpts,-1,thumbHeight*thumbWidth);
        newOpts.inJustDecodeBounds = false;
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        Bitmap bm = BitmapFactory.decodeFile(pathName, newOpts);
        return bm;
    }

    public static Bitmap decodeFile(String pathName) {
        Bitmap bm = BitmapFactory.decodeFile(pathName);
        return bm;
    }

    /**
     * 读取资源文件返回可编辑的Bitmap
     *
     * @param res
     * @param id
     * @return
     */
    public static Bitmap decodeResource(Resources res, int id) {
        Bitmap bitmap = null;
        try {
            Bitmap original = BitmapFactory.decodeResource(res, id);
            bitmap = original.copy(Bitmap.Config.ARGB_8888, true);
            original.recycle();
        } catch (OutOfMemoryError e) {
        }
        return bitmap;
    }
}
