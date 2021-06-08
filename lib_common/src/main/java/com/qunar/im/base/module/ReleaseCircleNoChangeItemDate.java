package com.qunar.im.base.module;

import java.io.Serializable;

import static com.qunar.im.base.module.ReleaseCircleType.TYPE_UNCLICKABLE;

public class ReleaseCircleNoChangeItemDate  implements MultiItemEntity,Serializable {

    private String imgUrl;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public int getItemType() {
        return TYPE_UNCLICKABLE;
    }
}
