package com.qunar.im.base.jsonbean;

import java.util.List;

/**
 * Created by zhaokai on 15-11-11.
 */
public class SystemResult extends BaseResult{
    public String title;
    public List<Content> content;
    public String operation_url;
    public String prompt;
    public String order_id;
    public static class Content{
        public String sub_title;
        public String sub_content;
    }
}
