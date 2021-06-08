package com.qunar.im.base.jsonbean;

import java.util.List;

/**
 * Created by xinbo.wang on 2016/7/13.
 */
public class OrderCardInfoResult extends BaseResult {
    /***
     * 头图url（可没有）
     */
    public String titleimg;
    /***
     * 产品名称
     */
    public String titletxt;
    /***
     *  产品图（可以没有）
     */
    public String productimg;
    /***
     *  点击卡片希望跳转到的页面地址
     */
    public String detailurl;
    /***
     * 文本内容
     */
    public List<DescItem> descs;
    /***
     * windows客户端专用
     */
    public String descdetail;

    public static class DescItem implements BaseData
    {
        public String k=""; //小标题
        public String v="";// 内容
        public String c=""; //字体颜色
    }
}
