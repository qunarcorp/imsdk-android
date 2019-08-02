package com.qunar.im.ui.view.facebookimageview.zoomable;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.qunar.im.ui.view.facebookimageview.gestures.TransformGestureDetector;

public class DoubleTapZoomController extends DefaultZoomableController
        implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private GestureDetector mGestureDetector;
    private static View mView;

    public DoubleTapZoomController(TransformGestureDetector gestureDetector, Context context) {
        super(gestureDetector);
        mGestureDetector = new GestureDetector(context, this);
        mGestureDetector.setOnDoubleTapListener(this);
    }

    public static DoubleTapZoomController newInstance(Context context,View view) {
        mView = view;
        return new DoubleTapZoomController(TransformGestureDetector.newInstance(), context);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
       if(((ZoomableDraweeView)mView).getmHomeActvity()!=null){
            ((ZoomableDraweeView)mView).getmHomeActvity().finish();
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {

        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
/*        PointF point = mapViewToImage(new PointF(event.getX(), event.getY()));
        if (getScaleFactor() < 2.0f) {
            zoomToImagePoint(2.0f, point);
        } else {
            zoomToImagePoint(1.0f, point);
        }
        return true;*/
        return false;
    }

    @Override
    public boolean onDown(MotionEvent event) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent event) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent event, MotionEvent event1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        if(mView!=null){
            mView.performLongClick();
        }
    }

    @Override
    public boolean onFling(MotionEvent event, MotionEvent event1, float v, float v1) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isEnabled()) {
            mGestureDetector.onTouchEvent(event);
            return super.onTouchEvent(event);
        }
        return false;
    }

}