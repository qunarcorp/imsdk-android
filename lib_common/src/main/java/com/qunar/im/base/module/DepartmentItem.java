package com.qunar.im.base.module;


import java.io.Serializable;

/**
 * Created by jiang.cheng on 2015/2/2.
 */
public class DepartmentItem extends BaseModel  implements Serializable {
    public int id;
    public String userId = "";
    public String fullName ="";
    public int parentId;
    public String deptName="";
    public int status;
    public String fuzzyCol="";
}
