package com.qunar.im.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.orhanobut.logger.Logger;
import com.qunar.im.base.util.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by xinbo.wang on 2015/5/15.
 */
public class QRUtil {
    private static final String TAG = QRUtil.class.getSimpleName();
    private static final int QR_HEIGHT = 192;
    private static final int QR_WIDTH = 192;

    public static Bitmap generateQRImageWihtDes(String source, String desc) {
        Bitmap bitmap = generateQRImage(source);
        Canvas canvas = new Canvas();

        return bitmap;
    }


    public static String generateQRBase64(String source){
        return bitmapToBase64(generateQRImage(source));
    }

    public static Bitmap generateQRImage(String source)
    {
        return generateQRImage(source,QR_WIDTH,QR_HEIGHT);
    }

    public static Bitmap generateQRImage(String source,int width,int height)
    {
        try
        {
            if(TextUtils.isEmpty(source))
                return null;
            Map<EncodeHintType,String> hints = new Hashtable<EncodeHintType,String>();
            hints.put(EncodeHintType.CHARACTER_SET,"utf-8");
            BitMatrix bitMatrix = new QRCodeWriter().encode(source, BarcodeFormat.QR_CODE,width,height,hints);
            int[] pixels = new int[width*height];

            for(int y=0;y<height;y++)
            {
                for(int x=0;x<width;x++)
                {
                    if(bitMatrix.get(x,y))
                    {
                        pixels[y*width+x] = Color.BLACK;
                    }
                    else {
                        pixels[y*width +x] = Color.WHITE;
                    }
                }
            }

            Bitmap QRBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            QRBitmap.setPixels(pixels,0,width,0,0,width,height);
            return QRBitmap;
        } catch (Exception e) {
            LogUtil.e(TAG,"ERROR",e);
        }
        return null;
    }

    public static String cognitiveQR(Bitmap bitmap)
    {
        if(bitmap==null) return "";
        String decodeStr = "";
        Hashtable<DecodeHintType,Object> hints = new Hashtable<>();
        hints.put(DecodeHintType.CHARACTER_SET,"utf-8");
        hints.put(DecodeHintType.TRY_HARDER,Boolean.TRUE);
        if(bitmap.getWidth()<200)
        {
            Matrix matrix = new Matrix();
            matrix.postScale(2,2);
            bitmap = Bitmap.createBitmap(
                    bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        }
        int[] pixels = new int[bitmap.getWidth()*bitmap.getHeight()];
        bitmap.getPixels(pixels,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
        RGBLuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(),bitmap.getHeight(),pixels);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            Result result = reader.decode(binaryBitmap, hints);
            decodeStr = result.getText();
        } catch (NotFoundException e) {
            Logger.i(TAG + "ERROR=%s", e.getMessage());
        } catch (ChecksumException e) {
            Logger.i(TAG + "ERROR=%s", e.getMessage());
        } catch (FormatException e) {
            Logger.i(TAG + "ERROR=%s", e.getMessage());
        } catch (Exception e) {
            Logger.i(TAG + "ERROR=%s", e.getMessage());
        }
        return decodeStr;
    }


    /**
     * bitmap转为base64
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result ="data:image/jpg;base64,"+ Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
