package com.qunar.im.ui.view.baseView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.QunarWebActvity;
import com.qunar.im.base.common.FacebookImageUtil;
import com.qunar.im.base.jsonbean.NoteMsgJson;

/**
 * Created by saber on 16-1-27.
 */
public class NoteActionView extends LinearLayout {
    TextView price,title,tag;
    SimpleDraweeView thumb;

    Context context;

    public NoteActionView(Context context) {
        this(context, null);
    }

    public NoteActionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NoteActionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.atom_ui_layout_note_action, this, true);
        price = findViewById(R.id.price);
        title = findViewById(R.id.txt_title);
        tag = findViewById(R.id.tag);
        thumb = findViewById(R.id.imageview_left);
    }

    public void bindData(NoteMsgJson json) {
        String openUrl = null;
        if (json.data != null && json.data.size() > 0) {
            String type = json.data.get("type").toString();
            openUrl = json.data.get("touchDtlUrl").toString();
            if(!TextUtils.isEmpty(type)){
                title.setText("[" + type + "]" + json.data.get("title").toString());
            }else {
                title.setText(json.data.get("title").toString());
            }
            String t = json.data.get("tag").toString();
            if(!TextUtils.isEmpty(t)){
                tag.setText(t);
            }
            price.setText(json.data.get("price").toString());
            FacebookImageUtil.loadWithCache(json.data.get("imageUrl").toString(),
                    thumb);
        } else if (!TextUtils.isEmpty(json.url)) {
            price.setText("点击打开");
            title.setText(json.url);
            openUrl = json.url;
        }
        if (openUrl != null) {
            final Uri uri = Uri.parse(openUrl);
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, QunarWebActvity.class);
                    intent.setData(uri);
                    context.startActivity(intent);
                }
            });
        }
    }
}
