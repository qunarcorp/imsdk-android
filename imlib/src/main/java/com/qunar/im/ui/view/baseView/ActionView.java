package com.qunar.im.ui.view.baseView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.QunarWebActvity;
import com.qunar.im.base.common.FacebookImageUtil;
import com.qunar.im.base.jsonbean.ActionRichText;
import com.qunar.im.base.util.Utils;

/**
 * Created by saber on 16-1-6.
 */
public class ActionView extends LinearLayout {
    public TextView getAction_introduce() {
        return action_introduce;
    }

    public SimpleDraweeView getAction_image_rich() {
        return action_image_rich;
    }

    public LinearLayout getAction_linear() {
        return action_linear;
    }

    public View getLine() {
        return line;
    }

    View line;
    TextView action_introduce;
    SimpleDraweeView action_image_rich;
    LinearLayout action_linear;
    Context context;
    int size = 96;
    public ActionView(Context context) {
        this(context, null);
    }

    public ActionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        size = Utils.dipToPixels(context, 96);
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.atom_ui_layout_action_view, this, true);
        action_introduce = findViewById(R.id.action_introduce);
        action_linear = findViewById(R.id.action_linear);
        action_image_rich = findViewById(R.id.action_image_rich);
        line = findViewById(R.id.action_richline_top);
    }

    public void bindData(final ActionRichText action)
    {
        line.setVisibility(VISIBLE);
        action_linear.removeAllViews();
        FacebookImageUtil.loadWithCache(action.imageurl, action_image_rich,
                false, null);
        action_image_rich.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, QunarWebActvity.class);
                intent.setData(Uri.parse(action.linkurl));
                context.startActivity(intent);
                context.startActivity(intent);
            }
        });
        action_introduce.setText(Html.fromHtml(action.introduce));
        for (final ActionRichText.SubTitle title : action.subtitles) {
            LinearLayout linearLayout = (LinearLayout) View.inflate(context, R.layout.atom_ui_item_subtitle, null);
            SimpleDraweeView imageView = linearLayout.findViewById(R.id.image);
            imageView.setLayoutParams(new LayoutParams(size,size));
            TextView textView = linearLayout.findViewById(R.id.text);
            FacebookImageUtil.loadWithCache(title.iconurl, imageView,
                    false, null);
            linearLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, QunarWebActvity.class);
                    intent.setData(Uri.parse(title.linkurl));
                    context.startActivity(intent);
                }
            });
            textView.setText(title.introduce);
            action_linear.addView(linearLayout);
        }
    }
}
