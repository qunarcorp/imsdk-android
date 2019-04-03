package com.qunar.im.ui.view.baseView;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.ui.util.FacebookImageUtil;
import com.qunar.im.base.jsonbean.OrderCardInfoResult;
import com.qunar.im.base.util.Utils;
import com.qunar.im.ui.R;

import java.util.List;

/**
 * Created by huayu.chen on 2016/7/13.
 */
public class OrderCardView extends LinearLayout {
    private SimpleDraweeView title_icon;
    private TextView title;
    private SimpleDraweeView product_icon;
    private LinearLayout product_info,ll_product;
    private int padding;

    public OrderCardView(Context context) {
        this(context, null);
    }

    public OrderCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OrderCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.atom_ui_item_order_card, this, true);
        title_icon = findViewById(R.id.title_icon);
        title = findViewById(R.id.title);
        product_icon = findViewById(R.id.product_icon);
        product_info = findViewById(R.id.product_info);
        ll_product = findViewById(R.id.ll_product);
        padding = Utils.dipToPixels(context,4);
    }

    public void setTitleIcon(String titleImg) {
        if (!TextUtils.isEmpty(titleImg)) {
            title_icon.setVisibility(VISIBLE);
            FacebookImageUtil.loadWithCache(titleImg,product_icon);
        }else {
            title_icon.setVisibility(GONE);
        }
    }

    public void setTitle(String titleTxt) {
        this.title.setText(titleTxt);
    }

    public void setProductIcon(String productImg) {
        if (!TextUtils.isEmpty(productImg)) {
            product_icon.setVisibility(VISIBLE);
            FacebookImageUtil.loadWithCache(productImg,title_icon);
        }else {
            product_icon.setVisibility(GONE);
        }
    }

    public void setProductInfo(List<OrderCardInfoResult.DescItem> desc,Context context) {
        product_info.removeAllViews();
        for (OrderCardInfoResult.DescItem item:desc) {
            LinearLayout tableRow = new LinearLayout(context);
            tableRow.setOrientation(HORIZONTAL);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1f);
            LinearLayout.LayoutParams params2=new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,3f);
            tableRow.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            TextView key=new TextView(context);
            TextView value=new TextView(context);
            key.setText(item.k);
            key.setSingleLine(true);
            key.setEllipsize(TextUtils.TruncateAt.END);
            key.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    context.getResources().getDimensionPixelSize(R.dimen.atom_ui_text_size_smaller));
            value.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    context.getResources().getDimensionPixelSize(R.dimen.atom_ui_text_size_smaller));
            value.setText(item.v);
            value.setSingleLine(true);
            value.setEllipsize(TextUtils.TruncateAt.END);
            key.setTextColor(context.getResources().getColor(R.color.atom_ui_light_gray_33));
            value.setTextColor(context.getResources().getColor(R.color.atom_ui_light_gray_33));
            key.setPadding(0,padding/4,padding,padding/4);
            value.setPadding(0,padding/4,0,padding/4);
            if (!TextUtils.isEmpty(item.c)){
                value.setTextColor(Color.parseColor(item.c));
            }
            tableRow.addView(key,params);
            tableRow.addView(value,params2);
            product_info.addView(tableRow);
        }
    }

}
