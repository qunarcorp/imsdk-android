package com.qunar.im.rtc.scheme;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.qunar.im.base.util.LogUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 度假业务的scheme总入口
 * 除了签证以外的业务都在这里面
 */
public class QchatSchemeActivity extends AppCompatActivity {
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

            HashMap<String, String> map = splitParams(data);
            deal(type, map, intent);
        }
        else {
            LogUtil.e("schema intent is null");
        }
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

    public static HashMap<String, String> splitParams(Uri uri) {
        if (uri == null) {
            return new HashMap<String, String>();
        }
        Set<String> keys = getQueryParameterNames(uri);
        HashMap<String, String> map = new HashMap<String, String>(keys.size());
        for (String key : keys) {
            map.put(key, uri.getQueryParameter(key));
        }
        return map;
    }

    public static Set<String> getQueryParameterNames(Uri uri) {
        if (uri.isOpaque()) {
            throw new UnsupportedOperationException("This isn't a hierarchical URI.");
        }

        String query = uri.getEncodedQuery();
        if (query == null) {
            return Collections.emptySet();
        }

        Set<String> names = new LinkedHashSet<String>();
        int start = 0;
        do {
            int next = query.indexOf('&', start);
            int end = next == -1 ? query.length() : next;

            int separator = query.indexOf('=', start);
            if (separator > end || separator == -1) {
                separator = end;
            }

            String name = query.substring(start, separator);
            names.add(Uri.decode(name));

            // Move start to end of name.
            start = end + 1;
        } while (start < query.length());

        return Collections.unmodifiableSet(names);
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