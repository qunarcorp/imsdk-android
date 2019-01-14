package com.qunar.im.ui.activity;

import android.content.Intent;
import android.os.Bundle;

/**
 * 发送至qtalk路由activity
 * Created by Lex lex on 2018/12/18.
 */
public class ShareRouteActivity extends IMBaseActivity {


    private boolean startActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toMainActivity();
    }

    private void toMainActivity() {
        Intent intent = getIntent();
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
