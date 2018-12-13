package com.qunar.im.ui.view.baseView;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.DynamicDrawableSpan;
import android.widget.TextView;

import com.qunar.im.base.common.QunarIMApp;

import java.lang.ref.WeakReference;


/**
 * 
 * @author tracyZhang  https://github.com/TracyZhangLei
 * @since  2014-4-4
 *
 */
public class AnimatedImageSpan extends DynamicDrawableSpan {

    /**
     * Interface to notify listener to update/redraw
     * Can't figure out how to invalidate the drawable (or span in which it sits) itself to force redraw
     */
    public interface UpdateListener {
        void update();
    }

    private UpdateListener mListener;

    public void setListener(UpdateListener listener)
    {
        this.mListener = listener;
    }

    private Drawable mDrawable;

    public AnimatedImageSpan(Drawable d , final WeakReference<TextView> weakReference) {
        super();
        mDrawable = d;
        QunarIMApp.mainHandler.post(new Runnable() {
            public void run() {
                TextView  tv = weakReference.get();
                if (tv == null || !tv.isShown()) {
                    return;
                }
                ((AnimatedGifDrawable) mDrawable).nextFrame();
                if(mListener!=null)mListener.update();
                // Set next with a delay depending on the duration for this frame
                QunarIMApp.mainHandler.postDelayed(this, ((AnimatedGifDrawable) mDrawable).getFrameDuration());
            }
        });
    }

    /*
     * Return current frame from animated drawable. Also acts as replacement for super.getCachedDrawable(),
     * since we can't cache the 'image' of an animated image.
     */
    @Override
    public Drawable getDrawable() {
        return ((AnimatedGifDrawable)mDrawable).getDrawable();
    }

    /*
     * Copy-paste of super.getSize(...) but use getDrawable() to get the image/frame to calculate the size,
     * in stead of the cached drawable.
     */
    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        Drawable d = getDrawable();
        Rect rect = d.getBounds();

        if (fm != null) {
            fm.ascent = -rect.bottom; 
            fm.descent = 0; 

            fm.top = fm.ascent;
            fm.bottom = 0;
        }

        return rect.right;
    }

    /*
     * Copy-paste of super.draw(...) but use getDrawable() to get the image/frame to draw, in stead of
     * the cached drawable.
     */
    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        Drawable b = getDrawable();
        canvas.save();

        int transY = bottom - b.getBounds().bottom;
        if (mVerticalAlignment == ALIGN_BASELINE) {
            transY -= paint.getFontMetricsInt().descent;
        }

        canvas.translate(x, transY);
        b.draw(canvas);
        canvas.restore();

    }

}
