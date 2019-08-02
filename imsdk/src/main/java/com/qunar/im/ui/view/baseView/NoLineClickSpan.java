package com.qunar.im.ui.view.baseView;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Created by xinbo.wang on 2016-12-15.
 */
public class NoLineClickSpan extends ClickableSpan {
    String text;
    ProcessHyperLinkClick processHyperLinkClick;
    int color;

    public NoLineClickSpan(String text, int color, ProcessHyperLinkClick click) {
        super();
        this.text = text;
        this.processHyperLinkClick = click;
        this.color = color;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        //#00AFC7
        ds.setColor(this.color);
        ds.setUnderlineText(false); //去掉下划线
    }

    @Override
    public void onClick(View widget) {
        this.processHyperLinkClick.process(text); //点击超链接时调用
    }

    public interface ProcessHyperLinkClick
    {
        void process(String url);
    }
}
