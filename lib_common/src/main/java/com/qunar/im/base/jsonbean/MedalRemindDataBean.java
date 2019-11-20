package com.qunar.im.base.jsonbean;

public class MedalRemindDataBean {

    /**
     * userId : hubin.hu@ejabhost1
     * strMap : {"allStr":"哇哦，勋章名，结语","highlightStr":"勋章名"}
     */

    private String userId;
    private StrMapBean strMap;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public StrMapBean getStrMap() {
        return strMap;
    }

    public void setStrMap(StrMapBean strMap) {
        this.strMap = strMap;
    }

    public static class StrMapBean {
        /**
         * allStr : 哇哦，勋章名，结语
         * highlightStr : 勋章名
         */

        private String allStr;
        private String highlightStr;

        public String getAllStr() {
            return allStr;
        }

        public void setAllStr(String allStr) {
            this.allStr = allStr;
        }

        public String getHighlightStr() {
            return highlightStr;
        }

        public void setHighlightStr(String highlightStr) {
            this.highlightStr = highlightStr;
        }
    }
}
