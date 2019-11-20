package com.qunar.im.base.structs;

/**
 * Created by hubin on 2018/3/7.
 */

public class NewMessageStatus {
    public final static int STATUS_FAILED = 0;
    public final static int STATUS_SUCCESS =1;
    public final static int STATUS_PROCESSION = 2;
    public final static int STATUS_DELIVERY = 8;

    public final static int STATUS_SINGLE_DELIVERED = 3;
    public final static int STATUS_SINGLE_DELIVERED_CONSULT = 4;

    public final static int STATUS_SINGLE_READED = 6;
    public final static int STATUS_SINGLE_READED_CONSULT = 7;
}
