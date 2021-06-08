package com.qunar.im.base.jsonbean;

import com.qunar.im.base.module.BaseModel;

import java.io.Serializable;

/**
 * Created by lihaibin.li on 2017/8/22.
 */

public class DailyMindMain extends BaseModel implements Serializable{
    public int qid;
    public String version;
    public int type;
    public String title;
    public String desc;
    public String content;
    public int state;//State 枚举值 Delete(-1),Normal(1), Collection(2), Basket(3), Create(4),Update(4)
    public int isComplete;//todolist 是否已完成
}
