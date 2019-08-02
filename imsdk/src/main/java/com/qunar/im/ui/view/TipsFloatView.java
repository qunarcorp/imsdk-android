package com.qunar.im.ui.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.util.Utils;
import com.qunar.im.ui.R;

/**
 * 悬浮的tips view支持动画
 * create by lihaibin
 */
public class TipsFloatView extends LinearLayout implements GestureDetector.OnGestureListener{
    private TextView text;
    private boolean isShrinkState;
    private GestureDetector gestureDetector;
    private OnClickListener clickListener;
    public TipsFloatView(Context context) {
        this(context, null);
    }

    public TipsFloatView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TipsFloatView(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        gestureDetector = new GestureDetector(context, this);

        View view = LayoutInflater.from(context).inflate(R.layout.atom_ui_view_search_line,null);
        text = (TextView) view.findViewById(R.id.text);
        int size = Utils.dipToPixels(context, 40);
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams.setMargins(0, size * 4, 0, 0);
        setLayoutParams(layoutParams);

        addView(view);
    }

    public void setClickListener(OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if(isShrinkState){
            startScaloutAnim();
        }else {
            if(clickListener != null){
                clickListener.onClick(this);
            }
        }
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Logger.i("onFling：" + velocityX + ":" +velocityY);
        if(velocityX > 200 && !isShrinkState){
            startScalInAnim();
        }else if(velocityX < -200 && isShrinkState){
            startScaloutAnim();
        }
        return true;
    }

    private void startScalInAnim(){
        int x = text.getWidth() - 5;
        ObjectAnimator anim = ObjectAnimator.ofFloat(this, "translationX", 0, x);
        anim.setDuration(800);
        anim.start();
        isShrinkState = true;
    }

    private void startScaloutAnim(){
        int x = text.getWidth() - 5;
        ObjectAnimator anim = ObjectAnimator.ofFloat(this, "translationX", x,0);
        anim.setDuration(800);
        anim.start();
        isShrinkState = false;
    }
}
