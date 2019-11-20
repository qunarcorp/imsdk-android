package com.qunar.im.base.jsonbean;

import java.util.List;

/**
 * Created by zhaokai on 15-9-15.
 */
public class ActionRichText {
    public String introduce;
    public List<SubTitle> subtitles;
    public String linkurl;
    public String imageurl;

    public static class SubTitle {
        public String linkurl;
        public String iconurl;
        public String introduce;
    }
}
