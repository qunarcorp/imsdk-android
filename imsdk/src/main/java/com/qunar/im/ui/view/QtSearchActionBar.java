package com.qunar.im.ui.view;

import android.content.Context;
import androidx.appcompat.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.qunar.im.ui.R;

/**
 * Created by saber on 15-11-13.
 */
public class QtSearchActionBar extends Toolbar {
    public QtSearchActionBar(Context context) {
        this(context, null);

    }

    public QtSearchActionBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0x7f0100b9);

    }

    public QtSearchActionBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setContentInsetsAbsolute(0, 0);
        setContentInsetsRelative(0, 0);
    }

    public FrameLayout getLeftLayout() {
        return (FrameLayout) this.findViewById(R.id.left_layout);
    }

    public MySearchView getSearchView() {
        return (MySearchView) this.findViewById(R.id.search);
    }

    public TextView getDeleteText() {
        return (TextView) this.findViewById(R.id.tv_delete);
    }

}
