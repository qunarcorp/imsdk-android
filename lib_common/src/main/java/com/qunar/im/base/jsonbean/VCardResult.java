package com.qunar.im.base.jsonbean;

import java.util.List;

/**
 * Created by xinbo.wang on 2015/5/15.
 */
public class VCardResult extends BaseResult {

    public List<VCardItem> data;

    public class VCardItem implements BaseData
    {
        public String U;
        public int V;
    }
}
