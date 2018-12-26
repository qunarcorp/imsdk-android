package com.qunar.im.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
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
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.permission.PermissionDispatcher;
import com.qunar.im.ui.R;
import com.qunar.im.ui.entity.NavConfigInfo;

/**
 * 普通导航配置（域名 配置）
 * Created by hubo.hu on 2017/8/31.
 */

public class NavConfigNormalFragment extends DialogFragment implements NavConfigFragment.OnSaveListener {
    private static final int SCAN_REQUEST = PermissionDispatcher.getRequestCode();
    private static final String DEFALT_PUBLIC_NAV_URL = "https://qt.qunar.com/package/static/qtalk/publicnav?c=";

    private EditText nav_config_domian;
    private TextView nav_config_dialog_confir, nav_config_dialog_cancel, nav_config_dialog_advanced;

    private String domain;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.atom_ui_activity_nav_config_normal_dialog, null);
        nav_config_dialog_confir = (TextView) view.findViewById(R.id.nav_config_dialog_confir);
        nav_config_dialog_cancel = (TextView) view.findViewById(R.id.nav_config_dialog_cancel);
        nav_config_dialog_advanced = (TextView) view.findViewById(R.id.nav_config_dialog_advanced);
        nav_config_domian = (EditText) view.findViewById(R.id.nav_config_domian);

//        nav_config_url.setText("https://qt.qunar.com/package/static/qtalk/publicnav?c=qunar.com");
        nav_config_dialog_advanced.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavConfigFragment dialog = new NavConfigFragment();
                dialog.show(getFragmentManager(), "NAVCONFIG");
                dialog.setOnSaveListener(NavConfigNormalFragment.this);
//                dismiss();
            }
        });
        nav_config_dialog_confir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                domain = nav_config_domian.getText().toString();
                if(TextUtils.isEmpty(domain)){
                    Toast.makeText(getActivity(), R.string.atom_ui_nav_hint_input_domain, Toast.LENGTH_SHORT).show();
                    return;
                }
                String url = QtalkNavicationService.NAV_CONFIG_PUBLIC_DEFAULT + "?c=" + domain;
                getServerConfig(url);
            }
        });
        nav_config_dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        Dialog dialog = builder.setView(view).create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.atom_ui_DialogAnimation;
        return dialog;
    }

    private void getServerConfig(final String url) {
        HttpUtil.getServerConfigAsync(url, new ProtocolCallback.UnitCallback<NavConfigResult>() {
            @Override
            public void onCompleted(final NavConfigResult navConfigResult) {
                if(getActivity() != null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), getString(R.string.atom_ui_tip_nav_load_success), Toast.LENGTH_SHORT).show();
//                        updateUI(navConfigResult);
                            NavConfigFragment dialog = new NavConfigFragment();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("navconfig_info", navConfigResult);
                            bundle.putString("navconfig_domain", domain);
                            bundle.putString("navconfig_url", url);
                            dialog.setArguments(bundle);
                            dialog.show(getFragmentManager(), "NAVCONFIG");
                            dialog.setOnSaveListener(NavConfigNormalFragment.this);
//                        dismiss();
                        }
                    });
                }
            }

            @Override
            public void onFailure(String errMsg) {
                if(getActivity() != null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), getString(R.string.atom_ui_tip_nav_load_failed), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void saveConfigInfo(NavConfigInfo info) {
        dismiss();
    }
}