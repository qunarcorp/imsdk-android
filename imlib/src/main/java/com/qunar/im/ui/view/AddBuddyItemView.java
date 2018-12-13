package com.qunar.im.ui.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.AddAuthMessageActivity;
import com.qunar.im.ui.activity.PersonalInfoActivity;
import com.qunar.im.base.jsonbean.SearchUserResult;
import com.qunar.im.base.util.ProfileUtils;

/**
 * Created by saber on 16-2-3.
 */
public class AddBuddyItemView extends LinearLayout implements View.OnClickListener {
    SimpleDraweeView view_gravatar_buddy;
    TextView m_name;
    LinearLayout ll_personal_info;
    Button btn_add;

    Context ctx;
    SearchUserResult.SearchUserInfo userInfo;
    public AddBuddyItemView(Context context) {
        this(context, null);
    }

    public AddBuddyItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AddBuddyItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ctx = context;
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.atom_ui_item_add_buddy_view,this,true);
        ll_personal_info = (LinearLayout) findViewById(R.id.ll_personal_info);
        view_gravatar_buddy = (com.facebook.drawee.view.SimpleDraweeView) findViewById(R.id.view_gravatar_buddy);
        m_name = (TextView) findViewById(R.id.m_name);
        btn_add = (Button) findViewById(R.id.btn_add);
    }

    public void bindData(SearchUserResult.SearchUserInfo info)
    {
        userInfo = info;
        if(info.isFriends)
        {
            btn_add.setVisibility(GONE);
        }
        else {
            btn_add.setVisibility(VISIBLE);
        }
        m_name.setText(info.nickname);
        ProfileUtils.displayGravatarByUserId(info.username + "@" + info.domain, view_gravatar_buddy);
        ll_personal_info.setOnClickListener(this);
        btn_add.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.ll_personal_info) {
            Intent personalInfoIntent = new Intent(ctx, PersonalInfoActivity.class);
            personalInfoIntent.putExtra("jid", userInfo.username + "@" + userInfo.domain);
            ctx.startActivity(personalInfoIntent);

        } else if (i == R.id.btn_add) {
            Intent intent = new Intent(ctx, AddAuthMessageActivity.class);
            intent.putExtra("jid", userInfo.username + "@" + userInfo.domain);
            ctx.startActivity(intent);

        }
    }
}
