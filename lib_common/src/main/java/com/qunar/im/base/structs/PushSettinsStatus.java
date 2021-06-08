package com.qunar.im.base.structs;

/**
 * Created by xinbo.wang on 2015/5/8.
 */
public class PushSettinsStatus {

    public static final int SHOW_CONTENT = 0x01;//是否显示推送详情
    public static final int PUSH_ONLINE = SHOW_CONTENT << 1;//pc端在线时是否收推送
    public static final int SOUND_INAPP = PUSH_ONLINE << 1;//app在前端是否有声音
    public static final int VIBRATE_INAPP = SOUND_INAPP << 1;//app在前端是否有震动
    public static final int PUSH_SWITCH = VIBRATE_INAPP << 1;//push总开关

    /**
     * 获取tag状态是否存在
     *
     * @param status 数据库值
     * @param tag    要获取的状态值
     * @return
     */
    public static boolean isExistStatus(int status, int tag) {
        return (status & tag) != 0;
    }

}
