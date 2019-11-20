package com.qunar.im.ui.view;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

public class WorkWorldSpannableTextView extends TextView {

    private  WorkWorldLinkTouchMovementMethod workWorldLinkTouchMovementMethod;

    long down = 0;
    long up = 0;

    public WorkWorldSpannableTextView(Context context) {
        super(context);
    }

    public WorkWorldSpannableTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WorkWorldSpannableTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public WorkWorldSpannableTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);
        Logger.i("点击事件:系统结果1:"+result);
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            down = System.currentTimeMillis();
            Logger.i("点击事件:down:"+down);
        }else if(event.getAction()==MotionEvent.ACTION_UP){
            up = System.currentTimeMillis();
            Logger.i("点击事件:up:"+up);
            long rs = up -down;
            Logger.i("点击事件:rs:"+rs);
            Logger.i("点击事件:事件:"+workWorldLinkTouchMovementMethod);
            if(rs<=500){
                boolean methodResult = workWorldLinkTouchMovementMethod != null ? workWorldLinkTouchMovementMethod.isPressedSpan() : result;
                Logger.i("点击事件:methodResult:"+methodResult);
                return methodResult;
            }else{
                return result;
            }

        }

        Logger.i("点击事件:系统结果2:"+result);
//        return workWorldLinkTouchMovementMethod != null ? workWorldLinkTouchMovementMethod.isPressedSpan() : result;
        return result;
    }

    public void setLinkTouchMovementMethod(WorkWorldLinkTouchMovementMethod linkTouchMovementMethod) {
        workWorldLinkTouchMovementMethod = linkTouchMovementMethod;
    }
}
