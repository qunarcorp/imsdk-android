package com.qunar.im.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qunar.im.ui.R;

import java.lang.ref.WeakReference;

/**
 * Created by hubin on 2017/12/19.
 */

public class QtNewActionBar extends Toolbar {
    //标题文字
    private TextView title;
    private TextView mood;
    private IconView leftIcon;
    private TextView leftText;
    private TextView rightText;
    private LinearLayout leftLayout;
    private RelativeLayout leftUnReadLayout;
    private TextView leftUnReadText;
    private LinearLayout rightLayout;
    private IconView rightIcon;
    private IconView rightIconSpecial;
    private LinearLayout titleLayout;
    private RelativeLayout title_bar_layout;

    //搜索部分
    private LinearLayout search_bar_layout;
    private LinearLayout search_bar_left_layout;
    private EditText search_bar_search_edittext;
    private IconView search_bar_clean;
    private LinearLayout search_bar_cancle;


    WeakReference<Context> wContext;

    public QtNewActionBar(Context context) {
        super(context);
    }

    public QtNewActionBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public QtNewActionBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public RelativeLayout getTitleBarLayout() {
        if (title_bar_layout == null) {
            title_bar_layout = (RelativeLayout) findViewById(R.id.title_bar_layout);
        }
        return title_bar_layout;
    }

    public LinearLayout getSearchBarCancleLayout() {
        if (search_bar_cancle == null) {
            search_bar_cancle = (LinearLayout) findViewById(R.id.search_bar_cancle);
        }
        return search_bar_cancle;
    }

    public IconView getSearchBarCleanView(){
        if (search_bar_clean == null) {
            search_bar_clean = (IconView) findViewById(R.id.search_bar_clean);
        }
        return search_bar_clean;
    }

    public EditText getSearchBarSearchEdittext() {
        if (search_bar_search_edittext == null) {
            search_bar_search_edittext = (EditText) findViewById(R.id.search_bar_search_edittext);
        }
        return search_bar_search_edittext;
    }

    public LinearLayout getSearchLayout(){
        if (search_bar_layout == null) {
            search_bar_layout = (LinearLayout) findViewById(R.id.search_bar_layout);
        }
        return search_bar_layout;
    }

    public LinearLayout getSearchLayoutLeftLayout(){

        if (search_bar_left_layout == null) {
            search_bar_left_layout = (LinearLayout) findViewById(R.id.search_bar_left_layout);
        }
        return search_bar_left_layout;
    }

    public IconView getRightIconSpecial(){
        if (rightIconSpecial == null) {
            rightIconSpecial = (IconView) findViewById(R.id.right_special);
        }
        return rightIconSpecial;
    }

    public IconView getRightIcon() {
        if (rightIcon == null) {
            rightIcon = (IconView) findViewById(R.id.right_icon);
        }
        return rightIcon;
    }

    public LinearLayout getRightLayout() {
        if (rightLayout == null) {
            rightLayout = (LinearLayout) findViewById(R.id.right_layout);
        }
        return rightLayout;
    }

    public TextView getLeftUnReadText() {
        if (leftUnReadText == null) {
            leftUnReadText = (TextView) findViewById(R.id.left_unread_text);
        }
        return leftUnReadText;
    }

    public LinearLayout getLeftLayout() {
        if (leftLayout == null) {
            leftLayout = (LinearLayout) findViewById(R.id.left_layout);
        }
        return leftLayout;
    }

    public TextView getTextTitle() {
        if (title == null) {
            title = (TextView) this.findViewById(R.id.title);
        }
        return title;
    }

    public LinearLayout getTitleLayout(){

        if (titleLayout == null) {
            titleLayout = (LinearLayout) this.findViewById(R.id.title_layout);
        }
        return titleLayout;
    }

    public IconView getLeftIcon() {
        if (leftIcon == null) {
            leftIcon = (IconView) this.findViewById(R.id.left_icon);
        }
        return leftIcon;
    }

    public TextView getLeftText() {
        if (leftText == null) {
            leftText = (TextView) this.findViewById(R.id.left_text);
        }
        return leftText;
    }
    public TextView getRightText() {
        if (rightText == null) {
            rightText = (TextView) this.findViewById(R.id.right_text);
        }
        return rightText;
    }

    public TextView getMood() {
        if (mood == null) {
            mood = (TextView) this.findViewById(R.id.mood);
        }
        return mood;
    }
}
