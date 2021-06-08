package com.qunar.im.base.jsonbean;

/**
 * 活动ext json
 */
public class ActivityMessageEntity extends BaseResult {


    public String url;//活动链接
    public String type;//消息分类（字段值：旅途活动）
    public String title;//活动标题
    public String img;//气泡活动图片
    public String intro;//活动介绍
    public String activity_city;//活动城市（例如：北京）
    public String address;//活动具体地址（例如：海淀区维亚大厦）
    public String ip_city;//IP定位当前城市（发布活动人的ip城市）
    public String start_date;//活动开始时间（例如：2018-06-30）
    public String end_date;//活动结束时间（例如：2018-07-01）
    public String category;//活动分类（例如：自驾、骑行、登山等）
    public String activity_type;//线上活动|线下活动
    public boolean auth;
    public boolean showbar;

}
