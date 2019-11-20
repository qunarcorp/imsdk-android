package com.qunar.im.base.jsonbean;

/**
 * Created by xingchao.song on 12/30/2015.
 */
public class AAShoukContent {
    public String url ;
    public String type;
    public String typestr;
    public String total_money;//总金额
    public String person_num;//总人数
    public String avg_money;//平均每人待付金额
    public String aa_type;//aa收款分类：single|total。single是指定人情况下的aa收款，avg_money没用；total是总人数平均收款方式。
}
