package com.qunar.im.ui.adapter;

import android.content.Context;
import android.util.AttributeSet;

import androidx.viewpager.widget.ViewPager;

import com.qunar.im.core.services.QtalkNavicationService;

public class TabMainFragmentViewPager extends ViewPager {
    public TabMainFragmentViewPager(Context context) {
        super(context);
    }

    public TabMainFragmentViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        if (item==2) {


            if ("ejabhost1".equals(QtalkNavicationService.getInstance().getXmppdomain())
                    || "ejabhost2".equals(QtalkNavicationService.getInstance().getXmppdomain())) {

                item=2;
            }else{
                item=3;
            }
        }else if(item==3){
            item=4;
        }
        super.setCurrentItem(item, smoothScroll);
    }
}
