package com.qunar.im.ui.activity;

import android.content.Intent;
import android.os.Bundle;

public class ShareWorkWorldRouteActivity extends IMBaseActivity {

    private boolean startActivity;
    public static String WORKWORLDSHARE="WORKWORLDSHARE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toMainActivity();
    }

    private void toMainActivity() {
        Intent intent = getIntent();
        intent.putExtra(WORKWORLDSHARE,true);
        intent.setClass(this, TabMainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        toMainActivity();
    }

}
