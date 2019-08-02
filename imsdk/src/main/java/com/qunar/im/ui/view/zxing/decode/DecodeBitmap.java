package com.qunar.im.ui.view.zxing.decode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Hashtable;

/**
 * Created by HDL on 2017/6/27.
 */

public class DecodeBitmap {
    private static final String TAG = DecodeBitmap.class.getSimpleName();

    public static Result scanningImage(Bitmap scanBitmap) throws Exception {
        if (scanBitmap == null) {
            return null;
        }
        Hashtable<DecodeHintType, Object> hints = new Hashtable<>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); //设置二维码内容的编码
        hints.put(DecodeHintType.TRY_HARDER,Boolean.TRUE);
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(bitmap1, hints);
        } catch (NotFoundException e) {
            throw  e;
        } catch (ChecksumException e) {
            throw  e;
        } catch (FormatException e) {
            throw  e;
        } finally {
            if(scanBitmap != null) {
                scanBitmap.recycle();
                scanBitmap = null;
            }
        }
    }

    /**
     * 扫描二维码图片的方法
     * @param path
     * @return
     */
    public static Result scanningImage(String path) {
        if(TextUtils .isEmpty(path)){
            return null;
        }
        try {
            Bitmap scanBitmap = BitmapFactory.decodeFile(path);
            return scanningImage(scanBitmap);
        }catch (Exception e){
            return scanningImageScale(path);
        }
    }

    public static Result scanningImageScale(String path){
        if(TextUtils .isEmpty(path)){
            return null;
        }
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true; // 先获取原大小
            Bitmap scanBitmap = BitmapFactory.decodeFile(path, options);
            options.inJustDecodeBounds = false; // 获取新的大小
            int sampleSize = (int) (options.outHeight / (float) 200);
            if (sampleSize <= 0)
                sampleSize = 1;
            options.inSampleSize = sampleSize;
            scanBitmap = BitmapFactory.decodeFile(path, options);
            return scanningImage(scanBitmap);
        }catch (Exception e){
            return null;
        }
    }

    /**
     * 中文乱码处理
     *
     * @param str 结果
     * @return
     */
    public static String parseReuslt(String str) {
        String formart = "";
        try {
            boolean ISO = Charset.forName("ISO-8859-1").newEncoder().canEncode(str);
            if (ISO) {
                formart = new String(str.getBytes("ISO-8859-1"), "GB2312");
            } else {
                formart = str;
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return formart;
    }
}
