package com.qunar.im.base.util.graphics;

import java.io.File;

/**
 * Created by xinbo.wang on 2015/5/20.
 */
public interface ICacheDir {
    void setDir(String dir);
    File getFile(String fileName);
    File getDirectory();
}
