package com.qunar.im.ui.imagepicker.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.qunar.im.base.module.ImageItem;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 图片文件夹
 */
public class ImageFolder implements Serializable, Parcelable {
    private static final long serialVersionUID = 1L;

    public String name;  //当前文件夹的名字
    public String path;  //当前文件夹的路径
    public ImageItem cover;   //当前文件夹需要要显示的缩略图，默认为最近的一次图片
    public ArrayList<ImageItem> images;  //当前文件夹下所有图片的集合


    /** 只要文件夹的路径和名字相同，就认为是相同的文件夹 */
    @Override
    public boolean equals(Object o) {
        try {
            ImageFolder other = (ImageFolder) o;
            return this.path.equalsIgnoreCase(other.path) && this.name.equalsIgnoreCase(other.name);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return super.equals(o);
    }


    public ImageFolder() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeSerializable(this.cover);
        dest.writeList(this.images);
    }

    protected ImageFolder(Parcel in) {
        this.name = in.readString();
        this.path = in.readString();
        this.cover = (ImageItem) in.readSerializable();
        this.images = new ArrayList<ImageItem>();
        in.readList(this.images, ImageItem.class.getClassLoader());
    }

    public static final Parcelable.Creator<ImageFolder> CREATOR = new Parcelable.Creator<ImageFolder>() {
        @Override
        public ImageFolder createFromParcel(Parcel source) {
            return new ImageFolder(source);
        }

        @Override
        public ImageFolder[] newArray(int size) {
            return new ImageFolder[size];
        }
    };
}
