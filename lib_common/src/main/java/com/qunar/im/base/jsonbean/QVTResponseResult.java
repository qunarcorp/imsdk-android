package com.qunar.im.base.jsonbean;

/**
 * Created by saber on 15-9-2.
 */
public class QVTResponseResult extends BaseJsonResult {
    public QVT data;

    static public class QVT{
        public String qcookie;
        public String tcookie;
        public String vcookie;
    }
    public QVTResponseResult(){
        data = new QVT();
    }
}
