package com.qunar.im.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.qunar.im.ui.R;
import com.qunar.im.ui.view.QtNewActionBar;

/**
 * Created by xinbo.wang on 2016/7/25.
 */
public class FragmentActivity extends IMBaseActivity {
    public static final String CONTENT = "im.fragment.content";
    public static final String TITLE = "im.fragment.title";

    @Override
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);

        setContentView(R.layout.atom_ui_activity_blank);
        QtNewActionBar actionBar = (QtNewActionBar) findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {

            if (intent.getExtras().containsKey(CONTENT)) {
                String fragmentName = intent.getExtras().getString(CONTENT);
                if (fragmentName != null) {
                    Fragment fragment = Fragment.instantiate(this, fragmentName, intent.getExtras());

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.layout_blanck_content, fragment);
                    transaction.commit();
                }
            }
            String title = getIntent().getStringExtra(TITLE);
            if (!TextUtils.isEmpty(title)) {
                setActionBarTitle(title);
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
    }
}
