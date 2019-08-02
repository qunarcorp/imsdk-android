package com.qunar.im.ui.broadcastreceivers;

/**
 * Created by xinbo.wang on 2016/6/30.
 */
public class ShareReceiver /*extends BroadcastReceiver*/ {
    public static final String SHARE_EXTRA_KEY ="ShareData";
    public static final String SHARE_TAG = "share_data";
    public static final String SHARE_TEXT ="share_txt";
    public static final String SHARE_IMG = "share_img";
    public static final String SHARE_FILE = "share_file";
    public static final String SHARE_VIDEO = "share_video";
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        Bundle extra = intent.getExtras();
//        if(extra!=null&&extra.containsKey(SHARE_EXTRA_KEY))
//        {
//            Intent intentActivty = new Intent();
//            intentActivty.setClass(context, TabMainActivity.class);
//            intentActivty.putExtra(SHARE_EXTRA_KEY,extra.getString(SHARE_EXTRA_KEY));
//            intentActivty.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intentActivty);
//        }
//    }
}
