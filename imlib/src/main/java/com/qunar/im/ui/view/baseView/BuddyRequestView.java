package com.qunar.im.ui.view.baseView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.ui.R;
import com.qunar.im.base.module.BuddyRequest;
import com.qunar.im.ui.util.ProfileUtils;

/**
 * Created by zhaokai on 15-12-9.
 */
public class BuddyRequestView extends LinearLayout {
    SimpleDraweeView gravatar;
    TextView name, reason;
    Button accept;
    TextView proceed;

    public void setHandler(ItemAcceptClickHandler handler) {
        this.handler = handler;
    }

    private ItemAcceptClickHandler handler;
    public BuddyRequestView(Context context) {
        this(context, null);
    }

    public BuddyRequestView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BuddyRequestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.atom_ui_item_buddy_request, this, true);
        gravatar = findViewById(R.id.gravatar);
        name = findViewById(R.id.name);
        reason = findViewById(R.id.reason);
        accept = findViewById(R.id.accept);
        proceed = findViewById(R.id.proceed);
    }

    public void bindData(final BuddyRequest request) {
        if (request == null) {
            return;
        }
        ProfileUtils.loadNickName(request.getId(),name, true);
        ProfileUtils.displayGravatarByUserId(request.getId(),gravatar);
        if(request.getReason() != null) {
            reason.setText(request.getReason());
        }else{
            if(request.getDirection() == BuddyRequest.Direction.RECEIVE)
                reason.setText("请求添加你为好友");
            else
                reason.setText("请求添加对方为好友");
        }
        accept.setClickable(true);
        accept.setFocusable(false);
        switch (request.getStatus()) {
            case BuddyRequest.Status.PENDING:
                accept.setVisibility(VISIBLE);
                proceed.setVisibility(GONE);
                accept.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(handler != null){
                            handler.accept(accept,request);
                        }
                    }
                });
                break;
            case BuddyRequest.Status.ACCEPT:
                accept.setVisibility(GONE);
                proceed.setVisibility(VISIBLE);
                proceed.setText("已接受请求");
                break;
            case BuddyRequest.Status.DENY:
                accept.setVisibility(GONE);
                proceed.setVisibility(VISIBLE);
                proceed.setText("已拒绝请求");
                break;
            default:
                break;
        }
    }

    public interface ItemAcceptClickHandler {
         void accept(Button button, BuddyRequest buddyRequest);
    }
}
