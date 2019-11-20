package com.qunar.im.base.util.graphics;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.qunar.im.base.util.LogUtil;
import com.qunar.im.common.CommonConfig;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.List;


public final class ImageUtils {

    public static final String TAG = "ImageUtils";

    public static Bitmap compressBimap(String mPath,Activity activity){
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int screenWidth = metric.widthPixels; // 屏幕宽度（像素）
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mPath, options);
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inSampleSize = calculateInSampleSize(options, screenWidth);
        options.inJustDecodeBounds = false;
        Bitmap resizeBmp = BitmapFactory.decodeFile(mPath, options);
        return resizeBmp;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth) {
        // Raw height and width of image
        final int width = options.outWidth;
        int inSampleSize = 1;

            while (  (width / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        return inSampleSize;
    }

    private static byte[] toByteArray(File file, int len) {
        FileInputStream fileInputStream = null;
        byte[] bFiles = new byte[len];
        if(!file.exists()) return bFiles;
        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFiles, 0, len);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bFiles;
    }

    private static boolean copy(File source, File dest) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(source));
            bos = new BufferedOutputStream(new FileOutputStream(dest, false));

            byte[] buf = new byte[1024];
            bis.read(buf);

            do {
                bos.write(buf);
            } while(bis.read(buf) != -1);
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                return false;
            }
        }

        return true;
    }

    public static void compressFile(File src,long maxSize,int maxW,int maxH,File target) {
        //文件本身就小于指定的size,不进行压缩处理
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(src.getAbsolutePath(),newOpts);//此时返回bm为空
        byte[] bytes = toByteArray(src, 4);
        ImageUtils.ImageType type = ImageUtils.adjustImageType(bytes);
        if (type == ImageType.GIF){
            //如果是gif图片则复制原文件
            copy(src,target);
            return ;
        }
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > maxW) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (1.0f * newOpts.outWidth / maxW);
        } else if (w < h && h > maxH) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (1.0f * newOpts.outHeight / maxH);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        Bitmap newBitmap = BitmapFactory.decodeFile(src.getAbsolutePath(), newOpts);
        compressImage(newBitmap, maxSize, target);//压缩好比例大小后再进行质量压缩
        if (bitmap != null){
            bitmap.recycle();
        }
        if(newBitmap!=null)
            newBitmap.recycle();
    }

    private static void compressImage(Bitmap image,long maxSize,File target) {
        if(image == null){
            return;
        }
        FileOutputStream fos = null;
        ByteArrayOutputStream baos = null;
        ByteArrayInputStream isBm = null;
        try {
            baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 100;
            while (baos.toByteArray().length > maxSize) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
                baos.reset();//重置baos即清空baos
                options -= 10;//每次都减少10
                image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            }
            isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
            fos = new FileOutputStream(target);
            baos.writeTo(fos);
        } catch (IOException e) {
            LogUtil.e(TAG, "IO Exception", e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    LogUtil.e(TAG, "IO Exception", e);
                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    LogUtil.e(TAG, "IO Exception", e);
                }
            }
            if (isBm != null) {
                try {
                    isBm.close();
                } catch (IOException e) {
                    LogUtil.e(TAG, "IO Exception", e);
                }
            }
        }
    }
    public static File compressFile(Bitmap bitmap,File target)
    {
        try {
            target.createNewFile();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, new FileOutputStream(target));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return target;
    }
    public static File compressFile(File file) {
        return compressFile(file, null);
    }
    public static File compressFile(File source, File target) {
        Bitmap bitmap = ImageUtils.getThumbnail(source, (int) 0.5f);
        if (target == null) {
            target = new File(source.getParent(), "compress_" + source.getName());
        }
        if (target.exists()) {
            target.delete();
        }
        File file = compressFile(bitmap,target);
        if (bitmap != null)
            bitmap.recycle();
        return file;
    }

    public static boolean saveBitmap(Bitmap bitmap, File file) {
        if (bitmap != null) {
            if (file.exists()) {
                file.delete();
            }
            try {
                file.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    public static int computeSampleSize(int w, int h, int minSideLength, int maxNumOfPixels) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.outHeight = h;
        options.outWidth = w;
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = maxNumOfPixels == -1 ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = minSideLength == -1 ? 128 : (int) Math.min(Math.floor(w / minSideLength),
                Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if (maxNumOfPixels == -1 && minSideLength == -1) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * 创建图片的缩略图
     *
     * @param imageFile
     * @param sampleSize 缩略图的尺寸（单位为px）
     * @return 创建缩略图的Bitmap
     */
    public static Bitmap getThumbnail(File imageFile, int sampleSize) {
        // 获取这个图片的宽和高
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

            options.inSampleSize = computeSampleSize(options, -1, sampleSize * sampleSize);
            // 重新读入图片，这次要把options.inJustDecodeBounds 设为 false
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
        } catch (OutOfMemoryError t) {
            System.gc();
            try {
                Method m = Runtime.class.getDeclaredMethod("runFinalization", boolean.class);
                m.setAccessible(true);
                m.invoke(Runtime.getRuntime(), true);
            } catch (Exception e) {
            }
        }
        return bitmap;
    }

    /**
     * Change the drawable to btimap.
     *
     * @param drawable A drawable object.
     * @return A Bitmap object.
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    /**
     * 获得当前屏幕的尺寸
     *
     * @param activity
     * @return
     */
    public static Display getScreenDisplay(Activity activity) {
        return activity.getWindowManager().getDefaultDisplay();
    }
    /**
     * 为textview指定一个等高的icon,若resId不合法将返回null
     *
     * @param textView
     * @param resId
     */
    public static Drawable getEqualHeightDrawable(TextView textView, int resId) {
        Drawable drawable = null;
        if (resId > 0) {
            try {
                drawable = textView.getContext().getResources().getDrawable(resId);
                drawable.setBounds(0, 0, (int) textView.getPaint().getTextSize(), (int) textView.getPaint()
                        .getTextSize());
            } catch (Exception e) {
                // do nothing
            }
        }
        return drawable;
    }

    /**
     * 获取图片的宽和高
     *
     * @param path
     * @return options.outWidth单位px
     */
    public static BitmapFactory.Options getImageSize(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        return options;
    }

    /**
     * 旋转bitmap
     *
     * @param b
     * @param degrees
     * @return
     */
    public static Bitmap rotate(Bitmap b, int degrees) {
        if (degrees != 0 && b != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, b.getWidth() / 2, b.getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                if (b != b2) {
                    b.recycle();
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
            }
        }
        return b;
    }

    public static ImageType adjustImageType(byte[] image) {
        if (image.length < 2) {
            return ImageType.NONE;
        }
        if (image[0] == 0x42 && image[1] == 0x4d) {
            return ImageType.BMP;
        }
        if (image[0] == 0xff && image[1] == 0xd8) {
            return ImageType.JPEG;
        }
        if (image[0] == 0x47 && image[1] == 0x49 && image[2] == 0x46) {
            return ImageType.GIF;
        }
        if (image[0] == 0x89 && image[1] == 0x50 && image[2] == 0x4E && image[3] == 0x47) {
            return ImageType.PNG;
        }
        return ImageType.NONE;
    }

    /**
     * 代码代替xml生成Drawable状态变色节省资源
     *
     * @param normal
     * @param pressed
     * @author ran.feng
     * @since 2012年9月2日下午3:56:34
     */
    public static StateListDrawable createBGSelector(int normal, int pressed) {
        final StateListDrawable drawable = new StateListDrawable();
        ColorDrawable cdNormal = new ColorDrawable(normal);
        ColorDrawable cdPressed = new ColorDrawable(pressed);
        drawable.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, cdPressed);
        drawable.addState(new int[]{android.R.attr.state_selected, android.R.attr.state_enabled}, cdPressed);
        drawable.addState(new int[]{android.R.attr.state_checked, android.R.attr.state_enabled}, cdPressed);
        drawable.addState(new int[]{android.R.attr.state_enabled}, cdNormal);
        return drawable;
    }

    public static void saveToGallery(final Context context, Bitmap bitmap) {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "desc");
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        OutputStream imageOut = null;
        // 指定保存图片路径
        File appDir = new File(Environment.getExternalStorageDirectory(), "Qtalk");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".png";
        final File outfile = new File(appDir, fileName);

        try {

            if(!outfile.exists()){
                outfile.createNewFile();
            }
            imageOut = new FileOutputStream(outfile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageOut);

            values.put(MediaStore.Images.Media.DATA, outfile.getAbsolutePath());
            //插入图库
            Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } catch (Exception e) {
            LogUtil.e(TAG,"error",e);
        } finally {
            try {
                imageOut.close();
            } catch (Exception e) {
                LogUtil.e(TAG,"error",e);
            }
        }
        CommonConfig.mainhandler.post(new Runnable() {
            @Override
            public void run() {
                //通知更新图库
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(outfile)));
                Toast.makeText(context, "保存到" + outfile.getAbsolutePath(), Toast.LENGTH_LONG).show();
            }
        });
    }



    public static void saveToGallery(final Context context, File file,boolean isGif) {

        String suffix;

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "desc");
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        if(isGif){
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/gif");
            suffix = ".gif";
        }else{
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            suffix = ".jpg";
        }

        // 指定保存图片路径
        File appDir = new File(Environment.getExternalStorageDirectory(), CommonConfig.currentPlat);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + suffix;
        final File outfile = new File(appDir, fileName);

        OutputStream imageOut = null;
        InputStream is = null;
        try {
            if(!outfile.exists()){
                outfile.createNewFile();
            }
            values.put(MediaStore.Images.Media.DATA, outfile.getAbsolutePath());

            is = new BufferedInputStream(new FileInputStream(file));
            imageOut = new FileOutputStream(outfile);
//            imageOut = context.getContentResolver().openOutputStream(uri);
            byte[] buffer = new byte[1024];
            int n = 0;
            while ((n = is.read(buffer)) != -1){
                imageOut.write(buffer, 0, n);
            }
            //插入图库
            final Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                imageOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        CommonConfig.mainhandler.post(new Runnable() {
            @Override
            public void run() {
                //通知更新图库
                if(context != null){
                    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(outfile)));
                    Toast.makeText(context, "保存到" + outfile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public static Bitmap transformRotation(String src) {
        if (src == null)
            return null;
        Bitmap bitmap = BitmapFactory.decodeFile(src);
        Bitmap target = null;
        ExifInterface exifInterface = null;
        if (bitmap != null) {
            try {
                exifInterface = new ExifInterface(src);
            } catch (IOException e) {
                LogUtil.e("IO Exception");
            }
            if (exifInterface != null) {
                Matrix matrix = new Matrix();
                switch (exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.postRotate(180);
                        target = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.postRotate(270);
                        target = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.postRotate(90);
                        target = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                        break;
                    default:
                        target = bitmap;
                        break;
                }
            }
        }
        if (target != bitmap) {
            bitmap.recycle();
        }
        return target;
    }

    public static Bitmap compose(final List<File> source, int width, int height) {
        if (source == null || source.size() == 0) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        int w, h;
        switch (source.size()) {
            case 1:
                w = width / 2;
                h = height / 2;
                compose1Bitmap(source, canvas, w, h);
                break;
            case 2:
                w = width / 2;
                h = height / 2;
                compose2Bitmap(source, canvas, w, h);
                break;
            case 3:
                w = width / 2;
                h = height / 2;
                compose3Bitmap(source, canvas, w, h);
                break;
            case 4:
                w = width / 2;
                h = height / 2;
                compose4Bitmap(source, canvas, w, h);
                break;
            case 5:
                w = width / 3;
                h = height / 3;
                compose5Bitmap(source, canvas, w, h);
                break;
            case 6:
                w = width / 3;
                h = height / 3;
                compose6Bitmap(source, canvas, w, h);
                break;
            case 7:
                w = width / 3;
                h = height / 3;
                compose7Bitmap(source, canvas, w, h);
                break;
            case 8:
                w = width / 3;
                h = height / 3;
                compose8Bitmap(source, canvas, w, h);
                break;
            case 9:
                w = width / 3;
                h = height / 3;
                compose9Bitmap(source, canvas, w, h);
                break;
            default:
                w = width / 3;
                h = height / 3;
                compose9PlusBitmap(source, canvas, w, h);
                break;
        }
        return bitmap;
    }


    private static void compose1Bitmap(final List<File> source, Canvas canvas, int w, int h) {
        Bitmap bp;
        int left = canvas.getWidth() / 2 - w / 2, top = canvas.getHeight() / 2 - h / 2;
        bp = scaleBitmap(source.get(0).getAbsolutePath(), w, h);
        canvas.save();
        canvas.clipRect(left + 0.5f, top + 0.5f, left + w - 0.5f, top + h - 0.5f, Region.Op.INTERSECT);
        canvas.drawBitmap(bp, left, top, null);
        canvas.restore();
        bp.recycle();
    }

    private static void compose2Bitmap(final List<File> source, Canvas canvas, int w, int h) {
        Bitmap bp;
        int left = 0, top = 0;
        for (int i = 0; i < source.size(); i++) {
            bp = scaleBitmap(source.get(i).getAbsolutePath(), w, h);
            switch (i) {
                case 0:
                    left = 0;
                    top = 0;
                    break;
                case 1:
                    left = w;
                    top = h;
                    break;
            }
            canvas.save();
            canvas.clipRect(left + 0.5f, top + 0.5f, left + w - 0.5f, top + h - 0.5f, Region.Op.INTERSECT);
            canvas.drawBitmap(bp, left, top, null);
            canvas.restore();
            bp.recycle();
        }
    }

    private static void compose3Bitmap(final List<File> source, Canvas canvas, int w, int h) {
        Bitmap bp;
        int left = 0, top = 0;
        for (int i = 0; i < source.size(); i++) {
            bp = scaleBitmap(source.get(i).getAbsolutePath(), w, h);
            switch (i) {
                case 0:
                    left = canvas.getWidth() / 2 - w / 2;
                    top = 0;
                    break;
                case 1:
                    left = 0;
                    top = h;
                    break;
                case 2:
                    left = w;
                    top = h;
                    break;
            }
            canvas.save();
            canvas.clipRect(left + 0.5f, top + 0.5f, left + w - 0.5f, top + h - 0.5f, Region.Op.INTERSECT);
            canvas.drawBitmap(bp, left, top, null);
            canvas.restore();
            bp.recycle();
        }
    }

    private static void compose4Bitmap(final List<File> source, Canvas canvas, int w, int h) {
        Bitmap bp;
        int left = 0, top = 0;
        for (int i = 0; i < source.size(); i++) {
            bp = scaleBitmap(source.get(i).getAbsolutePath(), w, h);
            switch (i) {
                case 0:
                    left = 0;
                    top = 0;
                    break;
                case 1:
                    left = w;
                    top = 0;
                    break;
                case 2:
                    left = 0;
                    top = h;
                    break;
                case 3:
                    left = w;
                    top = h;
                    break;
            }
            canvas.save();
            canvas.clipRect(left + 0.5f, top + 0.5f, left + w - 0.5f, top + h - 0.5f, Region.Op.INTERSECT);
            canvas.drawBitmap(bp, left, top, null);
            canvas.restore();
            bp.recycle();
        }
    }

    private static void compose5Bitmap(final List<File> source, Canvas canvas, int w, int h) {
        Bitmap bp;
        int left = 0, top = 0;
        for (int i = 0; i < source.size(); i++) {
            bp = scaleBitmap(source.get(i).getAbsolutePath(), w, h);
            switch (i) {
                case 0:
                    left = canvas.getWidth() / 2 - w;
                    top = canvas.getHeight() / 2 - h;
                    break;
                case 1:
                    left = canvas.getWidth() / 2;
                    top = canvas.getHeight() / 2 - h;
                    break;
                case 2:
                    left = 0;
                    top = canvas.getHeight() / 2;
                    break;
                case 3:
                    left = w;
                    top = canvas.getHeight() / 2;
                    break;
                case 4:
                    left = w * 2;
                    top = canvas.getHeight() / 2;
                    break;
            }
            canvas.save();
            canvas.clipRect(left + 0.5f, top + 0.5f, left + w - 0.5f, top + h - 0.5f, Region.Op.INTERSECT);
            canvas.drawBitmap(bp, left, top, null);
            canvas.restore();
            bp.recycle();
        }
    }

    private static void compose6Bitmap(final List<File> source, Canvas canvas, int w, int h) {
        Bitmap bp;
        int left = 0, top = 0;
        for (int i = 0; i < source.size(); i++) {
            bp = scaleBitmap(source.get(i).getAbsolutePath(), w, h);
            switch (i) {
                case 0:
                    left = 0;
                    top = canvas.getHeight() / 2 - h;
                    break;
                case 1:
                    left = w;
                    top = canvas.getHeight() / 2 - h;
                    break;
                case 2:
                    left = w * 2;
                    top = canvas.getHeight() / 2 - h;
                    break;
                case 3:
                    left = 0;
                    top = canvas.getHeight() / 2;
                    break;
                case 4:
                    left = w;
                    top = canvas.getHeight() / 2;
                    break;
                case 5:
                    left = w * 2;
                    top = canvas.getHeight() / 2;
                    break;
            }
            canvas.save();
            canvas.clipRect(left + 0.5f, top + 0.5f, left + w - 0.5f, top + h - 0.5f, Region.Op.INTERSECT);
            canvas.drawBitmap(bp, left, top, null);
            canvas.restore();
            bp.recycle();
        }
    }

    private static void compose7Bitmap(final List<File> source, Canvas canvas, int w, int h) {
        Bitmap bp;
        int left = 0, top = 0;
        for (int i = 0; i < source.size(); i++) {
            bp = scaleBitmap(source.get(i).getAbsolutePath(), w, h);
            switch (i) {
                case 0:
                    left = 0;
                    top = 0;
                    break;
                case 1:
                    left = w;
                    top = 0;
                    break;
                case 2:
                    left = w * 2;
                    top = 0;
                    break;
                case 3:
                    left = 0;
                    top = h;
                    break;
                case 4:
                    left = w;
                    top = h;
                    break;
                case 5:
                    left = w * 2;
                    top = h;
                    break;
                case 6:
                    left = canvas.getWidth() / 2 - w / 2;
                    top = h * 2;
                    break;
            }
            canvas.save();
            canvas.clipRect(left + 0.5f, top + 0.5f, left + w - 0.5f, top + h - 0.5f, Region.Op.INTERSECT);
            canvas.drawBitmap(bp, left, top, null);
            canvas.restore();
            bp.recycle();
        }
    }

    private static void compose8Bitmap(final List<File> source, Canvas canvas, int w, int h) {
        Bitmap bp;
        int left = 0, top = 0;
        for (int i = 0; i < source.size(); i++) {
            bp = scaleBitmap(source.get(i).getAbsolutePath(), w, h);
            switch (i) {
                case 0:
                    left = 0;
                    top = 0;
                    break;
                case 1:
                    left = w;
                    top = 0;
                    break;
                case 2:
                    left = w * 2;
                    top = 0;
                    break;
                case 3:
                    left = 0;
                    top = h;
                    break;
                case 4:
                    left = w;
                    top = h;
                    break;
                case 5:
                    left = w * 2;
                    top = h;
                    break;
                case 6:
                    left = canvas.getWidth() / 2 - w;
                    top = h * 2;
                    break;
                case 7:
                    left = canvas.getWidth() / 2;
                    top = h * 2;
                    break;
            }
            canvas.save();
            canvas.clipRect(left + 0.5f, top + 0.5f, left + w - 0.5f, top + h - 0.5f, Region.Op.INTERSECT);
            canvas.drawBitmap(bp, left, top, null);
            canvas.restore();
            bp.recycle();
        }
    }

    private static void compose9Bitmap(final List<File> source, Canvas canvas, int w, int h) {
        Bitmap bp;
        int left, top;
        for (int i = 0; i < source.size(); i++) {
            bp = scaleBitmap(source.get(i).getAbsolutePath(), w, h);
            switch (i) {
                case 0:
                    left = 0;
                    top = 0;
                    break;
                case 1:
                    left = w;
                    top = 0;
                    break;
                case 2:
                    left = w * 2;
                    top = 0;
                    break;
                case 3:
                    left = 0;
                    top = h;
                    break;
                case 4:
                    left = w;
                    top = h;
                    break;
                case 5:
                    left = w * 2;
                    top = h;
                    break;
                case 6:
                    left = 0;
                    top = h * 2;
                    break;
                case 7:
                    left = w;
                    top = h * 2;
                    break;
                case 8:
                    left = w * 2;
                    top = h * 2;
                    break;
                default:
                    return;
            }
            canvas.save();
            canvas.clipRect(left + 0.5f, top + 0.5f, left + w - 0.5f, top + h - 0.5f, Region.Op.INTERSECT);
            canvas.drawBitmap(bp, left, top, null);
            canvas.restore();
            bp.recycle();
        }
    }

    private static void compose9PlusBitmap(final List<File> source, Canvas canvas, int w, int h) {
        compose9Bitmap(source, canvas, w, h);
    }

    private static Bitmap scaleBitmap(String path, int w, int h) {
        Bitmap bitmap = BitmapHelper.decodeFile(path, w, h);
        if (bitmap != null) {
            if (w == bitmap.getWidth() && h == bitmap.getHeight())
                return bitmap;
            Bitmap result = Bitmap.createScaledBitmap(bitmap, w, h, false);
            bitmap.recycle();
            return result;
        }
        return Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    }

    public enum ImageType {
        BMP, JPEG, GIF, PNG, NONE
    }

    /**
     * 先缩小图片，再做高斯模糊速度快.灵感来源于StackOverflow
     * @param bkg
     * @param view
     */
    public static void blur(Bitmap bkg, View view) {
        if (bkg == null || view == null || bkg.isRecycled()) {
            return;
        }
        int viewHeight = view.getHeight()==0?bkg.getHeight():view.getHeight();
        int viewWidth = view.getWidth()==0?bkg.getWidth():view.getWidth();
        float radius = 2;
        float scaleFactor = 8;
        int width = bkg.getWidth();
        int height = (width*viewHeight)/viewWidth;
        int finalWidth = (int)(width/scaleFactor);
        int finalHeight = (int)(height/scaleFactor);
        if(finalWidth <= 0) finalWidth = 48;
        if(finalHeight <= 0) finalHeight = 48;
        Bitmap overlay = Bitmap.createBitmap(finalWidth,finalHeight , Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft()/scaleFactor, -view.getTop()/scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);
        overlay = FastBlur.doBlur(overlay, (int) radius, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(new BitmapDrawable(view.getResources(), overlay));
        }
        else {
            view.setBackgroundDrawable(new BitmapDrawable(view.getResources(), overlay));
        }
    }

    /**
     * stolen from fastBlur
     */
    public static class FastBlur {

        public static Bitmap doBlur(Bitmap sentBitmap, int radius,
                                    boolean canReuseInBitmap) {
            Bitmap bitmap;
            if (canReuseInBitmap) {
                bitmap = sentBitmap;
            } else {
                bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
            }

            if (radius < 1) {
                return (null);
            }

            int w = bitmap.getWidth();
            int h = bitmap.getHeight();

            int[] pix = new int[w * h];
            bitmap.getPixels(pix, 0, w, 0, 0, w, h);

            int wm = w - 1;
            int hm = h - 1;
            int wh = w * h;
            int div = radius + radius + 1;

            int r[] = new int[wh];
            int g[] = new int[wh];
            int b[] = new int[wh];
            int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
            int vmin[] = new int[Math.max(w, h)];

            int divsum = (div + 1) >> 1;
            divsum *= divsum;
            int dv[] = new int[256 * divsum];
            for (i = 0; i < 256 * divsum; i++) {
                dv[i] = (i / divsum);
            }

            yw = yi = 0;

            int[][] stack = new int[div][3];
            int stackpointer;
            int stackstart;
            int[] sir;
            int rbs;
            int r1 = radius + 1;
            int routsum, goutsum, boutsum;
            int rinsum, ginsum, binsum;

            for (y = 0; y < h; y++) {
                rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
                for (i = -radius; i <= radius; i++) {
                    p = pix[yi + Math.min(wm, Math.max(i, 0))];
                    sir = stack[i + radius];
                    sir[0] = (p & 0xff0000) >> 16;
                    sir[1] = (p & 0x00ff00) >> 8;
                    sir[2] = (p & 0x0000ff);
                    rbs = r1 - Math.abs(i);
                    rsum += sir[0] * rbs;
                    gsum += sir[1] * rbs;
                    bsum += sir[2] * rbs;
                    if (i > 0) {
                        rinsum += sir[0];
                        ginsum += sir[1];
                        binsum += sir[2];
                    } else {
                        routsum += sir[0];
                        goutsum += sir[1];
                        boutsum += sir[2];
                    }
                }
                stackpointer = radius;

                for (x = 0; x < w; x++) {

                    r[yi] = dv[rsum];
                    g[yi] = dv[gsum];
                    b[yi] = dv[bsum];

                    rsum -= routsum;
                    gsum -= goutsum;
                    bsum -= boutsum;

                    stackstart = stackpointer - radius + div;
                    sir = stack[stackstart % div];

                    routsum -= sir[0];
                    goutsum -= sir[1];
                    boutsum -= sir[2];

                    if (y == 0) {
                        vmin[x] = Math.min(x + radius + 1, wm);
                    }
                    p = pix[yw + vmin[x]];

                    sir[0] = (p & 0xff0000) >> 16;
                    sir[1] = (p & 0x00ff00) >> 8;
                    sir[2] = (p & 0x0000ff);

                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];

                    rsum += rinsum;
                    gsum += ginsum;
                    bsum += binsum;

                    stackpointer = (stackpointer + 1) % div;
                    sir = stack[(stackpointer) % div];

                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];

                    rinsum -= sir[0];
                    ginsum -= sir[1];
                    binsum -= sir[2];

                    yi++;
                }
                yw += w;
            }
            for (x = 0; x < w; x++) {
                rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
                yp = -radius * w;
                for (i = -radius; i <= radius; i++) {
                    yi = Math.max(0, yp) + x;

                    sir = stack[i + radius];

                    sir[0] = r[yi];
                    sir[1] = g[yi];
                    sir[2] = b[yi];

                    rbs = r1 - Math.abs(i);

                    rsum += r[yi] * rbs;
                    gsum += g[yi] * rbs;
                    bsum += b[yi] * rbs;

                    if (i > 0) {
                        rinsum += sir[0];
                        ginsum += sir[1];
                        binsum += sir[2];
                    } else {
                        routsum += sir[0];
                        goutsum += sir[1];
                        boutsum += sir[2];
                    }

                    if (i < hm) {
                        yp += w;
                    }
                }
                yi = x;
                stackpointer = radius;
                for (y = 0; y < h; y++) {
                    // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                    pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
                            | (dv[gsum] << 8) | dv[bsum];

                    rsum -= routsum;
                    gsum -= goutsum;
                    bsum -= boutsum;

                    stackstart = stackpointer - radius + div;
                    sir = stack[stackstart % div];

                    routsum -= sir[0];
                    goutsum -= sir[1];
                    boutsum -= sir[2];

                    if (x == 0) {
                        vmin[y] = Math.min(y + r1, hm) * w;
                    }
                    p = x + vmin[y];

                    sir[0] = r[p];
                    sir[1] = g[p];
                    sir[2] = b[p];

                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];

                    rsum += rinsum;
                    gsum += ginsum;
                    bsum += binsum;

                    stackpointer = (stackpointer + 1) % div;
                    sir = stack[stackpointer];

                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];

                    rinsum -= sir[0];
                    ginsum -= sir[1];
                    binsum -= sir[2];

                    yi += w;
                }
            }

            bitmap.setPixels(pix, 0, w, 0, 0, w, h);

            return (bitmap);
        }
    }
    /**
     * 截屏
     * @param view
     * @return
     */
    public static Bitmap getViewScreenshot(View view){
        if (view == null)
            return null;
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(measureSpec, measureSpec);

        if (view.getMeasuredWidth()<=0 || view.getMeasuredHeight()<=0) {
            return null;
        }
        Bitmap bm;
        try {
            bm = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        }catch (OutOfMemoryError e){
            System.gc();
            try {
                bm = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            }catch (OutOfMemoryError ee){
                return null;
            }
        }
        Canvas bigCanvas = new Canvas(bm);
        Paint paint = new Paint();
        int iHeight = bm.getHeight();
        bigCanvas.drawBitmap(bm, 0, iHeight, paint);
        view.draw(bigCanvas);
        return bm;
    }
    public static Bitmap getViewScreenshot(Activity context){
        View view = context.getWindow().getDecorView();
        Bitmap bm = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        view.draw(canvas);
        return bm;
    }


}
