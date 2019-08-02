package com.qunar.im.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by saber on 15-12-9.
 */
public class AutoHideSoftInputEditText extends EditText {

    OnFocusChangeListener focusChangeListener = null;
    Context context = null;


    public AutoHideSoftInputEditText(Context context) {
        this(context, null);
    }

    public AutoHideSoftInputEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AutoHideSoftInputEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        this.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideSoftInput();
                }
                if (focusChangeListener != null) {
                    focusChangeListener.onFocusChange(v, hasFocus);
                }
            }
        });
    }


    public AutoHideSoftInputEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideSoftInput();
                }
                if (focusChangeListener != null) {
                    focusChangeListener.onFocusChange(v, hasFocus);
                }
            }
        });
    }

    /**
     * @return see whether softinput was shown or not
     */
    void hideSoftInput() {
        final InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(super.getWindowToken(), 0);
        }
    }

    public void addFocusChangeListener(OnFocusChangeListener focusChangeListener) {
        this.focusChangeListener = focusChangeListener;
    }

}
