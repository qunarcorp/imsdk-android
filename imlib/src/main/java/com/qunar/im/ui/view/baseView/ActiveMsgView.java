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
import com.qunar.im.base.common.FacebookImageUtil;
import com.qunar.im.base.jsonbean.ActivityMessageEntity;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.QunarWebActvity;

import java.lang.ref.WeakReference;
import java.net.URLEncoder;

/**
 * Created by xinbo.wang on 2016/5/26.
 */
public class ActiveMsgView extends LinearLayout {
    private static final String TAG = ActiveMsgView.class.getSimpleName();
    TextView active_title, active_addr_category, active_time, active_endtime, active_type;
    SimpleDraweeView active_img;

    WeakReference<Context> context;

    public ActiveMsgView(Context context) {
        this(context, null);
    }

    public ActiveMsgView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActiveMsgView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = new WeakReference<Context>(context);
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.atom_ui_layout_active_msg, this, true);
        active_title = findViewById(R.id.active_title);
        active_addr_category = findViewById(R.id.active_addr_category);
        active_endtime = findViewById(R.id.active_endtime);
        active_time = findViewById(R.id.active_time);
        active_type = findViewById(R.id.active_type);
        active_img = findViewById(R.id.active_img);
    }

    public void bindData(final ActivityMessageEntity entity, final String id, final boolean isGroup) {
        if (entity != null) {
            if(TextUtils.isEmpty(entity.title)){
                active_title.setVisibility(GONE);
            }else {
                active_title.setText(entity.title);
            }
            active_addr_category.setText(entity.ip_city + "-" + entity.activity_city + " " + entity.category);
            if(!TextUtils.isEmpty(entity.img)){
                FacebookImageUtil.loadWithCache(entity.img, active_img);
            }else{
//                thumb.setVisibility(GONE);
            }
            active_type.setText((TextUtils.isEmpty(entity.activity_type)) ? "活动" : entity.activity_type);
            if(TextUtils.isEmpty(entity.start_date)) {
                active_time.setVisibility(GONE);
            } else {
                active_time.setText(String.format("开始时间：%s", entity.start_date));
            }
            if(TextUtils.isEmpty(entity.end_date)) {
                active_endtime.setVisibility(GONE);
            } else {
                active_endtime.setText(String.format("结束时间：%s", entity.end_date));
            }


            if (!TextUtils.isEmpty( entity.url)) {
                setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StringBuilder builder = new StringBuilder(entity.url);
                        if(entity.auth) {
                            if (TextUtils.isEmpty(CurrentPreference.getInstance().getVerifyKey())) return;
                            if (builder.indexOf("?") > -1) {
                                builder.append("&");
                            } else {
                                builder.append("?");
                            }
                            builder.append("username=");
                            builder.append(URLEncoder.encode(CurrentPreference.getInstance().getUserid()));
                            builder.append("&rk=");
                            builder.append(URLEncoder.encode(CurrentPreference.getInstance().getVerifyKey()));
                            if (isGroup) {
                                builder.append("&group_id=");
                            } else {
                                builder.append("&user_id=");
                            }
                            builder.append(URLEncoder.encode(id));
                            builder.append("&company=");
                            builder.append(URLEncoder.encode(QtalkNavicationService.COMPANY));
                            builder.append("&domain=");
                            builder.append(URLEncoder.encode(QtalkNavicationService.getInstance().getXmppdomain()));
                        }

                        Uri uri = Uri.parse(builder.toString());
                        Intent intent = new Intent(CommonConfig.globalContext, QunarWebActvity.class);
                        intent.setData(uri);
                        intent.putExtra(QunarWebActvity.IS_HIDE_BAR,!entity.showbar);
                        context.get().startActivity(intent);
                    }
                });
            }
        }
    }
}
