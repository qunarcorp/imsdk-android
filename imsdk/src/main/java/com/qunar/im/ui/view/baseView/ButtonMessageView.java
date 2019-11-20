package com.qunar.im.ui.view.baseView;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qunar.im.ui.R;
import com.qunar.im.ui.view.IconView;

public class ButtonMessageView extends LinearLayout {

    private View view;
    private LinearLayout content;
    private LinearLayout bottom_layout;
    private TextView bottom_content;
    private LinearLayout leftBtn, middleBtn, rightBtn,btnGroup;
    private IconView leftIcon, middleIcon, rightIcon;
    private TextView leftText, middleText, rightText;

    public ButtonMessageView(Context context) {
        super(context);
        init(context);
    }

    public ButtonMessageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ButtonMessageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    public void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
         view = inflater.inflate(R.layout.atom_ui_button_message, null);

        content = view.findViewById(R.id.content);
        bottom_layout = view.findViewById(R.id.bottom_layout);
        bottom_content = view.findViewById(R.id.bottom_content);
        leftBtn = view.findViewById(R.id.left_btn);
        middleBtn = view.findViewById(R.id.middle_btn);
        rightBtn = view.findViewById(R.id.right_btn);
        leftIcon = view.findViewById(R.id.left_icon);
        middleIcon = view.findViewById(R.id.middle_icon);
        rightIcon = view.findViewById(R.id.right_icon);
        leftText = view.findViewById(R.id.left_text);
        middleText = view.findViewById(R.id.middle_text);
        rightText = view.findViewById(R.id.right_text);
        btnGroup = view.findViewById(R.id.button_group);

        addView(view);
    }

    public LinearLayout getBtnGroup() {
        return btnGroup;
    }

    public void setLeftBtn(OnClickListener listener){
        leftBtn.setOnClickListener(listener);
    }
    public void setLeftIcon(@StringRes int res ){
        leftIcon.setText(res);
    }
    public void setLeftText(String text){
        leftText.setText(text);
    }

    public void setRightBtn(OnClickListener listener){
        rightBtn.setOnClickListener(listener);
    }
    public void setRightIcon(@StringRes int res ){
        rightIcon.setText(res);
    }
    public void setRightText(String text){
        rightText.setText(text);
    }
    public void setMiddleBtn(OnClickListener listener){
        middleBtn.setOnClickListener(listener);
    }
    public void setMiddleIcon(@StringRes int res ){
        middleIcon.setText(res);
    }
    public void setMiddleText(String text){
        middleText.setText(text);
    }

    public void setContent(View view){
        content.addView(view);
    }
    public LinearLayout getContentLayout(){
        return content;
    }

    public void showBottom(boolean show){
        if(show){
            bottom_layout.setVisibility(View.VISIBLE);
        }else{
            bottom_layout.setVisibility(View.GONE);
        }

        view.refreshDrawableState();
    }

    public void setBottomContent(String  text){
        bottom_content.setText(text);

    }

}
