package com.qunar.im.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
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
import com.qunar.im.base.jsonbean.NavConfigResult;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.util.AccountSwitchUtils;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.core.utils.GlobalConfigManager;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.ui.R;
import com.qunar.im.ui.entity.NavConfigInfo;
import com.qunar.im.ui.util.NavConfigUtils;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;
import com.qunar.im.utils.HttpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.qunar.im.base.util.JsonUtils.getGson;

public class NavConfigActivity extends SwipeBackActivity implements View.OnClickListener {

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
        dispatchUri(getIntent());
    }

    public void dispatchUri(Intent intent) {
        if(intent != null){
            Uri data = intent.getData();
            if(data == null){
                return;
            }
            String scheme = data.getScheme();
            String host = data.getHost();
            LogUtil.v("schema:"+ scheme  + " type:"+host);
            if (TextUtils.isEmpty(host)){ //不是走的scheme，不处理
                LogUtil.e("schema error");
                return;
            }

            HashMap<String, String> map = Protocol.splitParams(data);
            String configurl = map.get("configurl");
            String configname = map.get("configname");
            if(TextUtils.isEmpty(configurl)){
                return;
            } else {
                String url = new String(Base64.decode(configurl,Base64.URL_SAFE | Base64.NO_WRAP));
                getServerConfig(configname,url);
            }
        }
        else {
            LogUtil.e("schema intent is null");
        }
    }

    public void findViewById() {
        btnAddConfig = (TextView) findViewById(R.id.nav_config_add);
        nav_config_list = (ListView) findViewById(R.id.nav_config_list);
        nav_config_layout = (RelativeLayout) findViewById(R.id.nav_config_layout);
    }

    public void setOnClickListener() {
        btnAddConfig.setOnClickListener(this);
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
//        if(GlobalConfigManager.isQtalkPlat() && TextUtils.isEmpty(DataUtils.getInstance(this).getPreferences(DEFAULT_NAV_CONFIG_NAME, ""))){
//            NavConfigInfo config = new NavConfigInfo();
//            config.setName(DEFAULT_NAV_CONFIG_NAME);
//            config.setUrl(QtalkNavicationService.getInstance().getNavicationUrl());
//            mConfigInfoList.add(config);
//
//            String configjson = getGson().toJson(mConfigInfoList);
//            NavConfigUtils.saveAllNavJSONInfo(configjson);
//            DataUtils.getInstance(NavConfigActivity.this).putPreferences(DEFAULT_NAV_CONFIG_NAME, QtalkNavicationService.getInstance().getNavicationUrl());
//
//        }
        mConfigdapter = new ConfigAdapter();
        nav_config_list.setAdapter(mConfigdapter);

        nav_config_list.setOnItemClickListener((adapterView, view, i, l) -> {
            //更新配置列表
            NavConfigUtils.saveNavInfo(mConfigInfoList.get(i));
            Logger.i("切换导航:" + mConfigInfoList.get(i).getName() + "::" + mConfigInfoList.get(i).getUrl());
            //更新导航
            getServerConfig(mConfigInfoList.get(i),false);

        });
        nav_config_list.setOnItemLongClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(NavConfigActivity.this,QunarWebActvity.class);
            intent.putExtra(QunarWebActvity.IS_HIDE_BAR, false);
            intent.setData(Uri.parse(mConfigInfoList.get(position).getUrl()));
            startActivity(intent);
            return true;
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
            startActivityForResult(new Intent(this,NavConfigAddActivity.class),NavConfigAddActivity.NAV_ADD_REQUEST_CODE);
        }
    }
    private void getServerConfig(final NavConfigInfo navConfigInfo,final boolean isFreshUI) {
        final String name  = navConfigInfo.getName();
        final String url = navConfigInfo.getUrl();
        HttpUtil.getServerConfigAsync(url, new ProtocolCallback.UnitCallback<NavConfigResult>() {
            @Override
            public void onCompleted(final NavConfigResult navConfigResult) {
                String configStr = JsonUtils.getGson().toJson(navConfigResult);
                Logger.i("切换导航成功:" + configStr);
                final String navName = TextUtils.isEmpty(name) ? navConfigResult.baseaddess.domain : name;
                //保存导航信息
                NavConfigUtils.saveNavInfo(navName,url);
                //保存当前配置
                NavConfigUtils.saveCurrentNavJSONInfo(navName,configStr);
                //配置导航
                QtalkNavicationService.getInstance().configNav(navConfigResult);
                runOnUiThread(() -> {
                    navConfigInfo.setName(navName);
                    if(isFreshUI)
                        refreshNavList(navConfigInfo);
                    toast(getString(R.string.atom_ui_tip_switch_navigation_success));
                    NavConfigUtils.saveCurrentNavDomain(navName);
                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.CLEAR_BRIDGE_OPS);
                    Intent intent = new Intent();
                    intent.putExtra(Constants.BundleKey.NAV_ADD_NAME,navName);
                    setResult(RESULT_OK,intent);

                    if(!navConfigInfo.isSelected()){//切换后 跳转登录页面
                        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.START_LOGIN_VIEW);
                    }

                    finish();
                });
            }

            @Override
            public void onFailure(String errMsg) {
                toast(getString(R.string.atom_ui_tip_switch_navigation_failed));
            }
        });
    }

    private void getServerConfig(String navName,String navUrl){
        NavConfigInfo navConfigInfo = new NavConfigInfo();
        navConfigInfo.setName(navName);
        navConfigInfo.setUrl(navUrl);
        getServerConfig(navConfigInfo,true);
    }

    private void refreshNavList(NavConfigInfo config) {
        if(mConfigInfoList == null){
            mConfigInfoList = new ArrayList<>();
        }
        mConfigInfoList.add(config);
        mConfigdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == NavConfigAddActivity.NAV_ADD_REQUEST_CODE
                && resultCode == NavConfigAddActivity.NAV_ADD_RESPONSE_CODE
                && data != null){
            NavConfigInfo config = new NavConfigInfo();
            config.setName(data.getStringExtra(Constants.BundleKey.NAV_ADD_NAME));
            config.setUrl(data.getStringExtra(Constants.BundleKey.NAV_ADD_URL));
            getServerConfig(config,true);
        }
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
                holder.name = (TextView) view.findViewById(R.id.nav_config_item_name);
                holder.url = (TextView) view.findViewById(R.id.nav_config_item_url);
                holder.detail = (TextView) view.findViewById(R.id.nav_config_item_detail);
                holder.del = (TextView) view.findViewById(R.id.nav_config_item_del);
                holder.nav_layout = (RelativeLayout) view.findViewById(R.id.nav_layout);
                holder.nav_config_item_check = (TextView) view.findViewById(R.id.nav_config_item_check);
                view.setTag(holder);
            }else{
                holder = (Holder) view.getTag();
            }

            final NavConfigInfo info = mConfigInfoList.get(i);
            String name = DataUtils.getInstance(NavConfigActivity.this).getPreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_NAME, "");
            if((TextUtils.isEmpty(name) && getCount() == 1) || name.equals(info.getName())) {
                holder.nav_layout.setBackgroundColor(Color.parseColor("#DFF9F4"));
                holder.nav_config_item_check.setTextColor(ContextCompat.getColor(NavConfigActivity.this,R.color.atom_ui_button_primary_color));
                holder.nav_config_item_check.setText(R.string.atom_ui_hook);
                holder.name.setTextColor(Color.parseColor("#333333"));
                holder.url.setTextColor(Color.parseColor("#333333"));
                info.setSelected(true);
            }else{
                holder.nav_layout.setBackgroundColor(Color.parseColor("#F3F3F3"));
                holder.nav_config_item_check.setTextColor(Color.parseColor("#939393"));
                holder.nav_config_item_check.setText(R.string.atom_ui_new_uncheck_circle);
                holder.name.setTextColor(Color.parseColor("#9B9B9B"));
                holder.url.setTextColor(Color.parseColor("#9B9B9B"));
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
                            mConfigInfoList.remove(i);
                            notifyDataSetChanged();

                            dialog.dismiss();

                            //更新配置列表

                            NavConfigUtils.removeNavJSONInfoByName(info.getName());

                            String configjson = getGson().toJson(mConfigInfoList);
                            NavConfigUtils.saveAllNavJSONInfo(configjson);
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
            public RelativeLayout nav_layout;
            public TextView nav_config_item_check;
        }
    }

}
