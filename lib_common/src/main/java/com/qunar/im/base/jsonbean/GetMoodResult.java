package com.qunar.im.base.jsonbean;

import java.util.List;

/**
 * Created by zhaokai on 16-2-2.
 */
public class GetMoodResult extends BaseJsonResult {
    public List<Data> data;

    public static final class Data{
        public String U;
        public String V;
        public String M;
    }
}