package com.qunar.im.base.jsonbean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lihaibin.li on 2017/12/12.
 */

public class SeatStatusResult extends BaseJsonResult{
    public List<SeatStatus> data = new ArrayList<>();

    public static class SeatStatus{
        public String st;
        public String sname;
        public String sid;
    }
}
