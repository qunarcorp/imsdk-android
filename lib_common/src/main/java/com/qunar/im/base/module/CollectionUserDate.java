package com.qunar.im.base.module;


/**
 * Created by hubin on 2017/11/21.
 */

public class CollectionUserDate extends AbstractExpandableItem<CollectionConvItemDate> implements MultiItemEntity {

    private String userId;
    private int bind;
    private int unRead;
    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getUnRead() {
        return unRead;
    }

    public void setUnRead(int unRead) {
        this.unRead = unRead;
    }

    @Override
    public int getItemType() {
        return CollectionAdapterConstant.TYPE_LEVEL_0;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getBind() {
        return bind;
    }

    public void setBind(int bind) {
        this.bind = bind;
    }
}
