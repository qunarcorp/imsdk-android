package com.qunar.im.ui.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.qunar.im.base.util.Utils;
import com.qunar.im.ui.R;

/**
 * Created by froyomu on 2019/4/25
 * <p>
 * Describe:首页加号 扩展功能
 */
public class HomeMenuPopWindow extends PopupWindow{
    private Context context;
    private LinearLayout atom_ui_home_pop_scan;//扫一扫
    private LinearLayout atom_ui_home_pop_unread;//未读消息
    private LinearLayout atom_ui_home_pop_create_group;//创建群组
    private LinearLayout atom_ui_home_pop_readed;//一键已读

    private View.OnClickListener popOnClickListener;

    public HomeMenuPopWindow(Context context,View.OnClickListener popOnClickListener){
        this.context = context;
        this.popOnClickListener = popOnClickListener;
        initView();
    }

    private void initView(){
        View view = LayoutInflater.from(context).inflate(R.layout.atom_ui_home_pop_layout,null);
        atom_ui_home_pop_scan = (LinearLayout) view.findViewById(R.id.atom_ui_home_pop_unread);
        atom_ui_home_pop_unread = (LinearLayout) view.findViewById(R.id.atom_ui_home_pop_scan);
        atom_ui_home_pop_create_group = (LinearLayout) view.findViewById(R.id.atom_ui_home_pop_create_group);
        atom_ui_home_pop_readed = (LinearLayout) view.findViewById(R.id.atom_ui_home_pop_readed);

        atom_ui_home_pop_scan.setOnClickListener(popOnClickListener);
        atom_ui_home_pop_unread.setOnClickListener(popOnClickListener);
        atom_ui_home_pop_create_group.setOnClickListener(popOnClickListener);
        atom_ui_home_pop_readed.setOnClickListener(popOnClickListener);

        setPopupWindow(view);
    }

    private void setPopupWindow(View mPopView) {
        this.setContentView(mPopView);// 设置View
        //产生背景变暗效果
        WindowManager.LayoutParams lp = ((Activity)context).getWindow().getAttributes();
        lp.alpha = 0.6f;
        ((Activity)context).getWindow().setAttributes(lp);
        this.setWidth(Utils.getScreenWidth(context)* 6 / 15);// 设置弹出窗口的宽
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);// 设置弹出窗口的高
        setFocusable(true);
//        setBackgroundDrawable(context.getResources().getDrawable(R.drawable.atom_ui_home_pop_bg));
        setBackgroundDrawable(new BitmapDrawable());
        // 设置点击其他地方 就消失 (只设置这个，没有效果)
        setOutsideTouchable(false);
        setOnDismissListener(()-> {
            WindowManager.LayoutParams layoutParams = ((Activity)context).getWindow().getAttributes();
            layoutParams.alpha = 1.0f;
            ((Activity)context).getWindow().setAttributes(layoutParams);
        });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void showHomePop(View parent){
        int dp5 = Utils.dpToPx(context,8);
        int x = Utils.getScreenWidth(context)- getWidth() - dp5;
        showAsDropDown(parent,x,-Utils.dpToPx(context,6), Gravity.NO_GRAVITY);
    }
}
