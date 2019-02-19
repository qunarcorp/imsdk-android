package com.qunar.im.ui.activity;

import android.os.Bundle;

import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.ui.R;
import com.qunar.im.ui.fragment.GalleryFragment;
import com.qunar.im.ui.fragment.ImageBroswingFragment;
import com.qunar.im.ui.fragment.WorkWorldBrowersingFragment;
import com.qunar.im.ui.util.StatusBarUtil;

/**
 * Modify by xinbo.wang on 2015/4/24.
 */
public class ImageBrowersingActivity extends IMBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.d("debug", "image oncreate");
        super.onCreate(savedInstanceState);
        StatusBarUtil.setTransparentForImageView(this, null);
        setContentView(R.layout.atom_ui_layout_blank_content);
        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey(Constants.BundleKey.CONVERSATION_ID))
        {
            initViewPagerBrowsing();
        }else if(bundle.containsKey(Constants.BundleKey.WORK_WORLD_BROWERSING)){
            initViewWorkWorldBrowsing();
        }else {
            initViews();
        }
    }

    private void initViewWorkWorldBrowsing(){
        WorkWorldBrowersingFragment galleryFragment = new WorkWorldBrowersingFragment();

        galleryFragment.setArguments(getIntent().getExtras());

        getSupportFragmentManager().beginTransaction()

                .replace(R.id.layout_blanck_content, galleryFragment)

                .commit();
    }

    private void initViewPagerBrowsing() {
        GalleryFragment galleryFragment = new GalleryFragment();

        galleryFragment.setArguments(getIntent().getExtras());

        getSupportFragmentManager().beginTransaction()

                .replace(R.id.layout_blanck_content, galleryFragment)

                .commit();
    }

    private void initViews()
    {
        // Create fragment and define some of it transitions
        ImageBroswingFragment sharedElementFragment1 = ImageBroswingFragment.newInstance(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.layout_blanck_content, sharedElementFragment1)
                .commit();
    }
}