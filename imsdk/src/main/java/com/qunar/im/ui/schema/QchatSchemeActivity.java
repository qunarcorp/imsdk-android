package com.qunar.im.ui.schema;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.ui.activity.IMBaseActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * 度假业务的scheme总入口
 * 除了签证以外的业务都在这里面
 */
public class QchatSchemeActivity extends IMBaseActivity {
    public static final short SELECT_MULTI_USER = 101;
    public static final int SCAN_REQUEST = 102;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dispatchUri(getIntent());
    }



    public void dispatchUri(Intent intent) {
        if(intent != null){
            Uri data = intent.getData();
            String scheme = data.getScheme();
            String type = data.getPath();
            LogUtil.v("schema:"+ scheme  + " type:"+type);
            if (TextUtils.isEmpty(type)){ //不是走的scheme，不处理
                LogUtil.e("schema error");
                return;
            }
            /**
             * 首页的参数不能放在url后面进行传递，
             * 因为应用包的精简包，度假的APK都没有下载，第一次会访问TOUCH 地址，
             * 公共QCONFIG配置里，如果schema的URL里有参数的话，会匹配不上出错，所以首页的参数都放到bundle里处理
             */

            HashMap<String, String> map = Protocol.splitParams(data);
            //首页路由
            if(map.containsKey("isTransferHome") && "true".equals(map.get("isTransferHome"))) {
                jumpHomeScheme(this);
            }

            deal(type, map, intent);
        }
        else {
            LogUtil.e("schema intent is null");
        }
    }

    private void jumpHomeScheme(Context context) {
        //获取本应用程序信息
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = context.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String schema = "";
        if(applicationInfo != null){
            schema = applicationInfo.metaData.getString("MAIN_SCHEMA");
        }
        Intent i = new Intent("android.intent.action.VIEW",
                Uri.parse(CommonConfig.schema + "://"+ (TextUtils.isEmpty(schema)?"start_main_activity":schema)));
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }


    /**
     * 老版本的scheme解析逻辑（qunaraph....敏感字,你懂的）
     *
     * @param type
     * @param map
     * @param intent
     * @return
     */
    private void deal(String type, Map<String, String> map, Intent intent) {
        if (type != null) {
            try {
                QchatSchemaEnum e =  QchatSchemaEnum.getSchemeEnumByPath(type);
                if(!e.getService().startActivityAndNeedWating(this, map))
                {
                    finish();
                }
            }catch (Exception e) {
                LogUtil.e("schema error:Get VacationSchemaEnum  error. type=" + type + ",redirect to index.");
//                VacationSchemaEnum.index.getService().startActivity(this,map);
                finish();
            }
        } else {
//            VacationSchemaEnum.index.getService().startActivity(this,map);
            LogUtil.e("schema error: type is null , redirect to index.");
            finish();
        }

    }
    /**
     * 新版本的scheme解析逻辑(http前缀)
     *
     * @param type
     * @param map
     * @param intent
     * @return
     */
    private void dealNewScheme(String type, Map<String, String> map, Intent intent) {
        deal(type,map,intent);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_MULTI_USER:
                setResult(resultCode,data);
                break;
            case SCAN_REQUEST:
                setResult(resultCode,data);
            default:
                break;
        }
        this.finish();
    }
}