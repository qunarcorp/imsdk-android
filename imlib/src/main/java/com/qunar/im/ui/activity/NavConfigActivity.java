package com.qunar.im.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;
import com.qunar.im.utils.HttpUtil;
import com.qunar.im.base.jsonbean.NavConfigResult;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.util.AccountSwitchUtils;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.ui.R;
import com.qunar.im.ui.entity.NavConfigInfo;
import com.qunar.im.ui.fragment.NavConfigFragment;
import com.qunar.im.ui.fragment.NavConfigNormalFragment;
import com.qunar.im.ui.view.QtNewActionBar;

import java.util.ArrayList;
import java.util.List;

import static com.qunar.im.base.util.JsonUtils.getGson;

public class NavConfigActivity extends IMBaseActivity implements View.OnClickListener, NavConfigFragment.OnSaveListener {

    private TextView btnAddConfig;
    private ListView nav_config_list;
    private ConfigAdapter mConfigdapter;
    private QtNewActionBar actionBar;
    private RelativeLayout nav_config_layout;

    private List<NavConfigInfo> mConfigInfoList;

    private static final String DEFAULT_NAV_CONFIG_NAME = AccountSwitchUtils.defalt_nav_name;//默认导航配置，普通qtalk

    @Override
    protected void onCreate(Bundle savedBundle) {
        super.onCreate(savedBundle);
        setContentView(R.layout.atom_ui_activity_nav_config);
        findViewById();
        initActionbar();
        setOnClickListener();
        loadNavConfig();
    }

    public void findViewById() {
        btnAddConfig = (TextView) findViewById(R.id.nav_config_add);
        nav_config_list = (ListView) findViewById(R.id.nav_config_list);
        nav_config_layout = (RelativeLayout) findViewById(R.id.nav_config_layout);
    }

    public void setOnClickListener() {
        btnAddConfig.setOnClickListener(this);
        nav_config_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyboard();
                return false;
            }
        });
    }
    public void initActionbar(){
        actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setActionBarRightIcon(R.string.atom_ui_new_feedback);
        setActionBarRightIconClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NavConfigActivity.this, BugreportActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 读取配置文件
     */
    private void loadNavConfig() {
        //preference取json
        String configs = DataUtils.getInstance(this).getPreferences(QtalkNavicationService.NAV_CONFIG_JSON, "");
        Logger.i("当前配置的所有导航:" + configs);

        mConfigInfoList = JsonUtils.getGson().fromJson(configs, new TypeToken<List<NavConfigInfo>>() {}.getType());
        if(mConfigInfoList == null){
            mConfigInfoList = new ArrayList<>();
        }
        if(TextUtils.isEmpty(DataUtils.getInstance(this).getPreferences(DEFAULT_NAV_CONFIG_NAME, ""))){
            NavConfigInfo config = new NavConfigInfo();
            config.setName(DEFAULT_NAV_CONFIG_NAME);
            config.setUrl(QtalkNavicationService.getInstance().getNavicationUrl());
            mConfigInfoList.add(config);

            String configjson = getGson().toJson(mConfigInfoList);
            DataUtils.getInstance(NavConfigActivity.this).putPreferences(QtalkNavicationService.NAV_CONFIG_JSON, configjson);
            DataUtils.getInstance(NavConfigActivity.this).putPreferences(DEFAULT_NAV_CONFIG_NAME, QtalkNavicationService.getInstance().getNavicationUrl());

        }
        mConfigdapter = new ConfigAdapter();
        nav_config_list.setAdapter(mConfigdapter);

        nav_config_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //更新配置列表
                String configjson = JsonUtils.getGson().toJson(mConfigInfoList);
                DataUtils.getInstance(NavConfigActivity.this).putPreferences(QtalkNavicationService.NAV_CONFIG_JSON, configjson);
                //保存当前使用的配置名
                DataUtils.getInstance(NavConfigActivity.this).putPreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_NAME, mConfigInfoList.get(i).getName());
                //保存当前使用的URL
                DataUtils.getInstance(NavConfigActivity.this).putPreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_URL, mConfigInfoList.get(i).getUrl());
                Logger.i("切换导航:" + mConfigInfoList.get(i).getName() + "::" + mConfigInfoList.get(i).getUrl());
                //更新导航
                getServerConfig(mConfigInfoList.get(i).getName(), mConfigInfoList.get(i).getUrl());

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        setActionBarTitle(R.string.atom_ui_title_configuration);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.nav_config_add){
            NavConfigNormalFragment dialog = new NavConfigNormalFragment();
            dialog.show(getFragmentManager(), "NAVCONFIG");
        }
    }

    private void restartAPP(){
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
    private void getServerConfig(final String name, String url) {
        HttpUtil.getServerConfigAsync(url, new ProtocolCallback.UnitCallback<NavConfigResult>() {
            @Override
            public void onCompleted(final NavConfigResult navConfigResult) {
                String configStr = JsonUtils.getGson().toJson(navConfigResult);
                Logger.i("切换导航成功:" + configStr);
                //保存当前配置
                DataUtils.getInstance(NavConfigActivity.this).putPreferences(name, configStr);
                QtalkNavicationService.getInstance().configNav(navConfigResult);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(NavConfigActivity.this, R.string.atom_ui_tip_switch_navigation_success, Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                });
            }

            @Override
            public void onFailure(String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(NavConfigActivity.this, R.string.atom_ui_tip_switch_navigation_failed, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    /**
     * 查看导航详情
     * @param info
     */
    private void showDetailDialog(final NavConfigInfo info){
        String navString = DataUtils.getInstance(this).getPreferences(info.getName(), "");
        if (TextUtils.isEmpty(navString)) {
            Toast.makeText(this, R.string.atom_ui_tip_get_navinfo_failed, Toast.LENGTH_SHORT).show();
            return;
        }
        NavConfigResult result = JsonUtils.getGson().fromJson(navString, NavConfigResult.class);
        View view = LayoutInflater.from(this).inflate(R.layout.atom_ui_activity_nav_config_detail_dialog, null);
        TextView name = (TextView) view.findViewById(R.id.config_detail_name);
        TextView url = (TextView) view.findViewById(R.id.config_detail_url);
        TextView host = (TextView) view.findViewById(R.id.config_detail_host);
        TextView xmppport = (TextView) view.findViewById(R.id.config_detail_xmpp_port);
        TextView probufport = (TextView) view.findViewById(R.id.config_detail_pb_port);
        TextView domain = (TextView) view.findViewById(R.id.config_detail_domain);
        TextView logintype = (TextView) view.findViewById(R.id.config_detail_logintype);

        name.setText(info.getName());
        url.setText(info.getUrl());
        host.setText(result.baseaddess.xmpp);
        xmppport.setText(result.baseaddess.xmppmport+"");
        probufport.setText(result.baseaddess.protobufPort+"");
        domain.setText(result.baseaddess.domain);
        logintype.setText(result.Login.loginType);

        new AlertDialog.Builder(this)
                .setTitle(R.string.atom_ui_btn_configuration_details)
                .setView(view)
                .setCancelable(false)
                .setPositiveButton(R.string.atom_ui_btn_saveqcode, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(NavConfigActivity.this, NavConfigQRActivity.class);
                        intent.putExtra("name", info.getName());
                        intent.putExtra("url", info.getUrl());
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.atom_ui_common_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
    @Override
    public void saveConfigInfo(NavConfigInfo config) {
        if(mConfigInfoList == null){
            mConfigInfoList = new ArrayList<NavConfigInfo>();
        }
        mConfigInfoList.add(config);
        mConfigdapter.notifyDataSetChanged();

        String configjson = getGson().toJson(mConfigInfoList);
        DataUtils.getInstance(NavConfigActivity.this).putPreferences(QtalkNavicationService.NAV_CONFIG_JSON, configjson);
    }

    private class ConfigAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mConfigInfoList.size();
        }

        @Override
        public Object getItem(int i) {
            return mConfigInfoList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            Holder holder = null;
            if(view == null){
                holder = new Holder();
                view = LayoutInflater.from(NavConfigActivity.this).inflate(R.layout.atom_ui_activity_nav_config_item, null);
                view.setTag(holder);
            }else{
                holder = (Holder) view.getTag();
            }

            holder.name = (TextView) view.findViewById(R.id.nav_config_item_name);
            holder.url = (TextView) view.findViewById(R.id.nav_config_item_url);
            holder.detail = (TextView) view.findViewById(R.id.nav_config_item_detail);
            holder.del = (TextView) view.findViewById(R.id.nav_config_item_del);

            final NavConfigInfo info = mConfigInfoList.get(i);
            if(info.getName().equals(DEFAULT_NAV_CONFIG_NAME)){
                holder.detail.setVisibility(View.GONE);
                holder.del.setVisibility(View.GONE);
            }else{
                holder.detail.setVisibility(View.VISIBLE);
                holder.del.setVisibility(View.VISIBLE);
            }
            String name = DataUtils.getInstance(NavConfigActivity.this).getPreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_NAME, "");
            if(TextUtils.isEmpty(name) && info.getName().equals(DEFAULT_NAV_CONFIG_NAME)){
                holder.name.setTextColor(ContextCompat.getColor(NavConfigActivity.this, R.color.atom_ui_file_sys_cost));
                holder.url.setTextColor(ContextCompat.getColor(NavConfigActivity.this, R.color.atom_ui_file_sys_cost));
            }else if(info.getName().equals(name)) {
                holder.name.setTextColor(ContextCompat.getColor(NavConfigActivity.this, R.color.atom_ui_file_sys_cost));
                holder.url.setTextColor(ContextCompat.getColor(NavConfigActivity.this, R.color.atom_ui_file_sys_cost));
            }else{
                holder.name.setTextColor(ContextCompat.getColor(NavConfigActivity.this, R.color.atom_ui_black_212121));
                holder.url.setTextColor(ContextCompat.getColor(NavConfigActivity.this, R.color.atom_ui_black_212121));
            }
            holder.name.setText(info.getName());
            holder.url.setText(info.getUrl());
            holder.detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    showDetailDialog(info);
                    Intent intent = new Intent(NavConfigActivity.this, NavConfigQRActivity.class);
                    intent.putExtra("name", info.getName());
                    intent.putExtra("url", info.getUrl());
                    intent.putExtra("title", "导航二维码");
                    startActivity(intent);
                }
            });
            holder.del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    commonDialog.setTitle(R.string.atom_ui_tip_dialog_prompt);
                    commonDialog.setMessage(getString(R.string.atom_ui_tip_nav_del));
                    commonDialog.setPositiveButton(R.string.atom_ui_common_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                setLoginResult(false, 0);
                            DataUtils.getInstance(NavConfigActivity.this).removePreferences(info.getName());
//                    File file = new File(NavConfigActivity.this.getFilesDir(),mConfigInfoList.get(i).getName() + ConfigFileUtils.CONFIG_FILE_NAME);
//                    file.delete();
                            mConfigInfoList.remove(i);
                            notifyDataSetChanged();
                            //更新配置列表
                            String configjson = getGson().toJson(mConfigInfoList);
                            DataUtils.getInstance(NavConfigActivity.this).putPreferences(QtalkNavicationService.NAV_CONFIG_JSON, configjson);
                            dialog.dismiss();
                        }
                    });
                    commonDialog.setNegativeButton(R.string.atom_ui_common_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                        }
                    });
                    commonDialog.setCancelable(false);
                    commonDialog.show();

                }
            });
            return view;
        }

        class Holder{
            public TextView name;
            public TextView url;
            public TextView detail;
            public TextView del;
        }
    }

    private void hideKeyboard(){
//        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//        if (imm != null) {
//            //imm.toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);
//            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
//        }
    }

}
