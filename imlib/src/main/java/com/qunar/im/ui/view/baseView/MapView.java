package com.qunar.im.ui.view.baseView;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.ui.R;
import com.qunar.im.base.common.FacebookImageUtil;

/**
 * Created by zhaokai on 15-11-3.
 */
public class MapView extends RelativeLayout {
    SimpleDraweeView image;
    TextView text;
    public MapView(Context context) {
        this(context, null);
    }

    public MapView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.atom_ui_layout_map, this, true);
        image = findViewById(R.id.image);
        text = findViewById(R.id.text);
    }

    public void setMapInfo(Uri uri, String position) {
        text.setText(position);
        FacebookImageUtil.loadWithCache(uri.toString(),image);
    }
}
