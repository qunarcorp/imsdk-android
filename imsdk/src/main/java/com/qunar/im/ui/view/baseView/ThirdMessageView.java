package com.qunar.im.ui.view.baseView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qunar.im.base.jsonbean.ThirdRequestMsgJson;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.ui.R;
import com.qunar.im.ui.events.RushOrderEvent;

import de.greenrobot.event.EventBus;


/**
 * Created by saber on 16-1-27.
 */
public class ThirdMessageView extends LinearLayout {
    private static final String TAG = ThirdMessageView.class.getSimpleName();
    TextView tv_source,tv_message_content, tv_action;
    Context context;
    public ThirdMessageView(Context context) {
        this(context, null);
    }

    public ThirdMessageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThirdMessageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.atom_ui_layout_third_message, this, true);
        tv_source = findViewById(R.id.tv_source);
        tv_message_content = findViewById(R.id.tv_message_content);
        tv_action = findViewById(R.id.tv_action);

    }

     public void bindData(final IMMessage message) {
         final ThirdRequestMsgJson json = JsonUtils.getGson().fromJson(message.getExt(), ThirdRequestMsgJson.class);
         tv_source.setText(json.source);
         tv_message_content.setText(json.detail);
         if(json.status==1)
         {
             tv_action.setText(R.string.atom_ui_rushed);
             tv_action.setTextColor(context.getResources().getColor(R.color.atom_ui_white));
             tv_action.setBackgroundResource(R.color.atom_ui_button_primary_color_pressed);
             tv_action.setOnClickListener(null);
         }
         else if(json.status == 2)
         {
             tv_action.setText(R.string.atom_ui_answered);
             tv_action.setTextColor(context.getResources().getColor(R.color.atom_ui_light_gray_33));
             tv_action.setBackgroundResource(R.color.atom_ui_white);
             tv_action.setOnClickListener(null);
         }
         else {
             tv_action.setTextColor(context.getResources().getColor(R.color.atom_ui_white));
             tv_action.setText(R.string.atom_ui_response);
             tv_action.setBackgroundResource(R.color.atom_ui_primary_color);
             tv_action.setOnClickListener(new OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     int timeout = 5;
                     try {
                         timeout = Integer.valueOf(json.timeout);
                     } catch (Exception ex) {
                         LogUtil.e(TAG,"ERROR",ex);
                     }

                     EventBus.getDefault().post(new RushOrderEvent(json.dealid, message, timeout));
                 }
             });
         }
     }
}
