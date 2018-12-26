package com.qunar.im.ui.view.baseView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.base.common.FacebookImageUtil;
import com.qunar.im.base.jsonbean.ExtendMessageEntity;
import com.qunar.im.base.util.DESUtils;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.LogUtil;
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
public class ExtendMsgView extends LinearLayout {
    private static final String TAG = ExtendMsgView.class.getSimpleName();
    TextView desc,title;
    SimpleDraweeView thumb;

    WeakReference<Context> context;

    public ExtendMsgView(Context context) {
        this(context, null);
    }

    public ExtendMsgView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExtendMsgView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = new WeakReference<Context>(context);
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.atom_ui_layout_extend_msg, this, true);
        desc = findViewById(R.id.desc);
        title = findViewById(R.id.txt_title);
        thumb = findViewById(R.id.imageview_left);
    }

    public void bindData(final ExtendMessageEntity entity, final String id, final boolean isGroup,boolean as667) {
        if (entity != null) {
            if(TextUtils.isEmpty(entity.title)){
                title.setVisibility(GONE);
            } else {
                title.setText(entity.title);
            }
            if(TextUtils.isEmpty(entity.desc)) {
                desc.setText(R.string.atom_ui_tip_click_to_view);
            }else{
                desc.setText(entity.desc);
            }
            if(!TextUtils.isEmpty(entity.img)){
                FacebookImageUtil.loadWithCache(entity.img, thumb);
            }else{
//                thumb.setVisibility(GONE);
            }
            if(entity.showas667||as667)
            {
                RelativeLayout parent = (RelativeLayout) desc.getParent();
                LinearLayout.LayoutParams parentLayout = (LayoutParams) parent.getLayoutParams();
                parentLayout.width = ViewGroup.LayoutParams.MATCH_PARENT;
                parentLayout.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                parent.setLayoutParams(parentLayout);
                ViewGroup.LayoutParams layoutParams = desc.getLayoutParams();
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                desc.setLayoutParams(layoutParams);
            } else {
                int height = context.get().getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size);
                RelativeLayout parent = (RelativeLayout) desc.getParent();
                LinearLayout.LayoutParams parentLayout = (LayoutParams) parent.getLayoutParams();
                parentLayout.width = ViewGroup.LayoutParams.MATCH_PARENT;
                parentLayout.height = height;
                parent.setLayoutParams(parentLayout);
                ViewGroup.LayoutParams layoutParams = desc.getLayoutParams();
                layoutParams.height = height;
                desc.setLayoutParams(layoutParams);
            }
            if(!TextUtils.isEmpty(entity.reacturl)) {
                setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            String session = URLEncoder.encode(
                                    DESUtils.encrypt(DataUtils.S, CurrentPreference.getInstance().getToken()));
                            String key = URLEncoder.encode(DESUtils.encrypt(DataUtils.S, CurrentPreference.getInstance().getVerifyKey()));
                            String uid = URLEncoder.encode(
                                    DESUtils.encrypt(DataUtils.S, CurrentPreference.getInstance().getUserid()));

                            Intent intent = new Intent("android.intent.action.VIEW",
                                    Uri.parse(CommonConfig.schema+"://camelhelp?userid=" + uid
                                            + "&session=" + session
                                            + "&key=" + key
                                            +"&reacturl="+ URLEncoder.encode(entity.reacturl)));
                            context.get().startActivity(intent);
                        } catch (Exception e) {
                            LogUtil.e(TAG,"ERROR",e);
                        }
                    }
                });
            }
            else if (!TextUtils.isEmpty( entity.linkurl)) {
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StringBuilder builder = new StringBuilder(entity.linkurl);
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
