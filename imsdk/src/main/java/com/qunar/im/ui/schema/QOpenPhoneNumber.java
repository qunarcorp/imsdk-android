package com.qunar.im.ui.schema;

import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.qunar.im.base.jsonbean.GeneralJson;
import com.qunar.im.base.protocol.OpsAPI;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.common.CurrentPreference;
import com.qunar.im.ui.activity.IMBaseActivity;
import com.qunar.im.utils.QtalkStringUtils;

import java.util.Map;

public class QOpenPhoneNumber implements QChatSchemaService {
    public final static QOpenPhoneNumber instance = new QOpenPhoneNumber();


    //这个方法后面都要返回false , 否则会出现白屏
    @Override
    public boolean startActivityAndNeedWating(final IMBaseActivity context, Map<String, String> map) {
        String userId = map.get("userId");
        OpsAPI.getUserMobilePhoneNumber(CurrentPreference.getInstance().getUserid(), QtalkStringUtils.parseLocalpart(userId), new ProtocolCallback.UnitCallback<GeneralJson>() {
            @Override
            public void onCompleted(final GeneralJson generalJson) {
                if(generalJson == null){
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "请求失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                int code;
                try {
                    code = (int) Double.parseDouble(generalJson.errcode.toString());
                } catch (Exception e) {
//                    LogUtil.e("手机号", "ERROR", e);
                    code = -1;
                }
                final int finalCode = code;
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (finalCode != 0) {
                            Toast.makeText(context, generalJson.msg, Toast.LENGTH_SHORT).show();
                        } else if (generalJson.data == null) {
                            Toast.makeText(context, "获取电话次数超限", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent mIntent = new Intent(Intent.ACTION_DIAL);
                            mIntent.setData(Uri.parse("tel:" + generalJson.data.get("phone")));
                            context.startActivity(mIntent);
                        }
                    }
                });

            }

            @Override
            public void onFailure(String errMsg) {
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "请求失败", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

        return false;
    }
}