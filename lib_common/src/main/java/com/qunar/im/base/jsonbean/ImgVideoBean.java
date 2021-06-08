package com.qunar.im.base.jsonbean;

import android.os.Parcel;
import android.os.Parcelable;

public class ImgVideoBean implements Parcelable{
    public static final int IMG = 0;
    public static final int VIDEO = 1;
    public String url;
    public String thumbUrl;
    public String fileName;
    public String fileSize;
    public String Width;
    public String Height;
    public String Duration;
    public int type;//0:img 1:video


    public ImgVideoBean(){

    }
    @Override
    public boolean equals(Object o) {
        if (o instanceof ImgVideoBean) {
            ImgVideoBean item = (ImgVideoBean) o;
            return this.url.equalsIgnoreCase(item.url);
        }

        return super.equals(o);
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.thumbUrl);
        dest.writeString(this.fileName);
        dest.writeString(this.fileSize);
        dest.writeInt(this.type);
        dest.writeString(this.Width);
        dest.writeString(this.Height);
        dest.writeString(this.Duration);
    }

    protected ImgVideoBean(Parcel in) {
        this.url = in.readString();
        this.thumbUrl = in.readString();
        this.fileName = in.readString();
        this.fileSize = in.readString();
        this.type = in.readInt();
        this.Width = in.readString();
        this.Height = in.readString();
        this.Duration = in.readString();
    }

    public static final Parcelable.Creator<ImgVideoBean> CREATOR = new Parcelable.Creator<ImgVideoBean>() {
        @Override
        public ImgVideoBean createFromParcel(Parcel source) {
            return new ImgVideoBean(source);
        }

        @Override
        public ImgVideoBean[] newArray(int size) {
            return new ImgVideoBean[size];
        }
    };
}
