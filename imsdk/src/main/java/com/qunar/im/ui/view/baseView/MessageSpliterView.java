package com.qunar.im.ui.view.baseView;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.qunar.im.ui.R;

/**
 * Created by xinbo.wang on 2017-02-09.
 */
public class MessageSpliterView extends LinearLayout {
    public MessageSpliterView(Context context) {
        this(context,null);
    }

    public MessageSpliterView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MessageSpliterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MessageSpliterView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context)
    {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.atom_ui_message_spliter, this, true);
    }
}
