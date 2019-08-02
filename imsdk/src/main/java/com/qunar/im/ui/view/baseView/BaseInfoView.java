package com.qunar.im.ui.view.baseView;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.ui.util.FacebookImageUtil;
import com.qunar.im.ui.R;
import com.qunar.im.ui.util.ProfileUtils;
import com.qunar.im.base.view.BaseInfoBinderable;


/**
 * Created by zhaokai on 15-9-9.
 */
public class BaseInfoView extends LinearLayout {
    public SimpleDraweeView gravatar;
    public TextView deptName;
    TextView userName;
    TextView hint;

    public BaseInfoView(Context context) {
        this(context, null);
    }

    public BaseInfoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseInfoView(Context context,AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.atom_ui_item_searched_user, this, true);
        gravatar = findViewById(R.id.gravatar);
        deptName = findViewById(R.id.deptName);
        userName = findViewById(R.id.userName);
        hint = findViewById(R.id.hint);
    }


    public void bind(BaseInfoBinderable baseInfoBinderable) {
        hint.setVisibility(GONE);
        deptName.setVisibility(GONE);
        userName.setText(baseInfoBinderable.name);
        if(!TextUtils.isEmpty(baseInfoBinderable.desc))
        {
            deptName.setVisibility(VISIBLE);
            deptName.setText(baseInfoBinderable.desc);
        }
        if(TextUtils.isEmpty(baseInfoBinderable.imageUrl))
        {
            if(baseInfoBinderable.type == BaseInfoBinderable.CONTACT_TYPE)
            {
                ProfileUtils.displayGravatarByUserId(baseInfoBinderable.id, gravatar);
            }
            else if(baseInfoBinderable.type == BaseInfoBinderable.GROUP_TYPE)
            {
                ProfileUtils.setGroupPicture(gravatar,R.drawable.atom_ui_ic_my_chatroom,getContext(),
                        baseInfoBinderable.id);
            }
            else {
                FacebookImageUtil.loadFromResource(R.drawable.atom_ui_default_gravatar,gravatar);
            }
        }
        else {
            FacebookImageUtil.loadWithCache(baseInfoBinderable.imageUrl, gravatar);
        }
        if(!TextUtils.isEmpty(baseInfoBinderable.hint)) {
            hint.setText(baseInfoBinderable.hint);
            hint.setVisibility(VISIBLE);
        }
    }
}
