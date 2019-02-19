package com.qunar.im.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qunar.im.base.module.AnonymousData;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.ui.R;
import com.qunar.im.ui.imagepicker.ImagePicker;
import com.qunar.im.ui.view.IconView;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;
import com.qunar.im.utils.HttpUtil;

import static com.qunar.im.base.presenter.impl.ReleaseCircleManagerPresenter.ANONYMOUS_NAME;
import static com.qunar.im.base.presenter.impl.ReleaseCircleManagerPresenter.REAL_NAME;
import static com.qunar.im.ui.activity.ReleaseCircleActivity.UUID_STR;


public class IdentitySelectActivity extends SwipeBackActivity {

    protected QtNewActionBar qtNewActionBar;//头部导航
    protected LinearLayout real_name,anonymous_name;
    private TextView anonymous_real_name;
    private IconView identity_anonymous;
    private IconView identity_real;
    protected String uuid;
    private AnonymousData mAnonymousData;

    public static String now_identity_type= "now_identity_type";

    public static String ANONYMOUS_DATA="ANONYMOUS_DATA";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_identity_select);
        bindView();
        bindData();
    }

    private void bindView(){
        qtNewActionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(qtNewActionBar);
        real_name  = (LinearLayout) findViewById(R.id.real_name);
        anonymous_name = (LinearLayout) findViewById(R.id.anonymous_name);
        anonymous_real_name = (TextView) findViewById(R.id.anonymous_real_name);
        identity_anonymous = (IconView) findViewById(R.id.identity_anonymous);
        identity_real = (IconView) findViewById(R.id.identity_real);
    }

    private void showSelect(){
        int i = getIntent().getIntExtra(now_identity_type,0);
        if(i==REAL_NAME){
            identity_anonymous.setTextColor(getResources().getColor(R.color.send_no));
            identity_real.setTextColor(getResources().getColor(R.color.atom_ui_new_like_select));
        }else{

            identity_anonymous.setTextColor(getResources().getColor(R.color.atom_ui_new_like_select));
            identity_real.setTextColor(getResources().getColor(R.color.send_no));

        }
    }

    private void bindData(){
        setActionBarTitle("发布身份");
        showSelect();
        uuid = getIntent().getStringExtra(UUID_STR);
        real_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectIdentity(REAL_NAME);
            }
        });

        anonymous_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectIdentity(ANONYMOUS_NAME);
            }
        });


        HttpUtil.getAnonymous(uuid, new ProtocolCallback.UnitCallback<AnonymousData>() {
            @Override
            public void onCompleted(final AnonymousData anonymousData) {
//                mView.setAnonymousData(anonymousData);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAnonymousData = anonymousData;
                        anonymous_real_name.setText("花名: "+mAnonymousData.getData().getAnonymous());
                    }
                });

            }

            @Override
            public void onFailure(String errMsg) {

            }
        });

    }


    public void selectIdentity(int i ){
        if(i==ANONYMOUS_NAME){
            if(mAnonymousData==null){
                Toast.makeText(this,"未获取到匿名信息",Toast.LENGTH_LONG).show();
                return;
            }
        }
        Intent intent = new Intent();
        intent.putExtra(ReleaseCircleActivity.EXTRA_IDENTITY, i);
        intent.putExtra(ANONYMOUS_DATA,mAnonymousData);
        setResult(ImagePicker.RESULT_CODE_ITEMS, intent);
        finish();
    }
}
