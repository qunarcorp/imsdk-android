package com.qunar.im.ui.view.baseView;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qunar.im.ui.R;
import com.qunar.im.ui.view.IconView;

/**
 * Created by wangxinbo on 2017/1/23.
 */
public class RTCView extends LinearLayout {
    TextView textView;
    IconView iconView;
    public RTCView(Context context) {
        this(context,null);
    }

    public RTCView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RTCView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RTCView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    protected void init(Context context)
    {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.atom_ui_item_rtc_view, this, true);
        iconView = (IconView) findViewById(R.id.atom_ui_left_icon);
        textView = (TextView) findViewById(R.id.atom_ui_text);

    }

    public void bind(boolean isVideo)
    {
        if(isVideo)
        {
            iconView.setText(R.string.atom_ui_ic_camera_shooting);
            textView.setText(R.string.atom_ui_rtc_video_call);
        }
        else {
            iconView.setText(R.string.atom_ui_ic_mic);
            textView.setText(R.string.atom_ui_rtc_call);
        }
    }
}
