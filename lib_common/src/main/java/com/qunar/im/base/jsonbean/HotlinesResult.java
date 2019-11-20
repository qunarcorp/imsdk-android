package com.qunar.im.base.jsonbean;

import java.util.List;

/**
 * Created by Lex lex on 2018/7/30.
 */
public class HotlinesResult extends BaseJsonResult{


    public DataBean data;

    public static class DataBean {
        /**
         * allhotlines : {"qtalktesthotline@ejabhost1":"shop_5495@ejabhost2","shop_5489@ejabhost2":"shop_5489@ejabhost2"}
         * myhotlines : ["test0","test1"]
         */

        public List<String> allhotlines;
        public List<String> myhotlines;

    }
}
