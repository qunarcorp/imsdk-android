package com.qunar.im.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

import com.qunar.im.base.util.Utils;

/**
 * Created by xinbo.wang on 2015/5/25.
 */
public class ClipView extends View {
    private Paint paint = new Paint();
    private Paint borderPaint = new Paint();

    /** 自定义顶部栏高度，如不是自定义，则默认为0即可 */
    private int customTopBarHeight = 0;
    /** 裁剪框长宽比，默认 4:3 */
    private double clipRatio = 0.75;
    /** 裁剪框宽度 */
    private int clipWidth = -1;
    /** 裁剪框高度 */
    private int clipHeight = -1;
    /** 裁剪框左边空留宽度 */
    private int clipLeftMargin = 0;
    /** 裁剪框上边空留宽度 */
    private int clipTopMargin = 0;
    /** 裁剪框边框宽度 */
    private int clipBorderWidth;
    private boolean isSetMargin = false;
    private OnDrawListenerComplete listenerComplete;

    public ClipView(Context context) {
        this(context, null);
    }

    public ClipView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClipView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        clipBorderWidth = Utils.dipToPixels(context,1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = this.getWidth();
        int height = this.getHeight();
        // 如没有显示设置裁剪框高度和宽度，取默认值
        if (clipWidth == -1 || clipHeight == -1) {
            clipWidth = width - 50;
            clipHeight = (int) (clipWidth * clipRatio);
            // 横屏
            if (width > height) {
                clipHeight = height - 50;
                clipWidth = (int) (clipHeight / clipRatio);
            }
        }
        // 如没有显示设置裁剪框左和上预留宽度，取默认值
        if (!isSetMargin) {
            clipLeftMargin = (width - clipWidth) / 2;
            clipTopMargin = (height - clipHeight) / 2;
        }
        // 画阴影
        paint.setAlpha(24);
        // top
        canvas.drawRect(0, customTopBarHeight, width, clipTopMargin, paint);
        // left
        canvas.drawRect(0, clipTopMargin, clipLeftMargin, clipTopMargin
                + clipHeight, paint);
        // right
        canvas.drawRect(clipLeftMargin + clipWidth, clipTopMargin, width,
                clipTopMargin + clipHeight, paint);
        // bottom
        canvas.drawRect(0, clipTopMargin + clipHeight, width, height, paint);

        // 画边框
        borderPaint.setStyle(Style.STROKE);
        borderPaint.setColor(Color.WHITE);
        borderPaint.setStrokeWidth(clipBorderWidth);
        canvas.drawRect(clipLeftMargin, clipTopMargin, clipLeftMargin
                + clipWidth, clipTopMargin + clipHeight, borderPaint);

        if (listenerComplete != null) {
            listenerComplete.onDrawCompelete();
        }
    }

    public int getCustomTopBarHeight() {
        return customTopBarHeight;
    }

    public void setCustomTopBarHeight(int customTopBarHeight) {
        this.customTopBarHeight = customTopBarHeight;
    }

    public double getClipRatio() {
        return clipRatio;
    }

    public void setClipRatio(double clipRatio) {
        this.clipRatio = clipRatio;
    }

    public int getClipWidth() {
        // 减clipBorderWidth原因：截图时去除边框白线
        return clipWidth - clipBorderWidth;
    }

    public void setClipWidth(int clipWidth) {
        this.clipWidth = clipWidth;
    }

    public int getClipHeight() {
        return clipHeight - clipBorderWidth;
    }

    public void setClipHeight(int clipHeight) {
        this.clipHeight = clipHeight;
    }

    public int getClipLeftMargin() {
        return clipLeftMargin + clipBorderWidth;
    }

    public void setClipLeftMargin(int clipLeftMargin) {
        this.clipLeftMargin = clipLeftMargin;
        isSetMargin = true;
    }

    public int getClipTopMargin() {
        return clipTopMargin + clipBorderWidth;
    }

    public void setClipTopMargin(int clipTopMargin) {
        this.clipTopMargin = clipTopMargin;
        isSetMargin = true;
    }

    public void addOnDrawCompleteListener(OnDrawListenerComplete listener) {
        this.listenerComplete = listener;
    }

    public void removeOnDrawCompleteListener() {
        this.listenerComplete = null;
    }

    /**
     * 裁剪区域画完时调用接口
     *
     * @author Cow
     *
     */
    public interface OnDrawListenerComplete {
        public void onDrawCompelete();
    }

}
