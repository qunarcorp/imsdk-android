package com.qunar.im.ui.view.medias.record;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.qunar.im.ui.R;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.Utils;
import com.qunar.im.base.util.graphics.ImageUtils;
import com.qunar.im.ui.view.medias.play.MediaPlayerImpl;

/**
 * Created by saber on 15-11-19.
 */
public class VoiceView extends View {

    private static final String TAG = VoiceView.class.getName();

    private static final int STATE_NORMAL = 0;
    private static final int STATE_PRESSED = 1;
    private static final int STATE_CANCEL = 2;

    private Bitmap mNormalBitmap;
    private Bitmap mPressedBitmap;
    private Bitmap mCancelBitmap;
    private Paint mPaint;
    private Paint wPaint;
    private Paint iPaint;
    private AnimatorSet mAnimatorSet = new AnimatorSet();
    private OnRecordListener mOnRecordListener;

    private int mState = STATE_NORMAL;
    private boolean mIsRecording = false;
    private float mMinRadius;
    private float mMaxRadius;
    private float mCurrentRadius;

    private long downtime = -1;
    private Handler handle;

    private TextView status;

    private int bitmapSize;

    public VoiceView(Context context) {
        this(context, null);
    }

    public VoiceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoiceView(Context context,AttributeSet attrs,int def)
    {
        super(context,attrs,def);
        init();
    }

    public void setStatusView(TextView tv,Handler h)
    {
        this.status = tv;
        this.handle= h;
    }

    private void init(){

        mNormalBitmap = ImageUtils.drawableToBitmap(getResources().getDrawable(R.drawable.atom_ui_record_normal_bg));
        mPressedBitmap = ImageUtils.drawableToBitmap(getResources().getDrawable(R.drawable.atom_ui_record_press_bg));
        mCancelBitmap = ImageUtils.drawableToBitmap(getResources().getDrawable(R.drawable.atom_ui_cancel_send_bg));
        bitmapSize = mNormalBitmap.getWidth();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.rgb((int) ((51f / 255f) * 69 + (1 - 51f / 255f) * 255),
                (int) ((51f / 255f) * 206 + (1 - 51f / 255f) * 255),
                (int) ((51f / 255f) * 122 + (1 - 51f / 255f) * 255)));

        wPaint = new Paint();
        wPaint.setAntiAlias(true);
        wPaint.setColor(Color.WHITE);

        iPaint = new Paint();
        iPaint.setAntiAlias(true);
        iPaint.setColor(Color.rgb((int) ((102f / 255f) * 69 + (1 - 102f / 255f) * 255),
                (int) ((102f / 255f) * 206 + (1 - 102f / 255f) * 255),
                (int) ((102f / 255f) * 122 + (1 - 102f / 255f) * 255)));

        mMinRadius = Utils.dpToPx(getContext(), 120) / 2;
        mCurrentRadius = mMinRadius;
    }

    public void destroy()
    {
        if(mNormalBitmap!=null&&!mNormalBitmap.isRecycled()) {
            mNormalBitmap.recycle();
            mNormalBitmap = null;
        }
        if(mPressedBitmap!=null&&!mPressedBitmap.isRecycled())
        {
            mPressedBitmap.recycle();
            mPressedBitmap = null;
        }
        if(mCancelBitmap!=null&&!mCancelBitmap.isRecycled()) {
            mCancelBitmap.recycle();
            mCancelBitmap = null;
        }
    }

    public void stop()
    {
        mState = STATE_NORMAL;
        mIsRecording = false;
        if(status!=null)
        {
            status.setText(R.string.atom_ui_voice_hold_to_talk);
        }
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mMaxRadius = Math.min(w, h) / 2;
        LogUtil.d(TAG, "MaxRadius: " + mMaxRadius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = canvas.getWidth();
        int height = canvas.getHeight();
        int x = (width - bitmapSize)/2;
        int y = (height - bitmapSize)/2;

        if(mCurrentRadius > mMinRadius&&mState!=STATE_CANCEL){
            canvas.drawCircle(width / 2, height / 2, mCurrentRadius, mPaint);
            canvas.drawCircle(width/2,height/2,(mCurrentRadius+mMinRadius)/2,iPaint);
        }

        switch (mState){
            case STATE_NORMAL:
                if(mNormalBitmap!= null)
                    canvas.drawBitmap(mNormalBitmap, x,y, wPaint);
                break;
            case STATE_PRESSED:
                if(mPressedBitmap!=null)
                    canvas.drawBitmap(mPressedBitmap, x, y, wPaint);
                break;
            case STATE_CANCEL:
                if(mCancelBitmap!=null)
                    canvas.drawBitmap(mCancelBitmap, x,  y, wPaint);
                break;
        }
    }

    public void animateRadius(float radius){
        if(radius <= mCurrentRadius){
            return;
        }
        if(radius > mMaxRadius){
            radius = mMaxRadius;
        }else if(radius < mMinRadius){
            radius = mMinRadius;
        }
        if(radius == mCurrentRadius){
            return;
        }
        if(mAnimatorSet.isRunning()){
            mAnimatorSet.cancel();
        }
        mAnimatorSet.playSequentially(
                ObjectAnimator.ofFloat(this, "CurrentRadius", getCurrentRadius(), radius).setDuration(50),
                ObjectAnimator.ofFloat(this, "CurrentRadius", radius, mMinRadius).setDuration(600)
        );
        mAnimatorSet.start();
    }

    public float getCurrentRadius() {
        return mCurrentRadius;
    }

    public void setCurrentRadius(float currentRadius) {
        mCurrentRadius = currentRadius;
        invalidate();
    }
    private boolean checkPressedPosition(float x, float y) {
        return x >= 0 && y >= 0 && x <= getWidth() && y <= getHeight();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                MediaPlayerImpl.getInstance().stopPlay();
                downtime = System.currentTimeMillis();
                LogUtil.d(TAG, "ACTION_DOWN");
                mState = STATE_PRESSED;
                mIsRecording = true;
                if(mOnRecordListener != null){
                    mOnRecordListener.onRecordStart();
                }
                if(this.status!=null)
                {
                    this.status.setText(R.string.atom_ui_voice_release_to_send);
                }
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                LogUtil.d(TAG, "ACTION_UP");
                if ( downtime > 0 && System.currentTimeMillis() - downtime > 500
                    && checkPressedPosition(event.getX(), event.getY())
                    &&mIsRecording){
                    mState = STATE_NORMAL;
                    if(mOnRecordListener != null){
                        mOnRecordListener.onRecordFinish();
                    }
                }
                else {
                    if(mOnRecordListener != null){
                        mOnRecordListener.onRecordCancel();
                    }
                    mState = STATE_NORMAL;
                    if(mIsRecording)
                    {
                        if(this.status!=null)
                        {
                            this.status.setText(R.string.atom_ui_voice_too_short);
                        }
                        mState = STATE_CANCEL;
                        invalidate();
                        handle.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(status!=null)
                                {
                                    status.setText(R.string.atom_ui_voice_hold_to_talk);
                                }
                                mState = STATE_NORMAL;
                                invalidate();
                            }
                        },500);
                        return true;
                    }
                }
                downtime = 0;
                if(this.status!=null)
                {
                    this.status.setText(R.string.atom_ui_voice_hold_to_talk);
                }
                mIsRecording = false;
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:

                    if (!checkPressedPosition( event.getX(), event.getY())) {
                        mState = STATE_CANCEL;
                        mIsRecording = false;
                        if(this.status!=null)
                        {
                            this.status.setText(R.string.atom_ui_voice_release_to_cancel);
                        }
                    } else {
                        mState = STATE_PRESSED;
                        mIsRecording = true;
                        if(this.status!=null)
                        {
                            this.status.setText(R.string.atom_ui_voice_release_to_send);
                        }
                    }
                invalidate();
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    public void setOnRecordListener(OnRecordListener onRecordListener) {
        mOnRecordListener = onRecordListener;
    }

    public static interface OnRecordListener{
        void onRecordStart();
        void onRecordFinish();
        void onRecordCancel();
    }
}
