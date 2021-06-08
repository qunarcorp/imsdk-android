package com.qunar.im.base.jsonbean;


import java.util.List;

/**
 * Created by zhaokai on 15-7-30.
 */
public class DepartmentResult extends BaseResult {
        public String id;
        public String D;//部门
        public List<DepartmentResult> SD;//sub
        public List <PersonResult> UL;//person

        public static class PersonResult {
                public String N;   //full name
                public String U;   //id
                public String S;   //status 登录状态
                public String W;//webname
        }
}