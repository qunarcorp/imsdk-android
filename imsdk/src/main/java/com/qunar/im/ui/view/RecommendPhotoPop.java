package com.qunar.im.ui.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.bumptech.glide.Glide;
import com.qunar.im.ui.R;


public class RecommendPhotoPop extends PopupWindow {
    /**
     *
     */
    private ImageView iv;
    /**
     *
     */
    private static Context context;
    public RecommendPhotoPop(Context context) {
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.atom_ui_pop_recommendphoto,null,false);
        iv = (ImageView) view.findViewById(R.id.recommendphoto_img);
        setContentView(view);
        setWidth(dip2px(90));
        setHeight(dip2px(135));
        setFocusable(false);
        setBackgroundDrawable(new BitmapDrawable());
        // 设置点击其他地方 就消失 (只设置这个，没有效果)
        setOutsideTouchable(true);



    }

    public  void setImgPath(final String path, View.OnClickListener clickListener) {
        iv.setOnClickListener(clickListener);
        Glide.with(context).load(path).crossFade().centerCrop()
                .into(iv);
    }

    /**
     * 显示图片提示
     * @param context
     * @param view
     */
    public  static  RecommendPhotoPop recommendPhoto(Context context, View view,String path,View.OnClickListener clickListener) {

                final RecommendPhotoPop recommendPhotoPop = new RecommendPhotoPop(context);
                recommendPhotoPop.setImgPath(path,clickListener);
                int x = getScreenWidth(context) - dip2px(92);
                int y = getScreenHeight(context) - view.getMeasuredHeight() - dip2px(138);
                recommendPhotoPop.showAtLocation(view, Gravity.NO_GRAVITY,x,y);
                return recommendPhotoPop;



    }

    /** dip转换px */
    public  static int dip2px(int dip) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    /** pxz转换dip */
    public static int px2dip(int px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    public  static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕宽度
     *
     * @param context
     * @return
     */
    public  static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

}
