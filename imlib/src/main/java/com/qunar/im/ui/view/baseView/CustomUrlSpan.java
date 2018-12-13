package com.qunar.im.ui.view.baseView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.style.ClickableSpan;
import android.view.View;

import com.qunar.im.ui.activity.QunarWebActvity;

public class CustomUrlSpan extends ClickableSpan {
    private Context context;
    private String url;

    public CustomUrlSpan(Context context,String url){
        this.context = context;
        this.url = url;
    }

    @Override
    public void onClick(View widget) {
        // 在这里可以做任何自己想要的处理
        Intent intent = new Intent(context,QunarWebActvity.class);
        intent.putExtra(QunarWebActvity.IS_HIDE_BAR, false);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

}
