package com.qunar.im.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qunar.im.utils.HttpUtil;
import com.qunar.im.base.jsonbean.NavConfigResult;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.Utils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.permission.PermissionCallback;
import com.qunar.im.permission.PermissionDispatcher;
import com.qunar.im.ui.R;
import com.qunar.im.ui.entity.NavConfigInfo;
import com.qunar.im.ui.view.IconView;
import com.qunar.im.ui.view.zxing.activity.CaptureActivity;

import java.net.MalformedURLException;
import java.net.URL;

import static android.app.Activity.RESULT_OK;
import static com.qunar.im.base.util.JsonUtils.getGson;

/**
 * 高级导航配置
 * Created by hubo.hu on 2017/8/31.
 */

public class NavConfigFragment extends DialogFragment implements PermissionCallback {
    private static final int SCAN_REQUEST = PermissionDispatcher.getRequestCode();

    private EditText nav_config_url,et_xmpp_port,et_xmpp_domain,et_xmpp_address, nav_config_name;
    private TextView btnload, btnAddConfig, navSavebtn, navCancelBtn;
    private IconView nav_config_scan;

    private NavConfigResult mNavConfigResult;
    private String navname;
    private String navurl;

    private OnSaveListener mOnSaveListener;

    public interface OnSaveListener{
        void saveConfigInfo(NavConfigInfo info);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.atom_ui_activity_nav_config_dialog, null);
        btnload = (TextView) view.findViewById(R.id.btn_load);
        navSavebtn = (TextView) view.findViewById(R.id.nav_config_dialog_save);
        navCancelBtn = (TextView) view.findViewById(R.id.nav_config_dialog_cancel);
        nav_config_scan = (IconView) view.findViewById(R.id.nav_config_scan);
        nav_config_name = (EditText) view.findViewById(R.id.nav_config_name);
        nav_config_url = (EditText) view.findViewById(R.id.nav_config_url);
        et_xmpp_address = (EditText) view.findViewById(R.id.et_xmpp_address);
        et_xmpp_port = (EditText) view.findViewById(R.id.et_xmpp_port);
        et_xmpp_domain = (EditText) view.findViewById(R.id.et_xmpp_domain);

//        nav_config_url.setText("https://qt.qunar.com/package/static/qtalk/publicnav?c=qunar.com");
        btnload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getConfig();
            }
        });
        nav_config_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionDispatcher.requestPermissionWithCheck(getActivity(), new int[]{PermissionDispatcher.REQUEST_CAMERA}, NavConfigFragment.this  , SCAN_REQUEST);
            }
        });
        navSavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!saveSetting()){
                    return;
                }
                NavConfigInfo config = new NavConfigInfo();
                config.setName(nav_config_name.getText().toString());
                config.setUrl(nav_config_url.getText().toString());

                OnSaveListener listener = (OnSaveListener) getActivity();
                listener.saveConfigInfo(config);
                if(mOnSaveListener != null) {
                    mOnSaveListener.saveConfigInfo(config);
                }
                dismiss();

            }
        });
        navCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        Dialog dialog = builder.setView(view).create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.atom_ui_DialogAnimation;

        Bundle bundle = getArguments();
        if(bundle != null && bundle.containsKey("navconfig_info")){
            mNavConfigResult = (NavConfigResult) bundle.get("navconfig_info");
            navname = (String) bundle.get("navconfig_domain");
            navurl = (String) bundle.get("navconfig_url");
            updateUI(mNavConfigResult);
        }
        return dialog;
    }

    private void getServerConfig(final String url) {
        HttpUtil.getServerConfigAsync(url, new ProtocolCallback.UnitCallback<NavConfigResult>() {
            @Override
            public void onCompleted(final NavConfigResult navConfigResult) {
                mNavConfigResult = navConfigResult;
                navurl = url;
                navname = nav_config_name.getText().toString();
                CommonConfig.mainhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), R.string.atom_ui_tip_nav_load_success, Toast.LENGTH_SHORT).show();
                        updateUI(navConfigResult);
                    }
                });
            }

            @Override
            public void onFailure(String errMsg) {
                CommonConfig.mainhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), R.string.atom_ui_tip_nav_load_failed, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private boolean saveSetting() {
        if (mNavConfigResult != null && mNavConfigResult.ad != null) {
//            mNavConfigResult.baseaddess.xmpp = et_xmpp_address.getText().toString().trim();
            int port = 5222;
            try {
                port = Integer.parseInt(et_xmpp_port.getText().toString().trim());
            }catch (Exception e){
                Toast.makeText(getActivity(), R.string.atom_ui_tip_nav_port,Toast.LENGTH_LONG).show();
                return false;
            }
            if(TextUtils.isEmpty(nav_config_name.getText().toString())){
                Toast.makeText(getActivity(), R.string.atom_ui_tip_nav_name,Toast.LENGTH_LONG).show();
                return false;
            }else if(TextUtils.isEmpty(nav_config_url.getText().toString())){
                Toast.makeText(getActivity(), R.string.atom_ui_tip_nav_address,Toast.LENGTH_LONG).show();
                return false;
            }else if (TextUtils.isEmpty(et_xmpp_address.getText().toString())
                    || TextUtils.isEmpty(et_xmpp_port.getText().toString())
                    || TextUtils.isEmpty(et_xmpp_domain.getText().toString())){
                Toast.makeText(getActivity(), R.string.atom_ui_tip_nav_save_failed,Toast.LENGTH_LONG).show();
                return false;
            }

            String jsonString = getGson().toJson(mNavConfigResult);
            //根据名字保存config，重名覆盖
            if(!TextUtils.isEmpty(DataUtils.getInstance(getActivity()).getPreferences(nav_config_name.getText().toString(), ""))){
                Toast.makeText(getActivity(), R.string.atom_ui_tip_nav_name_exists,Toast.LENGTH_LONG).show();
                nav_config_url.requestFocus();
                return false;
            }
            DataUtils.getInstance(getActivity()).putPreferences(nav_config_name.getText().toString(), jsonString);
            //取消文件保存
//            ConfigFileUtils.saveNavConfig(jsonString, getActivity(), nav_config_name.getText().toString() + ConfigFileUtils.CONFIG_FILE_NAME);
            Toast.makeText(getActivity(), R.string.atom_ui_tip_nav_saved,Toast.LENGTH_LONG).show();
            return true;
        }else{
            Toast.makeText(getActivity(), R.string.atom_ui_tip_nav_save_faield,Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private void updateUI(NavConfigResult result) {
        if (result == null)
            return;
        nav_config_name.setText(navname);
        nav_config_url.setText(navurl);
        et_xmpp_address.setText(result.baseaddess.xmpp);
        et_xmpp_port.setText(result.baseaddess.xmppmport+"");
        et_xmpp_domain.setText(result.baseaddess.domain);
    }

    void scanQrCode() {
        Intent scanQRCodeIntent = new Intent(getActivity(), CaptureActivity.class);
        startActivityForResult(scanQRCodeIntent, SCAN_REQUEST);
    }

    public void setOnSaveListener(OnSaveListener listener){
        mOnSaveListener = listener;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SCAN_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                if (!TextUtils.isEmpty(data.getStringExtra("content"))) {
                    String content = data.getStringExtra("content");
                    if(content.contains("~")){
                        String[] strs = content.split("~");
                        nav_config_name.setText(strs[0]);
                        nav_config_url.setText(strs[1]);
                    }else{
                        nav_config_url.setText(content);
                    }
                    getConfig();}
            }
        }
    }

    private void getConfig() {
        String mNavUrl = nav_config_url.getText().toString().trim();
        if(TextUtils.isEmpty(mNavUrl)){
            Toast.makeText(getActivity(), R.string.atom_ui_tip_nav_url_null,Toast.LENGTH_LONG).show();
            return;
        }
        if(!Utils.IsUrl(mNavUrl)){
            Toast.makeText(getActivity(), R.string.atom_ui_tip_nav_url_invalidate,Toast.LENGTH_LONG).show();
            return;
        }
        try {
            URL url = new URL(mNavUrl);
            if(url != null){
                if(TextUtils.isEmpty(url.getHost())
                        || TextUtils.isEmpty(url.getProtocol())){
                    Toast.makeText(getActivity(),R.string.atom_ui_tip_nav_url_invalidate,Toast.LENGTH_LONG).show();
                    return;
                }
            }else{
                Toast.makeText(getActivity(),R.string.atom_ui_tip_nav_url_invalidate,Toast.LENGTH_LONG).show();
                return;
            }
        } catch (MalformedURLException e) {
            Toast.makeText(getActivity(),R.string.atom_ui_tip_nav_url_invalidate,Toast.LENGTH_LONG).show();
            return;
        }
        getServerConfig(mNavUrl);
    }

    @Override
    public void responsePermission(int requestCode, boolean granted) {
        if (!granted){
            Toast.makeText(getActivity(), getString(R.string.atom_ui_tip_request_permission), Toast.LENGTH_SHORT).show();
            return;
        }
        if (requestCode == SCAN_REQUEST) {
            scanQrCode();
        }
    }
}