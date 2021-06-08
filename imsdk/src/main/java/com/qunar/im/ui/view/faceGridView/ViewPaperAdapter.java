package com.qunar.im.ui.view.faceGridView;

import android.view.View;
import android.widget.GridView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

/**
 * Created by xinbo.wang on 2015/2/5.
 */
public class ViewPaperAdapter extends PagerAdapter {
    private List<GridView> pageViews;

    public ViewPaperAdapter(List<GridView> pageViews) {
        super();
        this.pageViews=pageViews;
    }

    // 显示数目
    @Override
    public int getCount() {
        return pageViews.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager)arg0).removeView(pageViews.get(arg1));
    }


    @Override
    public Object instantiateItem(View arg0, int arg1) {
        ((ViewPager)arg0).addView(pageViews.get(arg1));
        return pageViews.get(arg1);
    }
}
