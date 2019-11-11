package com.qunar.im.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.qunar.im.ui.R;
import com.qunar.im.ui.fragment.WorkWorldAtShowFragment;
import com.qunar.im.ui.fragment.WorkWorldFragment;
import com.qunar.im.ui.fragment.WorkWorldNoticeFragment;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;
import com.qunar.im.ui.view.tableLayout.SlidingTabLayout;

import java.util.ArrayList;

public class SelfWorkWorldActivity extends SwipeBackActivity {

    private SlidingTabLayout slidingTabLayout;
    protected QtNewActionBar qtNewActionBar;//头部导航
    private MyPagerAdapter mAdapter;
    private ViewPager vp;
    private ArrayList<Fragment> mFragments = new ArrayList<>();

    private String[] mTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_self_work_world_activity);
        mTitles = new String[]{getString(R.string.atom_ui_my_moments), getString(R.string.atom_ui_my_repliess), "@" + getString(R.string.atom_ui_at_me)};
        initView();
        bindData();
    }


    private void bindData() {
        mFragments = new ArrayList<>();
//        for (int i = 0; i < mTitles.length; i++) {
//            mFragments.add(new WorkWorldFragment());
//        }
        mFragments.add(new WorkWorldFragment());
        mFragments.add(new WorkWorldNoticeFragment());
        mFragments.add(new WorkWorldAtShowFragment());

//        mFragments.add(new WorkWorldFragment());
        mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        vp.setAdapter(mAdapter);


        vp.setOffscreenPageLimit(2);
        slidingTabLayout.setViewPager(vp);
    }

    private void initView() {
        qtNewActionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(qtNewActionBar);
        setActionBarTitle(getString(R.string.atom_ui_tab_mine) + getString(R.string.atom_ui_workworld));
        vp = (ViewPager) findViewById(R.id.vp);
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.slidingtablayout);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }
}
