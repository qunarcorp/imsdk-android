package com.qunar.im.ui.view.baseView;

import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.rastermill.Cacheable;

import com.qunar.im.base.util.MemoryCache;

import java.io.InputStream;


/**
 * 
 * @author tracyZhang  https://github.com/TracyZhangLei
 * @since  2014-4-4
 *
 */

public class AnimatedGifDrawable extends AnimationDrawable implements Cacheable {

    private int mCurrentIndex = 0;

    private int byteCount;

//    FrameSequenceDrawable frameSequenceDrawable;

    public AnimatedGifDrawable(InputStream source,final int iconSize) {
        GifDecoder decoder = new GifDecoder();
        decoder.read(source);
        // Iterate through the gif frames, add each as animation frame
        for (int i = 0; i < decoder.getFrameCount(); i++) {
            Bitmap bitmap = decoder.getFrame(i);
            byteCount+=bitmap.getByteCount();
            BitmapDrawable drawable = new BitmapDrawable(bitmap);
            // Explicitly set the bounds in order for the frames to display
            drawable.setBounds(0, 0,Math.max(iconSize,bitmap.getWidth()),
                    Math.max(iconSize,bitmap.getHeight()));
            addFrame(drawable, decoder.getDelay(i));
            if (i == 0) {
                // Also set the bounds for this container drawable
                setBounds(0, 0, Math.max(iconSize, bitmap.getWidth()),
                        Math.max(iconSize, bitmap.getHeight()));
            }
        }
    }


    public int getByteCount()
    {
        return byteCount;
    }

    /**
     * Naive method to proceed to next frame. Also notifies listener.
     */
    public void nextFrame() {
        mCurrentIndex = (mCurrentIndex + 1) % getNumberOfFrames();
    }

    /**
     * Return display duration for current frame
     */
    public int getFrameDuration() {
        return getDuration(mCurrentIndex);
    }

    /**
     * Return drawable for current frame
     */
    public Drawable getDrawable() {
        return getFrame(mCurrentIndex);
//        return frameSequenceDrawable;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this,flags);
    }



    public static final Parcelable.Creator<AnimatedGifDrawable> CREATOR
            = new Parcelable.Creator<AnimatedGifDrawable>() {
        /**
         * Rebuilds a bitmap previously stored with writeToParcel().
         *
         * @param p    Parcel object to read the bitmap from
         * @return a new bitmap created from the data in the parcel
         */
        public AnimatedGifDrawable createFromParcel(Parcel p) {
            AnimatedGifDrawable drawable = p.readParcelable(ClassLoader.getSystemClassLoader());
            if (drawable == null) {
                throw new RuntimeException("Failed to unparcel Bitmap");
            }
            return drawable;
        }
        public AnimatedGifDrawable[] newArray(int size) {
            return new AnimatedGifDrawable[size];
        }
    };
}
