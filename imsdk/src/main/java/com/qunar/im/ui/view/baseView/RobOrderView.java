package com.qunar.im.ui.view.baseView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qunar.im.base.jsonbean.RobOrderMsgJson;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.QunarWebActvity;

/**
 * 抢单消息view
 * Created by lihaibin.li on 2017/10/25.
 */

public class RobOrderView extends LinearLayout {
    Context context;
    TextView rob_order_title, rob_order_yusuan, rob_order_time, rob_order_mark, rob_order_button;

    public RobOrderView(Context context) {
        this(context, null);
    }

    public RobOrderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RobOrderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.atom_ui_layout_rob_order, this, true);
        rob_order_title = findViewById(R.id.rob_order_title);
        rob_order_yusuan = findViewById(R.id.rob_order_yusuan);
        rob_order_time = findViewById(R.id.rob_order_time);
        rob_order_mark = findViewById(R.id.rob_order_mark);
        rob_order_button = findViewById(R.id.rob_order_button);
    }

    public void bindData(final IMMessage message) {
        final RobOrderMsgJson robOrderMsgJson = JsonUtils.getGson().fromJson(message.getExt(), RobOrderMsgJson.class);
        rob_order_title.setText(robOrderMsgJson.getTitle());
        RobOrderMsgJson.DetailBean detailBean = robOrderMsgJson.getDetail();
        if (detailBean != null) {
            rob_order_yusuan.setText(detailBean.getBudgetInfo());
            rob_order_time.setText(detailBean.getOrderTime());
            rob_order_mark.setText("补充说明：" + detailBean.getRemarks());
        }
        if ("1".equals(robOrderMsgJson.getStatus())) {
            rob_order_button.setText(robOrderMsgJson.getBtnDisplay());
            rob_order_button.setOnClickListener(null);
            rob_order_button.setBackgroundColor(Color.parseColor("#FFDDDDDD"));
        } else {
            rob_order_button.setBackgroundColor(Color.parseColor("#15b0f9"));
            rob_order_button.setText(robOrderMsgJson.getBtnDisplay());
            rob_order_button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, QunarWebActvity.class);
                    intent.putExtra(QunarWebActvity.IS_HIDE_BAR, false);
                    if (robOrderMsgJson.getDealUrl() != null){
                        intent.setData(Uri.parse(robOrderMsgJson.getDealUrl()));
                        context.startActivity(intent);
                    }
                }
            });

        }
    }
}
