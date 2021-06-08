package com.qunar.im.base.structs;

/**
 * Created by xinbo.wang on 2015/4/23.
 */
public enum  UserStatus {
    offline(0),away(1),online(6);

    private final String STR_OFFLINE = " (离线) ";
    private final String STR_ONLINE  = " (在线) ";
    private final String STR_LEAVING = " (离开) ";

    private int value = 0;

    UserStatus(int value)
    {
        this.value = value;
    }

    public static UserStatus valueOf(int value)
    {
        switch (value)
        {
            case 0:
                return offline;
            case 6:
                return online;
            case 1:
                return away;
            default:
                return offline;
        }
    }

    public int value()
    {
        return value;
    }

    public String strByVal()
    {
        switch (value)
        {
            case 0:
                return STR_OFFLINE;
            case 6:
                return STR_ONLINE;
            case 1:
                return STR_LEAVING;
            default:
                return STR_OFFLINE;
        }
    }
}
