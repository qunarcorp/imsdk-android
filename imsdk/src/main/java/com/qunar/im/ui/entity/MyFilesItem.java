package com.qunar.im.ui.entity;

import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.module.MultiItemEntity;
import com.qunar.im.ui.adapter.MyFilesAdapter;

/**
 * Created by Lex lex on 2018/5/30.
 */

public class MyFilesItem  implements MultiItemEntity {

    public String filename;
    public String filesize;
    public String filetime;
    public String filestatus;
    public String filefrom;
    public String fileto;
    public String fileicon;
    public IMMessage filemessage;

    @Override
    public String toString() {
        return "MyFilesItem{" +
                "filename='" + filename + '\'' +
                ", filesize='" + filesize + '\'' +
                ", filetime='" + filetime + '\'' +
                ", filestatus='" + filestatus + '\'' +
                ", filefrom='" + filefrom + '\'' +
                ", fileto='" + fileto + '\'' +
                ", fileicon='" + fileicon + '\'' +
                '}';
    }

    @Override
    public int getItemType() {
        return MyFilesAdapter.TYPE_LEVEL_1;
    }
}
