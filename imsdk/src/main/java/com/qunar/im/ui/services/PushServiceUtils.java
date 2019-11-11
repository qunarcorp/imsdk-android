package com.qunar.im.ui.services;

import android.content.Context;
import android.content.Intent;

import com.qunar.im.base.util.Constants;
import com.qunar.im.thirdpush.QTPushConfiguration;


/**
 * Created by xinbo.wang on 2015/4/20.
 */
public class PushServiceUtils {



    public static void startAMDService(final Context context) {
        //        startAMDServiceOld(context);
        QTPushConfiguration.registPush(context);

    }

    private static void startAMDServiceOld(Context context) {
        Intent intent = new Intent();
        intent.setAction(Constants.BroadcastFlag.START_AMD_ACTION);
        intent.setClassName(context,"com.qunar.im.qtpush.AMDService");
        intent.setPackage(context.getPackageName());
        context.startService(intent);
    }


    private static void stopAMDServiceOld(Context context)
    {
        Intent intent = new Intent();
        intent.setAction(Constants.BroadcastFlag.STOP_AMD_ACTION);
        intent.setClassName(context,"com.qunar.im.qtpush.AMDService");
        intent.setPackage(context.getPackageName());
        context.startService(intent);
    }

    public static void stopAMDService(Context context){
        QTPushConfiguration.unRegistPush(context);
//        stopAMDServiceOld(context);
    }
}
