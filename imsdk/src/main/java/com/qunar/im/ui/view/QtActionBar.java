package com.qunar.im.ui.view;

import android.content.Context;
import androidx.appcompat.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.ui.R;
import com.qunar.im.base.util.Utils;
import com.qunar.im.base.util.graphics.ImageUtils;

import java.lang.ref.WeakReference;

/**
 * Created by xinbo.wang on 2015/4/13.
 */
public class QtActionBar extends Toolbar {
    LinearLayout container_right;
    FrameLayout left_btn;
    ImageView rightImgBtn;
    ImageView leftImgBtn;
    TextView rightTxtBtn,titleTxt;
    WeakReference<Context> wContext;

    public QtActionBar(Context context) {
        this(context, null);
    }

    public QtActionBar(Context context, AttributeSet attrs) {
        this(context,attrs, 0x7f0100b9);
    }

    public QtActionBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        int rightContentInsets=Utils.dpToPx(context, 16);
        setContentInsetsAbsolute(0, rightContentInsets);
        setContentInsetsRelative(0, rightContentInsets);
        wContext = new WeakReference<Context>(context);
    }

    public void initLeftBg()
    {
        left_btn = (FrameLayout) this.findViewById(R.id.left_layout);
        left_btn.setBackground(ImageUtils.createBGSelector(wContext.get().getResources().getColor(R.color.atom_ui_primary_color),
                wContext.get().getResources().getColor(R.color.atom_ui_button_primary_color_pressed)));
    }

    public void setPaddingTop(int height)
    {
        LinearLayout linearLayout = (LinearLayout) this.findViewById(R.id.action_bar_layout);
        linearLayout.setPadding(getPaddingLeft(),height,getPaddingRight(),getPaddingBottom());
        linearLayout.invalidate();
    }

    public FrameLayout getLeftButton()
    {
        return left_btn;
    }

    public SimpleDraweeView  getSelfGravatarImage(){
        return (SimpleDraweeView) this.findViewById(R.id.self_gravatar);
    }

    public TextView getShowNewVersion(){
        return (TextView) this.findViewById(R.id.textView_new_msg);
    }

    public TextView getTitleTextview()
    {
        if(titleTxt == null)
        {
            titleTxt  = (TextView) this.findViewById(R.id.txt_title);
        }
        return titleTxt;
    }

    public TextView getRightText()
    {
        if(rightTxtBtn == null)
        {
            rightTxtBtn = (TextView) this.findViewById(R.id.txt_right);
        }
        return rightTxtBtn;
    }


    public ImageView getRightImageBtn()
    {
        if(rightImgBtn == null)
        {
            rightImgBtn = (ImageView) this.findViewById(R.id.img_right);
        }
        return rightImgBtn;
    }

    public ImageView getLeftImgBtn(){
        if(leftImgBtn == null)
        {
            leftImgBtn = (ImageView) this.findViewById(R.id.img_left);
        }
        return leftImgBtn;
    }

    public LinearLayout getRightContainer()
    {
        if(container_right==null)
        {
            container_right = (LinearLayout) this.findViewById(R.id.container_right);
        }
        return container_right;
    }
}