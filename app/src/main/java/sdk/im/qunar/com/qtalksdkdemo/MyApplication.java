package sdk.im.qunar.com.qtalksdkdemo;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.qunar.im.common.CommonConfig;
import com.qunar.im.ui.sdk.QIMSdk;

/**
 * Created by lihaibin.li on 2018/2/22.
 */

public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        QIMSdk.getInstance().init(this);
        QIMSdk.getInstance().openDebug();
    }
}
