package com.qunar.im.base.jsonbean;

import java.util.List;

/**
 * Created by xingchao.song on 4/25/2016.
 */
public class AdvertiserBean extends BaseJsonResult {
    public int adsec;
    public int interval;
    public boolean carousel;
    public int carouseldelay;
    public boolean shown;
    public boolean allowskip;
    public long version;
    public String skiptips;
    public List<AdContent> adlist;

    public static class AdContent {
        public int adtype;
        public String linkurl;//图片点击后的跳转地址
        public String imgurl;
        public String url;
    }

}
