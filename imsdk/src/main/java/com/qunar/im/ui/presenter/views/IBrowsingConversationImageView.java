package com.qunar.im.ui.presenter.views;

import java.io.Serializable;
import java.util.List;

/**
 * Created by saber on 16-1-29.
 */
public interface IBrowsingConversationImageView {
    String getConversationId();
    String getOriginFrom();
    String getOriginTo();
    void setImageList(List<PreImage> urls);
    class PreImage implements Comparable<PreImage>,Serializable
    {
        //        public String dbUrl;
        public String smallUrl;
        public String originUrl;
        public String localPath;//本地图片路径
        public int width;
        public int height;

        @Override
        public int compareTo(PreImage another) {
            return originUrl.compareTo(another.originUrl);
        }

        @Override
        public boolean equals(Object preImage)
        {
            return preImage!=null&&preImage instanceof PreImage&&
                    (this.originUrl.equals(((PreImage)preImage).originUrl) || this.originUrl.equals(((PreImage)preImage).localPath));//this.dbUrl.equals(((PreImage)preImage).localPath)自己发送的图片逻辑判断
        }
    }
}