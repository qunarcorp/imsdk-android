package com.qunar.im.ui.view.baseView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qunar.im.ui.R;
import com.qunar.im.ui.view.IconView;

/**
 * Created by saber on 15-12-30.
 */
public class HongbaoView extends RelativeLayout {
    private TextView titleTextView, hongbaoType;
    private IconView hongbao_icon;

    public HongbaoView(Context context) {
        this(context, null);
    }

    public HongbaoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HongbaoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.atom_ui_item_hongbao, this, true);
        titleTextView = findViewById(R.id.hongbao_title);
        hongbaoType = findViewById(R.id.tv_hongbao_type);
        hongbao_icon = findViewById(R.id.hongbao_icon);
    }

    public void setTitle(String title) {
        titleTextView.setText(title);
    }

    public void setHongbaoType(String type) {
        hongbaoType.setText(type);
    }

    public void setIcon(int id)
    {
//        hongbao_icon.setImageResource(id);
    }
}
