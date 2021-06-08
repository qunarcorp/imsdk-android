package com.qunar.im.ui.view.tabview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.viewpager.widget.PagerAdapter;

/**
 * Created by saber on 16-1-5.
 */
public class MainTabProvider implements SmartTabLayout.TabProvider {
    @Override
    public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
        MainTabView tabView = new MainTabView(container.getContext());
        tabView.setTitle((String) adapter.getPageTitle(position));
        tabView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        return tabView;
    }
}
