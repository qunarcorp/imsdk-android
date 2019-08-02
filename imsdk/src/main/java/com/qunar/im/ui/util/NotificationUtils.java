package com.qunar.im.ui.util;

import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by Lex lex on 2018/9/12.
 */
public class NotificationUtils {

    public static boolean areNotificationsEnabled(Context mContext) {
        try {
            if (Build.VERSION.SDK_INT >= 24) {
                NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(
                        Context.NOTIFICATION_SERVICE);
                return mNotificationManager.areNotificationsEnabled();
            } else if (Build.VERSION.SDK_INT >= 19) {
                AppOpsManager appOps =
                        (AppOpsManager) mContext.getSystemService(Context.APP_OPS_SERVICE);
                ApplicationInfo appInfo = mContext.getApplicationInfo();
                String pkg = mContext.getApplicationContext().getPackageName();
                int uid = appInfo.uid;
                try {
                    Class<?> appOpsClass = Class.forName(AppOpsManager.class.getName());
                    Method checkOpNoThrowMethod = appOpsClass.getMethod("checkOpNoThrow", Integer.TYPE,
                            Integer.TYPE, String.class);
                    Field opPostNotificationValue = appOpsClass.getDeclaredField("OP_POST_NOTIFICATION");
                    int value = (Integer) opPostNotificationValue.get(Integer.class);
                    return ((Integer) checkOpNoThrowMethod.invoke(appOps, value, uid, pkg)
                            == AppOpsManager.MODE_ALLOWED);
                } catch (Exception e) {
                    return true;
                }
            } else {
                return true;
            }
        } catch (Exception e) {
            return true;
        }
    }

    public static void startNotificationSettings (Context context) {
        Intent intent = new Intent();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            //"android.settings.APP_NOTIFICATION_SETTINGS"
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            //"android.provider.extra.APP_PACKAGE"
        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
        } else if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
        }
        context.startActivity(intent);
    }
}
