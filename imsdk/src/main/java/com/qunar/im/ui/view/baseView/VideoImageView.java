package com.qunar.im.ui.view.baseView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.Utils;
import com.qunar.im.ui.R;
import com.qunar.im.base.util.graphics.BitmapHelper;

/**
 * Created by saber on 15-8-19.
 */
public class VideoImageView extends SimpleDraweeView {
    private static final String TAG = VideoImageView.class.getSimpleName();
   public Bitmap icPlay;
    Paint paint,textpaint;
    String fileSize,duration;
    float textwidth,textheight,padding,durationWidth,horizationPadding;
    public VideoImageView(Context context) {
        this(context,null);
    }

    public VideoImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VideoImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setVideoInfo(String  size,String inDuration)
    {
        fileSize = size;
        textwidth = textpaint.measureText(fileSize);
        textheight = Utils.dipToPixels(getContext(),8);
        padding = Utils.dipToPixels(getContext(),8);
        horizationPadding = Utils.dipToPixels(getContext(),4);
        int d =0;
        try {
            d = Integer.parseInt(inDuration);
        }
        catch (Exception ex)
        {
            LogUtil.e(TAG,"ERROR",ex);
        }
        this.duration = d/60 + ":";
        this.duration += String.format("%02d", d%60);
        durationWidth = textpaint.measureText(this.duration);
    }
    void init()
    {
        icPlay = BitmapHelper.decodeResource(this.getContext().getResources(), R.drawable.atom_ui_ic_video_play);
        paint = new Paint();
        paint.setAlpha(225);
        textpaint = new Paint();
        textpaint.setTextSize(Utils.dpToPx(getContext(),12));
        textpaint.setTypeface(Typeface.DEFAULT_BOLD);
        textpaint.setStrokeWidth(0);
        textpaint.setColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        float top = (getMeasuredHeight() - icPlay.getHeight())/2;
        float left = (getMeasuredWidth() - icPlay.getWidth())/2;
        canvas.drawBitmap(icPlay,left,top,paint);
        canvas.drawText(fileSize,horizationPadding ,
                canvas.getHeight()-textheight-padding, textpaint);
        canvas.drawText(this.duration,canvas.getWidth()-durationWidth-horizationPadding,
                canvas.getHeight()-textheight-padding,textpaint);
    }

}
