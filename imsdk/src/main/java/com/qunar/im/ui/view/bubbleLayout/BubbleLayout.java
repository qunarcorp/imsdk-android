package com.qunar.im.ui.view.bubbleLayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.FrameLayout;

import com.qunar.im.ui.R;

/**
 * Bubble View for Android with custom stroke width and color, arrow size, position and direction.
 * Created by sudamasayuki on 16/04/04.
 */
public class BubbleLayout extends FrameLayout {

    public static float DEFAULT_STROKE_WIDTH = -1;

    private Context context;

    private ArrowDirection mArrowDirection;
    private Bubble mBubble;

    private float mArrowWidth;
    private float mCornersRadius;
    private float mArrowHeight;
    private float mArrowPosition;
    private int mBubbleColor;
    private float mStrokeWidth;
    private int mStrokeColor;

    public BubbleLayout(Context context) {
        this(context, null, 0);
    }

    public BubbleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BubbleLayout);
        mArrowWidth = a.getDimension(R.styleable.BubbleLayout_bl_arrowWidth,
                convertDpToPixel(8, context));
        mArrowHeight = a.getDimension(R.styleable.BubbleLayout_bl_arrowHeight,
                convertDpToPixel(8, context));
        mCornersRadius = a.getDimension(R.styleable.BubbleLayout_bl_cornersRadius, 0);
        mArrowPosition = a.getDimension(R.styleable.BubbleLayout_bl_arrowPosition,
                convertDpToPixel(12, context));
        mBubbleColor = a.getColor(R.styleable.BubbleLayout_bl_bubbleColor, Color.WHITE);

        mStrokeWidth =
                a.getDimension(R.styleable.BubbleLayout_bl_strokeWidth, DEFAULT_STROKE_WIDTH);
        mStrokeColor = a.getColor(R.styleable.BubbleLayout_bl_strokeColor, Color.GRAY);

        int location = a.getInt(R.styleable.BubbleLayout_bl_arrowDirection, ArrowDirection.LEFT.getValue());
        mArrowDirection = ArrowDirection.fromInt(location);

        a.recycle();
        initPadding();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        initDrawable(0, getWidth(), 0, getHeight());
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mBubble != null) mBubble.draw(canvas);
        super.dispatchDraw(canvas);
    }

    private void initDrawable(int left, int right, int top, int bottom) {
        if (right < left || bottom < top) return;

        RectF rectF = new RectF(left, top, right, bottom);
        switch(mArrowDirection) {
            case LEFT_CENTER:
            case RIGHT_CENTER:
                mArrowPosition = (bottom - top) / 2 - mArrowHeight / 2;
                break;
            case TOP_CENTER:
            case BOTTOM_CENTER:
                mArrowPosition = (right - left) / 2 - mArrowWidth / 2;
            default:
                break;
        }
        mBubble = new Bubble(rectF, mArrowWidth, mCornersRadius, mArrowHeight, mArrowPosition,
                mStrokeWidth, mStrokeColor, mBubbleColor, mArrowDirection);
    }

    private void initPadding() {
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        switch (mArrowDirection) {
            case LEFT:
            case LEFT_CENTER:
                paddingLeft += mArrowWidth;
                break;
            case RIGHT:
            case RIGHT_CENTER:
                paddingRight += mArrowWidth;
                break;
            case TOP:
            case TOP_CENTER:
                paddingTop += mArrowHeight;
                break;
            case BOTTOM:
            case BOTTOM_CENTER:
                paddingBottom += mArrowHeight;
                break;
        }
        if (mStrokeWidth > 0) {
            paddingLeft += mStrokeWidth;
            paddingRight += mStrokeWidth;
            paddingTop += mStrokeWidth;
            paddingBottom += mStrokeWidth;
        }
        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    private void resetPadding() {
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        switch (mArrowDirection) {
            case LEFT:
            case LEFT_CENTER:
                paddingLeft -= mArrowWidth;
                break;
            case RIGHT:
            case RIGHT_CENTER:
                paddingRight -= mArrowWidth;
                break;
            case TOP:
            case TOP_CENTER:
                paddingTop -= mArrowHeight;
                break;
            case BOTTOM:
            case BOTTOM_CENTER:
                paddingBottom -= mArrowHeight;
                break;
        }
        if (mStrokeWidth > 0) {
            paddingLeft -= mStrokeWidth;
            paddingRight -= mStrokeWidth;
            paddingTop -= mStrokeWidth;
            paddingBottom -= mStrokeWidth;
        }
        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    static float convertDpToPixel(float dp, Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public BubbleLayout setArrowDirection(ArrowDirection arrowDirection) {
        resetPadding();
        mArrowDirection = arrowDirection;
        initPadding();
        return this;
    }

    public BubbleLayout setArrowWidth(float arrowWidth) {
        resetPadding();
        mArrowWidth = arrowWidth;
        initPadding();
        return this;
    }

    public BubbleLayout setCornersRadius(float cornersRadius) {
        mCornersRadius = cornersRadius;
        requestLayout();
        return this;
    }

    public BubbleLayout setArrowHeight(float arrowHeight) {
        resetPadding();
        mArrowHeight = arrowHeight;
        initPadding();
        return this;
    }

    public BubbleLayout setArrowPosition(float arrowPosition) {
        resetPadding();
        mArrowPosition = arrowPosition;
        initPadding();
        return this;
    }

    public BubbleLayout setBubbleColor(int bubbleColor) {
        mBubbleColor = bubbleColor;
        requestLayout();
        return this;
    }

    public BubbleLayout setBubbleAndStrokeColor(int bubbleColor,int strokeColor){
        mBubbleColor = bubbleColor;
        mStrokeColor = strokeColor;
        requestLayout();
        return this;
    }

    public void noBubbleStyle(){
//        setPadding(0,0,0,0);
        setBubbleColor(ContextCompat.getColor(context,R.color.translate));
        setStrokeColor(ContextCompat.getColor(context,R.color.translate));
    }

    public void resetBubble(boolean isReceive){
        setBubbleColor(ContextCompat.getColor(context,isReceive?R.color.atom_ui_chat_bubble_left_bg:R.color.atom_ui_chat_bubble_right_bg));
        setStrokeColor(ContextCompat.getColor(context,isReceive?R.color.atom_ui_chat_bubble_left_stoken_color:R.color.atom_ui_chat_bubble_right_stoken_color));
    }

    public BubbleLayout setStrokeWidth(float strokeWidth) {
        resetPadding();
        mStrokeWidth = strokeWidth;
        initPadding();
        return this;
    }

    public BubbleLayout setStrokeColor(int strokeColor) {
        mStrokeColor = strokeColor;
        requestLayout();
        return this;
    }

    public ArrowDirection getArrowDirection() {
        return mArrowDirection;
    }

    public float getArrowWidth() {
        return mArrowWidth;
    }

    public float getCornersRadius() {
        return mCornersRadius;
    }

    public float getArrowHeight() {
        return mArrowHeight;
    }

    public float getArrowPosition() {
        return mArrowPosition;
    }

    public int getBubbleColor() {
        return mBubbleColor;
    }

    public float getStrokeWidth() {
        return mStrokeWidth;
    }

    public int getStrokeColor() {
        return mStrokeColor;
    }

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }
}
