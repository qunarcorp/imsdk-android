package com.qunar.im.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


/**
 * Created by froyomu on 2019-09-06
 * <p>
 * Describe:
 */
public class IMBaseLoginActivity extends IMBaseActivity{
    protected int clickCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void bindCheckUpdateView(View v){
        if(v == null)
            return;
        v.setOnClickListener((view) -> {
            clickCount++;
            if(clickCount >= 6){
                goAbout();
                clickCount = 0;
            }
        });
    }

    private void goAbout(){
        startActivity(new Intent(this,AboutActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
