package com.qunar.im.base.jsonbean;

import java.util.List;

/**
 * Created by froyomu on 2019/2/1
 * <p>
 * Describe:
 */
public class DomainResult extends BaseJsonResult{
    public List<Result> data;

    public class Result{
        public String nav;
        public String domain;
        public int domainId;
        public String name;
    }
}
