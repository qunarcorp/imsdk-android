package com.qunar.rn_service.jsonbean;

import com.qunar.im.base.jsonbean.BaseJsonResult;

import java.util.List;

/**
 * Created by xinbo.wang on 2016-12-20.
 */
public class SearchData extends BaseJsonResult {
    public String msg;
    public List<SearchItemData> data;


    public static class SearchItemData implements Comparable<SearchItemData>
    {
        public String groupLabel;
        public String groupId;
        public int groupPriority;
        public int todoType;
        public boolean hasMore;
        public String defaultportrait;
        public boolean isLocalData;
        public boolean isLoaclData;
        public List<SearchInfoData> info;

        @Override
        public int compareTo(SearchItemData another) {
            if(another.groupPriority>groupPriority)
            {
                return -1;
            }
            else if(another.groupPriority<groupPriority)
            {
                return 1;
            }
            else {
                return 0;
            }
        }
    }

    /*
    "icon": "xxx.xxx.com/a.png", //左侧ICON
	  "label": "",                // 展示信息标题
          "content": "",              // 展示信息主体
          "uri": {}                    // uri       uri为字典类型，内容不限制，处理具体某条数据时，会将uri传给native
                                       // todotype  key参考
                                       // 1         组聊天id
                                       // 8         机器人id
     */
    public static class SearchInfoData
    {
        public String icon;
        public String label;
        public String content;
        public String uri;
    }
}
