package com.qunar.im.base.jsonbean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Lex lex on 2018/3/21.
 */

public class SendMailJson implements Serializable {
    public String from;
    public String from_name;
    public List<ARR> tos;
    public List<CC> ccs;
    public String body;
    public String subject;
    public String alt_body;
    public String is_html;
    public String plat;

    public static class ARR {
        public String to;
        public String name;
    }


    public static class CC {
        public String cc;
        public String name;
    }
}
