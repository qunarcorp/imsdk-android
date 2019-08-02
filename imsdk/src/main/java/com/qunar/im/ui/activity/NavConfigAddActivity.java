package com.qunar.im.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.Utils;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.permission.PermissionCallback;
import com.qunar.im.permission.PermissionDispatcher;
import com.qunar.im.protobuf.dispatch.DispatchHelper;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;
import com.qunar.im.ui.view.zxing.activity.CaptureActivity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by froyomu on 2019/3/13
 * <p>
 * Describe:
 */
public class NavConfigAddActivity extends SwipeBackActivity implements PermissionCallback {

    public static final int NAV_ADD_REQUEST_CODE = 2019;
    public static final int NAV_ADD_RESPONSE_CODE = 2020;

    private static final int SCAN_REQUEST = PermissionDispatcher.getRequestCode();

    private QtNewActionBar actionBar;
    private EditText atom_ui_nav_add_name,atom_ui_nav_add_url;
    private LinearLayout atom_ui_nav_config_scan_layout;

    private String navUrl;
    private String navName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_nav_config_add);

        initView();
        initActionbar();
    }

    private void initView(){
        atom_ui_nav_add_name = (EditText) findViewById(R.id.atom_ui_nav_add_name);
        atom_ui_nav_add_url = (EditText) findViewById(R.id.atom_ui_nav_add_url);

        atom_ui_nav_config_scan_layout = (LinearLayout) findViewById(R.id.atom_ui_nav_config_scan_layout);
        atom_ui_nav_config_scan_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionDispatcher.requestPermissionWithCheck(NavConfigAddActivity.this, new int[]{PermissionDispatcher.REQUEST_CAMERA}, NavConfigAddActivity.this  , SCAN_REQUEST);
            }
        });
    }

    private void initActionbar(){
        actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setActionBarTitle(R.string.atom_ui_btn_new_configuration);
        setActionBarRightText(R.string.atom_ui_common_save);
        setActionBarRightTextClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = atom_ui_nav_add_url.getText().toString();
                if(TextUtils.isEmpty(text)){
                    toast(getString(R.string.atom_ui_tip_nav_address));
                    return;
                }
                String url;
                if(text.toLowerCase().startsWith("http://") || text.toLowerCase().startsWith("https://")){
                    url = text;
                }else {
                    url = QtalkNavicationService.NAV_CONFIG_PUBLIC_DEFAULT + "?c=" + text;
                }
                setNavResult(url);
            }
        });
    }

    private void setNavResult(String url){
        navUrl = url;
        navName = TextUtils.isEmpty(atom_ui_nav_add_name.getText()) ? navName : atom_ui_nav_add_name.getText().toString();
        Intent intent = new Intent();
        intent.putExtra(Constants.BundleKey.NAV_ADD_NAME,navName);
        intent.putExtra(Constants.BundleKey.NAV_ADD_URL,navUrl);
        setResult(NAV_ADD_RESPONSE_CODE,intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionDispatcher.onRequestPermissionsResult(requestCode, grantResults);
    }


    @Override
    public void responsePermission(int requestCode, boolean granted) {
        if (!granted){
            toast(getString(R.string.atom_ui_tip_request_permission));
            return;
        }
        if (requestCode == SCAN_REQUEST) {
            scanQrCode();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SCAN_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra("content");
                if (!TextUtils.isEmpty(content)) {
                    if(content.contains("~")){
                        String[] strs = content.split("~");
                        navName = strs[0];
                        navUrl = strs[1];
                        getConfig();
                    }else{
                        navUrl = content;

                        Uri uri = Uri.parse(navUrl);
                        HashMap<String, String> map = Protocol.splitParams(uri);
                        String configurl = map.get("configurl");
                        String configname = map.get("configname");
                        if(!TextUtils.isEmpty(configurl)){
                            String url = new String(Base64.decode(configurl,Base64.NO_WRAP));
                            navName = configname;
                            navUrl = url;
                            getConfig();
                        }else {
                            redirectUrl(navUrl);
                        }
                    }
                }
            }
        }
    }

    private void redirectUrl(final String originUrl) {
        DispatchHelper.Async("redirectUrl",true, new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                try {
                    conn = (HttpURLConnection) new URL(originUrl).openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                conn.setInstanceFollowRedirects(false);
                conn.setConnectTimeout(5000);
                String url = conn.getHeaderField("Location");
                conn.disconnect();
                if(TextUtils.isEmpty(url)){
                    url = originUrl;
                }
                Uri uri = Uri.parse(url);
                HashMap<String, String> map = Protocol.splitParams(uri);
                String configurl = map.get("configurl");
                String configname = map.get("configname");
                if(!TextUtils.isEmpty(configurl)){
                    String realUrl = new String(Base64.decode(configurl,Base64.NO_WRAP));
                    navName = configname;
                    navUrl = realUrl;
                }
                getConfig();
            }
        });
    }

    private void getConfig() {
        if(TextUtils.isEmpty(navUrl)){
            toast(getString(R.string.atom_ui_tip_nav_url_null));
            return;
        }
        if(!Utils.IsUrl(navUrl)){
            toast(getString(R.string.atom_ui_tip_nav_url_invalidate));
            return;
        }
        try {
            URL url = new URL(navUrl);
            if(url != null){
                if(TextUtils.isEmpty(url.getHost())
                        || TextUtils.isEmpty(url.getProtocol())){
                    toast(getString(R.string.atom_ui_tip_nav_url_invalidate));
                    return;
                }
            }else{
                toast(getString(R.string.atom_ui_tip_nav_url_invalidate));
                return;
            }
        } catch (MalformedURLException e) {
            toast(getString(R.string.atom_ui_tip_nav_url_invalidate));
            return;
        }
        setNavResult(navUrl);
    }


    private void scanQrCode() {
        Intent scanQRCodeIntent = new Intent(this, CaptureActivity.class);
        startActivityForResult(scanQRCodeIntent, SCAN_REQUEST);
    }

}
