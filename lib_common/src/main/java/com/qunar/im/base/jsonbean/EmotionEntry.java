package com.qunar.im.base.jsonbean;

/**
 * Created by zhaokai on 15-8-4.
 */
public class EmotionEntry {
    public String pkgid;
    public String name;
    public String file;
    public String desc;
    public String thumb;
    public String md5;
    public long file_size;

    public boolean progressing;
    public boolean exist;

//    @Override
//    public String toString() {
//        return new StringBuilder()
//                .append("name").append(name)
//                .append("\nfile:").append(file)
//                .append("\ndesc:").append(desc)
//                .append("\nthumb:").append(thumb);
//                //.append("\nmd5:").append(md5).toString();
//    }
}
