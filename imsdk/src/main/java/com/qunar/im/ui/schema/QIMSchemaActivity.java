package com.qunar.im.ui.schema;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.ui.activity.IMBaseActivity;

import java.util.HashMap;
import java.util.Map;

public class QIMSchemaActivity extends IMBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dispatchUri(getIntent());
    }

    public void dispatchUri(Intent intent) {
        if (intent != null) {
            Uri data = intent.getData();
            String scheme = data.getScheme();
            String path = data.getPath();
            Logger.i("schema:" + scheme + " path:" + path);
            if (TextUtils.isEmpty(path)) { //不是走的scheme，不处理
                Logger.e("schema error");
                return;
            }

            HashMap<String, String> map = Protocol.splitParams(data);
            deal(path, map);
        } else {
            Logger.e("schema intent is null");
        }
    }

    private void deal(String path, Map<String, String> map) {
        if (path != null) {
            try {
                QIMSchemaEnum e = QIMSchemaEnum.getSchemeEnumByPath(path);
                if (!e.getqChatSchemaService().startActivityAndNeedWating(this, map)) {
                    finish();
                }
            } catch (Exception e) {
                Logger.e("schema error:Get VacationSchemaEnum  error. path=" + path + ",redirect to index.");
                finish();
            }
        } else {
            Logger.e("schema error: path is null , redirect to index.");
            finish();
        }


    }

}
