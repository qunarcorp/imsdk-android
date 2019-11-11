package com.qunar.im.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.module.AnonymousData;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.ui.R;
import com.qunar.im.ui.imagepicker.ImagePicker;
import com.qunar.im.ui.view.IconView;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;
import com.qunar.im.utils.HttpUtil;

import static com.qunar.im.ui.presenter.impl.ReleaseCircleManagerPresenter.ANONYMOUS_NAME;
import static com.qunar.im.ui.presenter.impl.ReleaseCircleManagerPresenter.REAL_NAME;
import static com.qunar.im.ui.activity.WorkWorldReleaseCircleActivity.UUID_STR;


public class IdentitySelectActivity extends SwipeBackActivity {

    protected QtNewActionBar qtNewActionBar;//头部导航
    protected LinearLayout real_name, anonymous_name;
    private TextView anonymous_real_name;
    private IconView identity_anonymous;
    private IconView identity_real;
    private IconView change_icon;
    private TextView change_text;
    private LinearLayout change_layout;
    protected String uuid;
    private AnonymousData mAnonymousData;

    public static String now_identity_type = "now_identity_type";

    public static int cacheIdentity = 0;

    public static String ANONYMOUS_DATA = "ANONYMOUS_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_identity_select);
        bindView();
        initChang();
        bindData();

    }

    private void initChang() {
        showChang(0);
        change_text.setText("正在寻找合适的花名");
    }

    private void bindView() {
        qtNewActionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(qtNewActionBar);
        real_name = (LinearLayout) findViewById(R.id.real_name);
        anonymous_name = (LinearLayout) findViewById(R.id.anonymous_name);
        anonymous_real_name = (TextView) findViewById(R.id.anonymous_real_name);
        identity_anonymous = (IconView) findViewById(R.id.identity_anonymous);
        identity_real = (IconView) findViewById(R.id.identity_real);
        change_icon = (IconView) findViewById(R.id.change_icon);
        change_text = (TextView) findViewById(R.id.change_text);
        change_layout = (LinearLayout) findViewById(R.id.change_layout);
    }

    private void showSelect() {
        mAnonymousData = null;
        cacheIdentity = getIntent().getIntExtra(now_identity_type, 0);
        if (cacheIdentity == REAL_NAME) {
            identity_anonymous.setTextColor(getResources().getColor(R.color.send_no));
            identity_real.setTextColor(getResources().getColor(R.color.atom_ui_new_like_select));

        } else {
            try {
                identity_anonymous.setTextColor(getResources().getColor(R.color.atom_ui_new_like_select));
                identity_real.setTextColor(getResources().getColor(R.color.send_no));
                if (getIntent().hasExtra(ANONYMOUS_DATA)) {
                    mAnonymousData = (AnonymousData) getIntent().getSerializableExtra(ANONYMOUS_DATA);
                    anonymous_real_name.setText(getString(R.string.atom_ui_nickname) + "：" + mAnonymousData.getData().getAnonymous());
                }


            } catch (Exception e) {
                Logger.i("初始化花名出错");
            }

        }
    }


    private void showChang(final int type) {

        if (type == 0) {
            change_text.setTextColor(getResources().getColor(R.color.bfbfbf));
            change_text.setText("花名已确定,不可更改");
            change_icon.setVisibility(View.GONE);
            change_layout.setEnabled(false);
        } else if (type == 1) {
            change_text.setTextColor(getResources().getColor(R.color.atom_ui_light_gray_99));
            change_text.setText(getString(R.string.atom_ui_change_change));
//            change_icon.setTextColor(getResources().getColor(R.color.atom_ui_new_like_select));
            change_icon.setVisibility(View.VISIBLE);
            change_layout.setEnabled(true);
        }


    }


    private void bindData() {
        setActionBarTitle(getString(R.string.atom_ui_my_identify));
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

        change_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAnonymousByUUID(true);
            }
        });

//        if (mAnonymousData==null) {


        getAnonymousByUUID(false);

        setActionBarRightText(getString(R.string.atom_ui_common_confirm));
        setActionBarRightTextClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectIdentity(cacheIdentity);
            }
        });
//        }

    }

    private void getAnonymousByUUID(final boolean isChange) {
        HttpUtil.getAnonymous(uuid, new ProtocolCallback.UnitCallback<AnonymousData>() {
            @Override
            public void onCompleted(final AnonymousData anonymousData) {
//                mView.setAnonymousData(anonymousData);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isChange) {
                            mAnonymousData = anonymousData;
                            anonymous_real_name.setText(getString(R.string.atom_ui_nickname) + ": " + mAnonymousData.getData().getAnonymous());
                        } else {
                            if (mAnonymousData == null) {
                                mAnonymousData = anonymousData;
                                anonymous_real_name.setText(getString(R.string.atom_ui_nickname) + ": " + mAnonymousData.getData().getAnonymous());
                            }
                        }


                        showChang(anonymousData.getData().getReplaceable());
                    }


                });

            }

            @Override
            public void onFailure(String errMsg) {

            }
        });
    }


    public void selectIdentity(int i) {
        if (i == ANONYMOUS_NAME) {
            if (mAnonymousData == null) {
                Toast.makeText(this, "未获取到匿名信息", Toast.LENGTH_LONG).show();
                return;
            }
        }
        Intent intent = new Intent();
        intent.putExtra(WorkWorldReleaseCircleActivity.EXTRA_IDENTITY, i);
        intent.putExtra(ANONYMOUS_DATA, mAnonymousData);
        setResult(ImagePicker.RESULT_CODE_ITEMS, intent);
        finish();
    }
}
