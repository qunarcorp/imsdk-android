package com.qunar.im.ui.view.baseView.processor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.base.structs.MessageStatus;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.QunarWebActvity;
import com.qunar.im.base.jsonbean.SystemResult;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.Utils;
import com.qunar.im.ui.view.baseView.ActionView;
import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.ViewPool;

import de.greenrobot.event.EventBus;

/**
 * Created by zhaokai on 15-11-12.
 */
public class SystemMessageProcessor extends DefaultMessageProcessor {
    @Override
    public void processChatView(ViewGroup parent,IMessageItem item) {
        final IMMessage message = item.getMessage();
        final Context context = item.getContext();
        ActionView actionView = ViewPool.getView(ActionView.class,item.getContext());
        TextView actionIntroduce = actionView.getAction_introduce();
        SimpleDraweeView actionImageRich = actionView.getAction_image_rich();
        LinearLayout action_linear = actionView.getAction_linear();
        View line = actionView.getLine();
        line.setVisibility(View.INVISIBLE);
        action_linear.removeAllViews();
        parent.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        parent.addView(actionView,layoutParams);
        final SystemResult systemMsg = JsonUtils.getGson().
                fromJson(message.getBody(), SystemResult.class);
        actionImageRich.setVisibility(View.GONE);
        actionIntroduce.setText(systemMsg.title);
        for (final SystemResult.Content content : systemMsg.content) {
            LinearLayout linearLayout = (LinearLayout) View.inflate(context, R.layout.atom_ui_item_system_sub, null);
            TextView left = linearLayout.findViewById(R.id.left);
            TextView right = linearLayout.findViewById(R.id.right);
            left.setText(content.sub_title);
            right.setText(content.sub_content);
            action_linear.addView(linearLayout);
        }
        TextView promtTextView = parent.findViewById(R.id.new_msg_prompt);
        if (promtTextView == null) {
            promtTextView = new TextView(item.getContext());
            promtTextView.setId(R.id.new_msg_prompt);
            promtTextView.setMaxLines(2);
            promtTextView.setPadding(Utils.dipToPixels(context,16), Utils.dipToPixels(context,8),
                    Utils.dipToPixels(context,16), Utils.dipToPixels(context,8));
            parent.addView(promtTextView);
        }
        promtTextView.setText(systemMsg.prompt);
        promtTextView.setGravity(Gravity.LEFT);
        promtTextView.setTextColor(Color.RED);
        if (parent.findViewById(R.id.line) == null) {
            View v  = new View(context);
            v.setBackgroundColor(context.getResources().getColor(R.color.atom_ui_light_gray_ee));
            v.setMinimumHeight(1);
            v.setId(R.id.line);
            parent.addView(v, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        }
        TextView orderTextView = parent.findViewById(R.id.tv_action);
        if (orderTextView== null) {
            orderTextView = new TextView(item.getContext());
            orderTextView.setPadding(0, Utils.dipToPixels(context,8),
                    Utils.dipToPixels(context,16), Utils.dipToPixels(context,8));
            orderTextView.setId(R.id.tv_action);
            parent.addView(orderTextView,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        orderTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, QunarWebActvity.class);
                intent.setData(Uri.parse(systemMsg.operation_url));
                intent.putExtra(Constants.BundleKey.WEB_FROM,
                        Constants.BundleValue.ORDER_HANDLE);
                context.startActivity(intent);
                EventBus.getDefault().post(new EventBusEvent.HandleOrderOperation(message));
            }
        });
        if (!MessageStatus.isExistStatus(message.getReadState(), MessageStatus.REMOTE_STATUS_CHAT_READED)) {
            //没有读过该消息
            orderTextView.setText("现在去处理");
            orderTextView.setTextColor(Color.BLUE);
        } else {
            //已读过该消息
            orderTextView.setText("已处理");
            orderTextView.setTextColor(Color.GREEN);
        }
        orderTextView.setGravity(Gravity.RIGHT);
    }
}