package com.qunar.im.base.jsonbean;

import java.util.List;

/**
 * Created by saber on 15-12-24.
 */
public class IncrementUsersResult extends BaseJsonResult {
    public Data data;
    public static class Data{
        public List<IncrementUser> update;
        public List<IncrementUser> delete;
        public int version;
    }
    public static class IncrementUser
    {
        public String U;//userid
        public String N;//name
        public String D;//部门
        public String pinyin;//快速查询
        public String sex;
        public String uType;//
        public String email;//
        public String hire_type;
        public boolean visibleFlag = true;
    }
}
