package com.qunar.im.base.view;


/**
 * Created by saber on 16-3-23.
 */
public class BaseInfoBinderable {
    public static final int CONTACT_TYPE = 1;
    public static final int GROUP_TYPE = 2;
    public static final int PUBLISH_TYPE = 3;

    public static final int SEARCH_MORE_GROUP = 4;
    public static final int SEARCH_MORE_CONTACT = 5;
    public static final int SEARCH_MORE_PUB = 6;

    public String name;
    public String desc;
    public String imageUrl;
    public String hint;
    public String id;
    public int    type;
    public boolean connection; //是否加入群组，是否成为好友
}
