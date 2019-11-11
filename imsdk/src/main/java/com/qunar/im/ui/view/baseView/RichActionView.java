package com.qunar.im.ui.view.baseView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.QunarWebActvity;
import com.qunar.im.ui.util.FacebookImageUtil;
import com.qunar.im.base.jsonbean.RichText;
import com.qunar.im.base.util.Utils;

/**
 * Created by saber on 16-1-6.
 */
public class RichActionView extends LinearLayout {
    TextView title,date,introduce;
    SimpleDraweeView image_rich;
    Context context;
    LinearLayout container;
    public RichActionView(Context context) {
        this(context, null);
    }

    public RichActionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RichActionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.atom_ui_layout_rich_action_view, this, true);
        title = findViewById(R.id.title);
        date = findViewById(R.id.date);
        introduce = findViewById(R.id.introduce);
        image_rich = findViewById(R.id.image_rich);
        container = findViewById(R.id.rich_view_container);
        this.context = context;
    }

    public SimpleDraweeView getImageRich()
    {
        return image_rich;
    }

    public void bindData(final RichText text)
    {
        if(container.getChildCount()>6) container.removeViewAt(container.getChildCount()-1);
        if(TextUtils.isEmpty(text.introduce)) {
            introduce.setText(Html.fromHtml(text.content));
            TextView textView = new TextView(context);
            textView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            textView.setText("点击查看更多");
            textView.setGravity(Gravity.RIGHT);
            textView.setTextColor(ContextCompat.getColor(context,R.color.atom_ui_button_primary_color));
            textView.setPadding(Utils.dipToPixels(context,16),Utils.dipToPixels(context,8),
                    Utils.dipToPixels(context,16), Utils.dipToPixels(context,8));

            container.addView(textView);
        }
        else {
            introduce.setText(Html.fromHtml(text.introduce));
        }

        title.setText(text.title);
        if(TextUtils.isEmpty(text.date))
        {
            date.setVisibility(GONE);
        }
        else {
            date.setVisibility(VISIBLE);
            date.setText(text.date);
        }
        if(!TextUtils.isEmpty(text.imageurl)) {
            FacebookImageUtil.loadWithCache(text.imageurl, image_rich,
                    false, new FacebookImageUtil.ImageLoadCallback.EmptyCallback());
        }
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, QunarWebActvity.class);
                intent.setData(Uri.parse(text.linkurl));
                context.startActivity(intent);
            }
        });
    }
}
