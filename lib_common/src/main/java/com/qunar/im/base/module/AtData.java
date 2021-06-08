package com.qunar.im.base.module;

import java.util.List;

/**
 * Created by hubin on 2017/10/23.
 */

public class AtData {

    /**
     * type : 10001
     * data : [{"jid":"hubo.hu@ejabhost1","text":"胡泊hu"}]
     */

    private int type;
    private List<DataBean> data;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * jid : hubo.hu@ejabhost1
         * text : 胡泊hu
         */

        private String jid;
        private String text;

        public String getJid() {
            return jid;
        }

        public void setJid(String jid) {
            this.jid = jid;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
