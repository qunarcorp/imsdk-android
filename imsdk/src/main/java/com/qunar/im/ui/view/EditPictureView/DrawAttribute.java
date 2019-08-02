package com.qunar.im.ui.view.EditPictureView;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;

import com.qunar.im.base.util.LogUtil;

import java.io.IOException;
import java.io.InputStream;

public class DrawAttribute
{
    private static final String TAG = DrawAttribute.class.getSimpleName();
    public static enum Corner
    {
        LEFTTOP, RIGHTTOP, LEFTBOTTOM, RIGHTBOTTOM, ERROR
    }

    public static enum DrawStatus
    {
        PEN_NORMAL,PEN_WATER, PEN_CRAYON, PEN_COLOR_BIG, PEN_ERASER,PEN_STAMP
    }

    public final static int backgroundOnClickColor = 0xfff08d1e;
    public static int screenHeight;
    public static int screenWidth;
    public static Paint paint = new Paint();

    public static Bitmap getImageFromAssetsFile(Context context,
                                                String fileName, boolean isBackground)
    {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try
        {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e)
        {
            LogUtil.e(TAG,"ERROR",e);
        }
        if (isBackground)
            image = Bitmap.createScaledBitmap(image, DrawAttribute.screenWidth,
                    DrawAttribute.screenHeight, false);
        return image;

    }
}