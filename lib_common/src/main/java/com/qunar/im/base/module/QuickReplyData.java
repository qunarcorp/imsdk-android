package com.qunar.im.base.module;

import java.util.List;

/**
 * Created by Lex lex on 2018/7/30.
 */
public class QuickReplyData {

    public long gid;
    public String groupname;
    public long groupseq;
    public List<String> contents;
//    public List<QuickReplyContent> contents;

    public static class QuickReplyContent {
        public long cid;
        public long gid;
        public String content;
        public long contentseq;
    }

}
