package com.qunar.im.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.permission.PermissionCallback;
import com.qunar.im.permission.PermissionDispatcher;
import com.qunar.im.protobuf.dispatch.DispatchHelper;
import com.qunar.im.ui.R;
import com.qunar.im.ui.util.UpdateManager;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;

/**
 * 关于类
 */
public class AboutActivity extends SwipeBackActivity implements View.OnClickListener, PermissionCallback {

    private static final int CHECK_UPDATE = PermissionDispatcher.getRequestCode();

    private View about_newfun_layout, about_newversion_layout, about_history_layout, about_score_layout;
    private TextView about_version_tv;
    private ImageView about_logo_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_about);
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setActionBarTitle(R.string.atom_ui_setting_about);

        initViews();

    }

    int count;
    private void initViews() {
        about_newfun_layout = findViewById(R.id.about_newfun_layout);
        about_newversion_layout = findViewById(R.id.about_newversion_layout);
        about_history_layout = findViewById(R.id.about_history_layout);
        about_score_layout = findViewById(R.id.about_score_layout);
        about_newfun_layout.setVisibility(View.GONE);
        about_history_layout.setVisibility(View.GONE);
        about_score_layout.setVisibility(View.GONE);

        about_version_tv = (TextView) findViewById(R.id.about_version_tv);
        about_logo_tv = (ImageView) findViewById(R.id.about_logo_tv);

        String vn = QunarIMApp.getQunarIMApp().getVersionName();
        about_version_tv.setText(getString(R.string.atom_ui_about_version) + vn +
                " (" + QunarIMApp.getQunarIMApp().getVersion() + ")" + "-" + DataUtils.getInstance(this).getPreferences(Constants.Preferences.PATCH_TIMESTAMP + "_" + vn, "0"));
        about_logo_tv.setImageResource(CommonConfig.globalContext.getApplicationInfo().icon);
        about_logo_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                toast(String.valueOf(count));
                if(count>5){
                    toast("导航已更新！");
                    DispatchHelper.sync("updateNav", new Runnable() {
                        @Override
                        public void run() {
                            QtalkNavicationService.getInstance().updateNavicationConfig(true);
                        }
                    });
                    count = 0;
                }
            }
        });
        about_newfun_layout.setOnClickListener(this);
        about_newversion_layout.setOnClickListener(this);
        about_newversion_layout.setOnLongClickListener(longclick);
        about_history_layout.setOnClickListener(this);
        about_score_layout.setOnClickListener(this);
    }

    @Override
    public void responsePermission(int requestCode, boolean granted) {
        if (!granted) return;
        if (requestCode == CHECK_UPDATE) {
            UpdateManager.getUpdateManager().checkAppUpdate(this, true,true);
        }
    }

    public void requestUpdate()
    {
        PermissionDispatcher.
                requestPermissionWithCheck(this, new int[]{PermissionDispatcher.REQUEST_WRITE_EXTERNAL_STORAGE,
                                PermissionDispatcher.REQUEST_READ_EXTERNAL_STORAGE}, this,
                        CHECK_UPDATE);
    }

    View.OnLongClickListener longclick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AboutActivity.this);
            View views = LayoutInflater.from(AboutActivity.this).inflate(R.layout.atom_ui_secret_talk, null);
            TextView title = (TextView) views.findViewById(R.id.secret_talk_title);
            title.setText(R.string.atom_ui_title_current_version);//用来测试已当前版本号申请更新
            final EditText etContext = (EditText) views.findViewById(R.id.et_content);
            etContext.setInputType(InputType.TYPE_CLASS_NUMBER);
            etContext.setText(QunarIMApp.getQunarIMApp().getVersion() + "");
            builder.setView(views);
            builder.setPositiveButton(getString(R.string.atom_ui_common_confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!TextUtils.isEmpty(etContext.getText().toString().trim())) {
                        int vc = Integer.valueOf(etContext.getText().toString());
                        UpdateManager.getUpdateManager().checkAppUpdate(AboutActivity.this, true, true, vc);
                    }
                }
            });
            builder.setNegativeButton(getString(R.string.atom_ui_common_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.create().show();
            return true;
        }
    };

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.about_newfun_layout) {

        } else if (i == R.id.about_newversion_layout) {
            requestUpdate();
        } else if (i == R.id.about_history_layout) {

        } else if (i == R.id.about_score_layout) {

        }
    }
}
