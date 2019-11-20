package com.qunar.im.ui.view;

import android.content.Context;
import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qunar.im.ui.R;

public class WorkWorldHeadView extends LinearLayout {

    private View view;

    public WorkWorldHeadView(Context context) {
        super(context);
        // 加载布局
        view = LayoutInflater.from(context).inflate(R.layout.atom_ui_work_world_head_view,null);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(view);

    }

    public WorkWorldHeadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WorkWorldHeadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setText(String str) {
        ( (TextView)view.findViewById(R.id.head_text)).setText(str);
    }
}
