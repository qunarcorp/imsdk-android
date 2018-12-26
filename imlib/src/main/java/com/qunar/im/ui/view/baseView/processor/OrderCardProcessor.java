package com.qunar.im.ui.view.baseView.processor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;

import com.qunar.im.base.jsonbean.OrderCardInfoResult;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.ui.activity.QunarWebActvity;
import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.OrderCardView;
import com.qunar.im.ui.view.baseView.ViewPool;

import java.util.List;

/**
 * Created by huayu.chen on 2016/7/13.
 */
public class OrderCardProcessor extends DefaultMessageProcessor {
    @Override
    public void processChatView(ViewGroup parent, final IMessageItem item) {
        IMMessage message = item.getMessage();
        final Context context = item.getContext();
        try {
            OrderCardInfoResult orderCardInfoResult = JsonUtils.getGson().fromJson(message.getExt()
                    , OrderCardInfoResult.class);
            String titleImg = orderCardInfoResult.titleimg;
            String titleTxt = orderCardInfoResult.titletxt;
            String productImg = orderCardInfoResult.productimg;
            final String detailurl = orderCardInfoResult.detailurl;
            List<OrderCardInfoResult.DescItem> desc = orderCardInfoResult.descs;
            OrderCardView orderCardView = ViewPool.getView(OrderCardView.class, context);
            orderCardView.setTitleIcon(titleImg);
            orderCardView.setTitle(titleTxt);
            orderCardView.setProductIcon(productImg);
            orderCardView.setProductInfo(desc,context);
            orderCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse(detailurl);
                    Intent intent = new Intent(context, QunarWebActvity.class);
                    intent.setData(uri);
                    context.startActivity(intent);
                }
            });
            parent.setVisibility(View.VISIBLE);
            parent.addView(orderCardView);
        } catch (Exception e) {

        }

    }
}
