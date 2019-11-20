package com.qunar.im.base.jsonbean;

import java.util.List;

/**
 * Created by Lex lex on 2018/7/30.
 */
public class QuickReplyResult extends BaseJsonResult{


    /**
     * data : {"groupInfo":{"version":1,"groups":[{"id":91,"username":null,"host":null,"groupname":"欢迎1","groupseq":1,"version":1,"isdel":0},{"id":92,"username":null,"host":null,"groupname":"欢迎2","groupseq":2,"version":1,"isdel":0}]},"contentInfo":{"version":1,"contents":[{"id":703,"content":"正在帮您查询1","groupid":91,"contentseq":1,"version":1,"isdel":0},{"id":713,"content":"正在帮您查询1","groupid":92,"contentseq":1,"version":1,"isdel":0}]}}
     */

    public DataBean data;

    public static class DataBean {
        /**
         * groupInfo : {"version":1,"groups":[{"id":91,"username":null,"host":null,"groupname":"欢迎1","groupseq":1,"version":1,"isdel":0},{"id":92,"username":null,"host":null,"groupname":"欢迎2","groupseq":2,"version":1,"isdel":0}]}
         * contentInfo : {"version":1,"contents":[{"id":703,"content":"正在帮您查询1","groupid":91,"contentseq":1,"version":1,"isdel":0},{"id":713,"content":"正在帮您查询1","groupid":92,"contentseq":1,"version":1,"isdel":0}]}
         */

        public GroupInfoBean groupInfo;
        public ContentInfoBean contentInfo;

        public static class GroupInfoBean {
            /**
             * version : 1
             * groups : [{"id":91,"username":null,"host":null,"groupname":"欢迎1","groupseq":1,"version":1,"isdel":0},{"id":92,"username":null,"host":null,"groupname":"欢迎2","groupseq":2,"version":1,"isdel":0}]
             */

            public int version;
            public List<GroupsBean> groups;

            public static class GroupsBean {
                /**
                 * id : 91
                 * username : null
                 * host : null
                 * groupname : 欢迎1
                 * groupseq : 1
                 * version : 1
                 * isdel : 0
                 */

                public int id;
                public String groupname;
                public int groupseq;
                public int version;
                public int isdel;

            }
        }

        public static class ContentInfoBean {
            /**
             * version : 1
             * contents : [{"id":703,"content":"正在帮您查询1","groupid":91,"contentseq":1,"version":1,"isdel":0},{"id":713,"content":"正在帮您查询1","groupid":92,"contentseq":1,"version":1,"isdel":0}]
             */

            public int version;
            public List<ContentsBean> contents;

            public int getVersion() {
                return version;
            }

            public void setVersion(int version) {
                this.version = version;
            }

            public List<ContentsBean> getContents() {
                return contents;
            }

            public void setContents(List<ContentsBean> contents) {
                this.contents = contents;
            }

            public static class ContentsBean {
                /**
                 * id : 703
                 * content : 正在帮您查询1
                 * groupid : 91
                 * contentseq : 1
                 * version : 1
                 * isdel : 0
                 */

                public int id;
                public String content;
                public int groupid;
                public int contentseq;
                public int version;
                public int isdel;
            }
        }
    }
}
