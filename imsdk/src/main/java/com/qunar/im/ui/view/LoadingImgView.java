package com.qunar.im.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.TypedValue;

/**
 * Created by hubo.hu on 2017/10/9.
 */

public class LoadingImgView extends AppCompatImageView{

    private float per;

    private boolean isfinished = false;

    private String colorStr;

    private Paint paintLayer;
    private Paint paintLayerbG;
    private Paint textPaint;

    private Rect textbound;

    private float layer_w;
    private float layer_h;


//    private BubbleDrawable bubbleDrawable;
//    private Drawable sourceDrawable;
//    private float mArrowWidth;
//    private float mAngle;
//    private float mArrowHeight;
//    private float mArrowPosition;
//    private Bitmap mBitmap;
//    private BubbleDrawable.ArrowLocation mArrowLocation;
//    private boolean mArrowCenter;
//    private boolean isBubble;

    public LoadingImgView(Context context) {
        super(context);
        init(context);
    }

    public LoadingImgView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadingImgView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    //初始化画笔
    private void init(Context context){
//        mArrowWidth = dp2px(context,8);
//        mArrowHeight = dp2px(context,10);
//        mArrowLocation = BubbleDrawable.ArrowLocation.mapIntToValue(0x00);
//        mAngle = dp2px(context,4);
//        mArrowPosition = dp2px(context,8);

        paintLayer = new Paint();
        paintLayer.setColor(Color.DKGRAY);
        paintLayer.setAlpha(100);
        paintLayerbG = new Paint();
        paintLayerbG.setColor(Color.BLACK);
        paintLayerbG.setAlpha(50);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(35);
        textbound = new Rect();
    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int width = getMeasuredWidth();
//        int height = getMeasuredHeight();
//        if (width <= 0 && height > 0){
//            setMeasuredDimension(height , height);
//        }
//        if (height <= 0 && width > 0){
//            setMeasuredDimension(width , width);
//        }
//    }
//
//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//        if (w > 0 && h > 0){
//            setUp(w, h);
//        }
//    }
//
//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
//        setUp();
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isfinished)
            return;
        String perStr = (int) (per) + "%";
        //获取文字区域的矩形大小，以便确定文字正中间的位置
        textPaint.getTextBounds(perStr,0, perStr.length(),textbound);
        layer_w = getWidth();
        layer_h = getHeight() * per / 100;
        float y = getHeight() - layer_h;
        //画遮蔽层
        canvas.drawRect(0,y,layer_w,getHeight(),paintLayer);
        canvas.drawRect(0,0,layer_w,getHeight(),paintLayerbG);
        //画文字
        canvas.drawText(perStr, getWidth() / 2 - textbound.width() / 2, getHeight() / 2 + textbound.height() / 2, textPaint);

//        int saveCount = canvas.getSaveCount();
//        canvas.translate(getPaddingLeft(), getPaddingTop());
//        if (bubbleDrawable != null)
//            bubbleDrawable.draw(canvas);
//        canvas.restoreToCount(saveCount);
    }
//
//    private void setUp(int left, int right, int top, int bottom){
//        if (right <= left || bottom <= top)
//            return;
//
//        RectF rectF = new RectF(left, top, right, bottom);
//        if (sourceDrawable != null)
//            mBitmap = getBitmapFromDrawable(sourceDrawable);
//        bubbleDrawable = new BubbleDrawable.Builder()
//                .rect(rectF)
//                .arrowLocation(mArrowLocation)
//                .angle(mAngle)
//                .arrowHeight(mArrowHeight)
//                .arrowWidth(mArrowWidth)
//                .bubbleType(BubbleDrawable.BubbleType.BITMAP)
//                .arrowPosition(mArrowPosition)
//                .bubbleBitmap(mBitmap)
//                .arrowCenter(mArrowCenter)
//                .build();
//    }
//
//    private void setUp(int width, int height){
//        setUp(getPaddingLeft(), width - getPaddingRight(),
//                getPaddingTop(), height - getPaddingBottom());
//    }
//
//    private void setUp(){
//        int width = getWidth();
//        int height = getHeight();
//        int scale;
//
//        if (width > 0 && height <= 0 && sourceDrawable != null){
//            if (sourceDrawable.getIntrinsicWidth() >= 0){
//                scale = width / sourceDrawable.getIntrinsicWidth();
//                height = scale * sourceDrawable.getIntrinsicHeight();
//            }
//        }
//
//        if (height > 0 &&  width <= 0 && sourceDrawable != null){
//            if (sourceDrawable.getIntrinsicHeight() >= 0){
//                scale = height / sourceDrawable.getIntrinsicHeight();
//                width = scale * sourceDrawable.getIntrinsicWidth();
//            }
//        }
//        setUp(width, height);
//    }
//
//    @Override
//    public void setImageBitmap(Bitmap mBitmap) {
//        if (mBitmap == null)
//            return;
//        this.mBitmap = mBitmap;
//        sourceDrawable = new BitmapDrawable(getResources(), mBitmap);
//        setUp();
//        super.setImageDrawable(bubbleDrawable);
//    }
//
//    @Override
//    public void setImageDrawable(Drawable drawable){
//        if (drawable == null )
//            return;
//        sourceDrawable = drawable;
//        setUp();
//        super.setImageDrawable(bubbleDrawable);
//    }
//
//    @Override
//    public void setImageResource(int res){
//        setImageDrawable(getDrawable(res));
//    }
//
//    private Drawable getDrawable(int res){
//        if (res == 0){
//            throw new IllegalArgumentException("getDrawable res can not be zero");
//        }
//        return getContext().getResources().getDrawable(res);
//    }
//
//    private Bitmap getBitmapFromDrawable(Drawable drawable) {
//        return getBitmapFromDrawable(getContext(), drawable, getWidth(), getWidth(), 25);
//    }
//
//    public static Bitmap getBitmapFromDrawable(Context mContext, Drawable drawable, int width, int height, int defaultSize) {
//        if (drawable == null) {
//            return null;
//        }
//        if (drawable instanceof BitmapDrawable) {
//            return ((BitmapDrawable) drawable).getBitmap();
//        }
//        try {
//            Bitmap bitmap;
//            if (width > 0 && height > 0){
//                bitmap = Bitmap.createBitmap(width,
//                        height, Bitmap.Config.ARGB_8888);
//            }else{
//                bitmap = Bitmap.createBitmap(dp2px(mContext, defaultSize),
//                        dp2px(mContext, defaultSize), Bitmap.Config.ARGB_8888);
//            }
//            Canvas canvas = new Canvas(bitmap);
//            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
//            drawable.draw(canvas);
//            return bitmap;
//        } catch (OutOfMemoryError e) {
//            return null;
//        }
//    }

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    public void setPer(float per){
        isfinished = false;
        this.per = per;
        //在主线程刷新
        postInvalidate();
    }

    public void finish(){
        isfinished = true;
        postInvalidate();
    }

}
