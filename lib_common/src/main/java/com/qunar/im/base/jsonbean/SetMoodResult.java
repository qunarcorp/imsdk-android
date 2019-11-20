package com.qunar.im.base.jsonbean;

/**
 * Created by zhaokai on 16-2-2.
 */
public class SetMoodResult extends BaseJsonResult {

    public Data data;

    public static final class Data{
        String user;
        String version;
    }
}