package com.qunar.im.base.jsonbean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by saber on 16-3-1.
 */
public class RobotInfoResult2 extends BaseJsonResult  {
    public List<RobotItemResult2> data;

    public static class RobotItemResult2 implements Serializable {

        public String rbt_name;
        public Object rbt_body;
        public int rbt_ver;
    }
}
