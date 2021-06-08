package com.qunar.im.base.module;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.Serializable;

/**
 * 图片信息
 */
public class ImageItem extends ReleaseCircleImageItemDate implements Serializable, Parcelable {
    private static final long serialVersionUID = 1L;

    public String name;       //图片的名字
    public String path;       //图片的路径
    public long size;         //图片的大小
    public int width;         //图片的宽度
    public int height;        //图片的高度
    public String mimeType;   //图片的类型
    public long addTime;      //图片的创建时间


    //为方便上传添加两个属性
    public String data;
    public String local;
    public boolean ret;
    /** 图片的路径和创建时间相同就认为是同一张图片 */
    @Override
    public boolean equals(Object o) {
        if (o instanceof ImageItem) {
            ImageItem item = (ImageItem) o;
            return this.path.equalsIgnoreCase(item.path) && this.addTime == item.addTime;
        }

        return super.equals(o);
    }

    public ImageItem() {
    }


    public String getPath() {
        return TextUtils.isEmpty(path)?data:path;
    }

    public void setPath(String path) {
        this.path = path;
        this.data =path;
    }

    public String getData() {
        return TextUtils.isEmpty(path)?data:path;
    }

    public void setData(String data) {
        this.data = data;
        this.path = data;
    }




    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeLong(this.size);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeString(this.mimeType);
        dest.writeLong(this.addTime);
    }

    protected ImageItem(Parcel in) {
        this.name = in.readString();
        this.path = in.readString();
        this.size = in.readLong();
        this.width = in.readInt();
        this.height = in.readInt();
        this.mimeType = in.readString();
        this.addTime = in.readLong();
    }

    public static final Parcelable.Creator<ImageItem> CREATOR = new Parcelable.Creator<ImageItem>() {
        @Override
        public ImageItem createFromParcel(Parcel source) {
            return new ImageItem(source);
        }

        @Override
        public ImageItem[] newArray(int size) {
            return new ImageItem[size];
        }
    };
}
